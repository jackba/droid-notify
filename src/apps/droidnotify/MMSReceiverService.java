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
 * This class listens for incoming text messages and triggers the event.
 * 
 * @author Camille Sevigny
 *
 */
public class MMSReceiverService extends Service {

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
	private ServiceHandler MMSServiceHandler;
    private Looper MMSServiceLooper;
	private static Object MMSStartingServiceSync = new Object();
	private static PowerManager.WakeLock _wakeLock;
	
	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * MMSReceiverService constructor.
	 */	
	public MMSReceiverService() {
		if (Log.getDebug()) Log.v("MMSReceiverService.MMSService().");
	}
	
	//================================================================================
	// Accessors
	//================================================================================

	/**
	 * Set the context property.
	 */
	public void setContext(Context context) {
		if (Log.getDebug()) Log.v("MMSReceiverService.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 */
	public Context getContext() {
		if (Log.getDebug()) Log.v("MMSReceiverService.getContext()");
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
		if (Log.getDebug()) Log.v("MMSReceiverService.onBind()");
		return null;
	}
	
	/**
	 * 
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		if (Log.getDebug()) Log.v("MMSReceiverService.onCreate()");
	    HandlerThread thread = new HandlerThread("DroidNotify", Process.THREAD_PRIORITY_BACKGROUND);
	    thread.start();
	    setContext(getApplicationContext());
	    MMSServiceLooper = thread.getLooper();
	    MMSServiceHandler = new ServiceHandler(MMSServiceLooper);
	}
	
	/**
	 * 
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (Log.getDebug()) Log.v("MMSReceiverService.onStart()");
	    Message message = MMSServiceHandler.obtainMessage();
	    message.arg1 = startId;
	    message.obj = intent;
	    MMSServiceHandler.sendMessage(message);
	}
	
	/**
	 * 
	 */
	@Override
	public void onDestroy() {
    	super.onDestroy();
    	if (Log.getDebug()) Log.v("MMSReceiverService.onDestroy()");
		MMSServiceLooper.quit();
	}
	
	/**
	 * Start the service to process the current event notifications.
	 * Acquiring the wake lock before returning to ensure that the service will run.
	 */
	public static void startMMSMonitoringService(Context context, Intent intent){
		synchronized (MMSStartingServiceSync) {
			if (Log.getDebug()) Log.v("MMSReceiverService.startMMSMonitoringService()");
			PowerManager.WakeLock wakeLock = _wakeLock;
			if (wakeLock == null) {
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DroidNotify.MMSReceiverService");
				wakeLock.setReferenceCounted(false);
			}
			if (Log.getDebug()) Log.v("MMSReceiverService.startMMSMonitoringService() Aquired wake lock");
			wakeLock.acquire();
			if (Log.getDebug()) Log.v("MMSReceiverService.startMMSMonitoringService() Starting service with intent");
			context.startService(intent);
		}
	}
	
	/**
	 * Called back by the service when it has finished processing notifications.
	 */
	public static void finishMMSMonitoringService(Service service, int startId) {
		synchronized (MMSStartingServiceSync) {
	    	if (Log.getDebug()) Log.v("MMSReceiverService.finishMMSMonitoringService()");
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
	    	if (Log.getDebug()) Log.v("MMSReceiverService.ServiceHandler.ServiceHandler()");
	    }
		
		/**
		 * Handle the message that was received.
		 */
	    @Override
	    public void handleMessage(Message message) {
	    	if (Log.getDebug()) Log.v("MMSReceiverService.ServiceHandler.HandleMessage()");
	    	int serviceID = message.arg1;
	    	Intent intent = (Intent) message.obj;
	    	String action = intent.getAction();
	        if (Log.getDebug()) Log.v("MMSReceiverService.ServiceHandler.handleMessage() Action Received: " + action);
	        if (action.equals("android.provider.Telephony.MMS_RECEIVED") || action.equals("android.provider.Telephony.WAP_PUSH_RECEIVED")) {
	        	handleMMSMessageReceived(intent);
	        }
	    	finishMMSMonitoringService(MMSReceiverService.this, serviceID);
	    }
	    
	}
	
	/**
     * Handle the MMS message that was received.
     * Trigger the notification if the message is valid.
     */
	private void handleMMSMessageReceived(Intent intent) {
		if (Log.getDebug()) Log.v("MMSReceiver.handleMMSMessageReceived()");
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			//displayMMSNotificationToScreen(intent);
		}
	}
	
	/**
	 * Display the notification to the screen.
	 * Send add the MMS message to the intent object that we created for the new activity.
	 */
	private void displayMMSNotificationToScreen(Intent intent) {
		if (Log.getDebug()) Log.v("MMSReceiver.displayMMSNotificationToScreen()");
//		Bundle bundle = intent.getExtras();
//		bundle.putInt("notificationType", NOTIFICATION_TYPE_MMS);
//	    // Get the call state, if the user is in a call or the phone is ringing, don't show the notification.
//	    TelephonyManager telemanager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
//	    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
//	    // If the user is not in a call then show the notification activity.
//	    if (callStateIdle) {
//	    	if (Log.getDebug()) Log.v("MMSReceiver.displayMMSNotificationToScreen() Display MMS Notification Window");    	
//	    	Intent newIntent = new Intent(getContext(), NotificationActivity.class);
//	    	newIntent.putExtras(bundle);
//	    	newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//	    	getContext().startActivity(newIntent);
//	    }
	}

}
