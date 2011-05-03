package apps.droidnotify;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ViewFlipper;

/**
 * 
 * @author Camille Sevigny
 *
 */
public class NotificationViewFlipper extends ViewFlipper {

	//================================================================================
    // Constants
    //================================================================================
	
	private final int NOTIFICATION_TYPE_PHONE = 0;
	private final int NOTIFICATION_TYPE_SMS = 1;
	private final int NOTIFICATION_TYPE_MMS = 2;
	private final int NOTIFICATION_TYPE_CALENDAR = 3;
	private final int NOTIFICATION_TYPE_EMAIL = 4;
	
	final String SMS_DELETE_KEY = "sms_delete_button_action";
	final String MMS_DELETE_KEY = "mms_delete_button_action";
	final String SMS_DISMISS_ACTION_MARK_READ = "0";
	final String SMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	final String SMS_DELETE_ACTION_DELETE_THREAD = "1";
	final String SMS_DELETE_ACTION_NOTHING = "2";
	final String MMS_DISMISS_ACTION_MARK_READ = "0";
	final String MMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	final String MMS_DELETE_ACTION_DELETE_THREAD = "1";
	final String MMS_DELETE_ACTION_NOTHING = "2";
	
	//================================================================================
    // Properties
    //================================================================================
	
	private Context _context;
	private ArrayList<Notification> _notifications;
	private int _currentNotification;
	private int _totalNotifications;
	private boolean _lockMode;
	private float _oldTouchValue;

	//================================================================================
	// Constructors
	//================================================================================
	  
	/**
	 * NotificationViewFlipper constructor.
	 */
	public NotificationViewFlipper(Context context) {
		super(context);
		if (Log.getDebug()) Log.v("NotificationViewFlipper.NotificationViewFlipper()");
		init(context);
	}
	
	/**
	 * NotificationViewFlipper constructor.
	 */	
	public  NotificationViewFlipper(Context context, AttributeSet attributes) {
		super(context, attributes);
		init(context);
	}
	  
	//================================================================================
	// Accessors
	//================================================================================

	/**
	 * Set the context property.
	 */
	public void setContext(Context context) {
		if (Log.getDebug()) Log.v("NotificationViewFlipper.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the notifications property.
	 */ 
	public ArrayList<Notification> getNotifications(){
		if (Log.getDebug()) Log.v("NotificationViewFlipper.getNotifications()");
		return _notifications;
	}
	
	/**
	 * Set the currentNotification property.
	 */ 
	public void setCurrentNotification(int currentNotification){
		if (Log.getDebug()) Log.v("NotificationViewFlipper.setCurrentNotification()");
		_currentNotification = currentNotification;
	}
	
	/**
	 * Get the currentNotification property.
	 */ 
	public int getCurrentNotification(){
		if (Log.getDebug()) Log.v("NotificationViewFlipper.getCurrentNotification()");
		return _currentNotification;
	}
	
	/**
	 * Set the totalNotifications property.
	 */ 
	public void setTotalNotifications(int totalNotifications){
		if (Log.getDebug()) Log.v("NotificationViewFlipper.setTotalNotifications()");
		_totalNotifications = totalNotifications;
	}	  
	
	/**
	 * Get the totalNotifications property.
	 */
	public int getTotalNotifications(){
		if (Log.getDebug()) Log.v("NotificationViewFlipper.getTotalNotifications()");
		return _totalNotifications;
	}
	
	//================================================================================
	// Public Methods
	//================================================================================

	/**
	 * Retrieve the Notification at the current index.
	 */
	public Notification getNotification(int notificationNumber){
		return _notifications.get(notificationNumber);
	}
	
	/**
	 * Determine if the current message is the last message in the list.
	 */
	public boolean isLastMessage(){
		if (Log.getDebug()) Log.v("NotificationViewFlipper.isLastMessage()");
		if((getCurrentNotification() + 1) >= getTotalNotifications()){
			return true;
		}else{
			return false;
		}
	}
	  
	/**
	 * Determine if the current message is the first message in the list.
	 */
	public boolean isFirstMessage(){
		if (Log.getDebug()) Log.v("NotificationViewFlipper.isFirstMessage()");
		if((getCurrentNotification() + 1) <= 1){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Add notification to the notifications ArrayList.
	 * Add new notification View to the ViewFlipper.
	 * 
	 * @param notification
	 */
	public void addNotification(Notification notification) {
		if (Log.getDebug()) Log.v("NotificationViewFlipper.addNotification()");
		if(!containsNotification(notification)){
			getNotifications().add(notification);
			setTotalNotifications(_notifications.size());
			addView(new NotificationView(getContext(), notification)); 
		}
	}

	/**
	 * Remove the currently active message.
	 *
	 */
	public void removeActiveNotification() {
		if (Log.getDebug()) Log.v("NotificationViewFlipper.removeActiveNotification()");
		removeNotification(getCurrentNotification());
	}
	
	/**
	* Remove the message and its view.
	*
	* @param notificationNumber
	*/
	public void removeNotification(int notificationNumber) {
		if (Log.getDebug()) Log.v("NotificationViewFlipper.removeNotification()");
		//Get the current notification object.
		Notification notification = getNotification(notificationNumber);
		int totalNotifications = getTotalNotifications();
		ArrayList<Notification> notifications = getNotifications();
		Context context = getContext();
		if (totalNotifications > 1) {
			// Fade out current notification.
			setOutAnimation(context, android.R.anim.fade_out);
			// If this is the last notification, slide in from left.
			if (notificationNumber == (totalNotifications-1)) {
				setInAnimation(inFromLeftAnimation());
			} else{ // Else slide in from right.
				setInAnimation(inFromRightAnimation());
			}
			// Remove the view from the ViewFlipper.
			removeViewAt(notificationNumber);
			//Set notification as being viewed in the phone.
			setNotificationViewed(notification);
			// Remove notification from the ArrayList.
			notifications.remove(notificationNumber);
			// Update total notifications count.
			setTotalNotifications(notifications.size());
			// If we removed the last notification then set current notification to the last one.
			if (notificationNumber >= totalNotifications) {
				setCurrentNotification(totalNotifications - 1);
			}
			//Update the activities navigation buttons.
			((NotificationActivity)context).updateNavigationButtons();
		}else{
			//Set notification as being viewed in the phone.
			setNotificationViewed(notification);
			//Close the ViewFlipper and finish the activity.
			((NotificationActivity)context).finishActivity();
		}
	}

	/**
	 * Return the active message.
	 * The active message is the current message.
	 */	
	public Notification getActiveMessage(){
		if (Log.getDebug()) Log.v("NotificationViewFlipper.getActiveMessage()");
		return getNotifications().get(getCurrentNotification());
	}
	
	/**
	 *
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (Log.getDebug()) Log.v("NotificationViewFlipper.onTouchEvent()");
		if (_lockMode) return true;
		switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				if (Log.getDebug()) Log.v("NotificationViewFlipper.onTouchEvent() ACTION_MOVE");
			final View currentView = getCurrentView();
			currentView.layout((int) (event.getX() - _oldTouchValue), currentView.getTop(),
			currentView.getRight(), currentView.getBottom());
			_oldTouchValue = event.getX();
			break;
		}
		return super.onTouchEvent(event);
	}
	  
	/**
	 * Function to show the next message in the list.
	 */
	@Override
	public void showNext() {
		if (Log.getDebug()) Log.v("NotificationViewFlipper.showNext()");
		if (getCurrentNotification() < getTotalNotifications()-1) {
			setCurrentNotification(getCurrentNotification() + 1);
			setInAnimation(inFromRightAnimation());
			setOutAnimation(outToLeftAnimation());
			super.showNext();
		}
	}
	  
	/**
	 * Function to show the previous message in the list.
	 */
	@Override
	public void showPrevious() {
		if (Log.getDebug()) Log.v("NotificationViewFlipper.showPrevious()");
		if (getCurrentNotification() > 0) {
			setCurrentNotification(getCurrentNotification() - 1);
			setInAnimation(inFromLeftAnimation());
			setOutAnimation(outToRightAnimation());
			super.showPrevious();
		}
	}
	
	/**
	 * Display the delete dialog from the activity and return the result.
	 * 
	 * @return Boolean of the confirmation of delete. 
	 */
	public void showDeleteDialog(){
		if (Log.getDebug()) Log.v("NotificationViewFlipper.showDeleteDialog()");
		((NotificationActivity)getContext()).showDeleteDialog();
	}
	
	/**
	 * Delete the current message from the users phone.
	 */
	public void deleteMessage(){
		if (Log.getDebug()) Log.v("NotificationViewFlipper.deleteMessage()");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		//Remove the notification from the ViewFlipper.
		Notification notification = getNotification(getCurrentNotification());
		int notificationType = notification.getNotificationType();
		if(notificationType == NOTIFICATION_TYPE_SMS){
			if(preferences.getString(SMS_DELETE_KEY, "0").equals(SMS_DELETE_ACTION_NOTHING)){
				//Remove the notification from the ViewFlipper
				removeActiveNotification();
			}else if(preferences.getString(SMS_DELETE_KEY, "0").equals(SMS_DELETE_ACTION_DELETE_MESSAGE)){
				//Delete the current message from the users phone.
				notification.deleteMessage();
				//Remove the notification from the ViewFlipper
				removeActiveNotification();
			}else if(preferences.getString(SMS_DELETE_KEY, "0").equals(SMS_DELETE_ACTION_DELETE_THREAD)){
				//Delete the current message from the users phone.
				//The notification will remove ALL messages for this thread from the phone for us.
				notification.deleteMessage();
				//Remove all Notifications with the thread ID.
				removeNotifications(notification.getThreadID());
			}
		}else if(notificationType == NOTIFICATION_TYPE_MMS){
			if(preferences.getString(MMS_DELETE_KEY, "0").equals(MMS_DELETE_ACTION_NOTHING)){
				//Remove the notification from the ViewFlipper
				removeActiveNotification();
			}else if(preferences.getString(MMS_DELETE_KEY, "0").equals(MMS_DELETE_ACTION_DELETE_MESSAGE)){
				//Delete the current message from the users phone.
				notification.deleteMessage();
			}else if(preferences.getString(MMS_DELETE_KEY, "0").equals(MMS_DELETE_ACTION_DELETE_THREAD)){
				//Remove all Notifications with the thread ID.
				removeNotifications(notification.getThreadID());
				//Delete the current message from the users phone.
				//The notification will remove ALL messages for this thread from the phone for us.
				notification.deleteMessage();
			}
		}
	}
	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Remove all Notifications with this thread ID.
	 * 
	 * @param threadID
	 */ 
	private void removeNotifications(long threadID){
		if (Log.getDebug()) Log.v("NotificationViewFlipper.removeNotifications() Thread ID: " + threadID);
		//Must iterate backwards through this collection.
		//By removing items from the end, we don't have to worry about shifting index numbers 
		//as we would if we removed from the beginning.
		for(int i = getTotalNotifications() - 1; i >= 0; i--){
			if(getNotification(i).getThreadID() == threadID){
				removeNotification(i);
			}
		}
	}
	
	/**
	 * Function to animate the moving of the a message that comes form the right.
	 */
	private Animation inFromRightAnimation() {
		if (Log.getDebug()) Log.v("NotificationViewFlipper.inFromRightAnimation()");
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
	 * Function to animate the moving of the a message that leaves to the left.
	 */
	private Animation outToLeftAnimation() {
		if (Log.getDebug()) Log.v("NotificationViewFlipper.outToLeftAnimation()");
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
	 * Function to animate the moving of the a message that comes form the left.
	 */
	private Animation inFromLeftAnimation() {
		if (Log.getDebug()) Log.v("NotificationViewFlipper.inFromLeftAnimation()");
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
	 * Function to animate the moving of the a message that leaves to the right.
	 */
	private Animation outToRightAnimation() {
		if (Log.getDebug()) Log.v("NotificationViewFlipper.outToRightAnimation()");
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
	 * Initialize the ViewFlipper properties.
	 */
	private void init(Context context) {
		if (Log.getDebug()) Log.v("NotificationViewFlipper.init()");
		setContext(context);
		_notifications = new ArrayList<Notification>(1);
		setTotalNotifications(0);
		setCurrentNotification(0);
	}
	
	/**
	 * Set the notification as being viewed.
	 * Let the Notification object handle this method.
	 * 
	 * @param notification
	 */
	private void setNotificationViewed(Notification notification){
		if (Log.getDebug()) Log.v("NotificationViewFlipper.setNotificationViewed()");
		notification.setViewed(true);
	}
	
	/**
	 * Checks the ViewFlipper's notifications ArrayList if it contains this particular Notification.
	 * 
	 * @param notification
	 * 
	 * @return boolean
	 */
	private boolean containsNotification(Notification notification) {
		if (Log.getDebug()) Log.v("NotificationViewFlipper.containsNotification()");
		ArrayList<Notification> notifications = getNotifications();
		for(int i = 0; i < notifications.size(); i++){
			String phoneNumber = notifications.get(i).getPhoneNumber();
			long timeStamp = notifications.get(i).getTimeStamp();
			if (Log.getDebug()) Log.v("NotificationViewFlipper.containsNotification() Checking Notification " + i + " PhoneNumber: " + phoneNumber + " TimeStamp: " + timeStamp);
			if(notification.getPhoneNumber() == phoneNumber && notification.getTimeStamp() == timeStamp){
				if (Log.getDebug()) Log.v("NotificationViewFlipper.containsNotification() TRUE");
				return true;
			}
		}
		if (Log.getDebug()) Log.v("NotificationViewFlipper.containsNotification() FALSE");
		return false;
	}

}
