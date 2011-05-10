package apps.droidnotify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class PhoneAlarmReceiverService extends WakefulIntentService {

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
	public PhoneAlarmReceiverService() {
		super("PhoneAlarmReceiverService");
		if (Log.getDebug()) Log.v("PhoneAlarmReceiverService.PhoneAlarmReceiverService()");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Display the notification for this Calendar Event.
	 * 
	 * @param intent
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		if (Log.getDebug()) Log.v("PhoneAlarmReceiverService.doWakefulWork()");
		Context context = getApplicationContext();
		Bundle bundle = new Bundle();
		bundle.putInt("notificationType", NOTIFICATION_TYPE_PHONE);
    	Intent phoneNotificationIntent = new Intent(context, NotificationActivity.class);
    	phoneNotificationIntent.putExtras(bundle);
    	phoneNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    	context.startActivity(phoneNotificationIntent);
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
}
