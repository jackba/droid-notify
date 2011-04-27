package apps.droidnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * 
 * @author Camille Sevigny
 *
 */
public class MMSReceiver extends BroadcastReceiver{

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
		if (Log.getDebug()) Log.v("MMSReceiver.onReceive()");
	    //intent.setClass(context, MMSReceiverService.class);
	    //MMSReceiverService.startMMSMonitoringService(context, intent);
	}
	  
	//================================================================================
	// Private Methods
	//================================================================================

}
