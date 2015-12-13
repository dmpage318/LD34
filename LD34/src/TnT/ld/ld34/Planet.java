package TnT.ld.ld34;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Planet extends Body {
	static String[] imNames = new String[] {
			"planet1"
	};
	static BufferedImage[] images = new BufferedImage[imNames.length];
	static {
		for (int i = 0; i < images.length; i++) {
			try {
				images[i] = ImageIO.read(Planet.class.getResource(imNames[i]+".png"));
			} catch (Exception e) {
				System.out.println("failed to load planet image: "+imNames[i]);
				e.printStackTrace();
			}
		}
	}
	
	public static final double RADIUS=100,OMEGA=.5;
	public static final double MIN_OMEGA=.05,MAX_OMEGA=5;//min isnt used
	public Planet(double x, double y, double radius, double omega) {
		super(x,y,radius,true,omega,0);
	}
	public Planet(){
		
	}
	public BufferedImage img;
	public void setRadius(double radius) {
		super.setRadius(radius);
		mass = hitRadius/10;
		double best = Double.POSITIVE_INFINITY;
		for (BufferedImage i : images) {
			double diff = Math.abs(2*radius-i.getWidth());
			if (diff<best) {
				best = diff;
				img = i;
			}
		}
	}
	public void doGravity(Ship s) {
		super.doGravity(s);
	}
	public void paint(Graphics2D g) {
		AffineTransform t = AffineTransform.getTranslateInstance(x, y);
		t.scale(2*hitRadius/img.getWidth(), 2*hitRadius/img.getHeight());
		t.translate(-img.getWidth()/2, -img.getHeight()/2);
		t.rotate(angle, img.getWidth()/2, img.getHeight()/2);
		g.drawImage(img, t, null);
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
