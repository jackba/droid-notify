package apps.droidnotify.preferences.theme;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ViewFlipper;

/**
 * This class is the main control window that displays and moves the Notifications.
 * 
 * @author Camille Sévigny
 */
public class ThemeViewFlipper extends ViewFlipper {

	//================================================================================
	// Constructors
	//================================================================================
	  
	/**
	 * Class Constructor.
	 */
	public ThemeViewFlipper(Context context){
		super(context);
	}
	
	/**
	 * Class Constructor.
	 */	
	public  ThemeViewFlipper(Context context, AttributeSet attributes){
		super(context, attributes);
	}
	  
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Add new View to the ViewFlipper.
	 * 
	 * @param view - The theme we are adding to the ViewFlipper.
	 */
	public void addTheme(ThemeView view){
		addView(view);
	}
	
	/**
	 * Return the currently displayed View's package name.
	 * 
	 * @return String - The package name of the currently displayed View.
	 */
	public String getThemePackage(){
		try{
			return this.getChildCount() > 0 ? ((ThemeView) this.getCurrentView()).getThemePackage() : null;
		}catch(Exception ex){
			return null;
		}
	}
	
	/**
	 * Set the displayed ViewFlipper theme.
	 */
	public void setDisplayedTheme(String packageName){
		int totalThemes = this.getChildCount();
		for(int i = 0; i<totalThemes; i++){
			String themePackage = ((ThemeView) this.getChildAt(i)).getThemePackage();
			if(themePackage.equals(packageName)){
				setDisplayedChild(i);
			}
		}
	}
	
	/**
	 * Show the next View in the list.
	 */
	@Override
	public void showNext(){
		if(this.getDisplayedChild() < this.getChildCount() - 1){
			setInAnimation(inFromRightAnimation());
			setOutAnimation(outToLeftAnimation());
			//Flip to next View.
			super.showNext();
		}
	}
	  
	/**
	 * Show the previous View in the list.
	 */
	@Override
	public void showPrevious(){
		if(this.getDisplayedChild() > 0){
			setInAnimation(inFromLeftAnimation());
			setOutAnimation(outToRightAnimation());
			//Flip to previous View.
			super.showPrevious();
		}
	}

	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Animation of the moving of the a Notification that comes from the right.
	 * 
	 * @return Animation - Returns the Animation object.
	 */
	private Animation inFromRightAnimation(){
		Animation inFromRight = new TranslateAnimation(
		Animation.RELATIVE_TO_PARENT, +1.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(350);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}
	  
	/**
	 * Animation of the moving of the a Notification that leaves to the left.
	 * 
	 * @return Animation - Returns the Animation object.
	 */
	private Animation outToLeftAnimation(){
		Animation outtoLeft = new TranslateAnimation(
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, -1.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(350);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}
	  
	/**
	 * Animation of the moving of the a Notification that comes from the left.
	 * 
	 * @return Animation - Returns the Animation object.
	 */
	private Animation inFromLeftAnimation(){
		Animation inFromLeft = new TranslateAnimation(
		Animation.RELATIVE_TO_PARENT, -1.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(350);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}
	  
	/**
	 * Animation of the moving of the a Notification that leaves to the right.
	 * 
	 * @return Animation - Returns the Animation object.
	 */
	private Animation outToRightAnimation(){
		Animation outtoRight = new TranslateAnimation(
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, +1.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f,
		Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoRight.setDuration(350);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}
	
}