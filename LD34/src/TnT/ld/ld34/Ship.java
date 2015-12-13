package TnT.ld.ld34;

import java.awt.Color;
import java.awt.Graphics2D;

public class Ship {
	public static double hitRadius = 35;
	public double landRadius = 35;
	public Body landed;
	public double landAngle;
	public double x, y; //coordinates of center
	public double vx, vy;
	
	public boolean hasLanded() {
		return landed != null;
	}
	
	public void physics() {
		if (!hasLanded()) {
			x += vx * LD34.dt;
			y += vy * LD34.dt;
		} else {
			x = landed.x + Math.cos(landed.angle+landAngle)*(landed.hitRadius+landRadius);
			y = landed.y + Math.sin(landed.angle+landAngle)*(landed.hitRadius+landRadius);
		}
	}
	
	public void land(Body b) {
		landAngle = Math.atan2(y-b.y, x-b.x)-b.angle;
		x = b.x + Math.cos(b.angle+landAngle)*(b.hitRadius+landRadius);
		y = b.y + Math.sin(b.angle+landAngle)*(b.hitRadius+landRadius);
		landed = b;
	}
	
	public void launch(double speed) {
		double dx = x-landed.x;
		double dy = y-landed.y;
		double mag = Math.hypot(dx, dy);
		vx = dx * speed / mag;
		vy = dy * speed / mag;
		landed = null;
	}
	
	public void approachExit(Exit exit) {
		double move = Math.hypot(vx, vy) * LD34.dt;
		double dist = Math.hypot(x-exit.x, exit.y);
		if (dist < move) {
			x = exit.x;
			y = exit.y;
		} else {
			x += (exit.x-x)*move/dist;
			y += (exit.y-y)*move/dist;
		}
	}
	
	public void paint(Graphics2D g) {
		g.setColor(Color.white);
		g.fillOval((int)(x-hitRadius), (int)(y-hitRadius), (int)(2*hitRadius), (int)(2*hitRadius));
	}
}
