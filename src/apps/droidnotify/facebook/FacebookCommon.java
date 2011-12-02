package apps.droidnotify.facebook;

import com.facebook.android.Facebook;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.receivers.FacebookAlarmReceiver;

/**
 * This class is a collection of Twitter methods.
 * 
 * @author Camille Sévigny
 */
public class FacebookCommon {

	//================================================================================
    // Properties
    //================================================================================
	
	private static boolean _debug = false; 
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Delete a Facebook item.
	 * 
	 * @param context - The current context of this Activity.
	 * @param messageID - The message ID that we want to delete.
	 */
	public static void deleteFacebookItem(Context context, apps.droidnotify.Notification notification){
		_debug = Log.getDebug();
		if (_debug) Log.v("FacebookCommon.deleteFacebookItem()");
		try{
			switch(notification.getNotificationSubType()){
				case Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION:{
					//deleteFacebookNotification(context, notification.getMessageID());
					return;
				}
				case Constants.NOTIFICATION_TYPE_FACEBOOK_MESSAGE:{
					//deleteFacebookMessage(context, notification.getMessageID());
					return;
				}
			}
		}catch(Exception ex){
			if (_debug) Log.e("FacebookCommon.deleteFacebookItem() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Determine if the user has authenticated their Facebook account. 
	 * 
	 * @param context - The application context.
	 *
	 * @return boolean - Return true if the user preferences have Facebook authentication data.
	 */
	public static boolean isFacebookAuthenticated(Context context) {
		_debug = Log.getDebug();
		if (_debug) Log.v("FacebookCommon.isFacebookAuthenticated()");	
		try {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			String accessToken = preferences.getString(Constants.FACEBOOK_ACCESS_TOKEN_KEY, null);
			//long expires = preferences.getLong(Constants.FACEBOOK_ACCESS_EXPIRES_KEY, 0);	
			if(accessToken == null){
				if (_debug) Log.v("FacebookCommon.isFacebookAuthenticated() Facebook stored authentication details are null. Exiting...");
				return false;
			}	
			return true;
		} catch (Exception ex) {
			if (_debug) Log.e("FacebookCommon.isFacebookAuthenticated() ERROR: " + ex.toString());
			return false;
		}
	}

	/**
	 * Initialize and return a Facebook object.
	 * 
	 * @param context - The application context.
	 * 
	 * @return Facebook - The initialized Facebook object or null.
	 */
	public static Facebook getFacebook(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("FacebookCommon.getFacebook()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			Facebook facebook = new Facebook(Constants.FACEBOOK_APP_ID);
		    String accessToken = preferences.getString(Constants.FACEBOOK_ACCESS_TOKEN_KEY, null);
		    long expires = preferences.getLong(Constants.FACEBOOK_ACCESS_EXPIRES_KEY, 0);
		    if(accessToken == null){
				if (_debug) Log.v("FacebookCommon.getFacebook() AccessToken is null. Exiting...");
				return null;
			}
		    if(accessToken != null) {
		    	facebook.setAccessToken(accessToken);
		    }
		    if(expires != 0) {
		    	facebook.setAccessExpires(expires);
		    }
		    if(!facebook.isSessionValid()){
		    	if (_debug) Log.v("FacebookCommon.getFacebook() Facebook object is not valid. Exiting...");
		    	return null;
		    }
			return facebook;
		}catch(Exception ex){
			if (_debug) Log.e("FacebookCommon.getFacebook() ERROR: " + ex.toString());
			return null;
		}	
	}
	
	/**
	 * Start the Facebook recurring alarm.
	 *  
	 * @param context - The application context.
	 * @param alarmStartTime - The time to start the alarm.
	 */
	public static void startFacebookAlarmManager(Context context, long alarmStartTime){
		_debug = Log.getDebug();
		if (_debug) Log.v("FacebookCommon.startFacebookAlarmManager()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, FacebookAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			long pollingFrequency = Long.parseLong(preferences.getString(Constants.FACEBOOK_POLLING_FREQUENCY_KEY, "15")) * 60 * 1000;
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime, pollingFrequency, pendingIntent);
		}catch(Exception ex){
			if (_debug) Log.e("FacebookCommon.startFacebookAlarmManager() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Cancel the Facebook recurring alarm.
	 *  
	 * @param context - The application context.
	 */
	public static void cancelFacebookAlarmManager(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("FacebookCommon.cancelFacebookAlarmManager()");
		try{
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, FacebookAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			alarmManager.cancel(pendingIntent);
		}catch(Exception ex){
			if (_debug) Log.e("FacebookCommon.cancelFacebookAlarmManager() ERROR: " + ex.toString());
		}
	}
	
}
