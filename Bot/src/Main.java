/**
 * Created by Maxime on 01/06/2017.
 */
/*
 *     Copyright 2015-2016 Austin Keener & Michael Ritter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Integer.parseInt;

public class Main extends ListenerAdapter
{

    /****************Draft***************/
    public ArrayList<Card> bdCards;
    private String fichierBD;

    private boolean stepBD = false;
    private boolean stepNbPlayers = false;
    private boolean stepNamePlayers = false;
    private boolean stepWaitClasse = false;
    private boolean stepWaitBoosters = true;

    private int waitChooseCard;

    private ArrayList<User> listUsers;
    private ArrayList<Player> listPlayers;
    private int nbPlayers;
    private int gamePlayers;

    private int boosterCreated;
    /***********************************/

    /**
     * This is the method where the program starts.
     */
    public static void main(String[] args)
    {

        //We construct a builder for a BOT account. If we wanted to use a CLIENT account
        // we would use AccountType.CLIENT
        try
        {
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken("MjQ2MDA4ODI5MTMxNzUxNDI0.C2U8EQ.0wSKoPwzJ9-XuFRY-5ANO0rUkNk")           //The token of the account that is logging in.
                    .addEventListener(new Main())  //An instance of a class that will handle events.
                    .buildBlocking();  //There are 2 ways to login, blocking vs async. Blocking guarantees that JDA will be completely loaded.
        }
        catch (LoginException e)
        {
            //If anything goes wrong in terms of authentication, this is the exception that will represent it
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            //Due to the fact that buildBlocking is a blocking method, one which waits until JDA is fully loaded,
            // the waiting can be interrupted. This is the exception that would fire in that situation.
            //As a note: in this extremely simplified example this will never occur. In fact, this will never occur unless
            // you use buildBlocking in a thread that has the possibility of being interrupted (async thread usage and interrupts)
            e.printStackTrace();
        }
        catch (RateLimitedException e)
        {
            //The login process is one which can be ratelimited. If you attempt to login in multiple times, in rapid succession
            // (multiple times a second), you would hit the ratelimit, and would see this exception.
            //As a note: It is highly unlikely that you will ever see the exception here due to how infrequent login is.
            e.printStackTrace();
        }


    }

    /**
     * NOTE THE @Override!
     * This method is actually overriding a method in the ListenerAdapter class! We place an @Override annotation
     *  right before any method that is overriding another to guarantee to ourselves that it is actually overriding
     *  a method from a super class properly. You should do this every time you override a method!
     *
     * As stated above, this method is overriding a hook method in the
     * {@link net.dv8tion.jda.core.hooks.ListenerAdapter ListenerAdapter} class. It has convience methods for all JDA events!
     * Consider looking through the events it offers if you plan to use the ListenerAdapter.
     *
     * In this example, when a message is received it is printed to the console.
     *
     * @param event
     *          An event containing information about a {@link net.dv8tion.jda.core.entities.Message Message} that was
     *          sent in a channel.
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        //initialisations


        //These are provided with every event in JDA
        JDA jda = event.getJDA();                       //JDA, the core of the api.
        long responseNumber = event.getResponseNumber();//The amount of discord events that JDA has received since the last reconnect.

        //Event specific information
        User author = event.getAuthor();                  //The user that sent the message
        Message message = event.getMessage();           //The message that was received.
        MessageChannel channel = event.getChannel();    //This is the MessageChannel that the message was sent to.
        //  This could be a TextChannel, PrivateChannel, or Group!

        String msg = message.getContent();              //This returns a human readable version of the Message. Similar to
        // what you would see in the client.

        boolean bot = author.isBot();                     //This boolean is useful to determine if the User that
        // sent the Message is a BOT or not!

        if (event.isFromType(ChannelType.TEXT))         //If this message was sent to a Guild TextChannel
        {
            //Because we now know that this message was sent in a Guild, we can do guild specific things
            // Note, if you don't check the ChannelType before using these methods, they might return null due
            // the message possibly not being from a Guild!

            Guild guild = event.getGuild();             //The Guild that this message was sent in. (note, in the API, Guilds are Servers)
            TextChannel textChannel = event.getTextChannel(); //The TextChannel that this message was sent to.
            Member member = event.getMember();          //This Member that sent the message. Contains Guild specific information about the User!

            String name = member.getEffectiveName();    //This will either use the Member's nickname if they have one,
            // otherwise it will default to their username. (User#getName())

            System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), name, msg);
        }
        else if (event.isFromType(ChannelType.PRIVATE)) //If this message was sent to a PrivateChannel
        {
            //The message was sent in a PrivateChannel.
            //In this example we don't directly use the privateChannel, however, be sure, there are uses for it!
            PrivateChannel privateChannel = event.getPrivateChannel();

            System.out.printf("[PRIV]<%s>: %s\n", author.getName(), msg);
        }
        else if (event.isFromType(ChannelType.GROUP))   //If this message was sent to a Group. This is CLIENT only!
        {
            //The message was sent in a Group. It should be noted that Groups are CLIENT only.
            Group group = event.getGroup();
            String groupName = group.getName() != null ? group.getName() : "";  //A group name can be null due to it being unnamed.

            System.out.printf("[GRP: %s]<%s>: %s\n", groupName, author.getName(), msg);
        }


        //Now that you have a grasp on the things that you might see in an event, specifically MessageReceivedEvent,
        // we will look at sending / responding to messages!
        //This will be an extremely simplified example of command processing.

        //Remember, in all of these .equals checks it is actually comparing
        // message.getContent().equals, which is comparing a string to a string.
        // If you did message.equals() it will fail because you would be comparing a Message to a String!
        if (msg.equals("-ping"))
        {
            //This will send a message, "pong!", by constructing a RestAction and "queueing" the action with the Requester.
            // By calling queue(), we send the Request to the Requester which will send it to discord. Using queue() or any
            // of its different forms will handle ratelimiting for you automatically!
            author.openPrivateChannel().queue((channelPrivate) ->
            {
                channelPrivate.sendMessage("pong" + author.getId()).complete();
            });

        }
        else if (msg.equals("-roll"))
        {
            //In this case, we have an example showing how to use the Success consumer for a RestAction. The Success consumer
            // will provide you with the object that results after you execute your RestAction. As a note, not all RestActions
            // have object returns and will instead have Void returns. You can still use the success consumer to determine when
            // the action has been completed!

            Random rand = new Random();
            int roll = rand.nextInt(6) + 1; //This results in 1 - 6 (instead of 0 - 5)
            channel.sendMessage("Your roll: " + roll).queue(sentMessage ->  //This is called a lambda statement. If you don't know
            {                                                               // what they are or how they work, try google!
                if (roll < 3)
                {
                    channel.sendMessage(" wasn't very good... Must be bad luck!\n").queue();
                }
            });
        }
        else if (msg.startsWith("-kick"))   //Note, I used "startsWith, not equals.
        {
            //This is an admin command. That means that it requires specific permissions to use it, in this case
            // it needs Permission.KICK_MEMBERS. We will have a check before we attempt to kick members to see
            // if the logged in account actually has the permission, but considering something could change after our
            // check we should also take into account the possibility that we don't have permission anymore, thus Discord
            // response with a permission failure!
            //We will use the error consumer, the second parameter in queue!

            //We only want to deal with message sent in a Guild.
            if (message.isFromType(ChannelType.TEXT))
            {
                //If no users are provided, we can't kick anyone!
                if (message.getMentionedUsers().isEmpty())
                {
                    channel.sendMessage("You must mention 1 or more Users to be kicked!").queue();
                }
                else
                {
                    Guild guild = event.getGuild();
                    Member selfMember = guild.getSelfMember();  //This is the currently logged in account's Member object.
                    // Very similar to JDA#getSelfUser()!

                    //Now, we the the logged in account doesn't have permission to kick members.. well.. we can't kick!
                    if (!selfMember.hasPermission(Permission.KICK_MEMBERS))
                    {
                        channel.sendMessage("Sorry! I don't have permission to kick members in this Guild!").queue();
                        return; //We jump out of the method instead of using cascading if/else
                    }

                    //Loop over all mentioned users, kicking them one at a time. Mwauahahah!
                    List<User> mentionedUsers = message.getMentionedUsers();
                    for (User user : mentionedUsers)
                    {
                        Member member = guild.getMember(user);  //We get the member object for each mentioned user to kick them!

                        //We need to make sure that we can interact with them. Interacting with a Member means you are higher
                        // in the Role hierarchy than they are. Remember, NO ONE is above the Guild's Owner. (Guild#getOwner())
                        if (!selfMember.canInteract(member))
                        {
                            channel.sendMessage("Cannot kicked member: " + member.getEffectiveName() +", they are higher " +
                                    "in the hierachy than I am!").queue();
                            continue;   //Continue to the next mentioned user to be kicked.
                        }

                        //Remember, due to the fact that we're using queue we will never have to deal with RateLimits.
                        // JDA will do it all for you so long as you are using queue!
                        guild.getController().kick(member).queue(
                                success -> channel.sendMessage("Kicked " + member.getEffectiveName() + "! Cya!").queue(),
                                error ->
                                {
                                    //The failure consumer provides a throwable. In this case we want to check for a PermissionException.
                                    if (error instanceof PermissionException)
                                    {
                                        PermissionException pe = (PermissionException) error;
                                        Permission missingPermission = pe.getPermission();  //If you want to know exactly what permission is missing, this is how.
                                        //Note: some PermissionExceptions have no permission provided, only an error message!

                                        channel.sendMessage("PermissionError kicking [" + member.getEffectiveName()
                                                + "]: " + error.getMessage()).queue();
                                    }
                                    else
                                    {
                                        channel.sendMessage("Unknown error while kicking [" + member.getEffectiveName()
                                                + "]: " + "<" + error.getClass().getSimpleName() + ">: " + error.getMessage()).queue();
                                    }
                                });
                    }
                }
            }
            else
            {
                channel.sendMessage("This is a Guild-Only command!").queue();
            }
        }
        else if (msg.equals("-block"))
        {
            //This is an example of how to use the complete() method on RestAction. The complete method acts similarly to how
            // JDABuilder's buildBlocking works, it waits until the request has been sent before continuing execution.
            //Most developers probably wont need this and can just use queue. If you use complete, JDA will still handle ratelimit
            // control, however if shouldQueue is false it won't queue the Request to be sent after the ratelimit retry after time is past. It
            // will instead fire a RateLimitException!
            //One of the major advantages of complete() is that it returns the object that queue's success consumer would have,
            // but it does it in the same execution context as when the request was made. This may be important for most developers,
            // but, honestly, queue is most likely what developers will want to use as it is faster.

            try
            {
                //Note the fact that complete returns the Message object!
                //The complete() overload queues the Message for execution and will return when the message was sent
                //It does handle rate limits automatically
                Message sentMessage = channel.sendMessage("I blocked and will return the message!").complete();
                //This should only be used if you are expecting to handle rate limits yourself
                //The completion will not succeed if a rate limit is breached and throw a RateLimitException
                Message sentRatelimitMessage = channel.sendMessage("I expect rate limitation and know how to handle it!").complete(false);

                System.out.println("Sent a message using blocking! Luckly I didn't get Ratelimited... MessageId: " + sentMessage.getId());
            }
            catch (RateLimitedException e)
            {
                System.out.println("Whoops! Got ratelimited when attempting to use a .complete() on a RestAction! RetryAfter: " + e.getRetryAfter());
            }
            //Note that RateLimitException is the only checked-exception thrown by .complete()
            catch (RuntimeException e)
            {
                System.out.println("Unfortunately something went wrong when we tried to send the Message and .complete() threw an Exception.");
                e.printStackTrace();
            }
        }


        /***********************************************************Commandes Draft***************************************************************/

        else if(msg.equals("-viewCards")){
            for(int i=0; i<listUsers.size(); i++){
                //trouver emplacement de l'auteur dans la liste
                if(author.getName().equals(listPlayers.get(i).getName())){
                    int finalI = i;
                    author.openPrivateChannel().queue((privateChannel)->{
                        //affichage liste des cartes
                        privateChannel.sendMessage(listPlayers.get(finalI).getListCards()).complete();
                    });
                }
            }
        }

        //3ème étape : "inscription" des joueurs et début du draft (MP pour règles + premiers boosters)
        else if(msg.length()>=8) {
            if (msg.substring(0,7).equals("-player")) {
                //partie à commence ?
                if (stepBD) {
                    //nb player donné ?
                    if (stepNbPlayers) {
                        //plus de place dans la partie ?
                        if (nbPlayers == 0) {
                            channel.sendMessage("La partie est déjà complète").queue();
                            return;
                        }
                        listUsers = new ArrayList<>();
                        listPlayers = new ArrayList<>();
                        //ajout dans la liste des authors
                        listUsers.add(author);
                        //nouveau joueur dans la liste des joueurs---------------nom de la classe donnée après -player
                        listPlayers.add(new Player(author.getName(), listUsers.size(), msg.substring(8)));
                        //une place de moins dans la partie
                        nbPlayers--;
                        //partie remplie début du draft
                        if (nbPlayers == 0) {
                            channel.sendMessage("Les " + listPlayers.size() + " joueurs sont prêts, les instructions vont vous être envoyé par message perso").queue();

                            /*****début draft************/

                            //envoie à chaque joueur la demande de cartes de la classe qu'ils veulent jouer
                            for(int i=0; i<listPlayers.size(); i++)
                            {
                                listUsers.get(i).openPrivateChannel().queue((privateChannel) ->
                                {
                                    privateChannel.sendMessage("donner la liste de vos cartes de la classe choisi avec : -classe [fichier.csv]").complete();
                                });
                            }
                            stepWaitClasse =true;

                            /****************************/
                        }

                    } else {
                        channel.sendMessage("Vous n'avez pas choisi le nombre de joueurs avec la commande : -nbPlayers [nombre de joueurs]").queue();
                    }
                } else {
                    channel.sendMessage("Commencé par démarré un draft avec la commande : -beginDraft [fichier.csv]").queue();
                }
            }

            //----------------------------tous les joueurs se sont inscrit et la demande de carte de classe est envoyée
            if(msg.substring(0,7).equals("-classe") && stepWaitClasse == true){
                //bon type de fichier ( .csv ) ?
                if(msg.substring(msg.length()-4).equals(".csv")){
                    for(int i=0; i<listPlayers.size(); i++){
                        //le joueur est dans la partie ?
                        if(listPlayers.get(i).getName().equals(author.getName())){

                            //création liste cartes classe d'un joueur
                            ArrayList<Card> listCardTmp = new ArrayList<>();
                            //lecture du fichier
                            try {
                                String chemin = msg.substring(8);
                                BufferedReader fichier_source = new BufferedReader(new FileReader(chemin));
                                String chaine;
                                int j = 1;

                                while ((chaine = fichier_source.readLine()) != null) {
                                    if (j >= 1) {
                                        String[] tabChaine = chaine.split(";");
                                        //création de carte le nombre de fois qu'elle existe
                                        for(int k=0; k<Integer.parseInt(tabChaine[2]); k++) {
                                            //nouvelle carte dans la liste de toutes les cartes de classe possible pour le joueur
                                            listCardTmp.add(new Card(tabChaine[0], tabChaine[1]));
                                        }
                                    }
                                    j++;
                                }
                                fichier_source.close();

                            } catch (IOException e) {
                                channel.sendMessage("Probleme import csv").queue();
                                return;
                            }

                            //remplissage aléatoire de la liste de cartes du joueur
                            for(int l=0; l<15; l++){
                                //tirage carte aléatoire parmis la liste donnée par le joueur
                                Card tmpCard = tirageCarte(listCardTmp);
                                //ajout de la carte dans la liste du joueur
                                listPlayers.get(i).addCard(tmpCard);
                                //parcours de toutes les cartes de la liste donnée par le joueur pour supprimer celle qui vient d'être ajouté
                                for(int m=0; m<listCardTmp.size(); m++){
                                    if(listCardTmp.get(m).getName().equals(tmpCard.getName())){
                                        listCardTmp.remove(m);
                                        m=listCardTmp.size();
                                    }
                                }
                            }
                            int finalI = i;
                            listUsers.get(i).openPrivateChannel().queue((privateChannel) -> {
                                privateChannel.sendMessage(listPlayers.get(finalI).getListCards()).complete();
                            });
                            break;
                        }
                        channel.sendMessage("Dans la boucle").queue();
                    }
                }else{
                    channel.sendMessage("La commande doit être de la forme : -classe [fichier.csv]").queue();
                }
            }

            else if(msg.length()>=10) {

                //2ème étape : choix du nombre de joueurs
                if (msg.substring(0, 10).equals("-nbPlayers")) {
                    if (stepBD) {
                        if (msg.length() > 10) {
                            nbPlayers = parseInt(msg.substring(11));
                            stepNbPlayers = true;
                            channel.sendMessage("Début des inscriptions\nChaque joueur doit s'inscrire en donnant ça classe avec : -player [classe]").queue();
                        } else {
                            channel.sendMessage("Donnez le nombre de joueurs avec la commande : -nbPlayers [nombre de joueurs]").queue();
                        }
                    } else {
                        channel.sendMessage("Commencé par démarré un draft avec la commande : -beginDraft [fichier.csv]").queue();
                    }
                } else if (msg.length() >= 11) {

                    //1ère étape : choix du .csv
                    if (msg.substring(0, 11).equals("-beginDraft")) {
                        //bon format de fichier ?
                        if (("" + msg.substring(msg.length() - 4)).equals(".csv")) {

                            bdCards = new ArrayList<>();
                            //lecture .csv pour remplir bdCards
                            try {
                                String chemin = msg.substring(12);
                                BufferedReader fichier_source = new BufferedReader(new FileReader(chemin));
                                String chaine;
                                int i = 1;

                                while ((chaine = fichier_source.readLine()) != null) {
                                    if (i >= 1) {
                                        String[] tabChaine = chaine.split(";");
                                        for(int k=0; k<Integer.parseInt(tabChaine[2]); k++) {
                                            bdCards.add(new Card(tabChaine[0], tabChaine[1]));
                                        }
                                    }
                                    i++;
                                }
                                fichier_source.close();
                                channel.sendMessage("BD de cartes OK").queue();

                                channel.sendMessage("Donnez le nombre de joueurs avec la commande : -nbPlayers [nombre de joueurs]").queue();
                                stepBD = true;
                            } catch (IOException e) {
                                channel.sendMessage("Probleme import csv").queue();
                                return;
                            }
                        } else {
                            channel.sendMessage("La commande doit être de la forme : -beginDraft [fichier.csv]").queue();
                        }
                    }
                }
            }
        }
    }

    public Card tirageCarte(ArrayList<Card> listCard)
    {

        String rareté = "";
        int nbRarete = -1;

        int nbCommun =  getNbRarete("C", listCard);
        int nbUncomm =  getNbRarete("U", listCard);
        int nbRare =  getNbRarete("R", listCard);
        int nbKros =  getNbRarete("K", listCard);
        int nbInfi =  getNbRarete("I", listCard);

        while(nbRarete <= 0) {
            //tirage rareté
            int tirageRareté = (int) (Math.random() * 100);

            if (tirageRareté < 30 && nbCommun != 0) {
                rareté = "C";
                nbRarete = nbCommun;
            }
            if (tirageRareté > 29 && tirageRareté < 60 && nbUncomm != 0) {
                rareté = "U";
                nbRarete = nbUncomm;
            }
            if (tirageRareté > 59 && tirageRareté < 75 && nbRare != 0) {
                rareté = "R";
                nbRarete = nbRare;
            }
            if (tirageRareté > 74 && tirageRareté < 90 && nbKros != 0) {
                rareté = "K";
                nbRarete = nbKros;
            }
            if (tirageRareté > 89 && nbInfi != 0) {
                rareté = "I";
                nbRarete = nbInfi;
            }
        }
        //list tmp de cartes de la rareté tiré aléatoirement
        ArrayList<Card> listTmp = new ArrayList<>();
        for(int i=0; i<listCard.size(); i++){
            if(listCard.get(i).getRarity().equals(rareté)){
                listTmp.add(listCard.get(i));
            }
        }
        //tirage carte
        Card choosedCard = listTmp.get((int)Math.random()*listTmp.size());

        return choosedCard;
    }

    public int getNbRarete(String rarete, ArrayList<Card> listCard){
        int nbRarete = 0;
        for(int i=0; i<listCard.size(); i++){
            if(listCard.get(i).getRarity().equals(rarete))nbRarete++;
        }
        return nbRarete;
    }
}
