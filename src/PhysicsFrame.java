import java.awt.*;

import javax.swing.JFrame;
import java.awt.image.BufferedImage;

public class PhysicsFrame extends JFrame{
	public int fieldWidth;
	public int fieldHeight;
	public BufferedImage buffer;
	public Graphics g;
	public Graphics screenGraphics;

	public GraphicsConfiguration gc;

	public static void main(String[] args){
		PhysicsWorld world = new PhysicsWorld(new PhysicsFrame(1300, 750));
		world.init();
		world.run();
	}

	public PhysicsFrame(int width, int height){
		super();
		fieldWidth = width;
		fieldHeight = height;
		setSize(width + 10, height + 25);
		setVisible(true);
		screenGraphics = getGraphics();

		gc = getGraphicsConfiguration();
		buffer = getBlankImage(width, height, Transparency.OPAQUE);
		g = buffer.getGraphics();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void renderToScreen(){
		screenGraphics.drawImage(buffer, 5, 20, null);
	}
	public BufferedImage getBlankImage(int width, int height, int alphaType){
		return gc.createCompatibleImage(width, height, alphaType);
	}
}