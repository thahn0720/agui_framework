package thahn.java.agui.media;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import thahn.java.agui.app.Context;


/**
 * 
 * @author thAhn
 *
 */
public class MediaPlayer {
    private final static String 														TAG = "MediaPlayer";

    private AudioInputStream 															mIn;
	private AudioInputStream 															mDin;
	private SourceDataLine 																mLine;
	private String																		mDataPath;
	private AudioFormat																	mDecodedFormat;					 
	private MediaTask																	mTask;
	private boolean																		mStart		= false;
	private boolean																		mPause		= false;
	private boolean																		mStop		= false;
	private OnCompletionListener														mOnCompletionListener;
	
    /**
     * Interface definition for a callback to be invoked when playback of
     * a media source has completed.
     */
    public interface OnCompletionListener {
        /**
         * Called when the end of a media source is reached during playback.
         *
         * @param mp the MediaPlayer that reached the end of the file
         */
        void onCompletion(MediaPlayer mp);
    }
	
	public MediaPlayer() {
	}
	
	public static MediaPlayer create(Context context, int rawid) {
		try {
			MediaPlayer mp = new MediaPlayer();
			mp.setDataSource(context.getResources().openRawResourcePath(rawid));
			mp.prepare();
			return mp;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void setDataSource(String path) {
		mDataPath = path;
	}

	public void prepare() throws IOException, IllegalStateException {
		try {
			if(mDataPath == null) {
				throw new NullPointerException("data path is null");
			}
			File file = new File(mDataPath);
			if(!file.exists()) {
				throw new FileNotFoundException(file.getPath() + " does not exist");
			}
			mIn = AudioSystem.getAudioInputStream(file);
			AudioFormat baseFormat = mIn.getFormat();
			mDecodedFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
					baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
					false);
			mDin = AudioSystem.getAudioInputStream(mDecodedFormat, mIn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start() throws IllegalStateException {
		try {
			mStart = true;
			
			if(mStop) {
				prepare();
				mTask = new MediaTask();
				mTask.start();
			} else if(mPause) {
				synchronized (mTask) {
					mTask.notify();
				}
			} else {
				mTask = new MediaTask();
				mTask.start();
			}
			mPause = false;
			mStop = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void pause() {
		mStart = false;
		mPause = true;
		mStop = false;
//		try { mTask.wait(); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	public void stop() {
		try {
			mStart = false;
			mPause = false;
			mStop = true;
			if(mTask != null) {
				mTask.interrupt();
			}
			if(mLine != null) {
				mLine.drain();
				mLine.stop();
				mLine.close();
			}
			if(mDin != null) {
				mDin.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void release() {
		
	}
	
	public void reset() {
		try {
			if(mDataPath != null) {
				prepare();
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	public boolean isPlaying() {
		return mStart;
	}
	
    /**
     * Seeks to specified time position.
     *
     * @param msec the offset in milliseconds from the start to seek to
     * @throws IllegalStateException if the internal player engine has not been
     * initialized
     */
    public void seekTo(int msec) throws IllegalStateException {
    	if(mDin != null) {
    		try {
				mDin.skip(msec);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }

    /**
     * Gets the current playback position.
     *
     * @return the current position in milliseconds
     */
    public int getCurrentPosition() {
    	int ret = 0;
    	if(mLine != null) {
    		ret = mLine.getFramePosition();
    	}
    	return ret;
    }

    /**
     * Gets the duration of the file.
     *
     * @return the duration in milliseconds, if no duration is available
     *         (for example, if streaming live content), -1 is returned.
     */
    public int getDuration() {
    	int ret = -1;
    	if(mDin != null) {
    		ret = (int) mDin.getFrameLength();
    	}
    	return ret;//mLine.getBufferSize();
    }
	
    /**
     * Register a callback to be invoked when the end of a media source
     * has been reached during playback.
     *
     * @param listener the callback that will be run
     */
    public void setOnCompletionListener(OnCompletionListener listener)
    {
        mOnCompletionListener = listener;
    }

	class MediaTask extends Thread {

		public MediaTask() {
		}

		@Override
		public void run() {
			try {
				// Play now.
				rawplay(mDecodedFormat, mDin);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void rawplay(AudioFormat targetFormat, AudioInputStream din) throws IOException, LineUnavailableException {
			byte[] data = new byte[4096];
			mLine = getLine(targetFormat);
			if (mLine != null) {
				// Start
				mLine.start();
				int nBytesRead = 0;
				int nBytesWritten = 0;
				while (nBytesRead != -1) {
					nBytesRead = din.read(data, 0, data.length);
					if(nBytesRead != -1) nBytesWritten = mLine.write(data, 0, nBytesRead);
					if(mPause) {
						synchronized (this) {
							try { this.wait(); } catch (InterruptedException e) { }
						}
					}
					if(mStop) {
						return ;
					}
				}
				
				if(mOnCompletionListener != null) mOnCompletionListener.onCompletion(MediaPlayer.this);
			}
		}

		private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException {
			SourceDataLine res = null;
			DataLine.Info info = new DataLine.Info(SourceDataLine.class,
					audioFormat);
			res = (SourceDataLine) AudioSystem.getLine(info);
			res.open(audioFormat);
			return res;
		}
	}
}

