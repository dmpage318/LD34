package TnT.ld.ld34;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Star extends Body {
	public static String[] imNames = new String[] {
		"star1", "star2"
	};
	public static BufferedImage[] images = new BufferedImage[imNames.length];
	static {
		for (int i = 0; i < images.length; i++) {
			try {
				images[i] = ImageIO.read(Star.class.getResource(imNames[i]+".png"));
			} catch (Exception e) {
				System.out.println("failed to load star image: "+imNames[i]);
				e.printStackTrace();
			}
		}
	}
	public static final double RADIUS=75;//NO IDEA
	public Star(){
		fixed=true;
	}
	public Star(double x, double y, double radius) {
		super(x,y,radius);
	}
	public void doGravity(Ship s) {
		super.doGravity(s);
	}
	public void setRadius(double radius) {
		super.setRadius(radius);
		mass = hitRadius/10;
	}
	public void paint(Graphics2D g) {
		g.drawImage(images[1], (int)(x-hitRadius), (int)(y-hitRadius), (int)(2*hitRadius), (int)(2*hitRadius), null);
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
