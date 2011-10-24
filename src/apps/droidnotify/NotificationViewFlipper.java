package apps.droidnotify;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This class is the main control window that displays and moves the Notifications.
 * 
 * @author Camille Sévigny
 */
public class NotificationViewFlipper extends ViewFlipper {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;
	private Context _context = null;
	private ArrayList<Notification> _notifications = null;
	private int _currentNotification = 0;
	private int _totalNotifications = 0;
	private SharedPreferences _preferences = null;
	private NotificationActivity _notificationActivity = null;

	//================================================================================
	// Constructors
	//================================================================================
	  
	/**
	 * Class Constructor.
	 */
	public NotificationViewFlipper(Context context) {
		super(context);
		_debug = Log.getDebug();
		if (_debug) Log.v("NotificationViewFlipper.NotificationViewFlipper()");
		_context = context;
		_preferences = PreferenceManager.getDefaultSharedPreferences(context);
		_notificationActivity = (NotificationActivity) context;
		_notifications = new ArrayList<Notification>(1);
		_totalNotifications = 0;
		_currentNotification = 0;
	}
	
	/**
	 * Class Constructor.
	 */	
	public  NotificationViewFlipper(Context context, AttributeSet attributes) {
		super(context, attributes);
		_debug = Log.getDebug();
		if (_debug) Log.v("NotificationViewFlipper.NotificationViewFlipper()");
		_context = context;
		_preferences = PreferenceManager.getDefaultSharedPreferences(context);
		_notificationActivity = (NotificationActivity) context;
		_notifications = new ArrayList<Notification>(1);
		_totalNotifications = 0;
		_currentNotification = 0;
	}
	  
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Add notification to the notifications ArrayList.
	 * Add new notification View to the ViewFlipper.
	 * 
	 * @param notification - Notification to add to the ArrayList.
	 */
	public void addNotification(Notification notification) {
		if (_debug) Log.v("NotificationViewFlipper.addNotification()");
		if(!containsNotification(notification)){
			_notifications.add(notification);
			_totalNotifications = _notifications.size();
			addView(new NotificationView(_context, notification));
			//Update the navigation information on the current View every time a new View is added.
			final View nextView = this.getChildAt(_currentNotification);
			updateViewNavigationButtons(nextView);
		}
	}

	/**
	 * Remove the current Notification.
	 */
	public void removeActiveNotification() {
		if (_debug) Log.v("NotificationViewFlipper.removeActiveNotification()");
		int notificationType = getActiveNotification().getNotificationType();
		removeNotification(_currentNotification);
    	//Clear the status bar notification.
    	Common.clearNotification(_context, this, notificationType, _totalNotifications);
	}

	/**
	 * Return the active Notification.
	 * The active Notification is the current message.
	 * 
	 * @return Notification - The Notification that is the current Notification.
	 */	
	public Notification getActiveNotification(){
		if (_debug) Log.v("NotificationViewFlipper.getActiveMessage()");
		return _notifications.get(_currentNotification);
	}

	/**
	 * Return the total notification count.
	 * 
	 * @return int - The number of current notifications.
	 */	
	public int getTotalNotifications(){
		if (_debug) Log.v("NotificationViewFlipper.getTotalNotifications()");
		return _totalNotifications;
	}
	
	/**
	 * Show the next Notification/View in the list.
	 */
	@Override
	public void showNext() {
		if (_debug) Log.v("NotificationViewFlipper.showNext()");
		if (_currentNotification < _totalNotifications - 1) {
			_currentNotification += 1;
			setInAnimation(inFromRightAnimation());
			setOutAnimation(outToLeftAnimation());
			//Update the navigation information on the next view before we switch to it.
			final View nextView = this.getChildAt(_currentNotification);
			updateViewNavigationButtons(nextView);
			//Flip to next View.
			super.showNext();
		}
	}
	  
	/**
	 * Show the previous Notification/View in the list.
	 */
	@Override
	public void showPrevious() {
		if (_debug) Log.v("NotificationViewFlipper.showPrevious()");
		if (_currentNotification > 0) {
			_currentNotification -= 1;
			setInAnimation(inFromLeftAnimation());
			setOutAnimation(outToRightAnimation());
			//Update the navigation information on the previous view before we switch to it.
			final View nextView = this.getChildAt(_currentNotification);
			updateViewNavigationButtons(nextView);
			//Flip to previous View.
			super.showPrevious();
		}
	}
	
	/**
	 * Display the delete dialog from the activity.
	 */
	public void showDeleteDialog(){
		if (_debug) Log.v("NotificationViewFlipper.showDeleteDialog()");
		_notificationActivity.showDeleteDialog();
	}
	
	/**
	 * Delete the current Notification.
	 */
	public void deleteMessage(){
		if (_debug) Log.v("NotificationViewFlipper.deleteMessage()");
		//Remove the notification from the ViewFlipper.
		Notification notification = getNotification(_currentNotification);
		int notificationType = notification.getNotificationType();
		switch(notificationType){
			case Constants.NOTIFICATION_TYPE_SMS:{
				if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_NOTHING)){
					//Remove the notification from the ViewFlipper
					removeActiveNotification();
				}else if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_DELETE_MESSAGE)){
					//Delete the current message from the users phone.
					notification.deleteMessage();
					//Remove the notification from the ViewFlipper
					removeActiveNotification();
				}else if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_DELETE_THREAD)){
					//Delete the current message from the users phone.
					//The notification will remove ALL messages for this thread from the phone for us.
					notification.deleteMessage();
					//Remove all Notifications with the thread ID.
					removeNotifications(notification.getThreadID());
				}
				break;
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
				if(_preferences.getString(Constants.MMS_DELETE_KEY, "0").equals(Constants.MMS_DELETE_ACTION_NOTHING)){
					//Remove the notification from the ViewFlipper
					removeActiveNotification();
				}else if(_preferences.getString(Constants.MMS_DELETE_KEY, "0").equals(Constants.MMS_DELETE_ACTION_DELETE_MESSAGE)){
					//Delete the current message from the users phone.
					notification.deleteMessage();
					//Remove the notification from the ViewFlipper
					removeActiveNotification();
				}else if(_preferences.getString(Constants.MMS_DELETE_KEY, "0").equals(Constants.MMS_DELETE_ACTION_DELETE_THREAD)){
					//Remove all Notifications with the thread ID.
					removeNotifications(notification.getThreadID());
					//Delete the current message from the users phone.
					//The notification will remove ALL messages for this thread from the phone for us.
					notification.deleteMessage();
					//Remove all Notifications with the thread ID.
					removeNotifications(notification.getThreadID());
				}
				break;
			}
		}
	}
	
	/**
	 * Check if there are any notifications of a certain type.
	 * 
	 * @param notificationType - The notification type.
	 * 
	 * @return boolean - Returns true if a notification of the supplied type is found.
	 */
	public boolean containsNotificationType(int notificationType){
		if (_debug) Log.v("NotificationViewFlipper.containsAnyNotificationType()");
		for (Notification currentNotification : _notifications) {
			if(currentNotification.getNotificationType() == notificationType){
				return true;
			}
		}
		return false;
	}

	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Retrieve the Notification at the current index.
	 * 
	 * @param notificationNumber - Index of a Notification in the ArrayList of Notifications.
	 * 
	 * @return Notification - Return the notification located at the specified index.
	 */
	private Notification getNotification(int notificationNumber){
		if (_debug) Log.v("NotificationViewFlipper.getNotification()");
		return _notifications.get(notificationNumber);
	}

	/**
	 * Determine if the current Notification is the last Notification in the list.
	 * 
	 * @return boolean - Returns true if the current Notification is the last Notification in the ArrayList.
	 */
	private boolean isLastMessage(){
		if (_debug) Log.v("NotificationViewFlipper.isLastMessage()");
		if((_currentNotification + 1) >= _totalNotifications){
			return true;
		}else{
			return false;
		}
	}
	  
	/**
	 * Determine if the current Notification is the first Notification in the list.
	 * 
	 * @return boolean - Returns true if the current Notification is the first Notification in the ArrayList.
	 */
	private boolean isFirstMessage(){
		if (_debug) Log.v("NotificationViewFlipper.isFirstMessage()");
		if((_currentNotification + 1) <= 1){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	* Remove the Notification and its view.
	*
	* @param notificationNumber - Int of the notificaiton to be removed.
	*/
	private void removeNotification(int notificationNumber) {
		if (_debug) Log.v("NotificationViewFlipper.removeNotification() NotificationNumber: " + notificationNumber);
		//Get the current notification object.
		Notification notification = getNotification(notificationNumber);
		if (_totalNotifications > 1) {
			try{
				//Set notification as being viewed in the phone.
				setNotificationViewed(notification);
				// Remove notification from the ArrayList.
				_notifications.remove(notificationNumber);
				// Fade out current notification.
				setOutAnimation(_context, android.R.anim.fade_out);
				// Update total notifications count.
				_totalNotifications = _notifications.size();
				// If we removed the last notification then set current notification to the last one.
				if (notificationNumber >= _totalNotifications) {
					_currentNotification = _totalNotifications - 1;
				}
				// If this is the last notification, slide in from left.
				if (notificationNumber == (_totalNotifications)) {
					setInAnimation(inFromLeftAnimation());
					//Update the navigation information on the previous view before we switch to it.
					final View previousView = this.getChildAt(notificationNumber - 1);
					updateViewNavigationButtons(previousView);
				} else{ // Else slide in from right.
					setInAnimation(inFromRightAnimation());
					//Update the navigation information on the next view before we switch to it.
					final View nextView = this.getChildAt(notificationNumber + 1);
					updateViewNavigationButtons(nextView);
				}
				// Remove the view from the ViewFlipper.
				removeViewAt(notificationNumber);
			}catch(Exception ex){
				if (_debug) Log.v("NotificationViewFlipper.removeNotification() [Total Notification > 1] ERROR: " + ex.toString());
			}
		}else{	
			try{
				//Set notification as being viewed in the phone.
				setNotificationViewed(notification);
				// Set the total notifications to 0
				_totalNotifications = 0;
				//Close the ViewFlipper and finish the activity.
				_notificationActivity.finishActivity();
			}catch(Exception ex){
				if (_debug) Log.v("NotificationViewFlipper.removeNotification() [TotalNotification <= 1] ERROR: " + ex.toString());
			}
		}
	}
	
	/**
	 * Animation of the moving of the a Notification that comes from the right.
	 * 
	 * @return Animation - Returns the Animation object.
	 */
	private Animation inFromRightAnimation() {
		if (_debug) Log.v("NotificationViewFlipper.inFromRightAnimation()");
		Animation inFromRight = new TranslateAnimation(
		Animation.RELATIVE_TO_PARENT, +1.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(350);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}
	  
	/**
	 * Animation of the moving of the a Notification that leaves to the left.
	 * 
	 * @return Animation - Returns the Animation object.
	 */
	private Animation outToLeftAnimation() {
		if (_debug) Log.v("NotificationViewFlipper.outToLeftAnimation()");
		Animation outtoLeft = new TranslateAnimation(
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, -1.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(350);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}
	  
	/**
	 * Animation of the moving of the a Notification that comes from the left.
	 * 
	 * @return Animation - Returns the Animation object.
	 */
	private Animation inFromLeftAnimation() {
		if (_debug) Log.v("NotificationViewFlipper.inFromLeftAnimation()");
		Animation inFromLeft = new TranslateAnimation(
		Animation.RELATIVE_TO_PARENT, -1.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(350);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}
	  
	/**
	 * Animation of the moving of the a Notification that leaves to the right.
	 * 
	 * @return Animation - Returns the Animation object.
	 */
	private Animation outToRightAnimation() {
		if (_debug) Log.v("NotificationViewFlipper.outToRightAnimation()");
		Animation outtoRight = new TranslateAnimation(
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, +1.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoRight.setDuration(350);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}
	
	/**
	 * Updates the navigation buttons on each of the Notification Views.
	 * 
	 * @param view - The View that we will update.
	 */
	private void updateViewNavigationButtons(View view){
    	if (_debug) Log.v("NotificationViewFlipper.updateNavigationButtons()");
		Button previousButton = (Button) view.findViewById(R.id.previous_button);
		TextView notificationCountTextView = (TextView) view.findViewById(R.id.notification_count_text_view);
		Button nextButton = (Button) view.findViewById(R.id.next_button);
    	if(isFirstMessage()){
    		previousButton.setVisibility(View.INVISIBLE);
    	}else{
    		previousButton.setVisibility(View.VISIBLE);
    	}
    	notificationCountTextView.setVisibility(View.VISIBLE);
    	notificationCountTextView.setText((_currentNotification + 1) + "/" + _totalNotifications);
    	nextButton.setEnabled(!isLastMessage());
    	if(isLastMessage()){
    		nextButton.setVisibility(View.INVISIBLE);
    	}else{
    		nextButton.setVisibility(View.VISIBLE);
    	}
    	//Hide notification header row if single notification.
    	if(_preferences.getBoolean(Constants.HIDE_SINGLE_MESSAGE_HEADER_KEY, false)){
	    	if(_notifications.size() == 1){
	    		previousButton.setVisibility(View.GONE);
	    		nextButton.setVisibility(View.GONE);
	    		notificationCountTextView.setVisibility(View.GONE);
	    	}
    	}
	}
	
	/**
	 * Remove all Notifications with this thread ID.
	 * 
	 * @param threadID - Thread ID of the Notifications to be removed.
	 */ 
	private void removeNotifications(long threadID){
		if (_debug) Log.v("NotificationViewFlipper.removeNotifications() Thread ID: " + threadID);
		//Must iterate backwards through this collection.
		//By removing items from the end, we don't have to worry about shifting index numbers as we would if we removed from the beginning.
		for(int i = _totalNotifications - 1; i >= 0; i--){
			if(getNotification(i).getThreadID() == threadID){
				removeNotification(i);
			}
		}
	}
	
	/**
	 * Set the notification as being viewed.
	 * Let the Notification object handle this method.
	 * 
	 * @param notification - The Notification to set as viewed.
	 */
	private void setNotificationViewed(Notification notification){
		if (_debug) Log.v("NotificationViewFlipper.setNotificationViewed()");
		notification.setViewed(true);
	}
	
	/**
	 * Checks the ViewFlipper's notification ArrayList and returns true if it contains this particular Notification.
	 * 
	 * @param newNotification - New Notification to compare.
	 * 
	 * @return boolean - Returns true if the ArrayList contains the passed in Notification.
	 */
	private boolean containsNotification(Notification newNotification) {
		if (_debug) Log.v("NotificationViewFlipper.containsNotification()");
		for (Notification currentNotification : _notifications) {
			if(newNotification.getSentFromAddress() == currentNotification.getSentFromAddress() && newNotification.getTimeStamp() == currentNotification.getTimeStamp()){
				return true;
			}
		}
		return false;
	}
	
}