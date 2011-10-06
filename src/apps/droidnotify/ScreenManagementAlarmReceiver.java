package apps.droidnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;

public class ScreenManagementAlarmReceiver extends BroadcastReceiver {
	
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
		if (_debug) Log.v("ScreenManagementAlarmReceiver.onReceive()");
		try{
			//Release the KeyguardLock & WakeLock
			Common.clearKeyguardLock();
			Common.clearWakeLock();
		}catch(Exception ex){
			if (_debug) Log.v("ScreenManagementAlarmReceiver.onReceive() ERROR: " + ex.toString());
		}
	}
	
}
