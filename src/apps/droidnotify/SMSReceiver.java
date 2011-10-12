package apps.droidnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import apps.droidnotify.log.Log;

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
	 * Receives the incomming SMS message. The SMS message is located within the Intent object.
	 * This function starts the service that will handle the work or reschedules the work.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent){
		_debug = Log.getDebug();
		if (_debug) Log.v("SMSReceiver.onReceive()");
		try{
			WakefulIntentService.acquireStaticLock(context);
		    Intent smsBroadcastReceiverServiceIntent = new Intent(context, SMSBroadcastReceiverService.class);
			smsBroadcastReceiverServiceIntent.putExtras(intent.getExtras());
			context.startService(smsBroadcastReceiverServiceIntent);
		}catch(Exception ex){
			if (_debug) Log.e("SMSReceiver.onReceive() ERROR: " + ex.toString());
		}
	}

}