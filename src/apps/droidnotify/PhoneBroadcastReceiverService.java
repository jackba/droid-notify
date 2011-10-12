package apps.droidnotify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This class does the work of the BroadcastReceiver.
 * 
 * @author Camille Sévigny
 */
public class PhoneBroadcastReceiverService extends WakefulIntentService {
	
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
	public PhoneBroadcastReceiverService() {
		super("PhoneBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("PhoneBroadcastReceiverService.PhoneBroadcastReceiverService()");
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
		if (_debug) Log.v("PhoneBroadcastReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("PhoneBroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Read preferences and exit if missed call notifications are disabled.
		    if(!preferences.getBoolean(Constants.PHONE_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v("PhoneBroadcastReceiverService.doWakefulWork() Missed Call Notifications Disabled. Exiting... ");
				return;
			}
		    //Check the state of the users phone.
			TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
		    if(callStateIdle){
				//Schedule phone task x seconds after the broadcast.
				//This time is set by the users advanced preferences. 5 seconds is the default value.
				//This should allow enough time to pass for the phone log to be written to.
				long timeoutInterval = Long.parseLong(preferences.getString(Constants.CALL_LOG_TIMEOUT_KEY, "5")) * 1000;
				AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
				Intent phoneIntent = new Intent(context, PhoneAlarmReceiver.class);
				PendingIntent phonePendingIntent = PendingIntent.getBroadcast(context, 0, phoneIntent, 0);
				alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeoutInterval, phonePendingIntent);		
		    }else{
		    	if (_debug) Log.v("PhoneBroadcastReceiverService.doWakefulWork() Phone Call In Progress. Exiting...");
		    }		
	    }catch(Exception ex){
			if (_debug) Log.e("PhoneBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}