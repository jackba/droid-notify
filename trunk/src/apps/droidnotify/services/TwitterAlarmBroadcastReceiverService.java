package apps.droidnotify.services;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import apps.droidnotify.NotificationActivity;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This class handles the polling of the users Facebook account.
 * 
 * @author Camille Sévigny
 */
public class TwitterAlarmBroadcastReceiverService extends WakefulIntentService {
	
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
			Context context = getApplicationContext();
			//Determine the reminder interval based on the users preferences.
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
			//Read preferences and exit if Twitter notifications are disabled.
		    if(!preferences.getBoolean(Constants.TWITTER_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() Twitter Notifications Disabled. Exiting... ");
				return;
			}
		    ArrayList<String> twitterArray = Common.getTwitterDirectMessages(context);
		    if(twitterArray != null && twitterArray.size() > 0){
				Bundle bundle = new Bundle();
				bundle.putInt("notificationType", Constants.NOTIFICATION_TYPE_TWITTER);
				bundle.putStringArrayList("twitterArrayList", twitterArray);
		    	Intent twitterNotificationIntent = new Intent(context, NotificationActivity.class);
		    	twitterNotificationIntent.putExtras(bundle);
		    	twitterNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		    	Common.acquireWakeLock(context);
		    	context.startActivity(twitterNotificationIntent);
			}else{
				if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() No new Twitter Direct Messages were found. Exiting...");
			}
		}catch(Exception ex){
			if (_debug) Log.e("TwitterAlarmBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}