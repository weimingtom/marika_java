package com.iteye.weimingtom.marika.vm.action;

public class MarikaGameLoadAction extends MarikaGameLoadSaveAction {

	@Override
	protected void doLoadSave() {
		mParent.loadGame(Selection);
	}
}
