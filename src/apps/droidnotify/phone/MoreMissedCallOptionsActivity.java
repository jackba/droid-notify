package apps.droidnotify.phone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

public class MoreMissedCallOptionsActivity extends Activity {
	
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	private Context _context = null;
	private TextView _descriptionTextView = null;
	private ImageView _buttonDividerImageView = null;
	private TextView _buttonTextView = null;

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
	    _debug = Log.getDebug();
	    if (_debug) Log.v("MissedCallMessengerActivity.onCreate()");
	    _context = getApplicationContext();
	    Common.setApplicationLanguage(_context, this);
	    this.setContentView(R.layout.more_missed_call_options_activity);
	    initLayoutItems();
	}
	
	/**
	 * Initialize the layout items.
	 * 
	 * @param context - Application context.
	 */
	private void initLayoutItems() {
		if (_debug) Log.v("MissedCallMessengerActivity.initLayoutItems()");
		//Button Divider
		_buttonDividerImageView = (ImageView)findViewById(R.id.button_divider_below);
		//Description	
		String descriptionText = _context.getString(R.string.missed_call_messenger_description);
		_descriptionTextView = (TextView)findViewById(R.id.content_text);
		_descriptionTextView.setText(Html.fromHtml(descriptionText.replace("&lt;", "<")));
		//Setup Button
		_buttonTextView = (TextView)findViewById(R.id.button);
		_buttonTextView.setBackgroundResource(R.drawable.preference_row_click);
		
		final String upgradeURL;
		boolean displayUpgradeButton = false;
		if(Log.getAndroidVersion()){
			displayUpgradeButton = true;
			upgradeURL = Constants.APP_ANDROID_MISSED_CALL_MESSENGER_LITE_URL;
        }else if(Log.getAmazonVersion()){
			displayUpgradeButton = true;
			upgradeURL = Constants.APP_AMAZON_MISSED_CALL_MESSENGER_LITE_URL;
        }else if(Log.getSamsungVersion()){
			displayUpgradeButton = true;
			upgradeURL = Constants.APP_SAMSUNG_MISSED_CALL_MESSENGER_LITE_URL;
        }else{
			upgradeURL = Constants.APP_ANDROID_MISSED_CALL_MESSENGER_LITE_URL;
        }	
        if(displayUpgradeButton){
        	_buttonDividerImageView.setVisibility(View.VISIBLE);
        	_buttonTextView.setVisibility(View.VISIBLE);
        	_buttonTextView.setOnClickListener(new OnClickListener(){
	        	public void onClick(View view) {
		    		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(upgradeURL)));
		    		finish();
	        	}
	        });
		}else{
			_buttonDividerImageView.setVisibility(View.INVISIBLE);
			_buttonTextView.setVisibility(View.INVISIBLE);
			_buttonTextView.setOnClickListener(new OnClickListener(){
	        	public void onClick(View view) {
		    		finish();
	        	}
	        });
		}
	}
	
}