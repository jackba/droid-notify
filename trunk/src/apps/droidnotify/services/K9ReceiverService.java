package apps.droidnotify.services;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import apps.droidnotify.NotificationActivity;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This class handles the work of processing incoming K9 email messages.
 * 
 * @author Camille Sévigny
 */
public class K9ReceiverService extends WakefulIntentService {
	
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
	public K9ReceiverService() {
		super("K9ReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("K9ReceiverService.K9ReceiverService()");
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
		if (_debug) Log.v("K9ReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			Bundle newK9Bundle = intent.getExtras();
			ArrayList<String> k9Array = Common.getK9MessagesFromIntent(context, newK9Bundle);
			if(k9Array != null && k9Array.size() > 0){
				Bundle bundle = new Bundle();
				bundle.putInt("notificationType", Constants.NOTIFICATION_TYPE_K9);
				bundle.putStringArrayList("k9ArrayList", k9Array);
		    	Intent k9NotificationIntent = new Intent(context, NotificationActivity.class);
		    	k9NotificationIntent.putExtras(bundle);
		    	k9NotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		    	Common.acquireWakeLock(context);
		    	context.startActivity(k9NotificationIntent);
			}else{
				if (_debug) Log.v("K9ReceiverService.doWakefulWork() No new emails were found. Exiting...");
			}
		}catch(Exception ex){
			if (_debug) Log.e("K9ReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}
