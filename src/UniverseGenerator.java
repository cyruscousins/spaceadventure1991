import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class UniverseGenerator {
	public static String[] starPrefixes = new String[]
	    {
		"α", "β", "γ", "δ", "ο", "θ", "π", "ρ"
	    };
	public static String[] starNames = new String[]
		{
			"Sol", "Centauri", "Pegasus", "Proximus", "16:10", "3000", "Orion", "Lucie"
		};
	public static byte[][] starCount = new byte[starPrefixes.length][starNames.length];
	
	public static String[] planetNames = new String[]
	    {
			"Barrass", "Meehan", "Katz", "Saeger", "Dylan", "Hemingway", "Jessie"
	    };

	public static byte[] planetCount = new byte[planetNames.length];
	
	public static String[] moonNames = new String[]
		{
			"Luna", "Jyotsna", "Celeste", "Radiance", "Lumina"
		};	
	
	public static byte[] moonCount = new byte[moonNames.length];
	
	public static Random rand = new Random();

	public static String romanNumeral(int i){
		String ret = "";
		while(i > 1000){
			ret += "M";
			i -= 1000;
		}
		while(i > 500){
			ret += "D";
			i-=500;
		}
		while(i > 100){
			ret += "C";
			i-=100;
		}
		while(i > 50){
			ret += "L";
			i-=50;
		}
		while(i > 10){
			ret += "X";
			i-=10;
		}
		while(i > 5){
			ret += "V";
			i-=5;
		}
		while(i > 1){
			ret += "I";
			i-=1;
		}
		return ret;
	}

	public static String getStarName(){
		int prefix = rand.nextInt(starPrefixes.length);
		int name = rand.nextInt(starNames.length);
		starCount[prefix][name]++;
		return starPrefixes[prefix] + " " + starNames[name] + " " + starCount[prefix][name];
	}
	public static String getPlanetName(){
		int name = rand.nextInt(planetNames.length);
		planetCount[name]++;
		return planetNames[name] + planetCount[name];
	}
	public static String getPlanetName(String starName){
		int name = rand.nextInt(planetNames.length);
		planetCount[name]++;
		return planetNames[name] + planetCount[name] + " (" + starName + ")";
	}
	public static	String getMoonName(){
		int name = rand.nextInt(moonNames.length);
		moonCount[name]++;
		return moonNames[name] + " " + romanNumeral(moonCount[name] + 1);
	}

	public static String getAsteroidName(){
		String name = "";
		for(int i = 0; i < 1 + rand.nextInt(3); i++){
			name += 'A' + rand.nextInt('Z' - 'A' + 1);
		}
		name += rand.nextInt(9999);
		return name;
	}
	//Galaxy Creation Methods
	public List<PhysicsNode> generateGalaxy(){
		List<PhysicsNode> galaxy = new ArrayList<PhysicsNode>();
		return galaxy;
	}
	
	public static final double MASS_STAR = .75f;
	public static final double MASS_PLANETS = .2f;
	public static final double MASS_ASTEROIDS = .05f;
	
	/* In the Sol system, mass distribution is as follows:
	 * Sun: 99.85%
	 * Planets: 0.135%
	 * Comets: 0.01% ?
	 * Satellites: 0.00005%
	 * Minor Planets: 0.0000002% ?
	 * Meteoroids: 0.0000001% ?
	 * Interplanetary Medium: 0.0000001% ?
	 */
	
	public static List<PhysicsNode> generateUniverse(PhysicsWorld world, double mass, double radius){
		List<PhysicsNode> universe = new ArrayList<PhysicsNode>();

		//use a series to calculate the masses of the system, the remainder added to the inner system.
		
		double base = .75;
		double constant = mass * (1 - base);
		
		int systemCount = 2;
		double[] masses = new double[systemCount];
		double massCount = 0;
		for(int i = 1; i < masses.length; i++){
			masses[i] = constant * Math.pow(base, i);
			massCount += masses[i];
		}
		masses[0] = mass - massCount;
//		//total sum of series minus the sum of the used part of the series.  
//		//partial series (to n) = a1 (1-rn) / (1-r).
//		masses[0] += 1 / (1 - base) - masses[0] * (1 - base * systemCount) / (1 - base);
		
		//center system.
		Vector center = new Vector();//Origin
		List<PhysicsNode> centerSystem = generateSolarSystem(world, center, masses[0], radius * .3, Star.BLACK_HOLE);
		universe.addAll(centerSystem);
		
		for(int i = 1; i < systemCount; i++){
			double θ = SpaceMath.randomθ();
			double sysRad = .2f * radius;
			double sysDisp = (.2 + .5 * Math.random()) * radius;
			Vector position = SpaceMath.polarToCartesian(θ, sysDisp);
			List<PhysicsNode> system = generateSolarSystem(world, position, masses[i], sysRad, Star.RED_GIANT);
			
			//transform the velocity so the system orbits the main system.
			gallileanVelocityTransform(system, SpaceMath.getOrbitalVector(world.G, masses[0], position, new Vector()));
			universe.addAll(system);
		};
		return universe;
	}

	
	public static List<PhysicsNode> generateSolarSystem(PhysicsWorld world, Vector origin, double totalMass, double radius, int starType){
		double massLeft = totalMass;
		
		List<PhysicsNode> solarSystem = new ArrayList<PhysicsNode>();
		
		double mass = totalMass * MASS_STAR;
		CelestialBody sun = new Star(world, UniverseGenerator.getStarName(), 0, 0, new Vector(), origin, mass * MASS_STAR, SpaceMath.getRadius(mass, 100), Double.MAX_VALUE, rand.nextInt(3));//this is the sun
		sun.color = rand.nextInt(0xffffff + 1);
		
		solarSystem.add(sun);
		
		massLeft -= mass;
		
		Random rand = new Random();
	
		
		//use an exponentially decaying series (3/4) ^ x.
		//we need a constant to multiply the series by.
		double base = 2.0 / 3;
		double asteroidMass = MASS_ASTEROIDS * totalMass;
		//use convergence of exponential series to obtain a constant to multiply the asteroid mass series by.
		//double asteroidConstant = asteroidMass / (1 / (1 - base));
		double asteroidConstant = asteroidMass * (1 - base);
		
		
		int count = 0;
		
		do { //asteroid belt
			int c = rand.nextInt(0xffffff + 1);
			mass = asteroidConstant * Math.pow(base, count);
			count ++;
			solarSystem.add(CelestialBody.getSatelite(world, sun, getAsteroidName() + "(" + sun.name + " System)", c, radius * .4f + rand.nextDouble() * radius * .2f, rand.nextDouble() * Math.PI * 2, mass, SpaceMath.getRadius(mass, 9))); //asteroids
			massLeft -= mass;
		}
		while (mass > 10);
		System.out.println(sun.name + " System Contains " + count + " asteroids");
		
		

		//planets an exponentially decaying series (3/4) ^ x.
		base = .75;
		double planetMass = MASS_PLANETS * totalMass;
		double planetConstant = planetMass * (1 - base); //Because convergent series are cool.
		
		count = 0;
		int c = 0; //color.
		
		do { //planets with satelites.
			double distance = radius / (count + 1);
			mass = planetConstant * Math.pow(base, count);
			double planetMassAllocation = .8 * mass; //the mass of the planet.
			double sateliteMassAllocation = .2 * mass; //the mass of the satelites.
			count ++;
			
			double θ = SpaceMath.randomθ();
			double r1 = SpaceMath.getRadius(planetMassAllocation, 10);
			CelestialBody planet = CelestialBody.getSatelite(sun, SpaceMath.randomColor(), distance, θ, planetMassAllocation, r1);
			planet.name = UniverseGenerator.getPlanetName(sun.name);
			solarSystem.add(planet);
			massLeft -= mass;
			

			double sateliteMass = 0;
			double sateliteBase = 4.0 / 5;
			double sateliteConstant = sateliteMassAllocation * (1 - sateliteBase);
			
			int sateliteCount = 0;
			do { //asteroid belt
				c = rand.nextInt(0xffffff + 1);
				mass = sateliteConstant * Math.pow(sateliteBase, sateliteCount);
				sateliteCount ++;
				solarSystem.add(CelestialBody.getSatelite(world, sun, getMoonName() + "(" + planet.name + " Satelite" + ")", c, radius * .4f + rand.nextDouble() * radius * .2f, SpaceMath.randomθ(), mass, SpaceMath.getRadius(mass, 9))); //asteroids
				massLeft -= mass;
			}
			while (sateliteMass > 10);
		}
		while (mass > 100);


		System.out.println("System " + sun.name + " has " + planetCount + " satelite planets");
		
		//incorporate any leftover mass into randomly moving asteroids, the tiny remainder being added to the star.
		
		do { 
			c = rand.nextInt(0xffffff + 1);
			mass = rand.nextDouble() * massLeft;
			float θ = (float)SpaceMath.randomθ();
			double r1 = .1f * radius + .9f * rand.nextDouble() * radius;
			solarSystem.add(new CelestialBody(world, "Unidentified Asteroid (" + sun.name + " System)", c, 0, 0, new Vector(rand.nextDouble()*10-5, rand.nextDouble()*10-5), new Vector(SpaceMath.fastCosine(θ) * r1, SpaceMath.fastSine(θ) * r1), mass, SpaceMath.getRadius(mass, 7.5), 10 + mass));//asteroids
			massLeft -= mass;
		}
		while (massLeft > 10); //random celestial bodies
		
		sun.mass += massLeft; //add remaining mass to the sun.  Should negligibly affect gravitational physics for nonminiscule systems.
		
		
		return solarSystem;
	}
	
	public static Color randomColor(){
		return new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
	}

	
	public static double cumMass(List<PhysicsNode> system){
		double sum = 0;
		for(PhysicsNode node : system){
			sum += node.mass;
		}
		return sum;
	}
	
	public static Vector center(List<PhysicsNode> system){
		double mass = 0;
		double x = 0;
		double y = 0;
		for(PhysicsNode node : system){
			mass += node.mass;
			x += node.position.x;
			y += node.position.y;
		}
		return new Vector(x / mass, y / mass);
	}
	
	public static Vector velocity(List<PhysicsNode> system){
		double mass = 0;
		double x = 0;
		double y = 0;
		for(PhysicsNode node : system){
			mass += node.mass;
			x += node.velocity.x;
			y += node.velocity.y;
		}
		return new Vector(x / mass, y / mass);
	}
	
	/*
	 * Performs a gallilean transformation on the velocity of a system.
	 */
	public static void gallileanVelocityTransform(List<PhysicsNode> system, Vector transform){
		for(PhysicsNode node : system){
			node.velocity.vectorAdd(transform);
		}
	}
	
	/*
	 * Orbits s0 around s1, assuming ms0 << ms1
	 */
	public static void solarSystemOrbit(List<PhysicsNode> s0, List<PhysicsNode> s1){
		double m0 = cumMass(s0);
		double m1 = cumMass(s1);
		
		Vector p0 = center(s0);
		Vector p1 = center(s1);

		
		double distance = p0.distance(p1);
		double θ = Math.atan2(p1.y - p0.y, p1.x - p1.y);
		
		//a = v*v / r, F = ma, F = Gm1m2 / (r * r)
		
		//so, v = 
		
		double vTransform = Math.sqrt(PhysicsWorld.G * m1 / distance);

		Vector trans = new Vector(Math.cos(θ) * vTransform, Math.sin(θ) * vTransform);
		trans.vectorAdd(velocity(s1));
		
		gallileanVelocityTransform(s0, trans);
		//a = vsquared / r.
	}
}
