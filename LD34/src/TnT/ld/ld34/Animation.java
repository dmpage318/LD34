package TnT.ld.ld34;

import java.awt.Graphics2D;

public abstract class Animation {
	
	public abstract void tick();
	public abstract boolean done();
	public abstract void paint(Graphics2D g);
	
}
