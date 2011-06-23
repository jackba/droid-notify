package apps.droidnotify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * This class handles the work of processing incoming SMS messages.
 * 
 * @author Camille Sévigny
 */
public class SMSReceiverService extends WakefulIntentService {

	//================================================================================
    // Constants
    //================================================================================
	
	private static final int NOTIFICATION_TYPE_SMS = 1;
	
	//================================================================================
    // Properties
    //================================================================================
	
	boolean _debug = false;
	
	//================================================================================
	// Constructors
	//================================================================================
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public SMSReceiverService() {
		super("SMSReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("SMSReceiverService.SMSReceiverService()");
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
		_debug = Log.getDebug();
		if (_debug) Log.v("SMSReceiverService.doWakefulWork()");
		startNotificationActivity(intent);
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Display the notification to the screen.
	 * Send add the SMS message to the Intent object that we created for the new activity.
	 * 
	 * @param intent - Intent object that we are working with.
	 */
	private void startNotificationActivity(Intent intent) {
		if (_debug) Log.v("SMSReceiverService.startNotificationActivity()");
		Context context = getApplicationContext();
		Bundle bundle = intent.getExtras();
		bundle.putInt("notificationType", NOTIFICATION_TYPE_SMS);
    	Intent smsIntent = new Intent(context, NotificationActivity.class);
    	smsIntent.putExtras(bundle);
    	smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    	context.startActivity(smsIntent);
	}

}