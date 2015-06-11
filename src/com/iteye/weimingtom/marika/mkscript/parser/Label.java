package com.iteye.weimingtom.marika.mkscript.parser;

public class Label {
	public String label;
	public int line;
	public int jmp_addr;
	/**
	 * Has reference (declared but not deleted)
	 */
	public LabelRef ref; 
	
	public Label(String label, int line, int jmp_addr, LabelRef ref) {
		this.label = label;
		this.line = line;
		this.jmp_addr = jmp_addr;
		this.ref = ref;
	}
	
	//TODO: FOR DEBUGGING
	@Override
	public String toString() {
		return "<" + line + ":ref == " + (ref != null) + ":" + label + ">\n";
	}
}
