package com.iteye.weimingtom.marika.mkscript.command;

public class SleepCommand extends Command {
	public int time;
	
	public SleepCommand(byte type) {
		super(type);
	}

	@Override
	public String toString() {
		return super.toString() + "[SleepCommand] { time: " + time +
			" }";
	}
}
