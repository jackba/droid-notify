package apps.droidnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This class listens for incoming text messages.
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
	    intent.setClass(context, SMSReceiverService.class);
	    intent.putExtra("result", getResultCode());
	    SMSReceiverService.startSMSMonitoringService(context, intent);
	}
	  
	//================================================================================
	// Private Methods
	//================================================================================
	
}
	

