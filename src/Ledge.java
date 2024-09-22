import java.awt.Rectangle;

public class Ledge {

	private Rectangle ledge;
	private final int tileWidth = 64;
	private final int tileHeight = 64;

	private int ledgeHeight = 640;
	private int startX;
	private int endX;


	public Ledge() {

	}
	public Ledge (int x, int y, int wid, int height) {
		ledge = new Rectangle (x, y, wid, height);
		ledgeHeight = y;
		startX = x;
		endX = x + wid;
	}

	// Description: extends the ledge to add another square to the right
	// Parameters: none
	// Return: void
	public void addTile() {
		ledge = new Rectangle (startX, ledgeHeight, (int) ledge.getWidth()+tileWidth, (int) ledge.getHeight());
		endX += tileWidth;
	}

	public boolean equals (Object o) {
		Ledge l = (Ledge) o;

		if (this.startX == l.startX && this.endX == l.endX && this.ledgeHeight == l.ledgeHeight) {
			return true;
		}
		return false;

	}

	// Description: controls what happens when player collides with ledge
	// Parameters: Player p is the player
	// Return: void
	public void checkCollision(Player p) {

		Rectangle rect = p.getRect();

		double left1 = rect.getX();
		double right1 = rect.getX() + rect.getWidth();
		double top1 = rect.getY();
		double bottom1 = rect.getY() + rect.getHeight();

		double left2 = ledge.getX();
		double right2 = ledge.getX() + ledge.getWidth();
		double top2 = ledge.getY();
		double bottom2 = ledge.getY() + ledge.getHeight();


		if (ledge.intersects(rect)) {

			// intersect from bottom
			if(top1 < bottom2 && bottom1 > bottom2) {

			}

			// intersect from top
			else if(bottom1 < top2 && top1 < top2 && p.getyVel() <= 0) {
				p.setJumpCount(0);

			}

			// rect collides from left side of the wall
			if(right1 > left2 && left1 < left2 && right1 - left2 < bottom1 - top2 && right1 - left2 < bottom2 - top1){

				p.setAllowRight(false);
				p.setX(p.getX() - 2);
				p.setxVel(0);
			}

			// rect collides from right side of the wall
			else if(left1 < right2 && right1 > right2 && right2 - left1 < bottom1 - top2 && right2 - left1 < bottom2 - top1){
				p.setAllowLeft(false);
				p.setX(p.getX() + 2);
				p.setxVel(0);
			}
		}
	}
	public int getLedgeHeight() {
		return ledgeHeight;
	}
	public void setLedgeHeight(int ledgeHeight) {
		this.ledgeHeight = ledgeHeight;
	}
	public int getStartX() {
		return startX;
	}
	public void setStartX(int startX) {
		this.startX = startX;
	}
	public int getEndX() {
		return endX;
	}
	public void setEndX(int endX) {
		this.endX = endX;
	}
	public Rectangle getLedge() {
		return ledge;
	}
	public void setLedge(Rectangle ledge) {
		this.ledge = ledge;
	}


}
