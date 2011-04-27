package apps.droidnotify;

/**
 * 
 * @author Camille Sevigny
 *
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
	 */
	public static String getLogTag(){
		return _logTag;
	}

	/**
	 *  Get debug property.
	 */
	public static boolean getDebug(){
		return _debug;
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 *  Add an entry to the Android LogCat log under the V (Verbose) type.
	 *  @param msg
	 */
	public static void v(String msg) {
		android.util.Log.v(getLogTag(), msg);
	}
	
	/**
	 *  Add an entry to the Android LogCat log under the D (Debug) type.
	 *  @param msg
	 */
	public static void d(String msg) {
		android.util.Log.d(getLogTag(), msg);
	}	
	
	/**
	 *  Add an entry to the Android LogCat log under the I (Info) type.
	 *  @param msg
	 */
	public static void i(String msg) {
		android.util.Log.i(getLogTag(), msg);
	}
	
	/**
	 *  Add an entry to the Android LogCat log under the W (Warning) type.
	 *  @param msg
	 */
	public static void w(String msg) {
		android.util.Log.w(getLogTag(), msg);
	}
	
	/**
	 *  Add an entry to the Android LogCat log under the E (Error) type.
	 *  @param msg
	 */
	public static void e(String msg) {
		android.util.Log.e(getLogTag(), msg);
	}
  
	//================================================================================
	// Private Methods
	//================================================================================
	
}
