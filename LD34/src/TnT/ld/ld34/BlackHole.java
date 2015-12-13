package TnT.ld.ld34;

import java.awt.Color;
import java.awt.Graphics2D;

public class BlackHole extends Body {

	public BlackHole(double x, double y) {
		this.x = x;
		this.y = y;
		hitRadius = 30;
		mass = 10;
	}
	
	public void paint(Graphics2D g) {
		g.setColor(Color.black);
		g.fillOval((int)(x-hitRadius), (int)(y-hitRadius), (int)(2*hitRadius), (int)(2*hitRadius));
		g.setColor(Color.white);
		g.drawOval((int)(x-hitRadius), (int)(y-hitRadius), (int)(2*hitRadius), (int)(2*hitRadius));
	}
	
	public boolean canLand() {
		return false;
	}

	public boolean canKill() {
		return true;
	}

	public Body clone() {
		return new BlackHole(x, y);
	}

}
