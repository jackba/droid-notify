package apps.droidnotify;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This class handles scheduled Missed Call notifications that we want to display.
 * 
 * @author Camille Sévigny
 */
public class PhoneReceiverService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor
	 */
	public PhoneReceiverService() {
		super("PhoneReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("PhoneReceiverService.PhoneReceiverService()");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Display the notification for this Missed Call.
	 * 
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		_debug = Log.getDebug();
		if (_debug) Log.v("PhoneReceiverService.doWakefulWork()");
		Context context = getApplicationContext();
		ArrayList<String> missedCallsArray = Common.getMissedCalls(context);
		if(missedCallsArray.size() > 0){
			Bundle bundle = new Bundle();
			bundle.putInt("notificationType", Constants.NOTIFICATION_TYPE_PHONE);
			bundle.putStringArrayList("missedCallsArrayList", missedCallsArray);
	    	Intent phoneNotificationIntent = new Intent(context, NotificationActivity.class);
	    	phoneNotificationIntent.putExtras(bundle);
	    	phoneNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	    	Common.acquirePartialWakeLock(context);
	    	context.startActivity(phoneNotificationIntent);
		}else{
			if (_debug) Log.v("PhoneReceiverService.doWakefulWork() No missed calls were found. Exiting...");
		}
	}
	
}