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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import apps.droidnotify.Notification;
import apps.droidnotify.NotificationActivity;
import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.receivers.FacebookAlarmReceiver;

/**
 * This class is a collection of Facebook methods.
 * 
 * @author Camille Sévigny
 */
public class FacebookCommon {

	//================================================================================
    // Properties
    //================================================================================
	
	private static boolean _debug = false; 
	private static Context _context = null;
	private static SharedPreferences _preferences = null;
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Delete a Facebook item.
	 * 
	 * @param context - The current context of this Activity.
	 * @param messageID - The message ID that we want to delete.
	 */
	public static void deleteFacebookItem(Context context, Notification notification){
		_debug = Log.getDebug();;
		if (_debug) Log.v("FacebookCommon.deleteFacebookItem()");
		try{
			switch(notification.getNotificationSubType()){
				case Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION:{
					//There is no way to delete a Facebook Notification.
					return;
				}
				case Constants.NOTIFICATION_TYPE_FACEBOOK_MESSAGE:{
					//There is no way to delete a Facebook Message.
					return;
				}
				case Constants.NOTIFICATION_TYPE_FACEBOOK_FRIEND_REQUEST:{
					//There is no way to delete a Facebook Friend Request.
					return;
				}
			}
		}catch(Exception ex){
			Log.e("FacebookCommon.deleteFacebookItem() ERROR: " + ex.toString());
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
		_debug = Log.getDebug();;
		if (_debug) Log.v("FacebookCommon.isFacebookAuthenticated()");	
		try {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			String accessToken = preferences.getString(Constants.FACEBOOK_ACCESS_TOKEN_KEY, null);
			if(accessToken == null){
				if (_debug) Log.v("FacebookCommon.isFacebookAuthenticated() Facebook stored authentication details are null. Exiting...");
				return false;
			}	
			return true;
		} catch (Exception ex) {
			Log.e("FacebookCommon.isFacebookAuthenticated() ERROR: " + ex.toString());
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
		_debug = Log.getDebug();;
		if (_debug) Log.v("FacebookCommon.getFacebook()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			Facebook facebook = new Facebook(Constants.FACEBOOK_APP_ID);
		    String accessToken = preferences.getString(Constants.FACEBOOK_ACCESS_TOKEN_KEY, null);
	        long expires = preferences.getLong(Constants.FACEBOOK_ACCESS_EXPIRES_KEY, 0);
		    if(accessToken == null){
				if (_debug) Log.v("FacebookCommon.getFacebook() AccessToken is null. Exiting...");
				return null;
			}else{
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
			Log.e("FacebookCommon.getFacebook() ERROR: " + ex.toString());
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
		_debug = Log.getDebug();;
		if (_debug) Log.v("FacebookCommon.setFacebookAlarm()");
		try{			
			String intentActionText = "apps.droidnotify.alarm/FacebookAlarmReceiverAlarm/" + String.valueOf(System.currentTimeMillis());
			Common.startAlarm(context, FacebookAlarmReceiver.class, null, intentActionText, alarmStartTime);
		}catch(Exception ex){
			Log.e("FacebookCommon.setFacebookAlarm() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Start the Facebook recurring alarm.
	 *  
	 * @param context - The application context.
	 * @param alarmStartTime - The time to start the alarm.
	 */
	public static void startFacebookAlarmManager(Context context, long alarmStartTime){
		_debug = Log.getDebug();;
		if (_debug) Log.v("FacebookCommon.startFacebookAlarmManager()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, FacebookAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			long pollingFrequency = Long.parseLong(preferences.getString(Constants.FACEBOOK_POLLING_FREQUENCY_KEY, "15")) * 60 * 1000;
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime, pollingFrequency, pendingIntent);
		}catch(Exception ex){
			Log.e("FacebookCommon.startFacebookAlarmManager() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Cancel the Facebook recurring alarm.
	 *  
	 * @param context - The application context.
	 */
	public static void cancelFacebookAlarmManager(Context context){
		_debug = Log.getDebug();;
		if (_debug) Log.v("FacebookCommon.cancelFacebookAlarmManager()");
		try{
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, FacebookAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			alarmManager.cancel(pendingIntent);
		}catch(Exception ex){
			Log.e("FacebookCommon.cancelFacebookAlarmManager() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Launch a Facebook application.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the application can be launched.
	 */
	public static boolean startFacebookAppActivity(Context context, NotificationActivity notificationActivity, int requestCode){
		_debug = Log.getDebug();;
		if (_debug) Log.v("FacebookCommon.startFacebookAppActivity()");
		try{
			Intent intent = getFacebookAppActivityIntent(context);
			if(intent == null){
				if (_debug) Log.v("FacebookCommon.startFacebookAppActivity() Application Not Found");
				Toast.makeText(context, context.getString(R.string.facebook_app_not_found_error), Toast.LENGTH_LONG).show();
				Common.setInLinkedAppFlag(context, false);
				return false;
			}
	        notificationActivity.startActivityForResult(intent, requestCode);
	        Common.setInLinkedAppFlag(context, true);
		    return true;
		}catch(Exception ex){
			Log.e("FacebookCommon.startFacebookAppActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.facebook_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
	}

	/**
	 * Get the Intent to launch a Facebook application.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * 
	 * @return Intent - Returns the Intent.
	 */
	public static Intent getFacebookAppActivityIntent(Context context){
		_debug = Log.getDebug();;
		if (_debug) Log.v("FacebookCommon.getFacebookAppActivityIntent()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			String packageName = preferences.getString(Constants.FACEBOOK_PREFERRED_CLIENT_KEY, Constants.FACEBOOK_PREFERRED_CLIENT_DEFAULT);
			if(packageName.startsWith("http://")){
				Intent browserIntent = new Intent(Intent.ACTION_VIEW);	
				browserIntent.setData(Uri.parse(packageName));
				browserIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				return browserIntent;
			}
			Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
			if(intent == null){
				if (_debug) Log.v("FacebookCommon.getFacebookAppActivityIntent() Package '" + packageName + "' Not Found. Exiting...");
				return null;
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        return intent;
		}catch(Exception ex){
			Log.e("FacebookCommon.getFacebookAppActivityIntent() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Poll Facebook for notifications.
	 * 
	 * @param accessToken - The Facebook access token.
	 * @param facebook - The Facebook Object.
	 */
	public static ArrayList<String> getFacebookNotifications(Context context, String accessToken, Facebook facebook){
		_debug = Log.getDebug();;
		if (_debug) Log.v("FacebookCommon.getFacebookNotifications()");
        try{
        	ArrayList<String> facebookArray = new ArrayList<String>();
        	Bundle bundle = new Bundle();
            bundle.putString(Facebook.TOKEN, accessToken);
        	String result = facebook.request("me/notifications", bundle, "GET");
        	//if (_debug) Log.v("FacebookCommon.getFacebookNotifications() Result: " + result);
        	JSONObject jsonResults = new JSONObject(result);
        	if(jsonResults.has("error")){
        		JSONObject jsonError = jsonResults.getJSONObject("error");
        		Log.e("FacebookCommon.getFacebookNotifications() FACAEBOOK API ERROR: " + jsonError.getString("message"));
        		return null;
        	}
        	if(!jsonResults.has("data")){
        		if (_debug) Log.v("FacebookCommon.getFacebookNotifications() No data in the DATA array found. Exiting...");
        		return null;
        	}
        	JSONArray jsonDataArray = jsonResults.getJSONArray("data");
        	int jsonDataArraySize = jsonDataArray.length();
        	for (int i=0;i<jsonDataArraySize;i++){
        	    JSONObject jsonNotificationData = jsonDataArray.getJSONObject(i);
        	    long timeStamp = 0;
        	    if(jsonNotificationData.has("updated_time")){
        	    	timeStamp = parseFacebookDatTime(jsonNotificationData.getString("updated_time"));
        	    }else if(jsonNotificationData.has("created_time")){
        	    	timeStamp = parseFacebookDatTime(jsonNotificationData.getString("created_time"));
        	    }
        	    String notificationText = jsonNotificationData.getString("title");
        	    String notificationExternalLinkURL = jsonNotificationData.getString("link");
				String notificationID = jsonNotificationData.getString("id");
				JSONObject fromFacebookUser = jsonNotificationData.getJSONObject("from");
				String fromFacebookName = fromFacebookUser.getString("name");
				String fromFacebookID = fromFacebookUser.getString("id");
	    		String[] facebookContactInfo = Common.getContactsInfoByName(context, fromFacebookName);
	    		if(facebookContactInfo == null){
	    			facebookArray.add(String.valueOf(Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION) + "|" + fromFacebookName + "|" + fromFacebookID + "|" + notificationText.replace("\n", "<br/>") + "|" + notificationID + "|" + notificationExternalLinkURL + "|" + timeStamp);
				}else{
					facebookArray.add(String.valueOf(Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION) + "|" + fromFacebookName + "|" + fromFacebookID + "|" + notificationText.replace("\n", "<br/>") + "|" + notificationID + "|" + notificationExternalLinkURL + "|" + timeStamp + "|" + facebookContactInfo[0] + "|" + facebookContactInfo[1] + "|" + facebookContactInfo[2] + "|" + facebookContactInfo[3]);
				}	    		
        	}
        	return facebookArray;
        }catch(Exception ex){
        	Log.e("FacebookCommon.getFacebookNotifications() ERROR: " + ex.toString());
        	return null;
        }
	}

	/**
	 * Poll Facebook for friend requests.
	 * 
	 * @param accessToken - The Facebook access token.
	 * @param facebook - The Facebook Object.
	 */
	public static ArrayList<String> getFacebookFriendRequests(Context context, String accessToken, Facebook facebook){
		_debug = Log.getDebug();;
		if (_debug) Log.v("FacebookCommon.getFacebookFriendRequests()");
        try{
        	ArrayList<String> facebookArray = new ArrayList<String>();
        	Bundle bundle = new Bundle();
            bundle.putString(Facebook.TOKEN, accessToken);
        	String result = facebook.request("me/friendrequests", bundle, "GET");
        	//if (_debug) Log.v("FacebookCommon.getFacebookFriendRequests() Result: " + result);
        	JSONObject jsonResults = new JSONObject(result);
        	if(jsonResults.has("error")){
        		JSONObject jsonError = jsonResults.getJSONObject("error");
        		Log.e("FacebookCommon.getFacebookNotifications() FACAEBOOK API ERROR: " + jsonError.getString("message"));
        		return null;
        	}
        	if(!jsonResults.has("data")){
        		if (_debug) Log.v("FacebookCommon.getFacebookFriendRequests() No data in the DATA array found. Exiting...");
        		return null;
        	}
        	JSONArray jsonDataArray = jsonResults.getJSONArray("data");
        	int jsonDataArraySize = jsonDataArray.length();
        	for (int i=0;i<jsonDataArraySize;i++){
        	    JSONObject jsonFriendRequestData = jsonDataArray.getJSONObject(i);
        	    long timeStamp = 0;
        	    if(jsonFriendRequestData.has("updated_time")){
        	    	timeStamp = parseFacebookDatTime(jsonFriendRequestData.getString("updated_time"));
        	    }else if(jsonFriendRequestData.has("created_time")){
        	    	timeStamp = parseFacebookDatTime(jsonFriendRequestData.getString("created_time"));
        	    }
				String friendRequestID = "0";
				JSONObject fromFacebookUser = jsonFriendRequestData.getJSONObject("from");
				String fromFacebookName = fromFacebookUser.getString("name");
				String fromFacebookID = fromFacebookUser.getString("id");
        	    String friendRequestText = "You have a new Facebook Friend Request from " + fromFacebookName;
	    		String[] facebookContactInfo = Common.getContactsInfoByName(context, fromFacebookName);
	    		if(facebookContactInfo == null){
	    			facebookArray.add(String.valueOf(Constants.NOTIFICATION_TYPE_FACEBOOK_FRIEND_REQUEST) + "|" + fromFacebookName + "|" + fromFacebookID + "|" + friendRequestText + "|" + friendRequestID + "||" + timeStamp);
				}else{
					facebookArray.add(String.valueOf(Constants.NOTIFICATION_TYPE_FACEBOOK_FRIEND_REQUEST) + "|" + fromFacebookName + "|" + fromFacebookID + "|" + friendRequestText + "|" + friendRequestID + "||" + timeStamp + "|" + facebookContactInfo[0] + "|" + facebookContactInfo[1] + "|" + facebookContactInfo[2] + "|" + facebookContactInfo[3]);
				}
        	}
        	return facebookArray;
        }catch(Exception ex){
        	Log.e("FacebookCommon.getFacebookFriendRequests() ERROR: " + ex.toString());
        	return null;
        }
	}

	/**
	 * Poll Facebook for messages.
	 * 
	 * @param accessToken - The Facebook access token.
	 * @param facebook - The Facebook Object.
	 */
	public static ArrayList<String> getFacebookMessages(Context context, String accessToken, Facebook facebook){
		_debug = Log.getDebug();;
		if (_debug) Log.v("FacebookCommon.getFacebookMessages()");
        try{
        	ArrayList<String> facebookArray = new ArrayList<String>();
        	Bundle bundle = new Bundle();
            bundle.putString(Facebook.TOKEN, accessToken);
        	String result = facebook.request("me/inbox", bundle, "GET");
        	//if (_debug) Log.v("FacebookCommon.getFacebookMessages() Result: " + result);
        	JSONObject jsonResults = new JSONObject(result);
        	if(jsonResults.has("error")){
        		JSONObject jsonError = jsonResults.getJSONObject("error");
        		Log.e("FacebookCommon.getFacebookNotifications() FACAEBOOK API ERROR: " + jsonError.getString("message"));
        		return null;
        	}
        	if(!jsonResults.has("data")){
        		if (_debug) Log.v("FacebookCommon.getFacebookMessages() No data in the DATA array found. Exiting...");
        		return null;
        	}
        	JSONArray jsonDataArray = jsonResults.getJSONArray("data");
        	int jsonDataArraySize = jsonDataArray.length();
        	for (int i=0;i<jsonDataArraySize;i++){
        	    JSONObject jsonMessageData = jsonDataArray.getJSONObject(i);
        	    int unreadFlag = jsonMessageData.getInt("unread");
        	    //int unseenFlag = jsonMessageData.getInt("unseen");
        	    if(unreadFlag > 0){
    				JSONObject originalFromFacebookUser = jsonMessageData.getJSONObject("from");
    				String originalFromFacebookName = originalFromFacebookUser.getString("name");
    				String originalFromFacebookID = originalFromFacebookUser.getString("id");
    				//Original/Start message details.
            	    long originalTimeStamp = 0;       	    
            	    if(jsonMessageData.has("updated_time")){
            	    	originalTimeStamp = parseFacebookDatTime(jsonMessageData.getString("updated_time"));
            	    }else if(jsonMessageData.has("created_time")){
            	    	originalTimeStamp = parseFacebookDatTime(jsonMessageData.getString("created_time"));
            	    }
            	    String originalMessageStringID = jsonMessageData.getString("id");
            	    String originalMessageText = jsonMessageData.getString("message");
					boolean commentsExist = true;
					JSONObject messageComments = null;
					if(jsonMessageData.has("comments")){
						messageComments = jsonMessageData.getJSONObject("comments");
						commentsExist = true;
					}else{
						messageComments = null;
						commentsExist = false;
					}
					if(commentsExist){
						//Multiple messages in thread.
		        	    JSONArray jsonCommentsDataArray = messageComments.getJSONArray("data");
	        	    	//Get latest/most recent message details.
	        	    	int jsonCommentsDataArraySize = jsonCommentsDataArray.length();
	        	    	for(int j=jsonCommentsDataArraySize-unreadFlag;j<jsonCommentsDataArraySize;j++){
	        	    		JSONObject jsonCommentMessageData = jsonCommentsDataArray.getJSONObject(j);
	        	    		long timeStamp = parseFacebookDatTime(jsonCommentMessageData.getString("created_time"));
	        	    		String messageStringID = jsonCommentMessageData.getString("id");
	        	    		String messageText = jsonCommentMessageData.getString("message");
	        	    		//Sent From User Info
	        	    		JSONObject fromFacebookUser = jsonCommentMessageData.getJSONObject("from");
	        				String fromFacebookName = fromFacebookUser.getString("name");
	        				String fromFacebookID = fromFacebookUser.getString("id");
		    	    		String[] facebookContactInfo = Common.getContactsInfoByName(context, fromFacebookName);
				    		if(facebookContactInfo == null){
				    			facebookArray.add(String.valueOf(Constants.NOTIFICATION_TYPE_FACEBOOK_MESSAGE) + "|" + fromFacebookName + "|" + fromFacebookID + "|" + messageText + "|" + messageStringID + "||" + timeStamp);
							}else{
								facebookArray.add(String.valueOf(Constants.NOTIFICATION_TYPE_FACEBOOK_MESSAGE) + "|" + fromFacebookName + "|" + fromFacebookID + "|" + messageText + "|" + messageStringID + "||" + timeStamp + "|" + facebookContactInfo[0] + "|" + facebookContactInfo[1] + "|" + facebookContactInfo[2] + "|" + facebookContactInfo[3]);
							}			        	    
		        	    }
					}else{
						//Single message.
						long timeStamp = originalTimeStamp;
						String messageStringID = originalMessageStringID;
	    	    		String messageText = originalMessageText;
        	    		//Sent From User Info
	    	    		String fromFacebookName = originalFromFacebookName;
	    	    		String fromFacebookID = originalFromFacebookID;
	    	    		String[] facebookContactInfo = Common.getContactsInfoByName(context, fromFacebookName);
			    		if(facebookContactInfo == null){
			    			facebookArray.add(String.valueOf(Constants.NOTIFICATION_TYPE_FACEBOOK_MESSAGE) + "|" + fromFacebookName + "|" + fromFacebookID + "|" + messageText + "|" + messageStringID + "||" + timeStamp);
						}else{
							facebookArray.add(String.valueOf(Constants.NOTIFICATION_TYPE_FACEBOOK_MESSAGE) + "|" + fromFacebookName + "|" + fromFacebookID + "|" + messageText + "|" + messageStringID + "||" + timeStamp + "|" + facebookContactInfo[0] + "|" + facebookContactInfo[1] + "|" + facebookContactInfo[2] + "|" + facebookContactInfo[3]);
						}
					}
        		}
        	}
        	return facebookArray;
        }catch(Exception ex){
        	Log.e("FacebookCommon.getFacebookMessages() ERROR: " + ex.toString());
        	return null;
        }
	}
	
	/**
	 * Mark a single FAcebook notification as being read or not.
	 * 
	 * @param context - The current context of this Activity.
	 * @param notificationID - The Notification ID that we want to alter.
	 * @param isViewed - The boolean value indicating if it was read or not.
	 * 
	 * @return boolean - Returns true if the notification was updated successfully.
	 */
	public static boolean setFacebookNotificationRead(Context context, String notificationID, boolean isViewed){
		_debug = Log.getDebug();;
		if (_debug) Log.v("FacebookCommon.setFacebookNotificationRead()");
		try{
			if(notificationID == null || notificationID.equals("")){
				if (_debug) Log.v("FacebookCommon.setFacebookNotificationRead() Notification ID == null/empty. Exiting...");
				return false;
			}
			_context = context;
			_preferences = PreferenceManager.getDefaultSharedPreferences(context);
			String unreadParameter = "1";
			if(isViewed){
				unreadParameter = "0";
			}
			new setFacebookNotificationReadAsyncTask().execute(unreadParameter, notificationID);
			return true;
		}catch(Exception ex){
			Log.e("FacebookCommon.setFacebookNotificationRead() ERROR: " + ex.toString());
			return false;
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
	
	/**
	 * Mark a notification post as being read or not through the Facebook SDK/API.
	 * 
	 * @author Camille Sévigny
	 */
	private static class setFacebookNotificationReadAsyncTask extends AsyncTask<String, Void, Boolean> {
	    
	    /**
	     * Do this work in the background.
	     * 
	     * @param params - The boolean value to set the notification flag with.
	     */
	    protected Boolean doInBackground(String... params) {
			if (_debug) Log.v("FacebookCommon.setFacebookNotificationReadAsyncTask.doInBackground()");
			try{
			    //Get Facebook Object
			    Facebook facebook = FacebookCommon.getFacebook(_context);
			    if(facebook == null){
			    	if (_debug) Log.v("FacebookCommon.setFacebookNotificationReadAsyncTask.doInBackground() Facebook object is null. Exiting... ");
			    	return false;
			    }
				String unreadParameter = params[0];
				String notificationID = params[1];
			    String accessToken = _preferences.getString(Constants.FACEBOOK_ACCESS_TOKEN_KEY, null);
	        	Bundle parameters = new Bundle();
	        	parameters.putString(Facebook.TOKEN, accessToken);
	        	parameters.putString("unread", unreadParameter);
	        	String result = facebook.request(notificationID, parameters, "POST");
	        	if (result == null || result.equals("") || result.equals("false")) {
	        		if (_debug) Log.v("FacebookCommon.setFacebookNotificationReadAsyncTask.doInBackground() Facebook API Post Failed. API Response: " + result);
	            	return false;
	        	}
	        	return true;
			}catch(Exception ex){
				Log.e("FacebookCommon.setFacebookNotificationReadAsyncTask.doInBackground() ERROR: " + ex.toString());
		    	return false;
			}
	    }
	    
	    /**
	     * Display a message if the Notification update encountered an error.
	     * 
	     * @param result - Boolean indicating success.
	     */
	    protected void onPostExecute(Boolean result) {
			if (_debug) Log.v("FacebookCommon.setFacebookNotificationReadAsyncTask.onPostExecute() RESULT: " + result);
			if(result){
				//Do Nothing
			}else{
				//Toast.makeText(_context, _context.getString(R.string.facebook_update_notification_read_status_error), Toast.LENGTH_LONG).show();
			}
	    }
	    
	}
	
}
