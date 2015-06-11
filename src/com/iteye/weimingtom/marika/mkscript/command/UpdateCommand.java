package com.iteye.weimingtom.marika.mkscript.command;

public class UpdateCommand extends Command {
	public byte flag;

	public UpdateCommand(byte type) {
		super(type);
	}

	@Override
	public String toString() {
		return super.toString() + "[UpdateCommand] { flag: " + flag +
			" }";
	}
}
