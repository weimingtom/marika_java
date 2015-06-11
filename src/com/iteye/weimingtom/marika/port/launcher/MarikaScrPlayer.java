package com.iteye.weimingtom.marika.port.launcher;

import com.iteye.weimingtom.marika.port.window.MarikaWindow;
import com.iteye.weimingtom.marika.vm.window.MarikaMainWin;

public class MarikaScrPlayer {
	public static void main(String[] args) {
		MarikaWindow win = new MarikaWindow();
		win.setAdapter(new MarikaMainWin());
		win.start();
	}
}
