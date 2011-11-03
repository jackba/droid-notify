package apps.droidnotify.services;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

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

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.receivers.CalendarNotificationAlarmReceiver;

/**
 * This class handles the checking of the users calendars.
 * 
 * @author CommonsWare edited by Camille Sévigny
 *
 */
public class CalendarAlarmReceiverService extends WakefulIntentService {
    
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * 
	 */
	public CalendarAlarmReceiverService() {
		super("CalendarAlarmReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarAlarmReceiverService.CalendarAlarmReceiverService()");
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
		if (_debug) Log.v("CalendarAlarmReceiverService.doWakefulWork()");
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
		if (_debug) Log.v("CalendarAlarmReceiverService.readCalendars()");
		try{
			//Determine the reminder interval based on the users preferences.
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("CalendarAlarmReceiverService.readCalendars() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("CalendarAlarmReceiverService.readCalendars() Quiet Time. Exiting...");
				return;
			}
			//Read preferences and exit if calendar notifications are disabled.
		    if(!preferences.getBoolean(Constants.CALENDAR_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v("CalendarAlarmReceiverService.readCalendars() Calendar Notifications Disabled. Exiting... ");
				return;
			}
			long reminderInterval = Long.parseLong(preferences.getString(Constants.CALENDAR_REMINDER_KEY, "15")) * 60 * 1000;
			long reminderIntervalAllDay = Long.parseLong(preferences.getString(Constants.CALENDAR_REMINDER_ALL_DAY_KEY, "6")) * 60 * 60 * 1000;
			long dayOfReminderIntervalAllDay = Long.parseLong(preferences.getString(Constants.CALENDAR_NOTIFY_DAY_OF_TIME_KEY, "12")) * 60 * 60 * 1000;
			String calendarPreferences = preferences.getString(Constants.CALENDAR_SELECTION_KEY, "");
			ArrayList<String> calendarsArray = new ArrayList<String>();
			if(!calendarPreferences.equals("")){
				Collections.addAll(calendarsArray, calendarPreferences.split("\\|")); 
			}
		 	Cursor cursor = null;
			try{
				ContentResolver contentResolver = context.getContentResolver();
				// Fetch a list of all calendars synced with the device, their display names and whether the user has them selected for display.
				String contentProvider = "";
				//Android 2.2+
				contentProvider = "content://com.android.calendar";
				//Android 2.1 and below.
				//contentProvider = "content://calendar";
				cursor = contentResolver.query(
					Uri.parse(contentProvider + "/calendars"), 
					new String[] { Constants.CALENDAR_ID, Constants.CALENDAR_DISPLAY_NAME, Constants.CALENDAR_SELECTED },
					null,
					null,
					null);
				HashMap<String, String> calendarIds = new HashMap<String, String>();
				while (cursor.moveToNext()) {
					final String calendarID = cursor.getString(cursor.getColumnIndex(Constants.CALENDAR_ID));
					final String calendarDisplayName = cursor.getString(cursor.getColumnIndex(Constants.CALENDAR_DISPLAY_NAME));
					final Boolean calendarSelected = !cursor.getString(cursor.getColumnIndex(Constants.CALENDAR_SELECTED)).equals("0");
					if (_debug) Log.v("CalendarAlarmReceiverService.readCalendars() FOUND CALENDAR - Id: " + calendarID + " Display Name: " + calendarDisplayName + " Selected: " + calendarSelected);
					if(calendarsArray.contains(calendarID)){
						if (_debug) Log.v("CalendarAlarmReceiverService.readCalendars() CHECKING CALENDAR -  Id: " + calendarID + " Display Name: " + calendarDisplayName + " Selected: " + calendarSelected);
						calendarIds.put(calendarID, calendarDisplayName);
					}
				}	
				// For each calendar, display all the events from the previous week to the end of next week.
				Iterator<Map.Entry<String, String>> calendarIdsEnumerator = calendarIds.entrySet().iterator();
				while(calendarIdsEnumerator.hasNext()) {
					Map.Entry<String, String> calendarInfo = calendarIdsEnumerator.next();
					String calendarID = calendarInfo.getKey();
					String calendarName = calendarInfo.getValue();
					Uri.Builder builder = Uri.parse(contentProvider + "/instances/when").buildUpon();
					long currentTime = System.currentTimeMillis();
					long queryStartTime = currentTime + reminderInterval;
					//The start time of the query.
					ContentUris.appendId(builder, queryStartTime);
					//The end time of the query should be one day past the start time.
					ContentUris.appendId(builder, queryStartTime + AlarmManager.INTERVAL_DAY);
					Cursor eventCursor = null;
					try{
						eventCursor = contentResolver.query(builder.build(),
							new String[] { Constants.CALENDAR_EVENT_ID, Constants.CALENDAR_EVENT_TITLE, Constants.CALENDAR_INSTANCE_BEGIN, Constants.CALENDAR_INSTANCE_END, Constants.CALENDAR_EVENT_ALL_DAY},
							"Calendars._id=" + calendarID,
							null,
							"startDay ASC, startMinute ASC"); 
						while (eventCursor.moveToNext()) {
							String eventID = eventCursor.getString(eventCursor.getColumnIndex(Constants.CALENDAR_EVENT_ID));
							String eventTitle = eventCursor.getString(eventCursor.getColumnIndex(Constants.CALENDAR_EVENT_TITLE));
							long eventStartTime = eventCursor.getLong(eventCursor.getColumnIndex(Constants.CALENDAR_INSTANCE_BEGIN));
							long eventEndTime = eventCursor.getLong(eventCursor.getColumnIndex(Constants.CALENDAR_INSTANCE_END));
							final Boolean allDay = !eventCursor.getString(eventCursor.getColumnIndex(Constants.CALENDAR_EVENT_ALL_DAY)).equals("0");
							if (_debug) Log.v("CalendarAlarmReceiverService.readCalendars() Event ID: " + eventID + " Title: " + eventTitle + " Begin: " + eventStartTime + " End: " + eventEndTime + " All Day: " + allDay);
							long timezoneOffsetValue =  TimeZone.getDefault().getOffset(System.currentTimeMillis());
							//For all any event in the past, don't schedule them.
							long currentSystemTime = System.currentTimeMillis();
							if(eventStartTime > currentSystemTime){
								if(allDay){
									//Special case for all-day events.
									eventStartTime = eventStartTime  - timezoneOffsetValue;
									eventEndTime = eventEndTime  - timezoneOffsetValue;
									//Schedule the notification for the event time.
									scheduleCalendarNotification(context, eventStartTime + dayOfReminderIntervalAllDay, eventTitle, Long.toString(eventStartTime), Long.toString(eventEndTime), Boolean.toString(allDay), calendarName, calendarID.toString(), eventID, "apps.droidnotify.VIEW/" + calendarID + "/" + eventID);
									//Schedule the reminder notification if it is enabled.
									if(preferences.getBoolean(Constants.CALENDAR_REMINDERS_ENABLED_KEY,true)){
										//Only schedule the all day event if the current time is before the notification time.
										if((eventStartTime - reminderIntervalAllDay) > currentSystemTime){
											scheduleCalendarNotification(context, eventStartTime - reminderIntervalAllDay, eventTitle, Long.toString(eventStartTime), Long.toString(eventEndTime), Boolean.toString(allDay), calendarName, calendarID.toString(), eventID, "apps.droidnotify.VIEW/" + calendarID + "/" + eventID + "/REMINDER");
										}
									}
								}else{
									//Schedule non-all-day events.
									//Schedule the notification for the event time.
									scheduleCalendarNotification(context, eventStartTime, eventTitle, Long.toString(eventStartTime), Long.toString(eventEndTime), Boolean.toString(allDay), calendarName, calendarID.toString(), eventID, "apps.droidnotify.VIEW/" + calendarID + "/" + eventID);
									//Schedule the reminder notification if it is enabled.
									if(preferences.getBoolean(Constants.CALENDAR_REMINDERS_ENABLED_KEY,true)){
										//Only schedule the event if the current time is before the notification time.
										if((eventStartTime - reminderInterval) > currentSystemTime){
											scheduleCalendarNotification(context, eventStartTime - reminderInterval, eventTitle, Long.toString(eventStartTime), Long.toString(eventEndTime), Boolean.toString(allDay), calendarName, calendarID.toString(), eventID, "apps.droidnotify.VIEW/" + calendarID + "/" + eventID + "/REMINDER");
										}
									}
								}
							}
						}
					}catch(Exception ex){
						if (_debug) Log.e("CalendarAlarmReceiverService.readCalendars() Event Query ERROR: " + ex.toString());
					}finally{
						eventCursor.close();
					}
				}
			}catch(Exception ex){
				if (_debug) Log.e("CalendarAlarmReceiverService.readCalendars() Calendar Query ERROR: " + ex.toString());
			}finally{
				cursor.close();
			}
		}catch(Exception ex){
			if (_debug) Log.v("CalendarAlarmReceiverService.readCalendars() ERROR: " + ex.toString());
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
	private void scheduleCalendarNotification(Context context, long scheduledAlarmTime, String title, String eventStartTime, String eventEndTime, String eventAllDay, String calendarName, String calendarID, String eventID, String intentAction){
		if (_debug) Log.v("CalendarAlarmReceiverService.scheduleCalendarNotification()");
		try{
			AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	    	Intent calendarNotificationIntent = new Intent(context, CalendarNotificationAlarmReceiver.class);
	    	Bundle bundle = new Bundle();
	    	bundle.putInt("notificationType", Constants.NOTIFICATION_TYPE_CALENDAR);
	    	bundle.putStringArray("calenderEventInfo",new String[]{title, "", eventStartTime, eventEndTime, eventAllDay, calendarName, calendarID, eventID});
	    	calendarNotificationIntent.putExtras(bundle);
	    	calendarNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	    	//Set the Action attribute for the scheduled intent. 
	    	//Add custom attributes based on the CalendarID and CalendarEventID in order to tell the AlarmManager that these are different intents (which they are of course).
	    	//If you don't do this the alarms will over write each other because the AlarmManager will think they are the same intents being rescheduled.
	    	calendarNotificationIntent.setAction(intentAction);
	    	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, calendarNotificationIntent, 0);
			alarmManager.set(AlarmManager.RTC_WAKEUP, scheduledAlarmTime, pendingIntent);
		}catch(Exception ex){
			if (_debug) Log.v("CalendarAlarmReceiverService.scheduleCalendarNotification() ERROR: " + ex.toString());
		}
	}
		
}