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

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallback;

/**
 * This simple callback allows to play a sound notification before calling
 * another timeline
 * 
 * @author Antoine Neveux
 * @version 2.1
 * @since 2.1
 * 
 */
public class AudioCallback implements TimelineCallback {

	// the achievement
	private Achievement achievement;

	// the next timeline
	private Timeline nextTimeline;

	/**
	 * Constructor.
	 * 
	 * @param achievement
	 *            The achievement.
	 * @param nextTimeline
	 *            the timeline to call after playing the sound
	 */
	public AudioCallback(Achievement achievement, Timeline nextTimeline) {
		// set it
		this.achievement = achievement;
		this.nextTimeline = nextTimeline;
	}

	@Override
	public void onTimelineStateChanged(TimelineState oldState,
			TimelineState newState, float durationFraction,
			float timelinePosition) {
		// if the current timeline is done
		if (newState == Timeline.TimelineState.DONE) {
			// play next
			nextTimeline.play();
		}
	}

	@Override
	public void onTimelinePulse(float durationFraction, float timelinePosition) {
		if (this.achievement.getConfig().isAudioEnabled()) {
			AchievementSound sound = new AchievementSound(this.achievement
					.getConfig().getAudioInputStream());
			sound.start();
		}
	}

}
