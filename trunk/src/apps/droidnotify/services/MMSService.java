package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import apps.droidnotify.NotificationActivity;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.sms.SMSCommon;

/**
 * This class handles the work of processing incoming MMS messages.
 * 
 * @author Camille Sévigny
 */
public class MMSService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public MMSService() {
		super("MMSReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("MMSReceiverService.MMSReceiverService()");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Do the work for the service inside this function.
	 * 
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		if (_debug) Log.v("MMSReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			Bundle mmsNotificationBundle = SMSCommon.getMMSMessagesFromDisk(context);
			if(mmsNotificationBundle != null){
				Bundle bundle = new Bundle();
				bundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_MMS);
				bundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, mmsNotificationBundle);
		    	Intent mmsNotificationIntent = new Intent(context, NotificationActivity.class);
		    	mmsNotificationIntent.putExtras(bundle);
		    	mmsNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		    	Common.acquireWakeLock(context);
		    	context.startActivity(mmsNotificationIntent);
			}else{
				if (_debug) Log.v("MMSReceiverService.doWakefulWork() No new MMSs were found. Exiting...");
			}
		}catch(Exception ex){
			Log.e("MMSReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
	
}