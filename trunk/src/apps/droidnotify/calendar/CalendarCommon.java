package apps.droidnotify.calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.Toast;
import apps.droidnotify.NotificationActivity;
import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.receivers.CalendarAlarmReceiver;

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
		if (_debug) Log.v("Common.startAddCalendarEventActivity()");
		try{
			//Androids calendar app.
			Intent intent = new Intent(Intent.ACTION_EDIT);
			intent.setType("vnd.android.cursor.item/event");
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
			Common.setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception e){
			Log.e("Common.startAddCalendarEventActivity ERROR: " + e.toString());
			try{
				//HTC Sense UI calendar app.
				Intent intent = new Intent(Intent.ACTION_EDIT);
				intent.setComponent(new ComponentName("com.htc.calendar", "com.htc.calendar.EditEvent"));
		        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				notificationActivity.startActivityForResult(intent, requestCode);
				Common.setInLinkedAppFlag(context, true);
				return true;
			}catch(Exception ex){
				Log.e("Common.startAddCalendarEventActivity ERROR: " + ex.toString());
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
		if (_debug) Log.v("Common.startViewCalendarEventActivity()");
		try{
			if(calendarEventID == 0){
				Toast.makeText(context, context.getString(R.string.app_android_calendar_event_not_found_error), Toast.LENGTH_LONG).show();
				Common.setInLinkedAppFlag(context, false);
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("content://com.android.calendar/events/" + String.valueOf(calendarEventID)));	
			intent.putExtra(Constants.CALENDAR_EVENT_BEGIN_TIME, calendarEventStartTime);
			intent.putExtra(Constants.CALENDAR_EVENT_END_TIME, calendarEventEndTime);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
			Common.setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			Log.e("Common.startViewCalendarEventActivity ERROR: " + ex.toString());
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
		if (_debug) Log.v("Common.startEditCalendarEventActivity()");
		try{
			if(calendarEventID == 0){
				Toast.makeText(context, context.getString(R.string.app_android_calendar_event_not_found_error), Toast.LENGTH_LONG).show();
				Common.setInLinkedAppFlag(context, false);
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_EDIT);
			intent.setData(Uri.parse("content://com.android.calendar/events/" + String.valueOf(calendarEventID)));	
			intent.putExtra(Constants.CALENDAR_EVENT_BEGIN_TIME, calendarEventStartTime);
			intent.putExtra(Constants.CALENDAR_EVENT_END_TIME, calendarEventEndTime);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
			Common.setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			Log.e("Common.startEditCalendarEventActivity ERROR: " + ex.toString());
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
		if (_debug) Log.v("Common.startViewCalendarActivity()");
		try{
			//Androids calendar app.
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName("com.android.calendar", "com.android.calendar.LaunchActivity"); 
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
			Common.setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception e){
			Log.e("Common.startAddCalendarEventActivity ERROR: " + e.toString());
			try{
				//HTC Sense UI calendar app.
				Intent intent = new Intent(Intent.ACTION_MAIN); 
				intent.setComponent(new ComponentName("com.htc.calendar", "com.htc.calendar.LaunchActivity"));
		        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				notificationActivity.startActivityForResult(intent, requestCode);
				Common.setInLinkedAppFlag(context, true);
				return true;
			}catch(Exception ex){
				Log.e("Common.startViewCalendarActivity() ERROR: " + ex.toString());
				Toast.makeText(context, context.getString(R.string.app_android_calendar_app_error), Toast.LENGTH_LONG).show();
				Common.setInLinkedAppFlag(context, false);
				return false;
			}
		}
	}

	/**
	 * Read the phones Calendars and return the information on them.
	 * @param context - The 
	 * 
	 * @return String - A string of the available Calendars. Specially formatted string with the Calendar information.
	 */
	public static String getAvailableCalendars(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getAvailableCalendars()");
		StringBuilder calendarsInfo = new StringBuilder();
		Cursor cursor = null;
		try{
			ContentResolver contentResolver = context.getContentResolver();
			// Fetch a list of all calendars synced with the device, their display names and whether the user has them selected for display.
			String contentProvider = "";
			contentProvider = "content://com.android.calendar";
			cursor = contentResolver.query(
				Uri.parse(contentProvider + "/calendars"), 
				new String[] { Constants.CALENDAR_ID, Constants.CALENDAR_DISPLAY_NAME, Constants.CALENDAR_SELECTED },
				null,
				null,
				null);
			while (cursor.moveToNext()) {
				final String calendarID = cursor.getString(cursor.getColumnIndex(Constants.CALENDAR_ID));
				final String calendarDisplayName = cursor.getString(cursor.getColumnIndex(Constants.CALENDAR_DISPLAY_NAME));
				final Boolean calendarSelected = !cursor.getString(cursor.getColumnIndex(Constants.CALENDAR_SELECTED)).equals("0");
				if(calendarSelected){
					if (_debug) Log.v("Id: " + calendarID + " Display Name: " + calendarDisplayName + " Selected: " + calendarSelected);
					if(!calendarsInfo.toString().equals("")){
						calendarsInfo.append(",");
					}
					calendarsInfo.append(calendarID + "|" + calendarDisplayName);
				}
			}	
		}catch(Exception ex){
			Log.e("Common.getAvailableCalendars() ERROR: " + ex.toString());
			return null;
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}
		if(calendarsInfo.toString().equals("")){
			return null;
		}else{
			return calendarsInfo.toString();
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
		if (_debug) Log.v("Common.startCalendarAlarmManager()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, CalendarAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			long pollingFrequency = Long.parseLong(preferences.getString(Constants.CALENDAR_POLLING_FREQUENCY_KEY, Constants.CALENDAR_POLLING_FREQUENCY_DEFAULT)) * 60 * 1000;
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime, pollingFrequency, pendingIntent);
		}catch(Exception ex){
			Log.e("Common.startCalendarAlarmManager() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Cancel the Calendar recurring alarm. 
	 * 
	 * @param context - The application context.
	 */
	public static void cancelCalendarAlarmManager(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.cancelCalendarAlarmManager()");
		try{
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, CalendarAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			alarmManager.cancel(pendingIntent);
		}catch(Exception ex){
			Log.e("Common.cancelCalendarAlarmManager() ERROR: " + ex.toString());
		}
	}
	
}
