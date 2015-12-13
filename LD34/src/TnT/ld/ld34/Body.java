package TnT.ld.ld34;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.PrintWriter;

public abstract class Body {
	public static double G = 50e5;
	public double hitRadius;
	public double x, y; //coordinates of center
	public double angle; //angle of rotation
	public double omega; //angular rate
	public double mass;
	public double localGravRadius;
	public double localGravAccel = 50;
	
	public static double MIN_RAD=50;
	public static double MAX_RAD=1000;
	public Path2D.Double path;
	public PathIterator it=null;
	double delta=.1;
	public boolean fixed=true;
	private double[] cod=new double[6];
	private double cx=0;
	private double cy=0;
	private double nx=0;
	private double ny=0;
	private double x0=0;
	private double y0=0;
	private double t=0;
	private double DT=.002;
	public double vel=0;//pixels per sec
	
	public abstract boolean canLand();
	public abstract boolean canKill();
	public abstract Body clone();
	
	public Body(double x, double y, double radius,boolean fixed, double omega,double vel){
		this.x=x;
		this.y=y;
		this.fixed=fixed;
		this.omega=omega;
		this.vel=vel;
		setRadius(radius);
	}
	public Body(double x, double y,double radius){
		this(x,y,radius,true,0,0);
	}
	public Body(double x, double y){
		this(x,y,0);
	}
//	public Body()
	public Body(){
		
	}
	public void init(double[] data){
		x=data[0];
		y=data[1];
		setRadius(data[2]);
		fixed=data[3]==1;
		omega=data[4];
		vel=data[5];
	}
	public boolean collides(Ship s) {
		return Math.hypot(x-s.x, y-s.y) < hitRadius+s.hitRadius;
	}
	public void setPoint(double x,double y){
		this.x=x;
		this.y=y;
	}
	public void physics() {
		angle += omega * LD34.dt;
		angle %= 2*Math.PI;
		if(!fixed)setNextPosition();
	}
	
	public void doGravity(Ship s) {
		double dx = x-s.x;
		double dy = y-s.y;
		double dist = Math.hypot(dx, dy);
		double mag = G * mass / Math.pow(dist, 3) * LD34.dt;
		s.vx += dx * mag;
		s.vy += dy * mag;
		if (dist<localGravRadius+s.hitRadius) {
			s.vx += dx * localGravAccel / dist;
			s.vy += dy * localGravAccel / dist;
		}
	}
	public void setRadius(double radius) {
		hitRadius = radius;
	}
	public void paint(Graphics2D g) {
		g.setColor(Color.gray);
		g.fillOval((int)(x-hitRadius), (int)(y-hitRadius), (int)(2*hitRadius), (int)(2*hitRadius));
		g.setColor(Color.black);
		g.drawLine((int)x, (int)y, (int)(x+hitRadius*Math.cos(angle)), (int)(y+hitRadius*Math.sin(angle)));
	}
	public boolean contains(Point2D.Double p){
		return Math.hypot(p.x-this.x,p.y-this.y)<this.hitRadius;
	}
	public boolean contains(Point p) {
		return Math.hypot(p.x-this.x,p.y-this.y)<this.hitRadius;
	}

	public void setNextPosition(){
		if(it==null){
			if(path==null)return;//wtf the try'na' pull?
			it=path.getPathIterator(null);
		}		
		if(t>=1){//move to next seg
			t=0;
			it.next();
			if(it.isDone()){
				it=path.getPathIterator(null);
			}
			x0=cx;
			y0=cy;
		}
		int q=it.currentSegment(cod);			
		if(q==it.SEG_QUADTO){
			while(Math.hypot(nx-x,ny-y)<delta && t<1){
				nx=(1-t)*((1-t)*x0+(t*cod[0]))+t*((1-t)*cod[0]+(t*cod[2]));
				ny=(1-t)*((1-t)*y0+(t*cod[1]))+t*((1-t)*cod[1]+(t*cod[3]));
				t+=DT;
			}
			cx=nx;
			cy=ny;
		}
		else if(q==it.SEG_CUBICTO){
			while(Math.hypot(nx-cx,ny-cy)<delta&&t<1){
				nx=Math.pow(1-t, 3)*x0+3*Math.pow(1-t, 2)*t*cod[0]+3*(1-t)*t*t*cod[2]+t*t*t*cod[4];//(1-t)*((1-t)*cod[0]+(t*cod[2]))+t*((1-t)*cod[2]+(t*cod[4]));
				ny=Math.pow(1-t, 3)*y0+3*Math.pow(1-t, 2)*t*cod[1]+3*(1-t)*t*t*cod[3]+t*t*t*cod[5];//(1-t)*((1-t)*cod[1]+(t*cod[3]))+t*((1-t)*cod[3]+(t*cod[5]));
				t+=DT;
			}
			cx=nx;
			cy=ny;
		}
		else if(q==it.SEG_LINETO){
			while(Math.hypot(nx-cx, ny-cy)<delta&&t<1){
				nx=x0+t*(cod[0]-x0);
				ny=y0+t*(cod[1]-y0);
				t+=DT;
			}
			cx=nx;
			cy=ny;
		}
		else if(q==it.SEG_MOVETO){
			cx=cod[0];
			cy=cod[1];
			nx=cx;
			ny=cy;
			x0=cx;
			y0=cy;
			it.next();
		}
		else if(q==it.SEG_CLOSE){
			it.next();
			if(it.isDone()){
				it=path.getPathIterator(null);
			}
			x0=cx;
			y0=cy;
		}
		x=cx;
		y=cy;

	}
	String c=",";
	public void save(PrintWriter pw){
		System.out.println("Saving: "+toString());
		pw.println(this.getClass().getSimpleName()+":"+x+c+y+c+hitRadius+c+(fixed?1:0)+c+omega+c+vel);
		if(!fixed){
			PathIterator iter=path.getPathIterator(null);
			double[] dat=new double[6];
			while(!iter.isDone()){
				int cur=iter.currentSegment(dat);
				switch(cur){
					case PathIterator.SEG_CLOSE:break;
					case PathIterator.SEG_MOVETO:pw.println("moveTo:"+dat[0]+c+dat[1]);break;
					case PathIterator.SEG_LINETO:pw.println("lineTo:"+dat[0]+c+dat[1]);break;
					case PathIterator.SEG_QUADTO:pw.println("quadTo:"+dat[0]+c+dat[1]+c+dat[2]+c+dat[3]);break;
					case PathIterator.SEG_CUBICTO:pw.println("curveTo:"+dat[0]+c+dat[1]+c+dat[2]+c+dat[3]+c+dat[4]+c+dat[5]);break;
				}
				iter.next();
			}
			pw.println("END");
		}
	}
	public String toString(){
		return this.getClass().getSimpleName()+": ("+x+","+y+"), fixed="+fixed;
	}

}
