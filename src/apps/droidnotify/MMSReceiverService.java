package apps.droidnotify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * This class handles the work of processing incoming MMS messages.
 * 
 * @author Camille Sévigny
 */
public class MMSReceiverService extends WakefulIntentService {

	//================================================================================
    // Constants
    //================================================================================
	
	private static final int NOTIFICATION_TYPE_MMS = 2;
	
	//================================================================================
    // Properties
    //================================================================================
	
	//================================================================================
	// Constructors
	//================================================================================
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public MMSReceiverService() {
		super("MMSReceiverService");
		if (Log.getDebug()) Log.v("MMSReceiverService.MMSReceiverService()");
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
		if (Log.getDebug()) Log.v("MMSReceiverService.doWakefulWork()");
		startNotificationActivity(intent);
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Display the notification to the screen.
	 * Send add the MMS message to the Intent object that we created for the new activity.
	 * 
	 * @param intent - Intent object that we are working with.
	 */
	private void startNotificationActivity(Intent intent) {
		if (Log.getDebug()) Log.v("MMSReceiverService.startNotificationActivity()");
		Context context = getApplicationContext();
		Bundle bundle = intent.getExtras();
		bundle.putInt("notificationType", NOTIFICATION_TYPE_MMS);
    	Intent mmsIntent = new Intent(context, NotificationActivity.class);
    	mmsIntent.putExtras(bundle);
    	mmsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    	context.startActivity(mmsIntent);
	}

}