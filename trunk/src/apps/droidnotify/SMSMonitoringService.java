package apps.droidnotify;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SMSMonitoringService extends Service {

	public SMSMonitoringService() {
		Log.v("SMSMonitoringService.SMSMonitoringService().");
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.v("SMSMonitoringService.onBind().");
		return null;
	}

	@Override
	public void onCreate() {
		Log.v("SMSMonitoringService.onCreate().");
		StartSMSMonitoringService();
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.v("SMSMonitoringService.onStart().");
		StartSMSMonitoringService();
		super.onStart(intent, startId);
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("SMSMonitoringService.onStartCommand().");

		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
    	//unregisterSMSObserver();
    	super.onDestroy();
    	return;
	}
	
	private void StartSMSMonitoringService(){

		
		
		
		
        
        return;
	}
	
}
