import java.util.ArrayList;

/**
 * Created by Maxime on 01/06/2017.
 */
public class Booster {

    public int idBooster;
    public ArrayList<Card> boosterCards;

    public Booster(int id, ArrayList<Card> cards) {
        this.idBooster = id;
        this.boosterCards = new ArrayList<>();
        this.boosterCards = cards;
    }

    public String getNameCard(int numCarte){
        if(numCarte < boosterCards.size())
        {
            return numCarte + ". " + boosterCards.get(numCarte).getName();
        }else
        {
            return "La carte n°" + numCarte + "n'existe pas";
        }
    }

    public void suppCard(int numCarte){
        if(numCarte < boosterCards.size()) {
            boosterCards.remove(numCarte);
        }
    }
}
