package com.iteye.weimingtom.marika.vm.ui;

import com.iteye.weimingtom.marika.model.MarikaRectangle;
import com.iteye.weimingtom.marika.port.file.MarikaFile;
import com.iteye.weimingtom.marika.port.image.MarikaFont;
import com.iteye.weimingtom.marika.port.image.MarikaImage;
import com.iteye.weimingtom.marika.port.window.MarikaWindow;

public interface IImage {
	boolean create(int width, int height);
	boolean loadFile(MarikaFile file, int ox, int oy);
	int getWidth();
	int getHeight();
	void clear();
	boolean loadImage(String name, int ox, int oy);
	void fillRect(MarikaRectangle rect, int color);
	void copy(MarikaImage image, MarikaRectangle rect);
	void mixImage(MarikaImage image, MarikaRectangle rect, int trans_color);
	void drawRect(MarikaRectangle rect, int color);
	void fillHalfToneRect(MarikaRectangle rect);
	void drawFrameRect(MarikaRectangle rect, int color);
	void drawText(MarikaFont hFont, int x1, int y1, String str, int color);
	void wipeIn(MarikaImage image, MarikaRectangle rect, int count);
	void wipeOut(MarikaRectangle rect, int count);
	boolean wipeIn2(MarikaImage image, MarikaRectangle rect, int count);
	boolean wipeOut2(MarikaRectangle rect, int count);
	void fadeCvt(MarikaImage image, MarikaRectangle rect, int[] cvt);
	void fadeFromBlack(MarikaImage image, MarikaRectangle rect, int count);
	void fadeToBlack(MarikaImage image, MarikaRectangle rect, int count);
	void fadeFromWhite(MarikaImage image, MarikaRectangle rect, int count);
	void fadeToWhite(MarikaImage image, MarikaRectangle rect, int count);
	void mix(MarikaImage image, MarikaRectangle rect, int count);
}
