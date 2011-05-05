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
public class CalendarNotificationOnAlarmReceiver extends BroadcastReceiver {
	
	/**
	 * 
	 * 
	 * @param context
	 * @param intent
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		if (Log.getDebug()) Log.v("CalendarNotificationOnAlarmReceiver.onReceive()");
		WakefulIntentService.acquireStaticLock(context);
		Intent newIntent = new Intent(context, CalendarNotificationReceiverService.class);
		Bundle bundle = intent.getExtras();
		newIntent.putExtras(bundle);
		context.startService(newIntent);
	}
	
}