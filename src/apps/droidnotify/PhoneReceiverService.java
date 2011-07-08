package apps.droidnotify;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;

/**
 * This class handles scheduled Missed Call notifications that we want to display.
 * 
 * @author Camille Sévigny
 */
public class PhoneReceiverService extends WakefulIntentService {

	//================================================================================
    // Constants
    //================================================================================
	
	private static final int NOTIFICATION_TYPE_PHONE = 0;
	private static final int MISSED_CALL_TYPE = android.provider.CallLog.Calls.MISSED_TYPE;
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor
	 */
	public PhoneReceiverService() {
		super("PhoneReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("PhoneReceiverService.PhoneReceiverService()");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Display the notification for this Missed Call.
	 * 
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		_debug = Log.getDebug();
		if (_debug) Log.v("PhoneReceiverService.doWakefulWork()");
		ArrayList<String> missedCallsArray = getMissedCalls();
		if(missedCallsArray.size() > 0){
			Context context = getApplicationContext();
			Bundle bundle = new Bundle();
			bundle.putInt("notificationType", NOTIFICATION_TYPE_PHONE);
			bundle.putStringArrayList("missedCallsArrayList", missedCallsArray);
	    	Intent phoneNotificationIntent = new Intent(context, NotificationActivity.class);
	    	phoneNotificationIntent.putExtras(bundle);
	    	phoneNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	    	context.startActivity(phoneNotificationIntent);
		}else{
			if (_debug) Log.v("PhoneReceiverService.doWakefulWork() No missed calls were found. Exiting...");
		}
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Function to query the call log and check for any missed calls.
	 * 
	 * @return ArrayList<String> - Returns an ArrayList of Strings that contain the missed call information.
	 */
	private ArrayList<String> getMissedCalls(){
		if (_debug) Log.v("PhoneReceiverService.getMissedCalls()");
		Context context = getApplicationContext();
		ArrayList<String> missedCallsArray = new ArrayList<String>();
		final String[] projection = null;
		final String selection = null;
		final String[] selectionArgs = null;
		final String sortOrder = android.provider.CallLog.Calls.DATE + " DESC";
		Cursor cursor = null;
		try{
		    cursor = context.getContentResolver().query(
		    		Uri.parse("content://call_log/calls"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
	    	while (cursor.moveToNext()) { 
	    		String callNumber = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
	    		String callDate = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE));
	    		String callType = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE));
	    		String isCallNew = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NEW));
	    		if(Integer.parseInt(callType) == MISSED_CALL_TYPE && Integer.parseInt(isCallNew) > 0){
    				if (_debug) Log.v("PhoneReceiverService.getMissedCalls() Missed Call Found: " + callNumber);
    				String[] missedCallContactInfo = Common.loadContactsInfoByPhoneNumber(context, callNumber);
    				if(missedCallContactInfo == null){
    					missedCallsArray.add(callNumber + "|" + callDate);
    				}else{
    					missedCallsArray.add(callNumber + "|" + callDate + "|" + missedCallContactInfo[0] + "|" + missedCallContactInfo[1] + "|" + missedCallContactInfo[2]);
    				}
    				break;
    			}else{
    				break;
    			}
	    	}
		}catch(Exception ex){
			if (_debug) Log.e("PhoneReceiverService.getMissedCalls() ERROR: " + ex.toString());
		}finally{
			cursor.close();
		}
	    return missedCallsArray;
	}
	
}