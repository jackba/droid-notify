package apps.droidnotify;

/**
 * This is a small utility class to help format and compare phone numbers.
 * 
 * @author Camille Sévigny
 */
public class PhoneNumber {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug;
	private String _phoneNumber;

	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * Class Constructor.
	 * 
	 * @param phoneNumber - String of the phone number.
	 */
	public PhoneNumber(String phoneNumber){
		_debug = Log.getDebug();
		if (_debug) Log.v("PhoneNumber.PhoneNumber()");
		setPhoneNumber(formatNumber(phoneNumber));
	}
	
	//================================================================================
	// Public Methods
	//=================================================================================
	
	/**
	 * Set the phoneNumber property.
	 * 
	 * @param phoneNumber - String of phone number property
	 */
	public void setPhoneNumber(String phoneNumber){
		if (_debug) Log.v("PhoneNumber.setPhoneNumber() PhoneNumber: " + phoneNumber);
		_phoneNumber = phoneNumber;
	}
	
	/**
	 * Get the phoneNumber property.
	 * 
	 * @return phoneNumber - String of phone number property
	 */
	public String getPhoneNumber(){
		if (_debug) Log.v("PhoneNumber.getPhoneNumber() PhoneNumber: " + _phoneNumber);
		return _phoneNumber;
	}
	  
	//================================================================================
	// Private Methods
	//================================================================================	
	
	/**
	 * Format the the phoneNumber property.
	 * Standardize the number for comparison purposes.
	 * 
	 * @param phoneNumber - String of original phone number property
	 * @return phoneNumber - String of formatted phone number property
	 */
	private String formatNumber(String  phoneNumber){
		if (_debug) Log.v("PhoneNumber.formatNumber()");
		phoneNumber = phoneNumber.replace("-", "");
		if(phoneNumber.substring(0,1).equals("1")){
			phoneNumber = phoneNumber.substring(1);
		}
		return phoneNumber;
	}
	
}
