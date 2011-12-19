package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import apps.droidnotify.log.Log;
import apps.droidnotify.services.FacebookAlarmBroadcastReceiverService;
import apps.droidnotify.services.WakefulIntentService;

/**
 * This class listens for scheduled notifications to check the users Facebook account.
 * 
 * @author Camille Sévigny
 */
public class FacebookAlarmReceiver extends BroadcastReceiver {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;
	  
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Receives a notification of a Facebook alarm.
	 * This function starts the service that will handle the work or reschedules the work if the phone is in use.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		_debug = Log.getDebug();
		if (_debug) Log.v("FacebookAlarmReceiver.onReceive()");
		try{			
			if(!Log.getAppProVersion()){
				if (_debug) Log.v("FacebookAlarmReceiver.onReceive() BASIC APP VERSION. Exiting...");
				return;
			}
			WakefulIntentService.sendWakefulWork(context, new Intent(context, FacebookAlarmBroadcastReceiverService.class));
		}catch(Exception ex){
			Log.e("FacebookAlarmReceiver.onReceive() ERROR: " + ex.toString());
		}
	}
	
}