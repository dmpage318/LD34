package TnT.ld.ld34;

import java.awt.geom.Path2D;
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
			String c=",";
			for(Body b:level.bodies){
				if(hold.contains(b))continue;
				b.save(pw);
				if(b instanceof Portal){
					((Portal)b).linked.save(pw);
					hold.add(((Portal) b).linked);
				}
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
		Help.clear();
		Scanner scan;
		try{
			scan=new Scanner(Loader.class.getResource(name+EXT).openStream());
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
		l.next=scan.nextLine();
		Portal p1=null;
		while(scan.hasNextLine()){
			String temp=scan.nextLine();
			String[] dat=temp.split("[:,]");
			double[] doub=new double[dat.length-1];
			for(int i=0;i<doub.length;i++){
				doub[i]=Double.parseDouble(dat[i+1]);
			}
			try {
				Body b=(Body) Class.forName(Loader.class.getPackage().getName()+"."+dat[0]).newInstance();
				b.init(doub);
				l.bodies.add(b);
				if(b instanceof Portal){
					if(p1==null)p1=(Portal) b;
					else{
						if(!(b instanceof Portal))throw new Exception("Corrupt File!");
						p1.link((Portal) b);
						p1=null;
					}
				}
				if(!b.fixed){
					String line=scan.nextLine();
					Path2D.Double path=new Path2D.Double();
					ArrayList<Double> pa=new ArrayList<Double>();
					ArrayList<Class> cl=new ArrayList<Class>();
					for(int i=0;i<6;i++)cl.add(double.class);
					Class c=path.getClass();					
					Class[] ca;					
					Class dc=double.class;
					while(!line.equals("END")){
						String[] param=line.split("[:,]");
						doub=new double[param.length-1];
						pa.clear();
						for(int i=0;i<doub.length;i++){
							pa.add(Double.parseDouble(param[i+1]));
						}
						ca=new Class[doub.length];
						System.out.println(param[0]+","+doub.length);
						cl.subList(0, doub.length).toArray(ca);
						c.getMethod(param[0],ca).invoke(path, pa.toArray());		
						line=scan.nextLine();
					}
					b.path=path;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		l.setStart(l.bodies.get(land));		
		scan.close();
		return l;
	}
}
