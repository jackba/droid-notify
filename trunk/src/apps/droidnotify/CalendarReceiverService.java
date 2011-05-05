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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

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
		
		
		
		
		
		//scheduleCalendarNotification(getApplicationContext(), SystemClock.elapsedRealtime() + 1000 ,"Camille's Birthday", "Get Camille a pressent NOW!", "1304516824000");
	
	
	
	
	
	}
	
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
	
	//================================================================================
	// Private Methods
	//================================================================================
	
}
