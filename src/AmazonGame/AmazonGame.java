package AmazonGame;

import AmazonBoard.AmazonBoard;
import AmazonUI.AmazonUI;

/**
 * Created by D on 2/8/2017.
 */
public class AmazonGame  {

    AmazonGameClient client;
    AmazonUI amazonUI;
    AmazonBoard board;


    public AmazonGame() {

        AmazonUI amazonUI = new AmazonUI(this);
        AmazonBoard board = new AmazonBoard();
        AmazonPlayer player = null; // = new AmazonPlayer();

    }

    public static void main(String[] args) {

        AmazonGame game = new AmazonGame();
    }

    /**
     * Joins the game with a selected player
     * @param player The string class name of the player to be played
     */
    public void joinGame(String player) {

        System.out.println("Joining game with player: " + player);

        client = new AmazonGameClient(player,"Password", new HumanPlayer(null));//new AmazonPlayer());

    }

}
