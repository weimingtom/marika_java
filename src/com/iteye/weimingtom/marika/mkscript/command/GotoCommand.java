package com.iteye.weimingtom.marika.mkscript.command;

public class GotoCommand extends Command {
	public int goto_label;

	public GotoCommand(byte type) {
		super(type);
	}

	@Override
	public String toString() {
		return super.toString() + "[GotoCommand] { }"; 
	}
}
