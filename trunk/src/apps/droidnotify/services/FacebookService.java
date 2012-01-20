package apps.droidnotify.services;

import com.facebook.android.Facebook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.facebook.FacebookCommon;

/**
 * This class handles the work of processing incoming Twitter messages.
 * 
 * @author Camille Sévigny
 */
public class FacebookService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;
	private Context _context = null;
	private SharedPreferences _preferences = null;
	private Facebook _facebook = null;
	private String _accessToken = null;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public FacebookService() {
		super("FacebookService");
		_debug = Log.getDebug();
		if (_debug) Log.v("FacebookService.FacebookService()");
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
		if (_debug) Log.v("FacebookService.doWakefulWork()");
		try{
			_context = getApplicationContext();
		    //Get Facebook Object
		    _facebook = FacebookCommon.getFacebook(_context);
		    if(_facebook == null){
		    	if (_debug) Log.v("FacebookService.doWakefulWork() Facebook object is null. Exiting... ");
		    	return;
		    }
			_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
		    _accessToken = _preferences.getString(Constants.FACEBOOK_ACCESS_TOKEN_KEY, null);
		    //Get Facebook Notifications.
		    if(_preferences.getBoolean(Constants.FACEBOOK_USER_NOTIFICATIONS_ENABLED_KEY, true)){
		    	Bundle facebookNotificationNotificationBundle = FacebookCommon.getFacebookNotifications(_context, _accessToken, _facebook);
			    if(facebookNotificationNotificationBundle != null){
					Bundle bundle = new Bundle();
					bundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_FACEBOOK);
					bundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, facebookNotificationNotificationBundle);
			    	Common.startNotificationActivity(_context, bundle);
				}else{
					if (_debug) Log.v("FacebookService.doWakefulWork() No Facebook Notifications were found. Exiting...");
				}
		    }
		    //Get Facebook Friend Requests.
		    if(_preferences.getBoolean(Constants.FACEBOOK_FRIEND_REQUESTS_ENABLED_KEY, true)){
		    	Bundle facebookFriendRequestNotificationBundle = FacebookCommon.getFacebookFriendRequests(_context, _accessToken, _facebook);
			    if(facebookFriendRequestNotificationBundle != null){
					Bundle bundle = new Bundle();
					bundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_FACEBOOK);
					bundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, facebookFriendRequestNotificationBundle);
			    	Common.startNotificationActivity(_context, bundle);
				}else{
					if (_debug) Log.v("FacebookService.doWakefulWork() No Facebook Friend Requests were found. Exiting...");
				}
		    }
		    //Get Facebook Messages.
		    if(_preferences.getBoolean(Constants.FACEBOOK_MESSAGES_ENABLED_KEY, true)){
			    Bundle facebookMessageNotificationBundle = FacebookCommon.getFacebookMessages(_context, _accessToken, _facebook);
			    if(facebookMessageNotificationBundle != null){
					Bundle bundle = new Bundle();
					bundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_FACEBOOK);
					bundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, facebookMessageNotificationBundle);
			    	Common.startNotificationActivity(_context, bundle);
				}else{
					if (_debug) Log.v("FacebookService.doWakefulWork() No Facebook Messages were found. Exiting...");
				}
		    }
		}catch(Exception ex){
			Log.e("FacebookService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}