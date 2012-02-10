package apps.droidnotify.linkedin;

import java.util.List;

import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.schema.Network;
import com.google.code.linkedinapi.schema.Update;
import com.google.code.linkedinapi.schema.Updates;

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
import apps.droidnotify.receivers.LinkedInAlarmReceiver;

/**
 * This class is a collection of LinkedIn methods.
 * 
 * @author Camille Sévigny
 */
public class LinkedInCommon {

	//================================================================================
    // Properties
    //================================================================================
	
	private static boolean _debug = false;
	
	//================================================================================
	// Public Methods
	//================================================================================
		
	/**
	 * Get LinkedIn Updates. Read account and notify as needed.
	 * 
	 * @param context - The application context.
	 * 
	 * @return ArrayList<String> - Returns an ArrayList of Strings that contain the LinkedIn information.
	 */
	public static Bundle getLinkedInupdates(Context context, LinkedInApiClient linkedInClient){
		_debug = Log.getDebug();
		if (_debug) Log.v("LinkedInCommon.getLinkedInupdates()");
		try{
        	Bundle linkedInNotificationNotificationBundle = new Bundle();
        	int bundleCount = 0;
		    Network results = linkedInClient.getUserUpdates();
		    Updates updateResults = results.getUpdates();
		    List<Update> updateList = updateResults.getUpdateList();
		    int updateListSize = updateList.size();
		    for(int i = 1; i< updateListSize; i++){
        		Bundle linkedInNotificationNotificationBundleSingle = new Bundle();
        		bundleCount++;
		    	Update linkedInUpdate = updateList.get(i);
		    	if (_debug) Log.v("LinkedInCommon.getLinkedInupdates() LinkedInUpdate.getTimestamp(): " + linkedInUpdate.getTimestamp());
//		    	String notificationText = jsonNotificationData.getString("title");
//        	    String notificationExternalLinkURL = jsonNotificationData.getString("link");
//				String notificationID = jsonNotificationData.getString("id");
//				JSONObject fromFacebookUser = jsonNotificationData.getJSONObject("from");
//				String fromFacebookName = fromFacebookUser.getString("name");
//				String fromFacebookID = fromFacebookUser.getString("id");
//	    		Bundle facebookContactInfoBundle = ContactsCommon.getContactsInfoByName(context, fromFacebookName);
//	    		if(facebookContactInfoBundle == null){
//					//Basic Notification Information.
//	    			facebookNotificationNotificationBundleSingle.putString(Constants.BUNDLE_SENT_FROM_ADDRESS, fromFacebookName);
//	    			facebookNotificationNotificationBundleSingle.putLong(Constants.BUNDLE_SENT_FROM_ID, Long.parseLong(fromFacebookID));
//	    			facebookNotificationNotificationBundleSingle.putString(Constants.BUNDLE_MESSAGE_BODY, notificationText.replace("\n", "<br/>"));
//	    			facebookNotificationNotificationBundleSingle.putString(Constants.BUNDLE_MESSAGE_STRING_ID, notificationID);
//	    			facebookNotificationNotificationBundleSingle.putString(Constants.BUNDLE_LINK_URL, notificationExternalLinkURL.replace("http://www.facebook.com/", "http://m.facebook.com/"));
//	    			facebookNotificationNotificationBundleSingle.putLong(Constants.BUNDLE_TIMESTAMP, timeStamp);	    			
//	    			facebookNotificationNotificationBundleSingle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_FACEBOOK);
//	    			facebookNotificationNotificationBundleSingle.putInt(Constants.BUNDLE_NOTIFICATION_SUB_TYPE, Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION);
//				}else{
//					//Basic Notification Information.
//	    			facebookNotificationNotificationBundleSingle.putString(Constants.BUNDLE_SENT_FROM_ADDRESS, fromFacebookName);
//	    			facebookNotificationNotificationBundleSingle.putLong(Constants.BUNDLE_SENT_FROM_ID, Long.parseLong(fromFacebookID));
//	    			facebookNotificationNotificationBundleSingle.putString(Constants.BUNDLE_MESSAGE_BODY, notificationText.replace("\n", "<br/>"));
//	    			facebookNotificationNotificationBundleSingle.putString(Constants.BUNDLE_MESSAGE_STRING_ID, notificationID);
//	    			facebookNotificationNotificationBundleSingle.putString(Constants.BUNDLE_LINK_URL, notificationExternalLinkURL.replace("http://www.facebook.com/", "http://m.facebook.com/"));
//	    			facebookNotificationNotificationBundleSingle.putLong(Constants.BUNDLE_TIMESTAMP, timeStamp);	    			
//	    			facebookNotificationNotificationBundleSingle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_FACEBOOK);
//	    			facebookNotificationNotificationBundleSingle.putInt(Constants.BUNDLE_NOTIFICATION_SUB_TYPE, Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION);
//	    			//Contact Information.
//					facebookNotificationNotificationBundleSingle.putLong(Constants.BUNDLE_CONTACT_ID, facebookContactInfoBundle.getLong(Constants.BUNDLE_CONTACT_ID, -1));
//					facebookNotificationNotificationBundleSingle.putString(Constants.BUNDLE_CONTACT_NAME, facebookContactInfoBundle.getString(Constants.BUNDLE_CONTACT_NAME));
//					facebookNotificationNotificationBundleSingle.putLong(Constants.BUNDLE_PHOTO_ID, facebookContactInfoBundle.getLong(Constants.BUNDLE_PHOTO_ID, -1));
//					facebookNotificationNotificationBundleSingle.putString(Constants.BUNDLE_LOOKUP_KEY, facebookContactInfoBundle.getString(Constants.BUNDLE_LOOKUP_KEY));
//				}
	    		linkedInNotificationNotificationBundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_" + String.valueOf(bundleCount), linkedInNotificationNotificationBundleSingle);		
			}
			if(bundleCount <= 0){
				if (_debug) Log.v("LinkedInCommon.getLinkedInupdates() No LinkedIn Updates Found. Exiting...");
				return null;
			}
			linkedInNotificationNotificationBundle.putInt(Constants.BUNDLE_NOTIFICATION_BUNDLE_COUNT, bundleCount);
		    return linkedInNotificationNotificationBundle;
		}catch(Exception ex){
			if (_debug) Log.e("LinkedInCommon.getLinkedInupdates() ERROR: " + ex.toString());
			return null;
		}
	}
	
//	/**
//	 * Launch a LinkedIn application.
//	 * 
//	 * @param context - Application Context.
//	 * @param notificationActivity - A reference to the parent activity.
//	 * @param requestCode - The request code we want returned.
//	 * 
//	 * @return boolean - Returns true if the application can be launched.
//	 */
//	public static boolean startLinkedInAppActivity(Context context, NotificationActivity notificationActivity, int requestCode){
//		_debug = Log.getDebug();
//		if (_debug) Log.v("LinkedInCommon.startLinkedInAppActivity()");
//		try{
//			Intent intent = getLinkedInAppActivityIntent(context);
//			if(intent == null){
//				if (_debug) Log.v("LinkedInCommon.startLinkedInAppActivity() Application Not Found");
//				Toast.makeText(context, context.getString(R.string.LINKEDIN_app_not_found_error), Toast.LENGTH_LONG).show();
//				Common.setInLinkedAppFlag(context, false);
//				return false;
//			}
//	        notificationActivity.startActivityForResult(intent, requestCode);
//	        Common.setInLinkedAppFlag(context, true);
//		    return true;
//		}catch(Exception ex){
//			if (_debug) Log.e("LinkedInCommon.startLinkedInAppActivity() ERROR: " + ex.toString());
//			Toast.makeText(context, context.getString(R.string.LINKEDIN_app_error), Toast.LENGTH_LONG).show();
//			Common.setInLinkedAppFlag(context, false);
//			return false;
//		}
//	}

//	/**
//	 * Get the Intent to launch a LinkedIn application.
//	 * 
//	 * @param context - Application Context.
//	 * @param notificationActivity - A reference to the parent activity.
//	 * 
//	 * @return Intent - Returns the Intent.
//	 */
//	public static Intent getLinkedInAppActivityIntent(Context context){
//		_debug = Log.getDebug();
//		if (_debug) Log.v("LinkedInCommon.getLinkedInAppActivityIntent()");
//		try{
//			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//			String packageName = preferences.getString(Constants.LINKEDIN_PREFERRED_CLIENT_KEY, Constants.LINKEDIN_PREFERRED_CLIENT_DEFAULT);
//			Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//	        return intent;
//		}catch(Exception ex){
//			if (_debug) Log.e("LinkedInCommon.getLinkedInAppActivityIntent() ERROR: " + ex.toString());
//			return null;
//		}
//	}
	
//	/**
//	 * Start the intent for the Quick Reply activity send a reply.
//	 * 
//	 * @param context - Application Context.
//	 * @param notificationActivity - A reference to the parent activity.
//	 * @param phoneNumber - The phone number we want to send a message to.
//	 * @param requestCode - The request code we want returned.
//	 * 
//	 * @return boolean - Returns true if the activity can be started.
//	 */
//	public static boolean startLinkedInQuickReplyActivity(Context context, NotificationActivity notificationActivity, int requestCode, long sendToID, String sendTo, String name, int notificationSubType){
//		_debug = Log.getDebug();
//		if (_debug) Log.v("LinkedInCommon.startLinkedInQuickReplyActivity()");
//		if(sendToID == 0){
//			Toast.makeText(context, context.getString(R.string.app_quick_reply_address_error), Toast.LENGTH_LONG).show();
//			return false;
//		}
//		try{
//			Intent intent = new Intent(context, QuickReplyActivity.class);
//	        if (_debug) Log.v("NotificationView.replyToMessage() Put bundle in intent");
//	        Bundle bundle = new Bundle();
//	        bundle.putInt("notificationType", Constants.NOTIFICATION_TYPE_LINKEDIN);
//	        bundle.putInt("notificationSubType", notificationSubType);
//	        bundle.putLong("sendToID", sendToID);
//	        bundle.putString("sendTo", sendTo);
//		    if(name != null && !name.equals(context.getString(android.R.string.unknownName))){
//		    	bundle.putString("name", name);
//		    }else{
//		    	bundle.putString("name", "");
//		    }
//		    bundle.putString("message", "");
//		    intent.putExtras(bundle);
//	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//	        notificationActivity.startActivityForResult(intent, requestCode);
//	        Common.setInLinkedAppFlag(context, true);
//	        return true;
//		}catch(Exception ex){
//			if (_debug) Log.e("LinkedInCommon.startLinkedInQuickReplyActivity() ERROR: " + ex.toString());
//			Toast.makeText(context, context.getString(R.string.app_android_quick_reply_app_error), Toast.LENGTH_LONG).show();
//			Common.setInLinkedAppFlag(context, false);
//			return false;
//		}
//	}
	
	/**
	 * Start the LinkedIn recurring alarm.
	 *  
	 * @param context - The application context.
	 * @param alarmStartTime - The time to start the alarm.
	 */
	public static void startLinkedInAlarmManager(Context context, long alarmStartTime){
		_debug = Log.getDebug();
		if (_debug) Log.v("LinkedInCommon.startLinkedInAlarmManager()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, LinkedInAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			long pollingFrequency = Long.parseLong(preferences.getString(Constants.LINKEDIN_POLLING_FREQUENCY_KEY, Constants.LINKEDIN_POLLING_FREQUENCY_DEFAULT)) * 60 * 1000;
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime, pollingFrequency, pendingIntent);
		}catch(Exception ex){
			if (_debug) Log.e("LinkedInCommon.startLinkedInAlarmManager() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Start a single LinkedIn alarm.
	 *  
	 * @param context - The application context.
	 * @param alarmStartTime - The time to start the alarm.
	 */
	public static void setLinkedInAlarm(Context context, long alarmStartTime){
		_debug = Log.getDebug();
		if (_debug) Log.v("LinkedInCommon.setLinkedInAlarm()");
		try{
			String intentActionText = "apps.droidnotify.alarm/LinkedInAlarmReceiverAlarm/" + String.valueOf(System.currentTimeMillis());
			Common.startAlarm(context, LinkedInAlarmReceiver.class, null, intentActionText, alarmStartTime);
		}catch(Exception ex){
			if (_debug) Log.e("LinkedInCommon.setLinkedInAlarm() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Cancel the LinkedIn recurring alarm.
	 *  
	 * @param context - The application context.
	 */
	public static void cancelLinkedInAlarmManager(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("LinkedInCommon.cancelLinkedInAlarmManager()");
		try{
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, LinkedInAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			alarmManager.cancel(pendingIntent);
		}catch(Exception ex){
			if (_debug) Log.e("LinkedInCommon.cancelLinkedInAlarmManager() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Determine if the user has authenticated their LinkedIn account. 
	 * 
	 * @param context - The application context.
	 *
	 * @return boolean - Return true if the user preferences have LinkedIn authentication data.
	 */
	public static boolean isLinkedInAuthenticated(Context context) {
		_debug = Log.getDebug();
		if (_debug) Log.v("LinkedInCommon.isLinkedInAuthenticated()");	
		try {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			String oauthToken = preferences.getString(Constants.LINKEDIN_OAUTH_TOKEN, null);
			String oauthTokenSecret = preferences.getString(Constants.LINKEDIN_OAUTH_TOKEN_SECRET, null);
			if(oauthToken == null || oauthTokenSecret == null){
				if (_debug) Log.v("LinkedInCommon.isLinkedInAuthenticated() LinkedIn stored authentication details are null. Exiting...");
				return false;
			}
			return true;
		} catch (Exception ex) {
			if (_debug) Log.e("LinkedInCommon.isLinkedInAuthenticated() ERROR: " + ex.toString());
			return false;
		}
	}
	
	/**
	 * Initialize and return a LinkedIn client object.
	 * 
	 * @param context - The application context.
	 * 
	 * @return LinkedIn - The initialized LinkedIn client object or null.
	 */
	public static LinkedInApiClient getLinkedIn(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("LinkedInCommon.getLinkedIn()");
		try{
			LinkedInApiClientFactory clientFactory = null;
			LinkedInApiClient linkedInClient = null;
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			String oauthToken = preferences.getString(Constants.LINKEDIN_OAUTH_TOKEN, null);
			String oauthTokenSecret = preferences.getString(Constants.LINKEDIN_OAUTH_TOKEN_SECRET, null);
			if(oauthToken == null || oauthTokenSecret == null){
				if (_debug) Log.v("LinkedInCommon.getLinkedIn() Oauth values are null. Exiting...");
				return null;
			}
			clientFactory = LinkedInApiClientFactory.newInstance(Constants.LINKEDIN_CONSUMER_KEY, Constants.LINKEDIN_CONSUMER_SECRET);
			LinkedInAccessToken accessToken = new LinkedInAccessToken(oauthToken, oauthTokenSecret);
			linkedInClient =  clientFactory.createLinkedInApiClient(accessToken);
			return linkedInClient;
		}catch(Exception ex){
			if (_debug) Log.e("LinkedInCommon.getLinkedIn() ERROR: " + ex.toString());
			return null;
		}	
	}
	
}
