package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import apps.droidnotify.log.Log;
import apps.droidnotify.services.OnBootBroadcastReceiverService;
import apps.droidnotify.services.WakefulIntentService;

/**
 * This class listens for the OnBoot event from the users phone. Then it schedules the users calendars to be checked.
 * 
 * @author Camille Sévigny
 */
public class OnBootReceiver extends BroadcastReceiver {

	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
    
	/**
	 * Receives a notification that the phone was restarted.
	 * This function starts the service that will handle the work to setup calendar event notifications.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		_debug = Log.getDebug();
		if (_debug) Log.v("OnBootReceiver.onReceive()");
		try{
			WakefulIntentService.acquireStaticLock(context);
		    Intent onBootBroadcastReceiverServiceIntent = new Intent(context, OnBootBroadcastReceiverService.class);
		    onBootBroadcastReceiverServiceIntent.putExtras(intent.getExtras());
			context.startService(onBootBroadcastReceiverServiceIntent);
		}catch(Exception ex){
			if (_debug) Log.e("OnBootReceiver.onReceive() ERROR: " + ex.toString());
		}
	}

}