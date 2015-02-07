package thahn.java.agui.app;

import java.awt.Point;

import thahn.java.agui.Global;
import thahn.java.agui.exception.WrongFormatException;
import thahn.java.agui.view.Gravity;


public class WindowPosition {
	public static final int											HORIZONTAL_MASK		= 0x0f;
	public static final int											VERTICAL_MASK 		= 0xf0;
	public static final int											IN_OUT_MASK 		= 0xf00;
	
	public static final int											CENTER 				= 0x01;
	public static final int											LEFT				= 0x02;
	public static final int											RIGHT				= 0x04;
	public static final int											TOP					= 0x10;
	public static final int											MID					= 0x20;
	public static final int											BOTTOM				= 0x40;
	public static final int											IN					= 0x100;
	public static final int											OUT					= 0x200;
	
	public static Point getWindowPosition(int windowWidth, int windowHeight, Intent intent) {
		int x = 0;
		int y = 0;
		
    	int screen = intent.getIntExtra(Intent.EXTRA_POSITION_RELATIVE_SCREEN, -1);
    	if(screen != -1) { 
	    	switch (screen & HORIZONTAL_MASK) {
			case CENTER:
				x = (Global.screenWidth - windowWidth)/2;
				break;
			case LEFT:
				x = 0;
				break;
			case RIGHT:
				x = (Global.screenWidth - windowWidth);
				break;
			}
	    	switch (screen & VERTICAL_MASK) {
	    	case TOP:
	    		y = 0;
				break;
			case MID:
				y = (Global.screenHeight - windowHeight)/2;
				break;
			case BOTTOM:
				y = (Global.screenHeight - windowHeight);
				break;
			}
    	}
    	
    	int windowHorizontal = intent.getIntExtra(Intent.EXTRA_POSITION_RELATIVE_WINDOW_HORIZONTAL, -1);
    	int windowVertical = intent.getIntExtra(Intent.EXTRA_POSITION_RELATIVE_WINDOW_VERTICAL, -1);
    	if(windowHorizontal != -1) {
    		Object obj = intent.getObjectExtra(Intent.EXTRA_POSITION_RELATIVE_WINDOW_INSTANCE, -1);
    		if(!(obj instanceof Activity)) {
    			throw new WrongFormatException(Intent.EXTRA_POSITION_RELATIVE_WINDOW_INSTANCE+" : must contain Activity Instance");
    		}
    		Activity activity = (Activity) obj;
    		x = getWindowHorizontal(windowHorizontal, activity, windowWidth, windowHeight);
    		y = getWindowVertical(windowVertical, activity, windowWidth, windowHeight);
    	}
    	
    	int tempX = intent.getIntExtra(Intent.EXTRA_POSITION_X, -1);
    	int tempY = intent.getIntExtra(Intent.EXTRA_POSITION_Y, -1);
    	if((tempX != -1) && (tempY != -1)) {
    		x = tempX;
    		y = tempY;
    	}
    	return new Point(x, y);
    }
	
	private static int getWindowHorizontal(int window, Activity activity, int windowWidth, int windowHeight) {
		Window relativeWindow = activity.getParent();
		
		int x = 0;
		int startX = 0;
		int gravityWidth = 0;
		if((window & IN_OUT_MASK) == IN) {
			startX = relativeWindow.getX();
			gravityWidth = relativeWindow.getWidth();
		} else {//if((window & IN_OUT_MASK) == OUT) {
			startX = relativeWindow.getX() - windowWidth;
			gravityWidth = relativeWindow.getWidth() + windowWidth*2;
		}
		switch (window & HORIZONTAL_MASK) {
		case CENTER:
			x = startX + (gravityWidth - windowWidth)/2;
			break;
		case LEFT:
			x = startX;
			break;
		case RIGHT:
			x = startX + (gravityWidth - windowWidth);
			break;
		}
    	
    	return x;
	}
	
	private static int getWindowVertical(int window, Activity activity, int windowWidth, int windowHeight) {
		Window relativeWindow = activity.getParent();
		
		int y = 0;
		int startY = 0;
		int gravityHeight = 0;
		if((window & IN_OUT_MASK) == IN) {
			startY = relativeWindow.getY() - activity.getActionBar().getHeight();
			gravityHeight = relativeWindow.getHeight();
		} else {//if((window & IN_OUT_MASK) == OUT) {
			startY = relativeWindow.getY() - windowHeight;
			gravityHeight = relativeWindow.getHeight() + windowHeight*2;
		}
		switch (window & VERTICAL_MASK) {
		case TOP:
			y = startY;
			break;
		case MID:
			y = startY + (gravityHeight - windowHeight)/2;
			break;
		case BOTTOM:
			y = startY + (gravityHeight - windowHeight);
			break;
		}
		
		return y;
	}
}
