package thahn.java.agui.app;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.Serializable;

import javax.swing.JPanel;

import thahn.java.agui.graphics.ColorDrawable;
import thahn.java.agui.graphics.Drawable;
import thahn.java.agui.view.View;


public class MyPanel extends JPanel implements Serializable {
	private static final long 									serialVersionUID 	= 3254168988273518199L;

	/*package*/ View											mDecorView;
	/*package*/ Image 											mOffScreen;
	/*package*/ Graphics 										mOffGc;
	/*package*/ Drawable										mBackgroundDrawable;
	
	public MyPanel() {
		super(new BorderLayout());
	}
	
	public void setView(View shellView) {
		mDecorView = shellView;
		setOpaque(false);
	}

	@Override
	public void update(Graphics g) {
		super.update(g);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		if(mDecorView != null) {
//			Log.i(""+
//					mDecorView.getLeft() + ", " +
//					mDecorView.getTop() + ", " +
//					(mDecorView.getWidth()+ApplicationSetting.WINDOW_WIDTH_PADDING) + ", " +
//					(mDecorView.getHeight()+ApplicationSetting.WINDOW_HEIGHT_PADDING)
//					);
//			g.setClip(mDecorView.getLeft(), mDecorView.getTop(), mDecorView.getWidth(), mDecorView.getHeight());
			mDecorView.mDefaultMatrix = ((Graphics2D)g).getTransform();
			mDecorView.draw(g);
			mDecorView.onDraw(g);
			// draw border line
//			g.setColor(Color.BLACK);
//			g.clearRect(mDecorView.getLeft(), mDecorView.getTop(), mDecorView.getWidth(), mDecorView.getHeight());
//			g.fillRect(
//					0, 0, 
//					mDecorView.getLeft()+mDecorView.getWidth()+ApplicationSetting.WINDOW_WIDTH_PADDING, 
//					mDecorView.getTop()+mDecorView.getHeight()+ApplicationSetting.WINDOW_HEIGHT_PADDING
//					);
//					mDecorView.getLeft(), mDecorView.getTop(), mDecorView.getRight(), mDecorView.getBottom());
			// draw border
//			g.setPaintMode();
//			g.setColor(Color.ORANGE);
//			g.fillRect(mDecorView.getRight(), mDecorView.getTop(), ApplicationSetting.WINDOW_WIDTH_PADDING, mDecorView.getHeight()+ApplicationSetting.WINDOW_WIDTH_PADDING);
//			g.fillRect(mDecorView.getLeft(), mDecorView.getBottom(), mDecorView.getWidth()+ApplicationSetting.WINDOW_WIDTH_PADDING, ApplicationSetting.WINDOW_WIDTH_PADDING);
			//
//			int w = mDecorView.getWidth()+ApplicationSetting.WINDOW_WIDTH_PADDING;
//			int h = mDecorView.getHeight()+ApplicationSetting.WINDOW_WIDTH_PADDING;
//			g.setColor(Color.BLACK); // g2.setPaint(Color.RED);
//			g.drawRect(0, 0, w - 1, h - 1);
//			g.drawLine(0, 2, 2, 0);
//			g.drawLine(w - 3, 0, w - 1, 2);
//			g.clearRect(0, 0, 2, 1);
//			g.clearRect(0, 0, 1, 2);
//			g.clearRect(w - 2, 0, 2, 1);
//			g.clearRect(w - 1, 0, 1, 2);
//			g.dispose();
		}
	}
	
	public void arrangeView() {
		if(mDecorView != null) mDecorView.arrange();
	}

	@Override
	public void invalidate() {
		// FIXME : optimal is needed really. ex) double buffering
		repaint();
	}
	
	public void setBackgroundColor(int rgba) {
		mBackgroundDrawable = ColorDrawable.load(rgba);
		// setBackgroundColor(thahn.java.agui.graphics.Color.toAwtColor(0x0045ffff));//thahn.java.agui.graphics.Color.toAwtColor(rgb));
	}
	
	public void setTranslucent(boolean is) {
		setOpaque(!is);
	}

	@Override
	public String toString() {
		return "MyPanel : "+super.toString();
	}
}
