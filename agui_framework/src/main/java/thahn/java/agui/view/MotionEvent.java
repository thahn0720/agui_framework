package thahn.java.agui.view;

import java.awt.event.MouseEvent;


public class MotionEvent {
	/** mouse left button */
	public static final int											BUTTON1 			= MouseEvent.BUTTON1;
	/** mouse wheel button */
	public static final int											BUTTON2 			= MouseEvent.BUTTON2;
	/** mouse right button */
	public static final int											BUTTON3 			= MouseEvent.BUTTON3;
	
	public static final int											ACTION_DOWN 		= 0;
	public static final int											ACTION_UP		 	= 1;
	public static final int											ACTION_MOVE		 	= 2;
	public static final int											ACTION_CLICK	 	= 3;
	public static final int											ACTION_WHEEL	 	= 4;

	/**
     * Constant for {@link #getActionMasked}: The current gesture has been aborted.
     * You will not receive any more points in it.  You should treat this as
     * an up event, but not perform any action that you normally would.
     */
    public static final int 										ACTION_CANCEL       = 11;
	
    private static final long 										MS_PER_NS = 1000000;
    /*
     * Minimum number of pointers for which to reserve space when allocating new
     * motion events.  This is explicitly not a bound on the maximum number of pointers.
     */
    static private final int 										BASE_AVAIL_POINTERS = 5;
    /*
     * Minimum number of samples for which to reserve space when allocating new motion events.
     */
    static private final int 										BASE_AVAIL_SAMPLES = 8;
    /*
     * Number of data items for each sample.
     */
    static private final int 										NUM_SAMPLE_DATA = 9;
    /*
     * Offset for the sample's X coordinate.
     */
    static private final int 										SAMPLE_X = 0;
    /*
     * Offset for the sample's Y coordinate.
     */
    static private final int 										SAMPLE_Y = 1;
    /*
     * Offset for the sample's pressure.
     */
    static private final int 										SAMPLE_PRESSURE = 2;
    /*
     * Offset for the sample's size
     */
    static private final int 										SAMPLE_SIZE = 3;
    /*
     * Offset for the sample's touch major axis length.
     */
    static private final int 										SAMPLE_TOUCH_MAJOR = 4;
    /*
     * Offset for the sample's touch minor axis length.
     */
    static private final int 										SAMPLE_TOUCH_MINOR = 5;
    /*
     * Offset for the sample's tool major axis length.
     */
    static private final int 										SAMPLE_TOOL_MAJOR = 6;
    /*
     * Offset for the sample's tool minor axis length.
     */
    static private final int 										SAMPLE_TOOL_MINOR = 7;
    /*
     * Offset for the sample's orientation.
     */
    static private final int 										SAMPLE_ORIENTATION = 8;
    
    /*package*/ MouseEvent											mMouseEvent;
    
	private int														mAction;
	private int														mButtonCode;
	private long 													mDownTime;
	private float 													mX;
	private float 													mY;
	
	private float 													mXOffset;
    private float 													mYOffset;
	private int 													mLastDataSampleIndex;
	private int 													mLastEventTimeNanoSampleIndex;
	private int 													mNumSamples;
	private int 													mNumPointers;
	private long 													mDownTimeNano;
	
    // Array of mNumPointers size of identifiers for each pointer of data.
    private int[] 													mPointerIdentifiers;
    
    // Array of (mNumSamples * mNumPointers * NUM_SAMPLE_DATA) size of event data.
    // Samples are ordered from oldest to newest.
    private float[] 												mDataSamples;
    
    // Array of mNumSamples size of event time stamps in nanoseconds.
    // Samples are ordered from oldest to newest.
    private long[] 													mEventTimeNanoSamples;
	
	private MotionEvent() {
        mPointerIdentifiers = new int[BASE_AVAIL_POINTERS];
        mDataSamples = new float[BASE_AVAIL_POINTERS * BASE_AVAIL_SAMPLES * NUM_SAMPLE_DATA];
        mEventTimeNanoSamples = new long[BASE_AVAIL_SAMPLES];
	}
	
	public MotionEvent(MouseEvent e, int action, int buttonCode) {
		mPointerIdentifiers = new int[BASE_AVAIL_POINTERS];
        mDataSamples = new float[BASE_AVAIL_POINTERS * BASE_AVAIL_SAMPLES * NUM_SAMPLE_DATA];
        mEventTimeNanoSamples = new long[BASE_AVAIL_SAMPLES];
        
		mMouseEvent = e;
		mDownTime = e.getWhen();
		mX = e.getX();
		mY = e.getY();
		mAction = action;
		mButtonCode = buttonCode;
		mNumSamples = 0;
	}
	
	static public MotionEvent obtain(long downTime, long eventTime, int action, float x, float y, int metaState) {
		MotionEvent ev = new MotionEvent();//obtain(1, 1);
		ev.mDownTime = downTime;
		ev.mAction = action;
		ev.mX = x;
		ev.mY = y;
		ev.mNumSamples = 0;
        ev.mDownTimeNano = downTime * MS_PER_NS;
        ev.mAction = action;
        ev.mXOffset = 0;
        ev.mYOffset = 0;
        
        ev.mNumPointers = 1;
        ev.mNumSamples = 1;
        
        ev.mLastDataSampleIndex = 0;
        ev.mLastEventTimeNanoSampleIndex = 0;
        
        ev.mPointerIdentifiers[0] = 0;
        
        ev.mEventTimeNanoSamples[0] = eventTime * MS_PER_NS;
        
        ev.setPointerCoordsAtSampleIndex(0, x, y, 1.0f, 1.0f);
        return ev;
//        return obtain(downTime, eventTime, action, x, y, 1.0f, 1.0f, metaState, 1.0f, 1.0f, 0, 0);
    }
	
	public int getAction() {
		return mAction;
	}
	
	public int getX() {
		return (int) mX;//mMouseEvent.getX();// + ApplicationSetting.WINDOW_BORDER_WIDTH;
//		return (int)(mDataSamples[mLastDataSampleIndex + SAMPLE_X] + mXOffset);
	}

	public int getY() {
		return (int) mY;//mMouseEvent.getY();// + ApplicationSetting.TITLE_BAR_HEIGHT + ApplicationSetting.WINDOW_BORDER_WIDTH; 
//		return (int)(mDataSamples[mLastDataSampleIndex + SAMPLE_Y] + mYOffset);
	}

	public int getButtonCode() {
		return mButtonCode;
	}
	
	/*package*/ MouseEvent getOriginalEvent() {
		return mMouseEvent;
	}
	
	/**
     * The number of pointers of data contained in this event.  Always
     * >= 1.
     */
    public final int getPointerCount() {
        return mNumPointers;
    }
    
    /**
     * Return the pointer identifier associated with a particular pointer
     * data index is this event.  The identifier tells you the actual pointer
     * number associated with the data, accounting for individual pointers
     * going up and down since the start of the current gesture.
     * @param pointerIndex Raw index of pointer to retrieve.  Value may be from 0
     * (the first pointer that is down) to {@link #getPointerCount()}-1.
     */
    public final int getPointerId(int pointerIndex) {
        return mPointerIdentifiers[pointerIndex];
    }
	
	/**
     * Returns the time that a historical movement occurred between this event
     * and the previous event.  Only applies to ACTION_MOVE events.
     *
     * @param pos Which historical value to return; must be less than
     * {@link #getHistorySize}
     *
     * @see #getHistorySize
     * @see #getEventTime
     */
    public final long getHistoricalEventTime(int pos) {
        return mEventTimeNanoSamples[pos] / MS_PER_NS;
    }
    
    /**
     * Returns the X coordinate of this event for the given pointer
     * <em>index</em> (use {@link #getPointerId(int)} to find the pointer
     * identifier for this index).
     * Whole numbers are pixels; the 
     * value may have a fraction for input devices that are sub-pixel precise. 
     * @param pointerIndex Raw index of pointer to retrieve.  Value may be from 0
     * (the first pointer that is down) to {@link #getPointerCount()}-1.
     */
    public final float getX(int pointerIndex) {
        return mDataSamples[mLastDataSampleIndex
                            + pointerIndex * NUM_SAMPLE_DATA + SAMPLE_X] + mXOffset;
    }

    /**
     * Returns the Y coordinate of this event for the given pointer
     * <em>index</em> (use {@link #getPointerId(int)} to find the pointer
     * identifier for this index).
     * Whole numbers are pixels; the
     * value may have a fraction for input devices that are sub-pixel precise.
     * @param pointerIndex Raw index of pointer to retrieve.  Value may be from 0
     * (the first pointer that is down) to {@link #getPointerCount()}-1.
     */
    public final float getY(int pointerIndex) {
        return mDataSamples[mLastDataSampleIndex
                            + pointerIndex * NUM_SAMPLE_DATA + SAMPLE_Y] + mYOffset;
    }

	/**
     * Returns the number of historical points in this event.  These are
     * movements that have occurred between this event and the previous event.
     * This only applies to ACTION_MOVE events -- all other actions will have
     * a size of 0.
     *
     * @return Returns the number of historical points in the event.
     */
    public final int getHistorySize() {
        return mLastEventTimeNanoSampleIndex;
    }
    
    /**
     * {@link #getHistoricalX(int)} for the first pointer index (may be an
     * arbitrary pointer identifier).
     */
    public final float getHistoricalX(int pos) {
        return mDataSamples[pos * mNumPointers * NUM_SAMPLE_DATA + SAMPLE_X] + mXOffset;
    }

    /**
     * {@link #getHistoricalY(int)} for the first pointer index (may be an
     * arbitrary pointer identifier).
     */
    public final float getHistoricalY(int pos) {
        return mDataSamples[pos * mNumPointers * NUM_SAMPLE_DATA + SAMPLE_Y] + mYOffset;
    }
    
    /**
     * Returns a historical X coordinate, as per {@link #getX(int)}, that
     * occurred between this event and the previous event for the given pointer.
     * Only applies to ACTION_MOVE events.
     *
     * @param pointerIndex Raw index of pointer to retrieve.  Value may be from 0
     * (the first pointer that is down) to {@link #getPointerCount()}-1.
     * @param pos Which historical value to return; must be less than
     * {@link #getHistorySize}
     *
     * @see #getHistorySize
     * @see #getX
     */
    public final float getHistoricalX(int pointerIndex, int pos) {
        return mDataSamples[(pos * mNumPointers + pointerIndex)
                            * NUM_SAMPLE_DATA + SAMPLE_X] + mXOffset;
    }

    /**
     * Returns a historical Y coordinate, as per {@link #getY(int)}, that
     * occurred between this event and the previous event for the given pointer.
     * Only applies to ACTION_MOVE events.
     *
     * @param pointerIndex Raw index of pointer to retrieve.  Value may be from 0
     * (the first pointer that is down) to {@link #getPointerCount()}-1.
     * @param pos Which historical value to return; must be less than
     * {@link #getHistorySize}
     *
     * @see #getHistorySize
     * @see #getY
     */
    public final float getHistoricalY(int pointerIndex, int pos) {
        return mDataSamples[(pos * mNumPointers + pointerIndex)
                            * NUM_SAMPLE_DATA + SAMPLE_Y] + mYOffset;
    }
    
    /**
     * Returns the time (in ms) when this specific event was generated.
     */
    public final long getEventTime() {
        return mEventTimeNanoSamples[mLastEventTimeNanoSampleIndex] / MS_PER_NS;
    }
    
    /**
     * Add a new movement to the batch of movements in this event.  The event's
     * current location, position and size is updated to the new values.
     * The current values in the event are added to a list of historical values.
     * 
     * Only applies to {@link #ACTION_MOVE} events.
     *
     * @param eventTime The time stamp (in ms) for this data.
     * @param x The new X position.
     * @param y The new Y position.
     * @param pressure The new pressure.
     * @param size The new size.
     * @param metaState Meta key state.
     */
    public final void addBatch(long eventTime, float x, float y,
            float pressure, float size, int metaState) {
        incrementNumSamplesAndReserveStorage(NUM_SAMPLE_DATA);
        
        mEventTimeNanoSamples[mLastEventTimeNanoSampleIndex] = eventTime * MS_PER_NS;
        setPointerCoordsAtSampleIndex(mLastDataSampleIndex, x, y, pressure, size);
        
//        mMetaState |= metaState;
    }

    /**
     * Add a new movement to the batch of movements in this event.  The event's
     * current location, position and size is updated to the new values.
     * The current values in the event are added to a list of historical values.
     * 
     * Only applies to {@link #ACTION_MOVE} events.
     *
     * @param eventTime The time stamp (in ms) for this data.
     * @param pointerCoords The new pointer coordinates.
     * @param metaState Meta key state.
     */
    public final void addBatch(long eventTime, PointerCoords[] pointerCoords, int metaState) {
        final int dataSampleStride = mNumPointers * NUM_SAMPLE_DATA;
        incrementNumSamplesAndReserveStorage(dataSampleStride);
        
        mEventTimeNanoSamples[mLastEventTimeNanoSampleIndex] = eventTime * MS_PER_NS;
        setPointerCoordsAtSampleIndex(mLastDataSampleIndex, pointerCoords);
        
//        mMetaState |= metaState;
    }
    
    private final void incrementNumSamplesAndReserveStorage(int dataSampleStride) {
        if (mNumSamples == mEventTimeNanoSamples.length) {
            long[] newEventTimeNanoSamples = new long[mNumSamples + BASE_AVAIL_SAMPLES];
            System.arraycopy(mEventTimeNanoSamples, 0, newEventTimeNanoSamples, 0, mNumSamples);
            mEventTimeNanoSamples = newEventTimeNanoSamples;
        }
        
        int nextDataSampleIndex = mLastDataSampleIndex + dataSampleStride;
        if (nextDataSampleIndex + dataSampleStride > mDataSamples.length) {
            float[] newDataSamples = new float[nextDataSampleIndex
                                               + BASE_AVAIL_SAMPLES * dataSampleStride];
            System.arraycopy(mDataSamples, 0, newDataSamples, 0, nextDataSampleIndex);
            mDataSamples = newDataSamples;
        }
        
        mLastEventTimeNanoSampleIndex = mNumSamples;
        mLastDataSampleIndex = nextDataSampleIndex;
        mNumSamples += 1;
    }
	
    private final void setPointerCoordsAtSampleIndex(int sampleIndex,
            PointerCoords[] pointerCoords) {
        final int numPointers = mNumPointers;
        for (int i = 0; i < numPointers; i++) {
            setPointerCoordsAtSampleIndex(sampleIndex, pointerCoords[i]);
            sampleIndex += NUM_SAMPLE_DATA;
        }
    }
    
    private final void setPointerCoordsAtSampleIndex(int sampleIndex,
            PointerCoords pointerCoords) {
        final float[] dataSamples = mDataSamples;
        dataSamples[sampleIndex + SAMPLE_X] = pointerCoords.x - mXOffset;
        dataSamples[sampleIndex + SAMPLE_Y] = pointerCoords.y - mYOffset;
        dataSamples[sampleIndex + SAMPLE_PRESSURE] = pointerCoords.pressure;
        dataSamples[sampleIndex + SAMPLE_SIZE] = pointerCoords.size;
        dataSamples[sampleIndex + SAMPLE_TOUCH_MAJOR] = pointerCoords.touchMajor;
        dataSamples[sampleIndex + SAMPLE_TOUCH_MINOR] = pointerCoords.touchMinor;
        dataSamples[sampleIndex + SAMPLE_TOOL_MAJOR] = pointerCoords.toolMajor;
        dataSamples[sampleIndex + SAMPLE_TOOL_MINOR] = pointerCoords.toolMinor;
        dataSamples[sampleIndex + SAMPLE_ORIENTATION] = pointerCoords.orientation;
    }
    
    private final void setPointerCoordsAtSampleIndex(int sampleIndex,
            float x, float y, float pressure, float size) {
        final float[] dataSamples = mDataSamples;
        dataSamples[sampleIndex + SAMPLE_X] = x - mXOffset;
        dataSamples[sampleIndex + SAMPLE_Y] = y - mYOffset;
        dataSamples[sampleIndex + SAMPLE_PRESSURE] = pressure;
        dataSamples[sampleIndex + SAMPLE_SIZE] = size;
        dataSamples[sampleIndex + SAMPLE_TOUCH_MAJOR] = pressure;
        dataSamples[sampleIndex + SAMPLE_TOUCH_MINOR] = pressure;
        dataSamples[sampleIndex + SAMPLE_TOOL_MAJOR] = size;
        dataSamples[sampleIndex + SAMPLE_TOOL_MINOR] = size;
        dataSamples[sampleIndex + SAMPLE_ORIENTATION] = 0;
    }
    
	/**
	 * Given a pointer identifier, find the index of its data in the event.
	 * 
	 * @param pointerId
	 *            The identifier of the pointer to be found.
	 * @return Returns either the index of the pointer (for use with
	 *         {@link #getX(int)} et al.), or -1 if there is no data available
	 *         for that pointer identifier.
	 */
	public final int findPointerIndex(int pointerId) {
		int i = mNumPointers;
		while (i > 0) {
			i--;
			if (mPointerIdentifiers[i] == pointerId) {
				return i;
			}
		}
		return -1;
	}
    
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("action : ");
		switch (mAction) {
		case ACTION_DOWN:
			builder.append("down");
			break;
		case ACTION_MOVE:
			builder.append("move");
			break;
		case ACTION_UP:
			builder.append("up");
			break;
		case ACTION_WHEEL:
			builder.append("wheel");
			break;
		case ACTION_CLICK:
			builder.append("click");
			break;
		}
		return builder.toString();
	}
	
	/**
	 * Transfer object for pointer coordinates.
	 * 
	 * Objects of this type can be used to manufacture new {@link MotionEvent2}
	 * objects and to query pointer coordinate information in bulk.
	 * 
	 * Refer to {@link InputDevice} for information about how different kinds of
	 * input devices and sources represent pointer coordinates.
	 */
	public static final class PointerCoords {
		/**
		 * The X coordinate of the pointer movement. The interpretation varies
		 * by input source and may represent the position of the center of the
		 * contact area, a relative displacement in device-specific units or
		 * something else.
		 */
		public float x;

		/**
		 * The Y coordinate of the pointer movement. The interpretation varies
		 * by input source and may represent the position of the center of the
		 * contact area, a relative displacement in device-specific units or
		 * something else.
		 */
		public float y;

		/**
		 * A scaled value that describes the pressure applied to the pointer.
		 * The pressure generally ranges from 0 (no pressure at all) to 1
		 * (normal pressure), however values higher than 1 may be generated
		 * depending on the calibration of the input device.
		 */
		public float pressure;

		/**
		 * A scaled value of the approximate size of the pointer touch area.
		 * This represents some approximation of the area of the screen being
		 * pressed; the actual value in pixels corresponding to the touch is
		 * normalized with the device specific range of values and scaled to a
		 * value between 0 and 1. The value of size can be used to determine fat
		 * touch events.
		 */
		public float size;

		/**
		 * The length of the major axis of an ellipse that describes the touch
		 * area at the point of contact.
		 */
		public float touchMajor;

		/**
		 * The length of the minor axis of an ellipse that describes the touch
		 * area at the point of contact.
		 */
		public float touchMinor;

		/**
		 * The length of the major axis of an ellipse that describes the size of
		 * the approaching tool. The tool area represents the estimated size of
		 * the finger or pen that is touching the device independent of its
		 * actual touch area at the point of contact.
		 */
		public float toolMajor;

		/**
		 * The length of the minor axis of an ellipse that describes the size of
		 * the approaching tool. The tool area represents the estimated size of
		 * the finger or pen that is touching the device independent of its
		 * actual touch area at the point of contact.
		 */
		public float toolMinor;

		/**
		 * The orientation of the touch area and tool area in radians clockwise
		 * from vertical. An angle of 0 degrees indicates that the major axis of
		 * contact is oriented upwards, is perfectly circular or is of unknown
		 * orientation. A positive angle indicates that the major axis of
		 * contact is oriented to the right. A negative angle indicates that the
		 * major axis of contact is oriented to the left. The full range is from
		 * -PI/2 radians (finger pointing fully left) to PI/2 radians (finger
		 * pointing fully right).
		 */
		public float orientation;

		/*
		 * private static final float PI_4 = (float) (Math.PI / 4);
		 * 
		 * public float getTouchWidth() { return Math.abs(orientation) > PI_4 ?
		 * touchMajor : touchMinor; }
		 * 
		 * public float getTouchHeight() { return Math.abs(orientation) > PI_4 ?
		 * touchMinor : touchMajor; }
		 * 
		 * public float getToolWidth() { return Math.abs(orientation) > PI_4 ?
		 * toolMajor : toolMinor; }
		 * 
		 * public float getToolHeight() { return Math.abs(orientation) > PI_4 ?
		 * toolMinor : toolMajor; }
		 */
	}
}
