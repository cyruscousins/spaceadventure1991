import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;


public class Star extends CelestialBody {
	
	public static final int RED_GIANT = 0, NEUTRINO = 1, BLACK_HOLE = 2;
	public int starType;
	
	
	public Star(PhysicsWorld world, String name, double rotation, double heading, Vector velocity, Vector position, double mass, double radius, double durability, int starType){
		super(world, name, 0xffffff, rotation, heading, velocity, position, mass, radius, durability);
		this.world = world;
		this.durability = maxDurability = durability;
		this.name = name;
		this.starType = starType;
	}
	
	public void render(BufferedImage image, double camX, double camY, double zoom){
		int x0 = (int)((position.x - camX) * zoom);
		int y0 = (int)((position.y - camY) * zoom);
		int radius = (int)(this.radius * zoom);
		int radSqr = radius * radius;
		
		int iWidth = image.getWidth();
		int iHeight = image.getHeight();
		//no need to render things off camera.
		//if (x0 + radius < 0 || x0 - radius > iWidth || y0 + radius < 0 || y0 - radius > iHeight) return;
		
		//gravitational light bending
		
		super.render(image,  camX, camY, zoom);
//		int bRad = radius * 2;
//		for(int x = -bRad; x <= bRad; x++){
//			for(int y = -bRad; y <= bRad; y++){
//				int d = SpaceMath.fastIntSqrt(x * x + y * y);
//				double theta = SpaceMath.fastatan2(x, y) + Math.random() * .5f - .25f;
//				int x1 = x0 + x;
//				int y1 = y0 + y;
//				int x2 = (int)(x0 + Math.cos(theta) * d * 2);
//				int y2 = (int)(y0 + Math.sin(theta) * d * 2);
//				if (x2 > 0 && x2 < iWidth && y2 > 0 && y2 < iHeight
//					&& x1 > 0 && x1 < iWidth && y1 > 0 && y1 < iHeight)
//					image.setRGB(x1, y1, image.getRGB(x2, y2));
//			}
//		}
			
//		
//		//draw the star
//		if (starType != BLACK_HOLE){
//			//fast circle drawing routine.  More efficient for larger circles.  
//			for(int x = -radius; x <= radius; x++){
//				int yh = SpaceMath.intSqrt(radSqr - x * x);
//				for(int y = -yh; y <= yh; y++){
//					int x1 = x0 + x;
//					int y1 = y0 + y;
//					if (x1 > 0 && x1 < iWidth && y1 > 0 && y1 < iHeight);
//					
//					image.setRGB(x1, y1, color);
//				}
//			}
//		}
//		
		Graphics g = image.getGraphics();
		g.setColor(Color.GREEN);
		int x = (int)((position.x - camX) * zoom);
		int y = (int)((position.y - camY) * zoom);
		g.drawString(name, x, y + 10);
		g.drawString("Mass:  " + world.df.format(mass), x, y + 10);
		g.drawString("Break: " + world.df.format(durability/maxDurability*100), x, y + 20);
	}
}
