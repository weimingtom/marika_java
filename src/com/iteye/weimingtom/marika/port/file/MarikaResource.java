package com.iteye.weimingtom.marika.port.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import com.iteye.weimingtom.marika.port.window.MarikaWindow;

public class MarikaResource {
	public final static boolean USE_CACHE = true;
	
	private final static String ZIP_FILENAME = "data.zip";
	private final static String DATA_PATH = "data";
	
	private String WORKPATH = "./assets/";
	private String ZIPFILE = WORKPATH + ZIP_FILENAME;
	
	public Map<String, BufferedImage> imageMap = new Hashtable<String, BufferedImage>();
	public Map<String, ByteBuffer> dataMap = new Hashtable<String, ByteBuffer>();
	public Map<String, String> textMap = new HashMap<String, String>();
	public Map<String, String> imagePathMap = new Hashtable<String, String>();
	
	public void init(MarikaWindow win) {
		
	}
	
	public void loadClassText(String name, Class<?> cls, String resName) {
		String str = null;
		InputStream input = cls.getResourceAsStream(resName);
		if (input != null) {
			try {
				byte[] bytes = new byte[input.available()];
				input.read(bytes);
				str = new String(bytes, "UTF8");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		textMap.put(name, str);
	}
	
	public void loadText(String name, String path) {
		InputStream instr = null;
		ZipFile zipFile = null;
		try {
			File file = new File(ZIPFILE);
			if (file.exists() && file.canRead()) {
				zipFile = new ZipFile(ZIPFILE);
				ZipEntry entry = zipFile.getEntry(path);
				if (entry != null) {
					instr = zipFile.getInputStream(entry);
				} else {
					instr = new FileInputStream(WORKPATH + File.separator + path);
				}
			} else {
				instr = new FileInputStream(WORKPATH + File.separator + path);
			}
			byte[] bytes = null;
			bytes = new byte[instr.available()];
			instr.read(bytes);
			String str = new String(bytes, "UTF8");
			textMap.put(name, str);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (instr != null) {
				try {
					instr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void loadClassImage(String name, Class<?> cls) {
		try {
			imageMap.put(name, ImageIO.read(cls.getResourceAsStream(name)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage loadImageNoCache(String name) {
		String path = imagePathMap.get(name);
		if (path != null) {
			InputStream instr = null;
			ZipFile zipFile = null;
			try {
				File file = new File(ZIPFILE);
				if (file.exists() && file.canRead()) {
					zipFile = new ZipFile(ZIPFILE);
					ZipEntry entry = zipFile.getEntry(path);
					if (entry != null) {
						instr = zipFile.getInputStream(entry);
					} else {
						instr = new FileInputStream(WORKPATH + File.separator + path);
					}
				} else {
					instr = new FileInputStream(WORKPATH + File.separator + path);
				}
				return ImageIO.read(instr);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (instr != null) {
					try {
						instr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (zipFile != null) {
					try {
						zipFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
	
	public void loadImage(String name, String path) {
		imagePathMap.put(name, path);
		if (USE_CACHE) {
			imageMap.put(name, loadImageNoCache(name));
		}
	}
	
	public void loadClassData(String name, Class<?> cls) {
		InputStream instr = cls.getResourceAsStream(name);
		byte[] bytes = null;
		try {
			bytes = new byte[instr.available()];
			instr.read(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		dataMap.put(name, ByteBuffer.wrap(bytes));
	}
	
	public void loadData(String name, String path) {
		InputStream instr = null;
		ZipFile zipFile = null;
		try {
			File file = new File(ZIPFILE);
			if (file.exists() && file.canRead()) {
				zipFile = new ZipFile(ZIPFILE);
				ZipEntry entry = zipFile.getEntry(path);
				if (entry != null) {
					instr = zipFile.getInputStream(entry);
				} else {
					instr = new FileInputStream(WORKPATH + File.separator + path);
				}
			} else {
				instr = new FileInputStream(WORKPATH + File.separator + path);
			}
			byte[] bytes = null;
			try {
				bytes = new byte[instr.available()];
				instr.read(bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
			dataMap.put(name, ByteBuffer.wrap(bytes));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (instr != null) {
				try {
					instr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void loadBytes(String name, ByteBuffer bytes) {
		dataMap.put(name, bytes);
	}
	
	public void unloadAll() {
		imageMap.clear();
		dataMap.clear();
		textMap.clear();
	}
}
