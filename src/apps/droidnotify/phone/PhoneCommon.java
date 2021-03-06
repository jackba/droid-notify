package apps.droidnotify.phone;

import java.lang.reflect.Method;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.widget.Toast;

import apps.droidnotify.NotificationActivity;
import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.contacts.ContactsCommon;
import apps.droidnotify.log.Log;

public class PhoneCommon {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private static boolean _debug = false;
	
	//================================================================================
	// Public Methods
	//================================================================================

	/**
	 * Query the call log and check for any missed calls.
	 * 
	 * @param context - The application context.
	 * 
	 * @return Bundle - Returns a Bundle that contain the missed call notification information.
	 */
	public static Bundle getMissedCalls(Context context){
		_debug = Log.getDebug(context);
		if (_debug) Log.v(context, "PhoneCommon.getMissedCalls()");
		Bundle missedCallNotificationBundle = new Bundle();
		Cursor cursor = null;
		try{
			int bundleCount = 0;
			Boolean missedCallFound = false;
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			String missedCallPreference = preferences.getString(Constants.PHONE_DISMISS_BUTTON_ACTION_KEY, "0");
			final String[] projection = new String[] {
					CallLog.Calls._ID, 
					CallLog.Calls.NUMBER, 
					CallLog.Calls.DATE, 
					CallLog.Calls.TYPE, 
					CallLog.Calls.NEW};
			final String selection = null;
			final String[] selectionArgs = null;
			//final String selection = CallLog.Calls.TYPE + "=? AND " + CallLog.Calls.NEW + "=?";
			//final String[] selectionArgs = new String[]{String.valueOf(CallLog.Calls.MISSED_TYPE), "1"};
			final String sortOrder = CallLog.Calls.DATE + " DESC";
		    cursor = context.getContentResolver().query(
		    		CallLog.Calls.CONTENT_URI,
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
		    if(cursor == null){
		    	if (_debug) Log.v(context, "PhoneCommon.getMissedCalls() Cursor is null. Exiting...");
		    	return null;
		    }
	    	while(cursor.moveToNext()){ 
	    		long callLogID = cursor.getLong(cursor.getColumnIndex(CallLog.Calls._ID));
	    		String callNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
	    		long timeStamp = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
	    		timeStamp = Common.convertGMTToLocalTime(context, timeStamp, true);
	    		int callType = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
	    		int isCallNew = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.NEW));
	    		if(callType == CallLog.Calls.MISSED_TYPE && isCallNew > 0){
	    			Bundle missedCallNotificationBundleSingle = new Bundle();
    				bundleCount++;
    				if (_debug) Log.v(context, "PhoneCommon.getMissedCalls() Missed Call Found: " + callNumber);
    				Bundle missedCallContactInfoBundle = null;
    				if(isPrivateUnknownNumber(context, callNumber)){
    					if (_debug) Log.v(context, "PhoneCommon.getMissedCalls() Is a private or unknown number.");
    				}else{
    					missedCallContactInfoBundle = ContactsCommon.getContactsInfoByPhoneNumber(context, callNumber);
    				}				
					//Basic Notification Information.
					missedCallNotificationBundleSingle.putLong(Constants.BUNDLE_CALL_LOG_ID, callLogID);
					missedCallNotificationBundleSingle.putString(Constants.BUNDLE_SENT_FROM_ADDRESS, callNumber);
					missedCallNotificationBundleSingle.putLong(Constants.BUNDLE_TIMESTAMP, timeStamp);
					missedCallNotificationBundleSingle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_PHONE);
    				if(missedCallContactInfoBundle != null){
    	    			//Contact Information.
    					missedCallNotificationBundleSingle.putLong(Constants.BUNDLE_CONTACT_ID, missedCallContactInfoBundle.getLong(Constants.BUNDLE_CONTACT_ID, -1));
    					missedCallNotificationBundleSingle.putString(Constants.BUNDLE_CONTACT_NAME, missedCallContactInfoBundle.getString(Constants.BUNDLE_CONTACT_NAME));
    					missedCallNotificationBundleSingle.putLong(Constants.BUNDLE_PHOTO_ID, missedCallContactInfoBundle.getLong(Constants.BUNDLE_PHOTO_ID, -1));
    					missedCallNotificationBundleSingle.putString(Constants.BUNDLE_LOOKUP_KEY, missedCallContactInfoBundle.getString(Constants.BUNDLE_LOOKUP_KEY));
    				}
    				missedCallNotificationBundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_" + String.valueOf(bundleCount), missedCallNotificationBundleSingle);
    				if(missedCallPreference.equals(Constants.PHONE_GET_LATEST)){
    					if (_debug) Log.v(context, "PhoneCommon.getMissedCalls() Missed call found. Breaking...");
    					break;
    				}
    				missedCallFound = true;
    			}else{
    				if(missedCallPreference.equals(Constants.PHONE_GET_RECENT)){
    					if (_debug) Log.v(context, "PhoneCommon.getMissedCalls() Found First Non-Missed Call. Breaking...");
    					break;
    				}
    			}
	    		if(!missedCallFound){
	    			if (_debug) Log.v(context, "PhoneCommon.getMissedCalls() Missed Call Not Found. Exiting...");
	    			cursor.close();
	    			return null;
	    		}
	    	}
	    	cursor.close();
			missedCallNotificationBundle.putInt(Constants.BUNDLE_NOTIFICATION_BUNDLE_COUNT, bundleCount);
		    return missedCallNotificationBundle;
		}catch(Exception ex){
			Log.e(context, "PhoneCommon.getMissedCalls() ERROR: " + ex.toString());
			if(cursor != null){
				cursor.close();
			}
			return null;
		}
	}
	
	/**
	 * Delete a call long entry.
	 * 
	 * @param context - The current context of this Activity.
	 * @param callLogID - The call log ID that we want to delete.
	 * 
	 * @return boolean - Returns true if the call log entry was deleted successfully.
	 */
	public static boolean deleteFromCallLog(Context context, long callLogID){
		try{
			if(callLogID < 0){
				Log.e(context, "PhoneCommon.deleteFromCallLog() Call Log ID < 0. Exiting...");
				return false;
			}
			final String selection = CallLog.Calls._ID + "=?";
			final String[] selectionArgs = new String[]{String.valueOf(callLogID)};
			context.getContentResolver().delete(
					CallLog.Calls.CONTENT_URI,
					selection, 
					selectionArgs);
			return true;
		}catch(Exception ex){
			Log.e(context, "PhoneCommon.deleteFromCallLog() ERROR: " + ex.toString());
			return false;
		}
	}
	
	/**
	 * Mark a call log entry as being viewed.
	 * 
	 * @param context - The current context of this Activity.
	 * @param callLogID - The call log ID that we want to delete.
	 * 
	 * @return boolean - Returns true if the call log entry was updated successfully.
	 */
	public static boolean setCallViewed(Context context, long callLogID, boolean isViewed){
		try{
			if(callLogID < 0){
				Log.e(context, "PhoneCommon.setCallViewed() Call Log ID < 0. Exiting...");
				return false;
			}
			ContentValues contentValues = new ContentValues();
			if(isViewed){
				contentValues.put(CallLog.Calls.NEW, 0);
			}else{
				contentValues.put(CallLog.Calls.NEW, 1);
			}
			final String selection = CallLog.Calls._ID + "=?";
			final String[] selectionArgs = new String[]{String.valueOf(callLogID)};
			context.getContentResolver().update(
					CallLog.Calls.CONTENT_URI,
					contentValues,
					selection, 
					selectionArgs);
			return true;
		}catch(Exception ex){
			Log.e(context, "PhoneCommon.setCallViewed() ERROR: " + ex.toString());
			return false;
		}
	}
	
	/**
	 * Place a phone call.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param phoneNumber - The phone number we want to send a message to.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the application can be launched.
	 */
	public static boolean makePhoneCall(Context context, NotificationActivity notificationActivity, String phoneNumber, int requestCode){
		try{
			phoneNumber = PhoneCommon.removePhoneNumberFormatting(phoneNumber);
			if(phoneNumber == null){
				Toast.makeText(context, context.getString(R.string.app_android_phone_number_format_error), Toast.LENGTH_LONG).show();
				Common.setInLinkedAppFlag(context, false);
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_CALL);
	        intent.setData(Uri.parse("tel:" + phoneNumber));
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        notificationActivity.startActivityForResult(intent, requestCode);
	        Common.setInLinkedAppFlag(context, true);
		    return true;
		}catch(Exception ex){
			Log.e(context, "PhoneCommon.makePhoneCall() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_phone_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
	/**
	 * Start the intent to view the phones call log.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startCallLogViewActivity(Context context, NotificationActivity notificationActivity, int requestCode){
		try{
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setType("vnd.android.cursor.dir/calls");
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
			Common.setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			Log.e(context, "PhoneCommon.startCallLogViewActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_call_log_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
	/**
	 * Determines if the incoming number is a Private or Unknown number.
	 * 
	 * @param incomingNumber - The incoming phone number.
	 * 
	 * @return boolean - Returns true if the number is a Private number or Unknown number.
	 */
	public static boolean isPrivateUnknownNumber(Context context, String incomingNumber){
		try{
			if(incomingNumber == null){
				return false;
			}else if(incomingNumber.length() > 4){
				return false;
			}
			int convertedNumber = Integer.parseInt(incomingNumber);
			if(convertedNumber < 1) return true;
		}catch(Exception ex){
			Log.e(context, "PhoneCommon.isPrivateUnknownNumber() Integer Parse Error");
			return false;
		}
		return false;
	}
	
	/**
	 * Cancel the stock missed call notification.
	 * 
	 * @return boolean - Returns true if the stock missed call notification was cancelled.
	 */
	@SuppressWarnings("rawtypes")
	public static boolean clearStockMissedCallNotification(Context context){
		try{
			if(Common.getDeviceAPILevel() == android.os.Build.VERSION_CODES.FROYO){
				try{
			        Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
			        Method getServiceMethod = serviceManagerClass.getMethod("getService", String.class);
			        Object phoneService = getServiceMethod.invoke(null, "phone");
			        Class ITelephonyClass = Class.forName("com.android.internal.telephony.ITelephony");
			        Class<?> ITelephonyStubClass = null;
			        for(Class clazz : ITelephonyClass.getDeclaredClasses()){
			            if (clazz.getSimpleName().equals("Stub")){
			                ITelephonyStubClass = clazz;
			                break;
			            }
			        }
			        if (ITelephonyStubClass != null) {
			            Class IBinderClass = Class.forName("android.os.IBinder");
			            Method asInterfaceMethod = ITelephonyStubClass.getDeclaredMethod("asInterface", IBinderClass);
			            Object iTelephony = asInterfaceMethod.invoke(null, phoneService);
			            if (iTelephony != null){
			                Method cancelMissedCallsNotificationMethod = iTelephony.getClass().getMethod("cancelMissedCallsNotification");
			                cancelMissedCallsNotificationMethod.invoke(iTelephony);
			            }else{
			            	Log.e(context, "Telephony service is null, can't call cancelMissedCallsNotification.");
			            }
			        }else{
			            if (_debug) Log.v(context, "Unable to locate ITelephony.Stub class.");
			        }
			    }catch (Exception ex){
			    	Log.e(context, "PhoneCommon.clearStockMissedCallNotification() REFLECTION ERROR: " + ex.toString());
			    }
			}
			//Send broadcast to NotiGo (If installed)
			Intent notiGoBroadcastIntent = new Intent();
			notiGoBroadcastIntent.setAction("thinkpanda.notigo.CLEAR_MISSED_CALL");
	        context.sendBroadcast(notiGoBroadcastIntent);
	        return true;
	    }catch (Exception ex){
	    	Log.e(context, "PhoneCommon.clearStockMissedCallNotification() ERROR: " + ex.toString());
	    	return false;
	    }
	}
	
	/**
	 * Function to format phone numbers.
	 * 
	 * @param context - The current context of this Activity.
	 * @param inputPhoneNumber - Phone number to be formatted.
	 * 
	 * @return String - Formatted phone number string.
	 */
	public static String formatPhoneNumber(Context context, String inputPhoneNumber){
		try{
			if(inputPhoneNumber == null){
				Log.e(context, "PhoneCommon.formatPhoneNumber() InputPhoneNumber is null. exiting...");
				return null;
			}
			if(inputPhoneNumber.equals(context.getString(R.string.private_number_text))){
				return inputPhoneNumber;
			}
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			inputPhoneNumber = PhoneCommon.removePhoneNumberFormatting(inputPhoneNumber);
			StringBuilder outputPhoneNumber = new StringBuilder("");		
			int phoneNumberFormatPreference = Integer.parseInt(preferences.getString(Constants.PHONE_NUMBER_FORMAT_KEY, Constants.PHONE_NUMBER_FORMAT_DEFAULT));
			String numberSeparator = "-";
			if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_7 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_8 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_9 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_10 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_16 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_20 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_23){
				numberSeparator = ".";
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_11 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_12 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_13 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_14 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_17 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_18 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_21 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_24){
				numberSeparator = " ";
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_5){
				numberSeparator = "";
			}
			if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_1 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_7 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_11){
				if(inputPhoneNumber.length() >= 10){
					//Format ###-###-#### (e.g.123-456-7890)
					//Format ###-###-#### (e.g.123.456.7890)
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 4, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 7, inputPhoneNumber.length() - 4));
					outputPhoneNumber.insert(0, numberSeparator);
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 7));
					}else{
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 10, inputPhoneNumber.length() - 7));
						outputPhoneNumber.insert(0, numberSeparator);
						if(preferences.getBoolean(Constants.PHONE_NUMBER_FORMAT_10_DIGITS_ONLY_KEY , false)){
							outputPhoneNumber.insert(0, "0");
						}else{
							outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 10));
						}
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_2 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_8 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_12){
				if(inputPhoneNumber.length() >= 10){
					//Format ##-###-##### (e.g.12-345-67890)
					//Format ##.###.##### (e.g.12.345.67890)
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 5, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 8, inputPhoneNumber.length() - 5));
					outputPhoneNumber.insert(0, numberSeparator);
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 8));
					}else{
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 10, inputPhoneNumber.length() - 8));
						outputPhoneNumber.insert(0, numberSeparator);
						if(preferences.getBoolean(Constants.PHONE_NUMBER_FORMAT_10_DIGITS_ONLY_KEY , false)){
							outputPhoneNumber.insert(0, "0");
						}else{
							outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 10));
						}
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_3 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_9 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_13){
				if(inputPhoneNumber.length() >= 10){
					//Format ##-###-###### (e.g.01-234-567890)
					//Format ##.###.###### (e.g.01.234.567890)
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 6, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 9, inputPhoneNumber.length() - 6));
					outputPhoneNumber.insert(0, numberSeparator);
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 9));
					}else if(inputPhoneNumber.length() == 11){
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 9));
					}else{
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 11, inputPhoneNumber.length() - 9));
						outputPhoneNumber.insert(0, numberSeparator);
						if(preferences.getBoolean(Constants.PHONE_NUMBER_FORMAT_10_DIGITS_ONLY_KEY , false)){
							outputPhoneNumber.insert(0, "0");
						}else{
							outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 11));
						}
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_4 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_10 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_14){
				if(inputPhoneNumber.length() >= 10){
					//Format ##-##-##-##-## (e.g.12-34-56-78-90)
					//Format ##.##.##.##.## (e.g.12.34.56.78.90)
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 2, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 4, inputPhoneNumber.length() - 2));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 6, inputPhoneNumber.length() - 4));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 8, inputPhoneNumber.length() - 6));
					outputPhoneNumber.insert(0, numberSeparator);
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0,inputPhoneNumber.substring(0, inputPhoneNumber.length() - 8));
					}else{
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 10, inputPhoneNumber.length() - 8));
						outputPhoneNumber.insert(0, numberSeparator);
						if(preferences.getBoolean(Constants.PHONE_NUMBER_FORMAT_10_DIGITS_ONLY_KEY , false)){
							outputPhoneNumber.insert(0, "0");
						}else{
							outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 10));
						}
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_15 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_16 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_17){
				if(inputPhoneNumber.length() >= 10){
					//Format ###-####-#### (e.g.012-3456-7890)
					//Format ###.####.#### (e.g.012.3456.7890)
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 4, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 8, inputPhoneNumber.length() - 4));
					outputPhoneNumber.insert(0, numberSeparator);
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 8));
					}else{
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 10, inputPhoneNumber.length() - 8));
						outputPhoneNumber.insert(0, numberSeparator);
						if(preferences.getBoolean(Constants.PHONE_NUMBER_FORMAT_10_DIGITS_ONLY_KEY , false)){
							outputPhoneNumber.insert(0, "0");
						}else{
							outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 10));
						}
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
			}else if( phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_19 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_20 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_21){
				if(inputPhoneNumber.length() >= 10){
					//Format ####-####### (e.g.0123-4567890)
					//Format ####.####### (e.g.0123.4567890)
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 7, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0, numberSeparator);
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 7));
					}else{
						if(preferences.getBoolean(Constants.PHONE_NUMBER_FORMAT_10_DIGITS_ONLY_KEY , false)){
							outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 10, inputPhoneNumber.length() - 7));
							outputPhoneNumber.insert(0, "0");
						}else{
							outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 7));
						}
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_22 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_23 || 
					phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_24){
				if(inputPhoneNumber.length() >= 10){
					//Format ####-###-#### (e.g.1234-567-890)
					//Format ####.###.#### (e.g.1234.567-890)
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 3, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 6, inputPhoneNumber.length() - 3));
					outputPhoneNumber.insert(0, numberSeparator);
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 6));
					}else{
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 10, inputPhoneNumber.length() - 6));
						outputPhoneNumber.insert(0, numberSeparator);
						if(preferences.getBoolean(Constants.PHONE_NUMBER_FORMAT_10_DIGITS_ONLY_KEY , false)){
							outputPhoneNumber.insert(0, "0");
						}else{
							outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 10));
						}
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_5){
				//Format ########## (e.g.1234567890)
				outputPhoneNumber.append(inputPhoneNumber);
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_18){
				//Format (####) ####### (e.g.(0123) 4567890)
				outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 7, inputPhoneNumber.length()));
				outputPhoneNumber.insert(0, numberSeparator);
				outputPhoneNumber.insert(0, ") ");
				outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 7));
				outputPhoneNumber.insert(0, "(");
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_6){
				if(inputPhoneNumber.length() >= 10){
					//Format (###) ###-#### (e.g.(123) 456-7890)
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 4, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 7, inputPhoneNumber.length() - 4));
					outputPhoneNumber.insert(0, ") ");
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 7));
						outputPhoneNumber.insert(0, "(");
					}else{
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 10, inputPhoneNumber.length() - 7));
						outputPhoneNumber.insert(0, " (");
						if(preferences.getBoolean(Constants.PHONE_NUMBER_FORMAT_10_DIGITS_ONLY_KEY , false)){
							outputPhoneNumber.insert(0, "0");
						}else{
							outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 10));
						}
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
			}else{
				outputPhoneNumber.append(inputPhoneNumber);
			}
			return outputPhoneNumber.toString();
		}catch(Exception ex){
			Log.e(context, "PhoneCommon.formatPhoneNumber() ERROR: " + ex.toString());
			return inputPhoneNumber;
		}
	}
	
	/**
	 * Remove all non-numeric items from the phone number.
	 * 
	 * @param phoneNumber - String of original phone number.
	 * 
	 * @return String - String of phone number with no formatting.
	 */
	public static String removePhoneNumberFormatting(String phoneNumber){
		//if (_debug) Log.v(context, "PhoneCommon.removePhoneNumberFormatting()");
		try{
			if(phoneNumber == null || phoneNumber.length() < 1){
				return phoneNumber;
			}
			phoneNumber = phoneNumber.replace(" ", "");
			phoneNumber = phoneNumber.replace("-", "");
			phoneNumber = phoneNumber.replace(".", "");
			phoneNumber = phoneNumber.replace(",", "");
			phoneNumber = phoneNumber.replace("(", "");
			phoneNumber = phoneNumber.replace(")", "");
			phoneNumber = phoneNumber.replace("/", "");
			phoneNumber = phoneNumber.replace("x", "");
			phoneNumber = phoneNumber.replace("X", "");
			return phoneNumber.trim();
		}catch(Exception ex){
			return phoneNumber;
		}
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
}
