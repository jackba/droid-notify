package apps.droidnotify.common;

/**
 * This class is a collection of all the constants used in this application.
 * 
 * @author Camille Sévigny
 */
public class Constants {
	
	//-----NOTIFICATIION BUNDLE CONSTANTS-----//
	public static final String BUNDLE_SENT_FROM_ADDRESS = "sentFromAddress";
	public static final String BUNDLE_SENT_FROM_ID  = "sentFromID";
	public static final String BUNDLE_MESSAGE_BODY  = "messageBody";
	public static final String BUNDLE_TIMESTAMP  = "timeStamp";
	public static final String BUNDLE_THREAD_ID  = "threadID";
	public static final String BUNDLE_CONTACT_ID  = "contactID";
	public static final String BUNDLE_CONTACT_NAME  = "contactName";
	public static final String BUNDLE_PHOTO_ID  = "photoID";
	public static final String BUNDLE_NOTIFICATION_TYPE  = "notificationType";
	public static final String BUNDLE_MESSAGE_ID  = "messageID";
	public static final String BUNDLE_MESSAGE_STRING_ID  = "messageStringID";
	public static final String BUNDLE_TITLE  = "title";
	public static final String BUNDLE_CALENDAR_ID  = "calendarID";
	public static final String BUNDLE_CALENDAR_EVENT_ID  = "calendarEventID";
	public static final String BUNDLE_CALENDAR_EVENT_START_TIME  = "calendarEventStartTime";
	public static final String BUNDLE_CALENDAR_EVENT_END_TIME  = "calendarEventEndTime";
	public static final String BUNDLE_CALENDAR_NAME  = "calendarName";
	public static final String BUNDLE_ALL_DAY  = "allDay";
	public static final String BUNDLE_CALL_LOG_ID  = "callLogID";
	public static final String BUNDLE_LOOKUP_KEY  = "lookupKey";
	public static final String BUNDLE_K9_EMAIL_URI  = "k9EmailUri";
	public static final String BUNDLE_K9_EMAIL_DEL_URI  = "k9EmailDelUri";
	public static final String BUNDLE_RESCHEDULE_NUMBER  = "rescheduleNumber";
	public static final String BUNDLE_NOTIFICATION_SUB_TYPE  = "notificationSubType";
	public static final String BUNDLE_LINK_URL  = "linkURL";
	
	public static final String BUNDLE_NAME_K9 = "k9NotificationBundle";
	
	//-----DEBUG CONSTANTS-----//
	public static final String DEBUG = "debug_log_enabled";
	
	//-----RUNONCE CONSTANTS-----//
	public static final String RUN_ONCE_EULA = "RUNONCE_EULA";
	public static final String RUN_ONCE_CALENDAR_ALARM = "RUNONCE_CALENDAR_V_2_16";
	
	//-----GENERAL APP CONSTANTS-----//
	public static final String DROID_NOTIFY_WAKELOCK = "DROID_NOTIFY_WAKELOCK";
	public static final String DROID_NOTIFY_KEYGUARD = "DROID_NOTIFY_KEYGUARD";
	
	public static final String APP_ENABLED_KEY = "app_enabled";
	public static final String HAPTIC_FEEDBACK_ENABLED_KEY = "haptic_feedback_enabled";
	public static final String LANDSCAPE_SCREEN_ENABLED_KEY = "landscape_screen_enabled";	
	public static final String BLUR_SCREEN_BACKGROUND_ENABLED_KEY = "blur_screen_background_enabled";
	public static final String DIM_SCREEN_BACKGROUND_ENABLED_KEY = "dim_screen_background_enabled";
	public static final String DIM_SCREEN_BACKGROUND_AMOUNT_KEY = "dim_screen_background_amount";
	public static final String SCREEN_TIMEOUT_KEY = "screen_timeout_settings";
	public static final String SCREEN_ENABLED_KEY = "screen_enabled";
	public static final String SCREEN_DIM_ENABLED_KEY = "screen_dim_enabled";
	public static final String KEYGUARD_ENABLED_KEY = "keyguard_enabled";
	public static final String HIDE_SINGLE_MESSAGE_HEADER_KEY = "hide_single_message_header_enabled";
    public static final String CLEAR_STATUS_BAR_NOTIFICATIONS_ON_EXIT_KEY = "clear_status_bar_notifications_on_exit_enabled";
    public static final String PREVIOUS_CALL_STATE_KEY = "previous_call_state";
	public static final String USER_IN_LINKED_APP_KEY = "user_in_linked_app";
	
	public static final String BLOCKING_APP_RUNNING_ACTION_RESCHEDULE = "0";
	public static final String BLOCKING_APP_RUNNING_ACTION_IGNORE = "1";
	public static final String BLOCKING_APP_RUNNING_ACTION_SHOW = "2";
	
	//-----NOTIFICATION TYPE CONSTANTS-----//
	public static final int NOTIFICATION_TYPE_TEST = -1;
	public static final int NOTIFICATION_TYPE_PHONE = 0;
	public static final int NOTIFICATION_TYPE_SMS = 1;
	public static final int NOTIFICATION_TYPE_MMS = 2;
	public static final int NOTIFICATION_TYPE_CALENDAR = 3;
	public static final int NOTIFICATION_TYPE_GMAIL = 4;
	public static final int NOTIFICATION_TYPE_TWITTER = 5;
	public static final int NOTIFICATION_TYPE_FACEBOOK = 6;
	public static final int NOTIFICATION_TYPE_K9 = 7;	
	public static final int NOTIFICATION_TYPE_LINKEDIN = 8;
	
	//-----NOTIFICATION RESCHEDULE CONSTANTS-----//
	public static final int NOTIFICATION_TYPE_RESCHEDULE_PHONE = 100;
	public static final int NOTIFICATION_TYPE_RESCHEDULE_SMS = 101;
	public static final int NOTIFICATION_TYPE_RESCHEDULE_MMS = 102;
	public static final int NOTIFICATION_TYPE_RESCHEDULE_CALENDAR = 103;
	public static final int NOTIFICATION_TYPE_RESCHEDULE_GMAIL = 104;
	public static final int NOTIFICATION_TYPE_RESCHEDULE_TWITTER = 105;
	public static final int NOTIFICATION_TYPE_RESCHEDULE_FACEBOOK = 106;
	public static final int NOTIFICATION_TYPE_RESCHEDULE_K9 = 107;
	public static final int NOTIFICATION_TYPE_RESCHEDULE_LINKEDIN = 108;

	//-----NOTIFICATION SUB-TYPE CONSTANTS-----//
	public static final int NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE = 200;
	public static final int NOTIFICATION_TYPE_TWITTER_MENTION = 201;
	public static final int NOTIFICATION_TYPE_TWITTER_FOLLOWER_REQUEST = 202;
	
	public static final int NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION = 210;
	public static final int NOTIFICATION_TYPE_FACEBOOK_MESSAGE = 211;
	public static final int NOTIFICATION_TYPE_FACEBOOK_FRIEND_REQUEST = 212;
	
	public static final int NOTIFICATION_TYPE_LINKEDIN_UPDATE = 220;
	
	public static final int NOTIFICATION_TYPE_K9_MAIL = 230;
	public static final int NOTIFICATION_TYPE_KAITEN_MAIL = 231;
	
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
	public static final int K9_VIEW_INBOX_ACTIVITY = 15;
	public static final int K9_VIEW_EMAIL_ACTIVITY = 16;
	public static final int K9_SEND_EMAIL_ACTIVITY = 17;
	public static final int TWITTER_OPEN_APP_ACTIVITY = 18;
	public static final int SEND_TWITTER_QUICK_REPLY_ACTIVITY = 19;
	public static final int FACEBOOK_OPEN_APP_ACTIVITY = 20;
	public static final int BROWSER_ACTIVITY = 21;
	
	public static final int MESSAGE_TYPE_SMS = 1;
	public static final int MESSAGE_TYPE_MMS = 2;
	
	//-----LANGUAGE CONSTANTS-----//	
	public static final String LANGUAGE_KEY = "language_setting";
	public static final String LANGUAGE_DEFAULT = "phone_default";
	
	//-----VIEW FLIPPER CONSTANTS-----//
	public static final String VIEW_NOTIFICATION_ORDER = "view_notification_order";
	public static final String NEWEST_FIRST = "0";
	public static final String OLDER_FIRST = "1";
	public static final String DISPLAY_NEWEST_NOTIFICATION = "display_newest_notification";
	
	//-----BUTTON CONSTANTS-----//
	public static final String BUTTON_DISPLAY_STYLE_KEY = "button_display_style";
	public static final String BUTTON_DISPLAY_STYLE_DEFAULT = "0";
	public static final String BUTTON_DISPLAY_BOTH_ICON_TEXT = "0";
	public static final String BUTTON_DISPLAY_ICON_ONLY = "1";
	public static final String BUTTON_DISPLAY_TEXT_ONLY = "2";
		
    public static final String BUTTON_ICONS_KEY = "button_icons_enabled";
	public static final String DISPLAY_RESCHEDULE_BUTTON_KEY = "display_reschedule_button";
	
	//-----CONTEXT MENU CONSTANTS-----//
	public static final String CONTEXT_MENU_DISABLED_KEY = "disable_context_menu";

	//-----QUICK CONTACT CONSTANTS-----//
	public static final String QUICK_CONTACT_DISABLED_KEY = "disable_quick_contact";
	
	
	//-----RESCHEDULE CONSTANTS-----//
	public static final String RESCHEDULE_NOTIFICATIONS_ENABLED_KEY = "reschedule_blocked_notifications";
	public static final String RESCHEDULE_BLOCKED_NOTIFICATION_TIMEOUT_KEY = "reschedule_blocked_notification_timeout";
	public static final String RESCHEDULE_BLOCKED_NOTIFICATION_TIMEOUT_DEFAULT = "5";
	public static final String RESCHEDULE_TIME_KEY = "reschedule_time";
	public static final String RESCHEDULE_TIME_DEFAULT = "60";

	//-----REMINDER CONSTANTS-----//
	public static final String REMINDERS_ENABLED_KEY = "reminders_enabled";
	public static final String REMINDER_FREQUENCY_KEY = "reminder_frequency";
	public static final String REMINDER_FREQUENCY_DEFAULT = "5";
	public static final String REMINDER_INTERVAL_KEY = "reminder_interval";
	public static final String REMINDER_INTERVAL_DEFAULT = "60";
	
	//-----CONTACT PHOTO CONSTANTS-----//
	public static final String CONTACT_PHOTO_DISPLAY_KEY = "display_contact_photo";
    public static final String CONTACT_PLACEHOLDER_KEY = "contact_placeholder";
    public static final String CONTACT_PLACEHOLDER_DEFAULT = "0";
    public static final String CONTACT_PHOTO_BACKGKROUND_KEY = "contact_photo_background";
	public static final String CONTACT_PHOTO_SIZE_KEY = "contact_photo_size";
	public static final String CONTACT_PHOTO_SIZE_DEFAULT = "80";
	
	//-----CONTACT NAME CONSTANTS-----//
	public static final String CONTACT_NAME_DISPLAY_KEY = "display_contact_name";
	public static final String CONTACT_NAME_HIDE_UNKNOWN_KEY = "hide_unknown_contact_name";
	public static final String CONTACT_NAME_SIZE_KEY = "contact_name_font_size";
	public static final String CONTACT_NAME_SIZE_DEFAULT = "22";
	
	//-----CONTACT NUMBER CONSTANTS-----//
	public static final String CONTACT_NUMBER_DISPLAY_KEY = "display_contact_number";
	public static final String CONTACT_NUMBER_SIZE_KEY = "contact_number_font_size";
	public static final String CONTACT_NUMBER_SIZE_DEFAULT = "18";
    
	
	//-----DIALOG CONSTANTS-----//
	public static final int DIALOG_DELETE_MESSAGE = 0;

	public static final int DIALOG_DONATE = 100;
	public static final int DIALOG_UPGRADE = 101;
	public static final int DIALOG_FEATURE_AVAILABLE_WITH_PRO_VERSION_GMAIL = 201;
	public static final int DIALOG_FEATURE_AVAILABLE_WITH_PRO_VERSION_TWITTER = 201;
	public static final int DIALOG_FEATURE_AVAILABLE_WITH_PRO_VERSION_FACEBOOK = 202;
	public static final int DIALOG_FEATURE_AVAILABLE_WITH_PRO_VERSION_LINKEDIN = 203;
	
	//-----UPGRADE CONSTATNS-----//
	public static final String UPGRADE_TO_PRO_PREFERENCE_KEY = "upgrade_app_to_pro";
	
	//Staring Array of the top SMS Messaging Apps:
	// Android Stock App
	// Handcent
	// Go SMS
	// Magic Text
	// Chomp SMS
	// Pansi
	// Text'n Drive
	//
	//---FORMAT = PACKAGE_NAME , CLASS_NAME---
	//
	public static final String[] BLOCKED_PACKAGE_NAMES_ARRAY = new String[]{
		"apps.droidnotify,apps.droidnotify.QuickReplyActivity", 
		"com.android.mms,com.android.mms.ui.ConversationList", 
		"com.android.mms,com.android.mms.ui.ComposeMessageActivity",
		"com.handcent.nextsms,com.handcent.sms.ui.ConversationExList",
		"com.jb.gosms,com.jb.gosms.ui.mainscreen.GoSmsMainActivity",  
		"com.p1.chompsms,com.p1.chompsms.activities.ConversationList", 
		"com.pansi.msg,com.pansi.msg.ui.ConversationList", 
		"com.pompeiicity.magictext,com.pompeiicity.magictext.SMSList",
		"com.zlango.zms,com.zlango.zms.app.ConversationList",
		"com.drivevox.drivevox" };
	
	//-----APP THEME CONSTANTS-----//
	public static final String APP_THEME_KEY = "app_theme";
	public static final String ANDROID_FROYO_THEME = "android";
	public static final String ANDROID_GINGERBREAD_THEME = "android_dark";
	public static final String ANDROID_ICECREAM_HOLO_DARK_THEME = "android_icecream_holo_dark";
	public static final String IPHONE_THEME = "iphone";
	public static final String DARK_TRANSLUCENT_THEME = "dark_translucent";
	public static final String DARK_TRANSLUCENT_V2_THEME = "dark_translucent_v2";
	public static final String DARK_TRANSLUCENT_V3_THEME = "dark_translucent_v3";
	public static final String HTC_SENSE_UI_THEME = "theme_htc";
	public static final String XPERIA_THEME = "theme_xperia";
	
	public static final String APP_THEME_DEFAULT = DARK_TRANSLUCENT_THEME;
	
	//-----PHONE NUMBER FORMAT CONSTANTS-----//
	public static final String PHONE_NUMBER_FORMAT_10_DIGITS_ONLY_KEY = "phone_number_format_10_digits_only";
	public static final String PHONE_NUMBER_FORMAT_KEY = "phone_number_format_settings";
	public static final String PHONE_NUMBER_FORMAT_DEFAULT = "1";
	public static final int PHONE_NUMBER_FORMAT_1 = 1;
	public static final int PHONE_NUMBER_FORMAT_2 = 2;
	public static final int PHONE_NUMBER_FORMAT_3 = 3;
	public static final int PHONE_NUMBER_FORMAT_4 = 4;
	public static final int PHONE_NUMBER_FORMAT_5 = 5;
	public static final int PHONE_NUMBER_FORMAT_6 = 6;
	public static final int PHONE_NUMBER_FORMAT_7 = 7;
	public static final int PHONE_NUMBER_FORMAT_8 = 8;
	public static final int PHONE_NUMBER_FORMAT_9 = 9;
	public static final int PHONE_NUMBER_FORMAT_10 = 10;
	public static final int PHONE_NUMBER_FORMAT_11 = 11;
	public static final int PHONE_NUMBER_FORMAT_12 = 12;
	public static final int PHONE_NUMBER_FORMAT_13 = 13;
	public static final int PHONE_NUMBER_FORMAT_14 = 14;
	
	//-----TIME FORMAT CONSTANTS-----//
	public static final String RUN_ONCE_DATE_TIME_FORMAT = "runOnceDateTimeFormat";
	public static final String TIME_FORMAT_KEY = "time_format_settings";
	public static final String TIME_FORMAT_DEFAULT = "0";
	public static final int TIME_FORMAT_12_HOUR = 0;
	public static final int TIME_FORMAT_24_HOUR = 1;

	//-----DATE FORMAT CONSTANTS-----//
	public static final String DATE_FORMAT_KEY = "date_format_settings";
	public static final String DATE_FORMAT_DEFAULT = "0";
	public static final int DATE_FORMAT_0 = 0;
	public static final int DATE_FORMAT_1 = 1;
	public static final int DATE_FORMAT_2 = 2;
	public static final int DATE_FORMAT_3 = 3;
	public static final int DATE_FORMAT_4 = 4;
	public static final int DATE_FORMAT_5 = 5;
	public static final int DATE_FORMAT_6 = 6;
	public static final int DATE_FORMAT_7 = 7;
	public static final int DATE_FORMAT_8 = 8;
	public static final int DATE_FORMAT_9 = 9;
	public static final int DATE_FORMAT_10 = 10;
	public static final int DATE_FORMAT_11 = 11;
	public static final int DATE_FORMAT_12 = 12;
	public static final int DATE_FORMAT_13 = 13;
	public static final int DATE_FORMAT_14 = 14;
	public static final int DATE_FORMAT_15 = 15;

	//-----QUIET TIME CONSTANTS-----//
	public static final String QUIET_TIME_BLACKOUT_PERIOD_SETTINGS_KEY = "quiet_time_blackout_period_settings";
	public static final String QUIET_TIME_ENABLED_KEY = "quiet_time_enabled";
	public static final String QUIET_TIME_OF_WEEK_KEY = "quiet_time_of_week";
	public static final String QUIET_TIME_EVERYDAY_VALUE = "0";
	public static final String QUIET_TIME_ONLY_WEEKEND_VALUE  = "1";
	public static final String QUIET_TIME_ONLY_WEEKDAY_VALUE  = "2";
	public static final String QUIET_TIME_START_TIME_KEY  = "quiet_time_start_time";
	public static final String QUIET_TIME_STOP_TIME_KEY  = "quiet_time_stop_time";
	
	
	//-----MESSAGE BODY CONSTANTS-----//
	public static final String NOTIFICATION_BODY_CENTER_ALIGN_TEXT_KEY = "center_align_body_text";
	public static final String NOTIFICATION_BODY_FONT_SIZE_KEY = "notification_body_font_size";
	public static final String NOTIFICATION_BODY_FONT_SIZE_DEFAULT = "14";
	
	public static final String NOTIFICATION_BODY_MAX_LINES_KEY = "notification_body_max_lines";
	public static final String NOTIFICATION_BODY_MAX_LINES_DEFAULT = "5";
	
	//-----NOTIFICATION TYPE INFO CONSTANTS-----//
	public static final String NOTIFICATION_TYPE_INFO_ICON_KEY = "display_notification_type_info_icon";
	public static final String NOTIFICATION_TYPE_INFO_FONT_SIZE_KEY = "notification_type_info_font_size";
	public static final String NOTIFICATION_TYPE_INFO_FONT_SIZE_DEFAULT = "14";	
	
	//-----QUICK REPLY CONSTANTS-----//
	public static final String SMS_GATEWAY_KEY = "quick_reply_sms_gateway_settings";	
	public static final String SAVE_MESSAGE_DRAFT_KEY = "quick_reply_save_draft_enabled";
	public static final String DISPLAY_QUICK_REPLY_CANCEL_BUTTON_KEY = "display_quick_reply_cancel_button";
	public static final String QUICK_REPLY_BLUR_SCREEN_BACKGROUND_ENABLED_KEY = "quick_reply_blur_screen_background_enabled";
	public static final String QUICK_REPLY_DIM_SCREEN_BACKGROUND_ENABLED_KEY = "quick_reply_dim_screen_background_enabled";
	public static final String QUICK_REPLY_DIM_SCREEN_BACKGROUND_AMOUNT_KEY = "quick_reply_dim_screen_background_amount";

	public static final String QUICK_REPLY_SETTINGS_SCREEN = "quick_reply_settings_screen";
	
	public static final int SMS_EMAIL_GATEWAY_1 = 1;
	public static final int SMS_EMAIL_GATEWAY_2 = 2;
	public static final int SMS_EMAIL_GATEWAY_3 = 3;
	public static final int SMS_EMAIL_GATEWAY_4 = 4;
	public static final int SMS_EMAIL_GATEWAY_5 = 5;
	public static final int SMS_EMAIL_GATEWAY_6 = 6;
	public static final int SMS_EMAIL_GATEWAY_7 = 7;
	public static final int SMS_EMAIL_GATEWAY_8 = 8;
	
	//-----PREFERENCE SCREEN CONSTANTS-----//
	public static final String PREFERENCE_SCREEN_NOTIFICATION_SETTINGS_KEY = "notifications_settings_screen";
	public static final String PREFERENCE_CATEGORY_APP_FEEDBACK_KEY = "app_feedback_category";
	public static final String PREFERENCE_CATEGORY_APP_LICENSE_KEY = "app_license_category";
	public static final String PREFERENCE_RATE_APP_KEY = "rate_app";
	public static final String ADVANCED_PREFERENCE_SCREEN_KEY = "advanced_settings_screen";
	
	//-----NOTIFICATION COUNT CONSTANTS-----//
	public static final String NOTIFICATION_COUNT_ACTION_NOTHING = "0";
	
	//-----SMS NOTIFICATION CONSTANTS-----//
	public static final String SMS_NOTIFICATIONS_ENABLED_KEY = "sms_notifications_enabled";
	public static final String SMS_READ_FROM_INTENT = "0";
	public static final String SMS_READ_FROM_DISK = "1";
	public static final String SMS_BLOCKING_APP_RUNNING_ACTION_KEY = "blocking_app_running_action_sms";
	public static final String SMS_TIMEOUT_KEY = "sms_timeout_settings";
	public static final String SMS_DELETE_KEY = "sms_delete_button_action";
	public static final String SMS_TIMESTAMP_ADJUSTMENT_KEY = "sms_timestamp_adjustment_settings";
	public static final String SMS_LOADING_SETTING_KEY = "sms_loading_settings"; 
    public static final String SMS_HIDE_NOTIFICATION_BODY_KEY = "sms_hide_notification_body_enabled";
	public static final String SMS_REPLY_BUTTON_ACTION_KEY = "sms_reply_button_action";
	public static final String SMS_DISPLAY_DISMISS_BUTTON_KEY = "sms_display_dismiss_button";
	public static final String SMS_DISPLAY_DELETE_BUTTON_KEY = "sms_display_delete_button";
	public static final String SMS_DISPLAY_REPLY_BUTTON_KEY = "sms_display_reply_button";
	public static final String SMS_NOTIFICATION_COUNT_ACTION_KEY = "sms_notification_count_action";
	public static final String SMS_DISMISS_KEY = "sms_dismiss_button_action";
	public static final String SMS_DISPLAY_UNREAD_KEY = "sms_display_unread_enabled";
	public static final String SMS_CONFIRM_DELETION_KEY = "confirm_sms_deletion";
	
	public static final String SMS_DISMISS_ACTION_MARK_READ = "0";
	public static final String SMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	public static final String SMS_DELETE_ACTION_DELETE_THREAD = "1";
	public static final String SMS_DELETE_ACTION_NOTHING = "2";
	
	public static final String SMS_MESSAGING_APP_REPLY = "0";
	public static final String SMS_QUICK_REPLY = "1";
	
	public static final String SMS_SPLIT_MESSAGE_KEY = "sms_split_message";
	
	public static final String SMS_TIME_IS_UTC_KEY = "sms_time_is_utc";
	
	//-----MMS NOTIFICATION CONSTANTS-----//
	public static final String MMS_NOTIFICATIONS_ENABLED_KEY = "mms_notifications_enabled";
	public static final String MMS_BLOCKING_APP_RUNNING_ACTION_KEY = "blocking_app_running_action_mms";
	public static final String MMS_TIMEOUT_KEY = "mms_timeout_settings";
	public static final String MMS_DELETE_KEY = "mms_delete_button_action";
    public static final String MMS_HIDE_NOTIFICATION_BODY_KEY = "mms_hide_notification_body_enabled";
	public static final String MMS_REPLY_BUTTON_ACTION_KEY = "mms_reply_button_action";
	public static final String MMS_DISPLAY_DISMISS_BUTTON_KEY = "mms_display_dismiss_button";
	public static final String MMS_DISPLAY_DELETE_BUTTON_KEY = "mms_display_delete_button";
	public static final String MMS_DISPLAY_REPLY_BUTTON_KEY = "mms_display_reply_button";
	public static final String MMS_NOTIFICATION_COUNT_ACTION_KEY = "mms_notification_count_action";
	public static final String MMS_DISMISS_KEY = "mms_dismiss_button_action";
	public static final String MMS_CONFIRM_DELETION_KEY = "confirm_mms_deletion";
	public static final String MMS_DISPLAY_UNREAD_KEY = "mms_display_unread_enabled";
	
	public static final String MMS_DISMISS_ACTION_MARK_READ = "0";
	public static final String MMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	public static final String MMS_DELETE_ACTION_DELETE_THREAD = "1";
	public static final String MMS_DELETE_ACTION_NOTHING = "2";
	
	public static final String MMS_MESSAGING_APP_REPLY = "0";
	public static final String MMS_QUICK_REPLY = "1";	

	//-----MISSED CALL NOTIFICATION CONSTANTS-----//
	public static final String PHONE_NOTIFICATIONS_ENABLED_KEY = "missed_call_notifications_enabled";
	public static final String PHONE_BLOCKING_APP_RUNNING_ACTION_KEY = "blocking_app_running_action_missed_call";
	public static final String CALL_LOG_TIMEOUT_KEY = "call_log_timeout_settings";
	public static final String PHONE_DISMISS_BUTTON_ACTION_KEY = "missed_call_loading_settings";
	public static final String PHONE_DISPLAY_DISMISS_BUTTON_KEY = "missed_call_display_dismiss_button";
	public static final String PHONE_DISPLAY_CALL_BUTTON_KEY = "missed_call_display_call_button";
	public static final String PHONE_NOTIFICATION_COUNT_ACTION_KEY = "missed_call_notification_count_action";
	public static final String PHONE_DISMISS_KEY = "missed_call_dismiss_button_action";
	
	public static final String PHONE_GET_LATEST = "0";
	public static final String PHONE_GET_RECENT = "1";
	public static final String PHONE_GET_ALL = "2";

	public static final String PHONE_DISMISS_ACTION_MARK_READ = "0";
	public static final String PHONE_DISMISS_ACTION_DELETE = "1";
	
	public static final int PHONE_TYPE = android.provider.CallLog.Calls.MISSED_TYPE;
	
	//-----CALENDAR NOTIFICATION CONSTANTS-----//
	public static final String CALENDAR_NOTIFICATIONS_ENABLED_KEY = "calendar_notifications_enabled";
	public static final String CALENDAR_BLOCKING_APP_RUNNING_ACTION_KEY = "blocking_app_running_action_calendar";
	public static final String CALENDAR_NOTIFY_DAY_OF_TIME_KEY = "calendar_notify_day_of_time";
	public static final String CALENDAR_REMINDERS_ENABLED_KEY = "calendar_reminders_enabled"; 
    public static final String CALENDAR_REMINDER_KEY = "calendar_reminder_settings";
    public static final String CALENDAR_REMINDER_ALL_DAY_KEY = "calendar_reminder_all_day_settings";
    public static final String CALENDAR_POLLING_FREQUENCY_KEY = "calendar_polling_frequency"; 
    public static final String CALENDAR_POLLING_FREQUENCY_DEFAULT = "15"; 
    public static final String CALENDAR_HIDE_NOTIFICATION_BODY_KEY = "calendar_hide_notification_body_enabled";
	public static final String CALENDAR_DISPLAY_DISMISS_BUTTON_KEY = "calendar_display_dismiss_button";
	public static final String CALENDAR_DISPLAY_VIEW_BUTTON_KEY = "calendar_display_view_button";
	public static final String CALENDAR_NOTIFICATION_COUNT_ACTION_KEY = "calendar_notification_count_action";
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
	public static final String TWITTER_PRO_PREFERENCE_CATEGORY_KEY = "twitter_notification_settings_category";
	public static final String TWITTER_PRO_PLACEHOLDER_PREFERENCE_KEY = "twitter_notification_settings_placeholder_preference";
	public static final String TWITTER_PRO_PREFERENCE_SCREEN_KEY = "twitter_notification_settings_screen";
	
	public static final String TWITTER_NOTIFICATIONS_ENABLED_KEY = "twitter_enabled";
	public static final String TWITTER_POLLING_FREQUENCY_KEY = "twitter_polling_frequency"; 
    public static final String TWITTER_POLLING_FREQUENCY_DEFAULT = "60"; 
	public static final String TWITTER_DIRECT_MESSAGES_ENABLED_KEY = "twitter_direct_messages_enabled";
	public static final String TWITTER_MENTIONS_ENABLED_KEY = "twitter_mentions_enabled";
	public static final String TWITTER_FOLLOWER_REQUESTS_ENABLED_KEY = "twitter_follower_requests_enabled";
	public static final String TWITTER_RETWEETS_ENABLED_KEY = "twitter_retweets_enabled";
	public static final String TWITTER_REPLY_TWEETS_ENABLED_KEY = "twitter_reply_tweets_enabled";
	
	public static final String TWITTER_BLOCKING_APP_RUNNING_ACTION_KEY = "blocking_app_running_action_twitter";
	
	public static final String TWITTER_CONSUMER_KEY = "AhWe8llUXyaZhix1oyhCA";
	public static final String TWITTER_CONSUMER_SECRET= "oFKn2cyLQanVESLWlwH0GK7twXisarFrClTfuZmVUI";
 	public static final String TWITTER_REQUEST_URL = "http://api.twitter.com/oauth/request_token";
	public static final String TWITTER_ACCESS_URL = "http://api.twitter.com/oauth/authorize";
	public static final String TWITTER_AUTHORIZE_URL = "http://api.twitter.com/oauth/access_token";
	public static final String TWITTER_CALLBACK_SCHEME = "droidnotify-oauth-twitter";
	public static final String TWITTER_CALLBACK_URL = TWITTER_CALLBACK_SCHEME + "://callback";
	public static final String TWITTER_OAUTH_TOKEN = "twitter_oauth_token";
	public static final String TWITTER_OAUTH_TOKEN_SECRET = "twitter_oauth_token_secret";

	public static final String TWITTER_DELETE_KEY = "twitter_delete_button_action";
	public static final String TWITTER_CONFIRM_DELETION_KEY = "twitter_confirm_deletion";
	
	public static final String TWITTER_DELETE_ACTION_DELETE_MESSAGE = "0";
	public static final String TWITTER_DELETE_ACTION_NOTHING = "2";	

    public static final String TWITTER_HIDE_NOTIFICATION_BODY_KEY = "twitter_hide_notification_body_enabled";
	public static final String TWITTER_DISPLAY_DISMISS_BUTTON_KEY = "twitter_display_dismiss_button";
	public static final String TWITTER_DISPLAY_VIEW_BUTTON_KEY = "twitter_display_view_button";
	public static final String TWITTER_DISPLAY_DELETE_BUTTON_KEY = "twitter_display_delete_button";
	public static final String TWITTER_DISPLAY_REPLY_BUTTON_KEY = "twitter_display_reply_button";
	public static final String TWITTER_NOTIFICATION_COUNT_ACTION_KEY = "twitter_notification_count_action";
	public static final String TWITTER_NOTIFICATION_COUNT_ACTION_LAUNCH_TWITTER_APP = "1";
	
	public static final String TWITTER_PREFERRED_CLIENT_KEY = "twitter_preferred_client";
	public static final String TWITTER_PREFERRED_CLIENT_DEFAULT = "com.twitter.android";
	public static final String TWITTER_USE_QUICK_REPLY = "0";
	public static final String TWITTER_USE_PREFERRED_CLIENT = "1";
	
	public static final String TWITTER_DIRECT_MESSAGE_DATE_FILTER_KEY = "twitter_direct_message_date_filter";
	public static final String TWITTER_MENTION_DATE_FILTER_KEY = "twitter_mention_date_filter";
	
	public static final String TWITTER_CLEAR_AUTHENTICATION_DATA_KEY = "clear_twitter_authentication";
	
	public static final String TWITTER_ADVANCED_SETTINGS_CATEGORY_KEY = "advanced_twitter_settings_category";
	
	public static final String TWITTER_REPLY_BUTTON_ACTION_KEY = "twitter_reply_button_action";
	public static final String TWITTER_DELETE_BUTTON_ACTION_KEY = "twitter_delete_button_action";
	
	//-----FACEBOOK NOTIFICATION CONSTANTS-----//
	public static final String FACEBOOK_PRO_PREFERENCE_CATEGORY_KEY = "facebook_notification_settings_category";
	public static final String FACEBOOK_PRO_PLACEHOLDER_PREFERENCE_KEY = "facebook_notification_settings_placeholder_preference";
	public static final String FACEBOOK_PRO_PREFERENCE_SCREEN_KEY = "facebook_notification_settings_screen";
	
	public static final String FACEBOOK_NOTIFICATIONS_ENABLED_KEY = "facebook_enabled";
	public static final String FACEBOOK_USER_NOTIFICATIONS_ENABLED_KEY = "facebook_user_notifications_enabled";
	public static final String FACEBOOK_FRIEND_REQUESTS_ENABLED_KEY = "facebook_friend_requests_enabled";
	public static final String FACEBOOK_MESSAGES_ENABLED_KEY = "facebook_messages_enabled";
	public static final String FACEBOOK_POLLING_FREQUENCY_KEY = "facebook_polling_frequency";
	
	public static final String FACEBOOK_BLOCKING_APP_RUNNING_ACTION_KEY = "blocking_app_running_action_facebook";
	
	public static final String FACEBOOK_APP_ID = "259676884089873";
	public static final String FACEBOOK_ACCESS_TOKEN_KEY = "facebook_access_token";
	public static final String FACEBOOK_ACCESS_EXPIRES_KEY = "facebook_access_expires";
	
	public static final String FACEBOOK_DELETE_KEY = "facebook_delete_button_action";
	public static final String FACEBOOK_CONFIRM_DELETION_KEY = "facebook_confirm_deletion";
	
	public static final String FACEBOOK_DISMISS_ACTION_MARK_READ = "0";

    public static final String FACEBOOK_HIDE_NOTIFICATION_BODY_KEY = "facebook_hide_notification_body_enabled";
	public static final String FACEBOOK_DISPLAY_DISMISS_BUTTON_KEY = "facebook_display_dismiss_button";
	public static final String FACEBOOK_DISPLAY_DELETE_BUTTON_KEY = "facebook_display_delete_button";
	public static final String FACEBOOK_DISPLAY_VIEW_BUTTON_KEY = "facebook_display_view_button";
	public static final String FACEBOOK_DISPLAY_REPLY_BUTTON_KEY = "facebook_display_reply_button";
	public static final String FACEBOOK_NOTIFICATION_COUNT_ACTION_KEY = "facebook_notification_count_action";
	public static final String FACEBOOK_NOTIFICATION_COUNT_ACTION_LAUNCH_FACEBOOK_APP = "1";
	
	public static final String FACEBOOK_ADVANCED_SETTINGS_CATEGORY_KEY = "advanced_facebook_settings_category";
	
	public static final String FACEBOOK_CLEAR_AUTHENTICATION_DATA_KEY = "clear_facebook_authentication";
	
	public static final String FACEBOOK_PREFERRED_CLIENT_KEY = "facebook_preferred_client";
	public static final String FACEBOOK_PREFERRED_CLIENT_DEFAULT = "com.facebook.katana";
	public static final String FACEBOOK_USE_QUICK_REPLY = "0";
	public static final String FACEBOOK_USE_PREFERRED_CLIENT = "1";
	
	//-----K9 NOTIFICATION CONSTANTS-----//
	public static final String INTENT_ACTION_K9 = "com.fsck.k9.intent.action.EMAIL_RECEIVED";
	public static final String INTENT_ACTION_KAITEN = "com.kaitenmail.intent.action.EMAIL_RECEIVED";
	
	public static final String K9_NOTIFICATIONS_ENABLED_KEY = "k9_notifications_enabled";
	public static final String K9_BLOCKING_APP_RUNNING_ACTION_KEY = "blocking_app_running_action_k9";
	public static final String K9_DELETE_KEY = "k9_delete_button_action";
    public static final String K9_HIDE_NOTIFICATION_BODY_KEY = "k9_hide_notification_body_enabled";
	public static final String K9_REPLY_BUTTON_ACTION_KEY = "k9_reply_button_action";
	public static final String K9_DISPLAY_DISMISS_BUTTON_KEY = "k9_display_dismiss_button";
	public static final String K9_DISPLAY_DELETE_BUTTON_KEY = "k9_display_delete_button";
	public static final String K9_DISPLAY_REPLY_BUTTON_KEY = "k9_display_reply_button";
	public static final String K9_NOTIFICATION_COUNT_ACTION_KEY = "k9_notification_count_action";
	public static final String K9_NOTIFICATION_COUNT_ACTION_K9_INBOX = "1";
	public static final String K9_DISMISS_KEY = "k9_dismiss_button_action";
	public static final String K9_CONFIRM_DELETION_KEY = "confirm_k9_deletion";
	
	public static final String K9_DISMISS_ACTION_MARK_READ = "0";
	public static final String K9_DELETE_ACTION_DELETE_MESSAGE = "0";
	public static final String K9_DELETE_ACTION_NOTHING = "2";	
	
	//-----LINKEDIN NOTIFICATION CONSTANTS-----//
	public static final String LINKEDIN_PRO_PREFERENCE_CATEGORY_KEY = "linkedin_notification_settings_category";
	public static final String LINKEDIN_PRO_PLACEHOLDER_PREFERENCE_KEY = "linkedin_notification_settings_placeholder_preference";
	public static final String LINKEDIN_PRO_PREFERENCE_SCREEN_KEY = "linkedin_notification_settings_screen";
	
	public static final String LINKEDIN_NOTIFICATIONS_ENABLED_KEY = "linkedin_enabled";	
	public static final String LINKEDIN_UPDATES_ENABLED_KEY = "linkedin_updates_enabled";	
	public static final String LINKEDIN_POLLING_FREQUENCY_KEY = "linkedin_polling_frequency"; 
    public static final String LINKEDIN_POLLING_FREQUENCY_DEFAULT = "60"; 
	
	public static final String LINKEDIN_CONSUMER_KEY = "i1ktinzc0j0c";
	public static final String LINKEDIN_CONSUMER_SECRET= "IaqaWiDaHxY0X0v3";
	public static final String LINKEDIN_CALLBACK_SCHEME = "droidnotify-oauth-linkedin";
	public static final String LINKEDIN_CALLBACK_URL = LINKEDIN_CALLBACK_SCHEME + "://callback";
	public static final String LINKEDIN_OAUTH_TOKEN = "linkedin_oauth_token";
	public static final String LINKEDIN_OAUTH_TOKEN_SECRET = "linkedin_oauth_token_secret";
	
	public static final String LINKEDIN_BLOCKING_APP_RUNNING_ACTION_KEY = "blocking_app_running_action_linkedin";
	
	public static final String LINKEDIN_ADVANCED_SETTINGS_CATEGORY_KEY = "advanced_linkedin_settings_category";	
	public static final String LINKEDIN_CLEAR_AUTHENTICATION_DATA_KEY = "clear_linkedin_authentication";
	
	//-----GOOGLEPLUS NOTIFICATION CONSTANTS-----//
	public static final String GOOGLEPLUS_CONSUMER_KEY = "503968671076.apps.googleusercontent.com";
	public static final String GOOGLEPLUS_CONSUMER_SECRET= "a8q_wS-TG11-vV96vMl8zHic";
	
	//-----APPLICATION URL CONSTANTS-----//
	//Android Market URL
	public static final String APP_ANDROID_URL = "http://market.android.com/details?id=apps.droidnotify";
	public static final String APP_PRO_ANDROID_URL = "http://market.android.com/details?id=apps.droidnotifydonate";
	//Amazon Appstore URL
	public static final String APP_AMAZON_URL = "http://www.amazon.com/gp/mas/dl/android?p=apps.droidnotify";
	public static final String APP_PRO_AMAZON_URL = "http://www.amazon.com/gp/mas/dl/android?p=apps.droidnotifydonate";
	//PayPal URL
	public static final String APP_PAYPAL_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=43V2NJQH5BQAA";
	
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
	
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY = "sms_status_bar_notifications_show_when_blocked_enabled";
	
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
	
	public static final String MMS_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY = "mms_status_bar_notifications_show_when_blocked_enabled";
	
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
	
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY = "missed_call_status_bar_notifications_show_when_blocked_enabled";
	
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
	
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY = "calendar_status_bar_notifications_show_when_blocked_enabled";
	
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
	
	//-----K9 STATUS BAR NOTIFICATION CONSTANTS-----//
	public static final String K9_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY = "k9_status_bar_notifications_enabled";
	
	public static final String K9_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY = "k9_status_bar_notifications_show_when_blocked_enabled";

	public static final String K9_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY = "k9_notification_sound";
	
	public static final String K9_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY = "k9_notification_vibrate_setting";
	public static final String K9_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY = "k9_notification_vibrate_pattern";
	public static final String K9_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY = "k9_custom";
	public static final String K9_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY = "k9_notification_vibrate_pattern_custom";
	
	public static final String K9_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY = "k9_notification_led_enabled";
	public static final String K9_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY = "k9_notification_led_pattern";	
	public static final String K9_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY = "k9_custom";
	public static final String K9_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY = "k9_notification_led_pattern_custom";
	public static final String K9_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY = "k9_notification_led_color";	
	public static final String K9_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY = "k9_custom";
	public static final String K9_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY = "k9_notification_led_color_custom";
	
	public static final String K9_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY = "k9_notification_in_call_sound_enabled";
	public static final String K9_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY = "k9_notification_in_call_vibrate_enabled";	
	
	public static final String K9_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY = "notification_icon_k9";
	public static final String K9_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT = "status_bar_notification_email_white";
	
	//-----GMAIL STATUS BAR NOTIFICATION CONSTANTS-----//
	public static final String GMAIL_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY = "gmail_status_bar_notifications_enabled";
	
	public static final String GMAIL_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY = "gmail_notification_sound";
	
	public static final String GMAIL_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY = "gmail_notification_vibrate_setting";
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
	
	public static final String TWITTER_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY = "twitter_status_bar_notifications_show_when_blocked_enabled";
	
	public static final String TWITTER_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY = "twitter_notification_sound";
	
	public static final String TWITTER_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY = "twitter_notification_vibrate_setting";
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
	
	public static final String TWITTER_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY = "notification_icon_twitter";
	public static final String TWITTER_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT = "status_bar_notification_twitter_blue";
	
	//-----FACEBOOK STATUS BAR NOTIFICATION CONSTANTS-----//
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY = "facebook_status_bar_notifications_enabled";
	
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY = "facebook_status_bar_notifications_show_when_blocked_enabled";
	
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY = "facebook_notification_sound";
	
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY = "facebook_notification_vibrate_setting";
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
	
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY = "notification_icon_facebook";
	public static final String FACEBOOK_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT = "status_bar_notification_facebook_blue";

	//-----LINKEDIN STATUS BAR NOTIFICATION CONSTANTS-----//
	public static final String LINKEDIN_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY = "linkedin_status_bar_notifications_enabled";
	
	public static final String LINKEDIN_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY = "linkedin_status_bar_notifications_show_when_blocked_enabled";
	
	//-----TEXT-TO-SPEECH CONSTANTS-----//
	public static final String DISPLAY_TEXT_TO_SPEECH_KEY = "display_text_to_speech_button";
	public static final int TEXT_TO_SPEECH_ACTIVITY = 500;
	
	//-----APPLICATION PUSHED TO BACKGROUND CONSTANTS-----//
	public static final String APPLICATION_CLOSE_WHEN_PUSHED_TO_BACKGROUND_KEY = "close_app_when_pushed_to_background";
	
	//-----IN-CALL SETTINGS CONSTANTS-----//
	public static final String IN_CALL_RESCHEDULING_ENABLED_KEY = "in_call_rescheduling_enabled";
	
	//-----POPUP FORMATTING CONSTANTS-----//
	public static final String POPUP_WINDOW_WIDTH_PADDING_KEY = "popup_width_padding";
	
	//-----FLASH SMS CONSTANTS-----//
	public static final String SMS_IGNORE_CLASS_0_MESSAGES_KEY = "sms_ignore_class_0_messages";	
	
}