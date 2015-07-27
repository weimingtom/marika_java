package com.iteye.weimingtom.marika.port.audio;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * 
 * http://www20.atpages.jp/daimeimitei/
 *
 * OGGBGMクラス
 * VorbisSPIを使ってOGGのBGMを簡単に再生します。
 * 使い方
 * インスタンスを作らずスタティックの関数で再生できます。
 * Oggbgm.play(String filename);//で繰り返し再生
 * Oggbgm.play1(String filename);//で一回再生
 * Oggbgm.play(String filename0, String filename1);//で前奏と繰り返し部に分けて繰り返し再生
 * Oggbgm.pausing=true;//で一時停止
 * Oggbgm.stopping=true;//で停止
 * Oggbgm.vol=0.5f;で音量を半分にする
 * 
 * @see KAS4PC, net.studiomikan.kas4pc.klib.Oggbgm
 * @author Administrator
 *
 */
public class KASOggbgm implements Runnable {
	public InputStream istream = null;
	public boolean loop;
	public float vol = 1.0f;
	public boolean pausing;
	public boolean stopping;
	private AudioInputStream ais = null;
	private AudioFormat af = null;
	private SourceDataLine sdl = null;
	private byte[] buf = new byte[16384];
	private OnCompletionListener onComp = null;

	public interface OnCompletionListener {
		public void onCompletion(KASOggbgm p);
	}
	
	public boolean loadSound(InputStream is) {
		this.istream = is;
		return setstart(is, true, this);
	}

	public void playOgg() {
		if (sdl != null) {
			new Thread(this, "MarikaWaveOut").start();
		}
	}

	public void stopOgg() {
		stopping = true;
	}

	public void pauseOgg() {
		pausing = true;
	}
	
	public void restartOgg() {
		pausing = false;
	}

	public void setVolume(float volume) {
		if (volume < 0f) {
			volume = 0;
		} else if (volume > 1f) {
			volume = 1;
		}
		this.vol = volume;
	}

	public void setOnCompletionListener(OnCompletionListener listner) {
		this.onComp = listner;
	}

	@Override
	public void run() {
		playn();
	}

	private void playn() {
		do {
			int bbytes = 0;
			sdl.start();
			try{
				while (bbytes != -1 && !stopping) {
					if (!pausing) {
						bbytes = ais.read(buf, 0, buf.length);
						if (vol != 1.0f) {
							for(int b = 0; b < buf.length; b++) {
								buf[b] *= vol;
							}
						}
						if (bbytes != -1) {
							sdl.write(buf, 0, bbytes);
						}
					} else {
						try {
							Thread.sleep(100);
						} catch(InterruptedException e){
							e.printStackTrace();
						}
					}
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
			if (stopping) {
				break;
			}
			if (loop) {
				setstart(this.istream, false, this);
			}
		} while(loop);
		sdl.drain();
		sdl.close();
		try {
			ais.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		if (onComp != null) {
			onComp.onCompletion(this);
		}
	}

	private static synchronized boolean setstart(InputStream is, 
		boolean setinfoline, KASOggbgm obj) {
		try {
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(is);
			AudioFormat format = audioStream.getFormat();
			obj.af = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					format.getSampleRate(),
					16,
					format.getChannels(),
					format.getChannels() * 2,
					format.getSampleRate(),
					false);
			obj.ais = AudioSystem.getAudioInputStream(obj.af, audioStream);
			if (setinfoline) {
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, obj.af);
				try {
					obj.sdl = (SourceDataLine)AudioSystem.getLine(info);
					obj.sdl.open(obj.af, obj.buf.length);
				} catch (LineUnavailableException e){
					e.printStackTrace();
					return false;
				}
			}
		} catch (UnsupportedAudioFileException e1){
			e1.printStackTrace();
			return false;
		} catch (IOException e1){
			e1.printStackTrace();
			return false;
		}
		return true;
	}
}
