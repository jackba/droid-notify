package apps.droidnotify.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This is the "Upgrade" applications preference Activity.
 * 
 * @author Camille Sévigny
 */
public class AddOnsActivity extends Activity {
	
	//================================================================================
    // Properties
    //================================================================================

	private Context _context = null;
	private TextView _contentTextView = null;
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
	    _context = getApplicationContext();
	    Common.setApplicationLanguage(_context, this);
	    this.setContentView(R.layout.add_ons_activity);
	    initLayoutItems();
	}
	
	/**
	 * Initialize the layout items.
	 * 
	 * @param context - Application context.
	 */
	private void initLayoutItems() {
		//Find Views.
		_contentTextView = (TextView)findViewById(R.id.content_text);
		_buttonTextView = (TextView)findViewById(R.id.button);
		_buttonTextView.setBackgroundResource(R.drawable.preference_row_click);	
		final String downloadURL;
		String descriptionText = null;
		String buttonText = null;
		boolean displayUpgradeButton = true;
		descriptionText = _context.getString(R.string.download_droid_notify_plus_description_text);
		if(Log.getAndroidVersion()){
			displayUpgradeButton = true;
			buttonText = _context.getString(R.string.download_now);
			downloadURL = Constants.APP_ANDROID_PLUS_URL;
        }else if(Log.getAmazonVersion()){
			displayUpgradeButton = true;
			buttonText = _context.getString(R.string.download_now);
			downloadURL = Constants.APP_AMAZON_PLUS_URL;
        }else if(Log.getSamsungVersion()){
			displayUpgradeButton = true;
			buttonText = _context.getString(R.string.download_now);
			downloadURL = Constants.APP_SAMSUNG_PLUS_URL;
        }else if(Log.getSlideMeVersion()){
			displayUpgradeButton = true;
			buttonText = _context.getString(R.string.download_now);
			downloadURL = Constants.APP_SLIDEME_PLUS_URL;
        }else{
			displayUpgradeButton = false;
			buttonText = _context.getString(R.string.close);
			downloadURL = Constants.APP_ANDROID_PLUS_URL;
        }
		_contentTextView.setText(descriptionText);
		_buttonTextView.setText(buttonText);	
        if(displayUpgradeButton){
        	_buttonTextView.setOnClickListener(new OnClickListener(){
	        	public void onClick(View view) {
	        		try{
	        			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(downloadURL)));
	        		}catch(Exception ex){
	        			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APP_GOOGLE_PLUS_URL)));
	        		}
		    		finish();
	        	}
	        });
		}else{
			_buttonTextView.setOnClickListener(new OnClickListener(){
	        	public void onClick(View view) {
		    		finish();
	        	}
	        });
		}
	}
	
}