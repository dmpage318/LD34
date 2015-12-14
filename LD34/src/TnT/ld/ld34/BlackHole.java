package TnT.ld.ld34;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class BlackHole extends Body {
	static String[] imNames = new String[] {
			"blackhole3", "blackhole5"
	};
	static BufferedImage[] images = new BufferedImage[imNames.length];
	static {
		for (int i = 0; i < images.length; i++) {
			try {
				images[i] = ImageIO.read(Planet.class.getResource(imNames[i]+".png"));
				System.out.println(images[i].getWidth()+", "+images[i].getHeight());
			} catch (Exception e) {
				System.out.println("failed to load planet image: "+imNames[i]);
				e.printStackTrace();
			}
		}
	}
	public BlackHole(){
		this(0,0);
	}
	public BlackHole(double x, double y) {
		super(x,y,75);
		mass = 30;
//		while(img==null){
//			img=images[(int)(Math.random()*images.length)];
//		}
		tick++;
		img=images[tick%images.length];
	}
	static int tick=0;
	BufferedImage img;
	public void paint(Graphics2D g) {
//		g.setColor(Color.black);
//		g.fillOval((int)(x-hitRadius), (int)(y-hitRadius), (int)(2*hitRadius), (int)(2*hitRadius));
//		g.setColor(Color.white);
//		g.drawOval((int)(x-hitRadius), (int)(y-hitRadius), (int)(2*hitRadius), (int)(2*hitRadius));
		AffineTransform t = AffineTransform.getTranslateInstance(x, y);
		t.scale(2*hitRadius/img.getWidth(), 2*hitRadius/img.getHeight());
		t.translate(-img.getWidth()/2, -img.getHeight()/2);
		t.rotate(angle, img.getWidth()/2, img.getHeight()/2);
		g.drawImage(img, t, null);
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
