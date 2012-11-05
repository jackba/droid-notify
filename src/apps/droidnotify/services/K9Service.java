package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.k9.K9Common;
import apps.droidnotify.log.Log;

/**
 * This class handles the work of processing incoming K9 email messages.
 * 
 * @author Camille Sévigny
 */
public class K9Service extends WakefulIntentService {

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public K9Service() {
		super("K9Service");
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
			Bundle newEmailBundle = intent.getExtras();
			Bundle emailNotificationBundle = K9Common.getK9MessagesFromIntent(context, newEmailBundle, intent.getAction());
			if(emailNotificationBundle != null){
				Bundle bundle = new Bundle();
				bundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_K9);
				bundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, emailNotificationBundle);
		    	Common.startNotificationActivity(context, bundle);
			}else{
				Log.e(context, "K9Service.doWakefulWork() No new emails were found. Exiting...");
			}
		}catch(Exception ex){
			Log.e(context, "K9Service.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}
