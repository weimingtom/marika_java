package com.iteye.weimingtom.marika.vm.effect;

import com.iteye.weimingtom.marika.port.image.MarikaImage;
import com.iteye.weimingtom.marika.vm.window.MarikaMainWin;

public class MarikaWhiteInEffect extends MarikaEffect {
	public MarikaWhiteInEffect(MarikaMainWin win, MarikaImage dst, MarikaImage src) {
		super(win, 1000 / 16, dst, src);
	}
	
	@Override 
	public boolean step() {
		Dst.fadeFromWhite(Src, EffectRect, EffectCnt);
		Window.repaintView(EffectRect);
		if (++EffectCnt >= 16)
			return false;
		return true;
	}
}
