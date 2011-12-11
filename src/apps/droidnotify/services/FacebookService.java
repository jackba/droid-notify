package apps.droidnotify.services;

import java.util.ArrayList;

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
		    if(_preferences.getBoolean(Constants.FACEBOOK_NOTIFICATIONS_ENABLED_KEY, true)){
			    ArrayList<String> facebookNotificationArray = FacebookCommon.getFacebookNotifications(_context, _accessToken, _facebook);
			    if(facebookNotificationArray != null && facebookNotificationArray.size() > 0){
					Bundle bundle = new Bundle();
					bundle.putInt("notificationType", Constants.NOTIFICATION_TYPE_FACEBOOK);
					bundle.putStringArrayList("facebookArrayList", facebookNotificationArray);
			    	Common.startNotificationActivity(_context, bundle);
				}else{
					if (_debug) Log.v("FacebookService.doWakefulWork() No Facebook Notifications were found. Exiting...");
				}
		    }
		    //Get Facebook Friend Requests.
		    if(_preferences.getBoolean(Constants.FACEBOOK_FRIEND_REQUESTS_ENABLED_KEY, true)){
			    ArrayList<String> facebookFriendRequestArray = FacebookCommon.getFacebookFriendRequests(_context, _accessToken, _facebook);
			    if(facebookFriendRequestArray != null && facebookFriendRequestArray.size() > 0){
					Bundle bundle = new Bundle();
					bundle.putInt("notificationType", Constants.NOTIFICATION_TYPE_FACEBOOK);
					bundle.putStringArrayList("facebookArrayList", facebookFriendRequestArray);
			    	Common.startNotificationActivity(_context, bundle);
				}else{
					if (_debug) Log.v("FacebookService.doWakefulWork() No Facebook Friend Requests were found. Exiting...");
				}
		    }
		}catch(Exception ex){
			if (_debug) Log.e("FacebookService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}