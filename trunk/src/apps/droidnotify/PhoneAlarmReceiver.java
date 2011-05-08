package apps.droidnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PhoneAlarmReceiver extends BroadcastReceiver {

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
	 * 
	 * @param context
	 * @param intent
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		if (Log.getDebug()) Log.v("PhoneAlarmReceiver.onReceive()");
		WakefulIntentService.acquireStaticLock(context);
		context.startService(new Intent(context, PhoneAlarmReceiverService.class));
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
}