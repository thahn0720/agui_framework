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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.Timer;

/**
 * Implements an achievement queue.
 * 
 * @author Paulo Roberto Massa Cereda
 * @version 2.1
 * @since 2.0
 */
public class AchievementQueue implements ActionListener {

	// a queue, a timer and a notification
	private Queue<Achievement> queue;
	private Timer timer;
	private Achievement current;

	/**
	 * Constructor.
	 */
	public AchievementQueue() {

		// set everything
		queue = new LinkedList<Achievement>();
		timer = new Timer(100, this);
		current = null;
	}

	/**
	 * Adds the achievement to the queue.
	 * 
	 * @param achievement
	 *            The achievement.
	 */
	public synchronized void add(Achievement achievement) {

		// check if queue is empty and there is no
		// current notification
		if (queue.isEmpty() && (current == null)) {

			// show notification
			current = achievement;
			current.show();
		} else {

			// there are other notifications, so we need to wait
			queue.offer(achievement);

			// check if timer is not running
			if (timer.isRunning() == false) {

				// start the timer
				timer.start();
			}
		}
	}

	/**
	 * Implements the listener for the queue.
	 * 
	 * @param e
	 *            The event.
	 */
	public synchronized void actionPerformed(ActionEvent e) {
		notifyAll();
		// there is a current notification going on
		if (current != null) {

			// check if queue is not empty and there is no
			// notification running
			if ((!queue.isEmpty()) && (!current.isRunning())) {

				// poll a notification from the queue
				current = (Achievement) queue.poll();

				// animate
				current.show();
			} else {

				// if the queue is empty
				if (queue.isEmpty()) {

					// stop the timer
					timer.stop();
				}
			}
		}
	}

	/**
	 * This method works like the join() method on a thread. It allows to wait
	 * for the whole queue to be processed by keeping its thread alive. You can
	 * use it easily by calling it after adding elements in the queue:
	 * 
	 * @code AchievementQueue q = new AchievementQueue(); q.add(new
	 *       Achievement("title","description"); q.add(new
	 *       Achievement("title","description"); q.add(new
	 *       Achievement("title","description"); q.join();
	 * @code
	 * 
	 *       Following this example, the 3 achievements will be displayed one by
	 *       one, and the program will end properly at the end of the third
	 *       achievement. Without the {@link #join()} method, the programs ends
	 *       directly after the third add() execution, without displaying
	 *       properly the achievements. Have a look at the unit tests and remove
	 *       the {@link #join()} call to see the difference.
	 * 
	 * @author Antoine Neveux
	 * @since 2.1
	 */
	public synchronized void join() throws InterruptedException {
		do {
			wait(100);
		} while ((current != null && current.isRunning()) || !queue.isEmpty());
	}
}