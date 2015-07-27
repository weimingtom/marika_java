package com.iteye.weimingtom.marika.vm.effect;

import com.iteye.weimingtom.marika.model.MarikaConfig;
import com.iteye.weimingtom.marika.model.MarikaRectangle;
import com.iteye.weimingtom.marika.port.image.MarikaImage;
import com.iteye.weimingtom.marika.vm.window.MarikaMainWin;

public class MarikaShakeEffect extends MarikaEffect {
	public MarikaShakeEffect(MarikaMainWin win, MarikaImage dst) {
		super(win, 1000 / 24, dst);
	}
	
	@Override 
	public boolean step() {
		int x, y, w, h, ox, oy;
		MarikaRectangle rect = new MarikaRectangle(0, 0, 0, 0);

		switch (EffectCnt) {
		case 0:
		case 2:
			x = 0;
			y = 0;
			w = MarikaConfig.WindowWidth;
			h = MarikaConfig.WindowHeight - 10;
			ox = 0;
			oy = 10;
			rect.setRect(0, MarikaConfig.WindowHeight - 10, 
				MarikaConfig.WindowWidth, MarikaConfig.WindowHeight);
			break;

		case 4:
			x = 0;
			y = 0;
			w = MarikaConfig.WindowWidth;
			h = MarikaConfig.WindowHeight;
			ox = 0;
			oy = 0;
			rect.setRect(0, 0, 0, 0);
			break;

	    case 1:
	    case 3:
			x = 0;
			y = 10;
			w = MarikaConfig.WindowWidth;
			h = MarikaConfig.WindowHeight - 10;
			ox = 0;
			oy = 0;
			rect.setRect(0, 0, MarikaConfig.WindowWidth, 10);
			break;

		default:
			return false;
		}
		Window.getWindow().draw(Dst, x, y, w, h, ox, oy);
		if (x != ox || y != oy) {
			Window.getWindow().extTextOutOpaque(0x000000, rect);
		}
		
		if (++EffectCnt >= 5)
			return false;
		return true;
	}
}
