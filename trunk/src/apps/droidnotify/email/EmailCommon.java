package apps.droidnotify.email;

public class EmailCommon {

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
		if(address == null){
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
		//if (_debug) Log.v(context, "EmailCommon.removeEmailFormatting() Formatted Email Address: " + address);
		return address.toLowerCase().trim();
	}
	
}
