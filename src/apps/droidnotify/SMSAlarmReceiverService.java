package apps.droidnotify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class SMSAlarmReceiverService extends WakefulIntentService {

	//================================================================================
    // Constants
    //================================================================================
	
	//private final int NOTIFICATION_TYPE_PHONE = 0;
	private final int NOTIFICATION_TYPE_SMS = 1;
	//private final int NOTIFICATION_TYPE_MMS = 2;
	//private final int NOTIFICATION_TYPE_CALENDAR = 3;
	//private final int NOTIFICATION_TYPE_EMAIL = 4;
	
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
	public SMSAlarmReceiverService() {
		super("SMSAlarmReceiverService");
		if (Log.getDebug()) Log.v("SMSAlarmReceiverService.SMSAlarmReceiverService()");
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
		if (Log.getDebug()) Log.v("SMSReceiverService.doWakefulWork()");
		startNotificationActivity(intent);
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Display the notification to the screen.
	 * Send add the SMS message to the intent object that we created for the new activity.
	 * 
	 * @param intent
	 */
	private void startNotificationActivity(Intent intent) {
		if (Log.getDebug()) Log.v("SMSReceiverService.startNotificationActivity()");
		Context context = getApplicationContext();
		Bundle bundle = intent.getExtras();
		bundle.putInt("notificationType", NOTIFICATION_TYPE_SMS);
    	Intent smsNotificationIntent = new Intent(context, NotificationActivity.class);
    	smsNotificationIntent.putExtras(bundle);
    	smsNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    	context.startActivity(smsNotificationIntent);
	}

}