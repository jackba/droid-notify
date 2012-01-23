package apps.droidnotify;

import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.email.EmailCommon;
import apps.droidnotify.facebook.FacebookCommon;
import apps.droidnotify.log.Log;
import apps.droidnotify.phone.PhoneCommon;
import apps.droidnotify.sms.SMSCommon;
import apps.droidnotify.twitter.TwitterCommon;

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
	private long _sentFromID = 0;
	private String _messageBody = null;
	private long _timeStamp;
	private long _threadID = 0;
	private long _contactID = 0;
	private String _contactName = null;
	private long _photoID = 0;
	private Bitmap _photoImg = null;
	private int _notificationType = -1;
	private long _messageID = 0;
	private String _messageStringID = null;
	private boolean _contactExists = false;
	private boolean _contactPhotoExists = false;
	private String _title = null;
	private long _calendarID = 0;
	private long _calendarEventID = 0;
	private long _calendarEventStartTime = 0;
	private long _calendarEventEndTime = 0;
	private String _calendarName = null;
	private boolean _allDay = false;
	private long _callLogID = 0;
	private String _lookupKey = null;
	private String _k9EmailUri = null;
	private String _k9EmailDelUri = null;
	private int _rescheduleNumber = 0;
	private PendingIntent _reminderPendingIntent = null;
	private int _notificationSubType = -1;
	private String _linkURL = null;
	
	//================================================================================
	// Constructors
	//================================================================================

	/**
	 * Class Constructor
	 */
	public Notification(Context context, Bundle notificationBundle){		
		_debug = Log.getDebug();
		if (_debug) Log.v("Notification.Notification() ==BUNDLE CONSTRUCTOR==");
		try{
			_context = context;
			_preferences = PreferenceManager.getDefaultSharedPreferences(context);
			_contactExists = false;
			_contactPhotoExists = false;
			
			//Extract information from the provided Bundle.
			_sentFromAddress = notificationBundle.getString(Constants.BUNDLE_SENT_FROM_ADDRESS);
			_sentFromID = notificationBundle.getLong(Constants.BUNDLE_SENT_FROM_ID, 0);
			_messageBody = notificationBundle.getString(Constants.BUNDLE_MESSAGE_BODY);
			_timeStamp = notificationBundle.getLong(Constants.BUNDLE_TIMESTAMP, 0);
			_threadID = notificationBundle.getLong(Constants.BUNDLE_THREAD_ID, 0);
			_contactID = notificationBundle.getLong(Constants.BUNDLE_CONTACT_ID, 0);
			_contactName = notificationBundle.getString(Constants.BUNDLE_CONTACT_NAME);
			_photoID = notificationBundle.getLong(Constants.BUNDLE_PHOTO_ID, 0);
			_notificationType = notificationBundle.getInt(Constants.BUNDLE_NOTIFICATION_TYPE, -1);
			_messageID = notificationBundle.getLong(Constants.BUNDLE_MESSAGE_ID, 0);
			_messageStringID = notificationBundle.getString(Constants.BUNDLE_MESSAGE_STRING_ID);
			_title = notificationBundle.getString(Constants.BUNDLE_TITLE);
			_calendarID = notificationBundle.getLong(Constants.BUNDLE_CALENDAR_ID, 0);
			_calendarEventID = notificationBundle.getLong(Constants.BUNDLE_CALENDAR_EVENT_ID, 0);
			_calendarEventStartTime = notificationBundle.getLong(Constants.BUNDLE_CALENDAR_EVENT_START_TIME, 0);
			_calendarEventEndTime = notificationBundle.getLong(Constants.BUNDLE_CALENDAR_EVENT_END_TIME, 0);
			_calendarName = notificationBundle.getString(Constants.BUNDLE_CALENDAR_NAME);
			_allDay = notificationBundle.getBoolean(Constants.BUNDLE_ALL_DAY, false);
			_callLogID = notificationBundle.getLong(Constants.BUNDLE_CALL_LOG_ID, 0);
			_lookupKey = notificationBundle.getString(Constants.BUNDLE_LOOKUP_KEY);
			_k9EmailUri = notificationBundle.getString(Constants.BUNDLE_K9_EMAIL_URI);
			_k9EmailDelUri = notificationBundle.getString(Constants.BUNDLE_K9_EMAIL_DEL_URI);
			_rescheduleNumber = notificationBundle.getInt(Constants.BUNDLE_RESCHEDULE_NUMBER, 0);
			_notificationSubType = notificationBundle.getInt(Constants.BUNDLE_NOTIFICATION_SUB_TYPE, -1);
			_linkURL = notificationBundle.getString(Constants.BUNDLE_LINK_URL);
			
			//Customize the Notification based on what was provided.
			if(_sentFromAddress != null && _sentFromAddress.equals("")) _sentFromAddress = null;
			
			switch(_notificationType){
				case Constants.NOTIFICATION_TYPE_PHONE:{
					if(_title == null) _title = "Missed Call";
					if(PhoneCommon.isPrivateUnknownNumber(context, _sentFromAddress)) _sentFromAddress = context.getString(R.string.private_number_text);
					if(_sentFromAddress != null) _sentFromAddress = _sentFromAddress.toLowerCase();
					break;
				}
				case Constants.NOTIFICATION_TYPE_SMS:{
					if(_title == null) _title = "SMS Message";
					if(_sentFromAddress != null) _sentFromAddress = _sentFromAddress.toLowerCase();
					break;
				}
				case Constants.NOTIFICATION_TYPE_MMS:{
					if(_title == null) _title = "MMS Message";	
					if(_sentFromAddress != null) _sentFromAddress = _sentFromAddress.toLowerCase();
					break;
				}
				case Constants.NOTIFICATION_TYPE_CALENDAR:{
					if(_title == null) _title = "Calendar Event";
					_messageBody = formatCalendarEventMessage(_title, _messageBody, _calendarEventStartTime, _calendarEventEndTime, _allDay, _calendarName);
					if(_sentFromAddress != null) _sentFromAddress = _sentFromAddress.toLowerCase();
					break;
				}
				case Constants.NOTIFICATION_TYPE_GMAIL:{
					if(_title == null) _title = "Email";
					if(_sentFromAddress != null) _sentFromAddress = _sentFromAddress.toLowerCase();
					break;
				}
				case Constants.NOTIFICATION_TYPE_TWITTER:{
					if(_title == null) _title = "Twitter";
					break;
				}
				case Constants.NOTIFICATION_TYPE_FACEBOOK:{
					if(_title == null) _title = "Facebook";
					break;
				}
				case Constants.NOTIFICATION_TYPE_K9:{
					if(_title == null) _title = "Email";
					if(_sentFromAddress != null) _sentFromAddress = _sentFromAddress.toLowerCase();
					break;
				}
			}
			
			if(_contactID == 0){
				_contactExists = false;
			}else{
				_contactExists = true;
			}
			
			if(_contactName != null && _contactName.equals("")) _contactName = null;
			
			if(_photoID == 0){
				_contactPhotoExists = false;
			}else{
				_contactPhotoExists = true;
			}
			
			setReminder();
			
		}catch(Exception ex){
			Log.e("Notification.Notification() ==BUNDLE CONSTRUCTOR== ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Class Constructor
	 */
	public Notification(Context context, String sentFromAddress, String messageBody, long messageID, long threadID, long timeStamp, long contactID, String contactName, long photoID, String lookupKey, int notificationType) {
		_debug = Log.getDebug();
		if (_debug) Log.v("Notification.Notification() ==CONSTRUCTOR 1==");
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
					_title = "Facebook";
					break;
				}
				case Constants.NOTIFICATION_TYPE_K9:{
					_title = "Email";
					break;
				}
			}
			_context = context;
			_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
			_contactExists = false;
			_contactPhotoExists = false;
			_notificationType = notificationType;
			if(sentFromAddress != null && !sentFromAddress.equals("")){
				if(_notificationType == Constants.NOTIFICATION_TYPE_FACEBOOK){
					_sentFromAddress = sentFromAddress;
				}else{
					_sentFromAddress = sentFromAddress.toLowerCase();
				}
			}else{
				_sentFromAddress = null;
			}
    		_messageBody = messageBody;
    		_messageID = messageID;
    		_threadID = threadID;
    		_timeStamp = timeStamp;
    		_contactID = contactID;
    		if(_contactID == 0){
    			_contactExists = false;
    		}else{
    			_contactExists = true;
    		}
    		if(contactName != null && !contactName.equals("")){
    			_contactName = contactName;
    		}else{
    			_contactName = null;
    		}
    		_photoID = photoID;
    		if(photoID == 0){
    			_contactPhotoExists = false;
    		}else{
    			_contactPhotoExists = true;
    		}
    		_lookupKey = lookupKey;
    		setReminder();
		}catch(Exception ex){
			Log.e("Notification.Notification() ==CONSTRUCTOR 1== ERROR: " + ex.toString());
		}
	}

	/**
	 * Class Constructor
	 */
	public Notification(Context context, String sentFromAddress, String messageBody, long timeStamp, long threadID, long contactID, String contactName, long photoID, long messageID, String messageStringID, String title, long calendarID, long calendarEventID, long calendarEventStartTime, long calendarEventEndTime, boolean allDay, long callLogID,  String lookupKey, String k9EmailUri, String k9EmailDelUri, int rescheduleNumber, int notificationType, int notificationSubType) {
		_debug = Log.getDebug();
		if (_debug) Log.v("Notification.Notification() ==CONSTRUCTOR 5==");
		try{
			_context = context;
			_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
			_contactExists = false;
			_contactPhotoExists = false;
			_notificationType = notificationType;
			if(sentFromAddress != null && !sentFromAddress.equals("")){
				if(_notificationType == Constants.NOTIFICATION_TYPE_FACEBOOK){
					_sentFromAddress = sentFromAddress;
				}else{
					_sentFromAddress = sentFromAddress.toLowerCase();
				}
			}else{
				_sentFromAddress = null;
			}
    		_messageBody = messageBody;
    		_messageID = messageID;
    		_messageStringID = messageStringID;
    		_threadID = threadID;
    		_timeStamp = timeStamp;
    		_contactID = contactID;
    		if(_contactID == 0){
    			_contactExists = false;
    		}else{
    			_contactExists = true;
    		}
    		_k9EmailUri = k9EmailUri;
    		_k9EmailDelUri = k9EmailDelUri;
    		if(contactName != null && !contactName.equals("")){
    			_contactName = contactName;
    		}else{
    			_contactName = null;    			
    		}
    		_photoID = photoID;
    		if(photoID == 0){
    			_contactPhotoExists = false;
    		}else{
    			_contactPhotoExists = true;
    		}
    		_title = title;
    		_calendarID = calendarID;
    		_calendarEventID = calendarEventID;
    		_calendarEventStartTime = calendarEventStartTime;
    		_calendarEventEndTime = calendarEventEndTime;
    		_allDay = allDay;
    		_callLogID = callLogID;
    		_lookupKey = lookupKey;
    		_rescheduleNumber = rescheduleNumber;
    		_notificationSubType = notificationSubType;
    		setReminder();
		}catch(Exception ex){
			Log.e("Notification.Notification() ==CONSTRUCTOR 5== ERROR: " + ex.toString());
		}
	}

	/**
	 * Class Constructor
	 */
	public Notification(Context context, String sentFromAddress, long sentFromID, String messageBody, long timeStamp, long contactID, String contactName, long photoID, long messageID, String messageStringID, String lookupKey, String k9EmailUri, String k9EmailDelUri, String linkURL, int notificationType, int notificationSubType) {
		_debug = Log.getDebug();
		if (_debug) Log.v("Notification.Notification() ==CONSTRUCTOR 6==");
		try{			
			switch(notificationType){
				case Constants.NOTIFICATION_TYPE_PHONE:{
					_title = "Missed Call";
					if(_linkURL != null) _linkURL = linkURL;
					break;
				}
				case Constants.NOTIFICATION_TYPE_SMS:{
					_title = "SMS Message";
					if(_linkURL != null) _linkURL = linkURL;
					break;
				}
				case Constants.NOTIFICATION_TYPE_MMS:{
					_title = "MMS Message";	
					if(_linkURL != null) _linkURL = linkURL;
					break;
				}
				case Constants.NOTIFICATION_TYPE_CALENDAR:{
					_title = "Calendar Event";
					if(_linkURL != null) _linkURL = linkURL;
					break;
				}
				case Constants.NOTIFICATION_TYPE_GMAIL:{
					_title = "Email";
					if(_linkURL != null) _linkURL = linkURL;
					break;
				}
				case Constants.NOTIFICATION_TYPE_TWITTER:{
					_title = "Twitter";
					if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE){
						if(sentFromAddress != null) _linkURL = String.format("https://mobile.twitter.com/%s/messages", sentFromAddress);
					}else if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_FOLLOWER_REQUEST){
						if(_linkURL != null) _linkURL = linkURL;
					}else if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_MENTION){
						_linkURL = "https://mobile.twitter.com/replies";
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_FACEBOOK:{
					_title = "Facebook";
					if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION){
						if(_linkURL != null) _linkURL = linkURL.replace("http://www.facebook.com/", "http://m.facebook.com/");
					}else if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_FRIEND_REQUEST){
						if(_linkURL != null) _linkURL = linkURL;
					}else if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_MESSAGE){
						if(messageStringID != null) _linkURL = String.format("https://m.facebook.com/messages/read?action=read&tid=id.%s", messageStringID.substring(0, messageStringID.indexOf("_")));
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_K9:{
					_title = "Email";
					if(_linkURL != null) _linkURL = linkURL;
					break;
				}
			}
			_context = context;
			_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
			_contactExists = false;
			_contactPhotoExists = false;
			_notificationType = notificationType;
			_notificationSubType = notificationSubType;
			if(sentFromAddress != null && !sentFromAddress.equals("")){
				if(_notificationType == Constants.NOTIFICATION_TYPE_FACEBOOK){
					_sentFromAddress = sentFromAddress;
				}else{
					_sentFromAddress = sentFromAddress.toLowerCase();
				}
			}else{
				_sentFromAddress = null;
			}
			_sentFromID = sentFromID;
    		_messageBody = messageBody;
    		_messageID = messageID;
    		_messageStringID = messageStringID;
    		_timeStamp = timeStamp;
    		_contactID = contactID;
    		if(_contactID == 0){
    			_contactExists = false;
    		}else{
    			_contactExists = true;
    		}
    		_k9EmailUri = k9EmailUri;
    		_k9EmailDelUri = k9EmailDelUri;
    		if(contactName != null && !contactName.equals("")){
    			_contactName = contactName;
    		}else{
    			_contactName = null;    			
    		}
    		_photoID = photoID;
    		if(photoID == 0){
    			_contactPhotoExists = false;
    		}else{
    			_contactPhotoExists = true;
    		}
    		_lookupKey = lookupKey;    		
    		setReminder();
		}catch(Exception ex){
			Log.e("Notification.Notification() ==CONSTRUCTOR 6== ERROR: " + ex.toString());
		}
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Get the sentFromAddress property.
	 * 
	 * @return sentFromAddress - Persons address that sent the item.
	 */
	public String getSentFromAddress() {
		if (_debug) Log.v("Notification.getSentFromAddress()");
		//if (_debug) Log.v("Notification.getSentFromAddress() SentFromAddress: " + _sentFromAddress);
		if (_sentFromAddress == null) {
			_sentFromAddress = _context.getString(android.R.string.unknownName);
	    }
		return _sentFromAddress;
	}

	/**
	 * Get the sentFromID property.
	 * 
	 * @return sentFromID - Persons ID that sent the item.
	 */
	public long getSentFromID() {
		if (_debug) Log.v("Notification.getSentFromID() SentFromID: " + _sentFromID);
		return _sentFromID;
	}
	
	/**
	 * Get the messageBody property.
	 * 
	 * @return messageBody - Notification's message.
	 */
	public String getMessageBody() {
		if (_debug) Log.v("Notification.getMessageBody()");
		//if (_debug) Log.v("Notification.getMessageBody() Message Body: " + _messageBody);
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
		if(_notificationType == Constants.NOTIFICATION_TYPE_SMS || _notificationType == Constants.NOTIFICATION_TYPE_MMS){
			if(_threadID == 0){
				_threadID = SMSCommon.getThreadID(_context, _sentFromAddress, _notificationType);
			}
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
		if (_debug) Log.v("Notification.getContactName()");
		//if (_debug) Log.v("Notification.getContactName() ContactName: " + _contactName);
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
	 * Get the notificationSubType property.
	 * 
	 * @return notificationType - The type of notification this is.
	 */
	public int getNotificationSubType() {
		if (_debug) Log.v("Notification.getNotificationSubType() NotificationSubType: " + _notificationSubType);
		return _notificationSubType;
	}
	
	/**
	 * Get the messageID property.
	 * 
	 * @return messageID - The message id of the notification message.
	 */
	public long getMessageID() {
		if(_notificationType == Constants.NOTIFICATION_TYPE_SMS || _notificationType == Constants.NOTIFICATION_TYPE_MMS){
			if(_messageID == 0){
				_messageID = SMSCommon.getMessageID(_context, getThreadID(), _messageBody, _timeStamp, _notificationType);
			}
		}
		if (_debug) Log.v("Notification.getMessageID() MessageID: " + _messageID);
  		return _messageID;
	}
	
	/**
	 * Get the messageStringID property.
	 * 
	 * @return messageStringID - The string message id of the notification message.
	 */
	public String getMessageStringID() {
		if (_debug) Log.v("Notification.getMessageStringID() MessageStringID: " + _messageStringID);
  		return _messageStringID;
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
		if (_debug) Log.v("Notification.getTitle()");
		//if (_debug) Log.v("Notification.getTitle() Title: " + _title);
  		return _title;
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
	 * Get the k9EmailUri property.
	 * 
	 * @return k9EmailUri - The k9 email URI.
	 */
	public String getK9EmailUri() {
		if (_debug) Log.v("Notification.getK9EmailUri() K9EmailUri: " + _k9EmailUri);
	    return _k9EmailUri;
	}
	
	/**
	 * Get the k9EmailDelUri property.
	 * 
	 * @return k9EmailDelUri - The k9 delete email URI.
	 */
	public String getK9EmailDelUri() {
		if (_debug) Log.v("Notification.getK9EmailDelUri() K9EmailDelUri: " + _k9EmailDelUri);
	    return _k9EmailDelUri;
	}
	
	/**
	 * Get the rescheduleNumber property.
	 * 
	 * @return rescheduleNumber - The current reschedule number.
	 */
	public int getRescheduleNumber() {
		if (_debug) Log.v("Notification.getRescheduleNumber() RescheduleNumber: " + _rescheduleNumber);
	    return _rescheduleNumber;
	}	
	
	/**
	 * Set the rescheduleNumber property.
	 */
	public void setRescheduleNumber(int rescheduleNumber) {
		if (_debug) Log.v("Notification.setRescheduleNumber()");
		_rescheduleNumber = rescheduleNumber;
	}

	/**
	 * Get the reminderPendingIntent property.
	 * 
	 * @return reminderPendingIntent - The current reminder PendingIntent.
	 */
	public PendingIntent getReminderPendingIntent() {
		if (_debug) Log.v("Notification.getReminderPendingIntent()");
	    return _reminderPendingIntent;
	}
	
	/**
	 * Set the reminderPendingIntent property.
	 */
	public void setReminderPendingIntent(PendingIntent pendingIntent) {
		if (_debug) Log.v("Notification.setReminderPendingIntent()");
		_reminderPendingIntent = pendingIntent;
	}
	
	/**
	 * Get the linkURL property.
	 * 
	 * @return linkURL - The URL link.
	 */
	public String getLinkURL() {
		if (_debug) Log.v("Notification.getLinkURL() LinkURL: " + _linkURL);
	    return _linkURL;
	}
	
	/**
	 * Set this notification as being viewed on the users phone.
	 * 
	 * @param isViewed - Boolean value to set or unset the item as being viewed.
	 */
	public void setViewed(boolean isViewed){
		if (_debug) Log.v("Notification.setViewed()");
		switch(_notificationType){
			case Constants.NOTIFICATION_TYPE_PHONE:{
	    		//Action is determined by the users preferences. 
	    		//Either mark the call log as viewed, delete the call log entry, or do nothing to the call log entry.
	    		if(_preferences.getString(Constants.PHONE_DISMISS_KEY, "0").equals(Constants.PHONE_DISMISS_ACTION_MARK_READ)){
	    			setCallViewed(isViewed);
	    		}else if(_preferences.getString(Constants.PHONE_DISMISS_KEY, "0").equals(Constants.PHONE_DISMISS_ACTION_DELETE)){
	    			deleteFromCallLog();
	    		}
				break;
			}
			case Constants.NOTIFICATION_TYPE_SMS:{
	    		//Action is determined by the users preferences. 
	    		//Either mark the message as viewed or do nothing to the message.
	    		if(_preferences.getString(Constants.SMS_DISMISS_KEY, "0").equals(Constants.SMS_DISMISS_ACTION_MARK_READ)){
	    			setMessageRead(isViewed);
	    		}
				break;
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
	    		//Action is determined by the users preferences. 
	    		//Either mark the message as viewed or do nothing to the message.
	    		if(_preferences.getString(Constants.MMS_DISMISS_KEY, "0").equals(Constants.MMS_DISMISS_ACTION_MARK_READ)){
	    			setMessageRead(isViewed);
	    		}
				break;
			}
			case Constants.NOTIFICATION_TYPE_CALENDAR:{
				//Do nothing. There is no log to update for Calendar Events.
				break;
			}
			case Constants.NOTIFICATION_TYPE_GMAIL:{
	
				break;
			}
			case Constants.NOTIFICATION_TYPE_TWITTER:{
				//Currently, there is no way to mark a Twitter message or friend request as being viewed.
				//if(_notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE){
				//	
				//}else if(_notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_MENTION){
				//	
				//}else if(_notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_FRIEND_REQUEST){
				//	
				//}
				break;
			}
			case Constants.NOTIFICATION_TYPE_FACEBOOK:{
				if(_notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION){
					FacebookCommon.setFacebookNotificationRead(_context, _messageStringID, isViewed);
				}
				//else if(_notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_FRIEND_REQUEST){
				//	//The Facebook API doesn't allow marking Friend Requests as being viewed.
				//}else if(_notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_MESSAGE){
				//	//The Facebook API doesn't allow marking Messages as being viewed.
				//}
				break;
			}
			case Constants.NOTIFICATION_TYPE_K9:{
				//Currently, there is no way to mark a K-9 Client message as being viewed.				
				break;
			}
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
		if(_notificationType == Constants.NOTIFICATION_TYPE_SMS || _notificationType == Constants.NOTIFICATION_TYPE_MMS){
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
					SMSCommon.deleteMessageThread(_context, getThreadID(), _notificationType);
				}else{
					SMSCommon.deleteSingleMessage(_context, getMessageID(), _notificationType);
				}
			}
		}else if(_notificationType == Constants.NOTIFICATION_TYPE_TWITTER){
			TwitterCommon.deleteTwitterItem(_context, this);
		}else if(_notificationType == Constants.NOTIFICATION_TYPE_FACEBOOK){
			FacebookCommon.deleteFacebookItem(_context, this);
		}else if(_notificationType == Constants.NOTIFICATION_TYPE_K9){
			EmailCommon.deleteK9Email(_context, _k9EmailDelUri, _notificationSubType);
		}
	}
	
	/**
	 * Sets the alarm that will remind the user with another popup of the notification.
	 */
	public void setReminder(){
		if (_debug) Log.v("Notification.setReminder()");
		if(_preferences.getBoolean(Constants.REMINDERS_ENABLED_KEY, false)){	
			boolean triggerReminder = true;
			int maxRescheduleAttempts = Integer.parseInt(_preferences.getString(Constants.REMINDER_FREQUENCY_KEY, Constants.REMINDER_FREQUENCY_DEFAULT));
			//Determine if the notification should be rescheduled or not.
			if(maxRescheduleAttempts < 0){
				//Infinite Attempts.
				triggerReminder = true;
			}else if(_rescheduleNumber > maxRescheduleAttempts){
				triggerReminder = false;
			}
			if(triggerReminder){		
				long rescheduleTime = System.currentTimeMillis() + Long.parseLong(_preferences.getString(Constants.REMINDER_INTERVAL_KEY, Constants.REMINDER_INTERVAL_DEFAULT)) * 60 * 1000;
				_reminderPendingIntent = Common.rescheduleNotification(_context, this, rescheduleTime, ++_rescheduleNumber);
			}
		}
	}
	
	/**
	 * Cancel the reminder alarm.
	 */
	public void cancelReminder() {
		if (_debug) Log.v("Notification.cancelReminder()");
		if (_reminderPendingIntent != null) {
	    	AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
	    	alarmManager.cancel(_reminderPendingIntent);
	    	_reminderPendingIntent.cancel();
	    	_reminderPendingIntent = null;
		}
	}
	
	/**
	 * SPeak the notification text using TTS.
	 */
	public void speak(TextToSpeech tts){
		if (_debug) Log.v("Notification.speak()");
		StringBuilder messageToSpeak = new StringBuilder();
		String sentFrom = null;
		if(_notificationType != Constants.NOTIFICATION_TYPE_CALENDAR){
			if(_contactName != null && !_contactName.equals(_context.getString(android.R.string.unknownName))){
				sentFrom  = _contactName;
			}else{
				if(_sentFromAddress.contains("@")){
					sentFrom = _sentFromAddress;
				}else{
					sentFrom = PhoneCommon.formatPhoneNumber(_context, _sentFromAddress);
				}
			}
		}
		switch(_notificationType){
			case Constants.NOTIFICATION_TYPE_PHONE:{
				String formattedTimestamp = Common.formatTimestamp(_context, _timeStamp, false);
				messageToSpeak.append(_context.getString(R.string.missed_call_at_text, formattedTimestamp.toLowerCase()));
				messageToSpeak.append(". " + _context.getString(R.string.from_text) + " " + sentFrom + ". ");
				break;
			}
			case Constants.NOTIFICATION_TYPE_SMS:{
				String formattedTimestamp = Common.formatTimestamp(_context, _timeStamp, _preferences.getBoolean(Constants.SMS_TIME_IS_UTC_KEY, false));
				messageToSpeak.append(_context.getString(R.string.message_at_text, formattedTimestamp.toLowerCase()));
				messageToSpeak.append(". " + _context.getString(R.string.from_text) + " " + sentFrom + ". ");
				messageToSpeak.append(_messageBody);
				break;
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
				String formattedTimestamp = Common.formatTimestamp(_context, _timeStamp, false);
				messageToSpeak.append(_context.getString(R.string.message_at_text, formattedTimestamp.toLowerCase()));
				messageToSpeak.append(". " + _context.getString(R.string.from_text) + " " + sentFrom + ". ");
				messageToSpeak.append(_messageBody);
				break;
			}
			case Constants.NOTIFICATION_TYPE_CALENDAR:{
				messageToSpeak.append(_context.getString(R.string.calendar_event_text) + ". " + _messageBody);
				break;
			}
			case Constants.NOTIFICATION_TYPE_GMAIL:{
				String formattedTimestamp = Common.formatTimestamp(_context, _timeStamp, false);
				messageToSpeak.append(_context.getString(R.string.email_at_text, formattedTimestamp.toLowerCase()));
				messageToSpeak.append(". " + _context.getString(R.string.from_text) + " " + sentFrom + ". ");
				messageToSpeak.append(_messageBody);
				break;
			}
			case Constants.NOTIFICATION_TYPE_TWITTER:{
				String formattedTimestamp = Common.formatTimestamp(_context, _timeStamp, false);
				if(_notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE){
					messageToSpeak.append(_context.getString(R.string.message_at_text, formattedTimestamp.toLowerCase()));
				}else if(_notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_MENTION){
					messageToSpeak.append(_context.getString(R.string.mention_at_text, formattedTimestamp.toLowerCase()));
				}else if(_notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_FOLLOWER_REQUEST){
					messageToSpeak.append(_context.getString(R.string.follower_request_text));
				}
				messageToSpeak.append(". " + _context.getString(R.string.from_text) + " " + sentFrom + ". ");
				messageToSpeak.append(_messageBody);				
				break;
			}
			case Constants.NOTIFICATION_TYPE_FACEBOOK:{
				String formattedTimestamp = Common.formatTimestamp(_context, _timeStamp, true);
				if(_notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION){
					messageToSpeak.append(_context.getString(R.string.notification_at_text, formattedTimestamp.toLowerCase()));
				}else if(_notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_FRIEND_REQUEST){
					messageToSpeak.append(_context.getString(R.string.friend_request_at_text, formattedTimestamp.toLowerCase()));
				}
				messageToSpeak.append(". " + _context.getString(R.string.from_text) + " " + sentFrom + ". ");
				messageToSpeak.append(_messageBody);
				break;
			}
			case Constants.NOTIFICATION_TYPE_K9:{
				String formattedTimestamp = Common.formatTimestamp(_context, _timeStamp, false);
				messageToSpeak.append(_context.getString(R.string.email_at_text, formattedTimestamp.toLowerCase()));
				messageToSpeak.append(". " + _context.getString(R.string.from_text) + " " + sentFrom + ". ");
				messageToSpeak.append(_messageBody);
				break;
			}
		}
		if(messageToSpeak != null){
			Common.speak(_context, tts, Common.removeHTML(messageToSpeak.toString()));
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
		PhoneCommon.setCallViewed(_context, _callLogID, isViewed);
	}
	
	/**
	 * Delete the call log entry.
	 */
	private void deleteFromCallLog(){
		if (_debug) Log.v("Notification.deleteFromCallLog()");
		PhoneCommon.deleteFromCallLog(_context, _callLogID);
	}

	/**
	 * Set the SMS/MMS message as read or unread depending on the input.
	 * 
	 * @param isViewed - Boolean, if true set the message as viewed.
	 */
	private void setMessageRead(boolean isViewed){
		if(_debug)Log.v("Notification.setMessageRead()");
		SMSCommon.setMessageRead(_context, getMessageID(), isViewed, _notificationType);
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
	private String formatCalendarEventMessage(String messageTitle, String messageBody, long eventStartTime, long eventEndTime, boolean allDay, String calendarName){
		if (_debug) Log.v("Notification.formatCalendarEventMessage()");
		String formattedMessage = "";
		Date eventEndDate = new Date(eventEndTime);
		Date eventStartDate = new Date(eventStartTime);
		if(messageTitle == null || messageTitle.equals("No Title")){
			messageTitle = "";
		}else{
			messageTitle = messageTitle + "<br/>";
		}
		if(messageBody == null){
			messageBody = "";	
		}else{
			messageBody = "<br/>" + messageBody;
		}
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
    		formattedMessage =  messageTitle + formattedMessage + messageBody;
		}catch(Exception ex){
			Log.e("Notification.formatCalendarEventMessage() ERROR: " + ex.toString());
			formattedMessage = startDateFormated + " - " + endDateFormated;
		}
    	if(_preferences.getBoolean(Constants.CALENDAR_LABELS_KEY, true)){
    		formattedMessage = "<b>" + calendarName + "</b><br/>" + formattedMessage;
    	}
		return formattedMessage.replace("\n", "<br/>").trim();
	}	
	
}