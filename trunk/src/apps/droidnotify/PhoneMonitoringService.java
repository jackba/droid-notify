package apps.droidnotify;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PhoneMonitoringService extends Service{

	public PhoneMonitoringService() {
		Log.v("PhoneMonitoringService.PhoneMonitoringService().");
		return;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.v("PhoneMonitoringService.onBind().");
		return null;
	}

	@Override
	public void onCreate() {
		Log.v("PhoneMonitoringService.onCreate().");
		super.onCreate();
		return;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.v("PhoneMonitoringService.onStart().");
		StartPhoneMonitoringService();
		super.onStart(intent, startId);
		return;
	}

	//By commenting out the line below, this makes this service backwards compatible with pre "Android 2.2" OS versions.
	//@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("PhoneMonitoringService.onStartCommand().");
		StartPhoneMonitoringService();
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
    	//unregisterSMSObserver();
    	super.onDestroy();
    	return;
	}
		
	private void StartPhoneMonitoringService(){

		
		
		
		
        
        return;
	}
		
}
