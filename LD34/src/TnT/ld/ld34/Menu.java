package TnT.ld.ld34;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Menu {
	ArrayList<BGStar> bgstars=new ArrayList<BGStar>();
	ArrayList<Body> bodies=new ArrayList<Body>();
	public static final double BODY_FREQ=.01;
	public Menu(){
		for(int i=0;i<Level.SIZE;i++){
			while(Math.random()<Level.STAR_FREQ){
				int y=(int)(Math.random()*Level.SIZE)-Level.SIZE/2;
				bgstars.add(new BGStar(i-Level.SIZE/2,y));
			}
		}
	}
	public void paint(Graphics2D g) {
		
		g.setColor(Color.white);
		BGStar b=null;
		for(int i=0;i<bgstars.size();i++){
//			g.draw(bgstars.get(i));
//			g.fill(bgstars.get(i));
			b=bgstars.get(i);
			g.fillOval((int)b.x-b.twinkle, (int)b.y-b.twinkle,(int)b.width+b.twinkle*2,(int)b.height+b.twinkle*2);
		}		
	}
	public void physics() {
		for(int i=0;i<bgstars.size();i++){
			bgstars.get(i).tick();
			if(bgstars.get(i).x+bgstars.get(i).width<-Level.SIZE/2){
				bgstars.remove(i);
				i--;
			}
		}
		while(Math.random()<Level.STAR_FREQ){
			bgstars.add(new BGStar(Level.SIZE/2,(int)(Math.random()*Level.SIZE)-Level.SIZE/2));
		}
		if(Math.random()<BODY_FREQ){
			//TODO
		}
	}
}
