package com.iteye.weimingtom.marika.mkscript.command;

public class ClearCommand extends Command {
	public byte pos;
	
	public ClearCommand(byte type) {
		super(type);
	}
	
	@Override
	public String toString() {
		return super.toString() + "[ClearCommand] { pos: " + pos + 
			" }";
	}
}
