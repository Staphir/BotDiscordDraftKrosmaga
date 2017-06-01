/**
 * Created by Maxime on 01/06/2017.
 */
public class Card {

    private String name;
    private String rarity;

    public int id;

    public Card(String name, String rarity, int id) {
        this.name = name;
        this.rarity = rarity;
        this.id = id;
    }

    public String getName() {
        return name;
    }
}
