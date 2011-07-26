package apps.droidnotify.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import android.os.Environment;

/**
 * This class logs messages to the Android log file.
 * 
 * @author Camille Sévigny
 */
public class Log {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private static final String _logTag = "DroidNotify";
	private static final boolean _debug = true;
	private static final boolean _debugCalendar = false;
	private static final boolean _showAndroidRateAppLink = true;
	private static final boolean _showAmazonRateAppLink = false;

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
	 *  Get debugCalendar property.
	 *  
	 *  @return boolean - Returns true if the log class is set to debug calendar entries.
	 */
	public static boolean getDebugCalendar(){
		return _debugCalendar;
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
	 *  Add an entry to the Android LogCat log under the V (Verbose) type.
	 *  
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void v(String msg) {
		if(_debug){
			android.util.Log.v(getLogTag(), msg);
			writeToCustomLog(1,msg);
		}
	}
	
	/**
	 *  Add an entry to the Android LogCat log under the D (Debug) type.
	 *  
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void d(String msg) {
		if(_debug){
			android.util.Log.d(getLogTag(), msg);
			writeToCustomLog(2,msg);
		}
	}	
	
	/**
	 *  Add an entry to the Android LogCat log under the I (Info) type.
	 *  
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void i(String msg) {
		if(_debug){
			android.util.Log.i(getLogTag(), msg);
			writeToCustomLog(3,msg);
		}
	}
	
	/**
	 *  Add an entry to the Android LogCat log under the W (Warning) type.
	 *  
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void w(String msg) {
		if(_debug){
			android.util.Log.w(getLogTag(), msg);
			writeToCustomLog(4,msg);
		}
	}
	
	/**
	 *  Add an entry to the Android LogCat log under the E (Error) type.
	 *  
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void e(String msg) {
		if(_debug){
			android.util.Log.e(getLogTag(), msg);
			writeToCustomLog(5,msg);
		}
	}
  
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 *  Writes a custom log file to the SD card of the phone.
	 * 
	 * @param text - String value to append to the custom log.
	 */
	private static void writeToCustomLog(int logType, String msg){
		//Check state of external storage.
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)){
		    //We can read and write the media. Do nothing.
		}else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
		    // We can only read the media.
			android.util.Log.v(getLogTag(), "Log.writeToCustomLog() External Storage Read Only State");
		    return;
		}else{
		    // Something else is wrong. It may be one of many other states, but all we need to know is we can neither read nor write
			android.util.Log.v(getLogTag(), "Log.writeToCustomLog() External Storage Can't Write Or Read State");
		    return;
		}
		File logFilePath = null;
		switch (logType) {
	        case 1:{
	        	logFilePath = Environment.getExternalStoragePublicDirectory("Droid Notify/Logs/V");
	        	break;
			}
	        case 2:{
	        	logFilePath = Environment.getExternalStoragePublicDirectory("Droid Notify/Logs/D");
	        	break;
	        }
	        case 3:{ 
	        	logFilePath = Environment.getExternalStoragePublicDirectory("Droid Notify/Logs/I");
	        	break;
	        }
	        case 4:{ 
	        	logFilePath = Environment.getExternalStoragePublicDirectory("Droid Notify/Logs/W");
	        	break;
	        }
	        case 5:{
	        	logFilePath = Environment.getExternalStoragePublicDirectory("Droid Notify/Logs/E");
	        	break;
	        }
		}
	    File logFile = new File(logFilePath, "DroidNotifyLog.txt");
	    try{
	    	logFilePath.mkdirs();
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
			buf.append(msg);
			buf.newLine();
			buf.close();
		}catch (Exception ex){
			android.util.Log.e(getLogTag(), "Log.writeToCustomLog WRITE ERROR: " + ex.toString());
		}
	}
	
}
