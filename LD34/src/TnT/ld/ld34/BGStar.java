package TnT.ld.ld34;


public class BGStar{
	double speed;
	double freq=100;
	int square=0;
	double x,y;
	double tick=0;
	int width,height;
	int twinkle;
	static final double TWINKLE_FREQ=2.2;//must be greater than 2
	public BGStar(int x,int y){
		super();
		this.x=x;
		this.y=y;
		speed=Math.random()/2+1;
		square=(int)(Math.random()*5)+3;
		width=square;
		height=square;
		twinkle=(int)Math.min(square/3, Math.random()*3+1);
	}
	boolean wasTwinkle=false;
	public void tick(){
		twinkle=(int)((Math.random()-.5)*TWINKLE_FREQ);
		x-=speed;
//		if(width>square){
//			System.out.println("TWINKLE");
//		}
	}
}
