package com.iteye.weimingtom.marika.mkscript.command;

public class MusicCommand extends Command {
	public int number;
	
	public MusicCommand(byte type) {
		super(type);
	}
	
	@Override
	public String toString() {
		return super.toString() + "[MusicCommand] { number: " + number +
			" }";
	}
}
