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
	private int _currentMessage;
	private int _totalMessages;
	private static boolean lockMode;
	private float oldTouchValue;

	//================================================================================
	// Constructors
	//================================================================================
	  
	/**
	 *
	 */
	public SMSNotificationViewFlipper(Context context) {
		super(context);
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.SMSNotificationViewFlipper()");
		init(context);
	}
	  
	/**
	 *
	 */
	public SMSNotificationViewFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.SMSNotificationViewFlipper()");
		init(context);
	}

	//================================================================================
	// Accessors
	//================================================================================
	  
	/**
	 *
	 */ 
	public int getCurrentMessage(){
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.getCurrentMessage() Current Message: " + _currentMessage);
		return _currentMessage;
	}
	  
	/**
	 *
	 */
	public int getTotalMessages(){
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.getTotalMessages() Total Messages: " + _totalMessages);
		return _totalMessages;
	}
	  
	/**
	 *
	 */
	public Boolean isLastMessage(){
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.isLastMessage() Is Last Message: " + (_currentMessage == _totalMessages));
		if((_currentMessage + 1) >= _totalMessages){
			return true;
		}else{
			return false;
		}
	}
	  
	/**
	 *
	 */
	public Boolean isFirstMessage(){
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.isFirstMessage() Is First Message: " + (_currentMessage <= 1));
		if((_currentMessage + 1) <= 1){
			return true;
		}else{
			return false;
		}
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
	  
	/**
	 *
	 */
	public void addMessage(TextMessage message) {
		 if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.addMessage()");
		_messages.add(message);
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.addMessage() Added message");
	    _totalMessages = _messages.size();
	    if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.addMessage() Set message size)");
	    addView(new SMSNotificationViewer(_context, message)); 
	    if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.addMessage() Created mesage View)");
	}
	
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
//		if (message_number < _totalMessages && message_number >= 0 && _totalMessages > 1) {
//			// Fadeout current message
//			setOutAnimation(_context, android.R.anim.fade_out);
//	
//			// If last message, slide in from left
//			if (message_number == (_totalMessages-1)) {
//				setInAnimation(inFromLeftAnimation());
//			} else{ // Else slide in from right
//				setInAnimation(inFromRightAnimation());
//			}
//			// Remove the view
//			removeViewAt(message_number);
//			// Remove message from arraylist
//			_messages.remove(message_number);
//			// Update total messages count
//			_totalMessages = _messages.size();
//			// If we removed the last message then set current message to last
//			if (_currentMessage >= _totalMessages) {
//				_currentMessage = _totalMessages - 1;
//			}
//			// If more messages, return false
//			if (_totalMessages > 0)
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
//		return removeMessage(_currentMessage);
//	}
	  
	/**
	 *
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.onTouchEvent()");
		if (lockMode) return true;
		switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.onTouchEvent() ACTION_MOVE");
			final View currentView = getCurrentView();
			currentView.layout((int) (event.getX() - oldTouchValue), currentView.getTop(),
			currentView.getRight(), currentView.getBottom());
			oldTouchValue = event.getX();
			break;
		}
		return super.onTouchEvent(event);
	}
	  
	/**
	 *
	 */
	@Override
	public void showNext() {
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.showNext()");
		if (_currentMessage < _totalMessages-1) {
			_currentMessage += 1;
			setInAnimation(inFromRightAnimation());
			setOutAnimation(outToLeftAnimation());
			super.showNext();
			if (Log.DEBUG) Log.v("showNext() - " + _currentMessage);
		}
	}
	  
	/**
	 *
	 */
	@Override
	public void showPrevious() {
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.showPrevious()");
		if (_currentMessage > 0) {
			_currentMessage -= 1;
			setInAnimation(inFromLeftAnimation());
			setOutAnimation(outToRightAnimation());
			super.showPrevious();			
			if (Log.DEBUG) Log.v("showPrevious() - " + _currentMessage);
		}
	}
	  
	/**
	 *
	 */
	public static Animation inFromRightAnimation() {
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
	 *
	 */
	public static Animation outToLeftAnimation() {
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
	 *
	 */
	public static Animation inFromLeftAnimation() {
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
	 *
	 */
	public static Animation outToRightAnimation() {
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
	 *
	 */
	private void init(Context context) {
		if (Log.DEBUG) Log.v("SMSNotificationViewFlipper.init()");
		_context = context;
		_messages = new ArrayList<TextMessage>(1);
		_totalMessages = 0;
		_currentMessage = 0;
	}

}
