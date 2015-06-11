package com.iteye.weimingtom.marika.vm.effect;

import com.iteye.weimingtom.marika.model.MarikaRectangle;
import com.iteye.weimingtom.marika.port.image.MarikaImage;
import com.iteye.weimingtom.marika.vm.window.MarikaMainWin;

public class MarikaMixFadeEffect extends MarikaEffect {
	private MarikaImage mixImage, maskImage;
	
	public MarikaMixFadeEffect(MarikaMainWin win, MarikaImage dst, MarikaImage src, MarikaRectangle rect, MarikaImage mixImage, MarikaImage maskImage) {
		super(win, 1000 / 8, dst, src, rect);
		this.mixImage = mixImage;
		this.maskImage = maskImage;
	}
	
	@Override 
	public boolean step() {
		Dst.mix(Src, EffectRect, EffectCnt, mixImage, maskImage);
		Window.repaintView(EffectRect);
		if (++EffectCnt >= 8)
			return false;
		return true;
	}
}
