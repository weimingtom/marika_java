package com.iteye.weimingtom.marika.port.test;

import com.iteye.weimingtom.marika.port.audio.MarikaWaveOut;
import com.iteye.weimingtom.marika.port.audio.GTGEMidiAudio;
import com.iteye.weimingtom.marika.port.audio.GTGEWaveAudio;
import com.iteye.weimingtom.marika.port.file.MarikaResource;
import com.iteye.weimingtom.marika.port.window.MarikaWindow;
import com.iteye.weimingtom.marika.port.window.MarikaWindowAdapter;

public class MarikaWaveOutTest {
	private static class CTestWindow extends MarikaWindowAdapter {
		private MarikaResource res = new MarikaResource();

		public GTGEMidiAudio bsMusic;
		public GTGEWaveAudio bsSound;

		@Override
		public boolean onCreate() {
			res.init(getWindow());
			init();
			
			testWaveOut();
			return super.onCreate();
		}
		
		
		private void testWaveOut() {
			if (false) {
				MarikaWaveOut wave = new MarikaWaveOut(res);
				wave.play2("wave/a5_04108.wav");
			} else {
				//playSound("assets/music/sound1.wav");
				playMusic("assets/music/music1.mid");
			}
		}
		
		public void init() {
			if (this.bsMusic == null) {
			    this.bsMusic = new GTGEMidiAudio();
			    this.bsMusic.setExclusive(true);
			    this.bsMusic.setLoop(true);
			}
			if (this.bsSound == null) {
			    this.bsSound = new GTGEWaveAudio();
			}
		}
	    public int playMusic(String audiofile) {
	    	return this.bsMusic.play(audiofile);
	    }
	    public int playSound(String audiofile) {
	    	return this.bsSound.play(audiofile);
	    }
		
		@Override
		public boolean onIdle(int count) {
			return super.onIdle(count);
		}

		@Override
		protected void onDestroy() {
			super.onDestroy();
			res.unloadAll();
		}
	}
	
	public static final void main(String[] args) {
		MarikaWindow win = new MarikaWindow();
		win.setAdapter(new CTestWindow());
		win.start();
	}
}
