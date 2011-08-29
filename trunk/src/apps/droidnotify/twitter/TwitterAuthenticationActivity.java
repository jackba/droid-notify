package apps.droidnotify.twitter;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import apps.droidnotify.R;
import apps.droidnotify.log.Log;

public class TwitterAuthenticationActivity extends Activity {

	//================================================================================
    // Constants
    //================================================================================
	
	public static final String CONSUMER_KEY = "Hr8aDOFeDdY9UbvQB0w2w";
	public static final String CONSUMER_SECRET= "wfZOJYkYVEYrmdmltOaKfRdnUfSiUkr2MQdjRUY2xU";
	final public static String CALLBACK_SCHEME = "droidnotify-oauth-twitter";
	final public static String CALLBACK_URL = CALLBACK_SCHEME + "://callback";	
	
	//================================================================================
    // Properties
    //================================================================================
	
	private OAuthProvider _provider;
	private CommonsHttpOAuthConsumer _consumer;
	private boolean _debug = false;
	private Context _context = null;
	private SharedPreferences _preferences = null;
	  
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
		//setContentView(R.layout.main_oauth);	
//		if(isTwitterAuthenticated()){
//			finish();
//		}else{
	    	Builder builder = new Builder(_context);
	    	builder.setIcon(R.drawable.ic_dialog_info);
            builder.setTitle(R.string.twitter_authentication_text);
            builder.setMessage(_context.getString(R.string.twitter_authentication_message_text));
            builder.setPositiveButton(R.string.continue_text, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	authenticateTwitterAccount();
                }
            });
            builder.setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	dialog.dismiss();
                }
            });
            AlertDialog twitterAlertDialog = builder.create();
            twitterAlertDialog.show();
//		}
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
		Uri uri = intent.getData();
		if (_debug) Log.v("TwitterAuthenticationActivity.onNewIntent() URI: " + uri);
		if (uri != null && uri.getScheme().equals(CALLBACK_SCHEME)) {
			String verifier = uri.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);
			try {
				// this will populate token and token_secret in consumer
				_provider.retrieveAccessToken(_consumer, verifier);
				String token = _consumer.getToken();
				String secret = _consumer.getTokenSecret();
				_consumer.setTokenWithSecret(token, secret);
				Editor edit = _preferences.edit();
				edit.putString(OAuth.OAUTH_TOKEN, token);
				edit.putString(OAuth.OAUTH_TOKEN_SECRET, secret);
				edit.commit();
				finish();
			} catch (Exception ex) {
				if (_debug) Log.e("TwitterAuthenticationActivity.onNewIntent() ERROR: " + ex.toString());
			}
		}
	}
	  
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Open the browser and asks the user to authorize the app.
	 * Afterwards, we redirect the user back here!
	 */
	private void authenticateTwitterAccount() {
		if (_debug) Log.v("TwitterAuthenticationActivity.authenticateTwitterAccount()");
		try {
			_consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			_provider = new DefaultOAuthProvider("http://twitter.com/oauth/request_token", "http://twitter.com/oauth/access_token", "http://twitter.com/oauth/authorize");
			String url = _provider.retrieveRequestToken(_consumer, CALLBACK_URL);
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(intent);
		} catch (Exception ex) {
			if (_debug) Log.e("TwitterAuthenticationActivity.authenticateTwitterAccount() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Determine if the user has authenticated their twitter account.
	 *
	 * @return boolean - Return true if the user preferences have Twitter authentication data.
	 */
	private boolean isTwitterAuthenticated() {	
		try {
			String token = _preferences.getString(OAuth.OAUTH_TOKEN, "");
			String secret = _preferences.getString(OAuth.OAUTH_TOKEN_SECRET, "");
			if(token.equals("") || secret.equals("")){
				return false;
			}
			AccessToken accessToken = new AccessToken(token, secret);
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			twitter.setOAuthAccessToken(accessToken);	
			twitter.getAccountSettings();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

}