package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.services.PhoneBroadcastReceiverService;
import apps.droidnotify.services.WakefulIntentService;

/**
 * This class listens for the phone state to change.
 * 
 * @author Camille Sévigny
 */
public class PhoneReceiver extends BroadcastReceiver{
	
	//================================================================================
    // Properties
    //================================================================================

    private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Receives a notification that the phone state changed.
	 * This function starts the service that will handle the work or reschedules the work.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent){
		_debug = Log.getDebug();
		if (_debug) Log.v("PhoneReceiver.onReceive()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("PhoneReceiver.onReceive() App Disabled. Exiting...");
				return;
			}
			//Read preferences and exit if missed call notifications are disabled.
		    if(!preferences.getBoolean(Constants.PHONE_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v("PhoneReceiver.onReceive() Missed Call Notifications Disabled. Exiting... ");
				return;
			}
		    //Check the state of the users phone.
			TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    int callState = telemanager.getCallState();
		    setCallStateFlag(preferences, callState);
			WakefulIntentService.sendWakefulWork(context, new Intent(context, PhoneBroadcastReceiverService.class));
		}catch(Exception ex){
			Log.e("PhoneReceiver.onReceive() ERROR: " + ex.toString());
		}
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Set the phone state flag.
	 */
	private void setCallStateFlag(SharedPreferences preferences, int callState){
		if (_debug) Log.v("PhoneReceiver.setCallStateFlag() callState: " + callState);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(Constants.CALL_STATE_KEY, callState);
		editor.commit();
	}
	
}