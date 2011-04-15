package apps.droidnotify;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ViewFlipper;

/**
 *
 */
public class SMSNotificationViewFlipper extends ViewFlipper {

	//================================================================================
    // Properties
    //================================================================================
	
	private Context _context;
	private ArrayList<TextMessage> _messages;
	private int _currentmessage;
	private int _totalmessages;
	private boolean _lockMode;
	private float _oldTouchValue;

	//================================================================================
	// Constructors
	//================================================================================
	  
	/**
	 * SMSNotificationViewFlipper constructor.
	 */
	public SMSNotificationViewFlipper(Context context) {
		super(context);
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.SMSNotificationViewFlipper()");
		init(context);
	}
	
	/**
	 * SMSNotificationViewFlipper constructor.
	 */	
	public  SMSNotificationViewFlipper(Context context, AttributeSet attributes) {
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
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the messages property.
	 */ 
	public ArrayList<TextMessage> getMessages(){
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.getMessages()");
		return _messages;
	}
	
	/**
	 * Set the currentMessage property.
	 */ 
	public void setCurrentMessage(int currentmessage){
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.setCurrentMessage()");
		_currentmessage = currentmessage;
	}
	
	/**
	 * Get the currentMessage property.
	 */ 
	public int getCurrentMessage(){
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.getCurrentMessage()");
		return _currentmessage;
	}
	
	/**
	 * Set the totalmessages property.
	 */ 
	public void setTotalMessages(int totalmessages){
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.setTotalMessages()");
		_totalmessages = totalmessages;
	}	  
	
	/**
	 * Get the totalmessages property.
	 */
	public int getTotalMessages(){
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.getTotalMessages()");
		return _totalmessages;
	}
	
	//================================================================================
	// Public Methods
	//================================================================================

	/**
	 * Determine if the current message is the last message in the list.
	 */
	public Boolean isLastMessage(){
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.isLastMessage()");
		if((getCurrentMessage() + 1) >= getTotalMessages()){
			return true;
		}else{
			return false;
		}
	}
	  
	/**
	 * Determine if the current message is the first message in the list.
	 */
	public Boolean isFirstMessage(){
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.isFirstMessage()");
		if((getCurrentMessage() + 1) <= 1){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Add message to the messages list.
	 * Add new message View to the ViewFlipper.
	 */
	public void addMessage(TextMessage message) {
		 if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.addMessage()");
		_messages.add(message);
		setTotalMessages(_messages.size());
	    addView(new SMSNotificationViewer(getContext(), message)); 
	}
	
	/**
	 * Return the active message.
	 * The active message is the current message.
	 */	
	public TextMessage getActiveMessage(){
		return _messages.get(getCurrentMessage());
	}
	
//	/**
//	* Remove the message and its view and the location numMessage
//	*
//	* @param numMessage
//	* @return true if there were no more messages to remove, false otherwise
//	*/
//	public boolean removeMessage(int message_number) {
//		if (message_number < getTotalMessages() && message_number >= 0 && getTotalMessages() > 1) {
//			// Fadeout current message
//			setOutAnimation(_context, android.R.anim.fade_out);
//	
//			// If last message, slide in from left
//			if (message_number == (getTotalMessages()-1)) {
//				setInAnimation(inFromLeftAnimation());
//			} else{ // Else slide in from right
//				setInAnimation(inFromRightAnimation());
//			}
//			// Remove the view
//			removeViewAt(message_number);
//			// Remove message from arraylist
//			_messages.remove(message_number);
//			// Update total messages count
//			getTotalMessages() = _messages.size();
//			// If we removed the last message then set current message to last
//			if (getCurrentMessage() >= getTotalMessages()) {
//				getCurrentMessage() = getTotalMessages() - 1;
//			}
//			// If more messages, return false
//			if (getTotalMessages() > 0)
//				return false;
//		}
//		return true;
//	}
//
//	/**
//	* Remove the currently active message, if there is only one message left then it will not
//	* be removed
//	*
//	* @return true if there were no more messages to remove, false otherwise
//	*/
//	public boolean removeActiveMessage() {
//		return removeMessage(getCurrentMessage());
//	}
	  
	/**
	 *
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.onTouchEvent()");
		if (_lockMode) return true;
		switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.onTouchEvent() ACTION_MOVE");
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
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.showNext()");
		if (getCurrentMessage() < getTotalMessages()-1) {
			setCurrentMessage(getCurrentMessage() + 1);
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
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.showPrevious()");
		if (getCurrentMessage() > 0) {
			setCurrentMessage(getCurrentMessage() - 1);
			setInAnimation(inFromLeftAnimation());
			setOutAnimation(outToRightAnimation());
			super.showPrevious();
		}
	}
	  
	/**
	 * Function to animate the moving of the a message that comes form the right.
	 */
	private Animation inFromRightAnimation() {
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.inFromRightAnimation()");
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
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.outToLeftAnimation()");
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
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.inFromLeftAnimation()");
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
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.outToRightAnimation()");
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
	 * Initialize the ViewFlipper properties.
	 */
	private void init(Context context) {
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.init()");
		//setContext(context);
		_messages = new ArrayList<TextMessage>(1);
		setTotalMessages(0);
		setCurrentMessage(0);
	}

}
