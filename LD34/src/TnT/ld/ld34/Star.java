package TnT.ld.ld34;

public class Star extends Body {
	
	public Star(double x, double y, double radius) {
		this.x = x;
		this.y = y;
		hitRadius = radius;
		
	}
	
	public boolean canLand() {
		return false;
	}

	public boolean canKill() {
		return true;
	}

	public Body clone() {
		return new Star(x, y, hitRadius);
	}

}
