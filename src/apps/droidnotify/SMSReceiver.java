package apps.droidnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

/**
 * This class listens for incoming text messages.
 * 
 * @author Camille Sevigny
 *
 */
public class SMSReceiver extends BroadcastReceiver{
	
	//================================================================================
    // Properties
    //================================================================================

	//================================================================================
	// Constructors
	//================================================================================
		
	//================================================================================
	// Accessors
	//================================================================================
	  
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * This method onReceive() takes too long here (more than 10 seconds) and causes a 
	 * "Application Not Responding: Wait/Close" message.
	 * Instead use a service that executes in a different thread.
	 */
	@Override
	public void onReceive(Context context, Intent intent){
		if (Log.getDebug()) Log.v("SMSReceiver.onReceive()");
	    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
	    // If the user is not in a call then start out work. 
	    if (callStateIdle) {
			WakefulIntentService.acquireStaticLock(context);
			context.startService(new Intent(context, SMSReceiverService.class));
	    }else{
	    	if (Log.getDebug()) Log.v("SMSReceiver.onReceive() Phone Call In Progress.");
	    	//TODO - Reschedule this notification in 5 minutes.
	    	
	    }
	}
	  
	//================================================================================
	// Private Methods
	//================================================================================
	
}