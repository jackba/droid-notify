package apps.droidnotify;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 *
 */
public class SMSNotificationActivity extends Activity {

	private Bundle bundle;
	private SMSNotificationViewFlipper SMSNotificationFlipper = null;
	private LinearLayout headerLayout;
	private LinearLayout mainLayout;
	private LinearLayout buttonLayout;
	private Button previousButton;
	private Button inboxButton;
	private Button nextButton;
	
	private double WIDTH = 0.9;
	private int MAX_WIDTH = 640;
	private int DIALOG_DELETE = Menu.FIRST;
	private int DIALOG_REPLY = Menu.FIRST + 1;
	  
	/**
	 *
	 */
	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
	    if (Log.DEBUG) Log.v("SMSNotificationActivity.onCreate()");
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.smsnotificationwindow);
	    setupViews();
	    if (b == null) { // This is a new activity
	    	if (Log.DEBUG) Log.v("SMSNotificationActivity.onCreate() Using Intent");
	    	setupMessages(getIntent().getExtras());
	    } else { // This activity was recreated after being destroyed
	    	if (Log.DEBUG) Log.v("SMSNotificationActivity.onCreate() Using Bundle");
	    	setupMessages(b);
	    }
	    // wake up app (turn on screen and run notification)
	    //wakeApp();
	}
	
	/**
	 *
	 */ 
	private void setupViews() {
		if (Log.DEBUG) Log.v("SMSNotificationActivity.setupViews()");
		// Find main views
		SMSNotificationFlipper = (SMSNotificationViewFlipper) findViewById(R.id.sms_notification_layout);
		
		previousButton = (Button) findViewById(R.id.previous_button);
		inboxButton = (Button) findViewById(R.id.inbox_button);
		nextButton = (Button) findViewById(R.id.next_button);
		
		// Previous Button
		previousButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	if (Log.DEBUG) Log.v("Previous Button Clicked()");
		    	SMSNotificationFlipper.showPrevious();
		    	updateNavigationButtons(previousButton, inboxButton, nextButton);
		    }
		});

		// Inbox Button
		inboxButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	if (Log.DEBUG) Log.v("Inbox Clicked()");
		    	gotoInbox();
		    }
		});
		
		// Next Button
		nextButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	if (Log.DEBUG) Log.v("Next Button Clicked()");
		    	SMSNotificationFlipper.showNext();
		    	updateNavigationButtons(previousButton, inboxButton, nextButton);
		    }
		});
		    
		// Close Button
	    Button closeButton = (Button) findViewById(R.id.close_button);		      
	    closeButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	if (Log.DEBUG) Log.v("Close Button Clicked()");
		    	closeNotification();
		    }
		});

	    // Delete Button
	    Button deleteButton = (Button) findViewById(R.id.delete_button);
	    deleteButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	if (Log.DEBUG) Log.v("Delete Button Clicked()");
		    	showDialog(Menu.FIRST);
		    	updateNavigationButtons(previousButton, inboxButton, nextButton);
		    }
		});

	    // Reply Button
	    Button replyButton = (Button) findViewById(R.id.reply_button);
	    replyButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	if (Log.DEBUG) Log.v("Reply Button Clicked()");
		    	replyToMessage();
		    }
		});
	    
	    initNavigationButtons(previousButton, inboxButton, nextButton);

//		    refreshViews();
//		    resizeLayout();
	}

	/**
	 * Setup messages within the popup given an intent bundle
	 *
	 * @param b the incoming intent bundle
	 * @param newIntent if this is from onNewIntent or not
	 */
	private void setupMessages(Bundle b) {
		if (Log.DEBUG) Log.v("SMSNotificationActivity.setupMessages()");
	    // Create message from bundle
	    TextMessage message = new TextMessage(getApplicationContext(), b);
	    if (Log.DEBUG) Log.v("SMSNotificationActivity.setupMessages() Message address: " + message.getFromAddress());
	    if (Log.DEBUG) Log.v("SMSNotificationActivity.setupMessages() Adding message to flipper");
	    SMSNotificationFlipper.addMessage(message);
	    updateNavigationButtons(previousButton, inboxButton, nextButton);
	}
	
	/**
	 *
	 */
	private void initNavigationButtons(Button previousButton, Button inboxButton, Button nextButton){
		if (Log.DEBUG) Log.v("SMSNotificationActivity.initNavigationButtons()");
		updateNavigationButtons(previousButton, inboxButton, nextButton);
	}
	  
	  
	/**
	 *
	 */
    private void updateNavigationButtons(Button previousButton, Button inboxButton, Button nextButton){
    	if (Log.DEBUG) Log.v("SMSNotificationActivity.UpdateNavigationButtons()");
		previousButton.setEnabled(!SMSNotificationFlipper.isFirstMessage());
		inboxButton.setText( (SMSNotificationFlipper.getCurrentMessage() + 1) + "/" + SMSNotificationFlipper.getTotalMessages());
		nextButton.setEnabled(!SMSNotificationFlipper.isLastMessage()); 		
    }
	  
//	  @Override
//	  protected void onNewIntent(Intent intent) {
//
//	    super.onNewIntent(intent);
//	    if (Log.DEBUG) Log.v("SMSPopupActivity: onNewIntent()");
//
//	    // Update intent held by activity
//	    setIntent(intent);
//
//	    // Setup messages
//	    setupMessages(intent.getExtras(), true);
//
//	    wakeApp();
//	}
	  
	/**
	 *
	 */
	@Override
	protected void onStart() {
		super.onStart();
	    if (Log.DEBUG) Log.v("SMSNotificationActivity.onStart()");
	    //ManageWakeLock.acquirePartial(getApplicationContext());
	}
	  
	/**
	 *
	 */
	@Override
	protected void onResume() {
	    super.onResume();
	    if (Log.DEBUG) Log.v("SMSNotificationActivity.onResume()");
	    //wasVisible = false;
	    // Reset exitingKeyguardSecurely bool to false
	    //exitingKeyguardSecurely = false;
	}
	  
	/**
	 *
	 */
	@Override
	protected void onPause() {
	    super.onPause();
	    if (Log.DEBUG) Log.v("SMSNotificationActivity.onPause()");

//	    // Hide the soft keyboard in case it was shown via quick reply
//	    hideSoftKeyboard();
//
//	    // Dismiss loading dialog
//	    if (mProgressDialog != null) {
//	      mProgressDialog.dismiss();
//	    }
//
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
	    if (Log.DEBUG) Log.v("SMSNotificationActivity.onStop()");

	    // Cancel the receiver that will clear our locks
	    //ClearAllReceiver.removeCancel(getApplicationContext());
	    //ClearAllReceiver.clearAll(!exitingKeyguardSecurely);
	}
	  
	/**
	 *
	 */
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (Log.DEBUG) Log.v("SMSNotificationActivity.onDestroy()");
	}

	/**
	 * Create Dialog
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		if (Log.DEBUG) Log.v("SMSNotificationActivity.onCreateDialog()");
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
//		            if (Log.DEBUG) Log.v("Quick Reply Dialog: onDissmiss()");
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
	    if (Log.DEBUG) Log.v("SMSNotificationActivity.onPrepareDialog()");

//	    if (Log.DEBUG) Log.v("onPrepareDialog()");
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
	    if (Log.DEBUG) Log.v("SMSNotificationActivity.onActivityResult()");
//	    if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
//	      ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//	      if (Log.DEBUG) Log.v("Voice recog text: " + matches.get(0));
//	      quickReply(matches.get(0));
//	    }
	}
	  
	/**
	 *
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	    super.onWindowFocusChanged(hasFocus);
	    if (Log.DEBUG) Log.v("SMSNotificationActivity.onWindowFocusChanged() Value: " + hasFocus);
	}
	  
	/**
	 *
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    if (Log.DEBUG) Log.v("SMSNotificationActivity.onSaveInstanceState()");
	    // Save values from most recent bundle (ie. most recent message)
	    outState.putAll(bundle);
	}
	  
	/**
	 *
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    if (Log.DEBUG) Log.v("SMSNotificationActivity.onConfigurationChanged()");
	    //resizeLayout();
	}

	/**
	 * Create Context Menu (Long-press menu)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    if (Log.DEBUG) Log.v("SMSNotificationActivity.onCreateContextMenu()");

//	    menu.add(Menu.NONE, CONTEXT_VIEWCONTACT_ID, Menu.NONE, getString(R.string.view_contact));
//	    menu.add(Menu.NONE, CONTEXT_CLOSE_ID, Menu.NONE, getString(R.string.button_close));
//	    menu.add(Menu.NONE, CONTEXT_DELETE_ID, Menu.NONE, getString(R.string.button_delete));
//	    menu.add(Menu.NONE, CONTEXT_REPLY_ID, Menu.NONE, getString(R.string.button_reply));
//	    menu.add(Menu.NONE, CONTEXT_QUICKREPLY_ID, Menu.NONE, getString(R.string.button_quickreply));
//	    menu.add(Menu.NONE, CONTEXT_TTS_ID, Menu.NONE, getString(R.string.button_tts));
//	    menu.add(Menu.NONE, CONTEXT_INBOX_ID, Menu.NONE, getString(R.string.button_inbox));
	}

	/**
	 * Context Menu Item Selected
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (Log.DEBUG) Log.v("SMSNotificationActivity.onContextItemSelected()");
//	    switch (item.getItemId()) {
//	      case CONTEXT_CLOSE_ID:
//	        closeMessage();
//	        break;
//	      case CONTEXT_DELETE_ID:
//	        showDialog(DIALOG_DELETE);
//	        break;
//	      case CONTEXT_REPLY_ID:
//	        replyToMessage();
//	        break;
//	      case CONTEXT_QUICKREPLY_ID:
//	        quickReply();
//	        break;
//	      case CONTEXT_INBOX_ID:
//	        gotoInbox();
//	        break;
//	      case CONTEXT_TTS_ID:
//	        speakMessage();
//	        break;
//	      case CONTEXT_VIEWCONTACT_ID:
//	        viewContact();
//	        break;
//	    }
		return super.onContextItemSelected(item);
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
		if (Log.DEBUG) Log.v("SMSNotificationActivity.gotoInbox()");
		Intent i = new Intent(Intent.ACTION_MAIN);
	    i.setType("vnd.android-dir/mms-sms");
	    int flags =
	    	Intent.FLAG_ACTIVITY_NEW_TASK |
	    	Intent.FLAG_ACTIVITY_SINGLE_TOP |
	    	Intent.FLAG_ACTIVITY_CLEAR_TOP;
	    i.setFlags(flags);	
		SMSNotificationActivity.this.getApplicationContext().startActivity(i);
		finishActivity();
	}

	/**
	 * Reply to the current message, start the reply intent
	 */
	private void replyToMessage() {
		if (Log.DEBUG) Log.v("SMSNotificationActivity.replyToMessage()");
		Intent i = SMSNotificationFlipper.getActiveMessage().getReplyIntent();
		SMSNotificationActivity.this.getApplicationContext().startActivity(i);
		finishActivity();
	}
	
	/**
	 * Resize the notification to fit the screen.
	 */
	private void resizeLayout() {
		if (Log.DEBUG) Log.v("SMSNotificationActivity.resizeLayout()");
		Display d = getWindowManager().getDefaultDisplay();
		int width = d.getWidth() > MAX_WIDTH ? MAX_WIDTH : (int) (d.getWidth() * WIDTH);
		mainLayout.setMinimumWidth(width);
		mainLayout.invalidate();
	}

	/**
	 * 
	 */
	private void refreshViews() {

//		    ManageKeyguard.initialize(this);
//		    if (ManageKeyguard.inKeyguardRestrictedInputMode()) {
//		      // Show unlock button
//		      buttonSwitcher.setDisplayedChild(BUTTON_SWITCHER_UNLOCK_BUTTON);
//
//		      // Disable long-press context menu
//		      unregisterForContextMenu(mSmsPopups);
//
//		      SmsPopupViewFlipper.setLockMode(true);
//
//		    } else {
//		      // Show main popup buttons
//		      buttonSwitcher.setDisplayedChild(BUTTON_SWITCHER_MAIN_BUTTONS);
//
//		      // Enable long-press context menu
//		      registerForContextMenu(mSmsPopups);
//
//		      SmsPopupViewFlipper.setLockMode(false);
//		    }
	}

	/**
	 * Close the notification window & mark the message read.
	 */
	private void closeNotification() {
		if (Log.DEBUG) Log.v("SMSNotificationActivity.closeNotification()");
		//TODO - Mark active message as read.
		finishActivity();
	}
	
	/**
	 * Customized activity finish.
	 * This closes this activity screen and removed it from the users screen.
	 */
	private void finishActivity() {
		if (Log.DEBUG) Log.v("SMSNotificationActivity.finishActivity()");
	    // Finish the activity
	    finish();
	}

}
