package com.iteye.weimingtom.marika.vm.window;

import java.nio.ByteBuffer;

import com.iteye.weimingtom.marika.mkscript.MkScriptDumper;
import com.iteye.weimingtom.marika.mkscript.MkScriptType;
import com.iteye.weimingtom.marika.model.MarikaConfig;
import com.iteye.weimingtom.marika.model.MarikaDataTitle;
import com.iteye.weimingtom.marika.model.MarikaMenuItem;
import com.iteye.weimingtom.marika.model.MarikaParams;
import com.iteye.weimingtom.marika.model.MarikaPoint;
import com.iteye.weimingtom.marika.model.MarikaRectangle;
import com.iteye.weimingtom.marika.port.audio.MarikaCDAudio;
import com.iteye.weimingtom.marika.port.audio.MarikaMci;
import com.iteye.weimingtom.marika.port.audio.MarikaWaveOut;
import com.iteye.weimingtom.marika.port.file.MarikaFile;
import com.iteye.weimingtom.marika.port.file.MarikaLog;
import com.iteye.weimingtom.marika.port.file.MarikaResource;
import com.iteye.weimingtom.marika.port.image.MarikaFont;
import com.iteye.weimingtom.marika.port.image.MarikaImage;
import com.iteye.weimingtom.marika.port.window.MarikaWindow;
import com.iteye.weimingtom.marika.port.window.MarikaWindowAdapter;
import com.iteye.weimingtom.marika.vm.action.MarikaAction;
import com.iteye.weimingtom.marika.vm.action.MarikaGameLoadAction;
import com.iteye.weimingtom.marika.vm.action.MarikaGameSaveAction;
import com.iteye.weimingtom.marika.vm.action.MarikaScriptAction;
import com.iteye.weimingtom.marika.vm.effect.MarikaEffect;
import com.iteye.weimingtom.marika.vm.effect.MarikaFadeInEffect;
import com.iteye.weimingtom.marika.vm.effect.MarikaFadeOutEffect;
import com.iteye.weimingtom.marika.vm.effect.MarikaFlashEffect;
import com.iteye.weimingtom.marika.vm.effect.MarikaMixFadeEffect;
import com.iteye.weimingtom.marika.vm.effect.MarikaShakeEffect;
import com.iteye.weimingtom.marika.vm.effect.MarikaWhiteInEffect;
import com.iteye.weimingtom.marika.vm.effect.MarikaWhiteOutEffect;
import com.iteye.weimingtom.marika.vm.effect.MarikaWipeIn2Effect;
import com.iteye.weimingtom.marika.vm.effect.MarikaWipeInEffect;
import com.iteye.weimingtom.marika.vm.effect.MarikaWipeOut2Effect;
import com.iteye.weimingtom.marika.vm.effect.MarikaWipeOutEffect;

public class MarikaMainWin extends MarikaWindowAdapter {
	private static final boolean NO_FIX_UPDATE_BUG = true;

	private static final int MusicCD = 0;
	private static final int MusicOff = 1;

	private static final int MAX_MENU_ITEM = 8;
	private static final int MAX_MENU_TEXT = 60;
	private static final int MAX_SAVE_TEXT = 62;
	
	private static final int None = 0;
	private static final int Left = 1;
	private static final int Right = 2;
	private static final int Both = 3;
	private static final int Center = 4;
	
	private static final int MSG_W = MarikaConfig.MessageFont * MarikaConfig.MessageWidth / 2 + 20;
	private static final int MSG_H = (MarikaConfig.MessageFont + 2) * MarikaConfig.MessageLine + 14;
	private static final int MSG_X = (MarikaConfig.WindowWidth - MSG_W) / 2;
	private static final int MSG_Y = MarikaConfig.WindowHeight - MSG_H - 8;
	private static final int MSG_TEXTOFFSET_X = MSG_X + 10;
	private static final int MSG_TEXTOFFSET_Y = MSG_Y + 7;
	private static final int MSG_TEXTSPACE_Y = 2;
	private static final int WAITMARK_X = MarikaConfig.MessageWidth - 2;
	private static final int WAITMARK_Y = MarikaConfig.MessageLine - 1;
	
	private static final int MENU_X = MSG_X; //20;
	private static final int MENU_Y = MSG_Y - 2;
	private static final int MENU_WIDTH = (MAX_MENU_TEXT + 2) * MarikaConfig.MessageFont / 2;
	private static final int MENU_HEIGHT = (MAX_MENU_ITEM + 1) * MarikaConfig.MessageFont;
	private static final int MENU_MIN_WIDTH = MarikaConfig.MessageFont * MarikaConfig.MenuWidth;
	private static final int MENU_FRAME_WIDTH = 10;
	private static final int MENU_FRAME_HEIGHT = 10;
	private static final int MENU_ITEM_SPACE = 20;
	private static final int MENU_ITEM_HEIGHT = MarikaConfig.MessageFont;

	private static final int SAVE_ITEM_HEIGHT = MarikaConfig.MessageFont * 2; // 32;
	private static final int SAVE_ITEM_SPACE = 4;
	private static final int SAVE_ITEM_INTERVAL = SAVE_ITEM_HEIGHT + SAVE_ITEM_SPACE;
	private static final int SAVE_W = MSG_W;//400; //整体宽度
	private static final int SAVE_H = SAVE_ITEM_INTERVAL * MarikaParams.PARAMS_MAX_SAVE + SAVE_ITEM_HEIGHT; //整体高度
	private static final int SAVE_X = (MarikaConfig.WindowWidth - SAVE_W) / 2; //整体居中
	private static final int SAVE_Y = (MarikaConfig.WindowHeight - SAVE_H) / 2; //整体居中
	private static final int SAVE_TEXT_OFFSET_X = SAVE_X + 10; //存档标题x坐标
	private static final int SAVE_TEXT_OFFSET_Y = SAVE_Y + 12;//8;  //存档标题y坐标
	private static final int SAVE_TITLE_WIDTH = MarikaConfig.MessageFont * 4; //72; //存档标题宽度

	//--------------------
	//flags
	private static final int TextMessage = 1 << 0;
	private static final int TextWaitMark = 1 << 1;
	private static final int MenuFrame = 1 << 2;
	private static final int SaveTitle = 1 << 3;
	
	private static final int MenuItemFirst = 4;
	private static final int SaveItemFirst = 12;
	
	private static int menuItem(int n) { 
		return 1 << (MenuItemFirst + n); 
	}
	
	private static int saveItem(int n) { 
		return 1 << (SaveItemFirst + n); 
	}
	
	//extFlags
	//--------------------
	
	private static final MarikaRectangle[] POSITION = {
		new MarikaRectangle(0, 0, 0, 0), //None = 0;
	    new MarikaRectangle(0, 0, MarikaConfig.WindowWidth / 2, MarikaConfig.WindowHeight), //Left = 1;
	    new MarikaRectangle(MarikaConfig.WindowWidth / 2, 0, MarikaConfig.WindowWidth / 2, MarikaConfig.WindowHeight), //Right = 2;
	    new MarikaRectangle(0, 0, MarikaConfig.WindowWidth, MarikaConfig.WindowHeight), //Both = 3;
	    new MarikaRectangle(MarikaConfig.WindowWidth / 4, 0, MarikaConfig.WindowWidth / 2, MarikaConfig.WindowHeight), //Center = 4;
	};
	
	private MarikaFont hFont = new MarikaFont();
	private int musicMode;
	
	private MarikaMci music;
	private MarikaCDAudio cdaudio;
	private int musicNo;
	private MarikaWaveOut wave;
	
	private MarikaParams loadParam = new MarikaParams();

	private MarikaAction mAction = new MarikaAction();
	private MarikaAction nopAction = new MarikaAction();
	private MarikaScriptAction scriptAction;
	private MarikaGameLoadAction gameLoadAction = new MarikaGameLoadAction(); 
	private MarikaGameSaveAction gameSaveAction = new MarikaGameSaveAction();
	
	private MarikaResource res;
	private MarikaImage viewImage;
	private MarikaImage mixedImage;
	private MarikaImage backLayer;
	private MarikaImage overlapLayer;
	private MarikaImage mixImage;
	private MarikaImage maskImage;
	private int overlapFlags;
	private boolean textDisplay;
	private boolean waitMarkShowing;
	
	//-----------------------------------
	//FIXME:new added
	private static final int CONTEXT_MENU_W = MarikaConfig.MessageFont * 4;
	private static final int CONTEXT_MENU_H = MarikaConfig.MessageFont * 2;
	private static final int CONTEXT_MENU_X = MSG_X + MSG_W - CONTEXT_MENU_W;
	private static final int CONTEXT_MENU_Y = MSG_Y - CONTEXT_MENU_H;
	private static final int CONTEXT_MENU_TEXT_OFFSET_X = CONTEXT_MENU_X + 30;
	private static final int CONTEXT_MENU_TEXT_OFFSET_Y = CONTEXT_MENU_Y + 12;
	private MarikaRectangle contextMenuRect = new MarikaRectangle(CONTEXT_MENU_X, CONTEXT_MENU_Y, CONTEXT_MENU_W, CONTEXT_MENU_H);
	private int contextMenuColor = MarikaConfig.WhitePixel;
	
	public void selectContextMenu(int index, boolean select) {
		if (index >= 0) {
			contextMenuColor = select? MarikaConfig.RedPixel: MarikaConfig.WhitePixel;
			//NOTE: when saveShow == true,
			//the screen will be repaint two times.
			//by weimingtom
			if (!saveShow) {
				mixing(contextMenuRect, TextMessage, 0xffffffff);
				copyAndRepaint(contextMenuRect);
			}
		}
	}
	
	public int getContextMenuSelect(MarikaPoint point) {
		if (saveShow) {
			return -1;
		} else {
			if (textShow) {
				if (point.x < contextMenuRect.x || 
					point.y < contextMenuRect.y || 
					point.x >= contextMenuRect.x + contextMenuRect.width || 
					point.y >= contextMenuRect.y + contextMenuRect.height) {
					return -1;
				} else {
					return 0;
				}
			} else {
				return -1;
			}
		}
	}
	
	//-----------------------------------
	
	private MarikaRectangle invalidRect = new MarikaRectangle(0, 0, 0, 0); // 无效区域
	private MarikaRectangle textRect = new MarikaRectangle(MSG_X, MSG_Y, MSG_W, MSG_H);
	private MarikaRectangle waitMarkRect = new MarikaRectangle(msgX(WAITMARK_X), msgY(WAITMARK_Y),
			MarikaConfig.MessageFont, MarikaConfig.MessageFont);
	private MarikaRectangle menuRect = new MarikaRectangle(0, 0, 0, 0);
	private MarikaRectangle overlapBounds = new MarikaRectangle(0, 0, 0, 0);
	private boolean backShow;
	private boolean overlapShow;
	private boolean textShow;
	private boolean menuShow;
	private boolean saveShow;
	private MarikaRectangle saveRect = new MarikaRectangle(SAVE_X, SAVE_Y, SAVE_W, SAVE_H);
	private int bgColor;
	
	private MarikaEffect viewEffect;
	private int timePeriod;
	
	private String[] msgBuffer = new String[MarikaConfig.MessageLine];
	
	private int curX;
	private int curY;
	
	private MarikaMenuItem[] menuBuffer = new MarikaMenuItem[MarikaMainWin.MAX_MENU_ITEM];
	private int menuCount;
	
	private boolean isSaveMenu;
	private MarikaDataTitle[] dataTitle = new MarikaDataTitle[MarikaParams.PARAMS_MAX_SAVE];
	
	public void repaintView(MarikaRectangle rect) {
		this.getWindow().draw(viewImage, rect);
	}
	
	public void copyAndRepaint(MarikaRectangle rect) {
		//FIXME:
		//Rectangle allRactangle = new Rectangle(320, 0, 320, 480);
		MarikaLog.trace("MarikaMainWin::copyAndRepaint rect == " + rect.x + " " + rect.y + " " + rect.width + " " + rect.height);
		viewImage.copy(mixedImage, rect);
		if (NO_FIX_UPDATE_BUG) {
			repaintView(/*allRactangle*/rect);
		}
	}
	
	public int getMenuSelect(MarikaPoint point) {
		if (point.x < menuRect.x + MENU_FRAME_WIDTH || 
			point.y < menuRect.y + MENU_FRAME_HEIGHT || 
			point.x >= menuRect.x + menuRect.width - MENU_FRAME_WIDTH || 
			point.y >= menuRect.y + menuRect.height - MENU_FRAME_HEIGHT)
			return -1;
		return (point.y - menuRect.y - MENU_FRAME_WIDTH) / (MENU_ITEM_HEIGHT + MENU_ITEM_SPACE);
	}
	
	public void wipeIn(MarikaRectangle rect) {
		wipeIn(rect, -1);
	}
	
	public void wipeIn(MarikaRectangle rect, int pattern) {
		MarikaRectangle rect2 = rect.cloneRect();
		updateView(false);
		switch (pattern) {
		case 1:
			viewEffect = new MarikaWipeInEffect(this, viewImage, mixedImage, rect2);
			break;
		
		default:
			viewEffect = new MarikaWipeIn2Effect(this, viewImage, mixedImage, rect2);
			break;
		}
	}
	
	public void wipeIn2() {
		wipeIn2(1);
	}
	
	public void wipeIn2(int pattern) {
		wipeIn(new MarikaRectangle(0, 0, MarikaConfig.WindowWidth, MarikaConfig.WindowHeight), pattern);
	}
	
	public void wipeOut(int pattern) {
		hideMessageWindow();
		switch (pattern) {
		case 1:
			viewEffect = new MarikaWipeOutEffect(this, viewImage, mixedImage);
			break;
		
		default:
			viewEffect = new MarikaWipeOut2Effect(this, viewImage, mixedImage);
			break;
		}
		hideAllLayer(MarikaConfig.BlackPixel);
	}
	
	public void fadeIn() {
		updateView(false);
		viewEffect = new MarikaFadeInEffect(this, viewImage, mixedImage);
	}
	
	public void fadeOut() {
		hideMessageWindow();
		viewEffect = new MarikaFadeOutEffect(this, viewImage, mixedImage);
		hideAllLayer(MarikaConfig.BlackPixel);
	}
	
	public void cutIn(MarikaRectangle rect) {
		MarikaRectangle rect2 = rect.cloneRect();
		updateView(false);
		copyAndRepaint(rect2);
	}
	
	public void cutIn2() {
		cutIn(new MarikaRectangle(0, 0, MarikaConfig.WindowWidth, MarikaConfig.WindowHeight));
	}
	
	public void cutOut(boolean white) {
		hideMessageWindow();
		hideAllLayer(white? MarikaConfig.WhitePixel: MarikaConfig.BlackPixel);
		invalidate(POSITION[Both]);
		updateView();
	}
	
	public void whiteIn() {
		updateView(false);
		viewEffect = new MarikaWhiteInEffect(this, viewImage, mixedImage);
	}
	
	public void whiteOut() {
		hideMessageWindow();
		viewEffect = new MarikaWhiteOutEffect(this, viewImage, mixedImage);
		hideAllLayer(MarikaConfig.WhitePixel);
	}
	
	public void mixFade(MarikaRectangle rect) {
		MarikaRectangle rect2 = rect.cloneRect();
		updateView(false);
		viewEffect = new MarikaMixFadeEffect(this, viewImage, mixedImage, rect2, mixImage, maskImage);
	}
	
	public void shake() {
		viewEffect = new MarikaShakeEffect(this, viewImage);
	}
	
	public void flash() {
		viewEffect = new MarikaFlashEffect(this, viewImage);
	}
	
	public void stopWipe() {
		viewEffect = null;
	}
	
	public boolean isLoadOK() {
		return mAction.isScriptRunning() && scriptAction.isSaveLoadOK();
	}
	
	public boolean isSaveOK() {
		return mAction.isScriptRunning() && scriptAction.isSaveLoadOK();
	}
	
	public MarikaMainWin() {
		
	}

	@Override 
	public void onLButtonUp(MarikaPoint point) {
		mAction.onActionLButtonUp(point);
	}

	@Override 
	public void onLButtonDown(MarikaPoint point) {
		mAction.onActionLButtonDown(point);
	}
	
	@Override 
	public void onRButtonUp(MarikaPoint point) {
		mAction.onActionRButtonUp(point);
	}

	@Override 
	public void onRButtonDown(MarikaPoint point) {
		mAction.onActionRButtonDown(point);
	}
		
	@Override 
	public void onMouseMove(MarikaPoint point) {
		mAction.onActionMouseMove(point);
	}
	
	@Override
	public void onKeyDown(int key) {
		mAction.onActionKeyDown(key);
	}

	@Override 
	public boolean onIdle(int count) {
		if (viewEffect != null) {
			if (viewEffect.step2(System.currentTimeMillis())) {
				return true;
			}
			stopWipe();
			mAction.onActionWipeDone();
		}
		if (mAction != null) {
			return mAction.onActionIdleAction();
		} else {
			return false;
		}
	}
	
	public void sleep(int i) {
		//FIXME:
	}
	
	@Override 
	public boolean onCreate() {
		MarikaLog.traceMemory("MarikaMainWin::onCreate");
		
		res = new MarikaResource();
		viewImage = new MarikaImage(this.res, 0, 0);
		mixedImage = new MarikaImage(this.res, 0, 0);
		backLayer = new MarikaImage(this.res, 0, 0);
		overlapLayer = new MarikaImage(this.res, 0, 0);
		mixImage = new MarikaImage(this.res, 0, 0);
		maskImage = new MarikaImage(this.res, 0, 0);
		
		cdaudio = new MarikaCDAudio(this.res);
		wave = new MarikaWaveOut(this.res);
		
		this.mAction = nopAction;
		this.scriptAction = new MarikaScriptAction(res);
		for (int i = 0; i < menuBuffer.length; i++) {
			menuBuffer[i] = new MarikaMenuItem();
		}
		for (int i = 0; i < dataTitle.length; i++) {
			dataTitle[i] = new MarikaDataTitle();
		}
		
		curX = curY = 0;
		overlapFlags = 0;
		bgColor = MarikaConfig.BlackPixel;
		textDisplay = false;
		waitMarkShowing = false;
		
		overlapBounds.setRect(0, 0, 0, 0);
		backShow = false;
		overlapShow = false;
		textShow = false;
		menuShow = false;
		saveShow = false;
		
		hFont = null;

		musicMode = MusicCD;
		music = cdaudio;
		musicNo = 0;
		
		viewEffect = null;
		
		res.init(getWindow());
		//FIXME:
		if (false) {
			res.loadData("data/main.scr", "data/sample3.dat");
		} else if (false) {
			res.loadText("data/sample3.txt", "data/sample3.txt");
			MkScriptDumper makeScript = new MkScriptDumper(res);
			makeScript.readScript("data/sample3.txt");
			ByteBuffer bytes = makeScript.duplicateBuffer();
			makeScript.close();
			res.loadBytes("data/main.scr", bytes);
		} else {
			res.loadText("data/main.txt", "data/main.txt");
			MkScriptDumper makeScript = new MkScriptDumper(res);
			makeScript.readScript("data/main.txt");
			ByteBuffer bytes = makeScript.duplicateBuffer();
			makeScript.close();
			res.loadBytes("data/main.scr", bytes);
			
			res.loadText("data/start.txt", "data/start.txt");
			MkScriptDumper makeScript2 = new MkScriptDumper(res);
			makeScript2.readScript("data/start.txt");
			ByteBuffer bytes2 = makeScript2.duplicateBuffer();
			makeScript2.close();
			res.loadBytes("data/start.scr", bytes2);
		}
		if (!MarikaFile.USE_LAZY_LOAD) {
			res.loadImage("cgdata/bg001", "cgdata/bg001.jpg");
			res.loadImage("cgdata/bg002", "cgdata/bg002.jpg");
			res.loadImage("cgdata/bg003", "cgdata/bg003.jpg");
			
			res.loadImage("cgdata/megu111", "cgdata/megu111.png");
			res.loadImage("cgdata/megu112", "cgdata/megu112.png");
			res.loadImage("cgdata/megu113", "cgdata/megu113.png");
			res.loadImage("cgdata/megu121", "cgdata/megu121.png");
			res.loadImage("cgdata/megu122", "cgdata/megu122.png");
			res.loadImage("cgdata/megu123", "cgdata/megu123.png");
			res.loadImage("cgdata/megu211", "cgdata/megu211.png");
			res.loadImage("cgdata/megu212", "cgdata/megu212.png");
			res.loadImage("cgdata/megu213", "cgdata/megu213.png");
			res.loadImage("cgdata/megu221", "cgdata/megu221.png");
			res.loadImage("cgdata/megu222", "cgdata/megu222.png");
			res.loadImage("cgdata/megu223", "cgdata/megu223.png");
			res.loadImage("cgdata/megu311", "cgdata/megu311.png");
			res.loadImage("cgdata/megu312", "cgdata/megu312.png");
			res.loadImage("cgdata/megu313", "cgdata/megu313.png");
			res.loadImage("cgdata/megu321", "cgdata/megu321.png");
			res.loadImage("cgdata/megu322", "cgdata/megu322.png");
			res.loadImage("cgdata/megu323", "cgdata/megu323.png");
			
			res.loadImage("cgdata/mesi111", "cgdata/mesi111.png");
	
			res.loadImage("cgdata/sino111", "cgdata/sino111.png");
			res.loadImage("cgdata/sino112", "cgdata/sino112.png");
			res.loadImage("cgdata/sino113", "cgdata/sino113.png");
			res.loadImage("cgdata/sino121", "cgdata/sino121.png");
			res.loadImage("cgdata/sino122", "cgdata/sino122.png");
			res.loadImage("cgdata/sino123", "cgdata/sino123.png");
			res.loadImage("cgdata/sino211", "cgdata/sino211.png");
			res.loadImage("cgdata/sino212", "cgdata/sino212.png");
			res.loadImage("cgdata/sino213", "cgdata/sino213.png");
			res.loadImage("cgdata/sino221", "cgdata/sino221.png");
			res.loadImage("cgdata/sino222", "cgdata/sino222.png");
			res.loadImage("cgdata/sino223", "cgdata/sino223.png");
			res.loadImage("cgdata/sino224", "cgdata/sino224.png");
			res.loadImage("cgdata/sino311", "cgdata/sino311.png");
			res.loadImage("cgdata/sino312", "cgdata/sino312.png");
			res.loadImage("cgdata/sino313", "cgdata/sino313.png");
			res.loadImage("cgdata/sino321", "cgdata/sino321.png");
			res.loadImage("cgdata/sino322", "cgdata/sino322.png");
			res.loadImage("cgdata/sino323", "cgdata/sino323.png");
			res.loadImage("cgdata/sino411", "cgdata/sino411.png");
			res.loadImage("cgdata/sino412", "cgdata/sino412.png");
			res.loadImage("cgdata/sino413", "cgdata/sino413.png");
			res.loadImage("cgdata/sino421", "cgdata/sino421.png");
			res.loadImage("cgdata/sino422", "cgdata/sino422.png");
			res.loadImage("cgdata/sino423", "cgdata/sino423.png");
			
			res.loadImage("cgdata/title001", "cgdata/title001.jpg");
			res.loadImage("cgdata/event1", "cgdata/event1.jpg");
			
	//		res.loadImage("wav/a5_04105", "wav/a5_04105.wav");
	//		res.loadImage("wav/a5_04107", "wav/a5_04107.wav");
	//		res.loadImage("wav/a5_04108", "wav/a5_04108.wav");
	//		res.loadImage("wav/a5_04121", "wav/a5_04121.wav");
	//		res.loadImage("wav/a5_10314", "wav/a5_10314.wav");
			
			res.loadImage("rule/mix", "rule/mix.png");
			res.loadImage("rule/wipe", "rule/wipe.png");
		}
		
		MarikaLog.traceMemory("MarikaMainWin::onCreate viewImage " + (viewImage != null));
		MarikaLog.traceMemory("MarikaMainWin::onCreate mixedImage " + (mixedImage != null));
		MarikaLog.traceMemory("MarikaMainWin::onCreate backLayer " + (backLayer != null));
		MarikaLog.traceMemory("MarikaMainWin::onCreate overlapLayer " + (overlapLayer != null));
		MarikaLog.traceMemory("MarikaMainWin::onCreate maskImage " + (maskImage != null));
		
		if (!viewImage.create(MarikaConfig.WindowWidth, MarikaConfig.WindowHeight) || 
			!mixedImage.create(MarikaConfig.WindowWidth, MarikaConfig.WindowHeight) || 
			!backLayer.create(MarikaConfig.WindowWidth, MarikaConfig.WindowHeight) || 
			!overlapLayer.create(MarikaConfig.WindowWidth, MarikaConfig.WindowHeight) ||
			!maskImage.create(MarikaConfig.WindowWidth, MarikaConfig.WindowHeight)) {
			this.getWindow().messageBox("内存无法配置。\n" +
				"请先关闭其他应用程序，在重新执行这个程序。");
			return false;
		}
		viewImage.clear();
		mixImage.loadRule("mix", 0, 0);
		if ((hFont = new MarikaFont()) == null) {
			this.getWindow().messageBox("找不到宋体。");
			return false;			
		}
		setAction(MarikaConfig.ActionNop);
		onFirstAction();
		return true;
	}
	
	public void onFirstAction() {
		if (music != null) {
			if (!music.open() && musicMode == MusicCD) {
				musicMode = MusicOff;
				music = null;
			}
		}
		wave.open();
		startMainMenu();
	}
	
	public void destroyWindow() {
		//FIXME:
	}
	
	public void onClose() {
		
	}
	
	public MarikaRectangle getRcPaint() {
		return new MarikaRectangle(0, 0, MarikaConfig.WindowWidth, MarikaConfig.WindowHeight);
	}
	
	@Override 
	public void onPaint() {
		this.getWindow().draw(viewImage, getRcPaint());
	}
	
	@Override 
	public void onDestroy() {
		if (viewImage != null) {
			viewImage.recycle();
			viewImage = null;
			MarikaLog.traceMemory("onDestroy viewImage");
		}
		if (mixedImage != null) {
			mixedImage.recycle();
			mixedImage = null;
			MarikaLog.traceMemory("onDestroy mixedImage");
		}
		if (backLayer != null) {
			backLayer.recycle();
			backLayer = null;
			MarikaLog.traceMemory("onDestroy backLayer");
		}
		if (overlapLayer != null) {
			overlapLayer.recycle();
			overlapLayer = null;
			MarikaLog.traceMemory("onDestroy overlapLayer");
		}
		if (mixImage != null) {
			mixImage.recycle();
			mixImage = null;
			MarikaLog.traceMemory("onDestroy mixImage");
		}
		if (maskImage != null) {
			maskImage.recycle();
			maskImage = null;
			MarikaLog.traceMemory("onDestroy maskImage");
		}
		if (music != null) {
			music.stop();
			music.close();
			music = null;
		}
		res.unloadAll();
		super.onDestroy();
		MarikaLog.traceMemory("end super.onDestroy");
		System.gc();
	}
	
	public void onTimer(int id) {
//		KillTimer(id);
		mAction.onActionTimedOut(id);
	}
	
	@Override
	public void onCommand(int notifyCode, int id, MarikaWindow ctrl) {
		switch (id) {
		case MarikaConfig.ID_APP_EXIT:
			//FIXME:未实现
//			SendMessage(WM_CLOSE);
			break;

		case MarikaConfig.ID_APP_ABOUT:
			//FIXME:未实现
//			CAboutDlg().DoModal(IDD_ABOUT, hWnd);
			break;

		case MarikaConfig.ID_MUSIC_CD:
			//FIXME:未实现
			changeMusicMode(MusicCD);
			break;

		case MarikaConfig.ID_MUSIC_OFF:
			//FIXME:未实现
			changeMusicMode(MusicOff);
			break;

		case MarikaConfig.ID_LOADGAME:
			//FIXME:未实现
			if (isLoadOK()) {
				setAction(MarikaConfig.ActionGameLoad);
			}
			break;

		case MarikaConfig.ID_SAVEGAME:
			//FIXME:未实现
			if (isSaveOK()) {
				setAction(MarikaConfig.ActionGameSave);
			}
			break;

		case MarikaConfig.ID_STOPSCRIPT:
			//FIXME:未实现
			if (isSaveOK() && this.getWindow().messageBox("您确定要停止游戏吗？", 
				MarikaConfig.ApplicationTitle,
				MarikaConfig.MB_ICONQUESTION | MarikaConfig.MB_OKCANCEL) == 
				MarikaConfig.IDOK) {
				scriptAction.abort();
			}
			break;

		default:
			break;
		}
	}
	
	private void checkMenuItem(Object hMenu, int id, int status) {
		
	}
	private void enableMenuItem(Object hMenu, int id, int status) {
		
	}
	//FIXME: 这个回调还没有实现
	public void onInitSubMenu(Object hMenu, int id) {
		switch (id) {
		case MarikaConfig.ID_MUSIC_CD:
			checkMenuItem(hMenu, id, musicMode == MusicCD? MarikaConfig.MF_CHECKED: MarikaConfig.MF_UNCHECKED);
			break;

		case MarikaConfig.ID_MUSIC_OFF:
			checkMenuItem(hMenu, id, musicMode == MusicOff? MarikaConfig.MF_CHECKED: MarikaConfig.MF_UNCHECKED);
			break;

		case MarikaConfig.ID_LOADGAME:
			enableMenuItem(hMenu, id, isLoadOK()? MarikaConfig.MF_ENABLED: (MarikaConfig.MF_DISABLED | MarikaConfig.MF_GRAYED));
			break;

		case MarikaConfig.ID_SAVEGAME:
			enableMenuItem(hMenu, id, isSaveOK()? MarikaConfig.MF_ENABLED: (MarikaConfig.MF_DISABLED | MarikaConfig.MF_GRAYED));
			break;

		case MarikaConfig.ID_STOPSCRIPT:
			enableMenuItem(hMenu, id, isSaveOK()? MarikaConfig.MF_ENABLED: (MarikaConfig.MF_DISABLED | MarikaConfig.MF_GRAYED));
			break;
		}
	}
	
	//FIXME: 这个回调还没有实现
	private final static int MCI_NOTIFY_SUCCESSFUL = 0;
	public void onMciNotify(int flag, int id) {
		if (flag == MCI_NOTIFY_SUCCESSFUL) {
			if (music != null && music.GetId() == id) {
				mAction.onActionMusicDone(musicNo);
			} else if (wave.GetId() == id) {
				wave.stop();
				mAction.onActionWaveDone();
			}
		}
	}
	
	public void changeMusicMode(int mode) {
		if (musicMode != mode) {		
			musicMode = mode;
			if (music != null) {
				music.stop();
				music.close();
				music = null;
			}
			switch (musicMode) {
			case MusicCD:
				music = cdaudio;
				if (!music.open()) {
					musicMode = MusicOff;
					music = null;
				}
				break;
			}
			if (music != null && musicNo > 0) {
				music.play(musicNo);
			}
		}
	}
	
	public boolean setAction(int action) {
		MarikaLog.trace("MarikaMainWin::setAction() " + action);
		return setAction(action, 0);
	}
	
	public boolean setAction(int action, int param) {
		stopWipe();
		switch (action) {
		case MarikaConfig.ActionScriptDone:
		case MarikaConfig.ActionScript:
			stopMusic();
			break;
		}
		switch (action) {
		case MarikaConfig.ActionNop:
			mAction = nopAction;
			nopAction.initialize(this);
			break;

		case MarikaConfig.ActionScriptDone:
			startMainMenu();
			break;
		
		case MarikaConfig.ActionScriptSetup:
			scriptAction.setup(loadParam);
			// no break
		
		case MarikaConfig.ActionScript:
			mAction = scriptAction;
			break;
		
		case MarikaConfig.ActionGameLoad:
			showLoadSaveMenu(false);
			gameLoadAction.initialize(this);
			mAction.onActionPause();
			mAction = gameLoadAction;
			break;
		
		case MarikaConfig.ActionGameSave:
			showLoadSaveMenu(true);
			gameSaveAction.initialize(this);
			mAction.onActionPause();
			mAction = gameSaveAction;
			break;
		}
		return true;
	}
	
	public boolean startScript(String name, int mode) {
		scriptAction.initialize(this, mode);
		if (!scriptAction.load(name))
			return false;
		setAction(MarikaConfig.ActionScript);
		return true;
	}
	
	public void startMainMenu() {
		if (!startScript("main", MkScriptType.MODE_SYSTEM))
			destroyWindow();
	}
	
	public void writeMessage(String msg) {
		formatMessage(msg);
		waitMarkShowing = true;
		showMessageWindow();
	}
	
	public void hideWaitMark() {
		if (waitMarkShowing) {
			waitMarkShowing = false;
			if (textShow) {
				mixing(waitMarkRect, TextWaitMark, 0xffffffff);
				copyAndRepaint(waitMarkRect);
			}
		}
	}
	
	public void openMenu() {
		//FIXME:
		int maxlen = MENU_MIN_WIDTH;
		menuRect.y = MENU_Y - ((MENU_FRAME_HEIGHT * 2) + menuCount * (MENU_ITEM_HEIGHT + MENU_ITEM_SPACE)
			- MENU_ITEM_SPACE);
		menuRect.x = MENU_X;
		menuRect.height = MENU_Y - menuRect.y;
		menuRect.width = (MENU_FRAME_WIDTH * 2) + maxlen;
//		menuRect.width = MENU_X + (MENU_FRAME_WIDTH * 2) + maxlen - menuRect.x;
		menuShow = true;
		MarikaLog.trace("MarikaMainWin::openMenu == " + menuRect.x + " " + menuRect.y + " " + menuRect.width + " " + menuRect.height);
		mixing(menuRect);
		if (NO_FIX_UPDATE_BUG) {
			copyAndRepaint(menuRect);
		}
	}
	
	public void selectMenu(int index, boolean select) {
		if (index >= 0) {
			//FIXME:
			menuBuffer[index].color = select? MarikaConfig.RedPixel: MarikaConfig.WhitePixel;
			MarikaRectangle r = new MarikaRectangle(
				menuRect.x + MENU_FRAME_WIDTH,
				menuRect.y + MENU_FRAME_HEIGHT + (MENU_ITEM_HEIGHT + MENU_ITEM_SPACE) * index,
				menuRect.width - MENU_FRAME_WIDTH * 2,
				/*MarikaConfig.MessageFont*/MENU_ITEM_HEIGHT + 5
			);
			//FIXME: more 5px, i don't know why some white pixels appear in menu text.
			mixing(r, menuItem(index), 0xffffffff);
			copyAndRepaint(r);
		}
	}
	
	public void showMessageWindow() {
		textDisplay = true;
		textShow = true;
		//FIXME:
		invalidate(textRect.union(contextMenuRect));
		updateView();
	}
	
	public void hideMessageWindow() {
		hideMessageWindow(true);
	}
	
	public void hideMessageWindow(boolean update) {
		textDisplay = false;
		if (textShow) {
			textShow = false;
			invalidate(textRect.union(contextMenuRect));
			if (update)
				updateView();
		}
	}
	
	public void flipMessageWindow() {
		if (textDisplay) {
			textShow = textShow? false: true;
			invalidate(textRect.union(contextMenuRect));
			updateView();
		}
	}
	
	public void showOverlapLayer(int pos) {
		if (overlapShow) {
			if ((overlapFlags == Center && pos != Center) ||
			    (overlapFlags != Center && pos == Center)) {	
				MarikaLog.trace("MarikaMainWin::showOverlapLayer 显示在中间，删除所有之前显示的图形");
				invalidate(POSITION[overlapFlags]);
				overlapFlags = None;
				overlapBounds.setRect(0, 0, 0, 0);
			}
		}
		overlapFlags |= pos;
		overlapBounds.copyRect(POSITION[overlapFlags]);
		overlapShow = true;
		MarikaLog.trace("MarikaMainWin::showOverlapLayer Invalidate == " + POSITION[pos]);
		invalidate(POSITION[pos]);
	}
	
	public void hideOverlapLayer(int pos) {
		if (overlapShow) {	
			if ((overlapFlags == Center && pos != Center) ||
			    (overlapFlags != Center && pos == Center)) {	
				invalidate(POSITION[overlapFlags]);
				overlapFlags = None;
				overlapBounds.setRect(0, 0, 0, 0);
			}
		}
		overlapFlags &= ~pos;
		overlapBounds.copyRect(POSITION[overlapFlags]);
		if (overlapFlags == None)
			overlapShow = false;
		invalidate(POSITION[pos]);
	}
	
	public void hideMenuWindow() {
		hideMenuWindow(true);
	}
	
	public void hideMenuWindow(boolean update) {
		if (menuShow) {
			menuShow = false;
			invalidate(menuRect);
			if (update)
				updateView();
		}
	}
	
	public int getMenuItemCount() { 
		return menuCount; 
	}
	
	public int getMenuAnser(int index) { 
		return menuBuffer[index].anser; 
	}
	
	public void hideAllLayer(int pix) {
		bgColor = pix;
		backShow = false;
		overlapShow = false;
		overlapFlags = None;
		overlapBounds.setRect(0, 0, 0, 0);
	}
	
	
	public void mixing(MarikaRectangle rect) {
		mixing(rect, 0xffffffff, 0xffffffff);
	}
	
	public void mixing(MarikaRectangle rect, int flags, int extFlags) {
		if (backShow) {
			mixedImage.copy(backLayer, rect);
		} else {
			mixedImage.fillRect(rect, bgColor);
		}
		if (overlapShow) {
			MarikaLog.trace("MarikaMainWin::mixing, OverlapShow" + overlapBounds.intersection(rect));
			mixedImage.mixImage(overlapLayer, overlapBounds.intersection(rect), 0x00FF00);
		}
		if (saveShow) {
			if ((flags & SaveTitle) != 0) {
				mixedImage.drawFrameRect(new MarikaRectangle(SAVE_X, SAVE_Y, SAVE_TITLE_WIDTH, SAVE_ITEM_HEIGHT), 0xffffff);
				mixedImage.drawText(hFont, SAVE_TEXT_OFFSET_X, SAVE_TEXT_OFFSET_Y,
					isSaveMenu? "储存"/*"存档"*/: "读取"/*"装入"*/, MarikaConfig.WhitePixel);
			}
			for (int i = 0; i < MarikaParams.PARAMS_MAX_SAVE; i++) {
				if ((flags & saveItem(i)) != 0) {
					int	y = (i + 1) * SAVE_ITEM_INTERVAL;
					mixedImage.drawFrameRect(new MarikaRectangle(SAVE_X, SAVE_Y + y, SAVE_W, SAVE_ITEM_HEIGHT),
						dataTitle[i].color);
					mixedImage.drawText(hFont, SAVE_TEXT_OFFSET_X, SAVE_TEXT_OFFSET_Y + y,
						dataTitle[i].title, dataTitle[i].color);
				}
			}
		} else {
			if (textShow) {
				if ((flags & TextMessage) != 0) {
					//FIXME:
					MarikaLog.trace("MarikaMainWin::mixing: TextRect == " + textRect.x + " " + textRect.y + " " + textRect.width + " " + textRect.height);
					mixedImage.drawFrameRect(textRect, 0xffffff);
					//FIXME:
					mixedImage.drawFrameRect(contextMenuRect, contextMenuColor);
					mixedImage.drawText(hFont, CONTEXT_MENU_TEXT_OFFSET_X, CONTEXT_MENU_TEXT_OFFSET_Y,
							"菜单", contextMenuColor);
					
					for (int i = 0; i < MarikaConfig.MessageLine; i++) {
						MarikaLog.trace("MarikaMainWin::mixing DrawText " + msgX(0) + "," + msgY(i) + "," + msgBuffer[i]);
						mixedImage.drawText(hFont, msgX(0), msgY(i), msgBuffer[i], MarikaConfig.WhitePixel);
					}
				} else {
					MarikaRectangle temp = textRect.intersection(rect);
					if (temp.isEmpty()) {
						temp.setRect(0, 0, 0, 0); //FIXME:Java可能会产生负数的宽高值
					}
					MarikaLog.trace("MarikaMainWin::mixing: FillHalfToneRect " + temp.x + " " + temp.y + " " + temp.width + " " + temp.height);
					mixedImage.fillHalfToneRect(temp);
				}
				if (waitMarkShowing && (flags & TextWaitMark) != 0)
					mixedImage.drawText(hFont, msgX(WAITMARK_X), msgY(WAITMARK_Y), "▼", MarikaConfig.WhitePixel);
			}
			if (menuShow) {
				if ((flags & MenuFrame) != 0) {
					mixedImage.drawFrameRect(menuRect, 0xffffff);
				} else {
					mixedImage.fillHalfToneRect(menuRect.intersection(rect));
				}
				for (int i = 0; i < menuCount; i++) {
					if ((flags & menuItem(i)) != 0) {
						mixedImage.drawText(hFont,
							menuRect.x + MENU_FRAME_WIDTH,
							menuRect.y + MENU_FRAME_HEIGHT + (MENU_ITEM_HEIGHT + MENU_ITEM_SPACE) * i,
							menuBuffer[i].text, menuBuffer[i].color);
						MarikaLog.trace("-->color:0x" + Integer.toHexString(menuBuffer[i].color) + "-->" + menuBuffer[i].text);
					}
				}
			}
		}
	}
	
	public boolean updateView() {
		return updateView(true);
	}
	
	public boolean updateView(boolean repaint) {
		if (!invalidRect.isEmpty()) {	
			if (NO_FIX_UPDATE_BUG) {
				mixing(invalidRect);
				if (repaint) {
					copyAndRepaint(invalidRect);
				}
			}
			invalidRect.setRect(0, 0, 0, 0);
			return true;
		}
		return false;
	}
	
	public boolean loadImageBack(String name) {
		backShow = true;
		invalidate(POSITION[Both]);
		return backLayer.loadImage(name, 0, 0);
	}
	
	public boolean loadImageOverlap(String name) {
		return loadImageOverlap(name, MarikaMainWin.Both);
	}
	
	public boolean loadImageOverlap(String name, int pos) {
		showOverlapLayer(pos);
		return overlapLayer.loadImage(name, POSITION[pos].x, POSITION[pos].y);
	}
	
	public boolean clearImageBack() {
		backShow = false;
		invalidate(POSITION[Both]);
		return true;
	}
	
	public boolean kinsoku(String p) {
		//FIXME:
		return false;
	}
	
	public static final int STR_LIMIT =	(MarikaConfig.MessageWidth - 2);
	public static final int STR_WIDTH =	(STR_LIMIT - 2);
	
	public void clearMessage() {
		hideMessageWindow();
		curX = curY = 0;
		for (int i = 0; i < MarikaConfig.MessageLine; i++) {
			msgBuffer[i] = "";
		}
	}
	
	public int formatMessageOld(String msg) {
		//FIXME:
		curX = curY = 0;
		String[] lines = msg.split("\n", MarikaConfig.MessageLine);
		for (int i = 0; i < MarikaConfig.MessageLine; i++) {
			if (i < lines.length && lines[i] != null) {
				msgBuffer[i] = lines[i];
				MarikaLog.trace("formatMessage" + " " + i + " " + msgBuffer[i]);
				curY++;
			} else {
				MarikaLog.trace("formatMessage" + " " + i);
				msgBuffer[i] = "";
			}
		}
		MarikaLog.trace("formatMessage CurY == " + curY);
		return curY;
	}

	//FIXME:
	public int formatMessage(String msg) {
		curX = curY = 0;
		for (int i = 0; i < MarikaConfig.MessageLine; i++) {
			msgBuffer[i] = "";
		}
		for (int i = 0; i < msg.length() && curY < MarikaConfig.MessageLine;) {
			if (msg.charAt(i) == '\n') {
				i++;
				curX = 0;
				curY++;
			} else {
				if (curX > STR_LIMIT) {
					curX = 0;
					curY++;
				}
				char ch = msg.charAt(i);
				msgBuffer[curY] += ch;
				i++;
				curX += 2;
			}
		}
		if (curX > 0 && curY < MarikaConfig.MessageLine) {
			curY++;
		}
		return curY;
	}
	
	public void setMenuItem(String str, int anser) {
		//FIXME:
		int n = str.length();
		menuBuffer[menuCount].text = str;
		menuBuffer[menuCount].anser = anser;
		menuBuffer[menuCount].length = n;
		//FIXME:
		menuBuffer[menuCount].color = MarikaConfig.WhitePixel;
		menuCount++;
	}
	
	public void clearMenuItemCount() { 
		menuCount = 0; 
	}
	
	public void loadGame(int no) {
		if (!loadParam.load(no)) {
			this.getWindow().messageBox("无法读取。");
			return;
		}
		scriptAction.initialize(this, MkScriptType.MODE_SCENARIO);
		if (scriptAction.load(loadParam.last_script)) {
			hideMessageWindow(false);
			hideMenuWindow(false);
			if (saveShow) {
				saveShow = false;
				invalidate(saveRect);
			}
			updateView();
			setAction(MarikaConfig.ActionScriptSetup);
		}
	}
	
	public void saveGame(int no, int flags) {
		if (!scriptAction.Params.save(no)) {
			this.getWindow().messageBox("无法存储。");
			return;
		}
		cancelLoadSaveMenu(flags);
	}
	
	public void showLoadSaveMenu(boolean isSave) {
		isSaveMenu = isSave;
		saveShow = true;
		for (int i = 0; i < MarikaParams.PARAMS_MAX_SAVE; i++) {
			MarikaParams param = new MarikaParams();
			if (param.load(i)) {
				dataTitle[i].activate = true;
				dataTitle[i].title = (i + 1) + ": " + 
					param.save_month + "/" + param.save_date + " " +
					param.save_hour + ":" + param.save_minute;
			} else {
				dataTitle[i].activate = isSaveMenu? true: false;
				dataTitle[i].title = (i + 1) + ": -- no data --";
			}
			dataTitle[i].color = dataTitle[i].activate? MarikaConfig.WhitePixel: MarikaConfig.GrayPixel;
		}
		invalidate(saveRect);
		if (textShow)
			invalidate(textRect.union(contextMenuRect));
		if (menuShow)
			invalidate(menuRect);
		updateView();
	}
	
	public void hideLoadSaveMenu() {
		saveShow = false;
		invalidate(saveRect);
		if (textShow)
			invalidate(textRect.union(contextMenuRect));
		if (menuShow)
			invalidate(menuRect);
		updateView();
	}
	
	public void cancelLoadSaveMenu(int flags) {
		hideLoadSaveMenu();
		mAction = scriptAction;
		mAction.onActionResume();
		if ((flags & MarikaConfig.IS_TIMEDOUT) != 0) {
			mAction.onActionTimedOut(MarikaConfig.TimerSleep);
		}
	}
	
	public void selectLoadSaveMenu(int index, boolean select) {
		if (index >= 0) {
			dataTitle[index].color = select? MarikaConfig.RedPixel: MarikaConfig.WhitePixel;
			int y = index * SAVE_ITEM_INTERVAL + SAVE_ITEM_INTERVAL;
			MarikaRectangle rect = new MarikaRectangle(
				SAVE_X, SAVE_Y + y, SAVE_W, SAVE_ITEM_HEIGHT + 5);
			//FIXME: more 5px, i don't know why some white pixels appear in menu text.
			mixing(rect, saveItem(index), 0xffffffff);
			copyAndRepaint(rect);
		}
	}
	
	public int getLoadSaveSelect(MarikaPoint point) {
		if (point.x >= SAVE_X && point.x < SAVE_X + SAVE_W && 
			point.y >= SAVE_Y + SAVE_ITEM_INTERVAL) {
			int index = point.y - SAVE_Y - SAVE_ITEM_INTERVAL;
			if (index % SAVE_ITEM_INTERVAL < SAVE_ITEM_HEIGHT) {
				index /= SAVE_ITEM_INTERVAL;
				if (index < MarikaParams.PARAMS_MAX_SAVE && dataTitle[index].activate)
					return index;
			}
		}
		return -1;
	}
	
	public int nextLoadSaveSelect(int index) {
		for (int i = 1; i <= MarikaParams.PARAMS_MAX_SAVE; i++) {
			int next = (index + i) % MarikaParams.PARAMS_MAX_SAVE;
			if (dataTitle[next].activate)
				return next;
		}
		return -1;
	}
	
	public int prevLoadSaveSelect(int index) {
		for (int i = MarikaParams.PARAMS_MAX_SAVE - 1; i > 0; i--) {
			int prev = (index + i) % MarikaParams.PARAMS_MAX_SAVE;
			if (dataTitle[prev].activate)
				return prev;
		}
		return -1;
	}
	
	public boolean startMusic(int no) {
		if (musicNo != no) {
			musicNo = no;
			if (music != null) {
				music.stop();
				return music.play(no);
			}
		}
		return true;
	}
	
	public boolean restartMusic() {
		if (music != null)
			return music.replay();
		return true;
	}
	
	public boolean stopMusic() {
		musicNo = 0;
		if (music != null)
			return music.stop();
		return true;
	}
	
	public boolean startWave(String name) {
		String path;
		path = MarikaConfig.WAVEPATH + name + ".wav";
		return wave.play2(path);
	}
	
	public void invalidate(MarikaRectangle rect) { 
		MarikaLog.trace("MarikaMainWin::invalidate() InvalidRect == " + invalidRect.x + "," + invalidRect.y + "," + invalidRect.width + "," + invalidRect.height);
		MarikaLog.trace("MarikaMainWin::invalidate() rect " + rect.x + "," + rect.y + "," + rect.width + "," + rect.height);
		if (invalidRect.isEmpty()) {
			invalidRect = rect.cloneRect();
		} else if (rect.isEmpty()) {
			//InvalidRect = InvalidRect;
		} else {
			invalidRect = invalidRect.union(rect); 
		}
		MarikaLog.trace("MarikaMainWin::invalidate() InvalidRect(2) == " + invalidRect.x + "," + invalidRect.y + "," + invalidRect.width + "," + invalidRect.height);
	}
	
	public int msgX(int x) { 
		return x * MarikaConfig.MessageFont / 2 + MarikaMainWin.MSG_TEXTOFFSET_X; 
	}
	
	public int msgY(int y) { 
		return y * (MarikaConfig.MessageFont + MarikaMainWin.MSG_TEXTSPACE_Y) + MarikaMainWin.MSG_TEXTOFFSET_Y; 
	}
	
	public void clearBack() { 
		clearImageBack(); 
	}
	
	public void clearCenter() { 
		hideOverlapLayer(MarikaMainWin.Center); 
	}
	
	public void clearLeft() { 
		hideOverlapLayer(MarikaMainWin.Left); 
	}
	
	public void clearRight() { 
		hideOverlapLayer(MarikaMainWin.Right); 
	}
	
	public void clearOverlap() { 
		hideOverlapLayer(MarikaMainWin.Both); 
	}
	
	public boolean loadImageLeft(String name) { 
		return loadImageOverlap(name, MarikaMainWin.Left); 
	}
	
	public boolean loadImageRight(String name) { 
		return loadImageOverlap(name, MarikaMainWin.Right); 
	}
	
	public boolean loadImageCenter(String name) { 
		MarikaLog.trace("MarikaMainWin::loadImageCenter");
		return loadImageOverlap(name, MarikaMainWin.Center); 
	}
	
	public MarikaRectangle getInvalidRect() { 
		return invalidRect; 
	}
}
