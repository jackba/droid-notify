package apps.droidnotify.common;

/**
 * This class is a collection of all the constants used in this application.
 * 
 * @author Camille Sévigny
 */
public class Constants {
	
	//-----GENERAL APP CONSTANTS-----//
	public static final String LOCK_NAME_STATIC="app.droidnotify.android.syssvc.AppService.Static";
	
	public static final String HAPTIC_FEEDBACK_ENABLED_KEY = "haptic_feedback_enabled";
	public static final String LANDSCAPE_SCREEN_ENABLED_KEY = "landscape_screen_enabled";
	
	public static final String APP_ENABLED_KEY = "app_enabled";
	public static final String SMS_NOTIFICATIONS_ENABLED_KEY = "sms_notifications_enabled";
	public static final String MMS_NOTIFICATIONS_ENABLED_KEY = "mms_notifications_enabled";
	public static final String MISSED_CALL_NOTIFICATIONS_ENABLED_KEY = "missed_call_notifications_enabled";
	public static final String CALENDAR_NOTIFICATIONS_ENABLED_KEY = "calendar_notifications_enabled";
	
	public static final String DROID_NOTIFY_WAKELOCK = "DROID_NOTIFY_WAKELOCK";
	public static final String DROID_NOTIFY_KEYGUARD = "DROID_NOTIFY_KEYGUARD";
	
	public static final String RESCHEDULE_NOTIFICATIONS_ENABLED = "reschedule_notifications_enabled";
	public static final String RESCHEDULE_NOTIFICATION_TIMEOUT_KEY = "reschedule_notification_timeout_settings";
	public static final String USER_IN_MESSAGING_APP = "user_in_messaging_app";
	
	public static final int NOTIFICATION_TYPE_TEST = -1;
	public static final int NOTIFICATION_TYPE_PHONE = 0;
	public static final int NOTIFICATION_TYPE_SMS = 1;
	public static final int NOTIFICATION_TYPE_MMS = 2;
	public static final int NOTIFICATION_TYPE_CALENDAR = 3;
	public static final int NOTIFICATION_TYPE_GMAIL = 4;
	
	public static final String MESSAGING_APP_RUNNING_ACTION_RESCHEDULE = "0";
	public static final String MESSAGING_APP_RUNNING_ACTION_IGNORE = "1";
	
	public static final int ADD_CONTACT_ACTIVITY = 1;
	public static final int EDIT_CONTACT_ACTIVITY = 2;
	public static final int VIEW_CONTACT_ACTIVITY = 3;
	public static final int SEND_SMS_ACTIVITY = 4;
	public static final int MESSAGING_ACTIVITY = 5;
	public static final int VIEW_SMS_MESSAGE_ACTIVITY = 6;
	public static final int VIEW_SMS_THREAD_ACTIVITY = 7;
	public static final int CALL_ACTIVITY = 8;
	public static final int CALENDAR_ACTIVITY = 9;
	public static final int ADD_CALENDAR_ACTIVITY = 10;
	public static final int EDIT_CALENDAR_ACTIVITY = 11;
	public static final int VIEW_CALENDAR_ACTIVITY = 12;
	public static final int SEND_SMS_QUICK_REPLY_ACTIVITY = 13;
	public static final int VIEW_CALL_LOG_ACTIVITY = 14;
	
	//-----APP THEME CONSTANTS-----//
	public static final String APP_THEME_KEY = "app_theme";
	public static final String ANDROID_FROYO_THEME = "android";
	public static final String ANDROID_GINGERBREAD_THEME = "android_dark";
	public static final String IPHONE_THEME = "iphone";
	public static final String DARK_TRANSLUCENT_THEME = "dark_translucent";
	public static final String DARK_TRANSLUCENT_V2_THEME = "dark_translucent_v2";
	public static final String DARK_TRANSLUCENT_V3_THEME = "dark_translucent_v3";
	public static final String HTC_SENSE_UI_THEME = "theme_htc";
	public static final String XPERIA_THEME = "theme_xperia";
	
	//-----QUICK REPLY CONSTANTS-----//
	public static final String SMS_GATEWAY_KEY = "quick_reply_sms_gateway_settings";
	public static final int SMS_EMAIL_GATEWAY_KEY_1 = 1;
	public static final int SMS_EMAIL_GATEWAY_KEY_2 = 2;
	public static final int SMS_EMAIL_GATEWAY_KEY_3 = 3;
	public static final int SMS_EMAIL_GATEWAY_KEY_4 = 4;
	public static final int SMS_EMAIL_GATEWAY_KEY_5 = 5;
	public static final int SMS_EMAIL_GATEWAY_KEY_6 = 6;
	public static final int SMS_EMAIL_GATEWAY_KEY_7 = 7;
	public static final int SMS_EMAIL_GATEWAY_KEY_8 = 8;
	
	public static final String SAVE_MESSAGE_DRAFT_KEY = "quick_reply_save_draft_enabled";
	public static final String HIDE_CANCEL_BUTTON_KEY = "quick_reply_hide_cancel_button_enabled";
	
	public static final String QUICK_REPLY_BLUR_SCREEN_ENABLED_KEY = "quick_reply_blur_screen_background_enabled";
	public static final String QUICK_REPLY_DIM_SCREEN_ENABLED_KEY = "quick_reply_dim_screen_background_enabled";
	public static final String QUICK_REPLY_DIM_SCREEN_AMOUNT_KEY = "quick_reply_dim_screen_background_amount";
	
	//-----SMS NOTIFICATION CONSTANTS-----//
	public static final boolean READ_SMS_FROM_DISK = true;
	public static final String MESSAGING_APP_RUNNING_ACTION_SMS = "messaging_app_running_action_sms";
	public static final String SMS_TIMEOUT_KEY = "sms_timeout_settings";
	
	public static final String SMS_MESSAGING_APP_REPLY = "0";
	public static final String SMS_QUICK_REPLY = "1";
	
	//-----MMS NOTIFICATION CONSTANTS-----//
	public static final String MESSAGING_APP_RUNNING_ACTION_MMS = "messaging_app_running_action_mms";
	public static final String MMS_TIMEOUT_KEY = "mms_timeout_settings";
	
	public static final String MMS_MESSAGING_APP_REPLY = "0";
	public static final String MMS_QUICK_REPLY = "1";	
	
	//-----MISSED CALL NOTIFICATION CONSTANTS-----//
	public static final String MESSAGING_APP_RUNNING_ACTION_PHONE = "messaging_app_running_action_missed_call";
	public static final String CALL_LOG_TIMEOUT_KEY = "call_log_timeout_settings";
	public static final int MISSED_CALL_TYPE = android.provider.CallLog.Calls.MISSED_TYPE;
	public static final String MISSED_CALL_DISMISS_BUTTON_ACTION = "missed_call_loading_settings";
	public static final String MISSED_CALL_GET_LATEST = "0";
	public static final String MISSED_CALL_GET_RECENT = "1";
	public static final String MISSED_CALL_GET_ALL = "2";
	
	
	//-----CALENDAR NOTIFICATION CONSTANTS-----//
	public static final String MESSAGING_APP_RUNNING_ACTION_CALENDAR = "messaging_app_running_action_calendar";
	public static final String _ID = "_id";
	public static final String CALENDAR_EVENT_ID = "event_id"; 
	public static final String CALENDAR_EVENT_TITLE = "title"; 
    public static final String CALENDAR_INSTANCE_BEGIN = "begin"; 
    public static final String CALENDAR_INSTANCE_END = "end"; 
    public static final String CALENDAR_EVENT_ALL_DAY = "allDay"; 
    public static final String CALENDAR_DISPLAY_NAME = "displayName"; 
    public static final String CALENDAR_SELECTED = "selected";
    public static final String CALENDAR_SELECTION_KEY = "calendar_selection";
	public static final String CALENDAR_NOTIFY_DAY_OF_TIME_KEY = "calendar_notify_day_of_time";
	public static final String CALENDAR_REMINDERS_ENABLED_KEY = "calendar_reminders_enabled"; 
    public static final String CALENDAR_REMINDER_KEY = "calendar_reminder_settings";
    public static final String CALENDAR_REMINDER_ALL_DAY_KEY = "calendar_reminder_all_day_settings";
	
	
	
	
	
	
	//-----EMAIL NOTIFICATION CONSTANTS-----//
	
	
	
	
	
	
	//-----TWITTER NOTIFICATION CONSTANTS-----//
	
	
	
	
	//-----FACEBOOK NOTIFICATION CONSTANTS-----//
	
	
	
	
	
	
	
}