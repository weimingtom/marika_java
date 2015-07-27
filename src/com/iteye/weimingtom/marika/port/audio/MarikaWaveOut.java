package com.iteye.weimingtom.marika.port.audio;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

import com.iteye.weimingtom.marika.port.file.MarikaLog;
import com.iteye.weimingtom.marika.port.file.MarikaResource;

public class MarikaWaveOut extends MarikaMci {
	private KASOggbgm bgm = new KASOggbgm();
	private GTGEWaveAudio audio = new GTGEWaveAudio();
	
	public MarikaWaveOut(MarikaResource res) {
		super(res);
	}

	@Override
	public boolean play2(String str) {
		this.mRes.loadData(str, str);
		ByteBuffer buffer = this.mRes.dataMap.get(str);
		if (buffer != null) {
			ByteArrayInputStream instr = 
				new ByteArrayInputStream(buffer.array());
			if (false) {
				bgm.loadSound(instr);
				bgm.playOgg();
			} else {
//				audio.play(str, instr);
				audio.play("assets/" + str);
			}
		} else {
			MarikaLog.trace("!!!!!  MarikaWaveOut.play2 asset not exists !!!!! ");
		}
		return false;
	}
}
