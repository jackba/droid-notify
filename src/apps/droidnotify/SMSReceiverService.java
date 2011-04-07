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
import android.telephony.SmsMessage.MessageClass;
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
    private int SMSResultCode;
	private static Object SMSStartingServiceSync = new Object();
	private static PowerManager.WakeLock SMSStartingService;
	
	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * 
	 */	
	public SMSReceiverService() {
		if (Log.DEBUG) Log.v("SMSReceiverService.SMSService().");
	}
	
	//================================================================================
	// Accessors
	//================================================================================
	  
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * 
	 */
	@Override
	public IBinder onBind(Intent intent) {
		if (Log.DEBUG) Log.v("SMSReceiverService.onBind()");
		return null;
	}
	
	/**
	 * 
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		if (Log.DEBUG) Log.v("SMSReceiverService.onCreate()");
	    HandlerThread thread = new HandlerThread("DroidNotify", Process.THREAD_PRIORITY_BACKGROUND);
	    thread.start();
	    _context = getApplicationContext();
	    SMSServiceLooper = thread.getLooper();
	    SMSServiceHandler = new ServiceHandler(SMSServiceLooper);
	}
	
	/**
	 * 
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (Log.DEBUG) Log.v("SMSReceiverService.onStart()");
		SMSResultCode = intent != null ? intent.getIntExtra("result", 0) : 0;
	    Message msg = SMSServiceHandler.obtainMessage();
	    msg.arg1 = startId;
	    msg.obj = intent;
	    SMSServiceHandler.sendMessage(msg);
	}
	
	/**
	 * 
	 */
	@Override
	public void onDestroy() {
    	super.onDestroy();
    	if (Log.DEBUG) Log.v("SMSReceiverService.onDestroy()");
		SMSServiceLooper.quit();
	}
	
	/**
	 * Start the service to process the current event notifications, acquiring the
	 * wake lock before returning to ensure that the service will run.
	 */
	public static void startSMSMonitoringService(Context context, Intent intent){
		synchronized (SMSStartingServiceSync) {
			if (Log.DEBUG) Log.v("SMSReceiverService.startSMSMonitoringService()");
			if (SMSStartingService == null) {
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				SMSStartingService = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DroidNotify.SMSReceiverService");
				SMSStartingService.setReferenceCounted(false);
			}
			if (Log.DEBUG) Log.v("SMSReceiverService.startSMSMonitoringService() Aquireing wake lock");
			SMSStartingService.acquire();
			if (Log.DEBUG) Log.v("SMSReceiverService.startSMSMonitoringService() Starting service with intent");
			context.startService(intent);
		}
	}
	
	/**
	 * Called back by the service when it has finished processing notifications,
	 * releasing the wake lock if the service is now stopping.
	 */
	public static void finishSMSMonitoringService(Service service, int startId) {
		synchronized (SMSStartingServiceSync) {
	    	if (Log.DEBUG) Log.v("SMSReceiverService.finishSMSMonitoringService()");
    		if (SMSStartingService != null) {
    			if (service.stopSelfResult(startId)) {
    				SMSStartingService.release();
    			}
    		}
		}
	}

	/**
     * Get SMS messages from the Intent object.
     */
	public static final SmsMessage[] getMessagesFromIntent(Intent intent) {
		if (Log.DEBUG) Log.v("SMSReceiver.getMessagesFromIntent()");
		Bundle bundle = intent.getExtras();        
		SmsMessage[] msgs = null;           
		if (bundle != null){
          // Retrieve the SMS message received
          Object[] pdus = (Object[]) bundle.get("pdus");
          msgs = new SmsMessage[pdus.length];  
		}
		return msgs;
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * 
	 */
	private final class ServiceHandler extends Handler {
		
		/**
		 * 
		 */
	    public ServiceHandler(Looper looper) {
	    	super(looper);
	    	if (Log.DEBUG) Log.v("SMSReceiverService.ServiceHandler.ServiceHandler()");
	    }
		
		/**
		 * 
		 */
	    @Override
	    public void handleMessage(Message msg) {
	    	if (Log.DEBUG) Log.v("SMSReceiverService.ServiceHandler.HandleMessage()");
	    	int serviceId = msg.arg1;
	    	Intent intent = (Intent) msg.obj;
	    	String action = intent.getAction();
	        String dataType = intent.getType();
	        if (Log.DEBUG) Log.v("SMSReceiverService.ServiceHandler.handleMessage() Action Received: " + action);
	        if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {
	        	handleSMSMessageReceived(intent);
	        }
	    	finishSMSMonitoringService(SMSReceiverService.this, serviceId);
	    }
	    
	}
	
	/**
     * Handle the SMS message that was received.
     */
	private void handleSMSMessageReceived(Intent intent) {
		if (Log.DEBUG) Log.v("SMSReceiver.handleSMSMessageReceived()");
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			SmsMessage[] SMSMessages = getMessagesFromIntent(intent);
			if (SMSMessages != null) {
				displaySMSNotificationToScreen(new TextMessage(_context, SMSMessages, System.currentTimeMillis()));
			}
		}
	}
	
	/**
	 * 
	 */
	private void displaySMSNotificationToScreen(TextMessage SMSMessage) {
		if (Log.DEBUG) Log.v("SMSReceiver.displaySMSNotificationToScreen()");
	    // Class 0 SMS, let the system handle this
	    if (SMSMessage.getMessageType() == 0 &&
	    	SMSMessage.getMessageClass() == MessageClass.CLASS_0) {
	    	return;
	    }

	    // Fetch call state, if the user is in a call or the phone is ringing we don't want to show the popup
	    TelephonyManager telemanager = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);
	    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;

	    //If the user is not in a call then show the popup activity
	    if (callStateIdle) {
	    	if (Log.DEBUG) Log.v("!!!!!Display SMS Popup Window!!!!!");
	    	//ManageWakeLock.acquireFull(context);
	    	_context.startActivity(SMSMessage.getPopupIntent());
	    }
	}
	
}
