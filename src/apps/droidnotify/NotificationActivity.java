package apps.droidnotify;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.LinearLayout;

/**
 * This is the main activity that runs the notifications.
 * 
 * @author Camille Sévigny
 */
public class NotificationActivity extends Activity {

	//================================================================================
    // Constants
    //================================================================================
	
	private final String DROID_NOTIFY_WAKELOCK = "DROID_NOTIFY_WAKELOCK";
	private final String DROID_NOTIFY_KEYGUARD = "DROID_NOTIFY_KEYGUARD";

	private final int NOTIFICATION_TYPE_TEST = -1;
	private final int NOTIFICATION_TYPE_PHONE = 0;
	private final int NOTIFICATION_TYPE_SMS = 1;
	private final int NOTIFICATION_TYPE_MMS = 2;
	private final int NOTIFICATION_TYPE_CALENDAR = 3;
	private final int NOTIFICATION_TYPE_EMAIL = 4;

	private final String SCREEN_ENABLED_KEY = "screen_enabled";
	private final String SCREEN_DIM_ENABLED_KEY = "screen_dim_enabled";
	private final String KEYGUARD_ENABLED_KEY = "keyguard_enabled";	
	private final String MISSED_CALL_VIBRATE_ENABLED_KEY = "missed_call_vibrate_enabled";
	private final String SMS_VIBRATE_ENABLED_KEY = "sms_vibrate_enabled";
	private final String MMS_VIBRATE_ENABLED_KEY = "mms_vibrate_enabled";
	private final String CALENDAR_VIBRATE_ENABLED_KEY = "calendar_vibrate_enabled";
	private final String HAPTIC_FEEDBACK_ENABLED_KEY = "haptic_feedback_enabled";
	private final String SMS_DELETE_KEY = "sms_delete_button_action";
	private final String MMS_DELETE_KEY = "mms_delete_button_action";
	private final String WAKELOCK_TIMEOUT_KEY = "wakelock_timeout_settings";
	private final String KEYGUARD_TIMEOUT_KEY = "keyguard_timeout_settings";
	private final String MISSED_CALL_RINGTONE_ENABLED_KEY = "missed_call_ringtone_enabled";
	private final String SMS_RINGTONE_ENABLED_KEY = "sms_ringtone_enabled";
	private final String MMS_RINGTONE_ENABLED_KEY = "mms_ringtone_enabled";
	private final String CALENDAR_RINGTONE_ENABLED_KEY = "calendar_ringtone_enabled";
	private final String ALL_VIBRATE_ENABLED_KEY = "app_vibrations_enabled";
	private final String ALL_RINGTONE_ENABLED_KEY = "app_ringtones_enabled";
	private final String SMS_RINGTONE_KEY = "sms_ringtone_audio";
	private final String MMS_RINGTONE_KEY = "mms_ringtone_audio";
	private final String MISSED_CALL_RINGTONE_KEY = "missed_call_ringtone_audio";
	private final String CALENDAR_RINGTONE_KEY = "calendar_ringtone_audio";
	private final String RINGTONE_LENGTH_KEY = "ringtone_length_settings";
	private final String SMS_DISPLAY_UNREAD_KEY = "sms_display_unread_enabled";
	
	private final String SMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	private final String SMS_DELETE_ACTION_DELETE_THREAD = "1";
	private final String SMS_DELETE_ACTION_NOTHING = "2";
	private final String MMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	private final String MMS_DELETE_ACTION_DELETE_THREAD = "1";
	private final String MMS_DELETE_ACTION_NOTHING = "2";
	
	private final int DIALOG_DELETE_MESSAGE = 0;
	
	private final int MENU_ITEM_SETTINGS = R.id.app_settings;
	private final int CONTACT_WRAPPER_LINEAR_LAYOUT = R.id.contact_wrapper_linear_layout;
	private final int VIEW_CONTACT_CONTEXT_MENU = R.id.view_contact;
	private final int ADD_CONTACT_CONTEXT_MENU = R.id.add_contact;
	private final int CALL_CONTACT_CONTEXT_MENU = R.id.call_contact;
	private final int TEXT_CONTACT_CONTEXT_MENU = R.id.text_contact;	
	private final int EDIT_CONTACT_CONTEXT_MENU = R.id.edit_contact;
	private final int EDIT_EVENT_CONTEXT_MENU = R.id.edit_calendar_event;
	
	private final String EVENT_BEGIN_TIME = "beginTime";
	private final String EVENT_END_TIME = "endTime";
	
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug;
	private Bundle _bundle = null;
	private Context _context = null;
	private WakeLock _wakeLock;
	private KeyguardLock _keyguardLock; 
	private NotificationViewFlipper _notificationViewFlipper = null;
	private LinearLayout _mainActivityLayout = null;
	private InputMethodManager _inputMethodManager = null;
	private WakeLockHandler _wakeLockHandler = new WakeLockHandler();
	private KeyguardHandler _keyguardHandler = new KeyguardHandler();
	private Ringtone _ringtone = null;
	private RingtoneHandler _ringtoneHandler = new RingtoneHandler();
	private MotionEvent _downMotionEvent = null;

	//================================================================================
	// Constructors
	//================================================================================
	
	//================================================================================
	// Accessors
	//================================================================================
	  
	/**
	 * Set the notificationViewFlipper property.
	 * 
	 * @param bundle - Bundle for this activity.
	 */
	public void setBundle(Bundle bundle) {
		if (_debug) Log.v("NotificationActivity.setBundle()");
	    _bundle = bundle;
	}
	
	/**
	 * Get the notificationViewFlipper property.
	 * 
	 * @return bundle - Bundle for this activity.
	 */
	public Bundle getBundle() {
		if (_debug) Log.v("NotificationActivity.getBundle()");
	    return _bundle;
	} 

	/**
	 * Set the keyguardLock property.
	 * 
	 * @param keyguardLock - Phone's KeyguardLock.
	 */
	public void setKeyguardLock(KeyguardLock keyguardLock) {
		if (_debug) Log.v("NotificationActivity.setKeyguardLock()");
		_keyguardLock = keyguardLock;
	}
	
	/**
	 * Get the keyguardLock property.
	 * 
	 * @return keyguardLock - Phone's KeyguardLock.
	 */
	public KeyguardLock getKeyguardLock() {
		if (_debug) Log.v("NotificationActivity.getKeyguardLock()");
	    return _keyguardLock;
	}

	/**
	 * Set the wakeLock property.
	 * 
	 * @param wakeLock - Phone's WakeLock.
	 */
	public void setWakeLock(WakeLock wakeLock) {
		if (_debug) Log.v("NotificationActivity.setWakeLock()");
		_wakeLock = wakeLock;
	}
	
	/**
	 * Get the wakeLock property.
	 * 
	 * @return wakeLock - Phone's WakeLock.
	 */
	public WakeLock getWakeLock() {
		if (_debug) Log.v("NotificationActivity.getWakeLock()");
	    return _wakeLock;
	}

	/**
	 * Set the context property.
	 * 
	 * @param context - Application's Context.
	 */
	public void setContext(Context context) {
		if (_debug) Log.v("NotificationActivity.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 * 
	 * @return context - Application's Context.
	 */
	public Context getContext() {
		if (_debug) Log.v("NotificationActivity.getContext()");
	    return _context;
	}
	
	/**
	 * Set the notificationViewFlipper property.
	 * 
	 * @param notificationViewFlipper - Applications' ViewFlipper.
	 */
	public void setNotificationViewFlipper(NotificationViewFlipper notificationViewFlipper) {
		if (_debug) Log.v("NotificationActivity.setNotificationViewFlipper()");
	    _notificationViewFlipper = notificationViewFlipper;
	}
	
	/**
	 * Get the notificationViewFlipper property.
	 * 
	 * @return notificationViewFlipper - Applications' ViewFlipper.
	 */
	public NotificationViewFlipper getNotificationViewFlipper() {
		if (_debug) Log.v("NotificationActivity.getNotificationViewFlipper()");
	    return _notificationViewFlipper;
	}
	  
	/**
	 * Set the mainActivityLayout property.
	 * 
	 * @param mainActivityLayout - The "main activity" LinearLayout
	 */
	public void setMainActivityLayout(LinearLayout mainActivityLayout) {
		if (_debug) Log.v("NotificationActivity.setMainActivityLayout()");
		_mainActivityLayout = mainActivityLayout;
	}
	
	/**
	 * Get the mainActivityLayout property.
	 * 
	 * @return mainActivityLayout - The "main activity" LinearLayout
	 */
	public LinearLayout getMainActivityLayout() {
		if (_debug) Log.v("NotificationActivity.getMainActivityLayout()");
	    return _mainActivityLayout;
	}

	/**
	 * Set the inputMethodManager property.
	 * 
	 * @param InputMethodManager
	 */
	public void setInputMethodManager(InputMethodManager inputMethodManager) {
		if (_debug) Log.v("NotificationActivity.setInputMethodManager()");
		_inputMethodManager = inputMethodManager;
	}
	
	/**
	 * Get the inputMethodManager property.
	 * 
	 * @return inputMethodManager - InputMethodManager
	 */
	public InputMethodManager getInputMethodManager() {
		if (_debug) Log.v("NotificationActivity.getInputMethodManager()");
	    return _inputMethodManager;
	}	

	/**
	 * Set the ringtone property.
	 * 
	 * @param Ringtone
	 */
	public void setRingtone(Ringtone ringtone) {
		if (_debug) Log.v("NotificationActivity.setRingtone()");
		_ringtone = ringtone;
	}
	
	/**
	 * Get the ringtone property.
	 * 
	 * @return ringtone - Ringtone
	 */
	public Ringtone getRingtone() {
		if (_debug) Log.v("NotificationActivity.getRingtone()");
	    return _ringtone;
	}

	/**
	 * Set the oldTouchValue property.
	 * 
	 * @param oldTouchValue - The touch value of a MotionEvent.
	 */
	public void setDownMotionEvent(MotionEvent downMotionEvent) {
		if (_debug) Log.v("NotificationActivity.setDownMotionEvent()");
	    _downMotionEvent = downMotionEvent;
	}
	
	/**
	 * Get the oldTouchValue property.
	 * 
	 * @return oldTouchValue - The touch value of a MotionEvent.
	 */
	public MotionEvent getDownMotionEvent() {
		if (_debug) Log.v("NotificationActivity.getDownMotionEvent()");
	    return _downMotionEvent;
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
  
	/**
	 * Creates the menu item for this activity.
	 * 
	 * @param menu - Menu.
	 * 
	 * @return boolean - Returns true to indicate that the menu was created.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.optionsmenu, menu);
	    return true;
	}
	
	/**
	 * Handle the users selecting of the menu items.
	 * 
	 * @param menuItem - Menu Item .
	 * 
	 * @return boolean - Returns true to indicate that the action was handled.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
	    // Handle item selection
	    switch (menuItem.getItemId()){
	    
	    	case MENU_ITEM_SETTINGS:
	    		launchPreferenceScreen();
	    		return true;
	    		
	    }
	    return false;
	}
	
	/**
	 * Create Context Menu (Long-press menu).
	 * 
	 * @param contextMenu - ContextMenu
	 * @param view - View
	 * @param contextMenuInfo - ContextMenuInfo
	 */
	@Override
	public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenuInfo contextMenuInfo) {
	    super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
	    if (_debug) Log.v("NotificationActivity.onCreateContextMenu()");
	    switch (view.getId()) {

	        /*
	         * Contact info/photo ConextMenu.
	         */
			case CONTACT_WRAPPER_LINEAR_LAYOUT:
				MenuInflater menuInflater = getMenuInflater();
				NotificationViewFlipper notificationViewFlipper = getNotificationViewFlipper();
				int currentNotification = notificationViewFlipper.getCurrentNotification();
				Notification notification = notificationViewFlipper.getNotification(currentNotification);
				String phoneNumber = notification.getPhoneNumber();
				int notificationType = notification.getNotificationType();
				if (_debug) Log.v("NotificationActivity.onCreateContextMenu() Does contact exist?" + notification.getContactExists());
				//Add the header text to the menu.
				if(notificationType == NOTIFICATION_TYPE_CALENDAR){
					contextMenu.setHeaderTitle("Calendar Event");
				}else{
					if(notification.getContactExists()){
						contextMenu.setHeaderTitle(notification.getContactName()); 
					}else{
						contextMenu.setHeaderTitle(phoneNumber); 
					}
				}
				menuInflater.inflate(R.menu.notificationcontextmenu, contextMenu);
				//Remove menu options based on the NotificationType.
				if(notification.getContactExists()){
					MenuItem addMenuItem = contextMenu.findItem(ADD_CONTACT_CONTEXT_MENU);
					addMenuItem.setVisible(false);
				}else{
					MenuItem viewMenuItem = contextMenu.findItem(VIEW_CONTACT_CONTEXT_MENU);
					viewMenuItem.setVisible(false);
					MenuItem editMenuItem = contextMenu.findItem(EDIT_CONTACT_CONTEXT_MENU);
					editMenuItem.setVisible(false);
				}
			    if(notificationType == NOTIFICATION_TYPE_PHONE){
			    	MenuItem callMenuItem = contextMenu.findItem(CALL_CONTACT_CONTEXT_MENU);
			    	callMenuItem.setVisible(false);
					MenuItem editEventMenuItem = contextMenu.findItem(EDIT_EVENT_CONTEXT_MENU);
					editEventMenuItem.setVisible(false);
			    }
			    if(notificationType == NOTIFICATION_TYPE_SMS){
			    	MenuItem textMenuItem = contextMenu.findItem(TEXT_CONTACT_CONTEXT_MENU);
			    	textMenuItem.setVisible(false);
					MenuItem editEventMenuItem = contextMenu.findItem(EDIT_EVENT_CONTEXT_MENU);
					editEventMenuItem.setVisible(false);
			    }
			    if(notificationType == NOTIFICATION_TYPE_MMS){
			    	MenuItem textMenuItem = contextMenu.findItem(TEXT_CONTACT_CONTEXT_MENU);
			    	textMenuItem.setVisible(false);
					MenuItem editEventMenuItem = contextMenu.findItem(EDIT_EVENT_CONTEXT_MENU);
					editEventMenuItem.setVisible(false);
			    }
			    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
					MenuItem addMenuItem = contextMenu.findItem(ADD_CONTACT_CONTEXT_MENU);
					addMenuItem.setVisible(false);
			    	MenuItem viewMenuItem = contextMenu.findItem(VIEW_CONTACT_CONTEXT_MENU);
					viewMenuItem.setVisible(false);
					MenuItem editMenuItem = contextMenu.findItem(EDIT_CONTACT_CONTEXT_MENU);
					editMenuItem.setVisible(false);
					MenuItem textMenuItem = contextMenu.findItem(TEXT_CONTACT_CONTEXT_MENU);
					textMenuItem.setVisible(false);
					MenuItem callMenuItem = contextMenu.findItem(CALL_CONTACT_CONTEXT_MENU);
					callMenuItem.setVisible(false);
			    }
			    if(notificationType == NOTIFICATION_TYPE_EMAIL){
			    	//TODO - Email
			    }
	    }
		    
	}

	/**
	 * Context Menu Item Selected (Long-press menu item selected).
	 * 
	 * @param menuItem - Create the context meny items for this Activity.
	 */
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		if (_debug) Log.v("NotificationActivity.onContextItemSelected()");
		Context context = getContext();
		NotificationViewFlipper notificationViewFlipper = getNotificationViewFlipper();
		int currentNotification = notificationViewFlipper.getCurrentNotification();
		Notification notification = notificationViewFlipper.getNotification(currentNotification);
		String phoneNumber = notification.getPhoneNumber();
		long contactID = notification.getContactID();
		Intent intent = null;
		//customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		switch (menuItem.getItemId()) {
			case VIEW_CONTACT_CONTEXT_MENU:
				try{
					intent = new Intent(Intent.ACTION_VIEW);
					Uri viewContactURI = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactID));
				    intent.setData(viewContactURI);	
			        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
			        		| Intent.FLAG_ACTIVITY_SINGLE_TOP
			        		| Intent.FLAG_ACTIVITY_CLEAR_TOP
			        		| Intent.FLAG_ACTIVITY_NO_HISTORY
			        		| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				    context.startActivity(intent);
					return true;
				}catch(Exception ex){
					if (_debug) Log.e("NotificationActivity.onContextItemSelected() VIEW_CONTACT_CONTEXT_MENU ERROR: " + ex.toString());
					return false;
				}
			case ADD_CONTACT_CONTEXT_MENU:
				try{
					intent = new Intent(Intent.ACTION_INSERT);
					intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
					intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);			
			        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
			        		| Intent.FLAG_ACTIVITY_SINGLE_TOP
			        		| Intent.FLAG_ACTIVITY_CLEAR_TOP
			        		| Intent.FLAG_ACTIVITY_NO_HISTORY
			        		| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				    context.startActivity(intent);
					return true;
				}catch(Exception ex){
					if (_debug) Log.e("NotificationActivity.onContextItemSelected() ADD_CONTACT_CONTEXT_MENU ERROR: " + ex.toString());
					return false;
				}
			case CALL_CONTACT_CONTEXT_MENU:
				try{
					intent = new Intent(Intent.ACTION_CALL);
			        intent.setData(Uri.parse("tel:" + phoneNumber));		
			        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
			        		| Intent.FLAG_ACTIVITY_SINGLE_TOP
			        		| Intent.FLAG_ACTIVITY_CLEAR_TOP
			        		| Intent.FLAG_ACTIVITY_NO_HISTORY
			        		| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				    context.startActivity(intent);
					return true;
				}catch(Exception ex){
					if (_debug) Log.e("NotificationActivity.onContextItemSelected() CALL_CONTACT_CONTEXT_MENU ERROR: " + ex.toString());
					return false;
				}
			case TEXT_CONTACT_CONTEXT_MENU:
				try{
					intent = new Intent(Intent.ACTION_VIEW);
					intent.setType("vnd.android-dir/mms-sms");
			        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
			        		| Intent.FLAG_ACTIVITY_SINGLE_TOP
			        		| Intent.FLAG_ACTIVITY_CLEAR_TOP
			        		| Intent.FLAG_ACTIVITY_NO_HISTORY
			        		| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				    intent.putExtra("address", phoneNumber);
				    context.startActivity(intent);
					return true;
				}catch(Exception ex){
					if (_debug) Log.e("NotificationActivity.onContextItemSelected() TEXT_CONTACT_CONTEXT_MENU ERROR: " + ex.toString());
					return false;
				}
			case EDIT_CONTACT_CONTEXT_MENU:
				try{
					intent = new Intent(Intent.ACTION_EDIT);
					Uri editContactURI = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactID));
				    intent.setData(editContactURI);	
			        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
			        		| Intent.FLAG_ACTIVITY_SINGLE_TOP
			        		| Intent.FLAG_ACTIVITY_CLEAR_TOP
			        		| Intent.FLAG_ACTIVITY_NO_HISTORY
			        		| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				    context.startActivity(intent);
					return true;
				}catch(Exception ex){
					if (_debug) Log.e("NotificationActivity.onContextItemSelected() EDIT_CONTACT_CONTEXT_MENU ERROR: " + ex.toString());
					return false;
				}
			case EDIT_EVENT_CONTEXT_MENU:
				try{
				    long calendarEventID = notification.getCalendarEventID();
					try{
						intent = new Intent(Intent.ACTION_EDIT);
						//Android 2.2+
						intent.setData(Uri.parse("content://com.android.calendar/events/" + String.valueOf(calendarEventID)));
						//Android 2.1 and below.
						//intent.setData(Uri.parse("content://calendar/events/" + String.valueOf(calendarEventID)));	
						intent.putExtra(EVENT_BEGIN_TIME,notification.getCalendarEventStartTime());
						intent.putExtra(EVENT_END_TIME,notification.getCalendarEventEndTime());
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				        		| Intent.FLAG_ACTIVITY_SINGLE_TOP
				        		| Intent.FLAG_ACTIVITY_CLEAR_TOP
				        		| Intent.FLAG_ACTIVITY_NO_HISTORY
				        		| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				        context.startActivity(intent);
					}catch(Exception ex){
						if (_debug) Log.e("NotificationActivity.onContextItemSelected() EDIT_EVENT_CONTEXT_MENU ERROR: " + ex.toString());
						return false;
					}
					return true;
				}catch(Exception ex){
					if (_debug) Log.e("NotificationActivity.onContextItemSelected() EDIT_EVENT_CONTEXT_MENU ERROR: " + ex.toString());
					return false;
				}
			default:
				return super.onContextItemSelected(menuItem);
		  }
	}
	
	/**
	 * Customized activity finish.
	 * This closes this activity screen.
	 */
	public void finishActivity() {
		if (_debug) Log.v("NotificationActivity.finishActivity()");
		//Release the WakeLock
		releaseWakeLock();
	    // Finish the activity.
	    finish();
	}
  
	/**
	 * Display the delete dialog from the activity and return the result. 
	 */
	public void showDeleteDialog(){
		if (_debug) Log.v("NotificationActivity.showDeleteDialog()");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		int notificationType = getNotificationViewFlipper().getActiveMessage().getNotificationType();
		if(notificationType == NOTIFICATION_TYPE_SMS){
			if(preferences.getString(SMS_DELETE_KEY, "0").equals(SMS_DELETE_ACTION_NOTHING)){
				//Remove the notification from the ViewFlipper
				deleteMessage();
			}else{
				showDialog(DIALOG_DELETE_MESSAGE);
			}
		}else if(notificationType == NOTIFICATION_TYPE_MMS){
			if(preferences.getString(MMS_DELETE_KEY, "0").equals(MMS_DELETE_ACTION_NOTHING)){
				//Remove the notification from the ViewFlipper
				deleteMessage();
			}else{
				showDialog(DIALOG_DELETE_MESSAGE);
			}
		}
	}
	
	/**
	 * Handles the activity when the configuration changes (e.g. The phone switches from portrait view to landscape view).
	 */
	public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);   
        if (_debug) Log.v("NotificationActivity.onConfigurationChanged()");
        //Do Nothing.
	}

	/**
	 * This function intercepts all the touch events.
	 * In here we decide what to pass on to child items and what to handle ourselves.
	 * 
	 * @param motionEvent - The touch event that occurred.
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent motionEvent){
		if (_debug) Log.v("NotificationActivity.dispatchTouchEvent()");
		NotificationViewFlipper notificationViewFlipper = getNotificationViewFlipper();
	    switch (motionEvent.getAction()){
	        case MotionEvent.ACTION_DOWN:{
		        //Keep track of the starting down-event.
		        setDownMotionEvent(MotionEvent.obtain(motionEvent));
		        break;
	        }
	        case MotionEvent.ACTION_UP:{
	            //Consume if necessary and perform the fling / swipe action
	            //if it has been determined to be a fling / swipe
	        	MotionEvent downMotionEvent = getDownMotionEvent();
	        	float deltaX = motionEvent.getX() - downMotionEvent.getX();
		        if(Math.abs(deltaX) > new ViewConfiguration().getScaledTouchSlop()  * 2){
		        	if (deltaX < 0){
	                   notificationViewFlipper.showNext();
	                   return true;
					}else if (deltaX > 0){
	                   notificationViewFlipper.showPrevious();
	                   return true;
	               	}
		        }
	            break;
	        }
	    }
	    return super.dispatchTouchEvent(motionEvent);
	}
	
	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Called when the activity is created. Set up views and notifications.
	 * 
	 * @param bundle - Activity bundle.
	 */
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		_debug = Log.getDebug();
	    if (_debug) Log.v("NotificationActivity.onCreate()");
	    Context context = getApplicationContext();
	    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    setBundle(bundle);
	    setContext(context);
	    Bundle extrasBundle = getIntent().getExtras();
	    int notificationType = extrasBundle.getInt("notificationType");
	    if (_debug) Log.v("NotificationActivity.onCreate() Notification Type: " + notificationType);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.notificationwrapper);
	    setupViews(notificationType);
	    if(notificationType == NOTIFICATION_TYPE_TEST){
	    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_TEST");
	    	createTestNotifications();
	    }    
	    if(notificationType == NOTIFICATION_TYPE_PHONE){
	    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_PHONE");
	    	setupMissedCalls(extrasBundle);
	    }
	    if(notificationType == NOTIFICATION_TYPE_SMS){
		    if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_SMS");
		    Notification newSMSNotification = setupMessage(extrasBundle);
		    if(preferences.getBoolean(SMS_DISPLAY_UNREAD_KEY, true)){
		    	getAllUnreadSMSMessages(newSMSNotification.getMessageID(), newSMSNotification.getMessageBody());
		    }
	    }
	    if(notificationType == NOTIFICATION_TYPE_MMS){
	    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_MMS");
	    	//TODO - MMS
	    }
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_CALENDAR");
		    setupCalendarEventNotifications(extrasBundle);
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_EMAIL");
	    	//TODO - Email
	    }  
	    //Set Vibration or Ringtone to announce Activity.
	    runNotificationFeedback(notificationType);
	    //Acquire WakeLock.
	    acquireWakeLock(context);
	    long wakelockTimeout = Long.parseLong(preferences.getString(WAKELOCK_TIMEOUT_KEY, "30")) * 1000;
	    _wakeLockHandler.sleep(wakelockTimeout * 1000);
	    //Remove the KeyGuard.
	    disableKeyguardLock(context);
	    long keyguardTimeout = Long.parseLong(preferences.getString(KEYGUARD_TIMEOUT_KEY, "30")) * 1000;
	    _keyguardHandler.sleep(keyguardTimeout * 1000);
	}
	  
	/**
	 * Activity was started after it stopped or for the first time.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		_debug = Log.getDebug();
	    if (_debug) Log.v("NotificationActivity.onStart()");
	}
	  
	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onResume() {
	    super.onResume();
	    _debug = Log.getDebug();
	    if (_debug) Log.v("NotificationActivity.onResume()");
	    Context context = getContext();
	    acquireWakeLock(context);
	}
	  
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
	@Override
	protected void onPause() {
	    super.onPause();
	    if (_debug) Log.v("NotificationActivity.onPause()");
	    releaseWakeLock(); 
	}
	  
	/**
	 * Activity was stopped due to a new Activity being started or other reason.
	 */
	@Override
	protected void onStop() {
	    super.onStop();
	    if (_debug) Log.v("NotificationActivity.onStop()");
	    releaseWakeLock();
	}
	  
	/**
	 * Activity was stopped and closed out completely.
	 */
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (_debug) Log.v("NotificationActivity.onDestroy()");
	    releaseWakeLock();
	    reenableKeyguardLock();
	}

	/**
	 * Create new Dialog.
	 * 
	 * @param id - ID of the Dialog that we want to display.
	 * 
	 * @return Dialog - Popup Dialog created.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		if (_debug) Log.v("NotificationActivity.onCreateDialog()");
		Context context = getContext();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		int notificationType = getNotificationViewFlipper().getActiveMessage().getNotificationType();
		AlertDialog alertDialog = null;
		switch (id) {

	        /*
	         * Delete confirmation dialog.
	         */
			case DIALOG_DELETE_MESSAGE:
				if (_debug) Log.v("NotificationActivity.onCreateDialog() DIALOG_DELETE_MESSAGE");
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setTitle(getString(R.string.delete_message_dialog_title_text));
				//Action is determined by the users preferences. 
				if(notificationType == NOTIFICATION_TYPE_SMS){
					if(preferences.getString(SMS_DELETE_KEY, "0").equals(SMS_DELETE_ACTION_DELETE_MESSAGE)){
						builder.setMessage(getString(R.string.delete_message_dialog_text));
					}else if(preferences.getString(SMS_DELETE_KEY, "0").equals(SMS_DELETE_ACTION_DELETE_THREAD)){
						builder.setMessage(getString(R.string.delete_thread_dialog_text));
					}
				}else if(notificationType == NOTIFICATION_TYPE_MMS){
					if(preferences.getString(MMS_DELETE_KEY, "0").equals(MMS_DELETE_ACTION_DELETE_MESSAGE)){
						builder.setMessage(getString(R.string.delete_message_dialog_text));
					}else if(preferences.getString(MMS_DELETE_KEY, "0").equals(MMS_DELETE_ACTION_DELETE_THREAD)){
						builder.setMessage(getString(R.string.delete_thread_dialog_text));
					}
				}
				builder.setPositiveButton(R.string.delete_button_text, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							//customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							deleteMessage();
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							//customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			            	dialog.cancel();
						}
					});
				alertDialog = builder.create();
				
		}
		return alertDialog;
	}

    /**
     * This is called when the activity is running and it is triggered and run again for a different notification.
     * This is a copy of the onCreate() method but without the initialization calls.
     * 
     * @param intent - Activity intent.
     */
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    if (_debug) Log.v("NotificationActivity.onNewIntent()");
	    setIntent(intent);
	    Context context = getContext();
	    Bundle extrasBundle = getIntent().getExtras();
	    int notificationType = extrasBundle.getInt("notificationType");
	    if (_debug) Log.v("NotificationActivity.onNewIntent() Notification Type: " + notificationType);
	    if(notificationType == NOTIFICATION_TYPE_PHONE){
	    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_PHONE");
	    	setupMissedCalls(extrasBundle);
	    }
	    if(notificationType == NOTIFICATION_TYPE_SMS){
		    if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_SMS");
		    setupMessage(extrasBundle);
	    }
	    if(notificationType == NOTIFICATION_TYPE_MMS){
	    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_MMS");
	    	//TODO - MMS
	    }
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	if (_debug) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_CALENDAR");
		    setupCalendarEventNotifications(extrasBundle);
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	if (_debug) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_EMAIL");
	    	//TODO - Email
	    }
	    //Set Vibration and/or Ringtone to announce Activity.
	    runNotificationFeedback(notificationType);
	    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    //Acquire WakeLock.
	    acquireWakeLock(context);
	    long wakelockTimeout = Long.parseLong(preferences.getString(WAKELOCK_TIMEOUT_KEY, "30")) * 1000;
	    _wakeLockHandler.sleep(wakelockTimeout * 1000);
	    //Remove the KeyGuard.
	    disableKeyguardLock(context);
	    long keyguardTimeout = Long.parseLong(preferences.getString(KEYGUARD_TIMEOUT_KEY, "30")) * 1000;
	    _keyguardHandler.sleep(keyguardTimeout * 1000);
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Set up the ViewFlipper elements.
	 * 
	 * @param notificationType - Notification type.
	 */ 
	private void setupViews(int notificationType) {
		if (_debug) Log.v("NotificationActivity.setupViews()");
		final NotificationViewFlipper notificationViewFlipper;
		notificationViewFlipper = (NotificationViewFlipper) findViewById(R.id.notification_view_flipper);
		setNotificationViewFlipper(notificationViewFlipper);
	}
	
	/**
	 * Setup the missed calls notifications.
	 * 
	 * @param bundle - Activity bundle.
	 */
	private boolean setupMissedCalls(Bundle bundle){
		if (_debug) Log.v("NotificationActivity.setupMissedCalls()"); 
		Context context = getContext();
		ArrayList<String> missedCallsArray = bundle.getStringArrayList("missedCallsArrayList");
		for(int i=0; i< missedCallsArray.size(); i++){
			String[] missedCallInfo = missedCallsArray.get(i).split("\\|");
			String phoneNumber = missedCallInfo[0];
			long timeStamp = Long.parseLong(missedCallInfo[1]);
			Notification missedCallNotification = new Notification(context, phoneNumber, timeStamp, NOTIFICATION_TYPE_PHONE);
			getNotificationViewFlipper().addNotification(missedCallNotification);
		}
	    return true;
	}
	
	/**
	 * Setup the incoming SMS/MMS message notification.
	 *
	 * @param bundle - Activity bundle.
	 * 
	 * @return Notification - Returns the new incoming SMS message in the form of a Notification object.
	 */
	private Notification setupMessage(Bundle bundle) {
		if (_debug) Log.v("NotificationActivity.setupMessages()"); 
		Context context = getContext();
		NotificationViewFlipper notificationViewFlipper = getNotificationViewFlipper();
	    // Create message from bundle.
	    Notification smsMessage = new Notification(context, bundle, NOTIFICATION_TYPE_SMS);
	    notificationViewFlipper.addNotification(smsMessage);
	    return smsMessage;
	}
	
	/**
	 * Get all unread Messages and load them.
	 * 
	 * @param messageIDFilter - Long value of the currently incoming SMS message.
	 * @param messagebodyFilter - String value of the currently incoming SMS message.
	 */
	private void getAllUnreadSMSMessages(long messageIDFilter, String messageBodyFilter){
		if (_debug) Log.v("NotificationActivity.getAllUnreadSMSMessages() messageIDFilter: " + messageIDFilter + " messageBodyFilter: " + messageBodyFilter ); 
		Context context = getContext();
		NotificationViewFlipper notificationViewFlipper = getNotificationViewFlipper();
		final String[] projection = new String[] { "_ID", "THREAD_ID", "ADDRESS", "PERSON", "DATE", "BODY"};
		final String selection = "READ = 0";
		final String[] selectionArgs = null;
		final String sortOrder = null;
		Cursor cursor = null;
        try{
		    cursor = context.getContentResolver().query(
		    		Uri.parse("content://sms/inbox"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
		    while (cursor.moveToNext()) { 
		    	long messageID = cursor.getLong(cursor.getColumnIndex("_ID"));
		    	long threadID = cursor.getLong(cursor.getColumnIndex("THREAD_ID"));
		    	String messageBody = cursor.getString(cursor.getColumnIndex("BODY"));
		    	String phoneNumber = cursor.getString(cursor.getColumnIndex("ADDRESS"));
		    	long timestamp = cursor.getLong(cursor.getColumnIndex("DATE"));
		    	long contactID = cursor.getLong(cursor.getColumnIndex("PERSON"));
		    	//Don't load the message that corresponds to the messageIDFilter or messageBodyFilter.
		    	//If we load this message we will have duplicate Notifications, which is bad.
		    	if(messageID != messageIDFilter && !messageBody.trim().equals(messageBodyFilter)){
			    	Notification smsMessage = new Notification(context, messageID, threadID, messageBody, phoneNumber, timestamp, contactID, NOTIFICATION_TYPE_SMS);		
			    	notificationViewFlipper.addNotification(smsMessage);
		    	}
		    }
		}catch(Exception ex){
			if (_debug) Log.e("NotificationActivity.getAllUnreadSMSMessages() ERROR: " + ex.toString());
		} finally {
    		cursor.close();
    	}
	}

	//TODO - ADD IN FUNCTION: getAllUnreadMMSMessages
	
	/**
	 * Setup the Calendar Event notifications.
	 * 
	 * @param bundle - Activity bundle.
	 */
	private void setupCalendarEventNotifications(Bundle bundle){
		if (_debug) Log.v("NotificationActivity.setupCalendarEventNotifications()");  
		Context context = getContext();
		String calenderEventInfo[] = (String[])bundle.getStringArray("calenderEventInfo");
		String title = calenderEventInfo[0];
		String messageBody = calenderEventInfo[1];
		long eventStartTime = Long.parseLong(calenderEventInfo[2]);
		long eventEndTime = Long.parseLong(calenderEventInfo[3]);
		boolean eventAllDay = Boolean.parseBoolean(calenderEventInfo[4]);
		long calendarID = Long.parseLong(calenderEventInfo[5]);
		long eventID = Long.parseLong(calenderEventInfo[6]);
		Notification calendarEventNotification = new Notification(context, title, messageBody, eventStartTime, eventEndTime, eventAllDay, calendarID, eventID, NOTIFICATION_TYPE_CALENDAR);
		getNotificationViewFlipper().addNotification(calendarEventNotification);		
	}
	
	/**
	 * Delete the current message from the users phone.
	 */
	private void deleteMessage(){
		if (_debug) Log.v("NotificationActivity.deleteMessage()");
		NotificationViewFlipper notificationViewFlipper = getNotificationViewFlipper();
		notificationViewFlipper.deleteMessage();
	}
	
	/**
	 * Launches the preferences screen as new intent.
	 */
	private void launchPreferenceScreen(){
		if (_debug) Log.v("NotificationActivity.launchPreferenceScreen()");
		Context context = getApplicationContext();
		Intent intent = new Intent(context, DroidNotifyPreferenceActivity.class);
		startActivity(intent);
	}
	
	/**
	 * Function that acquires the WakeLock for this Activity.
	 * The type flags for the WakeLock will be determined by the user preferences. 
	 * 
	 * @param context - The current context of this Activity.
	 */
	private void acquireWakeLock(Context context){
		if (_debug) Log.v("NotificationActivity.acquireWakeLock()");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		PowerManager pm = null;
		WakeLock wakeLock = getWakeLock();
		try{
			pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
			if(wakeLock == null){
				//Set the wakeLock properties based on the users preferences.
				if(preferences.getBoolean(SCREEN_ENABLED_KEY, true)){
					if(preferences.getBoolean(SCREEN_DIM_ENABLED_KEY, true)){
						if (_debug) Log.v("NotificationActivity.acquireWakeLock() Screen Wake Enabled Dim");
						wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, DROID_NOTIFY_WAKELOCK);
					}else{
						if (_debug) Log.v("NotificationActivity.acquireWakeLock() Screen Wake Enabled Full");
						wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, DROID_NOTIFY_WAKELOCK);
					}
				}else{
					if (_debug) Log.v("NotificationActivity.acquireWakeLock() Screen Wake Disabled");
					wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, DROID_NOTIFY_WAKELOCK);
				}
			}
			if(wakeLock != null){
				if (_debug) Log.v("NotificationActivity.acquireWakeLock() Aquired wake lock");
				wakeLock.setReferenceCounted(false);
				wakeLock.acquire();
				setWakeLock(wakeLock);
			}
		}catch(Exception ex){
			if (_debug) Log.e("NotificationActivity.acquireWakeLock() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Function that releases the WakeLock.
	 */
	private void releaseWakeLock(){
		if (_debug) Log.v("NotificationActivity.releaseWakeLock()");
		WakeLock wakeLock = getWakeLock();
		try{
			if(wakeLock != null){
				wakeLock.release();
				wakeLock = null;
				setWakeLock(wakeLock);
			}
		}catch(Exception ex){
			if (_debug) Log.e("NotificationActivity.releaseWakeLock() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Function that disables the Keyguard for this Activity.
	 * The removal of the Keyguard will be determined by the user preferences. 
	 * 
	 * @param context - The current context of this Activity.
	 */
	private void disableKeyguardLock(Context context){
		if (_debug) Log.v("NotificationActivity.disableKeyguardLock()");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		KeyguardManager km = null;
		KeyguardLock keyguardLock = getKeyguardLock();
		try{
			km = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
			if(keyguardLock == null){
				keyguardLock = km.newKeyguardLock(DROID_NOTIFY_KEYGUARD); 
			}
			//Set the keyguard properties based on the users preferences.
			if(preferences.getBoolean(KEYGUARD_ENABLED_KEY, true)){
				if (_debug) Log.v("NotificationActivity.disableKeyguardLock() Disable Keyguard Enabled");
				keyguardLock.disableKeyguard();
				setKeyguardLock(keyguardLock);
			}else{
				if (_debug) Log.v("NotificationActivity.disableKeyguardLock() Disable Keyguard Disabled");
			}
		}catch(Exception ex){
			if (_debug) Log.e("NotificationActivity.disableKeyguardLock() ERROR: " + ex.toString());
		}
	}

	/**
	 * Re-Enables the Keyguard for this Activity.
	 */
	private void reenableKeyguardLock(){
		if (_debug) Log.v("NotificationActivity.reenableKeyguardLock()");
		KeyguardLock keyguardLock = getKeyguardLock();
		try{
			if(keyguardLock != null){
				keyguardLock.reenableKeyguard();
				keyguardLock = null;
				setKeyguardLock(keyguardLock);
			}
		}catch(Exception ex){
			if (_debug) Log.e("NotificationActivity.reenableKeyguardLock() ERROR: " + ex.toString());
		}
	}

	/**
	 * Starts the playback of the ringtone.
	 */
	private void playRingtone(int notificationType){
		if (_debug) Log.v("NotificationActivity.playRingtone()");
		Context context = getContext();
		Ringtone ringtone = getRingtone();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		long rintoneStopValue = Long.parseLong(preferences.getString(RINGTONE_LENGTH_KEY, "3")) * 1000;
		if(notificationType == NOTIFICATION_TYPE_TEST){
			try{
				ringtone = RingtoneManager.getRingtone(context, Uri.parse(preferences.getString(SMS_RINGTONE_KEY, "DEFAULT_SOUND")));
				if(ringtone != null) ringtone.play();
 	    	}catch(Exception ex){
 	    		if (_debug) Log.e("NotificationActivity.playRingtone() NOTIFICATION_TYPE_TEST ERROR: " + ex.toString());
	    	}
		}
	    if(notificationType == NOTIFICATION_TYPE_PHONE){
	 		if(preferences.getBoolean(MISSED_CALL_RINGTONE_ENABLED_KEY, false)){
	 	    	try{
		 			ringtone = RingtoneManager.getRingtone(context, Uri.parse(preferences.getString(MISSED_CALL_RINGTONE_KEY, "DEFAULT_SOUND")));
		 			if(ringtone != null) ringtone.play();
	 	    	}catch(Exception ex){
	 	    		if (_debug) Log.e("NotificationActivity.playRingtone() NOTIFICATION_TYPE_PHONE ERROR: " + ex.toString());
		    	}
	 		}
	    }
	    if(notificationType == NOTIFICATION_TYPE_SMS){
	 	    if(preferences.getBoolean(SMS_RINGTONE_ENABLED_KEY, false)){
	 	    	try{
		 	    	ringtone = RingtoneManager.getRingtone(context, Uri.parse(preferences.getString(SMS_RINGTONE_KEY, "DEFAULT_SOUND")));
		 	    	if(ringtone != null) ringtone.play();
	 	    	}catch(Exception ex){
	 	    		if (_debug) Log.e("NotificationActivity.playRingtone() NOTIFICATION_TYPE_SMS ERROR: " + ex.toString());
		    	}
	 	    }
	    }
	    if(notificationType == NOTIFICATION_TYPE_MMS){
	 	    if(preferences.getBoolean(MMS_RINGTONE_ENABLED_KEY, false)){
	 	    	try{
		 	    	ringtone = RingtoneManager.getRingtone(context, Uri.parse(preferences.getString(MMS_RINGTONE_KEY, "DEFAULT_SOUND")));
		 	    	if(ringtone != null) ringtone.play();
	 	    	}catch(Exception ex){
	 	    		if (_debug) Log.e("NotificationActivity.playRingtone() NOTIFICATION_TYPE_MMS ERROR: " + ex.toString());
		    	}
	 	    }
	    }
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	 	    if(preferences.getBoolean(CALENDAR_RINGTONE_ENABLED_KEY, false)){
	 	    	try{
	 	    		ringtone = RingtoneManager.getRingtone(context, Uri.parse(preferences.getString(CALENDAR_RINGTONE_KEY, "DEFAULT_SOUND")));
	 	    		if(ringtone != null) ringtone.play();
	 	    	}catch(Exception ex){
	 	    		if (_debug) Log.e("NotificationActivity.playRingtone() NOTIFICATION_TYPE_CALENDAR ERROR: " + ex.toString());
		    	}
	 	    }
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	//TODO - Email
	    }
		setRingtone(ringtone);
		_ringtoneHandler.sleep(rintoneStopValue);
	}
	
	/**
	 * Stops the playback of the ringtone.
	 */
	private void stopRingtone(){
		if (_debug) Log.v("NotificationActivity.stopRingtone()");
		Ringtone ringtone = getRingtone();
		try{
			if(ringtone!= null){
				ringtone.stop();
				ringtone = null;
				setRingtone(ringtone);
			}
		}catch(Exception ex){
	    		if (_debug) Log.e("NotificationActivity.stopRingtone() ERROR: " + ex.toString());
    	}
	}
	
	/**
	 * Function to set ring tone or vibration based on users preferences for this notification.
	 * 
	 * @param notificationType - The type of the current notification.
	 */
	private void runNotificationFeedback(int notificationType){
		if (_debug) Log.v("NotificationActivity.runNotificationFeedback()");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Vibrator vibrator = null;
		try{
			vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
			//Set vibration based on user preferences.
			if(preferences.getBoolean(ALL_VIBRATE_ENABLED_KEY, true)){
				if(notificationType == NOTIFICATION_TYPE_TEST){
					if(vibrator != null) vibrator.vibrate(1 * 1000);
				}
			    if(notificationType == NOTIFICATION_TYPE_PHONE){
			 	    if(preferences.getBoolean(MISSED_CALL_VIBRATE_ENABLED_KEY, true)){
			 	    	if(vibrator != null) vibrator.vibrate(1 * 1000);
			 	    }
			    }
			    if(notificationType == NOTIFICATION_TYPE_SMS){
			 	    if(preferences.getBoolean(SMS_VIBRATE_ENABLED_KEY, true)){
			 	    	if(vibrator != null) vibrator.vibrate(1 * 1000);
			 	    }
			    }
			    if(notificationType == NOTIFICATION_TYPE_MMS){
			 	    if(preferences.getBoolean(MMS_VIBRATE_ENABLED_KEY, true)){
			 	    	if(vibrator != null) vibrator.vibrate(1 * 1000);
			 	    }
			    }
			    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
			 	    if(preferences.getBoolean(CALENDAR_VIBRATE_ENABLED_KEY, true)){
			 	    	if(vibrator != null) vibrator.vibrate(1 * 1000);
			 	    }
			    }
			    if(notificationType == NOTIFICATION_TYPE_EMAIL){
			    	//TODO - Email
			    }
			}
			//Set ringtone based on user preferences.
			if(preferences.getBoolean(ALL_RINGTONE_ENABLED_KEY, true)){
		    	playRingtone(notificationType);
		    }
		}catch(Exception ex){
			if (_debug) Log.e("NotificationActivity.runNotificationFeedback() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Function that performs custom haptic feedback.
	 * This function performs haptic feedback based on the users preferences.
	 * 
	 * @param hapticFeedbackConstant - What type of action the feedback is responding to.
	 */
	private void customPerformHapticFeedback(int hapticFeedbackConstant){
		if (_debug) Log.v("NotificationActivity.customPerformHapticFeedback()");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		Vibrator vibrator = null;
		try{
			vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
			//Perform the haptic feedback based on the users preferences.
			if(preferences.getBoolean(HAPTIC_FEEDBACK_ENABLED_KEY, true)){
				if(hapticFeedbackConstant == HapticFeedbackConstants.VIRTUAL_KEY){
					if(vibrator != null) vibrator.vibrate(50);
				}
			}
			if(preferences.getBoolean(HAPTIC_FEEDBACK_ENABLED_KEY, true)){
				if(hapticFeedbackConstant == HapticFeedbackConstants.LONG_PRESS){
					if(vibrator != null) vibrator.vibrate(100);
				}
			}
		}catch(Exception ex){
			if (_debug) Log.e("NotificationActivity.customPerformHapticFeedback() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Function to create a test notification of each type.
	 */
	private void createTestNotifications(){
		if (_debug) Log.v("NotificationActivity.createTextNotifications()");
		Context context = getContext();
		NotificationViewFlipper notificationViewFlipper = getNotificationViewFlipper();
		//Add SMS Message Notification.
		Notification smsNotification = new Notification(context, "5555555555", "Droid Notify SMS Message Test", System.currentTimeMillis(), NOTIFICATION_TYPE_SMS);
		notificationViewFlipper.addNotification(smsNotification);
		//Add Missed Call Notification.
		Notification missedCallNotification = new Notification(context, "5555555555", System.currentTimeMillis(), NOTIFICATION_TYPE_PHONE);
		notificationViewFlipper.addNotification(missedCallNotification);
		//Add Calendar Event Notification.
		Notification calendarEventNotification = new Notification(context, "Droid Notify Calendar Event Test", "", System.currentTimeMillis(), System.currentTimeMillis() + (10 * 60 * 1000), false, 0, 0, NOTIFICATION_TYPE_CALENDAR);
		notificationViewFlipper.addNotification(calendarEventNotification);	
	}
	
	/**
	 * This class is a Handler that executes in it's own thread and is used to delay the releasing of the WakeLock.
	 * 
	 * @author Camille Sévigny
	 */
	class WakeLockHandler extends Handler {

		/**
		 * Handles the delayed function call when the sleep period is over.
		 * 
		 * @param msg - Message to be handled.
		 */
		@Override
	    public void handleMessage(Message msg) {
			if (_debug) Log.v("WakeLockHandler.handleMessage()");
	    	NotificationActivity.this.releaseWakeLock();
	    }

		/**
		 * Put the thread to sleep for a period of time.
		 * 
		 * @param delayMillis - Delay time in milliseconds.
		 */
	    public void sleep(long delayMillis) {
	    	if (_debug) Log.v("WakeLockHandler.sleep()");
	    	this.removeMessages(0);
	    	sendMessageDelayed(obtainMessage(0), delayMillis);
	    }

	};
		
	/**
	 * This class is a Handler that executes in it's own thread and is used to delay the releasing of the Keyguard.
	 * 
	 * @author Camille Sévigny
	 */
	class KeyguardHandler extends Handler {

		/**
		 * Handles the delayed function call when the sleep period is over.
		 * 
		 * @param msg - Message to be handled.
		 */
		@Override
		public void handleMessage(Message msg) {
			if (_debug) Log.v("KeyguardHandler.handleMessage()");
			NotificationActivity.this.reenableKeyguardLock();
		}
		    
		/**
		 * Put the thread to sleep for a period of time.
		 * 
		 * @param delayMillis - Delay time in milliseconds.
		 */
		public void sleep(long delayMillis) {
			if (_debug) Log.v("KeyguardHandler.sleep()");
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}

	};
	
	/**
	 * This class is a Handler that executes in it's own thread and is used to delay the stopping of the ringtone feedback.
	 * 
	 * @author Camille Sévigny
	 */
	class RingtoneHandler extends Handler {

		/**
		 * Handles the delayed function call when the sleep period is over.
		 * 
		 * @param msg - Message to be handled.
		 */
		@Override
	    public void handleMessage(Message msg) {
			if (_debug) Log.v("RingtoneHandler.handleMessage()");
	    	NotificationActivity.this.stopRingtone();
	    }

		/**
		 * Put the thread to sleep for a period of time.
		 * 
		 * @param delayMillis - Delay time in milliseconds.
		 */
	    public void sleep(long delayMillis) {
	    	if (_debug) Log.v("RingtoneHandler.sleep()");
	    	this.removeMessages(0);
	    	sendMessageDelayed(obtainMessage(0), delayMillis);
	    }

	};
	
}
