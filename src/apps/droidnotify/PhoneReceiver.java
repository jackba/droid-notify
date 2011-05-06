package apps.droidnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * @author Camille Sevigny
 *
 */
public class PhoneReceiver extends BroadcastReceiver{

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
	 * 
	 */
	@Override
	public void onReceive(Context context, Intent intent){
		if (Log.getDebug()) Log.v("PhoneReceiver.onReceive()");
		//DELETE THIS SOON
	    //intent.setClass(context, PhoneReceiverService.class);
	    //PhoneReceiverService.startPhoneMonitoringService(context, intent);
		//----------------
		WakefulIntentService.acquireStaticLock(context);
		context.startService(new Intent(context, PhoneReceiverService.class));
	}
	  
	//================================================================================
	// Private Methods
	//================================================================================
	
}
