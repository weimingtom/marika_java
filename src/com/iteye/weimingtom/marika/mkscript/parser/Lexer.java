package com.iteye.weimingtom.marika.mkscript.parser;

import java.util.ArrayList;

import com.iteye.weimingtom.marika.port.file.MarikaLog;

public class Lexer {
	private static final boolean LEXER_DEBUG_01 = false;
	private static final boolean LEXER_DEBUG_02 = false;	
	
	public static final int IsError = -1;
	public static final int IsNumber = 0;
	public static final int IsString = 1;
	public static final int IsDelimitter = 2;
	public static final int IsLabel = 3;
	public static final int IsMinus = 4;
	private static final int IsSpace = 5;
	private static final int IsTerminater = 6;
	private static final int IsQuotation = 7;
	
	/**
	 * NOT UESED
	 */
	public static final String IsError_STR = "IsError".intern();
	public static final String IsNumber_STR = "IsNumber".intern();
	public static final String IsString_STR = "IsString".intern();
	public static final String IsDelimitter_STR	= "IsDelimitter".intern();
	public static final String IsLabel_STR = "IsLabel".intern();
	public static final String IsMinus_STR = "IsMinus".intern();
	private static final String IsSpace_STR = "IsSpace".intern();
	private static final String IsTerminater_STR = "IsTerminater".intern();
	private static final String IsQuotation_STR	= "IsQuotation".intern();
	
	protected int nToken;
	protected ArrayList<LexValue> Value = new ArrayList<LexValue>();
	protected int Count;
	
	private int _pos = 0;
	
	public Lexer(String str) {
		parse(str);
	}
	
	private void parse(String str) {
		for (nToken = 0; ; nToken++) 
		{
			if (LEXER_DEBUG_01) {
				MarikaLog.trace("["+ _pos + "/" + str.length() + "]" + 
						str.substring(_pos, 1));
			}
			skipSpace(str);
			if (_pos >= str.length() || str.charAt(_pos) == ';') {
				break;
			}
			int type = charType(str.charAt(_pos));
			if (type == Lexer.IsTerminater && type == Lexer.IsSpace) {
				break;
			}	
			LexValue value = new LexValue();
			if (type == Lexer.IsQuotation) {
				value.type = IsString;
				_pos++;
				while ( _pos < str.length() && charType(str.charAt(_pos)) != Lexer.IsQuotation) {
					//FIXME: NO NEED FOR UNICODE 
					/*
					if (_ismbblead(str.charAt(_pos))) {
						value.value += str.charAt(_pos);
						_pos++;
					}
					*/
					value.value += str.charAt(_pos);
					_pos++;
				}
				if (_pos < str.length()) {
					_pos++;
				}
			} else {
				if (str.charAt(_pos) == '-' && charType(str.charAt(_pos + 1)) == Lexer.IsNumber) {
					value.value += '-';
					value.type = Lexer.IsMinus;
					_pos++;
				} else {
					if (str.charAt(_pos) == '*' && nToken == 0) {
						type = Lexer.IsLabel;
					}
					value.type = type;
					while ( _pos < str.length() ) {
						char ch = str.charAt(_pos);
						int t = charType(ch);
						boolean res = true;
						switch (type) {
							case IsLabel:
								if (ch == '*')
								{
									res = true;
									break;
								}
								
							case IsNumber:
								if (t == Lexer.IsString) {
									type = Lexer.IsString;
									res = (t == Lexer.IsString || t == Lexer.IsNumber);
									break; //FIXME:
								}
								// no break
							
							case IsString:
								res = (t == Lexer.IsString || t == Lexer.IsNumber);
								break;
								
							default:
								res = (type == t);
								break;
						}
						if (res == false) {
							break;
						}
						//FIXME: NO NEED FOR UNICODE
						/*
						if (_ismbblead(str.charAt(_pos)))
						{
							value.value += str.charAt(_pos);
							_pos++;
						}
						*/
						value.value += str.charAt(_pos);
						_pos++;
					}
					if (value.type == Lexer.IsNumber) {
						value.type = type;
					}
				}
			}
			if (LEXER_DEBUG_02) {
				MarikaLog.trace(">>[" + value.type + "]" + 
						value.value);
			}
			Value.add(value);
		}
		Count = 0;
	}
	
	public int numToken() { 
		return nToken; 
	}
	
	public void nextToken() { 
		Count++; 
	}
	
	public String getString() {
		return getString(-1);
	}
	
	public String getString(int index) {
		if (index >= 0) {
			Count = index;
		}
		if (Count >= nToken) {
			return null;
		}
		return Value.get(Count++).value;
	}
	
	public double getValue() {
		return getValue(-1);
	}
	
	public double getValue(int index) {
		int value;
		boolean minus = false;
		int type = getType(index);
		if (type == IsMinus) {
			minus = true;
			nextToken();
			type = getType();
		}
		if (type != IsNumber) {
			return Double.NaN;
		}
		String p = getString();
		if (p == null) {
			return Double.NaN;
		}
		int v = Integer.parseInt(p);
		value = minus? -v: v;
		return value;
	}
	
	public int getType() {
		return getType(-1);
	}
	
	public int getType(int index) {
		if (index >= 0) {
			Count = index;
		}
		if (Count >= nToken) {
			return Lexer.IsError;
		}
		return Value.get(Count).type;
	}
	
	protected void skipSpace(String p) {
		while (_pos < p.length() && isspace(p.charAt(_pos))) {
			_pos++;
		}
	}
	
	protected int charType(char ch) {
		if (ch == '\n') {
			return Lexer.IsTerminater;
		}
		if (isdigit(ch)) {
			return Lexer.IsNumber;
		}
		if (isalpha(ch) || _ismbblead(ch) || ch == '_') {
			return Lexer.IsString;
		}
		if (isspace(ch)) {
			return Lexer.IsSpace;
		}
		if (ch == '"') {
			return Lexer.IsQuotation;
		}
		if (ch == '-') {
			return Lexer.IsMinus;
		}
		return Lexer.IsDelimitter;
	}
	
	//FIXME: NO NEED FOR UNICODE
	private boolean _ismbblead(char c) {
		return c > 255 || 
			c == '(' || c == ')' ||
			c == '[' || c == ']';
	}
	
	//FIXME: DON'T USE Character.isSpaceChar
	private boolean isspace(char c) {
		return c == ' ' || c == '\t' || (int)c == 0xFEFF; /*65279*/
	}
	
	private boolean isdigit(char c) {
		//return c >= '0' && c <= '9';
		return Character.isDigit(c);
	}
	
	private boolean isalpha(char c) {
		//return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_';
		return Character.isJavaIdentifierStart(c);
	}
}
