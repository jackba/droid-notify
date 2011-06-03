/**
 * Copyright CMW Mobile.com, 2010. 
 */
package apps.droidnotify.preferences;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import apps.droidnotify.Log;
import apps.droidnotify.R;

/**
 * The ImageArrayAdapter is the array adapter used for displaying an additional
 * image to a list preference item.
 * 
 * @author Casper Wakkers edited by Camille Sévigny
 */
public class ImageArrayAdapter extends ArrayAdapter<CharSequence> {

	//================================================================================
    // Properties
    //================================================================================

	private int _index = 0;
	private int[] _resourceIds = null;

	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * ImageArrayAdapter constructor.
	 * 
	 * @param context - Context.
	 * @param textViewResourceId - Resource id of the text view.
	 * @param objects - Objects to be displayed.
	 * @param ids - Ids resource id of the images to be displayed.
	 * @param i - Index of the previous selected item.
	 */
	public ImageArrayAdapter(Context context, int textViewResourceId, CharSequence[] objects, int[] ids, int i) {
		super(context, textViewResourceId, objects);
		if (Log.getDebug()) Log.v("ImageArrayAdapter.ImageArrayAdapter()");
		_index = i;
		_resourceIds = ids;
	}
	
	//================================================================================
	// Accessors
	//================================================================================
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Returns a view.
	 * 
	 * @param position - Int
	 * @param view - View
	 * @param parent - ViewGroup
	 */
	public View getView(int position, View view, ViewGroup parent) {
		if (Log.getDebug()) Log.v("ImageArrayAdapter.getView()");
		LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
		View rowView = inflater.inflate(R.layout.listitem, parent, false);
		ImageView imageView = (ImageView)rowView.findViewById(R.id.image);
		imageView.setImageResource(_resourceIds[position]);
		CheckedTextView checkedTextView = (CheckedTextView)rowView.findViewById(R.id.check);
		checkedTextView.setText(getItem(position));
		if (position == _index) {
			checkedTextView.setChecked(true);
		}
		return rowView;
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
}
