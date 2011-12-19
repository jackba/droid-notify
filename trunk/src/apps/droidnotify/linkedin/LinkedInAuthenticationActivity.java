package apps.droidnotify.linkedin;

import oauth.signpost.OAuth;

import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import apps.droidnotify.R;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This is the LinkedIn authorization Activity.
 * 
 * @author Camille Sévigny
 */
public class LinkedInAuthenticationActivity extends Activity {
	
	//================================================================================
    // Properties
    //================================================================================
	

	private boolean _debug = false;
	private Context _context = null;
	private SharedPreferences _preferences = null;
	private LinearLayout _mainLinearLayout = null;
	private LinearLayout _progressBarLinearLayout = null;
	private Button _continueButton = null;
	private Button _cancelButton = null;
	private LinkedInOAuthService _oAuthService = null;
	private LinkedInRequestToken _linkedInToken = null;
	//private LinkedInApiClientFactory _clientFactory = null;
	//private LinkedInApiClient _linkedInClient = null;
	  
	//================================================================================
	// Public Methods
	//================================================================================	
	
	/**
	 * This is called first when a user is authenticating and allow the app access to their LinkedIn account.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		_debug = Log.getDebug();
		if (_debug) Log.v("LinkedInAuthenticationActivity.onCreate()");
	    _context = getApplicationContext();
	    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.linkedin_authentication);
		_oAuthService = LinkedInOAuthServiceFactory.getInstance().createLinkedInOAuthService(Constants.LINKEDIN_CONSUMER_KEY, Constants.LINKEDIN_CONSUMER_SECRET);
        _linkedInToken = _oAuthService.getOAuthRequestToken(Constants.LINKEDIN_CALLBACK_URL);
		//_clientFactory = LinkedInApiClientFactory.newInstance(Constants.LINKEDIN_CONSUMER_KEY, Constants.LINKEDIN_CONSUMER_SECRET);
		setupViews();
		setupButtons();
		if(LinkedInCommon.isLinkedInAuthenticated(_context)){
			LinkedInCommon.startLinkedInAlarmManager(_context, System.currentTimeMillis());
			finish();
		}else{
			_mainLinearLayout.setVisibility(View.VISIBLE);
			_progressBarLinearLayout.setVisibility(View.GONE);
		}
	}		
	
	/**
	 * As soon as the user successfully authorized the app, we are notified
	 * here. Now we need to get the verifier from the callback URL, retrieve
	 * token and token_secret.
	 */
	@Override
	public void onNewIntent(Intent intent){
		if (_debug) Log.v("LinkedInAuthenticationActivity.onNewIntent()");
		setIntent(intent);
		_mainLinearLayout.setVisibility(View.GONE);
		_progressBarLinearLayout.setVisibility(View.VISIBLE);
		Uri uri = intent.getData();
		if (uri != null && uri.getScheme().equals(Constants.LINKEDIN_CALLBACK_SCHEME)) {
			try {
				//This will populate token and token_secret in consumer.
				String oauthVerifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
				LinkedInAccessToken accessToken = _oAuthService.getOAuthAccessToken(_linkedInToken, oauthVerifier);				
				SharedPreferences.Editor editor = _preferences.edit();
				editor.putString(Constants.LINKEDIN_OAUTH_TOKEN, accessToken.getToken());
				editor.putString(Constants.LINKEDIN_OAUTH_TOKEN_SECRET, accessToken.getTokenSecret());
				editor.commit();
				//LinkedInCommon.startLinkedInAlarmManager(_context, System.currentTimeMillis());				
				finish();
			} catch (Exception ex) {
				Log.e("LinkedInAuthenticationActivity.onNewIntent() ERROR: " + ex.toString());
				Toast.makeText(_context, _context.getString(R.string.linkedin_authentication_error), Toast.LENGTH_LONG).show();
				finish();
			}
		}else{
			Log.e("LinkedInAuthenticationActivity.onNewIntent() RESPONSE ERROR");
			Toast.makeText(_context, _context.getString(R.string.linkedin_authentication_error), Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	//================================================================================
	// Protected Methods
	//================================================================================	
	
	/**
	 * Activity was started after it stopped or for the first time.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		_debug = Log.getDebug();
	    if (_debug) Log.v("LinkedInAuthenticationActivity.onStart()");
	}
	  
	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onResume() {
	    super.onResume();
	    _debug = Log.getDebug();
	    if (_debug) Log.v("LinkedInAuthenticationActivity.onResume()");
	}
	  
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
	@Override
	protected void onPause() {
	    super.onPause();
	    if (_debug) Log.v("LinkedInAuthenticationActivity.onPause()");
	}
	  
	/**
	 * Activity was stopped due to a new Activity being started or other reason.
	 */
	@Override
	protected void onStop() {
	    super.onStop();
	    if (_debug) Log.v("LinkedInAuthenticationActivity.onStop()");
	}
	  
	/**
	 * Activity was stopped and closed out completely.
	 */
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (_debug) Log.v("LinkedInAuthenticationActivity.onDestroy()");
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Performs haptic feedback based on the users preferences.
	 * 
	 * @param hapticFeedbackConstant - What type of action the feedback is responding to.
	 */
	private void customPerformHapticFeedback(int hapticFeedbackConstant){
		Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		//Perform the haptic feedback based on the users preferences.
		if(_preferences.getBoolean(Constants.HAPTIC_FEEDBACK_ENABLED_KEY, true)){
			if(hapticFeedbackConstant == HapticFeedbackConstants.VIRTUAL_KEY){
				//performHapticFeedback(hapticFeedbackConstant);
				vibrator.vibrate(50);
			}
			if(hapticFeedbackConstant == HapticFeedbackConstants.LONG_PRESS){
				//performHapticFeedback(hapticFeedbackConstant);
				vibrator.vibrate(100);
			}
		}
	}
	
	/**
	 * Setup Activity Views.
	 */
	private void setupViews(){
		if (_debug) Log.v("LinkedInAuthenticationActivity.setupViews()");
		_mainLinearLayout = (LinearLayout) findViewById(R.id.main_linear_layout);
		_progressBarLinearLayout = (LinearLayout) findViewById(R.id.progress_bar_linear_layout);
		_mainLinearLayout.setVisibility(View.GONE);
		_progressBarLinearLayout.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Setup Activity Buttons.
	 */
	private void setupButtons(){
		if (_debug) Log.v("LinkedInAuthenticationActivity.setupButtons()");
		_continueButton = (Button) findViewById(R.id.continue_button);
		_continueButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View view) {
		    	if (_debug) Log.v("Continue Button Clicked()");
		    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		    	authenticateLinkedInAccount();
		    }
		});
		_cancelButton = (Button) findViewById(R.id.cancel_button);
		_cancelButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View view) {
		    	if (_debug) Log.v("Continue Button Clicked()");
		    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		    	finish();
		    }
		});
	}
	
	/**
	 * Open the browser and asks the user to authorize the app.
	 */
	private void authenticateLinkedInAccount(){
		if (_debug) Log.v("LinkedInAuthenticationActivity.authenticateLinkedInAccount()");
		try {
			String url = _linkedInToken.getAuthorizationUrl();
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
		} catch (Exception ex) {
			Log.e("LinkedInAuthenticationActivity.authenticateLinkedInAccount() ERROR: " + ex.toString());
		}
	}

}