package apps.droidnotify;

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
	 * This service function should read the users calendar events for the next 25 hours and start alarms for each one individually.
	 * 
	 * @param intent
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		if (Log.getDebug()) Log.v("PhoneReceiverService.doWakefulWork()");
		Context context = getApplicationContext();
		displayPhoneNotificationToScreen(context);
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
	
}
