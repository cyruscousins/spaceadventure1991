import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class PhysicsNode {

	public static final double ONE_THIRD = 1.0/3.0;

	double mass;
	double inverseMass;

	double radius;

	int color;


	double rotation; //rotational "speed"
	double heading;  //rotational position

	Vector velocity; //dp/dt
	Vector acceleration = new Vector();
	Vector position = new Vector();
	
	String name;

	PhysicsWorld world;
	
	public static final double FOURTHIRDSPI = 4 * Math.PI / 3;
	
	public PhysicsNode(PhysicsWorld world, String name, int color, double rotation, double heading, Vector velocity, Vector position, double mass, double radius){
		this.world = world;
		this.rotation = rotation;
		this.heading = heading;
		this.velocity = velocity;
		this.position = position;
		this.name = name;
		setMass(mass);
		this.color = color;
		this.radius = radius;
	}

	public PhysicsNode(double rotation, Vector position, double mass, double radius){
		this.position = position;
		velocity = new Vector();
		this.rotation = rotation;
		setMass(mass);
		this.radius = radius;
		color = (int)(Math.random() * 255) << 16 | (int)(Math.random() * 255) << 8 | (int)(Math.random() * 255);
	}

	public void setMass(double newMass){
		mass = newMass;
		inverseMass = 1f/mass;
	}

	public double getDensity(){
		return mass / (4 / 3 * Math.PI * radius * radius);
	}

	public void applyForce(Vector force){
		force.scalar(inverseMass);
		velocity.vectorAdd(force);
		acceleration.vectorAdd(force);
	}

	public void update(double dt){
		heading += rotation * dt;
		position.vectorAdd(velocity.duplicateScalar(dt));
	}

	public void render(BufferedImage image, double camX, double camY, double zoom){
		int x0 = (int)((position.x - camX) * zoom) + world.frame.fieldWidth / 2;
		int y0 = (int)((position.y - camY) * zoom) + world.frame.fieldHeight / 2;
		int radius = (int)(this.radius * zoom);
		int radSqr = radius * radius;
		
		int iWidth = image.getWidth();
		int iHeight = image.getHeight();
		//no need to render things off camera.
		if (x0 + radius < 0 || x0 - radius > iWidth || y0 + radius < 0 || y0 - radius > iHeight) return;
		
//		int rSqr = radius * radius;
//			
//		for(int x = -radius; x <= radius; x++){
//			for(int y = - radius; y <= radius; y++){
//
//				int x1 = x0 + x;
//				int y1 = y0 + y;
//				if (x1 > 0 && x1 < iWidth && y1 > 0 && y1 < iHeight
//						&& x * x + y * y < rSqr)
//					image.setRGB(x1, y1, color);
//			}
//		}
			
			
			
			
		//fast circle drawing routine.  More efficient for larger circles.  
			
			
		for(int x = -radius; x <= radius; x++){
			int yh = SpaceMath.intSqrt(radSqr - x * x);
			for(int y = -yh; y <= yh; y++){
				int x1 = x0 + x;
				int y1 = y0 + y;
				if (x1 > 0 && x1 < iWidth && y1 > 0 && y1 < iHeight)
					image.setRGB(x1, y1, color);
			}
		}
	}

	public void collide(double impulse){

	}
}