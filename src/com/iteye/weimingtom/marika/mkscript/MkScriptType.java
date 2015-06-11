package com.iteye.weimingtom.marika.mkscript;

public class MkScriptType {
	// <= 8 bytes
	public static final	String SCRIPT_MAGIC = "[SCRIPT]";

	public static final byte SET_VALUE_CMD = 0x00;
	public static final byte CALC_VALUE_CMD = 0x01;
	public static final byte TEXT_CMD = 0x02;
	public static final byte CLEAR_TEXT_CMD = 0x03;
	public static final byte GOTO_CMD = 0x04;
	public static final byte IF_TRUE_CMD = 0x05;
	public static final byte IF_FALSE_CMD = 0x06;
	public static final byte IF_BIGGER_CMD = 0x07;
	public static final byte IF_SMALLER_CMD = 0x08;
	public static final byte IF_BIGGER_EQU_CMD = 0x09;
	public static final byte IF_SMALLER_EQU_CMD = 0x0A;
	public static final byte MENU_INIT_CMD = 0x0B;
	public static final byte MENU_ITEM_CMD = 0x0C;
	public static final byte MENU_CMD = 0x0D;
	public static final byte EXEC_CMD = 0x0E;
	public static final byte LOAD_CMD = 0x0F;
	public static final byte UPDATE_CMD = 0x10;
	public static final byte CLEAR_CMD = 0x11;
	public static final byte MUSIC_CMD = 0x12;
	public static final byte STOPM_CMD = 0x13;
	public static final byte SOUND_CMD = 0x14;
	public static final byte SLEEP_CMD = 0x15;
	public static final byte FADEIN_CMD = 0x16;
	public static final byte FADEOUT_CMD = 0x17;
	public static final byte WIPEIN_CMD = 0x18;
	public static final byte WIPEOUT_CMD = 0x19;
	public static final byte CUTIN_CMD = 0x1A;
	public static final byte CUTOUT_CMD = 0x1B;
	public static final byte WHITEIN_CMD = 0x1C;
	public static final byte WHITEOUT_CMD = 0x1D;
	public static final byte FLASH_CMD = 0x1E;
	public static final byte SHAKE_CMD = 0x1F;
	public static final byte MODE_CMD = 0x20;
	public static final byte SYS_LOAD_CMD = 0x21;
	public static final byte SYS_EXIT_CMD = 0x22;
	public static final byte SYS_CLEAR_CMD = 0x23;
	public static final byte END_CMD = 0x24;
	
	public static final int POSITION_BACK = 0;
	public static final int POSITION_BACKONLY = 1;
	public static final int POSITION_CENTER = 2;
	public static final int POSITION_LEFT = 3;
	public static final int POSITION_RIGHT = 4;
	public static final int POSITION_OVERLAP = 5;

	public static final int MODE_SYSTEM = 0;
	public static final int MODE_SCENARIO = 1;
	
	public static final int UPDATE_NOW = 0;
	public static final int UPDATE_OVERLAP = 1;
	public static final int UPDATE_WIPE = 2;
}
