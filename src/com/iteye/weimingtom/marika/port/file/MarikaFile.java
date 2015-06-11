package com.iteye.weimingtom.marika.port.file;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class MarikaFile {
	private MarikaResource res;
	private String mFilename;
	private boolean isOK = false;
	// text
	private static final boolean DEBUG_GETSTRING = false;
	private String[] lines;
	private int currentLine;
	// image
	public BufferedImage mImage;
	// binary
	private ByteBuffer mBytes;
	
	public MarikaFile(MarikaResource res, String file) {
		this.res = res;
		open(file);
	}
	
	private boolean open(String file) {
		//FIXME:ADD
		//添加，方便调试
		this.mFilename = file;
		if (MarikaResource.USE_CACHE) {
			mImage = res.imageMap.get(file);
		} else {
			mImage = res.loadImageNoCache(file);
		}
		if (mImage != null) {
			MarikaLog.trace("MarikaFile::open() open image file success:" + file);
			isOK = true;
			return true;
		} else {
			//return false;
			mBytes = res.dataMap.get(file);
			if (mBytes != null) {
				MarikaLog.trace("MarikaFile::open() open hex file success:" + file);
				isOK = true;
				return true;
			} else {
				String content = res.textMap.get(file);
				if (content != null) {
					MarikaLog.trace("MarikaFile::open() open text file success:" + file);
					this.lines = content.split("\\n");
					this.currentLine = 0;
					isOK = true;
					return true;
				}
				return false;
			}
		}
	}
	
	public boolean close() {
		mFilename = null;
		isOK = false;
		lines = null;
		currentLine = 0;
		mImage = null; //FIMXE: no destroy
		mBytes = null;
		return true;
	}
	
	public boolean isOk() { 
		return this.isOK; 
	}
	
	//FIXME:
	public int read(ByteBuffer bytes, int length) {
		int pos = bytes.position();
		mBytes.position(0);
		byte[] b = new byte[length];
		mBytes.get(b, 0, length); //FIXME:
		bytes.put(b);
		return bytes.position() - pos;
	}
	
	//FIXME:
	public int getFileSize() {
		if (mBytes != null) {
			return mBytes.array().length; //FIXME:
		}
		return 0;
	}
	
	public String getLineString() {
		String str;
		if (this.currentLine >= this.lines.length) {
			str = null;
		} else {
			str = this.lines[this.currentLine];
			this.currentLine++;
		}
		return str;
	}
	
	public String getString() {
		String readBuffer = getLineString();
		if (readBuffer == null) {
			return null;
		}
		if (DEBUG_GETSTRING) {
			MarikaLog.trace("_readBuffer:" + readBuffer);
			if (readBuffer.startsWith("goto")) {
				try {
					throw new Exception();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		while (readBuffer.length() > 0 && 
			(readBuffer.endsWith("\n") || 
			 readBuffer.endsWith("\r"))) {
			readBuffer = readBuffer.substring(0, readBuffer.length() - 1);
		}
		return readBuffer;
	}
	
	public String getFileName() {
		return this.mFilename; 
	}
	
	public int getLineNo() {
		return this.currentLine; 
	}
}
