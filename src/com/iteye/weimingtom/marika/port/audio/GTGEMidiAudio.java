package com.iteye.weimingtom.marika.port.audio;

import java.io.File;
import java.net.URL;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;

/**
 * @see GTGE
 * @see http://code.google.com/p/gtge/
 */
public class GTGEMidiAudio implements Runnable, MetaEventListener {
	private int maxSimultaneous;

	private String rendererFile;
	private String lastAudioFile;
	private boolean exclusive;
	private boolean loop;
	private float volume;
	private boolean active;

	public GTGEMidiAudio() {
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
		if (this.isVolumeSupported() == false
				|| this.volume == volume) {
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
		this.setLoop(loop);
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
	
	
	private static final int MIDI_EOT_MESSAGE = 47;
	private static final int GAIN_CONTROLLER = 7;

	private Sequencer sequencer;
	private static boolean available;
	private static boolean volumeSupported;

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
						Sequencer sequencer = MidiSystem.getSequencer();
						sequencer.open();
						volumeSupported = (sequencer instanceof Synthesizer);
						sequencer.close();
						available = true;
					} catch (Throwable e) {
						System.err.println("WARNING: Midi audio playback is not available!");
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
			if (this.sequencer == null) {
				this.sequencer = MidiSystem.getSequencer();
				if (!this.sequencer.isOpen()) {
					this.sequencer.open();
				}
			}
			Sequence seq = MidiSystem.getSequence(this.getAudioFile());
			this.sequencer.setSequence(seq);
			this.sequencer.start();
			this.sequencer.addMetaEventListener(this);
			if (this.volume != 1.0f) {
				this.setSoundVolume(this.volume);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.status = ERROR;
		}
	}

	protected void replaySound(URL audiofile) {
		this.sequencer.start();
		this.sequencer.addMetaEventListener(this);
	}

	protected void stopSound() {
		this.sequencer.stop();
		this.sequencer.setMicrosecondPosition(0);
		this.sequencer.removeMetaEventListener(this);
	}

	public void meta(MetaMessage msg) {
		if (msg.getType() == MIDI_EOT_MESSAGE) {
			this.status = END_OF_SOUND;
			this.sequencer.setMicrosecondPosition(0);
			this.sequencer.removeMetaEventListener(this);
		}
	}

	protected void setSoundVolume(float volume) {
		if (this.sequencer == null) {
			return;
		}
		MidiChannel[] channels = ((Synthesizer) this.sequencer).getChannels();
		for (int i = 0; i < channels.length; i++) {
			channels[i].controlChange(GAIN_CONTROLLER,
					(int) (volume * 127));
		}
	}

	public boolean isVolumeSupported() {
		return volumeSupported;
	}
}
