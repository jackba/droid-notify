package apps.droidnotify.facebook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.facebook.android.Facebook;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import apps.droidnotify.common.Common;
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
	 * Start a single Facebook alarm.
	 *  
	 * @param context - The application context.
	 * @param alarmStartTime - The time to start the alarm.
	 */
	public static void setFacebookAlarm(Context context, long alarmStartTime){
		_debug = Log.getDebug();
		if (_debug) Log.v("FacebookCommon.setFacebookAlarm()");
		try{
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, FacebookAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			alarmManager.set(AlarmManager.RTC_WAKEUP, alarmStartTime, pendingIntent);
		}catch(Exception ex){
			if (_debug) Log.e("FacebookCommon.setFacebookAlarm() ERROR: " + ex.toString());
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
	
	/**
	 * Poll Facebook for notifications.
	 * 
	 * @param accessToken - The Facebook acess token.
	 * @param facebook - The Facebook Object.
	 */
	public static ArrayList<String> getFacebookNotifications(Context context, String accessToken, Facebook facebook){
		if (_debug) Log.v("FacebookService.getFacebookNotifications()");
        try{
        	ArrayList<String> facebookArray = new ArrayList<String>();
        	Bundle bundle = new Bundle();
            bundle.putString(Facebook.TOKEN, accessToken);
        	String result = facebook.request("me/notifications", bundle, "GET");
        	if (_debug) Log.v("FacebookService.getFacebookNotifications() Result: " + result);
        	JSONObject jsonResults = new JSONObject(result);
        	JSONArray jsonDataArray = jsonResults.getJSONArray("data");
        	int jsonDataArraySize = jsonDataArray.length();
        	for (int i=0;i<jsonDataArraySize;i++){
        	    JSONObject jsonNotificationData = jsonDataArray.getJSONObject(i);
        	    long timeStamp = parseFacebookDatTime(jsonNotificationData.getString("created_time"));
        	    String notificationText = jsonNotificationData.getString("title");
        	    String notificationExternalLink = jsonNotificationData.getString("link");
				String notificationID = jsonNotificationData.getString("id");
				JSONObject fromFacebookUser = jsonNotificationData.getJSONObject("from");
				String fromFacebookName = fromFacebookUser.getString("name");
				String fromFacebookID = fromFacebookUser.getString("id");				
	    		String[] facebookContactInfo = Common.getContactsInfoByName(context, fromFacebookName);
	    		if(facebookContactInfo == null){
	    			facebookArray.add(String.valueOf(Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION) + "|" + fromFacebookName + "|" + fromFacebookID + "|" + notificationText.replace("\n", "<br/>") + "|" + notificationID + "|" + timeStamp);
				}else{
					facebookArray.add(String.valueOf(Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION) + "|" + fromFacebookName + "|" + fromFacebookID + "|" + notificationText.replace("\n", "<br/>") + "|" + notificationID + "|" + timeStamp + "|" + facebookContactInfo[0] + "|" + facebookContactInfo[1] + "|" + facebookContactInfo[2] + "|" + facebookContactInfo[3]);
				}	    		
        	}
        	return facebookArray;
        }catch(Exception ex){
        	if (_debug) Log.e("FacebookService.getFacebookNotifications() ERROR: " + ex.toString());
        	return null;
        }
	}

	/**
	 * Poll Facebook for friend requests.
	 * 
	 * @param accessToken - The Facebook acess token.
	 * @param facebook - The Facebook Object.
	 */
	public static ArrayList<String> getFacebookFriendRequests(Context context, String accessToken, Facebook facebook){
		if (_debug) Log.v("FacebookService.getFacebookFriendRequests()");
        try{
        	ArrayList<String> facebookArray = new ArrayList<String>();
        	Bundle bundle = new Bundle();
            bundle.putString(Facebook.TOKEN, accessToken);
        	String result = facebook.request("me/friendrequests", bundle, "GET");
        	if (_debug) Log.v("FacebookService.getFacebookFriendRequests() Result: " + result);
        	JSONObject jsonResults = new JSONObject(result);
        	JSONObject jsonSummaryData = jsonResults.getJSONObject("summary");
        	if (_debug) Log.v("FacebookService.getFacebookFriendRequests() TotalCount: " + jsonSummaryData.getInt("total_count"));
        	if (_debug) Log.v("FacebookService.getFacebookFriendRequests() UnreadCount: " + jsonSummaryData.getInt("unread_count"));
        	JSONArray jsonDataArray = jsonResults.getJSONArray("data");
        	int jsonDataArraySize = jsonDataArray.length();
        	for (int i=0;i<jsonDataArraySize;i++){
        	    JSONObject jsonNotificationData = jsonDataArray.getJSONObject(i);
        	    long timeStamp = parseFacebookDatTime(jsonNotificationData.getString("created_time"));
				String notificationID = "0";
				JSONObject fromFacebookUser = jsonNotificationData.getJSONObject("from");
				String fromFacebookName = fromFacebookUser.getString("name");
				String fromFacebookID = fromFacebookUser.getString("id");
        	    String friendRequestText = "You have a new Facebook Friend Request from " + fromFacebookName;
	    		String[] facebookContactInfo = Common.getContactsInfoByName(context, fromFacebookName);
	    		if(facebookContactInfo == null){
	    			facebookArray.add(String.valueOf(Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION) + "|" + fromFacebookName + "|" + fromFacebookID + "|" + friendRequestText + "|" + notificationID + "|" + timeStamp);
				}else{
					facebookArray.add(String.valueOf(Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION) + "|" + fromFacebookName + "|" + fromFacebookID + "|" + friendRequestText + "|" + notificationID + "|" + timeStamp + "|" + facebookContactInfo[0] + "|" + facebookContactInfo[1] + "|" + facebookContactInfo[2] + "|" + facebookContactInfo[3]);
				}
        	}
        	return facebookArray;
        }catch(Exception ex){
        	if (_debug) Log.e("FacebookService.getFacebookFriendRequests() ERROR: " + ex.toString());
        	return null;
        }
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * 
	 * @param inputDateTime
	 * 
	 * @return
	 */
	private static long parseFacebookDatTime(String inputDateTime){
		if (_debug) Log.v("FacebookCommon.parseFacebookDatTime()");
		try {
		    long timeMillis = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+SSSS")
		        .parse(inputDateTime)
		        .getTime();
		    return timeMillis;
		}catch (Exception ex){
			if (_debug) Log.v("FacebookCommon.parseFacebookDatTime() ERROR: " + ex.toString());
		    return 0;
		}
	}
	
}
