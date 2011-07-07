package apps.droidnotify;

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
		    	String sentFromAddress = Common.getMMSAddress(context, messageID);
		    	if(sentFromAddress.contains("@")){
	            	sentFromAddress = Common.removeEmailFormatting(sentFromAddress);
	            }
		    	String messageBody = Common.getMMSText(context, messageID);
		    	String[] mmsContactInfo = null;
		    	if(sentFromAddress.contains("@")){
		    		mmsContactInfo = Common.loadContactsInfoByEmail(context, sentFromAddress);
		    	}else{
		    		mmsContactInfo = Common.loadContactsInfoByPhoneNumber(context, sentFromAddress);
		    	}
				if(mmsContactInfo == null){
					mmsArray.add(sentFromAddress + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + threadID + "|" + timeStamp);
				}else{
					mmsArray.add(sentFromAddress + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + threadID + "|" + timeStamp + "|" + mmsContactInfo[0] + "|" + mmsContactInfo[1] + "|" + mmsContactInfo[2]);
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
	
}