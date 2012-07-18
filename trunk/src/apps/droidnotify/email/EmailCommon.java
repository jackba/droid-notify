package apps.droidnotify.email;

import apps.droidnotify.log.Log;

public class EmailCommon {

	//================================================================================
    // Properties
    //================================================================================
	
	private static boolean _debug = false; 
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Remove formatting from email addresses.
	 * 
	 * @param address - String of original email address.
	 * 
	 * @return String - String of email address with no formatting.
	 */
	public static String removeEmailFormatting(String address){
		_debug = Log.getDebug();
		//if (_debug) Log.v("EmailCommon.removeEmailFormatting()");
		if(address == null){
			if (_debug) Log.v("EmailCommon.removeEmailFormatting() Email Address: " + address);
			return null;
		}
		if(address.contains("<") && address.contains(">")){
			address = address.substring(address.indexOf("<") + 1,address.indexOf(">"));
		}
		if(address.contains("(") && address.contains(")")){
			address = address.substring(address.indexOf("(") + 1,address.indexOf(")"));
		}
		if(address.contains("[") && address.contains("]")){
			address = address.substring(address.indexOf("[") + 1,address.indexOf("]"));
		}
		//if (_debug) Log.v("EmailCommon.removeEmailFormatting() Formatted Email Address: " + address);
		return address.toLowerCase().trim();
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
}
