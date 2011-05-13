package apps.droidnotify;

/**
 * This class logs messages to the Android log file.
 * 
 * @author Camille Sevigny
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
	}
	
	/**
	 *  Add an entry to the Android LogCat log under the D (Debug) type.
	 *  
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void d(String msg) {
		android.util.Log.d(getLogTag(), msg);
	}	
	
	/**
	 *  Add an entry to the Android LogCat log under the I (Info) type.
	 *  
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void i(String msg) {
		android.util.Log.i(getLogTag(), msg);
	}
	
	/**
	 *  Add an entry to the Android LogCat log under the W (Warning) type.
	 *  
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void w(String msg) {
		android.util.Log.w(getLogTag(), msg);
	}
	
	/**
	 *  Add an entry to the Android LogCat log under the E (Error) type.
	 *  
	 *  @param msg - Entry to be made to the log file.
	 */
	public static void e(String msg) {
		android.util.Log.e(getLogTag(), msg);
	}
  
	//================================================================================
	// Private Methods
	//================================================================================
	
}
