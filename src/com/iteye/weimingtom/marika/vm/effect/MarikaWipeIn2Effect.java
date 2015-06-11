package com.iteye.weimingtom.marika.vm.effect;

import com.iteye.weimingtom.marika.model.MarikaRectangle;
import com.iteye.weimingtom.marika.port.image.MarikaImage;
import com.iteye.weimingtom.marika.vm.window.MarikaMainWin;

public class MarikaWipeIn2Effect extends MarikaEffect {
	public MarikaWipeIn2Effect(MarikaMainWin win, MarikaImage dst, MarikaImage src, MarikaRectangle rect) {
		super(win, 1000 / 20, dst, src, rect);
	}
	
	@Override 
	public boolean step() {
		boolean result = Dst.wipeIn2(Src, EffectRect, EffectCnt++);
		Window.repaintView(EffectRect);
		return result;
	}
}
