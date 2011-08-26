package apps.droidnotify.twitter;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import apps.droidnotify.log.Log;

/**
 * An asynchronous task that communicates with Twitter to 
 * retrieve a request token.
 * (OAuthGetRequestToken)
 * 
 * After receiving the request token from Twitter, 
 * pop a browser to the user to authorize the Request Token.
 * (OAuthAuthorizeToken)
 * 
 */
public class OAuthRequestTokenTask extends AsyncTask<Void, Void, Void> {

	//================================================================================
	// Constants
	//================================================================================

	public static final String CONSUMER_KEY = "Hr8aDOFeDdY9UbvQB0w2w";
	public static final String CONSUMER_SECRET= "wfZOJYkYVEYrmdmltOaKfRdnUfSiUkr2MQdjRUY2xU";

	public static final String REQUEST_URL = "https://api.twitter.com/oauth/request_token";
	public static final String ACCESS_URL = "https://api.twitter.com/oauth/authorize";
	public static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/access_token";

	final public static String OAUTH_CALLBACK_SCHEME = "droidnotify-oauth-twitter";
	final public static String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME + "://callback";

	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;
	final String TAG = getClass().getName();
	private Context	_context;
	private OAuthProvider _provider;
	private OAuthConsumer _consumer;

	/**
	 * 
	 * We pass the OAuth consumer and provider.
	 * 
	 * @param 	context
	 * 			Required to be able to start the intent to launch the browser.
	 * @param 	provider
	 * 			The OAuthProvider object
	 * @param 	consumer
	 * 			The OAuthConsumer object
	 */
	public OAuthRequestTokenTask(Context context, OAuthConsumer consumer, OAuthProvider provider) {
		_debug = Log.getDebug();
		if (_debug) Log.v("OAuthRequestTokenTask.OAuthRequestTokenTask()");
		_context = context;
		_consumer = consumer;
		_provider = provider;
	}

	/**
	 * 
	 * Retrieve the OAuth Request Token and present a browser to the user to authorize the token.
	 * 
	 */
	@Override
	protected Void doInBackground(Void... params) {
		if (_debug) Log.v("OAuthRequestTokenTask.doInBackground() Retrieving request token from Google servers.");
		try {
			final String url = _provider.retrieveRequestToken(_consumer, OAUTH_CALLBACK_URL);
			if (_debug) Log.v("OAuthRequestTokenTask.doInBackground() Please authorize this app! URL : " + url);
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
			_context.startActivity(intent);
		} catch (Exception ex) {
			if (_debug) Log.e("OAuthRequestTokenTask.doInBackground() Error during OAUth retrieve request token: " + ex.toString());
		}
		return null;
	}

}