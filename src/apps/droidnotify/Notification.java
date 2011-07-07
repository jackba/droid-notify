package apps.droidnotify;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import apps.droidnotify.common.Common;

/**
 * This is the Notification class that holds all the information about all notifications we will display to the user.
 * 
 * @author Camille Sévigny
 */
public class Notification {
	
	//================================================================================
    // Constants
    //================================================================================
	
	private static final int NOTIFICATION_TYPE_PHONE = 0;
	private static final int NOTIFICATION_TYPE_SMS = 1;
	private static final int NOTIFICATION_TYPE_MMS = 2;
	private static final int NOTIFICATION_TYPE_CALENDAR = 3;
	private static final int NOTIFICATION_TYPE_EMAIL = 4;
	
	private static final String SMS_DISMISS_KEY = "sms_dismiss_button_action";
	private static final String MMS_DISMISS_KEY = "mms_dismiss_button_action";
	private static final String MISSED_CALL_DISMISS_KEY = "missed_call_dismiss_button_action";
	private static final String SMS_DELETE_KEY = "sms_delete_button_action";
	private static final String MMS_DELETE_KEY = "mms_delete_button_action";
	private static final String CALENDAR_LABELS_KEY = "calendar_labels_enabled";
	
	private static final String SMS_DISMISS_ACTION_MARK_READ = "0";
	private static final String SMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	private static final String SMS_DELETE_ACTION_DELETE_THREAD = "1";
	private static final String MMS_DISMISS_ACTION_MARK_READ = "0";
	private static final String MMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	private static final String MMS_DELETE_ACTION_DELETE_THREAD = "1";
	private static final String MISSED_CALL_DISMISS_ACTION_MARK_READ = "0";
	private static final String MISSED_CALL_DISMISS_ACTION_DELETE = "1";
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;
	private Context _context = null;
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
	private SharedPreferences _preferences = null;
	
	//================================================================================
	// Constructors
	//================================================================================

	/**
	 * Class Constructor
	 */
	public Notification(Context context, long messageID, long threadID, String messageBody, String sentFromAddress, long timeStamp, long contactID, int notificationType) {
		_debug = Log.getDebug();
		if (_debug) Log.v("Notification.Notification(Context context, long messageID, long threadID, String messageBody, String phoneNumber, long timeStamp, long contactID, int notificationType)");
		try{
			if(notificationType == NOTIFICATION_TYPE_PHONE){
				_title = "Missed Call";
		    }
			if(notificationType == NOTIFICATION_TYPE_SMS){
				_title = "SMS Message";
			}
			if(notificationType == NOTIFICATION_TYPE_MMS){
				_title = "MMS Message";	
		    }
		    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
		    	_title = "Calendar Event";
		    }
		    if(notificationType == NOTIFICATION_TYPE_EMAIL){
		    	_title = "Email";
		    }
			_context = context;
			_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
			_contactExists = false;
			_contactPhotoExists = false;
			_notificationType = notificationType;
			_messageID = messageID;
			_threadID = threadID;
			_messageBody = messageBody.replace("\n", "<br/>").trim();
			_sentFromAddress = sentFromAddress.toLowerCase();
            if(_sentFromAddress.contains("@")){
            	_sentFromAddress = 	Common.removeEmailFormatting(_sentFromAddress);
            }
	        _timeStamp = timeStamp;
	        _contactID = contactID;
			_notificationType = notificationType;
			loadContactsInfoByPhoneNumber(_context, _sentFromAddress);
			//Search by email if we can't find the contact info.
			if(!_contactExists){
				loadContactsInfoByEmail(_context, _sentFromAddress);
			}
		}catch(Exception ex){
			if (_debug) Log.v("Notification.Notification(Context context, long messageID, long threadID, String messageBody, String phoneNumber, long timeStamp, long contactID, int notificationType) ERROR: " + ex.toString());
		}
	}

	/**
	 * Class Constructor
	 */
	public Notification(Context context, String sentFromAddress, String messageBody, long messageID, long threadID, long timeStamp, long contactID, String contactName, long photoID, int notificationType) {
		_debug = Log.getDebug();
		if (_debug) Log.v("Notification.Notification(Context contex, String sentFromAddresst, String messageBody, long messageID, long threadID, long timeStamp, long contactID, String contactName, long photoID, int notificationType)");
		try{
			if(notificationType == NOTIFICATION_TYPE_PHONE){
				_title = "Missed Call";
		    }
			if(notificationType == NOTIFICATION_TYPE_SMS){
				_title = "SMS Message";
			}
			if(notificationType == NOTIFICATION_TYPE_MMS){
				_title = "MMS Message";	
		    }
		    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
		    	_title = "Calendar Event";
		    }
		    if(notificationType == NOTIFICATION_TYPE_EMAIL){
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
		}catch(Exception ex){
			if (_debug) Log.v("Notification.Notification(Context contex, String sentFromAddresst, String messageBody, long messageID, long threadID, long timeStamp, long contactID, String contactName, long photoID, int notificationType) ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Class Constructor
	 */
	public Notification(Context context, String sentFromAddress, String messageBody, long timeStamp, int notificationType) {
		_debug = Log.getDebug();
		if (_debug) Log.v("Notification.Notification(Context context, String phoneNumber, String messageBody, long timeStamp, int notificationType)");
		try{			
			if(notificationType == NOTIFICATION_TYPE_PHONE){
				_title = "Missed Call";
		    }
			if(notificationType == NOTIFICATION_TYPE_SMS){
				_title = "SMS Message";
			}
			if(notificationType == NOTIFICATION_TYPE_MMS){
				_title = "MMS Message";	
		    }
		    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
		    	_title = "Calendar Event";
		    }
		    if(notificationType == NOTIFICATION_TYPE_EMAIL){
		    	_title = "Email";
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
	public Notification(Context context, String sentFromAddress, long timeStamp, long contactID, String contactName, long photoID, int notificationType){
		_debug = Log.getDebug();
		if (_debug) Log.v("Notification.Notification(Context context, String sentFromAddress, long timeStamp, long contactID, string contactName, long photoID, int notificationType)");
		try{
			if(notificationType == NOTIFICATION_TYPE_PHONE){
				_title = "Missed Call";
		    }
			if(notificationType == NOTIFICATION_TYPE_SMS){
				_title = "SMS Message";
			}
			if(notificationType == NOTIFICATION_TYPE_MMS){
				_title = "MMS Message";	
		    }
		    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
		    	_title = "Calendar Event";
		    }
		    if(notificationType == NOTIFICATION_TYPE_EMAIL){
		    	_title = "Email";
		    }
			_context = context;
			_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
			_contactExists = false;
			_contactPhotoExists = false;
			_notificationType = notificationType;
    		_sentFromAddress = sentFromAddress.toLowerCase();
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
		}catch(Exception ex){
			if (_debug) Log.v("Notification.Notification(Context context, String sentFromAddress, long timeStamp, long contactID, String contactName, long photoID, int notificationType) ERROR: " + ex.toString());
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
	    	_messageBody = formatCalendarEventMessage(messageBody, eventStartTime, eventEndTime, allDay, calendarName).replace("\n", "<br/>").trim();
	    	_calendarID = calendarID;
	    	_calendarEventID = calendarEventID;
	    	_calendarEventStartTime = eventStartTime;
	    	_calendarEventEndTime = eventEndTime;
		}catch(Exception ex){
			if (_debug) Log.v("Notification.Notification(Context context, String title, String messageBody, long eventStartTime, long eventEndTime, boolean allDay, String calendarName, long calendarID, long calendarEventID, int notificationType) ERROR: " + ex.toString());
		}
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Get the phoneNumber property.
	 * 
	 * @return phoneNumber - Contact's phone number.
	 */
	public String getSentFromAddress() {
		if (_debug) Log.v("Notification.getSentFromAddress()");
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
		if (_debug) Log.v("Notification.getTimeStamp()");
	    return _timeStamp;
	}
	
	/**
	 * Get the threadID property.
	 * 
	 * @return threadID - SMS/MMS Message thread id.
	 */
	public long getThreadID() {
		if(_threadID == 0){
			_threadID = Common.loadThreadID(_context, _sentFromAddress);
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
		if (_debug) Log.v("Notification.getContactID()");
	    return _contactID;
	}
	
	/**
	 * Get the contactName property.
	 * 
	 * @return contactName - Contact's display name.
	 */
	public String getContactName() {
		if (_debug) Log.v("Notification.getContactName()");
		if (_contactName == null) {
			_contactName = _context.getString(android.R.string.unknownName);
	    }
		return _contactName;
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
		if (_debug) Log.v("Notification.getNotificationType()");
		return _notificationType;
	}
	
	/**
	 * Get the messageID property.
	 * 
	 * @return messageID - The message id of the SMS/MMS message.
	 */
	public long getMessageID() {
		if(_messageID == 0){
			_messageID = Common.loadMessageID(_context, getThreadID(), _messageBody, _timeStamp);
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
	 * Get the title property.
	 * 
	 * @return title - Notification title.
	 */
	public String getTitle() {
		if (_debug) Log.v("Notification.getTitle() Title: " + _title);
  		return _title;
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
	 * Set this notification as being viewed on the users phone.
	 * 
	 * @param isViewed - Boolean value to set or unset the item as being viewed.
	 */
	public void setViewed(boolean isViewed){
		if (_debug) Log.v("Notification.setViewed()");
    	if(_notificationType == NOTIFICATION_TYPE_PHONE){
    		//Action is determined by the users preferences. 
    		//Either mark the call log as viewed, delete the call log entry, or do nothing to the call log entry.
    		if(_preferences.getString(MISSED_CALL_DISMISS_KEY, "0").equals(MISSED_CALL_DISMISS_ACTION_MARK_READ)){
    			setCallViewed(isViewed);
    		}else if(_preferences.getString(MISSED_CALL_DISMISS_KEY, "0").equals(MISSED_CALL_DISMISS_ACTION_DELETE)){
    			deleteFromCallLog();
    		}
	    }
    	if(_notificationType == NOTIFICATION_TYPE_SMS){
    		//Action is determined by the users preferences. 
    		//Either mark the message as viewed or do nothing to the message.
    		if(_preferences.getString(SMS_DISMISS_KEY, "0").equals(SMS_DISMISS_ACTION_MARK_READ)){
    			setMessageRead(isViewed);
    		}
    	}
    	if(_notificationType == NOTIFICATION_TYPE_MMS){
    		//Action is determined by the users preferences. 
    		//Either mark the message as viewed or do nothing to the message.
    		if(_preferences.getString(MMS_DISMISS_KEY, "0").equals(MMS_DISMISS_ACTION_MARK_READ)){
    			setMessageRead(isViewed);
    		}
    	}
	    if(_notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	//Do nothing. There is no log to update for Calendar Events.
	    }
	    if(_notificationType == NOTIFICATION_TYPE_EMAIL){
	    	setEmailRead(isViewed);
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
		if(_notificationType == NOTIFICATION_TYPE_SMS){
			if(_preferences.getString(SMS_DELETE_KEY, "0").equals(SMS_DELETE_ACTION_DELETE_MESSAGE)){
				deleteThread = false;
				deleteMessage = true;
			}else if(_preferences.getString(SMS_DELETE_KEY, "0").equals(SMS_DELETE_ACTION_DELETE_THREAD)){
				deleteThread = true;
				deleteMessage = false;
			}
		}else if(_notificationType == NOTIFICATION_TYPE_MMS){
			if(_preferences.getString(MMS_DELETE_KEY, "0").equals(MMS_DELETE_ACTION_DELETE_MESSAGE)){
				deleteThread = false;
				deleteMessage = true;
			}else if(_preferences.getString(MMS_DELETE_KEY, "0").equals(MMS_DELETE_ACTION_DELETE_THREAD)){
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
	 * Load the various contact info for this notification from a phoneNumber.
	 * 
	 * @param context - Application Context.
	 * @param phoneNumber - Notifications's phone number.
	 */ 
	private void loadContactsInfoByPhoneNumber(Context context, String incomingNumber){
		if (_debug) Log.v("Notification.loadContactsInfo()");
		if (incomingNumber == null) {
			if (_debug) Log.v("Notification.loadContactsInfo() Phone number provided is null: Exiting...");
			return;
		}
		//Exit if the phone number is an email address.
		if (incomingNumber.contains("@")) {
			if (_debug) Log.v("Notification.loadContactsInfo() Phone number provided appears to be an email address: Exiting...");
			return;
		}
		try{
			final String[] projection = null;
			final String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1";
			final String[] selectionArgs = null;
			final String sortOrder = null;
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					projection, 
					selection, 
					selectionArgs, 
					sortOrder);
			if (_debug) Log.v("Notification.loadContactsInfo() Searching contacts");
			while (cursor.moveToNext()) { 
				String contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
				String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String photoID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)); 
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
					String contactNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					if(Common.removeFormatting(incomingNumber).equals(Common.removeFormatting(contactNumber))){
						_contactID = Long.parseLong(contactID);
		    		  	if(contactName != null){
		    		  		_contactName = contactName;
		    		  	}
		    		  	if(photoID != null){
		    			  	_photoID = Long.parseLong(photoID);
		    		  	}
		  	          	Uri uri = ContentUris.withAppendedId(
		  	        		  ContactsContract.Contacts.CONTENT_URI,
		  	        		  Long.parseLong(contactID));
		  		      	InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
		  		      	Bitmap contactPhotoBitmap = BitmapFactory.decodeStream(input);
		  		      	if(contactPhotoBitmap!= null){
		  		    	  	_photoImg = contactPhotoBitmap;
		  		    	  	_contactPhotoExists = true;
		  		      	}
		  		      _contactExists = true;
		  		      	break;
					}
				}
				phoneCursor.close(); 
				if(_contactExists) break;
		   	}
			cursor.close();
		}catch(Exception ex){
			if (_debug) Log.e("Notification.loadContactsInfo() ERROR: " + ex.toString());
		}
	}

	/**
	 * Load the various contact info for this notification from an email.
	 * 
	 * @param context - Application Context.
	 * @param incomingEmail - Notifications's email address.
	 */ 
	private void loadContactsInfoByEmail(Context context, String incomingEmail){
		if (_debug) Log.v("Notification.loadContactsInfoByEmail()");
		if (incomingEmail == null) {
			if (_debug) Log.v("Notification.loadContactsInfoByEmail() Email provided is null: Exiting...");
			return;
		}
		if (!incomingEmail.contains("@")) {
			if (_debug) Log.v("Notification.loadContactsInfoByEmail() Email provided does not appear to be a valid email address: Exiting...");
			return;
		}
		String contactID = null;
		try{
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
			if (_debug) Log.v("Notification.loadContactsInfoByEmail() Searching contacts");
			while (cursor.moveToNext()) { 
				contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
				String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String photoID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)); 
				final String[] emailProjection = null;
				final String emailSelection = ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactID;
				final String[] emailSelectionArgs = null;
				final String emailSortOrder = null;
                Cursor emailCursor = context.getContentResolver().query(
                		ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
                		emailProjection,
                		emailSelection, 
                        emailSelectionArgs, 
                        emailSortOrder);
                while (emailCursor.moveToNext()) {
                	String contactEmail = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    //String emailType = emailCursor.getString(emailCursor.getColumnIndex(Phone.TYPE));
                    //if (_debug) Log.v("Notification.loadContactsInfoByEmail() Email Address: " + emailIdOfContact + " Email Type: " + emailType);
					if(Common.removeEmailFormatting(incomingEmail).equals(Common.removeEmailFormatting(contactEmail))){
						_contactID = Long.parseLong(contactID);
		    		  	if(contactName != null){
		    		  		_contactName = contactName;
		    		  	}
		    		  	if(photoID != null){
		    			  	_photoID = Long.parseLong(photoID);
		    		  	}
		  	          	Uri uri = ContentUris.withAppendedId(
		  	        		  ContactsContract.Contacts.CONTENT_URI,
		  	        		  Long.parseLong(contactID));
		  		      	InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
		  		      	Bitmap contactPhotoBitmap = BitmapFactory.decodeStream(input);
		  		      	if(contactPhotoBitmap!= null){
		  		    	  	_photoImg = contactPhotoBitmap;
		  		    	  	_contactPhotoExists = true;
		  		      	}
		  		      	_contactExists = true;
		  		      	break;
					}
                    
                }
                emailCursor.close();
                if(_contactExists) break;
		   	}
			cursor.close();
		}catch(Exception ex){
			if (_debug) Log.e("Notification.loadContactsInfo() ERROR: " + ex.toString());
		}
	}
	
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
		String selection = android.provider.CallLog.Calls.NUMBER + " = ? and " + android.provider.CallLog.Calls.DATE + " = ?";
		String[] selectionArgs = new String[] {DatabaseUtils.sqlEscapeString(_sentFromAddress), Long.toString(_timeStamp)};
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
		String selection = android.provider.CallLog.Calls.NUMBER + " = ? and " + android.provider.CallLog.Calls.DATE + " = ?";
		String[] selectionArgs = new String[] {DatabaseUtils.sqlEscapeString(_sentFromAddress), Long.toString(_timeStamp)};
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
	 * Set the Email message as read or unread depending on the input.
	 * 
	 * @param isViewed - Boolean, if true sets the message as viewed.
	 */
	private void setEmailRead(boolean isViewed){
		if (_debug) Log.v("Notification.setEmailRead()");
		try{
			//TODO - Write This Function setEmailRead(boolean isViewed)
//			if(_messageID == 0){
//				if (_debug) Log.v("Notification.setMessageRead() Message ID == 0. Load Message ID");
//				loadMessageID(_context, _threadID, _messageBody, _timeStamp);
//			}
//			ContentValues contentValues = new ContentValues();
//			if(isViewed){
//				contentValues.put("READ", 1);
//			}else{
//				contentValues.put("READ", 0);
//			}
//			String selection = null;
//			String[] selectionArgs = null;
//			_context.getContentResolver().update(
//					Uri.parse("content://sms/" + messageID), 
//		    		contentValues, 
//		    		selection, 
//		    		selectionArgs);
		}catch(Exception ex){
			if (_debug) Log.e("Notification.setEmailRead() ERROR: " + ex.toString());
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
		SimpleDateFormat eventDateFormatted = new SimpleDateFormat();
		eventDateFormatted.setTimeZone(TimeZone.getDefault());
		Date eventEndDate = new Date(eventEndTime);
		Date eventStartDate = new Date(eventStartTime);
		String[] startTimeInfo = eventDateFormatted.format(eventStartDate).split(" ");
		String[] endTimeInfo = eventDateFormatted.format(eventEndDate).split(" ");
    	if(messageBody.equals("")){
    		try{
	    		if(allDay){
	    			formattedMessage = startTimeInfo[0] + " - All Day";
	    		}else{
	    			//Check if the event spans a single day or not.
	    			if(startTimeInfo[0].equals(endTimeInfo[0]) && startTimeInfo.length == 3){
	    				formattedMessage = startTimeInfo[0] + " " + startTimeInfo[1] + " " + startTimeInfo[2] +  " - " +  endTimeInfo[1] + " " + startTimeInfo[2];
	    			}else{
	    				formattedMessage = eventDateFormatted.format(eventStartDate) + " - " +  eventDateFormatted.format(eventEndDate);
	    			}
	    		}
    		}catch(Exception ex){
    			if (_debug) Log.e("Notification.formatCalendarEventMessage() ERROR: " + ex.toString());
    			formattedMessage = eventDateFormatted.format(eventStartDate) + " - " +  eventDateFormatted.format(eventEndDate);
    		}
    	}else{
    		formattedMessage = messageBody;
    	}
    	_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
    	if(_preferences.getBoolean(CALENDAR_LABELS_KEY, true)){
    		formattedMessage = "<b>" + calendarName + "</b><br/>" + formattedMessage;
    	}
		return formattedMessage;
	}	
	
}