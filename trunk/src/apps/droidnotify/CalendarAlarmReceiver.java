package apps.droidnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CalendarAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Log.getDebug()) Log.v("CalendarAlarmReceiver.onReceive()");
		Toast.makeText(context, "Repeating Alarm worked.", Toast.LENGTH_LONG).show();
	}
	
}