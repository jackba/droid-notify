package apps.droidnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

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
		TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
	    if (Log.getDebug()) Log.v("PhoneReceiver.onReceive() Current Call State: " + telemanager.getCallState());
	    if (callStateIdle) {
			WakefulIntentService.acquireStaticLock(context);
			Intent calendarNotificationIntent = new Intent(context, CalendarNotificationAlarmReceiverService.class);
			Bundle bundle = intent.getExtras();
			calendarNotificationIntent.putExtras(bundle);
			context.startService(calendarNotificationIntent);
	    }else{
	    	if (Log.getDebug()) Log.v("CalendarNotificationAlarmReceiver.onReceive() Phone Call In Progress.");
	    	//TODO - Reschedule this notification in 5 minutes.
	    	
	    }
	}
	
}