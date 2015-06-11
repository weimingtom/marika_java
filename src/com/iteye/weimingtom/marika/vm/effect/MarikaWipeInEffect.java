package com.iteye.weimingtom.marika.vm.effect;

import com.iteye.weimingtom.marika.model.MarikaRectangle;
import com.iteye.weimingtom.marika.port.image.MarikaImage;
import com.iteye.weimingtom.marika.vm.window.MarikaMainWin;

public class MarikaWipeInEffect extends MarikaEffect {
	public MarikaWipeInEffect(MarikaMainWin win, MarikaImage dst, MarikaImage src, MarikaRectangle rect) {
		super(win, 1000 / 8, dst, src, rect);
	}
	
	@Override 
	public boolean step() {
		Dst.wipeIn(Src, EffectRect, EffectCnt);
		Window.repaintView(EffectRect);
		if (++EffectCnt >= 8) {
			return false;
		}
		return true;
	}
}
