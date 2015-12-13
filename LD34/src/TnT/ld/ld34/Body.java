package TnT.ld.ld34;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

public abstract class Body {
	public static double G = 50e5;
	public double hitRadius;
	public double x, y; //coordinates of center
	public double angle; //angle of rotation
	public double omega; //angular rate
	public double mass;
	public double localGravRadius;
	public double localGravAccel = 50;
	
	public static double MIN_RAD=50;
	public static double MAX_RAD=1000;
	
	public abstract boolean canLand();
	public abstract boolean canKill();
	public abstract Body clone();
	
	public boolean collides(Ship s) {
		return Math.hypot(x-s.x, y-s.y) < hitRadius+s.hitRadius;
	}
	public void setPoint(double x,double y){
		this.x=x;
		this.y=y;
	}
	public void physics() {
		angle += omega * LD34.dt;
		angle %= 2*Math.PI;
	}
	
	public void doGravity(Ship s) {
		double dx = x-s.x;
		double dy = y-s.y;
		double dist = Math.hypot(dx, dy);
		double mag = G * mass / Math.pow(dist, 3) * LD34.dt;
		s.vx += dx * mag;
		s.vy += dy * mag;
		if (dist<localGravRadius+s.hitRadius) {
			s.vx += dx * localGravAccel / dist;
			s.vy += dy * localGravAccel / dist;
		}
	}
	
	public void paint(Graphics2D g) {
		g.setColor(Color.gray);
		g.fillOval((int)(x-hitRadius), (int)(y-hitRadius), (int)(2*hitRadius), (int)(2*hitRadius));
		g.setColor(Color.black);
		g.drawLine((int)x, (int)y, (int)(x+hitRadius*Math.cos(angle)), (int)(y+hitRadius*Math.sin(angle)));
	}
	public boolean contains(Point2D.Double p){
		return Math.hypot(p.x-this.x,p.y-this.y)<this.hitRadius;
	}
	public boolean contains(Point p) {
		return Math.hypot(p.x-this.x,p.y-this.y)<this.hitRadius;
	}
	
}
