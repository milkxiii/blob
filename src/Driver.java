// Lynn Tao
// June 15, 2024
// Final ISU: blob!

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


@SuppressWarnings("serial")
public class Driver extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	int gamestate;

	final int HOME = 0;
	final int PLAYING = 1;
	final int MENU = 11;

	static boolean paused;
	static boolean passed;
	static boolean failed;
	static boolean over;
	static boolean heldDown = false;
	static boolean heldUp = false;

	int dropScreenY = 0;
	int progress = 0;
	int currentRecipe = 0;	
	int viewLoc = 0;
	int rulesPage = 0;
	int resultPage = 0;
	int titleOption = 0;

	boolean up, down, left, right;
	final int speed = 4;
	final static int screenWidth = 1200;
	final static int screenHeight = 800;
	final static int tileWidth = 64;
	final static int tileHeight = 64;
	static int actualWidth;
	static int actualHeight;
	final static int textSize = 36;
	static int offsetX = 0;
	static int offsetY = 0;

	int menuHeight = 0;
	int backgroundX;

	int threadCount = 0;
	int timeoutCount = 0;


	boolean nameReverse = false;
	boolean diffReverse = false;
	boolean showRecipe = false;
	boolean showRules = false;
	boolean showAbout = false;

	ArrayList<Ledge> ledges;
	ArrayList<Enemy> enemies;
	ArrayList<Item> menuItems;
	HashMap<Item, Level> itemsMap;

	int idleNo = 0;
	int runNo = 0;
	int fruitNo = 0;
	int poofNo = 0;
	int enemyNo = 0;
	int dieNo = -1;
	int direction = 1;

	Level level;
	boolean timeout = false;
	int[][] layout;
	Food[][] foods;


	static int caughtCount = 0;
	static int totalCount = 0;

	Thread thread;
	Player player;

	Image[][] mapImage;
	Image[] apples, bananas, pineapples, kiwis, melons, oranges, strawberries, poof;
	Image[] charIdle, charRun, enemyRun, enemyDie,  recipes, numbersImages, title, hearts, progressBar, rules;
	Image menuFrame, aboutScreen, pauseScreen, settingsScreen, pinksky, box, dark, failScreen, gameOverScreen, xButton, aboutButton;
	Image cursor1;
	BufferedImage menuImage;

	Graphics menuGraphics;

	Clip mainMusic, collectSound, bumpSound, boingSound, woosh, clickSound, winSound, loseSound;


	public Driver() throws IOException {
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		setVisible(true);

		// load blob and enemy character images
		charIdle = new BufferedImage[4];		
		for (int i = 0; i < 4; i++) {
			charIdle[i] = ImageIO.read(new File("images/slime_idle.png")).getSubimage(5+32*i, 6, 21, 21);
		}

		charRun = new BufferedImage[6];
		for (int i = 0; i < 6; i++) {
			charRun[i] = ImageIO.read(new File("images/slime_run.png")).getSubimage(5+32*i, 6, 21, 21);
		}

		enemyRun = new BufferedImage[6];
		for (int i = 0; i < 6; i++) {
			enemyRun[i] = ImageIO.read(new File("images/enemy_run.png")).getSubimage(5+32*i, 6, 21, 21);
		}

		enemyDie = new BufferedImage[5];
		for (int i = 0; i < 5; i++) {
			enemyDie[i] = ImageIO.read(new File("images/enemy_die.png")).getSubimage(5+32*i, 6, 21, 21);
		}

		// load title screens
		title = new BufferedImage[3];
		title[0] = ImageIO.read(new File("images/title-0.png"));
		title[1] = ImageIO.read(new File("images/title-1.png"));
		title[2] = ImageIO.read(new File("images/title-2.png"));

		// numbers/symbols
		numbersImages = new Image[11];
		for (int i = 0; i < 10; i++) {
			numbersImages[i] = Toolkit.getDefaultToolkit ().getImage ("images/numbers/" + i + ".png");
		}
		numbersImages[10] = Toolkit.getDefaultToolkit ().getImage ("images/numbers/slash.png");

		// fruits
		apples = new BufferedImage[17];
		bananas = new BufferedImage[17];
		pineapples = new BufferedImage[17];
		kiwis = new BufferedImage[17];
		melons = new BufferedImage[17];
		oranges = new BufferedImage[17];
		pineapples = new BufferedImage[17];
		strawberries = new BufferedImage[17];
		poof = new BufferedImage[6];

		// add to array
		for (int i = 0; i < apples.length; i++) {
			apples[i] = ImageIO.read(new File("images/fruits/apple.png")).getSubimage(i*32, 5, 25, 25);
		}

		for (int i = 0; i < bananas.length; i++) {
			bananas[i] = ImageIO.read(new File("images/fruits/bananas.png")).getSubimage(i*32, 5, 25, 25);
		}

		for (int i = 0; i < kiwis.length; i++) {
			kiwis[i] = ImageIO.read(new File("images/fruits/kiwi.png")).getSubimage(i*32, 5, 25, 25);
		}

		for (int i = 0; i < pineapples.length; i++) {
			pineapples[i] = ImageIO.read(new File("images/fruits/pineapple.png")).getSubimage(i*32, 5, 25, 25);
		}

		for (int i = 0; i < strawberries.length; i++) {
			strawberries[i] = ImageIO.read(new File("images/fruits/strawberry.png")).getSubimage(i*32, 5, 25, 25);
		}

		for (int i = 0; i < poof.length; i++) {
			poof[i] = ImageIO.read(new File("images/fruits/collected.png")).getSubimage(i*32, 5, 25, 25);
		}

		// hearts
		hearts = new BufferedImage[3];
		hearts[0] = ImageIO.read(new File("images/hearts.png")).getSubimage(4, 53, 8, 7);
		hearts[1] = ImageIO.read(new File("images/hearts.png")).getSubimage(36, 53, 8, 7);
		hearts[2] = ImageIO.read(new File("images/hearts.png")).getSubimage(36, 69, 8, 7);

		// progress bar
		progressBar = new BufferedImage[2];
		progressBar[0] = ImageIO.read(new File("images/progress_light.png"));
		progressBar[1] = ImageIO.read(new File("images/progress_dark.png"));		

		// rules screens
		rules = new BufferedImage[12];

		for (int i = 0; i < 12; i++) {
			rules[i] = ImageIO.read(new File("images/rules/pg" + i + ".png"));
		}


		// other buttons/screens
		pinksky = ImageIO.read(new File("images/pinksky2.png"));
		box = ImageIO.read(new File("images/box.png"));
		xButton = ImageIO.read(new File("images/xbutton.png"));
		aboutButton = ImageIO.read(new File("images/aboutbutton.png"));
		aboutScreen = ImageIO.read(new File("images/about.png"));		
		pauseScreen = ImageIO.read(new File("images/paused.png"));		
		failScreen = ImageIO.read(new File("images/failscreen.png"));		
		settingsScreen = ImageIO.read(new File("images/settings.png"));		
		dark = ImageIO.read(new File("images/dark.png"));		
		gameOverScreen = ImageIO.read(new File("images/gameover.png"));	
		menuFrame = ImageIO.read(new File("images/menuframe.png"));	


		// map of items
		itemsMap = new HashMap<Item, Level>();
		itemsMap.put(new Item("jelly sundae", 1, 6, 
				ImageIO.read(new File("images/items/jellysundae.png")), 
				ImageIO.read(new File("images/recipes/jellysundaerecipe.png"))), 
				new Level("maps/jellysundaemap.txt"));

		itemsMap.put(new Item("fruit cake", 3, 15, 
				ImageIO.read(new File("images/items/fruitcake.png")), 
				ImageIO.read(new File("images/recipes/fruitcakerecipe.png"))), 
				new Level("maps/fruitcakemap.txt"));


		itemsMap.put(new Item("swiss roll", 3, 16, 
				ImageIO.read(new File("images/items/swissroll.png")), 
				ImageIO.read(new File("images/recipes/swissrollrecipe.png"))), 
				new Level("maps/swissrollmap.txt"));

		itemsMap.put(new Item("matcha cake", 2, 11, 
				ImageIO.read(new File("images/items/matchacake.png")), 
				ImageIO.read(new File("images/recipes/matchacakerecipe.png"))), 
				new Level("maps/matchacakemap.txt"));

		itemsMap.put(new Item("cupcake", 1, 9, 
				ImageIO.read(new File("images/items/cupcake.png")), 
				ImageIO.read(new File("images/recipes/cupcakerecipe.png"))), 
				new Level("maps/cupcakemap.txt"));


		// arraylist of items
		menuItems = new ArrayList<Item>();

		for (Item i : itemsMap.keySet()) {
			menuItems.add(i);
		}

		// height of menu
		menuHeight = menuItems.size()*150;


		// sounds
		try {
			AudioInputStream sound;

			// background music
			sound = AudioSystem.getAudioInputStream(new File ("sounds/music.wav"));
			mainMusic = AudioSystem.getClip();
			mainMusic.open(sound);

			// collect fruit sound
			sound = AudioSystem.getAudioInputStream(new File ("sounds/collect.wav"));
			collectSound = AudioSystem.getClip();
			collectSound.open(sound);

			// bump into enemy sound
			sound = AudioSystem.getAudioInputStream(new File ("sounds/oops.wav"));
			bumpSound = AudioSystem.getClip();
			bumpSound.open(sound);

			// boing sound
			sound = AudioSystem.getAudioInputStream(new File ("sounds/boing.wav"));
			boingSound = AudioSystem.getClip();
			boingSound.open(sound);

			// click sound
			sound = AudioSystem.getAudioInputStream(new File ("sounds/clicksound.wav"));
			clickSound = AudioSystem.getClip();
			clickSound.open(sound);

			// win sound
			sound = AudioSystem.getAudioInputStream(new File ("sounds/win.wav"));
			winSound = AudioSystem.getClip();
			winSound.open(sound);

			// lose sound
			sound = AudioSystem.getAudioInputStream(new File ("sounds/lose.wav"));
			loseSound = AudioSystem.getClip();
			loseSound.open(sound);
		}

		catch (Exception e) {

		}

		// start main menu music + keep looping
		mainMusic.setFramePosition (0);
		mainMusic.start();
		mainMusic.loop(Clip.LOOP_CONTINUOUSLY);


		// custom cursor
		cursor1 = Toolkit.getDefaultToolkit().getImage("images/mouse1.png");

		Point hotspot = new Point (0, 0);
		Toolkit toolkit = Toolkit.getDefaultToolkit ();
		Cursor cursor = toolkit.createCustomCursor (cursor1, hotspot, "images/mouse1.png");
		this.setCursor (cursor);

		// new player object
		player = new Player(52, 48);

		// start thread
		thread = new Thread(this);
		thread.start();
	}

	// Description: called automatically when thread is going on
	// Parameters: none
	// Return: void
	public void run() {
		gamestate = HOME;

		while(true) {
			//main game loop
			update();
			this.repaint();
			try {
				Thread.sleep(9);
			} 
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Description: calls each time thread refreshes to update everything
	// Parameters: none
	// Return: void
	public void update() {
		threadCount++;

		// home page
		if (gamestate == HOME) {
			if (threadCount % 10 == 0) {
				backgroundX -= speed;
				if(backgroundX < -screenWidth)
					backgroundX = 0;
				else if(backgroundX > screenWidth)
					backgroundX = 0;
			}

			// change blob sprite every 16 frame refreshes
			if (threadCount % 16 == 0) {
				idleNo++;
			}
			if (idleNo >= 4) 
				idleNo = 0;

		}

		// while playing game
		else if (gamestate == PLAYING && !paused && !passed && !failed) {

			keepInBound();
			player.move();
			player.setAirborne(true);
			player.setAllowLeft(true);
			player.setAllowRight(true);

			updateOffset();
			updateLedge();

			// check if fruit is collected
			for (int row = 1; row < foods.length-1; row++) {
				for (int col = 1; col < foods[row].length-1; col++) {
					Food f = foods[row][col];

					if (f != null) {
						// collect fruit
						if(f.checkCollision(player)) {
							caughtCount++;
							collectSound.setFramePosition(0);
							collectSound.start();
						}
					}
				}
			}

			// check if it touches enemies
			for (Enemy enemy : enemies) {
				enemy.move();

				// bump into enemy from left
				if (enemy.isAlive() && !enemy.isTimeout() && enemy.checkCollision(player) == 1) {
					player.setLives(player.getLives() - 1);
					enemy.setTimeout(true);

					player.setBounce(true);
					player.setX(player.getX() - 25);

					bumpSound.setFramePosition(0);
					bumpSound.start();
				}

				// bump into enemy from left
				if (enemy.isAlive() && !enemy.isTimeout() && enemy.checkCollision(player) == 2) {
					player.setLives(player.getLives() - 1);
					enemy.setTimeout(true);

					player.setBounce(true);
					player.setX(player.getX() + 25);

					bumpSound.setFramePosition(0);
					bumpSound.start();
				}

				// enemy dies
				else if (enemy.isAlive() && enemy.checkCollision(player) == 3 && player.getyVel() < 0) {

					// no flying or walking enemies -> enemy dies immediately
					if (enemy.getType() == 1 && !enemy.isMovable())
						enemy.setAlive(false);

					// walking enemies -> enemy dies after 2 bounces
					else if (enemy.getType() == 1  && enemy.isMovable()) {
						enemy.setMovable(false);

						// bounce 2 times -> enemy dies
						if (enemy.getBounceCount() > 2) {
							enemy.setAlive(false);
						}
					}
					// flying enemies
					else if (enemy.getType() == 2) {
						enemy.setMovable(false);

						// bounce 3 times -> enemy dies
						if (enemy.getBounceCount() > 3) {
							enemy.setAlive(false);
						}
					}
					player.setJumping(true);
					player.setJumpCount(0);
					dieNo++;

					boingSound.setFramePosition(0);
					boingSound.start();
				}

				// enemy reaches end of dying animation
				else if (enemy.isShow() && !enemy.isAlive() && dieNo == -1) {
					enemy.setShow(false);
				}

				// enemy is in cooldown before killing player again 
				if (enemy.isTimeout()) {
					enemy.setTimeoutCount(enemy.getTimeoutCount() + 1);

					// cooldown period ends
					if (enemy.getTimeoutCount() > 200) {
						enemy.setTimeout(false);
						enemy.setTimeoutCount(0);
					}
				}
			}


			// you lose
			if (player.getLives() == 0 && !over) {
				failed = true;
			}


			// you win
			else if (caughtCount == totalCount && caughtCount != 0 && !over) {
				passed = true;
				dropScreenY = -screenHeight; // screen that comes down
			}

			// run out of time
			if (screenWidth-60-progress < 0 && !over) {
				failed = true;
			}

			// fruit animations
			if (threadCount % 8 == 0) {
				fruitNo++;
				poofNo++;

			}

			// progress bar
			if (threadCount % 3 == 0) {
				progress++;

			}

			// change blob sprite every 16 frame refreshes
			if (threadCount % 16 == 0) {
				idleNo++;
				enemyNo++;
				runNo++;

				if (dieNo != -1)
					dieNo++;
			}


			// when it is at the last sprite animation
			if (dieNo >= 5) {
				dieNo = -1;
			}
			if (runNo >= 6) {
				runNo = 0;
			}
			if (idleNo >= 4) 
				idleNo = 0;

			if (enemyNo >= 6) {
				enemyNo = 0;
			}
			if (poofNo >= 6) {
				poofNo = 0;
			}
			if (fruitNo >= 16) {
				fruitNo = 0;
			}
		}

		// menu page
		else if (gamestate == MENU) {
			// holding down button
			if (heldDown)
				viewLoc+=5;

			// holding up button
			else if (heldUp)
				viewLoc-=5;

			// make sure you aren't viewing past the viewable area
			viewLoc = Math.max(viewLoc, 0);
			viewLoc = Math.min(viewLoc, menuHeight-430);

		}

		// dropScreenY controls movement for pause screen dropping down
		if (paused && dropScreenY < -30) {
			dropScreenY+=30;
		}

		if (passed && dropScreenY < -30) {
			dropScreenY+=30;
		}

		if (passed && !over) {
			winSound.setFramePosition(0);
			winSound.start();
			over = true;
		}

		if (failed && !over) {
			loseSound.setFramePosition(0);
			loseSound.start();
			over = true;
		}

	}


	// Description: finds the ledge that player would fall on
	// Parameters: none
	// Return: none, sets the player's current ledge and drop height
	public void updateLedge() {
		int startX = player.getX();
		int endX = player.getX()+player.getWidth();
		int startY = player.getY();
		int endY = player.getY() + player.getHeight();

		for (Ledge l : ledges) {
			l.checkCollision(player);

			// player is above the ledge
			// player is in between the edge horizontally
			if (endY < l.getLedgeHeight() && endX > l.getStartX() +3 && startX < l.getEndX()) {

				// this ledge is higher than the player's current ledge
				if (l.getLedgeHeight() < player.getCurrentLedge().getLedgeHeight() || !(endX > player.getCurrentLedge().getStartX() +3 && startX < player.getCurrentLedge().getEndX())) {
					player.setCurrentLedge(l);
					player.setDropHeight(l.getLedgeHeight());
				}

			}
		}
	}

	// Description: updates the offset while playing the map (ensure view portion of screen is good)
	// Parameters: none
	// Return: none, updates offsetX and offsetY
	public void updateOffset() {
		// HORIZONTAL
		// follow player to the right
		if (player.getxVel() > 0 && offsetX < 0 && offsetX > (-1)*(actualWidth-screenWidth))
			offsetX -= player.getxVel()*0.8;

		// follow player to the left
		else if (player.getxVel() < 0 && offsetX < 0) {
			offsetX -= player.getxVel()*0.5;
		}

		// NOT MOVING -> update offset to make player more centered

		// move to right
		else if (player.getX() + offsetX > actualWidth-screenWidth && direction == 1 && offsetX > (-1)*(actualWidth-screenWidth)) {
			offsetX-=2;
		}

		// move to left
		else if (player.getX() + offsetX < 150 && direction == -1 && offsetX < 0) {
			offsetX+=2;
		}

		// VERTICAL
		// move up
		if (player.getY()+offsetY < 100)
			offsetY+=2;
		// move down
		else if (player.getY()+offsetY > screenHeight-300 && offsetY > screenHeight-actualHeight)
			offsetY-=2;
	}

	// Description: draws all the graphics you see on the window
	// Parameters: Graphics g
	// Return: void
	public void paintComponent(Graphics g) {
		int x, y;

		super.paintComponent(g);
		g.translate(offsetX, offsetY);

		if (gamestate == HOME) {
			drawMovingBackground(g, pinksky);
			g.drawImage(charIdle[idleNo], 500, 300, 200, 220, this); 
			g.drawImage(title[titleOption], 0, 0, screenWidth, screenHeight, this);
			g.drawImage(aboutButton, 20, 20, 80, 80, this);


			if (showRules) {
				g.drawImage(dark, 0, 0, screenWidth, screenHeight, this);
				g.drawImage(rules[rulesPage], 0, 0, screenWidth, screenHeight, this);
				g.drawImage(xButton, 85, 160, 38, 38, this);
			}

			if (showAbout) {
				g.drawImage(dark, 0, 0, screenWidth, screenHeight, this);
				g.drawImage(aboutScreen, 0, 0, screenWidth, screenHeight, this);
				g.drawImage(xButton, 85, 160, 38, 38, this);
			}
		}

		if (gamestate == MENU) {
			offsetX = 0;
			offsetY = 0;

			if (menuGraphics == null) {
				menuImage = new BufferedImage (screenWidth, menuHeight, BufferedImage.TYPE_INT_ARGB);
				menuGraphics = menuImage.getGraphics ();

			}

			g.drawImage(pinksky, 0, 0, screenWidth, screenHeight, this);


			for (int i = 0; i < menuItems.size(); i++) {
				menuGraphics.drawImage(menuItems.get(i).getPreview(), 0, 0+150*i, screenWidth, screenHeight-220, this);
			}

			g.drawImage(menuFrame, 0, 0, screenWidth, screenHeight, this);

			g.drawImage(menuImage.getSubimage(0, viewLoc, screenWidth, 430), 0, 200, screenWidth, 430, this);



			if (showRecipe) {
				g.drawImage(dark, 0, 0, screenWidth, screenHeight, this);
				g.drawImage(menuItems.get(currentRecipe).getRecipe(), 0, 0, screenWidth, screenHeight, this);
				g.drawImage(xButton, 85, 160, 38, 38, this);
			}

		}

		if (gamestate == PLAYING) {

			g.drawImage(pinksky, -50, -300, (int)(screenWidth*2), (int) (screenHeight*2), this);

			for (int col = 1; col < layout[0].length-1; col++) {

				for (int row = 1; row < layout.length-1; row++) {
					x = (col-1) * tileWidth;
					y = (row-1) * tileHeight;
					g.drawImage(mapImage[row][col], x, y, tileWidth, tileHeight, this);
				}
			}


			if (player.getxVel() == 0) {
				if (direction > 0)
					g.drawImage(charIdle[idleNo], player.getX(), player.getY(), 50, 55, this); 

				else
					g.drawImage(charIdle[idleNo], player.getX()+player.getWidth(), player.getY(), -50, 55, this); 
			}

			else {
				if (direction > 0)
					g.drawImage(charRun[runNo], player.getX(), player.getY(), 50, 55, this); 
				else
					g.drawImage(charRun[runNo], player.getX() + player.getWidth(), player.getY(), -50, 55, this); 

			}
			drawFruits(g);


			for (Enemy enemy : enemies) {

				if (enemy.isAlive()) {
					if (enemy.getDirection() > 0) {
						g.drawImage(enemyRun[enemyNo], enemy.getX(), enemy.getY(), 50, 55, this); 
					}

					else {
						g.drawImage(enemyRun[enemyNo], enemy.getX()+enemy.getWidth(), enemy.getY(), -50, 55, this); 
					}
				}

				else if (enemy.isShow()){
					if (dieNo != -1)
						g.drawImage(enemyDie[dieNo], enemy.getX()+enemy.getWidth(), enemy.getY(), -50, 55, this);
				}
			}

			// draw the count
			String s = Integer.toString(caughtCount);
			String s2 = Integer.toString(totalCount);

			int pos = 140-(s.length() + s2.length() + 1)*textSize/2;

			g.drawImage(box, -offsetX + 5, -offsetY, 280, 140, this);

			for (int i = 0; i < s.length(); i++) {
				g.drawImage(numbersImages[Integer.parseInt(s.substring(i, i+1))], pos+i*textSize-offsetX, 50-offsetY, textSize, textSize, this);
			}

			g.drawImage(numbersImages[10], pos+(s.length())*textSize-offsetX, 50-offsetY, textSize, textSize, this);


			for (int i = 0; i < s2.length(); i++) {
				g.drawImage(numbersImages[Integer.parseInt(s2.substring(i, i+1))], pos+(i+1+s.length())*textSize-offsetX, 50-offsetY, textSize, textSize, this);
			}

			// draw progress
			g.drawImage(progressBar[0], 30-offsetX, 5-offsetY, screenWidth-60, 10, this);
			g.drawImage(progressBar[1], 30-offsetX, 5-offsetY, Math.max(screenWidth-60-progress, 0), 10, this);


			drawHearts(g);

			if (paused) {
				g.drawImage(dark, -offsetX, -offsetY, screenWidth, screenHeight, this);
				g.drawImage(pauseScreen, -offsetX, dropScreenY-offsetY, screenWidth, screenHeight, this);
			}

			if (passed) {
				g.drawImage(dark, -offsetX, -offsetY, screenWidth, screenHeight, this);
				g.drawImage(gameOverScreen, -offsetX, dropScreenY-offsetY, screenWidth, screenHeight, this);
				g.drawImage(menuItems.get(currentRecipe).getRecipe().getSubimage(305, 148, 180, 110), -offsetX+screenWidth/2-180, dropScreenY-offsetY+350, 180*2, 110*2, this);
			}

			if (failed) {
				g.drawImage(dark, -offsetX, -offsetY, screenWidth, screenHeight, this);
				g.drawImage(failScreen, -offsetX, dropScreenY-offsetY, screenWidth, screenHeight, this);
			}
		}
	}

	// Description: draws the background that moves slowly
	// Parameters: Image background is the background picture
	// Return: none (void)
	public void drawMovingBackground(Graphics g, Image background) {
		g.drawImage(background, backgroundX, 0, screenWidth, screenHeight, null);
		g.drawImage(background, backgroundX + screenWidth, 0, screenWidth, screenHeight, null);
		g.drawImage(background, backgroundX - screenWidth, 0, screenWidth, screenHeight, null);
	}


	// Description: draws the stamina 
	// Parameters: Graphics g 
	// Return: void
	public void drawHearts(Graphics g) {

		for (int i = 0; i < 3; i++) {

			if ((i+1) < (int)(player.getLives())) {
				g.drawImage(hearts[0], screenWidth - 250 - offsetX+78*i, 20 - offsetY, 56, 49, this); 
			}

			else if ((i+1) > (int)(player.getLives())) {
				g.drawImage(hearts[2], screenWidth - 250 - offsetX+78*i, 20 - offsetY, 56, 49, this); 
			}

			else {
				g.drawImage(hearts[0], screenWidth - 250 - offsetX+78*i, 20 - offsetY, 56, 49, this); 
			}
		}

	}

	// Description: draws the fruits 
	// Parameters: Graphics g 
	// Return: void
	public void drawFruits(Graphics g) {
		int[][] fruitLoc = level.getFruits();

		for (int row = 1; row < foods.length-1; row++) {
			for (int col = 1; col < foods[row].length-1; col++) {
				Food f = foods[row][col];

				if (f != null) {
					if (f.getType() == 1)
						g.drawImage(apples[fruitNo], f.getX(), f.getY(), f.getWidth(), f.getHeight(), this);

					else if (f.getType() == 2)
						g.drawImage(bananas[fruitNo], f.getX(), f.getY(), f.getWidth(), f.getHeight(), this);

					else if (f.getType() == 3)
						g.drawImage(kiwis[fruitNo], f.getX(), f.getY(), f.getWidth(), f.getHeight(), this);

					else if (f.getType() == 4)
						g.drawImage(pineapples[fruitNo], f.getX(), f.getY(), f.getWidth(), f.getHeight(), this);

					else if (f.getType() == 5)
						g.drawImage(strawberries[fruitNo], f.getX(), f.getY(), f.getWidth(), f.getHeight(), this);

					// fruit is disappearing
					else if (f.getType() == -1) {
						g.drawImage(poof[poofNo], f.getX(), f.getY(), f.getWidth(), f.getHeight(), this);

						// reached last animation -> remove the fruit
						if (poofNo == 5) {
							foods[row][col] = null;
							fruitLoc[row][col] = 0;
						}
					}
				}
			}
		}
	}

	// Description: draws rectangles around sprite (used for testing collision)
	// Parameters: Graphics g 
	// Return: void
	public void drawRectangles(Graphics g) {
		for (Ledge l : ledges) {
			g.drawRect((int)l.getLedge().getX(), (int)l.getLedge().getY(), (int)l.getLedge().getWidth(), (int)l.getLedge().getHeight());
		}
		g.drawRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());

		for (Enemy enemy : enemies)
			g.drawRect(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());

	}

	// Description: resets variables when new level is started
	// Parameters: Level l is the current level
	// Return: void
	public void newLevel(Level l) throws IOException {
		caughtCount = 0;
		failed = false;
		paused = false;
		passed = false;

		level = l;
		level.resetFruits();
		makeMap();
		loadEnemies();
		loadFruits();
		player.reset();
		player.setX(0);
		player.setY(actualHeight-screenHeight);

		showRecipe = false;
		progress = 0;
		over = false;

		offsetY = screenHeight-actualHeight;
		offsetX = 0;
	}


	// Description: adds new Ledge objects according to the map
	// Parameters: none
	// Return: void
	public void makeMap() throws IOException {
		ledges = new ArrayList<>();
		layout = level.getLayout();
		mapImage = new BufferedImage[layout.length][layout[0].length];

		// actual width and height (not just the view portion)
		actualWidth = tileWidth * (layout[0].length-2);
		actualHeight = tileHeight * (layout.length-2);

		// ground
		ledges.add(new Ledge (0, actualHeight, actualWidth, tileHeight));


		for (int row = 1; row < layout.length-1; row++) {
			for (int col = 1; col < layout[0].length-1; col++) {

				int x = (col-1) * tileWidth;
				int y = (row-1) * tileHeight;

				// has normal tile
				if (layout[row][col] == 1) {
					// has tile to the left -> extend that ledge one tile to the right
					if (layout[row][col-1] == 1) {
						ledges.get(ledges.size()-1).addTile();
					}
					// no tile to left -> add a new ledge in that position
					else {
						ledges.add(new Ledge (x, y, tileWidth, tileHeight));
					}

					addTiles(row, col, 0, 1);


				}

				// has pink tile
				else if (layout[row][col] == 2) {
					// has tile to the left -> extend that ledge one tile to the right
					if (layout[row][col-1] == 2) {
						ledges.get(ledges.size()-1).addTile();
					}
					// no tile to left -> add a new ledge in that position
					else {
						ledges.add(new Ledge (x, y, tileWidth, tileHeight));
					}

					addTiles(row, col, 19*4, 2);

				}

				// has hamburger tile
				else if (layout[row][col] == 3) {
					// has tile to the left -> extend that ledge one tile to the right
					if (layout[row][col-1] == 3) {
						ledges.get(ledges.size()-1).addTile();
					}
					// no tile to left -> add a new ledge in that position
					else {
						ledges.add(new Ledge (x, y, tileWidth, tileHeight));
					}

					addTiles(row, col, 0, 3);

				}

				// brown tile that you can walk through
				else if (layout[row][col] == 7) {
					addTiles(row, col, 0, 1);

				}

				// pink tile that you can walk through
				else if (layout[row][col] == 8) {
					addTiles(row, col, 0, 2);

				}

			}
		}
	}


	// Description: adds the corresponding tile images into mapImage
	// Parameters: int row, int col is the row and column, int shift is location of that tile on the sprite sheet relative to the first image, int num is the type of tile
	// Return: void, adds images to mapImage
	public void addTiles(int row, int col, int shift, int num) throws IOException {

		// pink
		if (num == 2) {
			// middle with diagonal left top missing
			if (layout[row-1][col] == num && layout[row+1][col] == num && layout[row][col-1] == num && layout[row][col+1] == num
					&& layout[row-1][col-1] != num && layout[row+1][col-1] == 1 && layout[row-1][col+1] == 1 && layout[row+1][col+1] == 1) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*2+shift, 19*2, 18, 18);

			}

			// single block
			else if (layout[row-1][col] != num && layout[row+1][col] != num && layout[row][col-1] != num && layout[row][col+1] != num
					&& layout[row-1][col-1] != num && layout[row+1][col-1] != num && layout[row-1][col+1] != num && layout[row+1][col+1] != num) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*2+shift, 19*2, 18, 18);

			}


			// top left tile
			else if (layout[row-1][col] != num && layout[row+1][col] == num && layout[row][col-1] != num && layout[row][col+1] == num) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19+shift, 19, 18, 18);
			}


			// top middle tile
			else if (layout[row-1][col] != num && layout[row+1][col] == num && layout[row][col-1] == num && layout[row][col+1] == num) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*2+shift, 19, 18, 18);
			}

			// top right tile
			else if (layout[row-1][col] != num && layout[row+1][col] == num && layout[row][col-1] == num && layout[row][col+1] != num) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*3+shift, 19, 18, 18);
			}



			// middle left tile
			else if (layout[row-1][col] == num && (layout[row+1][col] == num || layout[row+1][col] == 9) && layout[row][col-1] != num && layout[row][col+1] == num) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19+shift, 19*2, 18, 18);
			}

			// middle middle tile
			else if (layout[row-1][col] == num && (layout[row+1][col] == num || layout[row+1][col] == 9) && layout[row][col-1] == num && layout[row][col+1] == num
					&& layout[row-1][col-1] == num && (layout[row+1][col-1] == num || layout[row+1][col-1] == 9) && layout[row-1][col+1] == num && (layout[row+1][col+1] == num || layout[row+1][col+1] == 9)) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*2+shift, 19*2, 18, 18);
			}

			// middle right tile
			else if (layout[row-1][col] == num && (layout[row+1][col] == num || layout[row+1][col] == 9) && layout[row][col-1] == num && layout[row][col+1] != num) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*3+shift, 19*2, 18, 18);
			}


			// bottom left tile
			else if (layout[row-1][col] == num && layout[row+1][col] != num && layout[row][col-1] != num && layout[row][col+1] == num) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19+shift, 19*3, 18, 18);
			}

			// bottom middle tile
			else if (layout[row-1][col] == num && layout[row+1][col] != num && layout[row][col-1] == num && layout[row][col+1] == num) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*2+shift, 19*3, 18, 18);
			}


			// bottom right tile
			else if (layout[row-1][col] == num && layout[row+1][col] != num && layout[row][col-1] == num && layout[row][col+1] != num) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*3+shift, 19*3, 18, 18);
			}


			else {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*2+shift, 19*2, 18, 18);
			}
		}


		// brown
		else if (num == 1) {
			// middle with diagonal left top missing
			if (layout[row-1][col] != 0 && layout[row+1][col] != 0 && layout[row][col-1] != 0 && layout[row][col+1] != 0
					&& layout[row-1][col-1] == 0 && layout[row+1][col-1] == 1 && layout[row-1][col+1] == 1 && layout[row+1][col+1] == 1) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*2+shift, 19*2, 18, 18);

			}

			// single block
			else if (layout[row-1][col] == 0 && layout[row+1][col] == 0 && layout[row][col-1] == 0 && layout[row][col+1] == 0
					&& layout[row-1][col-1] == 0 && layout[row+1][col-1] == 0 && layout[row-1][col+1] == 0 && layout[row+1][col+1] == 0) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*2+shift, 19*2, 18, 18);

			}


			// top left tile
			else if (layout[row-1][col] == 0 && layout[row+1][col] != 0 && layout[row][col-1] == 0 && layout[row][col+1] != 0) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19+shift, 19, 18, 18);
			}


			// top middle tile
			else if (layout[row-1][col] == 0 && layout[row+1][col] != 0 && layout[row][col-1] != 0 && layout[row][col+1] != 0) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*2+shift, 19, 18, 18);
			}

			// top right tile
			else if (layout[row-1][col] == 0 && layout[row+1][col] != 0 && layout[row][col-1] != 0 && layout[row][col+1] == 0) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*3+shift, 19, 18, 18);
			}



			// middle left tile
			else if (layout[row-1][col] != 0 && layout[row+1][col] != 0 && layout[row][col-1] == 0 && layout[row][col+1] != 0) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19+shift, 19*2, 18, 18);
			}

			// middle middle tile
			else if (layout[row-1][col] != 0 && layout[row+1][col] != 0 && layout[row][col-1] != 0 && layout[row][col+1] != 0
					&& layout[row-1][col-1] != 0 && layout[row+1][col-1] != 0 && layout[row-1][col+1] != 0 && layout[row+1][col+1] != 0
					) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*2+shift, 19*2, 18, 18);
			}

			// middle right tile
			else if (layout[row-1][col] != 0 && layout[row+1][col] != 0 && layout[row][col-1] != 0 && layout[row][col+1] == 0) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*3+shift, 19*2, 18, 18);
			}



			// bottom left tile
			else if (layout[row-1][col] != 0 && layout[row+1][col] == 0 && layout[row][col-1] == 0 && layout[row][col+1] != 0) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*3+shift, 19, 18, 18);
			}

			// bottom middle tile
			else if (layout[row-1][col] != 0 && layout[row+1][col] == 0 && layout[row][col-1] != 0 && layout[row][col+1] != 0) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*2+shift, 19*3, 18, 18);
			}



			// bottom right tile
			else if (layout[row-1][col] != 0 && layout[row+1][col] != 1 && layout[row][col-1] != 0 && layout[row][col+1] != 1
					&& layout[row+1][col] != 7 && layout[row][col+1] != 7) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*3+shift, 19*3, 18, 18);
			}


			else {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*2+shift, 19*2, 18, 18);
			}
		}


		// hamburger
		else if (num == 3) {
			if (layout[row][col-1] == 3 && layout[row][col+1] == 3) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*14, 19*5, 18, 18);
			}
			else if (layout[row][col-1] == 0 && layout[row][col+1] == 3) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*13, 19*5, 18, 18);
			}
			else if (layout[row][col-1] == 3 && layout[row][col+1] == 0) {
				mapImage[row][col] = ImageIO.read(new File("images/tilemap.png")).getSubimage(19*15, 19*5, 18, 18);
			}
		}
	}

	// Description: adds the Food objects
	// Parameters: none
	// Return: void, updates food 2d array
	public void loadFruits() {
		totalCount = 0;
		foods = new Food[level.getFruits().length][level.getFruits()[0].length];

		int[][] fruitLoc = level.getFruits();


		for (int row = 1; row < fruitLoc.length-1; row++) {
			for (int col = 1; col < fruitLoc[row].length-1; col++) {
				int x = (col-1) * tileWidth;
				int y = (row-1) * tileHeight+16;

				if (fruitLoc[row][col] != 0) {
					foods[row][col] = new Food(x, y, tileWidth, tileHeight, fruitLoc[row][col]);
					totalCount++;
				}

			}
		}
	}

	// Description: adds the Enemy objects into arraylist
	// Parameters: none
	// Return: void, updates enemies ArrayList
	public void loadEnemies() {
		enemies = new ArrayList<Enemy>();
		int[][] enemyLoc = level.getEnemies();

		// NON-FLYING ENEMIES
		// each enemy only moves left to right on one row back and forth
		for (int row = 1; row < enemyLoc.length-1; row++) {
			int startX = actualWidth; // start of enemy in that area
			int endX = 0; // end of enemy in that area
			int enemyY = 0; // y position of enemy

			for (int col = 1; col < enemyLoc[row].length-1; col++) {
				int x = (col-1) * tileWidth;
				int y = (row-1) * tileHeight+16;

				// has enemy
				if (enemyLoc[row][col] == 1) {
					startX = Math.min(startX, x);
					endX = Math.max(endX, x);
					enemyY = y;
				}

				// no enemy in current tile, but previous tile would've had enemy
				else if (endX > 0) {
					// create new enemy and set ending/starting positions
					Enemy e = new Enemy (startX, enemyY, 52, 48, 1);
					e.setEndX(endX);
					e.setStartX(startX);

					if (startX == endX) {
						e.setMovable(false);
					}
					else {
						e.setMovable(true);
					}

					// add enemy to arraylist
					enemies.add(e);

					// reset end/start for next enemy
					endX = 0;
					startX = actualWidth;
				}
			}
		}


		// FLYING ENEMIES
		// each enemy only moves left to right on one row back and forth
		for (int row = 1; row < enemyLoc.length-1; row++) {
			int startX = actualWidth; // start of enemy in that area
			int endX = 0; // end of enemy in that area
			int enemyY = 0; // y position of enemy

			for (int col = 1; col < enemyLoc[row].length-1; col++) {
				int x = (col-1) * tileWidth;
				int y = (row-1) * tileHeight+16;

				// has enemy
				if (enemyLoc[row][col] == 2) {
					startX = Math.min(startX, x);
					endX = Math.max(endX, x);
					enemyY = y;
				}

				// no enemy in current tile, but previous tile would've had enemy
				else if (endX > 0) {
					// create new enemy and set ending/starting positions
					Enemy e = new Enemy (startX, enemyY, 52, 48, 2);
					e.setEndX(endX);
					e.setStartX(startX);

					// add enemy to arraylist
					enemies.add(e);

					// reset end/start for next enemy
					endX = 0;
					startX = actualWidth;
				}
			}
		}
	}

	// Description: make sure player can't walk out of the bounds
	// Parameters: none
	// Return: void
	public void keepInBound() {
		if(player.getX() < 0)
			player.setX(0);
		else if(player.getX() > actualWidth - player.getWidth())
			player.setX(actualWidth - player.getWidth());
	}

	public static void main(String[] args) throws IOException {
		JFrame frame = new JFrame ("blob!");
		Driver myPanel = new Driver ();
		frame.add(myPanel);
		frame.addKeyListener(myPanel);
		frame.addMouseListener(myPanel);
		frame.addMouseMotionListener(myPanel);
		frame.addMouseWheelListener(myPanel);
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		int k = e.getKeyCode();

		if (showRecipe)
			if (k == KeyEvent.VK_ESCAPE)
				showRecipe = false;

		if (showAbout)
			if (k == KeyEvent.VK_ESCAPE)
				showAbout = false;

		if (showRules)
			if (k == KeyEvent.VK_ESCAPE)
				showRules = false;

		if (k == KeyEvent.VK_ESCAPE && gamestate == PLAYING && !failed && !passed) {
			if (paused)
				paused = false;
			else {
				paused = true;
				dropScreenY = -screenHeight;
			}
		}

		if (gamestate == PLAYING && !failed && !passed && !paused) {
			if (k == KeyEvent.VK_A) {
				direction = -1;
				player.setLeft(true);

			}
			if (k == KeyEvent.VK_D) {
				direction = 1;
				player.setRight(true);

			}
			if (k == KeyEvent.VK_S) {
				player.setDown(true);

			}

			if (k == KeyEvent.VK_W) {
				if (player.getJumpCount() < 2) {
					player.setJumping(true);
					player.setJumpCount(player.getJumpCount() + 1);
				}
			}

			updateLedge();

		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_A) {
			//			player.notOnGround = true;
			player.setLeft(false);
		}

		else if(key == KeyEvent.VK_D) {
			//			player.notOnGround = true;
			player.setRight(false);
		}

		else if(key == KeyEvent.VK_W) {
			player.setUp(false);
		}

		else if(key == KeyEvent.VK_S) {
			player.setDown(false);
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public void mouseClicked(MouseEvent e) {

		int x = e.getX();
		int y = e.getY();

		// start button
		if (gamestate == HOME && x > 420 && x < 740 && y > 570 && y < 630 && !showRules && !showAbout) {
			gamestate = MENU;
			clickSound.setFramePosition(0);
			clickSound.start();
		}

		// rules button
		else if (gamestate == HOME && x > 420 && x < 740 && y > 690 && y < 750 && !showRules && !showAbout) {
			showRules = true;
			rulesPage = 0;
			clickSound.setFramePosition(0);
			clickSound.start();
		}

		// about button
		else if (gamestate == HOME && x > 20 && x < 120 && y > 20 && y < 120 && !showRules && !showAbout) {
			showAbout = true;
			clickSound.setFramePosition(0);
			clickSound.start();
		}

		else if (gamestate == MENU && !showRecipe) {
			// scroll up
			if (x > 410 && x < 805 && y > 665 && y < 680) {
				viewLoc += 5;
				viewLoc = Math.max(viewLoc, 0);
				viewLoc = Math.min(viewLoc, menuHeight-430);
				clickSound.setFramePosition(0);
				clickSound.start();
			}

			// scroll down
			else if (x > 410 && x < 805 && y > 200 && y < 215) {
				viewLoc -= 5;
				viewLoc = Math.max(viewLoc, 0);
				viewLoc = Math.min(viewLoc, menuHeight-430);
				clickSound.setFramePosition(0);
				clickSound.start();
			}

			// sort by name
			else if (x > 410 && x < 600 && y > 176 && y < 196) {

				if (!nameReverse)
					Collections.sort(menuItems);
				else
					Collections.sort(menuItems, Collections.reverseOrder());

				nameReverse = !nameReverse;

				clickSound.setFramePosition(0);
				clickSound.start();
			}

			// sort by diff
			else if (x > 615 && x < 805 && y > 176 && y < 196) {

				if (!diffReverse)
					Collections.sort(menuItems, new SortByDifficulty());
				else {
					Comparator<Item> c = Collections.reverseOrder(new SortByDifficulty());
					Collections.sort(menuItems, c);

				}

				diffReverse = !diffReverse;

				clickSound.setFramePosition(0);
				clickSound.start();
			}



			// exit
			else if (x > 410 && x < 444 & y > 132 && y < 169) {
				gamestate = HOME;
				clickSound.setFramePosition(0);
				clickSound.start();
			}

			// click on an item
			else if (x > 420 && x < 795 && y > 230 && y < 655) {
				// y + viewLoc

				if ((y+viewLoc-204)%150 > 25) {
					currentRecipe = (y+viewLoc-204)/150;
					showRecipe = true;

					clickSound.setFramePosition(0);
					clickSound.start();
				}

			}

		}

		else if (showRecipe) {
			// x button
			if (x > 85 && x < 85+38 && y > 195 && y < 195+38) {
				showRecipe = false;

				clickSound.setFramePosition(0);
				clickSound.start();
			}

			else {
				try {
					newLevel(itemsMap.get(menuItems.get(currentRecipe)));
				} 

				catch (IOException e1) {
					e1.printStackTrace();
				}

				gamestate = PLAYING;

				clickSound.setFramePosition(0);
				clickSound.start();
			}


		}


		else if (showRules) {
			// x button
			if (x > 85 && x < 85+38 && y > 195 && y < 195+38) {
				showRules = false;
			}

			else if (rulesPage < 11)
				rulesPage++;

			// end of rules -> close rules
			else {
				showRules = false;
				rulesPage = 0;
			}

			clickSound.setFramePosition(0);
			clickSound.start();
		}

		else if (showAbout) {
			// x button
			if (x > 85 && x < 85+38 && y > 195 && y < 195+38) {
				showAbout = false;

				clickSound.setFramePosition(0);
				clickSound.start();
			}
		}
		else if (gamestate == PLAYING && failed) {
			// back
			if (x > 850 && x < 1030 && y > 520 && y < 550) {
				gamestate = MENU;

				clickSound.setFramePosition(0);
				clickSound.start();
			}

			// retry
			else if (x > 190 && x < 400 && y > 520 && y < 550) {
				failed = false;
				try {
					newLevel(itemsMap.get(menuItems.get(currentRecipe)));
				} 

				catch (IOException e1) {
				}

				gamestate = PLAYING;

				clickSound.setFramePosition(0);
				clickSound.start();
			}
		}

		else if (gamestate == PLAYING && paused) {
			// quit
			if (x > 860 && x < 1030 && y > 510 && y < 540) {
				gamestate = MENU;

				clickSound.setFramePosition(0);
				clickSound.start();
			}


			// resume
			else if (x > 519 && x < 757 && y > 510 && y < 540) {
				paused = false;
				clickSound.setFramePosition(0);
				clickSound.start();
			}

			// restart
			else if (x > 150 && x < 420 && y > 510 && y < 540) {
				try {
					newLevel(itemsMap.get(menuItems.get(currentRecipe)));
				} 

				catch (IOException e1) {
				}

				gamestate = PLAYING;

				clickSound.setFramePosition(0);
				clickSound.start();
			}
		}

		// passed -> go back to menu
		else if (gamestate == PLAYING && passed) {
			gamestate = MENU;

			clickSound.setFramePosition(0);
			clickSound.start();
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		if (gamestate == MENU) {
			if (x > 410 && x < 805 && y > 665 && y < 680)
				heldDown = true;

			else if (x > 410 && x < 805 && y > 200 && y < 215)
				heldUp = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		heldDown = false;
		heldUp = false;

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {

		int x = e.getX();
		int y = e.getY();

		if (gamestate == HOME && x > 420 && x < 740 && y > 570 && y < 630) {
			titleOption = 1;
		}

		else if (gamestate == HOME && x > 420 && x < 740 && y > 690 && y < 750) {
			titleOption = 2;
		}

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();

		viewLoc += 20*notches;
		viewLoc = Math.max(viewLoc, 0);
		viewLoc = Math.min(viewLoc, menuHeight-430);

	}
}
