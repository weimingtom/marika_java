package com.iteye.weimingtom.marika.mkscript.command;

public class Command {
	public byte type;
	public int size;
	
	public Command(byte type) {
		this.type = type;
	}
	
	public String toString() {
		return "[Command-" + type + 
			"(0x" + Integer.toHexString(type) + ")]";
	}
}
