package apps.droidnotify;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.telephony.PhoneNumberUtils;

public class ContactEntry {

	//================================================================================
    // Properties
    //================================================================================
	
	private Uri emailUri = Uri.parse("content://contacts/people/with_email_or_im_filter");
	private Uri phoneUri;
	private int _maxphotosize = 1024;
	private int _thumbphotosize = 96;
	
	//================================================================================
	// Constructors
	//================================================================================
	
	//================================================================================
	// Accessors
	//================================================================================
	
	//================================================================================
	// Public Methods
	//================================================================================
 
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 *
	 * Looks up a contacts photo by their contact id, returns a Bitmap array
	 * that represents their photo (or null if not found or there was an error.
	 *
	 * I do my own scaling and validation of sizes - Android OS supports any size
	 * for contact photos and some apps are adding huge photos to contacts.  Doing
	 * the scaling myself allows me more control over how things play out in those
	 * cases.
	 *
	 * @param context
	 * @param id contact id
	 * @return Bitmap of the contacts photo (null if none or an error)
	 */
	private Bitmap GetPersonPhoto(Context context, Long id) {
	    if (id == null){
	    	return null;
	    }
	    if (id == 0){
	    	return null;
	    }
	    // First let's just check the dimensions of the contact photo
	    BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    // The height and width are stored in 'options' but the photo itself is not loaded
	    LoadContactPhoto(context, id, 0, options);
	    // Raw height and width of contact photo
	    int height = options.outHeight;
	    int width = options.outWidth;
	    if (Log.getDebug()) Log.v("ContactEntry.GetPersonPhoto() Contact photo size = H" + height + " x W" + width + ".");
	    // If photo is too large or not found get out
	    if (height > _maxphotosize || width > _maxphotosize  || width == 0 || height == 0) 
	    	return null;
	    // This time we're going to do it for real
	    options.inJustDecodeBounds = false;
	    // Calculate new thumbnail size based on screen density
	    final float scale = context.getResources().getDisplayMetrics().density;
	    int thumbsize = _thumbphotosize;
	    if (scale != 1.0) {
	    	if (Log.getDebug()) Log.v("ContactEntry.GetPersonPhoto() Screen density is not 1.0, adjusting contact photo.");
	      	thumbsize = Math.round(thumbsize * scale);
	    }
	    int newHeight = thumbsize;
	    int newWidth = thumbsize;
	    // If we have an abnormal photo size that's larger than thumbsize then sample it down
	    boolean sampleDown = false;
	    if (height > thumbsize || width > thumbsize)
	    	sampleDown = true;
	    // If the dimensions are not the same then calculate new scaled dimenions
	    if (height < width) {
	    	if (sampleDown) {
	    	  options.inSampleSize = Math.round(height / thumbsize);
	      	}
	      	newHeight = Math.round(thumbsize * height / width);
	    } else {
	    	if (sampleDown) {
	    		options.inSampleSize = Math.round(width / thumbsize);
	    	}
	    	newWidth = Math.round(thumbsize * width / height);
	    }
	    // Fetch the real contact photo (sampled down if needed)
	    Bitmap contactBitmap = null;
	    try {
	      contactBitmap = LoadContactPhoto(context, id, 0, options);
	    } catch (OutOfMemoryError e) {
	    	Log.e("ContactEntry.GetPersonPhoto() Out of memory when loading contact photo.");
	    }
	    // Not found or error, get out
	    if (contactBitmap == null){
	    	return null;
	    }
	    // Return bitmap scaled to new height and width
	    return Bitmap.createScaledBitmap(contactBitmap, newWidth, newHeight, true);
	  }
	
	/**
	   * Opens an InputStream for the person's photo and returns the photo as a Bitmap.
	   * If the person's photo isn't present returns the placeholderImageResource instead.
	   * @param context the Context
	   * @param id the id of the person
	   * @param placeholderImageResource the image resource to use if the person doesn't
	   *   have a photo
	   * @param options the decoding options, can be set to null
	   */
	private Bitmap LoadContactPhoto(Context context, Long id, int placeholderImageResource, BitmapFactory.Options options) {
	    if (id == null){
	    	return LoadPlaceholderPhoto(placeholderImageResource, context, options);
	    }
	    if (id == 0){
	    	return LoadPlaceholderPhoto(placeholderImageResource, context, options);
	    }
	    InputStream stream = OpenContactPhotoInputStream(context.getContentResolver(), id);
	    Bitmap bm = stream != null ? BitmapFactory.decodeStream(stream, null, options) : null;
	    if (bm == null) {
	      bm = LoadPlaceholderPhoto(placeholderImageResource, context, options);
	    }
	    return bm;
	}

	private Bitmap LoadPlaceholderPhoto(int placeholderImageResource, Context context, BitmapFactory.Options options) {
		if (placeholderImageResource == 0){
			return null;
		}
		return BitmapFactory.decodeResource(context.getResources(), placeholderImageResource, options);
	}
	
	/**
	 * Returns an InputStream for the person's photo
	 * @param id the id of the person
	 */
	public InputStream OpenContactPhotoInputStream(ContentResolver cr, Long id) {
	    if (id == null) return null;
	    if (id == 0) return null;
	    Uri photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
	    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, photoUri);
	    if (input == null) {
	        return null;
	    }
	    return input;
	}
	
}
