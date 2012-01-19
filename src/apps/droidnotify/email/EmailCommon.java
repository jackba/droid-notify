package apps.droidnotify.email;

import java.util.Date;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import apps.droidnotify.NotificationActivity;
import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

public class EmailCommon {

	//================================================================================
    // Properties
    //================================================================================
	
	private static boolean _debug = false; 
	
	//================================================================================
	// Public Methods
	//================================================================================	
	
	/**
	 * Parse the incoming K9 message directly.
	 * 
	 * @param context - The application context.
	 * @param bundle - Bundle from the incoming intent.
	 * 
	 * @return Bundle - Returns a Bundle that contain the K9 notification information.
	 */
	public static Bundle getK9MessagesFromIntent(Context context, Bundle bundle, String intentAction){
		_debug = Log.getDebug();
		if (_debug) Log.v("EmailCommon.getK9MessagesFromIntent() intentAction: " + intentAction + ":");
		Bundle k9NotificationBundle = new Bundle();
    	long timeStamp = 0;
    	String sentFromAddress = null;
    	String messageBody = null;
    	String messageSubject = null;
    	long messageID = 0;
		String k9EmailUri = null;
		String k9EmailDelUri = null;
		Date sentDate = null;
		String packageName = "com.fsck.k9";
		int notificationSubType = Constants.NOTIFICATION_TYPE_K9_MAIL;
		try{
			if(intentAction.equals(Constants.INTENT_ACTION_KAITEN)){
				notificationSubType = Constants.NOTIFICATION_TYPE_KAITEN_MAIL;
				packageName = "com.kaitenmail";
			}else if(intentAction.equals(Constants.INTENT_ACTION_K9)){
				notificationSubType = Constants.NOTIFICATION_TYPE_K9_MAIL;
				packageName = "com.fsck.k9";
			}
			if (_debug) Log.v("EmailCommon.getK9MessagesFromIntent() Email PackageName: " + packageName);
			sentDate = (Date) bundle.get(packageName + ".intent.extra.SENT_DATE");
            messageSubject = bundle.getString(packageName + ".intent.extra.SUBJECT").toLowerCase();
            sentFromAddress = parseFromEmailAddress(bundle.getString(packageName + ".intent.extra.FROM").toLowerCase());
			timeStamp = sentDate.getTime();
            //if (_debug) Log.v("EmailCommon.getK9MessagesFromIntent() sentFromAddress: " + sentFromAddress);
            //Get the message body.
    		final String[] projection = new String[] {"_id", "date", "sender", "subject", "preview", "account", "uri", "delUri"};
            final String selection = "date = " + timeStamp;
    		final String[] selectionArgs = null;
    		final String sortOrder = null;
    		boolean emailFoundFlag = false;
    		Cursor cursor = null;
            try{
    		    cursor = context.getContentResolver().query(
    		    		Uri.parse("content://" + packageName + ".messageprovider/inbox_messages/"),
    		    		projection,
    		    		selection,
    					selectionArgs,
    					sortOrder);
    	    	if(cursor.moveToFirst()){
		    		messageID = Long.parseLong(cursor.getString(cursor.getColumnIndex("_id")));
		    		messageBody = cursor.getString(cursor.getColumnIndex("preview"));
		    		k9EmailUri = cursor.getString(cursor.getColumnIndex("uri"));
		    		k9EmailDelUri = cursor.getString(cursor.getColumnIndex("delUri"));
		    		emailFoundFlag = true;
    	    	}else{
    	    		if (_debug) Log.v("EmailCommon.getK9MessagesFromIntent() No Email Found Using The Following URI! URI: 'content://" + packageName + ".messageprovider/inbox_messages/'");
    	    	}
    		}catch(Exception ex){
    			Log.e("EmailCommon.getK9MessagesFromIntent() CURSOR ERROR: " + ex.toString());
    		}finally{
        		cursor.close();
    		}
            if(emailFoundFlag == false){
            	return null;
            }
            if(messageSubject != null && !messageSubject.equals("")){
				messageBody = "<b>" + messageSubject + "</b><br/>" + messageBody.replace("\n", "<br/>").trim();
			}else{
				messageBody = messageBody.replace("\n", "<br/>").trim();
			}
    		Bundle k9ContactInfoBundle = Common.getContactsInfoByEmail(context, sentFromAddress);
    		if(k9ContactInfoBundle == null){
				//Basic Notification Information.
    			k9NotificationBundle.putString(Constants.BUNDLE_SENT_FROM_ADDRESS, sentFromAddress);
    			k9NotificationBundle.putString(Constants.BUNDLE_MESSAGE_BODY, messageBody);
    			k9NotificationBundle.putLong(Constants.BUNDLE_MESSAGE_ID, messageID);
    			k9NotificationBundle.putLong(Constants.BUNDLE_TIMESTAMP, timeStamp);
    			k9NotificationBundle.putString(Constants.BUNDLE_K9_EMAIL_URI, k9EmailUri);
    			k9NotificationBundle.putString(Constants.BUNDLE_K9_EMAIL_DEL_URI, k9EmailDelUri);
    			k9NotificationBundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_K9);
    			k9NotificationBundle.putInt(Constants.BUNDLE_NOTIFICATION_SUB_TYPE, notificationSubType);
			}else{
				//Basic Notification Information.
    			k9NotificationBundle.putString(Constants.BUNDLE_SENT_FROM_ADDRESS, sentFromAddress);
    			k9NotificationBundle.putString(Constants.BUNDLE_MESSAGE_BODY, messageBody);
    			k9NotificationBundle.putLong(Constants.BUNDLE_MESSAGE_ID, messageID);
    			k9NotificationBundle.putLong(Constants.BUNDLE_TIMESTAMP, timeStamp);
    			k9NotificationBundle.putString(Constants.BUNDLE_K9_EMAIL_URI, k9EmailUri);
    			k9NotificationBundle.putString(Constants.BUNDLE_K9_EMAIL_DEL_URI, k9EmailDelUri);
    			k9NotificationBundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_K9);
    			k9NotificationBundle.putInt(Constants.BUNDLE_NOTIFICATION_SUB_TYPE, notificationSubType);
    			//Contact Information.
    			k9NotificationBundle.putLong(Constants.BUNDLE_CONTACT_ID, k9ContactInfoBundle.getLong(Constants.BUNDLE_CONTACT_ID, 0));
    			k9NotificationBundle.putString(Constants.BUNDLE_CONTACT_NAME, k9ContactInfoBundle.getString(Constants.BUNDLE_CONTACT_NAME));
    			k9NotificationBundle.putLong(Constants.BUNDLE_PHOTO_ID, k9ContactInfoBundle.getLong(Constants.BUNDLE_PHOTO_ID, 0));
    			k9NotificationBundle.putString(Constants.BUNDLE_LOOKUP_KEY, k9ContactInfoBundle.getString(Constants.BUNDLE_LOOKUP_KEY));
			}
    		return k9NotificationBundle;
		}catch(Exception ex){
			Log.e("EmailCommon.getK9MessagesFromIntent() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Start the intent for any K9 email application to view the email inbox.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startK9EmailAppViewInboxActivity(Context context, NotificationActivity notificationActivity, int notificationSubType, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("EmailCommon.startK9EmailAppViewInboxActivity()");
		try{
	        Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);	   
	        String packageName = "com.fsck.k9";
			if(notificationSubType == Constants.NOTIFICATION_TYPE_KAITEN_MAIL){
				packageName = "com.kaitenmail";
			}else if(notificationSubType == Constants.NOTIFICATION_TYPE_K9_MAIL){
				packageName = "com.fsck.k9";
			}
            intent.setComponent(new ComponentName(packageName, packageName + ".activity.Accounts"));            
	        notificationActivity.startActivityForResult(intent, requestCode);
	        Common.setInLinkedAppFlag(context, true);
	        return true;
		}catch(Exception ex){
			Log.e("Common.startK9EmailAppViewInboxActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_email_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
	/**
	 * Start the intent for any android K9 email application to send a reply.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param k9EmailURI - The k9 email uri that is built for the k-9 clients.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startK9MailAppReplyActivity(Context context, NotificationActivity notificationActivity, String k9EmailUri, int notificationSubType, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("EmailCommon.startK9MailAppReplyActivity()");
		if(k9EmailUri == null || k9EmailUri.equals("")){
			Toast.makeText(context, context.getString(R.string.app_reply_email_address_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
		try{
			Intent intent = new Intent(Intent.ACTION_VIEW);
		    intent.setData(Uri.parse(k9EmailUri));
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        notificationActivity.startActivityForResult(intent, requestCode);
	        Common.setInLinkedAppFlag(context, true);
	        return true;
		}catch(Exception ex){
			Log.e("EmailCommon.startK9MailAppReplyActivity() ERROR: " + ex.toString());
			startK9EmailAppViewInboxActivity(context, notificationActivity, notificationSubType, requestCode);
			//Toast.makeText(context, context.getString(R.string.app_email_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
	}

	/**
	 * Delete a K9 Email using it's own URI.
	 * 
	 * @param context - The current context of this Activity.
	 * @param k9EmailDelUri - The URI provided to delete the email.
	 */
	public static void deleteK9Email(Context context, String k9EmailDelUri, int notificationSubType){
		_debug = Log.getDebug();
		if (_debug) Log.v("EmailCommon.deleteK9Email()");
		try{
			if(k9EmailDelUri == null || k9EmailDelUri.equals("")){
				if (_debug) Log.v("EmailCommon.deleteK9Email() k9EmailDelUri == null/empty. Exiting...");
				return;
			}
			String selection = null;
			String[] selectionArgs = null;
			context.getContentResolver().delete(
					Uri.parse(k9EmailDelUri),
					selection, 
					selectionArgs);
			return;
		}catch(Exception ex){
			Log.e("EmailCommon.deleteK9Email() ERROR: " + ex.toString());
			return;
		}
	}
	
	/**
	 * Remove formatting from email addresses.
	 * 
	 * @param address - String of original email address.
	 * 
	 * @return String - String of email address with no formatting.
	 */
	public static String removeEmailFormatting(String address){
		_debug = Log.getDebug();
		if (_debug) Log.v("EmailCommon.removeEmailFormatting()");
		//if (_debug) Log.v("EmailCommon.removeEmailFormatting() Email Address: " + address);
		if(address.contains("<") && address.contains(">")){
			address = address.substring(address.indexOf("<") + 1,address.indexOf(">"));
		}
		if(address.contains("(") && address.contains(")")){
			address = address.substring(address.indexOf("(") + 1,address.indexOf(")"));
		}
		if(address.contains("[") && address.contains("]")){
			address = address.substring(address.indexOf("[") + 1,address.indexOf("]"));
		}
		//if (_debug) Log.v("EmailCommon.removeEmailFormatting() Formatted Email Address: " + address);
		return address.toLowerCase().trim();
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Parse an email address form a "FROM" email address.
	 * 
	 * @param inputFromAddress - The address we wish to parse.
	 * 
	 * @return String- The email address that we parsed/extracted.
	 */
	private static String parseFromEmailAddress(String inputFromAddress){
		if (_debug) Log.v("EmailCommon.parseFromEmailAddress()");
		try{
			if(inputFromAddress == null || inputFromAddress.equals("")){
				if (_debug) Log.v("EmailCommon.parseFromEmailAddress() InputFromAddress is null/empty. Exiting...");
				return inputFromAddress;
			}
			String outputEmailAddress = null;
			if(inputFromAddress.contains("<") && inputFromAddress.contains(">")){
				outputEmailAddress = inputFromAddress.substring(inputFromAddress.indexOf("<") + 1, inputFromAddress.indexOf(">"));
			}else{
				 outputEmailAddress = inputFromAddress;
			}
			return outputEmailAddress;
		}catch(Exception ex){
			Log.e("EmailCommon.parseFromEmailAddress() ERROR: " + ex.toString());
			return inputFromAddress;
		}
	}
	
}
