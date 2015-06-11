package com.iteye.weimingtom.marika.model;

public class MarikaParams {
	public static final int PARAMS_MAX_SAVE = 10;
	public static final int PARAMS_MAX_VALUES = MarikaConfig.MAX_VALUES;
	
	public static final int SHOWCG_BLACKNESS = 0;
	public static final int SHOWCG_IMAGE = 1;
	public static final int SHOWCG_WHITENESS = 2;
	
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
		//TODO:
		return false;
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
}
