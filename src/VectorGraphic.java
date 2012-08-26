import java.awt.Graphics;
import java.awt.Color;

public class VectorGraphic{
	double[] xDataPoints;
	double[] yDataPoints;
	int inside;
	int outside;
	boolean outlineOnly = false;
	public VectorGraphic(double[] xPoints, double[] yPoints, int inside, int outside){
		xDataPoints = xPoints;
		yDataPoints = yPoints;
		this.inside = inside;
		this.outside = outside;
		if (inside == -1) outlineOnly = true;
		if (outside == -1){
			outside = 0xffffff;
		}
	}

	public static final double rad2 = 1.4;
	public static VectorGraphic getShip(int color){//make method for creating a ship graphic
		double[] shipX = new double[]{0, .4, 1, rad2, 0, -rad2, 1, -.4};
		double[] shipY = new double[]{.2, 1, 0, -rad2, -1, -rad2, 0, 1};
		return new VectorGraphic(shipX, shipY, color, -1);
	}
	/**
	 * An arrow facing θ = 0.  The nose of the arrow rests on the origin.  
	 */
	public static VectorGraphic getArrow(int color){
		double[] x = new double[]{0, -1, -1};
		double[] y = new double[]{0, 1, -1};
		return new VectorGraphic(x, y, color, -1);
	}
	public void render(Graphics g, PhysicsNode node, double xCam, double yCam, double zoom){
		int[] xRenderPoints = new int[xDataPoints.length];
		int[] yRenderPoints = new int[yDataPoints.length];
		double sinHead= Math.sin(node.heading);
		double cosHead = Math.cos(node.heading);
		for(int i = 0; i < xRenderPoints.length; i++){
			xRenderPoints[i] = (int)((node.position.x + (cosHead - sinHead) * node.radius * xDataPoints[i] - xCam) * zoom);
			yRenderPoints[i] = (int)((node.position.y + (sinHead - cosHead) * node.radius * yDataPoints[i] - yCam) * zoom);
		}
		if (!outlineOnly){
			g.setColor(new Color(inside));
			g.fillPolygon(xRenderPoints, yRenderPoints, xRenderPoints.length);
		}
		g.setColor(new Color(outside));
		g.drawPolygon(xRenderPoints, yRenderPoints, xRenderPoints.length);
		//System.out.println("X0: " + xRenderPoints[0] + ", Y0: " + yRenderPoints[0]);
	}
	/**
	 * A no nonsense rendering method intended for graphics unassociated with nodes.
	 */
	public void render(Graphics g, double x, double y, double size, double θ){
		int[] xRenderPoints = new int[xDataPoints.length];
		int[] yRenderPoints = new int[yDataPoints.length];
		double sinHead = SpaceMath.fastSine((float)θ);
		double cosHead = SpaceMath.fastCosine((float)θ);
		for(int i = 0; i < xRenderPoints.length; i++){
			xRenderPoints[i] = (int)(x + (cosHead * xDataPoints[i] - sinHead * yDataPoints[i]) * size);
			yRenderPoints[i] = (int)(y + (sinHead * xDataPoints[i] + cosHead * yDataPoints[i]) * size);
		}
		if (!outlineOnly){
			g.setColor(new Color(inside));
			g.fillPolygon(xRenderPoints, yRenderPoints, xRenderPoints.length);
		}
		g.setColor(new Color(outside));
		g.drawPolygon(xRenderPoints, yRenderPoints, xRenderPoints.length);
		//System.out.println("X0: " + xRenderPoints[0] + ", Y0: " + yRenderPoints[0]);
	}
}