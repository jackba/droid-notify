package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.facebook.FacebookCommon;
import apps.droidnotify.log.Log;
import apps.droidnotify.twitter.TwitterCommon;

/**
 * This class does the work of the BroadcastReceiver.
 * 
 * @author Camille Sévigny
 */
public class OnBootBroadcastReceiverService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public OnBootBroadcastReceiverService() {
		super("OnBootBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("OnBootBroadcastReceiverService.OnBootBroadcastReceiverService()");
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
		if (_debug) Log.v("OnBootBroadcastReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("OnBootBroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Start Calendar Alarms
		    if(preferences.getBoolean(Constants.CALENDAR_NOTIFICATIONS_ENABLED_KEY, true)){
		    	Common.startCalendarAlarmManager(context, System.currentTimeMillis() + (5 * 60 * 1000));
			}
		    //Start Twitter Alarms
		    if(preferences.getBoolean(Constants.TWITTER_NOTIFICATIONS_ENABLED_KEY, true)){
		    	TwitterCommon.startTwitterAlarmManager(context, System.currentTimeMillis() + (5 * 60 * 1000));
			}
		    //Start Facebook Alarms
		    if(preferences.getBoolean(Constants.FACEBOOK_NOTIFICATIONS_ENABLED_KEY, true)){
		    	FacebookCommon.startFacebookAlarmManager(context, System.currentTimeMillis() + (5 * 60 * 1000));
			}
		}catch(Exception ex){
			Log.e("OnBootBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}