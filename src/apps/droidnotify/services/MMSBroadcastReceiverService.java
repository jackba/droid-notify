package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.receivers.MMSAlarmReceiver;

/**
 * This class does the work of the BroadcastReceiver.
 * 
 * @author Camille Sévigny
 */
public class MMSBroadcastReceiverService extends WakefulIntentService {

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public MMSBroadcastReceiverService() {
		super("MMSBroadcastReceiverService");
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
		Context context = getApplicationContext();
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				Log.e(context, "MMSBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
			//Schedule mms task x seconds after the broadcast.
			//This time is set by the users advanced preferences. 40 seconds is the default value.
			//This should allow enough time to pass for the mms inbox to be written to.
			long timeoutInterval = Long.parseLong(preferences.getString(Constants.MMS_TIMEOUT_KEY, "40")) * 1000;
			String intentActionText = "apps.droidnotify.alarm/MMSAlarmReceiverAlarm/" + String.valueOf(System.currentTimeMillis());
			long alarmTime = System.currentTimeMillis() + timeoutInterval;
			Common.startAlarm(context, MMSAlarmReceiver.class, null, intentActionText, alarmTime);
		}catch(Exception ex){
			Log.e(context, "MMSBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}