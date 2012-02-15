package apps.droidnotify.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.ListView;

import apps.droidnotify.R;

/**
 * The ImageListView class is responsible for displaying an image for each item within the list.
 * 
 * @author Camille Sévigny
 */
public class ImageListView extends ListView {

	//================================================================================
    // Properties
    //================================================================================
	
	private int[] _resourceIds = null;
	private String[] _labels = null;

	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * Constructor of the ImageListview. 
	 * Initializes the custom images.
	 * 
	 * @param context - Application context.
	 * @param attrs - Custom xml attributes.
	 */
	public ImageListView(Context context, AttributeSet attrs){
		super(context, attrs);
		//Get array images.
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageList);
		String[] imageNames = context.getResources().getStringArray(typedArray.getResourceId(typedArray.getIndexCount()-1, -1));
		_resourceIds = new int[imageNames.length];
		for (int i=0; i<imageNames.length; i++) {
			String imageName = imageNames[i].substring(imageNames[i].lastIndexOf('/') + 1, imageNames[i].lastIndexOf('.'));
			_resourceIds[i] = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
		}
		//Get array labels.
		ImageListView contentListView = (ImageListView)this.findViewById(R.id.content_list_view);
		ListAdapter listViewAdapter = contentListView.getAdapter();
		int adaptorSize = listViewAdapter.getCount();
		_labels = new String[adaptorSize];
		for(int i=0; i < adaptorSize; i++) {
			_labels[i] = listViewAdapter.getItem(i).toString();
		}
		//Set the adaptor using the labels and images from the XML file.
		this.setAdapter(new ImageListViewArrayAdaptor(context, R.layout.imagelistviewitem, _labels , _resourceIds, 0));
	}

}
