package com.iteye.weimingtom.marika.port.window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.iteye.weimingtom.marika.model.MarikaConfig;
import com.iteye.weimingtom.marika.model.MarikaKey;
import com.iteye.weimingtom.marika.model.MarikaPoint;
import com.iteye.weimingtom.marika.model.MarikaRectangle;
import com.iteye.weimingtom.marika.port.file.MarikaLog;
import com.iteye.weimingtom.marika.port.image.MarikaImage;

public class MarikaWindow extends Panel implements Runnable, KeyListener,
	MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	
	private static final int WINDOW_WIDTH = 640;
	private static final int WINDOW_HEIGHT = 480;
	private static final String WINDOW_TITLE = "MarikaWindow";
	
	private Frame frame;
	private Graphics bufGraph;
	private Image bufImage;
	private Thread drawThread;
	private long prevTime;
	
	private String title;
	private int canvasWidth, canvasHeight, tickPerSecond;
	private boolean isStopped = false;
	private long tick = 0;
	private boolean enableTick = true;
	
	private MarikaWindowAdapter adapter;
	
	public MarikaWindow() {
		this.title = WINDOW_TITLE;
		this.canvasWidth = WINDOW_WIDTH;
		this.canvasHeight = WINDOW_HEIGHT;
		this.tickPerSecond = 24;
		setPreferredSize(new Dimension(this.canvasWidth, this.canvasHeight));
		addMouseListener(this);
		addKeyListener(this);
		addMouseMotionListener(this);
		frame = new Frame();
		frame.add(this);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				isStopped = true;
				if (drawThread != null) {
					try {
						drawThread.join(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				onExit();
				System.exit(0);	
			}
		});
		frame.setTitle(title);
	}

	public void setAdapter(MarikaWindowAdapter adapter) {
		this.adapter = adapter;
		if (this.adapter != null) {
			this.adapter.setWindow(this);
		}
	}
	
	public void start() {
		//NOTE:setResizable(false) must before pack()
		//see http://www.javaworld.com.tw/jute/post/view?bid=5&id=235878&sty=3&tpg=4&age=0
		frame.setResizable(true);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		this.requestFocus(); //listen for keyboard event
		bufImage = this.createImage(canvasWidth, canvasHeight);
		bufGraph = bufImage.getGraphics();
		bufGraph.clearRect(0, 0, canvasWidth, canvasHeight);
		drawThread = new Thread(this, "MarikaWindow");
		drawThread.start();	
	}
	
	@Override
	public void run() {
		onInit();
		while (true) {
			if (isStopped) {
				break;
			}
			long curTime = System.currentTimeMillis();
			if (curTime - (1000 / tickPerSecond) > this.prevTime) {
				this.prevTime = curTime;
				if (enableTick) {
					if (this.adapter != null) {
						this.adapter.onIdle(0); //FIXME:
					}
				}
				tick++;
			}
			//onDraw(bufGraph);
			//onPaint(); //FIXME: unnecessary!!!
			repaint();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//System.exit(0);
	}
	
	@Override
	public void update(Graphics g) {
		paint(g);
	}

	@Override
	public void paint(Graphics g) {
		//g.drawImage(bufImage, 0, 0, this);
		double x = 0;
		double y = 0;
		double w = (double)this.getWidth();
		double h = (double)this.getHeight();

		int ow;
		int oh;
		int ox;
		int oy;
		if (w > 0 && h > 0 && bufImage != null) {
			if ((double)WINDOW_WIDTH / w > (double)WINDOW_HEIGHT / h) {
				oh = ((int)((double)WINDOW_HEIGHT / WINDOW_WIDTH * w));
                ow = (int)(w);
            } else {
            	ow = ((int)((double)WINDOW_WIDTH / WINDOW_HEIGHT * h));
                oh = (int)(h);
            }
			ox = (int)(x + (double)w / 2 - (double)ow / 2);
			oy = (int)(y + (double)h / 2 - (double)oh / 2);
			g.drawImage(bufImage,
				ox, oy, ox + ow, oy + oh,
				0, 0, bufImage.getWidth(this), bufImage.getHeight(this),  
				this);
		}
	}
	
	public void setEnableTick(boolean enableTick) {
		this.enableTick = enableTick;
	}
	
	public void setFrameTitle(String title) {
		if (this.frame != null) {
			this.frame.setTitle(title);
		}
	}
	
	public Graphics getBufGraph() {
		return this.bufGraph;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ENTER:
			if (adapter != null) {
				adapter.onKeyDown(MarikaKey.ENTER);
			}
			break;
			
		case KeyEvent.VK_SPACE:
			if (adapter != null) {
				adapter.onKeyDown(MarikaKey.SPACE);
			}
			break;
			
		case KeyEvent.VK_ESCAPE:
			if (adapter != null) {
				adapter.onKeyDown(MarikaKey.ESCAPE);
			}
			break;
			
		case KeyEvent.VK_UP:
			if (adapter != null) {
				adapter.onKeyDown(MarikaKey.UP);
			}
			break;
			
		case KeyEvent.VK_DOWN:
			if (adapter != null) {
				adapter.onKeyDown(MarikaKey.DOWN);
			}
			break;
			
		//-------------------------------------
		//FIXME: 这是新增的，原来没有，映射到onCommand
		case KeyEvent.VK_S:
			if (adapter != null) {
				adapter.onCommand(0, MarikaConfig.ID_SAVEGAME, this);
			}
			break;
		
		case KeyEvent.VK_L:
			if (adapter != null) {
				adapter.onCommand(0, MarikaConfig.ID_LOADGAME, this);
			}
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
	
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (adapter != null) {
			/**
			 * @see http://www.coderanch.com/t/388898/java/java/Capturing-click-mouse-AWT
			 */
			if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
				adapter.onLButtonDown(calcPoint(e.getX(), e.getY()));
			} else if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
				adapter.onRButtonDown(calcPoint(e.getX(), e.getY()));
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (adapter != null) {
			if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
				adapter.onLButtonUp(calcPoint(e.getX(), e.getY()));
			} else if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
				adapter.onRButtonUp(calcPoint(e.getX(), e.getY()));
			} 
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (adapter != null) {
			adapter.onMouseMove(calcPoint(e.getX(), e.getY()));
		}
	}
	
	protected void onInit() {
		if (adapter != null) {
			adapter.onCreate();
		}
	}
	
	protected void onExit() {
		if (adapter != null) {
			adapter.onDestroy();
		}
	}
	
	//FIXME:未实现
	public void messageBox(String str) {
		MarikaLog.trace("NOTE:[messageBox] -> " + str);
	}
	//FIXME:未实现
	public int messageBox(String str, String title, int flag) {
		MarikaLog.trace("NOTE:[messageBox] -> " + str);
		return 0;
	}
	
	public void draw(MarikaImage image, MarikaRectangle rect) {
		int x = rect.x;
		int y = rect.y;
		int ox = x;
		int oy = y;
		int w = rect.width;
		int h = rect.height;
		this.getBufGraph().drawImage(image.mImage, 
			ox, oy, ox + w, oy + h, 
			 x,  y,  x + w,  y + h, 
			null);
	}
	
	private MarikaPoint calcPoint(int mx, int my) {
		MarikaPoint result = null;
		double x = 0;
		double y = 0;
		double w = (double)this.getWidth();
		double h = (double)this.getHeight();

		int ow;
		int oh;
		int ox;
		int oy;
		if (w > 0 && h > 0) {
			if ((double)WINDOW_WIDTH / w > (double)WINDOW_HEIGHT / h) {
				oh = ((int)((double)WINDOW_HEIGHT / WINDOW_WIDTH * w));
                ow = (int)(w);
            } else {
            	ow = ((int)((double)WINDOW_WIDTH / WINDOW_HEIGHT * h));
                oh = (int)(h);
            }
			ox = (int)(x + (double)w / 2 - (double)ow / 2);
			oy = (int)(y + (double)h / 2 - (double)oh / 2);
			
			double scale = (double)WINDOW_WIDTH / (double)ow;
			int rx = (int)((mx - ox) * scale);
			int ry = (int)((my - oy) * scale);
			result = new MarikaPoint(rx, ry);
		}
		return result;
	}
	
	// ==================================
	// 20150726
	
	public void draw(MarikaImage image, int x, int y, int w, int h, int ox, int oy) {
		this.getBufGraph().drawImage(image.mImage, 
			ox, oy, ox + w, oy + h, 
			 x,  y,  x + w,  y + h, 
			null);
	}
	
	public void draw(MarikaImage image, MarikaRectangle rect, MarikaPoint point) {
		int x = rect.x;
		int y = rect.y;
		int ox = point.x;
		int oy = point.y;
		int w = rect.width;
		int h = rect.height;
		this.getBufGraph().drawImage(image.mImage, 
			ox, oy, ox + w, oy + h, 
			 x,  y,  x + w,  y + h, 
			null);
	}
	
	//dc.SetBkColor(RGB(0, 0, 0));
	//dc.ExtTextOut(0, 0, ETO_OPAQUE, &rect, 0, 0, NULL);
	public void extTextOutOpaque(int color, MarikaRectangle rect) {
		this.getBufGraph().setColor(new Color(color));
		this.getBufGraph().fillRect(rect.x, rect.y, 
			rect.width, rect.height);
	}
}
