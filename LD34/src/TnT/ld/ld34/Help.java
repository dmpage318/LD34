package TnT.ld.ld34;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Help extends Rectangle implements Animation{
	public String message;
	
	public long duration=10000;
	public static int ON_DEATH=-1;
	public static int ON_FIRST_LAUNCH=-2;
	public static int ON_FIRST_LAND=-3;
	public static int ON_PORTAL_EXIT=-4;
	public static int ON_EXIT=-5;//not possible?
	public static int ON_BARRIER_DEATH=-6;
	public static int ON_BH_DEATH=-7;
	public static int ON_SUN_DEATH=-8;
	public int type=0;
	public long time=0;
	
	boolean shown=false;
	static ArrayList<Integer> events=new ArrayList<Integer>(); 
	public Help(int i, int j, int k, int l) {
		super(i,j,k,l);
		// TODO Auto-generated constructor stub
	}

	static void fireHelpEvent(int type){
		events.add(type);
		System.out.println("fire "+ type);
	}
	
//	public int start
	public void paint(Graphics2D g){

//		LD34.scaleFrom(g);
//		System.out.println();
		if(!shown)return;
//		System.out.println("HI!");
		Color c=g.getColor();
		Font f=g.getFont();
		g.setFont(f.deriveFont((float) (20/LD34.gscale)));
		g.setColor(Color.black);
		g.fillRect((int)(x/LD34.gscale),(int)(y/LD34.gscale),(int)(width/LD34.gscale),(int)(height/LD34.gscale));
		g.setColor(Color.white);
		int y=this.y+17;
		for(String s:message.split("%n")){
			g.drawString(s, (int)((x+5)/LD34.gscale),(int)( y/LD34.gscale));
			y+=25;
		}
		
		g.setColor(c);
		g.setFont(f);;
//		LD34.scaleTo(g);
	}
	public void tick(){
//		System.out.println(time);
		if(!shown){
			if(type>=0){
				if(type<=time){
					shown=true;
					time=0;
					tick();
				}
			}else if(events.contains(type)){
				shown=true;
				time=0;
				tick();
			}
		}
		else{			
			//NOT RESPONSIBLE FOR OWN DEATH!!!!
		}
		time+=20;
	}
	public static void clear(){
		events.clear();
	}


	@Override
	public boolean done() {
		return shown&&duration-time<=0;
	}

}
