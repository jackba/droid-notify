package apps.droidnotify.services;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.facebook.android.Facebook;

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
	
	private boolean _debug = false;
	private Context _context = null;
	private SharedPreferences _preferences = null;
	private Facebook _facebook = null;
	private String _accessToken = null;
	private ArrayList<String> _facebookArray = null;

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
			_context = getApplicationContext();
			_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
			//Read preferences and exit if app is disabled.
		    if(!_preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(_context)){
				if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
			//Read preferences and exit if Facebook notifications are disabled.
		    if(!_preferences.getBoolean(Constants.FACEBOOK_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork() Facebook Notifications Disabled. Exiting... ");
				return;
			}
		    //Get Facebook Object.
		    _facebook = Common.getFacebook(_context);
		    if(_facebook == null){
		    	if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork() Facebook object is null. Exiting... ");
		    	return;
		    }
		    _accessToken = _preferences.getString(Constants.FACEBOOK_ACCESS_TOKEN_KEY, null);
		    _facebookArray = new ArrayList<String>();
		    //Get Facebook Messages.
		    getFacebookNotifications();
		    //Get Facebook Friend Requests.
		    getFacebookFriendRequests();
		    if(_facebookArray != null && _facebookArray.size() > 0){
//				Bundle bundle = new Bundle();
//				bundle.putInt("notificationType", Constants.NOTIFICATION_TYPE_FACEBOOK);
//				bundle.putStringArrayList("facebookArrayList", _facebookArray);
//		    	Intent facebookNotificationIntent = new Intent(_context, NotificationActivity.class);
//		    	facebookNotificationIntent.putExtras(bundle);
//		    	facebookNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//		    	Common.acquireWakeLock(_context);
//		    	_context.startActivity(facebookNotificationIntent);
			}else{
				if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork() No new Facebook notifications were found. Exiting...");
			}
		}catch(Exception ex){
			if (_debug) Log.e("FacebookAlarmBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Poll Facebook for any new and unread notifications.
	 */
	private void getFacebookNotifications(){
		if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.getFacebookNotifications()");
        try{
        	Bundle bundle = new Bundle();
            bundle.putString(Facebook.TOKEN, _accessToken);
        	String result = _facebook.request("me/notifications", bundle, "GET");
        	JSONObject jsonObjectResults = new JSONObject(result);
        	JSONArray jsonNotificationDataArray = jsonObjectResults.getJSONArray("data");
        	for (int i=0;i<jsonNotificationDataArray.length();i++)
        	{
        	    JSONObject jsonNotificationData = jsonNotificationDataArray.getJSONObject(i);
        	    if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.getFacebookNotifications() Title: " + jsonNotificationData.getString("title"));

        	}
        }catch(Exception ex){
        	if (_debug) Log.e("FacebookAlarmBroadcastReceiverService.getFacebookNotifications() ERROR: " + ex.toString());
        }
	}

	/**
	 * Poll Facebook for any new friend requests.
	 */
	private void getFacebookFriendRequests(){
		if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.getFacebookFriendRequests()");
        try{
        	Bundle bundle = new Bundle();
            bundle.putString(Facebook.TOKEN, _accessToken);
//        	String result = _facebook.request("me/notifications", bundle, "GET");
//        	JSONObject jsonObjectResults = new JSONObject(result);
//        	JSONArray jsonNotificationDataArray = jsonObjectResults.getJSONArray("data");
//        	for (int i=0;i<jsonNotificationDataArray.length();i++)
//        	{
//        	    JSONObject jsonNotificationData = jsonNotificationDataArray.getJSONObject(i);
//        	    if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.getFacebookNotifications() Title: " + jsonNotificationData.getString("title"));
//
//        	}
        }catch(Exception ex){
        	if (_debug) Log.e("FacebookAlarmBroadcastReceiverService.getFacebookFriendRequests() ERROR: " + ex.toString());
        }
	}
		
}