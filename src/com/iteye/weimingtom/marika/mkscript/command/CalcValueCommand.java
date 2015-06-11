package com.iteye.weimingtom.marika.mkscript.command;

public class CalcValueCommand extends Command {
	public short value_addr;
	public int add_value;
	
	public CalcValueCommand(byte type) {
		super(type);
	}

	@Override
	public String toString() {
		return super.toString() + "[CalcValueCommand] { value_addr: " + value_addr + 
			", add_value: " + add_value + " }";
	}
}
