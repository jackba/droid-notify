package apps.droidnotify;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
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
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
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
import apps.droidnotify.facebook.FacebookCommon;
import apps.droidnotify.preferences.MainPreferenceActivity;
import apps.droidnotify.receivers.ScreenManagementAlarmReceiver;
import apps.droidnotify.twitter.TwitterCommon;

/**
 * This is the main activity that runs the notifications.
 * 
 * @author Camille Sévigny
 */
public class NotificationActivity extends Activity {

	//================================================================================
    // Constants
    //================================================================================
	
	//Context Menu Constants
	private static final int MENU_ITEM_SETTINGS = R.id.app_settings;
	private static final int CONTACT_WRAPPER_LINEAR_LAYOUT = R.id.contact_wrapper_linear_layout;
	private static final int ADD_CONTACT_CONTEXT_MENU = R.id.add_contact_context_menu;	
	private static final int EDIT_CONTACT_CONTEXT_MENU = R.id.edit_contact_context_menu;
	private static final int VIEW_CONTACT_CONTEXT_MENU = R.id.view_contact_context_menu;	
	private static final int VIEW_CALL_LOG_CONTEXT_MENU = R.id.view_call_log_context_menu;
	private static final int CALL_CONTACT_CONTEXT_MENU = R.id.call_contact_context_menu;
	private static final int MESSAGING_INBOX_CONTEXT_MENU = R.id.messaging_inbox_context_menu;
	private static final int VIEW_THREAD_CONTEXT_MENU = R.id.view_thread_context_menu;
	private static final int TEXT_CONTACT_CONTEXT_MENU = R.id.text_contact_context_menu;
	private static final int ADD_CALENDAR_EVENT_CONTEXT_MENU = R.id.add_calendar_event_context_menu;
	private static final int EDIT_CALENDAR_EVENT_CONTEXT_MENU = R.id.edit_calendar_event_context_menu;
	private static final int VIEW_CALENDAR_CONTEXT_MENU = R.id.view_calendar_context_menu;
	private static final int VIEW_K9_INBOX_CONTEXT_MENU = R.id.view_k9_inbox_context_menu;
	private static final int OPEN_TWITTER_APP_CONTEXT_MENU = R.id.open_twitter_app_context_menu;
	private static final int OPEN_FACEBOOK_APP_CONTEXT_MENU = R.id.open_facebook_app_context_menu;
	private static final int RESCHEDULE_NOTIFICATION_CONTEXT_MENU = R.id.reschedule_notification_context_menu;
	private static final int SPEAK_NOTIFICATION_CONTEXT_MENU = R.id.speak_notification_context_menu;
	private static final int DISMISS_NOTIFICATION_CONTEXT_MENU = R.id.dismiss_notification_context_menu;

	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	private Context _context = null;
	private NotificationViewFlipper _notificationViewFlipper = null;
	private MotionEvent _downMotionEvent = null;
	private SharedPreferences _preferences = null;
	private PendingIntent _screenTimeoutPendingIntent = null;
	private TextToSpeech _tts = null;

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
					MenuItem addContactMenuItem = contextMenu.findItem(ADD_CONTACT_CONTEXT_MENU);
					addContactMenuItem.setVisible(false);
				}else{
					MenuItem editContactMenuItem = contextMenu.findItem(EDIT_CONTACT_CONTEXT_MENU);
					editContactMenuItem.setVisible(false);
					MenuItem viewContactMenuItem = contextMenu.findItem(VIEW_CONTACT_CONTEXT_MENU);
					viewContactMenuItem.setVisible(false);
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
					Log.e("NotificationActivity.onContextItemSelected() CALL_CONTACT_CONTEXT_MENU ERROR: " + ex.toString());
					Toast.makeText(_context, _context.getString(R.string.app_android_contacts_phone_number_chooser_error), Toast.LENGTH_LONG).show();
					return false;
				}
			}
			case MESSAGING_INBOX_CONTEXT_MENU:{
				if(Common.startMessagingAppViewInboxActivity(_context, this, Constants.MESSAGING_ACTIVITY)){
					return true;
				}else{
					return false;
				}
			}
			case VIEW_THREAD_CONTEXT_MENU:{
				if(Common.startMessagingAppViewThreadActivity(_context, this, notification.getSentFromAddress(), Constants.VIEW_SMS_THREAD_ACTIVITY)){
					return true;
				}else{
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
					Log.e("NotificationActivity.onContextItemSelected() TEXT_CONTACT_CONTEXT_MENU ERROR: " + ex.toString());
					Toast.makeText(_context, _context.getString(R.string.app_android_contacts_phone_number_chooser_error), Toast.LENGTH_LONG).show();
					return false;
				}
			}
			case ADD_CALENDAR_EVENT_CONTEXT_MENU:{
				return Common.startAddCalendarEventActivity(_context, this, Constants.ADD_CALENDAR_ACTIVITY);
			}
			case EDIT_CALENDAR_EVENT_CONTEXT_MENU:{
				return Common.startEditCalendarEventActivity(_context, this, notification.getCalendarEventID(), notification.getCalendarEventStartTime(), notification.getCalendarEventEndTime(), Constants.EDIT_CALENDAR_ACTIVITY);
			}
			case VIEW_CALENDAR_CONTEXT_MENU:{
				return Common.startViewCalendarActivity(_context, this, Constants.CALENDAR_ACTIVITY);
			}
			case VIEW_K9_INBOX_CONTEXT_MENU:{
				return Common.startK9EmailAppViewInboxActivity(_context, this, Constants.K9_VIEW_EMAIL_ACTIVITY);
			}
			case OPEN_TWITTER_APP_CONTEXT_MENU:{
				return TwitterCommon.startTwitterAppActivity(_context, this, Constants.TWITTER_OPEN_APP_ACTIVITY);
			}
			case OPEN_FACEBOOK_APP_CONTEXT_MENU:{
				return FacebookCommon.startFacebookAppActivity(_context, this, Constants.FACEBOOK_OPEN_APP_ACTIVITY);
			}
			case RESCHEDULE_NOTIFICATION_CONTEXT_MENU:{
				try{
					_notificationViewFlipper.rescheduleNotification();
					return true;
				}catch(Exception ex){
					Log.e("NotificationActivity.onContextItemSelected() RESCHEDULE_NOTIFICATION_CONTEXT_MENU ERROR: " + ex.toString());
					return false;
				}
			}
			case SPEAK_NOTIFICATION_CONTEXT_MENU:{
				try{
					speak();
					return true;
				}catch(Exception ex){
					Log.e("NotificationActivity.onContextItemSelected() SPEAK_NOTIFICATION_CONTEXT_MENU ERROR: " + ex.toString());
					return false;
				}
			}
			case DISMISS_NOTIFICATION_CONTEXT_MENU:{
				try{
					//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
					return true;
				}catch(Exception ex){
					Log.e("NotificationActivity.onContextItemSelected() DISMISS_NOTIFICATION_CONTEXT_MENU ERROR: " + ex.toString());
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
		if (_tts != null){
	    	_tts.shutdown();
	    }
	    Common.clearKeyguardLock();
		if(_preferences.getBoolean(Constants.CLEAR_STATUS_BAR_NOTIFICATIONS_ON_EXIT_KEY, false)){
			Common.clearAllNotifications(_context);
		}
	    Common.clearWakeLock();
	    cancelScreenTimeout();
	    //Finish the activity.
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
			case Constants.NOTIFICATION_TYPE_TWITTER:{
				if(_preferences.getString(Constants.TWITTER_DELETE_KEY, "0").equals(Constants.TWITTER_DELETE_ACTION_NOTHING)){
					//Remove the notification from the ViewFlipper
					deleteMessage();
				}else{
					if(_preferences.getBoolean(Constants.TWITTER_CONFIRM_DELETION_KEY, true)){
						//Confirm deletion of the message.
						showDialog(Constants.DIALOG_DELETE_MESSAGE);
					}else{
						//Remove the notification from the ViewFlipper.
						deleteMessage();
					}
				}
				break;
			}
			case Constants.NOTIFICATION_TYPE_FACEBOOK:{
				//if(_preferences.getString(Constants.FACEBOOK_DELETE_KEY, "0").equals(Constants.FACEBOOK_DELETE_ACTION_NOTHING)){
				//	//Remove the notification from the ViewFlipper
				//	deleteMessage();
				//}else{
				//	if(_preferences.getBoolean(Constants.FACEBOOK_CONFIRM_DELETION_KEY, true)){
				//		//Confirm deletion of the message.
				//		showDialog(Constants.DIALOG_DELETE_MESSAGE);
				//	}else{
				//		//Remove the notification from the ViewFlipper.
				//		deleteMessage();
				//	}
				//}
				break;
			}
			case Constants.NOTIFICATION_TYPE_K9:{
				if(_preferences.getString(Constants.K9_DELETE_KEY, "0").equals(Constants.K9_DELETE_ACTION_NOTHING)){
					//Remove the notification from the ViewFlipper
					deleteMessage();
				}else{
					if(_preferences.getBoolean(Constants.K9_CONFIRM_DELETION_KEY, true)){
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
	           	    	//Poke the screen timeout.
	           	    	setScreenTimeoutAlarm();
	           	    	return true;
					}else if (deltaX > 0){
						_notificationViewFlipper.showPrevious();
	           	    	//Poke the screen timeout.
	           	    	setScreenTimeoutAlarm();
	           	    	return true;
	               	}
		        }
	            break;
	        }
	    }
	    //Poke the screen timeout.
	    setScreenTimeoutAlarm();
	    return super.dispatchTouchEvent(motionEvent);
	}
	
	/**
	 * Speak the notification message using TTS.
	 */
	public void speak(){
		if (_debug) Log.v("NotificationActivity.speak()");
		if(_tts == null){
			setupTextToSpeech();
		}else{
			Notification activeNotification = _notificationViewFlipper.getActiveNotification();
			activeNotification.speak(_tts);
			activeNotification.cancelReminder();
		}
	}
	
	/**
	 * Sets the alarm that will clear the KeyguardLock & WakeLock.
	 */
	public void setScreenTimeoutAlarm(){
		if (_debug) Log.v("NotificationActivity.setScreenTimeoutAlarm()");
		long scheduledAlarmTime = System.currentTimeMillis() + (Long.parseLong(_preferences.getString(Constants.SCREEN_TIMEOUT_KEY, "300")) * 1000);
		AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
    	Intent intent = new Intent(_context, ScreenManagementAlarmReceiver.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    	_screenTimeoutPendingIntent = PendingIntent.getBroadcast(_context, 0, intent, 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP, scheduledAlarmTime, _screenTimeoutPendingIntent);
	}

	/**
	 * Stops the playback of the TTS message.
	 */
	public void stopTextToSpeechPlayback(){
		if (_debug) Log.v("NotificationActivity.stopTextToSpeechPlayback()");
	    if (_tts != null){
	    	_tts.stop();
	    }
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
		if (_debug) Log.v("NotificationActivity.onActivityResult() RequestCode: " + requestCode + " ResultCode: " + resultCode);
	    switch(requestCode) {
		    case Constants.ADD_CONTACT_ACTIVITY:{
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() ADD_CONTACT_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() ADD_CONTACT_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() ADD_CONTACT_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_contacts_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		    	Common.setInLinkedAppFlag(_context, false);
		        break;
		    }
		    case Constants.EDIT_CONTACT_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() EDIT_CONTACT_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() EDIT_CONTACT_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() EDIT_CONTACT_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_contacts_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		    	Common.setInLinkedAppFlag(_context, false);
		        break;
		    }
		    case Constants.VIEW_CONTACT_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_CONTACT_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_CONTACT_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_CONTACT_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_contacts_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		    	Common.setInLinkedAppFlag(_context, false);
		        break;
		    }
		    case Constants.SEND_SMS_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() SEND_SMS_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() SEND_SMS_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() SEND_SMS_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_messaging_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		    	Common.setInLinkedAppFlag(_context, false);
		        break;
		    }
		    case Constants.VIEW_SMS_MESSAGE_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_SMS_MESSAGE_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_SMS_MESSAGE_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_SMS_MESSAGE_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_messaging_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		    	Common.setInLinkedAppFlag(_context, false);
		        break;
		    }
		    case Constants.VIEW_SMS_THREAD_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_SMS_THREAD_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_SMS_THREAD_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_SMS_THREAD_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_messaging_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		    	Common.setInLinkedAppFlag(_context, false);
		        break;
		    }
		    case Constants.MESSAGING_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() MESSAGING_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() MESSAGING_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() MESSAGING_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_messaging_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		    	Common.setInLinkedAppFlag(_context, false);
		        break;
		    }
		    case Constants.SEND_SMS_QUICK_REPLY_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() SEND_SMS_QUICK_REPLY_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
		    		_notificationViewFlipper.removeActiveNotification(false);
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() SEND_SMS_QUICK_REPLY_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					//_notificationViewFlipper.removeActiveNotification(false);
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() SEND_SMS_QUICK_REPLY_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_messaging_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		    	Common.setInLinkedAppFlag(_context, false);
		        break;
		    }
		    case Constants.CALL_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() CALL_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() CALL_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() CALL_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_phone_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		    	Common.setInLinkedAppFlag(_context, false);
		        break;
		    }
		    case Constants.ADD_CALENDAR_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() ADD_CALENDAR_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() ADD_CALENDAR_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() ADD_CALENDAR_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_calendar_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		    	Common.setInLinkedAppFlag(_context, false);
		        break;
		    }
		    case Constants.EDIT_CALENDAR_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() EDIT_CALENDAR_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() EDIT_CALENDAR_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() EDIT_CALENDAR_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_calendar_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		    	Common.setInLinkedAppFlag(_context, false);
		        break;
		    }
		    case Constants.VIEW_CALENDAR_ACTIVITY:{ 
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_CALENDAR_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_CALENDAR_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_CALENDAR_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_calendar_unknown_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		    	Common.setInLinkedAppFlag(_context, false);
		        break;
		    }
		    case Constants.VIEW_CALL_LOG_ACTIVITY:{
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_CALL_LOG_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
		    		_notificationViewFlipper.removeActiveNotification(false);
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_CALL_LOG_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() VIEW_CALL_LOG_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_call_log_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		    	Common.setInLinkedAppFlag(_context, false);
		        break;
		    }
		    case Constants.CALENDAR_ACTIVITY:{
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() CALENDAR_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
		    		_notificationViewFlipper.removeActiveNotification(false);
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() CALENDAR_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() CALENDAR_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_android_calendar_app_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		    	Common.setInLinkedAppFlag(_context, false);
		        break;
		    }
		    case Constants.TWITTER_OPEN_APP_ACTIVITY:{
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() TWITTER_OPEN_APP_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
		    		_notificationViewFlipper.removeActiveNotification(false);
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() TWITTER_OPEN_APP_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() TWITTER_OPEN_APP_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.twitter_app_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		    	Common.setInLinkedAppFlag(_context, false);
		        break;
		    }
		    case Constants.FACEBOOK_OPEN_APP_ACTIVITY:{
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() FACEBOOK_OPEN_APP_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
		    		_notificationViewFlipper.removeActiveNotification(false);
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() FACEBOOK_OPEN_APP_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() FACEBOOK_OPEN_APP_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.facebook_app_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		    	Common.setInLinkedAppFlag(_context, false);
		        break;
		    }
		    case Constants.K9_VIEW_INBOX_ACTIVITY:{
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() K9_VIEW_INBOX_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
		    		_notificationViewFlipper.removeActiveNotification(false);
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() K9_VIEW_INBOX_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() K9_VIEW_INBOX_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_email_app_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		    	Common.setInLinkedAppFlag(_context, false);
		        break;
		    }
		    case Constants.K9_VIEW_EMAIL_ACTIVITY:{
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() K9_VIEW_EMAIL_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
		    		_notificationViewFlipper.removeActiveNotification(false);
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() K9_VIEW_EMAIL_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() K9_VIEW_EMAIL_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_email_app_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		    	Common.setInLinkedAppFlag(_context, false);
		        break;
		    }
		    case Constants.K9_SEND_EMAIL_ACTIVITY:{
		    	if (resultCode == RESULT_OK) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() K9_SEND_EMAIL_ACTIVITY: RESULT_OK");
		        	//Remove notification from ViewFlipper.
		    		_notificationViewFlipper.removeActiveNotification(false);
		    	}else if (resultCode == RESULT_CANCELED) {
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() K9_SEND_EMAIL_ACTIVITY: RESULT_CANCELED");
		    		//Remove notification from ViewFlipper.
					_notificationViewFlipper.removeActiveNotification(false);
		    	}else{
		    		if (_debug) Log.v("NotificationActivity.onActivityResult() K9_SEND_EMAIL_ACTIVITY: " + resultCode);
		        	Toast.makeText(_context, _context.getString(R.string.app_email_app_error) + " " + resultCode, Toast.LENGTH_LONG).show();
		    	}
		    	Common.setInLinkedAppFlag(_context, false);
		        break;
		    }
		    case Constants.TEXT_TO_SPEECH_ACTIVITY:{
		        if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
		            //Success, create the TTS instance.
		            _tts = new TextToSpeech(_context, ttsOnInitListener);
		        }else{
		            //Missing data, install it.
		            Intent installIntent = new Intent();
		            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
		            startActivity(installIntent);
		        }
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
	    Common.setInLinkedAppFlag(_context, false);
	    Common.acquireWakeLock(_context);
	    final Bundle extrasBundle = getIntent().getExtras();
	    int notificationType = extrasBundle.getInt("notificationType");
	    if (_debug) Log.v("NotificationActivity.onCreate() Notification Type: " + notificationType);
	    //Don't rotate the Activity when the screen rotates based on the user preferences.
	    if(!_preferences.getBoolean(Constants.LANDSCAPE_SCREEN_ENABLED_KEY, false)){
	    	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    }
	    //Get main window for this Activity.
	    Window mainWindow = getWindow();
    	//Set Background Blur Flags
	    if(_preferences.getBoolean(Constants.BLUR_SCREEN_BACKGROUND_ENABLED_KEY, false)){
	    	mainWindow.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	    }
	    //Set Background Dim Flags
	    if(_preferences.getBoolean(Constants.DIM_SCREEN_BACKGROUND_ENABLED_KEY, false)){
	    	mainWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); 
		    WindowManager.LayoutParams params = mainWindow.getAttributes(); 
		    int dimAmt = Integer.parseInt(_preferences.getString(Constants.DIM_SCREEN_BACKGROUND_AMOUNT_KEY, "50"));
		    params.dimAmount = dimAmt / 100f; 
		    mainWindow.setAttributes(params); 
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
		    	if(!setupMissedCalls(extrasBundle)){
					finishActivity();
				}
		    	break;
		    }
		    case Constants.NOTIFICATION_TYPE_SMS:{
			    if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_SMS");
			    if(!setupSMSMessages(extrasBundle, true)){
					finishActivity();
				}
		    	break;
		    }
		    case Constants.NOTIFICATION_TYPE_MMS:{
		    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_MMS");
		    	if(!setupMMSMessages(extrasBundle, true)){
					finishActivity();
				}
		    	break;
		    }
		    case Constants.NOTIFICATION_TYPE_CALENDAR:{
		    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_CALENDAR");
		    	if(!setupCalendarEventNotifications(extrasBundle)){
					finishActivity();
				}
		    	break;
			}
		    case Constants.NOTIFICATION_TYPE_GMAIL:{
		    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_GMAIL");

		    	break;
		    }
			case Constants.NOTIFICATION_TYPE_TWITTER:{
				if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_TWITTER");
				if(!setupTwitterMessages(extrasBundle)){
					finishActivity();
				}
				break;
			}
			case Constants.NOTIFICATION_TYPE_FACEBOOK:{
				if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_FACEBOOK");
				if(!setupFacebookMessages(extrasBundle)){
					finishActivity();
				}
				break;
		    }
			case Constants.NOTIFICATION_TYPE_K9:{
				if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_K9");
				if(!setupK9EmailNotifications(extrasBundle)){
					finishActivity();
				}
				break;
		    }
		    case Constants.NOTIFICATION_TYPE_RESCHEDULE_PHONE:{
		    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_PHONE");
		    	setupRescheduledNotification(extrasBundle);
		    	break;
		    }
		    case Constants.NOTIFICATION_TYPE_RESCHEDULE_SMS:{
			    if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_RESCHEDULE_SMS");
			    setupRescheduledNotification(extrasBundle);
		    	break;
		    }
		    case Constants.NOTIFICATION_TYPE_RESCHEDULE_MMS:{
		    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_RESCHEDULE_MMS");
		    	setupRescheduledNotification(extrasBundle);
		    	break;
		    }
		    case Constants.NOTIFICATION_TYPE_RESCHEDULE_CALENDAR:{
		    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_RESCHEDULE_CALENDAR");
		    	setupRescheduledNotification(extrasBundle);
		    	break;
			}
		    case Constants.NOTIFICATION_TYPE_RESCHEDULE_GMAIL:{
		    	if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_RESCHEDULE_GMAIL");
		    	setupRescheduledNotification(extrasBundle);
		    	break;
		    }
			case Constants.NOTIFICATION_TYPE_RESCHEDULE_TWITTER:{
				if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_RESCHEDULE_TWITTER");
				setupRescheduledNotification(extrasBundle);
				break;
			}
			case Constants.NOTIFICATION_TYPE_RESCHEDULE_FACEBOOK:{
				if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_RESCHEDULE_FACEBOOK");
				setupRescheduledNotification(extrasBundle);
				break;
		    }
			case Constants.NOTIFICATION_TYPE_RESCHEDULE_K9:{
				if (_debug) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_RESCHEDULE_K9");
				setupRescheduledNotification(extrasBundle);
				break;
		    }
	    }
	    Common.acquireKeyguardLock(_context);
	    setScreenTimeoutAlarm();
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
	    _debug = Log.getDebug();
	    if (_debug) Log.v("NotificationActivity.onResume()");
	    Common.acquireWakeLock(_context);
	    setScreenTimeoutAlarm();
	    super.onResume();
	}
	  
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
	@Override
	protected void onPause() {
	    if (_debug) Log.v("NotificationActivity.onPause()");
	    if (_tts != null){
	    	_tts.stop();
	    }
	    cancelScreenTimeout();
	    Common.clearWakeLock();
	    super.onPause();
	}
	  
	/**
	 * Activity was stopped due to a new Activity being started or other reason.
	 */
	@Override
	protected void onStop() {
	    super.onStop();
	    if (_debug) Log.v("NotificationActivity.onStop()");
	    if(Common.isUserInLinkedApp(_context)){
	    	//Do Nothing.
	    }else{
	    	if(_preferences.getBoolean(Constants.APPLICATION_CLOSE_WHEN_PUSHED_TO_BACKGROUND_KEY, false)){
	    		finishActivity();
	    	}
	    }
	}
	  
	/**
	 * Activity was stopped and closed out completely.
	 */
	@Override
	protected void onDestroy() {
	    if (_debug) Log.v("NotificationActivity.onDestroy()");
	    if (_tts != null){
	    	_tts.shutdown();
	    }
	    Common.clearKeyguardLock();
		if(_preferences.getBoolean(Constants.CLEAR_STATUS_BAR_NOTIFICATIONS_ON_EXIT_KEY, false)){
			Common.clearAllNotifications(_context);
		}
	    Common.clearWakeLock();
	    cancelScreenTimeout();
	    super.onDestroy();
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
		int notificationSubType = _notificationViewFlipper.getActiveNotification().getNotificationSubType();
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
				}else if(notificationType == Constants.NOTIFICATION_TYPE_TWITTER){
					if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE){
						builder.setMessage(_context.getString(R.string.delete_twitter_direct_message_dialog_text));
					}else if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_MENTION){
						
					}else if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_FOLLOWER_REQUEST){
						
					}					
				}else if(notificationType == Constants.NOTIFICATION_TYPE_FACEBOOK){
					if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION){
						builder.setMessage(_context.getString(R.string.delete_facebook_notification_dialog_text));
					}else if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_FRIEND_REQUEST){
						
					}else if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_MESSAGE){
						builder.setMessage(_context.getString(R.string.delete_facebook_message_dialog_text));
					}					
				}else if(notificationType == Constants.NOTIFICATION_TYPE_K9){
					builder.setMessage(_context.getString(R.string.delete_email_dialog_text));
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
	    //Resend/Reschedule incoming notification. Fix for !@#$# Home Key Pressed action. 
	    //This is needed when there is only a single notification and it was removed prior to this method being called.
	    if(_notificationViewFlipper.getTotalNotifications() == 0){
	    	Common.resendNotification(_context, intent);
	    }
	    Common.setInLinkedAppFlag(_context, false);
	    Common.acquireWakeLock(_context);
	    setIntent(intent);
	    final Bundle extrasBundle = getIntent().getExtras();
	    int notificationType = extrasBundle.getInt("notificationType");
	    switch(notificationType){
	    	case Constants.NOTIFICATION_TYPE_PHONE:{
		    	if (_debug) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_PHONE");
		    	setupMissedCalls(extrasBundle);
		    	break;
		    }
	    	case Constants.NOTIFICATION_TYPE_SMS:{
			    if (_debug) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_SMS");
			    setupSMSMessages(extrasBundle, false);
		    	break;
		    }
	    	case Constants.NOTIFICATION_TYPE_MMS:{
		    	if (_debug) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_MMS");
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

		    	break;
		    }
			case Constants.NOTIFICATION_TYPE_TWITTER:{
				if (_debug) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_TWITTER");
				setupTwitterMessages(extrasBundle);
				break;
			}
			case Constants.NOTIFICATION_TYPE_FACEBOOK:{
				if (_debug) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_FACEBOOK");
				setupFacebookMessages(extrasBundle);
				break;
			}
			case Constants.NOTIFICATION_TYPE_K9:{
				if (_debug) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_K9");
				setupK9EmailNotifications(extrasBundle);
				break;
		    }
	    	case Constants.NOTIFICATION_TYPE_RESCHEDULE_PHONE:{
		    	if (_debug) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_RESCHEDULE_PHONE");
		    	setupRescheduledNotification(extrasBundle);
		    	break;
		    }
		    case Constants.NOTIFICATION_TYPE_RESCHEDULE_SMS:{
			    if (_debug) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_RESCHEDULE_SMS");
			    setupRescheduledNotification(extrasBundle);
		    	break;
		    }
		    case Constants.NOTIFICATION_TYPE_RESCHEDULE_MMS:{
		    	if (_debug) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_RESCHEDULE_MMS");
		    	setupRescheduledNotification(extrasBundle);
		    	break;
		    }
		    case Constants.NOTIFICATION_TYPE_RESCHEDULE_CALENDAR:{
		    	if (_debug) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_RESCHEDULE_CALENDAR");
		    	setupRescheduledNotification(extrasBundle);
		    	break;
			}
		    case Constants.NOTIFICATION_TYPE_RESCHEDULE_GMAIL:{
		    	if (_debug) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_RESCHEDULE_GMAIL");
		    	setupRescheduledNotification(extrasBundle);
		    	break;
		    }
			case Constants.NOTIFICATION_TYPE_RESCHEDULE_TWITTER:{
				if (_debug) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_RESCHEDULE_TWITTER");
				setupRescheduledNotification(extrasBundle);
				break;
			}
			case Constants.NOTIFICATION_TYPE_RESCHEDULE_FACEBOOK:{
				if (_debug) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_RESCHEDULE_FACEBOOK");
				setupRescheduledNotification(extrasBundle);
				break;
		    }
			case Constants.NOTIFICATION_TYPE_RESCHEDULE_K9:{
				if (_debug) Log.v("NotificationActivity.onNewIntent() NOTIFICATION_TYPE_RESCHEDULE_K9");
				setupRescheduledNotification(extrasBundle);
				break;
		    }
	    }
	    Common.acquireKeyguardLock(_context);
	    setScreenTimeoutAlarm();
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
	 * Function to create a test notification of each type.
	 */
	private void createTestNotifications(){
		if (_debug) Log.v("NotificationActivity.createTextNotifications()");
		String sentFromAddress = "5555555555";
		String smsTestMessage = "SMS Test Message";
		String mmsTestMessage = "MMS Test Message";
		String calendarTestCalendar = "Test Calendar";
		String calendarTestEvent = "Calendar Event Test";
		String calendarTestEventBody = "This is a calendar event test.";
		String sentFromTwitter = "tweetest";
		String sentFromTwitterName = "Tweet User";
		String twitterTestMessage = "Twitter Test Direct Message";
		String twitterTestMention = "Twitter Test Mention";
		String twitterTestFollowerRequest = "Twitter Test Follower Request";
		String sentFromFacebook = "Facebooktest";
		String sentFromFacebookName = "Facebook User";
		String facebookTestNotification = "Facebook Test Notification";
		String facebookTestFriendRequest = "Facebook Test Friend Request";
		String facebookTestMessage = "Facebook Test Message";
		String sentFromEmail = "test@gmail.com";
		String emailTestMessage = "Email Test Message";
		NotificationViewFlipper notificationViewFlipper = _notificationViewFlipper;
		boolean notificationDisplayed = false;
		if(_preferences.getBoolean(Constants.SMS_NOTIFICATIONS_ENABLED_KEY, true)){
			notificationDisplayed = true;
			//Add SMS Message Notification.
			Notification smsNotification = new Notification(_context, sentFromAddress, smsTestMessage, System.currentTimeMillis(), Constants.NOTIFICATION_TYPE_SMS);
			notificationViewFlipper.addNotification(smsNotification);
			//Display Status Bar Notification
		    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_SMS, 0, true, null, sentFromAddress, smsTestMessage, null);
		}
		if(_preferences.getBoolean(Constants.MMS_NOTIFICATIONS_ENABLED_KEY, true)){
			notificationDisplayed = true;
			//Add SMS Message Notification.
			Notification smsNotification = new Notification(_context, sentFromAddress, mmsTestMessage, System.currentTimeMillis(), Constants.NOTIFICATION_TYPE_MMS);
			notificationViewFlipper.addNotification(smsNotification);
			//Display Status Bar Notification
		    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_MMS, 0, true, null, sentFromAddress, mmsTestMessage, null);
		}
	    if(_preferences.getBoolean(Constants.PHONE_NOTIFICATIONS_ENABLED_KEY, true)){
			notificationDisplayed = true;
			//Add Missed Call Notification.
			Notification missedCallNotification = new Notification(_context, 0, sentFromAddress, System.currentTimeMillis(), 0, "", 0, "", Constants.NOTIFICATION_TYPE_PHONE);
			notificationViewFlipper.addNotification(missedCallNotification);
			//Display Status Bar Notification
		    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_PHONE, 0, true, null, sentFromAddress, null, null);
	    }
	    if(_preferences.getBoolean(Constants.CALENDAR_NOTIFICATIONS_ENABLED_KEY, true)){
			notificationDisplayed = true;
		    //Add Calendar Event Notification.
			Notification calendarEventNotification = new Notification(_context, calendarTestEvent, calendarTestEventBody, System.currentTimeMillis(), System.currentTimeMillis() + (10 * 60 * 1000), false, calendarTestCalendar,  0, 0, Constants.NOTIFICATION_TYPE_CALENDAR);
			notificationViewFlipper.addNotification(calendarEventNotification);	
			//Display Status Bar Notification
		    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_CALENDAR, 0, true, null, null, calendarTestEvent, null);
	    }
	    if(_preferences.getBoolean(Constants.TWITTER_NOTIFICATIONS_ENABLED_KEY, true)){
	    	if(_preferences.getBoolean(Constants.TWITTER_DIRECT_MESSAGES_ENABLED_KEY, true)){
				notificationDisplayed = true;
				//Add Twitter Message Notification.
				Notification twitterNotification = new Notification(_context, sentFromTwitter, 0, twitterTestMessage, System.currentTimeMillis(), 0, sentFromTwitterName, 0, 0, null, null, null, null, Constants.NOTIFICATION_TYPE_TWITTER, Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE);
				notificationViewFlipper.addNotification(twitterNotification);
				//Display Status Bar Notification
			    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_TWITTER, Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE, true, null, sentFromTwitter, twitterTestMessage, null);
	    	}
	    	if(_preferences.getBoolean(Constants.TWITTER_MENTIONS_ENABLED_KEY, true)){
				notificationDisplayed = true;
				//Add Twitter Message Notification.
				Notification twitterNotification = new Notification(_context, sentFromTwitter, 0, twitterTestMention, System.currentTimeMillis(), 0, sentFromTwitterName, 0, 0, null, null, null, null, Constants.NOTIFICATION_TYPE_TWITTER, Constants.NOTIFICATION_TYPE_TWITTER_MENTION);
				notificationViewFlipper.addNotification(twitterNotification);
				//Display Status Bar Notification
			    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_TWITTER, Constants.NOTIFICATION_TYPE_TWITTER_MENTION, true, null, sentFromTwitter, twitterTestMention, null);
	    	}
	    	if(_preferences.getBoolean(Constants.TWITTER_FOLLOWER_REQUESTS_ENABLED_KEY, true)){
				notificationDisplayed = true;
				//Add Twitter Message Notification.
				Notification twitterNotification = new Notification(_context, sentFromTwitter, 0, twitterTestFollowerRequest, System.currentTimeMillis(), 0, sentFromTwitterName, 0, 0, null, null, null, null, Constants.NOTIFICATION_TYPE_TWITTER, Constants.NOTIFICATION_TYPE_TWITTER_FOLLOWER_REQUEST);
				notificationViewFlipper.addNotification(twitterNotification);
				//Display Status Bar Notification
			    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_TWITTER, Constants.NOTIFICATION_TYPE_TWITTER_FOLLOWER_REQUEST, true, null, sentFromTwitter, twitterTestFollowerRequest, null);
	    	}
	    }
	    if(_preferences.getBoolean(Constants.FACEBOOK_NOTIFICATIONS_ENABLED_KEY, true)){
	    	if(_preferences.getBoolean(Constants.FACEBOOK_USER_NOTIFICATIONS_ENABLED_KEY, true)){
				notificationDisplayed = true;
				//Add Facebook Message Notification.
				Notification facebookNotification = new Notification(_context, sentFromFacebook, 0, facebookTestNotification, System.currentTimeMillis(), 0, sentFromFacebookName, 0, 0, null, null, null, null, Constants.NOTIFICATION_TYPE_FACEBOOK, Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION);
				notificationViewFlipper.addNotification(facebookNotification);
				//Display Status Bar Notification
			    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_FACEBOOK, Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION, true, null, sentFromFacebook, facebookTestNotification, null);
	    	}
		    if(_preferences.getBoolean(Constants.FACEBOOK_FRIEND_REQUESTS_ENABLED_KEY, true)){
		    	notificationDisplayed = true;
				//Add Facebook Message Notification.
				Notification facebookNotification = new Notification(_context, sentFromFacebook, 0, facebookTestFriendRequest, System.currentTimeMillis(), 0, sentFromFacebookName, 0, 0, null, null, null, null, Constants.NOTIFICATION_TYPE_FACEBOOK, Constants.NOTIFICATION_TYPE_FACEBOOK_FRIEND_REQUEST);
				notificationViewFlipper.addNotification(facebookNotification);
				//Display Status Bar Notification
			    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_FACEBOOK, Constants.NOTIFICATION_TYPE_FACEBOOK_FRIEND_REQUEST, true, null, sentFromFacebook, facebookTestFriendRequest, null);
	    	}
		    if(_preferences.getBoolean(Constants.FACEBOOK_MESSAGES_ENABLED_KEY, true)){
		    	notificationDisplayed = true;
				//Add Facebook Message Notification.
				Notification facebookNotification = new Notification(_context, sentFromFacebook, 0, facebookTestMessage, System.currentTimeMillis(), 0, sentFromFacebookName, 0, 0, null, null, null, null, Constants.NOTIFICATION_TYPE_FACEBOOK, Constants.NOTIFICATION_TYPE_FACEBOOK_MESSAGE);
				notificationViewFlipper.addNotification(facebookNotification);
				//Display Status Bar Notification
			    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_FACEBOOK, Constants.NOTIFICATION_TYPE_FACEBOOK_FRIEND_REQUEST, true, null, sentFromFacebook, facebookTestMessage, null);
	    	}
	    }
	    if(_preferences.getBoolean(Constants.K9_NOTIFICATIONS_ENABLED_KEY, true)){
			notificationDisplayed = true;
			//Add K9 Message Notification.
			Notification k9Notification = new Notification(_context, sentFromEmail, emailTestMessage, System.currentTimeMillis(), Constants.NOTIFICATION_TYPE_K9);
			notificationViewFlipper.addNotification(k9Notification);
			//Display Status Bar Notification
		    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_K9, 0, true, null, sentFromEmail, emailTestMessage, null);
	    }
	    if(!notificationDisplayed){
	    	Toast.makeText(_context, R.string.test_notifications_disabled_message, Toast.LENGTH_LONG);
	    }
	}
	
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
				Log.e("NotificationActivity.getPhoneNumbers() ERROR: " + ex.toString());
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
	private boolean setupSMSMessages(Bundle bundle, boolean loadAllNew) {
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
					Log.e("NotificationActivity.setupSMSMessages() FATAL NOTIFICATION ERROR. smsInfo.length: " + smsInfo.length);
					return false;
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
				Log.e("NotificationActivity.setupSMSMessages() ERROR: " + ex.toString());
				return false;
			}
    		_notificationViewFlipper.addNotification(new Notification(_context, messageAddress, messageBody, messageID, threadID, timeStamp, contactID, contactName, photoID, lookupKey, Constants.NOTIFICATION_TYPE_SMS));
		    //Display Status Bar Notification
		    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_SMS, 0, true, contactName, messageAddress, messageBody, null);
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
		return true;
	}
	
	/**
	 * Get new MMS messages.
	 *
	 * @param bundle - Activity bundle.
	 */
	private boolean setupMMSMessages(Bundle bundle, boolean loadAllNew) {
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
					Log.e("NotificationActivity.setupMMSMessages() FATAL NOTIFICATION ERROR. mmsInfo.length: " + mmsInfo.length);
					return false;
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
				Log.e("NotificationActivity.setupMMSMessages() ERROR: " + ex.toString());
				return false;
			}
    		_notificationViewFlipper.addNotification(new Notification(_context, messageAddress, messageBody, messageID, threadID, timeStamp, contactID, contactName, photoID, lookupKey, Constants.NOTIFICATION_TYPE_MMS));
		    //Display Status Bar Notification
		    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_MMS, 0, true, contactName, messageAddress, messageBody, null);
		}
		if(loadAllNew){
			//Load all unread MMS messages.
			if(_preferences.getBoolean(Constants.MMS_DISPLAY_UNREAD_KEY, false)){
				new getAllUnreadMMSMessagesAsyncTask().execute();
		    }
		}
		return true;
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
						Log.e("NotificationActivity.getAllUnreadSMSMessagesAsyncTask.onPostExecute() FATAL NOTIFICATION ERROR. smsInfo.length: " + smsInfo.length);
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
					Log.e("NotificationActivity.getAllUnreadSMSMessagesAsyncTask.onPostExecute() ERROR: " + ex.toString());
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
			Log.e("NotificationActivity.getAllUnreadSMSMessages() ERROR: " + ex.toString());
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
						Log.e("NotificationActivity.getAllUnreadMMSMessagesAsyncTask.onPostExecute() FATAL NOTIFICATION ERROR. mmsInfo.length: " + mmsInfo.length);
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
					Log.e("NotificationActivity.getAllUnreadMMSMessagesAsyncTask.onPostExecute() ERROR: " + ex.toString());
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
			Log.e("MMSReceiverService.getMMSMessages() ERROR: " + ex.toString());
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
				int missedCallInfoSize = missedCallInfo.length;
				if(missedCallInfoSize < 3){
					Log.e("NotificationActivity.setupMissedCalls() FATAL NOTIFICATION ERROR. missedCallInfo.length: " + missedCallInfoSize);
					return false;
				}else if( missedCallInfoSize == 3){
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
					if(missedCallInfoSize < 7){
						lookupKey = "";
					}else{
						lookupKey = missedCallInfo[6];
					}
				}
			}catch(Exception ex){
				Log.e("NotificationActivity.setupMissedCalls() ERROR: " + ex.toString()); 
				return false;
			}
			_notificationViewFlipper.addNotification(new Notification(_context, callLogID, phoneNumber, timeStamp, contactID, contactName, photoID, lookupKey, Constants.NOTIFICATION_TYPE_PHONE));		    
			//Display Status Bar Notification
		    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_PHONE, 0, true, contactName, phoneNumber, null, null);
		}
	    return true;
	}
	
	/**
	 * Setup the Calendar Event notifications.
	 * 
	 * @param bundle - Activity bundle.
	 * 
	 * @return boolean - Returns true if the notification did not have an error.
	 */
	private boolean setupCalendarEventNotifications(Bundle bundle){
		if (_debug) Log.v("NotificationActivity.setupCalendarEventNotifications()");  
		String calenderEventInfo[] = (String[]) bundle.getStringArray("calenderEventInfo");
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
				Log.e("NotificationActivity.setupCalendarEventNotifications() FATAL NOTIFICATION ERROR. calenderEventInfo.length: " + calenderEventInfo.length);
				return false;
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
			Log.e("NotificationActivity.setupCalendarEventNotifications() Error: " + ex.toString());  
			return false;
		}
		_notificationViewFlipper.addNotification(new Notification(_context, title, messageBody, eventStartTime, eventEndTime, eventAllDay, calendarName, calendarID, eventID, Constants.NOTIFICATION_TYPE_CALENDAR));
		//Display Status Bar Notification
	    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_CALENDAR, 0, true, null, null, title, null);
	    return true;
	}
	
	/**
	 * Setup the K-9 Email notifications.
	 * 
	 * @param bundle - Activity bundle.
	 * 
	 * @return boolean - Returns true if the notification did not have an error.
	 */
	private boolean setupK9EmailNotifications(Bundle bundle){
		if (_debug) Log.v("NotificationActivity.setupK9EmailNotifications()");  
		ArrayList<String> k9Array = bundle.getStringArrayList("k9ArrayList");
		int k9Arraysize = k9Array.size();
		for(int i=0; i<k9Arraysize ; i++){
			String[] k9Info = k9Array.get(i).split("\\|");
			String sentFromAddress = null;
			String messageBody = null;
			long messageID = 0;
			long contactID = 0;
			String contactName = null;
			long photoID = 0;
			String lookupKey = null;
			long timeStamp = 0;
			String k9EmailUri = null;
			String k9EmailDelUri = null;
			try{
				int k9InfoSize = k9Info.length;
				if(k9InfoSize < 6){
					Log.e("NotificationActivity.setupK9EmailNotifications() FATAL NOTIFICATION ERROR. k9Info.length: " + k9InfoSize);
					return false;
				}else if(k9InfoSize == 6){
					sentFromAddress = k9Info[0];
					messageBody = k9Info[1];
					messageID = Long.parseLong(k9Info[2]);
					timeStamp = Long.parseLong(k9Info[3]);
					k9EmailUri = k9Info[4];
					k9EmailDelUri = k9Info[5];
				}else{
					sentFromAddress = k9Info[0];
					messageBody = k9Info[1];
					messageID = Long.parseLong(k9Info[2]);
					timeStamp = Long.parseLong(k9Info[3]);
					k9EmailUri = k9Info[4];
					k9EmailDelUri = k9Info[5];
					contactID = Long.parseLong(k9Info[6]);
					contactName = k9Info[7];
					photoID = Long.parseLong(k9Info[8]);
					if(k9InfoSize < 10){
						lookupKey = "";
					}else{
						lookupKey = k9Info[9];
					}
				}
			}catch(Exception ex){
				Log.e("NotificationActivity.setupK9EmailNotifications() ERROR: " + ex.toString()); 
				return false;
			}
			_notificationViewFlipper.addNotification(new Notification(_context, sentFromAddress, 0, messageBody, timeStamp, contactID, contactName, photoID, messageID, null, lookupKey, k9EmailUri, k9EmailDelUri, Constants.NOTIFICATION_TYPE_K9, 0));		    
			//Display Status Bar Notification
		    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_K9, 0, true, contactName, sentFromAddress, messageBody, k9EmailUri);
		}
	    return true;
	}
	
	/**
	 * Setup the Twitter notifications.
	 * 
	 * @param bundle - Activity bundle.
	 * 
	 * @return boolean - Returns true if the notification did not have an error.
	 */
	private boolean setupTwitterMessages(Bundle bundle){
		if (_debug) Log.v("NotificationActivity.setupTwitterMessages()");  
		ArrayList<String> twitterArray = bundle.getStringArrayList("twitterArrayList");
		int twitterArraysize = twitterArray.size(); 
		for(int i=0; i<twitterArraysize ; i++){
			String[] twitterInfo = twitterArray.get(i).split("\\|");
			String sentFromAddress = null;
			long sentFromID = 0;
			String messageBody = null;
			long messageID = 0;
			long timeStamp = 0;
			long contactID = 0;
			String contactName = null;
			long photoID = 0;
			String lookupKey = null;
			int notificationSubType = 0;
			try{
				int twitterInfoSize = twitterInfo.length;
				if(twitterInfoSize < 4){
					Log.e("NotificationActivity.setupTwitterMessages() FATAL NOTIFICATION ERROR. twitterInfoSize.length: " + twitterInfoSize);
					return false;
				}else if(twitterInfoSize == 4){
					notificationSubType = Integer.parseInt(twitterInfo[0]);
					sentFromAddress = twitterInfo[1];
					sentFromID = Long.parseLong(twitterInfo[2]);
					messageBody = twitterInfo[3];
					messageID = Long.parseLong(twitterInfo[4]);
					timeStamp = Long.parseLong(twitterInfo[5]);
				}else{
					notificationSubType = Integer.parseInt(twitterInfo[0]);
					sentFromAddress = twitterInfo[1];
					sentFromID = Long.parseLong(twitterInfo[2]);
					messageBody = twitterInfo[3];
					messageID = Long.parseLong(twitterInfo[4]);
					timeStamp = Long.parseLong(twitterInfo[5]);
					contactID = Long.parseLong(twitterInfo[6]);
					contactName = twitterInfo[7];
					photoID = Long.parseLong(twitterInfo[8]);
					if(twitterInfoSize < 10){
						lookupKey = "";
					}else{
						lookupKey = twitterInfo[9];
					}
				}
			}catch(Exception ex){
				Log.e("NotificationActivity.setupTwitterMessages() ERROR: " + ex.toString()); 
				return false;
			}
			_notificationViewFlipper.addNotification(new Notification(_context, sentFromAddress, sentFromID, messageBody, timeStamp, contactID, contactName, photoID, messageID, null, lookupKey, null, null, Constants.NOTIFICATION_TYPE_TWITTER, notificationSubType));		    
			//Display Status Bar Notification
		    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_TWITTER, notificationSubType, true, contactName, sentFromAddress, messageBody, null);
		}
	    return true;
	}
	
	/**
	 * Setup the Facebook notifications.
	 * 
	 * @param bundle - Activity bundle.
	 * 
	 * @return boolean - Returns true if the notification did not have an error.
	 */
	private boolean setupFacebookMessages(Bundle bundle){
		if (_debug) Log.v("NotificationActivity.setupFacebookMessages()");  
		ArrayList<String> facebookArray = bundle.getStringArrayList("facebookArrayList");
		int facebookArraysize = facebookArray.size(); 
		for(int i=0; i<facebookArraysize ; i++){
			String[] facebookInfo = facebookArray.get(i).split("\\|");
			String sentFromAddress = null;
			long sentFromID = 0;
			String messageBody = null;
			String messageStringID = null;
			long timeStamp = 0;
			long contactID = 0;
			String contactName = null;
			long photoID = 0;
			String lookupKey = null;
			int notificationSubType = 0;
			try{
				int facebookInfoSize = facebookInfo.length;
				if(facebookInfoSize < 4){
					Log.e("NotificationActivity.setupFacebookMessages() FATAL NOTIFICATION ERROR. facebookInfoSize.length: " + facebookInfoSize);
					return false;
				}else if(facebookInfoSize == 4){
					notificationSubType = Integer.parseInt(facebookInfo[0]);
					sentFromAddress = facebookInfo[1];
					sentFromID = Long.parseLong(facebookInfo[2]);
					messageBody = facebookInfo[3];
					messageStringID = facebookInfo[4];
					timeStamp = Long.parseLong(facebookInfo[5]);
				}else{
					notificationSubType = Integer.parseInt(facebookInfo[0]);
					sentFromAddress = facebookInfo[1];
					sentFromID = Long.parseLong(facebookInfo[2]);
					messageBody = facebookInfo[3];
					messageStringID = facebookInfo[4];
					timeStamp = Long.parseLong(facebookInfo[5]);
					contactID = Long.parseLong(facebookInfo[6]);
					contactName = facebookInfo[7];
					photoID = Long.parseLong(facebookInfo[8]);
					if(facebookInfoSize < 10){
						lookupKey = "";
					}else{
						lookupKey = facebookInfo[9];
					}
				}
			}catch(Exception ex){
				Log.e("NotificationActivity.setupFacebookMessages() ERROR: " + ex.toString()); 
				return false;
			}
			_notificationViewFlipper.addNotification(new Notification(_context, sentFromAddress, sentFromID, messageBody, timeStamp, contactID, contactName, photoID, 0, messageStringID, lookupKey, null, null, Constants.NOTIFICATION_TYPE_FACEBOOK, notificationSubType));		    
			//Display Status Bar Notification
		    Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_FACEBOOK, notificationSubType, true, contactName, sentFromAddress, messageBody, null);
		}
	    return true;
	}
	
	/**
	 * Setup a rescheduled notification.
	 * 
	 * @param bundle - Activity bundle.
	 */
	private void setupRescheduledNotification(Bundle bundle){
		if (_debug) Log.v("NotificationActivity.setupRescheduledNotification()");
		String[] rescheduleNotificationInfo = bundle.getStringArray("rescheduleNotificationInfo");
		int rescheduleNumber = bundle.getInt("rescheduleNumber");
		int notificationType = bundle.getInt("notificationType") - 100;
		//========================================================
		//String[] Values:
		//[0]-notificationType
		//[1]-SentFromAddress
		//[2]-MessageBody
		//[3]-TimeStamp
		//[4]-ThreadID
		//[5]-ContactID
		//[6]-ContactName
		//[7]-MessageID
		//[8]-Title
		//[9]-CalendarID
		//[10]-CalendarEventID
		//[11]-CalendarEventStartTime
		//[12]-CalendarEventEndTime
		//[13]-AllDay
		//[14]-CallLogID
		//[15]-K9EmailUri
		//[16]-K9EmailDelUri
		//[17]-LookupKey
		//[18]-PhotoID
		//[19]-NotificationSubType
		//[20]-MessageStringID
		//========================================================
		//int notificationType = Integer.parseInt(rescheduleNotificationInfo[0]) - 100;
		String sentFromAddress = rescheduleNotificationInfo[1];
		String messageBody = rescheduleNotificationInfo[2];
		long timeStamp = Long.parseLong(rescheduleNotificationInfo[3]);
		long threadID = Long.parseLong(rescheduleNotificationInfo[4]);
		long contactID = Long.parseLong(rescheduleNotificationInfo[5]);
		String contactName = rescheduleNotificationInfo[6];
		long messageID = Long.parseLong(rescheduleNotificationInfo[7]);
		String title = rescheduleNotificationInfo[8];
		long calendarID = Long.parseLong(rescheduleNotificationInfo[9]);
		long calendarEventID = Long.parseLong(rescheduleNotificationInfo[10]);
		long calendarEventStartTime = Long.parseLong(rescheduleNotificationInfo[11]);
		long calendarEventEndTime = Long.parseLong(rescheduleNotificationInfo[12]);
		boolean allDay = false;
		if(rescheduleNotificationInfo[13].equals("1")){
			allDay = true;
		}
		long callLogID = Long.parseLong(rescheduleNotificationInfo[14]);
		String k9EmailUri = rescheduleNotificationInfo[15];
		String k9EmailDelUri = rescheduleNotificationInfo[16];
		String lookupKey = rescheduleNotificationInfo[17];
		long photoID = Long.parseLong(rescheduleNotificationInfo[18]);
		int notificationSubType = Integer.parseInt(rescheduleNotificationInfo[19]);
		String messageStringID = rescheduleNotificationInfo[20];
		Notification rescheduleNotification = new Notification(_context, sentFromAddress, messageBody, timeStamp, threadID, contactID, contactName, photoID, messageID, messageStringID, title, calendarID, calendarEventID, calendarEventStartTime, calendarEventEndTime, allDay, callLogID,  lookupKey, k9EmailUri, k9EmailDelUri, rescheduleNumber, notificationType, notificationSubType);
		_notificationViewFlipper.addNotification(rescheduleNotification);
		//Display Status Bar Notification
	    switch(notificationType){
	    	case Constants.NOTIFICATION_TYPE_PHONE:{
	    		Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_PHONE, 0, true, contactName, sentFromAddress, null, null);
		    	break;
		    }
	    	case Constants.NOTIFICATION_TYPE_SMS:{
	    		Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_SMS, 0, true, contactName, sentFromAddress, messageBody, null);
		    	break;
		    }
	    	case Constants.NOTIFICATION_TYPE_MMS:{
	    		Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_SMS, 0, true, contactName, sentFromAddress, messageBody, null);
		    	break;
		    }
	    	case Constants.NOTIFICATION_TYPE_CALENDAR:{
	    		Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_CALENDAR, 0, true, null, null, title, null);
		    	break;
		    }
	    	case Constants.NOTIFICATION_TYPE_GMAIL:{
	    		Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_GMAIL, 0, true, contactName, sentFromAddress, messageBody, null);
		    	break;
		    }
			case Constants.NOTIFICATION_TYPE_TWITTER:{
				Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_TWITTER, notificationSubType, true, contactName, sentFromAddress, messageBody, null);
				break;
			}
			case Constants.NOTIFICATION_TYPE_FACEBOOK:{
				Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_FACEBOOK, notificationSubType, true, contactName, sentFromAddress, messageBody, null);
				break;
			}
			case Constants.NOTIFICATION_TYPE_K9:{
				Common.setStatusBarNotification(_context, Constants.NOTIFICATION_TYPE_K9, 0, true, contactName, sentFromAddress, messageBody, k9EmailUri);
				break;
		    }
	    }
	}
	
	/**
	 * Setup Activity's context menus.
	 * 
	 * @param contextMenu - The context menu item.
	 * @param notificationType - The notification type to customize what is shown.
	 */
	private void setupContextMenus(ContextMenu contextMenu, int notificationType){
		if (_debug) Log.v("NotificationActivity.setupContextMenus()");
		switch(notificationType){
			case Constants.NOTIFICATION_TYPE_PHONE:{
		    	MenuItem addCalendarEventMenuItem = contextMenu.findItem(ADD_CALENDAR_EVENT_CONTEXT_MENU);
		    	addCalendarEventMenuItem.setVisible(false);
				MenuItem editCalendarEventMenuItem = contextMenu.findItem(EDIT_CALENDAR_EVENT_CONTEXT_MENU);
				editCalendarEventMenuItem.setVisible(false);
				MenuItem viewCalendarEventMenuItem = contextMenu.findItem(VIEW_CALENDAR_CONTEXT_MENU);
				viewCalendarEventMenuItem.setVisible(false);
				MenuItem messagingInboxMenuItem = contextMenu.findItem(MESSAGING_INBOX_CONTEXT_MENU);
				messagingInboxMenuItem.setVisible(false);
				MenuItem viewThreadMenuItem = contextMenu.findItem(VIEW_THREAD_CONTEXT_MENU);
				viewThreadMenuItem.setVisible(false);
				MenuItem viewK9EmailInboxMenuItem = contextMenu.findItem(VIEW_K9_INBOX_CONTEXT_MENU);
				viewK9EmailInboxMenuItem.setVisible(false);
				MenuItem openTwitterAppMenuItem = contextMenu.findItem(OPEN_TWITTER_APP_CONTEXT_MENU);
				openTwitterAppMenuItem.setVisible(false);
				MenuItem openFacebookAppMenuItem = contextMenu.findItem(OPEN_FACEBOOK_APP_CONTEXT_MENU);
				openFacebookAppMenuItem.setVisible(false);
				break;
		    }
			case Constants.NOTIFICATION_TYPE_SMS:{
		    	MenuItem addCalendarEventMenuItem = contextMenu.findItem(ADD_CALENDAR_EVENT_CONTEXT_MENU);
		    	addCalendarEventMenuItem.setVisible(false);
				MenuItem editCalendarEventMenuItem = contextMenu.findItem(EDIT_CALENDAR_EVENT_CONTEXT_MENU);
				editCalendarEventMenuItem.setVisible(false);
				MenuItem viewCalendarEventMenuItem = contextMenu.findItem(VIEW_CALENDAR_CONTEXT_MENU);
				viewCalendarEventMenuItem.setVisible(false);
				MenuItem viewCallLogMenuItem = contextMenu.findItem(VIEW_CALL_LOG_CONTEXT_MENU);
				viewCallLogMenuItem.setVisible(false);
				MenuItem viewK9EmailInboxMenuItem = contextMenu.findItem(VIEW_K9_INBOX_CONTEXT_MENU);
				viewK9EmailInboxMenuItem.setVisible(false);
				MenuItem openTwitterAppMenuItem = contextMenu.findItem(OPEN_TWITTER_APP_CONTEXT_MENU);
				openTwitterAppMenuItem.setVisible(false);
				MenuItem openFacebookAppMenuItem = contextMenu.findItem(OPEN_FACEBOOK_APP_CONTEXT_MENU);
				openFacebookAppMenuItem.setVisible(false);
				break;
		    }
			case Constants.NOTIFICATION_TYPE_MMS:{
		    	MenuItem addCalendarEventMenuItem = contextMenu.findItem(ADD_CALENDAR_EVENT_CONTEXT_MENU);
		    	addCalendarEventMenuItem.setVisible(false);
				MenuItem editCalendarEventMenuItem = contextMenu.findItem(EDIT_CALENDAR_EVENT_CONTEXT_MENU);
				editCalendarEventMenuItem.setVisible(false);
				MenuItem viewCalendarEventMenuItem = contextMenu.findItem(VIEW_CALENDAR_CONTEXT_MENU);
				viewCalendarEventMenuItem.setVisible(false);
		    	MenuItem viewCallLogMenuItem = contextMenu.findItem(VIEW_CALL_LOG_CONTEXT_MENU);
				viewCallLogMenuItem.setVisible(false);
				MenuItem viewK9EmailInboxMenuItem = contextMenu.findItem(VIEW_K9_INBOX_CONTEXT_MENU);
				viewK9EmailInboxMenuItem.setVisible(false);
				MenuItem openTwitterAppMenuItem = contextMenu.findItem(OPEN_TWITTER_APP_CONTEXT_MENU);
				openTwitterAppMenuItem.setVisible(false);
				MenuItem openFacebookAppMenuItem = contextMenu.findItem(OPEN_FACEBOOK_APP_CONTEXT_MENU);
				openFacebookAppMenuItem.setVisible(false);
				break;
		    }
			case Constants.NOTIFICATION_TYPE_CALENDAR:{
				MenuItem addContactMenuItem = contextMenu.findItem(ADD_CONTACT_CONTEXT_MENU);
				addContactMenuItem.setVisible(false);
				MenuItem editContactMenuItem = contextMenu.findItem(EDIT_CONTACT_CONTEXT_MENU);
				editContactMenuItem.setVisible(false);
		    	MenuItem viewContactMenuItem = contextMenu.findItem(VIEW_CONTACT_CONTEXT_MENU);
		    	viewContactMenuItem.setVisible(false);
				MenuItem viewCallLogMenuItem = contextMenu.findItem(VIEW_CALL_LOG_CONTEXT_MENU);
				viewCallLogMenuItem.setVisible(false);
				MenuItem callMenuItem = contextMenu.findItem(CALL_CONTACT_CONTEXT_MENU);
				callMenuItem.setVisible(false);
				MenuItem messagingInboxMenuItem = contextMenu.findItem(MESSAGING_INBOX_CONTEXT_MENU);
				messagingInboxMenuItem.setVisible(false);
				MenuItem viewThreadMenuItem = contextMenu.findItem(VIEW_THREAD_CONTEXT_MENU);
				viewThreadMenuItem.setVisible(false);
				MenuItem textContactMenuItem = contextMenu.findItem(TEXT_CONTACT_CONTEXT_MENU);
				textContactMenuItem.setVisible(false);
				MenuItem viewK9EmailInboxMenuItem = contextMenu.findItem(VIEW_K9_INBOX_CONTEXT_MENU);
				viewK9EmailInboxMenuItem.setVisible(false);
				MenuItem openTwitterAppMenuItem = contextMenu.findItem(OPEN_TWITTER_APP_CONTEXT_MENU);
				openTwitterAppMenuItem.setVisible(false);
				MenuItem openFacebookAppMenuItem = contextMenu.findItem(OPEN_FACEBOOK_APP_CONTEXT_MENU);
				openFacebookAppMenuItem.setVisible(false);
				break;
		    }
			case Constants.NOTIFICATION_TYPE_GMAIL:{
		    	MenuItem addCalendarEventMenuItem = contextMenu.findItem(ADD_CALENDAR_EVENT_CONTEXT_MENU);
		    	addCalendarEventMenuItem.setVisible(false);
				MenuItem editCalendarEventMenuItem = contextMenu.findItem(EDIT_CALENDAR_EVENT_CONTEXT_MENU);
				editCalendarEventMenuItem.setVisible(false);
				MenuItem viewCalendarEventMenuItem = contextMenu.findItem(VIEW_CALENDAR_CONTEXT_MENU);
				viewCalendarEventMenuItem.setVisible(false);
		    	MenuItem viewCallLogMenuItem = contextMenu.findItem(VIEW_CALL_LOG_CONTEXT_MENU);
				viewCallLogMenuItem.setVisible(false);
				MenuItem messagingInboxMenuItem = contextMenu.findItem(MESSAGING_INBOX_CONTEXT_MENU);
				messagingInboxMenuItem.setVisible(false);
				MenuItem viewThreadMenuItem = contextMenu.findItem(VIEW_THREAD_CONTEXT_MENU);
				viewThreadMenuItem.setVisible(false);
				MenuItem viewK9EmailInboxMenuItem = contextMenu.findItem(VIEW_K9_INBOX_CONTEXT_MENU);
				viewK9EmailInboxMenuItem.setVisible(false);
				MenuItem openTwitterAppMenuItem = contextMenu.findItem(OPEN_TWITTER_APP_CONTEXT_MENU);
				openTwitterAppMenuItem.setVisible(false);
				MenuItem openFacebookAppMenuItem = contextMenu.findItem(OPEN_FACEBOOK_APP_CONTEXT_MENU);
				openFacebookAppMenuItem.setVisible(false);
				break;
		    }
			case Constants.NOTIFICATION_TYPE_TWITTER:{
		    	MenuItem addCalendarEventMenuItem = contextMenu.findItem(ADD_CALENDAR_EVENT_CONTEXT_MENU);
		    	addCalendarEventMenuItem.setVisible(false);
				MenuItem editCalendarEventMenuItem = contextMenu.findItem(EDIT_CALENDAR_EVENT_CONTEXT_MENU);
				editCalendarEventMenuItem.setVisible(false);
				MenuItem viewCalendarEventMenuItem = contextMenu.findItem(VIEW_CALENDAR_CONTEXT_MENU);
				viewCalendarEventMenuItem.setVisible(false);
		    	MenuItem viewCallLogMenuItem = contextMenu.findItem(VIEW_CALL_LOG_CONTEXT_MENU);
				viewCallLogMenuItem.setVisible(false);
				MenuItem callMenuItem = contextMenu.findItem(CALL_CONTACT_CONTEXT_MENU);
				callMenuItem.setVisible(false);
				MenuItem messagingInboxMenuItem = contextMenu.findItem(MESSAGING_INBOX_CONTEXT_MENU);
				messagingInboxMenuItem.setVisible(false);
				MenuItem viewThreadMenuItem = contextMenu.findItem(VIEW_THREAD_CONTEXT_MENU);
				viewThreadMenuItem.setVisible(false);
				MenuItem textContactMenuItem = contextMenu.findItem(TEXT_CONTACT_CONTEXT_MENU);
				textContactMenuItem.setVisible(false);
				MenuItem viewK9EmailInboxMenuItem = contextMenu.findItem(VIEW_K9_INBOX_CONTEXT_MENU);
				viewK9EmailInboxMenuItem.setVisible(false);
				MenuItem openFacebookAppMenuItem = contextMenu.findItem(OPEN_FACEBOOK_APP_CONTEXT_MENU);
				openFacebookAppMenuItem.setVisible(false);
				break;
		    }
			case Constants.NOTIFICATION_TYPE_FACEBOOK:{
		    	MenuItem addCalendarEventMenuItem = contextMenu.findItem(ADD_CALENDAR_EVENT_CONTEXT_MENU);
		    	addCalendarEventMenuItem.setVisible(false);
				MenuItem editCalendarEventMenuItem = contextMenu.findItem(EDIT_CALENDAR_EVENT_CONTEXT_MENU);
				editCalendarEventMenuItem.setVisible(false);
				MenuItem viewCalendarEventMenuItem = contextMenu.findItem(VIEW_CALENDAR_CONTEXT_MENU);
				viewCalendarEventMenuItem.setVisible(false);
		    	MenuItem viewCallLogMenuItem = contextMenu.findItem(VIEW_CALL_LOG_CONTEXT_MENU);
				viewCallLogMenuItem.setVisible(false);
				MenuItem callMenuItem = contextMenu.findItem(CALL_CONTACT_CONTEXT_MENU);
				callMenuItem.setVisible(false);
				MenuItem messagingInboxMenuItem = contextMenu.findItem(MESSAGING_INBOX_CONTEXT_MENU);
				messagingInboxMenuItem.setVisible(false);
				MenuItem viewThreadMenuItem = contextMenu.findItem(VIEW_THREAD_CONTEXT_MENU);
				viewThreadMenuItem.setVisible(false);
				MenuItem textContactMenuItem = contextMenu.findItem(TEXT_CONTACT_CONTEXT_MENU);
				textContactMenuItem.setVisible(false);
				MenuItem viewK9EmailInboxMenuItem = contextMenu.findItem(VIEW_K9_INBOX_CONTEXT_MENU);
				viewK9EmailInboxMenuItem.setVisible(false);
				MenuItem openTwitterAppMenuItem = contextMenu.findItem(OPEN_TWITTER_APP_CONTEXT_MENU);
				openTwitterAppMenuItem.setVisible(false);
				break;
		    }
			case Constants.NOTIFICATION_TYPE_K9:{
		    	MenuItem addCalendarEventMenuItem = contextMenu.findItem(ADD_CALENDAR_EVENT_CONTEXT_MENU);
		    	addCalendarEventMenuItem.setVisible(false);
				MenuItem editCalendarEventMenuItem = contextMenu.findItem(EDIT_CALENDAR_EVENT_CONTEXT_MENU);
				editCalendarEventMenuItem.setVisible(false);
				MenuItem viewCalendarEventMenuItem = contextMenu.findItem(VIEW_CALENDAR_CONTEXT_MENU);
				viewCalendarEventMenuItem.setVisible(false);
		    	MenuItem viewCallLogMenuItem = contextMenu.findItem(VIEW_CALL_LOG_CONTEXT_MENU);
				viewCallLogMenuItem.setVisible(false);
				MenuItem callMenuItem = contextMenu.findItem(CALL_CONTACT_CONTEXT_MENU);
				callMenuItem.setVisible(false);
				MenuItem messagingInboxMenuItem = contextMenu.findItem(MESSAGING_INBOX_CONTEXT_MENU);
				messagingInboxMenuItem.setVisible(false);
				MenuItem viewThreadMenuItem = contextMenu.findItem(VIEW_THREAD_CONTEXT_MENU);
				viewThreadMenuItem.setVisible(false);
				MenuItem textContactMenuItem = contextMenu.findItem(TEXT_CONTACT_CONTEXT_MENU);
				textContactMenuItem.setVisible(false);
				MenuItem openTwitterAppMenuItem = contextMenu.findItem(OPEN_TWITTER_APP_CONTEXT_MENU);
				openTwitterAppMenuItem.setVisible(false);
				MenuItem openFacebookAppMenuItem = contextMenu.findItem(OPEN_FACEBOOK_APP_CONTEXT_MENU);
				openFacebookAppMenuItem.setVisible(false);
				break;
		    }
		}
	}
	
	/**
	 * Cancel the screen timeout alarm.
	 */
	private void cancelScreenTimeout(){
		if (_debug) Log.v("NotificationActivity.cancelScreenTimeout()");
		if (_screenTimeoutPendingIntent != null) {
	    	AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
	    	alarmManager.cancel(_screenTimeoutPendingIntent);
	    	_screenTimeoutPendingIntent.cancel();
	    	_screenTimeoutPendingIntent = null;
		}
	}
	
	/**
	 * Set up the phone for TTS.
	 */
	private void setupTextToSpeech(){
		if (_debug) Log.v("NotificationActivity.setupTextToSpeech()");
		Intent intent = new Intent();
		intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(intent, Constants.TEXT_TO_SPEECH_ACTIVITY);
	}
	
	/**
	 * The Android text-to-speech library OnInitListener
	 */
	private final OnInitListener ttsOnInitListener = new OnInitListener() {
		public void onInit(int status){
			if (_debug) Log.v("NotificationActivity.OnInitListener.onInit()");			
			if(status == TextToSpeech.SUCCESS){
				Notification activeNotification = _notificationViewFlipper.getActiveNotification();
				activeNotification.speak(_tts);
				activeNotification.cancelReminder();
			}else{
				Toast.makeText(_context, R.string.app_tts_error, Toast.LENGTH_LONG);
			}
    	}
  	};
	
}