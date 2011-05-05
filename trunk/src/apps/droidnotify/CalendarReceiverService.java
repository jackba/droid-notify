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
		readCalendar(context);
		//scheduleCalendarNotification(getApplicationContext(), System.currentTimeMillis() + 1000 ,"Camille's Birthday", "Get Camille a pressent NOW!", "1304516824000");
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * 
	 */
	private void scheduleCalendarNotification(Context context, long scheduledAlarmTime, String title, String body, String timeStamp){
		if (Log.getDebug()) Log.v("CalendarReceiverService.scheduleCalendarNotification()");
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    	Intent calendarNotificationIntent = new Intent(context, CalendarNotificationOnAlarmReceiver.class);
    	Bundle calendarNotificationBundle = new Bundle();
    	calendarNotificationBundle.putInt("notificationType", NOTIFICATION_TYPE_CALENDAR);
    	calendarNotificationBundle.putStringArray("calenderReminderInfo",new String[]{title, body, timeStamp});
    	calendarNotificationIntent.putExtras(calendarNotificationBundle);
    	calendarNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, calendarNotificationIntent, 0);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, scheduledAlarmTime, pendingIntent);
	}

	/**
	 * 
	 * 
	 * @param context
	 */
	public static void readCalendar(Context context) {
		ContentResolver contentResolver = context.getContentResolver();
		// Fetch a list of all calendars synced with the device, their display names and whether the
		// user has them selected for display.
		final Cursor cursor = contentResolver.query(
				Uri.parse("content://calendar/calendars"),
				new String[] { "_id", "displayName", "selected" },
				null,
				null,
				null);
		// For a full list of available columns see http://tinyurl.com/yfbg76w
		HashSet<String> calendarIds = new HashSet<String>();
		while (cursor.moveToNext()) {
			final String _id = cursor.getString(0);
			final String displayName = cursor.getString(1);
			final Boolean selected = !cursor.getString(2).equals("0");
			
			if (Log.getDebug()) Log.v("Id: " + _id + " Display Name: " + displayName + " Selected: " + selected);
			calendarIds.add(_id);
		}	
		// For each calendar, display all the events from the previous week to the end of next week.		
		for (String id : calendarIds) {
			Uri.Builder builder = Uri.parse("content://calendar/instances/when").buildUpon();
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
			}
		}
	}
	
	
	
//	private void viewCalendarInfo(){
//	
//		int iTestCalendarID = ListSelectedCalendars();
//
//		if (Log.getDebug()) Log.v("Ending Calendar Test");
//
//
//
//	}
//
//	private int ListSelectedCalendars() {
//		int result = 0;
//		String[] projection = new String[] { "_id", "name" };
//		String selection = "selected=1";
//		String path = "calendars";
//	
//		Cursor managedCursor = getCalendarManagedCursor(projection, selection,
//		        path);
//		
//		if (managedCursor != null && managedCursor.moveToFirst()) {
//		
//		    if (Log.getDebug()) Log.v("Listing Selected Calendars Only");
//		
//		    int nameColumn = managedCursor.getColumnIndex("name");
//		    int idColumn = managedCursor.getColumnIndex("_id");
//		
//		    do {
//		        String calName = managedCursor.getString(nameColumn);
//		        String calId = managedCursor.getString(idColumn);
//		        if (Log.getDebug()) Log.v("Found Calendar '" + calName + "' (ID="
//		                + calId + ")");
//		        if (calName != null && calName.contains("Test")) {
//		            result = Integer.parseInt(calId);
//		        }
//		    } while (managedCursor.moveToNext());
//		} else {
//		    if (Log.getDebug()) Log.v("No Calendars");
//		}
//	
//		return result;
//	
//	}
//
//	private void ListAllCalendarDetails() {
//		Cursor managedCursor = getCalendarManagedCursor(null, null, "calendars");
//		
//		if (managedCursor != null && managedCursor.moveToFirst()) {
//		
//		    if (Log.getDebug()) Log.v("Listing Calendars with Details");
//		
//		    do {
//		
//		        if (Log.getDebug()) Log.v("**START Calendar Description**");
//		
//		        for (int i = 0; i < managedCursor.getColumnCount(); i++) {
//		            if (Log.getDebug()) Log.v(managedCursor.getColumnName(i) + "="
//		                    + managedCursor.getString(i));
//		        }
//		        if (Log.getDebug()) Log.v("**END Calendar Description**");
//		    } while (managedCursor.moveToNext());
//		} else {
//		    if (Log.getDebug()) Log.v("No Calendars");
//		}
//	
//	}
//
//	private void ListAllCalendarEntries(int calID) {
//	
//		Cursor managedCursor = getCalendarManagedCursor(null, "calendar_id="
//		        + calID, "events");
//		
//		if (managedCursor != null && managedCursor.moveToFirst()) {
//		
//		    if (Log.getDebug()) Log.v("Listing Calendar Event Details");
//		
//		    do {
//		
//		        if (Log.getDebug()) Log.v("**START Calendar Event Description**");
//		
//		        for (int i = 0; i < managedCursor.getColumnCount(); i++) {
//		            if (Log.getDebug()) Log.v(managedCursor.getColumnName(i) + "="
//		                    + managedCursor.getString(i));
//		        }
//		        if (Log.getDebug()) Log.v("**END Calendar Event Description**");
//		    } while (managedCursor.moveToNext());
//		} else {
//		    if (Log.getDebug()) Log.v("No Calendars");
//		}
//	
//	}
//
//	private void ListCalendarEntry(int eventId) {
//		Cursor managedCursor = getCalendarManagedCursor(null, null, "events/" + eventId);
//		
//		if (managedCursor != null && managedCursor.moveToFirst()) {
//		
//		    if (Log.getDebug()) Log.v("Listing Calendar Event Details");
//		
//		    do {
//		
//		        if (Log.getDebug()) Log.v("**START Calendar Event Description**");
//		
//		        for (int i = 0; i < managedCursor.getColumnCount(); i++) {
//		            if (Log.getDebug()) Log.v(managedCursor.getColumnName(i) + "="
//		                    + managedCursor.getString(i));
//		        }
//		        if (Log.getDebug()) Log.v("**END Calendar Event Description**");
//		    } while (managedCursor.moveToNext());
//		} else {
//		    if (Log.getDebug()) Log.v("No Calendar Entry");
//		}
//		
//		}
//	
//	   /**
//     * @param projection
//     * @param selection
//     * @param path
//     * @return
//     */
//    private Cursor getCalendarManagedCursor(String[] projection, String selection, String path) {
//        Uri calendars = Uri.parse("content://calendar/" + path);
//
//        Cursor managedCursor = null;
//        try {
//            managedCursor = managedQuery(calendars, projection, selection, null, null);
//        } catch (IllegalArgumentException e) {
//            if (Log.getDebug()) Log.v("Failed to get provider at ["
//                    + calendars.toString() + "]");
//        }
//
//        if (managedCursor == null) {
//            // try again
//            calendars = Uri.parse("content://com.android.calendar/" + path);
//            try {
//                managedCursor = managedQuery(calendars, projection, selection, null, null);
//            } catch (IllegalArgumentException e) {
//                if (Log.getDebug()) Log.v("Failed to get provider at ["
//                        + calendars.toString() + "]");
//            }
//        }
//        return managedCursor;
//    }
//
//    /*
//     * Determines if it's a pre 2.1 or a 2.2 calendar Uri, and returns the Uri
//     */
//    private String getCalendarUriBase() {
//    	String calendarUriBase = null;
//        Uri calendars = Uri.parse("content://calendar/calendars");
//        Cursor managedCursor = null;
//        try {
//            managedCursor = managedQuery(calendars, null, null, null, null);
//        } catch (Exception e) {
//            // eat
//        }
//
//        if (managedCursor != null) {
//            calendarUriBase = "content://calendar/";
//        } else {
//            calendars = Uri.parse("content://com.android.calendar/calendars");
//            try {
//                managedCursor = managedQuery(calendars, null, null, null, null);
//            } catch (Exception e) {
//                // eat
//            }
//
//            if (managedCursor != null) {
//                calendarUriBase = "content://com.android.calendar/";
//            }
//
//        }
//
//        return calendarUriBase;
//    }

		
}
