package com.iteye.weimingtom.marika.port.test;

import com.iteye.weimingtom.marika.port.file.MarikaLog;
import com.iteye.weimingtom.marika.port.file.MarikaResource;

public class MarikaResourceTest {
	public static final void main(String[] args) {
		MarikaResource res = new MarikaResource();
		res.init(null);
		res.loadClassText("main.txt", res.getClass(), "main.txt");
		MarikaLog.trace(res.textMap.get("main.txt"));
		res.unloadAll();
	}
}
