package TnT.ld.ld34;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

public class LevelEditor{
	public Level level;
	public LevelEditor(){
		level=new Level();
		level.editMode=true;
		level.ship.x=-10000;
		level.ship.y=-10000;
		level.bodies.add(level.exit);
	}
	Body active=null;
	static double WHEEL_RATIO=-5.0;
	static String[] names=new String[]{"Planet","Star","Black Hole","Portal"};
	boolean selecting;
	public Rectangle selectRect;
	Point cur;
	Point2D.Double orig;
	Color activeCol=new Color(0x8000FF00,true);//has alpha
	private Graphics2D holdG;//read only copy of the graphics object that will let us size things later
	public void paint(Graphics2D g){
		LD34.theLD.gameGraphics(level,g);
		holdG=g;
		if(selecting){
			if(selectRect!=null){
				//				System.out.println(selectRect);
				//draw the selection rectangle
				g.setColor(Color.BLACK);
				g.fill(selectRect);
				g.setColor(Color.WHITE);
				g.draw(selectRect);
				for(int i=0;i<names.length;i++){
					g.drawString(names[i],selectRect.x+2,selectRect.y+(i+1)*g.getFontMetrics().getHeight()-2);
				}
			}
		}
		if(active!=null){
			g.setColor(activeCol);
			LD34.scaleTo(g);
			
			g.drawOval((int)(active.x-active.hitRadius),(int) (active.y-active.hitRadius),(int) (active.hitRadius*2),(int) (active.hitRadius*2));
			g.fillOval((int)(active.x-active.hitRadius),(int) (active.y-active.hitRadius),(int) (active.hitRadius*2),(int) (active.hitRadius*2));
			
			LD34.scaleFrom(g);
			
		}
	}
	Point2D.Double trans;
	//any two bodies must always satisfy the condition that Math.hypot(x1-x2,y1-y2)<rad1+rad2+ship.hitRadius
	public void mousePressed(MouseEvent e){
		cur=e.getPoint();
		trans=LD34.scalePointTo(new Point2D.Double(e.getX(),e.getY()));
		switch(e.getButton()){
			case MouseEvent.BUTTON1:
				if(!selecting){
					//we need to pick a body to control
					boolean b=true;
					for(int i=0;i<level.bodies.size()&&b;i++){
						if(level.bodies.get(i).contains(trans)){
							active=level.bodies.get(i);
							b=false;
						}
					}
					if(b){
						active=null;
					}
				}else{
					//we need to select something from the menu
					if(selectRect.contains(e.getPoint())){
						int sel=(int) ((e.getY()-selectRect.getY())/holdG.getFontMetrics().getHeight());//should be the index of the item that we clicked
//						System.out.println(names[sel]);
						switch(sel){
							case 0://planet
								active=new Planet((selectRect.getX()-LD34.getWidth()/2)/LD34.gscale,(selectRect.getY()-LD34.getHeight()/2)/LD34.gscale);
								level.bodies.add(active);
								break;
							case 1://star
								break;
							case 2://black hole
								System.out.println("bh");
								active=new BlackHole((selectRect.getX()-LD34.getWidth()/2)/LD34.gscale,(selectRect.getY()-LD34.getHeight()/2)/LD34.gscale);
								level.bodies.add(active);
								break;
							case 3://portal
								break;
						}
					}
						//TODO set the active body to be the type requested
						selectRect=null;
						selecting=false;
//					}
				}
				break;
			case MouseEvent.BUTTON3:
				Body under = null;
				Body near = null;//idk if we will need this but it is here if we do
				double dist=Double.POSITIVE_INFINITY;
				d:for(int i=0;i<level.bodies.size();i++){
					if(level.bodies.get(i).contains(e.getPoint())){
						under=level.bodies.get(i);
						if(Math.hypot(e.getX()-level.bodies.get(i).x, e.getY()-level.bodies.get(i).y)<dist){
							dist=Math.hypot(e.getX()-level.bodies.get(i).x, e.getY()-level.bodies.get(i).y);
							near=level.bodies.get(i);
						}
						break d;
					}
				}
				if(under==null&&dist>Body.MIN_RAD+Ship.hitRadius){//if there is not room for a body, do not let it exist
					selecting=true;
					int w=0,h=names.length*holdG.getFontMetrics().getHeight();//this might should be something else
					for(int i=0;i<names.length;i++){
						w=Math.max(SwingUtilities.computeStringWidth(holdG.getFontMetrics(),names[i]),w);
					}

					selectRect=new Rectangle(e.getX(),e.getY(),w,h);
				}
				break;
		}
		if(active!=null){
			orig=new Point2D.Double(active.x,active.y);
		}
	}
	public void mouseReleased(MouseEvent e){
		cur=null;
		orig=null;
	}
	public void keyPressed(KeyEvent e){
		switch(e.getKeyCode()){
			case KeyEvent.VK_SPACE:
				if(active instanceof Planet){
					level.ship.land(active);
				}
				break;
		}
	}
	public void keyReleased(KeyEvent e) {

	}
	public void mouseDragged(MouseEvent e){
		if(active!=null){
			//dx/gscale
			double x=orig.x+(e.getX()-cur.getX())/LD34.gscale;
			double y=orig.y+(e.getY()-cur.getY())/LD34.gscale;
			boolean b=true;
			for(int i=0;i<level.bodies.size()&&b;i++){
				if(level.bodies.get(i)!=active){
					b=Math.hypot(x-level.bodies.get(i).x,y-level.bodies.get(i).y)<active.hitRadius+level.bodies.get(i).hitRadius+Ship.hitRadius;
					b=!b;
				}
			}
			if(b){
				active.setPoint(x,y);
			}
			//we need to make it so that 
		}
	}
	public void mouseMoved(MouseEvent e){

	}
	public void mouseWheelMoved(MouseWheelEvent e){
		if(active!=null){
			double rad=active.hitRadius+e.getWheelRotation()*WHEEL_RATIO;
			if(rad<Body.MIN_RAD||rad>Body.MAX_RAD)return;//you aint allowed to be too big o too small
			boolean b=true;
			Body check=active.clone();
			check.hitRadius=rad;
			for(int i=0;i<level.bodies.size()&&b;i++){
				if(level.bodies.get(i)!=active){
					//we use a boolean similar to the above, but substituting to check for the radius primarily instead of the location
					b=Math.hypot(check.x-level.bodies.get(i).x,check.y-level.bodies.get(i).y)<check.hitRadius+level.bodies.get(i).hitRadius+Ship.hitRadius;
					b=!b;
				}
			}
			if(b){
				active.hitRadius=rad;
			}
		}
	}
	/*
	public boolean contains(Point p){
		return Math.hypot(p.x-this.x,p.y-this.y)<this.hitRadius;
	}
	 */
	public void physics() {
		level.physics();
	}
	
}
