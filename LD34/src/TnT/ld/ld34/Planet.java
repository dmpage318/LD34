package TnT.ld.ld34;

public class Planet extends Body {

	public Planet(double x, double y, double radius, double omega) {
		this.x = x;
		this.y = y;
		this.hitRadius = radius;
		this.omega = omega;
		mass = 10;
	}

	public boolean canLand() {
		return true;
	}

	public double getLocalGravRadius() {
		return 0;
	}

	public double getLocalGravAccel() {
		return 0;
	}

	public boolean canKill() {
		return false;
	}
	
	public Planet clone() {
		return new Planet(x, y, hitRadius, omega);
	}
}
