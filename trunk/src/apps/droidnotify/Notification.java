package apps.droidnotify;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This is the Notification class that holds all the information about all notifications we will display to the user.
 * 
 * @author Camille Sévigny
 */
public class Notification {

	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;
	private Context _context = null;
	private SharedPreferences _preferences = null;
	private String _sentFromAddress = null;
	private String _messageBody = null;
	private long _timeStamp;
	private long _threadID = 0;
	private long _contactID = 0;
	private String _contactName = null;
	private long _photoID = 0;
	private Bitmap _photoImg = null;
	private int _notificationType = 0;
	private long _messageID = 0;
	private boolean _contactExists = false;
	private boolean _contactPhotoExists = false;
	private String _title = null;
	private String _email = null;
	private long _calendarID = 0;
	private long _calendarEventID = 0;
	private long _calendarEventStartTime = 0;
	private long _calendarEventEndTime = 0;
	private boolean _allDay = false;
	private long _callLogID = 0;
	private String _lookupKey = null;
	
	//================================================================================
	// Constructors
	//================================================================================

	/**
	 * Class Constructor
	 */
	public Notification(Context context, String sentFromAddress, String messageBody, long messageID, long threadID, long timeStamp, long contactID, String contactName, long photoID, String lookupKey, int notificationType) {
		_debug = Log.getDebug();
		if (_debug) Log.v("Notification.Notification(Context contex, String sentFromAddress, String messageBody, long messageID, long threadID, long timeStamp, long contactID, String contactName, long photoID, String lookupKey, int notificationType)");
		try{
			if(notificationType == Constants.NOTIFICATION_TYPE_PHONE){
				_title = "Missed Call";
		    }
			if(notificationType == Constants.NOTIFICATION_TYPE_SMS){
				_title = "SMS Message";
			}
			if(notificationType == Constants.NOTIFICATION_TYPE_MMS){
				_title = "MMS Message";	
		    }
		    if(notificationType == Constants.NOTIFICATION_TYPE_CALENDAR){
		    	_title = "Calendar Event";
		    }
		    if(notificationType == Constants.NOTIFICATION_TYPE_GMAIL){
		    	_title = "Email";
		    }
			_context = context;
			_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
			_contactExists = false;
			_contactPhotoExists = false;
			_notificationType = notificationType;
    		_sentFromAddress = sentFromAddress.toLowerCase();
    		_messageBody = messageBody;
    		_messageID = messageID;
    		_threadID = threadID;
    		_timeStamp = timeStamp;
    		_contactID = contactID;
    		if(contactName.equals("")){
    			_contactName = null;
    			_contactExists = false;
    		}else{
    			_contactName = contactName;
    			_contactExists = true;
    		}
    		_photoID = photoID;
    		if(photoID == 0){
    			_contactPhotoExists = false;
    		}else{
    			_contactPhotoExists = true;
    		}
    		_lookupKey = lookupKey;
		}catch(Exception ex){
			if (_debug) Log.v("Notification.Notification(Context contex, String sentFromAddress, String messageBody, long messageID, long threadID, long timeStamp, long contactID, String contactName, long photoID, String lookupKey, int notificationType) ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Class Constructor
	 */
	public Notification(Context context, String sentFromAddress, String messageBody, long timeStamp, int notificationType) {
		_debug = Log.getDebug();
		if (_debug) Log.v("Notification.Notification(Context context, String phoneNumber, String messageBody, long timeStamp, int notificationType)");
		try{			
			switch(notificationType){
				case Constants.NOTIFICATION_TYPE_PHONE:{
					_title = "Missed Call";
					break;
				}
				case Constants.NOTIFICATION_TYPE_SMS:{
					_title = "SMS Message";
					break;
				}
				case Constants.NOTIFICATION_TYPE_MMS:{
					_title = "MMS Message";	
					break;
				}
				case Constants.NOTIFICATION_TYPE_CALENDAR:{
					_title = "Calendar Event";
					break;
				}
				case Constants.NOTIFICATION_TYPE_GMAIL:{
					_title = "Email";
					break;
				}
				case Constants.NOTIFICATION_TYPE_TWITTER:{
					_title = "Twitter";
					break;
				}
				case Constants.NOTIFICATION_TYPE_FACEBOOK:{
					_title = "Fascebook";
					break;
				}
			}
			_context = context;
			_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
			_contactExists = false;
			_contactPhotoExists = false;
			_notificationType = notificationType;
	        _timeStamp = timeStamp;
			_sentFromAddress = sentFromAddress.toLowerCase();
	        _messageBody = messageBody;
		}catch(Exception ex){
			if (_debug) Log.v("Notification.Notification(Context context, String phoneNumber, String messageBody, long timeStamp, int notificationType) ERROR: " + ex.toString());
		}
	}

	/**
	 * Class Constructor
	 */
	public Notification(Context context, long callLogID, String sentFromAddress, long timeStamp, long contactID, String contactName, long photoID, String lookupKey, int notificationType){
		_debug = Log.getDebug();
		if (_debug) Log.v("Notification.Notification(Context context, long callLogID, String sentFromAddress, long timeStamp, long contactID, string contactName, long photoID, String lookupKey, int notificationType)");
		try{
			switch(notificationType){
				case Constants.NOTIFICATION_TYPE_PHONE:{
					_title = "Missed Call";
					break;
				}
				case Constants.NOTIFICATION_TYPE_SMS:{
					_title = "SMS Message";
					break;
				}
				case Constants.NOTIFICATION_TYPE_MMS:{
					_title = "MMS Message";	
					break;
				}
				case Constants.NOTIFICATION_TYPE_CALENDAR:{
					_title = "Calendar Event";
					break;
				}
				case Constants.NOTIFICATION_TYPE_GMAIL:{
					_title = "Email";
					break;
				}
				case Constants.NOTIFICATION_TYPE_TWITTER:{
					_title = "Twitter";
					break;
				}
				case Constants.NOTIFICATION_TYPE_FACEBOOK:{
					_title = "Fascebook";
					break;
				}
			}
			_context = context;
			_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
			_contactExists = false;
			_contactPhotoExists = false;
			_notificationType = notificationType;
			if(Common.isPrivateUnknownNumber(sentFromAddress)){
				_sentFromAddress = "Private Number";
			}else{
				_sentFromAddress = sentFromAddress.toLowerCase();
			}
    		_timeStamp = timeStamp;
    		_contactID = contactID;
    		_callLogID = callLogID;
    		if(contactName.equals("")){
    			_contactName = null;
    			_contactExists = false;
    		}else{
    			_contactName = contactName;
    			_contactExists = true;
    		}
    		_photoID = photoID;
    		if(photoID == 0){
    			_contactPhotoExists = false;
    		}else{
    			_contactPhotoExists = true;
    		}	
    		_lookupKey = lookupKey;
		}catch(Exception ex){
			if (_debug) Log.v("Notification.Notification(Context context, long callLogID, String sentFromAddress, long timeStamp, long contactID, String contactName, long photoID, String lookupKey, int notificationType) ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Class Constructor
	 */
	public Notification(Context context, String title, String messageBody, long eventStartTime, long  eventEndTime, boolean allDay, String calendarName, long calendarID, long calendarEventID, int notificationType){
		_debug = Log.getDebug();
		if (_debug) Log.v("Notification.Notification(Context context, String title, String messageBody, long eventStartTime, long eventEndTime, boolean allDay, String calendarName, long calendarID, long calendarEventID, int notificationType)");
		try{
			_context = context;
			_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
			_contactExists = false;
			_contactPhotoExists = false;
			_notificationType = notificationType;
	    	_timeStamp = eventStartTime;
	    	_title = title;
	    	_allDay = allDay;
	    	if(notificationType == Constants.NOTIFICATION_TYPE_CALENDAR){
	    		_messageBody = formatCalendarEventMessage(messageBody, eventStartTime, eventEndTime, allDay, calendarName).replace("\n", "<br/>").trim();
	    	}else{
	    		_messageBody = messageBody;
	    	}
	    	_calendarID = calendarID;
	    	_calendarEventID = calendarEventID;
	    	_calendarEventStartTime = eventStartTime;
	    	_calendarEventEndTime = eventEndTime;
		}catch(Exception ex){
			if (_debug) Log.v("Notification.Notification(Context context, String title, String messageBody, long eventStartTime, long eventEndTime, boolean allDay, String calendarName, long calendarID, long calendarEventID, int notificationType) ERROR: " + ex.toString());
		}
	}

	/**
	 * Class Constructor
	 */
	public Notification(Context context, String sentFromAddress, String messageBody, long timeStamp, long threadID, long contactID, String contactName, long photoID, long messageID, String title, String email, long calendarID, long calendarEventID, long calendarEventStartTime, long calendarEventEndTime, boolean allDay, long callLogID,  String lookupKey, int notificationType) {
		_debug = Log.getDebug();
		if (_debug) Log.v("Notification.Notification(Context context, String sentFromAddress, String messageBody, long timeStamp, long threadID, long contactID, String contactName, long photoID, long messageID, String title, String email, long calendarID, long calendarEventID, long calendarEventStartTime, long calendarEventEndTime, boolean allDay, long callLogID,  String lookupKey, int notificationType)");
		try{
			_context = context;
			_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
			_contactExists = false;
			_contactPhotoExists = false;
			_notificationType = notificationType;
    		_sentFromAddress = sentFromAddress.toLowerCase();
    		_messageBody = messageBody;
    		_messageID = messageID;
    		_threadID = threadID;
    		_timeStamp = timeStamp;
    		_contactID = contactID;
    		if(contactName.equals("")){
    			_contactName = null;
    			_contactExists = false;
    		}else{
    			_contactName = contactName;
    			_contactExists = true;
    		}
    		_photoID = photoID;
    		if(photoID == 0){
    			_contactPhotoExists = false;
    		}else{
    			_contactPhotoExists = true;
    		}
    		_title = title;
    		_email = email;
    		_calendarID = calendarID;
    		_calendarEventID = calendarEventID;
    		_calendarEventStartTime = calendarEventStartTime;
    		_calendarEventEndTime = calendarEventEndTime;
    		_allDay = allDay;
    		_callLogID = callLogID;
    		_lookupKey = lookupKey;
		}catch(Exception ex){
			if (_debug) Log.v("Notification.Notification(Context context, String sentFromAddress, String messageBody, long timeStamp, long threadID, long contactID, String contactName, long photoID, long messageID, String title, String email, long calendarID, long calendarEventID, long calendarEventStartTime, long calendarEventEndTime, boolean allDay, long callLogID,  String lookupKey, int notificationType) ERROR: " + ex.toString());
		}
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Get the sentFromAddress property.
	 * 
	 * @return sentFromAddress - Contact's address that sent the message/call.
	 */
	public String getSentFromAddress() {
		if (_debug) Log.v("Notification.getSentFromAddress() SentFromAddress: " + _sentFromAddress);
		return _sentFromAddress;
	}
	
	/**
	 * Get the messageBody property.
	 * 
	 * @return messageBody - Notification's message.
	 */
	public String getMessageBody() {
		if (_debug) Log.v("Notification.getMessageBody()");
		if (_messageBody == null) {
			_messageBody = "";
	    }
	    return _messageBody;
	}
	
	/**
	 * Get the timeStamp property.
	 * 
	 * @return timeStamp - TimeStamp of notification.
	 */
	public long getTimeStamp() {
		if (_debug) Log.v("Notification.getTimeStamp() TimeStamp: " + _timeStamp);
	    return _timeStamp;
	}
	
	/**
	 * Get the threadID property.
	 * 
	 * @return threadID - SMS/MMS Message thread id.
	 */
	public long getThreadID() {
		if(_threadID == 0){
			_threadID = Common.getThreadID(_context, _sentFromAddress, _notificationType);
		}
		if (_debug) Log.v("Notification.getThreadID() ThreadID: " + _threadID);
	    return _threadID;
	}	
	
	/**
	 * Get the contactID property.
	 * 
	 * @return contactID - Contact's ID.
	 */
	public long getContactID() {
		if (_debug) Log.v("Notification.getContactID() ContactID: " + _contactID);
	    return _contactID;
	}
	
	/**
	 * Get the contactName property.
	 * 
	 * @return contactName - Contact's display name.
	 */
	public String getContactName() {
		if (_debug) Log.v("Notification.getContactName() ContactName: " + _contactName);
		if (_contactName == null) {
			_contactName = _context.getString(android.R.string.unknownName);
	    }
		return _contactName;
	}

	/**
	 * Get the photoID property.
	 * 
	 * @return photoID - Contact's photo ID.
	 */
	public long getPhotoID() {
		if (_debug) Log.v("Notification.getPhotoID() PhotoID: " + _photoID);
		return _photoID;
	}
	
	/**
	 * Get the photoIImg property.
	 * 
	 * @return photoImg - Bitmap of contact's photo.
	 */
	public Bitmap getPhotoImg() {
		if (_debug) Log.v("Notification.getPhotoImg()");
		return _photoImg;
	}
	
	/**
	 * Get the photoIImg property.
	 * 
	 * @return photoImg - Bitmap of contact's photo.
	 */
	public void setPhotoImg(Bitmap photoImg) {
		if (_debug) Log.v("Notification.setPhotoIImg()");
		_photoImg = photoImg;
	}
	
	/**
	 * Get the notificationType property.
	 * 
	 * @return notificationType - The type of notification this is.
	 */
	public int getNotificationType() {
		if (_debug) Log.v("Notification.getNotificationType() NotificationType: " + _notificationType);
		return _notificationType;
	}
	
	/**
	 * Get the messageID property.
	 * 
	 * @return messageID - The message id of the SMS/MMS message.
	 */
	public long getMessageID() {
		if(_messageID == 0){
			_messageID = Common.getMessageID(_context, getThreadID(), _messageBody, _timeStamp, _notificationType);
		}
		if (_debug) Log.v("Notification.getMessageID() MessageID: " + _messageID);
  		return _messageID;
	}
	
	/**
	 * Get the contactExists property.
	 * 
	 * @return  contactExists - Boolean returns true if there is a contact in the phone linked to this notification.
	 */
	public boolean getContactExists() {
		if (_debug) Log.v("Notification.getContactExists() Exists: " + _contactExists);
  		return _contactExists;
	}	

	/**
	 * Get the contactPhotoExists property.
	 * 
	 * @return  contactPhotoExists - Boolean returns true if there is a contact photo in the phone linked to this notification.
	 */
	public boolean getContactPhotoExists() {
		if (_debug) Log.v("Notification.getContactPhotoExists() Exists: " + _contactPhotoExists);
  		return _contactPhotoExists;
	}	
	
	/**
	 * Get the title property.
	 * 
	 * @return title - Notification title.
	 */
	public String getTitle() {
		if (_debug) Log.v("Notification.getTitle() Title: " + _title);
  		return _title;
	}
	
	/**
	 * Get the email property.
	 * 
	 * @return email - Notification email.
	 */
	public String getEmail() {
		if (_debug) Log.v("Notification.getEmail() Email: " + _email);
  		return _email;
	}	
	
	/**
	 * Get the calendarID property.
	 * 
	 * @return calendarID - Notification calendarID.
	 */
	public long getCalendarID() {
		if (_debug) Log.v("Notification.getCalendarID() CalendarID: " + _calendarID);
  		return _calendarID;
	}
	
	/**
	 * Get the calendarEventID property.
	 * 
	 * @param 
	 */
	public long getCalendarEventID() {
		if (_debug) Log.v("Notification.getCalendarEventID() CalendarEventID: " + _calendarEventID);
  		return _calendarEventID;
	}
	
	/**
	 * Get the calendarEventStartTime property.
	 * 
	 * @return calendarEventStartTime - Start time of the Calendar Event.
	 */
	public long getCalendarEventStartTime() {
		if (_debug) Log.v("Notification.getCalendarEventStartTime() CalendarEventStartTime: " + _calendarEventStartTime);
  		return _calendarEventStartTime;
	}
	
	/**
	 * Get the calendarEventEndTime property.
	 * 
	 * @return calendarEventEndTime - End time of the Calendar Event.
	 */
	public long getCalendarEventEndTime() {
		if (_debug) Log.v("Notification.getCalendarEventEndTime() CalendarEventEndTime: " + _calendarEventEndTime);
  		return _calendarEventEndTime;
	}
	
	/**
	 * Get the allDay property.
	 * 
	 * @return allDay - Boolean value set to true if the notification calendar event is an all day event.
	 */
	public boolean getAllDay() {
		if (_debug) Log.v("Notification.getAllDay() AllDay: " + _allDay);
  		return _allDay;
	}
	
	/**
	 * Get the callLogID property.
	 * 
	 * @return callLogID - The ID of the call in the call log.
	 */
	public long getCallLogID() {
		if (_debug) Log.v("Notification.getCallLogID() CallLogID: " + _callLogID);
  		return _callLogID;
	}
	
	/**
	 * Get the lookupKey property.
	 * 
	 * @return lookupKey - The contact LookupKey.
	 */
	public String getLookupKey() {
		if (_debug) Log.v("Notification.getLookupKey() LookupKey: " + _lookupKey);
	    return _lookupKey;
	}	
	/**
	 * Set this notification as being viewed on the users phone.
	 * 
	 * @param isViewed - Boolean value to set or unset the item as being viewed.
	 */
	public void setViewed(boolean isViewed){
		if (_debug) Log.v("Notification.setViewed()");
    	if(_notificationType == Constants.NOTIFICATION_TYPE_PHONE){
    		//Action is determined by the users preferences. 
    		//Either mark the call log as viewed, delete the call log entry, or do nothing to the call log entry.
    		if(_preferences.getString(Constants.PHONE_DISMISS_KEY, "0").equals(Constants.PHONE_DISMISS_ACTION_MARK_READ)){
    			setCallViewed(isViewed);
    		}else if(_preferences.getString(Constants.PHONE_DISMISS_KEY, "0").equals(Constants.PHONE_DISMISS_ACTION_DELETE)){
    			deleteFromCallLog();
    		}
	    }
    	if(_notificationType == Constants.NOTIFICATION_TYPE_SMS){
    		//Action is determined by the users preferences. 
    		//Either mark the message as viewed or do nothing to the message.
    		if(_preferences.getString(Constants.SMS_DISMISS_KEY, "0").equals(Constants.SMS_DISMISS_ACTION_MARK_READ)){
    			setMessageRead(isViewed);
    		}
    	}
    	if(_notificationType == Constants.NOTIFICATION_TYPE_MMS){
    		//Action is determined by the users preferences. 
    		//Either mark the message as viewed or do nothing to the message.
    		if(_preferences.getString(Constants.MMS_DISMISS_KEY, "0").equals(Constants.MMS_DISMISS_ACTION_MARK_READ)){
    			setMessageRead(isViewed);
    		}
    	}
	    if(_notificationType == Constants.NOTIFICATION_TYPE_CALENDAR){
	    	//Do nothing. There is no log to update for Calendar Events.
	    }
	    if(_notificationType == Constants.NOTIFICATION_TYPE_GMAIL){
	    	//TODO - Gmail
	    }
	}
	
	/**
	 * Delete the message or thread from the users phone.
	 */
	public void deleteMessage(){
		if (_debug) Log.v("Notification.deleteMessage()");
		//Decide what to do here based on the users preferences.
		//Delete the single message, delete the entire thread, or do nothing.
		boolean deleteThread = false;
		boolean deleteMessage = false;
		if(_notificationType == Constants.NOTIFICATION_TYPE_SMS){
			if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_DELETE_MESSAGE)){
				deleteThread = false;
				deleteMessage = true;
			}else if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_DELETE_THREAD)){
				deleteThread = true;
				deleteMessage = false;
			}
		}else if(_notificationType == Constants.NOTIFICATION_TYPE_MMS){
			if(_preferences.getString(Constants.MMS_DELETE_KEY, "0").equals(Constants.MMS_DELETE_ACTION_DELETE_MESSAGE)){
				deleteThread = false;
				deleteMessage = true;
			}else if(_preferences.getString(Constants.MMS_DELETE_KEY, "0").equals(Constants.MMS_DELETE_ACTION_DELETE_THREAD)){
				deleteThread = true;
				deleteMessage = false;
			}
		}
		if(deleteMessage || deleteThread){
			if(deleteThread){
			try{
				//Delete entire SMS thread.
				_context.getContentResolver().delete(
						Uri.parse("content://sms/conversations/" + getThreadID()), 
						null, 
						null);
				}catch(Exception ex){
					if (_debug) Log.e("Notification.deleteMessage() Delete Thread ERROR: " + ex.toString());
				}
			}else{
				try{
					//Delete single message.
					_context.getContentResolver().delete(
							Uri.parse("content://sms/" + getMessageID()),
							null, 
							null);
				}catch(Exception ex){
					if (_debug) Log.e("Notification.deleteMessageg() Delete Message ERROR: " + ex.toString());
				}
			}
		}
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Set the call log as viewed (not new) or new depending on the input.
	 * 
	 * @param isViewed - Boolean, if true sets the call log call as being viewed.
	 */
	private void setCallViewed(boolean isViewed){
		if (_debug) Log.v("Notification.setCallViewed()");
		ContentValues contentValues = new ContentValues();
		if(isViewed){
			contentValues.put(android.provider.CallLog.Calls.NEW, 0);
		}else{
			contentValues.put(android.provider.CallLog.Calls.NEW, 1);
		}
		String selection = android.provider.CallLog.Calls._ID + " = " + _callLogID;
		String[] selectionArgs = null;
		try{
			_context.getContentResolver().update(
					Uri.parse("content://call_log/calls"),
					contentValues,
					selection, 
					selectionArgs);
		}catch(Exception ex){
			if (_debug) Log.e("Notification.setCallViewed() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Delete the call log entry.
	 */
	private void deleteFromCallLog(){
		if (_debug) Log.v("Notification.deleteFromCallLog()");
		String selection = android.provider.CallLog.Calls._ID + " = " + _callLogID;
		String[] selectionArgs = null;
		try{
			_context.getContentResolver().delete(
					Uri.parse("content://call_log/calls"),
					selection, 
					selectionArgs);
		}catch(Exception ex){
			if (_debug) Log.e("Notification.deleteFromCallLog() ERROR: " + ex.toString());
		}
	}

	/**
	 * Set the SMS/MMS message as read or unread depending on the input.
	 * 
	 * @param isViewed - Boolean, if true sets the message as viewed.
	 */
	private void setMessageRead(boolean isViewed){
		if(_debug)Log.v("Notification.setMessageRead()");
		getMessageID();
		if(_messageID == 0){
			if (_debug) Log.v("Notification.setMessageRead() Message ID == 0: Exiting...");
			return;
		}
		ContentValues contentValues = new ContentValues();
		if(isViewed){
			contentValues.put("READ", 1);
		}else{
			contentValues.put("READ", 0);
		}
		String selection = null;
		String[] selectionArgs = null;
		try{
			_context.getContentResolver().update(
					Uri.parse("content://sms/" + _messageID), 
		    		contentValues, 
		    		selection, 
		    		selectionArgs);
		}catch(Exception ex){
			if (_debug) Log.e("Notification.setMessageRead() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Format/create the Calendar Event message.
	 * 
	 * @param eventStartTime - Calendar Event's start time.
	 * @param eventEndTime - Calendar Event's end time.
	 * @param allDay - Boolean, true if the Calendar Event is all day.
	 * 
	 * @return String - Returns the formatted Calendar Event message.
	 */
	private String formatCalendarEventMessage(String messageBody, long eventStartTime, long eventEndTime, boolean allDay, String calendarName){
		if (_debug) Log.v("Notification.formatCalendarEventMessage()");
		String formattedMessage = "";
		Date eventEndDate = new Date(eventEndTime);
		Date eventStartDate = new Date(eventStartTime);
    	if(messageBody.equals("")){
    		String startDateFormated = Common.formatDate(_context, eventStartDate);
    		String endDateFormated = Common.formatDate(_context, eventEndDate);
    		try{
    			String[] startDateInfo = Common.parseDateInfo(_context, startDateFormated);
    			String[] endDateInfo = Common.parseDateInfo(_context, endDateFormated);
	    		if(allDay){
	    			formattedMessage = startDateInfo[0] + " - All Day";
	    		}else{
	    			//Check if the event spans a single day or not.
	    			if(startDateInfo[0].equals(endDateInfo[0]) && startDateInfo.length == 3){
	    				if(startDateInfo.length < 3){
	    					formattedMessage = startDateInfo[0] + " " + startDateInfo[1] + " - " + endDateInfo[1];
	    				}else{
	    					formattedMessage = startDateInfo[0] + " " + startDateInfo[1] + " " + startDateInfo[2] +  " - " + endDateInfo[1] + " " + startDateInfo[2];
	    				}
	    			}else{
	    				formattedMessage = startDateFormated + " - " + endDateFormated;
	    			}
	    		}
    		}catch(Exception ex){
    			if (_debug) Log.e("Notification.formatCalendarEventMessage() ERROR: " + ex.toString());
    			formattedMessage = startDateFormated + " - " + endDateFormated;
    		}
    	}else{
    		formattedMessage = messageBody;
    	}
    	if(_preferences.getBoolean(Constants.CALENDAR_LABELS_KEY, true)){
    		formattedMessage = "<b>" + calendarName + "</b><br/>" + formattedMessage;
    	}
		return formattedMessage;
	}	
	
}