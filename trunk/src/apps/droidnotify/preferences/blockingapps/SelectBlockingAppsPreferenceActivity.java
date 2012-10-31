package apps.droidnotify.preferences.blockingapps;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

public class SelectBlockingAppsPreferenceActivity extends ListActivity {

	//================================================================================
    // Properties
    //================================================================================
	
	private static boolean _debug = false;
	private Context _context = null;
	private ProgressBar _progressBar = null;
	private TextView _okTextView = null;
	List<CustomPackage> _packageValues = null;
	boolean[] _selectedPackages = null;
    private SelectBlockingAppsPreferenceActivity _selectBlockingAppsPreferenceActivity = null;

	//================================================================================
	// Public Methods
	//================================================================================
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		_debug = Log.getDebug();
		if(_debug) Log.v("SelectBlockingAppsPreferenceActivity.onCreate()");
		_context = getApplicationContext();
	    _selectBlockingAppsPreferenceActivity = this;
	    Common.setApplicationLanguage(_context, this);
		setContentView(R.layout.select_blocking_apps_preference_activity);
	    initLayoutItems();
	    //Need this for ICS, crashes without it. It needs at least one item in the list.
		int layoutResource = 0;
		try{
			layoutResource = android.R.layout.simple_list_item_1;
		}catch(Exception ex){
			layoutResource = R.layout.simple_list_item;
		}
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, layoutResource, new String[]{""});
    	setListAdapter(adapter);
    	//Load the items in the list now.
	    new loadPackagesAsyncTask().execute();
	}

	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Initialize the layout items.
	 */
	private void initLayoutItems(){
		if(_debug) Log.v("SelectBlockingAppsPreferenceActivity.initLayoutItems()");	
		_progressBar = (ProgressBar)findViewById(R.id.progress_bar);
		_okTextView = (TextView)findViewById(R.id.ok_button);
		_okTextView.setBackgroundResource(R.drawable.preference_row_click);
		_okTextView.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
        		finish();
        	}
		});
	} 

	/**
	 * Load packages.
	 * 
	 * @author Camille Sévigny
	 */
	private class loadPackagesAsyncTask extends AsyncTask<Void, Void, Void> {

		/**
		 * Setup the Progress Dialog.
		 */
	    protected void onPreExecute(){
			if(_debug) Log.v("SelectBlockingAppsPreferenceActivity.loadPackagesAsyncTask.onPreExecute()");
			_progressBar.setVisibility(View.VISIBLE);
	    }
	    /**
	     * Do this work in the background.
	     * 
	     * @param params
	     */
	    protected Void doInBackground(Void... params){
			if(_debug) Log.v("SelectBlockingAppsPreferenceActivity.loadPackagesAsyncTask.doInBackground()");		    
			//Load the activity's ListView.
			_packageValues = getAllValues();
			return null;
	    }
	    /**
	     * Stop the Progress Dialog and do any post background work.
	     * 
	     * @param result
	     */
	    protected void onPostExecute(Void res){
			if(_debug) Log.v("SelectBlockingAppsPreferenceActivity.loadPackagesAsyncTask.onPostExecute()");
			_selectBlockingAppsPreferenceActivity.setListAdapter(new SelectBlockingAppsArrayAdaptor(_context, R.layout.package_listitem, _packageValues, _selectBlockingAppsPreferenceActivity));
			_progressBar.setVisibility(View.GONE);
	    }
	}
	
	/**
	 * Get a list of the phones packages.
	 * 
	 * @return List<CustomPackageInfo> - A list of the phones packages and info for each.
	 */
	private List<CustomPackage> getAllValues(){
		if(_debug) Log.v("SelectBlockingAppsPreferenceActivity.getAllValues()");
        try{
        	List<CustomPackage> packageInfoArray = new ArrayList<CustomPackage>();
        	final PackageManager packageManager = getPackageManager();
        	List<ApplicationInfo> installedPackages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        	int size = installedPackages.size();
        	int ignorePackagesArraySize = Constants.IGNORE_PACKAGES_ARRAY.length;
        	boolean skipPackage = false;
    	    for(int i=0; i<size; i++){
    	    	skipPackage = false;
    	    	String packageName = installedPackages.get(i).packageName;
    	    	//Log.v("SelectBlockingAppsPreferenceActivity.getAllValues() PackageName: " + packageName);
    	    	//Skip certain packages ============================================
    	    	if(packageName.startsWith("apps.droidnotify")){
    	    		skipPackage = true;
    	    	}else if(packageName.startsWith("com.droidnotify")){
    	    		skipPackage = true;
    	    	}else{
	    	    	for(int j=0; j<ignorePackagesArraySize; j++){
	    	    		if(packageName.equals(Constants.IGNORE_PACKAGES_ARRAY[j])) skipPackage = true;
	    	    	}
    	    	}
    	    	//==================================================================
    	    	if(!skipPackage){
	    	    	CustomPackage customPackageInfo = new CustomPackage(_context, packageName);
	    	    	packageInfoArray.add(customPackageInfo);
    	    	}
    	    }
    	    packageInfoArray = removeUninstalledPackages(packageInfoArray);
	        return packageInfoArray;
		}catch(Exception ex){
			Log.e("SelectBlockingAppsPreferenceActivity.getAllValues() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Remove any packages in the user preferences that no longer are installed on the users phone.
	 */
	private List<CustomPackage> removeUninstalledPackages(List<CustomPackage> packageInfoArray){
		if(_debug) Log.v("SelectBlockingAppsPreferenceActivity.removeUninstalledPackages()");
		try{
			String[] packagesArray = BlockingAppsCommon.getSelectedPackageNames(_context);
			if(packagesArray == null){
				//if(_debug) Log.v("SelectBlockingAppsPreferenceActivity.removeUninstalledPackages() PackageNames is null. Exiting...");
				return packageInfoArray;
			}
			if(packageInfoArray == null){
				//if(_debug) Log.v("SelectBlockingAppsPreferenceActivity.removeUninstalledPackages() PackageInfoArray is null. Exiting...");
				return packageInfoArray;
			}
			int packagesArraySize = packagesArray.length;
			int packageInfoArraySize = packageInfoArray.size();
			boolean packageFound = false;
			for(int i=packagesArraySize-1; i>=0; i--){
				packageFound = false;
				String packageName = packagesArray[i];
				for(int j=0; j<packageInfoArraySize; j++){
					if(packageName.equals(packageInfoArray.get(j).getPackageName())){
						packageFound = true;
						break;
					}
				}
				if(!packageFound){
					//Update user preferences. Remove packages that no longer are on the users phone.
			    	BlockingAppsCommon.setBlockingAppPackage(_context, packageName, false);
			    	packageInfoArray.remove(i);
			    	//if(_debug) Log.v("SelectBlockingAppsPreferenceActivity.removeUninstalledPackages() Package Removed: " + packageName);
				}
			}
		}catch(Exception ex){
			Log.e("SelectBlockingAppsPreferenceActivity.removeUninstalledPackages() ERROR: " + ex.toString());
		}
		return packageInfoArray;
	}
	
}
