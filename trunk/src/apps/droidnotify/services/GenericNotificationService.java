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
    // Properties
    //================================================================================

	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public GenericNotificationService() {
		super("GenericNotificationService");
		_debug = Log.getDebug();
		if (_debug) Log.v("GenericNotificationService.GenericNotificationService()");
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
		if (_debug) Log.v("GenericNotificationService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("GenericNotificationService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
		    //Check the state of the users phone.
		    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    boolean notificationIsBlocked = false;
		    boolean rescheduleNotificationInCall = true;
		    boolean rescheduleNotificationInQuickReply = true;
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
		    	Log.e("GenericNotificationService.doWakefulWork() Generic Notification Is Empty. Exiting...");
		    	return;
		    }
		    if(!notificationIsBlocked){
				Common.startNotificationActivity(context, intentExtrasBundle);
		    }else{					
		    	Common.rescheduleBlockedNotification(context, rescheduleNotificationInCall, rescheduleNotificationInQuickReply, Constants.NOTIFICATION_TYPE_GENERIC, intentExtrasBundle);
		    }
		}catch(Exception ex){
			Log.e("GenericNotificationService.doWakefulWork() ERROR: " + ex.toString());
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
		//if (_debug) Log.v("GenericNotificationService.isEmptyGenericNotification()");
		String packageName = bundle.getString(Constants.BUNDLE_PACKAGE);
		String displayText = bundle.getString(Constants.BUNDLE_DISPLAY_TEXT);
		if(packageName == null || packageName.trim().equals("") || displayText == null || displayText.trim().equals("")){
			Log.e("GenericNotificationService.isEmptyGenericNotification() PackageName: " + packageName);
			Log.e("GenericNotificationService.isEmptyGenericNotification() DisplayText: " + displayText);
			return true;
		}
		return false;
	}
	
}
