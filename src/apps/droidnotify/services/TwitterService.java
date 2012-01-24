package apps.droidnotify.services;

import twitter4j.Twitter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.twitter.TwitterCommon;

/**
 * This class handles the work of processing incoming Twitter messages.
 * 
 * @author Camille Sévigny
 */
public class TwitterService extends WakefulIntentService {
	
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
	public TwitterService() {
		super("TwitterService");
		_debug = Log.getDebug();
		if (_debug) Log.v("TwitterService.TwitterService()");
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
		if (_debug) Log.v("TwitterService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			//Get Twitter Object
			Twitter twitter = TwitterCommon.getTwitter(context);
			if(twitter == null){
				if (_debug) Log.v("TwitterService.doWakefulWork() Twitter object is null. Exiting...");
				return;
			}
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			if(preferences.getBoolean(Constants.TWITTER_DIRECT_MESSAGES_ENABLED_KEY, true)){
				Bundle twitterDirectMessageNotificationBundle = TwitterCommon.getTwitterDirectMessages(context, twitter);
			    if(twitterDirectMessageNotificationBundle != null){
					Bundle bundle = new Bundle();
					bundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_TWITTER);
					bundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, twitterDirectMessageNotificationBundle);
					Common.startNotificationActivity(context, bundle);
				}else{
					if (_debug) Log.v("TwitterService.doWakefulWork() No Twitter Direct Messages were found. Exiting...");
				}
			}
			if(preferences.getBoolean(Constants.TWITTER_MENTIONS_ENABLED_KEY, true)){
				Bundle twitterMentionNotificationBundle = TwitterCommon.getTwitterMentions(context, twitter);
			    if(twitterMentionNotificationBundle != null){
					Bundle bundle = new Bundle();
					bundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_TWITTER);
					bundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, twitterMentionNotificationBundle);
					Common.startNotificationActivity(context, bundle);
				}else{
					if (_debug) Log.v("TwitterService.doWakefulWork() No Twitter Mentions were found. Exiting...");
				}
			}
			if(preferences.getBoolean(Constants.TWITTER_FOLLOWER_REQUESTS_ENABLED_KEY, true)){
				Bundle twitterFollowerRequestNotificationBundle = TwitterCommon.getTwitterFollowerRequests(context, twitter);
			    if(twitterFollowerRequestNotificationBundle != null){
					Bundle bundle = new Bundle();
					bundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_TWITTER);
					bundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, twitterFollowerRequestNotificationBundle);
					Common.startNotificationActivity(context, bundle);
				}else{
					if (_debug) Log.v("TwitterService.doWakefulWork() No Twitter Follower Requests were found. Exiting...");
				}
			}
		}catch(Exception ex){
			Log.e("TwitterService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}
