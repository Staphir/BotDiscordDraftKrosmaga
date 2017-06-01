import java.util.ArrayList;

/**
 * Created by Maxime on 01/06/2017.
 */
public class Player {

    private String name;
    private String classe;

    public int idPlayer;
    public ArrayList<Card> playerCards;

    public Player(String name, int id, String classe) {
        this.name = name;
        this.idPlayer = id;
        this.classe = classe;
        this.playerCards = new ArrayList<>();
    }

    public void addCard(Card card){
        playerCards.add(card);
    }

    public String getListCards(){
        String cartesClasse = "";
        for(int i=0; i<15; i++){
            cartesClasse += "\n" + playerCards.get(i).getName();
        }

        String cartesNeutres = "";
        for(int i=15; i<playerCards.size();i++){
            cartesNeutres += "\n" + playerCards.get(i).getName();
        }

        return " Cartes " + classe + " :" + cartesClasse + "\nCartes neutres :" + cartesNeutres;
    }

}
