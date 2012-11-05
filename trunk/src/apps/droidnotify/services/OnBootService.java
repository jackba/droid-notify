package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;

import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;

/**
 * This class does the work of the BroadcastReceiver.
 * 
 * @author Camille Sévigny
 */
public class OnBootService extends WakefulIntentService {

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public OnBootService() {
		super("OnBootBroadcastReceiverService");
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
		Context context = this.getApplicationContext();
		try{
			Common.startAppAlarms(getApplicationContext());
		}catch(Exception ex){
			Log.e(context, "OnBootBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}