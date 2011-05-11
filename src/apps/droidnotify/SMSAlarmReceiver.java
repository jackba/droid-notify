package apps.droidnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SMSAlarmReceiver extends BroadcastReceiver {

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
		if (Log.getDebug()) Log.v("SMSAlarmReceiver.onReceive()");
		WakefulIntentService.acquireStaticLock(context);
		Intent smsIntent = new Intent(context, SMSReceiverService.class);
		smsIntent.putExtras(intent.getExtras());
		context.startService(smsIntent);
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
}