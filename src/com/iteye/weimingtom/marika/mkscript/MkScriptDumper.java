package com.iteye.weimingtom.marika.mkscript;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Stack;

import com.iteye.weimingtom.marika.mkscript.command.CalcValueCommand;
import com.iteye.weimingtom.marika.mkscript.command.ClearCommand;
import com.iteye.weimingtom.marika.mkscript.command.ExecCommand;
import com.iteye.weimingtom.marika.mkscript.command.GotoCommand;
import com.iteye.weimingtom.marika.mkscript.command.IfCommand;
import com.iteye.weimingtom.marika.mkscript.command.LoadCommand;
import com.iteye.weimingtom.marika.mkscript.command.MenuCommand;
import com.iteye.weimingtom.marika.mkscript.command.MenuItemCommand;
import com.iteye.weimingtom.marika.mkscript.command.ModeCommand;
import com.iteye.weimingtom.marika.mkscript.command.MusicCommand;
import com.iteye.weimingtom.marika.mkscript.command.SetValueCommand;
import com.iteye.weimingtom.marika.mkscript.command.SleepCommand;
import com.iteye.weimingtom.marika.mkscript.command.SoundCommand;
import com.iteye.weimingtom.marika.mkscript.command.TextCommand;
import com.iteye.weimingtom.marika.mkscript.command.UpdateCommand;
import com.iteye.weimingtom.marika.mkscript.command.WipeinCommand;
import com.iteye.weimingtom.marika.mkscript.command.WipeoutCommand;
import com.iteye.weimingtom.marika.mkscript.parser.Label;
import com.iteye.weimingtom.marika.mkscript.parser.LabelRef;
import com.iteye.weimingtom.marika.mkscript.parser.Lexer;
import com.iteye.weimingtom.marika.mkscript.parser.ValueOrNumber;
import com.iteye.weimingtom.marika.model.MarikaConfig;
import com.iteye.weimingtom.marika.port.file.MarikaFile;
import com.iteye.weimingtom.marika.port.file.MarikaLog;
import com.iteye.weimingtom.marika.port.file.MarikaResource;

public class MkScriptDumper {
	private static final boolean DEBUG_MENU = true; //-->menu:
	private static final int BYTES_MAX = 65536;
	private static final boolean DEBUG_NORMAL_ERROR = true;
	private static final boolean DEBUG_FATAL_ERROR = true;
	private static final boolean DEBUG_NOTICE = true;
	private static final boolean DEBUG_FMT_THEN_LABEL = false;
	private static final boolean DEBUG_FMT_ENDIF = false;
	private static final boolean DEBUG_REF_VALUE = true;
	private static final boolean DEBUG_WRITE_LABEL = true;
	private static final int MAX_COMMAND = 65536;
	private static final int MsgNotice = 0;
	private static final int MsgError = 1;
	private static final int MsgFatal = 2;
	private static final int MAX_VALUES	= 100;
	private static final int MAX_TEXTLINE = 4;
	
	private int nerror;
	private int then_index;
	private Stack<Integer> then_nest = new Stack<Integer>();
	private ByteBuffer command_buffer = ByteBuffer.allocate(BYTES_MAX);
	private boolean add_value;
	private ArrayList<String> value_name = new ArrayList<String>();
	private ArrayList<Label> labels = new ArrayList<Label>();
	private MarikaResource res;
	private MarikaFile reader;
	
	private boolean isError() { 
		return nerror != 0; 
	}
	
	public MkScriptDumper(MarikaResource res) {
		this.res = res;
		reader = null;
		nerror = 0;
		then_index = 0;
		command_buffer.position(0);
		add_value = false;

		command_buffer.order(ByteOrder.LITTLE_ENDIAN);
		command_buffer.clear();
	}
	
	private boolean openValueTable() {
		MarikaFile fin = new MarikaFile(res, getValueFile());	
		if (!fin.isOk()) {
			fin.close();
			return false;
		}
		String str;
		while ((str = fin.getLineString()) != null) {
			value_name.add(str);
			MarikaLog.trace(str);
		}
		fin.close();
		return true;
	}
	
	private boolean closeValueTable() {
		return false;
	}
	
	private int findValue(String value) {
		int i = value_name.indexOf(value);
		if (i != -1) {
			return i;
		}
		if (value_name.size() >= MAX_VALUES) {	
			normalError("too meny values.");
			return -1;
		}
		value_name.add(value);
		add_value = true;
		return value_name.size() - 1;
	}

	private String getValueFile() {
		return "value.txt";
	}
	
	private void outputMessage(int code, String str) {
		MarikaLog.trace(str);
	}
	
	private String getErrorPrefix() {
		if (reader == null) {
			return "";
		}
		//FXIME:
		return "[[" + reader.getFileName() + ":" + reader.getLineNo() + "]]";
	}
	
	private void errorMessage(String str) {
		nerror++;
		outputMessage(MsgError, getErrorPrefix() + str);
	}

	private void normalError(String str) {
		nerror++;
		outputMessage(MsgError, "[" + getErrorPrefix() + "]"+ str);
		if (DEBUG_NORMAL_ERROR) {
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}

	private void fatalError(String str) {
		nerror++;
		outputMessage(MsgFatal, getErrorPrefix() + str);
		if (DEBUG_FATAL_ERROR){
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.exit(0);
		}
	}
	
	private void notice(String str) {
		outputMessage(MsgNotice, getErrorPrefix() + str);
		if (DEBUG_NOTICE) {
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String thenLabel() {
		int idx = then_index++ << 16;	//FIXME:	
		then_nest.push(idx);
		return fmtThenLabel(idx);
	}

	private void addLabel(String label) {
		for (Label lp : labels) {
			if (lp.label.equals(label)) {
				if (lp.ref == null) {
					// redefine label 
					normalError("label " + label + 
						" redefinition line " + lp.line + 
						" and " + reader.getLineNo());
				} else {
					// delete label
					LabelRef chain = lp.ref;
					lp.line = reader.getLineNo();
					lp.ref = null;
					lp.jmp_addr = command_buffer.position();
					while (chain != null) {
						if (DEBUG_REF_VALUE) {
							MarikaLog.trace("===>ref label_ref_address:" + chain.label_ref_address);
						}
						LabelRef next = chain.next;
						if (DEBUG_WRITE_LABEL) {
							MarikaLog.trace("<<<< write address: " + chain.label_ref_address + " , value: " + command_buffer.position());
						}
						writeIntByAddress(chain.label_ref_address, command_buffer.position());
						chain = null;
						chain = next;
					}
					if (DEBUG_REF_VALUE) {
						MarikaLog.trace("===>set label_ref:" + label + "\t" +
								lp.line + "\t" +
								command_buffer.position());
					}
				}
				return;
			}
		}
		//new label
		labels.add(new Label(label, reader.getLineNo(), command_buffer.position(), null)); // ncommand, null));
		if (DEBUG_REF_VALUE) {
			MarikaLog.trace("===>new Label:" + label + "\t" + 
					reader.getLineNo() + "\t" +
					command_buffer.position());
		}
	}
	
	private void findLabel(String label, int reference_address) {
		if (DEBUG_WRITE_LABEL) {
			MarikaLog.trace("<<<< write address: " + reference_address + " , value: " + 0);
		}
		writeIntByAddress(reference_address, 0);
		for (Label lp : labels) {
			if (lp.label.equals(label)) {
				if (lp.ref != null) {
					lp.ref = new LabelRef(lp.ref, reference_address);
					if (DEBUG_REF_VALUE) {
						MarikaLog.trace("===>new LabelRef:" + "\t" + 
								lp.label + "\t" + 
								lp.jmp_addr);
					}
				} else {				
					if (DEBUG_WRITE_LABEL) {
						MarikaLog.trace("<<<< write address: " + reference_address + 
								", value: " + lp.jmp_addr + "\t" + lp.label);
					}
					writeIntByAddress(reference_address, lp.jmp_addr);
				}
				return;
			}
		}
		LabelRef chain = new LabelRef(null, reference_address);
		labels.add(new Label(label, reader.getLineNo(), 0, chain));
		if (DEBUG_REF_VALUE) {
			MarikaLog.trace("===>new Label:" + label + "\t" + reader.getLineNo());
		}
	}
	
	private void labelCheck() {
		for (Label lp : labels) {
			if (lp.ref != null) { 
				String label = lp.label;
				switch (label.charAt(0)) {
				case '#':
					errorMessage("can't find \"endif\". (line " + lp.line + ")");
					break;
					
				default:
					errorMessage("label " + label + " undefined. (line " + lp.line + ")");
					break;
				}
				LabelRef chain = lp.ref;
				while (chain != null) {
					LabelRef next = chain.next;
					chain = null;
					chain = next;
				}
			}
		}
	}
	
	private void setLabel(Lexer lexer) {
		if (lexer.numToken() != 1) {
			normalError("too meny parameter");
			return;
		}
		//FIXME:
		String p = lexer.getString().substring(1);
		addLabel(p);
	}
	
	//FIXME:
	private boolean chkKeyword(String str, String keyword) {
//		for (int pos = 0; pos < str.length(); ++pos) {
//			if (Character.toLowerCase(str.charAt(pos)) != keyword.charAt(pos)) {
//				return false;
//			}
//		}
//		return true;
		if (str != null && keyword != null) {
			return keyword.startsWith(str.toLowerCase());
		}
		return false;
	}

	private int getPosition(String str) {
		if (chkKeyword(str, "center")) {
			return MkScriptType.POSITION_CENTER;
		}
		if (chkKeyword(str, "left")) {
			return MkScriptType.POSITION_LEFT;
		}
		if (chkKeyword(str, "right")) {
			return MkScriptType.POSITION_RIGHT;
		}
		if (chkKeyword(str, "bg") || chkKeyword(str, "back")) {
			return MkScriptType.POSITION_BACK;
		}
		if (chkKeyword(str, "bgo") || chkKeyword(str, "backonly")) {
			return MkScriptType.POSITION_BACKONLY;
		}
		if (chkKeyword(str, "overlap")) {
			return MkScriptType.POSITION_OVERLAP;
		}
		normalError("syntax error (position)");
		return MkScriptType.POSITION_BACK;
	}

	private int getUpdateType(String str) {
		if (chkKeyword(str, "cut") || chkKeyword(str, "now")) {
			return MkScriptType.UPDATE_NOW;
		}
		if (chkKeyword(str, "overlap")) {
			return MkScriptType.UPDATE_OVERLAP;
		}
		if (chkKeyword(str, "wipe")) {
			return MkScriptType.UPDATE_WIPE;
		}
		normalError("syntax error (update type)");
		return MkScriptType.UPDATE_NOW;
	}

	private boolean getValueOrNumber(ValueOrNumber value, Lexer lexer) {
		int type = lexer.getType();
		if (type == Lexer.IsString) {
			String p = lexer.getString();
			value.value = findValue(p);
			value.isvalue = false;
		} else {
			double result = lexer.getValue();
			if (Double.isNaN(result)) {	
				return false;
			}
			value.value = (int)result;
			value.isvalue = true;
		}
		return true;
	}

	private byte boolOp(String op) {
		if ("==".equals(op)) { 
			return MkScriptType.IF_TRUE_CMD;
		} else if ("!=".equals(op)) { 
			return MkScriptType.IF_FALSE_CMD;
		} else if ("<=".equals(op)) {
			return MkScriptType.IF_SMALLER_EQU_CMD;
		} else if (">=".equals(op)) { 
			return MkScriptType.IF_BIGGER_EQU_CMD;
		} else if ("<".equals(op)) {
			return MkScriptType.IF_SMALLER_CMD;
		} else if (">".equals(op)) {
			return MkScriptType.IF_BIGGER_CMD;
		}
		normalError("syntax error");
		return -1;
	}

	private byte negBoolOp(String op) {
		if ("==".equals(op)) {
			return MkScriptType.IF_FALSE_CMD;
		} else if ("!=".equals(op)) {
			return MkScriptType.IF_TRUE_CMD;
		} else if ("<=".equals(op)) {
			return MkScriptType.IF_BIGGER_CMD;
		} else if (">=".equals(op)) {
			return MkScriptType.IF_SMALLER_CMD;
		} else if ("<".equals(op)) {
			return MkScriptType.IF_BIGGER_EQU_CMD;
		} else if (">".equals(op)) {
			return MkScriptType.IF_SMALLER_EQU_CMD;
		}
		normalError("syntax error");
		return -1;
	}
	
	private boolean chkTermination(String str) {
		if (str != null) {
			return str.startsWith(".");
		}
		return false;
	}
	
	private void parseCommand(Lexer lexer) {
		String command = lexer.getString(0);
		if ("set".equals(command)) {
			parseSetCommand(lexer);
		} else if ("calc".equals(command)) {
			//FIXME: calc ? set ?
			parseSetCommand(lexer);
		} else if ("text".equals(command)) {
			parseTextCommand(lexer);
		} else if ("goto".equals(command)) {
			parseGotoCommand(lexer);
		} else if ("if".equals(command)) {
			parseIfCommand(lexer);
		} else if ("else".equals(command)) {
			parseElseCommand(lexer);
		} else if ("endif".equals(command)) {
			parseEndifCommand(lexer);
		} else if ("menu".equals(command)) {
			parseMenuCommand(lexer);
		} else if ("exec".equals(command)) {
			parseExecCommand(lexer);
		} else if ("load".equals(command)) {
			parseLoadCommand(lexer);
		} else if ("update".equals(command)) {
			parseUpdateCommand(lexer);
		} else if ("clear".equals(command)) {
			parseClearCommand(lexer);
		} else if ("music".equals(command)) {
			parseMusicCommand(lexer);
		} else if ("stopm".equals(command)) {
			parseStopmCommand(lexer);
		} else if ("wait".equals(command)) {
			parseWaitCommand(lexer);
		} else if ("sound".equals(command)) {
			parseSoundCommand(lexer);
		} else if ("fadein".equals(command)) {
			parseFadeinCommand(lexer);
		} else if ("fadeout".equals(command)) {
			parseFadeoutCommand(lexer);
		} else if ("wipein".equals(command)) {
			parseWipeinCommand(lexer);
		} else if ("wipeout".equals(command)) {
			parseWipeoutCommand(lexer);
		} else if ("cutin".equals(command)) {
			parseCutinCommand(lexer);
		} else if ("cutout".equals(command)) {
			parseCutoutCommand(lexer);
		} else if ("whitein".equals(command)) {
			parseWhiteinCommand(lexer);
		} else if ("whiteout".equals(command)) {
			parseWhiteoutCommand(lexer);
		} else if ("flash".equals(command)) {
			parseFlashCommand(lexer);
		} else if ("shake".equals(command)) {
			parseShakeCommand(lexer);
		} else if ("mode".equals(command)) {
			parseModeCommand(lexer);
		} else if ("system".equals(command)) {
			parseSystemCommand(lexer);
		} else if ("end".equals(command)) {
			parseEndCommand(lexer);
		} else {
			if (lexer.numToken() >= 3) {
				String p3 = lexer.getString(1);
				lexer.getType(0);
				if ("+".equals(p3) || "-".equals(p3) || "=".equals(p3)) {
					//FIXME:
					parseSetCommand(lexer);
				} else {
					normalError("syntax error (command syntax)");
				}
			} else {
				normalError("syntax error (command syntax)");
			}
		}
	}

	private void parserString(String str) {
		Lexer lexer = new Lexer(str);
		if (lexer.numToken() == 0) {
			return;
		}
		int	type = lexer.getType();
		if (type == Lexer.IsLabel) {
			setLabel(lexer);
		} else {
			parseCommand(lexer);
		}
	}

	public int readScript(String name) {
		reader = new MarikaFile(res, name);
		try {
			openValueTable();
			String str;
			while ((str = reader.getString()) != null) {
				parserString(str);
			}
			MkScriptBufferUtil.writeCommand(MkScriptType.END_CMD, command_buffer);
			labelCheck();
			if (nerror != 0) {
				notice("I have " + nerror + " error" + (nerror == 1? "": "s") + " found.");
			}
			if (nerror == 0 && add_value) {
				closeValueTable();
			}
		} catch (Exception e) {
			e.printStackTrace();
			fatalError(e.getMessage());
		}
		return nerror;
	}
	
	private int writeScript(String name) {
		return -1;
	}
	
	private String fmtThenLabel(int i) {
		if (DEBUG_FMT_THEN_LABEL) {
			if (i == 0xffff) {
				try {
					throw new Error();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		String str = Integer.toHexString(i);
		String zero = "";
		if (str.length() < 8) {
			for (int n = 0; n < 8 - str.length(); n++) {
				zero += "0";
			}
		}
		if(DEBUG_FMT_ENDIF) {
			MarikaLog.trace("#endif#" + zero + str);
		}
		return "#endif#" + zero + str;
	}
	
	private void writeIntByAddress(int pos, int value) {
		int oldpos = command_buffer.position();
		command_buffer.position(pos);
		command_buffer.putInt(value);
		command_buffer.position(oldpos);
	}
	
	public ByteBuffer duplicateBuffer() {
		ByteBuffer bytes = ByteBuffer.allocate(BYTES_MAX);
		bytes.order(ByteOrder.LITTLE_ENDIAN);
		try {
			bytes.put(MkScriptType.SCRIPT_MAGIC.getBytes("gbk"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < 8 - MkScriptType.SCRIPT_MAGIC.length(); ++i) {
			bytes.put((byte)0);
		}
		MarikaLog.trace("header magic == " + MkScriptType.SCRIPT_MAGIC);	
		int size = command_buffer.position();
		bytes.putInt(size);
		MarikaLog.trace("header ncommand == " + size + " (0x" + Integer.toHexString(size) + ")");
		bytes.put(command_buffer.array(), 0, command_buffer.position());
		return bytes;
	}
	
	public void dumpBuffer() {
		ByteBuffer bytes = duplicateBuffer();
		int num = bytes.position();
		MarikaLog.trace("dumpBuffer: " + num);
		String str = "";
		bytes.position(0);
		if (num > 0) {
			for (int i = 0; i < num; i++) {
				int value = bytes.get() & 0xFF;
				if (i % 16 == 0) {
					String address = Integer.toHexString(i);
					for (int k = 0; k < 8 - address.length(); k++) {
						str += "0";
					}
					str += address + ":";
				}
				if(value >= 16) {
					str += Integer.toHexString(value) + " ";
				} else {
					str += "0" + Integer.toHexString(value) + " ";
				}
				if (i % 16 == 15) {
					str += '\n';
				}
			}
		}
		bytes.position(num);
		MarikaLog.trace(str);
	}
	
	private void parseSetCommand(Lexer lexer) {
		String p1 = lexer.getString();
		String p2 = lexer.getString();
		double value = lexer.getValue(); // b3
		String str = lexer.getString();
		if (p1 == null || p2 == null || Double.isNaN(value) || str != null) {
			MarikaLog.trace(str);
			normalError("syntax error");
		} else {
			if ("=".equals(p2)) {
				SetValueCommand cp1 = (SetValueCommand)MkScriptBufferUtil.newCommand(MkScriptType.SET_VALUE_CMD);
				cp1.value_addr = (short)findValue(p1);
				cp1.set_value = (int)value;
				MkScriptBufferUtil.writeCommand(MkScriptType.SET_VALUE_CMD, command_buffer);
			} else if ("+".equals(p2)) { 
				CalcValueCommand cp2 = (CalcValueCommand)MkScriptBufferUtil.newCommand(MkScriptType.CALC_VALUE_CMD);
				cp2.value_addr = (short)findValue(p1);
				cp2.add_value = (int)value;
				MkScriptBufferUtil.writeCommand(MkScriptType.CALC_VALUE_CMD, command_buffer);
			} else if ("-".equals(p2)) {
				CalcValueCommand cp3 = (CalcValueCommand) MkScriptBufferUtil.newCommand(MkScriptType.CALC_VALUE_CMD);
				cp3.value_addr = (short)findValue(p1);
				cp3.add_value = (int)-value;
				MkScriptBufferUtil.writeCommand(MkScriptType.CALC_VALUE_CMD, command_buffer);
			} else {
				normalError("syntax error");
			}
		}
	}
	
	private void parseTextCommand(Lexer lexer) {
		if (lexer.getString() != null) {
			normalError("syntax error (in text command)");
		} else {
			TextCommand cp = (TextCommand)MkScriptBufferUtil.newCommand(MkScriptType.TEXT_CMD);
			String work = "";
			for (int i = 0; ; i++) {
				String str;
				if ((str = reader.getString()) == null) {
					normalError("syntax error (text syntax)");
					break;
				}
				if (chkTermination(str)) {
					break;
				}
				work += str;
				work += '\n';
				if (i >= MkScriptDumper.MAX_TEXTLINE) {
					normalError("text line overflow");
					break;
				}
			}
			cp.msg_len = (short)cp.AddMessage(work, 255);
			if (cp.msg_len < 0) {
				throw new RuntimeException("msg_len < 0");
			}
			MkScriptBufferUtil.writeCommand(MkScriptType.TEXT_CMD, command_buffer);
		}
	}
	
	private void parseGotoCommand(Lexer lexer) {
		String p = lexer.getString();
		if (p == null || lexer.getString() != null) {
			normalError("syntax error (in goto command)");
		} else {
			GotoCommand cp = (GotoCommand)MkScriptBufferUtil.newCommand(MkScriptType.GOTO_CMD);
			findLabel(p, command_buffer.position() + 4);
			MkScriptBufferUtil.writeCommand(MkScriptType.GOTO_CMD, command_buffer);
		}
	}
	
	private void parseIfCommand(Lexer lexer) {
		ValueOrNumber val1 = new ValueOrNumber();
		ValueOrNumber val2 = new ValueOrNumber();
		boolean b1 = getValueOrNumber(val1, lexer);
		String op = lexer.getString();
		boolean b2 = getValueOrNumber(val2, lexer);
		if (!b1 || !b2 || op == null) {
			normalError("syntax error (in if command)");
		} else {
			IfCommand cp = (IfCommand)MkScriptBufferUtil.newCommand(MkScriptType.IF_TRUE_CMD);
			cp.flag = 0;
			if (val1.isvalue) {
				cp.flag |= 1;
			}
			cp.value1 = val1.value;
			if (val2.isvalue) {
				cp.flag |= 2;
			}
			cp.value2 = val2.value;
			String p = lexer.getString();
			String label = null;
			if (p != null) {
				if ("goto".equals(p.toLowerCase())) { 	
					// if-goto
					label = lexer.getString();
					cp.type = boolOp(op);
				} else if ("then".equals(p.toLowerCase())) {
					// if-then
					label = thenLabel();
					cp.type = negBoolOp(op);
				}
			}
			if (label == null || lexer.getString() != null) {
				normalError("syntax error");
			} else {
				findLabel(label, command_buffer.position() + 12); // cp.goto_label);
				MkScriptBufferUtil.writeCommand(MkScriptType.IF_TRUE_CMD, command_buffer);
			}
		}
	}
	
	private void parseElseCommand(Lexer lexer) {
		if (then_nest.size() == 0) {
			normalError("\"if\", \"else\" nest error.");
		} else {
			//FIXME:
			int idx = then_nest.pop();
			String else_label = fmtThenLabel(idx);
			GotoCommand cp1 = (GotoCommand)MkScriptBufferUtil.newCommand(MkScriptType.GOTO_CMD);
			String goto_label;
			then_nest.push(idx + 1);
			goto_label = fmtThenLabel(idx | 0xffff);
			findLabel(goto_label, command_buffer.position() + 4);
			MkScriptBufferUtil.writeCommand(MkScriptType.GOTO_CMD, command_buffer);
			addLabel(else_label);
			String p = lexer.getString();
			if (p == null) {
				
			} else if ("if".equals(p.toLowerCase())) {
				ValueOrNumber val1 = new ValueOrNumber();
				ValueOrNumber val2 = new ValueOrNumber();
				boolean b1 = getValueOrNumber(val1, lexer);
				String op = lexer.getString();
				boolean b2 = getValueOrNumber(val2, lexer);
				if (!b1 || !b2 || op == null) {
					normalError("syntax error (in else if command)");
				} else {
					IfCommand cp2 = (IfCommand)MkScriptBufferUtil.newCommand(MkScriptType.IF_TRUE_CMD);
					cp2.type = negBoolOp(op);
					cp2.flag = 0;
					if (val1.isvalue) {
						cp2.flag |= 1;
					}
					cp2.value1 = val1.value;
					if (val2.isvalue) {
						cp2.flag |= 2;
					}
					cp2.value2 = val2.value;
					String p2 = lexer.getString();
					if (p2 == null || !"then".equals(p2.toLowerCase()) ) {
						normalError("syntax error");
					} else {
						String label = fmtThenLabel(idx + 1);
						findLabel(label, command_buffer.position() + 12); //p2.goto_label);
						MkScriptBufferUtil.writeCommand(MkScriptType.IF_TRUE_CMD, command_buffer);
					}
				}
			} else {
				normalError("syntax error (in else command)");
			}
		}
	}
	
	private void parseEndifCommand(Lexer lexer) {
		if (lexer.getString() != null) {
			normalError("syntax error (in endif command)");
		} else {
			if (then_nest.size() == 0) {
				normalError("\"if\", \"endif\" nest error.");
			} else {
				String tmp;
				//FIXME:
				int idx = then_nest.pop();
				tmp = fmtThenLabel(idx);
				addLabel(tmp);
				if ((idx & 0xffff) != 0) {
					tmp = fmtThenLabel(idx | 0xffff);
					addLabel(tmp);
				}
			}
		}
	}
	
	private void parseMenuCommand(Lexer lexer) {
		String p = lexer.getString();
		if (p == null || lexer.getString() != null) {
			normalError("syntax error (in menu command)");
		} else {
			int	value_addr = findValue(p);
			MkScriptBufferUtil.writeCommand(MkScriptType.MENU_INIT_CMD, command_buffer);
			String str;
			for (int no = 0; (str = reader.getString()) != null; no++) {
				if (MkScriptDumper.DEBUG_MENU) {
					MarikaLog.trace("-->menu:" + str + ", length=" + str.length());
				}
				if (str.length() > MarikaConfig.MenuWidth) {
					throw new RuntimeException("menu item is too long !!! MarikaConfig.MenuWidth == " + MarikaConfig.MenuWidth);
				}
				if (str.toLowerCase().equals("end")) {
					break;
				}
				MenuItemCommand ip = (MenuItemCommand)MkScriptBufferUtil.newCommand(MkScriptType.MENU_ITEM_CMD);
				ip.label_len = (byte)ip.AddMessage(str, 255);
				ip.number = (byte)(no + 1);
				MkScriptBufferUtil.writeCommand(MkScriptType.MENU_ITEM_CMD, command_buffer);
			}
			MenuCommand op = (MenuCommand)MkScriptBufferUtil.newCommand(MkScriptType.MENU_CMD);
			op.value_addr = (short)value_addr;
			MkScriptBufferUtil.writeCommand(MkScriptType.MENU_CMD, command_buffer);
		}
	}
	
	private void parseExecCommand(Lexer lexer) {
		String p = lexer.getString();
		if (p == null || lexer.getString() != null) {
			normalError("syntax error (in exec command)");
		} else {
			ExecCommand cp = (ExecCommand)MkScriptBufferUtil.newCommand(MkScriptType.EXEC_CMD);
			cp.path_len = (byte)cp.AddMessage(p, 255);
			MkScriptBufferUtil.writeCommand(MkScriptType.EXEC_CMD, command_buffer);
		}
	}
	
	private void parseLoadCommand(Lexer lexer) {
		String p1 = lexer.getString();
		String p2 = lexer.getString();
		if (p1 == null || p2 == null || lexer.getString() != null) {
			normalError("syntax error (in load command)");
		} else {
			LoadCommand cp = (LoadCommand)MkScriptBufferUtil.newCommand(MkScriptType.LOAD_CMD);
			cp.flag = (byte)getPosition(p1);
			cp.path_len = (byte)cp.AddMessage(p2, 255);
			MkScriptBufferUtil.writeCommand(MkScriptType.LOAD_CMD, command_buffer);
		}
	}
	
	private void parseUpdateCommand(Lexer lexer) {
		String p = lexer.getString();
		if (p == null || lexer.getString() != null) {
			normalError("syntax error (in update command)");
		} else {
			UpdateCommand cp = (UpdateCommand)MkScriptBufferUtil.newCommand(MkScriptType.UPDATE_CMD);
			cp.flag = (byte)getUpdateType(p);
			MkScriptBufferUtil.writeCommand(MkScriptType.UPDATE_CMD, command_buffer);
		}
	}
	
	private void parseClearCommand(Lexer lexer) {
		String p = lexer.getString();
		if (p == null || lexer.getString() != null) {
			normalError("syntax error (in clear command)");
		} else {
			if ("text".equals(p.toLowerCase())) {
				MkScriptBufferUtil.writeCommand(MkScriptType.CLEAR_TEXT_CMD, command_buffer);
			} else {
				ClearCommand cp = (ClearCommand) MkScriptBufferUtil.newCommand(MkScriptType.CLEAR_CMD);
				cp.pos = (byte)getPosition(p);
				MkScriptBufferUtil.writeCommand(MkScriptType.CLEAR_CMD, command_buffer);
			}
		}
	}
	
	private void parseMusicCommand(Lexer lexer) {
		double value = lexer.getValue();
		if (Double.isNaN(value) || value <= 0 || lexer.getString() != null) {
			normalError("syntax error (in music command)");
		} else {
			MusicCommand cp = (MusicCommand)MkScriptBufferUtil.newCommand(MkScriptType.MUSIC_CMD);
			cp.number = (int)value;
			MkScriptBufferUtil.writeCommand(MkScriptType.MUSIC_CMD, command_buffer);
		}
	}
	
	private void parseStopmCommand(Lexer lexer) {
		if (lexer.getString() != null) {
			normalError("syntax error");
		} else {
			MkScriptBufferUtil.writeCommand(MkScriptType.STOPM_CMD, command_buffer);
		}
	}
	
	private void parseWaitCommand(Lexer lexer) {
		double value = lexer.getValue();
		if (Double.isNaN(value) || value <= 0 || lexer.getString() != null) {
			normalError("syntax error (in wait command)");
		} else {
			SleepCommand cp = (SleepCommand)MkScriptBufferUtil.newCommand(MkScriptType.SLEEP_CMD);
			cp.time = (int)value;
			MkScriptBufferUtil.writeCommand(MkScriptType.SLEEP_CMD, command_buffer);
		}
	}
	
	private void parseSoundCommand(Lexer lexer) {
		String p = lexer.getString();
		if (p == null || lexer.getString() != null) {
			normalError("syntax error (in sound command)");
		} else {
			SoundCommand cp = (SoundCommand)MkScriptBufferUtil.newCommand(MkScriptType.SOUND_CMD);
			cp.path_len = (byte)cp.AddMessage(p, 255);
			MkScriptBufferUtil.writeCommand(MkScriptType.SOUND_CMD, command_buffer);
		}
	}
	
	private void parseFadeinCommand(Lexer lexer) {
		if (lexer.getString() != null) {
			normalError("syntax error");
		} else {
			MkScriptBufferUtil.writeCommand(MkScriptType.FADEIN_CMD, command_buffer);
		}
	}
	
	private void parseFadeoutCommand(Lexer lexer) {
		if (lexer.getString() != null) {
			normalError("syntax error");
		} else {
			MkScriptBufferUtil.writeCommand(MkScriptType.FADEOUT_CMD, command_buffer);
		}
	}
	
	private void parseWipeinCommand(Lexer lexer) {
		double value = lexer.getValue();
		if (Double.isNaN(value) || value <= 0 || value > 2 || lexer.getString() != null) {
			normalError("syntax error (in wipein command)");
		} else {
			WipeinCommand cp = (WipeinCommand)MkScriptBufferUtil.newCommand(MkScriptType.WIPEIN_CMD);
			cp.pattern = (byte)value;
			MkScriptBufferUtil.writeCommand(MkScriptType.WIPEIN_CMD, command_buffer);
		}
	}
	
	private void parseWipeoutCommand(Lexer lexer) {
		double value = lexer.getValue();
		if (Double.isNaN(value) || value <= 0 || value > 2 || lexer.getString() != null) {
			normalError("syntax error (in wipeout command)");
		} else {
			WipeoutCommand cp = (WipeoutCommand)MkScriptBufferUtil.newCommand(MkScriptType.WIPEOUT_CMD);
			cp.pattern = (byte)value;
			MkScriptBufferUtil.writeCommand(MkScriptType.WIPEOUT_CMD, command_buffer);
		}
	}
	
	private void parseCutinCommand(Lexer lexer) {
		if (lexer.getString() != null) {
			normalError("syntax error");
		} else {
			MkScriptBufferUtil.writeCommand(MkScriptType.CUTIN_CMD, command_buffer);
		}
	}
	
	private void parseCutoutCommand(Lexer lexer) {
		if (lexer.getString() != null) {
			normalError("syntax error");
		} else {
			MkScriptBufferUtil.writeCommand(MkScriptType.CUTOUT_CMD, command_buffer);
		}
	}
	
	private void parseWhiteinCommand(Lexer lexer) {
		if (lexer.getString() != null) {
			normalError("syntax error");
		} else {
			MkScriptBufferUtil.writeCommand(MkScriptType.WHITEIN_CMD, command_buffer);
		}
	}
	
	private void parseWhiteoutCommand(Lexer lexer) {
		if (lexer.getString() != null) {
			normalError("syntax error");
		} else {
			MkScriptBufferUtil.writeCommand(MkScriptType.WHITEOUT_CMD, command_buffer);
		}
	}
	
	private void parseFlashCommand(Lexer lexer) {
		if (lexer.getString() != null) {
			normalError("syntax error");
		} else {
			MkScriptBufferUtil.writeCommand(MkScriptType.FLASH_CMD, command_buffer);
		}
	}
	
	private void parseShakeCommand(Lexer lexer) {
		if (lexer.getString() != null) {
			normalError("syntax error");
		} else {
			MkScriptBufferUtil.writeCommand(MkScriptType.SHAKE_CMD, command_buffer);
		}
	}
	
	private void parseModeCommand(Lexer lexer) {
		String p = lexer.getString();
		if (p == null || lexer.getString() != null) {
			normalError("syntax error");
		} else {
			if ("system".equals(p.toLowerCase())) {
				ModeCommand cp1 = (ModeCommand)MkScriptBufferUtil.newCommand(MkScriptType.MODE_CMD);
				cp1.mode = MkScriptType.MODE_SYSTEM;
				MkScriptBufferUtil.writeCommand(MkScriptType.MODE_CMD, command_buffer);
			} else if ("scenario".equals(p.toLowerCase())) {
				ModeCommand cp2 = (ModeCommand)MkScriptBufferUtil.newCommand(MkScriptType.MODE_CMD);
				cp2.mode = MkScriptType.MODE_SCENARIO;
				MkScriptBufferUtil.writeCommand(MkScriptType.MODE_CMD, command_buffer);
			} else {
				normalError("syntax error");
			}
		}
	}
	
	private void parseSystemCommand(Lexer lexer) {
		String p = lexer.getString();
		if (p == null || lexer.getString() != null) {
			normalError("syntax error");
		} else {
			if ("load".equals(p.toLowerCase())) {
				MkScriptBufferUtil.writeCommand(MkScriptType.SYS_LOAD_CMD, command_buffer);
			} else if ("exit".equals(p.toLowerCase())) {
				MkScriptBufferUtil.writeCommand(MkScriptType.SYS_EXIT_CMD, command_buffer);
			} else if ("clear".equals(p.toLowerCase())) {
				MkScriptBufferUtil.writeCommand(MkScriptType.SYS_CLEAR_CMD, command_buffer);
			} else {
				normalError("syntax error");
			}
		}
	}
	
	private void parseEndCommand(Lexer lexer) {
		if (lexer.getString() != null) {
			normalError("syntax error");
		} else {
			MkScriptBufferUtil.writeCommand(MkScriptType.END_CMD, command_buffer);
		}
	}
	
	public void close() {
		if (reader != null) {
			reader.close();
			reader = null;
		}
	}
}
