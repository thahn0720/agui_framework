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
import org.pushingpixels.trident.Timeline;

import thahn.java.agui.app.Context;
import thahn.java.agui.widget.RemoteViews;

/**
 * Packs the window and the animation configuration. Please dispose the object
 * if you don't add it to a queue.
 * 
 * @author Paulo Roberto Massa Cereda
 * @version 2.0
 * @since 2.0
 */
public class Achievement {

	// the achievement title
	private String title;

	// the achievement description
	private String description;

	// the configuration
	private AchievementConfig config;
	
	private Context context;
	private RemoteViews view;

	// the actual window
	private AchievementWindow window;

	// the timelines
	private Timeline timelineIntro;
	private Timeline timelineStay;
	private Timeline timelineAway;

	/**
	 * Constructor.
	 * 
	 * @param title
	 *            The achievement title.
	 * @param description
	 *            The achievement description.
	 */
	public Achievement(String title, String description) {

		// set both title and description
		this.title = title;
		this.description = description;

		// create a default configuration
		config = new AchievementConfig();

		// configure it
		configure();
	}

	/**
	 * Constructor with configuration options.
	 * 
	 * @param title
	 *            The achievement title.
	 * @param description
	 *            The achievement description.
	 * @param config
	 *            The configuration.
	 */
	public Achievement(String title, String description, AchievementConfig config) {

		// set everything
		this.title = title;
		this.description = description;
		this.config = config;

		// and configure it
		configure();
	}
	
	public Achievement(Context context, RemoteViews view, AchievementConfig config) {

		// set everything
		this.context = context;
		this.view = view;
		this.config = config;

		// and configure it
		configure();
	}

	/**
	 * Configures the achievement.
	 */
	private void configure() {

		// create a new achievement
		if(view == null) {
			window = new AchievementWindow(title, description, config);
		} else {
			window = new AchievementWindow(context, view, config);
		}

		// set the window height and width
		config.setWindowHeight(window.getHeight());
		config.setWindowWidth(window.getWidth());

		// create the three timelines
		timelineIntro = new Timeline(window);
		timelineStay = new Timeline(window);
		timelineAway = new Timeline(window);

		// configure the intro animation, when the window enters
		timelineIntro.addPropertyToInterpolate("position",
				config.getInitialCoordinates(), config.getFinalCoordinates());
		timelineIntro.addCallback(new AudioCallback(this, timelineStay));
		timelineIntro.setDuration(config.getInDuration());

		// configure the time the window should wait in the screen
		timelineStay.setDuration(config.getDuration());
		timelineStay.addCallback(new SimpleCallback(timelineAway));

		// configure the end animation, when the window goes away
		timelineAway.addPropertyToInterpolate("position",
				config.getFinalCoordinates(), config.getInitialCoordinates());
		timelineAway.setDuration(config.getOutDuration());
		timelineAway.addCallback(new EndCallback(window));
	}

	/**
	 * Plays the animation.
	 */
	protected void show() {
		timelineIntro.play();
	}

	/**
	 * Checks if the animation is still running.
	 * 
	 * @return A boolean which determines if the animation is still running.
	 */
	protected boolean isRunning() {

		// if every timeline is done
		if (timelineIntro.isDone() && timelineStay.isDone()
				&& timelineAway.isDone()) {

			// nothing is running, return false
			return false;
		}

		// something is still running, return true
		return true;
	}

	/**
	 * Disposes the achievement window. There's no need of calling this method,
	 * unless you don't add the achievement to the queue.
	 */
	public void dispose() {

		// if there's still an object reference
		if (window != null) {

			// dispose it
			window.dispose();
		}
	}

	/**
	 * Allows to get the configuration used by the achievement
	 * 
	 * @author Antoine Neveux
	 * @since 2.1
	 * @return the actual {@link #config} used by the current achievement
	 */
	public AchievementConfig getConfig() {
		return this.config;
	}

}
