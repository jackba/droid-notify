package apps.droidnotify;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

/**
 * 
 * @author Camille Sevigny
 *
 */
public class CallLogContentObserver extends ContentObserver {

	//================================================================================
    // Properties
    //================================================================================
	
    public final int INCOMING_CALL_TYPE = android.provider.CallLog.Calls.INCOMING_TYPE;
    public final int OUTGOING_CALL_TYPE = android.provider.CallLog.Calls.OUTGOING_TYPE;
    public final int MISSED_CALL_TYPE = android.provider.CallLog.Calls.MISSED_TYPE;
    
	public Context _context;
	
	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * 
	 */
    public CallLogContentObserver(Context context, Handler handler) {
        super(handler);
        if (Log.getDebug()) Log.v("CallLogContentObserver.CallLogContentObserver()");
        _context = context;
    }
	
	//================================================================================
	// Accessors
	//================================================================================
	  
	//================================================================================
	// Public Methods
	//================================================================================
    
    /**
     * 
     */
    @Override
    public boolean deliverSelfNotifications() {
    	if (Log.getDebug()) Log.v("CallLogContentObserver.deliverSelfNotifications()");
        return true;
    }

    /**
     * 
     */
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        if (Log.getDebug()) Log.v("CallLogContentObserver.onChange()");
		final String[] projection = null;
		final String selection = null;
		final String[] selectionArgs = null;
		final String sortOrder = "DATE DESC";
	    Cursor cursor = _context.getContentResolver().query(
	    		Uri.parse("content://call_log/calls"),
	    		projection,
	    		selection,
				selectionArgs,
				sortOrder);
	    if (cursor != null) {
	    	while (cursor.moveToNext()) { 
	    		String callNumber = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
	    		String callDate = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE));
	    		String callType = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE));
	    		String isCallNew = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NEW));
	    		if (Log.getDebug()) Log.v("CallLogContentObserver.onChange() Checking Call: " + callNumber + " Received At: " + callDate + " Call Type: " + callType + " Is Call New? " + isCallNew);
	    		if(Integer.parseInt(callType) == MISSED_CALL_TYPE && Integer.parseInt(isCallNew) > 0){
    				if (Log.getDebug()) Log.v("CallLogContentObserver.onChange() Missed Call Found: " + callNumber);
    				//Start Notification
    			}else{
    				break;
    			}
	    	}
	    	cursor.close();
	    }    	
    }
	  
	//================================================================================
	// Private Methods
	//================================================================================
	
}
