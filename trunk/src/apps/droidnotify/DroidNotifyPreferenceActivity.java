package apps.droidnotify;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.TextView;

/**
 * 
 * @author xqs230cs
 *
 */
public class DroidNotifyPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	//================================================================================
    // Constants
    //================================================================================
	final String APP_ENABLED_KEY = "app_enabled_settings";
	
	//================================================================================
    // Properties
    //================================================================================

	private Context _context;
	private CheckBoxPreference appEnabledCheckbox = null;

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
		if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 */
	public Context getContext() {
		if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.getContext()");
	    return _context;
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.onCreate()");	    
	    addPreferencesFromResource(R.xml.preferences);
	    appEnabledCheckbox = (CheckBoxPreference)getPreferenceScreen().findPreference(APP_ENABLED_KEY);

	    // Register the SharedPreferenceChanged listener.            
        //getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
      
	    
	    
//	    // Button 1 preference
//	    button1 =
//	      (ButtonListPreference) findPreference(getString(R.string.pref_button1_key));
//	    button1.refreshSummary();
//	    button1.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
//	      public boolean onPreferenceChange(Preference preference, Object newValue) {
//	        quickReplyPref.setChecked(
//	            isQuickReplyActive((String) newValue, button2.getValue(), button3.getValue()));
//	        updateReplyTypePref((String) newValue, button2.getValue(), button3.getValue());
//	        return true;
//	      }
//	    });

//	    // Button 2 preference
//	    button2 =
//	      (ButtonListPreference) findPreference(getString(R.string.pref_button2_key));
//	    button2.refreshSummary();
//	    button2.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
//	      public boolean onPreferenceChange(Preference preference, Object newValue) {
//	        quickReplyPref.setChecked(
//	            isQuickReplyActive((String) newValue, button1.getValue(), button3.getValue()));
//	        updateReplyTypePref((String) newValue, button1.getValue(), button3.getValue());
//	        return true;
//	      }
//	    });

//	    // Button 3 preference
//	    button3 =
//	      (ButtonListPreference) findPreference(getString(R.string.pref_button3_key));
//	    button3.refreshSummary();
//	    button3.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
//	      public boolean onPreferenceChange(Preference preference, Object newValue) {
//	        quickReplyPref.setChecked(
//	            isQuickReplyActive((String) newValue, button1.getValue(), button2.getValue()));
//	        updateReplyTypePref((String) newValue, button1.getValue(), button2.getValue());
//	        return true;
//	      }
//	    });

//	    // Quick Reply checkbox preference
//	    quickReplyPref =
//	      (QuickReplyCheckBoxPreference) findPreference(getString(R.string.pref_quickreply_key));

//	    quickReplyPref.setChecked(
//	        isQuickReplyActive(button1.getValue(), button2.getValue(), button3.getValue()));
//
//	    // Refresh reply type pref
//	    updateReplyTypePref(button1.getValue(), button2.getValue(), button3.getValue());
//
//	    /*
//	     * This is a really manual way of dealing with this, but I didn't think it was worth
//	     * spending the time to make it more generic.  This will basically look through the active
//	     * buttons and switch any Reply buttons to Quick Reply buttons when enabling and the opposite
//	     * when disabling.
//	     */
//	    quickReplyPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
//	      public boolean onPreferenceChange(Preference preference, Object newValue) {
//	        int button1val = Integer.valueOf(button1.getValue());
//	        int button2val = Integer.valueOf(button2.getValue());
//	        int button3val = Integer.valueOf(button3.getValue());
//
//	        int count = 0;
//
//	        if (button1.isReplyButton()) count++;
//	        if (button2.isReplyButton()) count++;
//	        if (button3.isReplyButton()) count++;
//
//	        if (count > 1) {
//	          Toast.makeText(SmsPopupConfigActivity.this,
//	              R.string.pref_quickreply_bothreplybuttons, Toast.LENGTH_LONG).show();
//	          return false;
//	        } else if (count == 0) {
//	          Toast.makeText(SmsPopupConfigActivity.this,
//	              R.string.pref_quickreply_noreplybuttons, Toast.LENGTH_LONG).show();
//	          return false;
//	        }
//
//	        if (Boolean.FALSE == newValue) {
//
//	          // Quick Reply should be turned off
//	          if (button1val == ButtonListPreference.BUTTON_QUICKREPLY) {
//	            button1.setValue(String.valueOf(ButtonListPreference.BUTTON_REPLY));
//	          } else if (button2val == ButtonListPreference.BUTTON_QUICKREPLY) {
//	            button2.setValue(String.valueOf(ButtonListPreference.BUTTON_REPLY));
//	          } else if (button3val == ButtonListPreference.BUTTON_QUICKREPLY) {
//	            button3.setValue(String.valueOf(ButtonListPreference.BUTTON_REPLY));
//	          }
//	          button1.refreshSummary();
//	          button2.refreshSummary();
//	          button3.refreshSummary();
//
//	          return true;
//	        } else if (Boolean.TRUE == newValue) {
//
//	          // Quick Reply should be turned on
//	          if (button1val == ButtonListPreference.BUTTON_REPLY) {
//	            button1.setValue(String.valueOf(ButtonListPreference.BUTTON_QUICKREPLY));
//	          } else if (button2val == ButtonListPreference.BUTTON_REPLY) {
//	            button2.setValue(String.valueOf(ButtonListPreference.BUTTON_QUICKREPLY));
//	          } else if (button3val == ButtonListPreference.BUTTON_REPLY) {
//	            button3.setValue(String.valueOf(ButtonListPreference.BUTTON_QUICKREPLY));
//
//	          }
//	          button1.refreshSummary();
//	          button2.refreshSummary();
//	          button3.refreshSummary();
//
//	          return true;
//	        }
//
//	        return false;
//	      }
//	    });

	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * 
	 */
	@Override
	protected void onResume() {
	    super.onResume();
	    if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.onResume()");
	    
	    //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	    //appEnabledCheckbox.setSummary(sharedPreferences.getBoolean(APP_ENABLED_KEY, false) ? "Droid Notify is disabled" : "Droid Notify is enabled");
	    // Register the SharedPreferenceChanged listener.            
        //getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	/**
	 * 
	 */
    @Override
    protected void onPause() {
        super.onPause();
        if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.onPause()");

        // Unregister the SharedPreferenceChanged listener.            
        //getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this); 
    }
	
	/**
	 * 
	 */
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.onSharedPreferenceChanged() Key: " + key);
        // Let's do something a preference value changes
		//if (key.equals(APP_ENABLED_KEY)) {
        //	if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.onSharedPreferenceChanged() Is Checked? " + sharedPreferences.getBoolean(key, false));
        //	appEnabledCheckbox.setSummary(sharedPreferences.getBoolean(key, false) ? "Droid Notify is disabled" : "Droid Notify is enabled");
        //}
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
}