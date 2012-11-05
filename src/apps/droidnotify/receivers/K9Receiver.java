package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import apps.droidnotify.log.Log;
import apps.droidnotify.services.K9BroadcastReceiverService;
import apps.droidnotify.services.WakefulIntentService;
import apps.droidnotify.common.Constants;

/**
 * This class listens for incoming K-9 Email messages.
 * 
 * @author Camille Sévigny
 */
public class K9Receiver extends BroadcastReceiver{

	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Receives the incomming K-9 Email message intent.
	 * This function starts the service that will handle the work.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent){
		_debug = Log.getDebug(context);
		if (_debug) Log.v(context, "K9Receiver.onReceive()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v(context, "K9Receiver.onReceive() App Disabled. Exiting...");
				return;
			}
			//Read preferences and exit if K9 notifications are disabled.
		    if(!preferences.getBoolean(Constants.K9_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v(context, "K9Receiver.onReceive() K9 Notifications Disabled. Exiting...");
				return;
			}
			Intent k9BroadcastReceiverServiceIntent = new Intent(context, K9BroadcastReceiverService.class);
		    k9BroadcastReceiverServiceIntent.putExtras(intent.getExtras());
		    k9BroadcastReceiverServiceIntent.setAction(intent.getAction());
			WakefulIntentService.sendWakefulWork(context, k9BroadcastReceiverServiceIntent);
		}catch(Exception ex){
			Log.e(context, "K9Receiver.onReceive() ERROR: " + ex.toString());
		}
	}

}