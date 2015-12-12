package TnT.ld.ld34;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.image.VolatileImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class LD34 extends InputAdapter {
	public static LD34 theLD;
	public static String GAME_NAME = "LD34";
	public JPanel panel;
	public JFrame frame;
	public Thread graphics, physics;
	public static long GRAPHICS_DELAY = 16;
	public static long PHYSICS_DELAY = 10;
	int fps;
	public static long graphicsTime, physicsTime;
	public VolatileImage img;
	public static final int GAME = 0;
	public static int gameState = GAME;
	public static double dt = PHYSICS_DELAY / 1000d;
	public Level level;
	public static int assumedWidth = 1920;
	public static int assumedHeight = 1080;
	public static double gscale;
	
	public static void main(String[] args) {
		theLD = new LD34();
		theLD.start();
	}
	
	public void start() {
		makeGUI();
		graphics = new Thread("Graphics") {
			public void run() {
				int frames = 0;
				long start;
				long time = System.currentTimeMillis();
				while (true) {
					start = System.currentTimeMillis();
					graphics();
					frames++;
					if (System.currentTimeMillis()-time>1000) {
						fps = frames;
						frames = 0;
						time = System.currentTimeMillis();
					}
					graphicsTime = System.currentTimeMillis()-start;
					try {
						Thread.sleep(Math.max(0, GRAPHICS_DELAY-graphicsTime));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		physics = new Thread("Physics") {
			public void run() {
				long start;
				while (true) {
					start = System.currentTimeMillis();
					physics();
					physicsTime = System.currentTimeMillis()-start;
					try {
						Thread.sleep(Math.max(0, PHYSICS_DELAY-physicsTime));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		//TODO actual starting game stuff
		gameState = GAME;
		makeLevel();
		
		graphics.start();
		physics.start();
	}
	
	public void makeGUI() {
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(1024, 768));
		frame = new JFrame(GAME_NAME);
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		panel.addComponentListener(this);
		frame.addKeyListener(this);
		frame.setFocusable(true);
	}
	
	public void graphics() {
		if (img==null) img = panel.createVolatileImage(panel.getWidth(), panel.getHeight());
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, panel.getWidth(), panel.getHeight());
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		switch (gameState) {
		case GAME: gameGraphics(g); break;
		}
		
		g.setColor(Color.red);
		g.drawString("Physics Time: "+physicsTime, 10, 20);
		g.drawString("Graphics Time: "+graphicsTime, 10, 40);
		g.drawString("FPS: "+fps, 10, 60);
		
		g.dispose();
		g = (Graphics2D) panel.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
	}
	
	public void gameGraphics(Graphics2D g) {
		g.translate(panel.getWidth()/2, panel.getHeight()/2);
		gscale = Math.min((double)panel.getWidth()/assumedWidth, (double)panel.getHeight()/assumedHeight);
		g.scale(gscale, gscale);
		
		level.paint(g);
		
		g.scale(1/gscale, 1/gscale);
		g.translate(-panel.getWidth()/2, -panel.getHeight()/2);
	}
	
	public void physics() {
		if (level != null) level.physics(false);
	}
	
	public void makeLevel() {
		//TODO load level from file
		level = new Level();
		level.ship.x = 200;
//		level.ship.vy = 680;//511.9;
//		level.bodies.add(new Planet(0, 0, 100, 1));
		level.bodies.add(new Planet(-600, 0, 100, -1));
		level.ship.vy = 200;
		Portal a = new Portal(70, 200);
		Portal b = new Portal(330, -200);
		a.link(b);
		level.addBody(a);
		level.addBody(b);
		level.exit = new Exit(800, 100);
		level.ship.land(level.bodies.get(0));
	}
	
	public static boolean windup = false;
	public static boolean launch = false;
	public void keyPressed(KeyEvent e) {
		System.out.println("key pressed");
		switch (e.getKeyCode()) {
		case KeyEvent.VK_SHIFT: windup = true; break;
		case KeyEvent.VK_SPACE: launch = true; break;
		}
	}
	
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_SHIFT: windup = false; break;
		case KeyEvent.VK_SPACE: launch = false; break;
		}
	}
	
	public void componentResized(ComponentEvent e) {
		img = panel.createVolatileImage(panel.getWidth(), panel.getHeight());
	}
	
	public static int getWidth() {
		return LD34.theLD.panel.getWidth();
	}
	
	public static int getHeight() {
		return LD34.theLD.panel.getHeight();
	}
	
}
