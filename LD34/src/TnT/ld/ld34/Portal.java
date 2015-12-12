package TnT.ld.ld34;

import java.awt.Color;
import java.awt.Graphics2D;

public class Portal extends Body {
	Portal linked;
	
	public Portal(double x, double y) {
		this.x = x;
		this.y = y;
		hitRadius = 50;
		localGravRadius = 100;
	}
	
	public void link(Portal p) {
		linked = p;
		p.linked = this;
	}
	
	public void teleport(Ship s) {
		if (linked==null) return;
		s.x = linked.x + x-s.x;
		s.y = linked.y + y-s.y;
	}
	
	public void paint(Graphics2D g) {
		g.setColor(Color.green);
		g.fillOval((int)(x-hitRadius), (int)(y-hitRadius), (int)(2*hitRadius), (int)(2*hitRadius));
		g.setColor(Color.yellow);
		g.drawOval((int)(x-localGravRadius), (int)(y-localGravRadius), (int)(2*localGravRadius), (int)(2*localGravRadius));
	}

	public boolean canLand() {
		return false;
	}

	public boolean canKill() {
		return false;
	}
	
	public Portal clone() {
		return new Portal(x, y);
	}

}
