package apps.droidnotify.services;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.facebook.FacebookCommon;
import apps.droidnotify.log.Log;

/**
 * This class handles the polling of the users Facebook account.
 * 
 * @author Camille Sévigny
 */
public class FacebookAlarmBroadcastReceiverService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public FacebookAlarmBroadcastReceiverService() {
		super("FacebookAlarmBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.FacebookAlarmBroadcastReceiverService()");
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
		if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
			//Read preferences and exit if Facebook notifications are disabled.
		    if(!preferences.getBoolean(Constants.FACEBOOK_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork() Facebook Notifications Disabled. Exiting... ");
				return;
			}
		  //Check the state of the users phone.
		    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    boolean rescheduleNotification = false;
		    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
		    boolean blockingAppRunning = Common.isBlockingAppRunning(context);
		    String blockingAppRuningAction = preferences.getString(Constants.FACEBOOK_BLOCKING_APP_RUNNING_ACTION_KEY, Constants.BLOCKING_APP_RUNNING_ACTION_SHOW);
		    //Reschedule notification based on the users preferences.
		    if(!callStateIdle){
		    	rescheduleNotification = true;
		    }else if(blockingAppRuningAction.equals(Constants.BLOCKING_APP_RUNNING_ACTION_RESCHEDULE) && blockingAppRunning){ 
		    	//Blocking App is running.
		    	rescheduleNotification = true;
		    }
		    if(!rescheduleNotification){
				WakefulIntentService.sendWakefulWork(context, new Intent(context, FacebookService.class));
		    }else{
		    	//Display the Status Bar Notification even though the popup is blocked based on the user preferences.
		    	if(preferences.getBoolean(Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true)){
		    		//Get the Facebook message info.
					String contactName = null;
					String messageAddress = null;
					String messageBody = null;
//					ArrayList<String> facebookDirectMessageArray = FacebookCommon.getFacebookNotifications(context);
//				    if(facebookDirectMessageArray != null && facebookDirectMessageArray.size() > 0){
//				    	int facebookDirectMessageArraySize = facebookDirectMessageArray.size();
//				    	for(int i=0; i<facebookDirectMessageArraySize; i++ ){
//				    		String facebookArrayItem = facebookDirectMessageArray.get(i);
//							String[] facebookInfo = facebookArrayItem.split("\\|");
//			    			int arraySize = facebookInfo.length;
//			    			if(arraySize > 0){
//								if(arraySize >= 1) messageAddress = facebookInfo[1];
//								if(arraySize >= 2) messageBody = facebookInfo[3];
//								if(arraySize >= 8) contactName = facebookInfo[7];
//			    			}
//							//Display Status Bar Notification
//						    Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_FACEBOOK_DIRECT_MESSAGE, callStateIdle, contactName, messageAddress, messageBody, null);
//				    	}
//					}else{
//						if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork() No Facebook Direct Messages were found. Exiting...");
//					}
//				    ArrayList<String> facebookMentionArray = FacebookCommon.getFacebookMentions(context);
//				    if(facebookMentionArray != null && facebookMentionArray.size() > 0){
//				    	int facebookMentionArraySize = facebookMentionArray.size();
//				    	for(int i=0; i<facebookMentionArraySize; i++ ){
//				    		String facebookArrayItem = facebookMentionArray.get(i);
//							String[] facebookInfo = facebookArrayItem.split("\\|");
//			    			int arraySize = facebookInfo.length;
//			    			if(arraySize > 0){
//								if(arraySize >= 1) messageAddress = facebookInfo[1];
//								if(arraySize >= 2) messageBody = facebookInfo[3];
//								if(arraySize >= 8) contactName = facebookInfo[7];
//			    			}
//							//Display Status Bar Notification
//						    Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_FACEBOOK_MENTION, callStateIdle, contactName, messageAddress, messageBody, null);
//				    	}
//					}else{
//						if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork() No Facebook Mentions were found. Exiting...");
//					}
			    }
		    	//Ignore notification based on the users preferences.
		    	if(blockingAppRuningAction.equals(Constants.BLOCKING_APP_RUNNING_ACTION_IGNORE)){
		    		return;
		    	}
		    	//Set alarm to go off x minutes from the current time as defined by the user preferences.
		    	if(preferences.getBoolean(Constants.RESCHEDULE_NOTIFICATIONS_ENABLED_KEY, true)){
		    		long rescheduleInterval = Long.parseLong(preferences.getString(Constants.RESCHEDULE_BLOCKED_NOTIFICATION_TIMEOUT_KEY, Constants.RESCHEDULE_BLOCKED_NOTIFICATION_TIMEOUT_DEFAULT)) * 60 * 1000;
		    		if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork() Rescheduling notification. Rechedule in " + rescheduleInterval + "minutes.");					
					FacebookCommon.setFacebookAlarm(context, System.currentTimeMillis() + rescheduleInterval);
		    	}
		    }
		}catch(Exception ex){
			if (_debug) Log.e("FacebookAlarmBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}