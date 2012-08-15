package apps.droidnotify.calendar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.provider.CalendarContract;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import apps.droidnotify.NotificationActivity;
import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.receivers.CalendarAlarmReceiver;
import apps.droidnotify.receivers.CalendarNotificationAlarmReceiver;

/**
 * This class is a collection of Calendar methods.
 * 
 * @author Camille Sévigny
 */
public class CalendarCommon {

	//================================================================================
    // Properties
    //================================================================================
	
	private static boolean _debug = false;
	
	//================================================================================
	// Public Methods
	//================================================================================

	/**
	 * Read the phones calendars and events. 
	 * Schedules Calendar Event notifications based on the Event date and time.
	 * 
	 * @param context - Application Context.
	 */
	@SuppressLint("NewApi")
	public static void readCalendars(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarCommon.readCalendars()");
		try{
			int APILevel = Common.getDeviceAPILevel();
			//Determine the reminder interval based on the users preferences.
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("CalendarCommon.readCalendars() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("CalendarCommon.readCalendars() Quiet Time. Exiting...");
				return;
			}
			//Read preferences and exit if calendar notifications are disabled.
		    if(!preferences.getBoolean(Constants.CALENDAR_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v("CalendarCommon.readCalendars() Calendar Notifications Disabled. Exiting... ");
				return;
			}
			String calendarPreferences = preferences.getString(Constants.CALENDAR_SELECTION_KEY, "");
			ArrayList<String> calendarsArray = new ArrayList<String>();
			if(!calendarPreferences.equals("")){
				Collections.addAll(calendarsArray, calendarPreferences.split("\\|")); 
			}
			ContentResolver contentResolver = context.getContentResolver();
			// Fetch a list of all calendars synced with the device, their display names and whether the user has them selected for display.
			String contentProvider = null;	
			String calendarIDColumn = null;
			String calendarDisplayNameColumn = null;
			String calendarSelectedColumn = null;
			if(APILevel >= 14){
				contentProvider = CalendarContract.Calendars.CONTENT_URI.toString();
				calendarIDColumn = CalendarContract.Calendars._ID;
				calendarDisplayNameColumn = CalendarContract.Events.CALENDAR_DISPLAY_NAME;
				calendarSelectedColumn = CalendarContract.Events.VISIBLE;
			}else{
				contentProvider = "content://com.android.calendar/calendars";
				calendarIDColumn = Constants.CALENDAR_ID;
				calendarDisplayNameColumn = Constants.CALENDAR_DISPLAY_NAME;
				calendarSelectedColumn = Constants.CALENDAR_SELECTED;
			}
			if (_debug) Log.v("CalendarCommon.readCalendars() ContentProvider URI String: " + contentProvider);
			HashMap<String, String> calendarIds = new HashMap<String, String>();
		 	Cursor cursor = null;
			try{
				cursor = contentResolver.query(
					Uri.parse(contentProvider), 						
					null,
					null,
					null,
					null);
				if(cursor ==  null){
					Log.e("CalendarCommon.readCalendars() READ CALENDARS ERROR: Cursor is null. Exiting...");
					return;
				}
				while(cursor.moveToNext()){
					long calendarID = -1;
					String calendarDisplayName = null;
					Boolean calendarSelected = true;
					calendarID = cursor.getLong(cursor.getColumnIndex(calendarIDColumn));
					calendarDisplayName = cursor.getString(cursor.getColumnIndex(calendarDisplayNameColumn));
					calendarSelected = !cursor.getString(cursor.getColumnIndex(calendarSelectedColumn)).equals("0");
					if(calendarsArray.contains(String.valueOf(calendarID))){
						if (_debug) Log.v("CalendarCommon.readCalendars() CHECKING CALENDAR -  Calendar ID: " + String.valueOf(calendarID) + " Display Name: " + calendarDisplayName + " Selected: " + calendarSelected);
						calendarIds.put(String.valueOf(calendarID), calendarDisplayName);
					}else{
						if (_debug) Log.v("CalendarCommon.readCalendars() CALENDAR NOT BEING CHECKED -  Calendar ID: " + String.valueOf(calendarID) + " Display Name: " + calendarDisplayName + " Selected: " + calendarSelected);
					}
				}
				cursor.close();
			}catch(Exception ex){
				Log.e("CalendarCommon.readCalendars() READ CALENDARS ERROR: " + ex.toString());
				//if (_debug) Common.debugReadContentProviderColumns(context, contentProvider, null);
				if(cursor != null){
					cursor.close();
				}
				return;
			}
			if(calendarIds.isEmpty()){
				if (_debug) Log.v("CalendarCommon.readCalendars() No calendars were found. Exiting...");
				return;
			}
			long reminderInterval = Long.parseLong(preferences.getString(Constants.CALENDAR_REMINDER_KEY, "15")) * 60 * 1000;
			long reminderIntervalAllDay = Long.parseLong(preferences.getString(Constants.CALENDAR_REMINDER_ALL_DAY_KEY, "6")) * 60 * 60 * 1000;
			long dayOfReminderIntervalAllDay = Long.parseLong(preferences.getString(Constants.CALENDAR_NOTIFY_DAY_OF_TIME_KEY, "12")) * 60 * 60 * 1000;
			// For each calendar, read the events.
			Iterator<Map.Entry<String, String>> calendarIdsEnumerator = calendarIds.entrySet().iterator();
			while(calendarIdsEnumerator.hasNext()){
				Map.Entry<String, String> calendarInfo = calendarIdsEnumerator.next();
				String calendarID = calendarInfo.getKey();
				String calendarName = calendarInfo.getValue();
				if (_debug) Log.v("CalendarCommon.readCalendars() CHECKING EVENTS FOR CALENDAR -  Calendar ID: " + calendarID  + " Calendar Name: " + calendarName);
				String calendarEventContentProvider = null;
				String eventCalendarIDColumn = null;
				String eventIDColumn = null;
				String eventTitleColumn = null;
				String eventStartTimeColumn = null;
				String eventEndTimeColumn = null;
				String eventAllDayColumn = null;
				String eventHasAlarmColumn = null;
				String eventQuerySortOrder = null;
				if(APILevel >= 14){
					calendarEventContentProvider = CalendarContract.Events.CONTENT_URI.toString();
					eventCalendarIDColumn = CalendarContract.Events.CALENDAR_ID;
					eventIDColumn = CalendarContract.Events._ID;
					eventTitleColumn = CalendarContract.Events.TITLE;
					eventStartTimeColumn = CalendarContract.Events.DTSTART;
					eventEndTimeColumn = CalendarContract.Events.DTEND;
    				eventAllDayColumn = CalendarContract.Events.ALL_DAY;
    				eventHasAlarmColumn = CalendarContract.Events.HAS_ALARM;
    				eventQuerySortOrder = eventStartTimeColumn + " ASC";
				}else{
					calendarEventContentProvider = "content://com.android.calendar/events";
					eventCalendarIDColumn = Constants.CALENDAR_CALENDAR_ID;
					eventIDColumn = Constants.CALENDAR_EVENT_ID;
					eventTitleColumn = Constants.CALENDAR_EVENT_TITLE;
					eventStartTimeColumn = Constants.CALENDAR_INSTANCE_BEGIN;
					eventEndTimeColumn = Constants.CALENDAR_INSTANCE_END;
    				eventAllDayColumn = Constants.CALENDAR_EVENT_ALL_DAY;
    				eventHasAlarmColumn = Constants.CALENDAR_EVENT_HAS_ALARM;
    				eventQuerySortOrder = eventStartTimeColumn + " ASC";
				}
				if (_debug) Log.v("CalendarCommon.readCalendars() CalendarEventContentProvider URI String: " + calendarEventContentProvider);			
				//The start time of the query.
				long queryStartTime = System.currentTimeMillis();
				long queryEndTime = queryStartTime + AlarmManager.INTERVAL_DAY;
	    		final String[] projection = new String[] {
						eventCalendarIDColumn,
						eventIDColumn,
						eventTitleColumn,
						eventStartTimeColumn,
						eventEndTimeColumn,
	    				eventAllDayColumn,
	    				eventHasAlarmColumn};
	            final String selection = eventCalendarIDColumn + "=? AND " + eventStartTimeColumn + ">=? AND " + eventStartTimeColumn + "<=?";
	    		final String[] selectionArgs = new String[]{calendarID, String.valueOf(queryStartTime), String.valueOf(queryEndTime)};
	    		final String sortOrder = eventQuerySortOrder;
				Cursor eventCursor = null;
				try{
					eventCursor = contentResolver.query(
							Uri.parse(calendarEventContentProvider)	,
							projection,
							selection,
							selectionArgs,
							sortOrder);
					if(eventCursor ==  null){
						Log.e("CalendarCommon.readCalendars() READ CALENDAR EVENTS: Event cursor is null. Exiting...");
						cursor.close();
						return;
					}
					while(eventCursor.moveToNext()){
						long eventCalendarID = eventCursor.getLong(eventCursor.getColumnIndex(eventCalendarIDColumn));
						String eventID = eventCursor.getString(eventCursor.getColumnIndex(eventIDColumn));
						String eventTitle = eventCursor.getString(eventCursor.getColumnIndex(eventTitleColumn));
						long eventStartTime = eventCursor.getLong(eventCursor.getColumnIndex(eventStartTimeColumn));
						long eventEndTime = eventCursor.getLong(eventCursor.getColumnIndex(eventEndTimeColumn));
						final Boolean allDay = !eventCursor.getString(eventCursor.getColumnIndex(eventAllDayColumn)).equals("0");
						final Boolean hasReminderAlarm = !eventCursor.getString(eventCursor.getColumnIndex(eventHasAlarmColumn)).equals("0");
						if (_debug) Log.v("CalendarCommon.readCalendars() Calendar ID: " + eventCalendarID + 
								" Event ID: " + eventID + 
								" Event Title: " + eventTitle + 
								" Event Begin: " + eventStartTime + 
								" Event End: " + eventEndTime + 
								" Event All Day: " + allDay + 
								" Event Has Reminder Alarm: " + hasReminderAlarm);
						long timezoneOffsetValue =  TimeZone.getDefault().getOffset(System.currentTimeMillis());
						//For all any event in the past, don't schedule them.
						long currentSystemTime = System.currentTimeMillis();
						if(eventStartTime > currentSystemTime){
							if(allDay){
								//Special case for all-day events.
								eventStartTime = eventStartTime  - timezoneOffsetValue;
								eventEndTime = eventEndTime  - timezoneOffsetValue;
								//Schedule the notification for the event time.
								Bundle calendarEventNotificationBundleSingle = new Bundle();
								calendarEventNotificationBundleSingle.putString(Constants.BUNDLE_TITLE, eventTitle);
								calendarEventNotificationBundleSingle.putString(Constants.BUNDLE_MESSAGE_BODY, eventTitle);
								calendarEventNotificationBundleSingle.putLong(Constants.BUNDLE_CALENDAR_EVENT_START_TIME, eventStartTime);
								calendarEventNotificationBundleSingle.putLong(Constants.BUNDLE_CALENDAR_EVENT_END_TIME, eventEndTime);
								calendarEventNotificationBundleSingle.putBoolean(Constants.BUNDLE_ALL_DAY, allDay);
								calendarEventNotificationBundleSingle.putString(Constants.BUNDLE_CALENDAR_NAME, calendarName);
								calendarEventNotificationBundleSingle.putLong(Constants.BUNDLE_CALENDAR_ID, Long.parseLong(calendarID));
								calendarEventNotificationBundleSingle.putLong(Constants.BUNDLE_CALENDAR_EVENT_ID, Long.parseLong(eventID));
								calendarEventNotificationBundleSingle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_CALENDAR);
								if(preferences.getBoolean(Constants.CALENDAR_EVENT_TIME_REMINDER_KEY, false)){
									scheduleCalendarNotification(context, eventStartTime + dayOfReminderIntervalAllDay, calendarEventNotificationBundleSingle, "apps.droidnotify.view.calendar." + calendarID + "." + eventID);
								}
								//Schedule the reminder notification if it is enabled.
								if(preferences.getBoolean(Constants.CALENDAR_REMINDERS_ENABLED_KEY,true)){
									//Only schedule the all day event if the current time is before the notification time.
									if(preferences.getBoolean(Constants.CALENDAR_USE_CALENDAR_REMINDER_SETTINGS_KEY,true)){
										if(hasReminderAlarm){
											reminderIntervalAllDay = getCalendarEventReminderTime(context, Long.parseLong(eventID), true);	
											if(reminderIntervalAllDay > 0){
												//Only schedule the event if the current time is before the notification time.
												if((eventStartTime - reminderIntervalAllDay) > currentSystemTime){
													scheduleCalendarNotification(context, eventStartTime - reminderIntervalAllDay, calendarEventNotificationBundleSingle, "apps.droidnotify.view.calendar.reminder." + calendarID + "." + eventID);
												}
											}
										}
									}else{
										//Only schedule the event if the current time is before the notification time.
										if((eventStartTime - reminderIntervalAllDay) > currentSystemTime){
											scheduleCalendarNotification(context, eventStartTime - reminderIntervalAllDay, calendarEventNotificationBundleSingle, "apps.droidnotify.view.calendar.reminder." + calendarID + "." + eventID);
										}
									}
								}
							}else{
								//Schedule non-all-day events.
								//Schedule the notification for the event time.
								Bundle calendarEventNotificationBundleSingle = new Bundle();
								calendarEventNotificationBundleSingle.putString(Constants.BUNDLE_TITLE, eventTitle);
								calendarEventNotificationBundleSingle.putString(Constants.BUNDLE_MESSAGE_BODY, eventTitle);
								calendarEventNotificationBundleSingle.putLong(Constants.BUNDLE_CALENDAR_EVENT_START_TIME, eventStartTime);
								calendarEventNotificationBundleSingle.putLong(Constants.BUNDLE_CALENDAR_EVENT_END_TIME, eventEndTime);
								calendarEventNotificationBundleSingle.putBoolean(Constants.BUNDLE_ALL_DAY, allDay);
								calendarEventNotificationBundleSingle.putString(Constants.BUNDLE_CALENDAR_NAME, calendarName);
								calendarEventNotificationBundleSingle.putLong(Constants.BUNDLE_CALENDAR_ID, Long.parseLong(calendarID));
								calendarEventNotificationBundleSingle.putLong(Constants.BUNDLE_CALENDAR_EVENT_ID, Long.parseLong(eventID));
								calendarEventNotificationBundleSingle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_CALENDAR);
								if(preferences.getBoolean(Constants.CALENDAR_EVENT_TIME_REMINDER_KEY, false)){
									scheduleCalendarNotification(context, eventStartTime, calendarEventNotificationBundleSingle, "apps.droidnotify.view.calendar." + calendarID + "." + eventID);
								}
								//Schedule the reminder notification if it is enabled.
								if(preferences.getBoolean(Constants.CALENDAR_REMINDERS_ENABLED_KEY,true)){
									if(preferences.getBoolean(Constants.CALENDAR_USE_CALENDAR_REMINDER_SETTINGS_KEY,true)){
										if(hasReminderAlarm){
											reminderInterval = getCalendarEventReminderTime(context, Long.parseLong(eventID), false);	
											if(reminderInterval > 0){
												//Only schedule the event if the current time is before the notification time.
												if((eventStartTime - reminderInterval) > currentSystemTime){
													scheduleCalendarNotification(context, eventStartTime - reminderInterval, calendarEventNotificationBundleSingle, "apps.droidnotify.view.calendar.reminder." + calendarID + "." + eventID);
												}
											}
										}
									}else{
										//Only schedule the event if the current time is before the notification time.
										if((eventStartTime - reminderInterval) > currentSystemTime){
											scheduleCalendarNotification(context, eventStartTime - reminderInterval, calendarEventNotificationBundleSingle, "apps.droidnotify.view.calendar.reminder." + calendarID + "." + eventID);
										}
									}
								}
							}
						}
					}
					eventCursor.close();
				}catch(Exception ex){
					Log.e("CalendarCommon.readCalendars() EVENT QUERY ERROR: " + ex.toString());						
					if (_debug) Common.debugReadContentProviderColumns(context, null, Uri.parse(calendarEventContentProvider));
					if(eventCursor != null){
						eventCursor.close();
					}
					return;
				}
			}
		}catch(Exception ex){
			Log.e("CalendarCommon.readCalendars() ERROR: " + ex.toString());
			return;
		}
	}

	/**
	 * Read the phones Calendars and return the information on them.
	 * 
	 * @param context - Application Context.
	 * 
	 * @return String - A string of the available Calendars. Specially formatted string with the Calendar information.
	 */
	@SuppressLint("NewApi")
	public static String getAvailableCalendars(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarCommon.getAvailableCalendars()");
		StringBuilder calendarsInfo = new StringBuilder();
		Cursor cursor = null;
		String contentProvider = null;
		try{
			int APILevel = Common.getDeviceAPILevel();
			ContentResolver contentResolver = context.getContentResolver();
			String calendarIDColumn = null;
			String calendarDisplayNameColumn = null;
			String calendarSelectedColumn = null;
			if(APILevel >= 14){
				contentProvider = CalendarContract.Calendars.CONTENT_URI.toString();
				calendarIDColumn = CalendarContract.Calendars._ID;
				calendarDisplayNameColumn = CalendarContract.Events.CALENDAR_DISPLAY_NAME;
				calendarSelectedColumn = CalendarContract.Events.VISIBLE;
			}else{
				contentProvider = "content://com.android.calendar/calendars";
				calendarIDColumn = Constants.CALENDAR_ID;
				calendarDisplayNameColumn = Constants.CALENDAR_DISPLAY_NAME;
				calendarSelectedColumn = Constants.CALENDAR_SELECTED;
			}
			if (_debug) Log.v("CalendarCommon.getAvailableCalendars() ContentProvider: " + contentProvider);
    		final String[] projection = new String[]{calendarIDColumn, calendarDisplayNameColumn, calendarSelectedColumn};
            final String selection = null;
    		final String[] selectionArgs = null;
    		final String sortOrder = null;
			cursor = contentResolver.query(
				Uri.parse(contentProvider), 
				projection,
				selection,
				selectionArgs,
				sortOrder);
			if(cursor ==  null){
				Log.e("CalendarCommon.getAvailableCalendars() Cursor is null. Exiting...");
				return null;
			}
			while(cursor.moveToNext()){
				long calendarID = -1; 
				String calendarDisplayName = null;
				Boolean calendarSelected = true;
				calendarID = cursor.getLong(cursor.getColumnIndex(calendarIDColumn));
				calendarDisplayName = cursor.getString(cursor.getColumnIndex(calendarDisplayNameColumn));
				calendarSelected = !cursor.getString(cursor.getColumnIndex(calendarSelectedColumn)).equals("0");
				if(calendarSelected){
					if(!calendarsInfo.toString().equals("")){
						calendarsInfo.append(",");
					}
					calendarsInfo.append(String.valueOf(calendarID) + "|" + calendarDisplayName);
				}
			}
			cursor.close();
		}catch(Exception ex){
			Log.e("CalendarCommon.getAvailableCalendars() ERROR: " + ex.toString());
			if (_debug) Common.debugReadContentProviderColumns(context, null, Uri.parse(contentProvider));
			if(cursor != null){
				cursor.close();
			}
			return null;
		}
		if(calendarsInfo.toString().equals("")){
			if (_debug) Log.v("CalendarCommon.getAvailableCalendars() No Calendars Found.");
			return null;
		}else{
			return calendarsInfo.toString();
		}
	}
	
	/**
	 * Get the reminder time in minutes (in milliseconds) for the event ID. Return -1 if none exists.
	 * 
	 * @param eventID - The event ID we want to query.
	 * @param allDay - Boolean indicating if the event is an all day event or not.
	 * 
	 * @return long - The number of minutes in milliseconds of the reminder time or -1 if none found.
	 */
	@SuppressLint("NewApi")
	private static long getCalendarEventReminderTime(Context context, long eventID, boolean allDay){
		if (_debug) Log.v("CalendarCommon.getCalendarEventReminderTime() EventID: " + eventID + " AllDay: " + allDay);
		Cursor cursor = null;
		String contentProvider = null;
		try{
			int APILevel = Common.getDeviceAPILevel();
			String eventIDColumn = null;
			String reminderTimeInMinutesColumn = null;
			int defaultEventReminderMinutes = 15;
			if(APILevel >= 14){
				contentProvider = CalendarContract.Reminders.CONTENT_URI.toString();
				eventIDColumn = CalendarContract.Reminders.EVENT_ID;
				reminderTimeInMinutesColumn = CalendarContract.Reminders.MINUTES;
				defaultEventReminderMinutes = 15; //TODO - Where Is This Stored Value???
			}else{
				contentProvider = "content://com.android.calendar/reminders";
				eventIDColumn = Constants.CALENDAR_REMINDER_EVENT_ID;
				reminderTimeInMinutesColumn = Constants.CALENDAR_REMINDER_MINUTES;
				defaultEventReminderMinutes = 15;
			}
			if (_debug) Log.v("CalendarCommon.getCalendarEventReminderTime() ContentProvider: " + contentProvider);
    		final String[] projection = new String[]{eventIDColumn, reminderTimeInMinutesColumn};
            final String selection = eventIDColumn + "=?";
    		final String[] selectionArgs = new String[] {String.valueOf(eventID)};
    		final String sortOrder = null;
			cursor = context.getContentResolver().query(
				Uri.parse(contentProvider), 						
				projection,
				selection,
				selectionArgs,
				sortOrder);
			if(cursor ==  null){
				Log.e("CalendarCommon.getCalendarEventReminderTime() Cursor is null. Exiting...");
				return -1;
			}
			if(cursor.moveToFirst()){
				int reminderTimeInMinutes = cursor.getInt(cursor.getColumnIndex(reminderTimeInMinutesColumn));
				if (_debug) Log.v("CalendarCommon.getCalendarEventReminderTime() Reminder Time (minutes): " + reminderTimeInMinutes);
				if(reminderTimeInMinutes == CalendarContract.Reminders.MINUTES_DEFAULT){
					//Use SystemDefault Reminder Time
					reminderTimeInMinutes = defaultEventReminderMinutes;
				}
				cursor.close();
				return reminderTimeInMinutes * 60 * 1000;
			}else{
				if (_debug) Log.v("CalendarCommon.getCalendarEventReminderTime() No Reminder Time Found!");
			}
			cursor.close();
			return -1;
		}catch(Exception ex){
			Log.e("CalendarCommon.getCalendarEventReminderTime() ERROR: " + ex.toString());
			if (_debug) Common.debugReadContentProviderColumns(context, null, Uri.parse(contentProvider));
			if(cursor != null){
				cursor.close();
			}
			return -1;
		}
	}
	
	/**
	 * Set the status of an event reminder/alert.
	 * 
	 * @param context - The application context.
	 * @param eventID - The event ID we want to query.
	 * @param isDismissed - The boolean value indicating if it was read or not.
	 * 
	 * @return boolean - Returns true if the operation was successful.
	 */
	@SuppressLint("NewApi")
	public static boolean setCalendarEventDismissed(Context context, long eventID, boolean isDismissed){
		if (_debug) Log.v("CalendarCommon.setCalendarEventDismissed() EventID: " + eventID);
//		String contentProvider = null;
//		try{
//			if(eventID < 0){
//				if(_debug) Log.v("CalendarCommon.setCalendarEventDismissed() Event ID < 0. Exiting...");
//				return true;
//			}
//			int APILevel = Common.getDeviceAPILevel();
//			String eventIDColumn = null;
//			String eventStatusColumn = null;
//			int STATE_SCHEDULED = -1;
//			int STATE_FIRED = -1;
//			int STATE_DISMISSED = -1;
//			if(APILevel >= 14){
//				contentProvider = CalendarContract.CalendarAlerts.CONTENT_URI.toString();
//				eventIDColumn = CalendarContract.CalendarAlerts.EVENT_ID;
//				eventStatusColumn = CalendarContract.CalendarAlerts.STATUS;
//				STATE_SCHEDULED = CalendarContract.CalendarAlerts.STATE_SCHEDULED;
//				STATE_FIRED = CalendarContract.CalendarAlerts.STATE_FIRED;
//				STATE_DISMISSED = CalendarContract.CalendarAlerts.STATE_DISMISSED;
//			}else{
//				contentProvider = "content://com.android.calendar/calendar_alerts";
//				eventIDColumn = Constants.CALENDAR_ALERT_EVENT_ID;
//				eventStatusColumn = Constants.CALENDAR_ALERT_EVENT_STATUS;
//				STATE_SCHEDULED = 0;
//				STATE_FIRED = 1;
//				STATE_DISMISSED = 2;
//			}
//			if (_debug) Log.v("CalendarCommon.setCalendarEventDismissed() ContentProvider: " + contentProvider);
//			ContentValues contentValues = new ContentValues();
//			if(isDismissed){
//				contentValues.put(eventStatusColumn, STATE_DISMISSED);
//			}else{
//				contentValues.put(eventStatusColumn, STATE_FIRED);
//			}
//            //final String selection = null;
//    		//final String[] selectionArgs = null;
//            final String selection = eventIDColumn + "=?";
//    		final String[] selectionArgs = new String[] {String.valueOf(eventID)};
//			context.getContentResolver().update(
//				Uri.parse(contentProvider), 						
//				contentValues,
//				selection,
//				selectionArgs);
			return true;
//		}catch(Exception ex){
//			Log.e("CalendarCommon.setCalendarEventDismissed() ERROR: " + ex.toString());
//			if (_debug) Common.debugReadContentProviderColumns(context, null, Uri.parse(contentProvider));
//			return false;
//		}
	}	
	
	/**
	 * Determine if a calendar event has already been dismissed.
	 * 
	 * @param context - The application context.
	 * @param eventID - The Event ID that we want to query.
	 * 
	 * @return boolean - Returns false if the event was found and has not been dismissed, returns true otherwise.
	 */
	@SuppressLint("NewApi")
	public static boolean isEventDismissed(Context context, long eventID){
		_debug = Log.getDebug();
		if(_debug) Log.v("CalendarCommon.isEventDismissed() EventID: " + eventID);
		Cursor cursor = null;
		String contentProvider = null;
		try{
			if(eventID < 0){
				if(_debug) Log.v("CalendarCommon.isEventDismissed() Event ID < 0. Exiting...");
				return true;
			}			
			int APILevel = Common.getDeviceAPILevel();
			String eventIDColumn = null;
			String eventStatusColumn = null;
			int STATE_DISMISSED = -1;
			if(APILevel >= 14){
				contentProvider = CalendarContract.CalendarAlerts.CONTENT_URI.toString();
				eventIDColumn = CalendarContract.CalendarAlerts.EVENT_ID;
				eventStatusColumn = CalendarContract.CalendarAlerts.STATUS;
				STATE_DISMISSED = CalendarContract.CalendarAlerts.STATE_DISMISSED;
			}else{
				contentProvider = "content://com.android.calendar/calendar_alerts";
				eventIDColumn = Constants.CALENDAR_ALERT_EVENT_ID;
				eventStatusColumn = Constants.CALENDAR_ALERT_EVENT_STATUS;
				STATE_DISMISSED = 2;
			}
    		final String[] projection = new String[] { "_id", eventIDColumn, eventStatusColumn};
			final String selection = eventIDColumn + "=?";
			final String[] selectionArgs = new String[]{String.valueOf(eventID)};
    		final String sortOrder = null;
				cursor = context.getContentResolver().query(
						Uri.parse(contentProvider),
						projection,
						selection, 
						selectionArgs,
						sortOrder);
		    if(cursor == null){
		    	if(_debug) Log.v("CalendarCommon.isEventDismissed() Currsor is null. Exiting...");
		    	return true;
		    }
		    int eventStatus = 0;
		    if(cursor.moveToFirst()){
		    	eventStatus = cursor.getInt(cursor.getColumnIndex(eventStatusColumn));
		    	if(_debug) Log.v("CalendarCommon.isEventDismissed() Event Found - Status: " + String.valueOf(eventStatus));
	    	}else{
	    		if(_debug) Log.v("CalendarCommon.isEventDismissed() Event ID: " + String.valueOf(eventID) + " was not found!  Exiting...");
	    		return true;
	    	}
			cursor.close();
		    return eventStatus == STATE_DISMISSED ? true : false;
		}catch(Exception ex){
			Log.e("CalendarCommon.isEventDismissed() ERROR: " + ex.toString());
    		if(cursor != null){
				cursor.close();
			}
			return true;
		}
	}
	
	/**
	 * Start the intent to add an event to the calendar app.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startAddCalendarEventActivity(Context context, NotificationActivity notificationActivity, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarCommon.startAddCalendarEventActivity()");
		try{
			//Androids calendar app.
			Intent intent = new Intent(Intent.ACTION_EDIT);
			intent.setType("vnd.android.cursor.item/event");
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
			Common.setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception e){
			Log.e("CalendarCommon.startAddCalendarEventActivity ERROR: " + e.toString());
			try{
				//HTC Sense UI calendar app.
				Intent intent = new Intent(Intent.ACTION_EDIT);
				intent.setComponent(new ComponentName("com.htc.calendar", "com.htc.calendar.EditEvent"));
		        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				notificationActivity.startActivityForResult(intent, requestCode);
				Common.setInLinkedAppFlag(context, true);
				return true;
			}catch(Exception ex){
				Log.e("CalendarCommon.startAddCalendarEventActivity ERROR: " + ex.toString());
				Toast.makeText(context, context.getString(R.string.app_android_calendar_app_error), Toast.LENGTH_LONG).show();
				Common.setInLinkedAppFlag(context, false);
				return false;
			}
		}
	}
	
	/**
	 * Start the intent to view an event to the calendar app.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param calendarEventID - The id of the calendar event.
	 * @param calendarEventStartTime - The start time of the calendar event.
	 * @param calendarEventEndTime - The end time of the calendar event.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startViewCalendarEventActivity(Context context, NotificationActivity notificationActivity, long calendarEventID, long calendarEventStartTime, long calendarEventEndTime, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarCommon.startViewCalendarEventActivity()");
		try{
			if(calendarEventID < 0){
				Toast.makeText(context, context.getString(R.string.app_android_calendar_event_not_found_error), Toast.LENGTH_LONG).show();
				Common.setInLinkedAppFlag(context, false);
				return false;
			}
			String deviceManufacturer = Common.getDeviceManufacturer();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			if(deviceManufacturer != null && deviceManufacturer.contains("HTC")){
				intent.setData(Uri.parse("content://com.htc.calendar/events/" + String.valueOf(calendarEventID)));	
			}else{
				intent.setData(Uri.parse("content://com.android.calendar/events/" + String.valueOf(calendarEventID)));	
			}
			intent.putExtra(Constants.CALENDAR_EVENT_BEGIN_TIME, calendarEventStartTime);
			intent.putExtra(Constants.CALENDAR_EVENT_END_TIME, calendarEventEndTime);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
			Common.setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			Log.e("CalendarCommon.startViewCalendarEventActivity ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_calendar_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
	/**
	 * Start the intent to edit an event to the calendar app.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param calendarEventID - The id of the calendar event.
	 * @param calendarEventStartTime - The start time of the calendar event.
	 * @param calendarEventEndTime - The end time of the calendar event.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startEditCalendarEventActivity(Context context, NotificationActivity notificationActivity, long calendarEventID, long calendarEventStartTime, long calendarEventEndTime, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarCommon.startEditCalendarEventActivity()");
		try{
			if(calendarEventID < 0){
				Toast.makeText(context, context.getString(R.string.app_android_calendar_event_not_found_error), Toast.LENGTH_LONG).show();
				Common.setInLinkedAppFlag(context, false);
				return false;
			}
			String deviceManufacturer = Common.getDeviceManufacturer();
			Intent intent = new Intent(Intent.ACTION_EDIT);
			if(deviceManufacturer != null && deviceManufacturer.contains("HTC")){
				intent.setData(Uri.parse("content://com.htc.calendar/events/events/" + String.valueOf(calendarEventID)));	
			}else{
				intent.setData(Uri.parse("content://com.android.calendar/events/" + String.valueOf(calendarEventID)));
			}
			intent.putExtra(Constants.CALENDAR_EVENT_BEGIN_TIME, calendarEventStartTime);
			intent.putExtra(Constants.CALENDAR_EVENT_END_TIME, calendarEventEndTime);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
			Common.setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			Log.e("CalendarCommon.startEditCalendarEventActivity ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_calendar_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
	/**
	 * Start the intent to start the calendar app.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startViewCalendarActivity(Context context, NotificationActivity notificationActivity, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarCommon.startViewCalendarActivity()");
		try{
			String deviceManufacturer = Common.getDeviceManufacturer();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			if(deviceManufacturer != null && deviceManufacturer.contains("HTC")){
				intent.setComponent(new ComponentName("com.htc.calendar", "com.htc.calendar.LaunchActivity"));
			}else{
				intent.setClassName("com.android.calendar", "com.android.calendar.LaunchActivity"); 
			}
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
			Common.setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			Log.e("CalendarCommon.startViewCalendarActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_calendar_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
	/**
	 * Start the Calendar recurring alarm.
	 * 
	 * @param context - The application context.
	 * @param alarmStartTime - The time to start the alarm.
	 */
	public static void startCalendarAlarmManager(Context context, long alarmStartTime){
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarCommon.startCalendarAlarmManager()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, CalendarAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			long pollingFrequency = Long.parseLong(preferences.getString(Constants.CALENDAR_POLLING_FREQUENCY_KEY, Constants.CALENDAR_POLLING_FREQUENCY_DEFAULT)) * 60 * 1000;
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime, pollingFrequency, pendingIntent);
		}catch(Exception ex){
			Log.e("CalendarCommon.startCalendarAlarmManager() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Cancel the Calendar recurring alarm. 
	 * 
	 * @param context - The application context.
	 */
	public static void cancelCalendarAlarmManager(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarCommon.cancelCalendarAlarmManager()");
		try{
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, CalendarAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			alarmManager.cancel(pendingIntent);
		}catch(Exception ex){
			Log.e("CalendarCommon.cancelCalendarAlarmManager() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Format/create the Calendar Event message.
	 * 
	 * @param context - The application context.
	 * @param eventStartTime - Calendar Event's start time.
	 * @param eventEndTime - Calendar Event's end time.
	 * @param allDay - Boolean, true if the Calendar Event is all day.
	 * 
	 * @return String - Returns the formatted Calendar Event message.
	 */
	public static String formatCalendarEventMessage(Context context, String messageTitle, long eventStartTime, long eventEndTime, boolean allDay, String calendarName){
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarCommon.formatCalendarEventMessage()");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String formattedMessage = "";
		Date eventEndDate = new Date(eventEndTime);
		Date eventStartDate = new Date(eventStartTime);
		if(messageTitle == null || messageTitle.equals("No Title")){
			messageTitle = "";
		}else{
			messageTitle = messageTitle + "<br/>";
		}
		String startDateFormated = Common.formatDate(context, eventStartDate);
		String endDateFormated = Common.formatDate(context, eventEndDate);
		try{
			String[] startDateInfo = Common.parseDateInfo(context, startDateFormated);
			String[] endDateInfo = Common.parseDateInfo(context, endDateFormated);
    		if(allDay){
    			formattedMessage = startDateInfo[0] + " - All Day";
    		}else{
    			//Check if the event spans a single day or not.
    			if(startDateInfo[0].equals(endDateInfo[0]) && startDateInfo.length == 3){
    				if(startDateInfo.length < 3){
    					formattedMessage = startDateInfo[0] + " " + startDateInfo[1] + " - " + endDateInfo[1];
    				}else{
    					formattedMessage = startDateInfo[0] + " " + startDateInfo[1] + " " + startDateInfo[2] +  " - " + endDateInfo[1] + " " + startDateInfo[2];
    				}
    			}else{
    				formattedMessage = startDateFormated + " - " + endDateFormated;
    			}
    		}
    		formattedMessage =  messageTitle + formattedMessage;
		}catch(Exception ex){
			Log.e("CalendarCommon.formatCalendarEventMessage() ERROR: " + ex.toString());
			formattedMessage = startDateFormated + " - " + endDateFormated;
		}
    	if(preferences.getBoolean(Constants.CALENDAR_LABELS_KEY, true)){
    		formattedMessage = "<b>" + calendarName + "</b><br/>" + formattedMessage;
    	}
		return formattedMessage.replace("\n", "<br/>").trim();
	}
	
	/**
	 * Check whether a calendar has been selected 
	 * 
	 * @param context - The application context.
	 * @param calendarID - The calendar ID.
	 * 
	 * @return boolean - Returns true if the user has selected this calendar to receive event notifications.
	 */
	public static boolean isCalendarEnabled(Context context, long calendarID){
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarCommon.isCalendarEnabled()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			String calendarPreferences = preferences.getString(Constants.CALENDAR_SELECTION_KEY, "");
			ArrayList<String> calendarsArray = new ArrayList<String>();
			if(!calendarPreferences.equals("")){
				Collections.addAll(calendarsArray, calendarPreferences.split("\\|")); 
			}
			return calendarsArray.contains(String.valueOf(calendarID));
		}catch(Exception ex){
			Log.e("CalendarCommon.isCalendarEnabled() ERROR: " + ex.toString());
			return true;
		}
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
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
	private static void scheduleCalendarNotification(Context context, long scheduledAlarmTime, Bundle calendarEventNotificationBundleSingle, String intentAction){
		if (_debug) Log.v("CalendarCommon.scheduleCalendarNotification()");
		try{
	    	Bundle calendarEventNotificationBundle = new Bundle();
	    	calendarEventNotificationBundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_1", calendarEventNotificationBundleSingle);
	    	calendarEventNotificationBundle.putInt(Constants.BUNDLE_NOTIFICATION_BUNDLE_COUNT, 1);
	    	Bundle bundle = new Bundle();
	    	bundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_CALENDAR);
	    	bundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, calendarEventNotificationBundle);	    	
			Common.startAlarm(context, CalendarNotificationAlarmReceiver.class, bundle, intentAction, scheduledAlarmTime);
		}catch(Exception ex){
			Log.e("CalendarCommon.scheduleCalendarNotification() ERROR: " + ex.toString());
		}
	}
	
}
