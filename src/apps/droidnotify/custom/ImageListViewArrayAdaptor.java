package apps.droidnotify.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import apps.droidnotify.R;

/**
 * The ImageListViewArrayAdaptor class is used to load the ImageListView data.
 * 
 * @author Camille Sévigny
 */
public class ImageListViewArrayAdaptor extends ArrayAdapter<CharSequence> {
	
	//================================================================================
    // Properties
    //================================================================================

	private LayoutInflater _inflater = null;
	private int[] _resourceIds = null;
	private String[] _labelValues = null;

	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * ImageArrayAdapter constructor.
	 * 
	 * @param context - Application context.
	 * @param textViewResourceId - Resource id of the text view.
	 * @param objects - Objects to be displayed.
	 * @param ids - Resource id of the images to be displayed.
	 * @param i - Index of the previous selected item.
	 */
	public ImageListViewArrayAdaptor(Context context, int textViewResourceId, CharSequence[] labelValues, int[] ids, int index){
		super(context, textViewResourceId, labelValues);
		_inflater = ((Activity)context).getLayoutInflater();
		_resourceIds = ids;
		_labelValues = (String[])labelValues;
	}

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Returns a view.
	 * 
	 * @param position - Index of the view to be returned.
	 * @param view - View
	 * @param parent - ViewGroup
	 */
	public View getView(int position, View currentView, ViewGroup parent){
		final ViewHolder viewHolder;
		if(currentView == null){
			currentView = _inflater.inflate(R.layout.imagelistviewitem, parent, false);
			// Creates a ViewHolder and store references to the children views we want to bind data to.
			viewHolder = new ViewHolder();
			viewHolder.imageView = (ImageView)currentView.findViewById(R.id.image);
			viewHolder.textView = (TextView)currentView.findViewById(R.id.label);
			// Store in tag
			currentView.setTag(viewHolder);
		}else{
			// Get the ViewHolder back to get fast access to the TextView and CheckBox
			viewHolder = (ViewHolder)currentView.getTag();
		}
		//Set the data for the Views.
		viewHolder.imageView.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), _resourceIds[position]));
		viewHolder.textView.setText(_labelValues[position]);
		return currentView;
	}
	
	// View holder to references to the views
	private static class ViewHolder {
		public ImageView imageView;
		public TextView textView;
	}
	
}
