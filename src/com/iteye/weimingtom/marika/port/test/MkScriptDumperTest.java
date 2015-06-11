package com.iteye.weimingtom.marika.port.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import com.iteye.weimingtom.marika.mkscript.MkScriptDumper;
import com.iteye.weimingtom.marika.mkscript.parser.Lexer;
import com.iteye.weimingtom.marika.port.file.MarikaFile;
import com.iteye.weimingtom.marika.port.file.MarikaLog;
import com.iteye.weimingtom.marika.port.file.MarikaResource;

public class MkScriptDumperTest {
	private MarikaResource res;
	
	private void test1() {
		MarikaFile fileReader = new MarikaFile(res, /*"main.txt");*/"sample3.txt");
		while (true) {
			String str = fileReader.getString();
			if (str == null) {
				break;
			}
			MarikaLog.trace(str);
			MarikaLog.trace("line:" + fileReader.getLineNo());
			//Lexer lexer;
			new Lexer(str);
		}
		fileReader.close();
	}
	
	private void test2() {
		MkScriptDumper makeScript = new MkScriptDumper(res);
		makeScript.readScript("sample3.txt");
		makeScript.dumpBuffer();
		makeScript.close();
	}
	
	private void test3() {
		ByteBuffer buffer = ByteBuffer.allocate(255);
		buffer.clear();
		for (int i = 0; i < 30; i++) {
			buffer.put((byte) 0);
		}
		MarikaLog.trace("capacity:" + buffer.capacity());
		MarikaLog.trace("arrayOffset:" + buffer.arrayOffset());
		MarikaLog.trace("array().length:" + buffer.array().length);
		MarikaLog.trace("position:" + buffer.position());
		
		MarikaLog.trace("out => " + ("; t".charAt(0) == ';'));
	}
	
	private void test4() {
		MkScriptDumper makeScript = new MkScriptDumper(res);
		makeScript.readScript("main100.txt");
		makeScript.dumpBuffer();
		dumpBufferToFile(makeScript.duplicateBuffer(), "main100.dat");
		makeScript.close();
	}
	
	private void test5() {
		MkScriptDumper makeScript = new MkScriptDumper(res);
		makeScript.readScript("start100.txt");
		makeScript.dumpBuffer();
		dumpBufferToFile(makeScript.duplicateBuffer(), "start100.dat");
		makeScript.close();
	}
	
	public MkScriptDumperTest() {
		res = new MarikaResource();
		res.init(null);
		res.loadClassText("main.txt", res.getClass(), "main.txt");
		res.loadClassText("sample1old.txt", res.getClass(), "sample1old.txt");
		res.loadClassText("sample3.txt", res.getClass(), "sample3.txt");
		
		res.loadClassText("main100.txt", res.getClass(), "main100.txt");
		res.loadClassText("start100.txt", res.getClass(), "start100.txt");
		
//		test1();
//		test2();
//		test3();
		test4();
//		test5();
		
		res.unloadAll();
	}
	
	public static void main(String[] args) {
		new MkScriptDumperTest();
	}
	
	
	public void dumpBufferToFile(ByteBuffer bytes, String filename) {
		OutputStream ostr = null;
		try {
			ostr = new FileOutputStream(filename);
			int num = bytes.position();
			bytes.position(0);
			if (num > 0) {
				for (int i = 0; i < num; i++) {
					byte b = bytes.get();
					ostr.write(b);
				}
				ostr.flush();
			}
			bytes.position(num);		
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ostr != null) {
				try {
					ostr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
