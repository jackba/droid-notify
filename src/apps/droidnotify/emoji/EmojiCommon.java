package apps.droidnotify.emoji;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;


import apps.droidnotify.MyApplication;
import apps.droidnotify.R;
import apps.droidnotify.log.Log;

public class EmojiCommon {
	
	//================================================================================
    // Constants
    //================================================================================

	public static final int[] _emojiIcons = new int[]{ 
			R.drawable.yo_angel,
			R.drawable.yo_happy,
			R.drawable.yo_sad,
			R.drawable.yo_winking,
			R.drawable.yo_tongue,
			R.drawable.yo_surprised,
			R.drawable.yo_kiss,
			R.drawable.yo_yelling,
			R.drawable.yo_cool,
			R.drawable.yo_money,
			R.drawable.yo_foot_in_mouth,
			R.drawable.yo_embarrassed,
			R.drawable.yo_undecided,
			R.drawable.yo_crying,
			R.drawable.yo_lips,
			R.drawable.yo_laughing,
			R.drawable.yo_wtf,
			R.drawable.yo_happy,
			R.drawable.yo_sad,
			R.drawable.yo_winking,
			R.drawable.yo_tongue			 
		};
	 
	public static final String[] _emojiLabels = new String[]{ 
			"Angel",
			"Happy",
			"Sad",
	        "Winking",
	        "Tongue Sticking Out",
	        "Surprised",
	        "Kiss",
	        "Yelling",
	        "Cool",
	        "Money",
	        "Foot In Mouth",
	        "Embarrased",
	        "Undecided",
	        "Crying",
	        "Lips Are Sealed",
	        "Laughing",
	        "Confused",
			"Happy",
			"Sad",
	        "Winking",
	        "Tongue Sticking Out"            
		};
	
	public static final String[] _emojis = new String[]{ 
			"O:-)",
			":-)",
			":-(",
			";-)",
			":-P",
	        "=-O",
	        ":-*",
	        ":-O",
	        "B-)",
	        ":-$",
	        ":-!",
	        ":-[",
	        ":-\\",
	        ":'(",
	        ":-X",
	        ":-D",
	        "o_O",           
			":)",
			":(",
			";)",
			":P"
		};
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Convert plain text into text that supports Emoji retrieval. 
	 * This method takes the plain text and replaces all defined instances of Emoji 
	 * characters and with an <img> tag for use with the accompanying ImageGetter.
	 * 
	 * @param context
	 * @param input
	 * 
	 * @return
	 */
	public static String convertTextToEmoji(Context context,String input){
		String output = input;
		int size = _emojis.length;
		for(int i=0;i<size;i++){
			output = output.replace(_emojis[i],"<img src=\"" + String.valueOf(i) + ".png\" />");
		}
		return output;
	}
	
	/**
	 * Android ImageGetter used to retrieve the Emoji Icon.
	 */
	public static ImageGetter emojiGetter = new ImageGetter(){
        public Drawable getDrawable(String index){
        	try{
	        	Resources resources = MyApplication.getContext().getResources();
	        	Drawable drawable = resources.getDrawable(_emojiIcons[Integer.valueOf(index.replace(".png", ""))]);
	        	//This MUST be done otherwise the image will not be displayed!
	        	drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
	        	return drawable;  
        	}catch(Exception ex){
        		Log.e("EmojiCommon.ImageGetter.getDrawable() ERROR: " + ex.toString());
	            return null;
        	}
        }
    };	

}
