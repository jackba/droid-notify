package apps.droidnotify.services;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.phone.PhoneCommon;

/**
 * This class handles scheduled Missed Call notifications that we want to display.
 * 
 * @author Camille Sévigny
 */
public class PhoneService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor
	 */
	public PhoneService() {
		super("PhoneService");
		_debug = Log.getDebug();
		if (_debug) Log.v("PhoneService.PhoneService()");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Display the notification for this Missed Call.
	 * 
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		if (_debug) Log.v("PhoneService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			ArrayList<String> missedCallsArray = PhoneCommon.getMissedCalls(context);
			if(missedCallsArray != null && missedCallsArray.size() > 0){
				Bundle bundle = new Bundle();
				bundle.putInt("notificationType", Constants.NOTIFICATION_TYPE_PHONE);
				bundle.putStringArrayList("missedCallsArrayList", missedCallsArray);
				Common.startNotificationActivity(context, bundle);
			}else{
				if (_debug) Log.v("PhoneService.doWakefulWork() No missed calls were found. Exiting...");
			}
		}catch(Exception ex){
			Log.e("PhoneService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
	
}