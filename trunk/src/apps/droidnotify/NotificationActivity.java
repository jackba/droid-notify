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
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This is the main activity that runs the notifications.
 * 
 * @author Camille Sevigny
 *
 */
public class NotificationActivity extends Activity {

	//================================================================================
    // Constants
    //================================================================================
	
	//private final double WIDTH = 0.9;
	//private final int MAX_WIDTH = 640;
	
	private final String DROID_NOTIFY_WAKELOCK = "DROID_NOTIFY_WAKELOCK";
	private final String DROID_NOTIFY_KEYGUARD = "DROID_NOTIFY_KEYGUARD";
	
	private final int MENU_ITEM_SETTINGS = R.id.app_settings;
	
	private final int NOTIFICATION_TYPE_TEST = -1;
	private final int NOTIFICATION_TYPE_PHONE = 0;
	private final int NOTIFICATION_TYPE_SMS = 1;
	private final int NOTIFICATION_TYPE_MMS = 2;
	private final int NOTIFICATION_TYPE_CALENDAR = 3;
	private final int NOTIFICATION_TYPE_EMAIL = 4;
	
	private final int DIALOG_DELETE_MESSAGE = 0;

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
	
	//private final String SMS_DISMISS_ACTION_MARK_READ = "0";
	private final String SMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	private final String SMS_DELETE_ACTION_DELETE_THREAD = "1";
	private final String SMS_DELETE_ACTION_NOTHING = "2";
	//private final String MMS_DISMISS_ACTION_MARK_READ = "0";
	private final String MMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	private final String MMS_DELETE_ACTION_DELETE_THREAD = "1";
	private final String MMS_DELETE_ACTION_NOTHING = "2";
	
	//private final int CONTACT_PHOTO_IMAGE_VIEW = R.id.contact_image_view;
	private final int CONTACT_PHOTO_LINEAR_LAYOUT = R.id.contact_linear_layout;
	
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

	private Bundle _bundle = null;
	private Context _context = null;
	private WakeLock _wakeLock;
	private KeyguardLock _keyguardLock; 
	private NotificationViewFlipper _notificationViewFlipper = null;
	private LinearLayout _mainActivityLayout = null;
	private Button _previousButton = null;
	private Button _nextButton = null;
	private TextView _notificationCountTextView = null;
	private InputMethodManager _inputMethodManager = null;
	//private View _softKeyboardTriggerView = null;

	//================================================================================
	// Constructors
	//================================================================================
	
	//================================================================================
	// Accessors
	//================================================================================
	  
	/**
	 * Set the notificationViewFlipper property.
	 * 
	 * @param bundle
	 */
	public void setBundle(Bundle bundle) {
		if (Log.getDebug()) Log.v("NotificationActivity.setBundle()");
	    _bundle = bundle;
	}
	
	/**
	 * Get the notificationViewFlipper property.
	 * 
	 * @return _bundle
	 */
	public Bundle getBundle() {
		if (Log.getDebug()) Log.v("NotificationActivity.getBundle()");
	    return _bundle;
	} 

	/**
	 * Set the keyguardLock property.
	 * 
	 * @param keyguardLock
	 */
	public void setKeyguardLock(KeyguardLock keyguardLock) {
		if (Log.getDebug()) Log.v("NotificationActivity.setKeyguardLock()");
		_keyguardLock = keyguardLock;
	}
	
	/**
	 * Get the keyguardLock property.
	 * 
	 * @return keyguardLock
	 */
	public KeyguardLock getKeyguardLock() {
		if (Log.getDebug()) Log.v("NotificationActivity.getKeyguardLock()");
	    return _keyguardLock;
	}

	/**
	 * Set the wakeLock property.
	 * 
	 * @param wakeLock
	 */
	public void setWakeLock(WakeLock wakeLock) {
		if (Log.getDebug()) Log.v("NotificationActivity.setWakeLock()");
		_wakeLock = wakeLock;
	}
	
	/**
	 * Get the wakeLock property.
	 * 
	 * @return wakeLock
	 */
	public WakeLock getWakeLock() {
		if (Log.getDebug()) Log.v("NotificationActivity.getWakeLock()");
	    return _wakeLock;
	}

	/**
	 * Set the context property.
	 * 
	 * @param context
	 */
	public void setContext(Context context) {
		if (Log.getDebug()) Log.v("NotificationActivity.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 * 
	 * @return context
	 */
	public Context getContext() {
		if (Log.getDebug()) Log.v("NotificationActivity.getContext()");
	    return _context;
	}
	
	/**
	 * Set the notificationViewFlipper property.
	 * 
	 * @param notificationViewFlipper
	 */
	public void setNotificationViewFlipper(NotificationViewFlipper notificationViewFlipper) {
		if (Log.getDebug()) Log.v("NotificationActivity.setNotificationViewFlipper()");
	    _notificationViewFlipper = notificationViewFlipper;
	}
	
	/**
	 * Get the notificationViewFlipper property.
	 * 
	 * @return _notificationViewFlipper
	 */
	public NotificationViewFlipper getNotificationViewFlipper() {
		if (Log.getDebug()) Log.v("NotificationActivity.getNotificationViewFlipper()");
	    return _notificationViewFlipper;
	}
	  
	/**
	 * Set the previousButton property.
	 * 
	 * @param mainActivityLayout
	 */
	public void setMainActivityLayout(LinearLayout mainActivityLayout) {
		if (Log.getDebug()) Log.v("NotificationActivity.setMainActivityLayout()");
		_mainActivityLayout = mainActivityLayout;
	}
	
	/**
	 * Get the previousButton property.
	 * 
	 * @return _mainActivityLayout
	 */
	public LinearLayout getMainActivityLayout() {
		if (Log.getDebug()) Log.v("NotificationActivity.getMainActivityLayout()");
	    return _mainActivityLayout;
	}
	  
	/**
	 * Set the previousButton property.
	 * 
	 * @param previousButton
	 */
	public void setPreviousButton(Button previousButton) {
		if (Log.getDebug()) Log.v("NotificationActivity.setPreviousButton()");
		_previousButton = previousButton;
	}
	
	/**
	 * Get the previousButton property.
	 * 
	 * @return _previousButton
	 */
	public Button getPreviousButton() {
		if (Log.getDebug()) Log.v("NotificationActivity.getPreviousButton()");
	    return _previousButton;
	}
	
	/**
	 * Set the nextButton property.
	 * 
	 * @param nextButton
	 */
	public void setNextButton(Button nextButton) {
		if (Log.getDebug()) Log.v("NotificationActivity.setNextButton()");
		_nextButton = nextButton;
	}
	
	/**
	 * Get the previousButton property.
	 * 
	 * @return _nextButton
	 */
	public Button getNextButton() {
		if (Log.getDebug()) Log.v("NotificationActivity.getNextButton()");
	    return _nextButton;
	}

	/**
	 * Set the notificationCountTextView property.
	 * 
	 * @param notificationCountTextView
	 */
	public void setNotificationCountTextView(TextView notificationCountTextView) {
		if (Log.getDebug()) Log.v("NotificationActivity.setNotificationCountTextView()");
		_notificationCountTextView = notificationCountTextView;
	}
	
	/**
	 * Get the notificationCountTextView property.
	 * 
	 * @return _notificationCountTextView
	 */
	public TextView getNotificationCountTextView() {
		if (Log.getDebug()) Log.v("NotificationActivity.getNotificationCountTextView()");
	    return _notificationCountTextView;
	}

	/**
	 * Set the inputMethodManager property.
	 * 
	 * @param inputMethodManager
	 */
	public void setInputMethodManager(InputMethodManager inputMethodManager) {
		if (Log.getDebug()) Log.v("NotificationActivity.setInputMethodManager()");
		_inputMethodManager = inputMethodManager;
	}
	
	/**
	 * Get the inputMethodManager property.
	 * 
	 * @return _inputMethodManager
	 */
	public InputMethodManager getInputMethodManager() {
		if (Log.getDebug()) Log.v("NotificationActivity.getInputMethodManager()");
	    return _inputMethodManager;
	}	

//	/**
//	 * Set the softKeyboardTriggerView property.
//	 * 
//	 * @param softKeyboardTriggerView
//	 */
//	public void setSoftKeyboardTriggerView(View softKeyboardTriggerView) {
//		if (Log.getDebug()) Log.v("NotificationActivity.setSoftKeyboardTriggerView()");
//		_softKeyboardTriggerView = softKeyboardTriggerView;
//	}

//	/**
//	 * Get the softKeyboardTriggerView property.
//	 *
//	 * @return _softKeyboardTriggerView
//	 */
//	public View getSoftKeyboardTriggerView() {
//		if (Log.getDebug()) Log.v("NotificationActivity.getGoftKeyboardTriggerView()");
//	    return _softKeyboardTriggerView;
//	}
	
	//================================================================================
	// Public Methods
	//================================================================================
  
	/**
	 * Creates the menu item for this activity.
	 * 
	 * @param menu
	 * 
	 * @return boolean
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
	 * @param item
	 * 
	 * @return boolean
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case MENU_ITEM_SETTINGS:
	        launchPreferenceScreen();
	        return true;
	    }
	    return false;
	}
	
	/**
	 * Create Context Menu (Long-press menu)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenuInfo contextMenuInfo) {
	    super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
	    if (Log.getDebug()) Log.v("NotificationActivity.onCreateContextMenu()");
	    switch (view.getId()) {

	        /*
	         * Contact photo ConextMenu.
	         */
			case CONTACT_PHOTO_LINEAR_LAYOUT:
				MenuInflater menuInflater = getMenuInflater();
				NotificationViewFlipper notificationViewFlipper = getNotificationViewFlipper();
				int currentNotification = notificationViewFlipper.getCurrentNotification();
				Notification notification = notificationViewFlipper.getNotification(currentNotification);
				String phoneNumber = notification.getPhoneNumber();
				int notificationType = notification.getNotificationType();
				if (Log.getDebug()) Log.v("NotificationActivity.onCreateContextMenu() Does contact exist?" + notification.getContactExists());
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
			    	//TODO - Email Message
			    }
	    }
		    
	}

	/**
	 * Context Menu Item Selected  (Long-press menu item selected)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		if (Log.getDebug()) Log.v("NotificationActivity.onContextItemSelected()");
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
					//This works but is deprecated. Trying a different way.
				    //intent.setData(ContentUris.withAppendedId(People.CONTENT_URI, contactID));
					//This is the Android API 5+ method to do this.
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
					if (Log.getDebug()) Log.e("NotificationActivity.onContextItemSelected() VIEW_CONTACT_CONTEXT_MENU ERROR: " + ex.toString());
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
					if (Log.getDebug()) Log.e("NotificationActivity.onContextItemSelected() ADD_CONTACT_CONTEXT_MENU ERROR: " + ex.toString());
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
					if (Log.getDebug()) Log.e("NotificationActivity.onContextItemSelected() CALL_CONTACT_CONTEXT_MENU ERROR: " + ex.toString());
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
					if (Log.getDebug()) Log.e("NotificationActivity.onContextItemSelected() TEXT_CONTACT_CONTEXT_MENU ERROR: " + ex.toString());
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
					if (Log.getDebug()) Log.e("NotificationActivity.onContextItemSelected() EDIT_CONTACT_CONTEXT_MENU ERROR: " + ex.toString());
					return false;
				}
			case EDIT_EVENT_CONTEXT_MENU:
				try{
				    long calendarEventID = notification.getCalendarEventID();
					try{
						//Android 2.2+
						intent = new Intent(Intent.ACTION_EDIT);
						intent.setData(Uri.parse("content://com.android.calendar/events/" + String.valueOf(calendarEventID)));	
						intent.putExtra(EVENT_BEGIN_TIME,notification.getCalendarEventStartTime());
						intent.putExtra(EVENT_END_TIME,notification.getCalendarEventEndTime());
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				        		| Intent.FLAG_ACTIVITY_SINGLE_TOP
				        		| Intent.FLAG_ACTIVITY_CLEAR_TOP
				        		| Intent.FLAG_ACTIVITY_NO_HISTORY
				        		| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				        context.startActivity(intent);
					}catch(Exception ex){
						//Android 2.1 and below.
						intent = new Intent(Intent.ACTION_EDIT);	
						intent.setData(Uri.parse("content://calendar/events/" + String.valueOf(calendarEventID)));	
						intent.putExtra(EVENT_BEGIN_TIME,notification.getCalendarEventStartTime());
						intent.putExtra(EVENT_END_TIME,notification.getCalendarEventEndTime());
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				        		| Intent.FLAG_ACTIVITY_SINGLE_TOP
				        		| Intent.FLAG_ACTIVITY_CLEAR_TOP
				        		| Intent.FLAG_ACTIVITY_NO_HISTORY
				        		| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				        context.startActivity(intent);	
					}
					return true;
				}catch(Exception ex){
					if (Log.getDebug()) Log.e("NotificationActivity.onContextItemSelected() EDIT_EVENT_CONTEXT_MENU ERROR: " + ex.toString());
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
		if (Log.getDebug()) Log.v("NotificationActivity.finishActivity()");
		//Release the WakeLock
		releaseWakeLock();
	    // Finish the activity.
	    finish();
	}
	  
	/**
	 * Update the navigation buttons and text when items are added or removed.
	 */
    public void updateNavigationButtons(){
    	if (Log.getDebug()) Log.v("NotificationActivity.updateNavigationButtons()");
		updateNavigationButtons(getPreviousButton(), getNotificationCountTextView(), getNextButton(), getNotificationViewFlipper());		
    }
  
	/**
	 * Display the delete dialog from the activity and return the result. 
	 */
	public void showDeleteDialog(){
		if (Log.getDebug()) Log.v("NotificationActivity.showDeleteDialog()");
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
    
	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Called when the activity is created. Set up views and notifications.
	 * 
	 * @param bundle
	 */
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
	    if (Log.getDebug()) Log.v("NotificationActivity.onCreate()");
	    Context context = getApplicationContext();
	    //Initialize properties.
	    setBundle(bundle);
	    setContext(context);
	    Bundle extrasBundle = getIntent().getExtras();
	    int notificationType = extrasBundle.getInt("notificationType");
	    if (Log.getDebug()) Log.v("NotificationActivity.onCreate() Notification Type: " + notificationType);

//	    //Remove the phone's KeyGuard & Wakelock.
//	    //This does not work in Android Release 2.2 even though it should.
//	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//	    		| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//	    		| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//	    		| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON); 
	    
	    requestWindowFeature(Window.FEATURE_NO_TITLE);	    
	    setContentView(R.layout.notificationwrapper);
	    setupViews(notificationType);
	    if(notificationType == NOTIFICATION_TYPE_TEST){
	    	createTestNotifications();
	    }    
	    if(notificationType == NOTIFICATION_TYPE_PHONE){
	    	if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_PHONE");
	    	setupMissedCalls(extrasBundle);
	    }
	    if(notificationType == NOTIFICATION_TYPE_SMS){
		    if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_SMS");
		    setupMessages(extrasBundle);
	    }
	    if(notificationType == NOTIFICATION_TYPE_MMS){
	    	if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_MMS");
	    	//TODO - MMS Message
	    }
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_CALENDAR");
		    setupCalendarEventNotifications(extrasBundle);
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_EMAIL");
	    	//TODO - Email Message
	    }  
	    //Set Vibration or Ringtone to announce Activity.
	    runNotificationFeedback(notificationType);
	    //Acquire WakeLock.
	    acquireWakeLock(context);
	    //Remove the KeyGuard.
	    disableKeyguardLock(context);
	}

	  
	/**
	 * Activity was started after it stopped or for the first time.
	 */
	@Override
	protected void onStart() {
		super.onStart();
	    if (Log.getDebug()) Log.v("NotificationActivity.onStart()");
	}
	  
	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onResume() {
	    super.onResume();
	    if (Log.getDebug()) Log.v("NotificationActivity.onResume()");
	    Context context = getContext();
	    acquireWakeLock(context);
	}
	  
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
	@Override
	protected void onPause() {
	    super.onPause();
	    if (Log.getDebug()) Log.v("NotificationActivity.onPause()");
	    releaseWakeLock(); 
	}
	  
	/**
	 * Activity was stopped due to a new Activity being started or other reason.
	 */
	@Override
	protected void onStop() {
	    super.onStop();
	    if (Log.getDebug()) Log.v("NotificationActivity.onStop()");
	    releaseWakeLock();
	}
	  
	/**
	 * Activity was stopped and closed out completely.
	 */
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (Log.getDebug()) Log.v("NotificationActivity.onDestroy()");
	    releaseWakeLock();
	    reenableKeyguardLock();
	}

	/**
	 * Create new Dialog.
	 * 
	 * @param id
	 * 
	 * @return Dialog
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		if (Log.getDebug()) Log.v("NotificationActivity.onCreateDialog()");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		int notificationType = getNotificationViewFlipper().getActiveMessage().getNotificationType();
		AlertDialog alertDialog = null;
		switch (id) {

	        /*
	         * Delete confirmation dialog.
	         */
			case DIALOG_DELETE_MESSAGE:
				if (Log.getDebug()) Log.v("NotificationActivity.onCreateDialog() DIALOG_DELETE_MESSAGE");
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setTitle(getString(R.string.delete_message_dialog_title_text));
				//Action is determined by the users preferences. 
				//Either show the 
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
							customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							deleteMessage();
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
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
     * @param intent
     */
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    if (Log.getDebug()) Log.v("NotificationActivity.onNewIntent()");
	    setIntent(intent);
	    Context context = getContext();
	    Bundle extrasBundle = getIntent().getExtras();
	    int notificationType = extrasBundle.getInt("notificationType");
	    if (Log.getDebug()) Log.v("NotificationActivity.onNewIntent() Notification Type: " + notificationType);
	    if(notificationType == NOTIFICATION_TYPE_PHONE){
	    	if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_PHONE");
	    	setupMissedCalls(extrasBundle);
	    }
	    if(notificationType == NOTIFICATION_TYPE_SMS){
		    if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_SMS");
		    setupMessages(extrasBundle);
		    //TODO - NotificationActivity.onNewIntent() - NOTIFICATION_TYPE_SMS Get all unread messages if new Activity?
	    }
	    if(notificationType == NOTIFICATION_TYPE_MMS){
	    	if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_MMS");
	    	//TODO - MMS Message
		    //TODO - NotificationActivity.onNewIntent() - NOTIFICATION_TYPE_MMS Get all unread messages if new Activity?
	    }
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	if (Log.getDebug()) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_CALENDAR");
		    setupCalendarEventNotifications(extrasBundle);
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	if (Log.getDebug()) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_EMAIL");
	    	//TODO - Email Message
	    }
	    //Set Vibration or Ringtone to announce Activity.
	    runNotificationFeedback(notificationType);
	    //Acquire WakeLock
	    acquireWakeLock(context);
	    //Remove the KeyGuard
	    disableKeyguardLock(context);
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Set up the ViewFlipper elements.
	 * 
	 * @param notificationType
	 */ 
	private void setupViews(int notificationType) {
		if (Log.getDebug()) Log.v("NotificationActivity.setupViews()");
		final NotificationViewFlipper notificationViewFlipper = (NotificationViewFlipper) findViewById(R.id.notification_view_flipper);
		final Button previousButton = (Button) findViewById(R.id.previous_button);
		final Button nextButton = (Button) findViewById(R.id.next_button);
		final TextView notificationCountTextView = (TextView) findViewById(R.id.notification_count_text_view);
		setNotificationViewFlipper(notificationViewFlipper);
		setPreviousButton(previousButton);
		setNextButton(nextButton);
		setNotificationCountTextView(notificationCountTextView);
		// Previous Button
		previousButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View view) {
		    	if (Log.getDebug()) Log.v("Previous Button Clicked()");
		    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		    	notificationViewFlipper.showPrevious();
		    	updateNavigationButtons(previousButton, notificationCountTextView, nextButton, notificationViewFlipper);
		    }
		});
		// Next Button
		nextButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View view) {
		    	if (Log.getDebug()) Log.v("Next Button Clicked()");
		    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		    	notificationViewFlipper.showNext();
		    	updateNavigationButtons(previousButton, notificationCountTextView, nextButton, notificationViewFlipper);
		    }
		});
		initNavigationButtons();	    
	}
	
	/**
	 * Setup the missed calls notifications.
	 * 
	 * @param bundle
	 */
	private boolean setupMissedCalls(Bundle bundle){
		if (Log.getDebug()) Log.v("NotificationActivity.setupMissedCalls()"); 
		Context context = getContext();
		final NotificationViewFlipper notificationViewFlipper = getNotificationViewFlipper();
		final Button previousButton = getPreviousButton();
		final Button nextButton = getNextButton();
		final TextView notificationCountTextView = getNotificationCountTextView();
		ArrayList<String> missedCallsArray = bundle.getStringArrayList("missedCallsArrayList");
		for(int i=0; i< missedCallsArray.size(); i++){
			String[] missedCallInfo = missedCallsArray.get(i).split("\\|");
			String phoneNumber = missedCallInfo[0];
			long timeStamp = Long.parseLong(missedCallInfo[1]);
			Notification missedCallNotification = new Notification(context, phoneNumber, timeStamp, NOTIFICATION_TYPE_PHONE);
			getNotificationViewFlipper().addNotification(missedCallNotification);
		}
	    updateNavigationButtons(previousButton, notificationCountTextView, nextButton, notificationViewFlipper);
	    return true;
	}
	
	/**
	 * Setup the SMS/MMS message notifications.
	 *
	 * @param bundle
	 */
	private void setupMessages(Bundle bundle) {
		if (Log.getDebug()) Log.v("NotificationActivity.setupMessages()"); 
		Context context = getContext();
		final NotificationViewFlipper notificationViewFlipper = getNotificationViewFlipper();
		final Button previousButton = getPreviousButton();
		final Button nextButton = getNextButton();
		final TextView notificationCountTextView = getNotificationCountTextView();
	    // Create message from bundle.
	    Notification smsMessage = new Notification(context, bundle, NOTIFICATION_TYPE_SMS);
	    if (Log.getDebug()) Log.v("NotificationActivity.setupMessages() Notification Phone Number: " + smsMessage.getPhoneNumber());
	    notificationViewFlipper.addNotification(smsMessage);
	    updateNavigationButtons(previousButton, notificationCountTextView, nextButton, notificationViewFlipper);
	}
	
	private void setupCalendarEventNotifications(Bundle bundle){
		if (Log.getDebug()) Log.v("NotificationActivity.setupCalendarEventNotifications()");  
		Context context = getContext();
		final NotificationViewFlipper notificationViewFlipper = getNotificationViewFlipper();
		final Button previousButton = getPreviousButton();
		final Button nextButton = getNextButton();
		final TextView notificationCountTextView = getNotificationCountTextView();
		String calenderEventInfo[] = (String[])bundle.getStringArray("calenderEventInfo");
		String title = calenderEventInfo[0];
		String messageBody = calenderEventInfo[1];
		long eventStartTime = Long.parseLong(calenderEventInfo[2]);
		long eventEndTime = Long.parseLong(calenderEventInfo[3]);
		//if (Log.getDebug()) Log.v("NotificationActivity.setupCalendarEventNotifications() AllDay: " + calenderEventInfo[4]); 
		boolean eventAllDay = Boolean.parseBoolean(calenderEventInfo[4]);
		//if (Log.getDebug()) Log.v("NotificationActivity.setupCalendarEventNotifications() CalendarID: " + calenderEventInfo[5]); 
		long calendarID = Long.parseLong(calenderEventInfo[5]);
		//if (Log.getDebug()) Log.v("NotificationActivity.setupCalendarEventNotifications() EventID: " + calenderEventInfo[6]); 
		long eventID = Long.parseLong(calenderEventInfo[6]);
		Notification calendarEventNotification = new Notification(context, title, messageBody, eventStartTime, eventEndTime, eventAllDay, calendarID, eventID, NOTIFICATION_TYPE_CALENDAR);
		getNotificationViewFlipper().addNotification(calendarEventNotification);		
		updateNavigationButtons(previousButton, notificationCountTextView, nextButton, notificationViewFlipper);
	}
	
	/**
	 * Initialize the navigation buttons and text.
	 */
	private void initNavigationButtons(){
		if (Log.getDebug()) Log.v("NotificationActivity.initNavigationButtons()");
		final NotificationViewFlipper notificationViewFlipper = getNotificationViewFlipper();
		final Button previousButton = getPreviousButton();
		final Button nextButton = getNextButton();
		final TextView notificationCountTextView = getNotificationCountTextView();
		updateNavigationButtons(previousButton, notificationCountTextView, nextButton, notificationViewFlipper);
	}  
	  
	/**
	 * Update the navigation buttons and text when items are added or removed.
	 * 
	 * @param previousButton
	 * @param notificationCountTextView
	 * @param nextButton
	 */
    public void updateNavigationButtons(Button previousButton, TextView notificationCountTextView, Button nextButton, NotificationViewFlipper notificationViewFlipper){
    	if (Log.getDebug()) Log.v("NotificationActivity.updateNavigationButtons()");
    	previousButton.setEnabled(!notificationViewFlipper.isFirstMessage());
    	notificationCountTextView.setText( (notificationViewFlipper.getCurrentNotification() + 1) + "/" + notificationViewFlipper.getTotalNotifications());
    	nextButton.setEnabled(!notificationViewFlipper.isLastMessage()); 		
    }
   
//	/**
//	 * Resize the notification to fit the screen.
//	 * Makes the notification pretty.
//	 */
//	private void resizeLayout() {
//		if (Log.getDebug()) Log.v("NotificationActivity.resizeLayout()");
//		Display d = getWindowManager().getDefaultDisplay();
//		int width = d.getWidth() > MAX_WIDTH ? MAX_WIDTH : (int) (d.getWidth() * WIDTH);
//		getMainActivityLayout().setMinimumWidth(width);
//		getMainActivityLayout().invalidate();
//	}
//
//	/**
//	 * Show the soft keyboard and store the view that triggered it.
//	 * 
//	 * @param triggeringView
//	 */
//	private void showSoftKeyboard(View triggeringView) {
//		if (Log.getDebug()) Log.v("NotificationActivity.showSoftKeyboard()");
//	    if (getInputMethodManager() == null) {
//	    	setInputMethodManager((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
//	    }
//	    setSoftKeyboardTriggerView(triggeringView);
//	    getInputMethodManager().toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//	}
//	
//	/**
//	 * Hide the soft keyboard.
//	 */
//	private void hideSoftKeyboard() {
//		if (Log.getDebug()) Log.v("NotificationActivity.hideSoftKeyboard()");
//	    if (getSoftKeyboardTriggerView() == null) return;
//	    if (getInputMethodManager() == null) {
//	    	setInputMethodManager((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
//	    }
//	    getInputMethodManager().hideSoftInputFromWindow(getSoftKeyboardTriggerView().getApplicationWindowToken(), 0);
//	    setSoftKeyboardTriggerView(null);
//	}
	
	/**
	 * Delete the current message from the users phone.
	 */
	private void deleteMessage(){
		if (Log.getDebug()) Log.v("NotificationActivity.deleteMessage()");
		NotificationViewFlipper notificationViewFlipper = getNotificationViewFlipper();
		notificationViewFlipper.deleteMessage();
	}
	
	/**
	 * Launches the preferences screen as new intent.
	 */
	private void launchPreferenceScreen(){
		if (Log.getDebug()) Log.v("NotificationActivity.launchPreferenceScreen()");
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
		if (Log.getDebug()) Log.v("NotificationActivity.acquireWakeLock()");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		WakeLock wakeLock = getWakeLock();
		if(wakeLock == null){
			//Set the wakeLock properties based on the users preferences.
			if(preferences.getBoolean(SCREEN_ENABLED_KEY, true)){
				if(preferences.getBoolean(SCREEN_DIM_ENABLED_KEY, true)){
					if (Log.getDebug()) Log.v("NotificationActivity.acquireWakeLock() Screen Wake Enabled Dim");
					wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, DROID_NOTIFY_WAKELOCK);
				}else{
					if (Log.getDebug()) Log.v("NotificationActivity.acquireWakeLock() Screen Wake Enabled Full");
					wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, DROID_NOTIFY_WAKELOCK);
				}
			}else{
				if (Log.getDebug()) Log.v("NotificationActivity.acquireWakeLock() Screen Wake Disabled");
				wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, DROID_NOTIFY_WAKELOCK);
			}
		}
		if (Log.getDebug()) Log.v("NotificationActivity.acquireWakeLock() Aquired wake lock");
		wakeLock.acquire();
		setWakeLock(wakeLock);
	}
	
	/**
	 * Function that releases the WakeLock.
	 */
	private void releaseWakeLock(){
		if (Log.getDebug()) Log.v("NotificationActivity.releaseWakeLock()");
		WakeLock wakeLock = getWakeLock();
		if(wakeLock != null){
			wakeLock.release();
			wakeLock = null;
			setWakeLock(wakeLock);
		}
	}
	
	/**
	 * Function that disables the Keyguard for this Activity.
	 * The removal of the Keyguard will be determined by the user preferences. 
	 * 
	 * @param context - The current context of this Activity.
	 */
	private void disableKeyguardLock(Context context){
		if (Log.getDebug()) Log.v("NotificationActivity.disableKeyguardLock()");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		KeyguardManager km = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardLock keyguardLock = getKeyguardLock();
		if(keyguardLock == null){
			keyguardLock = km.newKeyguardLock(DROID_NOTIFY_KEYGUARD); 
		}
		//Set the keyguard properties based on the users preferences.
		if(preferences.getBoolean(KEYGUARD_ENABLED_KEY, true)){
			if (Log.getDebug()) Log.v("NotificationActivity.disableKeyguardLock() Disable Keyguard Enabled");
			keyguardLock.disableKeyguard();
			setKeyguardLock(keyguardLock);
		}else{
			if (Log.getDebug()) Log.v("NotificationActivity.disableKeyguardLock() Disable Keyguard Disabled");
		}
	}

	/**
	 * Re-Enables the Keyguard for this Activity.
	 */
	private void reenableKeyguardLock(){
		if (Log.getDebug()) Log.v("NotificationActivity.reenableKeyguardLock()");
		KeyguardLock keyguardLock = getKeyguardLock();
		if(keyguardLock != null){
			keyguardLock.reenableKeyguard();
			keyguardLock = null;
			setKeyguardLock(keyguardLock);
		}
	}
	
	/**
	 * Function to set ring tone or vibration based on users preferences for this notification.
	 * 
	 * @param notificationType - The type of the current notification.
	 */
	private void runNotificationFeedback(int notificationType){
		if (Log.getDebug()) Log.v("NotificationActivity.runNotificationFeedback()");
		Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		//Set vibration based on user preferences.
	    if(notificationType == NOTIFICATION_TYPE_PHONE){
	 	    if(preferences.getBoolean(MISSED_CALL_VIBRATE_ENABLED_KEY, true)){
	 	    	vibrator.vibrate(1 * 1000);
	 	    }
	    }
	    if(notificationType == NOTIFICATION_TYPE_SMS){
	 	    if(preferences.getBoolean(SMS_VIBRATE_ENABLED_KEY, true)){
	 	    	vibrator.vibrate(1 * 1000);
	 	    }
	    }
	    if(notificationType == NOTIFICATION_TYPE_MMS){
	 	    if(preferences.getBoolean(MMS_VIBRATE_ENABLED_KEY, true)){
	 	    	vibrator.vibrate(1 * 1000);
	 	    }
	    }
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	 	    if(preferences.getBoolean(CALENDAR_VIBRATE_ENABLED_KEY, true)){
	 	    	vibrator.vibrate(1 * 1000);
	 	    }
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	//TODO - Email Message
	    }
	}
	
	/**
	 * Function that performs custom haptic feedback.
	 * This function performs haptic feedback based on the users preferences.
	 * 
	 * @param hapticFeedbackConstant - What type of action the feedback is responding to.
	 */
	private void customPerformHapticFeedback(int hapticFeedbackConstant){
		if (Log.getDebug()) Log.v("NotificationActivity.customPerformHapticFeedback()");
		Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		//Perform the haptic feedback based on the users preferences.
		if(preferences.getBoolean(HAPTIC_FEEDBACK_ENABLED_KEY, true)){
			if(hapticFeedbackConstant == HapticFeedbackConstants.VIRTUAL_KEY){
				vibrator.vibrate(50);
			}
		}
		if(preferences.getBoolean(HAPTIC_FEEDBACK_ENABLED_KEY, true)){
			if(hapticFeedbackConstant == HapticFeedbackConstants.LONG_PRESS){
				vibrator.vibrate(100);
			}
		}
	}
	
	/**
	 * Function to create a test notification of each type.
	 */
	private void createTestNotifications(){
		if (Log.getDebug()) Log.v("NotificationActivity.createTextNotifications()");
		Context context = getContext();
		NotificationViewFlipper notificationViewFlipper = getNotificationViewFlipper();
		//Add SMS Message Notification.
		Notification smsNotification = new Notification(context, "5555555555", "Droid Notify SMS Message Test", System.currentTimeMillis(), NOTIFICATION_TYPE_SMS);
		notificationViewFlipper.addNotification(smsNotification);
		//Add Missed Call Notification.
		Notification missedCallNotification = new Notification(context, "5555555555", System.currentTimeMillis(), NOTIFICATION_TYPE_PHONE);
		notificationViewFlipper.addNotification(missedCallNotification);
		//Add Calendar Event Notification.
		Notification calendarEventNotification = new Notification(context, "Droid Notify Calendar Event Test - Droid Notify Calendar Event Test - Droid Notify Calendar Event Test - Droid Notify Calendar Event Test - Droid Notify Calendar Event Test", "", System.currentTimeMillis(), System.currentTimeMillis() + (10 * 60 * 1000), false, 1, 1, NOTIFICATION_TYPE_CALENDAR);
		notificationViewFlipper.addNotification(calendarEventNotification);	
	    updateNavigationButtons(getPreviousButton(), getNotificationCountTextView(), getNextButton(), getNotificationViewFlipper());
	}
	
}
