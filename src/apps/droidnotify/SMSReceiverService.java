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
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

/**
 * This class listens for incoming text messages and triggers the event.
 */
public class SMSReceiverService extends Service {

	//================================================================================
    // Properties
    //================================================================================
	private Context _context;
	private ServiceHandler SMSServiceHandler;
    private Looper SMSServiceLooper;
	private static Object SMSStartingServiceSync = new Object();
	private static PowerManager.WakeLock SMSStartingServiceWakeLock;
	
	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * SMSReceiverService constructor.
	 */	
	public SMSReceiverService() {
		if (Log.getDebug()) Log.v("SMSReceiverService.SMSService().");
	}
	
	//================================================================================
	// Accessors
	//================================================================================

	/**
	 * Set the context property.
	 */
	public void setContext(Context context) {
		if (Log.getDebug()) Log.v("SMSReceiverService.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 */
	public Context getContext() {
		if (Log.getDebug()) Log.v("SMSReceiverService.getContext()");
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
		if (Log.getDebug()) Log.v("SMSReceiverService.onBind()");
		return null;
	}
	
	/**
	 * 
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		if (Log.getDebug()) Log.v("SMSReceiverService.onCreate()");
	    HandlerThread thread = new HandlerThread("DroidNotify", Process.THREAD_PRIORITY_BACKGROUND);
	    thread.start();
	    setContext(getApplicationContext());
	    SMSServiceLooper = thread.getLooper();
	    SMSServiceHandler = new ServiceHandler(SMSServiceLooper);
	}
	
	/**
	 * 
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (Log.getDebug()) Log.v("SMSReceiverService.onStart()");
	    Message message = SMSServiceHandler.obtainMessage();
	    message.arg1 = startId;
	    message.obj = intent;
	    SMSServiceHandler.sendMessage(message);
	}
	
	/**
	 * 
	 */
	@Override
	public void onDestroy() {
    	super.onDestroy();
    	if (Log.getDebug()) Log.v("SMSReceiverService.onDestroy()");
		SMSServiceLooper.quit();
	}
	
	/**
	 * Start the service to process the current event notifications.
	 * Acquiring the wake lock before returning to ensure that the service will run.
	 */
	public static void startSMSMonitoringService(Context context, Intent intent){
		synchronized (SMSStartingServiceSync) {
			if (Log.getDebug()) Log.v("SMSReceiverService.startSMSMonitoringService()");
			if (SMSStartingServiceWakeLock == null) {
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				SMSStartingServiceWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DroidNotify.SMSReceiverService");
				SMSStartingServiceWakeLock.setReferenceCounted(false);
			}
			if (Log.getDebug()) Log.v("SMSReceiverService.startSMSMonitoringService() Aquireing wake lock");
			SMSStartingServiceWakeLock.acquire();
			if (Log.getDebug()) Log.v("SMSReceiverService.startSMSMonitoringService() Starting service with intent");
			context.startService(intent);
		}
	}
	
	/**
	 * Called back by the service when it has finished processing notifications.
	 */
	public static void finishSMSMonitoringService(Service service, int startId) {
		synchronized (SMSStartingServiceSync) {
	    	if (Log.getDebug()) Log.v("SMSReceiverService.finishSMSMonitoringService()");
    		if (SMSStartingServiceWakeLock != null) {
    			if (service.stopSelfResult(startId)) {
    				SMSStartingServiceWakeLock.release();
    			}
    		}
		}
	}

//	/**
//     * Get SMS message from the Intent object.
//     */
//	public static final SmsMessage[] getMessagesFromIntent(Intent intent) {
//		if (Log.getDebug()) Log.v("SMSReceiver.getMessagesFromIntent()");
//		Bundle bundle = intent.getExtras();        
//		SmsMessage[] message = null;           
//		if (bundle != null){
//          // Retrieve the SMS message received from the intent object.
//          Object[] pdus = (Object[]) bundle.get("pdus");
//          message = new SmsMessage[pdus.length];  
//		}
//		return message;
//	}
	
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
	    	if (Log.getDebug()) Log.v("SMSReceiverService.ServiceHandler.ServiceHandler()");
	    }
		
		/**
		 * Handle the message that was received.
		 */
	    @Override
	    public void handleMessage(Message message) {
	    	if (Log.getDebug()) Log.v("SMSReceiverService.ServiceHandler.HandleMessage()");
	    	int serviceID = message.arg1;
	    	Intent intent = (Intent) message.obj;
	    	String action = intent.getAction();
	        //String dataType = intent.getType();
	        if (Log.getDebug()) Log.v("SMSReceiverService.ServiceHandler.handleMessage() Action Received: " + action);
	        if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {
	        	handleSMSMessageReceived(intent);
	        }
	    	finishSMSMonitoringService(SMSReceiverService.this, serviceID);
	    }
	    
	}
	
	/**
     * Handle the SMS message that was received.
     * Trigger the notification if the message is valid.
     */
	private void handleSMSMessageReceived(Intent intent) {
		if (Log.getDebug()) Log.v("SMSReceiver.handleSMSMessageReceived()");
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
//			SmsMessage[] message = getMessagesFromIntent(intent);
//			if (message != null) {
				displaySMSNotificationToScreen(intent);
//			}
		}
	}
	
	/**
	 * Display the notification to the screen.
	 * Send add the SMS message to the intent object that we created for the new activity.
	 */
	private void displaySMSNotificationToScreen(Intent intent) {
		if (Log.getDebug()) Log.v("SMSReceiver.displaySMSNotificationToScreen()");
		Bundle bundle = intent.getExtras();
		bundle.putInt("notificationType", 1);
	    // Get the call state, if the user is in a call or the phone is ringing, don't show the notification.
	    TelephonyManager telemanager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
	    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
	    // If the user is not in a call then show the notification activity.
	    if (callStateIdle) {
	    	if (Log.getDebug()) Log.v("SMSReceiver.displaySMSNotificationToScreen() Display SMS Notification Window");    	
	    	Intent newIntent = new Intent(getContext(), NotificationActivity.class);
	    	newIntent.putExtras(bundle);
	    	newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	    	getContext().startActivity(newIntent);
	    }
	}

}
