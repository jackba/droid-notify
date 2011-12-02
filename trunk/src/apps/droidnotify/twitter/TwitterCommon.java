package apps.droidnotify.twitter;

import java.util.ArrayList;

import oauth.signpost.OAuth;

import twitter4j.DirectMessage;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.widget.Toast;

import apps.droidnotify.NotificationActivity;
import apps.droidnotify.QuickReplyActivity;
import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.receivers.TwitterAlarmReceiver;
import apps.droidnotify.log.Log;

/**
 * This class is a collection of Twitter methods.
 * 
 * @author Camille Sévigny
 */
public class TwitterCommon {

	//================================================================================
    // Properties
    //================================================================================
	
	private static boolean _debug = false; 
	
	//================================================================================
	// Public Methods
	//================================================================================
		
	/**
	 * Get Twitter Direct Messages. Read account and notify as needed.
	 * 
	 * @param context - The application context.
	 * 
	 * @return ArrayList<String> - Returns an ArrayList of Strings that contain the Twitter information.
	 */
	public static ArrayList<String> getTwitterDirectMessages(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("TwitterCommon.getTwitterDirectMessages()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			if(preferences.getBoolean(Constants.TWITTER_DIRECT_MESSAGES_ENABLED_KEY, true)){
				Twitter twitter = getTwitter(context);
				if(twitter == null){
					if (_debug) Log.v("TwitterCommon.getTwitterDirectMessages() Twitter object is null. Exiting...");
					return null;
				}
			    ResponseList <DirectMessage> messages = twitter.getDirectMessages();
			    ArrayList<String> twitterArray = new ArrayList<String>();
				for(DirectMessage message: messages){
					String messageBody = message.getText();
					long messageID = message.getId();
					long timeStamp = message.getCreatedAt().getTime();
			    	String sentFromAddress = message.getSenderScreenName();
			    	long twitterID = message.getSenderId();
		    		String[] twitterContactInfo = null;
		    		twitterContactInfo = getContactsInfoByTwitterID(context, twitterID);
		    		if(twitterContactInfo == null){
		    			twitterArray.add(String.valueOf(Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE) + "|" + sentFromAddress + "|" + twitterID + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + timeStamp);
					}else{
						twitterArray.add(String.valueOf(Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE) + "|" + sentFromAddress + "|" + twitterID + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + timeStamp + "|" + twitterContactInfo[0] + "|" + twitterContactInfo[1] + "|" + twitterContactInfo[2] + "|" + twitterContactInfo[3]);
					}
				}
				return twitterArray;
			}else{
				return null;
			}
		}catch(Exception ex){
			if (_debug) Log.e("TwitterCommon.getTwitterDirectMessages() ERROR: " + ex.toString());
			return null;
		}
	}

	/**
	 * Get Twitter Mentions. Read account and notify as needed.
	 * 
	 * @param context - The application context.
	 * 
	 * @return ArrayList<String> - Returns an ArrayList of Strings that contain the Twitter information.
	 */
	public static ArrayList<String> getTwitterMentions(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("TwitterCommon.getTwitterMentions()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			if(preferences.getBoolean(Constants.TWITTER_MENTIONS_ENABLED_KEY, true)){
				Twitter twitter = getTwitter(context);
				if(twitter == null){
					if (_debug) Log.v("TwitterCommon.getTwitterMentions() Twitter object is null. Exiting...");
					return null;
				}
			    ResponseList <DirectMessage> messages = twitter.getDirectMessages();
			    ArrayList<String> twitterArray = new ArrayList<String>();
//				for(DirectMessage message: messages){
//					String messageBody = message.getText();
//					long messageID = message.getId();
//					long timeStamp = message.getCreatedAt().getTime();
//			    	String sentFromAddress = message.getSenderScreenName();
//			    	long twitterID = message.getSenderId();
//		    		String[] twitterContactInfo = null;
//		    		twitterContactInfo = getContactsInfoByTwitterID(context, twitterID);
//		    		if(twitterContactInfo == null){
//		    			twitterArray.add(String.valueOf(Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE) + "|" + sentFromAddress + "|" + twitterID + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + timeStamp);
//					}else{
//						twitterArray.add(String.valueOf(Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE) + "|" + sentFromAddress + "|" + twitterID + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + timeStamp + "|" + twitterContactInfo[0] + "|" + twitterContactInfo[1] + "|" + twitterContactInfo[2] + "|" + twitterContactInfo[3]);
//					}
//				}
				return twitterArray;
			}else{
				return null;
			}
		}catch(Exception ex){
			if (_debug) Log.e("TwitterCommon.getTwitterMentions() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Load the various contact info for this notification from a phoneNumber.
	 * 
	 * @param context - Application Context.
	 * @param twitterID - The twitter ID of the person we are searching for.
	 * 
	 * @return String[] - String Array of the contact information.
	 */ 
	public static String[] getContactsInfoByTwitterID(Context context, long twitterID){
		_debug = Log.getDebug();
		if (_debug) Log.v("TwitterCommon.getContactsInfoByTwitterID()");
		long _contactID = 0;
		String _contactName = "";
		long _photoID = 0;
		String _lookupKey = "";
		if (twitterID == 0) {
			if (_debug) Log.v("TwitterCommon.getContactsInfoByTwitterID() Twitter ID provided is 0. Exiting...");
			return null;
		}
		Twitter twitter = getTwitter(context);
		if(twitter == null){
			if (_debug) Log.v("TwitterCommon.getContactsInfoByTwitterID() Twitter object is null. Exiting...");
			return null;
		}
		try{
			User twitterUser = twitter.showUser(twitterID);
			String twitterName = twitterUser.getName();
			final String[] projection = null;
			final String selection = null;
			final String[] selectionArgs = null;
			final String sortOrder = null;
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					projection, 
					selection, 
					selectionArgs, 
					sortOrder);
			if (_debug) Log.v("TwitterCommon.getContactsInfoByTwitterID() Searching Contacts");
			while (cursor.moveToNext()) { 
				String contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
				String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String photoID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)); 
				String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)); 
				if(contactName.equals(twitterName)){
					_contactID = Long.parseLong(contactID);
					_contactName = contactName;
					_photoID = Long.parseLong(photoID);
					_lookupKey = lookupKey;					
					break;
				}
		   	}
			cursor.close();
			return new String[]{String.valueOf(_contactID), _contactName, String.valueOf(_photoID), _lookupKey};
		}catch(Exception ex){
			if (_debug) Log.e("TwitterCommon.getContactsInfoByTwitterID() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Delete a Twitter item.
	 * 
	 * @param context - The current context of this Activity.
	 * @param messageID - The message ID that we want to delete.
	 */
	public static void deleteTwitterItem(Context context, apps.droidnotify.Notification notification){
		_debug = Log.getDebug();
		if (_debug) Log.v("TwitterCommon.deleteTwitterItem()");
		try{
			switch(notification.getNotificationSubType()){
				case Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE:{
					deleteTwitterDirectMessage(context, notification.getMessageID());
					return;
				}
			}
		}catch(Exception ex){
			if (_debug) Log.e("TwitterCommon.deleteTwitterItem() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Delete a Twitter Direct Message.
	 * 
	 * @param context - The current context of this Activity.
	 * @param messageID - The message ID that we want to delete.
	 */
	public static void deleteTwitterDirectMessage(Context context, long messageID){
		_debug = Log.getDebug();
		if (_debug) Log.v("TwitterCommon.deleteTwitterDirectMessage()");
		try{
			if(messageID == 0){
				if (_debug) Log.v("TwitterCommon.deleteTwitterDirectMessage() messageID == 0. Exiting...");
				return;
			}
			Twitter twitter = getTwitter(context);
			if(twitter == null){
				if (_debug) Log.v("TwitterCommon.deleteTwitterDirectMessage() Twitter object is null. Exiting...");
				return;
			}
			twitter.destroyDirectMessage(messageID);
			return;
		}catch(Exception ex){
			if (_debug) Log.e("TwitterCommon.deleteTwitterDirectMessage() ERROR: " + ex.toString());
			return;
		}
	}	
	
	/**
	 * Start the intent for any Twitter application to view the direct message inbox.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startTwitterAppViewInboxActivity(Context context, NotificationActivity notificationActivity, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("TwitterCommon.startTwitterAppViewInboxActivity()");
		try{
			Intent intent = new Intent(Intent.ACTION_SEND); 
			//intent.putExtra(Intent.EXTRA_TEXT, ""); 
			//intent.setType("application/twitter");
			intent.setType("text/plain");
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
	        return true;
		}catch(Exception ex){
			if (_debug) Log.e("TwitterCommon.startTwitterAppViewInboxActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_twitter_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
	}	

	/**
	 * Start the intent for the Quick Reply activity send a reply.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param phoneNumber - The phone number we want to send a message to.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startTwitterQuickReplyActivity(Context context, NotificationActivity notificationActivity, int requestCode, long sendToID, String sendTo, String name){
		_debug = Log.getDebug();
		if (_debug) Log.v("TwitterCommon.startTwitterQuickReplyActivity()");
		if(sendToID == 0){
			Toast.makeText(context, context.getString(R.string.app_quick_reply_address_error), Toast.LENGTH_LONG).show();
			return false;
		}
		try{
			Intent intent = new Intent(context, QuickReplyActivity.class);
	        if (_debug) Log.v("NotificationView.replyToMessage() Put bundle in intent");
	        Bundle bundle = new Bundle();
	        bundle.putInt("notificationType", Constants.NOTIFICATION_TYPE_TWITTER);
	        bundle.putInt("notificationSubType", Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE);
	        bundle.putLong("sendToID", sendToID);
	        bundle.putString("sendTo", sendTo);
		    if(name != null && !name.equals(context.getString(android.R.string.unknownName))){
		    	bundle.putString("name", name);
		    }else{
		    	bundle.putString("name", "");
		    }
		    bundle.putString("message", "");
		    intent.putExtras(bundle);
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        notificationActivity.startActivityForResult(intent, requestCode);
	        Common.setInLinkedAppFlag(context, true);
	        return true;
		}catch(Exception ex){
			if (_debug) Log.e("TwitterCommon.startTwitterQuickReplyActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_quick_reply_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
	
	public static boolean sendTwitterDirectMessage(Context context, long userID, String message){
		_debug = Log.getDebug();
		if (_debug) Log.v("TwitterCommon.sendTwitterDirectMessage()");
		Twitter twitter = getTwitter(context);
		if(twitter == null){
			if (_debug) Log.v("TwitterCommon.sendTwitterDirectMessage() Twitter object is null. Exiting...");
			return false;
		} 
		try{
			twitter.sendDirectMessage(userID, message);
			return true;
		}catch(Exception ex){
			if (_debug) Log.v("TwitterCommon.sendTwitterDirectMessage() ERROR: " + ex.toString());
			return false;
		}
	}
	
	/**
	 * Start the Twitter recurring alarm.
	 *  
	 * @param context - The application context.
	 * @param alarmStartTime - The time to start the alarm.
	 */
	public static void startTwitterAlarmManager(Context context, long alarmStartTime){
		_debug = Log.getDebug();
		if (_debug) Log.v("TwitterCommon.startTwitterAlarmManager()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, TwitterAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			long pollingFrequency = Long.parseLong(preferences.getString(Constants.TWITTER_POLLING_FREQUENCY_KEY, "15")) * 60 * 1000;
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime, pollingFrequency, pendingIntent);
		}catch(Exception ex){
			if (_debug) Log.e("TwitterCommon.startTwitterAlarmManager() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Cancel the Twitter recurring alarm.
	 *  
	 * @param context - The application context.
	 */
	public static void cancelTwitterAlarmManager(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("TwitterCommon.cancelTwitterAlarmManager()");
		try{
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, TwitterAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			alarmManager.cancel(pendingIntent);
		}catch(Exception ex){
			if (_debug) Log.e("TwitterCommon.cancelTwitterAlarmManager() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Determine if the user has authenticated their Twitter account. 
	 * 
	 * @param context - The application context.
	 *
	 * @return boolean - Return true if the user preferences have Twitter authentication data.
	 */
	public static boolean isTwitterAuthenticated(Context context) {
		_debug = Log.getDebug();
		if (_debug) Log.v("TwitterCommon.isTwitterAuthenticated()");	
		try {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			String oauthToken = preferences.getString(OAuth.OAUTH_TOKEN, null);
			String oauthTokenSecret = preferences.getString(OAuth.OAUTH_TOKEN_SECRET, null);
			if(oauthToken == null || oauthTokenSecret == null){
				if (_debug) Log.v("TwitterCommon.isTwitterAuthenticated() Twitter stored authentication details are null. Exiting...");
				return false;
			}
			//try {
			//	Twitter twitter = getTwitter(context);
			//	if(twitter == null){
			//		if (_debug) Log.v("TwitterCommon.isTwitterAuthenticated() Twitter object is null. Exiting...");
			//		return false;
			//	} 
			//	twitter.getAccountSettings();
			//	return true;
			//} catch (Exception ex) {
			//	if (_debug) Log.e("TwitterCommon.isTwitterAuthenticated() Twitter Authentication - ERROR: " + ex.toString());
			//	return false;
			//}
			return true;
		} catch (Exception ex) {
			if (_debug) Log.e("TwitterCommon.isTwitterAuthenticated() ERROR: " + ex.toString());
			return false;
		}
	}
	
	/**
	 * Initialize and return a Twitter object.
	 * 
	 * @param context - The application context.
	 * 
	 * @return Twitter - The initialized Twitter object or null.
	 */
	public static Twitter getTwitter(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("TwitterCommon.getTwitter()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			String oauthToken = preferences.getString(OAuth.OAUTH_TOKEN, null);
			String oauthTokenSecret = preferences.getString(OAuth.OAUTH_TOKEN_SECRET, null);
			if(oauthToken == null || oauthTokenSecret == null){
				if (_debug) Log.v("TwitterCommon.getTwitter() Oauth values are null. Exiting...");
				return null;
			}
			ConfigurationBuilder configurationBuilder = new ConfigurationBuilder(); 
			configurationBuilder.setOAuthConsumerKey(Constants.TWITTER_CONSUMER_KEY); 
			configurationBuilder.setOAuthConsumerSecret(Constants.TWITTER_CONSUMER_SECRET); 
			Configuration configuration =  configurationBuilder.build();  
			AccessToken accessToken = new AccessToken(oauthToken, oauthTokenSecret);
			TwitterFactory twitterFactory = new TwitterFactory(configuration);
			Twitter twitter = twitterFactory.getInstance(accessToken);
			return twitter;
		}catch(Exception ex){
			if (_debug) Log.e("TwitterCommon.getTwitter() ERROR: " + ex.toString());
			return null;
		}	
	}
	
}
