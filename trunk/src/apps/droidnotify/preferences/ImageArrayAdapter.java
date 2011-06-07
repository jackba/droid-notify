/**
 * Copyright CMW Mobile.com, 2010. 
 */
package apps.droidnotify.preferences;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
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
    // Constants
    //================================================================================
	
	private final int SQUARE_IMAGE_SIZE = 50;
	
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
		imageView.setImageBitmap(getRoundedCornerBitmap(BitmapFactory.decodeResource(getContext().getResources(), _resourceIds[position]), 5));
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
	
	/**
	 * Rounds the corners of a Bitmap image.
	 * 
	 * @param bitmap - The Bitmap to be formatted.
	 * @param pixels - The number of pixels as the diameter of the rounded corners.
	 * 
	 * @return Bitmap - The formatted Bitmap image.
	 */
	private Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		if (Log.getDebug()) Log.v("NotificationView.getRoundedCornerBitmap()");
        Bitmap output = Bitmap.createBitmap(
        		bitmap.getWidth(), 
        		bitmap
                .getHeight(), 
                Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Resize the Bitmap so that all images are consistent.
        //Bitmap createScaledBitmap(Bitmap src, int dstWidth, int dstHeight, boolean filter)
        output = Bitmap.createScaledBitmap(output, SQUARE_IMAGE_SIZE, SQUARE_IMAGE_SIZE, true);
        return output;
	}
	
}
