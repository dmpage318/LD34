package TnT.ld.ld34;

import java.awt.geom.Path2D;

public class Planet extends Body {
	public Path2D.Double path;
	public boolean fixed=true;
	public static final double RADIUS=100,OMEGA=.5;
	public Planet(double x, double y, double radius, double omega) {
		this.x = x;
		this.y = y;
		this.hitRadius = radius;
		this.omega = omega;
		mass = 10;
	}
	public Planet(double x, double y){
		this(x,y,RADIUS,OMEGA);
	}
	public boolean canLand() {
		return true;
	}

	public double getLocalGravRadius() {
		return 0;
	}

	public double getLocalGravAccel() {
		return 0;
	}

	public boolean canKill() {
		return false;
	}
	
	public Planet clone() {
		Planet p =  new Planet(x, y, hitRadius, omega);
		p.mass = mass;
		return p;
	}
}
