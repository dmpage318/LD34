package TnT.ld.ld34;

import java.awt.Color;
import java.awt.Graphics2D;

public class Ship {
	public double hitRadius = 35;
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
		System.out.println("hit planet!");
		landAngle = Math.atan2(y-b.y, x-b.x)-b.angle;
		x = b.x + Math.cos(b.angle+landAngle)*(b.hitRadius+landRadius);
		y = b.y + Math.sin(b.angle+landAngle)*(b.hitRadius+landRadius);
		landed = b;
	}
	
	public void paint(Graphics2D g) {
		g.setColor(Color.white);
		g.fillOval((int)(x-hitRadius), (int)(y-hitRadius), (int)(2*hitRadius), (int)(2*hitRadius));
	}
}
