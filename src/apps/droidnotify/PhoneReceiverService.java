package apps.droidnotify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

/**
 * 
 * 
 * @author Camille Sevigny
 *
 */
public class PhoneReceiverService extends WakefulIntentService {

	//================================================================================
    // Constants
    //================================================================================
	
	private final int NOTIFICATION_TYPE_PHONE = 0;
	private final int NOTIFICATION_TYPE_SMS = 1;
	private final int NOTIFICATION_TYPE_MMS = 2;
	private final int NOTIFICATION_TYPE_CALENDAR = 3;
	private final int NOTIFICATION_TYPE_EMAIL = 4;
	
	//================================================================================
    // Properties
    //================================================================================
	
	//================================================================================
	// Constructors
	//================================================================================
	
	//================================================================================
	// Accessors
	//================================================================================
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * 
	 */
	public PhoneReceiverService() {
		super("PhoneReceiverService");
		if (Log.getDebug()) Log.v("PhoneReceiverService.PhoneReceiverService()");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Do the work for the service inside this function.
	 * 
	 * @param intent
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		if (Log.getDebug()) Log.v("PhoneReceiverService.doWakefulWork()");
		Context context = getApplicationContext();
		//Custom sleep function.
		//The correct way to do this would probably do schedule a service to occur in the future.
		//I may do that soon, but for now I want to test this method out.
		doSleep(5 * 1000);
		displayPhoneNotificationToScreen(context);	
		//Alarm Method
		//schedulePhoneNotification(context);
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Perform a sleep task that does nothing for the set time duration.
	 * This assumes that this class is running asynchronously in the background.
	 * 
	 * @param sleepDuration
	 */
	private void doSleep(int sleepDuration){
		if (Log.getDebug()) Log.v("PhoneReceiverService.doSleep() Started");
		long startTime = System.currentTimeMillis();
		long stopTime = startTime + sleepDuration;		
		while(System.currentTimeMillis() < stopTime){
			//Do Nothing
			//This assumes that the calling class is running asynchronously in the background.
		}
		if (Log.getDebug()) Log.v("PhoneReceiverService.doSleep() Stopped");
	}
	
	/**
	 * Display the notification to the screen.
	 * Let the activity check the call log and determine if we need to show a notification or not.
	 * 
	 * @param context
	 */
	private void displayPhoneNotificationToScreen(Context context) {
		if (Log.getDebug()) Log.v("PhoneReceiverService.displayPhoneNotificationToScreen()");	
		TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
	    if (Log.getDebug()) Log.v("PhoneReceiverService.displayPhoneNotificationToScreen() Current Call State: " + telemanager.getCallState());
	    // If the user is not in a call then start the check on the call log. 
	    if (callStateIdle) {
			Bundle bundle = new Bundle();
			bundle.putInt("notificationType", NOTIFICATION_TYPE_PHONE);
	    	Intent intent = new Intent(context, NotificationActivity.class);
	    	intent.putExtras(bundle);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	    	context.startActivity(intent);
	    }
	}

	/**
	 * Display the notification to the screen.
	 * Let the activity check the call log and determine if we need to show a notification or not.
	 * 
	 * @param context
	 */
	private void schedulePhoneNotification(Context context) {
		if (Log.getDebug()) Log.v("PhoneReceiverService.displayPhoneNotificationToScreen()");	
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent newIntent = new Intent(context, PhoneAlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, newIntent, 0);
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis() + (5 * 1000), AlarmManager.INTERVAL_DAY, pendingIntent);
	}

}
