package apps.droidnotify.preferences.blockingapps;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import apps.droidnotify.R;
//import apps.droidnotify.log.Log;

public class SelectBlockingAppsArrayAdaptor extends ArrayAdapter<CustomPackage> {
	
	//================================================================================
    // Properties
    //================================================================================
	
	//private static boolean _debug = false;
	private Context _context = null;
	private LayoutInflater _inflater = null;
    private List<CustomPackage> _customPackageInfoArray;
    private SelectBlockingAppsPreferenceActivity _selectBlockingAppsPreferenceActivity = null;
	
	//================================================================================
	// Constructors
	//================================================================================

    public SelectBlockingAppsArrayAdaptor(Context context, int textViewResourceId, List<CustomPackage> customPackageInfoArray, SelectBlockingAppsPreferenceActivity selectBlockingAppsPreferenceActivity) {
	    super(context, textViewResourceId, customPackageInfoArray);
		//_debug = Log.getDebug();
	    //if (_debug) Log.v("SelectBlockingAppsArrayAdaptor.SelectBlockingAppsArrayAdaptor()");
	    _context = context;
		_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    _customPackageInfoArray = customPackageInfoArray;
	    _selectBlockingAppsPreferenceActivity = selectBlockingAppsPreferenceActivity;
    }

	//================================================================================
	// Public Methods
	//================================================================================

	@Override
    public View getView(int position, View view, ViewGroup parent){
		//_debug = Log.getDebug();
	    //if (_debug) Log.v("SelectBlockingAppsArrayAdaptor.getView()");
		final ViewHolder viewHolder;
		if(view == null){
			view = _inflater.inflate(R.layout.package_listitem, parent, false);
			// Creates a ViewHolder and store references to the children views we want to bind data to.
			viewHolder = new ViewHolder();
			viewHolder.imageView = (ImageView)view.findViewById(R.id.image);
			viewHolder.textView = (TextView)view.findViewById(R.id.text);
			viewHolder.checkBox = (CheckBox)view.findViewById(R.id.check);
			// Store in tag
			view.setTag(viewHolder);
		}else{
			// Get the ViewHolder back to get fast access to the Icon, TextView and CheckBox
			viewHolder = (ViewHolder) view.getTag();
		}
		final CustomPackage customPackageInfo = _customPackageInfoArray.get(position);
		Bitmap packageIcon = customPackageInfo.getPackageIcon();
		if(packageIcon != null){
			viewHolder.imageView.setImageBitmap(packageIcon);
		}else{
			viewHolder.imageView.setImageResource(R.drawable.transparent);
		}
		String packageDisplayName = customPackageInfo.toString();
		viewHolder.textView.setText(packageDisplayName);
		if(BlockingAppsCommon.isSelectedPackage(_context, customPackageInfo.getPackageName())){
			viewHolder.checkBox.setChecked(true);
			customPackageInfo.setChecked(true);
		}else{
			viewHolder.checkBox.setChecked(false);
			customPackageInfo.setChecked(false);
		}
		view.setBackgroundResource(R.drawable.preference_row_click);
		view.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
        		//if (_debug) Log.v("SelectBlockingAppsArrayAdaptor() Row View Clicked");	        		
            	CheckBox checkBox = (CheckBox) view.findViewById(R.id.check);
            	checkBox.setChecked(!checkBox.isChecked());
            	new setSelectedPackageAsyncTask().execute(customPackageInfo.getPackageName(), String.valueOf(checkBox.isChecked()));
        	}
		});
		viewHolder.checkBox.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
        		//if (_debug) Log.v("SelectBlockingAppsArrayAdaptor() Row View Clicked");	        		
            	CheckBox checkBox = (CheckBox) view.findViewById(R.id.check);
            	new setSelectedPackageAsyncTask().execute(customPackageInfo.getPackageName(), String.valueOf(checkBox.isChecked()));
        	}
		});
	    return view;
    }

	//================================================================================
	// Private Methods
	//================================================================================

	// View holder to references to the views
	private static class ViewHolder {
		public ImageView imageView;
		public TextView textView;
		public CheckBox checkBox;
	}
	
	/**
	 * Set the selected package.
	 * 
	 * @author Camille Sévigny
	 */
	private class setSelectedPackageAsyncTask extends AsyncTask<String, Void, Boolean> {
		
		//ProgressDialog to display while the task is running.
		private ProgressDialog dialog;
		
		/**
		 * Setup the Progress Dialog.
		 */
	    protected void onPreExecute(){
			//if(_debug) Log.v("SelectBlockingAppsArrayAdaptor.setSelectedPackageAsyncTask.onPreExecute()");
	        dialog = ProgressDialog.show(_selectBlockingAppsPreferenceActivity, "", _context.getString(R.string.saving_selection), true);
	    }
	    /**
	     * Do this work in the background.
	     * 
	     * @param params
	     */
	    protected Boolean doInBackground(String... params){
			//if(_debug) Log.v("SelectBlockingAppsArrayAdaptor.setSelectedPackageAsyncTask.doInBackground()");
			setPackage(params[0], Boolean.parseBoolean(params[1]));
	    	return true;
	    }
	    /**
	     * Stop the Progress Dialog and do any post background work.
	     * 
	     * @param result
	     */
	    protected void onPostExecute(Boolean successful){
			//if(_debug) Log.v("SelectBlockingAppsArrayAdaptor.setSelectedPackageAsyncTask.onPostExecute()");
	        dialog.dismiss();
	    }
	}
	
	/**
	 * Add/Remove the provided package in the selected packages preference.
	 * 
	 * @param packageName - The package name that changed.
	 * @param isChecked - A boolean of whether or not the package is selected.
	 */
	private void setPackage(String packageName, boolean isChecked){
    	//Update user preferences.
    	BlockingAppsCommon.setBlockingAppPackage(_context, packageName, isChecked);
	}
    
}
