/*
 *   JAchievement -- An achievement notification library
 *   Copyright (c) 2012, Antoine Neveux, Paulo Roberto Massa Cereda
 *   All rights reserved.
 *
 *   Redistribution and  use in source  and binary forms, with  or without
 *   modification, are  permitted provided  that the  following conditions
 *   are met:
 *
 *   1. Redistributions  of source  code must  retain the  above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form  must reproduce the above copyright
 *   notice, this list  of conditions and the following  disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 *   3. Neither  the name  of the  project's author nor  the names  of its
 *   contributors may be used to  endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 *   THIS SOFTWARE IS  PROVIDED BY THE COPYRIGHT  HOLDERS AND CONTRIBUTORS
 *   "AS IS"  AND ANY  EXPRESS OR IMPLIED  WARRANTIES, INCLUDING,  BUT NOT
 *   LIMITED  TO, THE  IMPLIED WARRANTIES  OF MERCHANTABILITY  AND FITNESS
 *   FOR  A PARTICULAR  PURPOSE  ARE  DISCLAIMED. IN  NO  EVENT SHALL  THE
 *   COPYRIGHT HOLDER OR CONTRIBUTORS BE  LIABLE FOR ANY DIRECT, INDIRECT,
 *   INCIDENTAL, SPECIAL, EXEMPLARY,  OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *   BUT  NOT LIMITED  TO, PROCUREMENT  OF SUBSTITUTE  GOODS OR  SERVICES;
 *   LOSS  OF USE,  DATA, OR  PROFITS; OR  BUSINESS INTERRUPTION)  HOWEVER
 *   CAUSED AND  ON ANY THEORY  OF LIABILITY, WHETHER IN  CONTRACT, STRICT
 *   LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 *   WAY  OUT  OF  THE USE  OF  THIS  SOFTWARE,  EVEN  IF ADVISED  OF  THE
 *   POSSIBILITY OF SUCH DAMAGE.
 */
// package definition
package org.jachievement;

// needed imports
import java.awt.Font;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.MatteBorder;

import net.miginfocom.swing.MigLayout;
import thahn.java.agui.app.BorderManagerImpl;
import thahn.java.agui.app.Context;
import thahn.java.agui.app.MyPanel;
import thahn.java.agui.view.LayoutInflater;
import thahn.java.agui.view.View;
import thahn.java.agui.widget.RemoteViews;

/**
 * Implements the achievement window.
 * 
 * @author Paulo Roberto Massa Cereda
 * @version 2.0
 * @since 2.0
 */
@SuppressWarnings("serial")
public class AchievementWindow extends JWindow {

	// the configuration
	private AchievementConfig config;
	// the title
	String title;
	// the description
	String description;

	/**
	 * Constructor.
	 * 
	 * @param theTitle
	 *            The achievement title.
	 * @param theDescription
	 *            The achievement description.
	 * @param theConfig
	 *            The configuration.
	 */
	public AchievementWindow(String theTitle, String theDescription,
			AchievementConfig theConfig) {

		// instantiate superclass
		super();

		// set the attributes
		title = theTitle;
		description = theDescription;
		config = theConfig;

		// create a new border
		getRootPane().setBorder(
				new MatteBorder(config.getBorderThickness(), config
						.getBorderThickness(), config.getBorderThickness(),
						config.getBorderThickness(), config.getBorderColor()));

		// set the layout
		setLayout(new MigLayout());

		// set the background color
		getRootPane().setBackground(config.getBackgroundColor());

		// if there's a background image
		if (config.getBackgroundImage() != null) {

			// create a label with that image
			JLabel labelBackground = new JLabel(config.getBackgroundImage());

			// set the bounds
			labelBackground.setBounds(0, 0, config.getBackgroundImage()
					.getIconWidth(), config.getBackgroundImage()
					.getIconHeight());

			// add it
			getLayeredPane().add(labelBackground,
					new Integer(Integer.MIN_VALUE));
		}

		// create a new panel
		JPanel contentPanel = new JPanel();
		contentPanel.setOpaque(false);

		// set the new layout
		contentPanel.setLayout(new MigLayout("ins dialog, gapx 15, hidemode 0",
				"15[][grow]15", "15[][grow]15"));

		// create a new icon
		JLabel icon = new JLabel(config.getIcon());
		contentPanel.add(icon, "cell 0 0 0 2, align center");

		// create the achievement title
		String strTitle = String.format(
				"<html><div style=\"width:%dpx;\">%s</div><html>", 200, title);
		JLabel lblTitle = new JLabel(strTitle);

		// if there's no font
		if (config.getTitleFont() == null) {

			// set default
			lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14f));

		} else {

			// set the one from config
			lblTitle.setFont(config.getTitleFont());
		}

		// set the font color
		lblTitle.setForeground(config.getTitleColor());

		// create the achievement description
		String strDescription = String.format(
				"<html><div style=\"width:%dpx;\">%s</div><html>", 200,
				description);
		JLabel lblDescription = new JLabel(strDescription);

		// if there's font
		if (config.getDescriptionFont() != null) {

			// set it
			lblDescription.setFont(config.getDescriptionFont());
		}

		// set the description color
		lblDescription.setForeground(config.getDescriptionColor());

		// add both title and description
		contentPanel.add(lblTitle, "cell 1 0, aligny center");
		contentPanel.add(lblDescription,
				"cell 1 1, aligny center, growy, width 260!");

		// set content to the window
		setContentPane(contentPanel);

		// set the windows always on top
		setAlwaysOnTop(true);

		// pack everything
		pack();

		// put the window away
		setBounds(-getWidth(), -getHeight(), getWidth(), getHeight());
	}

	public AchievementWindow(Context context, RemoteViews view, AchievementConfig theConfig) {

		// instantiate superclass
		super();

		// set the attributes
		config = theConfig;

		// create a new border
		getRootPane().setBorder(
				new MatteBorder(config.getBorderThickness(), config
						.getBorderThickness(), config.getBorderThickness(),
						config.getBorderThickness(), config.getBorderColor()));

		// set the layout
		setLayout(new MigLayout());

		// set the background color
		getRootPane().setBackground(config.getBackgroundColor());

		// if there's a background image
		if (config.getBackgroundImage() != null) {

			// create a label with that image
			JLabel labelBackground = new JLabel(config.getBackgroundImage());

			// set the bounds
			labelBackground.setBounds(0, 0, config.getBackgroundImage()
					.getIconWidth(), config.getBackgroundImage()
					.getIconHeight());

			// add it
			getLayeredPane().add(labelBackground,
					new Integer(Integer.MIN_VALUE));
		}

		//  
		View childView = LayoutInflater.inflate(context, view.getLayoutId(), null);
		MyPanel myPanel = new MyPanel();
		myPanel.setView(childView);
		myPanel.setOpaque(false);
		
		// create a new panel
//		JPanel contentPanel = new JPanel();
//		contentPanel.setOpaque(false);
//		contentPanel.add(myPanel);

		// set content to the window
		setContentPane(myPanel);
//		setContentPane(contentPanel);

		// set the windows always on top
		setAlwaysOnTop(true);

		// pack everything
		pack();

		// put the window away
		int borderSize = BorderManagerImpl.mBorderWidth*2;
		setBounds(-childView.getWidth(), -childView.getHeight(), childView.getWidth()+borderSize, childView.getHeight()+borderSize);
	}
	
	/**
	 * Sets position on screen.
	 * 
	 * @param p
	 *            The new position.
	 */
	public void setPosition(Point p) {

		// if not visible
		if (!isVisible()) {

			// show window
			setVisible(true);
		}

		// set new location
		setBounds(p.x, p.y, getWidth(), getHeight());
	}
}