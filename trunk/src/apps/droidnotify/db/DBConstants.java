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
    public static final Uri CONTENT_URI_REMINDER = Uri.parse("content://" + AUTHORITY_REMINDER + "/" + TABLE_NAME_REMINDER);
    public static final String CONTENT_TYPE_REMINDER = "vnd.android.cursor.dir/vnd.droidnotify.blacklist";
    public static final String DATABASE_CREATE_REMINDER = "CREATE TABLE " + TABLE_NAME_REMINDER + "( " + 
    															COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
    															COLUMN_CREATED + " INTEGER NOT NULL, " +
    															COLUMN_ACTION + " TEXT NOT NULL, " +
    															COLUMN_DISMISSED + " INTEGER NOT NULL);";

}
