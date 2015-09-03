package com.iteye.weimingtom.marika.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Calendar;

public class MarikaParams {
	public static final int PARAMS_MAX_SAVE = 5;//10;
	public static final int PARAMS_MAX_VALUES = MarikaConfig.MAX_VALUES;
	
	public static final int SHOWCG_BLACKNESS = 0;
	public static final int SHOWCG_IMAGE = 1;
	public static final int SHOWCG_WHITENESS = 2;
	
	private static final int BYTES_MAX = 65536;
	private static final String HEADER_ID = "DATA";
	
	public int save_month = 0;
	public int save_date = 0;
	public int save_hour = 0;
	public int save_minute = 0;
	public int script_pos = 0;
	public String last_script = "";
	public String last_bg = "";
	public String last_center = "";
	public String last_left = "";
	public String last_right = "";
	public String last_overlap = "";
	public int last_bgm = 0;
	public int show_flag = 0;
	public int[] value_tab = new int[PARAMS_MAX_VALUES];			

	public MarikaParams() {
		
	}
	
	public void clear() {
		save_month = 0;
		save_date = 0;
		save_hour = 0;
		save_minute = 0;
		script_pos = 0;
		last_script = "";
		last_bg = "";
		last_center = "";
		last_left = "";
		last_right = "";
		last_overlap = "";
		last_bgm = 0;
		show_flag = 0;
		for (int i = 0; i < PARAMS_MAX_VALUES; ++i) {
			value_tab[i] = 0;
		}
	}
	
	public boolean load(int no) {
		//TODO:
		return false;
	}
	
	public boolean save(int no) {
		Calendar cal = Calendar.getInstance();
		save_month = cal.get(Calendar.MONTH) + 1;
		save_date = cal.get(Calendar.DAY_OF_MONTH);
		save_hour = cal.get(Calendar.HOUR_OF_DAY);
		save_minute = cal.get(Calendar.MINUTE);

		ByteBuffer buf = ByteBuffer.allocate(BYTES_MAX);
		buf.position(0);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.clear();
		
		buf.put(HEADER_ID.getBytes());
		buf.put((byte)(save_month & 0xff));
		buf.put((byte)(save_date & 0xff));
		buf.put((byte)(save_hour & 0xff));
		buf.put((byte)(save_minute & 0xff));
		buf.putInt(script_pos);
		buf.put(Arrays.copyOf(last_script.getBytes(), 16));
		buf.put(Arrays.copyOf(last_bg.getBytes(), 16));
		buf.put(Arrays.copyOf(last_center.getBytes(), 16));
		buf.put(Arrays.copyOf(last_left.getBytes(), 16));
		buf.put(Arrays.copyOf(last_right.getBytes(), 16));
		buf.put(Arrays.copyOf(last_overlap.getBytes(), 16));
		buf.putInt(last_bgm);
		buf.putInt(show_flag);
		for (int i = 0; i < PARAMS_MAX_VALUES; i++) {
			buf.putInt(value_tab[i]);
		}
		
		String file = String.format("SAVE%04d.DAT", no + 1);
		
		if (!saveData(file, buf)) {
			return false;
		}
		return true;
		
		//return false;
	}

	public void clearBackCG() {
		last_bg = "";
	}

	public void clearLeftCG() {
		last_left = "";
		last_center = "";
		last_overlap = "";
	}
	
	public void clearRightCG() {
		last_right = "";
		last_center = "";
		last_overlap = "";
	}

	public void clearCenterCG() {
		last_left = "";
		last_right = "";
		last_center = "";
		last_overlap = "";
	}

	public void clearOverlapCG() {
		last_left = "";
		last_right = "";
		last_center = "";
		last_overlap = "";
	}
	
	public void setBackCG(String file) {
		last_bg = file;
	}
	
	public void setLeftCG(String file) {
		last_center = "";
		last_overlap = "";
		last_left = file;
	}
	
	public void setRightCG(String file) {
		last_center = "";
		last_overlap = "";
		last_right = file;
	}
	
	public void setCenterCG(String file) {
		clearOverlapCG();
		last_center = file;
	}
	
	public void setOverlapCG(String file) {
		clearOverlapCG();
		last_overlap = file;
	}

	public void setShowFlag() {
		show_flag = SHOWCG_IMAGE;
	}
	
	public void resetShowFlag() {
		resetShowFlag(false);
	}
	
	public void resetShowFlag(boolean white) {
		show_flag = white? SHOWCG_WHITENESS: SHOWCG_BLACKNESS;
	}
	
	private boolean saveData(String filename, ByteBuffer buf) {
		int len = buf.position();
		byte[] bytes = new byte[len];
		buf.position(0);
		buf.get(bytes);
		OutputStream ostr = null;
		try {
			ostr = new FileOutputStream(filename);
			ostr.write(bytes);
			ostr.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ostr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
