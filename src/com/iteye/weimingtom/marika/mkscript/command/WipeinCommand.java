package com.iteye.weimingtom.marika.mkscript.command;

public class WipeinCommand extends Command {
	public byte pattern;

	public WipeinCommand(byte type) {
		super(type);
	}

	@Override
	public String toString() {
		return super.toString() + "[WipeinCommand] { pattern: " + pattern +
			" }";
	}
}
