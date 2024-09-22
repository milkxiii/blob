import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Level {

	private int[][] layout;	
	private int[][] fruits;
	final private int[][] templateFruits;
	private int[][] enemies;
	
	public Level (String fileName) throws FileNotFoundException {
		Scanner inFile = new Scanner (new File (fileName));
		
		// first two lines are number of rows and columns
		int rows = Integer.parseInt(inFile.nextLine());
		int cols = Integer.parseInt(inFile.nextLine());
		
		layout = new int[rows][cols];
		fruits = new int[rows][cols];
		templateFruits = new int[rows][cols];
		enemies = new int[rows][cols];
		
		inFile.nextLine();

		// next block is tiles
		for (int i = 0; i < rows; i++) {
			String line = inFile.nextLine();
			for (int j = 0; j < cols; j++) {
				layout[i][j] = Integer.parseInt(line.substring(0, 1));
				
				if (line.indexOf(" ") != -1)
					line = line.substring(2);
			}
		}
		
		inFile.nextLine();
		
		
		// next block is fruits
		for (int i = 0; i < rows; i++) {
			String line = inFile.nextLine();
			for (int j = 0; j < cols; j++) {
				fruits[i][j] = Integer.parseInt(line.substring(0, 1));
				templateFruits[i][j] = Integer.parseInt(line.substring(0, 1));
				
				if (line.indexOf(" ") != -1)
					line = line.substring(2);
			}
		}
		
		inFile.nextLine();
		
		
		// next block is enemies
		for (int i = 0; i < rows; i++) {
			String line = inFile.nextLine();
			for (int j = 0; j < cols; j++) {
				enemies[i][j] = Integer.parseInt(line.substring(0, 1));
				
				if (line.indexOf(" ") != -1)
					line = line.substring(2);
			}
		}

				
	}

	public int[][] getFruits() {
		return fruits;
	}

	public void resetFruits() {
		
		for (int i = 0; i < fruits.length; i++) {
			fruits[i] = Arrays.copyOf(templateFruits[i], templateFruits[i].length);
		}

	}

	public int[][] getLayout() {
		return layout;
	}

	public void setLayout(int[][] layout) {
		this.layout = layout;
	}

	public int[][] getEnemies() {
		return enemies;
	}

	public void setEnemies(int[][] enemies) {
		this.enemies = enemies;
	}

	public int[][] getTemplateFruits() {
		return templateFruits;
	}
}
