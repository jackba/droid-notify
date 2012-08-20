package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import apps.droidnotify.log.Log;
import apps.droidnotify.services.ReminderDBManagementService;
import apps.droidnotify.services.WakefulIntentService;
/**
 * This class listens for a reminder management alarm.
 * 
 * @author Camille Sévigny
 */
public class ReminderDBManagementReceiver extends BroadcastReceiver {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;
	  
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Receives a notification of a Screen Management Alarm.
	 * This function removes the KeyguardLock and WakeLock help by this application.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		_debug = Log.getDebug();
		if (_debug) Log.v("ReminderDBManagementReceiver.onReceive()");
		try{
			WakefulIntentService.sendWakefulWork(context,  new Intent(context, ReminderDBManagementService.class));
		}catch(Exception ex){
			Log.e("ReminderDBManagementReceiver.onReceive() ERROR: " + ex.toString());
		}
	}
	
}
