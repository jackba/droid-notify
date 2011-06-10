package apps.droidnotify;

/***
	Copyright (c) 2008-2011 CommonsWare, LLC
	Licensed under the Apache License, Version 2.0 (the "License"); you may not
	use this file except in compliance with the License. You may obtain	a copy
	of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
	by applicable law or agreed to in writing, software distributed under the
	License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
	OF ANY KIND, either express or implied. See the License for the specific
	language governing permissions and limitations under the License.
	
	From _The Busy Coder's Guide to Advanced Android Development_
		http://commonsware.com/AdvAndroid
*/

import java.util.HashSet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * This class handles the checking of the users calendars.
 * 
 * @author CommonsWare edited by Camille Sévigny
 *
 */
public class CalendarAlarmReceiverService extends WakefulIntentService {

	//================================================================================
    // Constants
    //================================================================================
	
	private final int NOTIFICATION_TYPE_CALENDAR = 3;
	
	private final String _ID = "_id";
	private final String CALENDAR_EVENT_ID = "event_id"; 
	private final String CALENDAR_EVENT_TITLE = "title"; 
    private final String CALENDAR_INSTANCE_BEGIN = "begin"; 
    private final String CALENDAR_INSTANCE_END = "end"; 
    private final String CALENDAR_EVENT_ALL_DAY = "allDay"; 
    private final String CALENDAR_EVENT_DISPLAY_NAME = "displayName"; 
    private final String CALENDAR_SELECTED = "selected"; 
    private final String CALENDAR_REMINDER_KEY = "calendar_reminder_settings";
    private final String CALENDAR_REMINDER_ALL_DAY_KEY = "calendar_reminder_all_day_settings";
    
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
	public CalendarAlarmReceiverService() {
		super("CalendarAlarmReceiverService");
		if (Log.getDebug()) Log.v("CalendarAlarmReceiverService.CalendarReceiverService()");
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
		if (Log.getDebug()) Log.v("CalendarAlarmReceiverService.doWakefulWork()");
		Context context = getApplicationContext();
		//Read the users calendar(s) and events.
		readCalendars(context);
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Read the phones calendars and events. 
	 * Schedules Calendar Event notifications based on the Event date and time.
	 * 
	 * @param context - Application Context.
	 */
	public void readCalendars(Context context) {
		if (Log.getDebug()) Log.v("CalendarAlarmReceiverService.readCalendars()");
		//Determine the reminder interval based on the users preferences.
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		long reminderInterval = Long.parseLong(preferences.getString(CALENDAR_REMINDER_KEY, "15")) * 60 * 1000;
		long reminderIntervalAllDay = Long.parseLong(preferences.getString(CALENDAR_REMINDER_ALL_DAY_KEY, "6")) * 60 * 60 * 1000;
		try{
			ContentResolver contentResolver = context.getContentResolver();
			// Fetch a list of all calendars synced with the device, their display names and whether the user has them selected for display.
			String contentProvider = "";
		 	Cursor cursor = null;
			try{
				//Android 2.2+
				contentProvider = "content://com.android.calendar";
				//Android 2.1 and below.
				//contentProvider = "content://calendar";
				cursor = contentResolver.query(
					Uri.parse(contentProvider + "/calendars"), 
					new String[] { _ID, CALENDAR_EVENT_DISPLAY_NAME, CALENDAR_SELECTED },
					null,
					null,
					null);
			}catch(Exception ex){
				if (Log.getDebug()) Log.e("CalendarAlarmReceiverService.readCalendars() Cursor ERROR: " + ex.toString());
				return;
			}
			if(cursor == null){
				if (Log.getDebug()) Log.e("CalendarAlarmReceiverService.readCalendars() ERROR: Content Resolver returned a null cursor. Exiting...");
				return;
			}
			// For a full list of available columns see http://tinyurl.com/yfbg76w
			HashSet<String> calendarIds = new HashSet<String>();
			while (cursor.moveToNext()) {
				final String calendarID = cursor.getString(cursor.getColumnIndex(_ID));
				final String calendarDisplayName = cursor.getString(cursor.getColumnIndex(CALENDAR_EVENT_DISPLAY_NAME));
				final Boolean calendarSelected = !cursor.getString(cursor.getColumnIndex(CALENDAR_SELECTED)).equals("0");
				if (Log.getDebug()) Log.v("Id: " + calendarID + " Display Name: " + calendarDisplayName + " Selected: " + calendarSelected);
				calendarIds.add(calendarID);
			}	
			// For each calendar, display all the events from the previous week to the end of next week.		
			for (String calendarID : calendarIds) {
				Uri.Builder builder = Uri.parse(contentProvider + "/instances/when").buildUpon();
				long currentTime = System.currentTimeMillis();
				long queryStartTime = currentTime + reminderInterval;
				//The start time of the query should be the current time + the reminder interval.
				ContentUris.appendId(builder, queryStartTime);
				//The end time of the query should be one day past the start time.
				ContentUris.appendId(builder, queryStartTime + AlarmManager.INTERVAL_DAY);
				Cursor eventCursor = contentResolver.query(builder.build(),
						new String[] { CALENDAR_EVENT_ID, CALENDAR_EVENT_TITLE, CALENDAR_INSTANCE_BEGIN, CALENDAR_INSTANCE_END, CALENDAR_EVENT_ALL_DAY},
						"Calendars._id=" + calendarID,
						null,
						"startDay ASC, startMinute ASC"); 
				// For a full list of available columns see http://tinyurl.com/yfbg76w
				while (eventCursor.moveToNext()) {
					String eventID = eventCursor.getString(eventCursor.getColumnIndex(CALENDAR_EVENT_ID));
					String eventTitle = eventCursor.getString(eventCursor.getColumnIndex(CALENDAR_EVENT_TITLE));
					long eventStartTime = eventCursor.getLong(eventCursor.getColumnIndex(CALENDAR_INSTANCE_BEGIN));
					long eventEndTime = eventCursor.getLong(eventCursor.getColumnIndex(CALENDAR_INSTANCE_END));
					final Boolean allDay = !eventCursor.getString(eventCursor.getColumnIndex(CALENDAR_EVENT_ALL_DAY)).equals("0");
					if (Log.getDebug()) Log.v("Event ID: " + eventID + " Title: " + eventTitle + " Begin: " + eventStartTime + " End: " + eventEndTime + " All Day: " + allDay);
					//--------------------------------
					//This line of code is for testing.
					//scheduleCalendarNotification(context, System.currentTimeMillis() + (30 * 1000), eventTitle, Long.toString(eventStartTime), Long.toString(eventEndTime), Boolean.toString(allDay), calendarID.toString(), eventID );
					//--------------------------------
					//For all day events and any event in the past, don't schedule them.
					if(allDay){
						//Special case for all-day events.
						if(eventStartTime >= System.currentTimeMillis()){
							scheduleCalendarNotification(context, eventStartTime - reminderIntervalAllDay, eventTitle, Long.toString(eventStartTime), Long.toString(eventEndTime), Boolean.toString(allDay), calendarID.toString(), eventID );
						}
					}else if(eventStartTime >= System.currentTimeMillis()){
						//Schedule non-all-day events.
						scheduleCalendarNotification(context, eventStartTime - reminderInterval, eventTitle, Long.toString(eventStartTime), Long.toString(eventEndTime), Boolean.toString(allDay), calendarID.toString(), eventID );
					}
				}
			}
		}catch(Exception ex){
			if (Log.getDebug()) Log.e("CalendarAlarmReceiverService.readCalendars() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Schedule an alarm that will trigger a Notification for a Calendar Event.
	 * 
	 * @param context - Application Context.
	 * @param scheduledAlarmTime - Time the alarm should be scheduled.
	 * @param title - Title of the Calendar Event.
	 * @param timeStamp - TimeStamp of the Calendar Event.
	 * @param calendarID - Calendar ID of the Calendar Event.
	 * @param eventID - Event ID of the Calendar Event.
	 */
	private void scheduleCalendarNotification(Context context, long scheduledAlarmTime, String title, String eventStartTime, String eventEndTime, String eventAllDay, String calendarID, String eventID){
		if (Log.getDebug()) Log.v("CalendarAlarmReceiverService.scheduleCalendarNotification()");
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    	Intent calendarNotificationIntent = new Intent(context, CalendarNotificationAlarmReceiver.class);
    	Bundle bundle = new Bundle();
    	bundle.putInt("notificationType", NOTIFICATION_TYPE_CALENDAR);
    	bundle.putStringArray("calenderEventInfo",new String[]{title, "", eventStartTime, eventEndTime, eventAllDay, calendarID, eventID});
    	calendarNotificationIntent.putExtras(bundle);
    	calendarNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    	//Set the Action attribute for the scheduled intent. 
    	//Add custom attributes based on the CalendarID and CalendarEventID in order to tell the AlarmManager that these are different intents (which they are of course).
    	//If you don't do this the alarms will over write each other because the AlarmManager will think they are the same intents being rescheduled.
    	calendarNotificationIntent.setAction("apps.droidnotify.VIEW/" + calendarID + "/" + eventID);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, calendarNotificationIntent, 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP, scheduledAlarmTime, pendingIntent);
	}
		
}