package apps.droidnotify;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
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
 */
public class NotificationActivity extends Activity {

	//================================================================================
    // Constants
    //================================================================================
	
	private final double WIDTH = 0.9;
	private final int MAX_WIDTH = 640;
	
	private final int NOTIFICATION_TYPE_PHONE = 0;
	private final int NOTIFICATION_TYPE_SMS = 1;
	private final int NOTIFICATION_TYPE_MMS = 2;
	private final int NOTIFICATION_TYPE_CALENDAR = 3;
	private final int NOTIFICATION_TYPE_EMAIL = 4;
	
	static final int DIALOG_DELETE_MESSAGE = 0;

	final String APP_ENABLED_KEY = "app_enabled_settings";
	final String SMS_NOTIFICATIONS_ENABLED_KEY = "sms_notifications_enabled";
	final String MMS_NOTIFICATIONS_ENABLED_KEY = "mms_notifications_enabled";
	final String MISSED_CALL_NOTIFICATIONS_ENABLED_KEY = "missed_call_notifications_enabled";
	
	//================================================================================
    // Properties
    //================================================================================

	private Bundle _bundle = null;
	private NotificationViewFlipper _notificationViewFlipper = null;
	private LinearLayout _mainActivityLayout = null;
	private Button _previousButton = null;
	private Button _nextButton = null;
	private TextView _notificationCountTextView = null;
	private InputMethodManager _inputMethodManager = null;
	private View _softKeyboardTriggerView = null;

	//================================================================================
	// Constructors
	//================================================================================
	
	//================================================================================
	// Accessors
	//================================================================================
	  
	/**
	 * Set the notificationViewFlipper property.
	 */
	public void setNotificationViewFlipper(NotificationViewFlipper notificationViewFlipper) {
		if (Log.getDebug()) Log.v("NotificationActivity.setNotificationViewFlipper()");
	    _notificationViewFlipper = notificationViewFlipper;
	}
	
	/**
	 * Get the notificationViewFlipper property.
	 */
	public NotificationViewFlipper getNotificationViewFlipper() {
		if (Log.getDebug()) Log.v("NotificationActivity.getNotificationViewFlipper()");
	    return _notificationViewFlipper;
	}
	  
	/**
	 * Set the previousButton property.
	 */
	public void setMainActivityLayout(LinearLayout mainActivityLayout) {
		if (Log.getDebug()) Log.v("NotificationActivity.setMainActivityLayout()");
		_mainActivityLayout = mainActivityLayout;
	}
	
	/**
	 * Get the previousButton property.
	 */
	public LinearLayout getMainActivityLayout() {
		if (Log.getDebug()) Log.v("NotificationActivity.getMainActivityLayout()");
	    return _mainActivityLayout;
	}
	  
	/**
	 * Set the previousButton property.
	 */
	public void setPreviousButton(Button previousButton) {
		if (Log.getDebug()) Log.v("NotificationActivity.setPreviousButton()");
		_previousButton = previousButton;
	}
	
	/**
	 * Get the previousButton property.
	 */
	public Button getPreviousButton() {
		if (Log.getDebug()) Log.v("NotificationActivity.getPreviousButton()");
	    return _previousButton;
	}
	
	/**
	 * Set the nextButton property.
	 */
	public void setNextButton(Button nextButton) {
		if (Log.getDebug()) Log.v("NotificationActivity.setNextButton()");
		_nextButton = nextButton;
	}
	
	/**
	 * Get the previousButton property.
	 */
	public Button getNextButton() {
		if (Log.getDebug()) Log.v("NotificationActivity.getNextButton()");
	    return _nextButton;
	}

	/**
	 * Set the notificationCountTextView property.
	 */
	public void setNotificationCountTextView(TextView notificationCountTextView) {
		if (Log.getDebug()) Log.v("NotificationActivity.setNotificationCountTextView()");
		_notificationCountTextView = notificationCountTextView;
	}
	
	/**
	 * Get the notificationCountTextView property.
	 */
	public TextView getNotificationCountTextView() {
		if (Log.getDebug()) Log.v("NotificationActivity.getNotificationCountTextView()");
	    return _notificationCountTextView;
	}

	/**
	 * Set the inputMethodManager property.
	 */
	public void setInputMethodManager(InputMethodManager inputMethodManager) {
		if (Log.getDebug()) Log.v("NotificationActivity.setInputMethodManager()");
		_inputMethodManager = inputMethodManager;
	}
	
	/**
	 * Get the inputMethodManager property.
	 */
	public InputMethodManager getInputMethodManager() {
		if (Log.getDebug()) Log.v("NotificationActivity.getInputMethodManager()");
	    return _inputMethodManager;
	}	

	/**
	 * Set the softKeyboardTriggerView property.
	 */
	public void setSoftKeyboardTriggerView(View softKeyboardTriggerView) {
		if (Log.getDebug()) Log.v("NotificationActivity.setSoftKeyboardTriggerView()");
		_softKeyboardTriggerView = softKeyboardTriggerView;
	}
	
	/**
	 * Get the softKeyboardTriggerView property.
	 */
	public View getSoftKeyboardTriggerView() {
		if (Log.getDebug()) Log.v("NotificationActivity.getGoftKeyboardTriggerView()");
	    return _softKeyboardTriggerView;
	}
	
	
	//================================================================================
	// Public Methods
	//================================================================================
  
	/**
	 * 
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	    super.onWindowFocusChanged(hasFocus);
	    if (Log.getDebug()) Log.v("NotificationActivity.onWindowFocusChanged() Value: " + hasFocus);
	}
	  
	/**
	 * 
	 */
	@Override
	public void onSaveInstanceState(Bundle saveBundle) {
	    super.onSaveInstanceState(saveBundle);
	    if (Log.getDebug()) Log.v("NotificationActivity.onSaveInstanceState()");
	    // Save values from most recent bundle.
	    saveBundle.putAll(_bundle);
	}
	
	/**
	 * 
	 */
	@Override
	public void onRestoreInstanceState(Bundle restoreBundle){
		super.onRestoreInstanceState(restoreBundle);
		//TODO - NotificationActivity().onRestoreInstanceState()
	}
	
	/**
	 * 
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    if (Log.getDebug()) Log.v("NotificationActivity.onConfigurationChanged()");
	    //TODO - NotificationActivity().onConfigurationChanged()
	}

	/**
	 * Create Context Menu (Long-press menu)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenuInfo contextMenuInfo) {
	    super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
	    if (Log.getDebug()) Log.v("NotificationActivity.onCreateContextMenu()");
	    //TODO - NotificationActivity().onCreateContextMenu()
	}

	/**
	 * Context Menu Item Selected
	 */
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		if (Log.getDebug()) Log.v("NotificationActivity.onContextItemSelected()");
	    //TODO - NotificationActivity().onCreateContextMenu()
		return super.onContextItemSelected(menuItem);
	}

	/**
	 * Customized activity finish.
	 * This closes this activity screen.
	 */
	public void finishActivity() {
		if (Log.getDebug()) Log.v("NotificationActivity.finishActivity()");
	    // Finish the activity
	    finish();
	}
	  
	/**
	 * Update the navigation buttons and text when items are added or removed.
	 */
    public void updateNavigationButtons(){
    	if (Log.getDebug()) Log.v("NotificationActivity.updateNavigationButtons()");
		updateNavigationButtons(getPreviousButton(), getNotificationCountTextView(), getNextButton());		
    }
  
	/**
	 * Display the delete dialog from the activity and return the result.
	 * 
	 * @return Boolean of the confirmation of delete. 
	 */
	public void showDeleteDialog(){
		if (Log.getDebug()) Log.v("NotificationActivity.showDeleteDialog()");
		showDialog(DIALOG_DELETE_MESSAGE);
	}
    
	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Called when the activity is created. Set up views and notifications.
	 */
	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
	    if (Log.getDebug()) Log.v("NotificationActivity.onCreate()");
		//Read preferences and end activity early if app is disabled.
	    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    if(!preferences.getBoolean(APP_ENABLED_KEY, true)){
			if (Log.getDebug()) Log.v("NotificationActivity.onCreate() App Disabled. Finishing Activity... ");
			finishActivity();
			return;
		}
	    Bundle bundle = getIntent().getExtras();
	    int notificationType = bundle.getInt("notificationType");
	    if (Log.getDebug()) Log.v("NotificationActivity.onCreate() Notification Type: " + notificationType);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
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
	    	setupMissedCalls(bundle);
	    }
	    if(notificationType == NOTIFICATION_TYPE_SMS){
		    if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_SMS");
			//Read preferences and end activity early if SMS notifications are disabled.
		    if(!preferences.getBoolean(SMS_NOTIFICATIONS_ENABLED_KEY, true)){
				if (Log.getDebug()) Log.v("NotificationActivity.onCreate() SMS Notifications Disabled. Finishing Activity... ");
				finishActivity();
				return;
			}
		    setupMessages(bundle);
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
	    // wake up app (turn on screen and run notification)
	    //wakeApp();
	}

	  
	/**
	 * 
	 */
	@Override
	protected void onStart() {
		super.onStart();
	    if (Log.getDebug()) Log.v("NotificationActivity.onStart()");
	    // TODO - NotificationActivity.onStart()
	}
	  
	/**
	 * 
	 */
	@Override
	protected void onResume() {
	    super.onResume();
	    if (Log.getDebug()) Log.v("NotificationActivity.onResume()");
	    // TODO - NotificationActivity.onResume()
	}
	  
	/**
	 * 
	 */
	@Override
	protected void onPause() {
	    super.onPause();
	    if (Log.getDebug()) Log.v("NotificationActivity.onPause()");
	    hideSoftKeyboard();
	 // TODO - NotificationActivity.onPause()
	}
	  
	/**
	 * 
	 */
	@Override
	protected void onStop() {
	    super.onStop();
	    if (Log.getDebug()) Log.v("NotificationActivity.onStop()");
	    // TODO - NotificationActivity.onStop()
	}
	  
	/**
	 * 
	 */
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (Log.getDebug()) Log.v("NotificationActivity.onDestroy()");
	 // TODO - NotificationActivity.onDestroy()
	}

	/**
	 * Create Dialog
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		if (Log.getDebug()) Log.v("NotificationActivity.onCreateDialog()");
		AlertDialog alert = null;
		switch (id) {
		  	
	      /*
	       * Delete message dialog
	       */
			case DIALOG_DELETE_MESSAGE:
				if (Log.getDebug()) Log.v("NotificationActivity.onCreateDialog() DIALOG_DELETE_MESSAGE");
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setTitle(getString(R.string.delete_message_dialog_title_text));
				builder.setMessage(getString(R.string.delete_message_dialog_text));
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
				alert = builder.create();

//			/**
//		     * Reply Dialog
//		     */
//		  	case DIALOG_REPLY:
//		    	LayoutInflater factory = getLayoutInflater();
//		        final View qrLayout = factory.inflate(R.layout.message_quick_reply, null);
//		        qrEditText = (EditText) qrLayout.findViewById(R.id.QuickReplyEditText);
//		        final TextView qrCounterTextView =
//		          (TextView) qrLayout.findViewById(R.id.QuickReplyCounterTextView);
//		        final Button qrSendButton = (Button) qrLayout.findViewById(R.id.send_button);
//	
//		        final ImageButton voiceRecognitionButton =
//		          (ImageButton) qrLayout.findViewById(R.id.SpeechRecogButton);
//	
//		        voiceRecognitionButton.setOnClickListener(new OnClickListener() {
//		          public void onClick(View view) {
//		            final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//		            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//		                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//	
//		            // Check if the device has the ability to do speech recognition
//		            final PackageManager packageManager = SmsPopupActivity.this.getPackageManager();
//		            List<ResolveInfo> list = packageManager.queryIntentActivities(intent, 0);
//	
//		            if (list.size() > 0) {
//		              // TODO: really I should allow voice input here without unlocking first (I allow
//		              // quick replies without unlock anyway)
//		              exitingKeyguardSecurely = true;
//		              ManageKeyguard.exitKeyguardSecurely(new LaunchOnKeyguardExit() {
//		                public void LaunchOnKeyguardExitSuccess() {
//		                  SmsPopupActivity.this.startActivityForResult(intent,
//		                      VOICE_RECOGNITION_REQUEST_CODE);
//		                }
//		              });
//		            } else {
//		              Toast.makeText(SmsPopupActivity.this, R.string.error_no_voice_recognition,
//		                  Toast.LENGTH_LONG).show();
//		              view.setEnabled(false);
//		            }
//		          }
//		        });
//	
//		        qrEditText.addTextChangedListener(new QmTextWatcher(this, qrCounterTextView, qrSendButton));
//		        qrEditText.setOnEditorActionListener(new OnEditorActionListener() {
//		          public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//	
//		            // event != null means enter key pressed
//		            if (event != null) {
//		              // if shift is not pressed then move focus to send button
//		              if (!event.isShiftPressed()) {
//		                if (v != null) {
//		                  View focusableView = v.focusSearch(View.FOCUS_RIGHT);
//		                  if (focusableView != null) {
//		                    focusableView.requestFocus();
//		                    return true;
//		                  }
//		                }
//		              }
//	
//		              // otherwise allow keypress through
//		              return false;
//		            }
//	
//		            if (actionId == EditorInfo.IME_ACTION_SEND) {
//		              if (v != null) {
//		                sendQuickReply(v.getText().toString());
//		              }
//		              return true;
//		            }
//	
//		            // else consume
//		            return true;
//		          }
//		        });
//	
//		        quickreplyTextView = (TextView) qrLayout.findViewById(R.id.QuickReplyTextView);
//		        QmTextWatcher.getQuickReplyCounterText(
//		            qrEditText.getText().toString(), qrCounterTextView, qrSendButton);
//	
//		        qrSendButton.setOnClickListener(new OnClickListener() {
//		          public void onClick(View v) {
//		            sendQuickReply(qrEditText.getText().toString());
//		          }
//		        });
//	
//		        // Construct basic AlertDialog using AlertDialog.Builder
//		        final AlertDialog qrAlertDialog = new AlertDialog.Builder(this)
//		        .setIcon(android.R.drawable.ic_dialog_email)
//		        .setTitle(R.string.quickreply_title)
//		        .create();
//	
//		        // Set the custom layout with no spacing at the bottom
//		        qrAlertDialog.setView(qrLayout, 0, 5, 0, 0);
//	
//		        // Preset messages button
//		        Button presetButton = (Button) qrLayout.findViewById(R.id.PresetMessagesButton);
//		        presetButton.setOnClickListener(new OnClickListener() {
//		          public void onClick(View v) {
//		            showDialog(DIALOG_PRESET_MSG);
//		          }
//		        });
//	
//		        // Cancel button
//		        Button cancelButton = (Button) qrLayout.findViewById(R.id.CancelButton);
//		        cancelButton.setOnClickListener(new OnClickListener() {
//		          public void onClick(View v) {
//		            if (qrAlertDialog != null) {
//		              hideSoftKeyboard();
//		              qrAlertDialog.dismiss();
//		            }
//		          }
//		        });
//	
//		        // Ensure this dialog is counted as "editable" (so soft keyboard will always show on top)
//		        qrAlertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//	
//		        qrAlertDialog.setOnDismissListener(new OnDismissListener() {
//		          public void onDismiss(DialogInterface dialog) {
//		            if (Log.getDebug()) Log.v("Quick Reply Dialog: onDissmiss()");
//		          }
//		        });
//	
//		        // Update quick reply views now that they have been created
//		        updateQuickReplyView("");
//	
//		        /*
//		         * TODO: due to what seems like a bug, setting selection to 0 here doesn't seem to work
//		         * but setting it to 1 first then back to 0 does.  I couldn't find a way around this :|
//		         * To reproduce, comment out the below line and set a quick reply signature, when
//		         * clicking Quick Reply the cursor will be positioned at the end of the EditText
//		         * rather than the start.
//		         */
//		        if (qrEditText.getText().toString().length() > 0) qrEditText.setSelection(1);
//	
//		        qrEditText.setSelection(0);
//	
//		        return qrAlertDialog;
//
//
//	        /*
//	         * Loading Dialog
//	         */
//	      case DIALOG_LOADING:
//	        mProgressDialog = new ProgressDialog(this);
//	        mProgressDialog.setMessage(getString(R.string.loading_message));
//	        mProgressDialog.setIndeterminate(true);
//	        mProgressDialog.setCancelable(true);
//	        return mProgressDialog;
		        
//		default:
//			alert = null;
		}
		return alert;
	}
	  
	/**
	 * 
	 */
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
	    super.onPrepareDialog(id, dialog);
	    if (Log.getDebug()) Log.v("NotificationActivity.onPrepareDialog()");
//	    // User interacted so remove all locks and cancel reminders
//	    ClearAllReceiver.removeCancel(getApplicationContext());
//	    ClearAllReceiver.clearAll(false);
//	    ReminderReceiver.cancelReminder(getApplicationContext());
//
//	    switch (id) {
//	      case DIALOG_QUICKREPLY:
//	        showSoftKeyboard(qrEditText);
//
//	        // Set width of dialog to fill_parent
//	        LayoutParams mLP = dialog.getWindow().getAttributes();
//
//	        // TODO: this should be limited in case the screen is large
//	        mLP.width = LayoutParams.FILL_PARENT;
//	        dialog.getWindow().setAttributes(mLP);
//	        break;
//
//	      case DIALOG_PRESET_MSG:
//	        break;
//	    }
	}

	/**
	 * Handle the results from the recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (Log.getDebug()) Log.v("NotificationActivity.onActivityResult()");
	    //TODO  - NotificationActivity.onActivityResult()
	}

    /**
     * This is called when the activity is running and it is run again for a different notification.
     */
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    if (Log.getDebug()) Log.v("NotificationActivity.onNewIntent()");
	    setIntent(intent);
	    setupMessages(intent.getExtras());
	    //TODO - NotificationActivity.onNewIntent() - Get all unread messages if new Activity?
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Set up the ViewFlipper elements.
	 */ 
	private void setupViews(int notificationType) {
		if (Log.getDebug()) Log.v("NotificationActivity.setupViews()");

		setNotificationViewFlipper((NotificationViewFlipper) findViewById(R.id.notification_view_flipper));
		setPreviousButton((Button) findViewById(R.id.previous_button));
		setNextButton((Button) findViewById(R.id.next_button));
		setNotificationCountTextView((TextView) findViewById(R.id.notification_count_text_view));
	
		// Previous Button
		getPreviousButton().setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	if (Log.getDebug()) Log.v("Previous Button Clicked()");
		    	getNotificationViewFlipper().showPrevious();
		    	updateNavigationButtons(getPreviousButton(), getNotificationCountTextView(), getNextButton());
		    }
		});
		
		// Next Button
		getNextButton().setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	if (Log.getDebug()) Log.v("Next Button Clicked()");
		    	getNotificationViewFlipper().showNext();
		    	updateNavigationButtons(getPreviousButton(), getNotificationCountTextView(), getNextButton());
		    }
		});
		
		initNavigationButtons(getPreviousButton(), getNotificationCountTextView(), getNextButton());
	    
	}
	
	/**
	 * 
	 * @param bundle
	 */
	private void setupMissedCalls(Bundle bundle){
		if (Log.getDebug()) Log.v("NotificationActivity.setupMissedCalls()");   
		ArrayList<String> missedCallsArray = bundle.getStringArrayList("missedCallsArray");
		for(int i=0; i< missedCallsArray.size(); i++){
			String[] missedCallInfo = missedCallsArray.get(i).split("\\|");
			if (Log.getDebug()) Log.v("NotificationActivity.setupMissedCalls() MissedCallInfo: " + missedCallsArray.get(i));
			String phoneNumber = missedCallInfo[0];
			if (Log.getDebug()) Log.v("NotificationActivity.setupMissedCalls() ParsedPhone Number: " + phoneNumber);
			long timeStamp = Long.parseLong(missedCallInfo[1]);
			if (Log.getDebug()) Log.v("NotificationActivity.setupMissedCalls() Parsed TimeStamp: " + timeStamp);
			Notification missedCallnotification = new Notification(getApplicationContext(), phoneNumber, timeStamp, NOTIFICATION_TYPE_PHONE);
			if (Log.getDebug()) Log.v("NotificationActivity.setupMissedCalls() Notification Phone Number: " + missedCallnotification.getPhoneNumber());
			if (Log.getDebug()) Log.v("NotificationActivity.setupMissedCalls() Adding misssed call to flipper");
			getNotificationViewFlipper().addNotification(missedCallnotification);
		}
	    updateNavigationButtons(getPreviousButton(), getNotificationCountTextView(), getNextButton());
	}
	
	/**
	 * Setup messages within the popup given an intent bundle
	 *
	 * @param b the incoming intent bundle
	 * @param newIntent if this is from onNewIntent or not
	 */
	private void setupMessages(Bundle bundle) {
		if (Log.getDebug()) Log.v("NotificationActivity.setupMessages()");
	    // Create message from bundle
	    Notification smsMessage = new Notification(getApplicationContext(), bundle, NOTIFICATION_TYPE_SMS);
	    if (Log.getDebug()) Log.v("NotificationActivity.setupMessages() Notification Phone Number: " + smsMessage.getPhoneNumber());
	    getNotificationViewFlipper().addNotification(smsMessage);
	    updateNavigationButtons(getPreviousButton(), getNotificationCountTextView(), getNextButton());
	}
	
	/**
	 * Initialize the navigation buttons and text.
	 */
	private void initNavigationButtons(Button _previousButton, TextView _notificationCountTextView, Button _nextButton){
		if (Log.getDebug()) Log.v("NotificationActivity.initNavigationButtons()");
		updateNavigationButtons(getPreviousButton(), getNotificationCountTextView(), getNextButton());
	}  
	  
	/**
	 * Update the navigation buttons and text when items are added or removed.
	 */
    public void updateNavigationButtons(Button _previousButton, TextView _notificationCountTextView, Button _nextButton){
    	if (Log.getDebug()) Log.v("NotificationActivity.updateNavigationButtons()");
		getPreviousButton().setEnabled(!getNotificationViewFlipper().isFirstMessage());
		_notificationCountTextView.setText( (getNotificationViewFlipper().getCurrentNotification() + 1) + "/" + getNotificationViewFlipper().getTotalNotifications());
		getNextButton().setEnabled(!getNotificationViewFlipper().isLastMessage()); 		
    }
    
//	/**
//	 * Wake up the activity, this will acquire the wakelock (turn on the screen)
//	 * and sound the notification if needed. This is called once all preparation
//	 * is done for this activity (end of onCreate()).
//	 */
//	private void wakeApp() {
//	    // Time to acquire a full WakeLock (turn on screen)
//	    ManageWakeLock.acquireFull(getApplicationContext());
//	    ManageWakeLock.releasePartial();
//
//	    replying = false;
//	    inbox = false;
//
//	    // See if a notification has been played for this message...
//	    if (mSmsPopups.getActiveMessage().getNotify()) {
//	    	// Store extra to signify we have already notified for this message
//	    	bundle.putBoolean(SmsMmsMessage.EXTRAS_NOTIFY, false);
//	      	// Reset the reminderCount to 0 just to be sure
//	      	mSmsPopups.getActiveMessage().updateReminderCount(0);
//	      	// Run the notification
//	      	ManageNotification.show(getApplicationContext(), mSmsPopups.getActiveMessage());
//	    }
//	}
	
	/**
	 * Resize the notification to fit the screen.
	 * Makes the notification pretty.
	 */
	private void resizeLayout() {
		if (Log.getDebug()) Log.v("NotificationActivity.resizeLayout()");
		Display d = getWindowManager().getDefaultDisplay();
		int width = d.getWidth() > MAX_WIDTH ? MAX_WIDTH : (int) (d.getWidth() * WIDTH);
		getMainActivityLayout().setMinimumWidth(width);
		getMainActivityLayout().invalidate();
	}

	/**
	 * Show the soft keyboard and store the view that triggered it.
	 */
	private void showSoftKeyboard(View triggeringView) {
		if (Log.getDebug()) Log.v("NotificationActivity.showSoftKeyboard()");
	    if (getInputMethodManager() == null) {
	    	setInputMethodManager((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
	    }
	    setSoftKeyboardTriggerView(triggeringView);
	    getInputMethodManager().toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}
	
	/**
	 * Hide the soft keyboard.
	 */
	private void hideSoftKeyboard() {
		if (Log.getDebug()) Log.v("NotificationActivity.hideSoftKeyboard()");
	    if (getSoftKeyboardTriggerView() == null) return;
	    if (getInputMethodManager() == null) {
	    	setInputMethodManager((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
	    }
	    getInputMethodManager().hideSoftInputFromWindow(getSoftKeyboardTriggerView().getApplicationWindowToken(), 0);
	    setSoftKeyboardTriggerView(null);
	}
	
	/**
	 * Delete the current message from the users phone.
	 */
	private void deleteMessage(){
		if (Log.getDebug()) Log.v("NotificationActivity.deleteMessage()");
		getNotificationViewFlipper().deleteMessage();
	}

}
