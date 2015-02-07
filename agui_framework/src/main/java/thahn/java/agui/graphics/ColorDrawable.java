package thahn.java.agui.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

public class ColorDrawable extends Drawable {
	
	private BufferedImage mImages; 
	
	private ColorDrawable(int color) {
		mImages = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics g = mImages.getGraphics();
		g.setColor(new Color(color, true));
		g.fillRect(0, 0, 1, 1);
	}

	public static ColorDrawable load(int color) {
		ColorDrawable drawable = new ColorDrawable(color);
		drawable.width = drawable.getImage().getWidth(null);
		drawable.height = drawable.getImage().getHeight(null);
		return drawable;
	}
	
	@Override
	public void draw(Graphics g) {
		g.drawImage(mImages, x, y, width, height, null);
	}
	
	public void setColor(int color) {
		Graphics g = mImages.getGraphics();
		g.setColor(new Color(color));
		g.fillRect(0, 0, 1, 1);
	}

	@Override
	public Image getImage() {
		return mImages;
	}

	@Override
	public void rotate(Graphics2D g, double theta, int x, int y) {
		g.rotate(theta, x, y);
		draw(g);
	}
}

