package apps.droidnotify.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

public class QuickReplyPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
	//================================================================================
    // Properties
    //================================================================================

    private boolean _debug = false;
    private Context _context = null;
    private SharedPreferences _preferences = null;
	
	//================================================================================
	// Public Methods
	//================================================================================

	/**
	 * Called when the activity is created. Set up views and buttons.
	 * 
	 * @param bundle - Activity bundle.
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle bundle){
	    super.onCreate(bundle);
	    _debug = Log.getDebug();
	    if (_debug) Log.v("QuickReplyPreferenceActivity.onCreate()");
	    _context = this;
	    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	    Common.setApplicationLanguage(_context, this);
	    this.addPreferencesFromResource(R.xml.quick_reply_preferences);
	    this.setContentView(R.layout.quick_reply_preferences);
	    updateQuickReplySignaturePreference();
	}
    
	/**
	 * When a SharedPreference is changed this registered function is called.
	 * 
	 * @param sharedPreferences - The Preference object who's key was changed.
	 * @param key - The String value of the preference Key who's preference value was changed.
	 */
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (_debug) Log.v("QuickReplyPreferenceActivity.onSharedPreferenceChanged() Key: " + key);
		if(key.equals(Constants.QUICK_REPLY_SIGNATURE_KEY)){
			updateQuickReplySignaturePreference();
		}
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onResume(){
	    if(_debug) Log.v("QuickReplyPreferenceActivity.onResume()");
	    super.onResume();
	    _preferences.registerOnSharedPreferenceChangeListener(this);
	}
	  
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
	@Override
	protected void onPause(){
	    if(_debug) Log.v("QuickReplyPreferenceActivity.onPause()");
	    super.onPause();
	    _preferences.unregisterOnSharedPreferenceChangeListener(this);
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Update the summary of the Signature preference.
	 */
	private void updateQuickReplySignaturePreference(){		
		@SuppressWarnings("deprecation")
		EditTextPreference signatureEditTextPreference = (EditTextPreference)findPreference(Constants.QUICK_REPLY_SIGNATURE_KEY);
		String signature = _preferences.getString(Constants.QUICK_REPLY_SIGNATURE_KEY, _context.getString(R.string.quick_reply_default_signature));
		if(signature.contains("\n")){
			signature = signature.replace("\n", "");
			_preferences.registerOnSharedPreferenceChangeListener(this);
			SharedPreferences.Editor editor = _preferences.edit();
        	editor.putString(Constants.QUICK_REPLY_SIGNATURE_KEY, signature);
            editor.commit();
    	    _preferences.unregisterOnSharedPreferenceChangeListener(this);
		}
		if(signatureEditTextPreference != null) signatureEditTextPreference.setSummary(signature);
	}
	
}
