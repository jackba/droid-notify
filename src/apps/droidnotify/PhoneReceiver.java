package apps.droidnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
	    intent.setClass(context, PhoneService.class);
	    PhoneService.startPhoneMonitoringService(context, intent);
	}
	  
	//================================================================================
	// Private Methods
	//================================================================================
	
}
