
public class Vector{
	public double x;
	public double y;

	//initializes a vector with no magnitude or heading.
	public Vector(){ }

	public Vector(double x, double y){
		this.x = x;
		this.y = y;
	}

	public static Vector vectorFromMH(double magnitude, double heading){
		double x = Math.cos(heading) * magnitude;
		double y = Math.sin(heading) * magnitude;
		return new Vector(x, y);
	}

	public Vector duplicate(){
		return new Vector(x, y);
	}

	public double getHeading(){
		return Math.atan2(y, x);  //this is a good method
	}

	public double getMagnitude(){
		return Math.hypot(x, y);
	}

	public void vectorAdd(Vector v){
		x += v.x;
		y += v.y;
	}
	public void vectorSubtract(Vector v){
		x -= v.x;
		y -= v.y;
	}
	public Vector duplicateVvectorAdd(Vector v){
		return new Vector(x + v.x, y + v.y);
	}
	public Vector duplicateVectorSubtract(Vector v){
		return new Vector(x - v.x, y - v.y);
	}
	public void scalar(double scalar){
		x *= scalar;
		y *= scalar;
	}
	public Vector duplicateScalar(double scalar){
		return new Vector(x * scalar, y * scalar);
	}
	public double distance(Vector v){
		double dx = x - v.x;
		double dy = y - v.y;
		return Math.sqrt(dx * dx + dy * dy);
	}
	public void zero(){
		x = y = 0;
	}
}