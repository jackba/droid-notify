package apps.droidnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 
 * @author Camille Sevigny
 *
 */
public class CalendarNotificationAlarmReceiver extends BroadcastReceiver {
	
	/**
	 * 
	 * 
	 * @param context
	 * @param intent
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		if (Log.getDebug()) Log.v("CalendarNotificationAlarmReceiver.onReceive()");
		WakefulIntentService.acquireStaticLock(context);
		Intent calendarNotificationIntent = new Intent(context, CalendarNotificationAlarmReceiverService.class);
		Bundle bundle = intent.getExtras();
		calendarNotificationIntent.putExtras(bundle);
		context.startService(calendarNotificationIntent);
	}
	
}