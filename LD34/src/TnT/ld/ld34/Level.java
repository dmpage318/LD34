package TnT.ld.ld34;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;

public class Level {
	public static int SIZE = 4000; //radius of the intergalactic barrier
	public static double STAR_FREQ=.05;//no idea. this is how likely each tick will produce a bg star is (.05)
	public static double STAR_COUNT=SIZE;
	public static double PROJECT_TIME = 4;
	public ArrayList<Body> bodies = new ArrayList<Body>();
	public Ship ship = new Ship();
	public boolean dead = false;
	public boolean win = false;
	public Portal ignore = null;
	public boolean teleported; //used for predicting path
	private int frame = 0; //physics time frame for drawing path
	public Exit exit = new Exit(0, 0);
	public static double launchSpeed = 800;
	public String next="";
	public Body startPlanet;
	public boolean ghostMode;
	public boolean editMode = false;
	ArrayList<Animation> anims = new ArrayList<Animation>();
	ArrayList<BGStar> bgstars=new ArrayList<BGStar>();
	private boolean firstLaunch=true;
	private int lands=0;
	public int endCount;
	//	public boolean eath=false;
	//	public boolean firstLaunch=false;
	public Level(){
		for(int i=0;i<SIZE;i++){
			while(Math.random()<STAR_FREQ){
				int y=(int)(Math.random()*SIZE)-SIZE/2;
				bgstars.add(new BGStar(i-SIZE/2,y));
			}
		}
	}
	double tx, ty;
	double dtx, dty;
	double etx, ety;
	boolean returning;
	public void paint(Graphics2D g) {
		tx = 0;
		ty = 0;
		double xbound = (LD34.getWidth()/2-75)/LD34.gscale;
		double ybound = (LD34.getHeight()/2-75)/LD34.gscale;
		if (ship.x>xbound) tx = xbound-ship.x;
		if (ship.x<-xbound) tx = -xbound-ship.x;
		if (ship.y>ybound) ty = ybound-ship.y;
		if (ship.y<-ybound) ty = -ybound-ship.y;
		if (returning && !dead) {
			double dist = Math.hypot(dtx-tx, dty-ty);
			double speed = Math.min(10*dist, 3000)*LD34.dt;
			if (dist<1) returning = false;
			else {
				dtx += (tx-dtx)*speed/dist;
				dty += (ty-dty)*speed/dist;
				tx = dtx;
				ty = dty;
			}
		}
		double scale = 1;
		double shipFade = 1;
		if (win) {
			endCount--;
			if (endCount<END_TIME-EXIT_TIME-WAIT_TIME) {
				double prog = Math.min(1, (double)(END_TIME-endCount-EXIT_TIME-WAIT_TIME)/ZOOM_TIME);
				scale = Math.pow(30, Math.pow(prog, 2));
				tx = -exit.x*(1-1/scale);
				ty = -exit.y*(1-1/scale);
				tx += (etx + prog*(-exit.x-etx))/scale;
				ty += (ety + prog*(-exit.y-ety))/scale;
				g.scale(scale, scale);
			}
			shipFade = Math.max(0, (double)(endCount-END_TIME+EXIT_TIME)/EXIT_TIME);
			if (endCount==0) LD34.loadLevel(next, true);
		}
		if (zintro) {
			introCount++;
			double prog = Math.min(1, (double)(ZOOM_TIME-introCount)/ZOOM_TIME);
			scale = Math.pow(30, Math.pow(prog, 2));
			tx = -exit.x*(1-1/scale);
			ty = -exit.y*(1-1/scale);
			tx += (etx + prog*(-exit.x-etx))/scale;
			ty += (ety + prog*(-exit.y-ety))/scale;
			g.scale(scale, scale);
			if (introCount==ZOOM_TIME) zintro = false;
		}

		double frac = .35;
		if (!editMode) g.translate(tx*frac, ty*frac);




		drawBGStars(g);

		if (!editMode) g.translate(tx*(1-frac), ty*(1-frac));


		Stroke s = g.getStroke();
		g.setStroke(new BasicStroke(10));
		g.setColor(Color.red);
		g.drawOval(-SIZE/2, -SIZE/2, SIZE, SIZE);
		g.setStroke(s);

		for (int i = 0; i < bodies.size(); i++) {
			Body b = bodies.get(i);
			if(!b.fixed){
				g.setColor(pathCol);
				s=g.getStroke();
				g.setStroke(dashed);
				g.draw(b.path);
				//				g.fill(b.path);
				g.setStroke(s);
			}
		}
		for (int i = 0; i < bodies.size(); i++) {
			Body b = bodies.get(i);
			b.paint(g);
		}
		exit.paint(g);
		if (!dead) ship.paint(g, shipFade);

		drawShadows(g);
		
		for (int i = 0; i < anims.size(); i++) {
			anims.get(i).paint(g);
		}

		s = g.getStroke();
		g.setStroke(new BasicStroke(2));
		g.setColor(Color.white);
		ArrayList<Path2D.Double> paths = this.paths;
		for (int i = 0; i < paths.size(); i++) {
			int alpha = Math.max(0, (int)(255*(1-(double)i/(PROJECT_TIME*100d/6))));
			g.setColor(new Color(255, 255, 255, alpha));
			g.draw(paths.get(i));
		}
		g.setStroke(s);
		if (!editMode) g.translate(-tx, -ty);
		if (win) g.scale(1/scale, 1/scale);
	}
	public void paintHUD(Graphics2D g) {
		if (editMode) return;
		Paint p = g.getPaint();
		int b1 = (int) (30*LD34.gscale);
		int h = (int) (30*LD34.gscale);
		int b2 = (int) (20*LD34.gscale);
		int w = (int) (400*LD34.gscale);
		g.setPaint(new LinearGradientPaint(b1, 0, w+b1, 0, new float[]{0, .5f, 1}, new Color[]{Color.red, Color.yellow, Color.green}));
		g.fillRect(b1, LD34.getHeight()-h-b1, (int)(power*w), h);
		g.setColor(Color.white);
		g.drawRect(b2, LD34.getHeight()-2*b1-h+b2, w+2*(b1-b2), h+2*(b1-b2));
		g.setPaint(p);
	}
	final static float dash1[] = {10.0f};
	final static BasicStroke dashed =
			new BasicStroke(1.0f,
					BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER,
					10.0f, dash1, 0.0f);
	Color pathCol=new Color(0xA17FFF);
	//	Color pathCol=Color.magenta;
	static final double LIGHT_RADIUS=.1;
	Polygon shadow;
	public void drawShadows(Graphics2D g){
		Paint old=g.getPaint();
		boolean hasStar=false;
		d:for(int i=0;i<bodies.size();i++){
			if(!(bodies.get(i) instanceof Planet)){
				continue d;//only planets get shadows
			}
			Planet p=(Planet)bodies.get(i);
			ArrayList<Area> lights=new ArrayList<Area>();
			Area shadow=new Area(new Ellipse2D.Double(p.x-p.hitRadius,p.y-p.hitRadius,p.hitRadius*2,p.hitRadius*2));
			Area planet=(Area)shadow.clone();
			dd:for(int j=0;j<bodies.size();j++){
				if(!(bodies.get(j)instanceof Star)){
					continue dd;
				}
				hasStar=true;
				Star s=(Star)bodies.get(j);
				double dx=-(p.y-s.y);
				double dy=p.x-s.x;
				double dist=Math.hypot(dx, dy);
				dx/=dist;
				dy/=dist;
				Polygon light=new Polygon();
				light.addPoint((int) (p.x+dx*p.hitRadius),(int) (p.y+dy*p.hitRadius));
				light.addPoint((int) (p.x-dx*p.hitRadius),(int) (p.y-dy*p.hitRadius));
				light.addPoint((int)(p.x-dx*p.hitRadius-dy*p.hitRadius),(int)(p.y-dy*p.hitRadius+dx*p.hitRadius));
				light.addPoint((int)(p.x+dx*p.hitRadius-dy*p.hitRadius),(int)(p.y+dy*p.hitRadius+dx*p.hitRadius));
				Area a=new Area(light);
				shadow.subtract(a);
				lights.add(a);
				//				g.setColor(new Color(1f,1f,1f,.75f));
				//				g.fill(light);
			}
			//			full.subtract(shadow);
			if(hasStar){
				ArrayList<Area> newLights=new ArrayList<Area>();
				for(int l1=0;l1<lights.size();l1++){
					newLights.add((Area)lights.get(l1).clone());
					newLights.get(l1).intersect(planet);
				}
				for(int l1=0;l1<lights.size();l1++){
					for(int l2=0;l2<lights.size();l2++){
						if(l1==l2)continue;
						newLights.get(l1).subtract(lights.get(l2));
						//					newLights.add(l);
					}
				}
				if(newLights.size()==0&&lights.size()>0){
					newLights.add(lights.get(0));
				}
				g.setColor(new Color(0f,0f,0f,.25f));
				for(int j=0;j<newLights.size();j++){
					g.fill(newLights.get(j));
					//				System.out.println("fill lights");
				}
			}
			g.setColor(new Color(0f,0f,0f,(hasStar?.75f:.5f)));
			g.fill(shadow);
		}

		g.setPaint(old);
	}
	public void drawBGStars(Graphics2D g) {
		g.setColor(Color.white);
		BGStar b=null;
		if(editMode){
			for(int i=0;i<bgstars.size();i++){
				//				g.draw(bgstars.get(i));
				//				g.fill(bgstars.get(i));
				b=bgstars.get(i);
				g.fillOval((int)(b.x-b.twinkle), (int)(b.y-b.twinkle),(int)(b.width+b.twinkle*2),(int)(b.height+b.twinkle*2));
			}
		}else{
			for(int i=0;i<bgstars.size();i++){
				//			g.draw(bgstars.get(i));
				//			g.fill(bgstars.get(i));
				b=bgstars.get(i);
				g.translate(b.speed*tx, b.speed*ty);
				g.fillOval((int)(b.x-b.twinkle), (int)(b.y-b.twinkle),(int)(b.width+b.twinkle*2),(int)(b.height+b.twinkle*2));
				g.translate(-b.speed*tx, -b.speed*ty);
			}
		}
	}

	public void setStart(Body b) {
		ship.ix = ship.x;
		ship.iy = ship.y;
		ship.land(b);
		startPlanet = b;
	}

	public void reset() {
		ship.x = ship.ix;
		ship.y = ship.iy;
		ship.land(startPlanet);
		dead = false;
		win = false;
	}
	
	public boolean zintro = false;
	public int introCount = 0;
	public void introZoom() {
		zintro = true;
	}

	public double power = 1;
	public double dpower = .5;
	public boolean lastLaunch = false;
	public void physics() {
		if (dead) {
			deadCount--;
			if (deadCount==0) {
				dead = false;
				reset();
			}
		}

		teleported = false;
		for (int i = 0; i < bodies.size(); i++) {
			Body b = bodies.get(i);
			b.physics();
			if (!editMode && !win && !dead && !ship.hasLanded()) b.doGravity(ship);
			if (!editMode && !win && !dead && b.collides(ship)) {
				if (b.canKill()){
					die();
					if(b instanceof Star)if(!ghostMode)Help.fireHelpEvent(Help.ON_SUN_DEATH);
					if(b instanceof BlackHole)if(!ghostMode)Help.fireHelpEvent(Help.ON_BH_DEATH);
				}
				else if (b.canLand() && !ship.hasLanded()) {
					ship.land(b);
					lands++;
					if(lands==1)if(!ghostMode)Help.fireHelpEvent(Help.ON_FIRST_LAND);
				}
				else if (b instanceof Portal && b!=ignore){
					((Portal)b).teleport(ship);
					if(!ghostMode)Help.fireHelpEvent(Help.ON_PORTAL_EXIT);
					teleported = true;
					ignore = ((Portal)b).linked;
				}
			}
		}
		boolean launch = LD34.launch;
		if (LD34.windup && ship.hasLanded()) {
			power += dpower * LD34.dt;
			if (power>1) {
				power = 2-power;
				dpower *= -1;
			} else if (power<0) {
				power *= -1;
				dpower *= -1;
			}
		}
		if (!win && !dead) {
			if (!editMode && !ghostMode && launch && !lastLaunch) {
				if (ship.hasLanded()){
					ship.launch(getLaunchSpeed());
					if(firstLaunch){
						if(!ghostMode)Help.fireHelpEvent(Help.ON_FIRST_LAUNCH);
						firstLaunch=false;
					}
					
				}
				else{
					die();
				}
			}
			ship.physics();
			if (!editMode && !ship.hasLanded()) {
				exit.doGravity(ship);
				if (exit.collides(ship)) win();
			}
		}
		if (win) {
			if (next.equals("") && !ship.hasLanded()) ship.land(exit);
			else ship.approachExit(exit);
		}
		if (!dead && !win && !editMode && Math.hypot(ship.x, ship.y) > SIZE/2-Ship.hitRadius) {
			die();
			if(!ghostMode)Help.fireHelpEvent(Help.ON_BARRIER_DEATH);
		}
		if (ignore!=null && !ignore.collides(ship)) ignore = null;
		if (!ship.hasLanded() && !dead && !win) {
			frame++;
			frame %= 6;
		}
		if (!win && !dead && !ghostMode) plotCourse();
		else paths.clear();
		lastLaunch = launch;
	}
	int deadCount;
	public void die() {
		if(!ghostMode)Help.fireHelpEvent(Help.ON_DEATH);
		//		System.out.println("dead");
		if (tx!=0 || ty!=0) returning = true;
		dtx = tx;
		dty = ty;
		dead = true;
		deadCount = Explosion.TOTAL*2;
		anims.add(new Explosion(ship));
	}
	int END_TIME = 210;
	int ZOOM_TIME = 75;
	int EXIT_TIME = 100;
	int WAIT_TIME = 10;
	public void win() {
		etx = tx;
		ety = ty;
		win = true;
		endCount = END_TIME;
	}

	public double getLaunchSpeed() {
		return Math.pow(power, .2) * launchSpeed;
	}

	ArrayList<Path2D.Double> paths = new ArrayList<Path2D.Double>();
	public void plotCourse() {
		Level l = makeGhost();
		ArrayList<Path2D.Double> paths = new ArrayList<Path2D.Double>();
		Path2D.Double path = new Path2D.Double();
		paths.add(path);
		path.moveTo(ship.x, ship.y);
		int count = frame;
		for (int i = 0; i < PROJECT_TIME/LD34.dt && !l.dead && !l.ship.hasLanded() && !l.win; i++) {
			l.physics();
			if (count<3 || l.teleported) path.moveTo(l.ship.x, l.ship.y);
			else path.lineTo(l.ship.x, l.ship.y);
			count++;
			if (count==6) {
				path = new Path2D.Double();
				paths.add(path);
				count = 0;
			}
		}
		this.paths = paths;
	}

	public void addBody(Body b) {
		bodies.add(b);
	}

	public Level makeGhost() {
		Level l = new Level();
		Body b, bc;
		for (int i = 0; i < bodies.size(); i++) {
			b = bodies.get(i);
			bc = b.clone();
			l.bodies.add(bc);
			if (b instanceof Portal) {
				int j = bodies.indexOf(((Portal) b).linked);
				if (j>-1 && j<i) ((Portal)bc).link((Portal)l.bodies.get(j));
			}
		}
		l.exit = (Exit) exit.clone();
		l.ship.x = ship.x;
		l.ship.y = ship.y;
		l.ship.vx = ship.vx;
		l.ship.vy = ship.vy;
		if (ship.hasLanded()) {
			l.ship.landed = ship.landed;
			l.ship.launch(getLaunchSpeed());
			l.ship.physics();
		}
		l.ghostMode = true;
		return l;
	}

	public void animate() {
		Animation a;
		for (int i = 0; i < anims.size(); i++) {
			a = anims.get(i);
			a.tick();
			if (a.done()) {
				anims.remove(i);
				i--;
			}
		}
				for(int i=0;i<bgstars.size();i++){
					bgstars.get(i).tick();
		//			if(bgstars.get(i).x+bgstars.get(i).width<-SIZE/2){
		//				bgstars.remove(i);
//						i--;
		//			}
				}
		//		while(Math.random()<STAR_FREQ){
		//			bgstars.add(new BGStar(SIZE/2,(int)(Math.random()*SIZE)-SIZE/2));
		//		}
	}

	public void fireHelpEvent(int type){
		//doesnt actually fire events	
	}

}
