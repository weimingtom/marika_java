package com.iteye.weimingtom.marika.vm.effect;

import com.iteye.weimingtom.marika.port.image.MarikaImage;
import com.iteye.weimingtom.marika.vm.window.MarikaMainWin;

public class MarikaFadeOutEffect extends MarikaEffect {
	private static final int STEP = 16;

	public MarikaFadeOutEffect(MarikaMainWin win, MarikaImage dst, MarikaImage src) {
		super(win, 1000 / STEP, dst, src);
	}
	
	@Override 
	public boolean step() {
		Dst.fadeToBlack(Src, EffectRect, EffectCnt);
		Window.repaintView(EffectRect);
		if (++EffectCnt >= STEP)
			return false;
		return true;
	}
}
