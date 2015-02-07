package thahn.java.agui.graphics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import thahn.java.agui.utils.MyUtils;

public class ImageDrawable extends Drawable {
	
//	private ImageIcon mImages; 
	protected BufferedImage mImages; 
	
	protected ImageDrawable() {
		
	}
	
	private ImageDrawable(URL path) {
		try {
//			BufferedImage bi 
			mImages = ImageIO.read(MyUtils.getResourceInputStream(path.getPath()));//path);
//			((Graphics2D)bi.getGraphics()).rotate(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		mImages = new ImageIcon(path);
	}

	public static ImageDrawable load(String path) {
		ImageDrawable drawable = null;
		try {
			drawable = new ImageDrawable(new File(path).toURL());
			drawable.width = drawable.getImage().getWidth(null);
			drawable.height = drawable.getImage().getHeight(null);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} 
		return drawable;
	}
	
	@Override
	public void draw(Graphics g) {
		g.drawImage(mImages, x, y, width, height, null);
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
