package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

public class TwitterAlarmBroadcastReceiverService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	boolean _debug = false;
	private Context _context = null;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public TwitterAlarmBroadcastReceiverService() {
		super("TwitterAlarmBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.TwitterAlarmBroadcastReceiverService()");
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
		if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork()");
		try{
			_context = getApplicationContext();
			//Determine the reminder interval based on the users preferences.
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(_context)){
				if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
			//Read preferences and exit if Twitter notifications are disabled.
		    if(!preferences.getBoolean(Constants.CALENDAR_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() Twitter Notifications Disabled. Exiting... ");
				return;
			}
		    processTwitterNotifications();
		}catch(Exception ex){
			if (_debug) Log.e("TwitterAlarmBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Process the Twitter notifications. Read account and notify as needed.
	 */
	private void processTwitterNotifications(){
		if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.processTwitterNotifications()");
		try{
		
			
			
			
			
		}catch(Exception ex){
			if (_debug) Log.e("TwitterAlarmBroadcastReceiverService.processTwitterNotifications() ERROR: " + ex.toString());
		}
	}
		
}