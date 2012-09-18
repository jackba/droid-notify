package apps.droidnotify.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import apps.droidnotify.receivers.ReminderDBManagementReceiver;
import apps.droidnotify.db.DBConstants;
import apps.droidnotify.log.Log;
import apps.droidnotify.db.SQLiteHelperReminder;

/**
 * This class is a collection of methods that are used to manage reminders.
 * 
 * @author Camille Sévigny
 */
public class ReminderCommon {
	
	//================================================================================
    // Constants
    //================================================================================
	
	private final static long MILLISECONDS_PER_DAY = 1000L * 60 * 60 * 24;
	
	//================================================================================
    // Properties
    //================================================================================
	
	private static boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Insert the specified value into the reminder DB.
	 * 
	 * @param context - The application context.
	 * @param action - The action of the reminder.
	 * @param dismissed - Whether or not this reminder is dismissed or not.
	 * 
	 * @return Reminder - The reminder item that was inserted.
	 */
	public static boolean insertValue(Context context, String intentAction, boolean dismissed){		
		_debug = Log.getDebug();
		if(_debug) Log.v("ReminderCommon.insertValue()");
        try{
			int dismissedInt = dismissed ? 1 : 0;
			//Check the DB to ensure that this reminder was not already added.
			Cursor cursor = null;
	        try{
	    		final String[] projection = new String[]{DBConstants.COLUMN_ID, DBConstants.COLUMN_CREATED, DBConstants.COLUMN_ACTION, DBConstants.COLUMN_DISMISSED};
	    		final String selection = DBConstants.COLUMN_ACTION + "=?";
	    		final String[] selectionArgs = new String[]{intentAction};
	    		final String sortOrder = null;
			    cursor = context.getContentResolver().query(
			    		DBConstants.CONTENT_URI_REMINDER,
			    		projection,
			    		selection,
						selectionArgs,
						sortOrder);
			    if(cursor ==  null){
			    	if(_debug) Log.v("ReminderCommon.insertValue() Currsor is null.");
			    	return false;
			    }
			    if(cursor.moveToFirst()){
			    	//This reminder has already been added.
			    	if(_debug) Log.v("ReminderCommon.insertValue() Reminder action has already been added. Returning existing entry.");
			    	cursor.close();
			    	//Return existing row of data.
		        	return true;
			    }
			    cursor.close();
			}catch(Exception ex){
				Log.e("ReminderCommon.insertValue() Check If Entry Exists ERROR: " + ex.toString());
				if(cursor != null){
					cursor.close();
				}
				return false;
			}
	        //Insert the new reminder into the db.
        	ContentValues contentValues = new ContentValues();
			contentValues.put(DBConstants.COLUMN_CREATED, System.currentTimeMillis());
			contentValues.put(DBConstants.COLUMN_ACTION, intentAction);
			contentValues.put(DBConstants.COLUMN_DISMISSED, dismissedInt);
        	context.getContentResolver().insert(DBConstants.CONTENT_URI_REMINDER, contentValues);
        	return true;
        }catch(IllegalArgumentException iae){
        	//Create the database if it's not already been created.
        	@SuppressWarnings("unused")
			SQLiteHelperReminder reminderDBHelper = new SQLiteHelperReminder(context);
        	//Try to execute the above command again.
        	return insertValue(context, intentAction, dismissed);
		}catch(Exception ex){
			Log.e("ReminderCommon.insertValue() ERROR: " + ex.toString());
			return false;
		}
	}

	/**
	 * Update the specified value into the reminder DB.
	 * 
	 * @param context - The application context.
	 * @param action - The action of the reminder.
	 * @param dismissed - Whether or not this reminder is dismissed or not.
	 * 
	 * @return Reminder - The reminder item that was updated.
	 */
	public static boolean updateValue(Context context, String intentAction, boolean dismissed){		
		_debug = Log.getDebug();
		if(_debug) Log.v("ReminderCommon.updateValue()");
        try{
        	ContentValues contentValues = new ContentValues();
        	if(intentAction == null){
        		if (_debug) Log.v("ReminderCommon.updateValue() Reminder Action is null. Exiting...");
        		return false;
        	}        	
			int dismissedInt = dismissed ? 1 : 0;
			contentValues.put(DBConstants.COLUMN_DISMISSED, dismissedInt);
			String updateWhere = DBConstants.COLUMN_ACTION + "=?";
        	context.getContentResolver().update(DBConstants.CONTENT_URI_REMINDER, contentValues, updateWhere, new String[]{intentAction});
        	return true;
        }catch(IllegalArgumentException iae){
        	//Create the database if it's not already been created.
        	@SuppressWarnings("unused")
			SQLiteHelperReminder reminderDBHelper = new SQLiteHelperReminder(context);
        	//Try to execute the above command again.
        	return updateValue(context, intentAction, dismissed);
		}catch(Exception ex){
			Log.e("ReminderCommon.updateValue() ERROR: " + ex.toString());
			return false;
		}
	}
	
	/**
	 * Search the reminder DB for the dismissed value of the reminder.
	 * 
	 * @param context - The application context.
	 * @param action - The action of the reminder.
	 * 
	 * @return boolean - Returns the dismissed value of the reminder.
	 */
	public static boolean isDismissed(Context context, String intentAction){		
		_debug = Log.getDebug();
		_debug = Log.getDebug();
		if (_debug) Log.v("ReminderCommon.isDismissed() Intent Action: " + intentAction);
    	try{    		
    		if(intentAction == null){
				if (_debug) Log.v("ReminderCommon.isDismissed() Reminder Action is null. Exiting...");
				return false;
			}
    		Cursor cursor = null;
            try{
        		final String[] projection = new String[]{DBConstants.COLUMN_ID, DBConstants.COLUMN_ACTION, DBConstants.COLUMN_DISMISSED};
	    		final String selection = DBConstants.COLUMN_ACTION + "=?";
	    		final String[] selectionArgs = new String[]{intentAction};
        		final String sortOrder = null;
    		    cursor = context.getContentResolver().query(
    		    		DBConstants.CONTENT_URI_REMINDER,
    		    		projection,
    		    		selection,
    					selectionArgs,
    					sortOrder);
			    if(cursor ==  null){
			    	if(_debug) Log.v("ReminderCommon.isDismissed() Currsor is null. Exiting...");
			    	return false;
			    }
			    if(cursor.moveToFirst()){
    		    	long dismissedInt = cursor.getInt(cursor.getColumnIndex(DBConstants.COLUMN_DISMISSED));
    		    	if (_debug) Log.v("ReminderCommon.isDismissed() Dismissed: " + dismissedInt);
    		    	cursor.close();
    		    	return dismissedInt == 1 ? true : false;  		    	
    		    }else{
    		    	if (_debug) Log.v("ReminderCommon.isDismissed() Intent Action not found. Exiting...");
    		    	cursor.close();
    		    	return false;
    		    }
            }catch(IllegalArgumentException iae){
            	//Create the database if it's not already been created.
            	@SuppressWarnings("unused")
    			SQLiteHelperReminder reminderDBHelper = new SQLiteHelperReminder(context);
            	//Try to execute the above command again.
            	return isDismissed(context, intentAction);
    		}catch(Exception ex){
    			Log.e("ReminderCommon.isDismissed() DB Search ERROR: " + ex.toString());
    			if(cursor != null){
    				cursor.close();
    			}
    		}   		
    		return false;
    	}catch(Exception ex){
    		Log.e("ReminderCommon.isDismissed() ERROR: " + ex.toString());
    	    return false;
    	}
	}
	
	/**
	 * Clean old reminders from the reminder DB.
	 * 
	 * @return boolean - Returns true if the operation was successful.
	 */
	public static boolean cleanDB(Context context){
		_debug = Log.getDebug();
		if(_debug) Log.v("ReminderCommon.cleanDB()");
        try{
			String deleteWhere = DBConstants.COLUMN_CREATED + "<?";
			String[] selectionArgs = new String[]{String.valueOf(System.currentTimeMillis() - (MILLISECONDS_PER_DAY * 3))};
        	context.getContentResolver().delete(DBConstants.CONTENT_URI_REMINDER, deleteWhere, selectionArgs);
        	return true;
        }catch(IllegalArgumentException iae){
        	//Create the database if it's not already been created.
        	@SuppressWarnings("unused")
			SQLiteHelperReminder reminderDBHelper = new SQLiteHelperReminder(context);
        	//Try to execute the above command again.
        	return cleanDB(context);
		}catch(Exception ex){
			Log.e("ReminderCommon.cleanDB() ERROR: " + ex.toString());
			return false;
		}
	}
	
	/**
	* Start the reminder DB cleanup recurring alarm.
	* 
	* @param context - The application context.
	* @param alarmStartTime - The time to start the alarm.
	*/
	public static void startReminderDBManagementAlarmManager(Context context, long alarmStartTime){
		_debug = Log.getDebug();
		if (_debug) Log.v("ReminderCommon.startReminderDBManagementAlarmManager()");
		try{
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, ReminderDBManagementReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime, MILLISECONDS_PER_DAY, pendingIntent);
		}catch(Exception ex){
			Log.e("ReminderCommon.startReminderDBManagementAlarmManager() ERROR: " + ex.toString());
		}
	}
	
}
