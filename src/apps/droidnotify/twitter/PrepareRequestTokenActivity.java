package apps.droidnotify.twitter;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import apps.droidnotify.log.Log;

/**
 * Prepares a OAuthConsumer and OAuthProvider 
 * 
 * OAuthConsumer is configured with the consumer key & consumer secret.
 * OAuthProvider is configured with the 3 OAuth endpoints.
 * 
 * Execute the OAuthRequestTokenTask to retrieve the request, and authorize the request.
 * 
 * After the request is authorized, a callback is made here.
 * 
 */
public class PrepareRequestTokenActivity extends Activity {

	//================================================================================
	// Constants
	//================================================================================

	public static final String CONSUMER_KEY = "Hr8aDOFeDdY9UbvQB0w2w";
	public static final String CONSUMER_SECRET= "wfZOJYkYVEYrmdmltOaKfRdnUfSiUkr2MQdjRUY2xU";

	public static final String REQUEST_URL = "http://twitter.com/oauth/request_token"; //"https://api.twitter.com/oauth/request_token"
	public static final String ACCESS_URL = "http://twitter.com/oauth/authorize"; //"https://api.twitter.com/oauth/authorize"
	public static final String AUTHORIZE_URL = "http://twitter.com/oauth/access_token"; //"https://api.twitter.com/oauth/access_token"

	final public static String CALLBACK_SCHEME = "droidnotify-oauth-twitter";
	final public static String CALLBACK_URL = CALLBACK_SCHEME + "://callback";

	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;
	private Context	_context;
    private OAuthConsumer _consumer; 
    private OAuthProvider _provider;
    
    /**
     * 
     */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_debug = Log.getDebug();
		if (_debug) Log.v("PrepareRequestTokenActivity.onCreate()");
		_context = getApplicationContext();
//    	try {
//    		_consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
//    	    //_provider = new CommonsHttpOAuthProvider(REQUEST_URL, ACCESS_URL, AUTHORIZE_URL);
//    	    _provider = new DefaultOAuthProvider(REQUEST_URL, ACCESS_URL, AUTHORIZE_URL);
//    	} catch (Exception ex) {
//    		if (_debug) Log.e("PrepareRequestTokenActivity.onCreate() Error creating consumer / provider: " + ex.toString());
//		}
//    	if (_debug) Log.v("PrepareRequestTokenActivity.onCreate() Starting task to retrieve request token.");
		//new OAuthRequestTokenTask(this, _consumer, _provider).execute();
		askOAuth();
	}

	/**
	 * Called when the OAuthRequestTokenTask finishes (user has authorized the request token).
	 * The callback URL will be intercepted here.
	 */
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent); 
		if (_debug) Log.v("PrepareRequestTokenActivity.onNewIntent()");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final Uri uri = intent.getData();
		if (uri != null && uri.getScheme().equals(CALLBACK_SCHEME)) {
			if (_debug) Log.v("PrepareRequestTokenActivity.onNewIntent() Callback received : " + uri);
			if (_debug) Log.v("PrepareRequestTokenActivity.onNewIntent() Retrieving Access Token");
			new RetrieveAccessTokenTask(this, _consumer, _provider, prefs).execute(uri);
			finish();	
		}
	}

	private void askOAuth() {  
		 try {  
			  _consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);  
			  _provider = new DefaultOAuthProvider("http://twitter.com/oauth/request_token", "http://twitter.com/oauth/access_token", "http://twitter.com/oauth/authorize");  
			  String authUrl = _provider.retrieveRequestToken(_consumer, CALLBACK_URL);  
			  Toast.makeText(_context, "Please authorize this app!", Toast.LENGTH_LONG).show();  
			  startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));  
		 } catch (Exception e) {  
			 Toast.makeText(_context, e.getMessage(), Toast.LENGTH_LONG).show();  
		 }  
	} 
	
	/**
	 * 
	 * @author xqs230cs
	 *
	 */
	public class RetrieveAccessTokenTask extends AsyncTask<Uri, Void, Void> {

		private Context	_context;
		private OAuthProvider _provider;
		private OAuthConsumer _consumer;
		private SharedPreferences _prefs;

		/**
		 * 
		 * @param context
		 * @param consumer
		 * @param provider
		 * @param prefs
		 */
		public RetrieveAccessTokenTask(Context context, OAuthConsumer consumer,OAuthProvider provider, SharedPreferences prefs) {
			_context = context;
			_consumer = consumer;
			_provider = provider;
			_prefs=prefs;
		}

		/**
		 * Retrieve the oauth_verifier, and store the oauth and oauth_token_secret 
		 * for future API calls.
		 */
		@Override
		protected Void doInBackground(Uri...params) {
			final Uri uri = params[0];
			final String oauth_verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
			try {
				_provider.retrieveAccessToken(_consumer, oauth_verifier);
				final Editor edit = _prefs.edit();
				edit.putString(OAuth.OAUTH_TOKEN, _consumer.getToken());
				edit.putString(OAuth.OAUTH_TOKEN_SECRET, _consumer.getTokenSecret());
				edit.commit();
				String token = _prefs.getString(OAuth.OAUTH_TOKEN, "");
				String secret = _prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
				_consumer.setTokenWithSecret(token, secret);
				//_context.startActivity(new Intent(_context, AndroidTwitterSample.class));
				//executeAfterAccessTokenRetrieval();
				Toast.makeText(_context, "Twitter Authentication Successfull", Toast.LENGTH_LONG);
				Toast.makeText(_context, "OAuth.OAUTH_TOKEN KEY: " + OAuth.OAUTH_TOKEN + ", OAuth.OAUTH_TOKEN Value: " + _consumer.getToken(), Toast.LENGTH_LONG);
				Toast.makeText(_context, "OAuth.OAUTH_TOKEN_SECRET KEY: " + OAuth.OAUTH_TOKEN_SECRET + ", OAuth.OAUTH_TOKEN_SECRET Value: " + _consumer.getTokenSecret(), Toast.LENGTH_LONG);
				if (_debug) Log.v("OAuth - Access Token Retrieved");

			} catch (Exception ex) {
				if (_debug) Log.e("OAuth - Access Token Retrieval Error: " + ex.toString());
			}
			return null;
		}
	
//		private void checkForSavedLogin() {  
//			 // Get Access Token and persist it  
//			 AccessToken a = getAccessToken();  
//			 if (a==null) return; //if there are no credentials stored then return to usual activity  
//			  
//			 // initialize Twitter4J  
//			 twitter = new TwitterFactory().getInstance();  
//			 twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);  
//			 twitter.setOAuthAccessToken(a);  
//			 ((TwitterApplication)getApplication()).setTwitter(twitter);  
//			   
//			 startFirstActivity();  
//			 finish();  
//			}  
		
 
		

		
//		/**
//		 * 
//		 */
//		private void executeAfterAccessTokenRetrieval() {
//			String msg = getIntent().getExtras().getString("tweet_msg");
//			try {
//				TwitterUtils.sendTweet(_prefs, msg);
//				if (_debug) Log.v("OAuth - Error sending to Twitter:");
//			} catch (Exception ex) {
//				if (_debug) Log.e("OAuth - Error sending to Twitter: " + ex.toString());
//			}
//		}
	}	

}