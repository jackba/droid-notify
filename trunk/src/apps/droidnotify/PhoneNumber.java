package apps.droidnotify;

/**
 * 
 * @author csevigny
 *
 */
public class PhoneNumber {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private String _phoneNumber;

	//================================================================================
	// Constructors
	//================================================================================
	
	public PhoneNumber(String phoneNumber){
		if (Log.getDebug()) Log.v("PhoneNumber.PhoneNumber()");
		setPhoneNumber(formatNumber(phoneNumber));
	}
	
	//================================================================================
	// Accessors
	//================================================================================
	
	/**
	 * Set the phoneNumber property.
	 * 
	 * @param phoneNumber
	 */
	public void setPhoneNumber(String phoneNumber){
		if (Log.getDebug()) Log.v("PhoneNumber.setPhoneNumber()");
		_phoneNumber = phoneNumber;
	}
	
	/**
	 * Get the phoneNumber property.
	 * 
	 * @return
	 */
	public String getPhoneNumber(){
		if (Log.getDebug()) Log.v("PhoneNumber.getPhoneNumber()");
		return _phoneNumber;
	}
	  
	//================================================================================
	// Public Methods
	//================================================================================
	  
	//================================================================================
	// Private Methods
	//================================================================================	
	
	/**
	 * Format the the phoneNumber property.
	 * Standardize the number for comparison purposes.
	 * 
	 * @param phoneNumber
	 * @return phoneNumber
	 */
	private String formatNumber(String  phoneNumber){
		if (Log.getDebug()) Log.v("PhoneNumber.formatNumber()");
		phoneNumber = phoneNumber.replace("-", "");
		if(phoneNumber.substring(0,1).equals("1")){
			phoneNumber = phoneNumber.substring(1);
		}
		if (Log.getDebug()) Log.v("PhoneNumber.formatNumber() Formatted Number: " + phoneNumber);
		return phoneNumber;
	}
	
}
