package thahn.java.agui.graphics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.HashMap;

import thahn.java.agui.utils.Log;

public class MultipleDrawable extends Drawable {

	public static final int 								STATE_NONE 					= "none".hashCode();
	
	private HashMap<Integer, Drawable>						mDrawables;
	
	private MultipleDrawable() {
		mDrawables = new HashMap<>();
		setState(Drawable.STATE_PRESSED_FALSE);
	}

	public static MultipleDrawable load() {
		MultipleDrawable drawable = new MultipleDrawable();
		drawable.setState(Drawable.STATE_NORMAL);
		return drawable;
	}

	public void add(int state, Drawable d) {
		mDrawables.put(state, d);
		width = d.width = d.getImage().getWidth(null);
		height = d.height = d.getImage().getHeight(null);
	}
	
	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		super.setBounds(left, top, right, bottom);
		for(Drawable d : mDrawables.values()) {
			d.setBounds(left, top, right, bottom);
		}
	}

	@Override
	public void draw(Graphics g) {
		boolean none = true;
		
		for (int i = 0; i < currentState.length; i++) {
			if (currentState[i] && mDrawables.containsKey(VIEW_STATE_SETS[i])) {
				none = false;
				mDrawables.get(VIEW_STATE_SETS[i]).draw(g);
			}
		}
		
		if (none && mDrawables.containsKey(STATE_NONE)) {
			mDrawables.get(STATE_NONE).draw(g);
		}
	}

	@Override
	public Image getImage() {
		return mDrawables.get(currentState).getImage();
	}

	@Override
	public void rotate(Graphics2D g, double theta, int x, int y) {
		g.rotate(theta, x, y);
	}
}
