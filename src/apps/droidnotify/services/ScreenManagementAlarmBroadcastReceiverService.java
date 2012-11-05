package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;

import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;

public class ScreenManagementAlarmBroadcastReceiverService extends WakefulIntentService {

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public ScreenManagementAlarmBroadcastReceiverService() {
		super("ScreenManagementAlarmBroadcastReceiverService");
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
			//Check to see if the user is in a linked app. If they are, do not release the wakelock or keyguard.
			if(Common.isUserInLinkedApp(context)){
				//Do not release the wakelock or keyguard.
			}else{
				//Release the KeyguardLock & WakeLock
				Common.clearKeyguardLock(null);
				Common.clearWakeLock();
			}
		}catch(Exception ex){
			Log.e(context, "ScreenManagementAlarmBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}