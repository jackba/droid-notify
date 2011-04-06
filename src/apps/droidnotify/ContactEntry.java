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
	
	private Long _contactid = null;
	private String _contactlookupkey = null;
	private String _contactdisplayname = null;
	private static final Pattern NAME_ADDR_EMAIL_PATTERN = Pattern.compile("\\s*(\"[^\"]*\"|[^<>\"]+)\\s*<([^<>]+)>\\s*");
	private Uri emailUri = Uri.parse("content://contacts/people/with_email_or_im_filter");
	private Uri phoneUri;
	private String columns[] = new String[] { Contacts._ID, Contacts.DISPLAY_NAME };
	private int _maxphotosize = 1024;
	private int _thumbphotosize = 96;
	
	//================================================================================
	// Constructors
	//================================================================================
	public ContactEntry(){
		_contactid = null;
		_contactlookupkey = null;
		_contactdisplayname = null;	
		return;
	}
	
	public ContactEntry(Long contactid, String contactlookup, String contactname){
		_contactid = contactid;
		_contactlookupkey = contactlookup;
		_contactdisplayname = contactname;	
		return;
	}
	
	//================================================================================
	// Accessors
	//================================================================================
	  
	public Long getContactID(){
		return _contactid;
	}

	public void setContactID(Long contactid){
		_contactid = contactid;
		return;
	}

	public String getContactLookupKey(){
		return _contactlookupkey;
	}

	public void setContactLookupKey(String contactlookup){
		_contactlookupkey = contactlookup;
		return;
	}
	
	public String getContactName(){
		return _contactdisplayname;
	}

	public void setContactName(String contactname){
		_contactdisplayname = contactname;
		return;
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
  
	/**
	 * Looks up a contacts id, given their email address.
	 * Leaves properties as null if not found.
	 */
	public void PopulateContactFromEmail(Context context, String email) {
		if (email == null){
			return;
		}
	    Cursor cursor = null;
	    try {
	    	cursor = context.getContentResolver().query(
	    			Uri.withAppendedPath(emailUri, Uri.encode(ExtractEmailAddress(email))),
	    			columns,
	    			null, 
	    			null, 
	    			null);
	    } catch (Exception e) {
	    	Log.v("ContactEntry.PopulateContactFromEmail() Exception: " + e.toString());
	    	return;
	    }
	    if (cursor != null) {
	    	try {
	    		if (cursor.moveToFirst()) {
	    			_contactid = Long.valueOf(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
	    			_contactdisplayname = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	    			_contactlookupkey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
	    			if (Log.DEBUG) Log.v("Found person: " + _contactid + ", " + _contactdisplayname + ", " + _contactlookupkey);
	    		}
		    } catch (Exception e) {
		    	Log.v("ContactEntry.PopulateContactFromEmail() Exception: " + e.toString());
	    	} finally {
	    		cursor.close();
	    	}
	    }
	    return;
	}
	
	/**
	 * Looks up a contacts id, given their phone number.
	 * Leaves properties as null if not found.
	 */
	public void PopulateContactFromPhoneNumber(Context context, String phonenumber) {
	    if (phonenumber == null){
	    	return;
	    }
	    Cursor cursor = null;
	    phoneUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phonenumber));
	    try {
	    	cursor = context.getContentResolver().query(
	    			phoneUri,
	    			columns,
	    			null, 
	    			null, 
	    			null);
	    } catch (Exception e) {
	    	Log.v("ContactEntry.PopulateContactFromEmail() Exception: " + e.toString());
	    	return;
	    }
	    if (cursor != null) {
	      try {
	    		if (cursor.moveToFirst()) {
	    			_contactid = Long.valueOf(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
	    			_contactdisplayname = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	    			_contactlookupkey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
	    			if (Log.DEBUG) Log.v("Found person: " + _contactid + ", " + _contactdisplayname + ", " + _contactlookupkey);
	    		}
		    } catch (Exception e) {
		    	Log.v("ContactEntry.PopulateContactFromPhoneNumber() Exception: " + e.toString());
	    	} finally {
	    		cursor.close();
	    	}
	    }
	    return;
	}
	
	/**
	 * Looks up a contacts display name by contact id - if not found, the 
	 * phone number will be formatted and returned instead.
	 */
	public String GetPersonName(Context context, Long id, String phonenumber) {
		String columns[] = new String[] { Contacts.DISPLAY_NAME };
		_contactdisplayname = null;
	    // Check for id, if null return the formatting phone number as the name
	    if (id == null) {
	    	if (phonenumber != null)
	    		_contactdisplayname = PhoneNumberUtils.formatNumber(phonenumber);
	    	return _contactdisplayname;
	    }
	    Cursor cursor = null;
	    try {
	    	cursor = context.getContentResolver().query(
	        Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(id)),
	        columns,
	        null, 
	        null, 
	        null);
	    } catch (Exception e) {
	    	_contactdisplayname = null;
	    	Log.v("ContactEntry.GetPersonName() Exception: " + e.toString());
	    }
	    if (cursor != null) {
	    	try {
	    		if (cursor.getCount() > 0) {
	    			cursor.moveToFirst();
	    			_contactdisplayname = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	    			if (Log.DEBUG) Log.v("Contact Display Name: " + _contactdisplayname);
	    		}
	    	} catch (Exception e) {
	    		_contactdisplayname = null;
		    	Log.v("ContactEntry.GetPersonName() Exception: " + e.toString());
		    } finally {
	    	  cursor.close();
	    	}
	    	if(_contactdisplayname != null)
	    		return _contactdisplayname;
	    }
	    if (phonenumber != null) {
	    	_contactdisplayname = PhoneNumberUtils.formatNumber(phonenumber);
	    }
	    return _contactdisplayname;
	  }

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
	    if (Log.DEBUG) Log.v("ContactEntry.GetPersonPhoto() Contact photo size = H" + height + " x W" + width + ".");
	    // If photo is too large or not found get out
	    if (height > _maxphotosize || width > _maxphotosize  || width == 0 || height == 0) 
	    	return null;
	    // This time we're going to do it for real
	    options.inJustDecodeBounds = false;
	    // Calculate new thumbnail size based on screen density
	    final float scale = context.getResources().getDisplayMetrics().density;
	    int thumbsize = _thumbphotosize;
	    if (scale != 1.0) {
	    	if (Log.DEBUG) Log.v("ContactEntry.GetPersonPhoto() Screen density is not 1.0, adjusting contact photo.");
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
	
	private String ExtractEmailAddress(String email) {
		Matcher match = NAME_ADDR_EMAIL_PATTERN.matcher(email);
		if (match.matches()){
			return match.group(2);
		}
		return email;
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
