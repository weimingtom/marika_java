package com.iteye.weimingtom.marika.vm.effect;

import com.iteye.weimingtom.marika.port.image.MarikaImage;
import com.iteye.weimingtom.marika.vm.window.MarikaMainWin;

public class MarikaShakeEffect extends MarikaEffect {
	public MarikaShakeEffect(MarikaMainWin win, MarikaImage dst) {
		super(win, 1000 / 24, dst);
	}
	
	@Override 
	public boolean step() {
		return false;
	}
}