package apps.droidnotify.preferences;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import apps.droidnotify.R;
import apps.droidnotify.log.Log;

public class PreferencesActivity extends Activity {
	
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	private Context _context = null;
	
	private TextView _basicSettingsRow = null;	
	private TextView _localeSettingsRow = null;	
	private TextView _screenSettingsRow = null;	
	private TextView _customizeSettingsRow = null;	
	private TextView _notificationsSettingsRow = null;	
	private TextView _advancedSettingsRow = null;	
	private TextView _rateAppSettingsRow = null;	
	private TextView _emailDeveloperSettingsRow = null;	
	private TextView _aboutSettingsRow = null;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Called when the activity is created. Set up views and notifications.
	 * 
	 * @param bundle - Activity bundle.
	 */
	@Override
	protected void onCreate(Bundle bundle){
		super.onCreate(bundle);
	    _context = getApplicationContext();
		_debug = Log.getDebug();
	    if (_debug) Log.v("PreferencesActivity.onCreate()");
	    //Common.setApplicationLanguage(_context, this);
	    setContentView(R.layout.preference_activity);
	    initLayoutItems();
	    setupRowAttributes();
	    
	    
	}

	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Initialize the layout items.
	 * 
	 * @param context - Application's Context.
	 */
	private void initLayoutItems() {
		if (_debug) Log.v("PreferencesActivity.initLayoutItems()");		
		_basicSettingsRow = (TextView)findViewById(R.id.row_basic);
		_localeSettingsRow = (TextView)findViewById(R.id.row_locale);
		_screenSettingsRow = (TextView)findViewById(R.id.row_screen);
		_customizeSettingsRow = (TextView)findViewById(R.id.row_customize);
		_notificationsSettingsRow = (TextView)findViewById(R.id.row_notifications);
		_advancedSettingsRow = (TextView)findViewById(R.id.row_advanced);
		_rateAppSettingsRow = (TextView)findViewById(R.id.row_rate_app);
		_emailDeveloperSettingsRow = (TextView)findViewById(R.id.row_email_developer);
		_aboutSettingsRow = (TextView)findViewById(R.id.row_about);
	}
	
	private void setupRowAttributes(){
		OnTouchListener onTouchListener = new OnTouchListener() {
			public boolean onTouch(View view, MotionEvent motionEvent){
	     		switch (motionEvent.getAction()){
		     		case MotionEvent.ACTION_DOWN:{
		     			if (_debug) Log.v("PreferencesActivity.initLayoutItems() ACTION DOWN");	
	        			view.setBackgroundResource(R.color.blue);
		                break;
			        }
		     		case MotionEvent.ACTION_UP:{
		     			if (_debug) Log.v("PreferencesActivity.initLayoutItems() ACTION UP");	
	        			view.setBackgroundResource(R.color.transparent);
		                break;
		     		}
		     		case MotionEvent.ACTION_CANCEL:{
		     			if (_debug) Log.v("PreferencesActivity.initLayoutItems() ACTION CANCEL");	
	        			view.setBackgroundResource(R.color.transparent);
		                break;
		     		}
	     		}
	     		return true;
			}
	     };
		_basicSettingsRow.setOnTouchListener(onTouchListener);
		_localeSettingsRow.setOnTouchListener(onTouchListener);
		
	}
	
}
