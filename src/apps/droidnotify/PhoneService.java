package apps.droidnotify;

import android.app.Service;
import android.content.ContentResolver;
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
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

public class PhoneService extends Service {

	//================================================================================
    // Properties
    //================================================================================
	
	private Context _context;
	private ServiceHandler PhoneServiceHandler;
    private Looper PhoneServiceLooper;
	private static Object PhoneStartingServiceSync = new Object();
	private static PowerManager.WakeLock PhoneStartingServiceWakeLock;
	
	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * PhoneService constructor.
	 */	
	public PhoneService() {
		if (Log.getDebug()) Log.v("PhoneService.SMSService().");
	}
	
	//================================================================================
	// Accessors
	//================================================================================

	/**
	 * Set the context property.
	 */
	public void setContext(Context context) {
		if (Log.getDebug()) Log.v("PhoneService.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 */
	public Context getContext() {
		if (Log.getDebug()) Log.v("PhoneService.getContext()");
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
		if (Log.getDebug()) Log.v("PhoneService.onBind()");
		return null;
	}
	
	/**
	 * 
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		if (Log.getDebug()) Log.v("PhoneService.onCreate()");
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
		if (Log.getDebug()) Log.v("PhoneService.onStart()");
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
    	if (Log.getDebug()) Log.v("PhoneService.onDestroy()");
    	PhoneServiceLooper.quit();
	}
	
	/**
	 * Start the service to process the current event notifications.
	 * Acquiring the wake lock before returning to ensure that the service will run.
	 */
	public static void startPhoneMonitoringService(Context context, Intent intent){
		synchronized (PhoneStartingServiceSync) {
			if (Log.getDebug()) Log.v("PhoneService.startSMSMonitoringService()");
			if (PhoneStartingServiceWakeLock == null) {
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				PhoneStartingServiceWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DroidNotify.PhoneService");
				PhoneStartingServiceWakeLock.setReferenceCounted(false);
			}
			if (Log.getDebug()) Log.v("PhoneService.startSMSMonitoringService() Aquireing wake lock");
			PhoneStartingServiceWakeLock.acquire();
			if (Log.getDebug()) Log.v("PhoneService.startSMSMonitoringService() Starting service with intent");
			context.startService(intent);
		}
	}
	
	/**
	 * Called back by the service when it has finished processing notifications.
	 */
	public static void finishPhoneMonitoringService(Service service, int startId) {
		synchronized (PhoneStartingServiceSync) {
	    	if (Log.getDebug()) Log.v("PhoneService.finishSMSMonitoringService()");
    		if (PhoneStartingServiceWakeLock != null) {
    			if (service.stopSelfResult(startId)) {
    				PhoneStartingServiceWakeLock.release();
    			}
    		}
		}
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Honestly, I don't know why we need this extra class. 
	 * I will see if I can do this another way. 
	 * I copied this from antoher project without knowing why it's needed.
	 */
	private final class ServiceHandler extends Handler {
		
		/**
		 * ServiceHandler constructor.
		 */
	    public ServiceHandler(Looper looper) {
	    	super(looper);
	    	if (Log.getDebug()) Log.v("PhoneService.ServiceHandler.ServiceHandler()");
	    }
		
		/**
		 * Handle the message that was received.
		 */
	    @Override
	    public void handleMessage(Message message) {
	    	if (Log.getDebug()) Log.v("PhoneService.ServiceHandler.HandleMessage()");
	    	int serviceID = message.arg1;
	    	Intent intent = (Intent) message.obj;
	    	String action = intent.getAction();
	        //String dataType = intent.getType();
	        if (Log.getDebug()) Log.v("PhoneService.ServiceHandler.handleMessage() Action Received: " + action);
	        if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {
	        	handlePhoneMessageReceived(intent);
	        }
	    	finishPhoneMonitoringService(PhoneService.this, serviceID);
	    }
	    
	}
	
	/**
     * Handle the SMS message that was received.
     * Trigger the notification if the message is valid.
     */
	private void handlePhoneMessageReceived(Intent intent) {
		if (Log.getDebug()) Log.v("SMSReceiver.handleSMSMessageReceived()");
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
//			SmsMessage[] message = getMessagesFromIntent(intent);
//			if (message != null) {
				displayPhoneNotificationToScreen(intent);
//			}
		}
	}
	
	/**
	 * Display the notification to the screen.
	 * Send add the SMS message to the intent object that we created for the new activity.
	 */
	private void displayPhoneNotificationToScreen(Intent intent) {
		if (Log.getDebug()) Log.v("Phone.displaySMSNotificationToScreen()");
//		Bundle bundle = intent.getExtras();
//		bundle.putInt("notificationType", 0);
//	    // Get the call state, if the user is in a call or the phone is ringing, don't show the notification.
//	    TelephonyManager telemanager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
//	    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
//	    // If the user is not in a call then show the notification activity.
//	    if (callStateIdle) {
//	    	if (Log.getDebug()) Log.v("Phone.displaySMSNotificationToScreen() Display SMS Notification Window");    	
//	    	Intent newIntent = new Intent(getContext(), NotificationActivity.class);
//	    	newIntent.putExtras(bundle);
//	    	newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//	    	getContext().startActivity(newIntent);
//	    }
	    	
		Handler handler = new Handler();
		Context context = getContext();
     	ContentResolver contentresolver = context.getContentResolver();
     	contentresolver.registerContentObserver(
 	            android.provider.CallLog.Calls.CONTENT_URI, 
 	            true,
 	            new CallLogContentObserver(context, handler));
	    	
	}
	
}
