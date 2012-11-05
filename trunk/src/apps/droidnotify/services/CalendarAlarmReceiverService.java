package apps.droidnotify.services;

import android.content.Intent;

import apps.droidnotify.calendar.CalendarCommon;

/**
 * This class handles the checking of the users calendars.
 * 
 * @author CommonsWare edited by Camille Sévigny
 *
 */
public class CalendarAlarmReceiverService extends WakefulIntentService {
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class constructor.
	 */
	public CalendarAlarmReceiverService() {
		super("CalendarAlarmReceiverService");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * This service function should read the users calendar events for the next 25 hours and start alarms for each one individually.
	 * 
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		//Read the users calendar(s) and events.
		CalendarCommon.readCalendars(getApplicationContext());
	}

}