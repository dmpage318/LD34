package TnT.ld.ld34;

import java.awt.Color;
import java.awt.Graphics2D;

public class Exit extends Body {

	public Exit(double x, double y) {
		this.x = x;
		this.y = y;
		hitRadius = 50;
		localGravRadius = 100;
	}
	
	public void paint(Graphics2D g) {
		g.setColor(Color.orange);
		g.fillOval((int)(x-hitRadius), (int)(y-hitRadius), (int)(2*hitRadius), (int)(2*hitRadius));
		g.setColor(Color.yellow);
		g.drawOval((int)(x-localGravRadius), (int)(y-localGravRadius), (int)(2*localGravRadius), (int)(2*localGravRadius));
	}
	
	public boolean canLand() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canKill() {
		// TODO Auto-generated method stub
		return false;
	}

	public Body clone() {
		return new Exit(x, y);
	}
	
	
}
