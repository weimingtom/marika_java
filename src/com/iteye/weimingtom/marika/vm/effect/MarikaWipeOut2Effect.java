package com.iteye.weimingtom.marika.vm.effect;

import com.iteye.weimingtom.marika.port.image.MarikaImage;
import com.iteye.weimingtom.marika.vm.window.MarikaMainWin;

public class MarikaWipeOut2Effect extends MarikaEffect {
	public MarikaWipeOut2Effect(MarikaMainWin win, MarikaImage dst, MarikaImage src) {
		super(win, 1000 / 20, dst, src);
	}
	
	@Override 
	public boolean step() {
		boolean result = Dst.wipeOut2(EffectRect, EffectCnt++);
		Window.repaintView(EffectRect);
		return result;
	}
}
