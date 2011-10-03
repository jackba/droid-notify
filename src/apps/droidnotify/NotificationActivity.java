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
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import apps.droidnotify.log.Log;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.preferences.MainPreferenceActivity;

/**
 * This is the main activity that runs the notifications.
 * 
 * @author Camille Sévigny
 */
public class NotificationActivity extends Activity {

	//================================================================================
    // Constants
    //================================================================================
	
	private static final int MENU_ITEM_SETTINGS = R.id.app_settings;
	private static final int CONTACT_WRAPPER_LINEAR_LAYOUT = R.id.contact_wrapper_linear_layout;	
	private static final int VIEW_CONTACT_CONTEXT_MENU = R.id.view_contact_context_menu;
	private static final int ADD_CONTACT_CONTEXT_MENU = R.id.add_contact_context_menu;
	private static final int VIEW_CALL_LOG_CONTEXT_MENU = R.id.view_call_log_context_menu;
	private static final int CALL_CONTACT_CONTEXT_MENU = R.id.call_contact_context_menu;
	private static final int TEXT_CONTACT_CONTEXT_MENU = R.id.text_contact_context_menu;	
	private static final int EDIT_CONTACT_CONTEXT_MENU = R.id.edit_contact_context_menu;
	private static final int ADD_CALENDAR_EVENT_CONTEXT_MENU = R.id.add_calendar_event_context_menu;
	private static final int EDIT_CALENDAR_EVENT_CONTEXT_MENU = R.id.edit_calendar_event_context_menu;
	private static final int VIEW_THREAD_CONTEXT_MENU = R.id.view_thread_context_menu;
	private static final int MESSAGING_INBOX_CONTEXT_MENU = R.id.messaging_inbox_context_menu;
	private static final int DISMISS_NOTIFICATION_CONTEXT_MENU = R.id.dismiss_notification_context_menu;
	private static final int VIEW_CALENDAR_CONTEXT_MENU = R.id.view_calendar_context_menu;
		
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	private Context _context = null;
	private WakeLock _wakeLock = null;
	private KeyguardLock _keyguardLock = null; 
	private NotificationViewFlipper _notificationViewFlipper = null;
	private WakeLockHandler _wakeLockHandler = null;
	private KeyguardHandler _keyguardHandler = null;
	private MotionEvent _downMotionEvent = null;
	SharedPreferences _preferences = null;

	//================================================================================
	// Public Methods
	//================================================================================

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
	 * Creates the menu item for this activity.
	 * 
	 * @param menu - Menu.
	 * 
	 * @return boolean - Returns true to indicate that the menu was created.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.optionsmenu, menu);
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
	    	case MENU_ITEM_SETTINGS:{
	    		launchPreferenceScreen();
	    		return true;
	    	}
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
			case CONTACT_WRAPPER_LINEAR_LAYOUT:{
				MenuInflater menuInflater = getMenuInflater();
				Notification notification = _notificationViewFlipper.getActiveNotification();
				int notificationType = notification.getNotificationType();
				//Add the header text to the menu.
				if(notificationType == Constants.NOTIFICATION_TYPE_CALENDAR){
					contextMenu.setHeaderTitle("Calendar Event");
				}else{
					if(notification.getContactExists()){
						contextMenu.setHeaderTitle(notification.getContactName()); 
					}else{
						contextMenu.setHeaderTitle(notification.getSentFromAddress()); 
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
				setupContextMenus(contextMenu, notificationType);
				break;
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
		Notification notification = _notificationViewFlipper.getActiveNotification();	
		//customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		switch (menuItem.getItemId()) {
			case ADD_CONTACT_CONTEXT_MENU:{
				return Common.startContactAddActivity(_context, this, notification.getSentFromAddress(), Constants.ADD_CONTACT_ACTIVITY);
			}
			case EDIT_CONTACT_CONTEXT_MENU:{
				return Common.startContactEditActivity(_context, this, notification.getContactID(), Constants.EDIT_CONTACT_ACTIVITY);
			}
			case VIEW_CONTACT_CONTEXT_MENU:{
				return Common.startContactViewActivity(_context, this, notification.getContactID(), Constants.VIEW_CONTACT_ACTIVITY);
			}
			case VIEW_CALL_LOG_CONTEXT_MENU:{
				return Common.startCallLogViewActivity(_context, this, Constants.VIEW_CALL_LOG_ACTIVITY);
			}
			case CALL_CONTACT_CONTEXT_MENU:{
				try{
					final String[] phoneNumberArray = getPhoneNumbers(notification);
					if(phoneNumberArray == null){
						Toast.makeText(_context, _context.getString(R.string.app_android_no_number_found_error), Toast.LENGTH_LONG).show();
						return false;
					}else if(phoneNumberArray.length == 1){
						return makePhoneCall(phoneNumberArray[0]);
					}else{
						AlertDialog.Builder builder = new AlertDialog.Builder(this);
						builder.setTitle(_context.getString(R.string.select_number_text));
						builder.setSingleChoiceItems(phoneNumberArray, -1, new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog, int selectedPhoneNumber) {
						        //Launch the SMS Messaging app to send a text to the selected number.
						    	String[] phoneNumberInfo = phoneNumberArray[selectedPhoneNumber].split(":");
						    	if(phoneNumberInfo.length == 2){
						    		makePhoneCall(phoneNumberInfo[1].trim());
						    	}else{
						    		Toast.makeText(_context, _context.getString(R.string.app_android_contacts_phone_number_chooser_error), Toast.LENGTH_LONG).show();
						    	}
						    	//Close the dialog box.
						    	dialog.dismiss();
						    }
						});
						builder.create().show();
						return true;
					}
				}catch(Exception ex){
					if (_debug) Log.e("NotificationActivity.onContextItemSelected() CALL_CONTACT_CONTEXT_MENU ERROR: " + ex.toString());
					Toast.makeText(_context, _context.getString(R.string.app_android_contacts_phone_number_chooser_error), Toast.LENGTH_LONG).show();
					return false;
				}
			}
			case TEXT_CONTACT_CONTEXT_MENU:{
				try{
					final String[] phoneNumberArray = getPhoneNumbers(notification);
					if(phoneNumberArray == null){
						Toast.makeText(_context, _context.getString(R.string.app_android_no_number_found_error), Toast.LENGTH_LONG).show();
						return false;
					}else if(phoneNumberArray.length == 1){
						return sendSMSMessage(phoneNumberArray[0]);
					}else{
						AlertDialog.Builder builder = new AlertDialog.Builder(this);
						builder.setTitle(_context.getString(R.string.select_number_text));
						builder.setSingleChoiceItems(phoneNumberArray, -1, new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog, int selectedPhoneNumber) {
						        //Launch the SMS Messaging app to send a text to the selected number.
						    	String[] phoneNumberInfo = phoneNumberArray[selectedPhoneNumber].split(":");
						    	if(phoneNumberInfo.length == 2){
						    		sendSMSMessage(phoneNumberInfo[1].trim());
						    	}else{
						    		Toast.makeText(_context, _context.getString(R.string.app_android_contacts_phone_number_chooser_error), Toast.LENGTH_LONG).show();
						    	}
						    	//Close the dialog box.
						    	dialog.dismiss();
						    }
						});
						builder.create().show();
						return true;
					}
				}catch(Exception ex){
					if (_debug) Log.e("NotificationActivity.onContextItemSelected() TEXT_CONTACT_CONTEXT_MENU ERROR: " + ex.toString());
					Toast.makeText(_context, _context.getString(R.string.app_android_contacts_phone_number_chooser_error), Toast.LENGTH_LONG).show();
					return false;
				}
			}
			case VIEW_THREAD_CONTEXT_MENU:{
				if(Common.startMessagingAppViewThreadActivity(_context, this, notification.getSentFromAddress(), Constants.VIEW_SMS_THREAD_ACTIVITY)){
				    //Set "In Reply Screen" flag.
					SharedPreferences.Editor editor = _preferences.edit();
					editor.putBoolean(Constants.USER_IN_MESSAGING_APP_KEY, true);
					editor.commit();
					return true;
				}else{
					return false;
				}
			}
			case MESSAGING_INBOX_CONTEXT_MENU:{
				if(Common.startMessagingAppViewInboxActivity(_context, this, Constants.MESSAGING_ACTIVITY)){
				    //Set "In Reply Screen" flag.
					SharedPreferences.Editor editor = _preferences.edit();
					editor.putBoolean(Constants.USER_IN_MESSAGING_APP_KEY, true);
					editor.commit();
					return true;
				}else{
					return false;
				}
			}
			case VIEW_CALENDAR_CONTEXT_MENU:{
				return Common.startViewCalendarActivity(_context, this, Constants.CALENDAR_ACTIVITY);
			}
			case ADD_CALENDAR_EVENT_CONTEXT_MENU:{
				return Common.startAddCalendarEventActivity(_context, this, Constants.ADD_CALENDAR_ACTIVITY);
			}
			case EDIT_CALENDAR_EVENT_CONTEXT_MENU:{
				return Common.startEditCalendarEventActivity(_context, this, notification.getCalendarEventID(), notification.getCalendarEventStartTime(), notification.getCalendarEventEndTime(), Constants.EDIT_CALENDAR_ACTIVITY);
			}
			case DISMISS_NOTIFICATION_CONTEXT_MENU:{
				try{
					//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
					return true;
				}catch(Exception ex){
					if (_debug) Log.e("NotificationActivity.onContextItemSelected() DISMISS_NOTIFICATION_CONTEXT_MENU ERROR: " + ex.toString());
					return false;
				}
			}
			default:{
				return super.onContextItemSelected(menuItem);
			}
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
		int notificationType = _notificationViewFlipper.getActiveNotification().getNotificationType();
		switch(notificationType){
			case Constants.NOTIFICATION_TYPE_SMS:{
				if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_NOTHING)){
					//Remove the notification from the ViewFlipper.
					deleteMessage();
				}else{
					if(_preferences.getBoolean(Constants.SMS_CONFIRM_DELETION_KEY, true)){
						//Confirm deletion of the message.
						showDialog(Constants.DIALOG_DELETE_MESSAGE);
					}else{
						//Remove the notification from the ViewFlipper.
						deleteMessage();
					}
				}
				break;
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
				if(_preferences.getString(Constants.MMS_DELETE_KEY, "0").equals(Constants.MMS_DELETE_ACTION_NOTHING)){
					//Remove the notification from the ViewFlipper
					deleteMessage();
				}else{
					if(_preferences.getBoolean(Constants.MMS_CONFIRM_DELETION_KEY, true)){
						//Confirm deletion of the message.
						showDialog(Constants.DIALOG_DELETE_MESSAGE);
					}else{
						//Remove the notification from the ViewFlipper.
						deleteMessage();
					}
				}
				break;
			}
		}
	}
	
	/**
	 * Handles the activity when the configuration changes (e.g. The phone switches from portrait view to landscape view).
	 */
	@Override
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
	    switch (motionEvent.getAction()){
	        case MotionEvent.ACTION_DOWN:{
		        //Keep track of the starting down-event.
		        _downMotionEvent = MotionEvent.obtain(motionEvent);
		        break;
	        }
	        case MotionEvent.ACTION_UP:{
	            //Consume if necessary and perform the fling / swipe action if it has been determined to be a fling / swipe.
	        	float deltaX = motionEvent.getX() - _downMotionEvent.getX();
		        if(Math.abs(deltaX) > new ViewConfiguration().getScaledTouchSlop()  * 2){
		        	if (deltaX < 0){
	                   _notificationViewFlipper.showNext();
	                   return true;
					}else if (deltaX > 0){
	                   _notificationViewFlipper.showPrevious();
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
	 * When a result is returned from an Activity that this activity launched, react based on the resturned result.
	 * 
	 * @param requestCode - The Activity code id that the result came from.
	 * @param resultCode - The result from the Activity.
	 * @param returnedIntent - The intent that was returned.
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent){
		if (_debug) Log.v("NotificationActivity.onActivityResult( ) requestCode: " + requestCode + " resultCode: " + resultCode);
	    switch(requestCode) {
		    case Constants.ADD_CONTACT_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() ADD_CONTACT_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() ADD_CONTACT_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() ADD_CONTACT_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_contacts_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		        break;
		    }
		    case Constants.EDIT_CONTACT_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() EDIT_CONTACT_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() EDIT_CONTACT_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() EDIT_CONTACT_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_contacts_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		        break;
		    }
		    case Constants.VIEW_CONTACT_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_CONTACT_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_CONTACT_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_CONTACT_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_contacts_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		        break;
		    }
		    case Constants.SEND_SMS_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() SEND_SMS_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() SEND_SMS_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() SEND_SMS_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_messaging_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}  
			    //Set "In Reply Screen" flag.
				SharedPreferences.Editor editor = _preferences.edit();
				editor.putBoolean(Constants.USER_IN_MESSAGING_APP_KEY, false);
				editor.commit();
		        break;
		    }
		    case Constants.VIEW_SMS_MESSAGE_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_SMS_MESSAGE_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_SMS_MESSAGE_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_SMS_MESSAGE_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_messaging_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}  
			    //Set "In Reply Screen" flag.
				SharedPreferences.Editor editor = _preferences.edit();
				editor.putBoolean(Constants.USER_IN_MESSAGING_APP_KEY, false);
				editor.commit();
		        break;
		    }
		    case Constants.VIEW_SMS_THREAD_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_SMS_THREAD_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_SMS_THREAD_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_SMS_THREAD_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_messaging_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}  
			    //Set "In Reply Screen" flag.
				SharedPreferences.Editor editor = _preferences.edit();
				editor.putBoolean(Constants.USER_IN_MESSAGING_APP_KEY, false);
				editor.commit();
		        break;
		    }
		    case Constants.MESSAGING_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() MESSAGING_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() MESSAGING_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() MESSAGING_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_messaging_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		    	//Set "In Reply Screen" flag.
				SharedPreferences.Editor editor = _preferences.edit();
				editor.putBoolean(Constants.USER_IN_MESSAGING_APP_KEY, false);
				editor.commit();
		        break;
		    }
		    case Constants.SEND_SMS_QUICK_REPLY_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() SEND_SMS_QUICK_REPLY_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
		    		_notificationViewFlipper.removeActiveNotification();
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() SEND_SMS_QUICK_REPLY_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					//_notificationViewFlipper.removeActiveNotification();
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() SEND_SMS_QUICK_REPLY_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_messaging_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}  
			    //Set "In Reply Screen" flag.
				SharedPreferences.Editor editor = _preferences.edit();
				editor.putBoolean(Constants.USER_IN_MESSAGING_APP_KEY, false);
				editor.commit();
		        break;
		    }
		    case Constants.CALL_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() CALL_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() CALL_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() CALL_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_phone_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		        break;
		    }
		    case Constants.ADD_CALENDAR_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() ADD_CALENDAR_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() ADD_CALENDAR_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() ADD_CALENDAR_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_calendar_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		        break;
		    }
		    case Constants.EDIT_CALENDAR_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() EDIT_CALENDAR_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() EDIT_CALENDAR_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() EDIT_CALENDAR_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_calendar_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		        break;
		    }
		    case Constants.VIEW_CALENDAR_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_CALENDAR_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_CALENDAR_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_CALENDAR_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_calendar_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		        break;
		    }
		    case Constants.VIEW_CALL_LOG_ACTIVITY:{
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_CALL_LOG_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
		    		_notificationViewFlipper.removeActiveNotification();
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_CALL_LOG_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_CALL_LOG_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_call_log_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		        break;
		    }
		    case Constants.CALENDAR_ACTIVITY:{
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() CALENDAR_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
		    		_notificationViewFlipper.removeActiveNotification();
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() CALENDAR_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification();
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() CALENDAR_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_calendar_app_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		        break;
		    }
	    }
    }
	
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
	    _context = getApplicationContext();
	    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
		_wakeLockHandler = new WakeLockHandler();
		_keyguardHandler = new KeyguardHandler();
		//_ringtoneHandler = new RingtoneHandler();
	    final Bundle extrasBundle = getIntent().getExtras();
	    int notificationType = extrasBundle.getInt("notificationType");
	    if (_debug) Log.v("NotificationActivity.onCreate() Notification Type: " + notificationType);
	    //Don't rotate the Activity when the screen rotates based on the user preferences.
	    if(!_preferences.getBoolean(Constants.LANDSCAPE_SCREEN_ENABLED_KEY, false)){
	    	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    }
	    //Get main window for this Activity.
	    Window mainWindow = getWindow(); 
	    //Set Blur 
	    if(_preferences.getBoolean(Constants.BLUR_SCREEN_ENABLED_KEY, false)){
	    	mainWindow.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	    }
	    //Set Dim
	    if(_preferences.getBoolean(Constants.DIM_SCREEN_ENABLED_KEY, false)){
	    	mainWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); 
		    WindowManager.LayoutParams params = mainWindow.getAttributes(); 
		    int dimAmt = Integer.parseInt(_preferences.getString(Constants.DIM_SCREEN_AMOUNT_KEY, "50"));
		    params.dimAmount = dimAmt / 100f; 
		    mainWindow.setAttributes(params); 
	    }
	    if(_preferences.getBoolean(Constants.SCREEN_ENABLED_KEY, true) && _preferences.getBoolean(Constants.KEYGUARD_ENABLED_KEY, true)){
	    	mainWindow.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	    	mainWindow.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
	    }
	    setContentView(R.layout.notificationwrapper);
	    setupViews(notificationType);
	    switch(notificationType){
	    	case Constants.NOTIFICATION_TYPE_TEST:{
		    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_TEST");
		    	createTestNotifications(); 
		    	break;
		    }    
		    case Constants.NOTIFICATION_TYPE_PHONE:{
		    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_PHONE");
		    	setupMissedCalls(extrasBundle);
		    	break;
		    }
		    case Constants.NOTIFICATION_TYPE_SMS:{
			    if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_SMS");
			    setupSMSMessages(extrasBundle, true);
		    	break;
		    }
		    case Constants.NOTIFICATION_TYPE_MMS:{
		    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_MMS");
		    	setupMMSMessages(extrasBundle, true);
		    	break;
		    }
		    case Constants.NOTIFICATION_TYPE_CALENDAR:{
		    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_CALENDAR");
		    	setupCalendarEventNotifications(extrasBundle);
		    	break;
			}
		    case Constants.NOTIFICATION_TYPE_GMAIL:{
		    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_GMAIL");
		    	//TODO - Email
		    	break;
		    }
			case Constants.NOTIFICATION_TYPE_TWITTER:{
				if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_TWITTER");
		    	//TODO - Twitter
				break;
			}
			case Constants.NOTIFICATION_TYPE_FACEBOOK:{
				if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_FACEBOOK");
		    	//TODO - Facebook
				break;
		    }
	    }
	    //Acquire WakeLock.
	    acquireWakeLock(_context);
	    long wakelockTimeout = Long.parseLong(_preferences.getString(Constants.WAKELOCK_TIMEOUT_KEY, "300")) * 1000;
	    _wakeLockHandler.sleep(wakelockTimeout);
	    //Remove the KeyGuard.
	    disableKeyguardLock(_context);
	    long keyguardTimeout = Long.parseLong(_preferences.getString(Constants.KEYGUARD_TIMEOUT_KEY, "300")) * 1000;
	    _keyguardHandler.sleep(keyguardTimeout);  
	    //Set "In Reply Screen" flag.
	    setInReplyScreenFlag();
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
	    acquireWakeLock(_context);
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
	    //Set "In Reply Screen" flag.
		SharedPreferences.Editor editor = _preferences.edit();
		editor.putBoolean(Constants.USER_IN_MESSAGING_APP_KEY, false);
		editor.commit();
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
		int notificationType = _notificationViewFlipper.getActiveNotification().getNotificationType();
		AlertDialog alertDialog = null;
		switch (id) {
	        /*
	         * Delete confirmation dialog.
	         */
			case Constants.DIALOG_DELETE_MESSAGE:{
				if (_debug) Log.v("NotificationActivity.onCreateDialog() DIALOG_DELETE_MESSAGE");
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setTitle(_context.getString(R.string.delete_text));
				//Action is determined by the users preferences. 
				if(notificationType == Constants.NOTIFICATION_TYPE_SMS){
					if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_DELETE_MESSAGE)){
						builder.setMessage(_context.getString(R.string.delete_message_dialog_text));
					}else if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_DELETE_THREAD)){
						builder.setMessage(_context.getString(R.string.delete_thread_dialog_text));
					}
				}else if(notificationType == Constants.NOTIFICATION_TYPE_MMS){
					if(_preferences.getString(Constants.MMS_DELETE_KEY, "0").equals(Constants.MMS_DELETE_ACTION_DELETE_MESSAGE)){
						builder.setMessage(_context.getString(R.string.delete_message_dialog_text));
					}else if(_preferences.getString(Constants.MMS_DELETE_KEY, "0").equals(Constants.MMS_DELETE_ACTION_DELETE_THREAD)){
						builder.setMessage(_context.getString(R.string.delete_thread_dialog_text));
					}
				}
				builder.setPositiveButton(R.string.delete_text, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							//customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							deleteMessage();
						}
					})
					.setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							//customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			            	dialog.cancel();
						}
					});
				alertDialog = builder.create();
				break;
			}
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
	    Bundle extrasBundle = getIntent().getExtras();
	    int notificationType = extrasBundle.getInt("notificationType");
	    if (_debug) Log.v("NotificationActivity.onNewIntent() Notification Type: " + notificationType);
	    switch(notificationType){
	    	case Constants.NOTIFICATION_TYPE_PHONE:{
		    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_PHONE");
		    	setupMissedCalls(extrasBundle);
		    	break;
		    }
	    	case Constants.NOTIFICATION_TYPE_SMS:{
			    if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_SMS");
			    setupSMSMessages(extrasBundle, false);
		    	break;
		    }
	    	case Constants.NOTIFICATION_TYPE_MMS:{
		    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_MMS");
		    	setupMMSMessages(extrasBundle, false);
		    	break;
		    }
	    	case Constants.NOTIFICATION_TYPE_CALENDAR:{
		    	if (_debug) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_CALENDAR");
			    setupCalendarEventNotifications(extrasBundle);
		    	break;
		    }
	    	case Constants.NOTIFICATION_TYPE_GMAIL:{
		    	if (_debug) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_GMAIL");
		    	//TODO - Gmail
		    	break;
		    }
			case Constants.NOTIFICATION_TYPE_TWITTER:{
				if (_debug) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_TWITTER");
		    	//TODO - Twitter
				break;
			}
			case Constants.NOTIFICATION_TYPE_FACEBOOK:{
				if (_debug) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_FACEBOOK");
		    	//TODO - Facebook
				break;
			}
	    }
	    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	    //Acquire WakeLock.
	    acquireWakeLock(_context);
	    long wakelockTimeout = Long.parseLong(preferences.getString(Constants.WAKELOCK_TIMEOUT_KEY, "300")) * 1000;
	    _wakeLockHandler.sleep(wakelockTimeout);
	    //Remove the KeyGuard.
	    disableKeyguardLock(_context);
	    long keyguardTimeout = Long.parseLong(preferences.getString(Constants.KEYGUARD_TIMEOUT_KEY, "300")) * 1000;
	    _keyguardHandler.sleep(keyguardTimeout);
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
		_notificationViewFlipper = (NotificationViewFlipper) findViewById(R.id.notification_view_flipper);
	}
	
	/**
	 * Delete the current message from the users phone.
	 */
	private void deleteMessage(){
		if (_debug) Log.v("NotificationActivity.deleteMessage()");
		_notificationViewFlipper.deleteMessage();
	}
	
	/**
	 * Launches the preferences screen as new intent.
	 */
	private void launchPreferenceScreen(){
		if (_debug) Log.v("NotificationActivity.launchPreferenceScreen()");
		Context context = getApplicationContext();
		Intent intent = new Intent(context, MainPreferenceActivity.class);
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
		PowerManager pm = null;
		try{
			pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
			if(_wakeLock == null){
				//Set the wakeLock properties based on the users preferences.
				if(_preferences.getBoolean(Constants.SCREEN_ENABLED_KEY, true)){
					if(_preferences.getBoolean(Constants.SCREEN_DIM_ENABLED_KEY, true)){
						if (_debug) Log.v("NotificationActivity.acquireWakeLock() Screen Wake Enabled Dim.");
						_wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, Constants.DROID_NOTIFY_WAKELOCK);
					}else{
						if (_debug) Log.v("NotificationActivity.acquireWakeLock() Screen Wake Enabled Full.");
						_wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, Constants.DROID_NOTIFY_WAKELOCK);
					}
				}else{
					if (_debug) Log.v("NotificationActivity.acquireWakeLock() Turn On Screen Disabled.");
					_wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Constants.DROID_NOTIFY_WAKELOCK);
				}
			}
			if(_wakeLock != null){
				if (_debug) Log.v("NotificationActivity.acquireWakeLock() Aquired wake lock");
				_wakeLock.setReferenceCounted(false);
				_wakeLock.acquire();
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
		try{
			if(_wakeLock != null){
				_wakeLock.release();
				_wakeLock = null;
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
		try{
			KeyguardManager km = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
			_keyguardLock = km.newKeyguardLock(Constants.DROID_NOTIFY_KEYGUARD);
			if(_preferences.getBoolean(Constants.SCREEN_ENABLED_KEY, true) && _preferences.getBoolean(Constants.KEYGUARD_ENABLED_KEY, true)){
				_keyguardLock.disableKeyguard();
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
		try{
			if(_keyguardLock != null){
				_keyguardLock.reenableKeyguard();
				_keyguardLock = null;
			}
		}catch(Exception ex){
			if (_debug) Log.e("NotificationActivity.reenableKeyguardLock() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Function to create a test notification of each type.
	 */
	private void createTestNotifications(){
		if (_debug) Log.v("NotificationActivity.createTextNotifications()");
		NotificationViewFlipper notificationViewFlipper = _notificationViewFlipper;
		//Add SMS Message Notification.
		Notification smsNotification = new Notification(_context, "5555555555", "Droid Notify SMS Message Test", System.currentTimeMillis(), Constants.NOTIFICATION_TYPE_SMS);
		notificationViewFlipper.addNotification(smsNotification);
		//Add Missed Call Notification.
		Notification missedCallNotification = new Notification(_context, 0, "5555555555", System.currentTimeMillis(), 0, "", 0, "", Constants.NOTIFICATION_TYPE_PHONE);
		notificationViewFlipper.addNotification(missedCallNotification);
		//Add Calendar Event Notification.
		Notification calendarEventNotification = new Notification(_context, "Droid Notify Calendar Event Test", "", System.currentTimeMillis(), System.currentTimeMillis() + (10 * 60 * 1000), false, "Test Calendar",  0, 0, Constants.NOTIFICATION_TYPE_CALENDAR);
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
	 * Send a text message using any android messaging app.
	 * 
	 * @param phoneNumber - The phone number we want to send a text message to.
	 * 
	 * @return boolean - Returns true if the activity was started.
	 */
	private boolean sendSMSMessage(String phoneNumber){
		if (_debug) Log.v("NotificationActivity.sendSMSMessage()");
		if(Common.startMessagingAppReplyActivity(_context, this, phoneNumber, Constants.SEND_SMS_ACTIVITY)){
			//Set "In Reply Screen" flag.
			SharedPreferences.Editor editor = _preferences.edit();
			editor.putBoolean(Constants.USER_IN_MESSAGING_APP_KEY, true);
			editor.commit();
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Place a phone call.
	 * 
	 * @param phoneNumber - The phone number we want to send a place a call to.
	 */
	private boolean makePhoneCall(String phoneNumber){
		if (_debug) Log.v("NotificationActivity.makePhoneCall()");
		return Common.makePhoneCall(_context, this, phoneNumber, Constants.CALL_ACTIVITY);
	}	
	
	/**
	 * 
	 * 
	 * @param notification
	 * 
	 * @return String[] - Array of phone numbers for this contact. Returns null if no numbers are found or available.
	 */
	private String[] getPhoneNumbers(Notification notification){
		if (_debug) Log.v("NotificationActivity.getPhoneNumbers()");	
		if(notification.getContactExists()){
			try{
				ArrayList<String> phoneNumberArray = new ArrayList<String>();
				long contactID = notification.getContactID();
				final String[] phoneProjection = new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.LABEL};
				final String phoneSelection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + String.valueOf(contactID);
				final String[] phoneSelectionArgs = null;
				final String phoneSortOrder = null;
				Cursor phoneCursor = _context.getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
						phoneProjection, 
						phoneSelection, 
						phoneSelectionArgs, 
						phoneSortOrder); 
				while (phoneCursor.moveToNext()) { 
					String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					int phoneNumberTypeInt = Integer.parseInt(phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));
					String phoneNumberType = null;
					switch(phoneNumberTypeInt){
						case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:{
							phoneNumberType = "Home: ";
							break;
						}
						case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:{
							phoneNumberType = "Mobile: ";
							break;
						}
						case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:{
							phoneNumberType = "Work: ";
							break;
						}
						case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:{
							phoneNumberType = "Work Fax: ";
							break;
						}
						case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:{
							phoneNumberType = "Home Fax: ";
							break;
						}
						case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:{
							phoneNumberType = "Pager: ";
							break;
						}
						case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:{
							phoneNumberType = "Other: ";
							break;
						}
						case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:{
							phoneNumberType = "Callback: ";
							break;
						}
						case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:{
							phoneNumberType = "Car: ";
							break;
						}
						case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:{
							phoneNumberType = "Company: ";
							break;
						}
						case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN:{
							phoneNumberType = "ISDN: ";
							break;
						}
						case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:{
							phoneNumberType = "Main: ";
							break;
						}
						case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX:{
							phoneNumberType = "Other Fax: ";
							break;
						}
						case ContactsContract.CommonDataKinds.Phone.TYPE_RADIO:{
							phoneNumberType = "Radio: ";
							break;
						}
						case ContactsContract.CommonDataKinds.Phone.TYPE_TELEX:{
							phoneNumberType = "Telex: ";
							break;
						}
						case ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD:{
							phoneNumberType = "TTY/TDD: ";
							break;
						}
						case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:{
							phoneNumberType = "Work Mobile: ";
							break;
						}
						case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER:{
							phoneNumberType = "Work Pager: ";
							break;
						}
						case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT:{
							phoneNumberType = "Assistant: ";
							break;
						}
						case ContactsContract.CommonDataKinds.Phone.TYPE_MMS:{
							phoneNumberType = "MMS: ";
							break;
						}
						case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM:{
							phoneNumberType = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL)) + ": ";
							break;
						}
						default:{
							phoneNumberType = "No Label: ";
							break;
						}
					}
					phoneNumberArray.add(phoneNumberType + phoneNumber);
				}
				phoneCursor.close(); 
				return phoneNumberArray.toArray(new String[]{});
			}catch(Exception ex){
				if (_debug) Log.e("NotificationActivity.getPhoneNumbers() ERROR: " + ex.toString());
				return null;
			}
		}else{
			String phoneNumber = notification.getSentFromAddress();
			if(!phoneNumber.contains("@")){
				return new String[] {phoneNumber};
			}else{
				return null;
			}
		}
	}

	/**
	 * Get new SMS messages.
	 *
	 * @param bundle - Activity bundle.
	 */
	private void setupSMSMessages(Bundle bundle, boolean loadAllNew) {
		if (_debug) Log.v("NotificationActivity.setupSMSMessages()"); 
		ArrayList<String> smsArray = bundle.getStringArrayList("smsArrayList");
		String currentMessageBody = null;
		String currentMessageID = "0";
		for(String smsArrayItem : smsArray){
			String[] smsInfo = smsArrayItem.split("\\|");
			String messageAddress = null;
			String messageBody = null;
			long messageID = 0;
			long threadID = 0;
			long contactID = 0;
			String contactName = null;
			long photoID = 0;
			String lookupKey = null;
			long timeStamp = 0;
			try{
				if(smsInfo.length < 5){
					if (_debug) Log.e("NotificationActivity.setupSMSMessages() FATAL NOTIFICATION ERROR. smsInfo.length: " + smsInfo.length);
					return;
				}else if(smsInfo.length == 5){ 
					messageAddress = smsInfo[0];
					messageBody = smsInfo[1];
					messageID = Long.parseLong(smsInfo[2]);
					threadID = Long.parseLong(smsInfo[3]);
					timeStamp = Long.parseLong(smsInfo[4]);
				}else{ 
					messageAddress = smsInfo[0];
					messageBody = smsInfo[1];
					messageID = Long.parseLong(smsInfo[2]);
					threadID = Long.parseLong(smsInfo[3]);
					timeStamp = Long.parseLong(smsInfo[4]);
					contactID = Long.parseLong(smsInfo[5]);
					contactName = smsInfo[6];
					photoID = Long.parseLong(smsInfo[7]);
					if(smsInfo.length < 9){
						lookupKey = "";
					}else{
						lookupKey = smsInfo[8];
					}
					currentMessageBody = messageBody;
					currentMessageID = String.valueOf(messageID);
				}
			}catch(Exception ex){
				if (_debug) Log.e("NotificationActivity.setupSMSMessages() ERROR: " + ex.toString());
			}
    		_notificationViewFlipper.addNotification(new Notification(_context, messageAddress, messageBody, messageID, threadID, timeStamp, contactID, contactName, photoID, lookupKey, Constants.NOTIFICATION_TYPE_SMS));
		    //Display Status Bar Notification
		    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_SMS, true, contactName, messageAddress, messageBody, null, null, null);
		}
		if(loadAllNew){
			//Load all unread SMS messages.
			if(_preferences.getBoolean(Constants.SMS_DISPLAY_UNREAD_KEY, false)){
				if(_preferences.getString(Constants.SMS_LOADING_SETTING_KEY, "0").equals(Constants.SMS_READ_FROM_INTENT)){
					new getAllUnreadSMSMessagesAsyncTask().execute(currentMessageID, currentMessageBody);
				}else{
					new getAllUnreadSMSMessagesAsyncTask().execute();
				}
		    }
		}
	}
	
	/**
	 * Get new MMS messages.
	 *
	 * @param bundle - Activity bundle.
	 */
	private void setupMMSMessages(Bundle bundle, boolean loadAllNew) {
		if (_debug) Log.v("NotificationActivity.setupMMSMessages()"); 
		ArrayList<String> mmsArray = bundle.getStringArrayList("mmsArrayList");
		for(String mmsArrayItem : mmsArray){
			String[] mmsInfo = mmsArrayItem.split("\\|");
			String messageAddress = null;
			String messageBody = null;
			long messageID = 0;
			long threadID = 0;
			long contactID = 0;
			String contactName = null;
			long photoID = 0;
			String lookupKey = null;
			long timeStamp = 0;
			try{
				if(mmsInfo.length < 5){
					if (_debug) Log.e("NotificationActivity.setupMMSMessages() FATAL NOTIFICATION ERROR. mmsInfo.length: " + mmsInfo.length);
					return;
				}else if( mmsInfo.length == 5){
					messageAddress = mmsInfo[0];
					messageBody = mmsInfo[1];
					messageID = Long.parseLong(mmsInfo[2]);
					threadID = Long.parseLong(mmsInfo[3]);
					//The timestamp is in seconds and not milliseconds. You must multiply by 1000. :)
					timeStamp = Long.parseLong(mmsInfo[4]) * 1000;
				}else{
					messageAddress = mmsInfo[0];
					messageBody = mmsInfo[1];
					messageID = Long.parseLong(mmsInfo[2]);
					threadID = Long.parseLong(mmsInfo[3]);
					//The timestamp is in seconds and not milliseconds. You must multiply by 1000. :)
					timeStamp = Long.parseLong(mmsInfo[4]) * 1000;
					contactID = Long.parseLong(mmsInfo[5]);
					contactName = mmsInfo[6];
					photoID = Long.parseLong(mmsInfo[7]);
					if(mmsInfo.length < 9){
						lookupKey = "";
					}else{
						lookupKey = mmsInfo[8];
					}
				}
			}catch(Exception ex){
				if (_debug) Log.e("NotificationActivity.setupMMSMessages() ERROR: " + ex.toString());
			}
    		_notificationViewFlipper.addNotification(new Notification(_context, messageAddress, messageBody, messageID, threadID, timeStamp, contactID, contactName, photoID, lookupKey, Constants.NOTIFICATION_TYPE_MMS));
		    //Display Status Bar Notification
		    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_MMS, true, contactName, messageAddress, messageBody, null, null, null);
		}
		if(loadAllNew){
			//Load all unread MMS messages.
			if(_preferences.getBoolean(Constants.MMS_DISPLAY_UNREAD_KEY, false)){
				new getAllUnreadMMSMessagesAsyncTask().execute();
		    }
		}
	}

	/**
	 * Get unread SMS messages in the background.
	 * 
	 * @author Camille Sévigny
	 */
	private class getAllUnreadSMSMessagesAsyncTask extends AsyncTask<String, Void, ArrayList<String>> {
	    
		/**
	     * Do this work in the background.
	     * 
	     * @param params - The contact's id.
	     */
	    protected ArrayList<String> doInBackground(String... params) {
			if (_debug) Log.v("NotificationActivity.getAllUnreadSMSMessagesAsyncTask.doInBackground()");
			if(_preferences.getString(Constants.SMS_LOADING_SETTING_KEY, "0").equals(Constants.SMS_READ_FROM_INTENT)){
				return getAllUnreadSMSMessages(Long.parseLong(params[0]), params[1]);
			}else{
				return getAllUnreadSMSMessages(0, null);
			}
	    }
	    
	    /**
	     * Set the image to the notification View.
	     * 
	     * @param result - The image of the contact.
	     */
	    protected void onPostExecute(ArrayList<String> smsArray) {
			if (_debug) Log.v("NotificationActivity.getAllUnreadSMSMessagesAsyncTask.onPostExecute()");		
			for(String smsArrayItem : smsArray){
				String[] smsInfo = smsArrayItem.split("\\|");
				String messageAddress = null;
				String messageBody = null;
				long messageID = 0;
				long threadID = 0;
				long contactID = 0;
				String contactName = null;
				long photoID = 0;
				String lookupKey = null;
				long timeStamp = 0;
				try{
					if(smsInfo.length < 5){
						if (_debug) Log.e("NotificationActivity.getAllUnreadSMSMessagesAsyncTask.onPostExecute() FATAL NOTIFICATION ERROR. smsInfo.length: " + smsInfo.length);
						return;
					}else if( smsInfo.length == 5){ 
						messageAddress = smsInfo[0];
						messageBody = smsInfo[1];
						messageID = Long.parseLong(smsInfo[2]);
						threadID = Long.parseLong(smsInfo[3]);
						timeStamp = Long.parseLong(smsInfo[4]);
					}else{ 
						messageAddress = smsInfo[0];
						messageBody = smsInfo[1];
						messageID = Long.parseLong(smsInfo[2]);
						threadID = Long.parseLong(smsInfo[3]);
						timeStamp = Long.parseLong(smsInfo[4]);
						contactID = Long.parseLong(smsInfo[5]);
						contactName = smsInfo[6];
						photoID = Long.parseLong(smsInfo[7]);
						if(smsInfo.length < 9){
							lookupKey = "";
						}else{
							lookupKey = smsInfo[8];
						}
					}
				}catch(Exception ex){
					if (_debug) Log.e("NotificationActivity.getAllUnreadSMSMessagesAsyncTask.onPostExecute() ERROR: " + ex.toString());
				}
	    		_notificationViewFlipper.addNotification(new Notification(_context, messageAddress, messageBody, messageID, threadID, timeStamp, contactID, contactName, photoID, lookupKey, Constants.NOTIFICATION_TYPE_SMS));
			}
	    }
	}

	/**
	 * Get all unread Messages and load them.
	 * 
	 * @param messageIDFilter - Long value of the currently incoming SMS message.
	 * @param messagebodyFilter - String value of the currently incoming SMS message.
	 */
	private ArrayList<String> getAllUnreadSMSMessages(long messageIDFilter, String messageBodyFilter){
		if (_debug) Log.v("NotificationActivity.getAllUnreadSMSMessages()" );
		Context context = getApplicationContext();
		ArrayList<String> smsArray = new ArrayList<String>();
		final String[] projection = new String[] { "_id", "thread_id", "address", "person", "date", "body"};
		final String selection = "read = 0";
		final String[] selectionArgs = null;
		final String sortOrder = null;
		Cursor cursor = null;
		boolean isFirst = true;
        try{
		    cursor = _context.getContentResolver().query(
		    		Uri.parse("content://sms/inbox"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
		    while (cursor.moveToNext()) { 
		    	long messageID = cursor.getLong(cursor.getColumnIndex("_id"));
		    	long threadID = cursor.getLong(cursor.getColumnIndex("thread_id"));
		    	String messageBody = cursor.getString(cursor.getColumnIndex("body"));
		    	String sentFromAddress = cursor.getString(cursor.getColumnIndex("address"));
		    	if(sentFromAddress.contains("@")){
	            	sentFromAddress = Common.removeEmailFormatting(sentFromAddress);
	            }
		    	long timeStamp = cursor.getLong(cursor.getColumnIndex("date"));
		    	if(messageIDFilter == 0 && messageBodyFilter == null){
		    		//Do not grab the first unread SMS message.
		    		if(!isFirst){
			    		String[] smsContactInfo = null;
			    		if(sentFromAddress.contains("@")){
				    		smsContactInfo = Common.getContactsInfoByEmail(context, sentFromAddress);
				    	}else{
				    		smsContactInfo = Common.getContactsInfoByPhoneNumber(context, sentFromAddress);
				    	}
			    		if(smsContactInfo == null){
							smsArray.add(sentFromAddress + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + threadID + "|" + timeStamp);
						}else{
							smsArray.add(sentFromAddress + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + threadID + "|" + timeStamp + "|" + smsContactInfo[0] + "|" + smsContactInfo[1] + "|" + smsContactInfo[2] + "|" + smsContactInfo[3]);
						}
		    		}
					isFirst = false;
		    	}else{
                    //Don't load the message that corresponds to the messageIDFilter or messageBodyFilter.
                    if(messageID != messageIDFilter && !messageBody.replace("\n", "<br/>").trim().equals(messageBodyFilter.replace("\n", "<br/>").trim())){
                        String[] smsContactInfo = null;
                        if(sentFromAddress.contains("@")){
                                smsContactInfo = Common.getContactsInfoByEmail(context, sentFromAddress);
                        }else{
                                smsContactInfo = Common.getContactsInfoByPhoneNumber(context, sentFromAddress);
                        }
                        if(smsContactInfo == null){
                                smsArray.add(sentFromAddress + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + threadID + "|" + timeStamp);
                        }else{
                                smsArray.add(sentFromAddress + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + threadID + "|" + timeStamp + "|" + smsContactInfo[0] + "|" + smsContactInfo[1] + "|" + smsContactInfo[2] + "|" + smsContactInfo[3]);
                        }
                    }
		    	}
		    }
		}catch(Exception ex){
			if (_debug) Log.e("NotificationActivity.getAllUnreadSMSMessages() ERROR: " + ex.toString());
		} finally {
    		cursor.close();
    	}
		return smsArray;
	}

	/**
	 * Get unread MMS messages in the background.
	 * 
	 * @author Camille Sévigny
	 */
	private class getAllUnreadMMSMessagesAsyncTask extends AsyncTask<Void, Void, ArrayList<String>> {
	    
		/**
	     * Do this work in the background.
	     * 
	     * @param params - The contact's id.
	     */
	    protected ArrayList<String> doInBackground(Void...params) {
			if (_debug) Log.v("NotificationActivity.getAllUnreadMMSMessagesAsyncTask.doInBackground()");
	    	return getAllUnreadMMSMessages();
	    }
	    
	    /**
	     * Set the image to the notification View.
	     * 
	     * @param result - The image of the contact.
	     */
	    protected void onPostExecute(ArrayList<String> mmsArray) {
			if (_debug) Log.v("NotificationActivity.getAllUnreadMMSMessagesAsyncTask.onPostExecute()");
			for(String mmsArrayItem : mmsArray){
				String[] mmsInfo = mmsArrayItem.split("\\|");
				String messageAddress = null;
				String messageBody = null;
				long messageID = 0;
				long threadID = 0;
				long contactID = 0;
				String contactName = null;
				long photoID = 0;
				String lookupKey = null;
				long timeStamp = 0;
				try{
					if(mmsInfo.length < 5){
						if (_debug) Log.e("NotificationActivity.getAllUnreadMMSMessagesAsyncTask.onPostExecute() FATAL NOTIFICATION ERROR. mmsInfo.length: " + mmsInfo.length);
						return;
					}else if( mmsInfo.length == 5){ 
						messageAddress = mmsInfo[0];
						messageBody = mmsInfo[1];
						messageID = Long.parseLong(mmsInfo[2]);
						threadID = Long.parseLong(mmsInfo[3]);
						//The timestamp is in seconds and not milliseconds. You must multiply by 1000. :)
						timeStamp = Long.parseLong(mmsInfo[4]) * 1000;
					}else{ 
						messageAddress = mmsInfo[0];
						messageBody = mmsInfo[1];
						messageID = Long.parseLong(mmsInfo[2]);
						threadID = Long.parseLong(mmsInfo[3]);
						//The timestamp is in seconds and not milliseconds. You must multiply by 1000. :)
						timeStamp = Long.parseLong(mmsInfo[4]) * 1000;
						contactID = Long.parseLong(mmsInfo[5]);
						contactName = mmsInfo[6];
						photoID = Long.parseLong(mmsInfo[7]);
						if(mmsInfo.length < 9){
							lookupKey = "";
						}else{
							lookupKey = mmsInfo[8];
						}
					}
				}catch(Exception ex){
					if (_debug) Log.e("NotificationActivity.getAllUnreadMMSMessagesAsyncTask.onPostExecute() ERROR: " + ex.toString());
				}
	    		_notificationViewFlipper.addNotification(new Notification(_context, messageAddress, messageBody, messageID, threadID, timeStamp, contactID, contactName, photoID, lookupKey, Constants.NOTIFICATION_TYPE_SMS));
			}
	    }
	}
	
	/**
	 * Get all unread Messages and load them.
	 * 
	 * @param messageIDFilter - Long value of the currently incoming SMS message.
	 * @param messagebodyFilter - String value of the currently incoming SMS message.
	 */
	private ArrayList<String> getAllUnreadMMSMessages(){
		if (_debug) Log.v("NotificationActivity.getAllUnreadMMSMessages()");
		Context context = getApplicationContext();
		ArrayList<String> mmsArray = new ArrayList<String>();
		final String[] projection = new String[] {"_id", "thread_id", "date"};
		final String selection = "read = 0";
		final String[] selectionArgs = null;
		final String sortOrder = "date DESC";
		Cursor cursor = null;
		boolean isFirst = true;
        try{
		    cursor = context.getContentResolver().query(
		    		Uri.parse("content://mms/inbox"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
	    	while (cursor.moveToNext()) {
	    		//Do not grab the first unread MMS message.
	    		if(!isFirst){
		    		String messageID = cursor.getString(cursor.getColumnIndex("_id"));
		    		String threadID = cursor.getString(cursor.getColumnIndex("thread_id"));
			    	String timeStamp = cursor.getString(cursor.getColumnIndex("date"));
			    	String sentFromAddress = Common.getMMSAddress(context, messageID);
		            if(sentFromAddress.contains("@")){
		            	sentFromAddress = Common.removeEmailFormatting(sentFromAddress);
		            }
			    	String messageBody = Common.getMMSText(context, messageID);
			    	String[] mmsContactInfo = null;
			    	if(sentFromAddress.contains("@")){
			    		mmsContactInfo = Common.getContactsInfoByEmail(context, sentFromAddress);
			    	}else{
			    		mmsContactInfo = Common.getContactsInfoByPhoneNumber(context, sentFromAddress);
			    	}
					if(mmsContactInfo == null){
						mmsArray.add(sentFromAddress + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + threadID + "|" + timeStamp);
					}else{
						mmsArray.add(sentFromAddress + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + threadID + "|" + timeStamp + "|" + mmsContactInfo[0] + "|" + mmsContactInfo[1] + "|" + mmsContactInfo[2]);
					}
		    	}
				isFirst = false;
	    	}
		}catch(Exception ex){
			if (_debug) Log.e("MMSReceiverService.getMMSMessages() ERROR: " + ex.toString());
		} finally {
    		cursor.close();
    	}
		return mmsArray;
	}
	
	/**
	 * Setup the missed calls notifications.
	 * 
	 * @param bundle - Activity bundle.
	 */
	private boolean setupMissedCalls(Bundle bundle){
		if (_debug) Log.v("NotificationActivity.setupMissedCalls()"); 
		ArrayList<String> missedCallsArray = bundle.getStringArrayList("missedCallsArrayList");
		int missedCallArraysize = missedCallsArray.size();
		for(int i=0; i<missedCallArraysize ; i++){
			String[] missedCallInfo = missedCallsArray.get(i).split("\\|");
			long callLogID = 0;
			String phoneNumber = null;
			long timeStamp = 0;
			long contactID = 0;
			String contactName = null;
			long photoID = 0;
			String lookupKey = null;
			try{
				if(missedCallInfo.length < 3){
					if (_debug) Log.e("NotificationActivity.setupMissedCalls() FATAL NOTIFICATION ERROR. missedCallInfo.length: " + missedCallInfo.length);
					return false;
				}else if( missedCallInfo.length == 3){
					callLogID = Long.parseLong(missedCallInfo[0]);
					phoneNumber = missedCallInfo[1];
					timeStamp = Long.parseLong(missedCallInfo[2]);
				}else{
					callLogID = Long.parseLong(missedCallInfo[0]);
					phoneNumber = missedCallInfo[1];
					timeStamp = Long.parseLong(missedCallInfo[2]);
					contactID = Long.parseLong(missedCallInfo[3]);
					contactName = missedCallInfo[4];
					photoID = Long.parseLong(missedCallInfo[5]);
					if(missedCallInfo.length < 7){
						lookupKey = "";
					}else{
						lookupKey = missedCallInfo[6];
					}
				}
			}catch(Exception ex){
				if (_debug) Log.e("NotificationActivity.setupMissedCalls() ERROR: " + ex.toString());
			}
			_notificationViewFlipper.addNotification(new Notification(_context, callLogID, phoneNumber, timeStamp, contactID, contactName, photoID, lookupKey, Constants.NOTIFICATION_TYPE_PHONE));		    
			//Display Status Bar Notification
		    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_PHONE, true, contactName, phoneNumber, null, null, null, null);
		}
	    return true;
	}
	
	/**
	 * Setup the Calendar Event notifications.
	 * 
	 * @param bundle - Activity bundle.
	 */
	private void setupCalendarEventNotifications(Bundle bundle){
		if (_debug) Log.v("NotificationActivity.setupCalendarEventNotifications()");  
		String calenderEventInfo[] = (String[])bundle.getStringArray("calenderEventInfo");
		String title = null;
		String messageBody = null;
		long eventStartTime = 0; 
		long eventEndTime = 0;
		boolean eventAllDay = false; 
		String calendarName = null;
		long calendarID = 0; 
		long eventID = 0;
		try{
			if(calenderEventInfo.length < 8){
				if (_debug) Log.e("NotificationActivity.setupCalendarEventNotifications() FATAL NOTIFICATION ERROR. calenderEventInfo.length: " + calenderEventInfo.length);
				return ;
			}else{
				title = calenderEventInfo[0];
				messageBody = calenderEventInfo[1];
				eventStartTime = Long.parseLong(calenderEventInfo[2]); 
				eventEndTime = Long.parseLong(calenderEventInfo[3]);
				eventAllDay = Boolean.parseBoolean(calenderEventInfo[4]); 
				calendarName = calenderEventInfo[5];
				calendarID = Long.parseLong(calenderEventInfo[6]); 
				eventID = Long.parseLong(calenderEventInfo[7]);
			}
		}catch(Exception ex){
			if (_debug) Log.e("NotificationActivity.setupCalendarEventNotifications() Error: " + ex.toString());  
			return;
		}
		_notificationViewFlipper.addNotification(new Notification(_context, title, messageBody, eventStartTime, eventEndTime, eventAllDay, calendarName, calendarID, eventID, Constants.NOTIFICATION_TYPE_CALENDAR));
		//Display Status Bar Notification
	    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_PHONE, true, null, null, messageBody, String.valueOf(calendarID), String.valueOf(eventStartTime), String.valueOf(eventEndTime));
	}
	
	/**
	 * Setup Activity's context menus.
	 * 
	 * @param contextMenu - The context menu item.
	 * @param notificationType - The notification type to customize what is shown.
	 */
	private void setupContextMenus(ContextMenu contextMenu, int notificationType){
		switch(notificationType){
			case Constants.NOTIFICATION_TYPE_PHONE:{
				MenuItem viewCalendarEventMenuItem = contextMenu.findItem(VIEW_CALENDAR_CONTEXT_MENU);
				viewCalendarEventMenuItem.setVisible(false);
		    	MenuItem addCalendarEventMenuItem = contextMenu.findItem(ADD_CALENDAR_EVENT_CONTEXT_MENU);
		    	addCalendarEventMenuItem.setVisible(false);
				MenuItem editCalendarEventMenuItem = contextMenu.findItem(EDIT_CALENDAR_EVENT_CONTEXT_MENU);
				editCalendarEventMenuItem.setVisible(false);
				MenuItem viewThreadMenuItem = contextMenu.findItem(VIEW_THREAD_CONTEXT_MENU);
				viewThreadMenuItem.setVisible(false);
				MenuItem messagingInboxMenuItem = contextMenu.findItem(MESSAGING_INBOX_CONTEXT_MENU);
				messagingInboxMenuItem.setVisible(false);
				break;
		    }
			case Constants.NOTIFICATION_TYPE_SMS:{
				MenuItem viewCalendarEventMenuItem = contextMenu.findItem(VIEW_CALENDAR_CONTEXT_MENU);
				viewCalendarEventMenuItem.setVisible(false);
		    	MenuItem addCalendarEventMenuItem = contextMenu.findItem(ADD_CALENDAR_EVENT_CONTEXT_MENU);
		    	addCalendarEventMenuItem.setVisible(false);
				MenuItem editCalendarEventMenuItem = contextMenu.findItem(EDIT_CALENDAR_EVENT_CONTEXT_MENU);
				editCalendarEventMenuItem.setVisible(false);
				MenuItem viewCallLogMenuItem = contextMenu.findItem(VIEW_CALL_LOG_CONTEXT_MENU);
				viewCallLogMenuItem.setVisible(false);
				break;
		    }
			case Constants.NOTIFICATION_TYPE_MMS:{
				MenuItem viewCalendarEventMenuItem = contextMenu.findItem(VIEW_CALENDAR_CONTEXT_MENU);
				viewCalendarEventMenuItem.setVisible(false);
		    	MenuItem addCalendarEventMenuItem = contextMenu.findItem(ADD_CALENDAR_EVENT_CONTEXT_MENU);
		    	addCalendarEventMenuItem.setVisible(false);
				MenuItem editCalendarEventMenuItem = contextMenu.findItem(EDIT_CALENDAR_EVENT_CONTEXT_MENU);
				editCalendarEventMenuItem.setVisible(false);
		    	MenuItem viewCallLogMenuItem = contextMenu.findItem(VIEW_CALL_LOG_CONTEXT_MENU);
				viewCallLogMenuItem.setVisible(false);
				break;
		    }
			case Constants.NOTIFICATION_TYPE_CALENDAR:{
				MenuItem addContactMenuItem = contextMenu.findItem(ADD_CONTACT_CONTEXT_MENU);
				addContactMenuItem.setVisible(false);
		    	MenuItem viewContactMenuItem = contextMenu.findItem(VIEW_CONTACT_CONTEXT_MENU);
		    	viewContactMenuItem.setVisible(false);
				MenuItem editContactMenuItem = contextMenu.findItem(EDIT_CONTACT_CONTEXT_MENU);
				editContactMenuItem.setVisible(false);
				MenuItem textContactMenuItem = contextMenu.findItem(TEXT_CONTACT_CONTEXT_MENU);
				textContactMenuItem.setVisible(false);
				MenuItem viewCallLogMenuItem = contextMenu.findItem(VIEW_CALL_LOG_CONTEXT_MENU);
				viewCallLogMenuItem.setVisible(false);
				MenuItem callMenuItem = contextMenu.findItem(CALL_CONTACT_CONTEXT_MENU);
				callMenuItem.setVisible(false);
				MenuItem viewThreadMenuItem = contextMenu.findItem(VIEW_THREAD_CONTEXT_MENU);
				viewThreadMenuItem.setVisible(false);
				MenuItem messagingInboxMenuItem = contextMenu.findItem(MESSAGING_INBOX_CONTEXT_MENU);
				messagingInboxMenuItem.setVisible(false);
				break;
		    }
			case Constants.NOTIFICATION_TYPE_GMAIL:{
				MenuItem viewCalendarEventMenuItem = contextMenu.findItem(VIEW_CALENDAR_CONTEXT_MENU);
				viewCalendarEventMenuItem.setVisible(false);
		    	MenuItem addCalendarEventMenuItem = contextMenu.findItem(ADD_CALENDAR_EVENT_CONTEXT_MENU);
		    	addCalendarEventMenuItem.setVisible(false);
				MenuItem editCalendarEventMenuItem = contextMenu.findItem(EDIT_CALENDAR_EVENT_CONTEXT_MENU);
				editCalendarEventMenuItem.setVisible(false);
		    	MenuItem viewCallLogMenuItem = contextMenu.findItem(VIEW_CALL_LOG_CONTEXT_MENU);
				viewCallLogMenuItem.setVisible(false);
				MenuItem viewThreadMenuItem = contextMenu.findItem(VIEW_THREAD_CONTEXT_MENU);
				viewThreadMenuItem.setVisible(false);
				MenuItem messagingInboxMenuItem = contextMenu.findItem(MESSAGING_INBOX_CONTEXT_MENU);
				messagingInboxMenuItem.setVisible(false);
				break;
		    }
		}
	}
	
	/**
	 * Set the InReplyScreen flag.
	 */
	private void setInReplyScreenFlag(){
		SharedPreferences.Editor editor = _preferences.edit();
		editor.putBoolean(Constants.USER_IN_MESSAGING_APP_KEY, false);
		editor.commit();
	}

}