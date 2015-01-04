import java.util.ArrayList;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

public class PhysicsWorld implements KeyListener{

	PhysicsFrame frame;

	int halfFieldWidth, halfFieldHeight;
	
	public boolean[] keys = new boolean[512];

	ArrayList<PhysicsNode> objects = new ArrayList<PhysicsNode>();
	ArrayList<Star> stars = new ArrayList<Star>();
	
	ArrayList<PhysicsNode> objectsToAdd = new ArrayList<PhysicsNode>();
	ArrayList<PhysicsNode> trash = new ArrayList<PhysicsNode>();

	
	//speed control
	int targetFPS;
	int mspt; //milliseconds per tick
	public void setTargetFPS(int fps){
		targetFPS = fps;
		mspt = 1000 / fps;
	}
	
	
	int shipCount = 2;
	ArrayList<PhysicsShip> ships = new ArrayList<PhysicsShip>();

	double camX;
	double camY;
	double zoom = .8;
	
	//graphics stuff
	
	public static VectorGraphic arrow = VectorGraphic.getArrow(0x00ff00);

	public PhysicsWorld(PhysicsFrame frame){
		this.frame = frame;
		frame.addKeyListener(this);
		display = new GradientPaint(50, 50, new Color(100, 100, 100, 50), frame.fieldWidth-50, frame.fieldHeight-50, new Color(0, 0, 0, 50));
		setTargetFPS(50);
		halfFieldWidth = frame.fieldWidth / 2;
		halfFieldHeight = frame.fieldHeight / 2;
	}
	public void init(){
		
		objects.addAll(UniverseGenerator.generateUniverse(this, 100000000, 10000));
		
		//objects.addAll(UniverseGenerator.generateSolarSystem(this, new Vector(), 1000000, 1000, 1));
		for(PhysicsNode object : objects){
			if (object instanceof Star){
				stars.add((Star)object);
			}
		}
		System.out.println("Current Simulation includes" + objects.size() + "particles.");
		System.out.println("Σmass = " + UniverseGenerator.cumMass(objects));
		
		initShips(shipCount);

	}

	public Color randomColor(){
		return new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
	}

	public void initShips(int shipCount){
		double radianStep = Math.PI * 2 / shipCount;
		Vector shipOrigin = new Vector(-500, -500);
		for(int i = 0; i < shipCount; i++){
			Vector position = Vector.vectorFromMH(50, i * radianStep);
			position.vectorAdd(shipOrigin);
			//Customization
			String[] names = new String[]{"Red", "Green", "Blue", "Thrusters", "Shields", "Weapon"};
			int stats[] = new int[]{255, 255, 255, 100, 100, 100};
			int index = 0;
			int statsMax = 500;
			while(!keys[KeyEvent.VK_ENTER]){
				if (keys[KeyEvent.VK_DOWN]){
					index++;
					keys[KeyEvent.VK_DOWN] = false;
					if (index >= stats.length)
						index = 0;
				}
				if (keys[KeyEvent.VK_UP]){
					index--;
					keys[KeyEvent.VK_UP] = false;
					if (index < 0)
						index = stats.length - 1;
				}
				if (keys[KeyEvent.VK_LEFT]){
					stats[index]-=5;
					if (stats[index] < 0) stats[index] = 0;
					keys[KeyEvent.VK_LEFT] = false;
				}
				if (keys[KeyEvent.VK_RIGHT]){
					stats[index]+=5;
					if (index < 3){ //color
						if (stats[index] > 255) stats[index] = 255;
					}
					else //spaceship stats
						if (stats[3] + stats[4] + stats[5] > 500)
							stats[index]-=5;
					keys[KeyEvent.VK_RIGHT] = false;
				}
				//render spaceship stats
				Graphics2D g = (Graphics2D) frame.g;
				//g.setPaint(display);
				g.setColor(Color.BLACK);

				g.fillRoundRect(100, 100, frame.fieldWidth-200, frame.fieldHeight-200, 50, 50);
				g.setColor(Color.WHITE);
				g.drawRoundRect(100, 100, frame.fieldWidth-200, frame.fieldHeight-200, 50, 50);

				g.drawString("Ship " + (i + 1) + ":", 150, 150);
				for(int j = 0; j < stats.length; j++){
					g.drawString(names[j] + ": " + stats[j], 150, 170 + 20 * j);
					g.fillRect(250, 160 + 20 * j, stats[j], 10);
					g.draw3DRect(250, 160 + 20 * j, stats[j], 10, index == j);
				}

				frame.renderToScreen();
				g.setColor(new Color(0, 0, 0, 20));
				g.fillRect(0, 0, frame.fieldWidth, frame.fieldHeight);
				try{
					Thread.sleep(1);
				}
				catch (Exception e){

				}
			}
			int color = stats[2] << 16 | stats[1] << 8 | stats[0];
			//Color color = new Color(stats[0], stats[1], stats[2]);
			PhysicsShip ship = new PhysicsShip("Star Ship α" + i, color, 0, Math.PI/2, new Vector(), position, 1000 + stats[4], 10, this, (10 + stats[3]) * 200000, stats[4] + 100, (10 + stats[5]) * 10);
			ship.setControls(i);
			ships.add(ship);
			keys[KeyEvent.VK_ENTER] = false;
		}
		objects.addAll(ships);
	}

	public static final double G = .01, CC = .00005;

	public DecimalFormat df = new DecimalFormat("0000.000");
	public DecimalFormat sensitiveDF = new DecimalFormat("00.000000");
	public GradientPaint display;
	public void run(){
		long oldTime;
		while (!keys[KeyEvent.VK_ESCAPE]){
			oldTime = System.currentTimeMillis();
			
			double dt = mspt / 1000.0; //assuming a fast enough CPU.  Should be calculated based on previous frame performance.
			
			
			//the main code goes here

			/*for(PhysicsNode obj1 : objects) {
				//System.out.println("X: " + obj1.position.x + ", Y: " + obj1.position.y);
				for(PhysicsNode obj2 : objects){
					if (obj1 == obj2) continue;
						double distance = Math.hypot(obj1.position.x - obj2.position.x, obj1.position.y - obj2.position.y);
					if (distance < 1) continue; //gravity gets silly at close distances.
					//System.out.println("Gravitation is occuring");
					double magnitude = G * obj1.mass * obj2.mass /
						(distance * distance);
					double heading = Math.atan2(obj1.position.y - obj2.position.y, obj1.position.x - obj2.position.x);
					System.out.println(magnitude);
					obj2.applyForce(Vector.vectorFromMH(magnitude, heading));
				}
				//object.applyForce(new Vector(0, -.1f));
				obj1.update();
				obj1.render(frame.g, camX, camY, camZoom)
				//obj1.render(frame.g);
			}*/
			if (keys[KeyEvent.VK_EQUALS]){ //zoom in key
				zoom *= Math.pow(2, dt);
			}
			else if (keys[KeyEvent.VK_MINUS]){ //zoom out key
				zoom *= Math.pow(.5, dt);
			}
			//update camera logic
			camX = 0;
			camY = 0;
			for(PhysicsShip ship: ships){
				camX += ship.position.x;
				camY += ship.position.y;
			}
			camX /= ships.size();
			camY /= ships.size();

			//camX -= .5 * frame.fieldWidth;
			//camY -= .5 * frame.fieldHeight;

			//System.out.println("Camera X: " + camX + ", Camera Y: " + camY);

			if (keys[KeyEvent.VK_SHIFT]){ //ship display key
				for(PhysicsNode object: objects) { //render object
					object.render(frame.buffer, camX, camY, zoom);
				}
				//render spaceship stats
				Graphics2D g = (Graphics2D) frame.g;
				g.setPaint(display);
				g.fillRoundRect(100, 100, frame.fieldWidth-200, frame.fieldHeight-200, 50, 50);
				g.setColor(Color.WHITE);
				g.drawRoundRect(100, 100, frame.fieldWidth-200, frame.fieldHeight-200, 50, 50);
				for(int i = 0; i < ships.size(); i++){
					g.drawString("Ship " + (i + 1) + " Hull: " + ships.get(i).health +", Energy: " + ships.get(i).energy, 150, 150 + 50 * i);
					g.drawString("X " + (ships.get(i).position.x) + ", Y: " + ships.get(i).position.y, 150, 170 + 50 * i);
				}
			}
			else if (keys[KeyEvent.VK_CONTROL]) //universe simulation display key
			{
				for(PhysicsNode object: objects) { //render object
					object.render(frame.buffer, camX, camY, zoom);
				}
				//render spaceship stats
				Graphics2D g = (Graphics2D) frame.g;
				g.setPaint(display);
				g.fillRoundRect(100, 100, frame.fieldWidth-200, frame.fieldHeight-200, 50, 50);
				g.setColor(Color.WHITE);
				g.drawRoundRect(100, 100, frame.fieldWidth-200, frame.fieldHeight-200, 50, 50);
				
				g.drawString("Simulation contains\n" + objects.size() + " total particles, including\n" + 
						shipCount + " player controlled ships,\n" + stars.size() + " Star Systems.", 150, 170);
				
				//star map
				
				double starMapZoom = .01 * zoom;
				int mapx0 = 100;
				int mapWidth = frame.fieldWidth - 200;
				int mapy0 = 100;
				int mapHeight = frame.fieldHeight - 200;
				int mapxOrigin = frame.fieldWidth / 2;
				int mapyOrigin = frame.fieldHeight / 2;
				for(PhysicsShip ship : ships){
					int x = (int) (mapxOrigin + starMapZoom * (ship.position.x - camX));
					int y = (int) (mapyOrigin + starMapZoom * (ship.position.y - camY));
					if (x > mapx0 && x < mapx0 + mapWidth && y > mapy0 && y < mapy0 + mapHeight){
						frame.buffer.setRGB(x, y, ship.color);
						g.drawString(ship.name, x, y);
					}
				}
				for(Star star : stars){
					int x = (int) (mapxOrigin + starMapZoom * (star.position.x - camX));
					int y = (int) (mapyOrigin + starMapZoom * (star.position.y - camY));
					if (x > mapx0 && x < mapx0 + mapWidth && y > mapy0 && y < mapy0 + mapHeight){
						frame.buffer.setRGB(x, y, star.color);
						g.drawString(star.name, x, y);
					}
				}
			}
			else{ //main game loop.

			for(int i = 0; i < objects.size() - 1; i ++){
				for(int j = i + 1; j < objects.size(); j++){
					
					PhysicsNode obj1 = objects.get(i);
					PhysicsNode obj2 = objects.get(j);
					double distance = Math.hypot(obj1.position.x - obj2.position.x, obj1.position.y - obj2.position.y);
					double heading = Math.atan2(obj1.position.y - obj2.position.y, obj1.position.x - obj2.position.x);

					double radialSum = obj1.radius + obj2.radius + 1;//sum of radii plus one to reduce passthrough.
					if (distance < 1){ //the objects are inside of each other.  do nothing, this is bad.
					}
					else if (distance < radialSum){ //ignore gravity, instead use collision physics.
						PhysicsNode larger = null;
						PhysicsNode smaller = null;
						if (obj1.mass > obj2.mass){
							larger = obj1;
							smaller = obj2;
						}
						else{
							larger = obj2;
							smaller = obj1;
						}
						if ((!(smaller instanceof PhysicsShip || larger instanceof PhysicsShip)) && Math.abs(larger.mass/smaller.mass) > 2){ //inelastic collision

							double momentumX = smaller.velocity.x * smaller.mass + larger.velocity.x * larger.mass;
							double momentumY = smaller.velocity.y * smaller.mass + larger.velocity.y * larger.mass;
							
							double newVolume = SpaceMath.sphereVolume(larger.radius) + SpaceMath.sphereVolume(smaller.radius); 
							
							larger.setMass(larger.mass + smaller.mass);

							larger.velocity.x = (momentumX * larger.inverseMass);
							larger.velocity.y = (momentumY * larger.inverseMass);

							removeObject(smaller);
							System.out.println(smaller.name + " was assimilated into " + larger.name);
							
							larger.radius = SpaceMath.sphereRadius(newVolume);
							
							//double magnitude = smaller.velocity.getMagnitude();
							
							//larger.collide(calculateKE(smaller.mass, larger.velocity.getMagnitude() - smaller.velocity.getMagnitude();//deal damage equal to KE
						}
						else{ //semielastic collision
						double massProduct = obj1.mass * obj2.mass;
						double magnitude = massProduct * CC * radialSum / distance;
						Vector force1 = Vector.vectorFromMH(magnitude * dt, heading);
						Vector force2 = force1.duplicate();
						force2.scalar(-1);
						obj1.applyForce(force1);
						obj2.applyForce(force2);
						obj1.collide(magnitude * 2);
						obj2.collide(magnitude * 2);
						//System.out.println("Collision");
						}
					}
					else{ //gravity

						double magnitude = G * obj1.mass * obj2.mass /
							(distance * distance);

						//System.out.println(magnitude);

						Vector force = Vector.vectorFromMH(magnitude * dt, heading);
						obj2.applyForce(force);
						force.scalar(-1);
						obj1.applyForce(force);
					}
				}
			}
			Vector cam = new Vector(camX, camY);
			for(PhysicsNode object: objects) {
				object.update(dt);
				
				//test for in bounds of the screen.
				Vector cDisplacement = object.position.duplicateVectorSubtract(cam);
				if (true && cDisplacement.x + object.radius > - halfFieldWidth && cDisplacement.x - object.radius  < halfFieldWidth &&
					cDisplacement.y + object.radius > - halfFieldHeight && cDisplacement.y - object.radius < halfFieldHeight)
					object.render(frame.buffer, camX, camY, zoom);
				else{
					double massOverDistance = object.mass / cDisplacement.getMagnitude();
					if (massOverDistance > 10){
						float θ = SpaceMath.fastatan2((float)cDisplacement.x, (float) cDisplacement.y);
						//arrow.render(frame.g, arrowControl, -halfFieldHeight * SpaceMath.fastCosine(θ), -halfFieldHeight * SpaceMath.fastSine(θ), dt);
						//arrow.render(frame.g, halfFieldWidth + halfFieldHeight * SpaceMath.fastCosine(θ) * .95, halfFieldHeight - halfFieldHeight * SpaceMath.fastSine(θ) * .95, massOverDistance * .1 + 1, θ);
						//frame.g.setColor(Color.green);
						//frame.g.drawString(object.name, halfFieldHeight + (int)(-.9 * halfFieldHeight * SpaceMath.fastCosine(θ)), halfFieldHeight + (int)(-.9 * halfFieldHeight * SpaceMath.fastSine(θ)));
					}
				}
			}
			objects.removeAll(trash);
			ships.removeAll(trash);
			stars.removeAll(trash);
			trash.clear();

			for(PhysicsNode object: objectsToAdd) {
				objects.add(object);
				if (object instanceof Star){
					stars.add((Star)object);
				}
			}
			objectsToAdd.clear();
			}


			//render health and energy bars, as well as ship information
			int barLength = 800;//frame.fieldWith-100;
			Graphics g = frame.g;
			for(int i = 0; i < ships.size(); i++){

				PhysicsShip ship = ships.get(i);

				int shipHealth = (int)(ship.health/ship.maxHealth * barLength);
				int shipEnergy = (int)(ship.energy/ship.maxEnergy * barLength);

				g.setColor(Color.RED);
				g.fillRect(50, 20 + 20*i, shipHealth, 8);

				g.setColor(Color.YELLOW);
				g.fillRect(50, 30 + 20*i, shipEnergy, 8);

				g.setColor(Color.DARK_GRAY);
				g.draw3DRect(50, 20 + 20*i, barLength, 8, false);

				g.setColor(Color.DARK_GRAY);
				g.draw3DRect(50, 30 + 20*i, barLength, 8, false);
			}

			frame.renderToScreen();
			g.setColor(new Color(0, 0, 0));
			g.fillRect(0, 0, frame.fieldWidth, frame.fieldHeight);

			//sleeping code
			long newTime = System.currentTimeMillis();
			int timePassed = (int)(newTime - oldTime);
			try{
				//System.out.println("Sleeping for " + sleepTime + " milliseconds.");
				Thread.sleep(Math.max(2, mspt - newTime + oldTime));
			}
			catch (Exception e){
			}
		}
		System.exit(0);
	}
	public double calculateKE(double mass, double velocity){
		return .5 * mass * velocity * velocity;
	}
	public void addObject(PhysicsNode node){
		objectsToAdd.add(node);
	}
	public void removeObject(PhysicsNode node){
		trash.add(node);
	}
	public void keyPressed(KeyEvent e){
		keys[e.getKeyCode()] = true;
	}
	public void keyReleased(KeyEvent e){
		keys[e.getKeyCode()] = false;
	}
	public void keyTyped(KeyEvent e){
	}
}
