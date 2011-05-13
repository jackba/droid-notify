package apps.droidnotify;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ViewFlipper;

/**
 * This class is the main control window that displays and moves the Notifications.
 * 
 * @author Camille Sévigny
 */
public class NotificationViewFlipper extends ViewFlipper {

	//================================================================================
    // Constants
    //================================================================================
	
	private final int NOTIFICATION_TYPE_SMS = 1;
	private final int NOTIFICATION_TYPE_MMS = 2;
	
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
	
	private ArrayList<Notification> _notifications;
	private int _currentNotification;
	private int _totalNotifications;

	//================================================================================
	// Constructors
	//================================================================================
	  
	/**
	 * Class Constructor.
	 */
	public NotificationViewFlipper(Context context) {
		super(context);
		if (Log.getDebug()) Log.v("NotificationViewFlipper.NotificationViewFlipper()");
		init(context);
	}
	
	/**
	 * Class Constructor.
	 */	
	public  NotificationViewFlipper(Context context, AttributeSet attributes) {
		super(context, attributes);
		init(context);
	}
	  
	//================================================================================
	// Accessors
	//================================================================================
	
	/**
	 * Get the notifications property.
	 * 
	 * @return ArrayList<Notification> - Returns the ArrayList of the Notifications.
	 */ 
	public ArrayList<Notification> getNotifications(){
		if (Log.getDebug()) Log.v("NotificationViewFlipper.getNotifications()");
		return _notifications;
	}
	
	/**
	 * Set the currentNotification property.
	 * 
	 * @param currentNotification - Int value of the current Notification.
	 */ 
	public void setCurrentNotification(int currentNotification){
		if (Log.getDebug()) Log.v("NotificationViewFlipper.setCurrentNotification()");
		_currentNotification = currentNotification;
	}
	
	/**
	 * Get the currentNotification property.
	 * 
	 * @return int - Int value of the current Notification.
	 */ 
	public int getCurrentNotification(){
		if (Log.getDebug()) Log.v("NotificationViewFlipper.getCurrentNotification()");
		return _currentNotification;
	}
	
	/**
	 * Set the totalNotifications property.
	 * 
	 * @param totalNotifications - Int value of the total number of notifications.
	 */ 
	public void setTotalNotifications(int totalNotifications){
		if (Log.getDebug()) Log.v("NotificationViewFlipper.setTotalNotifications()");
		_totalNotifications = totalNotifications;
	}	  
	
	/**
	 * Get the totalNotifications property.
	 * 
	 * @return int - Int value of the total number of notifications.
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
	 * 
	 * @param notificationNumber - Index of a Notification in the ArrayList of Notifications.
	 * 
	 * @return Notification - Return the notification located at the specified index.
	 */
	public Notification getNotification(int notificationNumber){
		return _notifications.get(notificationNumber);
	}
	
	/**
	 * Determine if the current Notification is the last Notification in the list.
	 * 
	 * @return boolean - Returns true if the current Notification is the last Notification in the ArrayList.
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
	 * Determine if the current Notification is the first Notification in the list.
	 * 
	 * @return boolean - Returns true if the current Notification is the first Notification in the ArrayList.
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
	 * @param notification - Notification to add to the ArrayList.
	 */
	public void addNotification(Notification notification) {
		if (Log.getDebug()) Log.v("NotificationViewFlipper.addNotification()");
		Context context = getContext();
		if(!containsNotification(notification)){
			getNotifications().add(notification);
			setTotalNotifications(_notifications.size());
			addView(new NotificationView(context, notification)); 
		}
	}

	/**
	 * Remove the current Notification.
	 */
	public void removeActiveNotification() {
		if (Log.getDebug()) Log.v("NotificationViewFlipper.removeActiveNotification()");
		removeNotification(getCurrentNotification());
	}
	
	/**
	* Remove the Notification and its view.
	*
	* @param notificationNumber - Int of the notificaiton to be removed.
	*/
	public void removeNotification(int notificationNumber) {
		if (Log.getDebug()) Log.v("NotificationViewFlipper.removeNotification() NotificationNumber: " + notificationNumber);
		//Get the current notification object.
		Notification notification = getNotification(notificationNumber);
		int totalNotifications = getTotalNotifications();
		Context context = getContext();
		NotificationActivity notificationActivity = (NotificationActivity)context;
		if (totalNotifications > 1) {
			try{
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
				_notifications.remove(notificationNumber);
				// Update total notifications count.
				setTotalNotifications(_notifications.size());
				totalNotifications -= 1;
				// If we removed the last notification then set current notification to the last one.
				if (notificationNumber >= totalNotifications) {
					setCurrentNotification(totalNotifications - 1);
				}
				//Update the activities navigation buttons.
				notificationActivity.updateNavigationButtons();
			}catch(Exception ex){
				if (Log.getDebug()) Log.v("NotificationViewFlipper.removeNotification() [Total Notification > 1] ERROR: " + ex.toString());
			}
		}else{
			//Set notification as being viewed in the phone.
			try{
				setNotificationViewed(notification);
				//Close the ViewFlipper and finish the activity.
				notificationActivity.finishActivity();
			}catch(Exception ex){
				if (Log.getDebug()) Log.v("NotificationViewFlipper.removeNotification() [TotalNotification <= 1] ERROR: " + ex.toString());
			}
		}
	}

	/**
	 * Return the active Notification.
	 * The active Notification is the current message.
	 * 
	 * @return Notification - The Notification that is the current Notification.
	 */	
	public Notification getActiveMessage(){
		if (Log.getDebug()) Log.v("NotificationViewFlipper.getActiveMessage()");
		return getNotifications().get(getCurrentNotification());
	}
	
	/**
	 * Show the next Notification/View in the list.
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
	 * Show the previous Notification/View in the list.
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
	 * Display the delete dialog from the activity.
	 */
	public void showDeleteDialog(){
		if (Log.getDebug()) Log.v("NotificationViewFlipper.showDeleteDialog()");
		((NotificationActivity)getContext()).showDeleteDialog();
	}
	
	/**
	 * Delete the current Notification.
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
	
	
	/**
	 * Animation of the moving of the a Notification that comes from the right.
	 * 
	 * @return Animation - Returns the Animation object.
	 */
	public Animation inFromRightAnimation() {
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
	 * Animation of the moving of the a Notification that leaves to the left.
	 * 
	 * @return Animation - Returns the Animation object.
	 */
	public Animation outToLeftAnimation() {
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
	 * Animation of the moving of the a Notification that comes from the left.
	 * 
	 * @return Animation - Returns the Animation object.
	 */
	public Animation inFromLeftAnimation() {
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
	 * Animation of the moving of the a Notification that leaves to the right.
	 * 
	 * @return Animation - Returns the Animation object.
	 */
	public Animation outToRightAnimation() {
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
	
	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Remove all Notifications with this thread ID.
	 * 
	 * @param threadID - Thread ID of the Notifications to be removed.
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
	 * Initialize the ViewFlipper properties.
	 * 
	 * @param context - Application Context.
	 */
	private void init(Context context) {
		if (Log.getDebug()) Log.v("NotificationViewFlipper.init()");
		_notifications = new ArrayList<Notification>(1);
		setTotalNotifications(0);
		setCurrentNotification(0);
	}
	
	/**
	 * Set the notification as being viewed.
	 * Let the Notification object handle this method.
	 * 
	 * @param notification - The Notification to set as viewed.
	 */
	private void setNotificationViewed(Notification notification){
		if (Log.getDebug()) Log.v("NotificationViewFlipper.setNotificationViewed()");
		notification.setViewed(true);
	}
	
	/**
	 * Checks the ViewFlipper's notification ArrayList and returns true if it contains this particular Notification.
	 * 
	 * @param notification - Notification to compare.
	 * 
	 * @return boolean - Returns true if the ArrayList contains the passed in Notification.
	 */
	private boolean containsNotification(Notification notification) {
		if (Log.getDebug()) Log.v("NotificationViewFlipper.containsNotification()");
		ArrayList<Notification> notifications = getNotifications();
		for(int i = 0; i < notifications.size(); i++){
			String phoneNumber = notifications.get(i).getPhoneNumber();
			long timeStamp = notifications.get(i).getTimeStamp();
			if(notification.getPhoneNumber() == phoneNumber && notification.getTimeStamp() == timeStamp){
				if (Log.getDebug()) Log.v("NotificationViewFlipper.containsNotification() TRUE");
				return true;
			}
		}
		if (Log.getDebug()) Log.v("NotificationViewFlipper.containsNotification() FALSE");
		return false;
	}

}
