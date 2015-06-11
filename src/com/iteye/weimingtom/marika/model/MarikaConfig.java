package com.iteye.weimingtom.marika.model;

public class MarikaConfig {
	public static final int MAX_VALUES = 100;
	
	public static final String CompanyName = "HyperWorks";
	public static final String ApplicationName = "ScriptPlayer";
	public static final String ApplicationTitle = "Script player";

	public static final int WindowWidth = 640;
	public static final int WindowHeight = 480;

	public static final String CGPATH = "cgdata/";
	public static final String RULEPATH = "rule/";
	public static final String SCRIPTPATH =	"data/";
	public static final String WAVEPATH = "wave/";

	public static final int MessageFont = 32;
	public static final int MessageWidth = 36; // English : 32, Chinese : 16
	public static final int MenuWidth = 9; // max words number: 继续之前的存档记录
	
	public static final int MessageStyle = 0; // FW_BOLD
	public static final int MessageLine = 6;
	
	public static final int WM_KICKIDLE = 0x036A;
	
	public static final int WhitePixel = 0xffffff;
	public static final int BlackPixel = 0x000000;
	public static final int GrayPixel = 0x8080C0;
	public static final int RedPixel = 0xFF6060;
	
	
	public static final int ActionNop = 0;
	public static final int ActionScriptDone = 1;
	public static final int ActionScriptSetup = 2;
	public static final int ActionScript = 3;
	public static final int ActionGameLoad = 4;
	public static final int ActionGameSave = 5;
	
	public static final int TimerSleep = 0;

	public static final int IS_TIMEDOUT = (1 << 0);
}
