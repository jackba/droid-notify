package apps.droidnotify.preferences;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;

/**
 * This is the "About" applications preference Activity.
 * 
 * @author Camille Sévigny
 */
public class AboutPreferenceActivity extends Activity {
	
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	private TextView _aboutTitleTextView = null;
	private TextView _aboutTextView = null;
	private TextView _closeTextView = null;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Called when the activity is created. Set up views and buttons.
	 * 
	 * @param bundle - Activity bundle.
	 */
	@Override
	protected void onCreate(Bundle bundle){
		super.onCreate(bundle);
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    Context context = getApplicationContext();
	    _debug = Log.getDebug();
	    if (_debug) Log.v("AboutPreferenceActivity.onCreate()");
	    Common.setApplicationLanguage(context, this);
	    this.setContentView(R.layout.about_preference_activity);
	    initLayoutItems(context);
	}
	
	/**
	 * Initialize the layout items.
	 * 
	 * @param context - Application context.
	 */
	private void initLayoutItems(Context context) {
		if (_debug) Log.v("AboutPreferenceActivity.initLayoutItems()");	
		//About Title Text View
		String appNameVersion = Log.getAppProVersion() ? context.getString(R.string.app_name_pro_formatted_version, Common.getApplicationVersion(context)) : context.getString(R.string.app_name_basic_formatted_version, Common.getApplicationVersion(context));
		_aboutTitleTextView = (TextView)findViewById(R.id.about_title_text);
		_aboutTitleTextView.setText(appNameVersion);
		//About Text View
		_aboutTextView = (TextView)findViewById(R.id.about_text);
		_aboutTextView.setMovementMethod(LinkMovementMethod.getInstance());
		int whiteColor = this.getResources().getColor(R.color.white);
		_aboutTextView.setTextColor(whiteColor);
		_aboutTextView.setLinkTextColor(whiteColor);
		String aboutText = context.getString(R.string.preference_about_text) + context.getString(R.string.preference_translated_by_text) + context.getString(R.string.preference_copyright_text);
		_aboutTextView.setText(Html.fromHtml(aboutText.replace("&lt;", "<")));
		//Close Text View
		_closeTextView = (TextView)findViewById(R.id.close_button);
		_closeTextView.setBackgroundResource(R.drawable.preference_row_click);
		_closeTextView.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
        		finish();
        	}
		});
	}
	
}