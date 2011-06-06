package apps.droidnotify;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

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

	//================================================================================
	// Constructors
	//================================================================================
	
	//================================================================================
	// Accessors
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
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 *  Add an entry to the Android LogCat log under the V (Verbose) type.
	 *  
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void v(String msg) {
		android.util.Log.v(getLogTag(), msg);
		writeToCustomLog("V: " + msg);
	}
	
	/**
	 *  Add an entry to the Android LogCat log under the D (Debug) type.
	 *  
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void d(String msg) {
		android.util.Log.d(getLogTag(), msg);
		writeToCustomLog("D: " + msg);
	}	
	
	/**
	 *  Add an entry to the Android LogCat log under the I (Info) type.
	 *  
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void i(String msg) {
		android.util.Log.i(getLogTag(), msg);
		writeToCustomLog("I: " + msg);
	}
	
	/**
	 *  Add an entry to the Android LogCat log under the W (Warning) type.
	 *  
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void w(String msg) {
		android.util.Log.w(getLogTag(), msg);
		writeToCustomLog("W: " + msg);
	}
	
	/**
	 *  Add an entry to the Android LogCat log under the E (Error) type.
	 *  
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void e(String msg) {
		android.util.Log.e(getLogTag(), msg);
		writeToCustomLog("E: " + msg);
	}
  
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 *  Writes a custom log file to the SD card of the phone.
	 * 
	 * @param text - String value to append to the custom log.
	 */
	private static void writeToCustomLog(String text)
	{       
		File logFile = new File("sdcard/DroidNotifyLog.txt");
		if (!logFile.exists()){
			try{
				logFile.createNewFile();
			}catch (Exception ex){
				//Do Nothing
			}
		}
		try{
	    	//BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
			buf.append(text);
			buf.newLine();
			buf.close();
		}catch (Exception ex){
			//Do Nothing
		}
	}
	
}
