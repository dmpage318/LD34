package TnT.ld.ld34;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.jar.Attributes.Name;

public class Level {
	public static int SIZE = 4000; //radius of the intergalactic barrier
	public static double PROJECT_TIME = 4;
	public ArrayList<Body> bodies = new ArrayList<Body>();
	public Ship ship = new Ship();
	public boolean dead = false;
	public boolean win = false;
	public Portal ignore = null;
	public boolean teleported; //used for predicting path
	private int frame = 0; //physics time frame for drawing path
	public Exit exit = new Exit(0, 0);
	public static double launchSpeed = 800;
	public String next="";
	public Body startPlanet;
	public boolean ghostMode;
	public boolean editMode = false;
	
	public int endCount;
	public void paint(Graphics2D g) {
		double tx = 0, ty = 0;
		double xbound = (LD34.getWidth()/2-75)/LD34.gscale;
		double ybound = (LD34.getHeight()/2-75)/LD34.gscale;
		if (ship.x>xbound) tx = xbound-ship.x;
		if (ship.x<-xbound) tx = -xbound-ship.x;
		if (ship.y>ybound) ty = ybound-ship.y;
		if (ship.y<-ybound) ty = -ybound-ship.y;
		if (!editMode) g.translate(tx, ty);
		
		if (win) {
			endCount--;
			if (endCount==0) LD34.loadLevel(next);
		}
		
		drawBackground(g);
		
		for (int i = 0; i < bodies.size(); i++) {
			Body b = bodies.get(i);
			b.paint(g);
		}
		exit.paint(g);
		ship.paint(g);
		
		Stroke s = g.getStroke();
		g.setStroke(new BasicStroke(2));
		g.setColor(Color.white);
		ArrayList<Path2D.Double> paths = this.paths;
		for (int i = 0; i < paths.size(); i++) {
			int alpha = Math.max(0, (int)(255*(1-(double)i/(PROJECT_TIME*100d/6))));
			g.setColor(new Color(255, 255, 255, alpha));
			g.draw(paths.get(i));
		}
		g.setStroke(s);
		
		if (!editMode) g.translate(-tx, -ty);
	}
	
	public void drawBackground(Graphics2D g) {
		Stroke s = g.getStroke();
		g.setStroke(new BasicStroke(10));
		g.setColor(Color.red);
		g.drawOval(-SIZE/2, -SIZE/2, SIZE, SIZE);
		g.setStroke(s);
	}
	
	public void setStart(Body b) {
		ship.ix = ship.x;
		ship.iy = ship.y;
		ship.land(b);
		startPlanet = b;
	}
	
	public void reset() {
		ship.x = ship.ix;
		ship.y = ship.iy;
		ship.land(startPlanet);
		dead = false;
		win = false;
	}

	public double power = 1;
	public double dpower = .5;
	public boolean lastLaunch = false;
	public void physics() {
		teleported = false;
		for (int i = 0; i < bodies.size(); i++) {
			Body b = bodies.get(i);
			b.physics();
			if (!editMode && !win && !dead && !ship.hasLanded()) b.doGravity(ship);
			if (!editMode && !win && !dead && b.collides(ship)) {
				if (b.canKill()) die();
				else if (b.canLand() && !ship.hasLanded()) ship.land(b);
				else if (b instanceof Portal && b!=ignore){
					((Portal)b).teleport(ship);
					teleported = true;
					ignore = ((Portal)b).linked;
				}
			}
		}
		boolean launch = LD34.launch;
		if (LD34.windup && ship.hasLanded()) {
			power += dpower * LD34.dt;
			if (power>1) {
				power = 2-power;
				dpower *= -1;
			} else if (power<0) {
				power *= -1;
				dpower *= -1;
			}
		}
		if (!win && !dead) {
			if (!editMode && !ghostMode && ship.hasLanded() && launch && !lastLaunch) {
				ship.launch(getLaunchSpeed());
			}
			ship.physics();
			if (!editMode && !ship.hasLanded()) {
				exit.doGravity(ship);
				if (exit.collides(ship)) win();
			}
		}
		if (win) {
			if (next.equals("") && !ship.hasLanded()) ship.land(exit);
			else ship.approachExit(exit);
		}
		if (!editMode && Math.hypot(ship.x, ship.y) > SIZE/2-Ship.hitRadius) die();
		if (ignore!=null && !ignore.collides(ship)) ignore = null;
		if (!ship.hasLanded() && !dead && !win) {
			frame++;
			frame %= 6;
		}
		if (!win && !dead && !ghostMode) plotCourse();
		else paths.clear();
		lastLaunch = launch;
	}
	
	public void die() {
		System.out.println("dead");
		dead = true;
		if (!ghostMode) reset();
	}
	
	public void win() {
		win = true;
		endCount = 100;
	}
	
	public double getLaunchSpeed() {
		return Math.pow(power, .2) * launchSpeed;
	}
	
	ArrayList<Path2D.Double> paths = new ArrayList<Path2D.Double>();
	public void plotCourse() {
		Level l = makeGhost();
		ArrayList<Path2D.Double> paths = new ArrayList<Path2D.Double>();
		Path2D.Double path = new Path2D.Double();
		paths.add(path);
		path.moveTo(ship.x, ship.y);
		int count = frame;
		for (int i = 0; i < PROJECT_TIME/LD34.dt && !l.dead && !l.ship.hasLanded() && !l.win; i++) {
			l.physics();
			if (count<3 || l.teleported) path.moveTo(l.ship.x, l.ship.y);
			else path.lineTo(l.ship.x, l.ship.y);
			count++;
			if (count==6) {
				path = new Path2D.Double();
				paths.add(path);
				count = 0;
			}
		}
		this.paths = paths;
	}
	
	public void addBody(Body b) {
		bodies.add(b);
	}
	
	public Level makeGhost() {
		Level l = new Level();
		Body b, bc;
		for (int i = 0; i < bodies.size(); i++) {
			b = bodies.get(i);
			bc = b.clone();
			l.bodies.add(bc);
			if (b instanceof Portal) {
				int j = bodies.indexOf(((Portal) b).linked);
				if (j>-1 && j<i) ((Portal)bc).link((Portal)l.bodies.get(j));
			}
		}
		l.exit = (Exit) exit.clone();
		l.ship.x = ship.x;
		l.ship.y = ship.y;
		l.ship.vx = ship.vx;
		l.ship.vy = ship.vy;
		if (ship.hasLanded()) {
			l.ship.landed = ship.landed;
			l.ship.launch(getLaunchSpeed());
			l.ship.physics();
		}
		l.ghostMode = true;
		return l;
	}
	
}
