package apps.droidnotify.services;

import java.util.ArrayList;

import com.google.code.linkedinapi.client.LinkedInApiClient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.services.WakefulIntentService;
import apps.droidnotify.linkedin.LinkedInCommon;

/**
 * This class handles the work of processing incoming LinkedIn messages.
 * 
 * @author Camille Sévigny
 */
public class LinkedInService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;
	private Context _context = null;
	private SharedPreferences _preferences = null;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public LinkedInService() {
		super("LinkedInService");
		_debug = Log.getDebug();
		if (_debug) Log.v("LinkedInService.LinkedInService()");
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
		if (_debug) Log.v("LinkedInService.doWakefulWork()");
		try{
			_context = getApplicationContext();
		    //Get LinkedIn Object
			LinkedInApiClient linkedInClient = LinkedInCommon.getLinkedIn(_context);
		    if(linkedInClient == null){
		    	if (_debug) Log.v("LinkedInService.doWakefulWork() LinkedInClient object is null. Exiting... ");
		    	return;
		    }
			_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
		    if(_preferences.getBoolean(Constants.LINKEDIN_UPDATES_ENABLED_KEY, true)){
			    ArrayList<String> linkedInUpdateArray = LinkedInCommon.getLinkedInupdates(_context, linkedInClient);
			    if(linkedInUpdateArray != null && linkedInUpdateArray.size() > 0){
					Bundle bundle = new Bundle();
					bundle.putInt("notificationType", Constants.NOTIFICATION_TYPE_LINKEDIN);
					bundle.putStringArrayList("linkedInArrayList", linkedInUpdateArray);
			    	Common.startNotificationActivity(_context, bundle);
				}else{
					if (_debug) Log.v("LinkedInService.doWakefulWork() No Facebook Notifications were found. Exiting...");
				}
		    }
		}catch(Exception ex){
			Log.e("LinkedInService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}
