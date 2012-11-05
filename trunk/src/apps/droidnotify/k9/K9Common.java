package apps.droidnotify.k9;

import java.util.Date;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import apps.droidnotify.NotificationActivity;
import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.contacts.ContactsCommon;
import apps.droidnotify.log.Log;

public class K9Common {

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
		_debug = Log.getDebug(context);
		if (_debug) Log.v(context, "K9Common.getK9MessagesFromIntent() IntentAction: " + intentAction);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Bundle k9NotificationBundle = new Bundle();
		int bundleCount = 0;
    	long timeStamp = -1;
		String accountName = null;
    	String sentFromAddress = null;
    	String messageBody = null;
    	String messageSubject = null;
    	long messageID = -1;
		String k9EmailUri = null;
		String k9EmailDelUri = null;
		Date sentDate = null;
		String packageName = "com.fsck.k9";
		int notificationSubType = Constants.NOTIFICATION_TYPE_K9_MAIL;
		try{	    			
			Bundle k9NotificationBundleSingle = new Bundle();
			bundleCount++;
			if(intentAction.startsWith(Constants.INTENT_ACTION_KAITEN)){
				notificationSubType = Constants.NOTIFICATION_TYPE_KAITEN_MAIL;
				packageName = "com.kaitenmail";
			}else if(intentAction.startsWith(Constants.INTENT_ACTION_K9)){
				notificationSubType = Constants.NOTIFICATION_TYPE_K9_MAIL;
				packageName = "com.fsck.k9";
			}else if(intentAction.startsWith(Constants.INTENT_ACTION_K9_FOR_PURE)){
				notificationSubType = Constants.NOTIFICATION_TYPE_K9_FOR_PURE;
				packageName = "org.koxx.k9ForPureWidget";
			}
			//if (_debug) Log.v(context, "K9Common.getK9MessagesFromIntent() Email PackageName: " + packageName);
			sentDate = (Date) bundle.get(packageName + ".intent.extra.SENT_DATE");
			//if (_debug) Log.v(context, "K9Common.getK9MessagesFromIntent() Email SentDate: " + String.valueOf(sentDate));
			timeStamp = sentDate.getTime();
			//if (_debug) Log.v(context, "K9Common.getK9MessagesFromIntent() Email TimeStamp: " + String.valueOf(timeStamp));
			try{
				messageSubject = bundle.getString(packageName + ".intent.extra.SUBJECT");
			}catch(Exception ex){
				messageSubject = "";
			}
            //if (_debug) Log.v(context, "K9Common.getK9MessagesFromIntent() Email MessageSubject: " + messageSubject);
            sentFromAddress = parseFromEmailAddress(bundle.getString(packageName + ".intent.extra.FROM").toLowerCase());
            //if (_debug) Log.v(context, "K9Common.getK9MessagesFromIntent() Email SentFromAddress: " + sentFromAddress);			
            accountName = bundle.getString(packageName + ".intent.extra.ACCOUNT");
            //if (_debug) Log.v(context, "K9Common.getK9MessagesFromIntent() Email AccountName: " + accountName);
            //Get the message body.
    		boolean emailFoundFlag = false;
    		Cursor cursor = null;
    		String emailURI = "content://" + packageName + ".messageprovider/inbox_messages/";
            try{
	    		if(packageName.equals("org.koxx.k9ForPureWidget")){
	    			String accountUID = getK9ForPureEmailAccountUID(context, accountName);
	    			emailURI = emailURI + accountUID;
	            	//if (_debug) Log.v(context, "K9Common.getK9MessagesFromIntent() EmailURI: " + emailURI);
	        		final String[] projection = new String[] {"_id", "date", "sender", "subject", "preview", "account", "uri"};
	        		final String selection = null;
	        		final String[] selectionArgs = null;
	        		final String sortOrder = "date DESC";
	    		    cursor = context.getContentResolver().query(
	    		    		Uri.parse(emailURI),
	    		    		projection,
	    		    		selection,
	    					selectionArgs,
	    					sortOrder);
	    		    if(cursor == null){
	    		    	Log.e(context, "K9Common.getK9MessagesFromIntent() Cursor is null. Exiting...");
	    		    	return null;
	    		    }
		    		while(cursor.moveToNext()){
	    				String accountNameTmp = cursor.getString(cursor.getColumnIndex("account"));
		    			long timeStampTmp = cursor.getLong(cursor.getColumnIndex("date"));
		    			//String subjectTmp = cursor.getString(cursor.getColumnIndex("subject"));    	    			
		    			//if (_debug) Log.v(context, "K9Common.getK9MessagesFromIntent() accountNameTmp: " + accountNameTmp + " accountName: " + accountName);
		    			//if (_debug) Log.v(context, "K9Common.getK9MessagesFromIntent() timeStampTmp: " + timeStampTmp + " timeStamp: " + timeStamp);
		    			//if (_debug) Log.v(context, "K9Common.getK9MessagesFromIntent() subjectTmp: " + subjectTmp + " messageSubject: " + messageSubject);
		    			if(timeStampTmp == timeStamp && accountNameTmp.equals(accountName)){
				    		messageID = cursor.getLong(cursor.getColumnIndex("_id"));
				    		messageBody = cursor.getString(cursor.getColumnIndex("preview"));
				    		k9EmailUri = cursor.getString(cursor.getColumnIndex("uri"));
							k9EmailDelUri = emailURI + "/" + messageID;
			    			//if (_debug) Log.v(context, "K9Common.getK9MessagesFromIntent() k9EmailDelUri: " + k9EmailDelUri);
				    		emailFoundFlag = true;	
		    			}
			    		if(emailFoundFlag){
			    			break;
			    		}
		    		}
		    		cursor.close();
	    		}else{
	            	final String[] projection = new String[] {"_id", "date", "sender", "subject", "preview", "account", "uri", "delUri"};
	        		final String selection = "date=? AND account=?";
	        		final String[] selectionArgs = new String[] {String.valueOf(timeStamp), accountName};
	        		final String sortOrder = "date DESC";
	    		    cursor = context.getContentResolver().query(
	    		    		Uri.parse(emailURI),
	    		    		projection,
	    		    		selection,
	    					selectionArgs,
	    					sortOrder);
	    		    if(cursor == null){
	    		    	Log.e(context, "K9Common.getK9MessagesFromIntent() Cursor is null. Exiting...");
	    		    	return null;
	    		    }
		    		while(cursor.moveToNext()){
	    				String accountNameTmp = cursor.getString(cursor.getColumnIndex("account"));
		    			long timeStampTmp = cursor.getLong(cursor.getColumnIndex("date"));
		    			//String subjectTmp = cursor.getString(cursor.getColumnIndex("subject"));    	    			
		    			//if (_debug) Log.v(context, "K9Common.getK9MessagesFromIntent() accountNameTmp: " + accountNameTmp + " accountName: " + accountName);
		    			//if (_debug) Log.v(context, "K9Common.getK9MessagesFromIntent() timeStampTmp: " + timeStampTmp + " timeStamp: " + timeStamp);
		    			//if (_debug) Log.v(context, "K9Common.getK9MessagesFromIntent() subjectTmp: " + subjectTmp + " messageSubject: " + messageSubject);
		    			if(timeStampTmp == timeStamp && accountNameTmp.equals(accountName)){
				    		messageID = cursor.getLong(cursor.getColumnIndex("_id"));
				    		messageBody = cursor.getString(cursor.getColumnIndex("preview"));
				    		k9EmailUri = cursor.getString(cursor.getColumnIndex("uri"));
				    		k9EmailDelUri = cursor.getString(cursor.getColumnIndex("delUri"));
				    		emailFoundFlag = true;	
		    			}
			    		if(emailFoundFlag){
			    			break;
			    		}
		    		}
		    		cursor.close();
	    		}
    		}catch(Exception ex){
    			Log.e(context, "K9Common.getK9MessagesFromIntent() CURSOR ERROR: " + ex.toString());
    			if(cursor != null){
    				cursor.close();
    			}
    		}
            if(emailFoundFlag == false){
            	Log.e(context, "K9Common.getK9MessagesFromIntent() No Email Found Matching The Date & Account.");
            	return null;
            }
            if(messageSubject != null && !messageSubject.equals("")){
	    		if(preferences.getBoolean(Constants.K9_INCLUDE_ACCOUNT_NAME_KEY, true)){
					messageBody = "<b>" + context.getString(R.string.account) + ": " + accountName + "<br/>" + messageSubject + "</b><br/>" + messageBody.replace("\n", "<br/>").trim();
	    		}else{
					messageBody = "<b>" + messageSubject + "</b><br/>" + messageBody.replace("\n", "<br/>").trim();
	    		}
			}else{
	    		if(preferences.getBoolean(Constants.K9_INCLUDE_ACCOUNT_NAME_KEY, true)){
	    			messageBody = "<b>" + context.getString(R.string.account) + ": " + accountName + "</b><br/>" + messageBody;
	    		}else{
	    			messageBody = messageBody.replace("\n", "<br/>").trim();
	    		}				
			}
            timeStamp = Common.convertGMTToLocalTime(context, timeStamp, true);
    		Bundle k9ContactInfoBundle = ContactsCommon.getContactsInfoByEmail(context, sentFromAddress);
			//Basic Notification Information.
			k9NotificationBundleSingle.putString(Constants.BUNDLE_SENT_FROM_ADDRESS, sentFromAddress);
			k9NotificationBundleSingle.putString(Constants.BUNDLE_MESSAGE_BODY, messageBody);
			k9NotificationBundleSingle.putLong(Constants.BUNDLE_MESSAGE_ID, messageID);
			k9NotificationBundleSingle.putLong(Constants.BUNDLE_TIMESTAMP, timeStamp);
			k9NotificationBundleSingle.putString(Constants.BUNDLE_K9_EMAIL_URI, k9EmailUri);
			k9NotificationBundleSingle.putString(Constants.BUNDLE_K9_EMAIL_DEL_URI, k9EmailDelUri);
			k9NotificationBundleSingle.putString(Constants.BUNDLE_K9_EMAIL_ACCOUNT_NAME, accountName);
			k9NotificationBundleSingle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_K9);
			k9NotificationBundleSingle.putInt(Constants.BUNDLE_NOTIFICATION_SUB_TYPE, notificationSubType);
    		if(k9ContactInfoBundle != null){
    			//Contact Information.
    			k9NotificationBundleSingle.putLong(Constants.BUNDLE_CONTACT_ID, k9ContactInfoBundle.getLong(Constants.BUNDLE_CONTACT_ID, -1));
    			k9NotificationBundleSingle.putString(Constants.BUNDLE_CONTACT_NAME, k9ContactInfoBundle.getString(Constants.BUNDLE_CONTACT_NAME));
    			k9NotificationBundleSingle.putLong(Constants.BUNDLE_PHOTO_ID, k9ContactInfoBundle.getLong(Constants.BUNDLE_PHOTO_ID, -1));
    			k9NotificationBundleSingle.putString(Constants.BUNDLE_LOOKUP_KEY, k9ContactInfoBundle.getString(Constants.BUNDLE_LOOKUP_KEY));
			}
    		k9NotificationBundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_" + String.valueOf(bundleCount), k9NotificationBundleSingle);
    		k9NotificationBundle.putInt(Constants.BUNDLE_NOTIFICATION_BUNDLE_COUNT, bundleCount);
    		return k9NotificationBundle;
		}catch(Exception ex){
			Log.e(context, "K9Common.getK9MessagesFromIntent() ERROR: " + ex.toString());
			return null;
		}
	}

	/**
	 * Start the intent for any K9 email application to view the email inbox.
	 * 
	 * @param context - The application context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param notificationSubType - The notification sub type.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startK9EmailAppViewInboxActivity(Context context, NotificationActivity notificationActivity, int notificationSubType, int requestCode){
		try{
	        Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);	   
	        String packageName = "com.fsck.k9";
			if(notificationSubType == Constants.NOTIFICATION_TYPE_KAITEN_MAIL){
				packageName = "com.kaitenmail";
			}else if(notificationSubType == Constants.NOTIFICATION_TYPE_K9_MAIL){
				packageName = "com.fsck.k9";
			}else if(notificationSubType == Constants.NOTIFICATION_TYPE_K9_FOR_PURE){
				packageName = "org.koxx.k9ForPureWidget";
			}
            intent.setComponent(new ComponentName(packageName, packageName + ".activity.Accounts"));            
	        notificationActivity.startActivityForResult(intent, requestCode);
	        Common.setInLinkedAppFlag(context, true);
	        return true;
		}catch(Exception ex){
			Log.e(context, "Common.startK9EmailAppViewInboxActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_email_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
	/**
	 * Start the intent for any android K9 email application to send a reply.
	 * 
	 * @param context - The application context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param k9EmailURI - The k9 email uri that is built for the k-9 clients.
	 * @param notificationSubType - The notification sub type.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startK9MailAppReplyActivity(Context context, NotificationActivity notificationActivity, String k9EmailUri, int notificationSubType, int requestCode){
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
			Log.e(context, "K9Common.startK9MailAppReplyActivity() ERROR: " + ex.toString());
			startK9EmailAppViewInboxActivity(context, notificationActivity, notificationSubType, requestCode);
			//Toast.makeText(context, context.getString(R.string.app_email_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
	}

	/**
	 * Delete a K9 Email using it's own URI.
	 * 
	 * @param context - The application context.
	 * @param k9EmailDelUri - The URI provided to delete the email.
	 * @param notificationSubType - The notification sub type.
	 */
	public static void deleteK9Email(Context context, String k9EmailDelUri, int notificationSubType){
		try{
			if(k9EmailDelUri == null || k9EmailDelUri.equals("")){
				Log.e(context, "K9Common.deleteK9Email() k9EmailDelUri == null/empty. Exiting...");
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
			Log.e(context, "K9Common.deleteK9Email() ERROR: " + ex.toString());
			return;
		}
	}
	
//	/**
//	 * Determine if the email has been deleted or not.
//	 * 
//	 * @param context - The application context.
//	 * @param messageID - The id of the message we are looking for.
//	 * @param notificationSubType - The notification sub type.
//	 * @param accountName - The name of the account we are working with.
//	 * 
//	 * @return boolean - Returns true if the email is found, false otherwise.
//	 */
//	public static boolean k9EmailExists(Context context, long messageID, int notificationSubType, String accountName){
//		_debug = Log.getDebug(context);
//		if (_debug) Log.v(context, "K9Common.k9EmailExists() MessageID: " + String.valueOf(messageID) + " Account Name: " + accountName);
//		Cursor cursor = null;
//	    try{
//	    	String packageName = null;
//	    	if(notificationSubType == Constants.NOTIFICATION_TYPE_KAITEN_MAIL){
//				packageName = "com.kaitenmail";
//			}else if(notificationSubType == Constants.NOTIFICATION_TYPE_K9_MAIL){
//				packageName = "com.fsck.k9";
//			}else if(notificationSubType == Constants.NOTIFICATION_TYPE_K9_FOR_PURE){
//				packageName = "org.koxx.k9ForPureWidget";
//			}
//			String emailURI = "content://" + packageName + ".messageprovider/inbox_messages";
//			if(packageName.equals("org.koxx.k9ForPureWidget")){
//				String accountUID = getK9ForPureEmailAccountUID(context, accountName);
//				emailURI = emailURI + "/" + accountUID;
//			}
//        	if (_debug) Log.v(context, "K9Common.k9EmailExists() EmailURI: " + emailURI);
//			final String[] projection = new String[] {"_id", "account"};
//    		final String selection = "_id=? AND account=?";
//    		final String[] selectionArgs = new String[] {String.valueOf(messageID), accountName};
//    		final String sortOrder = null;
//    		
//    		Common.debugReadContentProviderColumns(context, emailURI, null);
//    		
//		    cursor = context.getContentResolver().query(
//		    		Uri.parse(emailURI),
//		    		projection,
//		    		selection,
//					selectionArgs,
//					sortOrder);
//		    if(cursor == null){
//		    	Log.e(context, "K9Common.k9EmailExists() Cursor is null. Exiting...");
//		    	return false;
//		    }
//    		if(cursor.moveToFirst()){
//	    		cursor.close();
//	    		if (_debug) Log.v(context, "K9Common.k9EmailExists() FOUND!");
//    			return true;
//    		}else{
//	    		cursor.close();
//	    		if (_debug) Log.v(context, "K9Common.k9EmailExists() NOT FOUND!");
//    			return false;
//    		}
//		}catch(Exception ex){
//			Log.e(context, "K9Common.k9EmailExists() ERROR: " + ex.toString());
//			if(cursor != null){
//				cursor.close();
//			}
//			return false;
//		}
//	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Parse an email address from a "FROM" email address.
	 * 
	 * @param inputFromAddress - The address we wish to parse.
	 * 
	 * @return String- The email address that we parsed/extracted.
	 */
	private static String parseFromEmailAddress(String inputFromAddress){
		try{
			if(inputFromAddress == null || inputFromAddress.equals("")){
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
			return inputFromAddress;
		}
	}
	
	/**
	 * Get the account UID associated with the provided account name.
	 * 
	 * @param context - The application context.
	 * @param accountName - The name of the account we are working with.
	 * 
	 * @return String - The account UID associated with this account name.
	 */
	private static String getK9ForPureEmailAccountUID(Context context, String accountName){
		String accountUID = null;
		Cursor cursor = null;
        try{
    		final String[] projection = new String[] {"accountName", "accountUuid"};
    		final String selection = null;
    		final String[] selectionArgs = null;
    		final String sortOrder = null;
		    cursor = context.getContentResolver().query(
		    		Uri.parse("content://org.koxx.k9ForPureWidget.messageprovider/accounts/"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
		    if(cursor == null){
		    	Log.e(context, "K9Common.getK9ForPureEmailAccountUID() Cursor is null. Exiting...");
		    	return null;
		    }
    		while(cursor.moveToNext()){  
    			String accountNameTmp = cursor.getString(cursor.getColumnIndex("accountName"));
    			if(accountNameTmp.equals(accountName)){
	    			accountUID = cursor.getString(cursor.getColumnIndex("accountUuid"));
	    			break;
    			}
    		}
    		cursor.close();
		}catch(Exception ex){
			Log.e(context, "K9Common.getK9ForPureEmailAccountUID() CURSOR ERROR: " + ex.toString());
			if(cursor != null){
				cursor.close();
			}
			return null;
		}
        return accountUID;
	}

}
