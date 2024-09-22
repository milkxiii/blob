import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

public class Food {

	private int x;
	private int y;
	private int width;
	private int height;
	private int type;
	private Rectangle bounds;
	
	
	public Food(int x, int y, int width, int height, int type) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.type = type;
		this.bounds = new Rectangle(x, y, width, height);
	}
	
	
	public boolean checkCollision(Player p) {
		Rectangle rect = p.getRect();

		double top1 = rect.getY();
		double bottom1 = rect.getY() + rect.getHeight();

		double bottom2 = this.y + this.height;

		if (bounds.intersects(rect)) {

			// intersect from bottom
			if(!(top1 < bottom2 && bottom1 > bottom2)) {
				
				if (type != -1) {
					type = -1;
					return true;
				}
				
			}
		}
		return false;
	}


	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}


	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}


	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}


	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}


	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}


	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}


	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}


	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}


	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
}
