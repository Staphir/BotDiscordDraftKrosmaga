/**
 * Created by Maxime on 01/06/2017.
 */
public class Card {

    private String name;
    private String rarity;

    public int quantity;

    public Card(String name, String rarity, int quantity) {
        this.name = name;
        this.rarity = rarity;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getRarity() {
        return rarity;
    }

    public int getQuantity() {
        return quantity;
    }
}
