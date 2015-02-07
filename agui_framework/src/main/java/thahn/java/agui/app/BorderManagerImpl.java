package thahn.java.agui.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BorderManagerImpl implements BorderManager {

	public static final int												mBorderWidth 				= 3;
	
	private Window														mWindow;
	private Color 														mBorderBackColor 			= new Color(50, 100, 255, 220);
	private Color 														mBorderLineColor 			= Color.DARK_GRAY;
	private JLabel 														mLeft, mRight, mTop, mBottom, mTopLeft, mTopRight, mBottomLeft, mBottomRight;
	private JPanel 														mContentPanel 				= new JPanel(new BorderLayout());
	private TitleActionBar												mTitleActionBar;
	
	public enum Side { 
		NW_SIDE, 
		N_SIDE, 
		NE_SIDE, 
		L_SIDE, 
		R_SIDE, 
		SW_SIDE, 
		S_SIDE, 
		SE_SIDE; 
	}
	
	/*package*/ BorderManagerImpl(Window window, final JFrame frame) {
		init(window, frame);
	}
	
	private void init(Window window, final JFrame frame) {
		mWindow = window;
		
		frame.setContentPane(mContentPanel);
		frame.setUndecorated(true);
//		frame.setBackground(new Color(255, 255, 255, 0));
		
		initBorder(frame);
		initInternalPanel(frame);

		frame.setContentPane(mResizePanel);
	}

	private void initInternalPanel(final JFrame frame) {
		// //javax/swing/plaf/metal/MetalTitlePane.java
		// JButton iconify = new JButton("_");
		// iconify.setContentAreaFilled(false);
		// iconify.setFocusPainted(false);
		// iconify.setBorder(BorderFactory.createEmptyBorder());
		// iconify.setOpaque(true);
		// iconify.setBackground(Color.ORANGE);
		// iconify.addActionListener(new ActionListener() {
		// @Override public void actionPerformed(ActionEvent e) {
		// frame.setExtendedState(state | Frame.ICONIFIED);
		// }
		// });
//		JButton button = new JButton(new CloseIcon());
//		button.setContentAreaFilled(false);
//		button.setFocusPainted(false);
//		button.setBorder(BorderFactory.createEmptyBorder());
//		button.setOpaque(true);
//		button.setBackground(Color.ORANGE);
//		button.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				frame.dispatchEvent(new WindowEvent(frame,
//						WindowEvent.WINDOW_CLOSING));
//			}
//		});
		mTitleActionBar = new TitleActionBar();
		mTitleActionBar.setTitleBar(new TitleBar(mWindow)); // , new BorderLayout()
		mTitleActionBar.mTitleBar.setTitleContext(new TitleContext(mWindow, mTitleActionBar.mTitleBar));
		mTitleActionBar.mTitleBar.setOpaque(false);
		// title.setBackground(Color.ORANGE);
//		title.setPreferredSize(new Dimension(100, 100));
		mTitleActionBar.mTitleBar.setBorder(BorderFactory.createEmptyBorder(mBorderWidth, mBorderWidth, mBorderWidth, mBorderWidth));
//		mTitleBar.setPreferredSize(new Dimension(0, mTitleBarHeight));
//		mTitleBar.add(new JLabel("", JLabel.CENTER));
//		mTitleBar.add(button, BorderLayout.EAST);
		// title.add(iconify, BorderLayout.WEST);
		
		/**
		 *     resizePanel                             
		 *          NORTH   ->  northPanel  -    mTopLeft  | titlePanel | mTopRight
		 *       WEST, EAST ->  mLeft, mRight
		 *          CENTER  ->  mContentPanel
		 *          SOUTH   ->  southPanel  -  mBottomLeft |  mBottom   | mBottomRight
		 *       
		 */
		JPanel titlePanel = new JPanel(new BorderLayout(0, 0));
		titlePanel.add(mTop, BorderLayout.NORTH);
		titlePanel.add(mTitleActionBar.mTitleBar, BorderLayout.CENTER);

		JPanel northPanel = new JPanel(new BorderLayout(0, 0));
		northPanel.add(mTopLeft, BorderLayout.WEST);
		northPanel.add(titlePanel, BorderLayout.CENTER);
		northPanel.add(mTopRight, BorderLayout.EAST);

		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(mBottomLeft, BorderLayout.WEST);
		southPanel.add(mBottom, BorderLayout.CENTER);
		southPanel.add(mBottomRight, BorderLayout.EAST);

		mResizePanel.add(mLeft, BorderLayout.WEST);
		mResizePanel.add(mRight, BorderLayout.EAST);
		mResizePanel.add(northPanel, BorderLayout.NORTH);
		mResizePanel.add(southPanel, BorderLayout.SOUTH);
		mResizePanel.add(mContentPanel, BorderLayout.CENTER);

		titlePanel.setOpaque(false);
		northPanel.setOpaque(false);
		southPanel.setOpaque(false);

		mContentPanel.setOpaque(false);
		mResizePanel.setOpaque(false);
		
		makeTitleBarByFeature(null);
	}
	
	private void initBorder(final JFrame frame) {
		ResizeWindowListener rwl = new ResizeWindowListener(frame);
		for (JLabel l : java.util.Arrays.asList(mLeft = new JLabel(),
				mRight = new JLabel(), mTop = new JLabel(),
				mBottom = new JLabel(), mTopLeft = new JLabel(),
				mTopRight = new JLabel(), mBottomLeft = new JLabel(),
				mBottomRight = new JLabel())) {
			l.addMouseListener(rwl);
			l.addMouseMotionListener(rwl);
			// l.setOpaque(true);
			// l.setBackground(Color.RED);
		}

		// //top.setBorder(BorderFactory.createMatteBorder(1,0,0,0,borderColor));
		// left.setBorder(BorderFactory.createMatteBorder(0,1,0,0,borderColor));
		// //bottom.setBorder(BorderFactory.createMatteBorder(0,0,1,0,borderColor));
		// right.setBorder(BorderFactory.createMatteBorder(0,0,0,1,borderColor));

		// topleft.setBorder(BorderFactory.createMatteBorder(1,1,0,0,borderColor));
		// bottomleft.setBorder(BorderFactory.createMatteBorder(0,1,1,0,borderColor));
		// bottomright.setBorder(BorderFactory.createMatteBorder(0,0,1,1,borderColor));
		// topright.setBorder(BorderFactory.createMatteBorder(1,0,0,1,borderColor));

		// topleft.setBackground(Color.GREEN);
		// topright.setBackground(Color.GREEN);
		// bottomleft.setBackground(Color.GREEN);
		// bottomright.setBackground(Color.GREEN);

		Dimension d = new Dimension(mBorderWidth, 0);
		mLeft.setPreferredSize(d);
		mLeft.setMinimumSize(d);
		mRight.setPreferredSize(d);
		mRight.setMinimumSize(d);

		d = new Dimension(0, mBorderWidth);
		mTop.setPreferredSize(d);
		mTop.setMinimumSize(d);
		mBottom.setPreferredSize(d);
		mBottom.setMinimumSize(d);

		d = new Dimension(mBorderWidth, mBorderWidth);
		mTopLeft.setPreferredSize(d);
		mTopLeft.setMinimumSize(d);
		mTopRight.setPreferredSize(d);
		mTopRight.setMinimumSize(d);
		mBottomLeft.setPreferredSize(d);
		mBottomLeft.setMinimumSize(d);
		mBottomRight.setPreferredSize(d);
		mBottomRight.setMinimumSize(d);

		mLeft.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
		mRight.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
		mTop.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
		mBottom.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
		mTopLeft.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
		mTopRight.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
		mBottomLeft.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
		mBottomRight.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
	}
	
	/*package*/ void addContents(Component panel, int index) {
		mContentPanel.add(panel, 0);
	}
	
	/*package*/ void removeContents(Component panel) {
		mContentPanel.remove(panel);
	}
	
	private JPanel mResizePanel = new JPanel(new BorderLayout()) {
		
		private static final long serialVersionUID = 919872466010434869L;

		@Override
		protected void paintComponent(Graphics g) {
			// super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			int w = getWidth();
			int h = getHeight();
			//
			// FIXME : setClip?
//			g.setClip(0, 0, w, h);
			//
			g2.setPaint(mBorderBackColor); 
			g2.fillRect(0, 0, w, h);
			g2.setPaint(mBorderLineColor);
			g2.drawRect(0, 0, w - 1, h - 1);

			// g2.setPaint(Color.WHITE);
			// g2.setPaint(new Color(0,0,0,0));
			// g2.drawLine(0,0,0,0);
			// g2.drawLine(w-1,0,w-1,0);
			g2.drawLine(0, 2, 2, 0);
			g2.drawLine(w - 3, 0, w - 1, 2);

			g2.clearRect(0, 0, 2, 1);
			g2.clearRect(0, 0, 1, 2);
			g2.clearRect(w - 2, 0, 2, 1);
			g2.clearRect(w - 1, 0, 1, 2);

			g2.dispose();
		}
	};
	
	class ResizeWindowListener extends MouseAdapter {
		private Rectangle startSide = null;
		private final JFrame frame;

		public ResizeWindowListener(JFrame frame) {
			this.frame = frame;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			startSide = frame.getBounds();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if(!ApplicationSetting.getInstance().isAutoResize() || startSide == null) return;
			Component c = e.getComponent();
			int x = e.getX();
			int y = e.getY();
			int limitW =  ApplicationSetting.WINDOW_WIDTH_PADDING + mWindow.getCurrentActivity().mActivityInfo.width;
			int limitH = mWindow.getCurrentActivity().mActivityInfo.height;
			
			if(mTitleActionBar == null || mTitleActionBar.isTitleVisible()) {
				limitH += ApplicationSetting.WINDOW_HEIGHT_PADDING;
			} else {
				limitH += ApplicationSetting.WINDOW_WIDTH_PADDING;
			}
			
			if(startSide.width + x < limitW) {
				startSide.width -= x; 
			}
			if(startSide.height + y < limitH) {
				startSide.height -= y;
			}
			
			if (c == mTopLeft) {
				startSide.y += y;
				startSide.height -= y;
				startSide.x += x;
				startSide.width -= x;
			} else if (c == mTop) {
				startSide.y += y;
				startSide.height -= y;	
			} else if (c == mTopRight) {
				startSide.y += y;
				startSide.height -= y;
				startSide.width += x;
			} else if (c == mLeft) {
				startSide.x += x;
				startSide.width -= x;
			} else if (c == mRight) {
				startSide.width += x;
			} else if (c == mBottomLeft) {
				startSide.height += y;
				startSide.x += x;
				startSide.width -= x;
			} else if (c == mBottom) {
				startSide.height += y;
			} else if (c == mBottomRight) {
				startSide.height += y;
				startSide.width += x;
			}
			frame.setBounds(startSide);
		}
	}

	/*package*/ TitleActionBar getTitleActionBar() {
		return mTitleActionBar;
	}
	
	/*package*/ void makeTitleBarByFeature(ActivityInfo info) {
		if(!mWindow.hasFeature(Window.FEATURE_NO_TITLE)) {
			if(mWindow.hasFeature(Window.FEATURE_CUSTOM_TITLE)) {
				mTitleActionBar.mTitleBar.setVisible(true);
				if(info != null && info.customTitle != -1) {
					mTitleActionBar.setCustomView(info.customTitle);
				} else {
					mTitleActionBar.mTitleBar.setDefaultTitleBar();
				}
			} else {
				mTitleActionBar.mTitleBar.setDefaultTitleBar();
			}
			mTitleActionBar.mTitleBar.setVisible(true);
		}
	}
	
	/*package*/ void setBorderBackColor(int a, int r, int g, int b) {
		mBorderBackColor = new Color(r, g, b, a);
		mResizePanel.invalidate();
	}
	
	/*package*/ void setBorderLineColor(int a, int r, int g, int b) {
		mBorderLineColor = new Color(r, g, b, a);
		mResizePanel.invalidate();
	}

	/*package*/ JPanel getContentPanel() {
		return mContentPanel;
	}
}
