package apps.droidnotify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;

import apps.droidnotify.calendar.CalendarCommon;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.contacts.ContactsCommon;
import apps.droidnotify.k9.K9Common;
import apps.droidnotify.log.Log;
import apps.droidnotify.phone.PhoneCommon;
import apps.droidnotify.receivers.RescheduleReceiver;
import apps.droidnotify.sms.SMSCommon;
import apps.droidnotify.reminder.ReminderCommon;

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
	private long _sentFromID = -1;
	private String _messageBody = null;
	private long _timeStamp;
	private long _threadID = -1;
	private long _contactID = -1;
	private String _contactName = null;
	private long _photoID = -1;
	private Bitmap _photoImg = null;
	private int _notificationType = -1;
	private long _messageID = -1;
	private String _messageStringID = null;
	private boolean _contactExists = false;
	private boolean _contactPhotoExists = false;
	private String _title = null;
	private long _calendarID = -1;
	private long _calendarEventID = -1;
	private long _calendarEventStartTime = -1;
	private long _calendarEventEndTime = -1;
	private String _calendarName = null;
	private boolean _allDay = false;
	private long _callLogID = -1;
	private String _lookupKey = null;
	private String _k9EmailUri = null;
	private String _k9EmailDelUri = null;
	private int _reminderNumber = 0;
	private int _notificationSubType = -1;
	private String _linkURL = null;
	private String _packageName = null;
	private PendingIntent _dismissPendingIntent = null;
	private PendingIntent _deletePendingIntent = null;
	private PendingIntent _viewPendingIntent = null;
	private Bundle _statusBarNotificationBundle = null;
	private String _soundURI = null;
	private boolean _inCallSoundEnabled = false;
	private String _vibrateSetting = null;
	private String _vibratePattern = null;
	private boolean _inCallVibrateEnabled = false;
	
	//================================================================================
	// Constructors
	//================================================================================

	/**
	 * Class Constructor
	 */
	public Notification(Context context, Bundle notificationBundle){		
		_debug = Log.getDebug();
		if (_debug) Log.v("Notification.Notification()");
		try{
			_context = context;
			_preferences = PreferenceManager.getDefaultSharedPreferences(context);
			_contactExists = false;
			_contactPhotoExists = false;
			
			//Extract information from the provided Bundle.
			_sentFromAddress = notificationBundle.getString(Constants.BUNDLE_SENT_FROM_ADDRESS);
			_sentFromID = notificationBundle.getLong(Constants.BUNDLE_SENT_FROM_ID, -1);			
			_messageBody = notificationBundle.getString(Constants.BUNDLE_MESSAGE_BODY);			
			_timeStamp = notificationBundle.getLong(Constants.BUNDLE_TIMESTAMP, -1);
			_threadID = notificationBundle.getLong(Constants.BUNDLE_THREAD_ID, -1);
			_contactID = notificationBundle.getLong(Constants.BUNDLE_CONTACT_ID, -1);
			_contactName = notificationBundle.getString(Constants.BUNDLE_CONTACT_NAME);
			_photoID = notificationBundle.getLong(Constants.BUNDLE_PHOTO_ID, -1);
			_notificationType = notificationBundle.getInt(Constants.BUNDLE_NOTIFICATION_TYPE, -1);
			_messageID = notificationBundle.getLong(Constants.BUNDLE_MESSAGE_ID, -1);
			_messageStringID = notificationBundle.getString(Constants.BUNDLE_MESSAGE_STRING_ID);
			_title = notificationBundle.getString(Constants.BUNDLE_TITLE);
			_calendarID = notificationBundle.getLong(Constants.BUNDLE_CALENDAR_ID, -1);
			_calendarEventID = notificationBundle.getLong(Constants.BUNDLE_CALENDAR_EVENT_ID, -1);
			_calendarEventStartTime = notificationBundle.getLong(Constants.BUNDLE_CALENDAR_EVENT_START_TIME, -1);
			_calendarEventEndTime = notificationBundle.getLong(Constants.BUNDLE_CALENDAR_EVENT_END_TIME, -1);
			_calendarName = notificationBundle.getString(Constants.BUNDLE_CALENDAR_NAME);
			_allDay = notificationBundle.getBoolean(Constants.BUNDLE_ALL_DAY, false);
			_callLogID = notificationBundle.getLong(Constants.BUNDLE_CALL_LOG_ID, -1);
			_lookupKey = notificationBundle.getString(Constants.BUNDLE_LOOKUP_KEY);
			_k9EmailUri = notificationBundle.getString(Constants.BUNDLE_K9_EMAIL_URI);
			_k9EmailDelUri = notificationBundle.getString(Constants.BUNDLE_K9_EMAIL_DEL_URI);
			_reminderNumber = notificationBundle.getInt(Constants.BUNDLE_REMINDER_NUMBER, 0);
			_notificationSubType = notificationBundle.getInt(Constants.BUNDLE_NOTIFICATION_SUB_TYPE, -1);
			_linkURL = notificationBundle.getString(Constants.BUNDLE_LINK_URL);			
			_packageName = notificationBundle.getString(Constants.BUNDLE_PACKAGE);
			_dismissPendingIntent = notificationBundle.getParcelable(Constants.BUNDLE_DISMISS_PENDINGINTENT);
			_deletePendingIntent = notificationBundle.getParcelable(Constants.BUNDLE_DELETE_PENDINGINTENT);
			_viewPendingIntent = notificationBundle.getParcelable(Constants.BUNDLE_VIEW_PENDINGINTENT);
			_statusBarNotificationBundle = notificationBundle.getBundle(Constants.BUNDLE_STATUS_BAR_NOTIFICATON_BUNDLE);
			
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
					_messageBody = CalendarCommon.formatCalendarEventMessage(_context, _title, _calendarEventStartTime, _calendarEventEndTime, _allDay, _calendarName);
					if(_sentFromAddress != null) _sentFromAddress = _sentFromAddress.toLowerCase();
					break;
				}
				case Constants.NOTIFICATION_TYPE_K9:{
					if(_title == null) _title = "Email";
					if(_sentFromAddress != null) _sentFromAddress = _sentFromAddress.toLowerCase();
					break;
				}
				case Constants.NOTIFICATION_TYPE_GENERIC:{
					_messageBody = String.valueOf(notificationBundle.getCharSequence(Constants.BUNDLE_DISPLAY_TEXT));
					//Get Contact Info.
					Bundle contactInfoBundle = null;
					if(_contactID >= 0 && _contactName == null){
						contactInfoBundle = ContactsCommon.getContactsInfoByID(_context, _contactID);
					}else if(_contactName != null && _contactID < 0){
						contactInfoBundle = ContactsCommon.getContactsInfoByName(_context, _contactName);
					}else{
						contactInfoBundle = ContactsCommon.getContactsInfoByID(_context, _contactID);
					}
					if(contactInfoBundle != null){
						_contactID = contactInfoBundle.getLong(Constants.BUNDLE_CONTACT_ID, -1);
						_contactName = contactInfoBundle.getString(Constants.BUNDLE_CONTACT_NAME);
						_photoID = contactInfoBundle.getLong(Constants.BUNDLE_PHOTO_ID, -1);
						_lookupKey = contactInfoBundle.getString(Constants.BUNDLE_LOOKUP_KEY);
					}
					//Sound & Vibrate Settings
					_soundURI = notificationBundle.getString(Constants.BUNDLE_STATUS_BAR_NOTIFICATION_SOUND_URI);
					_inCallSoundEnabled = notificationBundle.getBoolean(Constants.BUNDLE_STATUS_BAR_NOTIFICATION_IN_CALL_SOUND_ENABLED);
					_vibrateSetting = notificationBundle.getString(Constants.BUNDLE_STATUS_BAR_NOTIFICATION_VIBRATE_SETTING);
					_vibratePattern = notificationBundle.getString(Constants.BUNDLE_STATUS_BAR_NOTIFICATION_VIBRATE_PATTERN);
					_inCallVibrateEnabled = notificationBundle.getBoolean(Constants.BUNDLE_STATUS_BAR_NOTIFICATION_IN_CALL_VIBRATE_ENABLED);
					break;
				}
				case Constants.NOTIFICATION_TYPE_PREVIEW_PHONE:{
					if(_title == null) _title = "Missed Call";
					if(PhoneCommon.isPrivateUnknownNumber(context, _sentFromAddress)) _sentFromAddress = context.getString(R.string.private_number_text);
					if(_sentFromAddress != null) _sentFromAddress = _sentFromAddress.toLowerCase();
					break;
				}
				case Constants.NOTIFICATION_TYPE_PREVIEW_SMS:{
					if(_title == null) _title = "SMS Message";
					if(_sentFromAddress != null) _sentFromAddress = _sentFromAddress.toLowerCase();
					break;
				}
				case Constants.NOTIFICATION_TYPE_PREVIEW_CALENDAR:{
					if(_title == null) _title = "Calendar Event";
					_messageBody = CalendarCommon.formatCalendarEventMessage(_context, _title, _calendarEventStartTime, _calendarEventEndTime, _allDay, _calendarName);
					if(_sentFromAddress != null) _sentFromAddress = _sentFromAddress.toLowerCase();
					break;
				}
				case Constants.NOTIFICATION_TYPE_PREVIEW_K9:{
					if(_title == null) _title = "Email";
					if(_sentFromAddress != null) _sentFromAddress = _sentFromAddress.toLowerCase();
					break;
				}
				default:{
					break;
				}
			}
			
			if(_contactID < 0){
				_contactExists = false;
			}else{
				_contactExists = true;
			}
			
			if(_contactName != null && _contactName.equals("")) _contactName = null;
			
			if(_photoID < 0){
				_contactPhotoExists = false;
			}else{
				_contactPhotoExists = true;
			}

		}catch(Exception ex){
			Log.e("Notification.Notification() ERROR: " + ex.toString());
		}
		
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Get a Bundle that contains all the Notification information.
	 * 
	 * @param rescheduleType - An integer representing the reschedule type.
	 * 
	 * @return Bundle - Returns a bundle that contains all of the Notification information.
	 */
	public Bundle getNotificationBundle(int rescheduleType){
		if (_debug) Log.v("Notification.getNotificationBundle()");
		Bundle notificationBundle = new Bundle();
		notificationBundle.putString(Constants.BUNDLE_SENT_FROM_ADDRESS, _sentFromAddress);
		notificationBundle.putLong(Constants.BUNDLE_SENT_FROM_ID, _sentFromID);
		notificationBundle.putString(Constants.BUNDLE_MESSAGE_BODY, _messageBody);
		notificationBundle.putLong(Constants.BUNDLE_TIMESTAMP, _timeStamp);
		notificationBundle.putLong(Constants.BUNDLE_THREAD_ID, getThreadID());
		notificationBundle.putLong(Constants.BUNDLE_CONTACT_ID, _contactID);
		notificationBundle.putString(Constants.BUNDLE_CONTACT_NAME, _contactName);
		notificationBundle.putLong(Constants.BUNDLE_PHOTO_ID, _photoID);
		notificationBundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, _notificationType);
		notificationBundle.putLong(Constants.BUNDLE_MESSAGE_ID, getMessageID());
		notificationBundle.putString(Constants.BUNDLE_MESSAGE_STRING_ID, _messageStringID);
		notificationBundle.putString(Constants.BUNDLE_TITLE, _title);
		notificationBundle.putLong(Constants.BUNDLE_CALENDAR_ID, _calendarID);
		notificationBundle.putLong(Constants.BUNDLE_CALENDAR_EVENT_ID, _calendarEventID);
		notificationBundle.putLong(Constants.BUNDLE_CALENDAR_EVENT_START_TIME, _calendarEventStartTime);
		notificationBundle.putLong(Constants.BUNDLE_CALENDAR_EVENT_END_TIME, _calendarEventEndTime);
		notificationBundle.putString(Constants.BUNDLE_CALENDAR_NAME, _calendarName);
		notificationBundle.putBoolean(Constants.BUNDLE_ALL_DAY, _allDay);
		notificationBundle.putLong(Constants.BUNDLE_CALL_LOG_ID, _callLogID);
		notificationBundle.putString(Constants.BUNDLE_LOOKUP_KEY, _lookupKey);
		notificationBundle.putString(Constants.BUNDLE_K9_EMAIL_URI, _k9EmailUri);
		notificationBundle.putString(Constants.BUNDLE_K9_EMAIL_DEL_URI, _k9EmailDelUri);
		if(rescheduleType == Constants.PENDING_INTENT_TYPE_REMINDER){
			notificationBundle.putInt(Constants.BUNDLE_REMINDER_NUMBER, _reminderNumber + 1);
		}else{
			notificationBundle.putInt(Constants.BUNDLE_REMINDER_NUMBER, _reminderNumber);
		}
		notificationBundle.putInt(Constants.BUNDLE_NOTIFICATION_SUB_TYPE, _notificationSubType);		
		notificationBundle.putString(Constants.BUNDLE_PACKAGE, _packageName );
		notificationBundle.putString(Constants.BUNDLE_LINK_URL, _linkURL);
		notificationBundle.putParcelable(Constants.BUNDLE_DISMISS_PENDINGINTENT, _dismissPendingIntent);
		notificationBundle.putParcelable(Constants.BUNDLE_DELETE_PENDINGINTENT, _deletePendingIntent);
		notificationBundle.putParcelable(Constants.BUNDLE_VIEW_PENDINGINTENT, _viewPendingIntent);
		notificationBundle.putBundle(Constants.BUNDLE_STATUS_BAR_NOTIFICATON_BUNDLE, _statusBarNotificationBundle);
		notificationBundle.putString(Constants.BUNDLE_DISPLAY_TEXT, _messageBody);
		notificationBundle.putString(Constants.BUNDLE_STATUS_BAR_NOTIFICATION_SOUND_URI, _soundURI);
		notificationBundle.putBoolean(Constants.BUNDLE_STATUS_BAR_NOTIFICATION_IN_CALL_SOUND_ENABLED, _inCallSoundEnabled);
		notificationBundle.putString(Constants.BUNDLE_STATUS_BAR_NOTIFICATION_VIBRATE_SETTING, _vibrateSetting);
		notificationBundle.putString(Constants.BUNDLE_STATUS_BAR_NOTIFICATION_VIBRATE_PATTERN, _vibratePattern);
		notificationBundle.putBoolean(Constants.BUNDLE_STATUS_BAR_NOTIFICATION_IN_CALL_VIBRATE_ENABLED, _inCallVibrateEnabled);
		return notificationBundle;
	}
	
	/**
	 * Get the sentFromAddress property.
	 * 
	 * @return sentFromAddress - Persons address that sent the item.
	 */
	public String getSentFromAddress() {
		if (_debug) Log.v("Notification.getSentFromAddress()");
		//if (_debug) Log.v("Notification.getSentFromAddress() SentFromAddress: " + _sentFromAddress);
		if (_sentFromAddress == null) {
			try{
				_sentFromAddress = _context.getString(android.R.string.unknownName);
			}catch(Exception ex){
				//Set the address to blank if this fails.
				_sentFromAddress = "";
			}
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
			if(_threadID < 0){
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
			if(_messageID < 0){
				_messageID = SMSCommon.getMessageID(_context, _sentFromAddress, getThreadID(), _messageBody, _timeStamp);
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
	 * Get the reminderNumber property.
	 * 
	 * @return reminderNumber - The current reminder number.
	 */
	public int getReminderNumber() {
		if (_debug) Log.v("Notification.getReminderNumber() ReminderNumber: " + _reminderNumber);
	    return _reminderNumber;
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
	 * Get the packageName property.
	 * 
	 * @return String - The name of the package of the source of the generic notification.
	 */
	public String getPackageName() {
		if (_debug) Log.v("Notification.getPackageName()");
	    return _packageName;
	}
	
	/**
	 * Get the dismissPendingIntent property.
	 * 
	 * @return PendingIntent - The dismiss PendingIntent.
	 */
	public PendingIntent getDismissPendingIntent() {
		if (_debug) Log.v("Notification.getDismissPendingIntent()");
	    return _dismissPendingIntent;
	}
	
	/**
	 * Get the deletePendingIntent property.
	 * 
	 * @return PendingIntent - The delete PendingIntent.
	 */
	public PendingIntent getDeletePendingIntent() {
		if (_debug) Log.v("Notification.getDeletePendingIntent()");
	    return _deletePendingIntent;
	}
	
	/**
	 * Get the viewPendingIntent property.
	 * 
	 * @return PendingIntent - The view PendingIntent.
	 */
	public PendingIntent getViewPendingIntent() {
		if (_debug) Log.v("Notification.getViewPendingIntent()");
	    return _viewPendingIntent;
	}
	
	/**
	 * Get the statusBarNotificationBundle property.
	 * 
	 * @return statusBarNotificationBundle - The Bundle that contains status bar notification properties.
	 */
	public Bundle getStatusBarNotificationBundle() {
		if (_debug) Log.v("Notification.getStatusBarNotificationBundle()");
	    return _statusBarNotificationBundle;
	}
	
	/**
	 * Get the soundURI property.
	 * 
	 * @return String - The sound URI of the notification.
	 */
	public String getSoundURI(){
		return _soundURI;
	}	
	
	/**
	 * Get the inCallSoundEnabled property.
	 * 
	 * @return String - The in call sound setting  of the source of the notification.
	 */
	public boolean getInCallSoundEnabled(){
		return _inCallSoundEnabled;
	}
	/**
	 * Get the vibrateSetting property.
	 * 
	 * @return String - The vibrate setting of the notification.
	 */
	public String getVibrateSetting(){
		return _vibrateSetting;
	}
	/**
	 * Get the vibratePattern property.
	 * 
	 * @return String - The vibrate pattern of the notification.
	 */
	public String getVibratePattern(){
		return _vibratePattern;
	}
	
	/**
	 * Get the inCallVibrateEnabled property.
	 * 
	 * @return String - The in call vibrate setting of the notification.
	 */
	public boolean getInCallVibrateEnabled(){
		 return _inCallVibrateEnabled;
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
				String dismissButtonAction = _preferences.getString(Constants.PHONE_DISMISS_KEY, "0");
	    		//Action is determined by the users preferences. 
	    		//Either mark the call log as viewed, delete the call log entry, or do nothing.
	    		if(dismissButtonAction.equals(Constants.PHONE_DISMISS_ACTION_MARK_READ)){
	    			setCallViewed(isViewed);
	    		}else if(dismissButtonAction.equals(Constants.PHONE_DISMISS_ACTION_DELETE)){
	    			deleteFromCallLog();
	    		}
				break;
			}
			case Constants.NOTIFICATION_TYPE_SMS:{
	    		//Action is determined by the users preferences. 
	    		//Either mark the message as viewed or do nothing.
	    		if(_preferences.getString(Constants.SMS_DISMISS_KEY, "0").equals(Constants.SMS_DISMISS_ACTION_MARK_READ)){
	    			setMessageRead(isViewed);
	    		}
				break;
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
	    		if(_preferences.getString(Constants.SMS_DISMISS_KEY, "0").equals(Constants.SMS_DISMISS_ACTION_MARK_READ)){
	    			setMessageRead(isViewed);
	    		}
				break;
			}
			case Constants.NOTIFICATION_TYPE_CALENDAR:{
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
		boolean deleteThread = false;
		boolean deleteMessage = false;
		if(_notificationType == Constants.NOTIFICATION_TYPE_SMS || _notificationType == Constants.NOTIFICATION_TYPE_MMS){
			//Decide what to do here based on the users preferences.
			//Delete the single message, delete the entire thread, or do nothing.
			if(_notificationType == Constants.NOTIFICATION_TYPE_SMS){
				if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_DELETE_MESSAGE)){
					deleteThread = false;
					deleteMessage = true;
				}else if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_DELETE_THREAD)){
					deleteThread = true;
					deleteMessage = false;
				}
			}else if(_notificationType == Constants.NOTIFICATION_TYPE_MMS){
				if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_DELETE_MESSAGE)){
					deleteThread = false;
					deleteMessage = true;
				}else if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_DELETE_THREAD)){
					deleteThread = true;
					deleteMessage = false;
				}
			}
			if(deleteMessage || deleteThread){
				if(deleteThread){
					SMSCommon.deleteMessageThread(_context, getThreadID(), _notificationType);
				}else{
					SMSCommon.deleteSingleMessage(_context, getMessageID(), getThreadID(), _notificationType);
				}
			}
		}else if(_notificationType == Constants.NOTIFICATION_TYPE_K9){
			K9Common.deleteK9Email(_context, _k9EmailDelUri, _notificationSubType);
		}
	}
	
	/**
	 * Sets the alarm that will remind the user with another popup of the notification.
	 */
	public void setReminder(){
		if (_debug) Log.v("Notification.setReminder()");
		boolean calendarReminder = false;
		if(_notificationType == Constants.NOTIFICATION_TYPE_CALENDAR){
			calendarReminder = true;	
		}
		if(_preferences.getBoolean(Constants.REMINDERS_ENABLED_KEY, false) || calendarReminder){							
			boolean triggerReminder = true;
			int maxRescheduleAttempts = -1;		
			long rescheduleTime = -1;
			if(calendarReminder){
				maxRescheduleAttempts = Integer.parseInt(_preferences.getString(Constants.CALENDAR_REMINDER_FREQUENCY_KEY, Constants.CALENDAR_REMINDER_FREQUENCY_DEFAULT));		
				rescheduleTime = System.currentTimeMillis() + Long.parseLong(_preferences.getString(Constants.CALENDAR_REMINDER_INTERVAL_KEY, Constants.CALENDAR_REMINDER_INTERVAL_DEFAULT)) * 60 * 1000;
			}else{
				maxRescheduleAttempts = Integer.parseInt(_preferences.getString(Constants.REMINDER_FREQUENCY_KEY, Constants.REMINDER_FREQUENCY_DEFAULT));		
				rescheduleTime = System.currentTimeMillis() + Long.parseLong(_preferences.getString(Constants.REMINDER_INTERVAL_KEY, Constants.REMINDER_INTERVAL_DEFAULT)) * 60 * 1000;
			}
			//Determine if the notification should be rescheduled or not.
			if(maxRescheduleAttempts < 0){
				//Infinite Attempts.
				triggerReminder = true;
			}else if(_reminderNumber >= maxRescheduleAttempts){
				triggerReminder = false;
			}
			if(triggerReminder){
				if (_debug) Log.v("Notification.setReminder() Reminder has been triggered.");
				reschedule(rescheduleTime, Constants.PENDING_INTENT_TYPE_REMINDER);
			}else{
				if (_debug) Log.v("Notification.setReminder() Reminder will not be triggered.");
			}
		}
	}
	
	/**
	 * Cancel the reminder alarm.
	 */
	public void cancelReminder() {
		if (_debug) Log.v("Notification.cancelReminder()");
		
		//Create and cancel the PendingIntent that we want to cancel.
		Intent reminderIntent = new Intent(_context, RescheduleReceiver.class);
		String intentAction = getIntentAction(Constants.PENDING_INTENT_TYPE_REMINDER);
		reminderIntent.setAction(intentAction);
		PendingIntent reminderPendingIntent = PendingIntent.getBroadcast(_context, 0, reminderIntent, 0);
		reminderPendingIntent.cancel();
		if (_debug) Log.v("Notification.cancelReminder() Reminder Notification Action: " + intentAction);
		
		//Cancel the alarm.
    	AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
    	alarmManager.cancel(reminderPendingIntent);
    	
    	//Update the reminder in the db.
    	ReminderCommon.updateValue(_context, intentAction, true);
	}
	
	/**
	 * Speak the notification text using TTS.
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
				String formattedTimestamp = Common.formatTimestamp(_context, _timeStamp);
				messageToSpeak.append(_context.getString(R.string.missed_call_at_text, formattedTimestamp.toLowerCase()));
				messageToSpeak.append(". " + _context.getString(R.string.from_text) + " " + sentFrom + ". ");
				break;
			}
			case Constants.NOTIFICATION_TYPE_SMS:{
				String formattedTimestamp = Common.formatTimestamp(_context, _timeStamp);
				messageToSpeak.append(_context.getString(R.string.message_at_text, formattedTimestamp.toLowerCase()));
				messageToSpeak.append(". " + _context.getString(R.string.from_text) + " " + sentFrom + ". ");
				messageToSpeak.append(_messageBody);
				break;
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
				String formattedTimestamp = Common.formatTimestamp(_context, _timeStamp);
				messageToSpeak.append(_context.getString(R.string.message_at_text, formattedTimestamp.toLowerCase()));
				messageToSpeak.append(". " + _context.getString(R.string.from_text) + " " + sentFrom + ". ");
				messageToSpeak.append(_messageBody);
				break;
			}
			case Constants.NOTIFICATION_TYPE_CALENDAR:{
				messageToSpeak.append(_context.getString(R.string.calendar_event_text) + ". " + _messageBody);
				break;
			}
			case Constants.NOTIFICATION_TYPE_K9:{
				String formattedTimestamp = Common.formatTimestamp(_context, _timeStamp);
				messageToSpeak.append(_context.getString(R.string.email_at_text, formattedTimestamp.toLowerCase()));
				messageToSpeak.append(". " + _context.getString(R.string.from_text) + " " + sentFrom + ". ");
				messageToSpeak.append(_messageBody);
				break;
			}
			case Constants.NOTIFICATION_TYPE_GENERIC:{
				String formattedTimestamp = Common.formatTimestamp(_context, _timeStamp);
				messageToSpeak.append(_context.getString(R.string.message_at_text, formattedTimestamp.toLowerCase()));
				if(_contactName != null && !_contactName.equals(_context.getString(android.R.string.unknownName))){
					messageToSpeak.append(". " + _context.getString(R.string.from_text) + " " + _contactName + ". ");
				}else{
					messageToSpeak.append(". ");
				}
				messageToSpeak.append(_messageBody);
				break;
			}
		}
		if(messageToSpeak != null){
			Common.speak(_context, tts, Common.removeHTML(messageToSpeak.toString()));
		}
	}
	
	/**
	 * Determine if two notification objects are equal.
	 * 
	 * @param notification - The notification that we are comparing to.
	 * 
	 * @return boolean - Returns true if these two notifications are determined to be equal. 
	 *                   This equality calculation differs depending on their notification type.
	 */
	public boolean equals(Notification notification) {
		if (_debug) Log.v("Notification.equals()");
		if(_notificationType != notification.getNotificationType()){
			return false;
		}
		if(_notificationSubType != notification.getNotificationSubType()){
			return false;
		}
		switch(_notificationType){
			case Constants.NOTIFICATION_TYPE_PHONE:{
				if(_callLogID == notification.getCallLogID()){
					return true;
				}
				return false;
			}
			case Constants.NOTIFICATION_TYPE_SMS:{
				if(getMessageID() == notification.getMessageID()){
					return true;
				}
				return false;
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
				if(getMessageID() == notification.getMessageID()){
					return true;
				}
				return false;
			}
			case Constants.NOTIFICATION_TYPE_CALENDAR:{
				if(_calendarID == notification.getCalendarID() && _calendarEventID == notification.getCalendarEventID()){
					return true;
				}
				return false;
			}
			case Constants.NOTIFICATION_TYPE_K9:{
				if(_messageID == notification.getMessageID()){
					return true;
				}
				return false;
			}
			case Constants.NOTIFICATION_TYPE_GENERIC:{
				if(_packageName.equals(notification.getPackageName()) && _timeStamp == notification.getTimeStamp()){
					return true;
				}
				return false;
			}
			default:{
				String notificationSentFromAddress = notification.getSentFromAddress();
				if(_timeStamp == notification.getTimeStamp()){
					if(_sentFromAddress == null && notificationSentFromAddress == null){
						return true;
					}else if(_sentFromAddress != null && notificationSentFromAddress != null && _sentFromAddress.equals(notificationSentFromAddress)){
						return true;
					}
				}
				return false;
			}
		}
	}

	/**
	 * Get a unique intent action for this notification.
	 * 
	 * This MUST be unique even across reschedule/reminder events, so the reschedule/reminder Number must be included 
	 * in the action or this may fail to be recognized as a new Intent! 
	 * 
	 * @param rescheduleType - An integer representing the reschedule type.
	 * 
	 * @return String - Returns a unique intent action for this notification.
	 */
	public String getIntentAction(int rescheduleType){
		if (_debug) Log.v("Notification.getIntentAction() RescheduleType: " + rescheduleType);
		String preText = null;
		if(rescheduleType == Constants.PENDING_INTENT_TYPE_REMINDER){
			preText = "apps.droidnotify.reminder.";
		}else if(rescheduleType == Constants.PENDING_INTENT_TYPE_RESCHEDULE){
			preText = "apps.droidnotify.reschedule.";
		}else if(rescheduleType == Constants.PENDING_INTENT_TYPE_SNOOZE){
			preText = "apps.droidnotify.snooze.";
		}
		switch(_notificationType){
			case Constants.NOTIFICATION_TYPE_PHONE:{
				return preText + 
						String.valueOf(_reminderNumber) + "." + 
						String.valueOf(_notificationType) + "." + 
						String.valueOf(_notificationSubType) + "." + 
						String.valueOf(_callLogID) + "." + 
						String.valueOf(_timeStamp);
			}
			case Constants.NOTIFICATION_TYPE_SMS:{
				return preText + 
						String.valueOf(_reminderNumber) + "." + 
						String.valueOf(_notificationType) + "." + 
						String.valueOf(_notificationSubType) + "." + 
						String.valueOf(_messageID) + "." + 
						String.valueOf(_threadID) + "." + 
						String.valueOf(_timeStamp);
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
				return preText + 
						String.valueOf(_reminderNumber) + "." + 
						String.valueOf(_notificationType) + "." + 
						String.valueOf(_notificationSubType) + "." + 
						String.valueOf(_messageID) + "." + 
						String.valueOf(_threadID) + "." + 
						String.valueOf(_timeStamp);
			}
			case Constants.NOTIFICATION_TYPE_CALENDAR:{
				return preText + 
						String.valueOf(_reminderNumber) + "." +  
						String.valueOf(_notificationType) + "." + 
						String.valueOf(_notificationSubType) + "." + 
						String.valueOf(_calendarID) + "." + 
						String.valueOf(_calendarEventID);
			}
			case Constants.NOTIFICATION_TYPE_K9:{
				return preText + 
						String.valueOf(_reminderNumber) + "." + 
						String.valueOf(_notificationType) + "." + 
						String.valueOf(_notificationSubType) + "." + 
						String.valueOf(_messageID) + "." + 
						String.valueOf(_timeStamp);
			}
			case Constants.NOTIFICATION_TYPE_GENERIC:{
				return preText + 
						String.valueOf(_reminderNumber) + "." + 
						String.valueOf(_notificationType) + "." + 
						String.valueOf(_notificationSubType) + "." +  
						_packageName + "." + 
						String.valueOf(_timeStamp);
			}
			default:{
				return null;
			}
		}
	}
	
	/**
	 * Reschedule this notification.
	 * 
	 * @param rescheduleTime - The time we want the notification to be rescheduled.
	 * @param rescheduleType - An integer representing the reschedule type.
	 * 
	 */
	public void reschedule(long rescheduleTime, int rescheduleType){
		if (_debug) Log.v("Notification.reschedule() RescheduleTime: " + rescheduleTime + " RescheduleType: " + rescheduleType);
		long rescheduleInMinutes = (rescheduleTime - System.currentTimeMillis()) / 60 / 1000;
		if (_debug) Log.v("Notification.reschedule() Rescheduling notification. Rechedule in " + String.valueOf(rescheduleInMinutes) + " minutes.");
		
		//Create the bundle that will be rescheduled.
		Bundle rescheduleBundle = new Bundle();
		if(_notificationType == Constants.NOTIFICATION_TYPE_GENERIC){
			rescheduleBundle = getNotificationBundle(rescheduleType);
		}else{
			Bundle rescheduleNotificationBundle = new Bundle();
			rescheduleNotificationBundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_1", getNotificationBundle(rescheduleType));
			rescheduleNotificationBundle.putInt(Constants.BUNDLE_NOTIFICATION_BUNDLE_COUNT, 1);			
			rescheduleBundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, rescheduleNotificationBundle);
			rescheduleBundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, _notificationType);
		}

		//Create the PendingIntent.
		Intent rescheduleIntent = new Intent(_context, RescheduleReceiver.class);
		rescheduleIntent.putExtras(rescheduleBundle);
		//The action is what makes this intent unique from all other intents using this class.
		String intentAction = getIntentAction(rescheduleType);
		rescheduleIntent.setAction(intentAction);
		if (_debug) Log.v("Notification.reschedule() Notification Action: " + intentAction);		
		//Android can be very frustrating. The Android OS saves PendingIntent Broadcasts for later use. 
		//If you have set a PendingIntent before with the same Action, Class but DIFFERENT extras (data), it will return the first saved PendingIntent.
		//It will NOT return the latest one!!! Very Frustrating!
		PendingIntent reschedulePendingIntent = PendingIntent.getBroadcast(_context, 0, rescheduleIntent, 0);
		
		//Schedule the alarm.
		AlarmManager alarmManager = (AlarmManager)_context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, rescheduleTime, reschedulePendingIntent);
		
		//Insert reminder into the reminder db.
		ReminderCommon.insertValue(_context, intentAction, false);
	}
	
	/**
	 * Post a status bar notification to the Android notification system.
	 * 
	 * @param notificationTypecount - The total number of notifications of this type currently being displayed.
	 * @param statusBarNotificationBundle - A Bundle that contains the android status bar notification settings.
	 * 
	 */
	public void postStatusBarNotification(int notificationTypecount, Bundle statusBarNotificationBundle){
		if (_debug) Log.v("Notification.setStatusBarNotification()");
		if(_notificationType == Constants.NOTIFICATION_TYPE_GENERIC){
		    Common.setStatusBarNotification(_context, 1, Constants.NOTIFICATION_TYPE_GENERIC, -1, true, null, -1, null, null, null, null, -1, false, statusBarNotificationBundle);
		}else{
			Common.setStatusBarNotification(_context, notificationTypecount, _notificationType, _notificationSubType, true, _contactName, _contactID, _sentFromAddress, _messageBody, _k9EmailUri, _linkURL, _threadID, false, statusBarNotificationBundle);
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
		SMSCommon.setMessageRead(_context, getMessageID(), getThreadID(), isViewed);
	}
	
}