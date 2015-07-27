package com.iteye.weimingtom.marika.port.audio;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.Mixer;

/**
 * @see GTGE
 * @see http://code.google.com/p/gtge/
 */
public class GTGEWaveAudio implements Runnable, LineListener{
	private int maxSimultaneous;

	private String rendererFile;
	private String lastAudioFile;
	private boolean exclusive;
	private boolean loop;
	private float volume;
	private boolean active;

	public GTGEWaveAudio() {
		initRenderer();
		
		this.active = isAvailable();
		this.volume = 1.0f;
		this.maxSimultaneous = 6;
		this.rendererFile = null;
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(100L);
			} catch (InterruptedException e) {

			}
			if (this.isLoop() && 
				this.getStatus() == END_OF_SOUND) {
				this.play();
			}
		}
	}
	
	public int play(String audiofile) {
		this.lastAudioFile = audiofile;
		if (!this.active) {
			return -1;
		}
		int playedSound = 0;
		if (this.rendererFile != null && this.rendererFile.equals(audiofile)) {
			if (this.getStatus() == PLAYING) {
				playedSound++;
			}
			if (!this.exclusive) {
				if (this.getStatus() != PLAYING) {
					this.setVolume(this.volume);
					this.play();
					return 0;
				}
			} else {
				if (this.exclusive) {
					this.stopAll();
				}
				if (this.getStatus() != PLAYING) {
					this.setVolume(this.volume);
					this.play();
				}
				return 0;
			}
		}
		if (playedSound >= this.maxSimultaneous) {
			return -1;
		}
		this.setLoop(this.loop);
		if (this.exclusive) {
			this.stopAll();
		} else {
			this.stop();
		}
		this.setVolume(this.volume);
		this.play(getURL(audiofile));
		this.rendererFile = audiofile;
		return 1;
	}

	public int play(String audiofile, InputStream istr) {
		this.lastAudioFile = audiofile;
		if (!this.active) {
			return -1;
		}
		int playedSound = 0;
		if (this.rendererFile != null && this.rendererFile.equals(audiofile)) {
			if (this.getStatus() == PLAYING) {
				playedSound++;
			}
			if (!this.exclusive) {
				if (this.getStatus() != PLAYING) {
					this.setVolume(this.volume);
					this.play();
					return 0;
				}
			} else {
				if (this.exclusive) {
					this.stopAll();
				}
				if (this.getStatus() != PLAYING) {
					this.setVolume(this.volume);
					this.play();
				}
				return 0;
			}
		}
		if (playedSound >= this.maxSimultaneous) {
			return -1;
		}
		this.setLoop(this.loop);
		if (this.exclusive) {
			this.stopAll();
		} else {
			this.stop();
		}
		this.setVolume(this.volume);
		this.play(istr);
		this.rendererFile = audiofile;
		return 1;
	}

	
	private URL getURL(String path) {
		URL url = null;
		try {
			File f = new File(path);
			if (f.exists()) {
				url = f.toURL();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (url == null) {
			String str = "Resource not found (" + this + "): " + 
				System.getProperty("user.dir") + 
				File.separator + path;
			throw new RuntimeException(str);
		}
		return url;
	}
	
	public void stop() {
		if (this.getStatus() == PLAYING) {
			if (this.audiofile != null && 
				this.status == PLAYING) {
				this.status = STOPPED;
				this.stopSound();
			}
		}
	}

	public void stopAll() {
		this.stop();
	}

	public String getLastAudioFile() {
		return this.lastAudioFile;
	}

	public float getVolume() {
		return this.volume;
	}

	public void setVolume(float volume) {
		if (volume < 0.0f) {
			volume = 0.0f;
		}
		if (volume > 1.0f) {
			volume = 1.0f;
		}
		if (this.isVolumeSupported() == false || 
			this.volume == volume) {
			return;
		}
		this.volume = volume;
		this.setSoundVolume(volume);
	}

	public int getMaxSimultaneous() {
		return this.maxSimultaneous;
	}

	public void setMaxSimultaneous(int i) {
		this.maxSimultaneous = i;
	}

	public boolean isExclusive() {
		return this.exclusive;
	}

	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;

		if (exclusive) {
			this.stopAll();
		}
	}
	
	public boolean isLoop() {
		return this.loop;
	}

	public void setLoop(boolean loop) {
		if (this.loop == loop) {
			return;
		}
		this.loop = loop;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean b) {
		this.active = (this.isAvailable()) ? b : false;
		if (!this.active) {
			this.stopAll();
		}
	}
	
	//-----------------------
	
	public static final int PLAYING = 1;
	public static final int STOPPED = 2;
	public static final int END_OF_SOUND = 3;
	public static final int ERROR = 4;

	private URL audiofile;
//	private boolean loop;
	protected int status;
//	protected float volume;

	public void play(URL audiofile) {
		this.status = PLAYING;
		if (this.audiofile == audiofile) {
			this.replaySound(audiofile);
			return;
		}
		this.audiofile = audiofile;
		this.playSound(audiofile);
	}

	public void play(InputStream istr) {
		this.status = PLAYING;
//		if (this.audiofile == audiofile) {
//			this.replaySound(audiofile);
//			return;
//		}
//		this.audiofile = audiofile;
		this.playSound(istr);
	}
	
	public void play() {
		this.status = PLAYING;
		this.replaySound(this.audiofile);
	}

	public URL getAudioFile() {
		return this.audiofile;
	}

	public int getStatus() {
		return this.status;
	}
	
	
	private Clip clip;

	private static boolean available;
	private static boolean volumeSupported;
	private static Mixer mixer;
	private static final int UNINITIALIZED = 0;
	private static final int INITIALIZING = 1;
	private static final int INITIALIZED = 2;
	private static int rendererStatus = UNINITIALIZED;

	public void initRenderer() {
		this.volume = 1.0f;
		this.status = STOPPED;
		
		if (rendererStatus == UNINITIALIZED) {
			rendererStatus = INITIALIZING;
			Thread thread = new Thread() {
				@Override
				public final void run() {
					try {
						URL sample = GTGEWaveAudio.class
								.getResource("Sample.wav");
						AudioInputStream ain = AudioSystem
								.getAudioInputStream(sample);
						AudioFormat format = ain.getFormat();
						DataLine.Info info = new DataLine.Info(Clip.class,
								ain.getFormat(),
								((int) ain.getFrameLength() * format
										.getFrameSize()));
						Clip clip = (Clip) AudioSystem.getLine(info);
						clip.open(ain);
						volumeSupported = clip
								.isControlSupported(FloatControl.Type.VOLUME)
								|| clip.isControlSupported(FloatControl.Type.MASTER_GAIN);
						clip.drain();
						clip.close();
						Mixer.Info[] mixers = AudioSystem.getMixerInfo();
						for (int i = 0; i < mixers.length; i++) {
							if ("Java Sound Audio Engine".equals(mixers[i]
									.getName())) {
								mixer = AudioSystem
										.getMixer(mixers[i]);
							}
						}
						available = true;
					} catch (Throwable e) {
						System.err.println("WARNING: Wave audio playback is not available!");
						available = false;
					}
					rendererStatus = INITIALIZED;
				}
			};
			thread.setDaemon(true);
			thread.start();
		}
	}

	public boolean isAvailable() {
		if (rendererStatus != INITIALIZED) {
			int i = 0;
			while (rendererStatus != INITIALIZED
					&& i++ < 50) {
				try {
					Thread.sleep(50L);
				} catch (InterruptedException e) {

				}
			}
			if (rendererStatus != INITIALIZED) {
				rendererStatus = INITIALIZED;
				available = false;
			}
		}
		return available;
	}

	protected void playSound(URL audiofile) {
		try {
			if (this.clip != null) {
				this.clip.drain();
				this.clip.close();
			}
			AudioInputStream ain = AudioSystem.getAudioInputStream(this
					.getAudioFile());
			AudioFormat format = ain.getFormat();
			if ((format.getEncoding() == AudioFormat.Encoding.ULAW)
					|| (format.getEncoding() == AudioFormat.Encoding.ALAW)) {
				AudioFormat temp = new AudioFormat(
						AudioFormat.Encoding.PCM_SIGNED,
						format.getSampleRate(),
						format.getSampleSizeInBits() * 2, format.getChannels(),
						format.getFrameSize() * 2, format.getFrameRate(), true);
				ain = AudioSystem.getAudioInputStream(temp, ain);
				format = temp;
			}
			DataLine.Info info = new DataLine.Info(Clip.class, ain.getFormat(),
					((int) ain.getFrameLength() * format.getFrameSize()));
			if (mixer != null) {
				this.clip = (Clip) mixer.getLine(info);
			} else {
				this.clip = (Clip) AudioSystem.getLine(info);
			}
			this.clip.addLineListener(this);
			this.clip.open(ain);
			this.clip.start();
			if (this.volume != 1.0f) {
				this.setSoundVolume(this.volume);
			}
		} catch (Exception e) {
			this.status = ERROR;
			System.err.println("ERROR: Can not playing " + this.getAudioFile()
					+ " caused by: " + e);
		}
	}

	protected void playSound(InputStream istr) {
		try {
			if (this.clip != null) {
				this.clip.drain();
				this.clip.close();
			}
			AudioInputStream ain = AudioSystem.getAudioInputStream(istr);
			AudioFormat format = ain.getFormat();
			if ((format.getEncoding() == AudioFormat.Encoding.ULAW)
					|| (format.getEncoding() == AudioFormat.Encoding.ALAW)) {
				AudioFormat temp = new AudioFormat(
						AudioFormat.Encoding.PCM_SIGNED,
						format.getSampleRate(),
						format.getSampleSizeInBits() * 2, format.getChannels(),
						format.getFrameSize() * 2, format.getFrameRate(), true);
				ain = AudioSystem.getAudioInputStream(temp, ain);
				format = temp;
			}
			DataLine.Info info = new DataLine.Info(Clip.class, ain.getFormat(),
					((int) ain.getFrameLength() * format.getFrameSize()));
			if (mixer != null) {
				this.clip = (Clip) mixer.getLine(info);
			} else {
				this.clip = (Clip) AudioSystem.getLine(info);
			}
			this.clip.addLineListener(this);
			this.clip.open(ain);
			this.clip.start();
			if (this.volume != 1.0f) {
				this.setSoundVolume(this.volume);
			}
		} catch (Exception e) {
			this.status = ERROR;
			System.err.println("ERROR: Can not playing " + this.getAudioFile()
					+ " caused by: " + e);
		}
	}
	
	protected void replaySound(URL audiofile) {
		this.clip.setMicrosecondPosition(0);
		this.clip.start();
		this.clip.addLineListener(this);
	}

	protected void stopSound() {
		this.clip.stop();
		this.clip.setMicrosecondPosition(0);
		this.clip.removeLineListener(this);
	}

	@Override
	public void update(LineEvent e) {
		if (e.getType() == LineEvent.Type.STOP) {
			this.status = END_OF_SOUND;
			this.clip.stop();
			this.clip.setMicrosecondPosition(0);
			this.clip.removeLineListener(this);
		}
	}

	protected void setSoundVolume(float volume) {
		if (this.clip == null) {
			return;
		}
		Control.Type vol1 = FloatControl.Type.VOLUME;
		Control.Type vol2 = FloatControl.Type.MASTER_GAIN;
		if (this.clip.isControlSupported(vol1)) {
			FloatControl volumeControl = (FloatControl) this.clip
					.getControl(vol1);
			volumeControl.setValue(volume);
		} else if (this.clip.isControlSupported(vol2)) {
			FloatControl gainControl = (FloatControl) this.clip
					.getControl(vol2);
			float dB = (float) (Math.log(((volume == 0.0) ? 0.0001 : volume))
					/ Math.log(10.0) * 20.0);
			gainControl.setValue(dB);
		}
	}

	public boolean isVolumeSupported() {
		return volumeSupported;
	}
}
