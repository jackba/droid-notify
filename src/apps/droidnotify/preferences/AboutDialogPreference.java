package apps.droidnotify.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import apps.droidnotify.Log;

/**
 * 
 * @author xqs230cs
 *
 */
public class AboutDialogPreference extends DialogPreference {

	//================================================================================
    // Properties
    //================================================================================

	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * 
	 * @param context
	 * @param attrs
	 */
	public AboutDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (Log.getDebug()) Log.v("AboutDialogPreference.AboutDialogPreference(Context context, AttributeSet attrs)");
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public AboutDialogPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (Log.getDebug()) Log.v("AboutDialogPreference.AboutDialogPreference(Context context, AttributeSet attrs, int defStyle)");
	}
	
	//================================================================================
	// Accessors
	//================================================================================
	  
	//================================================================================
	// Public Methods
	//================================================================================
	  
	//================================================================================
	// Private Methods
	//================================================================================
	
}
