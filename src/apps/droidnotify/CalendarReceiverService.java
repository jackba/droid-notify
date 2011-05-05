package apps.droidnotify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.telephony.TelephonyManager;

/**
 * 
 * @author Camille Sevigny
 *
 */
public class CalendarReceiverService extends Service {

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
    
	private Context _context;
	private ServiceHandler CalendarServiceHandler;
    private Looper CalendarServiceLooper;
	private static Object CalendarStartingServiceSync = new Object();
	private static PowerManager.WakeLock _wakeLock;
	
	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * CalendarService constructor.
	 */	
	public CalendarReceiverService() {
		if (Log.getDebug()) Log.v("CalendarReceiverService.CalendarService()");
	}
	
	//================================================================================
	// Accessors
	//================================================================================

	/**
	 * Set the context property.
	 */
	public void setContext(Context context) {
		if (Log.getDebug()) Log.v("CalendarReceiverService.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 */
	public Context getContext() {
		if (Log.getDebug()) Log.v("CalendarReceiverService.getContext()");
	    return _context;
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * 
	 */
	@Override
	public IBinder onBind(Intent intent) {
		if (Log.getDebug()) Log.v("CalendarReceiverService.onBind()");
		return null;
	}
	
	/**
	 * 
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		if (Log.getDebug()) Log.v("CalendarReceiverService.onCreate()");
	    HandlerThread thread = new HandlerThread("DroidNotify", Process.THREAD_PRIORITY_BACKGROUND);
	    thread.start();
	    setContext(getApplicationContext());
	    CalendarServiceLooper = thread.getLooper();
	    CalendarServiceHandler = new ServiceHandler(CalendarServiceLooper);
	}
	
	/**
	 * 
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (Log.getDebug()) Log.v("CalendarReceiverService.onStart()");
	    Message message = CalendarServiceHandler.obtainMessage();
	    message.arg1 = startId;
	    message.obj = intent;
	    CalendarServiceHandler.sendMessage(message);
	}
	
	/**
	 * 
	 */
	@Override
	public void onDestroy() {
    	super.onDestroy();
    	if (Log.getDebug()) Log.v("CalendarReceiverService.onDestroy()");
    	CalendarServiceLooper.quit();
	}
	
	/**
	 * Start the service to process the current event notifications.
	 * Acquiring the wake lock before returning to ensure that the service will run.
	 */
	public static void startCalendarMonitoringService(Context context, Intent intent){
		synchronized (CalendarStartingServiceSync) {
			if (Log.getDebug()) Log.v("CalendarReceiverService.startCalendarMonitoringService()");
			PowerManager.WakeLock wakeLock = _wakeLock;
			if (wakeLock == null) {
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DroidNotify.CalendarService");
				wakeLock.setReferenceCounted(false);
			}
			if (Log.getDebug()) Log.v("CalendarReceiverService.startCalendarMonitoringService() Aquired wake lock");
			wakeLock.acquire();
			if (Log.getDebug()) Log.v("CalendarReceiverService.startCalendarMonitoringService() Starting service with intent");
			context.startService(intent);
		}
	}
	
	/**
	 * Called back by the service when it has finished processing notifications.
	 */
	public static void finishCalendarMonitoringService(Service service, int startId) {
		synchronized (CalendarStartingServiceSync) {
	    	if (Log.getDebug()) Log.v("CalendarReceiverService.finishCalendarMonitoringService()");
	    	PowerManager.WakeLock wakeLock = _wakeLock;
    		if (wakeLock != null) {
    			if (service.stopSelfResult(startId)) {
    				wakeLock.release();
    			}
    		}
		}
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * This class has something to do with the new thread we started. 
	 * I am not completely sure how this part works.
	 * I copied this from another project.
	 */
	private final class ServiceHandler extends Handler {
		
		/**
		 * ServiceHandler constructor.
		 */
	    public ServiceHandler(Looper looper) {
	    	super(looper);
	    	if (Log.getDebug()) Log.v("CalendarReceiverService.ServiceHandler.ServiceHandler()");
	    }
		
		/**
		 * Handle the message that was received.
		 */
	    @Override
	    public void handleMessage(Message message) {
	    	if (Log.getDebug()) Log.v("CalendarReceiverService.ServiceHandler.HandleMessage()");
	    	int serviceID = message.arg1;
	    	Intent intent = (Intent) message.obj;
	    	String action = intent.getAction();
	        if (Log.getDebug()) Log.v("CalendarReceiverService.ServiceHandler.handleMessage() Action Received: " + action);
	        displayCalendarNotificationToScreen();
	    	finishCalendarMonitoringService(CalendarReceiverService.this, serviceID);
	    }
	    
	}
	
	/**
	 * 
	 * 
	 * @param intent
	 */
	private void displayCalendarNotificationToScreen() {
		if (Log.getDebug()) Log.v("CalendarReceiverService.displayCalendarNotificationToScreen()");
//    	Intent intent = new Intent(getContext(), CalendarAlarmReceiver.class);
//    	PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
//		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (5 * 1000), 30 * 1000, pendingIntent);
	}
	
}
