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
import android.widget.Toast;
import apps.droidnotify.R;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This is the Facebook authorization Activity.
 * 
 * @author Camille S�vigny
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
	    _context = getApplicationContext();
		_debug = Log.getDebug();
		if (_debug) Log.v("FacebookAuthenticationActivity.onCreate()");
	    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);	    
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.facebook_authentication);
	    _facebook = new Facebook(Constants.FACEBOOK_APP_ID);
	    /*
         * Get existing access_token if any
         */
        String accessToken = _preferences.getString(Constants.FACEBOOK_ACCESS_TOKEN_KEY, null);
        long expires = _preferences.getLong(Constants.FACEBOOK_ACCESS_EXPIRES_KEY, 0);
        if(accessToken != null) {
        	_facebook.setAccessToken(accessToken);
        }
        if(expires != 0) {
            _facebook.setAccessExpires(expires);
        }
        /*
         * Only call authorize if the access_token has expired.
         */
        if(!_facebook.isSessionValid()) {

        	String permissions[] = new String[] {"offline_access", "manage_notifications", "read_mailbox", "read_requests"};
            _facebook.authorize(this, permissions, new DialogListener() {

                public void onComplete(Bundle values) {
                    SharedPreferences.Editor editor = _preferences.edit();
                    editor.putString(Constants.FACEBOOK_ACCESS_TOKEN_KEY, _facebook.getAccessToken());
                    editor.putLong(Constants.FACEBOOK_ACCESS_EXPIRES_KEY, _facebook.getAccessExpires());
                    editor.commit();
                    FacebookCommon.startFacebookAlarmManager(_context, System.currentTimeMillis());
                    finish();
                }
    
                public void onFacebookError(FacebookError error){
                	Log.e("FacebookAuthenticationActivity.onCreate() OnFacebookError ERROR: " + error.toString());
                	Toast.makeText(_context, _context.getString(R.string.facebook_authentication_error), Toast.LENGTH_LONG).show();
                	finish();
                }

                public void onError(DialogError e){
                	Log.e("FacebookAuthenticationActivity.onCreate() OnError ERROR: " + e.toString());
                	Toast.makeText(_context, _context.getString(R.string.facebook_authentication_error), Toast.LENGTH_LONG).show();
                	finish();
                }
    
                public void onCancel(){
                	Toast.makeText(_context, _context.getString(R.string.facebook_authentication_error), Toast.LENGTH_LONG).show();
                	finish();
                }
                
            });
            
        }else{
        	FacebookCommon.startFacebookAlarmManager(_context, System.currentTimeMillis());
            finish();
        }
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        _facebook.authorizeCallback(requestCode, resultCode, data);
    }

}