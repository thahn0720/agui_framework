package thahn.java.agui.graphics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import thahn.java.agui.utils.MyUtils;

import com.android.ninepatch.NinePatch;


public class NinePatchDrawable extends Drawable {
	private NinePatch mNinePatch;
	
	private NinePatchDrawable(NinePatch ninePatch) {
		mNinePatch = ninePatch;
	}

	public static NinePatchDrawable load(String path) {
		NinePatchDrawable drawable = null;
		try {
			drawable = new NinePatchDrawable(NinePatch.load(MyUtils.getResourceInputStream(path), true, false));
					//new File(path).toURL(), false));
			drawable.width = drawable.getImage().getWidth(null);
			drawable.height = drawable.getImage().getHeight(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return drawable;
	}
	
	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		super.setBounds(left, top, right, bottom);
//		if(this.width != width || this.height != height) mNinePatch.computePatches(width, height);
	}

	@Override
	public void draw(Graphics g) {
		mNinePatch.draw((Graphics2D)g, x, y, width, height);
	}

	@Override
	public Image getImage() {
		return mNinePatch.getImage();
	}

	@Override
	public void rotate(Graphics2D g, double theta, int x, int y) {
		g.rotate(theta, x, y);
		draw(g);
	}
}
