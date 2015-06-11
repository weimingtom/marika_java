package com.iteye.weimingtom.marika.mkscript;

import java.nio.ByteBuffer;

import com.iteye.weimingtom.marika.mkscript.command.CalcValueCommand;
import com.iteye.weimingtom.marika.mkscript.command.ClearCommand;
import com.iteye.weimingtom.marika.mkscript.command.Command;
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
import com.iteye.weimingtom.marika.port.file.MarikaLog;

public class MkScriptBufferUtil {
	private static final boolean DEBUG_WRITE_COMMAND = true;
	
	private static SetValueCommand setValueCmd = new SetValueCommand(MkScriptType.SET_VALUE_CMD);
	private static CalcValueCommand calcValueCmd = new CalcValueCommand(MkScriptType.CALC_VALUE_CMD);
	private static TextCommand textCmd = new TextCommand(MkScriptType.TEXT_CMD);
	private static GotoCommand gotoCmd = new GotoCommand(MkScriptType.GOTO_CMD);
	private static IfCommand ifCmd = new IfCommand((byte)0);
	private static MenuItemCommand menuItemCmd = new MenuItemCommand(MkScriptType.MENU_ITEM_CMD);
	private static MenuCommand menuCmd = new MenuCommand(MkScriptType.MENU_CMD);
	private static ExecCommand execCmd = new ExecCommand(MkScriptType.EXEC_CMD);
	private static LoadCommand loadCmd = new LoadCommand(MkScriptType.LOAD_CMD);
	private static UpdateCommand updateCmd = new UpdateCommand(MkScriptType.UPDATE_CMD);
	private static ClearCommand clearCmd = new ClearCommand(MkScriptType.CLEAR_CMD);
	private static MusicCommand musicCmd = new MusicCommand(MkScriptType.MUSIC_CMD);
	private static SoundCommand soundCmd = new SoundCommand(MkScriptType.SOUND_CMD);
	private static SleepCommand sleepCmd = new SleepCommand(MkScriptType.SLEEP_CMD);
	private static WipeinCommand wipeinCmd = new WipeinCommand(MkScriptType.WIPEIN_CMD);
	private static WipeoutCommand wipeoutCmd = new WipeoutCommand(MkScriptType.WIPEOUT_CMD);
	private static ModeCommand modeCmd = new ModeCommand(MkScriptType.MODE_CMD);
	
	private static Command normalCmd = new Command((byte)0);
	private static Command current_read_cmd;
	
	public MkScriptBufferUtil() {
		
	}
	
	public static Command getCommand() {
		return current_read_cmd;
	}
	
	public static Command newCommand(int id) {
		switch(id) {
			case MkScriptType.SET_VALUE_CMD:
				return setValueCmd;
			
			case MkScriptType.CALC_VALUE_CMD:
				return calcValueCmd;
			
			case MkScriptType.TEXT_CMD:
				return textCmd;
		
			case MkScriptType.CLEAR_TEXT_CMD:
				return clearCmd;
			
			case MkScriptType.GOTO_CMD:
				return gotoCmd;
				
			case MkScriptType.IF_TRUE_CMD:
			case MkScriptType.IF_FALSE_CMD:
			case MkScriptType.IF_BIGGER_CMD:
			case MkScriptType.IF_SMALLER_CMD:
			case MkScriptType.IF_BIGGER_EQU_CMD:
			case MkScriptType.IF_SMALLER_EQU_CMD:
				return ifCmd;
			
			case MkScriptType.MENU_ITEM_CMD:
				return menuItemCmd;
				
			case MkScriptType.MENU_CMD:
				return menuCmd;
			
			case MkScriptType.EXEC_CMD:
				return execCmd;
				
			case MkScriptType.LOAD_CMD:
				return loadCmd;
				
			case MkScriptType.UPDATE_CMD:
				return updateCmd;
			
			case MkScriptType.CLEAR_CMD:
				return clearCmd;
			
			case MkScriptType.MUSIC_CMD:
				return musicCmd;
				
			case MkScriptType.SOUND_CMD:
				return soundCmd;
				
			case MkScriptType.SLEEP_CMD:
				return sleepCmd;
				
			case MkScriptType.WIPEIN_CMD:
				return wipeinCmd;
				
			case MkScriptType.WIPEOUT_CMD:
				return wipeoutCmd;
			
			case MkScriptType.MODE_CMD:
				return modeCmd;
				
			default:
				break;
		}
		return null;
	}
	
	public static int writeCommand(byte id, ByteBuffer bytes) {
		return readWriteCommand(id, bytes, true);
	}
	
	public static int readCommand(byte id, ByteBuffer bytes) {
		return readWriteCommand(id, bytes, false);
	}
	
	private static int readWriteCommand(byte id, ByteBuffer bytes, boolean isWrite) {
		int nBytes = 0;
		if (isWrite == false) {
			id = bytes.get();
			nBytes = bytes.get();
			current_read_cmd = null;
		} else {
			if (DEBUG_WRITE_COMMAND) {
				int pos = bytes.position();
				MarikaLog.trace("<<<<<write bytes : " + 
					pos + "(0x" + Integer.toHexString(pos) + ") => " + 
					(pos + 0xc) + "(0x" + Integer.toHexString(pos + 0xc) + ")"
				);
			}
		}
		switch(id) {
			case MkScriptType.SET_VALUE_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<SET_VALUE_CMD : " + setValueCmd);
				}
				if (isWrite) {
					bytes.put(setValueCmd.type); //1
					bytes.put((byte)8); //2
					bytes.putShort(setValueCmd.value_addr); //4
					bytes.putInt(setValueCmd.set_value); //8
				} else {
					setValueCmd.type = id;
					setValueCmd.value_addr = bytes.getShort();
					setValueCmd.set_value = bytes.getInt();
					setValueCmd.size = nBytes;
					current_read_cmd = setValueCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.CALC_VALUE_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<CALC_VALUE_CMD : " + calcValueCmd);
				}
				if (isWrite) {
					bytes.put(calcValueCmd.type); //1
					bytes.put((byte)8); //2
					bytes.putShort(calcValueCmd.value_addr); //4
					bytes.putInt(calcValueCmd.add_value); //8
				} else {
					calcValueCmd.type = id;
					calcValueCmd.value_addr = bytes.getShort();
					calcValueCmd.add_value = bytes.getInt();
					calcValueCmd.size = nBytes;
					current_read_cmd = calcValueCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.TEXT_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<TEXT_CMD : " + textCmd);
				}
				if (isWrite) {
					bytes.put(textCmd.type); //1
					bytes.put((byte)4); //2
					bytes.putShort(textCmd.msg_len); //4
					bytes.put(textCmd.bytes.toByteArray());
				} else {
					textCmd.msg_len = bytes.getShort();
					textCmd.size = nBytes;
					current_read_cmd = textCmd;
					return nBytes;
				}
				break;
		
			case MkScriptType.CLEAR_TEXT_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<CLEAR_TEXT_CMD : ");
				}
				if (isWrite) {
					bytes.put(MkScriptType.CLEAR_TEXT_CMD); //1
					bytes.put((byte)4); //2
					bytes.put((byte)0); //3 
					bytes.put((byte)0); //4
				} else {
					bytes.position(bytes.position() + 2); //TODO:
					normalCmd.size = nBytes;
					current_read_cmd = normalCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.GOTO_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<GOTO_CMD : " + gotoCmd);
				}
				if (isWrite) {
					bytes.put(gotoCmd.type); //1
					bytes.put((byte)8); //2
					bytes.put((byte)0); //3
					bytes.put((byte)0); //4
					bytes.position(bytes.position() + 4); //8 //label position: +4 //FIXME:
				} else {
					bytes.position(bytes.position() + 2);
					gotoCmd.goto_label = bytes.getInt();
					gotoCmd.size = nBytes;
					current_read_cmd = gotoCmd;
					return nBytes;
				}
				break;
				
			case MkScriptType.IF_TRUE_CMD:
			case MkScriptType.IF_FALSE_CMD:
			case MkScriptType.IF_BIGGER_CMD:
			case MkScriptType.IF_SMALLER_CMD:
			case MkScriptType.IF_BIGGER_EQU_CMD:
			case MkScriptType.IF_SMALLER_EQU_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<IF_XXX_CMD : " + ifCmd);
				}
				if (isWrite) {
					bytes.put(ifCmd.type); //1
					bytes.put((byte)16); //2
					bytes.put(ifCmd.flag); //3
					bytes.put((byte)0); //4
					bytes.putInt(ifCmd.value1); //8
					bytes.putInt(ifCmd.value2); //12
					bytes.position(bytes.position() + 4); //16 //label position: +12 //FIXME:
				} else {
					ifCmd.type = id;
					ifCmd.flag = bytes.get();
					bytes.position(bytes.position() + 1);
					ifCmd.value1 = bytes.getInt();
					ifCmd.value2 = bytes.getInt();
					ifCmd.goto_label = bytes.getInt();
					ifCmd.size = nBytes;
					current_read_cmd = ifCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.MENU_INIT_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<MENU_INIT_CMD : ");
				}
				if (isWrite) {
					bytes.put(MkScriptType.MENU_INIT_CMD); //1
					bytes.put((byte)4); //2
					bytes.put((byte)0); //3
					bytes.put((byte)0); //4
				} else {
					bytes.position(bytes.position() + 2);
					normalCmd.type = MkScriptType.MENU_INIT_CMD;
					normalCmd.size = nBytes;
					current_read_cmd = normalCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.MENU_ITEM_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<MENU_ITEM_CMD : " + menuItemCmd);
				}
				if (isWrite) {
					bytes.put(menuItemCmd.type); //1
					bytes.put((byte)4); //2
					bytes.put(menuItemCmd.number); //3
					bytes.put(menuItemCmd.label_len); //4
					bytes.put(menuItemCmd.bytes.toByteArray());
				} else {
					menuItemCmd.number = bytes.get();
					menuItemCmd.label_len = bytes.get();
					menuItemCmd.size = nBytes;
					current_read_cmd = menuItemCmd;
					return nBytes;
				}
				break;
				
			case MkScriptType.MENU_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<MENU_CMD : " + menuCmd);
				}
				if (isWrite) {
					bytes.put(menuCmd.type); //1
					bytes.put((byte)4); //2
					bytes.putShort(menuCmd.value_addr); //4
				} else {
					menuCmd.value_addr = bytes.getShort();
					menuCmd.size = nBytes;
					current_read_cmd = menuCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.EXEC_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<EXEC_CMD : " + execCmd);
				}
				if (isWrite) {
					bytes.put(execCmd.type); //1
					bytes.put((byte)4); //2
					bytes.put((byte)0); //3
					bytes.put(execCmd.path_len); //4
					bytes.put(execCmd.bytes.toByteArray());
				} else {
					bytes.position(bytes.position() + 1);
					execCmd.path_len = bytes.get();
					execCmd.size = nBytes;
					current_read_cmd = execCmd;
					return nBytes;
				}
				break;
				
			case MkScriptType.LOAD_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<LOAD_CMD : " + loadCmd);
				}
				if (isWrite) {
					bytes.put(loadCmd.type); //1
					bytes.put((byte)4); //2
					bytes.put(loadCmd.flag); //3
					bytes.put(loadCmd.path_len); //4
					bytes.put(loadCmd.bytes.toByteArray());
				} else {
					loadCmd.flag = bytes.get();
					loadCmd.path_len = bytes.get();
					loadCmd.size = nBytes;
					current_read_cmd = loadCmd;
					return nBytes;
				}
				break;
				
			case MkScriptType.UPDATE_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<UPDATE_CMD : " + updateCmd);
				}
				if (isWrite) {
					bytes.put(updateCmd.type); //1
					bytes.put((byte)4); //2
					bytes.put(updateCmd.flag); //3
					bytes.put((byte)0); //4
				} else {
					updateCmd.flag = bytes.get();
					updateCmd.size = nBytes;
					current_read_cmd = updateCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.CLEAR_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<CLEAR_CMD : " + clearCmd);
				}
				if (isWrite) {
					bytes.put(clearCmd.type); //1
					bytes.put((byte)4); //2
					bytes.put(clearCmd.pos); //3
					bytes.put((byte)0); //4
				} else {
					clearCmd.pos = bytes.get();
					clearCmd.size = nBytes;
					current_read_cmd = clearCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.MUSIC_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<MUSIC_CMD : " + musicCmd);
				}
				if (isWrite) {
					bytes.put(musicCmd.type); //1
					bytes.put((byte)8); //2
					bytes.put((byte)0); //3
					bytes.put((byte)0); //4
					bytes.putInt(musicCmd.number); //8
				} else {
					bytes.position(bytes.position() + 2);
					musicCmd.number = bytes.getInt();
					musicCmd.size = nBytes;
					current_read_cmd = musicCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.STOPM_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<STOPM_CMD : ");
				}
				if (isWrite) {
					bytes.put(MkScriptType.STOPM_CMD); //1
					bytes.put((byte)4); //2
					bytes.put((byte)0); //3
					bytes.put((byte)0); //4
				} else {
					bytes.position(bytes.position() + 2);
					normalCmd.type = MkScriptType.STOPM_CMD;
					normalCmd.size = nBytes;
					current_read_cmd = normalCmd;
					return nBytes;
				}
				break;
				
			case MkScriptType.SOUND_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<SOUND_CMD : " + soundCmd);
				}
				if (isWrite) {
					bytes.put(soundCmd.type); //1
					bytes.put((byte)4); //2
					bytes.put((byte)0); //3
					bytes.put(soundCmd.path_len); //4
					bytes.put(soundCmd.bytes.toByteArray());
				} else {
					bytes.position(bytes.position() + 1);
					soundCmd.path_len = bytes.get();
					soundCmd.size = nBytes;
					current_read_cmd = soundCmd;
					return nBytes;
				}
				break;
				
			case MkScriptType.SLEEP_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<SLEEP_CMD : " + sleepCmd);
				}
				if (isWrite) {
					bytes.put(sleepCmd.type); //1 //FIXME:
					bytes.put((byte)8); //2
					bytes.put((byte)0); //3
					bytes.put((byte)0); //4
					bytes.putInt(sleepCmd.time); //8
				} else {
					bytes.position(bytes.position() + 2);
					sleepCmd.time = bytes.getInt();
					sleepCmd.size = nBytes;
					current_read_cmd = sleepCmd;
					return nBytes;
				}
				break;
				
			case MkScriptType.FADEIN_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<FADEIN_CMD : ");
				}
				if (isWrite) {
					bytes.put(MkScriptType.FADEIN_CMD); //1
					bytes.put((byte)4); //2
					bytes.put((byte)0); //3
					bytes.put((byte)0); //4
				} else {
					bytes.position(bytes.position() + 2);
					normalCmd.type = MkScriptType.FADEIN_CMD;
					normalCmd.size = nBytes;
					current_read_cmd = normalCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.FADEOUT_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<FADEOUT_CMD : ");
				}
				if (isWrite) {
					bytes.put(MkScriptType.FADEOUT_CMD); //1
					bytes.put((byte)4); //2
					bytes.put((byte)0); //3
					bytes.put((byte)0); //4
				} else {
					bytes.position(bytes.position() + 2);
					normalCmd.type = MkScriptType.FADEOUT_CMD;
					normalCmd.size = nBytes;
					current_read_cmd = normalCmd;
					return nBytes;
				}
				break;
				
			case MkScriptType.WIPEIN_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<WIPEIN_CMD : " + wipeinCmd);
				}
				if (isWrite) {
					bytes.put(wipeinCmd.type); //1
					bytes.put((byte)4); //2
					bytes.put((byte)0); //3
					bytes.put(wipeinCmd.pattern); //4
				} else {
					bytes.position(bytes.position() + 1);
					wipeinCmd.pattern = bytes.get();
					wipeinCmd.size = nBytes;
					current_read_cmd = wipeinCmd;
					return nBytes;
				}
				break;
				
			case MkScriptType.WIPEOUT_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<WIPEOUT_CMD : " + wipeoutCmd);
				}
				if (isWrite) {
					bytes.put(wipeoutCmd.type); //1
					bytes.put((byte)4); //2
					bytes.put((byte)0); //3
					bytes.put(wipeoutCmd.pattern); //4
				} else {
					bytes.position(bytes.position() + 1);
					wipeoutCmd.pattern = bytes.get();
					wipeoutCmd.size = nBytes;
					current_read_cmd = wipeoutCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.CUTIN_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<CUTIN_CMD : ");
				}
				if (isWrite) {
					bytes.put(MkScriptType.CUTIN_CMD); //1
					bytes.put((byte)4); //2
					bytes.put((byte)0); //3
					bytes.put((byte)0); //4
				} else {
					bytes.position(bytes.position() + 2);
					normalCmd.type = MkScriptType.CUTIN_CMD;
					normalCmd.size = nBytes;
					current_read_cmd = normalCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.CUTOUT_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<CUTOUT_CMD : ");
				}
				if (isWrite) {
					bytes.put(MkScriptType.CUTOUT_CMD); //1
					bytes.put((byte)4); //2
					bytes.put((byte)0); //3
					bytes.put((byte)0); //4
				} else {
					bytes.position(bytes.position() + 2);
					normalCmd.type = MkScriptType.CUTOUT_CMD;
					normalCmd.size = nBytes;
					current_read_cmd = normalCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.WHITEIN_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<WHITEIN_CMD : ");
				}
				if (isWrite) {
					bytes.put(MkScriptType.WHITEIN_CMD); //1
					bytes.put((byte)4); //2
					bytes.put((byte)0); //3
					bytes.put((byte)0); //4
				} else {
					bytes.position(bytes.position() + 2);
					normalCmd.type = MkScriptType.WHITEIN_CMD;
					normalCmd.size = nBytes;
					current_read_cmd = normalCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.WHITEOUT_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<WHITEOUT_CMD : ");
				}
				if (isWrite) {
					bytes.put(MkScriptType.WHITEOUT_CMD); //1
					bytes.put((byte)4); //2
					bytes.put((byte)0); //3
					bytes.put((byte)0); //4
				} else {
					bytes.position(bytes.position() + 2);
					normalCmd.type = MkScriptType.WHITEOUT_CMD;
					normalCmd.size = nBytes;
					current_read_cmd = normalCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.FLASH_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<FLASH_CMD : ");
				}
				if (isWrite) {
					bytes.put(MkScriptType.FLASH_CMD); //1
					bytes.put((byte)4); //2
					bytes.put((byte)0); //3
					bytes.put((byte)0); //4
				} else {
					bytes.position(bytes.position() + 2);
					normalCmd.type = MkScriptType.FLASH_CMD;
					normalCmd.size = nBytes;
					current_read_cmd = normalCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.SHAKE_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<SHAKE_CMD : ");
				}
				if (isWrite) {
					bytes.put(MkScriptType.SHAKE_CMD); //1
					bytes.put((byte)4); //2
					bytes.put((byte)0); //3
					bytes.put((byte)0); //4
				} else {
					bytes.position(bytes.position() + 2);
					normalCmd.type = MkScriptType.SHAKE_CMD;
					normalCmd.size = nBytes;
					current_read_cmd = normalCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.MODE_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<MODE_CMD : " + modeCmd);
				}
				if (isWrite) {
					bytes.put(modeCmd.type); //1
					bytes.put((byte)4); //2
					bytes.put(modeCmd.mode); //3
					bytes.put((byte)0); //4
				} else {
					modeCmd.mode = bytes.get();
					bytes.position(bytes.position() + 1);
					modeCmd.size = nBytes;
					current_read_cmd = modeCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.SYS_LOAD_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<SYS_LOAD_CMD : ");
				}
				if (isWrite) {
					bytes.put(MkScriptType.SYS_LOAD_CMD); //1
					bytes.put((byte)4); //2
					bytes.put((byte)0); //3
					bytes.put((byte)0); //4
				} else {
					bytes.position(bytes.position() + 2);
					normalCmd.type = MkScriptType.SYS_LOAD_CMD;
					normalCmd.size = nBytes;
					current_read_cmd = normalCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.SYS_EXIT_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<SYS_EXIT_CMD : ");
				}
				if (isWrite) {
					bytes.put(MkScriptType.SYS_EXIT_CMD); //1
					bytes.put((byte)4); //2
					bytes.put((byte)0); //3
					bytes.put((byte)0); //4
				} else {
					bytes.position(bytes.position() + 2);
					normalCmd.type = MkScriptType.SYS_EXIT_CMD;
					normalCmd.size = nBytes;
					current_read_cmd = normalCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.SYS_CLEAR_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<SYS_CLEAR_CMD : ");
				}
				if (isWrite) {
					bytes.put(MkScriptType.SYS_CLEAR_CMD); //1
					bytes.put((byte)4); //2
					bytes.put((byte)0); //3
					bytes.put((byte)0); //4
				} else {
					bytes.position(bytes.position() + 2);
					normalCmd.type = MkScriptType.SYS_CLEAR_CMD;
					normalCmd.size = nBytes;
					current_read_cmd = normalCmd;
					return nBytes;
				}
				break;
			
			case MkScriptType.END_CMD:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<END_CMD : ");
				}
				if (isWrite) {
					bytes.put(MkScriptType.END_CMD); //1
					bytes.put((byte)4); //2
					bytes.put((byte)0); //3
					bytes.put((byte)0); //4
				} else {
					bytes.position(bytes.position() + 2);
					normalCmd.type = MkScriptType.END_CMD;
					normalCmd.size = nBytes;
					current_read_cmd = normalCmd;
					return nBytes;
				}
				break;
				
			default:
				if (DEBUG_WRITE_COMMAND) {
					MarikaLog.trace("<<defalut : ");
				}
				break;
		}
		return 0;
	}
}
