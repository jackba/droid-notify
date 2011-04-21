package apps.droidnotify;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

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
	
	//================================================================================
    // Properties
    //================================================================================

	private Bundle bundle;
	private NotificationViewFlipper SMSNotificationFlipper = null;
	private LinearLayout mainLayout;
	private Button previousButton;
	private Button inboxButton;
	private Button nextButton;
	private InputMethodManager inputMethodManager = null;
	private View softKeyboardTriggerView = null;

	//================================================================================
	// Constructors
	//================================================================================
	
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
	    saveBundle.putAll(bundle);
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
	    Bundle bundle = getIntent().getExtras();
	    if (Log.getDebug()) Log.v("NotificationActivity.onCreate() Got bundle");
	    int notificationType = bundle.getInt("notificationType");
	    if (Log.getDebug()) Log.v("NotificationActivity.onCreate() Notification Type: " + notificationType);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.notificationwrapper);
	    setupViews(notificationType);
	    if(notificationType == NOTIFICATION_TYPE_PHONE){
	    	if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_PHONE");
	    	setupMissedCalls(bundle);
	    }
	    if(notificationType == NOTIFICATION_TYPE_SMS){
//		    if (b == null) { // This is a new activity
		    	if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_SMS");
		    	setupMessages(bundle);
//		    } else { // This activity was recreated after being destroyed
//		    	if (Log.getDebug()) Log.v("NotificationActivity.onCreate() Using Bundle");
//		    	setupMessages(b);
//		    }
	    }
	    if(notificationType == NOTIFICATION_TYPE_MMS){
	    	if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_MMS");
	    	//TODO - MMS Message
	    }
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	if (Log.getDebug()) Log.v("NotificationActivity.onCreate() NOTIFICATION_TYPE_CALENDAR");
	    	//TODO - Calendar Reminder
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

//	    // Dismiss loading dialog
//	    if (mProgressDialog != null) {
//	      mProgressDialog.dismiss();
//	    }

//	    if (wasVisible) {
//	      // Cancel the receiver that will clear our locks
//	      ClearAllReceiver.removeCancel(getApplicationContext());
//	      ClearAllReceiver.clearAll(!exitingKeyguardSecurely);
//	    }

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
	}

	/**
	 * Create Dialog
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		if (Log.getDebug()) Log.v("NotificationActivity.onCreateDialog()");
		switch (id) {
		  	
//	      /*
//	       * Delete message dialog
//	       */
//	      case DIALOG_DELETE:
//	        return new AlertDialog.Builder(this)
//	        .setIcon(android.R.drawable.ic_dialog_alert)
//	        .setTitle(getString(R.string.pref_show_delete_button_dialog_title))
//	        .setMessage(getString(R.string.pref_show_delete_button_dialog_text))
//	        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//	          public void onClick(DialogInterface dialog, int whichButton) {
//	            deleteMessage();
//	          }
//	        })
//	        .setNegativeButton(android.R.string.cancel, null)
//	        .create();

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


//	        /*
//	         * Loading Dialog
//	         */
//	      case DIALOG_LOADING:
//	        mProgressDialog = new ProgressDialog(this);
//	        mProgressDialog.setMessage(getString(R.string.loading_message));
//	        mProgressDialog.setIndeterminate(true);
//	        mProgressDialog.setCancelable(true);
//	        return mProgressDialog;
		        
	    }
	    return null;
	}
	  
	/**
	 * 
	 */
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
	    super.onPrepareDialog(id, dialog);
	    if (Log.getDebug()) Log.v("NotificationActivity.onPrepareDialog()");

//	    if (Log.getDebug()) Log.v("onPrepareDialog()");
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
	    //TODO  - NotificationActivity.onNewIntent()
	    setIntent(intent);
	    setupMessages(intent.getExtras());
	    //TODO - NotificationActivity.onNewIntent() - Get all unread messages if new Activity?
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * 
	 */ 
	private void setupViews(int notificationType) {
		if (Log.getDebug()) Log.v("NotificationActivity.setupViews()");

		SMSNotificationFlipper = (NotificationViewFlipper) findViewById(R.id.notification_layout);
	
		previousButton = (Button) findViewById(R.id.previous_button);
		inboxButton = (Button) findViewById(R.id.inbox_button);
		nextButton = (Button) findViewById(R.id.next_button);
		
	    if(notificationType == NOTIFICATION_TYPE_PHONE){
	    	//TODO - Missed Call
	    }
		if(notificationType == NOTIFICATION_TYPE_SMS){
							
			// Inbox Button
			inboxButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View v) {
			    	if (Log.getDebug()) Log.v("Inbox Clicked()");
			    	gotoInbox();
			    }
			});
						    			
			// Delete Button
			Button deleteButton = (Button) findViewById(R.id.delete_button);
			deleteButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View v) {
			    	if (Log.getDebug()) Log.v("Delete Button Clicked()");
			    	showDialog(Menu.FIRST);
			    	updateNavigationButtons(previousButton, inboxButton, nextButton);
			    }
			});
			
			// Reply Button
			Button replyButton = (Button) findViewById(R.id.reply_button);
			replyButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View v) {
			    	if (Log.getDebug()) Log.v("Reply Button Clicked()");
			    	replyToMessage();
			    }
			});

		}
	    if(notificationType == NOTIFICATION_TYPE_MMS){
	    	//TODO - MMS Message
	    }
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	//TODO - Calendar Reminder
	    }
	    
	    //Items that are the same for aLL notification types.
	
		// Previous Button
		previousButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	if (Log.getDebug()) Log.v("Previous Button Clicked()");
		    	SMSNotificationFlipper.showPrevious();
		    	updateNavigationButtons(previousButton, inboxButton, nextButton);
		    }
		});
		
		// Next Button
		nextButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	if (Log.getDebug()) Log.v("Next Button Clicked()");
		    	SMSNotificationFlipper.showNext();
		    	updateNavigationButtons(previousButton, inboxButton, nextButton);
		    }
		});
	    
		// Close Button
		Button closeButton = (Button) findViewById(R.id.close_button);		      
		closeButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	if (Log.getDebug()) Log.v("Close Button Clicked()");
		    	closeNotification();
		    }
		});
		
		initNavigationButtons(previousButton, inboxButton, nextButton);
	    
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
			Notification missedCallnotification = new Notification(getApplicationContext(), phoneNumber, timeStamp, 0);
			if (Log.getDebug()) Log.v("NotificationActivity.setupMissedCalls() Notification Phone Number: " + missedCallnotification.getPhoneNumber());
			if (Log.getDebug()) Log.v("NotificationActivity.setupMissedCalls() Adding misssed call to flipper");
			SMSNotificationFlipper.addMessage(missedCallnotification);
			break;
		}
	    updateNavigationButtons(previousButton, inboxButton, nextButton);
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
	    Notification smsMessage = new Notification(getApplicationContext(), bundle, 1);
	    if (Log.getDebug()) Log.v("NotificationActivity.setupMessages() Notification Phone Number: " + smsMessage.getPhoneNumber());
	    if (Log.getDebug()) Log.v("NotificationActivity.setupMessages() Adding SMS message to flipper");
	    SMSNotificationFlipper.addMessage(smsMessage);
	    updateNavigationButtons(previousButton, inboxButton, nextButton);
	}
	
	/**
	 * 
	 */
	private void initNavigationButtons(Button previousButton, Button inboxButton, Button nextButton){
		if (Log.getDebug()) Log.v("NotificationActivity.initNavigationButtons()");
		updateNavigationButtons(previousButton, inboxButton, nextButton);
	}  
	  
	/**
	 * 
	 */
    private void updateNavigationButtons(Button previousButton, Button inboxButton, Button nextButton){
    	if (Log.getDebug()) Log.v("NotificationActivity.UpdateNavigationButtons()");
		previousButton.setEnabled(!SMSNotificationFlipper.isFirstMessage());
		inboxButton.setText( (SMSNotificationFlipper.getCurrentMessage() + 1) + "/" + SMSNotificationFlipper.getTotalMessages());
		nextButton.setEnabled(!SMSNotificationFlipper.isLastMessage()); 		
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
	 * Take the user to the messaging application inbox
	 */
	private void gotoInbox() {
		if (Log.getDebug()) Log.v("NotificationActivity.gotoInbox()");
		Intent i = new Intent(Intent.ACTION_MAIN);
	    i.setType("vnd.android-dir/mms-sms");
	    int flags =
	    	Intent.FLAG_ACTIVITY_NEW_TASK |
	    	Intent.FLAG_ACTIVITY_SINGLE_TOP |
	    	Intent.FLAG_ACTIVITY_CLEAR_TOP;
	    i.setFlags(flags);	
		NotificationActivity.this.getApplicationContext().startActivity(i);
		finishActivity();
	}

	/**
	 * Reply to the current message using the built in SMS app.
	 * This starts the built in SMS app Activity.
	 */
	private void replyToMessage() {
		if (Log.getDebug()) Log.v("NotificationActivity.replyToMessage()");
		//Intent i = SMSNotificationFlipper.getActiveMessage().getReplyIntent();
		//NotificationActivity.this.getApplicationContext().startActivity(i);
		//finishActivity();
		Notification message = SMSNotificationFlipper.getActiveMessage();
		Intent intent = getMessageReplyIntent(message);
		getApplicationContext().startActivity(intent);
		finishActivity();
	}
	
	/**
	 * 
	 */ 
	private Intent getMessageReplyIntent(Notification message) {
		if (Log.getDebug()) Log.v("NotificationActivity.getMessageReplyIntent()");
		//Reply to SMS "thread_id"
		if (Log.getDebug()) Log.v("NotificationActivity.getMessageReplyIntent() Replying to threadID: " + message.getThreadID());
	    Intent intent = new Intent(Intent.ACTION_VIEW);
	    intent.setType("vnd.android-dir/mms-sms");
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    intent.putExtra("address", message.getPhoneNumber());
	    return intent;
	}
	
	/**
	 * Resize the notification to fit the screen.
	 * Makes the notification pretty.
	 */
	private void resizeLayout() {
		if (Log.getDebug()) Log.v("NotificationActivity.resizeLayout()");
		Display d = getWindowManager().getDefaultDisplay();
		int width = d.getWidth() > MAX_WIDTH ? MAX_WIDTH : (int) (d.getWidth() * WIDTH);
		mainLayout.setMinimumWidth(width);
		mainLayout.invalidate();
	}

	/**
	 * Show the soft keyboard and store the view that triggered it.
	 */
	private void showSoftKeyboard(View triggeringView) {
		if (Log.getDebug()) Log.v("NotificationActivity.showSoftKeyboard()");
	    if (inputMethodManager == null) {
	    	inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	    }
	    softKeyboardTriggerView = triggeringView;
	    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}
	
	/**
	 * Hide the soft keyboard.
	 */
	private void hideSoftKeyboard() {
		if (Log.getDebug()) Log.v("NotificationActivity.hideSoftKeyboard()");
	    if (softKeyboardTriggerView == null) return;
	    if (inputMethodManager == null) {
	    	inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	    }
	    inputMethodManager.hideSoftInputFromWindow(softKeyboardTriggerView.getApplicationWindowToken(), 0);
	    softKeyboardTriggerView = null;
	}
	
	/**
	 * Close the notification window & mark the active message read.
	 */
	private void closeNotification() {
		if (Log.getDebug()) Log.v("NotificationActivity.closeNotification()");
		//TODO - Mark active message as read.
		finishActivity();
	}
	
	/**
	 * Customized activity finish.
	 * This closes this activity screen.
	 */
	private void finishActivity() {
		if (Log.getDebug()) Log.v("NotificationActivity.finishActivity()");
	    // Finish the activity
	    finish();
	}

}
