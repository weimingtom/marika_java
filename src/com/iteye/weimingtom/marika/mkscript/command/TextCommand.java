package com.iteye.weimingtom.marika.mkscript.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class TextCommand extends Command {
	public short msg_len;
	public String message;
	public int nMessageTail;
	public ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	
	public TextCommand(byte type) {
		super(type);
	}

	@Override
	public String toString() {
		return super.toString() + "[TextCommand] { msg_len: " + msg_len +
			", message: " + message + 
			" }";
	}
	
	public int AddMessage(String msg, int limit) {
//		if (msg.contains("这个部分要使用")) {
//			msg.length();
//		}
		this.message = msg;
		this.bytes.reset();
		try {
			this.bytes.write(this.message.getBytes("gbk"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int n = this.bytes.size() % 4;
		this.nMessageTail = n >= 0 ? (4 - n) : 0;
		for (int i = 0; i < this.nMessageTail; i++) {
			this.bytes.write(0);
		}
		return this.bytes.size();
	}
}
