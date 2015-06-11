package com.iteye.weimingtom.marika.vm.action;

import com.iteye.weimingtom.marika.model.MarikaPoint;
import com.iteye.weimingtom.marika.vm.window.MarikaMainWin;

public class MarikaAction {
	private void error() {
		try {
			throw new RuntimeException("abstract function");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected boolean scriptRunning;
	protected MarikaMainWin mParent;
	protected int mParam1;
	protected int mParam2;
	
	public boolean isScriptRunning() { 
		return scriptRunning; 
	}
	
	public MarikaAction() {
		this(false);
	}
	
	public MarikaAction(boolean scriptRun) {
		scriptRunning = scriptRun;
	}
	
	public void initialize(MarikaMainWin parent) {
		initialize(parent, 0, 0);
	}
	
	public void initialize(MarikaMainWin parent, int param1) {
		initialize(parent, param1, 0);
	}	
	
	public void initialize(MarikaMainWin parent, int param1, int param2) {
		this.mParent = parent;
		this.mParam1 = param1;
		this.mParam2 = param2;
	}
	
	public void onActionPause() {
		error();
	}
	
	public void onActionResume() {
		error();
	}
	
	public void onActionLButtonDown(MarikaPoint point) {
		error();
	}
	
	public void onActionLButtonUp(MarikaPoint point) {
		error();
	}
	
	public void onActionRButtonDown(MarikaPoint point) {
		error();
	}
	
	public void onActionRButtonUp(MarikaPoint point) {
		error();
	}
	
	public void onActionMouseMove(MarikaPoint point) {
		error();
	}
	
	public void onActionKeyDown(int key) {
		error();
	}
	
	public void onActionTimedOut(int timerId) {
		error();
	}

	public boolean onActionIdleAction() {
		error();
		return false;
	}

	public void onActionMusicDone(int music) {
		error();
	}
	
	public void onActionWipeDone() {
		error();
	}
	
	public void onActionWaveDone() {
		error();
	}
}
