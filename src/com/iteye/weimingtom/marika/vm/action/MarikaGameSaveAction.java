package com.iteye.weimingtom.marika.vm.action;

public class MarikaGameSaveAction extends MarikaGameLoadSaveAction{

	@Override
	protected void doLoadSave() {
		mParent.saveGame(Selection, Flags);
	}
}
