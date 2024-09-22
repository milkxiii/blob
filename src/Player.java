import java.awt.Rectangle;

public class Player extends Movable {

	private int x = 64*0;
	private int y = 64*7;

	private int jumpCount = 0;

	private Rectangle rect;

	private int dropHeight;

	private int height;
	private int width;
	private double lives = 3;

	// movement
	private boolean left;
	private boolean right;
	private boolean up;
	private boolean down;
	private boolean jumping;
	private boolean bounce;

	private Ledge currentLedge = new Ledge();

	private boolean airborne;


	private boolean allowRight = true;
	private boolean allowLeft = true;

	boolean stillBounce;

	private double xVel = 0;
	private double yVel = 0;
	final static double gravity = 0.3;
	final static double speed = 1;			//double variables for better accuracy/simulation of gravity

	public Player(int height, int width) {
		this.height = height;
		this.width = width;
	}

	// Description: resets player's variables
	// Parameters: none
	// Return: void
	public void reset() {
		left = false;
		right = false;
		up = false;
		down = false;
		jumping = false;
		airborne = false;
		allowRight = true;
		allowLeft = true;
		xVel = 0;
		yVel = 0;
		jumpCount = 0;
		lives = 3;
	}
	
	// Description: controls moving of player
	// Parameters: none
	// Return: void
	public void move() {
	
		// bounce back
		if (bounce) {
			yVel = 6;
			
			stillBounce = true;
			bounce = false;
		}
	
		// x direction
		else if(allowLeft && left) {
			xVel = -2;
		}
		else if(allowRight && right) {
			xVel = 2;
		}
		else {
			xVel = 0;
		}
	
		int startX = this.x;
		int endX = this.x+this.width;
	
		// y direction
		// jump from ground
		if(jumping) {
			airborne = true;
			yVel = 9;
			jumping = false;
		}
	
	
		// in air
		else if ((y+height) < dropHeight || !(endX > this.currentLedge.getStartX() +3 && startX < this.currentLedge.getEndX())){	
			yVel -= gravity;
			airborne = true;
		}
	
		else if (y+height == dropHeight){
			jumpCount = 0;
			//					yVel = 0;
			airborne = false;
		}
	
		this.x += xVel;
		this.y -= yVel;
		if (yVel < 0 && dropHeight-height-this.y < 10 
				&& (endX > this.currentLedge.getStartX() +3 && startX < this.currentLedge.getEndX())) { // this is when blob is in correct x position of current ledge (ie. not in air)
			this.y = dropHeight-height;
			jumpCount = 0;
			stillBounce = false;
		}
	
	}


	public Rectangle getRect() {
		return new Rectangle(x, y, width, height);
	}

	public void setLeft(boolean b) {
		left = b;
	}

	public void setRight(boolean b) {
		right = b;
	}

	public void setUp(boolean b) {
		up = b;
	}

	public void setDown(boolean b) {
		down = b;
	}

	public void setJumping(boolean b) {
		jumping = b;
	}

	public double getLives() {
		return lives;
	}

	public void setLives(double lives) {
		this.lives = lives;
	}

	public boolean isAirborne() {
		return airborne;
	}

	public void setAirborne(boolean airborne) {
		this.airborne = airborne;
	}

	public boolean isAllowLeft() {
		return allowLeft;
	}

	public void setAllowLeft(boolean allowLeft) {
		this.allowLeft = allowLeft;
	}

	public boolean isAllowRight() {
		return allowRight;
	}

	public void setAllowRight(boolean allowRight) {
		this.allowRight = allowRight;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
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

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Ledge getCurrentLedge() {
		return currentLedge;
	}

	public void setCurrentLedge(Ledge currentLedge) {
		this.currentLedge = currentLedge;
	}

	public int getDropHeight() {
		return dropHeight;
	}

	public void setDropHeight(int dropHeight) {
		this.dropHeight = dropHeight;
	}

	public double getxVel() {
		return xVel;
	}

	public void setxVel(double xVel) {
		this.xVel = xVel;
	}

	/**
	 * @return the jumpCount
	 */
	public int getJumpCount() {
		return jumpCount;
	}

	/**
	 * @param jumpCount the jumpCount to set
	 */
	public void setJumpCount(int jumpCount) {
		this.jumpCount = jumpCount;
	}


	public double getyVel() {
		return yVel;
	}


	public void setyVel(double yVel) {
		this.yVel = yVel;
	}


	public boolean isBounce() {
		return bounce;
	}


	public void setBounce(boolean bounce) {
		this.bounce = bounce;
	}


}
