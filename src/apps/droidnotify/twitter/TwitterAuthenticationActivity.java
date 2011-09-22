package apps.droidnotify.twitter;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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

import apps.droidnotify.R;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

public class TwitterAuthenticationActivity extends Activity {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private OAuthProvider _provider;
	private CommonsHttpOAuthConsumer _consumer;
	private boolean _debug = false;
	private Context _context = null;
	private SharedPreferences _preferences = null;
	private LinearLayout _mainLinearLayout = null;
	private LinearLayout _progressBarLinearLayout = null;
	private Button _continueButton = null;
	private Button _cancelButton = null;
	  
	//================================================================================
	// Public Methods
	//================================================================================	
	
	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_debug = Log.getDebug();
		if (_debug) Log.v("Twitter AuthenticationActivity.onCreate()");
	    _context = getApplicationContext();
	    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.twitter_authentication);
		setupViews();
		setupButtons();
		if(isTwitterAuthenticated()){
			finish();
		}else{
			_mainLinearLayout.setVisibility(View.VISIBLE);
			_progressBarLinearLayout.setVisibility(View.GONE);
		}
	}		
	
	/**
	 * As soon as the user successfully authorized the app, we are notified
	 * here. Now we need to get the verifier from the callback URL, retrieve
	 * token and token_secret and feed them to twitter4j (as well as
	 * consumer key and secret).
	 */
	@Override
	public void onNewIntent(Intent intent){
		if (_debug) Log.v("TwitterAuthenticationActivity.onNewIntent()");
		_mainLinearLayout.setVisibility(View.GONE);
		_progressBarLinearLayout.setVisibility(View.VISIBLE);
		Uri uri = intent.getData();
		if (uri != null && uri.getScheme().equals(Constants.TWITTER_CALLBACK_SCHEME)) {
			if (_debug) Log.v("TwitterAuthenticationActivity.onNewIntent() URI: " + uri);
			if (_debug) Log.v("TwitterAuthenticationActivity.onNewIntent() uri.getScheme(): " + uri.getScheme());
			try {
				// this will populate token and token_secret in consumer
				String oauthVerifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
				_provider.retrieveAccessToken(_consumer, oauthVerifier);
				Editor edit = _preferences.edit();
				edit.putString(OAuth.OAUTH_TOKEN, _consumer.getToken());
				edit.putString(OAuth.OAUTH_TOKEN_SECRET, _consumer.getTokenSecret());
				edit.commit();
				finish();
			} catch (Exception ex) {
				if (_debug) Log.e("TwitterAuthenticationActivity.onNewIntent() ERROR: " + ex.toString());
			}
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
	    if (_debug) Log.v("TwitterAuthenticationActivity.onStart()");
	}
	  
	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onResume() {
	    super.onResume();
	    _debug = Log.getDebug();
	    if (_debug) Log.v("TwitterAuthenticationActivity.onResume()");
	}
	  
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
	@Override
	protected void onPause() {
	    super.onPause();
	    if (_debug) Log.v("TwitterAuthenticationActivity.onPause()");
	}
	  
	/**
	 * Activity was stopped due to a new Activity being started or other reason.
	 */
	@Override
	protected void onStop() {
	    super.onStop();
	    if (_debug) Log.v("TwitterAuthenticationActivity.onStop()");
	}
	  
	/**
	 * Activity was stopped and closed out completely.
	 */
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (_debug) Log.v("TwitterAuthenticationActivity.onDestroy()");
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
		if (_debug) Log.v("TwitterAuthenticationActivity.setupViews()");
		_mainLinearLayout = (LinearLayout) findViewById(R.id.main_linear_layout);
		_progressBarLinearLayout = (LinearLayout) findViewById(R.id.progress_bar_linear_layout);
		_mainLinearLayout.setVisibility(View.GONE);
		_progressBarLinearLayout.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Setup Activity Buttons.
	 */
	private void setupButtons(){
		if (_debug) Log.v("TwitterAuthenticationActivity.setupButtons()");
		_continueButton = (Button) findViewById(R.id.continue_button);
		_continueButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View view) {
		    	if (_debug) Log.v("Continue Button Clicked()");
		    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		    	authenticateTwitterAccount();
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
	private void authenticateTwitterAccount() {
		if (_debug) Log.v("TwitterAuthenticationActivity.authenticateTwitterAccount()");
		try {
			_consumer = new CommonsHttpOAuthConsumer(Constants.TWITTER_CONSUMER_KEY, Constants.TWITTER_CONSUMER_SECRET);
			_provider = new DefaultOAuthProvider("https://api.twitter.com/oauth/request_token", "https://api.twitter.com/oauth/access_token", "https://api.twitter.com/oauth/authorize");
			String url = _provider.retrieveRequestToken(_consumer, Constants.TWITTER_CALLBACK_URL);
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
		} catch (Exception ex) {
			if (_debug) Log.e("TwitterAuthenticationActivity.authenticateTwitterAccount() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Determine if the user has authenticated their twitter account.
	 *
	 * @return boolean - Return true if the user preferences have Twitter authentication data & are able to log into Twitter.
	 */
	private boolean isTwitterAuthenticated() {
		if (_debug) Log.v("TwitterAuthenticationActivity.isTwitterAuthenticated()");	
		try {
			String oauthToken = _preferences.getString(OAuth.OAUTH_TOKEN, "");
			String oauthTokenSecret = _preferences.getString(OAuth.OAUTH_TOKEN_SECRET, "");
			if (_debug) Log.v("TwitterAuthenticationActivity.isTwitterAuthenticated() oauthToken: " + oauthToken);	
			if (_debug) Log.v("TwitterAuthenticationActivity.isTwitterAuthenticated() oauthTokenSecret: " + oauthTokenSecret);	
			if(oauthToken.equals("") || oauthTokenSecret.equals("")){
				return false;
			}	
			try {
				ConfigurationBuilder configurationBuilder = new ConfigurationBuilder(); 
				configurationBuilder.setOAuthConsumerKey(Constants.TWITTER_CONSUMER_KEY); 
				configurationBuilder.setOAuthConsumerSecret(Constants.TWITTER_CONSUMER_SECRET); 
				Configuration config =  configurationBuilder.build();  
				AccessToken accessToken = new AccessToken(oauthToken, oauthTokenSecret);
				TwitterFactory factory = new TwitterFactory(config);
				Twitter twitter = factory.getInstance(accessToken); 
				twitter.getAccountSettings();
				return true;
			} catch (Exception ex) {
				if (_debug) Log.e("TwitterAuthenticationActivity.isTwitterAuthenticated() Twitter Authentication - ERROR: " + ex.toString());
				return false;
			}
		} catch (Exception ex) {
			if (_debug) Log.e("TwitterAuthenticationActivity.isTwitterAuthenticated() General - ERROR: " + ex.toString());
			return false;
		}
	}

}