package apps.droidnotify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

/**
 * This class handles the work of processing incoming MMS messages.
 * 
 * @author Camille Sévigny
 */
public class MMSAlarmReceiverService extends WakefulIntentService {

	//================================================================================
    // Constants
    //================================================================================
	
	private static final int NOTIFICATION_TYPE_MMS = 2;
	
	//================================================================================
    // Properties
    //================================================================================
	
	//================================================================================
	// Constructors
	//================================================================================
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public MMSAlarmReceiverService() {
		super("MMSAlarmReceiverService");
		if (Log.getDebug()) Log.v("MMSAlarmReceiverService.MMSReceiverService()");
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
		if (Log.getDebug()) Log.v("MMSAlarmReceiverService.doWakefulWork()");
		ArrayList<String> mmsArray = getMMSMessages();
		if(mmsArray.size() > 0){
			Context context = getApplicationContext();
			Bundle bundle = new Bundle();
			bundle.putInt("notificationType", NOTIFICATION_TYPE_MMS);
			bundle.putStringArrayList("mmsArrayList",mmsArray);
	    	Intent mmsNotificationIntent = new Intent(context, NotificationActivity.class);
	    	mmsNotificationIntent.putExtras(bundle);
	    	mmsNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	    	//context.startActivity(mmsNotificationIntent);
		}else{
			if (Log.getDebug()) Log.v("MMSAlarmReceiverService.doWakefulWork() No new MMSs were found. Exiting...");
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
		if (Log.getDebug()) Log.v("MMSAlarmReceiverService.getMMSMessages()");
		ArrayList<String> mmsArray = new ArrayList<String>();
		final String[] projection = new String[] { "_id", "thread_id", "date"};
		final String selection = "read = 0";
		final String[] selectionArgs = null;
		final String sortOrder = null;
		Cursor cursor = null;
        try{
		    cursor = getApplicationContext().getContentResolver().query(
		    		Uri.parse("content://mms/inbox"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
	    	while (cursor.moveToNext()) {		    	
	    		long messageID = cursor.getLong(cursor.getColumnIndex("_id"));
		    	long threadID = cursor.getLong(cursor.getColumnIndex("thread_id"));
		    	long timestamp = cursor.getLong(cursor.getColumnIndex("date"));
		    	String messageAddress = getMMSAddress(String.valueOf(messageID));
//	    		for(int i=0; i<cursor.getColumnCount(); i++){
//	    			if (Log.getDebug()) Log.v("MMSAlarmReceiverService.getMMSMessages() Cursor Column: " + cursor.getColumnName(i));
//	    		}
		    	
		    	if (Log.getDebug()) Log.v("MMSAlarmReceiverService.getMMSMessages() address: " + messageAddress);
		    	if (Log.getDebug()) Log.v("MMSAlarmReceiverService.getMMSMessages() timestamp: " + timestamp);


		    	break;
	    	}
		}catch(Exception ex){
			if (Log.getDebug()) Log.e("MMSAlarmReceiverService.getMMSMessages() ERROR: " + ex.toString());
		} finally {
    		cursor.close();
    	}	    
		return mmsArray;	
	}

	private String getMMSAddress(String messageID) {
	    String selectionAdd = new String("msg_id=" + messageID);
	    Uri uriAddress = Uri.parse("content://mms/" + messageID + "/addr");
	    Cursor cAdd = getContentResolver().query(
	    		uriAddress, 
	    		null,
	    		selectionAdd, 
	    		null, 
	    		null);
	    String name = null;
	    if (cAdd.moveToFirst()) {
	        do {
	            String number = cAdd.getString(cAdd.getColumnIndex("address"));
	            name = number;
	            break;
	        } while (cAdd.moveToNext());
	    }
	    if (cAdd != null) {
	        cAdd.close();
	    }
	    return name;
	}
	
	private String getMMSText(String id) {
	    Uri partURI = Uri.parse("content://mms/part/" + id);
	    InputStream is = null;
	    StringBuilder sb = new StringBuilder();
	    try {
	        is = getContentResolver().openInputStream(partURI);
	        if (is != null) {
	            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
	            BufferedReader reader = new BufferedReader(isr);
	            String temp = reader.readLine();
	            while (temp != null) {
	                sb.append(temp);
	                temp = reader.readLine();
	            }
	        }
	    } catch (Exception ex) {
	    	
	    }finally {
	        if (is != null) {
	            try {
	                is.close();
	            } catch (Exception ex) {
	            	
	            }
	        }
	    }
	    return sb.toString();
	}
	
}