package com.iteye.weimingtom.marika.port.audio;

import com.iteye.weimingtom.marika.port.file.MarikaResource;

public class MarikaMci {
	protected MarikaResource mRes;
	
	public MarikaMci(MarikaResource res) {
		this.mRes = res;
	}
	
	public boolean open() {
		return true;
	}
	
	public boolean close() {
		return true;
	}
	
	public boolean play(int no) {
		return false;
	}
	
	public boolean play2(String name) {
		return false;
	}
	
	public boolean replay() {
		return false;
	}
	
	public boolean stop() {
		return false;
	}
}
