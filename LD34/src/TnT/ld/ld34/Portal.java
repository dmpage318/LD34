package TnT.ld.ld34;

import java.awt.Color;
import java.awt.Graphics2D;

public class Portal extends Body {
	Portal linked;
	public static Color[] cols=new Color[]{Color.orange,Color.magenta,Color.blue,Color.pink,Color.cyan,Color.yellow};
	public double color;
	public Portal(){
		this(0,0);
	}
	public Portal(double x, double y,double color) {
		super(x,y,50);
		localGravRadius = 00;
		this.color=color;
	}
	public Portal(double x,double y){
		this(x,y,0);
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
		Color col=cols[((int)color)%cols.length];
		if(color%1>0){
			col=col.darker();
		}
		g.setColor(col);
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
		return new Portal(x, y,color);
	}

}
