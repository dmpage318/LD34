package TnT.ld.ld34;


public class BGStar{
	double speed;
	double freq=100;
	int square=0;
	double x,y;
	double tick=0;
	int width,height;
	double twinkle;
	static final double BG_DEPTH=.15;
	
	public BGStar(int x,int y){
		super();
		this.x=x;
		this.y=y;
		speed=Math.random()-.5;
		speed*=BG_DEPTH;
		square=(int)(Math.random()*5)+3;
		width=square;
		height=square;
		
	}
	int twinkleCount;
	int maxTwinkle=50;
	int twinkleDir;
	static final int TWINKLE_MAG=2;
	static int num_twinkle=0;
	static int max_num_twinkle=50;
	static final double TWINKLE_FREQ=.01;//idk
	public void tick(){
		if(num_twinkle<max_num_twinkle&&twinkleCount==0&&Math.random()<TWINKLE_FREQ){
			twinkleDir=(int)Math.signum(Math.random()-.5);
			twinkleCount=maxTwinkle;
			num_twinkle++;
//			System.out.println("BEGIN TWINKLE");
		}
		if(twinkleCount>0){
			twinkle=Math.max(1,2*TWINKLE_MAG*twinkleDir*(maxTwinkle/2-Math.abs(maxTwinkle/2-twinkleCount))/maxTwinkle);
			twinkleCount--;
			if(twinkleCount==0){
				num_twinkle--;
			}
		}
//		System.out.println(num_twinkle);
//		x-=speed;
//		if(width>square){
//			System.out.println("TWINKLE");
//		}
	}
}
