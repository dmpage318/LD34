package TnT.ld.ld34;

import java.awt.Color;
import java.awt.Graphics2D;

public class Star extends Body {
	public static final double RADIUS=75;//NO IDEA
	public Star(double x, double y, double radius) {
		this.x = x;
		this.y = y;
		hitRadius = radius;
		
	}
	public void doGravity(Ship s) {
		mass = hitRadius/10;
		super.doGravity(s);
	}
	public void paint(Graphics2D g) {
		g.setColor(Color.yellow);
		g.fillOval((int)(x-hitRadius), (int)(y-hitRadius), (int)(2*hitRadius), (int)(2*hitRadius));
	}
	public Star(double x,double y){
		this(x,y,RADIUS);
	}
	public boolean canLand() {
		return false;
	}

	public boolean canKill() {
		return true;
	}

	public Body clone() {
		return new Star(x, y, hitRadius);
	}

}
