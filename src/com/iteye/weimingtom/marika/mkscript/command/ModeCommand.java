package com.iteye.weimingtom.marika.mkscript.command;

public class ModeCommand extends Command {
	public byte mode;

	public ModeCommand(byte type) {
		super(type);
	}
	
	@Override
	public String toString() {
		return super.toString() + "[ModeCommand] { mode: " + mode +
			" }";
	}
}
