package TnT.ld.ld34;

import java.awt.Color;
import java.awt.Graphics2D;

public class Explosion implements Animation {
	double x, y;
	static final int TOTAL = 50;
	static final int GROW = 10;
	
	public Explosion(Ship s) {
		x = s.x;
		y = s.y;
	}
	
	int ticks = 0;
	public void tick() {
		ticks++;
	}

	public boolean done() {
		return ticks>=TOTAL;
	}

	public void paint(Graphics2D g) {
		double size = 100;
		int alpha = 255;
		if (ticks<GROW) size *= (double)ticks/GROW;
		else alpha -= 255*(ticks-GROW)/(TOTAL-GROW);
		g.setColor(new Color(255, 128, 0, Math.max(0, alpha)));
		g.fillOval((int)(x-size/2), (int)(y-size/2), (int)(size), (int)(size));
	}
	
}
