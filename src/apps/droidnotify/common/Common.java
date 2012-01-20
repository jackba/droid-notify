package apps.droidnotify.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager.KeyguardLock;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import apps.droidnotify.email.EmailCommon;
import apps.droidnotify.common.Constants;
import apps.droidnotify.NotificationActivity;
import apps.droidnotify.NotificationViewFlipper;
import apps.droidnotify.facebook.FacebookCommon;
import apps.droidnotify.log.Log;
import apps.droidnotify.phone.PhoneCommon;
import apps.droidnotify.receivers.RescheduleReceiver;
import apps.droidnotify.twitter.TwitterCommon;
import apps.droidnotify.R;

/**
 * This class is a collection of methods that are used more than once.
 * If a method is used more than once it is put here and made static so that 
 * it becomes accessible to all classes in the application.
 * 
 * @author Camille Sévigny
 */
public class Common {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private static boolean _debug = false; 
	private static Context _context = null; 
	private static PowerManager.WakeLock _partialWakeLock = null;
	private static PowerManager.WakeLock _wakeLock = null;
	private static KeyguardLock _keyguardLock = null;
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	//================================================================================
	// Debug Methods
	//================================================================================
	
	/**
	 * Read/Display the content provider columns. This outputs to the log file the information.
	 * 
	 * @param context - Application Context.
	 * @param contentProviderURI - The URI we want to read.
	 */
	public static void debugReadContentProviderColumns(Context context, String contentProviderURI) {
	    try{
		    Cursor conversations = context.getContentResolver().query(Uri.parse(contentProviderURI), null, null, null, null);
		    while (conversations.moveToNext()) { 
		    	for(int i=0;i<conversations.getColumnCount();i++){
		    		Log.v("Common.debugReadContentProviderColumns() " + conversations.getColumnName(i) + " = " + conversations.getString(i));
		    	}
		    	break;
		    }
	    }catch(Exception ex){
	    	Log.e("Common.debugReadContentProviderColumns()  ERROR:" + ex.toString());
	    }
	}

	//================================================================================
	// Application Methods
	//================================================================================
	
	/**
	 * Rounds the corners of a Bitmap image.
	 * 
	 * @param bitmap - The Bitmap to be formatted.
	 * @param pixels - The number of pixels as the diameter of the rounded corners.
	 * 
	 * @return Bitmap - The formatted Bitmap image.
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels, boolean resizeImage, int resizeX, int resizeY) {
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getRoundedCornerBitmap()");
		try{
			Bitmap output = null;
			if(bitmap == null){
				return null;
			}else{
		        output = Bitmap.createBitmap(
		        		bitmap.getWidth(), 
		        		bitmap
		                .getHeight(), 
		                Config.ARGB_8888);
		        Canvas canvas = new Canvas(output);
		        final int color = 0xff424242;
		        final Paint paint = new Paint();
		        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		        final RectF rectF = new RectF(rect);
		        final float roundPx = pixels;
		        paint.setAntiAlias(true);
		        canvas.drawARGB(0, 0, 0, 0);
		        paint.setColor(color);
		        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		        canvas.drawBitmap(bitmap, rect, rect, paint);
		        //Resize the Bitmap so that all images are consistent.
		        //Bitmap createScaledBitmap(Bitmap src, int dstWidth, int dstHeight, boolean filter)
		        if(resizeImage){
		        	output = Bitmap.createScaledBitmap(output, resizeX, resizeY, true);
		        }
		        return output;
			}
		}catch(Exception ex){
			Log.e("Common.getRoundedCornerBitmap() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Get various contact info for a given phoneNumber.
	 * 
	 * @param context - Application Context.
	 * @param incomingNumber -  - The phoneNumber to search the contacts by.
	 * 
	 * @return Bundle - Returns a Bundle of the contact information.
	 */ 
	public static Bundle getContactsInfoByPhoneNumber(Context context, String incomingNumber){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getContactsInfoByPhoneNumber()");
		Bundle contactInfoBundle = new Bundle();
		long contactID = 0;
		String contactName = "";
		long photoID = 0;
		String lookupKey = "";
		boolean contactExists = false;
		if (incomingNumber == null) {
			if (_debug) Log.v("Common.getContactsInfoByPhoneNumber() Phone number provided is null. Exiting...");
			return null;
		}
		//Exit if the phone number is an email address.
		if (incomingNumber.contains("@")) {
			if (_debug) Log.v("Common.getContactsInfoByPhoneNumber() Phone number provided appears to be an email address. Exiting...");
			return null;
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
			if (_debug) Log.v("Common.getContactsInfoByPhoneNumber() Searching Contacts...");
			while (cursor.moveToNext()) { 
				String contactIDTmp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
				String contactNameTmp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String photoIDTmp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)); 
				String lookupKeyTmp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)); 
				final String[] phoneProjection = null;
				final String phoneSelection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactIDTmp;
				final String[] phoneSelectionArgs = null;
				final String phoneSortOrder = null;
				Cursor phoneCursor = context.getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
						phoneProjection, 
						phoneSelection, 
						phoneSelectionArgs, 
						phoneSortOrder); 
				while (phoneCursor.moveToNext()) { 
					String contactNumberTmp = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					if(PhoneCommon.isPhoneNumberEqual(contactNumberTmp, incomingNumber)){
						contactID = Long.parseLong(contactIDTmp);
		    		  	contactName = contactNameTmp;
		    		  	if(photoIDTmp != null){
		    			  	photoID = Long.parseLong(photoIDTmp);
		    		  	}
		    		  	lookupKey = lookupKeyTmp;
		  		      	contactExists = true;
		  		      	break;
					}
				}
				phoneCursor.close(); 
				if(contactExists) break;
		   	}
			cursor.close();
			if(contactID != 0) contactInfoBundle.putLong(Constants.BUNDLE_CONTACT_ID, contactID);
			if(contactName != null) contactInfoBundle.putString(Constants.BUNDLE_CONTACT_NAME, contactName);
			if(photoID != 0) contactInfoBundle.putLong(Constants.BUNDLE_PHOTO_ID, photoID);
			if(lookupKey != null) contactInfoBundle.putString(Constants.BUNDLE_LOOKUP_KEY, lookupKey);
			return contactInfoBundle;
		}catch(Exception ex){
			Log.e("Common.getContactsInfoByPhoneNumber() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Get various contact info for a given email.
	 * 
	 * @param context - Application Context.
	 * @param incomingEmail - The email to search the contacts by.
	 * 
	 * @return Bundle - Returns a Bundle of the contact information.
	 */ 
	public static Bundle getContactsInfoByEmail(Context context, String incomingEmail){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getContactsInfoByEmail()");
		Bundle contactInfoBundle = new Bundle();
		long contactID = 0;
		String contactName = "";
		long photoID = 0;
		String lookupKey = "";
		boolean contactExists = false;
		if (incomingEmail == null) {
			if (_debug) Log.v("Common.getContactsInfoByEmail() Email provided is null. Exiting...");
			return null;
		}
		if (!incomingEmail.contains("@")) {
			if (_debug) Log.v("Common.getContactsInfoByEmail() Email provided does not appear to be a valid email address. Exiting...");
			return null;
		}
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
			if (_debug) Log.v("Common.getContactsInfoByEmail() Searching Contacts...");
			while (cursor.moveToNext()) { 
				String contactIDTmp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
				String contactNameTmp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String photoIDTmp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)); 
				String lookupKeyTmp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
				final String[] emailProjection = null;
				final String emailSelection = ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactIDTmp;
				final String[] emailSelectionArgs = null;
				final String emailSortOrder = null;
                Cursor emailCursor = context.getContentResolver().query(
                		ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
                		emailProjection,
                		emailSelection, 
                        emailSelectionArgs, 
                        emailSortOrder);
                while (emailCursor.moveToNext()) {
                	String contactEmailTmp = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                	if(EmailCommon.removeEmailFormatting(incomingEmail).equals(EmailCommon.removeEmailFormatting(contactEmailTmp))){
						contactID = Long.parseLong(contactIDTmp);
		    		  	contactName = contactNameTmp;
		    		  	if(photoIDTmp != null){
		    			  	photoID = Long.parseLong(photoIDTmp);
		    		  	}
		    		  	lookupKey = lookupKeyTmp;
		  		      	contactExists = true;
		  		      	break;
					}
                }
                emailCursor.close();
                if(contactExists) break;
		   	}
			cursor.close();
			if(contactID != 0) contactInfoBundle.putLong(Constants.BUNDLE_CONTACT_ID, contactID);
			if(contactName != null) contactInfoBundle.putString(Constants.BUNDLE_CONTACT_NAME, contactName);
			if(photoID != 0) contactInfoBundle.putLong(Constants.BUNDLE_PHOTO_ID, photoID);
			if(lookupKey != null) contactInfoBundle.putString(Constants.BUNDLE_LOOKUP_KEY, lookupKey);
			return contactInfoBundle;
		}catch(Exception ex){
			Log.e("Common.getContactsInfoByEmail() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Get various contact info for a given name.
	 * 
	 * @param context - Application Context.
	 * @param incomingName - The name to search the contacts by.
	 * 
	 * @return Bundle - Returns a Bundle of the contact information.
	 */ 
	public static Bundle getContactsInfoByName(Context context, String incomingName){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getContactsInfoByName() IncomingName: " + incomingName);
		Bundle contactInfoBundle = new Bundle();
		long contactID = 0;
		String contactName = "";
		long photoID = 0;
		String lookupKey = "";
		if (incomingName == null || incomingName.equals("")) {
			if (_debug) Log.v("Common.getContactsInfoByName() Name provided is null or empty. Exiting...");
			return null;
		}
		try{
			final String[] projection = null;
			final String selection = ContactsContract.Contacts.DISPLAY_NAME + " = " + DatabaseUtils.sqlEscapeString(incomingName);
			final String[] selectionArgs = null;
			final String sortOrder = null;
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					projection, 
					selection, 
					selectionArgs, 
					sortOrder);
			if (_debug) Log.v("Common.getContactsInfoByName() Searching Contacts...");
			while (cursor.moveToNext()){
				contactID = Long.parseLong(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))); 
				contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String photoIDtmp = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
				if(photoIDtmp != null){
					photoID = Long.parseLong(photoIDtmp); 
				}
				lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
				break;
		   	}
			cursor.close();
			if(contactID != 0) contactInfoBundle.putLong(Constants.BUNDLE_CONTACT_ID, contactID);
			if(contactName != null) contactInfoBundle.putString(Constants.BUNDLE_CONTACT_NAME, contactName);
			if(photoID != 0) contactInfoBundle.putLong(Constants.BUNDLE_PHOTO_ID, photoID);
			if(lookupKey != null) contactInfoBundle.putString(Constants.BUNDLE_LOOKUP_KEY, lookupKey);
			return contactInfoBundle;
		}catch(Exception ex){
			Log.e("Common.getContactsInfoByName() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Start the intent to view a contact.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param contactID - The id of the contact we want to view.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startContactViewActivity(Context context, NotificationActivity notificationActivity, long contactID, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startContactViewActivity()");
		try{
			if(contactID == 0){
				Toast.makeText(context, context.getString(R.string.app_android_contact_not_found_error), Toast.LENGTH_LONG).show();
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri viewContactURI = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactID));
		    intent.setData(viewContactURI);	
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		    notificationActivity.startActivityForResult(intent, requestCode);
		    setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			Log.e("Common.startContactViewActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_contacts_app_error), Toast.LENGTH_LONG).show();
			setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
	/**
	 * Start the intent to edit a contact.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param contactID - The id of the contact we want to edit.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startContactEditActivity(Context context, NotificationActivity notificationActivity, long contactID, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startContactEditActivity()");
		try{
			if(contactID == 0){
				Toast.makeText(context, context.getString(R.string.app_android_contact_not_found_error), Toast.LENGTH_LONG).show();
				setInLinkedAppFlag(context, false);
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_EDIT);
			Uri viewContactURI = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactID));
		    intent.setData(viewContactURI);	
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		    notificationActivity.startActivityForResult(intent, requestCode);
		    setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			Log.e("Common.startContactEditActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_contacts_app_error), Toast.LENGTH_LONG).show();
			setInLinkedAppFlag(context, false);
			return false;
		}
	}	
	
	/**
	 * Start the intent to add a contact.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param sentFromAddress - The address (email or phone) of the contact we want to add.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startContactAddActivity(Context context, NotificationActivity notificationActivity, String sentFromAddress, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startContactAddActivity()");
		try{
			Intent intent = new Intent(Intent.ACTION_INSERT);
			intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
			if(sentFromAddress.contains("@")){
				intent.putExtra(ContactsContract.Intents.Insert.EMAIL, sentFromAddress);
			}else{
				intent.putExtra(ContactsContract.Intents.Insert.PHONE, sentFromAddress);
			}
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		    notificationActivity.startActivityForResult(intent, requestCode);
		    setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			Log.e("Common.startContactAddActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_contacts_app_error), Toast.LENGTH_LONG).show();
			setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
	/**
	 * Determine if a notification should be shown or blocked.
	 * 
	 * @param context - Application Context.
	 * @param blockingAppRuningAction - The action to perform based on the user preferences.
	 * 
	 * @return boolean - Returns true if a the notification should be blocked.
	 */
	public static boolean isNotificationBlocked(Context context, String blockingAppRuningAction){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.isNotificationBlocked()");
		boolean blockedFlag = false;
	    boolean blockingAppRunning = Common.isBlockingAppRunning(context);
	    if(blockingAppRunning){
			if(blockingAppRuningAction.equals(Constants.BLOCKING_APP_RUNNING_ACTION_RESCHEDULE)){ 
				blockedFlag = true;
		    }else if(blockingAppRuningAction.equals(Constants.BLOCKING_APP_RUNNING_ACTION_SHOW)){
		    	blockedFlag = false;
		    }else if(blockingAppRuningAction.equals(Constants.BLOCKING_APP_RUNNING_ACTION_IGNORE)){
		    	blockedFlag = true;
		    }
			blockedFlag = false;
	    }else{
	    	blockedFlag = false;
	    }
	    if (_debug) Log.v("Common.isNotificationBlocked() BlockedFlag: " + blockedFlag);
	    return blockedFlag;
	}
	
	/**
	 * Determine if the users phone has a blocked app currently running on the phone.
	 * 
	 * @param context - Application Context.
	 * 
	 * @return boolean - Returns true if a app that is flagged to block is currently running.
	 */
	public static boolean isBlockingAppRunning(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.isBlockingAppRunning()");
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    List <RunningTaskInfo> runningTaskArray = activityManager.getRunningTasks(99999);
	    Iterator <RunningTaskInfo> runningTaskArrayIterator = runningTaskArray.iterator();
	    RunningTaskInfo runningTaskInfo = null;
	    while(runningTaskArrayIterator.hasNext()){
	    	runningTaskInfo = runningTaskArrayIterator.next();
	    	ComponentName runningTaskComponent = runningTaskInfo.baseActivity;
	    	String runningTaskPackageName = runningTaskComponent.getPackageName();
	    	String runningTaskClassName = runningTaskComponent.getClassName();
	        //if (_debug) Log.v("Common.isBlockingAppRunning() runningTaskPackageName: " + runningTaskPackageName + " runningTaskClassName: " + runningTaskClassName);
	        int messagingPackageNamesArraySize = Constants.BLOCKED_PACKAGE_NAMES_ARRAY.length;
	        for(int i = 0; i < messagingPackageNamesArraySize; i++){
	        	String[] blockedInfoArray = Constants.BLOCKED_PACKAGE_NAMES_ARRAY[i].split(",");
		        if(blockedInfoArray[0].equals(runningTaskPackageName)){
		        	if (_debug) Log.v("Common.isBlockingAppRunning() blockedInfoArray[0]: " + blockedInfoArray[0] + " runningTaskPackageName: " + runningTaskPackageName);
		        	if(blockedInfoArray.length > 1){
		        		if (_debug) Log.v("Common.isBlockingAppRunning() blockedInfoArray[1]: " + blockedInfoArray[1] + " runningTaskClassName: " + runningTaskClassName);
		        		if(blockedInfoArray[1].equals(runningTaskClassName)){
		        			return true;
		        		}
		        	}else{
		        		return true;
		        	}
		        }
	        }
	    }
		return false;
	}
	
	/**
	 * Convert a GMT timestamp to the phones local time.
	 * 
	 * @param inputTimestamp - GMT timestamp we want to convert.
	 * 
	 * @return long - The timestamp in the phones local time.
	 */
	public static long convertGMTToLocalTime(Context context, long inputTimeStamp){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.convertGMTToLocalTime()");
		//if (_debug) Log.v("Common.convertGMTToLocalTime() InputTimeStamp: " + inputTimeStamp);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    long offset = TimeZone.getDefault().getOffset(inputTimeStamp);
		long timeStampAdjustment = Long.parseLong(preferences.getString(Constants.SMS_TIMESTAMP_ADJUSTMENT_KEY, "0")) * 60 * 60 * 1000;
	    long outputTimeStamp = inputTimeStamp - offset + timeStampAdjustment;
	    //if (_debug) Log.v("Common.convertGMTToLocalTime() OutputTimeStamp: " + outputTimeStamp);
	    return outputTimeStamp;
	}
	
	/**
	 * Display the status bar notification.
	 * 
	 * @param context - The application context.
	 * @param notificationType - The type of notification we are working with.
	 * @param callStateIdle - The call state of the users phone. True if the users phone is idle (not being used).
	 * @param sentFromContactName - The contact name.
	 * @param sentFromAddress - The sent from address (phone number)
	 * @param message - The message of the notification.
	 * @param calendarEventID - The calendar event id.
	 * @param calendarEventStartTime - The calendar event start time.
	 * @param calendarEventEndTime - The calendar event end time.
	 */
	public static void setStatusBarNotification(Context context, int notificationType, int notificationSubType, boolean callStateIdle, String sentFromContactName, String sentFromAddress, String message, String k9EmailUri){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.setStatusBarNotification()");
		//if (_debug) Log.v("Common.setStatusBarNotification() sentFromContactName: " + sentFromContactName + " sentFromAddress: " + sentFromAddress + " message: " + message);
		try{
			_context = context;
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Stop if app is disabled.
			if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("Common.setStatusBarNotification() App Disabled. Exiting...");
				return;
			}
			//Preference keys.
			String POPUP_ENABLED_KEY = null;
			String ENABLED_KEY = null;
			boolean POPUP_ENABLED_DEFAULT = true;
			String SOUND_SETTING_KEY = null;
			String RINGTONE_DEFAULT = Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_DEFAULT;
			String IN_CALL_SOUND_ENABLED_KEY = null;
			String VIBRATE_SETTING_KEY = null;
			String VIBRATE_ALWAYS_VALUE = Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_ALWAYS_VALUE;
			String VIBRATE_WHEN_VIBRATE_MODE_VALUE = Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_WHEN_VIBRATE_MODE_VALUE;
			String IN_CALL_VIBRATE_ENABLED_KEY = null;
			String VIBRATE_PATTERN_KEY = null;
			String VIBRATE_PATTERN_DEFAULT = Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_DEFAULT;
			String VIBRATE_PATTERN_CUSTOM_VALUE_KEY = null;
			String VIBRATE_PATTERN_CUSTOM_KEY = null;
			String LED_ENABLED_KEY  = null;
			String LED_COLOR_DEFAULT = Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT;
			String LED_COLOR_KEY = null;
			String LED_COLOR_CUSTOM_VALUE_KEY = null;
			String LED_COLOR_CUSTOM_KEY = null;
			String LED_PATTERN_KEY = null;
			String LED_PATTERN_DEFAULT = Constants.STATUS_BAR_NOTIFICATIONS_LED_PATTERN_DEFAULT;
			String LED_PATTERN_CUSTOM_VALUE_KEY = null;
			String LED_PATTERN_CUSTOM_KEY = null;
			String ICON_ID = null;
			String ICON_DEFAULT = null;
			int icon = 0;
			CharSequence tickerText = null;
			CharSequence contentTitle = null;
			CharSequence contentText = null;
			Intent notificationContentIntent = null;
			PendingIntent contentIntent = null;
			Intent notificationDeleteIntent = null;
			PendingIntent deleteIntent = null;
			String sentFrom = null;
			if(message != null && message.length() > 0){
				message = message.replace("<br/><br/>", " ").replace("<br/>", " ")
						.replace("<b>", "").replace("</b>", "")
						.replace("<i>", "").replace("</i>", "")
						.replace("<u>", "").replace("</u>", "");
			}else{
				message = "";
			}
			//Load values into the preference keys based on the notification type.
			switch(notificationType){
				case Constants.NOTIFICATION_TYPE_SMS:{
					if (_debug) Log.v("Common.setStatusBarNotification() NOTIFICATION_TYPE_SMS");
					POPUP_ENABLED_KEY = Constants.SMS_NOTIFICATIONS_ENABLED_KEY;
					POPUP_ENABLED_DEFAULT = true;
					ENABLED_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY;
					SOUND_SETTING_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY;
					IN_CALL_SOUND_ENABLED_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY;
					VIBRATE_SETTING_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY;
					IN_CALL_VIBRATE_ENABLED_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY;
					VIBRATE_PATTERN_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY;
					VIBRATE_PATTERN_CUSTOM_VALUE_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY;
					VIBRATE_PATTERN_CUSTOM_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY;
					LED_ENABLED_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY;
					LED_COLOR_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY;
					LED_COLOR_CUSTOM_VALUE_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY;
					LED_COLOR_CUSTOM_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY;
					LED_PATTERN_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY;
					LED_PATTERN_CUSTOM_VALUE_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY;
					LED_PATTERN_CUSTOM_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY;
					ICON_ID = Constants.SMS_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY;
					ICON_DEFAULT = Constants.SMS_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT;
					contentTitle = context.getText(R.string.status_bar_notification_content_title_text_sms);
					if(sentFromContactName == null || sentFromContactName.equals("")){
						if(sentFromAddress.contains("@")){
							sentFrom = sentFromAddress;
						}else{
							sentFrom = PhoneCommon.formatPhoneNumber(context, sentFromAddress);
						}	
					}else{
						sentFrom = sentFromContactName;
					}
					if( (sentFrom == null || sentFrom.equals("")) && (message == null || message.equals("")) ){
						contentText = context.getString(R.string.status_bar_notification_content_text_sms_null);
						tickerText = context.getString(R.string.status_bar_notification_ticker_text_sms_null);
						//Content Intent
						notificationContentIntent = new Intent(Intent.ACTION_MAIN);
						notificationContentIntent.setType("vnd.android-dir/mms-sms");
						//For now, don't display empty status bar notifications.
						return;
					}else{
						contentText = context.getString(R.string.status_bar_notification_content_text_sms, sentFrom, message);
						if(sentFromContactName == null || sentFromContactName.equals("")){
							tickerText = context.getString(R.string.status_bar_notification_ticker_text_unknown_contact_sms, message);
						}else{
							tickerText = context.getString(R.string.status_bar_notification_ticker_text_sms, sentFromContactName, message);
						}
						//Content Intent
						notificationContentIntent = new Intent(Intent.ACTION_VIEW);
						notificationContentIntent.setData(Uri.parse("smsto:" + sentFromAddress));
					}
					//Delete Intent
					notificationDeleteIntent = null;
					//Content Intent
					contentIntent = PendingIntent.getActivity(context, 0, notificationContentIntent, 0);
					//Delete Intent
					deleteIntent = null;
					break;
				}
				case Constants.NOTIFICATION_TYPE_MMS:{
					if (_debug) Log.v("Common.setStatusBarNotification() NOTIFICATION_TYPE_MMS");
					POPUP_ENABLED_KEY = Constants.MMS_NOTIFICATIONS_ENABLED_KEY;
					POPUP_ENABLED_DEFAULT = true;
					ENABLED_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY;
					SOUND_SETTING_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY;
					IN_CALL_SOUND_ENABLED_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY;
					VIBRATE_SETTING_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY;
					IN_CALL_VIBRATE_ENABLED_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY;
					VIBRATE_PATTERN_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY;
					VIBRATE_PATTERN_CUSTOM_VALUE_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY;
					VIBRATE_PATTERN_CUSTOM_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY;
					LED_ENABLED_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY;
					LED_COLOR_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY;
					LED_COLOR_CUSTOM_VALUE_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY;
					LED_COLOR_CUSTOM_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY;
					LED_PATTERN_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY;
					LED_PATTERN_CUSTOM_VALUE_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY;
					LED_PATTERN_CUSTOM_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY;
					ICON_ID = Constants.MMS_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY;
					ICON_DEFAULT = Constants.MMS_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT;
					contentTitle = context.getText(R.string.status_bar_notification_content_title_text_mms);
					if(sentFromContactName == null || sentFromContactName.equals("")){
						if(sentFromAddress.contains("@")){
							sentFrom = sentFromAddress;
						}else{
							sentFrom = PhoneCommon.formatPhoneNumber(context, sentFromAddress);
						}						
					}else{
						sentFrom = sentFromContactName;
					}
					if( (sentFrom == null || sentFrom.equals("")) && (message == null || message.equals("")) ){
						contentText = context.getString(R.string.status_bar_notification_content_text_mms_null);
						tickerText = context.getString(R.string.status_bar_notification_ticker_text_mms_null);
						//Content Intent
						notificationContentIntent = new Intent(Intent.ACTION_MAIN);
						notificationContentIntent.setType("vnd.android-dir/mms-sms");
						//For now, don't display empty status bar notifications.
						return;
					}else{
						contentText = context.getString(R.string.status_bar_notification_content_text_mms, sentFrom, message);
						if(sentFromContactName == null || sentFromContactName.equals("")){
							tickerText = context.getString(R.string.status_bar_notification_ticker_text_unknown_contact_mms, message);
						}else{
							tickerText = context.getString(R.string.status_bar_notification_ticker_text_mms, sentFromContactName, message);
						}
						//Content Intent
						notificationContentIntent = new Intent(Intent.ACTION_VIEW);
						notificationContentIntent.setData(Uri.parse("smsto:" + sentFromAddress));
					}
					//Delete Intent
					notificationDeleteIntent = null;
					//Content Intent
					contentIntent = PendingIntent.getActivity(context, 0, notificationContentIntent, 0);
					//Delete Intent
					deleteIntent = null;
					break;
				}
				case Constants.NOTIFICATION_TYPE_PHONE:{
					if (_debug) Log.v("Common.setStatusBarNotification() NOTIFICATION_TYPE_PHONE");
					POPUP_ENABLED_KEY = Constants.PHONE_NOTIFICATIONS_ENABLED_KEY;
					POPUP_ENABLED_DEFAULT = true;
					ENABLED_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY;
					SOUND_SETTING_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY;
					IN_CALL_SOUND_ENABLED_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY;
					VIBRATE_SETTING_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY;
					IN_CALL_VIBRATE_ENABLED_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY;
					VIBRATE_PATTERN_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY;
					VIBRATE_PATTERN_CUSTOM_VALUE_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY;
					VIBRATE_PATTERN_CUSTOM_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY;
					LED_ENABLED_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY;
					LED_COLOR_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY;
					LED_COLOR_CUSTOM_VALUE_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY;
					LED_COLOR_CUSTOM_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY;
					LED_PATTERN_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY;
					LED_PATTERN_CUSTOM_VALUE_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY;
					LED_PATTERN_CUSTOM_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY;
					ICON_ID = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY;
					ICON_DEFAULT = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT;
					contentTitle = context.getText(R.string.status_bar_notification_content_title_text_phone);
					if(sentFromContactName == null || sentFromContactName.equals("")){
						sentFrom = PhoneCommon.formatPhoneNumber(context, sentFromAddress);
					}else{
						sentFrom = sentFromContactName;
					}
					if(sentFrom == null || sentFrom.equals("")){
						contentText = context.getString(R.string.status_bar_notification_content_text_phone_null);
						tickerText = context.getString(R.string.status_bar_notification_ticker_text_phone_null);
						//For now, don't display empty status bar notifications.
						return;
					}else{
						contentText = context.getString(R.string.status_bar_notification_content_text_phone, sentFrom);
						tickerText = context.getString(R.string.status_bar_notification_ticker_text_phone, sentFrom);
					}
					//Content Intent
					notificationContentIntent =  new Intent(Intent.ACTION_VIEW);
					notificationContentIntent.setType("vnd.android.cursor.dir/calls");
					//Delete Intent
					notificationDeleteIntent = null;
					//Content Intent
					contentIntent = PendingIntent.getActivity(context, 0, notificationContentIntent, 0);
					//Delete Intent
					deleteIntent = null;
					break;
				}
				case Constants.NOTIFICATION_TYPE_CALENDAR:{
					if (_debug) Log.v("Common.setStatusBarNotification() NOTIFICATION_TYPE_CALENDAR");
					POPUP_ENABLED_KEY = Constants.CALENDAR_NOTIFICATIONS_ENABLED_KEY;
					POPUP_ENABLED_DEFAULT = true;
					ENABLED_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY;
					SOUND_SETTING_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY;
					IN_CALL_SOUND_ENABLED_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY;
					VIBRATE_SETTING_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY;
					IN_CALL_VIBRATE_ENABLED_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY;
					VIBRATE_PATTERN_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY;
					VIBRATE_PATTERN_CUSTOM_VALUE_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY;
					VIBRATE_PATTERN_CUSTOM_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY;
					LED_ENABLED_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY;
					LED_COLOR_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY;
					LED_COLOR_CUSTOM_VALUE_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY;
					LED_COLOR_CUSTOM_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY;
					LED_PATTERN_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY;
					LED_PATTERN_CUSTOM_VALUE_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY;
					LED_PATTERN_CUSTOM_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY;
					ICON_ID = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY;
					ICON_DEFAULT = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT;
					contentTitle = context.getText(R.string.status_bar_notification_content_title_text_calendar);
					if(message == null || message.equals("")){
						contentText = context.getString(R.string.status_bar_notification_content_text_calendar_null);
						tickerText = context.getString(R.string.status_bar_notification_ticker_text_calendar_null);
						//For now, don't display empty status bar notifications.
						return;
					}else{
						contentText = context.getString(R.string.status_bar_notification_content_text_calendar, message);
						tickerText = context.getString(R.string.status_bar_notification_ticker_text_calendar, message);
					}
					//Content Intent
					notificationContentIntent = new Intent(Intent.ACTION_VIEW);
					notificationContentIntent.setClassName("com.android.calendar", "com.android.calendar.LaunchActivity");
					//Delete Intent
					notificationDeleteIntent = null;
					//Content Intent
					contentIntent = PendingIntent.getActivity(context, 0, notificationContentIntent, 0);
					//Delete Intent
					deleteIntent = null;
					break;
				}
				case Constants.NOTIFICATION_TYPE_GMAIL:{
					if (_debug) Log.v("Common.setStatusBarNotification() NOTIFICATION_TYPE_GMAIL");
		
					break;
				}
				case Constants.NOTIFICATION_TYPE_TWITTER:{
					if (_debug) Log.v("Common.setStatusBarNotification() NOTIFICATION_TYPE_TWITTER");
					POPUP_ENABLED_KEY = Constants.TWITTER_NOTIFICATIONS_ENABLED_KEY;
					POPUP_ENABLED_DEFAULT = false;
					ENABLED_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY;
					SOUND_SETTING_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY;
					IN_CALL_SOUND_ENABLED_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY;
					VIBRATE_SETTING_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY;
					IN_CALL_VIBRATE_ENABLED_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY;
					VIBRATE_PATTERN_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY;
					VIBRATE_PATTERN_CUSTOM_VALUE_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY;
					VIBRATE_PATTERN_CUSTOM_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY;
					LED_ENABLED_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY;
					LED_COLOR_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY;
					LED_COLOR_CUSTOM_VALUE_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY;
					LED_COLOR_CUSTOM_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY;
					LED_PATTERN_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY;
					LED_PATTERN_CUSTOM_VALUE_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY;
					LED_PATTERN_CUSTOM_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY;
					ICON_ID = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY;
					ICON_DEFAULT = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT;
					if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE){
						contentTitle = context.getText(R.string.status_bar_notification_content_title_text_twitter_direct_message);
					}else if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_MENTION){
						contentTitle = context.getText(R.string.status_bar_notification_content_title_text_twitter_mention);
					}else if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_FOLLOWER_REQUEST){
						contentTitle = context.getText(R.string.status_bar_notification_content_title_text_twitter_follower_request);
					}
					if(sentFromContactName == null || sentFromContactName.equals("")){
						sentFrom = sentFromAddress;
					}else{
						sentFrom = sentFromContactName;
					}
					if( (sentFrom == null || sentFrom.equals("")) && (message == null || message.equals("")) ){
						if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE){
							contentText = context.getString(R.string.status_bar_notification_content_text_twitter_direct_message_null);
							tickerText = context.getString(R.string.status_bar_notification_ticker_text_twitter_direct_message_null);
						}else if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_MENTION){
							contentText = context.getString(R.string.status_bar_notification_content_text_twitter_mention_null);
							tickerText = context.getString(R.string.status_bar_notification_ticker_text_twitter_mention_null);
						}else if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_FOLLOWER_REQUEST){
							contentText = context.getString(R.string.status_bar_notification_content_text_twitter_follower_request_null);
							tickerText = context.getString(R.string.status_bar_notification_ticker_text_twitter_follower_request_null);
						}
						//Content Intent
						notificationContentIntent = null;
						//For now, don't display empty status bar notifications.
						return;
					}else{
						if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE){
							contentText = context.getString(R.string.status_bar_notification_content_text_twitter_direct_message, sentFrom, message);
							if(sentFromContactName == null || sentFromContactName.equals("")){
								tickerText = context.getString(R.string.status_bar_notification_ticker_text_unknown_contact_twitter_direct_message, message);
							}else{
								tickerText = context.getString(R.string.status_bar_notification_ticker_text_twitter_direct_message, sentFromContactName, message);
							}
						}else if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_MENTION){
							contentText = context.getString(R.string.status_bar_notification_content_text_twitter_mention, sentFrom, message);
							if(sentFromContactName == null || sentFromContactName.equals("")){
								tickerText = context.getString(R.string.status_bar_notification_ticker_text_unknown_contact_twitter_mention, message);
							}else{
								tickerText = context.getString(R.string.status_bar_notification_ticker_text_twitter_mention, sentFromContactName, message);
							}
						}else if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_FOLLOWER_REQUEST){
							contentText = context.getString(R.string.status_bar_notification_content_text_twitter_follower_request, sentFrom, message);
							if(sentFromContactName == null || sentFromContactName.equals("")){
								tickerText = context.getString(R.string.status_bar_notification_ticker_text_unknown_contact_twitter_follower_request, message);
							}else{
								tickerText = context.getString(R.string.status_bar_notification_ticker_text_twitter_follower_request, sentFromContactName, message);
							}
						}
						notificationContentIntent = TwitterCommon.getTwitterAppActivityIntent(context);
					}
					//Delete Intent
					notificationDeleteIntent = null;
					//Content Intent
					contentIntent = PendingIntent.getActivity(context, 0, notificationContentIntent, 0);
					//Delete Intent
					deleteIntent = null;
					break;
				}
				case Constants.NOTIFICATION_TYPE_FACEBOOK:{
					if (_debug) Log.v("Common.setStatusBarNotification() NOTIFICATION_TYPE_FACEBOOK");
					POPUP_ENABLED_KEY = Constants.FACEBOOK_NOTIFICATIONS_ENABLED_KEY;
					POPUP_ENABLED_DEFAULT = false;
					ENABLED_KEY = Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY;
					SOUND_SETTING_KEY = Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY;
					IN_CALL_SOUND_ENABLED_KEY = Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY;
					VIBRATE_SETTING_KEY = Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY;
					IN_CALL_VIBRATE_ENABLED_KEY = Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY;
					VIBRATE_PATTERN_KEY = Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY;
					VIBRATE_PATTERN_CUSTOM_VALUE_KEY = Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY;
					VIBRATE_PATTERN_CUSTOM_KEY = Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY;
					LED_ENABLED_KEY = Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY;
					LED_COLOR_KEY = Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY;
					LED_COLOR_CUSTOM_VALUE_KEY = Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY;
					LED_COLOR_CUSTOM_KEY = Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY;
					LED_PATTERN_KEY = Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY;
					LED_PATTERN_CUSTOM_VALUE_KEY = Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY;
					LED_PATTERN_CUSTOM_KEY = Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY;
					ICON_ID = Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY;
					ICON_DEFAULT = Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT;
					if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION){
						contentTitle = context.getText(R.string.status_bar_notification_content_title_text_facebook_notification);
					}else if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_FRIEND_REQUEST){
						contentTitle = context.getText(R.string.status_bar_notification_content_title_text_facebook_friend_request);
					}else if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_MESSAGE){
						contentTitle = context.getText(R.string.status_bar_notification_content_title_text_facebook_message);
					}
					if(sentFromContactName == null || sentFromContactName.equals("")){
						sentFrom = sentFromAddress;
					}else{
						sentFrom = sentFromContactName;
					}
					if( (sentFrom == null || sentFrom.equals("")) && (message == null || message.equals("")) ){
						if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION){
							contentText = context.getString(R.string.status_bar_notification_content_text_facebook_notification_null);
							tickerText = context.getString(R.string.status_bar_notification_ticker_text_facebook_notification_null);
						}else if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_FRIEND_REQUEST){
							contentText = context.getString(R.string.status_bar_notification_content_text_facebook_friend_request_null);
							tickerText = context.getString(R.string.status_bar_notification_ticker_text_facebook_friend_request_null);
						}else if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_MESSAGE){
							contentText = context.getString(R.string.status_bar_notification_content_text_facebook_message_null);
							tickerText = context.getString(R.string.status_bar_notification_ticker_text_facebook_message_null);
						}
						//Content Intent
						notificationContentIntent = null;
						//For now, don't display empty status bar notifications.
						return;
					}else{
						if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION){
							contentText = context.getString(R.string.status_bar_notification_content_text_facebook_notification, sentFrom, message);
							if(sentFromContactName == null || sentFromContactName.equals("")){
								tickerText = context.getString(R.string.status_bar_notification_ticker_text_unknown_contact_facebook_notification, message);
							}else{
								tickerText = context.getString(R.string.status_bar_notification_ticker_text_facebook_notification, sentFromContactName, message);
							}
						}else if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_FRIEND_REQUEST){
							contentText = context.getString(R.string.status_bar_notification_content_text_facebook_friend_request, sentFrom, message);
							if(sentFromContactName == null || sentFromContactName.equals("")){
								tickerText = context.getString(R.string.status_bar_notification_ticker_text_unknown_contact_facebook_friend_request, message);
							}else{
								tickerText = context.getString(R.string.status_bar_notification_ticker_text_facebook_friend_request, sentFromContactName, message);
							}
						}else if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_MESSAGE){
							contentText = context.getString(R.string.status_bar_notification_content_text_facebook_message, sentFrom, message);
							if(sentFromContactName == null || sentFromContactName.equals("")){
								tickerText = context.getString(R.string.status_bar_notification_ticker_text_unknown_contact_facebook_message, message);
							}else{
								tickerText = context.getString(R.string.status_bar_notification_ticker_text_facebook_message, sentFromContactName, message);
							}
						}
						notificationContentIntent = FacebookCommon.getFacebookAppActivityIntent(context);
					}
					//Delete Intent
					notificationDeleteIntent = null;
					//Content Intent
					contentIntent = PendingIntent.getActivity(context, 0, notificationContentIntent, 0);
					//Delete Intent
					deleteIntent = null;
					break;
				}
				case Constants.NOTIFICATION_TYPE_K9:{
					if (_debug) Log.v("Common.setStatusBarNotification() NOTIFICATION_TYPE_K9");
					POPUP_ENABLED_KEY = Constants.K9_NOTIFICATIONS_ENABLED_KEY;
					POPUP_ENABLED_DEFAULT = true;
					ENABLED_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY;
					SOUND_SETTING_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY;
					IN_CALL_SOUND_ENABLED_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY;
					VIBRATE_SETTING_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY;
					IN_CALL_VIBRATE_ENABLED_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY;
					VIBRATE_PATTERN_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY;
					VIBRATE_PATTERN_CUSTOM_VALUE_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY;
					VIBRATE_PATTERN_CUSTOM_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY;
					LED_ENABLED_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY;
					LED_COLOR_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY;
					LED_COLOR_CUSTOM_VALUE_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY;
					LED_COLOR_CUSTOM_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY;
					LED_PATTERN_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY;
					LED_PATTERN_CUSTOM_VALUE_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY;
					LED_PATTERN_CUSTOM_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY;
					ICON_ID = Constants.K9_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY;
					ICON_DEFAULT = Constants.K9_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT;
					contentTitle = context.getText(R.string.status_bar_notification_content_title_text_email);
					if(sentFromContactName == null || sentFromContactName.equals("")){
						sentFrom = sentFromAddress;
					}else{
						sentFrom = sentFromContactName;
					}
					if( (sentFrom == null || sentFrom.equals("")) && (message == null || message.equals("")) ){
						contentText = context.getString(R.string.status_bar_notification_content_text_email_null);
						tickerText = context.getString(R.string.status_bar_notification_ticker_text_email_null);
						//Content Intent
						notificationContentIntent = null;
						//For now, don't display empty status bar notifications.
						return;
					}else{
						contentText = context.getString(R.string.status_bar_notification_content_text_email, sentFrom, message);
						if(sentFromContactName == null || sentFromContactName.equals("")){
							tickerText = context.getString(R.string.status_bar_notification_ticker_text_unknown_contact_email, message);
						}else{
							tickerText = context.getString(R.string.status_bar_notification_ticker_text_email, sentFromContactName, message);
						}
						//Content Intent
						if(k9EmailUri!= null){
							notificationContentIntent = new Intent(Intent.ACTION_VIEW);
							notificationContentIntent.setData(Uri.parse(k9EmailUri));
						}else{
							notificationContentIntent = new Intent(Intent.ACTION_MAIN);
							notificationContentIntent.addCategory(Intent.CATEGORY_LAUNCHER);
							notificationContentIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);	   
					        String packageName = "com.fsck.k9";
							if(notificationSubType == Constants.NOTIFICATION_TYPE_KAITEN_MAIL){
								packageName = "com.kaitenmail";
							}else if(notificationSubType == Constants.NOTIFICATION_TYPE_K9_MAIL){
								packageName = "com.fsck.k9";
							}
							notificationContentIntent.setComponent(new ComponentName(packageName, packageName + ".activity.Accounts"));  
						}
					}
					//Delete Intent
					notificationDeleteIntent = null;
					//Content Intent
					contentIntent = PendingIntent.getActivity(context, 0, notificationContentIntent, 0);
					//Delete Intent
					deleteIntent = null;
					break;
				}
			}
			//Notification properties
			Vibrator vibrator = null;
			AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			boolean inNormalMode = audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
			boolean inVibrateMode = audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE;
			String notificationSound = null;
			boolean soundEnabled = false;
			boolean soundInCallEnabled = false;
			String notificationVibrate = null;
			boolean vibrateEnabled = false;
			boolean vibrateInCallEnabled = false;
			//Check if notifications are enabled or not.
			if(!preferences.getBoolean(ENABLED_KEY, true) || !preferences.getBoolean(POPUP_ENABLED_KEY, POPUP_ENABLED_DEFAULT)){
				if (_debug) Log.v("Common.setStatusBarNotification() Notifications Disabled: ENABLED_KEY " + ENABLED_KEY + " - Exiting...");
				return;
			}
			//Sound preferences
			notificationSound = preferences.getString(SOUND_SETTING_KEY, RINGTONE_DEFAULT);
			if(notificationSound != null && !notificationSound.equals("")){
				soundEnabled = true;
			}
			soundInCallEnabled = preferences.getBoolean(IN_CALL_SOUND_ENABLED_KEY, false);
			//Vibrate preferences
			notificationVibrate = preferences.getString(VIBRATE_SETTING_KEY, VIBRATE_ALWAYS_VALUE);
			if(notificationVibrate.equals(VIBRATE_ALWAYS_VALUE)){
				vibrateEnabled = true;
			}else if(notificationVibrate.equals(VIBRATE_WHEN_VIBRATE_MODE_VALUE) && inVibrateMode){
				vibrateEnabled = true;
			}
			vibrateInCallEnabled = preferences.getBoolean(IN_CALL_VIBRATE_ENABLED_KEY, true);
			String vibratePattern = null;
			if(vibrateEnabled){
				vibratePattern = preferences.getString(VIBRATE_PATTERN_KEY, VIBRATE_PATTERN_DEFAULT);
				if(vibratePattern.equals(VIBRATE_PATTERN_CUSTOM_VALUE_KEY)){
					vibratePattern = preferences.getString(VIBRATE_PATTERN_CUSTOM_KEY, VIBRATE_PATTERN_DEFAULT);
				}
			}	
			//LED preferences
			boolean ledEnabled = preferences.getBoolean(LED_ENABLED_KEY, true);
			String ledPattern = null;
			int ledColor = Color.parseColor(LED_COLOR_DEFAULT);
			String ledColorString = null;
			if(ledEnabled){
				//LED Color
				ledColorString = preferences.getString(LED_COLOR_KEY, LED_COLOR_DEFAULT);
				if(ledColorString.equals(LED_COLOR_CUSTOM_VALUE_KEY)){
					ledColorString = preferences.getString(LED_COLOR_CUSTOM_KEY, LED_COLOR_DEFAULT);
				}
				try{
					ledColor = Color.parseColor(ledColorString);
				}catch(Exception ex){
					//Do Nothing
				}
				//LED Pattern
				ledPattern = preferences.getString(LED_PATTERN_KEY, LED_PATTERN_DEFAULT);
				if(ledPattern.equals(LED_PATTERN_CUSTOM_VALUE_KEY)){
					ledPattern = preferences.getString(LED_PATTERN_CUSTOM_KEY, LED_PATTERN_DEFAULT);
				}
			}
			//Set Notification Icon
			switch(notificationType){
				case Constants.NOTIFICATION_TYPE_SMS:{
					if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_blue")){
						icon = R.drawable.status_bar_notification_sms_blue;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_green")){
						icon = R.drawable.status_bar_notification_sms_green;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_grey")){
						icon = R.drawable.status_bar_notification_sms_grey;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_orange")){
						icon = R.drawable.status_bar_notification_sms_orange;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_pink")){
						icon = R.drawable.status_bar_notification_sms_pink;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_purple")){
						icon = R.drawable.status_bar_notification_sms_purple;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_red")){
						icon = R.drawable.status_bar_notification_sms_red;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_white")){
							icon = R.drawable.status_bar_notification_sms_white;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_aqua")){
						icon = R.drawable.status_bar_notification_sms_postcard_aqua;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_blue")){
						icon = R.drawable.status_bar_notification_sms_postcard_blue;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_green")){
						icon = R.drawable.status_bar_notification_sms_postcard_green;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_grey")){
						icon = R.drawable.status_bar_notification_sms_postcard_grey;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_orange")){
						icon = R.drawable.status_bar_notification_sms_postcard_orange;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_pink")){
						icon = R.drawable.status_bar_notification_sms_postcard_pink;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_purple")){
						icon = R.drawable.status_bar_notification_sms_postcard_purple;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_red")){
						icon = R.drawable.status_bar_notification_sms_postcard_red;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_white")){
						icon = R.drawable.status_bar_notification_sms_postcard_white;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_yellow")){
						icon = R.drawable.status_bar_notification_sms_postcard_yellow;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_glass_aqua")){
						icon = R.drawable.status_bar_notification_sms_postcard_glass_aqua;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_glass_blue")){
						icon = R.drawable.status_bar_notification_sms_postcard_glass_blue;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_glass_green")){
						icon = R.drawable.status_bar_notification_sms_postcard_glass_green;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_glass_orange")){
						icon = R.drawable.status_bar_notification_sms_postcard_glass_orange;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_glass_pink")){
						icon = R.drawable.status_bar_notification_sms_postcard_glass_pink;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_glass_purple")){
						icon = R.drawable.status_bar_notification_sms_postcard_glass_purple;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_glass_red")){
						icon = R.drawable.status_bar_notification_sms_postcard_glass_red;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_glass_white")){
						icon = R.drawable.status_bar_notification_sms_postcard_glass_white;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_glass_yellow")){
						icon = R.drawable.status_bar_notification_sms_postcard_glass_yellow;
					}else{
						//Default Value
						icon = R.drawable.status_bar_notification_sms_green;
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_MMS:{
					if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_blue")){
						icon = R.drawable.status_bar_notification_sms_blue;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_green")){
						icon = R.drawable.status_bar_notification_sms_green;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_grey")){
						icon = R.drawable.status_bar_notification_sms_grey;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_orange")){
						icon = R.drawable.status_bar_notification_sms_orange;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_pink")){
						icon = R.drawable.status_bar_notification_sms_pink;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_purple")){
						icon = R.drawable.status_bar_notification_sms_purple;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_red")){
						icon = R.drawable.status_bar_notification_sms_red;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_white")){
							icon = R.drawable.status_bar_notification_sms_white;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_aqua")){
						icon = R.drawable.status_bar_notification_sms_postcard_aqua;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_blue")){
						icon = R.drawable.status_bar_notification_sms_postcard_blue;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_green")){
						icon = R.drawable.status_bar_notification_sms_postcard_green;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_grey")){
						icon = R.drawable.status_bar_notification_sms_postcard_grey;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_orange")){
						icon = R.drawable.status_bar_notification_sms_postcard_orange;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_pink")){
						icon = R.drawable.status_bar_notification_sms_postcard_pink;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_purple")){
						icon = R.drawable.status_bar_notification_sms_postcard_purple;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_red")){
						icon = R.drawable.status_bar_notification_sms_postcard_red;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_white")){
						icon = R.drawable.status_bar_notification_sms_postcard_white;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_yellow")){
						icon = R.drawable.status_bar_notification_sms_postcard_yellow;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_glass_aqua")){
						icon = R.drawable.status_bar_notification_sms_postcard_glass_aqua;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_glass_blue")){
						icon = R.drawable.status_bar_notification_sms_postcard_glass_blue;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_glass_green")){
						icon = R.drawable.status_bar_notification_sms_postcard_glass_green;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_glass_orange")){
						icon = R.drawable.status_bar_notification_sms_postcard_glass_orange;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_glass_pink")){
						icon = R.drawable.status_bar_notification_sms_postcard_glass_pink;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_glass_purple")){
						icon = R.drawable.status_bar_notification_sms_postcard_glass_purple;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_glass_red")){
						icon = R.drawable.status_bar_notification_sms_postcard_glass_red;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_glass_white")){
						icon = R.drawable.status_bar_notification_sms_postcard_glass_white;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_glass_yellow")){
						icon = R.drawable.status_bar_notification_sms_postcard_glass_yellow;
					}else{
						//Default Value
						icon = R.drawable.status_bar_notification_sms_green;
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_PHONE:{
					if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_missed_call_black")){
						icon = R.drawable.status_bar_notification_missed_call_black;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_missed_call_grey")){
						icon = R.drawable.status_bar_notification_missed_call_grey;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_missed_call_red")){
						icon = R.drawable.status_bar_notification_missed_call_red;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_missed_call_white")){
						icon = R.drawable.status_bar_notification_missed_call_white;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_missed_call_glass_red")){
						icon = R.drawable.status_bar_notification_missed_call_glass_red;
					}else{
						//Default Value
						icon = R.drawable.status_bar_notification_missed_call_black;
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_CALENDAR:{
					if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_calendar_black")){
						icon = R.drawable.status_bar_notification_calendar_black;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_calendar_blue")){
						icon = R.drawable.status_bar_notification_calendar_blue;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_calendar_green")){
						icon = R.drawable.status_bar_notification_calendar_green;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_calendar_grey")){
						icon = R.drawable.status_bar_notification_calendar_grey;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_calendar_orange")){
						icon = R.drawable.status_bar_notification_calendar_orange;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_calendar_red")){
						icon = R.drawable.status_bar_notification_calendar_red;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_calendar_glass_blue")){
						icon = R.drawable.status_bar_notification_calendar_glass_blue;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_calendar_glass_green")){
						icon = R.drawable.status_bar_notification_calendar_glass_green;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_calendar_glass_grey")){
						icon = R.drawable.status_bar_notification_calendar_glass_grey;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_calendar_glass_orange")){
						icon = R.drawable.status_bar_notification_calendar_glass_orange;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_calendar_glass_red")){
						icon = R.drawable.status_bar_notification_calendar_glass_red;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_calendar_glass_yellow")){
						icon = R.drawable.status_bar_notification_calendar_glass_yellow;
					}else{
						//Default Value
						icon = R.drawable.status_bar_notification_calendar_blue;
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_GMAIL:{
		
					break;
				}
				case Constants.NOTIFICATION_TYPE_TWITTER:{
					if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_twitter_bird_blue")){
						icon = R.drawable.status_bar_notification_twitter_bird_blue;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_twitter_blue")){
						icon = R.drawable.status_bar_notification_twitter_blue;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_twitter_grey")){
						icon = R.drawable.status_bar_notification_twitter_grey;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_twitter_t_blue")){
						icon = R.drawable.status_bar_notification_twitter_t_blue;
					}else{
						//Default Value
						icon = R.drawable.status_bar_notification_twitter_blue;
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_FACEBOOK:{
					if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_facebook_grey")){
						icon = R.drawable.status_bar_notification_facebook_grey;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_facebook_blue")){
						icon = R.drawable.status_bar_notification_facebook_blue;
					}else{
						//Default Value
						icon = R.drawable.status_bar_notification_facebook_blue;
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_K9:{
					if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_black")){
						icon = R.drawable.status_bar_notification_email_black;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_blue")){
						icon = R.drawable.status_bar_notification_email_blue;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_green")){
						icon = R.drawable.status_bar_notification_email_green;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_grey")){
						icon = R.drawable.status_bar_notification_email_grey;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_orange")){
						icon = R.drawable.status_bar_notification_email_orange;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_red")){
						icon = R.drawable.status_bar_notification_email_red;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_white")){
						icon = R.drawable.status_bar_notification_email_white;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_glass_black")){
						icon = R.drawable.status_bar_notification_email_glass_black;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_glass_blue")){
						icon = R.drawable.status_bar_notification_email_glass_blue;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_glass_green")){
						icon = R.drawable.status_bar_notification_email_glass_green;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_glass_orange")){
						icon = R.drawable.status_bar_notification_email_glass_orange;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_glass_red")){
						icon = R.drawable.status_bar_notification_email_glass_red;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_glass_white")){
						icon = R.drawable.status_bar_notification_email_glass_white;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_glass_yellow")){
						icon = R.drawable.status_bar_notification_email_glass_yellow;
					}else{
						//Default Value
						icon = R.drawable.status_bar_notification_email_white;
					}
					break;
				}
			}
			//Setup the notification
			Notification notification = new Notification(icon, tickerText, System.currentTimeMillis());
			//Set notification flags
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			//Setup the notification vibration
			if(vibrateEnabled && callStateIdle){
				long[] vibrationPattern = parseVibratePattern(vibratePattern);
				if(vibrationPattern == null){
					notification.defaults |= Notification.DEFAULT_VIBRATE;
				}else{
					notification.vibrate = vibrationPattern;
				}
			}else if(vibrateEnabled && !callStateIdle && vibrateInCallEnabled && (inVibrateMode || inNormalMode)){
				long[] vibrationPattern = parseVibratePattern(vibratePattern);
				if(vibrationPattern == null){
					//Do Nothing
				}else{
					try{
						vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
						vibrator.vibrate(vibrationPattern, -1);
					}catch(Exception ex){
						Log.e("Common.setStatusBarNotification() Notification Vibrator ERROR: " + ex.toString());
					}
				}
			}
			//Setup the notification sound
			notification.audioStreamType = Notification.STREAM_DEFAULT;
			if(soundEnabled && callStateIdle){
				try{
					notification.sound = Uri.parse(notificationSound);
				}catch(Exception ex){
					Log.e("Common.setStatusBarNotification() Notification Sound Set ERROR: " + ex.toString());
					notification.defaults |= Notification.DEFAULT_SOUND;
				}
			}else if(soundEnabled && !callStateIdle && soundInCallEnabled && inNormalMode){
				try{
					new playNotificationMediaFileAsyncTask().execute(notificationSound);
				}catch(Exception ex){
					Log.e("Common.setStatusBarNotification() Notification Sound Play ERROR: " + ex.toString());
				}
				
			}
			//Setup the notification LED lights
			if(ledEnabled){
				notification.flags |= Notification.FLAG_SHOW_LIGHTS;
				try{
					int[] ledPatternArray = parseLEDPattern(ledPattern);
					if(ledPatternArray == null){
						notification.defaults |= Notification.DEFAULT_LIGHTS;
					}else{
						//LED Color
				        notification.ledARGB = ledColor;
						//LED Pattern
						notification.ledOnMS = ledPatternArray[0];
				        notification.ledOffMS = ledPatternArray[1];
					}
				}catch(Exception ex){
					notification.defaults |= Notification.DEFAULT_LIGHTS;
				}
			}
			//Set notification intent values
			notification.deleteIntent = deleteIntent;
			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(notificationType, notification);
			//Remove the stock status bar notification.
			if(notificationType == Constants.NOTIFICATION_TYPE_PHONE){
				PhoneCommon.clearStockMissedCallNotification(context);
			}
		}catch(Exception ex){
			Log.e("Common.setStatusBarNotification() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Clear the status bar notification if there are no more notifications of this type displayed.
	 * 
	 * @param context - The application context.
	 * @param notificationViewFlipper - The notification ViewFlipper.
	 * @param notificationType - The notification type.
	 * @param totalNotifications - The total number of current notifications.
	 */
	public static void clearNotification(Context context, NotificationViewFlipper notificationViewFlipper, int notificationType, int totalNotifications){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.clearNotification()");
		try{
			if(totalNotifications > 0){
				if(!notificationViewFlipper.containsNotificationType(notificationType)){
					removeStatusBarNotification(context, notificationType);
				}
			}else{
				removeStatusBarNotification(context, notificationType);
			}
		}catch(Exception ex){
			Log.e("Common.clearNotification() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Clear all status bar notifications.
	 * 
	 * @param context - The application context.
	 */
	public static void clearAllNotifications(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.clearAllNotifications()");
		try{
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancelAll();
		}catch(Exception ex){
			Log.e("Common.clearAllNotifications() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Aquire a global partial wakelock within this context.
	 * 
	 * @param context - The application context.
	 */
	public static void acquirePartialWakeLock(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.aquirePartialWakelock()");
		try{
			if(_partialWakeLock == null){
		    	PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		    	_partialWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Constants.DROID_NOTIFY_WAKELOCK);
		    	_partialWakeLock.setReferenceCounted(false);
			}
			_partialWakeLock.acquire();
		}catch(Exception ex){
			Log.e("Common.aquirePartialWakelock() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Release the global partial wakelock within this context.
	 * 
	 * @param context - The application context.
	 */
	public static void clearPartialWakeLock(){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.clearPartialWakelock()");
		try{
	    	if(_partialWakeLock != null){
	    		_partialWakeLock.release();
	    	}
		}catch(Exception ex){
			Log.e("Common.clearPartialWakelock() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Function that acquires the WakeLock for this Activity.
	 * The type flags for the WakeLock will be determined by the user preferences. 
	 * 
	 * @param context - The application context.
	 */
	public static void acquireWakeLock(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.aquireWakelock()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			if(_wakeLock == null){
				PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
				if(preferences.getBoolean(Constants.SCREEN_ENABLED_KEY, true)){
					if(preferences.getBoolean(Constants.SCREEN_DIM_ENABLED_KEY, true)){
						_wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, Constants.DROID_NOTIFY_WAKELOCK);
					}else{
						_wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, Constants.DROID_NOTIFY_WAKELOCK);
					}
				}else{
					_wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Constants.DROID_NOTIFY_WAKELOCK);
				}
				_wakeLock.setReferenceCounted(false);
			}
			if(_wakeLock != null){
				_wakeLock.acquire();
			}
			Common.clearPartialWakeLock();
		}catch(Exception ex){
			Log.e("Common.aquireWakelock() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Release the global wakelock within this context.
	 * 
	 * @param context - The application context.
	 */
	public static void clearWakeLock(){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.clearWakelock()");
		try{
			Common.clearPartialWakeLock();
	    	if(_wakeLock != null){
	    		_wakeLock.release();
	    	}
		}catch(Exception ex){
			Log.e("Common.clearWakelock() ERROR: " + ex.toString());
		}
	}

	/**
	 * Function that disables the Keyguard for this Activity.
	 * The removal of the Keyguard will be determined by the user preferences. 
	 * 
	 * @param context - The current context of this Activity.
	 */
	public static void acquireKeyguardLock(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.acquireKeyguardLock()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
			if(keyguardManager.inKeyguardRestrictedInputMode() && preferences.getBoolean(Constants.SCREEN_ENABLED_KEY, true) && preferences.getBoolean(Constants.KEYGUARD_ENABLED_KEY, true)){
				if(_keyguardLock == null){
					_keyguardLock = keyguardManager.newKeyguardLock(Constants.DROID_NOTIFY_KEYGUARD);
				}
				_keyguardLock.disableKeyguard();
			}
		}catch(Exception ex){
			Log.e("Common.acquireKeyguardLock() ERROR: " + ex.toString());
		}
	}

	/**
	 * Re-Enables the Keyguard for this Activity.
	 */
	public static void clearKeyguardLock(){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.clearKeyguardLock()");
		try{
			if(_keyguardLock != null){
				_keyguardLock.reenableKeyguard();
			}
		}catch(Exception ex){
			Log.e("Common.clearKeyguardLock() ERROR: " + ex.toString());
		}
	}

	/**
	 * Function to format timestamps.
	 * 
	 * @param context - The current context of this Activity.
	 * @param inputTimestamp - The timestamp to be formatted.
	 * 
	 * @return String - Formatted time string.
	 */
	public static String formatTimestamp(Context context, long inputTimestamp, boolean isTimeUTC){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.formatTimestamp()");
		try{
			if(inputTimestamp == 0){
				return "";
			}
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			int timeFormatPreference = Integer.parseInt(preferences.getString(Constants.TIME_FORMAT_KEY, Constants.TIME_FORMAT_DEFAULT));
			String displayTime = null;
			String timestampFormat = null;
			if(timeFormatPreference == Constants.TIME_FORMAT_12_HOUR){
				timestampFormat = "h:mma";
			}else if(timeFormatPreference == Constants.TIME_FORMAT_24_HOUR){
				timestampFormat = "H:mm";
			}else{
				timestampFormat = "h:mma";
			}
			SimpleDateFormat dateFormatted = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			if(isTimeUTC){
				//Convert to UTC format.
				dateFormatted.setTimeZone(TimeZone.getTimeZone("UTC"));
				Date utcOriginalDate = new Date(inputTimestamp);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(utcOriginalDate);
				if (_debug) Log.v("Common.formatTimestamp() utcOriginalDate: " + utcOriginalDate);
				Date timestampDateUTC = dateFormatted.parse(calendar.get(Calendar.MONTH)  + "/" + calendar.get(Calendar.DAY_OF_WEEK)  + "/" + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.HOUR_OF_DAY)  + ":" + calendar.get(Calendar.MINUTE)  + ":" + calendar.get(Calendar.SECOND) );
				//Convert to Local format.
				dateFormatted.setTimeZone(TimeZone.getDefault());
				dateFormatted.applyPattern(timestampFormat);
				displayTime = dateFormatted.format(timestampDateUTC);
			}else{
				dateFormatted.setTimeZone(TimeZone.getDefault());
				dateFormatted.applyPattern(timestampFormat);
				displayTime = dateFormatted.format(inputTimestamp);
			}
			return displayTime;
		}catch(Exception ex){
			Log.e("Common.formatTimestamp() ERROR: " + ex.toString());
			return "";
		}
	}

	/**
	 * Function to format dates.
	 * 
	 * @param context - The current context of this Activity.
	 * @param inputTimestamp - The date to be formatted.
	 * 
	 * @return String - Formatted date string.
	 */
	public static String formatDate(Context context, Date inputDate){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.formatDate()");
		try{
			if(inputDate == null){
				return "";
			}
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			int timeFormatPreference = Integer.parseInt(preferences.getString(Constants.TIME_FORMAT_KEY, Constants.TIME_FORMAT_DEFAULT));
			int dateFormatPreference = Integer.parseInt(preferences.getString(Constants.DATE_FORMAT_KEY, Constants.DATE_FORMAT_DEFAULT));
			String dateFormat = null;
			String timeFormat = null;
			switch(timeFormatPreference){
				case Constants.TIME_FORMAT_12_HOUR:{
					timeFormat = "h:mm a";
					break;
				}
				case Constants.TIME_FORMAT_24_HOUR:{
					timeFormat = "H:mm";
					break;
				}
				default:{
					timeFormat = "h:mm a";
					break;
				}
			}
			switch(dateFormatPreference){
				case Constants.DATE_FORMAT_0:{
					dateFormat = "M/d/yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_1:{
					dateFormat = "M.d.yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_2:{
					dateFormat = "MMM d yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_3:{
					dateFormat = "MMM d, yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_4:{
					dateFormat = "MMMM d yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_5:{
					dateFormat = "MMMM d, yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_6:{
					dateFormat = "d/M/yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_7:{
					dateFormat = "d.M.yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_8:{
					dateFormat = "d MMM yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_9:{
					dateFormat = "d MMM, yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_10:{
					dateFormat = "d MMMM yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_11:{
					dateFormat = "d MMMM, yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_12:{
					dateFormat = "yyyy/M/d" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_13:{
					dateFormat = "yyyy.M.d" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_14:{
					dateFormat = "yyyy MMM d" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_15:{
					dateFormat = "yyyy MMMM d" + " " + " " + timeFormat;
					break;
				}
				default:{
					dateFormat = "M/d/yyyy" + " " + timeFormat;
					break;
				}
			}
			SimpleDateFormat dateFormatted = new SimpleDateFormat(dateFormat);
			dateFormatted.setTimeZone(TimeZone.getDefault());
			return dateFormatted.format(inputDate);
		}catch(Exception ex){
			Log.e("Common.formatDate() ERROR: " + ex.toString());
			return "";
		}
	}

	/**
	 * Function to parse date parts from formated date strings.
	 * 
	 * @param context - The current context of this Activity.
	 * @param inputFormattedDate - The formated date to be parsed.
	 * 
	 * @return String[] - Parsed date string.
	 */
	public static String[] parseDateInfo(Context context, String inputFormattedDate){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.parseDateInfo()");
		try{
			if(inputFormattedDate == null){
				return null;
			}
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			int dateFormatPreference = Integer.parseInt(preferences.getString(Constants.DATE_FORMAT_KEY, Constants.DATE_FORMAT_DEFAULT));
			String[] dateInfoArray = null;
			if(dateFormatPreference == Constants.DATE_FORMAT_0 || dateFormatPreference == Constants.DATE_FORMAT_1 || 
			   dateFormatPreference == Constants.DATE_FORMAT_6 || dateFormatPreference == Constants.DATE_FORMAT_7 ||
			   dateFormatPreference == Constants.DATE_FORMAT_12 || dateFormatPreference == Constants.DATE_FORMAT_13){
				dateInfoArray = inputFormattedDate.split(" ");
			}else{
				String[] dateInfoArrayTemp = inputFormattedDate.split(" ");
				if(dateInfoArrayTemp.length < 5){
					dateInfoArray = new String[]{dateInfoArrayTemp[0] + " " + dateInfoArrayTemp[1] + " " + dateInfoArrayTemp[2], dateInfoArrayTemp[3]};
				}else{
					dateInfoArray = new String[]{dateInfoArrayTemp[0] + " " + dateInfoArrayTemp[1] + " " + dateInfoArrayTemp[2], dateInfoArrayTemp[3], dateInfoArrayTemp[4]};
				}
			}
			return dateInfoArray;
		}catch(Exception ex){
			Log.e("Common.parseDateInfo() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Reschedule a notification.
	 * 
	 * @param context - The application context.
	 * @param notification - The Notification to reschedule.
	 * @param rescheduleTime - The time we want the notification to be rescheduled.
	 * @param rescheduleNumber - The reschedule attempt (in case we need to keep track).
	 * 
	 * @return PendingIntent - The Pending Intent used in the Alarm Manager.
	 */
	public static PendingIntent rescheduleNotification(Context context, apps.droidnotify.Notification notification, long rescheduleTime, int rescheduleNumber){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.rescheduleNotification()");
		//Store the notification information into an ArrayList.
		int notificationType = notification.getNotificationType() + 100;
		//Get Notification Values.
		//========================================================
		//String[] Values:
		//[0]-notificationType
		//[1]-SentFromAddress
		//[2]-MessageBody
		//[3]-TimeStamp
		//[4]-ThreadID
		//[5]-ContactID
		//[6]-ContactName
		//[7]-MessageID
		//[8]-Title
		//[9]-CalendarID
		//[10]-CalendarEventID
		//[11]-CalendarEventStartTime
		//[12]-CalendarEventEndTime
		//[13]-AllDay
		//[14]-CallLogID
		//[15]-K9EmailUri
		//[16]-K9EmailDelUri
		//[17]-LookupKey
		//[18]-PhotoID
		//[19]-NotificationSubType
		//[20]-MessageStringID
		//========================================================
		String sentFromAddress = notification.getSentFromAddress();
		String messageBody = notification.getMessageBody();
		long timeStamp = notification.getTimeStamp();
		long threadID = notification.getThreadID();
		long contactID = notification.getContactID();
		String contactName = notification.getContactName();
		long messageID = notification.getMessageID();
		String title = notification.getTitle();
		long calendarID = notification.getCalendarID();
		long calendarEventID = notification.getCalendarEventID();
		long calendarEventStartTime = notification.getCalendarEventStartTime();
		long calendarEventEndTime = notification.getCalendarEventEndTime();
		String allDay = "0";
		if(notification.getAllDay()){
			allDay = "1";
		}
		long callLogID = notification.getCallLogID();
		String k9EmailUri = notification.getK9EmailUri();
		String k9EmailDelUri = notification.getK9EmailDelUri();
		String lookupKey = notification.getLookupKey();
		long photoID = notification.getPhotoID();
		int notificationSubType = notification.getNotificationSubType();
		String messageStringID = notification.getMessageStringID();
		//Build Notification Information String Array.
		String[] rescheduleNotificationInfo = new String[] {String.valueOf(notificationType), sentFromAddress, messageBody, String.valueOf(timeStamp), String.valueOf(threadID), String.valueOf(contactID), contactName, String.valueOf(messageID), title, String.valueOf(calendarID), String.valueOf(calendarEventID), String.valueOf(calendarEventStartTime), String.valueOf(calendarEventEndTime), allDay, String.valueOf(callLogID), k9EmailUri, k9EmailDelUri, lookupKey, String.valueOf(photoID), String.valueOf(notificationSubType), messageStringID};
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent rescheduleIntent = new Intent(context, RescheduleReceiver.class);
		Bundle rescheduleBundle = new Bundle();
		rescheduleBundle.putStringArray("rescheduleNotificationInfo", rescheduleNotificationInfo);
		rescheduleBundle.putInt("rescheduleNumber", rescheduleNumber);
		rescheduleBundle.putInt("notificationType", notificationType);
		rescheduleIntent.putExtras(rescheduleBundle);
		rescheduleIntent.setAction("apps.droidnotify.VIEW/RescheduleNotification/" + rescheduleNumber + "/" + String.valueOf(notification.getTimeStamp()));
		PendingIntent reschedulePendingIntent = PendingIntent.getBroadcast(context, 0, rescheduleIntent, 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP, rescheduleTime, reschedulePendingIntent);
		return reschedulePendingIntent;
	}
	
	/**
	 * Determine if the current time falls during a defined quiet time.
	 * 
	 * @param context - The application context.
	 * 
	 * @return boolean - Returns true if Quiet Time is enabled and the current time falls within the defined tiem period.
	 */
	public static boolean isQuietTime(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.isQuietTime()");
		_context = context;
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		if(preferences.getBoolean(Constants.QUIET_TIME_ENABLED_KEY, false)){
			Calendar calendar = new GregorianCalendar();
			if (_debug) Log.v("Common.isQuietTime() HOUR: " + calendar.get(Calendar.HOUR_OF_DAY));
			String startTime = preferences.getString(Constants.QUIET_TIME_START_TIME_KEY, "");
			String stopTime = preferences.getString(Constants.QUIET_TIME_STOP_TIME_KEY, "");
			int hourStart = 0;
			int minuteStart = 0;
			int hourStop = 0;
			int minueStop = 0;
			if(startTime.equals("")){
				return false;
			}
			if(stopTime.equals("")){
				return false;
			}
			String[] startTimeArray = startTime.split("\\|");
			if(startTimeArray.length != 2){
				return false;
			}
			String[] stopTimeArray = stopTime.split("\\|");
			if(stopTimeArray.length != 2){
				return false;
			}
			hourStart = Integer.parseInt(startTimeArray[0]);
			minuteStart = Integer.parseInt(startTimeArray[1]);
			hourStop = Integer.parseInt(stopTimeArray[0]);
			minueStop = Integer.parseInt(stopTimeArray[1]);
			if(preferences.getString(Constants.QUIET_TIME_OF_WEEK_KEY, Constants.QUIET_TIME_EVERYDAY_VALUE).equals(Constants.QUIET_TIME_EVERYDAY_VALUE)){
				return timeFallsWithinPeriod(calendar, hourStart, minuteStart, hourStop, minueStop);
			}else if(preferences.getString(Constants.QUIET_TIME_OF_WEEK_KEY, Constants.QUIET_TIME_EVERYDAY_VALUE).equals(Constants.QUIET_TIME_ONLY_WEEKEND_VALUE)){
				if(calendar.get(Calendar.DAY_OF_WEEK) == 1 || calendar.get(Calendar.DAY_OF_WEEK) == 7){
					return timeFallsWithinPeriod(calendar, hourStart, minuteStart, hourStop, minueStop);
				}else{
					return false;
				}
			}else if(preferences.getString(Constants.QUIET_TIME_OF_WEEK_KEY, Constants.QUIET_TIME_EVERYDAY_VALUE).equals(Constants.QUIET_TIME_ONLY_WEEKDAY_VALUE)){
				if(calendar.get(Calendar.DAY_OF_WEEK) == 2 || calendar.get(Calendar.DAY_OF_WEEK) == 3 || calendar.get(Calendar.DAY_OF_WEEK) == 4 || calendar.get(Calendar.DAY_OF_WEEK) == 5 || calendar.get(Calendar.DAY_OF_WEEK) == 6){
					return timeFallsWithinPeriod(calendar, hourStart, minuteStart, hourStop, minueStop);
				}else{
					return false;
				}
			}else{
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Wrapper function to speak a message using TTS.
	 * 
	 * @param context - The application context.
	 * @param tts - The TTS Object.
	 * @param text - The text to speak.
	 * 
	 * @return boolean - Return true if the Android TTS engine could be started.
	 */
	public static boolean speak(Context context, TextToSpeech tts, String text){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.speak()");
		if (tts == null) {
			return false;
	    }else{
	    	tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	    	return true;
	    }
	}
	
	/**
	 * Remove the HTML formatting from a string.
	 * 
	 * @param input - The string to remove the formatting from.
	 * 
	 * @return String - The output string without any html.
	 */
	public static String removeHTML(String input){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.removeHTML()");
		String output = input;
		output = output.replace("<br/>", ". ");
		output = output.replace("<i>", "").replace("</i>", "");
		output = output.replace("<b>", "").replace("</b>", "");
		output = output.replace("<u>", "").replace("</u>", "");
		return output;
	}
	
	/**
	 * Set the UserInLinkedApp flag.
	 * 
	 * @param context - The application context.
	 * @param flag - Boolean flag to set.
	 */
	public static void setInLinkedAppFlag(Context context, boolean flag){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.setInLinkedAppFlag()");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(Constants.USER_IN_LINKED_APP_KEY, flag);
		editor.commit();
	}

	/**
	 * Get the UserInLinkedApp flag.
	 * 
	 * @param context - The application context.
	 * 
	 * @return boolean - The boolean flag to return.
	 */
	public static boolean isUserInLinkedApp(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.isUserInLinkedApp()");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getBoolean(Constants.USER_IN_LINKED_APP_KEY, false);
	}
	
	/**
	 * This resends an intent to the main notification activity.
	 * 
	 * @param context - The application context.
	 * @param intent - The intent to resend.
	 */
	public static void resendNotification(Context context, Intent intent){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.resendNotification()");
		try{
			Bundle bundle = intent.getExtras();
	    	Intent notificationIntent = new Intent(context, NotificationActivity.class);
	    	notificationIntent.putExtras(bundle);
	    	notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	    	Common.acquireWakeLock(context);
	    	context.startActivity(notificationIntent);
		}catch(Exception ex){
			Log.e("Common.resendNotification() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Start the Notification Activity and send it the provided bundle.
	 * 
	 * @param context - The application context.
	 * @param bundle - The bundle to send to the activity.
	 * 
	 * @return boolean - Returns true if the activity was started successfully.
	 */
	public static boolean startNotificationActivity(Context context, Bundle bundle){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startNotificationActivity()");
		try{
			Intent notificationIntent = new Intent(context, NotificationActivity.class);
	    	notificationIntent.putExtras(bundle);
	    	notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	    	Common.acquireWakeLock(context);
	    	context.startActivity(notificationIntent);	
	    	return true;
		}catch(Exception ex){
			Log.e("Common.startNotificationActivity() ERROR: " + ex.toString());
			return false;
		}
	}
	
	/**
	 * Start an alarm with the given parameters. This is mainly used to reschedule notifications.
	 * 
	 * @param context - The application context.
	 * @param className - The name of the receiver class.
	 * @param extrasBundle - The extras information to pass to the recevier class.
	 * @param actionText - The text that differentiates this alarm from other alarms.
	 * @param rescheduleTime - The time the alarm should go off.
	 */
	public static void startAlarm(Context context, Class<?> className, Bundle extrasBundle, String actionText, long alarmTime){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startAlarm()");
		try{
			AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, className);
			if(extrasBundle != null){
				intent.putExtras(extrasBundle);
			}
			if(actionText != null){
				intent.setAction(actionText);
			}
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
		}catch(Exception ex){
			Log.e("Common.startAlarm() ERROR: " + ex.toString());
		}
	}	
	
	/**
	 * Checks if the users phone is online or not.
	 * 
	 * @param context - The application context.
	 * 
	 * @return boolean - Returns true if the user is online.
	 */
	public static boolean isOnline(Context context) {
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.isOnline()");
		try{
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if(connectivityManager == null){
				if (_debug) Log.v("Common.isOnline() ConnectivityManager is null. Exiting...");
				return false;
			}
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if(networkInfo == null){
				if (_debug) Log.v("Common.isOnline() NetworkInfo is null. Exiting...");
				return false;
			}
			return networkInfo.isConnected();
		 }catch(Exception ex){
			Log.e("Common.isOnline() ERROR: " + ex.toString());
			return false;
		}
	}
	
	/**
	 * Read the Application info and return the app version number.
	 * 
	 * @param context - The application context.
	 * 
	 * @return String - The version number of the application.
	 */
	public static String getApplicationVersion(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getApplicationVersion()");
		PackageInfo packageInfo = null;
		try{
			packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;
		}catch(Exception ex){
			return "";
		}
	}
	
	/**
	 * Set the application language.
	 * 
	 * @param context - The application context.
	 */
	public static void setApplicationLanguage(Context context, Activity activity){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.setApplicationLanguage()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			String appLanguage = preferences.getString(Constants.LANGUAGE_KEY, Constants.LANGUAGE_DEFAULT);
			Locale locale = null;
			if(appLanguage.equals(Constants.LANGUAGE_DEFAULT)){				
				locale = new Locale(Resources.getSystem().getConfiguration().locale.getLanguage());
			}else{
				locale = new Locale(appLanguage);
			} 
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            activity.getBaseContext().getResources().updateConfiguration(config, activity.getBaseContext().getResources().getDisplayMetrics());
		}catch(Exception ex){
			Log.e("Common.setApplicationLanguage() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Open a URL in a browser application.
	 * 
	 * @param context - The application context.
	 * @param notificationActivity - A reference to the parent activity. 
	 * @param linkURL - The URL we want to browser to open.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startBrowserActivity(Context context, NotificationActivity notificationActivity, String linkURL, int requestCode, boolean displayErrors){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startBrowserActivity() LinkURL: " + linkURL);
		try{
			if(linkURL == null || linkURL.equals("")){
				if(displayErrors) Toast.makeText(context, context.getString(R.string.url_link_not_found_error), Toast.LENGTH_LONG).show();
				return false;
			}
			Intent browserIntent = new Intent(Intent.ACTION_VIEW);	
			browserIntent.setData(Uri.parse(linkURL));
			browserIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(browserIntent, requestCode);
			setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			Log.e("Common.startBrowserActivity() ERROR: " + ex.toString());
			if(displayErrors) Toast.makeText(context, context.getString(R.string.browser_app_error), Toast.LENGTH_LONG).show();
			setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
	/**
	 * Return a bundle that contains all the notification information provided.
	 * 
	 * @param sentFromAddress
	 * @param sentFromID
	 * @param messageBody
	 * @param timeStamp
	 * @param threadID
	 * @param contactID
	 * @param contactName
	 * @param photoID
	 * @param notificationType
	 * @param messageID
	 * @param messageStringID
	 * @param title
	 * @param calendarID
	 * @param calendarEventID
	 * @param calendarEventStartTime
	 * @param calendarEventEndTime
	 * @param allDay
	 * @param callLogID
	 * @param lookupKey
	 * @param k9EmailUri
	 * @param k9EmailDelUri
	 * @param rescheduleNumber
	 * @param notificationSubType
	 * @param linkURL
	 * 
	 * @return Bundle - A Bundle that contains all the information provided.
	 */
	public static Bundle createNotificationBundle(String sentFromAddress, long sentFromID, String messageBody, long timeStamp, long threadID, long contactID, String contactName, long photoID, int notificationType, long messageID, String messageStringID, String title, long calendarID, long calendarEventID, long calendarEventStartTime, long calendarEventEndTime, String calendarName, boolean allDay, long callLogID, String lookupKey, String k9EmailUri, String k9EmailDelUri, int rescheduleNumber, int notificationSubType, String linkURL){
		Bundle notificationBundle = new Bundle();		
		if(sentFromAddress != null) notificationBundle.putString(Constants.BUNDLE_SENT_FROM_ADDRESS, sentFromAddress);
		if(sentFromID != 0) notificationBundle.putLong(Constants.BUNDLE_SENT_FROM_ID, sentFromID);
		if(messageBody != null) notificationBundle.putString(Constants.BUNDLE_MESSAGE_BODY, messageBody);
		if(timeStamp != 0) notificationBundle.putLong(Constants.BUNDLE_TIMESTAMP, timeStamp);
		if(threadID != 0) notificationBundle.putLong(Constants.BUNDLE_THREAD_ID, threadID);
		if(contactID != 0) notificationBundle.putLong(Constants.BUNDLE_CONTACT_ID, contactID);
		if(contactName != null) notificationBundle.putString(Constants.BUNDLE_CONTACT_NAME, contactName);
		if(photoID != 0) notificationBundle.putLong(Constants.BUNDLE_PHOTO_ID, photoID);
		if(notificationType != 0) notificationBundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, notificationType);
		if(messageID != 0) notificationBundle.putLong(Constants.BUNDLE_MESSAGE_ID, messageID);
		if(messageStringID != null) notificationBundle.putString(Constants.BUNDLE_MESSAGE_STRING_ID, messageStringID);
		if(title != null) notificationBundle.putString(Constants.BUNDLE_TITLE, title);
		if(calendarID != 0) notificationBundle.putLong(Constants.BUNDLE_CALENDAR_ID, calendarID);
		if(calendarEventID != 0) notificationBundle.putLong(Constants.BUNDLE_CALENDAR_EVENT_ID, calendarEventID);
		if(calendarEventStartTime != 0) notificationBundle.putLong(Constants.BUNDLE_CALENDAR_EVENT_START_TIME, calendarEventStartTime);
		if(calendarEventEndTime != 0) notificationBundle.putLong(Constants.BUNDLE_CALENDAR_EVENT_END_TIME, calendarEventEndTime);
		if(calendarName != null) notificationBundle.putString(Constants.BUNDLE_CALENDAR_NAME, calendarName);
		notificationBundle.putBoolean(Constants.BUNDLE_ALL_DAY, allDay);
		if(callLogID != 0) notificationBundle.putLong(Constants.BUNDLE_CALL_LOG_ID, callLogID);
		if(lookupKey != null) notificationBundle.putString(Constants.BUNDLE_LOOKUP_KEY, lookupKey);
		if(k9EmailUri != null) notificationBundle.putString(Constants.BUNDLE_K9_EMAIL_URI, k9EmailUri);
		if(k9EmailDelUri != null) notificationBundle.putString(Constants.BUNDLE_K9_EMAIL_DEL_URI, k9EmailDelUri);
		if(rescheduleNumber != 0) notificationBundle.putInt(Constants.BUNDLE_RESCHEDULE_NUMBER, rescheduleNumber);
		if(notificationSubType != 0) notificationBundle.putInt(Constants.BUNDLE_NOTIFICATION_SUB_TYPE, notificationSubType);
		if(linkURL != null) notificationBundle.putString(Constants.BUNDLE_LINK_URL, linkURL);
		return notificationBundle;
	}
	
	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Remove a particular status bar notification.
	 * 
	 * @param context - The application context.
	 * @param notificationType - The notification type.
	 */
	private static void removeStatusBarNotification(Context context, int notificationType){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.removeStatusBarNotification()");
		try{
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(notificationType);
		}catch(Exception ex){
			Log.e("Common.removeStatusBarNotification() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Parse a vibration pattern.
	 * 
	 * @param vibratePattern - The vibrate pattern to verify.
	 * 
	 * @return boolean - Returns True if the vibrate pattern is valid.
	 */
	private static long[] parseVibratePattern(String vibratePattern){
		if (_debug) Log.v("Common.parseVibratePattern()");
	    final int VIBRATE_PATTERN_MAX_LENGTH = 60000;
	    final int VIBRATE_PATTERN_MAX_SIZE = 100;
		ArrayList<Long> vibratePatternArrayList = new ArrayList<Long>();
		long[] vibratePatternArray = null;
		String[] vibratePatternStringArray = vibratePattern.split(",");
		int arraySize = vibratePatternStringArray.length;
	    for (int i = 0; i < arraySize; i++) {
	    	long vibrateLength = 0;
	    	try {
	    		vibrateLength = Long.parseLong(vibratePatternStringArray[i].trim());
	    	} catch (Exception ex) {
	    		Log.e("Common.parseVibratePattern() ERROR: " + ex.toString());
	    		return null;
	    	}
	    	if(vibrateLength < 0){
	    		vibrateLength = 0;
	    	}
	    	if(vibrateLength > VIBRATE_PATTERN_MAX_LENGTH){
	    		vibrateLength = VIBRATE_PATTERN_MAX_LENGTH;
	    	}
	    	vibratePatternArrayList.add(vibrateLength);
	    }
	    arraySize = vibratePatternArrayList.size();
	    if (arraySize > VIBRATE_PATTERN_MAX_SIZE){
	    	arraySize = VIBRATE_PATTERN_MAX_SIZE;
	    }
	    vibratePatternArray = new long[arraySize];
	    for (int i = 0; i < arraySize; i++) {
	    	vibratePatternArray[i] = vibratePatternArrayList.get(i);
	    }
		return vibratePatternArray;
	}
	
	/**
	 * Parse an led blink pattern.
	 * 
	 * @param ledPattern - The blink pattern to verify.
	 * 
	 * @return boolean - Returns True if the blink pattern is valid.
	 */
	private static int[] parseLEDPattern(String ledPattern){
		if (_debug) Log.v("Common.parseLEDPattern()");
	    final int LED_PATTERN_MAX_LENGTH = 60000;
		int[] ledPatternArray = {0, 0};
		String[] ledPatternStringArray = ledPattern.split(",");
		if(ledPatternStringArray.length != 2){
			return null;
		}
	    for (int i = 0; i < 2; i++) {
	    	int blinkLength = 0;
	    	try {
	    		blinkLength = Integer.parseInt(ledPatternStringArray[i].trim());
	    	} catch (Exception ex) {
	    		Log.e("Common.parseLEDPattern() ERROR: " + ex.toString());
	    		return null;
	    	}
	    	if(blinkLength < 0){
	    		blinkLength = 0;
	    	}
	    	if(blinkLength > LED_PATTERN_MAX_LENGTH){
	    		blinkLength = LED_PATTERN_MAX_LENGTH;
	    	}
	    	ledPatternArray[i] = blinkLength;
	    }
		return ledPatternArray;
	}
	
	/**
	 * Play a notification sound through the media player.
	 * 
	 * @author Camille Sévigny
	 */
	private static class playNotificationMediaFileAsyncTask extends AsyncTask<String, Void, Void> {
	    
	    /**
	     * Do this work in the background.
	     * 
	     * @param params - The URI of the notification sound.
	     */
	    protected Void doInBackground(String... params) {
			if (_debug) Log.v("Common.playNotificationMediaFileAsyncTask.doInBackground()");
			MediaPlayer mediaPlayer = null;
			try{
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setLooping(false);
				mediaPlayer.setDataSource(_context,  Uri.parse(params[0]));
				mediaPlayer.prepare();
				mediaPlayer.start();
				mediaPlayer.setOnCompletionListener(new OnCompletionListener(){
	                public void onCompletion(MediaPlayer mediaPlayer) {
	                	mediaPlayer.release();
	                	mediaPlayer = null;
	                }
				});	
		    	return null;
			}catch(Exception ex){
				Log.e("Common.playNotificationMediaFileAsyncTask.doInBackground() ERROR: " + ex.toString());
				mediaPlayer.release();
            	mediaPlayer = null;
				return null;
			}
	    }
	    
	    /**
	     * Nothing needs to happen once the media file has been played.
	     * 
	     * @param result - Void.
	     */
	    protected void onPostExecute(Void result) {
			if (_debug) Log.v("Common.playNotificationMediaFileAsyncTask.onPostExecute()");
	    }
	    
	}
	
	/**
	 * Determine if the current time falls within the period time.
	 * 
	 * @param calendar - The calendar we should use.
	 * @param hourStart - The starting hour of the time period.
	 * @param minuteStart - The starting minute of the time period.
	 * @param hourStop - The ending hour of the time period.
	 * @param minueStop - The ending minute of the time period.
	 * 
	 * @return boolean - Returns true if the current time falls within the period time.
	 */
	private static boolean timeFallsWithinPeriod(Calendar calendar, int hourStart, int minuteStart, int hourStop, int minuteStop){
		if (_debug) Log.v("Common.timeFallsWithinPeriod()");
		int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		int currentMinute = calendar.get(Calendar.MINUTE);
		if(hourStart < hourStop){
			//Time period is within the same day.
			if(currentHour >= hourStart && currentHour <= hourStop){
				if(currentHour == hourStart || currentHour == hourStop){
					if(currentHour == hourStart){
						if(currentMinute >= minuteStart){
							return true;
						}else{
							return false;
						}
					}else{
						if(currentMinute <= minuteStop){
							return true;
						}else{
							return false;
						}	
					}
				}else{
					return true;
				}
			}else{
				return false;
			}
		}else{
			//Time period spans 2 days.
			if(currentHour >= hourStart || currentHour <= hourStop){
				if(currentHour == hourStart || currentHour == hourStop){
					if(currentHour == hourStart){
						if(currentMinute >= minuteStart){
							return true;
						}else{
							return false;
						}
					}else{
						if(currentMinute <= minuteStop){
							return true;
						}else{
							return false;
						}	
					}
				}else{
					return true;
				}
			}else{
				return false;
			}
		}
	}
	
}