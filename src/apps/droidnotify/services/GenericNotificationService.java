package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import apps.droidnotify.log.Log;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;

public class GenericNotificationService extends WakefulIntentService {

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public GenericNotificationService() {
		super("GenericNotificationService");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Display the notification for this custom event.
	 * 
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		Context context = getApplicationContext();
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				Log.e(context, "GenericNotificationService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
		    //Check the state of the users phone.
		    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    boolean notificationIsBlocked = false;
		    boolean rescheduleNotificationInCall = false;
		    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
		    //Reschedule notification based on the users preferences.
		    if(!callStateIdle){
		    	notificationIsBlocked = true;		    	
		    	rescheduleNotificationInCall = preferences.getBoolean(Constants.IN_CALL_RESCHEDULING_ENABLED_KEY, false);
		    }else{
		    	notificationIsBlocked = Common.isNotificationBlocked(context);
		    }
		    Bundle intentExtrasBundle = intent.getExtras();
		    //Check for empty notifications.
		    if(isEmptyGenericNotification(intentExtrasBundle)){
		    	Log.e(context, "GenericNotificationService.doWakefulWork() Generic Notification Is Empty. Exiting...");
		    	return;
		    }
		    if(!notificationIsBlocked){
				Common.startNotificationActivity(context, intentExtrasBundle);
		    }else{					
		    	Common.rescheduleBlockedNotification(context, callStateIdle, rescheduleNotificationInCall, Constants.NOTIFICATION_TYPE_GENERIC, intentExtrasBundle);
		    }
		}catch(Exception ex){
			Log.e(context, "GenericNotificationService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Check for an empty notification API bundle.
	 * 
	 * @param bundle - The bundle to check.
	 * 
	 * @return boolean- Return true if the bundle is missing key information.
	 */
	private boolean isEmptyGenericNotification(Bundle bundle){
		String packageName = bundle.getString(Constants.BUNDLE_PACKAGE);
		String displayText = bundle.getString(Constants.BUNDLE_DISPLAY_TEXT);
		if(packageName == null || packageName.trim().equals("") || displayText == null || displayText.trim().equals("")){
			return true;
		}
		return false;
	}
	
}
