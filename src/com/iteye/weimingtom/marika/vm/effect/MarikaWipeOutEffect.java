package com.iteye.weimingtom.marika.vm.effect;

import com.iteye.weimingtom.marika.port.image.MarikaImage;
import com.iteye.weimingtom.marika.vm.window.MarikaMainWin;

public class MarikaWipeOutEffect extends MarikaEffect {
	public MarikaWipeOutEffect(MarikaMainWin win, MarikaImage dst, MarikaImage src) {
		super(win, 1000 / 8, dst, src);
	}
	
	@Override 
	public boolean step() {
		Dst.wipeOut(EffectRect, EffectCnt);
		Window.repaintView(EffectRect);
		if (++EffectCnt >= 8) {
			return false;
		}
		return true;
	}
}
