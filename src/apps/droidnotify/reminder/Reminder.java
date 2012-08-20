package apps.droidnotify.reminder;

/**
 * This class holds reminder information.
 * 
 * @author Camille Sévigny
 */
public class Reminder{
	
	//================================================================================
    // Properties
    //================================================================================

	private long _id = -1;
	private String _action = null;
	private boolean _dismissed = false;
	
	//================================================================================
	// Constructors
	//================================================================================

	/**
	 * Class Constructor
	 */
	public Reminder(long id, String action){
		_id = id;
		_action = action;
		_dismissed = true;
	}	
	
	/**
	 * Class Constructor
	 */
	public Reminder(long id, String action, boolean dismissed){
		_id = id;
		_action = action;
		_dismissed = dismissed;
	}

	//================================================================================
	// Public Methods
	//================================================================================
	
	public long getId(){
		return _id;
	}

	public void setId(long id){
		_id = id;
	}

	public String getAction(){
		return _action;
	}

	public void setAction(String action){
		_action = action;
	}

	public boolean getDismissed(){
		return _dismissed;
	}

	public void setDismissed(boolean dismissed){
		_dismissed = dismissed;
	}

	@Override
	public String toString(){
		return _action;	
	}
	
}
