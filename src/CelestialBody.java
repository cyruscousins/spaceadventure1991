import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class CelestialBody extends PhysicsNode{

	public double durability;
	public double maxDurability;
	
	public String name;
	
	public CelestialBody(PhysicsWorld world, String name, int color, double rotation, double heading, Vector velocity, Vector position, double mass, double radius, double durability){
		super(world, name, color, rotation, heading, velocity, position, mass, radius);
		this.durability = maxDurability = durability;
		this.name = name;
	}
	public void explode(double KE){
		world.removeObject(this);
		int fragSum = (int)Math.sqrt(mass/10);
		CelestialBody[] fragments = new CelestialBody[fragSum];
		
		String newName = this.name;
		if (!name.endsWith("Debris")) name += " Debris";
		
		//simplified calculation involving volume of spheres.
		// V = 4 / 3 Pi r ^ 3.
		
		double newRadius = Math.cbrt(radius * radius * radius / fragments.length);
		
		for(int i = 0; i < fragments.length; i++){
			Vector velocity = this.velocity.duplicate();
			Vector position = this.position.duplicate();
			double theta = Math.PI * 2 * i / fragSum;
			double mass = this.mass / fragSum;

			double energy = KE/fragSum; //v = sqrt(2KE/m)
			double explosionVelocity = Math.sqrt(2 * energy / mass);

			velocity.vectorAdd(Vector.vectorFromMH(explosionVelocity, theta));
			position.vectorAdd(Vector.vectorFromMH(radius/fragSum, theta));
			

			fragments[i] = new CelestialBody(world, newName, color, rotation, heading, velocity, position, mass, newRadius,maxDurability/fragSum);
		}
	}
	public void update(double dt){
		super.update(dt);
		durability += mass/100 * dt; //gravity pulls the body back together
		if (durability > maxDurability) durability = maxDurability;
	}
	public void collide(double impulse){
		durability-=impulse;
		if(durability <=0){
			explode(impulse);
		}
	}
	public static CelestialBody getSatelite(CelestialBody planet, int color, double distance, double theta, double mass, double radius){
		double cosHead = Math.cos(theta);
		double sinHead = Math.sin(theta);
		Vector translation = new Vector(planet.position.x + cosHead * distance, planet.position.y + sinHead * distance);

		double speed = Math.sqrt(PhysicsWorld.G * planet.mass / distance);
		Vector velocity = new Vector(-sinHead * speed + planet.velocity.x, cosHead * speed + planet.velocity.y); //perpendicular to the location, with the planets velocity added.

		String newName = "Unidentified " + planet.name + " Satelite";
		return new CelestialBody(planet.world, newName, color, 0, 0, velocity, translation, mass, radius, 50 + mass);
	}
	public static CelestialBody getSatelite(PhysicsWorld world, PhysicsNode planet, String sateliteName, int color, double distance, double theta, double mass, double charge){
		double cosHead = Math.cos(theta);
		double sinHead = Math.sin(theta);
		Vector translation = new Vector(planet.position.x + cosHead * distance, planet.position.y + sinHead * distance);

		double speed = Math.sqrt(PhysicsWorld.G * planet.mass / distance);
		Vector velocity = new Vector(-sinHead * speed + planet.velocity.x, cosHead * speed + planet.velocity.y); //perpendicular to the location, with the planets velocity added.

		return new CelestialBody(world, sateliteName, color, 0, 0, velocity, translation, mass, charge, 50 + mass);
	}
	public void render(BufferedImage image, double camX, double camY, double zoom){
		super.render(image, camX, camY, zoom);
		if (radius * zoom > 5 || this instanceof Star){ //this object is zoomed in enough to display data.
			Graphics g = image.getGraphics();
			g.setColor(Color.GREEN);
			int x = (int)((position.x - camX) * zoom) + world.frame.fieldWidth / 2;
			int y = (int)((position.y - camY) * zoom) + world.frame.fieldHeight / 2;
			g.drawString(name, x, y + 10);
			g.drawString("Mass:  " + world.sensitiveDF.format(mass), x, y + 20);
			g.drawString("Break: " + world.sensitiveDF.format(durability/maxDurability*100), x, y + 30);
			
			//movement info
			
			g.drawString("X: " + world.df.format(position.x), x, y + 42);
			g.drawString("Y: " + world.df.format(position.y), x, y + 52);
			
			g.drawString("dx/dt: " + world.sensitiveDF.format(velocity.x), x, y + 64);
			g.drawString("dy/dt: " + world.sensitiveDF.format(velocity.y), x, y + 74);
			
			g.drawString("d2x/dt2: " + world.sensitiveDF.format(acceleration.x), x, y + 86);
			g.drawString("d2y/dt2: " + world.sensitiveDF.format(acceleration.y), x, y + 96);
			acceleration.zero();
		}
	}
}