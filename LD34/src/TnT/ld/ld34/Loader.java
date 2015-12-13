package TnT.ld.ld34;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class Loader {
	public static String EXT=".bacon";
	public static void saveLevel(Level level,String name){
		File f=new File(name+EXT);
		try {
			f.createNewFile();
			PrintWriter pw=new PrintWriter(f);
			pw.println(level.bodies.indexOf(level.ship.landed));
			pw.println(level.ship.x+","+level.ship.y);
			pw.println(level.exit.x+","+level.exit.y);
			pw.println(level.next);
			ArrayList<Portal> hold=new ArrayList<Portal>();
			for(Body b:level.bodies){
				if(b instanceof Planet){
					pw.println("PLANET:"+b.x+","+b.y+","+b.hitRadius+","+b.omega+","+(((Planet)b).fixed?"1":"0"));
					if(!((Planet)b).fixed){
						//woooohooo save dem paths!!!!!!!
						//TODO PATHS
					}
				}
				if(b instanceof Portal){
					if(hold.contains(b))continue;
					Portal l=((Portal)b).linked;
					pw.println("PORTAL:"+b.x+","+b.y+","+l.x+","+l.y);
					hold.add(l);
					hold.add((Portal)b);
				}
				if(b instanceof BlackHole){
					pw.println("BH:"+b.x+","+b.y);
				}
				if(b instanceof Star){
					pw.println("STAR:"+b.x+","+b.y+","+b.hitRadius);
				}
				//TODO HELP
			}
			pw.flush();
			pw.close();
			
		} catch (IOException e) {
			System.out.println("SAVE FAILED");
			e.printStackTrace();
			return;
		}
	}
	public static Level loadLevel(String name){
		Scanner scan;
		try{
			scan=new Scanner(Loader.class.getResource(name+EXT).openStream());
//			File f=new File(Loader.class.getResource(name+EXT));
		}catch(Exception e){
			System.out.println("THIS FILE AINT IN ECLIPSE!!!!!");
			try {
				scan=new Scanner(new File(name+EXT));
			} catch (FileNotFoundException e1) {
				System.out.println("NOPE! CANT FIND IT! I QUIT! KTHXBYE");
				e1.printStackTrace();
				return null;
			}
			//not in eclipse?			
		}
		Level l=new Level();
		Ship s=new Ship();
		int land=Integer.parseInt(scan.nextLine());
		l.ship=s;
		String[] d=scan.nextLine().split(",");
		l.ship.x=Double.parseDouble(d[0]);
		l.ship.y=Double.parseDouble(d[1]);
		d=scan.nextLine().split(",");
		l.exit.x=Double.parseDouble(d[0]);
		l.exit.y=Double.parseDouble(d[1]);
//		String str=scan.nextLine();
//		str=scan.nextLine();//maybe
		int portNum = 0;
		l.next=scan.nextLine();
		while(scan.hasNextLine()){
			String temp=scan.nextLine();
			String[] dat=temp.split("[:,]");
			double[] doub=new double[dat.length-1];
			for(int i=0;i<doub.length;i++){
				doub[i]=Double.parseDouble(dat[i+1]);
			}
			System.out.println(dat[0]);
			if(dat[0].equals("PLANET")){
				Planet p=new Planet(doub[0],doub[1],doub[2],doub[3]);
				p.fixed=doub[4]==1;
				if(!p.fixed){
					//TODO path
				}
				l.bodies.add(p);
			}else if(dat[0].equals("PORTAL")){
				Portal p1=new Portal(doub[0],doub[1],portNum);
				Portal p2=new Portal(doub[2],doub[3],portNum);
				p1.link(p2);
				l.bodies.add(p1);
				l.bodies.add(p2);
				portNum++;
			}else if(dat[0].equals("BH")){
				l.bodies.add(new BlackHole(doub[0],doub[1]));
			}else if(dat[0].equals("STAR")){
				l.bodies.add(new Star(doub[0],doub[1],doub[2]));
			}else if(dat[0].equals("HELP")){
				
			}else{
				//:(
			}
		}
		l.setStart(l.bodies.get(land));
		
		scan.close();
		return l;
	}
}
