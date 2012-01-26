package apps.droidnotify.log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;

/**
 * This class logs messages to the Android log file.
 * 
 * @author Camille Sévigny
 */
public class Log {
	
	//================================================================================
    // Constants
    //================================================================================
	
	private final String LINE_SEPARATOR = System.getProperty("line.separator");
	private final int MAX_LOG_MESSAGE_LENGTH = 100000;
	
	//================================================================================
    // Properties
    //================================================================================
	
	private static final String _logTag = "DroidNotify";
	private static final boolean _appProVersion = false;
	private static boolean _debug = true;
	private static final boolean _showAndroidRateAppLink = true;
	private static final boolean _showAmazonRateAppLink = false;
	
	private static Context _context = null;
	private static CollectLogTask _collectLogTask = null;
    private ProgressDialog _progressDialog;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 *  Get logTag property.
	 *  
	 *  @return String - Returns the tag of these log entries.
	 */
	public static String getLogTag(){
		return _logTag;
	}

	/**
	 *  Get debug property.
	 *  
	 *  @return boolean - Returns true if the log class is set to log entries.
	 */
	public static boolean getDebug(){
		return _debug;		
	}

	/**
	 *  Set debug property.
	 *  
	 *  @param debug - The value we want to set the debug flag to.
	 */
	public static void setDebug(boolean debug){
		_debug = debug;		
	}

	/**
	 *  Get showAndroidRateAppLink property.
	 *  
	 *  @return boolean - Returns true if we want to show the Android Market link.
	 */
	public static boolean getShowAndroidRateAppLink(){
		return _showAndroidRateAppLink;
	}

	/**
	 *  Get showAmazonRateAppLink property.
	 *  
	 *  @return boolean - Returns true if we want to show the Amazon Appstore link.
	 */
	public static boolean getShowAmazonRateAppLink(){
		return _showAmazonRateAppLink;
	}

	/**
	 *  Get appProVersionproperty.
	 *  
	 *  @return boolean - Returns true if this is setup as the Pro Version app.
	 */
	public static boolean getAppProVersion(){
		return _appProVersion;
	}
	
	/**
	 *  Add an entry to the Android LogCat log under the V (Verbose) type.
	 *  
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void v(String msg) {
		if(_debug){
			android.util.Log.v(_logTag, msg);
		}
	}
	
	/**
	 *  Add an entry to the Android LogCat log under the D (Debug) type.
	 *  
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void d(String msg) {
		if(_debug){
			android.util.Log.d(_logTag, msg);
		}
	}	
	
	/**
	 *  Add an entry to the Android LogCat log under the I (Info) type.
	 *  
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void i(String msg) {
		if(_debug){
			android.util.Log.i(_logTag, msg);
		}
	}
	
	/**
	 *  Add an entry to the Android LogCat log under the W (Warning) type.
	 *  
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void w(String msg) {
		if(_debug){
			android.util.Log.w(_logTag, msg);
		}
	}
	
	/**
	 *  Add an entry to the Android LogCat log under the E (Error) type.
	 *  
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void e(String msg) {
		if(_debug){
			android.util.Log.e(_logTag, msg);
		}
	}
	
	/**
	 * Read the logs from the users phone and email them to the developer.
	 * 
	 * @param context - The application context.
	 */
    @SuppressWarnings("unchecked")
	public static void collectAndSendLog(Context context){
    	if (_debug) Log.v("Log.collectAndSendLog()");
    	_context = context;
        ArrayList<String> commandLineOptions = new ArrayList<String>();
        /*
			---FORMAT OPTIONS---
			brief — Display priority/tag and PID of originating process (the default format).
			process — Display PID only.
			tag — Display the priority/tag only.
			thread — Display process:thread and priority/tag only.
			raw — Display the raw log message, with no other metadata fields.
			time — Display the date, invocation time, priority/tag, and PID of the originating process.
			long — Display all metadata fields and separate messages with a blank lines.
         */
        commandLineOptions.add("-v");
        commandLineOptions.add("long");
        /*
			---FILTER OPTIONS---
			A series of <tag>[:priority]
			
			<tag> is a log component tag (or * for all) and priority is:
          		V    Verbose
          		D    Debug
          		I    Info
          		W    Warn
          		E    Error
          		F    Fatal
          		S    Silent (supress all output)
			
			A filter expression follows this format tag:priority ..., where tag indicates the tag of interest and priority 
			indicates the minimum level of priority to report for that tag. Messages for that tag at or above the specified 
			priority are written to the log. You can supply any number of tag:priority specifications in a single filter 
			expression. The series of specifications is whitespace-delimited.
			
			Here's an example of a filter expression that suppresses all log messages except those with the tag "ActivityManager", 
			at priority "Info" or above, and all log messages with tag "MyApp", with priority "Debug" or above:
			
			adb logcat ActivityManager:I MyApp:D *:S
			
			The final element in the above expression, *:S, sets the priority level for all tags to "silent", thus ensuring only 
			log messages with "View" and "MyApp" are displayed. Using *:S

        	'*' means '*:d' and <tag> by itself means <tag>:v

        If not specified on the commandline, filterspec is set from ANDROID_LOG_TAGS.
        If no filterspec is found, filter defaults to '*:I'        	
        */
        commandLineOptions.add(_logTag + ":V");
        commandLineOptions.add("AndroidRuntime:E");
        commandLineOptions.add("*:S");
        _collectLogTask = (CollectLogTask) (new Log()).new CollectLogTask().execute(commandLineOptions);
    }
 
	//================================================================================
	// Private Methods
	//================================================================================

    /*
     * 
     */
    private class CollectLogTask extends AsyncTask<ArrayList<String>, Void, StringBuilder>{
        
        /**
         * Do this work before the background task starts.
         */  	
        @Override
        protected void onPreExecute(){
    		if(_debug) android.util.Log.v(_logTag, "CollectLogTask.onPreExecute()");
            showProgressDialog(_context.getString(R.string.log_file_acquiring_system_logs));
        }
        
	    /**
	     * Do this work in the background.
	     * 
	     * @param params - An ArrayList of the command line parameters to use.
	     */
        @Override
        protected StringBuilder doInBackground(ArrayList<String>... params){
    		if(_debug) android.util.Log.v(_logTag, "CollectLogTask.doInBackground()");
            final StringBuilder log = new StringBuilder();
            try{
                ArrayList<String> commandLine = new ArrayList<String>();
                commandLine.add("logcat");
                commandLine.add("-d");
                ArrayList<String> commandLineOptions = params[0];
                if (commandLineOptions != null){
                    commandLine.addAll(commandLineOptions);
                }                
                Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[0]));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));                
                String line;
                while ((line = bufferedReader.readLine()) != null){ 
                    log.append(line);
                    log.append(LINE_SEPARATOR);
                }
            }catch (Exception ex){
            	android.util.Log.e(_logTag, "CollectLogTask.doInBackground() ERROR: " + ex.toString());
            } 
            return log;
        }

	    /**
	     * Do this work after the background has finished.
	     * 
	     * @param StringBuilder - A StringBuilder of the log file that was pulled from the phone.
	     */
        @Override
        protected void onPostExecute(StringBuilder log){
    		if(_debug) android.util.Log.v(_logTag, "CollectLogTask.onPostExecute()");
            if (log != null){
                //Truncate if necessary.
                int keepOffset = Math.max(log.length() - MAX_LOG_MESSAGE_LENGTH, 0);
                if (keepOffset > 0){
                    log.delete(0, keepOffset);
                }
                log.insert(0, LINE_SEPARATOR);
                log.insert(0, _context.getString(R.string.log_file_device_info, Common.getApplicationVersion(_context), Build.MODEL, Build.VERSION.RELEASE, Build.DISPLAY));
                //Send Log Info In Email To Developer AKA Me :)
		    	Intent sendEmailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:droidnotify@gmail.com"));
		    	sendEmailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		    	sendEmailIntent.putExtra(Intent.EXTRA_SUBJECT, "Droid Notify Debug Logs");
	    		sendEmailIntent.putExtra(Intent.EXTRA_TEXT, log.toString());
	    		_context.startActivity(sendEmailIntent);
	    		_progressDialog.dismiss();
            }else{
            	_progressDialog.dismiss();
                showErrorDialog(_context.getString(R.string.log_file_retrieval_failure));
            }
        }
        
    }
    
    /**
     * Display an error dialog to the user.
     * 
     * @param errorMessage - The error message to display to the user.
     */
	private void showErrorDialog(String errorMessage){
		if(_debug) android.util.Log.v(_logTag, "Log.showErrorDialog()");
        new AlertDialog.Builder(_context)
        .setTitle(_context.getString(R.string.log_file_error_title))
        .setMessage(errorMessage)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){
                dialog.dismiss();
            }
        })
        .show();
    }
    
	/**
	 * Display a progress dialog window to the user.
	 * 
	 * @param message - The message to display to the user while the dialog is running.
	 */
    private void showProgressDialog(String message){
    	if(_debug) android.util.Log.v(_logTag, "Log.showProgressDialog()");
        _progressDialog = new ProgressDialog(_context);
        _progressDialog.setIndeterminate(true);
        _progressDialog.setMessage(message);
        _progressDialog.setCancelable(true);
        _progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
            public void onCancel(DialogInterface dialog){
            	cancellCollectLogTask();
            }
        });
        _progressDialog.show();
    }
    
    /**
     * Can cell the Collect Log Async Task.
     */
    private void cancellCollectLogTask(){
    	if(_debug) android.util.Log.v(_logTag, "Log.cancellCollectLogTask()");
        if (_collectLogTask != null && _collectLogTask.getStatus() == AsyncTask.Status.RUNNING){
        	_collectLogTask.cancel(true);
        	_collectLogTask = null;
        }
    }
	
}
