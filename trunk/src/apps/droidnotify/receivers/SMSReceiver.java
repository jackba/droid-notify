package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import apps.droidnotify.log.Log;
import apps.droidnotify.services.SMSReceiverService;
import apps.droidnotify.services.WakefulIntentService;
import apps.droidnotify.common.Constants;

/**
 * This class listens for incoming SMS messages.
 * 
 * @author Camille Sévigny
 */
public class SMSReceiver extends BroadcastReceiver{

	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Receives the incomming SMS message.
	 * This function starts the service that will handle the work.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent){
		_debug = Log.getDebug(context);
		if (_debug) Log.v(context, "SMSReceiver.onReceive()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v(context, "SMSReceiver.onReceive() App Disabled. Exiting...");
				return;
			}
			//Read preferences and exit if SMS notifications are disabled.
		    if(!preferences.getBoolean(Constants.SMS_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v(context, "SMSReceiver.onReceive() SMS Notifications Disabled. Exiting...");
				return;
			}
			Intent smsBroadcastReceiverServiceIntent = new Intent(context, SMSReceiverService.class);
			smsBroadcastReceiverServiceIntent.putExtras(intent.getExtras());
			WakefulIntentService.sendWakefulWork(context, smsBroadcastReceiverServiceIntent);
		}catch(Exception ex){
			Log.e(context, "SMSReceiver.onReceive() ERROR: " + ex.toString());
		}
	}

}