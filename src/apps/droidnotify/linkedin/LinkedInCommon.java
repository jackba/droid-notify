package apps.droidnotify.linkedin;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
		
//	/**
//	 * Get LinkedIn Direct Messages. Read account and notify as needed.
//	 * 
//	 * @param context - The application context.
//	 * 
//	 * @return ArrayList<String> - Returns an ArrayList of Strings that contain the LinkedIn information.
//	 */
//	public static ArrayList<String> getLinkedInDirectMessages(Context context, LinkedIn LINKEDIN){
//		_debug = Log.getDebug();
//		if (_debug) Log.v("LinkedInCommon.getLinkedInDirectMessages()");
//		try{
//			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//			//Retrieve the date filter.
//			Calendar today = Calendar.getInstance();
//			today.set(Calendar.MILLISECOND, 0);
//			today.set(Calendar.SECOND, 0);
//			today.set(Calendar.MINUTE, 0);
//			today.set(Calendar.HOUR_OF_DAY, 0);
//			long currentDateTime = today.getTimeInMillis();
//			long dateFilter = preferences.getLong(Constants.LINKEDIN_DIRECT_MESSAGE_DATE_FILTER_KEY, currentDateTime);		
//			long maxDateTime = 0;
//		    ResponseList <DirectMessage> messages = LINKEDIN.getDirectMessages();
//		    ArrayList<String> LINKEDINArray = new ArrayList<String>();
//			for(DirectMessage message: messages){
//				long timeStamp = message.getCreatedAt().getTime();
//				if(timeStamp > maxDateTime){
//					maxDateTime = timeStamp;
//				}
//				if(timeStamp > dateFilter){
//					String messageBody = message.getText();
//					long messageID = message.getId();					
//			    	String sentFromAddress = message.getSenderScreenName();
//			    	long LINKEDINID = message.getSenderId();
//		    		String[] LINKEDINContactInfo = getContactInfoByLinkedInID(context, LINKEDINID);
//		    		if(LINKEDINContactInfo == null){
//		    			LINKEDINArray.add(String.valueOf(Constants.NOTIFICATION_TYPE_LINKEDIN_DIRECT_MESSAGE) + "|" + sentFromAddress + "|" + LINKEDINID + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + timeStamp);
//					}else{
//						LINKEDINArray.add(String.valueOf(Constants.NOTIFICATION_TYPE_LINKEDIN_DIRECT_MESSAGE) + "|" + sentFromAddress + "|" + LINKEDINID + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + timeStamp + "|" + LINKEDINContactInfo[0] + "|" + LINKEDINContactInfo[1] + "|" + LINKEDINContactInfo[2] + "|" + LINKEDINContactInfo[3]);
//					}
//				}
//			}
//			//Store the max date in the preferences.
//			//Don't load any messages that are older than this date next time around.
//			SharedPreferences.Editor editor = preferences.edit();
//			editor.putLong(Constants.LINKEDIN_DIRECT_MESSAGE_DATE_FILTER_KEY, maxDateTime);
//			editor.commit();
//			//Return array.
//			return LINKEDINArray;
//		}catch(Exception ex){
//			if (_debug) Log.e("LinkedInCommon.getLinkedInDirectMessages() ERROR: " + ex.toString());
//			return null;
//		}
//	}
	
//	/**
//	 * Delete a LinkedIn item.
//	 * 
//	 * @param context - The current context of this Activity.
//	 * @param messageID - The message ID that we want to delete.
//	 */
//	public static void deleteLinkedInItem(Context context, apps.droidnotify.Notification notification){
//		_debug = Log.getDebug();
//		if (_debug) Log.v("LinkedInCommon.deleteLinkedInItem()");
//		try{
//			switch(notification.getNotificationSubType()){
//				case Constants.NOTIFICATION_TYPE_LINKEDIN_DIRECT_MESSAGE:{
//					deleteLinkedInDirectMessage(context, notification.getMessageID());
//					return;
//				}
//			}
//		}catch(Exception ex){
//			if (_debug) Log.e("LinkedInCommon.deleteLinkedInItem() ERROR: " + ex.toString());
//		}
//	}
	
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
	
//	/**
//	 * Initialize and return a LinkedIn object.
//	 * 
//	 * @param context - The application context.
//	 * 
//	 * @return LinkedIn - The initialized LinkedIn object or null.
//	 */
//	public static LinkedIn getLinkedIn(Context context){
//		_debug = Log.getDebug();
//		if (_debug) Log.v("LinkedInCommon.getLinkedIn()");
//		try{
//			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//			String oauthToken = preferences.getString(Constants.LINKEDIN_OAUTH_TOKEN, null);
//			String oauthTokenSecret = preferences.getString(Constants.LINKEDIN_OAUTH_TOKEN_SECRET, null);
//			if(oauthToken == null || oauthTokenSecret == null){
//				if (_debug) Log.v("LinkedInCommon.getLinkedIn() Oauth values are null. Exiting...");
//				return null;
//			}
//			ConfigurationBuilder configurationBuilder = new ConfigurationBuilder(); 
//			configurationBuilder.setOAuthConsumerKey(Constants.LINKEDIN_CONSUMER_KEY); 
//			configurationBuilder.setOAuthConsumerSecret(Constants.LINKEDIN_CONSUMER_SECRET); 
//			Configuration configuration =  configurationBuilder.build();  
//			AccessToken accessToken = new AccessToken(oauthToken, oauthTokenSecret);
//			LinkedInFactory LINKEDINFactory = new LinkedInFactory(configuration);
//			LinkedIn LINKEDIN = LINKEDINFactory.getInstance(accessToken);
//			return LINKEDIN;
//		}catch(Exception ex){
//			if (_debug) Log.e("LinkedInCommon.getLinkedIn() ERROR: " + ex.toString());
//			return null;
//		}	
//	}

	//================================================================================
	// Private Methods
	//================================================================================
	

	
}
