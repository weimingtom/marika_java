package com.iteye.weimingtom.marika.mkscript.command;

public class WipeoutCommand extends Command {
	public byte pattern;

	public WipeoutCommand(byte type) {
		super(type);
	}

	@Override
	public String toString() {
		return super.toString() + "[WipeoutCommand] { pattern: " + pattern +
			" }";
	}
}
