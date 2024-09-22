import java.awt.image.BufferedImage;

public class Item implements Comparable<Item> {

	private BufferedImage preview;
	private BufferedImage recipe;
	private int stars;
	private int numFruits;
	private String name;
	
	public Item(String name, int stars, int numFruits, BufferedImage preview, BufferedImage recipe) {
		this.name = name;
		this.stars = stars;
		this.numFruits = numFruits;
		this.preview = preview.getSubimage(0, 96, preview.getWidth(), preview.getHeight()-97);
		this.recipe = recipe;
	}


	public int compareTo(Item i) {
		return this.name.compareTo(i.name);
	}



	public BufferedImage getPreview() {
		return preview;
	}


	public void setPreview(BufferedImage preview) {
		this.preview = preview;
	}


	public BufferedImage getRecipe() {
		return recipe;
	}


	public void setRecipe(BufferedImage recipe) {
		this.recipe = recipe;
	}


	public int getNumFruits() {
		return numFruits;
	}


	public void setNumFruits(int numFruits) {
		this.numFruits = numFruits;
	}
}
