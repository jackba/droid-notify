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

import java.sql.Date;
import java.util.HashSet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.DateUtils;

/**
 * 
* @author CommonsWare edited by Camille Sevigny
 *
 */
public class CalendarReceiverService extends WakefulIntentService {

	//================================================================================
    // Constants
    //================================================================================
	
	private final int NOTIFICATION_TYPE_PHONE = 0;
	private final int NOTIFICATION_TYPE_SMS = 1;
	private final int NOTIFICATION_TYPE_MMS = 2;
	private final int NOTIFICATION_TYPE_CALENDAR = 3;
	private final int NOTIFICATION_TYPE_EMAIL = 4;
	
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
	public CalendarReceiverService() {
		super("CalendarReceiverService");
		if (Log.getDebug()) Log.v("CalendarReceiverService.CalendarReceiverService()");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * This service function should read the users calendar events for the next 25 hours and start alarms for each one individually.
	 * 
	 * @param intent
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		if (Log.getDebug()) Log.v("CalendarReceiverService.doWakefulWork()");
		Context context = getApplicationContext();
		//Read the users calendar(s) and events.
		readCalendars(context);
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * 
	 * 
	 * @param context
	 */
	public static void readCalendars(Context context) {
		if (Log.getDebug()) Log.v("CalendarReceiverService.readCalendars()");
		try{
			ContentResolver contentResolver = context.getContentResolver();
			// Fetch a list of all calendars synced with the device, their display names and whether the user has them selected for display.
			String contentProvider = "";
		 	Cursor cursor = null;
			try{
				//Android 2.1
				contentProvider = "content://calendar";
				cursor = contentResolver.query(
					Uri.parse(contentProvider + "/calendars"), 
					new String[] { "_id", "displayName", "selected" },
					null,
					null,
					null);
			}catch(Exception ex){
				//Do Nothing
			}
			if(cursor == null){
				try{
					//Android 2.2
					contentProvider = "content://com.android.calendar";
					cursor = contentResolver.query(
						Uri.parse(contentProvider + "/calendars"),
						new String[] { "_id", "displayName", "selected" },
						null,
						null,
						null);
				}catch(Exception ex){
					return;
				}
			}
			// For a full list of available columns see http://tinyurl.com/yfbg76w
			HashSet<String> calendarIds = new HashSet<String>();
			if (Log.getDebug()) Log.v("CalendarReceiverService.readCalendars() About To Read Calendar");
			while (cursor.moveToNext()) {
				if (Log.getDebug()) Log.v("CalendarReceiverService.readCalendar() Reading Calendars...");
				final String _id = cursor.getString(0);
				final String displayName = cursor.getString(1);
				final Boolean selected = !cursor.getString(2).equals("0");
				if (Log.getDebug()) Log.v("Id: " + _id + " Display Name: " + displayName + " Selected: " + selected);
				calendarIds.add(_id);
			}	
			if (Log.getDebug()) Log.v("CalendarReceiverService.readCalendar() Calendar IDs Read");
			// For each calendar, display all the events from the previous week to the end of next week.		
			for (String id : calendarIds) {
				Uri.Builder builder = Uri.parse(contentProvider + "/instances/when").buildUpon();
				long now = System.currentTimeMillis();
				ContentUris.appendId(builder, now - DateUtils.WEEK_IN_MILLIS);
				ContentUris.appendId(builder, now + DateUtils.WEEK_IN_MILLIS);
				Cursor eventCursor = contentResolver.query(builder.build(),
						new String[] { "title", "begin", "end", "allDay"},
						"Calendars._id=" + id,
						null,
						"startDay ASC, startMinute ASC"); 
				// For a full list of available columns see http://tinyurl.com/yfbg76w
				while (eventCursor.moveToNext()) {
					final String title = eventCursor.getString(0);
					final Date begin = new Date(eventCursor.getLong(1));
					final Date end = new Date(eventCursor.getLong(2));
					final Boolean allDay = !eventCursor.getString(3).equals("0");
					if (Log.getDebug()) Log.v("Title: " + title + " Begin: " + begin + " End: " + end + " All Day: " + allDay);
					//scheduleCalendarNotification(Context context, long scheduledAlarmTime, String title, String body, String timeStamp);
					//scheduleCalendarNotification(getApplicationContext(), System.currentTimeMillis() + 1000 ,"Camille's Birthday", "Get Camille a pressent NOW!", "1304516824000");
				}
			}
		}catch(Exception ex){
			if (Log.getDebug()) Log.e("CalendarReceiverService.readCalendars() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * 
	 */
	private void scheduleCalendarNotification(Context context, long scheduledAlarmTime, String title, String body, String timeStamp, String calendarID, String calendarEventID){
		if (Log.getDebug()) Log.v("CalendarReceiverService.scheduleCalendarNotification()");
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    	Intent calendarNotificationIntent = new Intent(context, CalendarNotificationAlarmReceiver.class);
    	Bundle calendarNotificationBundle = new Bundle();
    	calendarNotificationBundle.putInt("notificationType", NOTIFICATION_TYPE_CALENDAR);
    	calendarNotificationBundle.putStringArray("calenderReminderInfo",new String[]{title, body, timeStamp, calendarID, calendarEventID});
    	calendarNotificationIntent.putExtras(calendarNotificationBundle);
    	calendarNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, calendarNotificationIntent, 0);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, scheduledAlarmTime, pendingIntent);
	}
		
}
