package TnT.ld.ld34;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.VolatileImage;
import java.awt.geom.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class LD34 extends InputAdapter {
	public static LD34 theLD;
	public static String GAME_NAME = "LD34";
	public static JPanel panel;
	public JFrame frame;
	public Thread graphics, physics, animate;
	public static long GRAPHICS_DELAY = 16;
	public static long PHYSICS_DELAY = 10;
	public static long ANIM_DELAY = 20;
	int fps;
	public static long graphicsTime, physicsTime, animTime;
	public VolatileImage img;
	public static final int GAME = 0;
	public static final int EDITOR = -1;
	public static final int ENDGAME = 1;
	public static final int MENU=2;
//	public static int gameState = GAME;
	public static int gameState = EDITOR;
	public static double dt = PHYSICS_DELAY/ 1000d;
	public Level level;
	public static int assumedWidth = 1920;
	public static int assumedHeight = 1080;
	public static double gscale;
	public static Menu menu;
	public LevelEditor edit;
	
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
					try{
						graphics();
					}catch(Exception e){
						System.out.println("GRAPHICS ERROR!");
						e.printStackTrace();
					}
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
					try {
						physics();
					} catch (Exception e) {
						System.out.println("PHYSICS ERROR!");
						e.printStackTrace();
					}
					physicsTime = System.currentTimeMillis()-start;
					try {
						Thread.sleep(Math.max(0, PHYSICS_DELAY-physicsTime));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		animate = new Thread("Animation") {
			public void run() {
				long start;
				while (true) {
					start = System.currentTimeMillis();
					try {
						animate();
					} catch (Exception e) {
						System.out.println("ANIMATION ERROR!");
						e.printStackTrace();
					}
					animTime = System.currentTimeMillis()-start;
					try {
						Thread.sleep(Math.max(0, ANIM_DELAY-animTime));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		//TODO actual starting game stuff
		gameState=MENU;
		menu=new Menu();
		
		gameState = GAME;
//		loadLevel("take 1");
		loadLevel("defaultMove", false);
//		makeLevel();
		Help h=new Help(0,0,100,20);
		h.message="DIE!";
		h.type=Help.ON_DEATH;
		h.duration=5000;
		level.anims.add(h);
		Path2D.Double path=new Path2D.Double(new Line2D.Double(-600,0,200,0));;
//		level.bodies.get(1).path=new Path2D.Double(new Rectangle2D.Double(-800,-400,400,400));//new Ellipse2D.Double(-800,-400,400,400));
//		path.append(new Line2D.Double(200,0,-200,0), true);
//		level.bodies.get(0).path=path;
//		level.bodies.get(1).fixed=false;
//		Loader.saveLevel(level, "defaultMoveSqaure");
		launchEditor();
		
		
		
		
//		gameState=MENU;
//		menu=new Menu();
		
		graphics.start();
		physics.start();
		animate.start();
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
		panel.addMouseListener(this);
		panel.addMouseMotionListener(this);
		panel.addMouseWheelListener(this);
		frame.setFocusable(true);
	}
	
	public void animate() {
		if (gameState==GAME) {
			level.animate();
		} else if (gameState==EDITOR) {
			edit.level.animate();
		}
	}
	
	public void graphics() {
		if (img==null) img = panel.createVolatileImage(panel.getWidth(), panel.getHeight());
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, panel.getWidth(), panel.getHeight());
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		
		switch (gameState) {
		case GAME: gameGraphics(g); break;
		case EDITOR: edit.paint(g); break;
		case ENDGAME: endGraphics(g); break;
		case MENU: menuGraphics(g);
		}
		
		g.setColor(Color.red);
		g.drawString("Physics Time: "+physicsTime, 10, 20);
		g.drawString("Graphics Time: "+graphicsTime, 10, 40);
		g.drawString("Animation Time: "+animTime, 10, 60);
		g.drawString("FPS: "+fps, 10, 80);
		
		g.dispose();
		g = (Graphics2D) panel.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
	}
	
	private void menuGraphics(Graphics2D g) {
		g.translate(panel.getWidth()/2, panel.getHeight()/2);
		gscale = Math.min((double)panel.getWidth()/assumedWidth, (double)panel.getHeight()/assumedHeight);
		g.scale(gscale, gscale);
		
		menu.paint(g);
		
		g.scale(1/gscale, 1/gscale);
		g.translate(-panel.getWidth()/2, -panel.getHeight()/2);
	}

	public void gameGraphics(Graphics2D g) {
		gameGraphics(level,g);
	}
	public void gameGraphics(Level l,Graphics2D g){
		g.translate(panel.getWidth()/2, panel.getHeight()/2);
		gscale = Math.min((double)panel.getWidth()/assumedWidth, (double)panel.getHeight()/assumedHeight);
		g.scale(gscale, gscale);
		
		l.paint(g);
		
		g.scale(1/gscale, 1/gscale);
		g.translate(-panel.getWidth()/2, -panel.getHeight()/2);
		
		l.paintHUD(g);
	}
	public void endGraphics(Graphics2D g) {
		g.setColor(Color.white);
		g.drawString("YAY YOU WIN", 200, 200);
	}
	public void physics() {
		if (gameState==GAME) level.physics();
		if (gameState==EDITOR) edit.physics();
		if (gameState==MENU)menu.physics();
	}
	
	public void makeLevel() {
		//TODO lo\ad level from file
		level = new Level();
		level.ship.x = 200;
		level.ship.vy = /*680;*/511.9;
		level.bodies.add(new Planet(0, 0, 100, .5));
		level.bodies.add(new Planet(-600, 0, 100, -.5));
//		level.bodies.add(new Star(-100, 400, 200));
		level.ship.vy = 200;
		Portal a = new Portal(70, 220, 0);
		Portal b = new Portal(330, -200, 0);
		a.link(b);
		level.addBody(a);
		level.addBody(b);
		level.exit = new Exit(800, 100);
		level.setStart(level.bodies.get(0));
	}
	
	public void launchEditor() {
		edit = new LevelEditor();
		gameState = EDITOR;
	}
	
	public static void loadLevel(String name, boolean withZoom) {
		if (name.equals("")) {
			gameState = ENDGAME;
			return;
		}
		Level l = Loader.loadLevel(name);
		if (withZoom) l.introZoom();
		LD34.theLD.level = l;
		gameState = GAME;
	}
	
	public static boolean windup = false;
	public static boolean launch = false;
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_SHIFT: windup = true; break;
		case KeyEvent.VK_SPACE: launch = true; break;
		}
		if (gameState==EDITOR) edit.keyPressed(e);
	}
	
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_SHIFT: windup = false; break;
		case KeyEvent.VK_SPACE: launch = false; break;
		}
		if (gameState==EDITOR) edit.keyReleased(e);
	}
	
	public void componentResized(ComponentEvent e) {
		img = panel.createVolatileImage(panel.getWidth(), panel.getHeight());
	}
	
	public static int getWidth() {
		return LD34.panel.getWidth();
	}
	
	public static int getHeight() {
		return LD34.panel.getHeight();
	}
	
	public static void doAnimation() {
		
	}
	
	public void mousePressed(MouseEvent e) {
		if (gameState==EDITOR) edit.mousePressed(e);
	}
	
	public void mouseReleased(MouseEvent e) {
		if (gameState==EDITOR) edit.mouseReleased(e);
	}
	
	public void mouseDragged(MouseEvent e) {
		if (gameState==EDITOR) edit.mouseDragged(e);
	}
	
	public void mouseMoved(MouseEvent e) {
		if (gameState==EDITOR) edit.mouseMoved(e);
	}
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (gameState==EDITOR) edit.mouseWheelMoved(e);
	}

	public static void scaleTo(Graphics2D g) {
		g.translate(panel.getWidth()/2, panel.getHeight()/2);
		gscale = Math.min((double)panel.getWidth()/assumedWidth, (double)panel.getHeight()/assumedHeight);
		g.scale(gscale, gscale);
	}
	public static Point2D.Double scalePointTo(Point2D.Double p){
		return new Point2D.Double((p.x-getWidth()/2)/gscale,(p.y-getHeight()/2)/gscale);
	}
	public static void scaleFrom(Graphics2D g) {
		g.scale(1/gscale, 1/gscale);
		g.translate(-panel.getWidth()/2, -panel.getHeight()/2);
	}
	
}
