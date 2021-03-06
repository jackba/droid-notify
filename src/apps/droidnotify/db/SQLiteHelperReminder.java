package apps.droidnotify.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import apps.droidnotify.common.FileUtils;

public class SQLiteHelperReminder extends SQLiteOpenHelper{

	public SQLiteHelperReminder(Context context){
		super(context, DBConstants.DATABASE_NAME_REMINDER, null, DBConstants.DATABASE_VERSION_REMINDER);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL(DBConstants.DATABASE_CREATE_REMINDER);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		//Alter DB on upgrade.
	}
	
	/**
	 * Copies the database file at the current internal application database to the specified location.
	 * 
	 * @param dbPath - The path to the SD card database.
	 */
	public boolean exportDatabase(String dbPath, String packageName) throws IOException{
		try{
			String internalDBFilePath = "/data/data/" + packageName + "/databases/" + DBConstants.DATABASE_NAME_REMINDER;
			if(!(new File(internalDBFilePath).exists())){
				getWritableDatabase().close();
			}else{
				this.close();
			}
		    File newDb = new File(dbPath);
		    File oldDb = new File(internalDBFilePath);
		    if (oldDb.exists()){
		    	FileUtils.copyFile(new FileInputStream(oldDb), new FileOutputStream(newDb));
		        return true;
		    }
		}catch(Exception ex){
			return false;
		}
	    return false;
	}
	
	/**
	 * Copies the database file at the specified location over the current internal application database.
	 * 
	 * @param dbPath - The path to the SD card database.
	 * @param packageName - THe package name of this application.
	 */
	public boolean importDatabase(String dbPath, String packageName) throws IOException{
		try{
			String internalDBFilePath = "/data/data/" + packageName + "/databases/" + DBConstants.DATABASE_NAME_REMINDER;
			if(!(new File(internalDBFilePath).exists())){
				getWritableDatabase().close();
			}else{
				this.close();
			}
		    File newDb = new File(dbPath);
		    File oldDb = new File(internalDBFilePath);
		    if (newDb.exists()){
		    	FileUtils.copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));
		        getWritableDatabase().close();
		        return true;
		    }
		}catch(Exception ex){
			return false;
		}
	    return false;
	}

}