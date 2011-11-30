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
public class FacebookAlarmBroadcastReceiverService extends WakefulIntentService {
	
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
	public FacebookAlarmBroadcastReceiverService() {
		super("FacebookAlarmBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.FacebookAlarmBroadcastReceiverService()");
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
		if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork()");
		try{
//			Context context = getApplicationContext();
//			//Determine the reminder interval based on the users preferences.
//			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//			//Read preferences and exit if app is disabled.
//		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
//				if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
//				return;
//			}
//			//Block the notification if it's quiet time.
//			if(Common.isQuietTime(context)){
//				if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
//				return;
//			}
//			//Read preferences and exit if Facebook notifications are disabled.
//		    if(!preferences.getBoolean(Constants.FACEBOOK_NOTIFICATIONS_ENABLED_KEY, true)){
//				if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork() Facebook Notifications Disabled. Exiting... ");
//				return;
//			}
//		    ArrayList<String> facebookArray = Common.getFacebookDirectMessages(context);
//		    if(facebookArray != null && facebookArray.size() > 0){
//				Bundle bundle = new Bundle();
//				bundle.putInt("notificationType", Constants.NOTIFICATION_TYPE_FACEBOOK);
//				bundle.putStringArrayList("facebookArrayList", facebookArray);
//		    	Intent facebookNotificationIntent = new Intent(context, NotificationActivity.class);
//		    	facebookNotificationIntent.putExtras(bundle);
//		    	facebookNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//		    	Common.acquireWakeLock(context);
//		    	context.startActivity(facebookNotificationIntent);
//			}else{
//				if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork() No new Facebook Direct Messages were found. Exiting...");
//			}
		}catch(Exception ex){
			if (_debug) Log.e("FacebookAlarmBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}