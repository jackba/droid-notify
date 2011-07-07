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
	private static void writeToCustomLog(int logType, String msg)
	{   
		File logFile = null;
		File directoryStructure = null;
		switch (logType) {
	        case 1:  
	        	logFile = new File("sdcard/Droid Notify/Logs/V/DroidNotifyLog.txt");
	        	directoryStructure = new File("sdcard/Droid Notify/Logs/V");
	        	break;
	        case 2:  
	        	logFile = new File("sdcard/Droid Notify/Logs/D/DroidNotifyLog.txt");
	        	directoryStructure = new File("sdcard/Droid Notify/Logs/D");
	        	break;
	        case 3:  
	        	logFile = new File("sdcard/Droid Notify/Logs/I/DroidNotifyLog.txt"); 
	        	directoryStructure = new File("sdcard/Droid Notify/Logs/I");
	        	break;
	        case 4:  
	        	logFile = new File("sdcard/Droid Notify/Logs/W/DroidNotifyLog.txt"); 
	        	directoryStructure = new File("sdcard/Droid Notify/Logs/W");
	        	break;
	        case 5:  
	        	logFile = new File("sdcard/Droid Notify/Logs/E/DroidNotifyLog.txt");
	        	directoryStructure = new File("sdcard/Droid Notify/Logs/E");
	        	break;
		}
		if (!logFile.exists()){
			try{
				directoryStructure.mkdirs();
				logFile.createNewFile();
			}catch (Exception ex){
				android.util.Log.e(getLogTag(), "Log.writeToCustomLog CREATE ERROR: " + ex.toString());
			}
		}
		try{
	    	//BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
			buf.append(msg);
			buf.newLine();
			buf.close();
		}catch (Exception ex){
			android.util.Log.e(getLogTag(), "Log.writeToCustomLog WRITE ERROR: " + ex.toString());
		}
	}
	
}
