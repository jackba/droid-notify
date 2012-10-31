package apps.droidnotify.services;

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
    // Properties
    //================================================================================
	
	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public OnBootService() {
		super("OnBootBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("OnBootBroadcastReceiverService.OnBootBroadcastReceiverService()");
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
		if (_debug) Log.v("OnBootBroadcastReceiverService.doWakefulWork()");
		try{
			Common.startAppAlarms(getApplicationContext());
		}catch(Exception ex){
			Log.e("OnBootBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}