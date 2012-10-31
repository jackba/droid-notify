package apps.droidnotify.db;

import android.net.Uri;

/**
 * This class is a collection of all the constants used in the DB files.
 * 
 * @author Camille Sévigny
 */
public class DBConstants {
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_ACTION = "_action";
	public static final String COLUMN_DISMISSED = "_dismissed";
	public static final String COLUMN_CREATED = "_created";

    public static final String AUTHORITY_REMINDER = "apps.droidnotify.providers.remindercontentprovider";
	public static final String DATABASE_NAME_REMINDER = "reminder.db";
	public static final int DATABASE_VERSION_REMINDER = 1;
	public static final String TABLE_NAME_REMINDER = "reminder";
    public static final String CONTENT_URI_REMINDER_PATH = "content://" + AUTHORITY_REMINDER + "/" + TABLE_NAME_REMINDER;
    public static final Uri CONTENT_URI_REMINDER = Uri.parse(CONTENT_URI_REMINDER_PATH);
    public static final String CONTENT_TYPE_REMINDER = "vnd.android.cursor.dir/vnd.droidnotify.reminder";
    public static final String DATABASE_CREATE_REMINDER = "CREATE TABLE " + TABLE_NAME_REMINDER + "( " + 
    															COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
    															COLUMN_CREATED + " INTEGER NOT NULL, " +
    															COLUMN_ACTION + " TEXT NOT NULL, " +
    															COLUMN_DISMISSED + " INTEGER NOT NULL);";
	
	public static final String COLUMN_PACKAGE = "_package";
    
    public static final String AUTHORITY_BLOCKINGAPPS = "apps.droidnotify.providers.blockingappscontentprovider";
	public static final String DATABASE_NAME_BLOCKINGAPPS = "blockingapps.db";
	public static final int DATABASE_VERSION_BLOCKINGAPPS = 1;
	public static final String TABLE_NAME_BLOCKINGAPPS = "blockingapps";
    public static final String CONTENT_URI_BLOCKINGAPPS_PATH = "content://" + AUTHORITY_BLOCKINGAPPS + "/" + TABLE_NAME_BLOCKINGAPPS;
    public static final Uri CONTENT_URI_BLOCKINGAPPS = Uri.parse(CONTENT_URI_BLOCKINGAPPS_PATH);
    public static final String CONTENT_TYPE_BLOCKINGAPPS = "vnd.android.cursor.dir/vnd.droidnotify.blockingapps";
    public static final String DATABASE_CREATE_BLOCKINGAPPS = "CREATE TABLE " + TABLE_NAME_BLOCKINGAPPS + "( " + 
    															COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
    															COLUMN_PACKAGE + " TEXT NOT NULL);";

}
