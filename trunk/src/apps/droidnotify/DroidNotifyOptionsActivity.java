package apps.droidnotify;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

/**
 * 
 * @author csevigny
 *
 */
public class DroidNotifyOptionsActivity extends Activity {
	
	//================================================================================
    // Properties
    //================================================================================

	private Handler handler = new Handler();
	private Context _context;

	//================================================================================
	// Constructors
	//================================================================================
	
	//================================================================================
	// Accessors
	//================================================================================

	/**
	 * Set the context property.
	 */
	public void setContext(Context context) {
		if (Log.getDebug()) Log.v("DroidNotifyOptionsActivity.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 */
	public Context getContext() {
		if (Log.getDebug()) Log.v("DroidNotifyOptionsActivity.getContext()");
	    return _context;
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
     *  Called when the activity is first created. 
     *  
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Log.getDebug()) Log.v("DroidNotifyOptionsActivity.onCreate()");
        setContext(getApplicationContext());
        setContentView(R.layout.main);
        registerCallLogContentObserver();
    }
	  
	//================================================================================
	// Private Methods
	//================================================================================
    
    /**
     * 
     */
    private void registerCallLogContentObserver(){
    	if (Log.getDebug()) Log.v("DroidNotifyOptionsActivity.registerCallLogContentObserver()");
    	Context context = getContext();
    	ContentResolver contentresolver = context.getContentResolver();
    	contentresolver.registerContentObserver(
	            android.provider.CallLog.Calls.CONTENT_URI, 
	            true,
	            new CallLogContentObserver(context, handler));
    }
}