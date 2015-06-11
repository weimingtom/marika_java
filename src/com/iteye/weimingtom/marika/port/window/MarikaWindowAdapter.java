package com.iteye.weimingtom.marika.port.window;

import com.iteye.weimingtom.marika.model.MarikaPoint;

public class MarikaWindowAdapter {
	private MarikaWindow mWindow;
	
	public void setWindow(MarikaWindow window) {
		this.mWindow = window;
	}

	public MarikaWindow getWindow() {
		return this.mWindow;
	}
	
	protected void onLButtonUp(MarikaPoint point) {
		
	}
	
	protected void onLButtonDown(MarikaPoint point) {
		
	}

	protected void onRButtonUp(MarikaPoint point) {
		
	}

	protected void onMouseMove(MarikaPoint point) {
		
	}

	protected boolean onIdle(int count) {
		return false;
	}
	
	protected boolean onCreate() {
		return true;
	}
	
	/**
	 * FIXME:unnecessary
	 */
	protected void onPaint() {
		
	}
	
	protected void onDestroy() {
		
	}
	
	protected void onKeyDown(int key) {
		
	}
}
