package TnT.ld.ld34;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.util.ArrayList;

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
	public Exit exit;
	
	public void paint(Graphics2D g) {
		double tx = 0, ty = 0;
		double xbound = (LD34.getWidth()/2-150)/LD34.gscale;
		double ybound = (LD34.getHeight()/2-150)/LD34.gscale;
		if (ship.x>xbound) tx = xbound-ship.x;
		if (ship.x<-xbound) tx = -xbound-ship.x;
		if (ship.y>ybound) ty = ybound-ship.y;
		if (ship.y<-ybound) ty = -ybound-ship.y;
		g.translate(tx, ty);
		
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
		for (int i = 0; i < paths.size(); i++) {
			g.setColor(new Color(255, 255, 255, (int)(255*(1-(double)i/paths.size()))));
			g.draw(paths.get(i));
		}
		g.setStroke(s);
		
		g.translate(-tx, -ty);
	}
	
	public void drawBackground(Graphics2D g) {
		Stroke s = g.getStroke();
		g.setStroke(new BasicStroke(10));
		g.setColor(Color.red);
		g.drawOval(-SIZE/2, -SIZE/2, SIZE, SIZE);
		g.setStroke(s);
	}
	
	public void physics(boolean ghostMode) {
		teleported = false;
		for (int i = 0; i < bodies.size(); i++) {
			Body b = bodies.get(i);
			b.physics();
			if (!win && !dead && !ship.hasLanded()) b.doGravity(ship);
			if (!win && !dead && b.collides(ship)) {
				if (b.canKill()) dead = true;
				else if (b.canLand() && !ship.hasLanded()) ship.land(b);
				else if (b instanceof Portal && b!=ignore){
					((Portal)b).teleport(ship);
					teleported = true;
					ignore = ((Portal)b).linked;
				}
			}
		}
		if (!win && !dead) {
			ship.physics();
			if (!ship.hasLanded()) {
				exit.doGravity(ship);
				if (exit.collides(ship)) win = true;
			}
		}
		if (Math.hypot(ship.x, ship.y) > SIZE/2-ship.hitRadius) dead = true;
		if (ignore!=null && !ignore.collides(ship)) ignore = null;
		frame++;
		frame %= 6;
		if (!win && !dead && !ghostMode) plotCourse();
		else paths.clear();
	}
	
	ArrayList<Path2D.Double> paths = new ArrayList<Path2D.Double>();
	public void plotCourse() {
		Level l = clone();
		ArrayList<Path2D.Double> paths = new ArrayList<Path2D.Double>();
		Path2D.Double path = new Path2D.Double();
		paths.add(path);
		path.moveTo(ship.x, ship.y);
		int count = frame;
		for (int i = 0; i < PROJECT_TIME/LD34.dt && !l.dead && !l.ship.hasLanded() && !l.win; i++) {
			l.physics(true);
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
	
	public Level clone() {
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
		return l;
	}
	
}
