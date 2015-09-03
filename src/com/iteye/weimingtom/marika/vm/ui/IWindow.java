package com.iteye.weimingtom.marika.vm.ui;

import com.iteye.weimingtom.marika.model.MarikaPoint;
import com.iteye.weimingtom.marika.model.MarikaRectangle;
import com.iteye.weimingtom.marika.port.image.MarikaImage;

public interface IWindow {
	void onLButtonUp(MarikaPoint point);
	void onLButtonDown(MarikaPoint point);
	void onRButtonUp(MarikaPoint point);
	void onRButtonDown(MarikaPoint point);
	void onMouseMove(MarikaPoint point);
	boolean onIdle(int count);
	boolean onCreate();
	void onPaint();
	void onDestroy();
	void onKeyDown(int key);
	void messageBox(String str);
	void draw(MarikaImage image, MarikaRectangle rect);
	MarikaPoint getCursorPos();
	void screenToClient(MarikaPoint point);
	void setTimer(int idTimer, int uTimeout);
}
