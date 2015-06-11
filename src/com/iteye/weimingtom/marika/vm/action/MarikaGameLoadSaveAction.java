package com.iteye.weimingtom.marika.vm.action;

import com.iteye.weimingtom.marika.model.MarikaConfig;
import com.iteye.weimingtom.marika.model.MarikaKey;
import com.iteye.weimingtom.marika.model.MarikaPoint;
import com.iteye.weimingtom.marika.vm.window.MarikaMainWin;

public abstract class MarikaGameLoadSaveAction extends MarikaAction {
	protected int Selection;
	protected boolean Pressed;
	protected boolean CancelPressed;
	protected int Flags;
	
	public MarikaGameLoadSaveAction() {
		
	}
	
	@Override 
	public void initialize(MarikaMainWin parent, int param1, int param2) {
		super.initialize(parent, param1, param2);
		Selection = -1;
		Pressed = false;
		CancelPressed = false;
		Flags = 0;
	}
	
	@Override
	public void onActionPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActionResume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActionLButtonDown(MarikaPoint point) {
		Pressed = true;
	}

	@Override
	public void onActionLButtonUp(MarikaPoint point) {
		Pressed = false;
		if (Selection >= 0) {
			doLoadSave();
		}
	}

	@Override
	public void onActionRButtonDown(MarikaPoint point) {
		CancelPressed = true;
	}

	@Override
	public void onActionRButtonUp(MarikaPoint point) {
		if (CancelPressed) {
			mParent.cancelLoadSaveMenu(Flags);
		}
	}

	@Override
	public void onActionMouseMove(MarikaPoint point) {
		int sel = mParent.getLoadSaveSelect(point);
		if (sel != Selection) {
			mParent.selectLoadSaveMenu(Selection, false);
			Selection = sel;
			mParent.selectLoadSaveMenu(Selection, true);
		}
	}

	@Override
	public void onActionKeyDown(int key) {
		int sel;
		switch (key) {
			case MarikaKey.ENTER:
			case MarikaKey.SPACE: // 执行装入存储
				if (Selection >= 0)
					doLoadSave();
				break;
			
			case MarikaKey.ESCAPE: // 取消
				mParent.cancelLoadSaveMenu(Flags);
				break;

			case MarikaKey.UP:	// 选前一项
				{
					sel = mParent.prevLoadSaveSelect(Selection);
					if (sel != Selection) 
					{
						mParent.selectLoadSaveMenu(Selection, false);
						Selection = sel;
						mParent.selectLoadSaveMenu(Selection, true);
					}
				}
				break;

			case MarikaKey.DOWN:
				{
					sel = mParent.nextLoadSaveSelect(Selection);
					if (sel != Selection) 
					{
						mParent.selectLoadSaveMenu(Selection, false);
						Selection = sel;
						mParent.selectLoadSaveMenu(Selection, true);
					}
				}
				break;
		}
	}

	@Override
	public void onActionTimedOut(int timerId) {
		switch (timerId) {
			case MarikaConfig.TimerSleep:
				Flags |= MarikaConfig.IS_TIMEDOUT;
				break;
		}
	}

	@Override
	public boolean onActionIdleAction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onActionMusicDone(int music) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActionWipeDone() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActionWaveDone() {
		// TODO Auto-generated method stub
		
	}

	protected abstract void doLoadSave();
}
