import java.awt.Color;

//mass starts at near 0 and follows a reverse parabola.  momentum is not conserved.
public class MassBomb extends PhysicsNode{
	PhysicsWorld world;

	public int time;

	public MassBomb	(PhysicsShip ship, Color color){
		super (ship.rotation, ship.heading, ship.velocity.duplicate(), ship.position.duplicate(), 1, 0);
		double cosHead = Math.cos(ship.heading);
		double sinHead = Math.sin(ship.heading);
		position.vectorAdd(new Vector(cosHead*ship.radius*2, sinHead*ship.radius*2));
		velocity.vectorAdd(new Vector(cosHead*ship.gunVelocity*.25, sinHead*ship.gunVelocity*.25));
		rotation = ship.rotation;
		heading = ship.heading;
		charge = ship.charge;
		world = ship.world;
		if (color == null){
			this.color = world.randomColor();
		}
		else this.color = color;
	}

	public MassBomb (double rotation, double heading, Vector velocity, Vector position, double charge, PhysicsWorld world, Color color){
		super(rotation, heading, velocity, position, .1, charge);
		this.world = world;
		this.color = color;
	}
	public void update(){
		super.update();
		time++;
		double newMass = time * 20 - .1 * time * time;
		if (newMass <= 0)
			world.removeObject(this);
		else setMass(newMass);
	}
	public void collide(double impulse){
		world.removeObject(this);
	}
}