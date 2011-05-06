package apps.droidnotify;

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
public class PhoneReceiverService_OLD extends Service {

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
	private ServiceHandler PhoneServiceHandler;
    private Looper PhoneServiceLooper;
	private static Object PhoneStartingServiceSync = new Object();
	private static PowerManager.WakeLock _wakeLock;
	
	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * PhoneService constructor.
	 */	
	public PhoneReceiverService_OLD() {
		if (Log.getDebug()) Log.v("PhoneReceiverService_OLD.PhoneService()");
	}
	
	//================================================================================
	// Accessors
	//================================================================================

	/**
	 * Set the context property.
	 */
	public void setContext(Context context) {
		if (Log.getDebug()) Log.v("PhoneReceiverService_OLD.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 */
	public Context getContext() {
		if (Log.getDebug()) Log.v("PhoneReceiverService_OLD.getContext()");
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
		if (Log.getDebug()) Log.v("PhoneReceiverService_OLD.onBind()");
		return null;
	}
	
	/**
	 * 
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		if (Log.getDebug()) Log.v("PhoneReceiverService_OLD.onCreate()");
	    HandlerThread thread = new HandlerThread("DroidNotify", Process.THREAD_PRIORITY_BACKGROUND);
	    thread.start();
	    setContext(getApplicationContext());
	    PhoneServiceLooper = thread.getLooper();
	    PhoneServiceHandler = new ServiceHandler(PhoneServiceLooper);
	}
	
	/**
	 * 
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (Log.getDebug()) Log.v("PhoneReceiverService_OLD.onStart()");
	    Message message = PhoneServiceHandler.obtainMessage();
	    message.arg1 = startId;
	    message.obj = intent;
	    PhoneServiceHandler.sendMessage(message);
	}
	
	/**
	 * 
	 */
	@Override
	public void onDestroy() {
    	super.onDestroy();
    	if (Log.getDebug()) Log.v("PhoneReceiverService_OLD.onDestroy()");
    	PhoneServiceLooper.quit();
	}
	
	/**
	 * Start the service to process the current event notifications.
	 * Acquiring the wake lock before returning to ensure that the service will run.
	 */
	public static void startPhoneMonitoringService(Context context, Intent intent){
		synchronized (PhoneStartingServiceSync) {
			if (Log.getDebug()) Log.v("PhoneReceiverService_OLD.startPhoneMonitoringService()");
			PowerManager.WakeLock wakeLock = _wakeLock;
			if (wakeLock == null) {
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DroidNotify.PhoneService");
				wakeLock.setReferenceCounted(false);
			}
			if (Log.getDebug()) Log.v("PhoneReceiverService_OLD.startPhoneMonitoringService() Aquired wake lock");
			wakeLock.acquire();
			if (Log.getDebug()) Log.v("PhoneReceiverService_OLD.startPhoneMonitoringService() Starting service with intent");
			context.startService(intent);
		}
	}
	
	/**
	 * Called back by the service when it has finished processing notifications.
	 */
	public static void finishPhoneMonitoringService(Service service, int startId) {
		synchronized (PhoneStartingServiceSync) {
	    	if (Log.getDebug()) Log.v("PhoneReceiverService_OLD.finishPhoneMonitoringService()");
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
	    	if (Log.getDebug()) Log.v("PhoneReceiverService_OLD.ServiceHandler.ServiceHandler()");
	    }
		
		/**
		 * Handle the message that was received.
		 */
	    @Override
	    public void handleMessage(Message message) {
	    	if (Log.getDebug()) Log.v("PhoneReceiverService_OLD.ServiceHandler.HandleMessage()");
	    	int serviceID = message.arg1;
	    	Intent intent = (Intent) message.obj;
	    	String action = intent.getAction();
	        if (Log.getDebug()) Log.v("PhoneReceiverService_OLD.ServiceHandler.handleMessage() Action Received: " + action);
	        displayPhoneNotificationToScreen();
	    	finishPhoneMonitoringService(PhoneReceiverService_OLD.this, serviceID);
	    }
	    
	}
	
	/**
	 * Display the notification to the screen.
	 * Let the activity check the call log and determine if we need to show a notification or not.
	 * 
	 * @param intent
	 */
	private void displayPhoneNotificationToScreen() {
		if (Log.getDebug()) Log.v("PhoneReceiverService_OLD.displayPhoneNotificationToScreen()");
		Context context = getContext();
		TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
	    if (Log.getDebug()) Log.v("PhoneReceiverService_OLD.displayPhoneNotificationToScreen() Current Call State: " + telemanager.getCallState());
	    // If the user is not in a call then start the check on the call log.
	    if (Log.getDebug()) Log.v("PhoneReceiverService_OLD.displayPhoneNotificationToScreen() Current Call State Idle? " + callStateIdle); 
	    if (callStateIdle) {
	    	if (Log.getDebug()) Log.v("PhoneReceiverService_OLD.displayPhoneNotificationToScreen() Call state idle.");
			Bundle bundle = new Bundle();
			bundle.putInt("notificationType", NOTIFICATION_TYPE_PHONE);
	    	Intent intent = new Intent(context, NotificationActivity.class);
	    	intent.putExtras(bundle);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	    	context.startActivity(intent);
	    }
	}
	
}
