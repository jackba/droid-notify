package apps.droidnotify;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import apps.droidnotify.common.Common;

/**
 * This class handles the work of processing incoming MMS messages.
 * 
 * @author Camille Sévigny
 */
public class MMSReceiverService extends WakefulIntentService {

	//================================================================================
    // Constants
    //================================================================================
	
	private static final int NOTIFICATION_TYPE_MMS = 2;
	
	//================================================================================
    // Properties
    //================================================================================
	
	boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public MMSReceiverService() {
		super("MMSReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("MMSReceiverService.MMSReceiverService()");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Do the work for the service inside this function.
	 * 
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		_debug = Log.getDebug();
		if (_debug) Log.v("MMSReceiverService.doWakefulWork()");
		ArrayList<String> mmsArray = getMMSMessages();
		if(mmsArray.size() > 0){
			Context context = getApplicationContext();
			Bundle bundle = new Bundle();
			bundle.putInt("notificationType", NOTIFICATION_TYPE_MMS);
			bundle.putStringArrayList("mmsArrayList",mmsArray);
	    	Intent mmsNotificationIntent = new Intent(context, NotificationActivity.class);
	    	mmsNotificationIntent.putExtras(bundle);
	    	mmsNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	    	context.startActivity(mmsNotificationIntent);
		}else{
			if (_debug) Log.v("MMSReceiverService.doWakefulWork() No new MMSs were found. Exiting...");
		}
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Function to query the mms inbox and check for any new messages.
	 * 
	 * @return ArrayList<String> - Returns an ArrayList of Strings that contain the mms information.
	 */
	private ArrayList<String> getMMSMessages(){
		if (_debug) Log.v("MMSReceiverService.getMMSMessages()");
		Context context = getApplicationContext();
		ArrayList<String> mmsArray = new ArrayList<String>();
		final String[] projection = new String[] {"_id", "thread_id", "date"};
		final String selection = "read = 0";
		final String[] selectionArgs = null;
		final String sortOrder = "date DESC";
		Cursor cursor = null;
        try{
		    cursor = context.getContentResolver().query(
		    		Uri.parse("content://mms/inbox"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
	    	while (cursor.moveToNext()) {		    	
	    		String messageID = cursor.getString(cursor.getColumnIndex("_id"));
	    		String threadID = cursor.getString(cursor.getColumnIndex("thread_id"));
		    	String timeStamp = cursor.getString(cursor.getColumnIndex("date"));
		    	String messageAddress = getMMSAddress(messageID);
		    	String messageBody = getMMSText(messageID);
		    	if (_debug) Log.v("MMSReceiverService.getMMSMessages() messageID: " + messageID);
		    	if (_debug) Log.v("MMSReceiverService.getMMSMessages() threadID: " + threadID);
		    	if (_debug) Log.v("MMSReceiverService.getMMSMessages() timestamp: " + timeStamp);
		    	if (_debug) Log.v("MMSReceiverService.getMMSMessages() messageAddress: " + messageAddress);
		    	if (_debug) Log.v("MMSReceiverService.getMMSMessages() messageBody: " + messageBody);
		    	String[] mmsContactInfo = null;
		    	if(messageAddress.contains("@")){
		    		mmsContactInfo = Common.loadContactsInfoByEmail(context, messageAddress);
		    	}else{
		    		mmsContactInfo = Common.loadContactsInfoByPhoneNumber(context, messageAddress);
		    	}
				if(mmsContactInfo == null){
					mmsArray.add(messageAddress + "|" + messageBody + "|" + messageID + "|" + threadID + "|" + timeStamp);
				}else{
					mmsArray.add(messageAddress + "|" + messageBody + "|" + messageID + "|" + threadID + "|" + timeStamp + "|" + mmsContactInfo[0] + "|" + mmsContactInfo[1] + "|" + mmsContactInfo[2]);
				}
		    	break;
	    	}
		}catch(Exception ex){
			if (_debug) Log.e("MMSReceiverService.getMMSMessages() ERROR: " + ex.toString());
		} finally {
    		cursor.close();
    	}
		return mmsArray;	
	}

	/**
	 * Gets the address of the MMS message.
	 * 
	 * @param messageID - The MMS message ID.
	 * 
	 * @return String - The phone or email address of the MMS message.
	 */
	private String getMMSAddress(String messageID) {
		if (_debug) Log.v("MMSReceiverService.getMMSAddress()");
		final String[] projection = new String[] {"address"};
		final String selection = "msg_id = " + messageID;
		final String[] selectionArgs = null;
		final String sortOrder = null;
		String messageAddress = null;
		Cursor cursor = null;
        try{
		    cursor = getApplicationContext().getContentResolver().query(
		    		Uri.parse("content://mms/" + messageID + "/addr"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
		    while(cursor.moveToNext()){
		    	messageAddress = cursor.getString(cursor.getColumnIndex("address"));
	            break;
	        }
		}catch(Exception ex){
			if (_debug) Log.e("MMSReceiverService.getMMSAddress() ERROR: " + ex.toString());
		} finally {
    		cursor.close();
    	}	   
	    return messageAddress;
	}
	
	/**
	 * Read the message text of the MMS message.
	 * 
	 * @param messageID - The MMS message ID.
	 * 
	 * @return String - The message text of the MMS message.
	 */
	private String getMMSText(String messageID) {
		if (_debug) Log.v("MMSReceiverService.getMMSText()");
		final String[] projection = new String[] {"_id", "ct", "_data", "text"};
		final String selection = "mid = " + messageID;
		final String[] selectionArgs = null;
		final String sortOrder = null;
		StringBuilder messageText = new StringBuilder();
		Cursor cursor = null;
        try{
		    cursor = getApplicationContext().getContentResolver().query(
		    		Uri.parse("content://mms/part"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
		    while(cursor.moveToNext()){
		        String partId = cursor.getString(cursor.getColumnIndex("_id"));
		        String contentType = cursor.getString(cursor.getColumnIndex("ct"));
		        String text = cursor.getString(cursor.getColumnIndex("text"));
		        if(text != null){
	            	if(!messageText.toString().equals("")){
	            		messageText.append(" ");
	            	}
			        messageText.append(text);
		        }
		        if (contentType.equals("text/plain")) {
		            String data = cursor.getString(cursor.getColumnIndex("_data"));
		            if (data != null) {
		            	if(!messageText.toString().equals("")){
		            		messageText.append(" ");
		            	}
		            	messageText.append(getMMSTextFromPart(partId));
		            }
		        }
	        }
		}catch(Exception ex){
			if (_debug) Log.e("MMSReceiverService.getMMSText ERROR: " + ex.toString());
		} finally {
    		cursor.close();
    	}	   
	    return messageText.toString();  
	}
	
	/**
	 * Read the message text of the MMS message.
	 * 
	 * @param messageID - The MMS message ID.
	 * 
	 * @return String - The message text of the MMS message.
	 */
	private String getMMSTextFromPart(String messageID) {
		if (_debug) Log.v("MMSReceiverService.getMMSTextFromPart()");
	    InputStream inputStream = null;
	    StringBuilder messageText = new StringBuilder();
	    try {
	    	inputStream = getContentResolver().openInputStream(Uri.parse("content://mms/part/" + messageID));
	        if (inputStream != null) {
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
	            BufferedReader reader = new BufferedReader(inputStreamReader);
	            String temp = reader.readLine();
	            while (temp != null) {
	            	messageText.append(temp);
	                temp = reader.readLine();
	            }
	        }
	    } catch (Exception ex) {
	    	if (_debug) Log.e("MMSReceiverService.getMMSTextFromPart() ERROR: " + ex.toString());
	    }finally {
	    	try{
	    		inputStream.close();
	    	}catch(Exception ex){
	    		if (_debug) Log.e("MMSReceiverService.getMMSTextFromPart() ERROR: " + ex.toString());
	    	}
	    }
	    return messageText.toString();
	}
	
}