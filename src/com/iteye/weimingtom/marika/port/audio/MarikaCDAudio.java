package com.iteye.weimingtom.marika.port.audio;

import java.nio.ByteBuffer;

import com.iteye.weimingtom.marika.port.file.MarikaLog;
import com.iteye.weimingtom.marika.port.file.MarikaResource;

public class MarikaCDAudio extends MarikaMci {
	private GTGEMidiAudio audio = new GTGEMidiAudio();
	
	public MarikaCDAudio(MarikaResource res) {
		super(res);
	}
	
	@Override
	public boolean play(int no) {
		String str = "music/music1.mid";
		this.mRes.loadData(str, str);
		ByteBuffer buffer = this.mRes.dataMap.get(str);
		if (buffer != null) {
			byte[] bytes = buffer.array();
			MarikaLog.trace("MarikaCDAudio.play length: " + bytes.length);
			audio.play("assets/" + str);
		} else {
			MarikaLog.trace("!!!!!  MarikaCDAudio.play asset not exists !!!!! ");
		}
		return false;
	}
	
	@Override
	public boolean stop() {
		audio.stop();
		return false;
	}
}
