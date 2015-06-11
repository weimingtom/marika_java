package com.iteye.weimingtom.marika.vm.effect;

import com.iteye.weimingtom.marika.model.MarikaConfig;
import com.iteye.weimingtom.marika.model.MarikaRectangle;
import com.iteye.weimingtom.marika.port.image.MarikaImage;
import com.iteye.weimingtom.marika.vm.window.MarikaMainWin;

public abstract class MarikaEffect {
	private static final MarikaRectangle default_rect = new MarikaRectangle(0, 0, MarikaConfig.WindowWidth, MarikaConfig.WindowHeight);
	
	protected MarikaMainWin Window;
	protected MarikaImage Dst;
	protected MarikaImage Src;

	protected int TimeBase;
	protected int EffectCnt;
	protected MarikaRectangle EffectRect;
	protected long lastTime;
	
	public abstract boolean step();

	public MarikaEffect(MarikaMainWin win, int step, MarikaImage dst) {
		this(win, step, dst, null, MarikaEffect.default_rect);
	}
	
	public MarikaEffect(MarikaMainWin win, int step, MarikaImage dst, MarikaImage src) {
		this(win, step, dst, src, MarikaEffect.default_rect);
	}
	
	public MarikaEffect(MarikaMainWin win, int step, MarikaImage dst, MarikaImage src, MarikaRectangle rect) {
		Window = win;
		Dst = dst;
		Src = src;
		TimeBase = step;
		//FXIME:???
		EffectRect = rect.cloneRect(); 
		EffectCnt = 0;
		lastTime = 0;
	}
	
	public boolean step2(long time) {
		if (TimeBase <= time - lastTime) {
			lastTime = time;
			return step();
		}
		return true;
	}
}
