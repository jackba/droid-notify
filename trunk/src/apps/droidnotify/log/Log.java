package apps.droidnotify.log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;

/**
 * This class logs messages to the Android log file.
 * 
 * @author Camille Sévigny
 */
public class Log {

	//================================================================================
    // Constants
    //================================================================================
	
	private static int LOG_FILE_MAX_LINES = 2000;
	
	//================================================================================
    // Properties
    //================================================================================
	
	private static boolean _debug = false;

	private static final boolean _AndroidVersion = true;
	private static final boolean _AmazonVersion = false;
	private static final boolean _SamsungVersion = false;
	private static final boolean _SlideMeVersion = false;

	private static Context _context = null;
	private static CollectLogTask _collectLogTask = null;
	private static ClearLogsTask _clearLogsTask = null;
    private ProgressDialog _progressDialog;

	//================================================================================
	// Public Methods
	//================================================================================

	/**
	 *  Get debug property.
	 *  
	 *  @param context - The application context.
	 *  
	 *  @return boolean - Returns true if the log class is set to log entries.
	 */
	public static boolean getDebug(Context context){
		try{
			if(context == null){
				android.util.Log.e(Constants.LOGTAG, "Log.getDebug(context) Context Is Null");
				return _debug;
			}
			if(!_debug){
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
				if(preferences == null){
					android.util.Log.e(Constants.LOGTAG, "Log.getDebug(context) Preferences Are Null");
					return _debug;
				}else{
					_debug = preferences.getBoolean(Constants.DEBUG, false);
					return _debug;
				}
			}else{
				return true;
			}
		}catch(Exception ex){
			android.util.Log.e(Constants.LOGTAG, "Log.getDebug(context) ERROR: " + ex.toString());
			return _debug;
		}
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
	 *  Get AndroidVersion property.
	 *  
	 *  @return boolean - Returns true if we want to show the Android links.
	 */
	public static boolean getAndroidVersion(){
		return _AndroidVersion;
	}

	/**
	 *  Get AmazonVersion property.
	 *  
	 *  @return boolean - Returns true if we want to show the Amazon links.
	 */
	public static boolean getAmazonVersion(){
		return _AmazonVersion;
	}

	/**
	 *  Get SamsungVersion property.
	 *  
	 *  @return boolean - Returns true if we want to show the Samsung links.
	 */
	public static boolean getSamsungVersion(){
		return _SamsungVersion;
	}

	/**
	 *  Get SlideMeVersion property.
	 *  
	 *  @return boolean - Returns true if we want to show the SlideMe links.
	 */
	public static boolean getSlideMeVersion(){
		return _SlideMeVersion;
	}
	
	/**
	 *  Add an entry to the Android LogCat log under the V (Verbose) type.
	 *  
	 *  @param context - The application context.
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void v(Context context, String msg) {
		android.util.Log.v(Constants.LOGTAG, msg);
		if(!appendToExternalLogFile("V", msg)){
			appendToInternalLogFile(context, "V", msg);
		}
	}
	
	/**
	 *  Add an entry to the Android LogCat log under the D (Debug) type.
	 *  
	 *  @param context - The application context.
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void d(Context context, String msg) {
		android.util.Log.d(Constants.LOGTAG, msg);
		if(!appendToExternalLogFile("D", msg)){
			appendToInternalLogFile(context, "D", msg);
		}
	}	
	
	/**
	 *  Add an entry to the Android LogCat log under the I (Info) type.
	 *  
	 *  @param context - The application context.
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void i(Context context, String msg) {
		android.util.Log.i(Constants.LOGTAG, msg);
		if(!appendToExternalLogFile("I", msg)){
			appendToInternalLogFile(context, "I", msg);
		}
	}
	
	/**
	 *  Add an entry to the Android LogCat log under the W (Warning) type.
	 *  
	 *  @param context - The application context.
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void w(Context context, String msg) {
		android.util.Log.w(Constants.LOGTAG, msg);
		if(!appendToExternalLogFile("W", msg)){
			appendToInternalLogFile(context, "W", msg);
		}
	}
	
	/**
	 *  Add an entry to the Android LogCat log under the E (Error) type.
	 *  
	 *  @param context - The application context.
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void e(Context context, String msg) {
		android.util.Log.e(Constants.LOGTAG, msg);
		if(!appendToExternalLogFile("E", msg)){
			appendToInternalLogFile(context, "E", msg);
		}
	}
	
	/**
	 * Read the logs from the users phone and email them to the developer.
	 * 
	 * @param context - The application context.
	 */
	public static void collectAndSendLog(Context context){
		_context = context;
		_collectLogTask = (CollectLogTask) (new Log()).new CollectLogTask().execute();
    }
	
	/**
	 * Clear the logs from the users phone.
	 * 
	 * @param context - The application context.
	 */
	public static void clearLogs(Context context){
		_context = context;
		_clearLogsTask = (ClearLogsTask) (new Log()).new ClearLogsTask().execute();
    }
    
    /**
     * Determine if the external storage can be written to.
     * 
     * @return boolean - Returns true if the external storage can be written to.
     */
    public static boolean writeExternalStorage(){
    	//Check state of external storage.
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)){
		    //We can read and write the media. Do nothing.
			return true;
		}else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
		    // We can only read the media.
		    return false;
		}else{
		    // Something else is wrong. It may be one of many other states, but all we need to know is we can neither read nor write.
		    return false;
		}
    }
 
	//================================================================================
	// Private Methods
	//================================================================================

    /**
     * Append text to the log file being kept internally.
     * 
     * @param context - The application context.
     * @param level - The log level.
     * @param msg - The message to append to the log file.
     */
    @SuppressWarnings("deprecation")
	@SuppressLint("WorldReadableFiles")
	private static boolean appendToInternalLogFile(Context context, String level, String msg){
    	try{
        	_context = context;
    		FileOutputStream fileOutputStream = context.openFileOutput("Log.txt", Context.MODE_WORLD_READABLE | Context.MODE_APPEND);
    		String logString = level + " - " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SSS", Locale.US).format(System.currentTimeMillis()) + " - " + Constants.LOGTAG + " - " + msg + "\n";
    		fileOutputStream.write(logString.getBytes());
    		fileOutputStream.close();
			return true; 
		}catch (Exception ex){
			android.util.Log.e(Constants.LOGTAG, "Log.appendToInternalLogFile() ERROR: " + ex.toString());
			return false;
		}
    }
	
    /**
     * Append text to the log file being kept on the SD card.
     * 
     * @param context - The application context.
     * @param msg - The message to append to the log file.
     */
    private static boolean appendToExternalLogFile(String level, String msg){
    	try{
    		//Check to see if we can write to the external storage.
			if(!writeExternalStorage()){
			    return false;
			}
	    	File logFilePath = Environment.getExternalStoragePublicDirectory("DroidNotify/Log");
	    	File logFile = new File(logFilePath, "DroidNotifyLog.txt");
    		logFilePath.mkdirs();
    		if(!logFile.exists()){
    			logFile.createNewFile();
    		}
    		if(!logFile.canWrite()){
    			android.util.Log.e(Constants.LOGTAG, "Log.appendToExternalLogFile() External Log File Not Writable");
    			return false;
    		}
    		//Write each preference to the text file.
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logFile, true), msg.length());
			bufferedWriter.append(level + " - " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SSS", Locale.US).format(System.currentTimeMillis()) + " - " + Constants.LOGTAG + " - " + msg);
			bufferedWriter.newLine();
			bufferedWriter.flush();
			bufferedWriter.close();
			return true; 
		}catch (Exception ex){
			android.util.Log.e(Constants.LOGTAG, "Log.appendToExternalLogFile() Wrtie File ERROR: " + ex.toString());
			return false;
		}
    }
	
	/**
	 * Shrink the log file if it's too large.
	 */
	private static void shrinkLogFile(){
		if(!readExternalStorage()){
			return;
		}
    	File logFilePath = Environment.getExternalStoragePublicDirectory("DroidNotify/Log");
    	File logFile = new File(logFilePath, "DroidNotifyLog.txt");
    	File logFileTmp = new File(logFilePath, "DroidNotifyLogTMP.txt");
    	try{
    		boolean startWriting = false;
    		int logFileLines = countNumberOfLines(logFile.getAbsolutePath());
			if(logFileLines > LOG_FILE_MAX_LINES){
				logFile.renameTo(logFileTmp);
				logFile.delete();
				logFile.createNewFile();
				int currentLine = 0;
	    	    BufferedReader bufferedReader = new BufferedReader(new FileReader(logFileTmp));
	    	    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logFile, true)); 
	    	    String line;
	    	    while((line = bufferedReader.readLine()) != null){
	    	    	currentLine++;
	    	    	if(startWriting){
	    	    		bufferedWriter.append(line);
	    				bufferedWriter.newLine();
	    	    	}else{
	    	    		startWriting = (logFileLines - currentLine) <= LOG_FILE_MAX_LINES;
	    	    	}
				}
				bufferedWriter.flush();
				bufferedWriter.close();
				bufferedReader.close();
				logFileTmp.delete();
			}
    	}catch(IOException ex){
    		android.util.Log.e(Constants.LOGTAG, "Log.shrinkLogFile() ERROR: " + ex.toString());
    		return;
    	}
	}
	
	/**
	 * Count the number of lines in a text file.
	 * 
	 * @param filename - The file that we want the number of lines.
	 * 
	 * @return int - Returns the number of lines in the text file.
	 * 
	 */
	private static int countNumberOfLines(String file) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(file));
	    try{
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        while((readChars = is.read(c)) != -1){
	            for(int i = 0; i < readChars; ++i){
	                if(c[i] == '\n')
	                    ++count;
	            }
	        }
	        return count;
	    }finally{
	        is.close();
	    }
	}
	
    /*
     * Collect the log files.
     */
    private class CollectLogTask extends AsyncTask<Void, Void, Boolean>{
        
        /**
         * Do this work before the background task starts.
         */  	
        @Override
        protected void onPreExecute(){
            showProgressDialog(_context.getString(R.string.log_file_acquiring_system_logs));
        }
        
	    /**
	     * Do this work in the background.
	     */
        @Override
        protected Boolean doInBackground(Void... params){
            try{
    			//Shrink the file to ensure that it never gets too large.
    			shrinkLogFile();
    			//Export the current application preferences.
    			return Common.exportApplicationPreferences(_context, "DroidNotify/Preferences", "DroidNotifyPreferences.txt", false);
            }catch(Exception ex){
            	android.util.Log.e(Constants.LOGTAG, "Log.collectAndSendLog() ERROR: " + ex.toString());
            	showErrorDialog(_context, ex.toString());
            	return false;
            }
        }

	    /**
	     * Do this work after the background has finished.
	     */
        @Override
        protected void onPostExecute(Boolean result){
 
	    	Intent sendEmailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
	    	sendEmailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	sendEmailIntent.setType("plain/text");
	    	sendEmailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"droidnotify@gmail.com"});
	    	sendEmailIntent.putExtra(Intent.EXTRA_SUBJECT, "Droid Notify Pro - Debug Logs");
			sendEmailIntent.putExtra(Intent.EXTRA_TEXT, _context.getString(R.string.log_file_device_info, Common.getApplicationVersion(_context), Build.MODEL, Build.VERSION.RELEASE, Build.DISPLAY) + "\n\nThe application logs are attached.");
	    	ArrayList<Uri> uris = new ArrayList<Uri>();
	    	
	    	//Try to get the external storage logs.
	    	if(readExternalStorage()){

		    	File externalLogFilePath = Environment.getExternalStoragePublicDirectory("DroidNotify/Log");
		    	File externalLogFile = new File(externalLogFilePath, "DroidNotifyLog.txt");
		    	if(externalLogFile.exists()){
		    		uris.add(Uri.fromFile(externalLogFile));
		    	}
	
		    	File externalPreferencesFilePath = Environment.getExternalStoragePublicDirectory("DroidNotify/Preferences");
		    	File externalPreferencesFile = new File(externalPreferencesFilePath, "DroidNotifyPreferences.txt");
		    	if(externalPreferencesFile.exists()){
		    		uris.add(Uri.fromFile(externalPreferencesFile));
		    	}
		    	
	    	}
	    	
	    	//Try to get the internal logs.
	    	
	    	File internalLogFile = new File(_context.getFilesDir(), "Log.txt");
	    	if(internalLogFile.exists()){
	    		uris.add(Uri.fromFile(internalLogFile));
	    	}
	
	    	File internalPeferencesFile = new File(_context.getFilesDir(), "Preferences.txt");
	    	if(internalPeferencesFile.exists()){
	    		uris.add(Uri.fromFile(internalPeferencesFile));
	    	}

     		sendEmailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
     		
    		try{
    			_context.startActivity(sendEmailIntent);
    		}catch(Exception ex){
    			android.util.Log.e(Constants.LOGTAG, "Log.CollectLogTask.onPostExecute() ERROR: " + ex.toString());
    		}
			
			_progressDialog.dismiss();
			
        }
        
    }    
    
    /*
     * Clear the log files.
     */
    private class ClearLogsTask extends AsyncTask<Void, Void, Boolean>{
        
        /**
         * Do this work before the background task starts.
         */  	
        @Override
        protected void onPreExecute(){
            showProgressDialog(_context.getString(R.string.removing_logs));
        }
        
	    /**
	     * Do this work in the background.
	     * 
	     * @param params - An ArrayList of the command line parameters to use.
	     */
        @Override
        protected Boolean doInBackground(Void... params){
            try{
    			//Export the current application preferences.
    			return Common.clearLogFiles(_context);
            }catch(Exception ex){
            	android.util.Log.e(Constants.LOGTAG, "Log.collectAndSendLog() ERROR: " + ex.toString());
            	showErrorDialog(_context, ex.toString());
            	return false;
            }
        }

	    /**
	     * Do this work after the background has finished.
	     * 
	     * @param Boolean - Boolean result flag.
	     */
        @Override
        protected void onPostExecute(Boolean result){
			_progressDialog.dismiss();			
        }
        
    }
    
    /**
     * Display an error dialog to the user.
     * 
     * @param errorMessage - The error message to display to the user.
     */
	private static void showErrorDialog(Context context, String errorMessage){
        Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.log_file_error_title));
        builder.setMessage(errorMessage);
        try{
        	builder.setIcon(android.R.drawable.ic_dialog_alert);
        }catch(Exception ex){
        	//Don't set the icon if this fails.
        }
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){
                dialog.dismiss();
            }
        });
        builder.show();
    }
    
	/**
	 * Display a progress dialog window to the user.
	 * 
	 * @param message - The message to display to the user while the dialog is running.
	 */
    private void showProgressDialog(String message){
        _progressDialog = new ProgressDialog(_context);
        _progressDialog.setIndeterminate(true);
        _progressDialog.setMessage(message);
        _progressDialog.setCancelable(true);
        _progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
            public void onCancel(DialogInterface dialog){
            	cancellCollectLogTask();
            	cancellCleartLogsTask();
            }
        });
        _progressDialog.show();
    }
    
    /**
     * Cancell the Collect Log Async Task.
     */
    private void cancellCollectLogTask(){
        if (_collectLogTask != null && _collectLogTask.getStatus() == AsyncTask.Status.RUNNING){
        	_collectLogTask.cancel(true);
        	_collectLogTask = null;
        }
    }
    
    /**
     * Cancell the Clear Logs Async Task.
     */
    private void cancellCleartLogsTask(){
        if (_clearLogsTask != null && _clearLogsTask.getStatus() == AsyncTask.Status.RUNNING){
        	_clearLogsTask.cancel(true);
        	_clearLogsTask = null;
        }
    }
    
    /**
     * Determine if the external storage can be read.
     * 
     * @return boolean - Returns true if the external storage can be read.
     */
    private static boolean readExternalStorage(){
    	//Check state of external storage.
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)){
		    //We can read and write the media. Do nothing.
			return true;
		}else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
		    // We can only read the media.
			return true;
		}else{
		    // Something else is wrong. It may be one of many other states, but all we need to know is we can neither read nor write.
		    return false;
		}
    }
	
}
