package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.calendar.CalendarCommon;

/**
 * This class does the work of the BroadcastReceiver.
 * 
 * @author Camille Sévigny
 */
public class CalendarNotificationAlarmBroadcastReceiverService extends WakefulIntentService {

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public CalendarNotificationAlarmBroadcastReceiverService() {
		super("CalendarNotificationAlarmBroadcastReceiverService");
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
		Context context = getApplicationContext();
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				Log.e(context, "CalendarNotificationAlarmBroadcastReceiverService.onReceive() Quiet Time. Exiting...");
				return;
			}
	    	Bundle bundle = intent.getExtras();
    		Bundle calendarEventNotificationBundle = bundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME);
    		if(calendarEventNotificationBundle != null){
    			Bundle calendarEventNotificationBundleSingle = calendarEventNotificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_1");
    			if(calendarEventNotificationBundleSingle != null){
				    //Check to ensure that this calendar event should be displayed.
			    	if(!CalendarCommon.isCalendarEnabled(context, calendarEventNotificationBundleSingle.getLong(Constants.BUNDLE_CALENDAR_ID))){
						Log.e(context, "CalendarNotificationAlarmBroadcastReceiverService.onReceive() This calendar is not enabled. Exiting... ");
						return;
				    }
				    if(!CalendarCommon.eventExists(context, calendarEventNotificationBundleSingle.getLong(Constants.BUNDLE_CALENDAR_EVENT_ID))){
				    	Log.e(context, "CalendarNotificationAlarmBroadcastReceiverService.onReceive() Calendar event does not exist. Exiting... ");
						return;
				    }
	    		}else{
	    			Log.e(context, "CalendarNotificationAlarmBroadcastReceiverService.onReceive() CalendarEventBundleSingle is null. Exiting... ");
	    			return;
	    		}
    		}else{
    			Log.e(context, "CalendarNotificationAlarmBroadcastReceiverService.onReceive() CalendarEventBundle is null. Exiting... ");
    			return;
    		}
		    //Check the state of the users phone.
		    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    boolean notificationIsBlocked = false;
		    boolean rescheduleNotificationInCall = false;
		    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
		    //Reschedule notification based on the users preferences.
		    if(!callStateIdle){
		    	notificationIsBlocked = true;		    	
		    	rescheduleNotificationInCall = preferences.getBoolean(Constants.IN_CALL_RESCHEDULING_ENABLED_KEY, false);
		    }else{
		    	notificationIsBlocked = Common.isNotificationBlocked(context);
		    }
		    if(!notificationIsBlocked){
				Intent calendarIntent = new Intent(context, CalendarService.class);
				calendarIntent.putExtras(intent.getExtras());
				WakefulIntentService.sendWakefulWork(context, calendarIntent);
		    }else{	    	
		    	//Display the Status Bar Notification even though the popup is blocked based on the user preferences.
		    	if(preferences.getBoolean(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true)){
		    		if(calendarEventNotificationBundle != null){
		    			Bundle calendarEventNotificationBundleSingle = calendarEventNotificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_1");
		    			if(calendarEventNotificationBundleSingle != null){
							//Display Status Bar Notification
						    Common.setStatusBarNotification(context, 1, Constants.NOTIFICATION_TYPE_CALENDAR, -1, callStateIdle, null, -1, null, calendarEventNotificationBundleSingle.getString(Constants.BUNDLE_MESSAGE_BODY), null, null, -1, false, Common.getStatusBarNotificationBundle(context, Constants.NOTIFICATION_TYPE_CALENDAR));
		    			}
		    		}
		    	}
		    	if(calendarEventNotificationBundle != null) Common.rescheduleBlockedNotification(context, callStateIdle, rescheduleNotificationInCall, Constants.NOTIFICATION_TYPE_CALENDAR, calendarEventNotificationBundle);
		    }
		}catch(Exception ex){
			Log.e(context, "CalendarNotificationAlarmBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}