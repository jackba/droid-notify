package apps.droidnotify.facebook;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import apps.droidnotify.R;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This is the Facebook authorization Activity.
 * 
 * @author Camille Sévigny
 */
public class FacebookAuthenticationActivity extends Activity {
	
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	private Context _context = null;
	private SharedPreferences _preferences = null;
    private Facebook _facebook = null;
	  
	//================================================================================
	// Public Methods
	//================================================================================	
	
	/**
	 * This is called first when a user is authenticating and allow the app access to their Twitter account.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		_debug = Log.getDebug();
		if (_debug) Log.v("FacebookAuthenticationActivity.onCreate()");
	    _context = getApplicationContext();
	    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);	    
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.facebook_authentication);
	    _facebook = new Facebook(Constants.FACEBOOK_APP_ID);
	    /*
         * Get existing access_token if any
         */
        String access_token = _preferences.getString(Constants.FACEBOOK_ACCESS_TOKEN_KEY, null);
        long expires = _preferences.getLong(Constants.FACEBOOK_ACCESS_EXPIRES_KEY, 0);
        if(access_token != null) {
        	_facebook.setAccessToken(access_token);
        }
        if(expires != 0) {
        	_facebook.setAccessExpires(expires);
        }
        /*
         * Only call authorize if the access_token has expired.
         */
        if(!_facebook.isSessionValid()) {

            _facebook.authorize(this, new String[] {}, new DialogListener() {

                public void onComplete(Bundle values) {
                    SharedPreferences.Editor editor = _preferences.edit();
                    editor.putString(Constants.FACEBOOK_ACCESS_TOKEN_KEY, _facebook.getAccessToken());
                    editor.putLong(Constants.FACEBOOK_ACCESS_EXPIRES_KEY, _facebook.getAccessExpires());
                    editor.commit();
                    finish();
                }
    
                public void onFacebookError(FacebookError error) {}
    
                public void onError(DialogError e) {}
    
                public void onCancel() {}
                
            });
            
        }else{
            finish();
        }
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        _facebook.authorizeCallback(requestCode, resultCode, data);
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
	    if (_debug) Log.v("FacebookAuthenticationActivity.onStart()");
	}
	  
	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onResume() {
	    super.onResume();
	    _debug = Log.getDebug();
	    if (_debug) Log.v("FacebookAuthenticationActivity.onResume()");
	}
	  
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
	@Override
	protected void onPause() {
	    super.onPause();
	    if (_debug) Log.v("FacebookAuthenticationActivity.onPause()");
	}
	  
	/**
	 * Activity was stopped due to a new Activity being started or other reason.
	 */
	@Override
	protected void onStop() {
	    super.onStop();
	    if (_debug) Log.v("FacebookAuthenticationActivity.onStop()");
	}
	  
	/**
	 * Activity was stopped and closed out completely.
	 */
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (_debug) Log.v("FacebookAuthenticationActivity.onDestroy()");
	}

}
