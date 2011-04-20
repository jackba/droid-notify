package apps.droidnotify;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.telephony.TelephonyManager;

public class PhoneService extends Service {

	//================================================================================
    // Properties
    //================================================================================
	
    public final int INCOMING_CALL_TYPE = android.provider.CallLog.Calls.INCOMING_TYPE;
    public final int OUTGOING_CALL_TYPE = android.provider.CallLog.Calls.OUTGOING_TYPE;
    public final int MISSED_CALL_TYPE = android.provider.CallLog.Calls.MISSED_TYPE;
    
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
		if (Log.getDebug()) Log.v("PhoneService.PhoneService()");
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
			if (Log.getDebug()) Log.v("PhoneService.startPhoneMonitoringService()");
			if (PhoneStartingServiceWakeLock == null) {
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				PhoneStartingServiceWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DroidNotify.PhoneService");
				PhoneStartingServiceWakeLock.setReferenceCounted(false);
			}
			if (Log.getDebug()) Log.v("PhoneService.startPhoneMonitoringService() Aquireing wake lock");
			PhoneStartingServiceWakeLock.acquire();
			if (Log.getDebug()) Log.v("PhoneService.startPhoneMonitoringService() Starting service with intent");
			context.startService(intent);
		}
	}
	
	/**
	 * Called back by the service when it has finished processing notifications.
	 */
	public static void finishPhoneMonitoringService(Service service, int startId) {
		synchronized (PhoneStartingServiceSync) {
	    	if (Log.getDebug()) Log.v("PhoneService.finishPhoneMonitoringService()");
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
	        if (Log.getDebug()) Log.v("PhoneService.ServiceHandler.handleMessage() Action Received: " + action);
	        displayPhoneNotificationToScreen(intent);
	    	finishPhoneMonitoringService(PhoneService.this, serviceID);
	    }
	    
	}
	
	/**
	 * Display the notification to the screen.
	 */
	private void displayPhoneNotificationToScreen(final Intent intent) {
		if (Log.getDebug()) Log.v("PhoneService.displayPhoneNotificationToScreen()");
		TelephonyManager telemanager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
	    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
	    if (Log.getDebug()) Log.v("Phone.displaySMSNotificationToScreen() Current Call State: " + telemanager.getCallState());
	    // If the user is not in a call then start the check on the call log.
	    if (callStateIdle) {
	    	if (Log.getDebug()) Log.v("PhoneService.checkCallLog() Call state idle.");
	    	//Possibly need to "SLEEP" a few seconds to allow the phone log to be updated before we query it.
	        checkCallLog(intent);
	    }
	}
	
	private void checkCallLog(Intent intent){
		if (Log.getDebug()) Log.v("PhoneService.checkCallLog()");
		Bundle bundle = intent.getExtras();
		bundle.putInt("notificationType", 0);
		boolean missedCalls = false;
		final String[] projection = null;
		final String selection = null;
		final String[] selectionArgs = null;
		final String sortOrder = "DATE DESC";
	    Cursor cursor = _context.getContentResolver().query(
	    		Uri.parse("content://call_log/calls"),
	    		projection,
	    		selection,
				selectionArgs,
				sortOrder);
	    if (cursor != null) {
	    	while (cursor.moveToNext()) { 
	    		String callNumber = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
	    		String callDate = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE));
	    		String callType = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE));
	    		String isCallNew = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NEW));
	    		if (Log.getDebug()) Log.v("PhoneService.checkCallLog() Checking Call: " + callNumber + " Received At: " + callDate + " Call Type: " + callType + " Is Call New? " + isCallNew);
	    		if(Integer.parseInt(callType) == MISSED_CALL_TYPE && Integer.parseInt(isCallNew) > 0){
    				if (Log.getDebug()) Log.v("PhoneService.checkCallLog() Missed Call Found: " + callNumber);
    				//Start Notification
    				missedCalls = true;

    				//TODO Need to create an array of missed calls and pass that into the Notification Activity.
    				
    				
    				
    			}else{
    				break;
    			}
	    	}
	    	cursor.close();
	    }
	    if (Log.getDebug()) Log.v("PhoneService.checkCallLog() Missed Calls? " + missedCalls); 
	    if(missedCalls){
	    	if (Log.getDebug()) Log.v("PhoneService.checkCallLog() Display Phone Notification Window");    	
	    	Intent newIntent = new Intent(getContext(), NotificationActivity.class);
	    	newIntent.putExtras(bundle);
	    	newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	    	getContext().startActivity(newIntent);
	    }
	}
	
}
