package apps.droidnotify.preferences.blockingapps;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.view.Display;
import android.view.WindowManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.db.DBConstants;
import apps.droidnotify.log.Log;

public class BlockingAppsCommon {
	
	//================================================================================
    // Properties
    //================================================================================
	
	//private static boolean _debug = false; 

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Insert the specified value into the custom contact DB.
	 * 
	 * @param context - The application context.
	 * @param packageName - The package name.
	 * 
	 * @return CustomContact - The custom contact item that was inserted.
	 */
	public static CustomPackage insertValue(Context context, String packageName){
		//_debug = Log.getDebug();
		//if(_debug) Log.v("BlockingAppsCommon..insertValue()");
        try{
        	ContentValues contentValues = new ContentValues();
			contentValues.put(DBConstants.COLUMN_PACKAGE, packageName);
			//Check the DB to ensure that this contact was not already added.
			Cursor cursor = null;
	        try{
	    		final String[] projection = new String[] {DBConstants.COLUMN_ID, DBConstants.COLUMN_PACKAGE};
	    		final String selection = DBConstants.COLUMN_PACKAGE + "=?";
	    		final String[] selectionArgs = new String[]{packageName};
	    		final String sortOrder = null;
			    cursor = context.getContentResolver().query(
			    		DBConstants.CONTENT_URI_BLOCKINGAPPS,
			    		projection,
			    		selection,
						selectionArgs,
						sortOrder);
			    if(cursor.moveToFirst()){
			    	//This contact id has already been added.
			    	//if(_debug) Log.v("BlockingAppsCommon.insertValue() Package has already been added. Returning existing entry.");
			    	long rowID = cursor.getLong(cursor.getColumnIndex(DBConstants.COLUMN_ID));
			    	cursor.close();
			    	//Return existing row of data.
			    	CustomPackage existingCustomContact = new CustomPackage(context, rowID, packageName, null, null);
			    	return existingCustomContact;
			    }
			}catch(Exception ex){
				Log.e("BlockingAppsCommon.insertValue() Check If Entry Exists ERROR: " + ex.toString());
			}finally{
				cursor.close();
			}
			//Insert the new contact entry into the db.
        	Uri insertedUri = context.getContentResolver().insert(DBConstants.CONTENT_URI_BLOCKINGAPPS, contentValues);
        	long insertedID = Long.parseLong(insertedUri.toString().substring( insertedUri.toString().lastIndexOf("/") + 1 )); 
        	return new CustomPackage(context, insertedID, packageName, null, null);
		}catch(Exception ex){
			Log.e("BlockingAppsCommon.insertValue() ERROR: " + ex.toString());
			return null;
		}
	}

	/**
	 * Update the specified values in the packBLOCKINGAPPS
	 * 
	 * @param context - The application context.
	 * @param customPackageID - The package DB ID.
	 * @param packageName - The package name.
	 * 
	 * @return CustomContact - The custom contact item that was updated.
	 */
	public static CustomPackage updateValues(Context context, ContentValues contentValues, long customPackageID, String packageName){
		//_debug = Log.getDebug();
		//if(_debug) Log.v("BlockingAppsCommon.updateValues()");
        try{
			String updateWhere = DBConstants.COLUMN_ID + "=?";
        	context.getContentResolver().update(DBConstants.CONTENT_URI_BLOCKINGAPPS, contentValues, updateWhere, new String[]{String.valueOf(customPackageID)}); 
            return new CustomPackage(context, customPackageID, packageName, null, null);
		}catch(Exception ex){
			Log.e("BlockingAppsCommon.updateValues() ERROR: " + ex.toString());
			return null;
		}
	}	
	
	/**
	 * Delete the specified value in the packages DB.
	 * 
	 * @param context - The application context.
	 * @param customPackageID - The package DB ID.
	 * @param packageName - The package name.
	 * 
	 * @return CustomContact - The custom contact item that was updated.
	 */
	public static boolean deleteValues(Context context, String packageName){
		//_debug = Log.getDebug();
		//if(_debug) Log.v("BlockingAppsCommon.deleteValues()");
        try{
			String updateWhere = DBConstants.COLUMN_PACKAGE + "=?";
        	context.getContentResolver().delete(DBConstants.CONTENT_URI_BLOCKINGAPPS, updateWhere, new String[]{packageName}); 
            return true;
		}catch(Exception ex){
			Log.e("BlockingAppsCommon.deleteValues() ERROR: " + ex.toString());
			return false;
		}
	}
	
	/**
	 * Get the package db id for the provided package.
	 * 
	 * @param context - The application context.
	 * @param packageName - The package we are searching for.
	 * 
	 * @return long - Returns the package db id.
	 */
	public static long getPackageDBID(Context context, String packageName){
		//_debug = Log.getDebug();
		//if (_debug) Log.v("BlockingAppsCommon.getPackageDBID() PackageName: " + packageName);
		if(packageName == null){
			Log.e("BlockingAppsCommon.getPackageDBID() PackageName is null. Exiting...");
			return -1;
		}
		long packageDBID = -1;
		Cursor cursor = null;
        try{
    		final String[] projection = new String[] {DBConstants.COLUMN_ID, DBConstants.COLUMN_PACKAGE};
    		final String selection = DBConstants.COLUMN_PACKAGE + "=?";
    		final String[] selectionArgs = new String[]{packageName};
    		final String sortOrder = null;
		    cursor = context.getContentResolver().query(
		    		DBConstants.CONTENT_URI_BLOCKINGAPPS,
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
		    if(cursor.moveToFirst()){
		    	packageDBID = cursor.getLong(cursor.getColumnIndex(DBConstants.COLUMN_ID));
		    }
		}catch(Exception ex){
			Log.e("BlockingAppsCommon.getSelectedPackageNames() Cursor ERROR: " + ex.toString());
		}finally{
			cursor.close();
		}
        return packageDBID;
	}
	
	/**
	 * Get the package display name for the provided package.
	 * 
	 * @param context - The application context.
	 * @param packageName - The package we are searching for.
	 * 
	 * @return String - Returns the package display name.
	 */
	public static String getPackageDisplayName(Context context, String packageName){
		//_debug = Log.getDebug();
		//if (_debug) Log.v("BlockingAppsCommon.getPackageDisplayName() PackageName: " + packageName);
		try{
			if(packageName == null){
				Log.e("BlockingAppsCommon.getPackageDisplayName() PackageName is null. Exiting...");
				return null;
			}
			ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
			int labelId = applicationInfo.labelRes;
			if(labelId == 0){
				Log.e("BlockingAppsCommon.getPackageDisplayName() Label ID is null. Exiting...");
				return null;
			}
			Resources resources = context.getPackageManager().getResourcesForApplication(packageName);
			return resources.getString(labelId);
		}catch(Exception ex){
			Log.e("BlockingAppsCommon.getPackageDisplayName() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Get the package launcher icon for the provided package.
	 * 
	 * @param context - The application context.
	 * @param packageName - The package we are searching for.
	 * 
	 * @return Bitmap - Returns the package launcher icon.
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static Bitmap getPackageIcon(Context context, String packageName){
		//_debug = Log.getDebug();
		//if (_debug) Log.v("BlockingAppsCommon.getPackageIcon() PackageName: " + packageName);
		try{
			if(packageName == null){
				Log.e("BlockingAppsCommon.getPackageIcon() PackageName is null. Exiting...");
				return null;
			}
			ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
			int packageIconId = applicationInfo.icon;			
			if(packageIconId == 0){
				Log.e("BlockingAppsCommon.getPackageIcon() Package Icon ID is null. Exiting...");
				return null;
			}
			int resizeWidth = 48;
			try{				
				Display display= ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		        Point size = new Point();
		        int width;
		        if(Common.getDeviceAPILevel() >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2){
			        display.getSize(size);
			        width = size.x;
		        }else{
		        	width = display.getWidth();
		        }
				if(width < 320){ //~ldpi
					resizeWidth = 36;
				}else if(width >= 320 && width < 480){ //~mdpi
					resizeWidth = 48;
				}else if(width >= 480 && width < 720){ //~hdpi
					resizeWidth = 72;
				}else{ //~xhdpi
					//resizeWidth = 96;	
					resizeWidth = 72;
				}
			}catch(Exception ex){
				Log.e("BlockingAppsCommon.getPackageIcon() WindowManager ERROR: " + ex.toString());
				resizeWidth = 48;
			}
			Resources resources = context.getPackageManager().getResourcesForApplication(packageName);
			Bitmap packageIcon = BitmapFactory.decodeResource(resources, packageIconId);
			int packageIconWidth = packageIcon.getWidth();
			//If the icon is within 10 pixels in width & height, don't resize.
			if(packageIconWidth >= (resizeWidth - 10) && packageIconWidth <= (resizeWidth + 10)){
				return packageIcon;
			}else{
				return getResizedBitmap(packageIcon, resizeWidth);	
			}		
		}catch(Exception ex){
			Log.e("BlockingAppsCommon.getPackageIcon() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Get the stored selected package names.
	 * 
	 * @param context - The application context.
	 * 
	 * @return String[] - A string array of the stored selected package names.
	 */
	public static String[] getSelectedPackageNames(Context context){
		//_debug = Log.getDebug();
		//if(_debug) Log.v("BlockingAppsCommon.getSelectedPackageNames()");
		ArrayList<String> packageNameArray = new ArrayList<String>();
		Cursor cursor = null;
        try{
    		final String[] projection = new String[] {DBConstants.COLUMN_PACKAGE};
    		final String selection = null;
    		final String[] selectionArgs = null;
    		final String sortOrder = null;
		    cursor = context.getContentResolver().query(
		    		DBConstants.CONTENT_URI_BLOCKINGAPPS,
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
		    while(cursor.moveToNext()){
		    	String packageName = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN_PACKAGE));
		    	packageNameArray.add(packageName);
		    }
		}catch(Exception ex){
			Log.e("BlockingAppsCommon.getSelectedPackageNames() Cursor ERROR: " + ex.toString());
		}finally{
			cursor.close();
		}
        if(packageNameArray.size() == 0){
        	return null;
        }else{
        	return packageNameArray.toArray(new String[packageNameArray.size()]);
        }
	}	
	
	/**
	 * Resizes a Bitmap image.
	 * 
	 * @param bitmap - The Bitmap to be formatted.
	 * @param resizeX - The number of pixels as the width of the image.
	 * 
	 * @return Bitmap - The formatted Bitmap image.
	 */
	public static Bitmap getResizedBitmap(Bitmap bitmap, int resizeX) {
		//_debug = Log.getDebug();
		//if (_debug) Log.v("BlockingAppsCommon.getResizedBitmap()");
		try{
			Bitmap output = null;
			if(bitmap == null){
				return null;
			}else{
				int bitmapWidth = bitmap.getWidth();
				int bitmapHeight = bitmap.getHeight();
		        if(bitmapWidth == bitmapHeight){
					if(resizeX < 36 ){
						return bitmap;
					}else{
						output = Bitmap.createScaledBitmap(bitmap, resizeX, resizeX, true);
					}
		        }else{
		        	int resizeY = (resizeX/bitmapWidth) * bitmapHeight;
					if(resizeY < 36 && resizeX < 36){
						return bitmap;
					}
		        	output = Bitmap.createScaledBitmap(bitmap, resizeX, resizeY, true);
		        }
		        return output;
			}
		}catch(Exception ex){
			Log.e("BlockingAppsCommon.getResizedBitmap() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Determine if a package name is currently selected or not.
	 * 
	 * @param context - The application context.
	 * @param packageName - The package name we are searching for.
	 * 
	 * @return boolean - Returns true if the package name is currently selected.
	 */
	public static boolean isSelectedPackage(Context context, String packageName){
		//_debug = Log.getDebug();
		//if (_debug) Log.v("BlockingAppsCommon.isSelectedPackage()");    		
		if(packageName == null){
			Log.e("BlockingAppsCommon.isSelectedPackage() PackageName is null. Exiting...");
			return false;
		}
		//Check the DB and see if this package is selected or not.
		boolean packageFound = false;
		Cursor cursor = null;
        try{
    		final String[] projection = new String[] {DBConstants.COLUMN_PACKAGE};
    		final String selection = DBConstants.COLUMN_PACKAGE + "=?";
    		final String[] selectionArgs = new String[]{packageName};
    		final String sortOrder = null;
		    cursor = context.getContentResolver().query(
		    		DBConstants.CONTENT_URI_BLOCKINGAPPS,
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
		    if(cursor.moveToFirst()){
		    	packageFound = true;
		    }
		}catch(Exception ex){
			Log.e("BlockingAppsCommon.isSelectedPackage() Cursor ERROR: " + ex.toString());
		}finally{
			cursor.close();
		}
        return packageFound;
	}	
	
	/**
	 * Update the stored user preferences of the selected packages.
	 * This function removes or adds a user package.
	 * 
	 * @param context - The application context.
	 * @param getPackageName - The package name we are working with.
	 * @param isSelected - A boolean indicating if the package is selected or not.
	 */
	public static void setBlockingAppPackage(Context context, String packageName, boolean isSelected){
		//_debug = Log.getDebug();
		//if (_debug) Log.v("BlockingAppsCommon.setSelectedPackages()");	
		try{		
			if(packageName == null){
				Log.e("BlockingAppsCommon.setBlockingAppPackage() PackageName is null. Exiting...");
				return;
			}
			if(isSelected){
				//Add this package to the packages DB.
				insertValue(context, packageName);
			}else{
				//Remove this package to the packages DB.
				deleteValues(context, packageName);
			}	
		}catch(Exception ex){
			Log.e("BlockingAppsCommon.setBlockingAppPackage() ERROR: " + ex.toString());
		}
	}	
	
}

