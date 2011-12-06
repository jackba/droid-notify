package apps.droidnotify.services;

import java.util.ArrayList;

import twitter4j.Twitter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.twitter.TwitterCommon;

/**
 * This class handles the polling of the users Facebook account.
 * 
 * @author Camille Sévigny
 */
public class TwitterAlarmBroadcastReceiverService extends WakefulIntentService {
	
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
	public TwitterAlarmBroadcastReceiverService() {
		super("TwitterAlarmBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.TwitterAlarmBroadcastReceiverService()");
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
		if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			//Determine the reminder interval based on the users preferences.
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
			//Read preferences and exit if Twitter notifications are disabled.
		    if(!preferences.getBoolean(Constants.TWITTER_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() Twitter Notifications Disabled. Exiting... ");
				return;
			}
		    //Check the state of the users phone.
		    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    boolean rescheduleNotification = false;
		    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
		    boolean blockingAppRunning = Common.isBlockingAppRunning(context);
		    String blockingAppRuningAction = preferences.getString(Constants.TWITTER_BLOCKING_APP_RUNNING_ACTION_KEY, Constants.BLOCKING_APP_RUNNING_ACTION_SHOW);
		    //Reschedule notification based on the users preferences.
		    if(!callStateIdle){
		    	rescheduleNotification = true;
		    }else if(blockingAppRuningAction.equals(Constants.BLOCKING_APP_RUNNING_ACTION_RESCHEDULE) && blockingAppRunning){ 
		    	//Blocking App is running.
		    	rescheduleNotification = true;
		    }
		    if(!rescheduleNotification){
				WakefulIntentService.sendWakefulWork(context, new Intent(context, TwitterService.class));
		    }else{
		    	//Display the Status Bar Notification even though the popup is blocked based on the user preferences.
		    	if(preferences.getBoolean(Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true)){
		    		//Get the Twitter message info.
					String contactName = null;
					String messageAddress = null;
					String messageBody = null;
					//Get Twitter Object
					Twitter twitter = TwitterCommon.getTwitter(context);
					if(twitter == null){
						if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() Twitter object is null. Exiting...");
						return;
					}
					ArrayList<String> twitterDirectMessageArray = TwitterCommon.getTwitterDirectMessages(context, twitter);
				    if(twitterDirectMessageArray != null && twitterDirectMessageArray.size() > 0){
				    	int twitterDirectMessageArraySize = twitterDirectMessageArray.size();
				    	for(int i=0; i<twitterDirectMessageArraySize; i++ ){
				    		String twitterArrayItem = twitterDirectMessageArray.get(i);
							String[] twitterInfo = twitterArrayItem.split("\\|");
			    			int arraySize = twitterInfo.length;
			    			if(arraySize > 0){
								if(arraySize >= 1) messageAddress = twitterInfo[1];
								if(arraySize >= 2) messageBody = twitterInfo[3];
								if(arraySize >= 8) contactName = twitterInfo[7];
			    			}
							//Display Status Bar Notification
						    Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE, callStateIdle, contactName, messageAddress, messageBody, null);
				    	}
					}else{
						if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() No Twitter Direct Messages were found. Exiting...");
					}
				    ArrayList<String> twitterMentionArray = TwitterCommon.getTwitterMentions(context, twitter);
				    if(twitterMentionArray != null && twitterMentionArray.size() > 0){
				    	int twitterMentionArraySize = twitterMentionArray.size();
				    	for(int i=0; i<twitterMentionArraySize; i++ ){
				    		String twitterArrayItem = twitterMentionArray.get(i);
							String[] twitterInfo = twitterArrayItem.split("\\|");
			    			int arraySize = twitterInfo.length;
			    			if(arraySize > 0){
								if(arraySize >= 1) messageAddress = twitterInfo[1];
								if(arraySize >= 2) messageBody = twitterInfo[3];
								if(arraySize >= 8) contactName = twitterInfo[7];
			    			}
							//Display Status Bar Notification
						    Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_TWITTER_MENTION, callStateIdle, contactName, messageAddress, messageBody, null);
				    	}
					}else{
						if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() No Twitter Mentions were found. Exiting...");
					}
			    }
		    	//Ignore notification based on the users preferences.
		    	if(blockingAppRuningAction.equals(Constants.BLOCKING_APP_RUNNING_ACTION_IGNORE)){
		    		return;
		    	}
		    	//Set alarm to go off x minutes from the current time as defined by the user preferences.
		    	if(preferences.getBoolean(Constants.RESCHEDULE_NOTIFICATIONS_ENABLED_KEY, true)){
		    		long rescheduleInterval = Long.parseLong(preferences.getString(Constants.RESCHEDULE_BLOCKED_NOTIFICATION_TIMEOUT_KEY, Constants.RESCHEDULE_BLOCKED_NOTIFICATION_TIMEOUT_DEFAULT)) * 60 * 1000;
		    		if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() Rescheduling notification. Rechedule in " + rescheduleInterval + "minutes.");					
					TwitterCommon.setTwitterAlarm(context, System.currentTimeMillis() + rescheduleInterval);
		    	}
		    }
		}catch(Exception ex){
			if (_debug) Log.e("TwitterAlarmBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}