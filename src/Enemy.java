import java.awt.Rectangle;

public class Enemy extends Movable {

	private int x;
	private int y;
	private int width;
	private int height;
	private Rectangle rect;
	private int direction = 1;
	private int type;
	private int bounceCount = 0;
	private boolean movable = true;

	private int startX;
	private int endX;
	private boolean timeout = false;
	private int timeoutCount = 0;
	private boolean alive = true;
	private boolean show = true;

	private boolean intersect = false;
	
	public Enemy(int x, int y, int width, int height, int type) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.type = type;
	}

	public Rectangle getRect() {
		return new Rectangle(x, y, width, height);
	}

	// Description: checks if player is colliding with enemy
	// Parameters: none
	// Return: 1 if intersect from left, 2 if from right, 3 if from top, 0 if no collision
	public int checkCollision(Player p) {

		Rectangle pRect = p.getRect();

		double left1 = pRect.getX();
		double right1 = pRect.getX() + pRect.getWidth();
		double top1 = pRect.getY();
		double bottom1 = pRect.getY() + pRect.getHeight();

		double left2 = this.getRect().getX();
		double right2 = this.getRect().getX() + this.getRect().getWidth();
		double top2 = this.getRect().getY();
		double bottom2 = this.getRect().getY() + this.getRect().getHeight();


		if (this.getRect().intersects(p.getRect())) {
			// player collides from left side
			if(right1 > left2 && left1 < left2 && right1 - left2 < bottom1 - top2 && right1 - left2 < bottom2 - top1){
				return 1;
			}

			// player collides from right side
			else if(left1 < right2 && right1 > right2 && right2 - left1 < bottom1 - top2 && right2 - left1 < bottom2 - top1){
				return 2;
			}

			// enemy dies
			else if(!p.stillBounce && bottom1 > top2 && top1 + 6 < top2 && Math.abs(right1-right2) < 40 && Math.abs(left1-left2) < 40) {
				// JUST intersected
				if (!intersect) {
					bounceCount++;
					intersect = true;
				}
				return 3;
			}
						
		}
		
		else {
			intersect = false;
		}
		return 0;
	}

	// Description: controls moving of enemy
	// Parameters: none
	// Return: void
	public void move() {
		if (movable && startX != endX) {
			x+=direction*2;

			if (x >= endX) {
				direction = -1;
			}
			else if (x <= startX) {
				direction = 1;
			}
		}
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getEndX() {
		return endX;
	}

	public void setEndX(int endX) {
		this.endX = endX;
	}

	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isTimeout() {
		return timeout;
	}

	public void setTimeout(boolean timeout) {
		this.timeout = timeout;
	}

	public int getTimeoutCount() {
		return timeoutCount;
	}

	public void setTimeoutCount(int timeoutCount) {
		this.timeoutCount = timeoutCount;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getBounceCount() {
		return bounceCount;
	}

	public void setBounceCount(int bounceCount) {
		this.bounceCount = bounceCount;
	}

	public boolean isMovable() {
		return movable;
	}

	public void setMovable(boolean movable) {
		this.movable = movable;
	}

}
