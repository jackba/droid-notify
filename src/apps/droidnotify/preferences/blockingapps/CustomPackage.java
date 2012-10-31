package apps.droidnotify.preferences.blockingapps;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * This class holds blacklist information.
 * 
 * @author Camille Sévigny
 */
public class CustomPackage {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private Context _context = null;
	private long _id = -1;
	private String _packageName = null;
	private String _packageDisplayName = null;
	private Bitmap _packageIcon = null;
	private boolean _checked = false;
	
	//================================================================================
	// Constructors
	//================================================================================

	/**
	 * Class Constructor
	 */
	public CustomPackage(Context context, long id, String packageName, String packageDisplayName, Bitmap packageIcon){
		_context = context;
		_id = id;
		_packageName = packageName;
		if(packageDisplayName != null){
			_packageDisplayName = packageDisplayName;
		}else{
			_packageDisplayName = BlockingAppsCommon.getPackageDisplayName(_context, _packageName);
		}
		if(packageIcon != null){
			_packageIcon = packageIcon;
		}else{
			_packageIcon = BlockingAppsCommon.getPackageIcon(_context, _packageName);
		}
	}
	
	/**
	 * Class Constructor
	 */
	public CustomPackage(Context context, String packageName){
		_context = context;
		_packageName = packageName;
		_packageDisplayName = BlockingAppsCommon.getPackageDisplayName(_context, _packageName);
		_packageIcon = BlockingAppsCommon.getPackageIcon(_context, _packageName);
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

	public String getPackageName(){
		return _packageName;
	}

	public void setPackageName(String packageName){
		_packageName = packageName;
	}
	
	public String getPackageDisplayName(){
		return _packageDisplayName;
	}

	public void setPackageDisplayName(String packageDisplayName){
		_packageDisplayName = packageDisplayName;
	}
	
	public Bitmap getPackageIcon(){
		return _packageIcon;
	}
	
	public void setPackageIcon(Bitmap packageIcon){
		_packageIcon = packageIcon;
	}
	
	public boolean getChecked(){
		return _checked;
	}
	
	public void setChecked(boolean checked){
		_checked = checked;
	}	

	@Override
	public String toString(){
		return _packageDisplayName == null ? _packageName : _packageDisplayName;		
	}
	
}