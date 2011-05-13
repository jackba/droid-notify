package apps.droidnotify;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.telephony.SmsMessage.MessageClass;

/**
 * 
 * @author Camille Sevigny
 *
 */
public class Notification {
	
	//================================================================================
    // Constants
    //================================================================================
	
	private final int NOTIFICATION_TYPE_PHONE = 0;
	private final int NOTIFICATION_TYPE_SMS = 1;
	private final int NOTIFICATION_TYPE_MMS = 2;
	private final int NOTIFICATION_TYPE_CALENDAR = 3;
	private final int NOTIFICATION_TYPE_EMAIL = 4;
	
	private final String SMS_DISMISS_KEY = "sms_dismiss_button_action";
	private final String MMS_DISMISS_KEY = "mms_dismiss_button_action";
	private final String MISSED_CALL_DISMISS_KEY = "missed_call_dismiss_button_action";
	private final String SMS_DELETE_KEY = "sms_delete_button_action";
	private final String MMS_DELETE_KEY = "mms_delete_button_action";
	private final String SMS_DISMISS_ACTION_MARK_READ = "0";
	private final String SMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	private final String SMS_DELETE_ACTION_DELETE_THREAD = "1";
	private final String MMS_DISMISS_ACTION_MARK_READ = "0";
	private final String MMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	private final String MMS_DELETE_ACTION_DELETE_THREAD = "1";
	private final String MISSED_CALL_DISMISS_ACTION_MARK_READ = "0";
	private final String MISSED_CALL_DISMISS_ACTION_DELETE = "1";
	
	//================================================================================
    // Properties
    //================================================================================
	
	private Context _context;
	private String _phoneNumber;
	private String _addressBookPhoneNumber;
	private String _messageBody;
	private long _timeStamp;
	private long _threadID;
	private long _contactID;
	private String _contactLookupKey;
	private String _contactName;
	private long _photoID;
	private Bitmap _photoImg;
	private int _notificationType;
	private long _messageID;
	private boolean _fromEmailGateway;
	private MessageClass _messageClass;
	private boolean _contactExists;
	private boolean _contactPhotoExists;
	private String _title;
	private String _email;
	private long _calendarID;
	private long _calendarEventID;
	private long _calendarEventStartTime;
	private long _calendarEventEndTime;
	private boolean _allDay;
	
	//================================================================================
	// Constructors
	//================================================================================
  
	/**
	 * This constructor should be called for SMS & MMS Messages.
	 * 
	 * @param context
	 * @param bundle
	 * @param notificationType
	 */
	public Notification(Context context, Bundle bundle, int notificationType) {
		if (Log.getDebug()) Log.v("Notification.Notification(Context context, Bundle bundle, int notificationType)");
		setContext(context);
		setContactExists(false);
		SmsMessage[] msgs = null;
        String messageBody = "";            
        if (bundle != null){
        	setNotificationType(notificationType);
        	if(notificationType == NOTIFICATION_TYPE_PHONE){
        		//Do Nothing. This should not be called if a missed call is received.
    	    }
        	if(notificationType == NOTIFICATION_TYPE_SMS){
        		// Retrieve SMS message from bundle.
	            Object[] pdus = (Object[]) bundle.get("pdus");
	            msgs = new SmsMessage[pdus.length]; 
	            for (int i=0; i<msgs.length; i++){
	                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
	            }
	            SmsMessage sms = msgs[0];
	            setTimeStamp(sms.getTimestampMillis());
	    		setPhoneNumber(sms.getDisplayOriginatingAddress());
	    		setFromEmailGateway(sms.isEmail());
	    		setMessageClass(sms.getMessageClass());
	    		setTitle("SMS Message");
	            //Get the entire message body from the new message.
	            for (int i=0; i<msgs.length; i++){                
	                messageBody += msgs[i].getMessageBody().toString();
	            }
	            setMessageBody(messageBody);
	    		loadThreadID(getContext(), getPhoneNumber());
	    		loadMessageID(getContext(), getThreadID(), getMessageBody(), getTimeStamp());
	    		loadContactsInfoByPhoneNumber(getContext(), getPhoneNumber());
        	}
    	    if(notificationType == NOTIFICATION_TYPE_MMS){
    	    	setTitle("MMS Message");
    	    	//TODO - MMS
    	    }
    	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
    	    	//Do Nothing. This should not be called if a calendar event is received.
    	    }
    	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
    	    	//Do Nothing. This should not be called if an email is received.
    	    }
        }
	}

	/**
	 * This constructor should be called for TEST SMS & MMS Messages.
	 * 
	 * @param context
	 * @param bundle
	 * @param notificationType
	 */
	public Notification(Context context, String phoneNumber, String messageBody, long timeStamp, int notificationType) {
		if (Log.getDebug()) Log.v("Notification.Notification(Context context, String phoneNumber, String messageBody, long timeStamp, int notificationType)");
		setContext(context);
		setNotificationType(notificationType);
		setContactExists(false);
        setTimeStamp(timeStamp);
		setPhoneNumber(phoneNumber);
		setFromEmailGateway(false);
		setMessageClass(MessageClass.CLASS_0);
		setTitle("SMS Message");
        setMessageBody(messageBody);
		loadThreadID(getContext(), getPhoneNumber());
		loadMessageID(getContext(), getThreadID(), getMessageBody(), getTimeStamp());
		loadContactsInfoByPhoneNumber(getContext(), getPhoneNumber());
	}
	
	/**
	 * This constructor should be called for Missed Calls.
	 * 
	 * @param context
	 * @param phoneNumber
	 * @param timestamp
	 * @param notificationType
	 */
	public Notification(Context context, String phoneNumber, long timeStamp, int notificationType){
		if (Log.getDebug()) Log.v("Notification.Notification(Context context, String phoneNumber, long timeStamp, int notificationType)");
		setContext(context);
		setContactExists(false);
		setContactPhotoExists(false);
		setNotificationType(notificationType);
    	if(notificationType == NOTIFICATION_TYPE_PHONE){
    		setPhoneNumber(phoneNumber);
    		setTimeStamp(timeStamp);
      		setTitle("Missed Call");
    		loadContactsInfoByPhoneNumber(getContext(), getPhoneNumber());
	    }
    	if(notificationType == NOTIFICATION_TYPE_SMS || notificationType == NOTIFICATION_TYPE_MMS){
    		//Do Nothing. This should not be called if a SMS or MMS is received.
    	}
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	//Do Nothing. This should not be called if a calendar event is received.
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	//Do Nothing. This should not be called if an email is received.
	    }
	}

	/**
	 * This constructor should be called for Calendar Events.
	 * 
	 * @param context
	 * @param phoneNumber
	 * @param timestamp
	 * @param notificationType
	 */
	public Notification(Context context, String title, String messageBody, long eventStartTime, long eventEndTime, boolean allDay, long calendarID, long calendarEventID, int notificationType){
		if (Log.getDebug()) Log.v("Notification.Notification(Context context, String title, String messageBody, long eventStartTime, long eventEndTime, boolean allDay, long calendarID, long calendarEventID, int notificationType)");
		setContext(context);
		setContactExists(false);
		setContactPhotoExists(false);
		setNotificationType(notificationType);
    	if(notificationType == NOTIFICATION_TYPE_PHONE){
    		//Do Nothing. This should not be called if a missed call is received.
	    }
    	if(notificationType == NOTIFICATION_TYPE_SMS || notificationType == NOTIFICATION_TYPE_MMS){
    		if (Log.getDebug()) Log.v("Notification.Notification() NOTIFICATION_TYPE_SMS OR NOTIFICATION_TYPE_MMS");
    		//Do Nothing. This should not be called if a SMS or MMS is received.
    	}
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	setTimeStamp(eventStartTime);
	    	setTitle(title);
	    	setAllDay(allDay);
	    	setMessageBody(formatCalendarEventMessage(messageBody, eventStartTime, eventEndTime, allDay));
	    	setCalendarID(calendarID);
	    	setCalendarEventID(calendarEventID);
	    	setCalendarEventStartTime(eventStartTime);
	    	setCalendarEventEndTime(eventEndTime);
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	//Do Nothing. This should not be called if an email is received.
	    }
	}
	
	//================================================================================
	// Accessors
	//================================================================================

	/**
	 * Set the context property.
	 */
	public void setContext(Context context) {
		if (Log.getDebug()) Log.v("Notification.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 */
	public Context getContext() {
		if (Log.getDebug()) Log.v("Notification.getContext()");
	    return _context;
	}

	/**
	 * Set the addressBookPhoneNumber property.
	 */
	public void setAddressBookPhoneNumber(String addressBookPhoneNumber) {
		if (Log.getDebug()) Log.v("Notification.setAddressBookPhoneNumber() PhoneNumber: " + addressBookPhoneNumber);
		_addressBookPhoneNumber = addressBookPhoneNumber;
	}
	
	/**
	 * Get the addressBookPhoneNumber property.
	 */
	public String getAddressBookPhoneNumber() {
		if (Log.getDebug()) Log.v("Notification.getAddressBookPhoneNumber()");
		if(_addressBookPhoneNumber == null){
			loadContactsInfoByPhoneNumber(getContext(),getPhoneNumber());
		}
		if(_addressBookPhoneNumber == null){
			return _phoneNumber;
		}
		return _addressBookPhoneNumber;
	}

	/**
	 * Set the phoneNumber property.
	 */
	public void setPhoneNumber(String phoneNumber) {
		if (Log.getDebug()) Log.v("Notification.setPhoneNumber() PhoneNumber: " + phoneNumber);
		_phoneNumber = phoneNumber;
	}
	
	/**
	 * Get the phoneNumber property.
	 */
	public String getPhoneNumber() {
		if (Log.getDebug()) Log.v("Notification.getPhoneNumber()");
		return _phoneNumber;
	}
	
	/**
	 * Set the messageBody property.
	 */
	public void setMessageBody(String messageBody) {
		if (Log.getDebug()) Log.v("Notification.setMessageBody() MessageBody: " + messageBody);
		_messageBody = messageBody;
	}
	
	/**
	 * Get the messageBody property.
	 */
	public String getMessageBody() {
		if (Log.getDebug()) Log.v("Notification.getMessageBody()");
		if (_messageBody == null) {
			_messageBody = "";
	    }
	    return _messageBody;
	}

	/**
	 * Set the timeStamp property.
	 */
	public void setTimeStamp(long timeStamp) {
		if (Log.getDebug()) Log.v("Notification.setTimeStamp() TimeStamp: " + timeStamp);
	    _timeStamp = timeStamp;
	}
	
	/**
	 * Get the timeStamp property.
	 */
	public long getTimeStamp() {
		if (Log.getDebug()) Log.v("Notification.getTimeStamp()");
	    return _timeStamp;
	}
	
	/**
	 * Set the threadID property.
	 */
	public void setThreadID(long threadID) {
		if (Log.getDebug()) Log.v("Notification.setThreadID() ThreadID: " + threadID);
	    _threadID = threadID;
	}
	
	/**
	 * Get the threadID property.
	 */
	public long getThreadID() {
		if(_threadID == 0){
			loadThreadID(getContext(), getPhoneNumber());
		}
		if (Log.getDebug()) Log.v("Notification.getThreadID() ThreadID: " + _threadID);
	    return _threadID;
	}	
	
	/**
	 * Set the contactID property.
	 */
	public void setContactID(long contactID) {
		if (Log.getDebug()) Log.v("Notification.setContactID() ContactID: " + contactID);
	    _contactID = contactID;
	}
	
	/**
	 * Get the contactID property.
	 */
	public long getContactID() {
		if (Log.getDebug()) Log.v("Notification.getContactID()");
		if(_contactID == 0){
			loadContactsInfoByPhoneNumber(getContext(),getPhoneNumber());
		}
	    return _contactID;
	}
	
	/**
	 * Set the contactLookupKey property.
	 */
	public void setContactLookupKey(String contactLookupKey) {
		if (Log.getDebug()) Log.v("Notification.setContactLookupKey() ContactLookupKey: " + contactLookupKey);
		_contactLookupKey = contactLookupKey;
	}
	
	/**
	 * Get the contactLookupKey property.
	 */
	public String getContactLookupKey() {
		if (Log.getDebug()) Log.v("Notification.getContactLookupKey()");
		if(_contactLookupKey == null){
			loadContactsInfoByPhoneNumber(getContext(),getPhoneNumber());
		}
	    return _contactLookupKey;
	}	

	/**
	 * Set the contactName property.
	 */
	public void setContactName(String contactName) {
		if (Log.getDebug()) Log.v("Notification.setContactName() ContactName: " + contactName);
		_contactName = contactName;
	}
	
	/**
	 * Get the contactName property.
	 */
	public String getContactName() {
		if (Log.getDebug()) Log.v("Notification.getContactName()");
		if (_contactName == null) {
			_contactName = _context.getString(android.R.string.unknownName);
	    }
		return _contactName;
	}

	/**
	 * Set the photoID property.
	 */
	public void setPhotoID(long photoID) {
		if (Log.getDebug()) Log.v("Notification.setPhotoID() PhotoID: " + photoID);
		_photoID = photoID;
	}
	
	/**
	 * Get the photoID property.
	 */
	public long getPhotoID() {
		if (Log.getDebug()) Log.v("Notification.getPhotoID()");
		return _photoID;
	}

	/**
	 * Set the photoImg property.
	 */
	public void setPhotoImg(Bitmap photoImg) {
		if (Log.getDebug()) Log.v("Notification.setPhotoID() PhotoIImg: " + photoImg);
		_photoImg = photoImg;
	}
	
	/**
	 * Get the photoIImg property.
	 */
	public Bitmap getPhotoImg() {
		if (Log.getDebug()) Log.v("Notification.getPhotoIImg()");
		return _photoImg;
	}
	
	/**
	 * Set the notificationType property.
	 */
	public void setNotificationType(int notificationType) {
		if (Log.getDebug()) Log.v("Notification.setNotificationType() NotificationType: " + notificationType);
		_notificationType = notificationType;
	}
	
	/**
	 * Get the notificationType property.
	 */
	public int getNotificationType() {
		if (Log.getDebug()) Log.v("Notification.getNotificationType()");
		return _notificationType;
	}

	/**
	 * Set the messageID property.
	 */
	public void setMessageID(long messageID) {
		if (Log.getDebug()) Log.v("Notification.setMessageID() MessageID: " + messageID);
  		_messageID = messageID;
	}
	
	/**
	 * Get the messageID property.
	 */
	public long getMessageID() {
		if (Log.getDebug()) Log.v("Notification.getMessageID()");
		if(_messageID == 0){
			loadMessageID(getContext(), getThreadID(), getMessageBody(), getTimeStamp());
		}
  		return _messageID;
	}

	/**
	 * Set the fromEmailGateway property.
	 */
	public void setFromEmailGateway(boolean fromEmailGateway) {
		if (Log.getDebug()) Log.v("Notification.setFromEmailGateway() FromEmailGateway: " + fromEmailGateway);
  		_fromEmailGateway = fromEmailGateway;
	}
	
	/**
	 * Get the fromEmailGateway property.
	 */
	public boolean setFromEmailGateway() {
		if (Log.getDebug()) Log.v("Notification.getFromEmailGateway()");
  		return _fromEmailGateway;
	}	

	/**
	 * Set the messageClass property.
	 */
	public void setMessageClass(MessageClass messageClass) {
		if (Log.getDebug()) Log.v("Notification.setMessageClass()");
		_messageClass = messageClass;
	}
	
	/**
	 * Get the messageClass property.
	 */
	public MessageClass getMessageClass() {
		if (Log.getDebug()) Log.v("Notification.getMessageClass()");
  		return _messageClass;
	}

	/**
	 * Set the contactExists property.
	 */
	public void setContactExists(boolean contactExists) {
		if (Log.getDebug()) Log.v("Notification.setContactExists()");
		_contactExists = contactExists;
	}
	
	/**
	 * Get the contactExists property.
	 */
	public boolean getContactExists() {
		if (Log.getDebug()) Log.v("Notification.getContactExists()");
  		return _contactExists;
	}

	/**
	 * Set the contactExists property.
	 */
	public void setContactPhotoExists(boolean contactPhotoExists) {
		if (Log.getDebug()) Log.v("Notification.setContactPhotoExists()");
		_contactPhotoExists = contactPhotoExists;
	}
	
	/**
	 * Get the contactExists property.
	 */
	public boolean getContactPhotoExists() {
		if (Log.getDebug()) Log.v("Notification.getContactPhotoExists()");
  		return _contactPhotoExists;
	}	

	/**
	 * Set the title property.
	 */
	public void setTitle(String title) {
		if (Log.getDebug()) Log.v("Notification.setTitle() Title: " + title);
		_title = title;
	}
	
	/**
	 * Get the title property.
	 */
	public String getTitle() {
		if (Log.getDebug()) Log.v("Notification.getTitle() Title: " + _title);
  		return _title;
	}

	/**
	 * Set the email property.
	 */
	public void setEmail(String email) {
		if (Log.getDebug()) Log.v("Notification.setEmail() Email: " + email);
		_email = email;
	}
	
	/**
	 * Get the email property.
	 */
	public String getEmail() {
		if (Log.getDebug()) Log.v("Notification.getEmail() Email: " + _email);
  		return _email;
	}


	/**
	 * Set the calendarID property.
	 */
	public void setCalendarID(long calendarID) {
		if (Log.getDebug()) Log.v("Notification.setCalendarID() CalendarID: " + calendarID);
		_calendarID = calendarID;
	}
	
	/**
	 * Get the calendarID property.
	 */
	public long getCalendarID() {
		if (Log.getDebug()) Log.v("Notification.getCalendarID() CalendarID: " + _calendarID);
  		return _calendarID;
	}

	/**
	 * Set the calendarEventStartTime property.
	 */
	public void setCalendarEventStartTime(long calendarEventStartTime) {
		if (Log.getDebug()) Log.v("Notification.setCalendarEventStartTime() CalendarEventStartTime: " + calendarEventStartTime);
		_calendarEventStartTime = calendarEventStartTime;
	}

	/**
	 * Set the calendarEventID property.
	 */
	public void setCalendarEventID(long calendarEventID) {
		if (Log.getDebug()) Log.v("Notification.setCalendarEventID() CalendarEventID: " + calendarEventID);
		_calendarEventID = calendarEventID;
	}
	
	/**
	 * Get the calendarEventID property.
	 */
	public long getCalendarEventID() {
		if (Log.getDebug()) Log.v("Notification.getCalendarEventID() CalendarEventID: " + _calendarEventID);
  		return _calendarEventID;
	}
	
	/**
	 * Get the calendarEventStartTime property.
	 */
	public long getCalendarEventStartTime() {
		if (Log.getDebug()) Log.v("Notification.getCalendarEventStartTime() CalendarEventStartTime: " + _calendarEventStartTime);
  		return _calendarEventStartTime;
	}

	/**
	 * Set the calendarEventEndTime property.
	 */
	public void setCalendarEventEndTime(long calendarEventEndTime) {
		if (Log.getDebug()) Log.v("Notification.setCalendarEventEndTime() CalendarEventEndTime: " + calendarEventEndTime);
		_calendarEventEndTime = calendarEventEndTime;
	}
	
	/**
	 * Get the calendarEventEndTime property.
	 */
	public long getCalendarEventEndTime() {
		if (Log.getDebug()) Log.v("Notification.getCalendarEventEndTime() CalendarEventEndTime: " + _calendarEventEndTime);
  		return _calendarEventEndTime;
	}

	/**
	 * Set the allDay property.
	 */
	public void setAllDay(boolean allDay) {
		if (Log.getDebug()) Log.v("Notification.setCalendarEventEndTime() AllDay: " + allDay);
		_allDay = allDay;
	}
	
	/**
	 * Get the allDay property.
	 */
	public boolean getAllDay() {
		if (Log.getDebug()) Log.v("Notification.getCalendarEventEndTime() AllDay: " + _allDay);
  		return _allDay;
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
  
	/**
	 * Set this notification as being viewed on the users phone.
	 */
	public void setViewed(boolean isViewed){
		if (Log.getDebug()) Log.v("Notification.setViewed()");
		Context context = getContext();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		int notificationType = getNotificationType();
		if (Log.getDebug()) Log.v("Notification.setViewed() Preference Value: " + preferences.getString(MISSED_CALL_DISMISS_KEY, "0"));
    	if(notificationType == NOTIFICATION_TYPE_PHONE){
    		//Action is determined by the users preferences. 
    		//Either mark the call log as viewed, delete the call log entry, or do nothing to the call log entry.
    		if(preferences.getString(MISSED_CALL_DISMISS_KEY, "0").equals(MISSED_CALL_DISMISS_ACTION_MARK_READ)){
    			setCallViewed(isViewed);
    		}else if(preferences.getString(MISSED_CALL_DISMISS_KEY, "0").equals(MISSED_CALL_DISMISS_ACTION_DELETE)){
    			deleteFromCallLog();
    		}
	    }
    	if(notificationType == NOTIFICATION_TYPE_SMS){
    		//Action is determined by the users preferences. 
    		//Either mark the message as viewed or do nothing to the message.
    		if(preferences.getString(SMS_DISMISS_KEY, "0").equals(SMS_DISMISS_ACTION_MARK_READ)){
    			setMessageRead(isViewed);
    		}
    	}
    	if(getNotificationType() == NOTIFICATION_TYPE_MMS){
    		//Action is determined by the users preferences. 
    		//Either mark the message as viewed or do nothing to the message.
    		if(preferences.getString(MMS_DISMISS_KEY, "0").equals(MMS_DISMISS_ACTION_MARK_READ)){
    			setMessageRead(isViewed);
    		}
    	}
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	//Do nothing. There is no log to update for Calendar Events.
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	setEmailRead(isViewed);
	    }
	}
	
	/**
	 * Set this notification as being viewed on the users phone.
	 */
	public void deleteMessage(){
		if (Log.getDebug()) Log.v("Notification.deleteMessage()");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		int notificationType = getNotificationType();
		//Decide what to do here based on the users preferences.
		//Delete the single message, delete the entire thread, or do nothing.
		boolean deleteThread = false;
		boolean deleteMessage = false;
		if(notificationType == NOTIFICATION_TYPE_SMS){
			if(preferences.getString(SMS_DELETE_KEY, "0").equals(SMS_DELETE_ACTION_DELETE_MESSAGE)){
				deleteThread = false;
				deleteMessage = true;
			}else if(preferences.getString(SMS_DELETE_KEY, "0").equals(SMS_DELETE_ACTION_DELETE_THREAD)){
				deleteThread = true;
				deleteMessage = false;
			}
		}else if(notificationType == NOTIFICATION_TYPE_MMS){
			if(preferences.getString(MMS_DELETE_KEY, "0").equals(MMS_DELETE_ACTION_DELETE_MESSAGE)){
				deleteThread = false;
				deleteMessage = true;
			}else if(preferences.getString(MMS_DELETE_KEY, "0").equals(MMS_DELETE_ACTION_DELETE_THREAD)){
				deleteThread = true;
				deleteMessage = false;
			}
		}
		Context context = getContext();
		long threadID = getThreadID();
		if(threadID == 0){
			if (Log.getDebug()) Log.v("Notification.deleteMessage() Thread ID == 0. Load Thread ID");
			loadThreadID(context, getPhoneNumber());
			threadID = getThreadID();
		}
		long messageID = getMessageID();
		if(messageID == 0){
			if (Log.getDebug()) Log.v("Notification.deleteMessage() Message ID == 0. Load Message ID");
			loadMessageID(context, getThreadID(), getMessageBody(), getTimeStamp());
			messageID = getMessageID();
		}
		if(deleteMessage || deleteThread){
			if(deleteThread){
			try{
				//Delete entire SMS thread.
				if (Log.getDebug()) Log.v("Notification.deleteMessage() Delete Thread ID: " + threadID);
				//Delete from URI "content://sms/conversations/"
				context.getContentResolver().delete(
						Uri.parse("content://sms/conversations/" + threadID), 
						null, 
						null);
				}catch(Exception ex){
					if (Log.getDebug()) Log.e("Notification.deleteMessage() Delete Thread ERROR: " + ex.toString());
				}
			}else{
				try{
					//Delete single message.
					if (Log.getDebug()) Log.v("Notification.deleteMessage() Delete Message ID: " + messageID);
					//Delete from URI "content://sms"
					context.getContentResolver().delete(
							Uri.parse("content://sms/" + messageID),
							null, 
							null);
				}catch(Exception ex){
					if (Log.getDebug()) Log.e("Notification.deleteMessageg() Delete Message ERROR: " + ex.toString());
				}
			}
		}
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Load the SMS Thread ID for this notification.
	 * 
	 * @param context
	 * @param phoneNumber
	 */
	private void loadThreadID(Context context, String phoneNumber){
		if (Log.getDebug()) Log.v("Notification.getThreadIdByAddress()");
		if (phoneNumber == null){
			if (Log.getDebug()) Log.v("Notification.loadThreadID() Phone number provided is NULL: Exiting loadThreadID()");
			return;
		}
		try{
			final String[] projection = new String[] { "_ID", "THREAD_ID" };
			final String selection = "ADDRESS = " + DatabaseUtils.sqlEscapeString(phoneNumber);
			final String[] selectionArgs = null;
			final String sortOrder = null;
		    long threadID = 0;
		    Cursor cursor = context.getContentResolver().query(
		    		Uri.parse("content://sms/inbox"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
		    if (cursor != null) {
		    	try {
		    		if (cursor.moveToFirst()) {
		    			threadID = cursor.getLong(cursor.getColumnIndex("THREAD_ID"));
		    			if (Log.getDebug()) Log.v("Notification.loadThreadID() Thread ID Found: " + threadID);
		    		}
		    	}catch(Exception e){
			    		if (Log.getDebug()) Log.v("Notification.loadThreadID() EXCEPTION: " + e.toString());
		    	} finally {
		    		cursor.close();
		    	}
		    }
		    setThreadID(threadID);
		}catch(Exception ex){
			if (Log.getDebug()) Log.e("Notification.loadThreadID() ERROR: " + ex.toString());
		}
	}

	/**
	 * Load the SMS Message ID for this notification.
	 * 
	 * @param context
	 * @param threadId
	 * @param timestamp
	 */
	public void loadMessageID(Context context, long threadID, String messageBody, long timeStamp) {
		if (Log.getDebug()) Log.v("Notification.loadMessageID()");
		if (threadID == 0){
			if (Log.getDebug()) Log.v("Notification.loadMessageID() Thread ID provided is NULL: Exiting loadMessageId()");
			return;
		}    
		if (messageBody == null){
			if (Log.getDebug()) Log.v("Notification.loadMessageID() Message body provided is NULL: Exiting loadMessageId()");
			return;
		} 
		try{
			final String[] projection = new String[] { "_ID"};
			final String selection = "THREAD_ID = " + threadID + " AND BODY = " + DatabaseUtils.sqlEscapeString(messageBody);
			final String[] selectionArgs = null;
			final String sortOrder = null;
			long messageID = 0;
		    Cursor cursor = context.getContentResolver().query(
		    		Uri.parse("content://sms/inbox"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
		    if (cursor != null) {
		    	try {
		    		if (cursor.moveToFirst()) {
		    			messageID = cursor.getLong(cursor.getColumnIndex("_ID"));
		    			if (Log.getDebug()) Log.v("Notification.loadMessageID() Message ID Found: " + messageID);
		    		}
		    	}catch(Exception e){
		    		if (Log.getDebug()) Log.v("Notification.loadMessageID() EXCEPTION: " + e.toString());
		    	} finally {
		    		cursor.close();
		    	}
		    }
		    setMessageID(messageID);
		}catch(Exception ex){
			if (Log.getDebug()) Log.e("Notification.loadMessageID() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Load the various contact info for this notification from a phoneNumber.
	 * 
	 * @param context
	 * @param phoneNumber
	 */ 
	private void loadContactsInfoByPhoneNumber(Context context, String phoneNumber){
		if (Log.getDebug()) Log.v("Notification.loadContactsInfo()");
		if (phoneNumber == null) {
			if (Log.getDebug()) Log.v("Notification.loadContactsInfo() Phone number provided is NULL: Exiting...");
			return;
		}
		try{
			//TODO - Update this code to query the DB faster by inserting a "WHERE phonenumber=?" clause.
			PhoneNumber incomingNumber = new PhoneNumber(phoneNumber);
			if (Log.getDebug()) Log.v("Notification.loadContactsInfo() Got PhoneNumber object");
			final String[] projection = null;
			final String selection = null;
			final String[] selectionArgs = null;
			final String sortOrder = null;
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					projection, 
					selection, 
					selectionArgs, 
					sortOrder);
			if (Log.getDebug()) Log.v("Notification.loadContactsInfo() Searching contacts");
			while (cursor.moveToNext()) { 
				String contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
				String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)); 
				String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String contactLookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
				String photoID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)); 
				if (Integer.parseInt(hasPhone) > 0) { 
					final String[] phoneProjection = null;
					final String phoneSelection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactID;
					final String[] phoneSelectionArgs = null;
					final String phoneSortOrder = null;
					Cursor phoneCursor = context.getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
							phoneProjection, 
							phoneSelection, 
							phoneSelectionArgs, 
							phoneSortOrder); 
			      while (phoneCursor.moveToNext()) { 
			    	  String addressBookPhoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			    	  PhoneNumber contactNumber = new PhoneNumber(addressBookPhoneNumber);
			    	  if(incomingNumber.getPhoneNumber().equals(contactNumber.getPhoneNumber())){
			    		  setContactID(Long.parseLong(contactID));
			    		  if(addressBookPhoneNumber != null){
			    			  setAddressBookPhoneNumber(addressBookPhoneNumber);
			    		  }
			    		  if(contactLookupKey != null){
			    			  setContactLookupKey(contactLookupKey);
			    		  }
			    		  if(contactName != null){
			    			  setContactName(contactName);
			    		  }
			    		  if(photoID != null){
			    			  setPhotoID(Long.parseLong(photoID));
			    		  }
			  	          Uri uri = ContentUris.withAppendedId(
			  	        		  ContactsContract.Contacts.CONTENT_URI,
			  	        		  Long.parseLong(contactID));
			  		      InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
			  		      Bitmap contactPhotoBitmap = BitmapFactory.decodeStream(input);
			  		      if(contactPhotoBitmap!= null){
			  		    	  setPhotoImg(contactPhotoBitmap);
			  		    	  setContactPhotoExists(true);
			  		      }
			  		      setContactExists(true);
			  		      break;
			    	  }
			      } 
			      phoneCursor.close(); 
			   }
			}
			cursor.close();
		}catch(Exception ex){
			if (Log.getDebug()) Log.e("Notification.loadContactsInfo() ERROR: " + ex.toString());
		}
	}

//	/**
//	 * Load the various contact info for this notification from an email address.
//	 * 
//	 * @param context
//	 * @param email
//	 */ 
//	private void loadContactsInfoByEmail(Context context, String email){
//		if (Log.getDebug()) Log.v("Notification.loadContactsInfoByEmail()");
//		if (email == null) {
//			if (Log.getDebug()) Log.v("Notification.loadContactsInfoByEmail() Email provided is NULL: Exiting...");
//			return;
//		}
//		try{
//			//TODO - Write This Function loadContactsInfoByEmail(Context context, String email)
//			if (Log.getDebug()) Log.v("Notification.loadContactsInfo() Got PhoneNumber object");
//			final String[] projection = null;
//			final String selection = null;
//			final String[] selectionArgs = null;
//			final String sortOrder = null;
//			Cursor cursor = context.getContentResolver().query(
//					ContactsContract.Contacts.CONTENT_URI,
//					projection, 
//					selection, 
//					selectionArgs, 
//					sortOrder);
//			if (Log.getDebug()) Log.v("Notification.loadContactsInfo() Searching contacts");
//			while (cursor.moveToNext()) { 
//				String contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
//				String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)); 
//				String hasEmail = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.e)); 
//				String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//				String contactLookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
//				String photoID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)); 
//					final String[] phoneProjection = null;
//					final String phoneSelection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactID;
//					final String[] phoneSelectionArgs = null;
//					final String phoneSortOrder = null;
//					Cursor phoneCursor = context.getContentResolver().query(
//							ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
//							phoneProjection, 
//							phoneSelection, 
//							phoneSelectionArgs, 
//							phoneSortOrder); 
//					while (phoneCursor.moveToNext()) { 
//			    	  String addressBookPhoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//			    	  PhoneNumber contactNumber = new PhoneNumber(addressBookPhoneNumber);
//			    	  if(incomingNumber.getPhoneNumber().equals(contactNumber.getPhoneNumber())){
//			    		  setContactID(Long.parseLong(contactID));
//			    		  if(addressBookPhoneNumber != null){
//			    			  setAddressBookPhoneNumber(addressBookPhoneNumber);
//			    		  }
//			    		  if(contactLookupKey != null){
//			    			  setContactLookupKey(contactLookupKey);
//			    		  }
//			    		  if(contactName != null){
//			    			  setContactName(contactName);
//			    		  }
//			    		  if(photoID != null){
//			    			  setPhotoID(Long.parseLong(photoID));
//			    		  }
//			  	          Uri uri = ContentUris.withAppendedId(
//			  	        		  ContactsContract.Contacts.CONTENT_URI,
//			  	        		  Long.parseLong(contactID));
//			  		      InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
//			  		      Bitmap contactPhotoBitmap = BitmapFactory.decodeStream(input);
//			  		      if(contactPhotoBitmap!= null){
//			  		    	  setPhotoImg(contactPhotoBitmap);
//			  		    	  setContactPhotoExists(true);
//			  		      }
//			  		      setContactExists(true);
//			  		      break;
//			    	  }
//			      } 
//			      phoneCursor.close(); 
//			}
//			cursor.close();
//		}catch(Exception ex){
//			if (Log.getDebug()) Log.e("Notification.loadContactsInfo() ERROR: " + ex.toString());
//		}
//	}
	
	/**
	 * Set the call log as viewed (not new) or new depending on the input.
	 * 
	 * @param isViewed
	 */
	private void setCallViewed(boolean isViewed){
		if (Log.getDebug()) Log.v("Notification.setCallViewed()");
		try{
			Context context = getContext();
			String phoneNumber = getPhoneNumber();
			long timeStamp = getTimeStamp();
			ContentValues contentValues = new ContentValues();
			if(isViewed){
				contentValues.put(android.provider.CallLog.Calls.NEW, 0);
			}else{
				contentValues.put(android.provider.CallLog.Calls.NEW, 1);
			}
			String selection = android.provider.CallLog.Calls.NUMBER + " = ? and " + android.provider.CallLog.Calls.DATE + " = ?";
			String[] selectionArgs = new String[] {DatabaseUtils.sqlEscapeString(phoneNumber), Long.toString(timeStamp)};
			context.getContentResolver().update(
					Uri.parse("content://call_log/calls"),
					contentValues,
					selection, 
					selectionArgs);
		}catch(Exception ex){
			if (Log.getDebug()) Log.e("Notification.setCallViewed() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Delete the call log entry.
	 */
	private void deleteFromCallLog(){
		if (Log.getDebug()) Log.v("Notification.deleteFromCallLog()");
		try{
			Context context = getContext();
			String phoneNumber = getPhoneNumber();
			long timeStamp = getTimeStamp();
			String selection = android.provider.CallLog.Calls.NUMBER + " = ? and " + android.provider.CallLog.Calls.DATE + " = ?";
			String[] selectionArgs = new String[] {DatabaseUtils.sqlEscapeString(phoneNumber), Long.toString(timeStamp)};
			context.getContentResolver().delete(
					Uri.parse("content://call_log/calls"),
					selection, 
					selectionArgs);
		}catch(Exception ex){
			if (Log.getDebug()) Log.e("Notification.deleteFromCallLog() ERROR: " + ex.toString());
		}
	}

	/**
	 * Set the SMS/MMS message as read or unread depending on the input.
	 * 
	 * @param isViewed
	 */
	private void setMessageRead(boolean isViewed){
		if (Log.getDebug()) Log.v("Notification.setMessageRead()");
		try{
			Context context = getContext();
			long messageID = getMessageID();
			long threadID = getThreadID();
			String messageBody = getMessageBody();
			long timeStamp = getTimeStamp();
			if(messageID == 0){
				if (Log.getDebug()) Log.v("Notification.setMessageRead() Message ID == 0. Load Message ID");
				loadMessageID(context, threadID, messageBody, timeStamp);
				messageID = getMessageID();
			}
			ContentValues contentValues = new ContentValues();
			if(isViewed){
				contentValues.put("READ", 1);
			}else{
				contentValues.put("READ", 0);
			}
			String selection = null;
			String[] selectionArgs = null;
			context.getContentResolver().update(
					Uri.parse("content://sms/" + messageID), 
		    		contentValues, 
		    		selection, 
		    		selectionArgs);
		}catch(Exception ex){
			if (Log.getDebug()) Log.e("Notification.setMessageRead() ERROR: " + ex.toString());
		}
	}

	/**
	 * Set the Email message as read or unread depending on the input.
	 * 
	 * @param isViewed
	 */
	private void setEmailRead(boolean isViewed){
		if (Log.getDebug()) Log.v("Notification.setEmailRead()");
		try{
			//TODO - Write This Function setEmailRead(boolean isViewed)
//			Context context = getContext();
//			long messageID = getMessageID();
//			long threadID = getThreadID();
//			String messageBody = getMessageBody();
//			long timeStamp = getTimeStamp();
//			if(messageID == 0){
//				if (Log.getDebug()) Log.v("Notification.setMessageRead() Message ID == 0. Load Message ID");
//				loadMessageID(context, threadID, messageBody, timeStamp);
//				messageID = getMessageID();
//			}
//			ContentValues contentValues = new ContentValues();
//			if(isViewed){
//				contentValues.put("READ", 1);
//			}else{
//				contentValues.put("READ", 0);
//			}
//			String selection = null;
//			String[] selectionArgs = null;
//			context.getContentResolver().update(
//					Uri.parse("content://sms/" + messageID), 
//		    		contentValues, 
//		    		selection, 
//		    		selectionArgs);
		}catch(Exception ex){
			if (Log.getDebug()) Log.e("Notification.setEmailRead() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Format the Notification message to display for a Calendar Event.
	 * 
	 * @param eventStartTime
	 * @param eventEndTime
	 * @param allDay
	 * @return formatted string
	 */
	private String formatCalendarEventMessage(String messageBody, long eventStartTime, long eventEndTime, boolean allDay){
		if (Log.getDebug()) Log.e("Notification.formatCalendarEventMessage()");
		String formattedMessage = "";
		SimpleDateFormat eventDateFormatted = new SimpleDateFormat();
		Date eventEndDate = new Date(eventEndTime);
		Date eventStartDate = new Date(eventStartTime);
		String[] startTimeInfo = eventDateFormatted.format(eventStartDate).split(" ");
		String[] endTimeInfo = eventDateFormatted.format(eventEndDate).split(" ");
    	if(messageBody.equals("")){
    		if(allDay){
    			formattedMessage = startTimeInfo[0] + " - All Day";
    		}else{
    			//Check if the event spans a single day or not.
    			if(startTimeInfo[0].equals(endTimeInfo[0])){
    				formattedMessage = startTimeInfo[0] + " " + startTimeInfo[1] + " " + startTimeInfo[2] +  " - " +  endTimeInfo[1] + " " + startTimeInfo[2];
    			}else{
    				formattedMessage = eventDateFormatted.format(eventStartDate) + " - " +  eventDateFormatted.format(eventEndDate);
    			}
    		}
    	}else{
    		formattedMessage = messageBody;
    	}
		return formattedMessage;
	}
}