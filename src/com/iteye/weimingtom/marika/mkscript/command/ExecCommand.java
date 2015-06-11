package com.iteye.weimingtom.marika.mkscript.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ExecCommand extends Command {
	public byte path_len;
	public String message;
	public int nMessageTail;
	public ByteArrayOutputStream bytes = new ByteArrayOutputStream();

	public ExecCommand(byte type) {
		super(type);
	}

	@Override
	public String toString() {
		return super.toString() + "[ExecCommand] { path_len: " + path_len + 
			", message: " + message +
			" }";
	}
	
	public int AddMessage(String msg, int limit) {
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
