package apps.droidnotify.common;

/**
 * This class is a collection of all the constants used in this application.
 * 
 * @author Camille Sévigny
 */
public class Constants {
	
	//-----GENERAL APP CONSTANTS-----//
	public static final String LOCK_NAME_STATIC="app.droidnotify.android.syssvc.AppService.Static";
	public static final String DROID_NOTIFY_WAKELOCK = "DROID_NOTIFY_WAKELOCK";
	public static final String DROID_NOTIFY_KEYGUARD = "DROID_NOTIFY_KEYGUARD";
	
	public static final String APP_ENABLED_KEY = "app_enabled";
	public static final String SMS_NOTIFICATIONS_ENABLED_KEY = "sms_notifications_enabled";
	public static final String MMS_NOTIFICATIONS_ENABLED_KEY = "mms_notifications_enabled";
	public static final String PHONE_NOTIFICATIONS_ENABLED_KEY = "missed_call_notifications_enabled";
	public static final String CALENDAR_NOTIFICATIONS_ENABLED_KEY = "calendar_notifications_enabled";
	public static final String HAPTIC_FEEDBACK_ENABLED_KEY = "haptic_feedback_enabled";
	public static final String LANDSCAPE_SCREEN_ENABLED_KEY = "landscape_screen_enabled";	
	public static final String BLUR_SCREEN_ENABLED_KEY = "blur_screen_background_enabled";
	public static final String DIM_SCREEN_ENABLED_KEY = "dim_screen_background_enabled";
	public static final String DIM_SCREEN_AMOUNT_KEY = "dim_screen_background_amount";
	public static final String WAKELOCK_TIMEOUT_KEY = "wakelock_timeout_settings";
	public static final String KEYGUARD_TIMEOUT_KEY = "keyguard_timeout_settings";
	public static final String RESCHEDULE_NOTIFICATIONS_ENABLED_KEY = "reschedule_notifications_enabled";
	public static final String RESCHEDULE_NOTIFICATION_TIMEOUT_KEY = "reschedule_notification_timeout_settings";
	public static final String USER_IN_MESSAGING_APP_KEY = "user_in_messaging_app";
	public static final String SCREEN_ENABLED_KEY = "screen_enabled";
	public static final String SCREEN_DIM_ENABLED_KEY = "screen_dim_enabled";
	public static final String KEYGUARD_ENABLED_KEY = "keyguard_enabled";
	public static final String HIDE_SINGLE_MESSAGE_HEADER_KEY = "hide_single_message_header_enabled";
    public static final String CONTACT_PLACEHOLDER_KEY = "contact_placeholder";
    public static final String BUTTON_ICONS_KEY = "button_icons_enabled";
	
	public static final String MESSAGING_APP_RUNNING_ACTION_RESCHEDULE = "0";
	public static final String MESSAGING_APP_RUNNING_ACTION_IGNORE = "1";
	
	public static final int NOTIFICATION_TYPE_TEST = -1;
	public static final int NOTIFICATION_TYPE_PHONE = 0;
	public static final int NOTIFICATION_TYPE_SMS = 1;
	public static final int NOTIFICATION_TYPE_MMS = 2;
	public static final int NOTIFICATION_TYPE_CALENDAR = 3;
	public static final int NOTIFICATION_TYPE_GMAIL = 4;
	public static final int NOTIFICATION_TYPE_TWITTER = 5;
	public static final int NOTIFICATION_TYPE_FACEBOOK = 6;
	
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
	
	public static final int MESSAGE_TYPE_SMS = 1;
	public static final int MESSAGE_TYPE_MMS = 2;
	
	public static final int SQUARE_IMAGE_SIZE = 80;
    
	public static final int DIALOG_DELETE_MESSAGE = 0;
	
	//Staring Array of the top SMS Messaging Apps:
	// Android Stock App
	// Handcent
	// Go SMS
	// Magic Text
	// Chomp SMS
	// Pansi
	// Text'n Drive
	//
	public static final String[] MESSAGING_PACKAGE_NAMES_ARRAY = new String[]{
		"com.android.mms", 
		"com.handcent.nextsms", 
		"com.jb.gosms", 
		"com.pompeiicity.magictext", 
		"com.p1.chompsms", 
		"com.pansi.msg", 
		"com.drivevox.drivevox" };
	
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
	public static final String SAVE_MESSAGE_DRAFT_KEY = "quick_reply_save_draft_enabled";
	public static final String HIDE_CANCEL_BUTTON_KEY = "quick_reply_hide_cancel_button_enabled";
	public static final String QUICK_REPLY_BLUR_SCREEN_ENABLED_KEY = "quick_reply_blur_screen_background_enabled";
	public static final String QUICK_REPLY_DIM_SCREEN_ENABLED_KEY = "quick_reply_dim_screen_background_enabled";
	public static final String QUICK_REPLY_DIM_SCREEN_AMOUNT_KEY = "quick_reply_dim_screen_background_amount";

	public static final String QUICK_REPLY_SETTINGS_SCREEN = "quick_reply_settings_screen";
	
	public static final int SMS_EMAIL_GATEWAY_1 = 1;
	public static final int SMS_EMAIL_GATEWAY_2 = 2;
	public static final int SMS_EMAIL_GATEWAY_3 = 3;
	public static final int SMS_EMAIL_GATEWAY_4 = 4;
	public static final int SMS_EMAIL_GATEWAY_5 = 5;
	public static final int SMS_EMAIL_GATEWAY_6 = 6;
	public static final int SMS_EMAIL_GATEWAY_7 = 7;
	public static final int SMS_EMAIL_GATEWAY_8 = 8;
	
	//-----SMS NOTIFICATION CONSTANTS-----//
	public static final String SMS_READ_FROM_INTENT = "0";
	public static final String SMS_READ_FROM_DISK = "1";
	public static final String SMS_MESSAGING_APP_RUNNING_ACTION_KEY = "messaging_app_running_action_sms";
	public static final String SMS_TIMEOUT_KEY = "sms_timeout_settings";
	public static final String SMS_DELETE_KEY = "sms_delete_button_action";	
	public static final String SMS_TIMESTAMP_ADJUSTMENT_KEY = "sms_timestamp_adjustment_settings";
	public static final String SMS_LOADING_SETTING_KEY = "sms_loading_settings";
	public static final String SMS_HIDE_CONTACT_PANEL_ENABLED_KEY = "sms_hide_contact_panel_enabled";
	public static final String SMS_HIDE_CONTACT_PHOTO_ENABLED_KEY = "sms_hide_contact_photo_enabled";
	public static final String SMS_HIDE_CONTACT_NAME_ENABLED_KEY = "sms_hide_contact_name_enabled";
	public static final String SMS_HIDE_CONTACT_NUMBER_ENABLED_KEY = "sms_hide_contact_number_enabled"; 
	public static final String SMS_REPLY_BUTTON_ACTION_KEY = "sms_reply_button_action";
	public static final String SMS_HIDE_MESSAGE_KEY = "sms_hide_message_body_enabled";
	public static final String SMS_HIDE_DISMISS_BUTTON_KEY = "sms_hide_dismiss_button_enabled";
	public static final String SMS_HIDE_DELETE_BUTTON_KEY = "sms_hide_delete_button_enabled";
	public static final String SMS_HIDE_REPLY_BUTTON_KEY = "sms_hide_reply_button_enabled";
	public static final String SMS_NOTIFICATION_COUNT_ACTION_KEY = "sms_notification_count_action";
	public static final String SMS_MESSAGE_BODY_FONT_SIZE_KEY = "sms_message_body_font_size";
	public static final String SMS_DISMISS_KEY = "sms_dismiss_button_action";
	public static final String SMS_DISPLAY_UNREAD_KEY = "sms_display_unread_enabled";
	public static final String SMS_CONFIRM_DELETION_KEY = "confirm_sms_deletion_enabled";
	
	public static final String SMS_DISMISS_ACTION_MARK_READ = "0";
	public static final String SMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	public static final String SMS_DELETE_ACTION_DELETE_THREAD = "1";
	public static final String SMS_DELETE_ACTION_NOTHING = "2";
	
	public static final String SMS_MESSAGING_APP_REPLY = "0";
	public static final String SMS_QUICK_REPLY = "1";
	
	//-----MMS NOTIFICATION CONSTANTS-----//
	public static final String MMS_MESSAGING_APP_RUNNING_ACTION_KEY = "messaging_app_running_action_mms";
	public static final String MMS_TIMEOUT_KEY = "mms_timeout_settings";
	public static final String MMS_DELETE_KEY = "mms_delete_button_action";
	public static final String MMS_HIDE_CONTACT_PANEL_ENABLED_KEY = "mms_hide_contact_panel_enabled";
	public static final String MMS_HIDE_CONTACT_PHOTO_ENABLED_KEY = "mms_hide_contact_photo_enabled";
	public static final String MMS_HIDE_CONTACT_NAME_ENABLED_KEY = "mms_hide_contact_name_enabled";
	public static final String MMS_HIDE_CONTACT_NUMBER_ENABLED_KEY = "mms_hide_contact_number_enabled";	
	public static final String MMS_REPLY_BUTTON_ACTION_KEY = "mms_reply_button_action";
	public static final String MMS_HIDE_MESSAGE_KEY = "mms_hide_message_body_enabled";
	public static final String MMS_HIDE_DISMISS_BUTTON_KEY = "mms_hide_dismiss_button_enabled";
	public static final String MMS_HIDE_DELETE_BUTTON_KEY = "mms_hide_delete_button_enabled";
	public static final String MMS_HIDE_REPLY_BUTTON_KEY = "mms_hide_reply_button_enabled";
	public static final String MMS_NOTIFICATION_COUNT_ACTION_KEY = "mms_notification_count_action";
	public static final String MMS_MESSAGE_BODY_FONT_SIZE_KEY = "mms_message_body_font_size";
	public static final String MMS_DISMISS_KEY = "mms_dismiss_button_action";
	public static final String MMS_CONFIRM_DELETION_KEY = "confirm_mms_deletion_enabled";
	public static final String MMS_DISPLAY_UNREAD_KEY = "mms_display_unread_enabled";
	
	public static final String MMS_DISMISS_ACTION_MARK_READ = "0";
	public static final String MMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	public static final String MMS_DELETE_ACTION_DELETE_THREAD = "1";
	public static final String MMS_DELETE_ACTION_NOTHING = "2";
	
	public static final String MMS_MESSAGING_APP_REPLY = "0";
	public static final String MMS_QUICK_REPLY = "1";	

	//-----MISSED CALL NOTIFICATION CONSTANTS-----//
	public static final String PHONE_MESSAGING_APP_RUNNING_ACTION_KEY = "messaging_app_running_action_missed_call";
	public static final String CALL_LOG_TIMEOUT_KEY = "call_log_timeout_settings";
	public static final String PHONE_DISMISS_BUTTON_ACTION_KEY = "missed_call_loading_settings";
	public static final String PHONE_HIDE_CONTACT_PANEL_ENABLED_KEY = "missed_call_hide_contact_panel_enabled";
	public static final String PHONE_HIDE_CONTACT_NUMBER_ENABLED_KEY = "missed_call_hide_contact_number_enabled";
	public static final String PHONE_HIDE_CONTACT_PHOTO_ENABLED_KEY = "missed_call_hide_contact_photo_enabled";
	public static final String PHONE_HIDE_CONTACT_NAME_ENABLED_KEY = "missed_call_hide_contact_name_enabled";
	public static final String PHONE_NUMBER_FORMAT_KEY = "phone_number_format_settings";
	public static final String PHONE_HIDE_DISMISS_BUTTON_KEY = "missed_call_hide_dismiss_button_enabled";
	public static final String PHONE_HIDE_CALL_BUTTON_KEY = "missed_call_hide_call_button_enabled";
	public static final String PHONE_NOTIFICATION_COUNT_ACTION_KEY = "missed_call_notification_count_action";
	public static final String PHONE_DISMISS_KEY = "missed_call_dismiss_button_action";
	
	public static final String PHONE_GET_LATEST = "0";
	public static final String PHONE_GET_RECENT = "1";
	public static final String PHONE_GET_ALL = "2";

	public static final String PHONE_DISMISS_ACTION_MARK_READ = "0";
	public static final String PHONE_DISMISS_ACTION_DELETE = "1";
	
	public static final int PHONE_TYPE = android.provider.CallLog.Calls.MISSED_TYPE;

	public static final int PHONE_NUMBER_FORMAT_A = 1;
	public static final int PHONE_NUMBER_FORMAT_B = 2;
	public static final int PHONE_NUMBER_FORMAT_C = 3;
	public static final int PHONE_NUMBER_FORMAT_D = 4;
	public static final int PHONE_NUMBER_FORMAT_E = 5;
	public static final int PHONE_NUMBER_FORMAT_F = 6;
	public static final int PHONE_NUMBER_FORMAT_G = 7;
	public static final int PHONE_NUMBER_FORMAT_H = 8;
	
	//-----CALENDAR NOTIFICATION CONSTANTS-----//
	public static final String CALENDAR_MESSAGING_APP_RUNNING_ACTION_KEY = "messaging_app_running_action_calendar";
	public static final String CALENDAR_NOTIFY_DAY_OF_TIME_KEY = "calendar_notify_day_of_time";
	public static final String CALENDAR_REMINDERS_ENABLED_KEY = "calendar_reminders_enabled"; 
    public static final String CALENDAR_REMINDER_KEY = "calendar_reminder_settings";
    public static final String CALENDAR_REMINDER_ALL_DAY_KEY = "calendar_reminder_all_day_settings";
    public static final String CALENDAR_POLLING_FREQUENCY_KEY = "calendar_polling_frequency";
	public static final String CALENDAR_HIDE_DISMISS_BUTTON_KEY = "calendar_hide_dismiss_button_enabled";
	public static final String CALENDAR_HIDE_VIEW_BUTTON_KEY = "calendar_hide_view_button_enabled";
	public static final String CALENDAR_NOTIFICATION_COUNT_ACTION_KEY = "calendar_notification_count_action";
	public static final String CALENDAR_MESSAGE_BODY_FONT_SIZE = "calendar_message_body_font_size";
	public static final String CALENDAR_LABELS_KEY = "calendar_labels_enabled";
	public static final String CALENDAR_SELECTION_KEY = "calendar_selection";
	
	public static final String CALENDAR_ID = "_id";
	public static final String CALENDAR_EVENT_ID = "event_id"; 
	public static final String CALENDAR_EVENT_TITLE = "title"; 
    public static final String CALENDAR_INSTANCE_BEGIN = "begin"; 
    public static final String CALENDAR_INSTANCE_END = "end"; 
    public static final String CALENDAR_EVENT_ALL_DAY = "allDay"; 
    public static final String CALENDAR_DISPLAY_NAME = "displayName"; 
    public static final String CALENDAR_SELECTED = "selected";
    public static final String CALENDAR_EVENT_BEGIN_TIME = "beginTime";
    public static final String CALENDAR_EVENT_END_TIME = "endTime";
    
	//-----GMAIL NOTIFICATION CONSTANTS-----//
	public static final String GMAIL_NOTIFICATION_COUNT_ACTION_KEY = "gmail_notification_count_action";	

	//-----TWITTER NOTIFICATION CONSTANTS-----//
	public static final String TWITTER_ENABLED_KEY = "twitter_enabled";
	
	public static final String TWITTER_CONSUMER_KEY = "AhWe8llUXyaZhix1oyhCA";
	public static final String TWITTER_CONSUMER_SECRET= "oFKn2cyLQanVESLWlwH0GK7twXisarFrClTfuZmVUI";
 	public static final String TWITTER_REQUEST_URL = "https://api.twitter.com/oauth/request_token";
	public static final String TWITTER_ACCESS_URL = "https://api.twitter.com/oauth/authorize";
	public static final String TWITTER_AUTHORIZE_URL = "https://api.twitter.com/oauth/access_token";
	public static final String TWITTER_CALLBACK_SCHEME = "droidnotify-oauth-twitter";
	public static final String TWITTER_CALLBACK_URL = TWITTER_CALLBACK_SCHEME + "://callback";
	
	//-----FACEBOOK NOTIFICATION CONSTANTS-----//
	
	
	//-----APPLICATION RATING CONSTANTS-----//
	//Android Market URL
	public static final String RATE_APP_ANDROID_URL = "http://market.android.com/details?id=apps.droidnotify";
	//Amazon Appstore URL
	public static final String RATE_APP_AMAZON_URL = "http://www.amazon.com/gp/mas/dl/android?p=apps.droidnotify";
	
	//-----EULA CONSTANTS-----//
	public static final String RUN_ONCE_EULA = "runOnceEula";
	public static final String RUN_ONCE_CALENDAR_ALARM = "runOnce_v_2_4";
	
	//-----STATUS BAR NOTIFICATION CONSTANTS-----//
	public static final String STATUS_BAR_NOTIFICATIONS_RINGTONE_DEFAULT = "content://settings/system/notification_sound";
	public static final String STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_DEFAULT = "0,1200";
	public static final String STATUS_BAR_NOTIFICATIONS_LED_PATTERN_DEFAULT = "1000,1000";
	public static final String STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT = "yellow";
	
	public static final String STATUS_BAR_NOTIFICATIONS_RINGTONE_SILENT_VALUE = "";
	public static final String STATUS_BAR_NOTIFICATIONS_VIBRATE_ALWAYS_VALUE = "0";
	public static final String STATUS_BAR_NOTIFICATIONS_VIBRATE_NEVER_VALUE = "1";
	public static final String STATUS_BAR_NOTIFICATIONS_VIBRATE_WHEN_VIBRATE_MODE_VALUE = "2";
	public static final String STATUS_BAR_NOTIFICATIONS_VIBRATE_DEFAULT = STATUS_BAR_NOTIFICATIONS_VIBRATE_ALWAYS_VALUE;
	
	//-----SMS STATUS BAR NOTIFICATION CONSTANTS-----//
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY = "sms_status_bar_notifications_enabled";
	
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY = "sms_notification_sound";
	
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY = "sms_notification_vibrate_setting";
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY = "sms_notification_vibrate_pattern";
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY = "sms_custom";
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY = "sms_notification_vibrate_pattern_custom";
	
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY = "sms_notification_led_enabled";
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY = "sms_notification_led_pattern";	
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY = "sms_custom";
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY = "sms_notification_led_pattern_custom";
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY = "sms_notification_led_color";	
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY = "sms_custom";
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY = "sms_notification_led_color_custom";
	
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY = "sms_notification_in_call_sound_enabled";
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY = "sms_notification_in_call_vibrate_enabled";
	
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY = "notification_icon_sms";
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT = "status_bar_notification_sms_green";
	
	//-----MMS STATUS BAR NOTIFICATION CONSTANTS-----//
	public static final String MMS_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY = "mms_status_bar_notifications_enabled";
	
	public static final String MMS_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY = "mms_notification_sound";
	
	public static final String MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY = "mms_notification_vibrate_setting";
	public static final String MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY = "mms_notification_vibrate_pattern";
	public static final String MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY = "mms_custom";
	public static final String MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY = "mms_notification_vibrate_pattern_custom";
	
	public static final String MMS_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY = "mms_notification_led_enabled";
	public static final String MMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY = "mms_notification_led_pattern";	
	public static final String MMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY = "mms_custom";
	public static final String MMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY = "mms_notification_led_pattern_custom";
	public static final String MMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY = "mms_notification_led_color";	
	public static final String MMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY = "mms_custom";
	public static final String MMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY = "mms_notification_led_color_custom";
	
	public static final String MMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY = "mms_notification_in_call_sound_enabled";
	public static final String MMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY = "mms_notification_in_call_vibrate_enabled";
	
	public static final String MMS_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY = "notification_icon_mms";
	public static final String MMS_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT = "status_bar_notification_sms_green";
	
	//-----PHONE STATUS BAR NOTIFICATION CONSTANTS-----//
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY = "missed_call_status_bar_notifications_enabled";
	
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY = "missed_call_notification_sound";
	
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY = "missed_call_notification_vibrate_setting";
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY = "missed_call_notification_vibrate_pattern";
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY = "phone_custom";
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY = "missed_call_notification_vibrate_pattern_custom";
	
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY = "missed_call_notification_led_enabled";
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY = "missed_call_notification_led_pattern";	
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY = "missed_call_custom";
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY = "missed_call_notification_led_pattern_custom";
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY = "missed_call_notification_led_color";	
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY = "phone_custom";
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY = "missed_call_notification_led_color_custom";
	
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY = "missed_call_notification_in_call_sound_enabled";
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY = "missed_call_notification_in_call_vibrate_enabled";
	
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY = "notification_icon_missed_call";
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT = "status_bar_notification_missed_call_black";
	
	//-----CALENDAR STATUS BAR NOTIFICATION CONSTANTS-----//
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY = "calendar_status_bar_notifications_enabled";
	
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY = "calendar_notification_sound";
	
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY = "calendar_notification_vibrate_setting";
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY = "calendar_notification_vibrate_pattern";
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY = "calendar_custom";
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY = "calendar_notification_vibrate_pattern_custom";
	
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY = "calendar_notification_led_enabled";
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY = "calendar_notification_led_pattern";	
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY = "calendar_custom";
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY = "calendar_notification_led_pattern_custom";
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY = "calendar_notification_led_color";	
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY = "calendar_custom";
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY = "calendar_notification_led_color_custom";
	
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY = "calendar_notification_in_call_sound_enabled";
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY = "calendar_notification_in_call_vibrate_enabled";	
	
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY = "notification_icon_calendar";
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT = "status_bar_notification_calendar_blue";
	
	//-----GMAIL STATUS BAR NOTIFICATION CONSTANTS-----//
	public static final String GMAIL_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY = "gmail_status_bar_notifications_enabled";
	
	public static final String GMAIL_STATUS_BAR_NOTIFICATIONS_SOUND_KEY = "gmail_notification_sound";
	
	public static final String GMAIL_STATUS_BAR_NOTIFICATIONS_VIBRATE_ENABLED_KEY = "gmail_notification_vibrate_enabled";
	public static final String GMAIL_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY = "gmail_notification_vibrate_pattern";
	public static final String GMAIL_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY = "gmail_custom";
	public static final String GMAIL_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY = "gmail_notification_vibrate_pattern_custom";
	
	public static final String GMAIL_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY = "gmail_notification_led_enabled";
	public static final String GMAIL_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY = "gmail_notification_led_pattern";	
	public static final String GMAIL_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY = "gmail_custom";
	public static final String GMAIL_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY = "gmail_notification_led_pattern_custom";
	public static final String GMAIL_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY = "gmail_notification_led_color";	
	public static final String GMAIL_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY = "gmail_custom";
	public static final String GMAIL_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY = "gmail_notification_led_color_custom";
	
	public static final String GMAIL_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY = "gmail_notification_in_call_sound_enabled";
	public static final String GMAIL_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY = "gmail_notification_in_call_vibrate_enabled";		
	
	//-----TWITTER STATUS BAR NOTIFICATION CONSTANTS-----//
	public static final String TWITTER_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY = "twitter_status_bar_notifications_enabled";
	
	public static final String TWITTER_STATUS_BAR_NOTIFICATIONS_SOUND_KEY = "twitter_notification_sound";
	
	public static final String TWITTER_STATUS_BAR_NOTIFICATIONS_VIBRATE_ENABLED_KEY = "twitter_notification_vibrate_enabled";
	public static final String TWITTER_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY = "twitter_notification_vibrate_pattern";
	public static final String TWITTER_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY = "twitter_custom";
	public static final String TWITTER_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY = "twitter_notification_vibrate_pattern_custom";
	
	public static final String TWITTER_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY = "twitter_notification_led_enabled";
	public static final String TWITTER_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY = "twitter_notification_led_pattern";	
	public static final String TWITTER_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY = "twitter_custom";
	public static final String TWITTER_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY = "twitter_notification_led_pattern_custom";
	public static final String TWITTER_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY = "twitter_notification_led_color";	
	public static final String TWITTER_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY = "twitter_custom";
	public static final String TWITTER_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY = "twitter_notification_led_color_custom";
	
	public static final String TWITTER_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY = "twitter_notification_in_call_sound_enabled";
	public static final String TWITTER_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY = "twitter_notification_in_call_vibrate_enabled";	
	
	//-----FACEBOOK STATUS BAR NOTIFICATION CONSTANTS-----//
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY = "facebook_status_bar_notifications_enabled";
	
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_SOUND_KEY = "facebook_notification_sound";
	
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_VIBRATE_ENABLED_KEY = "facebook_notification_vibrate_enabled";
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY = "facebook_notification_vibrate_pattern";
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY = "facebook_custom";
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY = "facebook_notification_vibrate_pattern_custom";
	
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY = "facebook_notification_led_enabled";
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY = "facebook_notification_led_pattern";	
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY = "facebook_custom";
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY = "facebook_notification_led_pattern_custom";
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY = "facebook_notification_led_color";	
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY = "facebook_custom";
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY = "facebook_notification_led_color_custom";
	
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY = "facebook_notification_in_call_sound_enabled";
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY = "facebook_notification_in_call_vibrate_enabled";	
	
}