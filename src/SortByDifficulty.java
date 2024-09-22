import java.util.Comparator;

public class SortByDifficulty implements Comparator<Item> {

	@Override
	public int compare(Item i1, Item i2) {
		return (i1.getNumFruits() - i2.getNumFruits());
	}

}
