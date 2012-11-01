package apps.droidnotify.common;

/**
 * This class is a collection of all the constants used in this application.
 * 
 * @author Camille Sévigny
 */
public class Constants {

	//-----INTENT ACTION CONSTANTS-----//
	public static final String INTENT_ACTION_CALENDAR_ALARMS  = "apps.droidnotify.calendar.alarms";

	//-----DEBUG CONSTANTS-----//
	public static final String LOGTAG = "DroidNotifyLite";
	public static final String DEBUG = "debug_log_enabled";

	//-----FIRST RUN CONSTANTS-----//	
	public static final String FIRST_RUN_KEY = LOGTAG + "_first_run";

	//-----NOTIFICATIION BUNDLE CONSTANTS-----//
	public static final String BUNDLE_SENT_FROM_ADDRESS = "sentFromAddress";
	public static final String BUNDLE_SENT_FROM_ID  = "sentFromID";
	public static final String BUNDLE_MESSAGE_BODY  = "messageBody";
	public static final String BUNDLE_TIMESTAMP  = "timeStamp";
	public static final String BUNDLE_THREAD_ID  = "threadID";
	public static final String BUNDLE_CONTACT_ID  = "contactID";
	public static final String BUNDLE_CONTACT_NAME  = "contactName";
	public static final String BUNDLE_CONTACT_PHONE_NUMBERS  = "contactPhoneNumbers";
	public static final String BUNDLE_CONTACT_EMAIL_ADDRESSES  = "contactEmailAddresses";
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
	public static final String BUNDLE_K9_EMAIL_ACCOUNT_NAME  = "k9EmailAccountName";
	public static final String BUNDLE_REMINDER_NUMBER  = "reminderNumber";
	public static final String BUNDLE_NOTIFICATION_SUB_TYPE  = "notificationSubType";
	public static final String BUNDLE_LINK_URL  = "linkURL";
	public static final String BUNDLE_PACKAGE  = "package";	
	public static final String BUNDLE_DISPLAY_TEXT  = "displayText";
	public static final String BUNDLE_DISMISS_PENDINGINTENT  = "dismissPendingIntent";
	public static final String BUNDLE_DELETE_PENDINGINTENT  = "deletePendingIntent";
	public static final String BUNDLE_VIEW_PENDINGINTENT  = "viewPendingIntent";
	public static final String BUNDLE_STATUS_BAR_NOTIFICATON_BUNDLE  = "statusBarNotificationBundle";
	public static final String BUNDLE_ENABLE_STATUS_BAR_NOTIFICATION = "enableStatusBarNotification";
	public static final String BUNDLE_STATUS_BAR_NOTIFICATION_SOUND_URI = "statusBarNotificationSoundURI";
	public static final String BUNDLE_STATUS_BAR_NOTIFICATION_VIBRATE_SETTING = "statusBarNotificationVibrateSetting";
	public static final String BUNDLE_STATUS_BAR_NOTIFICATION_VIBRATE_PATTERN = "statusBarNotificationVibratePattern";
	public static final String BUNDLE_STATUS_BAR_NOTIFICATION_LED_ENABLED = "statusBarNotificationLEDEnabled";
	public static final String BUNDLE_STATUS_BAR_NOTIFICATION_LED_PATTERN = "statusBarNotificationLEDPattern";
	public static final String BUNDLE_STATUS_BAR_NOTIFICATION_LED_COLOR = "statusBarNotificationLEDColor";
	public static final String BUNDLE_STATUS_BAR_NOTIFICATION_IN_CALL_SOUND_ENABLED = "statusBarNotificationInCallSoundEnabled";
	public static final String BUNDLE_STATUS_BAR_NOTIFICATION_IN_CALL_VIBRATE_ENABLED = "statusBarNotificationInCallVibrateEnabled";
	
	public static final String BUNDLE_NOTIFICATION_BUNDLE_NAME = "NOTIFICATION_BUNDLE_NAME";
	public static final String BUNDLE_NOTIFICATION_BUNDLE_COUNT = "NOTIFICATION_BUNDLE_COUNT";
	
	//-----QUICK REPLY CONSTANTS-----//
	public static final String QUICK_REPLY_ENABLED_KEY = "quick_reply_enabled";
	public static final String QUICK_REPLY_SIGNATURE_ENABLED_KEY = "quick_reply_signature_enabled";
	public static final String QUICK_REPLY_SIGNATURE_KEY = "quick_reply_signature";
	public static final String QUICK_REPLY_HIDE_HEADER_PANEL_KEY = "quick_reply_hide_header_panel";
	public static final String QUICK_REPLY_HIDE_CONTACT_PANEL_KEY = "quick_reply_hide_contact_panel";
	public static final String QUICK_REPLY_HIDE_BUTTON_PANEL_KEY = "quick_reply_hide_button_panel";
	public static final String QUICK_REPLY_MESSAGE_SENT_DELIVERY_REPORT_ENABLED_KEY = "message_sent_notification_enabled";
	public static final String QUICK_REPLY_MESSAGE_DELIVERED_DELIVERY_REPORT_ENABLED_KEY = "message_delivered_notification_enabled";
	public static final String QUICK_REPLY_MESSAGE_FAILED_DELIVERY_REPORT_ENABLED_KEY = "message_failed_notification_enabled";
	
	//-----GENERAL APP CONSTANTS-----//
	public static final String DROID_NOTIFY_WAKELOCK = "app.droidnotify.wakelock";
	public static final String DROID_NOTIFY_KEYGUARD = "app.droidnotify.keyguard";
	
	public static final String APP_ENABLED_KEY = "app_enabled";
	public static final String HAPTIC_FEEDBACK_ENABLED_KEY = "haptic_feedback_enabled";
	public static final String AUTO_ROTATE_SCREEN_KEY = "landscape_screen_enabled";	
	public static final String BLUR_SCREEN_BACKGROUND_ENABLED_KEY = "blur_screen_background_enabled";
	public static final String DIM_SCREEN_BACKGROUND_ENABLED_KEY = "dim_screen_background_enabled";
	public static final String DIM_SCREEN_BACKGROUND_AMOUNT_KEY = "dim_screen_background_amount";
	public static final String SCREEN_TIMEOUT_KEY = "screen_timeout_settings";
	public static final String SCREEN_ENABLED_KEY = "screen_enabled";
	public static final String SCREEN_DIM_ENABLED_KEY = "screen_dim_enabled";
	public static final String KEYGUARD_ENABLED_KEY = "keyguard_enabled";
	public static final String HIDE_SINGLE_MESSAGE_HEADER_KEY = "hide_single_message_header_enabled";
    public static final String CLEAR_STATUS_BAR_NOTIFICATIONS_ON_EXIT_KEY = "clear_status_bar_notifications_on_exit_enabled";
    
	public static final String CALL_STATE_KEY = LOGTAG + "_call_state";
	public static final String PREVIOUS_CALL_STATE_KEY = LOGTAG + "_previous_call_state";
    
	//-----SETTING ACTIVITIES CONSTANTS-----//
	public static final String SETTINGS_STATUS_BAR_NOTIFICATIONS_PREFERENCE = "status_bar_notifications_preference";
	public static final String SETTINGS_CUSTOMIZE_PREFERENCE = "customize_preference";
    
    //-----LINKED APP CONSTANTS-----//
	public static final String USER_IN_LINKED_APP_KEY = "user_in_linked_app";

	//-----BLOCKING APP CONSTANTS-----//
	public static final String BLOCKING_APPS_ENABLED_KEY = "blocking_apps_enabled";
	public static final String BLOCKING_APPS_ACTION_KEY = "blocking_app_action";
	public static final String BLOCKING_APPS_ACTION_RESCHEDULE = "0";
	public static final String BLOCKING_APPS_ACTION_IGNORE = "1";
	
	//-----NOTIFICATION TYPE CONSTANTS-----//
	public static final int NOTIFICATION_TYPE_PHONE = 0;
	public static final int NOTIFICATION_TYPE_SMS = 1;
	public static final int NOTIFICATION_TYPE_MMS = 2;
	public static final int NOTIFICATION_TYPE_CALENDAR = 3;
	public static final int NOTIFICATION_TYPE_K9 = 4;

	public static final int NOTIFICATION_TYPE_GENERIC = 1000;
	
	//-----NOTIFICATION TYPE PREVIEW CONSTANTS-----//
	public static final int NOTIFICATION_TYPE_PREVIEW_PHONE = 2000;
	public static final int NOTIFICATION_TYPE_PREVIEW_SMS = 2001;
	public static final int NOTIFICATION_TYPE_PREVIEW_MMS = 2002;
	public static final int NOTIFICATION_TYPE_PREVIEW_CALENDAR = 2003;
	public static final int NOTIFICATION_TYPE_PREVIEW_K9 = 2004;

	//-----NOTIFICATION SUB-TYPE CONSTANTS-----//
	
	public static final int NOTIFICATION_TYPE_K9_MAIL = 230;
	public static final int NOTIFICATION_TYPE_KAITEN_MAIL = 231;
	public static final int NOTIFICATION_TYPE_K9_FOR_PURE = 232;
	
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
	public static final int VIEW_CALL_LOG_ACTIVITY = 14;
	public static final int K9_VIEW_INBOX_ACTIVITY = 15;
	public static final int K9_VIEW_EMAIL_ACTIVITY = 16;
	public static final int K9_SEND_EMAIL_ACTIVITY = 17;
	public static final int BROWSER_ACTIVITY = 21;
	public static final int CONTACT_PICKER_ACTIVITY = 22;
	public static final int RINGTONE_PICKER_ACTIVITY = 23;
	public static final int STT_ACTIVITY = 26;
	
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
	
	//-----CONTEXT MENU CONSTANTS-----//
	public static final String CONTEXT_MENU_DISABLED_KEY = "disable_context_menu";

	//-----QUICK CONTACT CONSTANTS-----//
	public static final String QUICK_CONTACT_DISABLED_KEY = "disable_quick_contact";
	
	//-----BUTTON CONSTANTS-----//
	public static final String BUTTON_DISPLAY_STYLE_KEY = "button_display_style";
	public static final String BUTTON_DISPLAY_STYLE_DEFAULT = "0";
	public static final String BUTTON_DISPLAY_BOTH_ICON_TEXT = "0";
	public static final String BUTTON_DISPLAY_ICON_ONLY = "1";
	public static final String BUTTON_DISPLAY_TEXT_ONLY = "2";	
	
    public static final String BUTTON_ICONS_KEY = "button_icons_enabled";
	
    public static final String BUTTON_FONT_SIZE_KEY = "button_font_size";
    public static final String BUTTON_FONT_SIZE_DEFAULT = "14";
    
    public static final String BUTTON_BOLD_TEXT_KEY = "bold_button_text";
    
    //-----PENDING INTENT TYPE CONSTANTS-----//
	public static final int PENDING_INTENT_TYPE_REMINDER = 1;
	public static final int PENDING_INTENT_TYPE_SNOOZE = 2;
	public static final int PENDING_INTENT_TYPE_RESCHEDULE = 3;
    
	//-----RESCHEDULE CONSTANTS-----//
	public static final String RESCHEDULE_NOTIFICATIONS_ENABLED_KEY = "reschedule_blocked_notifications";
	public static final String RESCHEDULE_BLOCKED_NOTIFICATION_TIMEOUT_KEY = "reschedule_blocked_notification_timeout";
	public static final String RESCHEDULE_BLOCKED_NOTIFICATION_TIMEOUT_DEFAULT = "5";
	public static final String RESCHEDULE_TIME_KEY = "reschedule_time";
	public static final String RESCHEDULE_TIME_DEFAULT = "60";
	public static final String DISPLAY_RESCHEDULE_BUTTON_KEY = "display_reschedule_button";

	//-----REMINDER CONSTANTS-----//
	public static final String REMINDERS_ENABLED_KEY = "reminders_enabled";
	public static final String REMINDER_FREQUENCY_KEY = "reminder_frequency";
	public static final String REMINDER_FREQUENCY_DEFAULT = "5";
	public static final String REMINDER_INTERVAL_KEY = "reminder_interval";
	public static final String REMINDER_INTERVAL_DEFAULT = "60";
	
	//-----CONTACT PHOTO CONSTANTS-----//
	public static final String CONTACT_PHOTO_DISPLAY_KEY = "display_contact_photo";
    public static final String CONTACT_PLACEHOLDER_KEY = "contact_placeholder";
    public static final String CONTACT_PLACEHOLDER_DEFAULT = "1";
    public static final String CONTACT_PHOTO_BACKGKROUND_KEY = "contact_photo_background";
	public static final String CONTACT_PHOTO_SIZE_KEY = "contact_photo_size";
	public static final String CONTACT_PHOTO_SIZE_DEFAULT = "100";
	
	//-----CONTACT NAME CONSTANTS-----//
	public static final String CONTACT_NAME_DISPLAY_KEY = "display_contact_name";
	public static final String CONTACT_NAME_HIDE_UNKNOWN_KEY = "hide_unknown_contact_name";
	public static final String CONTACT_NAME_SIZE_KEY = "contact_name_font_size";
	public static final String CONTACT_NAME_SIZE_DEFAULT = "22";
	public static final String CONTACT_NAME_CENTER_ALIGN_KEY = "center_align_contact_name";
	public static final String CONTACT_NAME_BOLD_KEY = "bold_contact_name";
	
	//-----CONTACT NUMBER CONSTANTS-----//
	public static final String CONTACT_NUMBER_DISPLAY_KEY = "display_contact_number";
	public static final String CONTACT_NUMBER_SIZE_KEY = "contact_number_font_size";
	public static final String CONTACT_NUMBER_SIZE_DEFAULT = "18";
	public static final String CONTACT_NUMBER_DISPLAY_UNKNOWN_KEY = "display_unknown_contact_number";  
	public static final String CONTACT_NUMBER_CENTER_ALIGN_KEY = "center_align_contact_number";
	public static final String CONTACT_NUMBER_BOLD_KEY = "bold_contact_number";
	
	//-----DIALOG CONSTANTS-----//
	public static final String DIALOG_UPGRADE_TYPE = "upgrade_type";
	
	public static final int DIALOG_DONATE = 100;
	public static final int DIALOG_UPGRADE = 101;
	public static final int DIALOG_FEATURE_PRO_ONLY = 102;
	
	public static final int DIALOG_K9_CLIENT_NOT_INSTALLED = 501;
	
	//-----UPGRADE CONSTATNS-----//
	public static final String UPGRADE_TO_PRO_PREFERENCE_KEY = "upgrade_app_to_pro";
	
	//Array of the top SMS Messaging Apps:
	//---FORMAT = PACKAGE_NAME , CLASS_NAME---
	// Android Stock SMS App
	// Handcent
	// Go SMS
	// Chomp SMS
	// Pansi
	// Magic Text
	// Zlango Messaging
	// WhatsApp
	// Motorola Messaging
	// Hookt
	// Google Talk
	// Google Messenger
	//
	public static final String[] BLOCKED_SMS_PACKAGE_NAMES_ARRAY = new String[]{ 
		"com.android.mms,com.android.mms.ui.ConversationList", 
		"com.android.mms,com.android.mms.ui.ComposeMessageActivity",
		"com.handcent.nextsms,com.handcent.sms.ui.ConversationExList",
		"com.jb.gosms,com.jb.gosms.ui.mainscreen.GoSmsMainActivity",  
		"com.p1.chompsms,com.p1.chompsms.activities.ConversationList", 
		"com.pansi.msg,com.pansi.msg.ui.ConversationList", 
		"com.pompeiicity.magictext,com.pompeiicity.magictext.SMSList",
		"com.zlango.zms,com.zlango.zms.app.ConversationList",
		"com.whatsapp,com.whatsapp.Conversations",
		"com.motorola.blur.conversations,com.motorola.blur.conversations.ui.ConversationList",
		"com.motorola.blur.messaging,com.motorola.blur.messaging.MessagingActivity",
		"com.airg.hookt,com.airg.hookt.activity.Conversation",
		"com.google.android.talk,com.google.android.talk.SigningInActivity",
		"com.google.android.apps.plus,com.google.android.apps.plus.phone.ConversationActivity"};

	//Array of the top Email Messaging Apps:
	//---FORMAT = PACKAGE_NAME , CLASS_NAME---
	// Android Stock Email App
	// Google Gmail
	// Kaiten Mail
	// K-9 Mail
	// Yahoo Mail
	// Hotmail
	//
	public static final String[] BLOCKED_EMAIL_PACKAGE_NAMES_ARRAY = new String[]{ 
		"com.android.email,com.android.email.activity.AccountFolderList,",
		"com.google.android.gm,com.google.android.gm.ConversationListActivity",
		"com.kaitenmail,com.kaitenmail.activity.Accounts",
		"com.fsck.k9,com.fsck.k9.activity.Accounts",
		"com.yahoo.mobile.client.android.mail,com.yahoo.mobile.client.android.mail.activity.YahooMail",
		"com.hotmail.Z7,com.seven.Z7.app.email.EmailFront"};

	//Array of the Misc Apps:
	//---FORMAT = PACKAGE_NAME , CLASS_NAME---
	// Text'n Drive
	// Motorola You Mail Voicemail
	//
	public static final String[] BLOCKED_MISC_PACKAGE_NAMES_ARRAY = new String[]{
		"com.drivevox.drivevox",
		"com.youmail.android.vvm,com.youmail.android.vvm.activity.MainTabActivity"};
	
	//-----APP THEME CONSTANTS-----//
    public static final String APP_THEME_KEY = "app_theme";       
	public static final String APP_THEME_PREFIX = "apps.droidnotify.theme.";
	public static final String NOTIFY_DEFAULT_THEME = "apps.droidnotify.theme.default.notify";
	public static final String PHONE_DEFAULT_THEME = "apps.droidnotify.theme.default.phone";
	
	public static final String APP_THEME_DEFAULT = NOTIFY_DEFAULT_THEME;
	
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
	public static final int PHONE_NUMBER_FORMAT_15 = 15;
	public static final int PHONE_NUMBER_FORMAT_16 = 16;
	public static final int PHONE_NUMBER_FORMAT_17 = 17;
	public static final int PHONE_NUMBER_FORMAT_18 = 18;
	public static final int PHONE_NUMBER_FORMAT_19 = 19;
	public static final int PHONE_NUMBER_FORMAT_20 = 20;
	public static final int PHONE_NUMBER_FORMAT_21 = 21;
	public static final int PHONE_NUMBER_FORMAT_22 = 22;
	public static final int PHONE_NUMBER_FORMAT_23 = 23;
	public static final int PHONE_NUMBER_FORMAT_24 = 24;
	
	//-----TIME FORMAT CONSTANTS-----//
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
	public static final String DISPLAY_NOTIFICATION_BODY_KEY = "display_notification_body";	
	public static final String NOTIFICATION_BODY_CENTER_ALIGN_TEXT_KEY = "center_align_body_text";
	public static final String NOTIFICATION_BODY_FONT_SIZE_KEY = "notification_body_font_size";
	public static final String NOTIFICATION_BODY_FONT_SIZE_DEFAULT = "14";
	public static final String NOTIFICATION_BODY_MAX_LINES_KEY = "notification_body_max_lines";
	public static final String NOTIFICATION_BODY_MAX_LINES_DEFAULT = "5";
	public static final String NOTIFICATION_BODY_BOLD_KEY = "bold_notification_body";
	
	//-----NOTIFICATION TYPE INFO CONSTANTS-----//
	public static final String NOTIFICATION_TYPE_INFO_ICON_DISPLAY_KEY  = "display_notification_type_info_icon";
	public static final String NOTIFICATION_TYPE_INFO_DISPLAY_KEY = "display_notification_type_info";
	public static final String NOTIFICATION_TYPE_INFO_CENTER_ALIGN_KEY = "center_align_notification_type_info";
	public static final String NOTIFICATION_TYPE_INFO_FONT_SIZE_KEY = "notification_type_info_font_size";
	public static final String NOTIFICATION_TYPE_INFO_FONT_SIZE_DEFAULT = "14";
	public static final String NOTIFICATION_TYPE_INFO_BOLD_KEY = "bold_notification_type_info";
	
	//-----QUICK REPLY CONSTANTS-----//
	public static final String SMS_GATEWAY_KEY = "quick_reply_sms_gateway_settings";	
	public static final String SAVE_MESSAGE_DRAFT_KEY = "quick_reply_save_draft_enabled";
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
	public static final int SMS_EMAIL_GATEWAY_9 = 9;
	
	//-----PREFERENCE SCREEN CONSTANTS-----//
	public static final String PREFERENCE_SCREEN_NOTIFICATION_SETTINGS_KEY = "notifications_settings_screen";
	public static final String PREFERENCE_CATEGORY_APP_FEEDBACK_KEY = "app_feedback_category";
	public static final String PREFERENCE_CATEGORY_APP_LICENSE_KEY = "app_license_category";
	public static final String PREFERENCE_RATE_APP_KEY = "rate_app";
	public static final String ADVANCED_PREFERENCE_SCREEN_KEY = "advanced_settings_screen";
	
	//-----NOTIFICATION COUNT CONSTANTS-----//
	public static final String NOTIFICATION_COUNT_ACTION_NOTHING = "0";
	
	//-----SMS CONSTANTS-----//
	public static final String SMS_NOTIFICATIONS_ENABLED_KEY = "sms_notifications_enabled";
	public static final String SMS_BLOCKING_APP_RUNNING_ACTION_KEY = "blocking_app_running_action_sms";
	public static final String SMS_DELETE_KEY = "sms_delete_button_action";
	public static final String SMS_TIMESTAMP_ADJUSTMENT_KEY = "sms_timestamp_adjustment_settings";
	public static final String SMS_DISPLAY_DISMISS_BUTTON_KEY = "sms_display_dismiss_button";
	public static final String SMS_DISPLAY_DELETE_BUTTON_KEY = "sms_display_delete_button";
	public static final String SMS_DISPLAY_REPLY_BUTTON_KEY = "sms_display_reply_button";
	public static final String SMS_NOTIFICATION_COUNT_ACTION_KEY = "sms_notification_count_action";
	public static final String SMS_DISMISS_KEY = "sms_dismiss_button_action";
	public static final String SMS_DISPLAY_UNREAD_KEY = "sms_display_unread_enabled";
	public static final String SMS_CONFIRM_DELETION_KEY = "confirm_sms_deletion";
	
	public static final String SMS_QUICK_REPLY_ENABLED_KEY = "sms_quick_reply_enabled";
	
	public static final String SMS_DISMISS_ACTION_MARK_READ = "0";
	public static final String SMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	public static final String SMS_DELETE_ACTION_DELETE_THREAD = "1";
	public static final String SMS_DELETE_ACTION_NOTHING = "2";
	
	public static final String SMS_MESSAGING_APP_REPLY = "0";
	public static final String SMS_QUICK_REPLY = "1";
	
	public static final String SMS_SPLIT_MESSAGE_KEY = "sms_split_message";
	
	public static final String SMS_TIME_IS_UTC_KEY = "sms_time_is_utc";
	
	//-----MMS CONSTANTS-----//
	public static final String MMS_TIMEOUT_KEY = "mms_timeout_settings";
	public static final String MMS_DISPLAY_UNREAD_KEY = "mms_display_unread_enabled";

	//-----MISSED CALL CONSTANTS-----//
	public static final String PHONE_NOTIFICATIONS_ENABLED_KEY = "missed_call_notifications_enabled";
	public static final String PHONE_BLOCKING_APP_RUNNING_ACTION_KEY = "blocking_app_running_action_missed_call";
	public static final String CALL_LOG_TIMEOUT_KEY = "call_log_timeout_settings";
	public static final String PHONE_DISMISS_BUTTON_ACTION_KEY = "missed_call_loading_settings";
	public static final String PHONE_DISPLAY_DISMISS_BUTTON_KEY = "missed_call_display_dismiss_button";
	public static final String PHONE_DISPLAY_CALL_BUTTON_KEY = "missed_call_display_call_button";
	public static final String PHONE_NOTIFICATION_COUNT_ACTION_KEY = "missed_call_notification_count_action";
	public static final String PHONE_DISMISS_KEY = "missed_call_dismiss_button_action";
	public static final String PHONE_CALL_KEY = "missed_call_call_button_action";
	
	public static final String PHONE_GET_LATEST = "0";
	public static final String PHONE_GET_RECENT = "1";
	public static final String PHONE_GET_ALL = "2";

	public static final String PHONE_DISMISS_ACTION_MARK_READ = "0";
	public static final String PHONE_DISMISS_ACTION_DELETE = "1";
	
	public static final String PHONE_CALL_ACTION_CALL = "0";
	public static final String PHONE_CALL_ACTION_CALL_LOG = "1";
	
	//-----CALENDAR CONSTANTS-----//
	public static final String CALENDAR_NOTIFICATIONS_ENABLED_KEY = "calendar_notifications_enabled";
	public static final String CALENDAR_BLOCKING_APP_RUNNING_ACTION_KEY = "blocking_app_running_action_calendar";
	public static final String CALENDAR_NOTIFY_DAY_OF_TIME_KEY = "calendar_notify_day_of_time";
	public static final String CALENDAR_REMINDERS_ENABLED_KEY = "calendar_reminders_enabled";
	public static final String CALENDAR_USE_CALENDAR_REMINDER_SETTINGS_KEY = "use_current_calendar_reminder_settings";
    public static final String CALENDAR_REMINDER_KEY = "calendar_reminder_settings";
    public static final String CALENDAR_REMINDER_ALL_DAY_KEY = "calendar_reminder_all_day_settings";
    public static final String CALENDAR_POLLING_FREQUENCY_KEY = "calendar_polling_frequency"; 
    public static final String CALENDAR_POLLING_FREQUENCY_DEFAULT = "15";
	public static final String CALENDAR_DISPLAY_DISMISS_BUTTON_KEY = "calendar_display_dismiss_button";
	public static final String CALENDAR_DISPLAY_VIEW_BUTTON_KEY = "calendar_display_view_button";
	public static final String CALENDAR_DISPLAY_SNOOZE_BUTTON_KEY = "calendar_display_snooze_button";
	public static final String CALENDAR_NOTIFICATION_COUNT_ACTION_KEY = "calendar_notification_count_action";
	public static final String CALENDAR_LABELS_KEY = "calendar_labels_enabled";
	public static final String CALENDAR_SELECTION_KEY = "calendar_selection";
	public static final String CALENDAR_REFRESH_KEY = "calendar_refresh";
	public static final String CALENDAR_EVENT_TIME_REMINDER_KEY = "calendar_event_time_reminder_enabled";
	public static final String CALENDAR_REMINDER_FREQUENCY_KEY = "calendar_reminder_frequency";
	public static final String CALENDAR_REMINDER_FREQUENCY_DEFAULT = "-1";
	public static final String CALENDAR_REMINDER_INTERVAL_KEY = "calendar_reminder_interval";
	public static final String CALENDAR_REMINDER_INTERVAL_DEFAULT = "10";
	
	public static final String CALENDAR_ID = "_id";
	public static final String CALENDAR_CALENDAR_ID = "calendar_id";
	public static final String CALENDAR_EVENT_ID = "_id"; 
	public static final String CALENDAR_REMINDER_EVENT_ID = "event_id"; 
	public static final String CALENDAR_EVENT_TITLE = "title"; 
    public static final String CALENDAR_INSTANCE_BEGIN = "dtstart"; 
    public static final String CALENDAR_INSTANCE_END = "dtend"; 
    public static final String CALENDAR_EVENT_ALL_DAY = "allDay"; 
    public static final String CALENDAR_DISPLAY_NAME = "displayName"; 
    public static final String CALENDAR_SELECTED = "selected";
    public static final String CALENDAR_EVENT_BEGIN_TIME = "beginTime";
    public static final String CALENDAR_EVENT_END_TIME = "endTime";
    public static final String CALENDAR_EVENT_HAS_ALARM = "hasAlarm";
    public static final String CALENDAR_REMINDER_MINUTES = "minutes";  
	public static final String CALENDAR_ALERT_EVENT_ID = "event_id";
	public static final String CALENDAR_ALERT_EVENT_STATUS = "eventStatus";
	public static final String CALENDAR_DISMISS_KEY = "calendar_dismiss_button_action";
	public static final String CALENDAR_DISMISS_ACTION_MARK_DISMISSED = "1";
	
	//-----K9 CONSTANTS-----//	
	public static final String INTENT_ACTION_K9 = "com.fsck.k9.intent.action.EMAIL_RECEIVED";
	public static final String INTENT_ACTION_KAITEN = "com.kaitenmail.intent.action.EMAIL_RECEIVED";
	public static final String INTENT_ACTION_K9_FOR_PURE = "org.koxx.k9ForPureWidget.intent.action.EMAIL_RECEIVED";
	
	public static final String K9_NOTIFICATIONS_ENABLED_KEY = "k9_notifications_enabled";
	public static final String K9_BLOCKING_APP_RUNNING_ACTION_KEY = "blocking_app_running_action_k9";
	public static final String K9_DELETE_KEY = "k9_delete_button_action";
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
	
	public static final String K9_INCLUDE_ACCOUNT_NAME_KEY = "k9_include_account_name";
	
	public static final String K9_TIMEOUT_KEY = "k9_timeout";
	
	//-----GENERIC NOTIFICATION CONSTANTS-----//	
	public static final String GENERIC_NOTIFICATIONS_ENABLED_KEY = "generic_notifications_enabled";
	
	//-----APPLICATION URL CONSTANTS-----//
	//Android Market URL
	public static final String APP_ANDROID_URL = "https://play.google.com/store/apps/details?id=apps.droidnotify";
	public static final String APP_PRO_ANDROID_URL = "https://play.google.com/store/apps/details?id=apps.droidnotifydonate";
	public static final String K9_MAIL_ANDROID_URL = "https://play.google.com/store/apps/details?id=com.fsck.k9";
	public static final String KAITEN_MAIL_ANDROID_URL = "https://play.google.com/store/apps/details?id=com.kaitenmail";
	public static final String APP_SEARCH_ANDROID_URL = "https://play.google.com/store/search?q=apps.droidnotify.theme";
	public static final String APP_ANDROID_PLUS_URL = "https://play.google.com/store/apps/details?id=apps.droidnotifyplus";
	public static final String APP_ANDROID_MISSED_CALL_MESSENGER_LITE_URL = "https://play.google.com/store/apps/details?id=com.missedcallmessenger.lite";
	public static final String APP_ANDROID_CALENDAR_MESSENGER_LITE_URL = "https://play.google.com/store/apps/details?id=com.calendarmessenger.lite";
	public static final String APP_ANDROID_WEB_URL = "http://play.google.com/store/apps/";	
	
	//Amazon Appstore URL
	public static final String APP_AMAZON_URL = "http://www.amazon.com/gp/mas/dl/android?p=apps.droidnotify";
	public static final String APP_PRO_AMAZON_URL = "http://www.amazon.com/gp/mas/dl/android?p=apps.droidnotifydonate";
	public static final String K9_MAIL_AMAZON_URL = "http://www.amazon.com/gp/mas/dl/android?p=com.fsck.k9";
	public static final String KAITEN_MAIL_AMAZON_URL = "http://www.amazon.com/gp/mas/dl/android?p=com.kaitenmail";
	public static final String APP_SEARCH_AMAZON_URL = "http://www.amazon.com/gp/mas/dl/android?p=apps.droidnotify.theme&showAll=1";
	public static final String APP_AMAZON_PLUS_URL = "http://www.amazon.com/gp/mas/dl/android?p=apps.droidnotifyplus";
	public static final String APP_AMAZON_MISSED_CALL_MESSENGER_LITE_URL = "http://www.amazon.com/gp/mas/dl/android?p=com.missedcallmessenger.lite";
	public static final String APP_AMAZON_CALENDAR_MESSENGER_LITE_URL = "http://www.amazon.com/gp/mas/dl/android?p=com.calendarmessenger.lite";
	public static final String APP_AMAZON_WEB_URL = "http://www.amazon.com/appstore/";
	
	//Samsung Apps URL
	public static final String APP_SAMSUNG_URL = "samsungapps://ProductDetail/apps.droidnotify";
	public static final String APP_PRO_SAMSUNG_URL = "samsungapps://ProductDetail/apps.droidnotifydonate";
	public static final String K9_MAIL_SAMSUNG_URL = "samsungapps://ProductDetail/com.fsck.k9";
	public static final String KAITEN_MAIL_SAMSUNG_URL = "samsungapps://ProductDetail/com.kaitenmail";
	public static final String APP_SEARCH_SAMSUNG_URL = "http://www.samsungapps.com/topApps/topAppsList.as?mkt_keyword=Droid+Notify";
	public static final String APP_SAMSUNG_PLUS_URL = "samsungapps://ProductDetail/apps.droidnotifyplus";
	public static final String APP_SAMSUNG_MISSED_CALL_MESSENGER_LITE_URL = "samsungapps://ProductDetail/com.missedcallmessenger.lite";
	public static final String APP_SAMSUNG_CALENDAR_MESSENGER_LITE_URL = "samsungapps://ProductDetail/com.calendarmessenger.lite";
	public static final String APP_SAMSUNG_WEB_URL = "http://www.samsungapps.com/";
	
	//SlideMe Apps URL
	public static final String APP_SLIDEME_URL = "sam://details?id=apps.droidnotify";
	public static final String APP_PRO_SLIDEME_URL = "sam://details?id=apps.droidnotifydonate";
	public static final String K9_MAIL_SLIDEME_URL = "sam://details?id=com.fsck.k9";
	public static final String KAITEN_MAIL_SLIDEME_URL = "sam://details?id=com.kaitenmail";
	public static final String APP_SEARCH_SLIDEME_URL = "http://slideme.org/applications?text=Droid+Notify";
	public static final String APP_SLIDEME_PLUS_URL = "sam://details?id=apps.droidnotifyplus";
	public static final String APP_SLIDEME_MISSED_CALL_MESSENGER_LITE_URL = "sam://details?id=com.missedcallmessenger.lite";
	public static final String APP_SLIDEME_CALENDAR_MESSENGER_LITE_URL = "sam://details?id=com.calendarmessenger.lite";
	public static final String APP_SLIDEME_WEB_URL = "http://slideme.org/applications/";
	
	//Google Search URL
	public static final String APP_GOOGLE_URL = "http://google.com/search?q=Android+Droid+Notify+Lite";
	public static final String APP_PRO_GOOGLE_URL = "http://google.com/search?q=Android+Droid+Notify+Pro";
	public static final String K9_MAIL_GOOGLE_URL = "http://google.com/search?q=Android+K-9+Mail";
	public static final String KAITEN_MAIL_GOOGLE_URL = "http://google.com/search?q=Android+Kaiten+Mail";
	public static final String APP_SEARCH_GOOGLE_URL = "http://google.com/search?q=Android+Droid+Notify+Theme";
	public static final String APP_GOOGLE_PLUS_URL = "http://google.com/search?q=Android+Droid+Notify+Plus";
	public static final String APP_GOOGLE_MISSED_CALL_MESSENGER_LITE_URL = "http://google.com/search?q=Android+Missed+Call+Messenger+Lite";
	public static final String APP_GOOGLE_CALENDAR_MESSENGER_LITE_URL = "http://google.com/search?q=Android+Calendar+Messenger+Lite";
	
	//PayPal URL
	public static final String APP_PAYPAL_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=XV4WLUKUBTMV6";
	
	//-----STATUS BAR NOTIFICATION CONSTANTS-----//
	public static final String STATUS_BAR_NOTIFICATIONS_RINGTONE_DEFAULT = "content://settings/system/notification_sound";
	public static final String STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_DEFAULT = "0,1200";
	public static final String STATUS_BAR_NOTIFICATIONS_LED_PATTERN_DEFAULT = "1000,1000";
	public static final String STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT = "red";
	
	public static final String STATUS_BAR_NOTIFICATIONS_RINGTONE_SILENT_VALUE = "";
	public static final String STATUS_BAR_NOTIFICATIONS_VIBRATE_ALWAYS_VALUE = "0";
	public static final String STATUS_BAR_NOTIFICATIONS_VIBRATE_NEVER_VALUE = "1";
	public static final String STATUS_BAR_NOTIFICATIONS_VIBRATE_WHEN_VIBRATE_MODE_VALUE = "2";
	public static final String STATUS_BAR_NOTIFICATIONS_VIBRATE_DEFAULT = STATUS_BAR_NOTIFICATIONS_VIBRATE_ALWAYS_VALUE;
	
	//-----SMS STATUS BAR NOTIFICATION CONSTANTS-----//
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_CUSTOM_VALUE = "sms_custom";
	
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY = "sms_status_bar_notifications_enabled";
	
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY = "sms_status_bar_notifications_show_when_blocked_enabled";
	
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY = "sms_notification_sound";
	
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY = "sms_notification_vibrate_setting";
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY = "sms_notification_vibrate_pattern";
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY = "sms_notification_vibrate_pattern_custom";
	
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY = "sms_notification_led_enabled";
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY = "sms_notification_led_pattern";
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY = "sms_notification_led_pattern_custom";
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY = "sms_notification_led_color";
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY = "sms_notification_led_color_custom";
	
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY = "sms_notification_in_call_sound_enabled";
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY = "sms_notification_in_call_vibrate_enabled";
	
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY = "notification_icon_sms";
	public static final String SMS_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT = "status_bar_notification_sms_green";
	
	//-----PHONE STATUS BAR NOTIFICATION CONSTANTS-----//
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_CUSTOM_VALUE = "phone_custom";
	
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY = "missed_call_status_bar_notifications_enabled";
	
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY = "missed_call_status_bar_notifications_show_when_blocked_enabled";
	
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY = "missed_call_notification_sound";
	
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY = "missed_call_notification_vibrate_setting";
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY = "missed_call_notification_vibrate_pattern";
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY = "missed_call_notification_vibrate_pattern_custom";
	
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY = "missed_call_notification_led_enabled";
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY = "missed_call_notification_led_pattern";
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY = "missed_call_notification_led_pattern_custom";
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY = "missed_call_notification_led_color";
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY = "missed_call_notification_led_color_custom";
	
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY = "missed_call_notification_in_call_sound_enabled";
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY = "missed_call_notification_in_call_vibrate_enabled";
	
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY = "notification_icon_missed_call";
	public static final String PHONE_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT = "status_bar_notification_missed_call_black";
	
	//-----CALENDAR STATUS BAR NOTIFICATION CONSTANTS-----//
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_CUSTOM_VALUE = "calendar_custom";
	
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY = "calendar_status_bar_notifications_enabled";
	
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY = "calendar_status_bar_notifications_show_when_blocked_enabled";
	
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY = "calendar_notification_sound";
	
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY = "calendar_notification_vibrate_setting";
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY = "calendar_notification_vibrate_pattern";
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY = "calendar_notification_vibrate_pattern_custom";
	
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY = "calendar_notification_led_enabled";
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY = "calendar_notification_led_pattern";
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY = "calendar_notification_led_pattern_custom";
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY = "calendar_notification_led_color";
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY = "calendar_notification_led_color_custom";
	
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY = "calendar_notification_in_call_sound_enabled";
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY = "calendar_notification_in_call_vibrate_enabled";	
	
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY = "notification_icon_calendar";
	public static final String CALENDAR_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT = "status_bar_notification_calendar_blue";
	
	//-----K9 STATUS BAR NOTIFICATION CONSTANTS-----//
	public static final String K9_STATUS_BAR_NOTIFICATIONS_CUSTOM_VALUE = "k9_custom";
	
	public static final String K9_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY = "k9_status_bar_notifications_enabled";
	
	public static final String K9_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY = "k9_status_bar_notifications_show_when_blocked_enabled";

	public static final String K9_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY = "k9_notification_sound";
	
	public static final String K9_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY = "k9_notification_vibrate_setting";
	public static final String K9_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY = "k9_notification_vibrate_pattern";
	public static final String K9_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY = "k9_notification_vibrate_pattern_custom";
	
	public static final String K9_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY = "k9_notification_led_enabled";
	public static final String K9_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY = "k9_notification_led_pattern";
	public static final String K9_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY = "k9_notification_led_pattern_custom";
	public static final String K9_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY = "k9_notification_led_color";
	public static final String K9_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY = "k9_notification_led_color_custom";
	
	public static final String K9_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY = "k9_notification_in_call_sound_enabled";
	public static final String K9_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY = "k9_notification_in_call_vibrate_enabled";	
	
	public static final String K9_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY = "notification_icon_k9";
	public static final String K9_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT = "status_bar_notification_email_white";
	
	//-----TEXT-TO-SPEECH CONSTANTS-----//
	public static final String DISPLAY_TEXT_TO_SPEECH_KEY = "display_text_to_speech_button";
	public static final int TEXT_TO_SPEECH_ACTIVITY = 500;
	
	//-----APPLICATION PUSHED TO BACKGROUND CONSTANTS-----//
	public static final String APPLICATION_CLOSE_WHEN_PUSHED_TO_BACKGROUND_KEY = "close_app_when_pushed_to_background";
	public static final String IGNORE_LINKED_APPS_WHEN_PUSHED_TO_BACKGROUND_KEY = "ignore_linked_apps_when_pushed_to_background";
	
	//-----IN-CALL SETTINGS CONSTANTS-----//
	public static final String IN_CALL_RESCHEDULING_ENABLED_KEY = "in_call_rescheduling_enabled";
	
	//-----POPUP FORMATTING CONSTANTS-----//
	public static final String POPUP_VERTICAL_LOCATION_KEY = "popup_vertical_location";
	public static final String POPUP_VERTICAL_LOCATION_TOP = "0";
	public static final String POPUP_VERTICAL_LOCATION_CENTER = "1";
	public static final String POPUP_VERTICAL_LOCATION_BOTTOM = "2";
	public static final String POPUP_VERTICAL_LOCATION_DEFAULT = POPUP_VERTICAL_LOCATION_CENTER;
	public static final String POPUP_WINDOW_WIDTH_PADDING_KEY = "popup_width_padding";
	public static final String POPUP_WINDOW_WIDTH_PADDING_DEFAULT = "0";
	
	//-----FLASH SMS CONSTANTS-----//
	public static final String SMS_IGNORE_CLASS_0_MESSAGES_KEY = "sms_ignore_class_0_messages";	
	
	//-----THEME CONSTANTS-----//
	public static final int THEME_BUTTON_NORMAL = 0;
	public static final int THEME_BUTTON_NAV_PREV = 1;
	public static final int THEME_BUTTON_NAV_NEXT = 2;
	public static final int THEME_SPINNER = 3;
	public static final int THEME_EDIT_TEXT =4;
	
	//-----CUSTOM STATUS BAR NOTIFICATION CONSTANTS-----//
	public static final String CUSTOM_STATUS_BAR_NOTIFICATIONS_CUSTOM_VALUE = "custom";
	
	public static final String CUSTOM_STATUS_BAR_NOTIFICATIONS_RINGTONE_KEY = "custom_ringtone";
	
	public static final String CUSTOM_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY = "custom_vibrate_setting";
	public static final String CUSTOM_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY = "custom_vibrate_pattern";
	public static final String CUSTOM_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY = "custom_vibrate_pattern_custom";
	
	public static final String CUSTOM_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY = "custom_led_enabled";
	public static final String CUSTOM_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY = "custom_led_pattern";
	public static final String CUSTOM_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY = "custom_led_pattern_custom";
	public static final String CUSTOM_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY = "custom_led_color";
	public static final String CUSTOM_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY = "custom_led_color_custom";
	
	public static final String CUSTOM_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY = "custom_in_call_sound_enabled";
	public static final String CUSTOM_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY = "custom_in_call_vibrate_enabled";

	//-----PRIVACY CONSTANTS-----//
	public static final String STATUS_BAR_NOTIFICATION_PRIVACY_ENABLED_KEY = "status_bar_notification_privacy_enabled";
	public static final String SMS_MESSAGE_PRIVACY_ENABLED_KEY = "sms_message_privacy_enabled";
	public static final String EMAIL_MESSAGE_PRIVACY_ENABLED_KEY = "email_message_privacy_enabled";
	public static final String MISSED_CALL_PRIVACY_ENABLED_KEY = "missed_call_privacy_enabled";

	//-----RESTRICT POPUP CONSTANTS-----//
	public static final String RESTRICT_POPUP_KEY = "restrict_popup";
	
	//-----RESET PREFERENCES CONSTANTS-----//
	public static final String RESET_APP_PREFERENCES_KEY = "reset_app_preferences";	
	
	//-----MORE OPTIONS CONSTANTS-----//
	public static final String MORE_OPTIONS_KEY = "more_options_preference";
	
	//-----EMOJI CONSTANTS-----//
	public static final String EMOTICONS_ENABLED = "emoticons_enabled";
	
	//-----DROID NOTIFY API CONSTANTS-----//
//	public static final String DROID_NOTIFY_API_NOTIFICATION_ACTION = "apps.droidnotify.api.NOTIFICATION_RECEIVED";
//	public static final String DROID_NOTIFY_API_PACKAGE = "package";
//	public static final String DROID_NOTIFY_API_TIMESTAMP = "timeStamp";
//	public static final String DROID_NOTIFY_API_DISPLAY_TEXT = "displayText";
//	public static final String DROID_NOTIFY_API_DISMISS_PENDINGINTENT = "dismissPendingIntent";
//	public static final String DROID_NOTIFY_API_DELETE_PENDINGINTENT = "deletePendingIntent";
//	public static final String DROID_NOTIFY_API_VIEW_PENDINGINTENT = "viewPendingIntent";	
//	public static final String DROID_NOTIFY_API_CONTACT_ID  = "contactID";
//	public static final String DROID_NOTIFY_API_CONTACT_NAME  = "contactName";
//	public static final String DROID_NOTIFY_API_SENT_FROM_ADDRESS = "sentFromAddress";	
//	public static final String DROID_NOTIFY_API_ENABLE_STATUS_BAR_NOTIFICATION = "enableStatusBarNotification";
//	public static final String DROID_NOTIFY_API_STATUS_BAR_NOTIFICATION_SOUND_URI = "statusBarNotificationSoundURI";
//	public static final String DROID_NOTIFY_API_STATUS_BAR_NOTIFICATIONS_VIBRATE_ALWAYS_VALUE = "0";
//	public static final String DROID_NOTIFY_API_STATUS_BAR_NOTIFICATIONS_VIBRATE_NEVER_VALUE = "1";
//	public static final String DROID_NOTIFY_API_STATUS_BAR_NOTIFICATIONS_VIBRATE_WHEN_VIBRATE_MODE_VALUE = "2";
//	public static final String DROID_NOTIFY_API_STATUS_BAR_NOTIFICATION_VIBRATE_SETTING = "statusBarNotificationVibrateSetting";
//	public static final String DROID_NOTIFY_API_STATUS_BAR_NOTIFICATION_VIBRATE_PATTERN = "statusBarNotificationVibratePattern";
//	//public static final String DROID_NOTIFY_API_STATUS_BAR_NOTIFICATION_LED_ENABLED = "statusBarNotificationLEDEnabled";
//	//public static final String DROID_NOTIFY_API_STATUS_BAR_NOTIFICATION_LED_PATTERN = "statusBarNotificationLEDPattern";
//	//public static final String DROID_NOTIFY_API_STATUS_BAR_NOTIFICATION_LED_COLOR = "statusBarNotificationLEDColor";
//	public static final String DROID_NOTIFY_API_STATUS_BAR_NOTIFICATION_IN_CALL_SOUND_ENABLED = "statusBarNotificationInCallSoundEnabled";
//	public static final String DROID_NOTIFY_API_STATUS_BAR_NOTIFICATION_IN_CALL_VIBRATE_ENABLED = "statusBarNotificationInCallVibrateEnabled";
	
	//-----LONG PRESS DISMISS BUTTON CONSTANTS-----//
	public static final String DISMISS_BUTTON_LONG_PRESS_ACTION_KEY = "long_press_dismiss_button_action";
	public static final String DISMISS_BUTTON_LONG_PRESS_ACTION_DISMISS_ALL = "0";
	public static final String DISMISS_BUTTON_LONG_PRESS_ACTION_LAUNCH_OPTIONS_MENU = "1";
	
	//-----PACKAGE LOADING CONSTANTS-----//
	//Array of packages to ignore when loading.
	public static final String[] IGNORE_PACKAGES_ARRAY = new String[]{
		"android.tts",
		"jp.co.omronsoft.openwnn",
		"com.svox.pico",
		"com.android.certinstaller",
		"com.google.android.apps.uploader",
		"com.google.android.gsf",
		"com.google.android.location",
		"com.google.android.partnersetup",
		"com.android.backupconfirm", 
		"com.android.defcontainer",
		"com.android.emulator.connectivity.test",
		"com.android.emulator.gps.test", 
		"com.android.development", 
		"com.android.gesture.builder", 
		"com.android.DunServer",  
		"com.android.htmlviewer", 
		"com.android.inputmethod.latin", 
		"com.android.LGSetupWizard",
		"com.android.packageinstaller",
		"com.android.providers.applications", 
		"com.android.providers.downloads",   
		"com.android.providers.drm", 
		"com.android.providers.media", 
		"com.android.providers.userdictionary", 
		"com.android.sdksetup",
		"com.android.server.vpn",
		"com.android.setupwizard", 
		"com.android.sharedstoragebackup", 
		"com.android.vending.updater", 
		"com.android.voicedialer", 
		"com.google.android.feedback",
		"com.lge.hiddenmenu",
		"com.lge.internal",
		"com.lge.LgHiddenMenu",  
		"com.lge.SprintHiddenMenu",
		"com.lge.uts",
		"com.adobe.flashplayer",
		"com.android.CSDFunctionG",
		"com.android.backupconfirm",
		"com.android.certinstaller",
		"com.android.defcontainer",
		"com.android.dmportread",
		"com.android.providers.htcCheckin",
		"com.android.providers.htcmessage",
		"com.android.sharedstoragebackup",
		"com.android.smith",
		"com.android.updater",
		"com.android.vpndialogs",
		"com.android.MtpApplication",
		"com.android.Preconfig",
		"com.android.keychain",
		"com.android.pickuptutorial",
		"com.android.server.device.enterprise",
		"com.android.server.vpn.enterprise",
		"com.android.smspush",
		"com.broadcom.bt.app.system",
		"com.cellmania.android.storefront.webview.vmu",
		"com.fd.httpd.service.wifi.detect",
		"com.google.android.backup",
		"com.google.android.onetimeinitializer",
		"com.google.android.setupwizard",
		"com.google.android.tts",
		"com.htc",
		"com.htc.AutoMotive.Traffic",
		"com.htc.CustomizationSetup",
		"com.htc.MediaAutoUploadSetting",
		"com.htc.MediaCacheService",
		"com.htc.android.epst",
		"com.htc.android.fieldtrial",
		"com.htc.android.inputset.cxt9ldb",
		"com.htc.android.inputset.pphwr",
		"com.htc.android.inputset.xt9bul",
		"com.htc.android.inputset.xt9cze",
		"com.htc.android.inputset.xt9dan",
		"com.htc.android.inputset.xt9dut",
		"com.htc.android.inputset.xt9eng",
		"com.htc.android.inputset.xt9est",
		"com.htc.android.inputset.xt9fin",
		"com.htc.android.inputset.xt9fre",
		"com.htc.android.inputset.xt9ger",
		"com.htc.android.inputset.xt9gre",
		"com.htc.android.inputset.xt9hrv",
		"com.htc.android.inputset.xt9hun",
		"com.htc.android.inputset.xt9ita",
		"com.htc.android.inputset.xt9lav",
		"com.htc.android.inputset.xt9lit",
		"com.htc.android.inputset.xt9nor",
		"com.htc.android.inputset.xt9pol",
		"com.htc.android.inputset.xt9por",
		"com.htc.android.inputset.xt9rom",
		"com.htc.android.inputset.xt9rus",
		"com.htc.android.inputset.xt9slo",
		"com.htc.android.inputset.xt9slv",
		"com.htc.android.inputset.xt9spa",
		"com.htc.android.inputset.xt9srp",
		"com.htc.android.inputset.xt9swe",
		"com.htc.android.inputset.xt9tur",
		"com.htc.csengine",
		"com.htc.demoflopackageinstaller",
		"com.htc.dlnamiddlelayer",
		"com.htc.dropbox.glrplugin",
		"com.htc.dummyskin",
		"com.htc.framework",
		"com.htc.fusion.FusionApk",
		"com.htc.htcCOTAClient",
		"com.htc.htcsprintservice",
		"com.htc.providers.settings",
		"com.htc.resetnotify",
		"com.htc.task.gtask",
		"com.google.android.gsf",
		"com.google.android.gsf.login",
		"com.infraware.filemanager.webstorage.boxnet",
		"com.infraware.filemanager.webstorage.dropbox",
		"com.infraware.filemanager.webstorage.google",
		"com.monotype.android.font.chococooky",
		"com.monotype.android.font.helvneuelt",
		"com.monotype.android.font.rosemary",
		"com.samsung.app.playreadyui",
		"com.sec.android.app.DataCreate",
		"com.sec.android.app.FileShareServer",
		"com.sec.android.app.SecSetupWizard",
		"com.sec.android.app.bluetoothtest",
		"com.sec.android.app.factorymode",
		"com.sec.android.app.kieswifi",
		"com.sec.android.app.lcdtest",
		"com.sec.android.app.minimode.res",
		"com.sec.android.app.personalization",
		"com.sec.android.app.phoneutil",
		"com.sec.android.app.popupuireceiver",
		"com.sec.android.app.samsungapps.una2",
		"com.sec.android.app.shareapp",
		"com.sec.android.app.wallpaperchooser",
		"com.sec.android.app.wlantest",
		"com.sec.android.drmpopup",
		"com.sec.android.dttsupport",
		"com.sec.android.providers.downloads",
		"com.sec.factory",
		"com.sec.minimode.taskcloser"};
	
}