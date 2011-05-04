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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
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
	
	private final int MENU_ITEM_SETTINGS = R.id.app_settings;
	
	private final int NOTIFICATION_TYPE_PHONE = 0;
	private final int NOTIFICATION_TYPE_SMS = 1;
	private final int NOTIFICATION_TYPE_MMS = 2;
	private final int NOTIFICATION_TYPE_CALENDAR = 3;
	private final int NOTIFICATION_TYPE_EMAIL = 4;
	
	private final int INCOMING_CALL_TYPE = android.provider.CallLog.Calls.INCOMING_TYPE;
	private final int OUTGOING_CALL_TYPE = android.provider.CallLog.Calls.OUTGOING_TYPE;
	private final int MISSED_CALL_TYPE = android.provider.CallLog.Calls.MISSED_TYPE;
	
	private final int DIALOG_DELETE_MESSAGE = 0;

	private final String APP_ENABLED_KEY = "app_enabled";
	private final String SMS_NOTIFICATIONS_ENABLED_KEY = "sms_notifications_enabled";
	private final String MMS_NOTIFICATIONS_ENABLED_KEY = "mms_notifications_enabled";
	private final String MISSED_CALL_NOTIFICATIONS_ENABLED_KEY = "missed_call_notifications_enabled";
	private final String SCREEN_ENABLED_KEY = "screen_enabled";
	private final String SCREEN_DIM_ENABLED_KEY = "screen_dim_enabled";
	private final String KEYGUARD_ENABLED_KEY = "keyguard_enabled";
	
	private final String SMS_DELETE_KEY = "sms_delete_button_action";
	private final String MMS_DELETE_KEY = "mms_delete_button_action";
	private final String SMS_DISMISS_ACTION_MARK_READ = "0";
	private final String SMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	private final String SMS_DELETE_ACTION_DELETE_THREAD = "1";
	private final String SMS_DELETE_ACTION_NOTHING = "2";
	private final String MMS_DISMISS_ACTION_MARK_READ = "0";
	private final String MMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	private final String MMS_DELETE_ACTION_DELETE_THREAD = "1";
	private final String MMS_DELETE_ACTION_NOTHING = "2";
	
	private final int CONTACT_PHOTO_IMAGE_VIEW = R.id.contact_image_view;
	private final int CONTACT_PHOTO_LINEAR_LAYOUT = R.id.contact_linear_layout;
	
	private final int VIEW_CONTACT_CONTEXT_MENU = R.id.view_contact;
	private final int ADD_CONTACT_CONTEXT_MENU = R.id.add_contact;
	private final int CALL_CONTACT_CONTEXT_MENU = R.id.call_contact;
	private final int TEXT_CONTACT_CONTEXT_MENU = R.id.text_contact;	
	private final int EDIT_CONTACT_CONTEXT_MENU = R.id.edit_contact;
	
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
//	
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
				if(notification.getContactExists()){
					contextMenu.setHeaderTitle(notification.getContactName()); 
				}else{
					contextMenu.setHeaderTitle(phoneNumber); 
				}
				menuInflater.inflate(R.menu.photocontextmenu, contextMenu);
				//Remove menu options based on the NotificationType.
				if(notification.getContactExists()){
					MenuItem menuItem = contextMenu.findItem(ADD_CONTACT_CONTEXT_MENU);
					menuItem.setVisible(false);
				}else{
					MenuItem viewMenuItem = contextMenu.findItem(VIEW_CONTACT_CONTEXT_MENU);
					viewMenuItem.setVisible(false);
					MenuItem editMenuItem = contextMenu.findItem(EDIT_CONTACT_CONTEXT_MENU);
					editMenuItem.setVisible(false);
				}
			    if(notificationType == NOTIFICATION_TYPE_PHONE){
			    	MenuItem menuItem = contextMenu.findItem(CALL_CONTACT_CONTEXT_MENU);
					menuItem.setVisible(false);
			    }
			    if(notificationType == NOTIFICATION_TYPE_SMS){
			    	MenuItem menuItem = contextMenu.findItem(TEXT_CONTACT_CONTEXT_MENU);
					menuItem.setVisible(false);
			    }
			    if(notificationType == NOTIFICATION_TYPE_MMS){
			    	MenuItem menuItem = contextMenu.findItem(TEXT_CONTACT_CONTEXT_MENU);
					menuItem.setVisible(false);
			    }
			    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
			    	//TODO - Calendar Event
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
		switch (menuItem.getItemId()) {
			case VIEW_CONTACT_CONTEXT_MENU:
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
			case ADD_CONTACT_CONTEXT_MENU:
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
			case CALL_CONTACT_CONTEXT_MENU:
				intent = new Intent(Intent.ACTION_CALL);
		        intent.setData(Uri.parse("tel:" + phoneNumber));		
		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
		        		| Intent.FLAG_ACTIVITY_SINGLE_TOP
		        		| Intent.FLAG_ACTIVITY_CLEAR_TOP
		        		| Intent.FLAG_ACTIVITY_NO_HISTORY
		        		| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			    context.startActivity(intent);
				return true;
			case TEXT_CONTACT_CONTEXT_MENU:
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
			case EDIT_CONTACT_CONTEXT_MENU:
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
		//Read preferences and end activity early if app is disabled.
	    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    if(!preferences.getBoolean(APP_ENABLED_KEY, true)){
			if (Log.getDebug()) Log.v("NotificationActivity.onCreate() App Disabled. Finishing Activity... ");
			finishActivity();
			return;
		}
	    Bundle extrasBundle = getIntent().getExtras();
	    int notificationType = extrasBundle.getInt("notificationType");
	    if (Log.getDebug()) Log.v("NotificationActivity.onCreate() Notification Type: " + notificationType);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	    //This does not work in Android Release 2.2 even though it should.
	    //Remove the phone's KeyGuard based on the users preferences.
//	    if(preferences.getBoolean(KEYGUARD_ENABLED_KEY, true)){
//	    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); 
//	    }
//	    if(preferences.getBoolean(SCREEN_ENABLED_KEY, true)){
//	    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//	    }    
//	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//	    		| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//	    		| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//	    		| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON); 
	    	    
	    setContentView(R.layout.notificationwrapper);    
	    setupViews(notificationType);
	    if(notificationType == NOTIFICATION_TYPE_PHONE){
	    	if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_PHONE");
			//Read preferences and end activity early if missed call notifications are disabled.
		    if(!preferences.getBoolean(MISSED_CALL_NOTIFICATIONS_ENABLED_KEY, true)){
				if (Log.getDebug()) Log.v("NotificationActivity.onCreate() Missed Call Notifications Disabled. Finishing Activity... ");
				finishActivity();
				return;
			}
	    	setupMissedCalls(extrasBundle, false);
	    }
	    if(notificationType == NOTIFICATION_TYPE_SMS){
		    if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_SMS");
			//Read preferences and end activity early if SMS notifications are disabled.
		    if(!preferences.getBoolean(SMS_NOTIFICATIONS_ENABLED_KEY, true)){
				if (Log.getDebug()) Log.v("NotificationActivity.onCreate() SMS Notifications Disabled. Finishing Activity... ");
				finishActivity();
				return;
			}
		    setupMessages(extrasBundle);
	    }
	    if(notificationType == NOTIFICATION_TYPE_MMS){
	    	if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_MMS");
			//Read preferences and end activity early if MMS notifications are disabled.
		    if(!preferences.getBoolean(MMS_NOTIFICATIONS_ENABLED_KEY, true)){
				if (Log.getDebug()) Log.v("NotificationActivity.onCreate() MMS Notifications Disabled. Finishing Activity... ");
				finishActivity();
				return;
			}
	    	//TODO - MMS Message
	    }
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_CALENDAR");
	    	//TODO - Calendar Event
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_EMAIL");
	    	//TODO - Email Message
	    }
	    
	    //Testing out the calendar function. This can only be run on a phone, not on the emulator.
	    setupCalendarReminders(extrasBundle);
	    
	    //Acquire WakeLock
	    acquireWakeLock(context);
	    //Remove the KeyGuard
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
	    disableKeyguardLock(context);
	}
	  
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
	@Override
	protected void onPause() {
	    super.onPause();
	    if (Log.getDebug()) Log.v("NotificationActivity.onPause()");
	    releaseWakeLock();
	    reenableKeyguardLock();  
	}
	  
	/**
	 * Activity was stopped due to a new Activity being started or other reason.
	 */
	@Override
	protected void onStop() {
	    super.onStop();
	    if (Log.getDebug()) Log.v("NotificationActivity.onStop()");
	    releaseWakeLock();
	    reenableKeyguardLock();
	}
	  
	/**
	 * Activity was stopped and closed out completely.
	 */
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (Log.getDebug()) Log.v("NotificationActivity.onDestroy()");
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
							deleteMessage();
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
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
	    //Read preferences and end activity early if app is disabled.
	    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    if(!preferences.getBoolean(APP_ENABLED_KEY, true)){
			if (Log.getDebug()) Log.v("NotificationActivity.onNewIntent() App Disabled. Finishing Activity... ");
			return;
		}
	    Bundle extrasBundle = getIntent().getExtras();
	    int notificationType = extrasBundle.getInt("notificationType");
	    if (Log.getDebug()) Log.v("NotificationActivity.onNewIntent() Notification Type: " + notificationType);
	    if(notificationType == NOTIFICATION_TYPE_PHONE){
	    	if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_PHONE");
			//Read preferences and end activity early if missed call notifications are disabled.
		    if(!preferences.getBoolean(MISSED_CALL_NOTIFICATIONS_ENABLED_KEY, true)){
				if (Log.getDebug()) Log.v("NotificationActivity.onNewIntent() Missed Call Notifications Disabled.");
				return;
			}
	    	setupMissedCalls(extrasBundle, true);
	    }
	    if(notificationType == NOTIFICATION_TYPE_SMS){
		    if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_SMS");
			//Read preferences and end activity early if SMS notifications are disabled.
		    if(!preferences.getBoolean(SMS_NOTIFICATIONS_ENABLED_KEY, true)){
				if (Log.getDebug()) Log.v("NotificationActivity.onNewIntent() SMS Notifications Disabled.");
				return;
			}
		    setupMessages(extrasBundle);
		    //TODO - NotificationActivity.onNewIntent() - NOTIFICATION_TYPE_SMS Get all unread messages if new Activity?
	    }
	    if(notificationType == NOTIFICATION_TYPE_MMS){
	    	if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_MMS");
			//Read preferences and end activity early if MMS notifications are disabled.
		    if(!preferences.getBoolean(MMS_NOTIFICATIONS_ENABLED_KEY, true)){
				if (Log.getDebug()) Log.v("NotificationActivity.onNewIntent() MMS Notifications Disabled.");
				return;
			}
	    	//TODO - MMS Message
		    //TODO - NotificationActivity.onNewIntent() - NOTIFICATION_TYPE_MMS Get all unread messages if new Activity?
	    }
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	if (Log.getDebug()) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_CALENDAR");
	    	//TODO - Calendar Event
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	if (Log.getDebug()) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_EMAIL");
	    	//TODO - Email Message
	    }
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
		    public void onClick(View v) {
		    	if (Log.getDebug()) Log.v("Previous Button Clicked()");
		    	notificationViewFlipper.showPrevious();
		    	updateNavigationButtons(previousButton, notificationCountTextView, nextButton, notificationViewFlipper);
		    }
		});
		// Next Button
		nextButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	if (Log.getDebug()) Log.v("Next Button Clicked()");
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
	private void setupMissedCalls(Bundle bundle, boolean newIntent){
		if (Log.getDebug()) Log.v("NotificationActivity.setupMissedCalls()"); 
		Context context = getContext();
		final NotificationViewFlipper notificationViewFlipper = getNotificationViewFlipper();
		final Button previousButton = getPreviousButton();
		final Button nextButton = getNextButton();
		final TextView notificationCountTextView = getNotificationCountTextView();
		ArrayList<String> missedCallsArray = getMissedCalls(newIntent);
		for(int i=0; i< missedCallsArray.size(); i++){
			String[] missedCallInfo = missedCallsArray.get(i).split("\\|");
			if (Log.getDebug()) Log.v("NotificationActivity.setupMissedCalls() MissedCallInfo: " + missedCallsArray.get(i));
			String phoneNumber = missedCallInfo[0];
			if (Log.getDebug()) Log.v("NotificationActivity.setupMissedCalls() ParsedPhone Number: " + phoneNumber);
			long timeStamp = Long.parseLong(missedCallInfo[1]);
			if (Log.getDebug()) Log.v("NotificationActivity.setupMissedCalls() Parsed TimeStamp: " + timeStamp);
			Notification missedCallNotification = new Notification(context, phoneNumber, timeStamp, NOTIFICATION_TYPE_PHONE);
			getNotificationViewFlipper().addNotification(missedCallNotification);
		}
	    updateNavigationButtons(previousButton, notificationCountTextView, nextButton, notificationViewFlipper);
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
	
	private void setupCalendarReminders(Bundle bundle){
		if (Log.getDebug()) Log.v("NotificationActivity.setupCalendarReminders()");  
		Context context = getContext();
		final NotificationViewFlipper notificationViewFlipper = getNotificationViewFlipper();
		final Button previousButton = getPreviousButton();
		final Button nextButton = getNextButton();
		final TextView notificationCountTextView = getNotificationCountTextView();
		String calenderReminderInfo[] = (String[])bundle.getStringArray("calenderReminderInfo");
		String title = calenderReminderInfo[0];
		String messageBody = calenderReminderInfo[1];
		long timeStamp = Long.parseLong(calenderReminderInfo[2]);
		Notification calendarReminderNotification = new Notification(context, title, messageBody, timeStamp, NOTIFICATION_TYPE_CALENDAR);
		getNotificationViewFlipper().addNotification(calendarReminderNotification);		
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
	 * Query the call log and check for any missed calls.
	 * 
	 * @param isNewIntent
	 * 
	 * @return ArrayList<String>
	 */
	private ArrayList<String> getMissedCalls(boolean isNewIntent){
		if (Log.getDebug()) Log.v("NotificationActivity.getMissedCalls() IsNewIntent? " + isNewIntent);
		ArrayList<String> missedCallsArray = new ArrayList<String>();
		final String[] projection = null;
		final String selection = null;
		final String[] selectionArgs = null;
		final String sortOrder = "DATE DESC";
	    Cursor cursor = getContext().getContentResolver().query(
	    		Uri.parse("content://call_log/calls"),
	    		projection,
	    		selection,
				selectionArgs,
				sortOrder);
	    if (cursor != null) {
	    	//TODO - Possible Bug - call log might not have been updated with the last call when we check it. I am not sure how to handle that case yet.
	    	while (cursor.moveToNext()) { 
	    		String callNumber = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
	    		String callDate = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE));
	    		String callType = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE));
	    		String isCallNew = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NEW));
	    		if (Log.getDebug()) Log.v("NotificationActivity.getMissedCalls() Checking Call: " + callNumber + " Received At: " + callDate + " Call Type: " + callType + " Is Call New? " + isCallNew);
	    		if(Integer.parseInt(callType) == MISSED_CALL_TYPE && Integer.parseInt(isCallNew) > 0){
    				if (Log.getDebug()) Log.v("NotificationActivity.getMissedCalls() Missed Call Found: " + callNumber);
    				//Store missed call numbers and dates in an array.
    				String missedCallInfo = callNumber + "|" + callDate;
    				missedCallsArray.add(missedCallInfo);
    				if(isNewIntent){
    					break;
    				}
    			}else{
    				break;
    			}
	    	}
	    	cursor.close();
	    }
	    return missedCallsArray;
	}
	
	/**
	 * Acquires the WakeLock for this Activity.
	 * The type flags for the WakeLock will be determined by the user preferences. 
	 * 
	 * @param context
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
					wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "DroidNotify.NotificationActivity");
				}else{
					if (Log.getDebug()) Log.v("NotificationActivity.acquireWakeLock() Screen Wake Enabled Full");
					wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "DroidNotify.NotificationActivity");
				}
			}else{
				if (Log.getDebug()) Log.v("NotificationActivity.acquireWakeLock() Screen Wake Disabled");
				wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "DroidNotify.NotificationActivity");
			}
		}
		if (Log.getDebug()) Log.v("NotificationActivity.acquireWakeLock() Aquired wake lock");
		wakeLock.acquire();
		setWakeLock(wakeLock);
	}
	
	/**
	 * Release the WakeLock.
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
	 * Disables the KeyGuard for this Activity.
	 * The removal of the KeyGuard will be determined by the user preferences. 
	 * 
	 * @param context
	 */
	private void disableKeyguardLock(Context context){
		if (Log.getDebug()) Log.v("NotificationActivity.removeKeyGuard()");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		KeyguardManager km = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardLock keyguardLock = getKeyguardLock();
		if(keyguardLock == null){
			keyguardLock = km.newKeyguardLock(KEYGUARD_SERVICE); 
		}
		//Set the wakeLock properties based on the users preferences.
		if(preferences.getBoolean(KEYGUARD_ENABLED_KEY, true)){
			keyguardLock.disableKeyguard();
			setKeyguardLock(keyguardLock);
		}
	}

	/**
	 * Re-Enables the KeyGuard for this Activity. 
	 * 
	 * @param context
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
	
}
