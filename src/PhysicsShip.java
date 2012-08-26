import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class PhysicsShip extends PhysicsNode {
	public double enginePower ;
	public double gunVelocity;
	public double gunPower;

	public double energy = 100;
	public double maxEnergy = 100;

	public double gunCoolDownTimer;

	public double maxHealth;
	public double health;

	public double rotationSpeed;
	
	public int screenX;
	public int screenY;

	public VectorGraphic sprite;

	public static final int LEFT = 0, RIGHT = 1, UP = 2, FIRE = 3, STABILIZE = 4;
	public int[] controls = new int[]{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_SPACE, KeyEvent.VK_ALT};
	public static final int[][] possibleControls = new int[][]{
		{KeyEvent.VK_NUMPAD4, KeyEvent.VK_NUMPAD6, KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD2, KeyEvent.VK_NUMPAD3},
		{KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_X, KeyEvent.VK_Z},
		{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_SPACE, KeyEvent.VK_SHIFT}
	};
	public PhysicsShip(String name, int color, double rotation, double heading, Vector velocity, Vector position, double mass, int radius, PhysicsWorld world, double enginePower, double health, double gunPower){
		super(world, name, color, rotation, heading, velocity, position, mass, radius);
		this.enginePower = enginePower;
		this.gunPower = gunPower;
		this.gunVelocity = Math.max(50, 250-gunPower);
		this.maxHealth = this.health = health;
		rotationSpeed = 2000 / mass;
		sprite = VectorGraphic.getShip(color);
	}

	public void setControls(int[] controls){
		this.controls = controls;
	}

	public void setControls(int number){
		this.controls = possibleControls[number];
	}

	public void update(double dt){
		if (world.keys[controls[LEFT]]){
			rotation += rotationSpeed * dt;
		}
		if (world.keys[controls[RIGHT]]){
			rotation -= rotationSpeed * dt;
		}
		if (world.keys[controls[UP]]){
			applyForce(Vector.vectorFromMH(enginePower / mass * dt, heading));
		}
		if (world.keys[controls[FIRE]]){
			if (gunCoolDownTimer <= 0 && energy>=10){
				gunCoolDownTimer = .1;
				energy -= 5;
				double cosHead = Math.cos(heading);
				double sinHead = Math.sin(heading);
				Vector bulletPosition = position.duplicate();
				bulletPosition.vectorAdd(new Vector(cosHead*radius*2, sinHead*radius*2));
				Vector bulletVelocity = velocity.duplicate();
				bulletVelocity.vectorAdd(new Vector(cosHead*gunVelocity, sinHead*gunVelocity));
				PhysicsNode bullet = new PhysicsNode(world, name + " projectile", SpaceMath.randomColor(), rotation, heading, bulletVelocity, bulletPosition, gunPower, 1);
				world.addObject(bullet);
			}
		}
		else if (world.keys[controls[STABILIZE]]){
			rotation *= Math.pow(.25, dt);
		}
		if (gunCoolDownTimer > 0) gunCoolDownTimer -= dt;
		if (energy < maxEnergy) energy += 5 * dt;
		if (health < maxHealth) health += 1 * dt;
		super.update(dt);
	}

	public void collide(double impulse){
		health-=impulse;
		if(health <=0){
			world.removeObject(this);
		}
	}


	/*public void render(Graphics g, double camX, double camY, double zoom){

		g.setColor(color);
		g.fillOval(x - radius, y - radius, radius * 2 + 1, radius * 2 + 1);
	}*/

	public void render(BufferedImage image, double camX, double camY, double zoom){

		Graphics g = image.getGraphics();
		screenX = (int)((position.x - camX) * zoom) + world.frame.fieldWidth / 2;
		screenY = (int)((position.y - camY) * zoom) + world.frame.fieldHeight / 2;
		int radius = (int)(this.radius * zoom);
		int deltaX = (int)(Math.cos(heading) * radius);
		int deltaY = (int)(Math.sin(heading) * radius);
		g.setColor(new Color(color));
		g.drawLine(screenX - deltaX, screenY - deltaY, screenX + deltaX, screenY + deltaY);
		g.fillOval(screenX-deltaX - 2, screenY - deltaY -2, 4, 4); //tail.
		//sprite.render(g, this, camX, camY, zoom);
		
		//ship info
		g.setColor(Color.GREEN);
		g.drawOval(screenX - radius, screenY - radius, radius * 2 + 1, radius * 2 + 1); //shield
		g.drawString("X: " + world.df.format(position.x), screenX, screenY + 10);
		g.drawString("Y: " + world.df.format(position.y), screenX, screenY + 20);
		
		g.drawString("dx/dt: " + world.sensitiveDF.format(velocity.x), screenX, screenY + 32);
		g.drawString("dy/dt: " + world.sensitiveDF.format(velocity.y), screenX, screenY + 42);
		
		g.drawString("d2x/dt2: " + world.sensitiveDF.format(acceleration.x), screenX, screenY + 54);
		g.drawString("d2y/dt2: " + world.sensitiveDF.format(acceleration.y), screenX, screenY + 64);
		acceleration.zero();
	}
}