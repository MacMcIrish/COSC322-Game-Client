package AmazonGame;

import AmazonBoard.AmazonBoard;
import AmazonUI.AmazonUI;

/**
 * Created by D on 2/8/2017.
 */
public class AmazonGame  {

    public AmazonGame() {

        AmazonUI amazonUI = new AmazonUI();
        AmazonBoard board = new AmazonBoard();
        AmazonPlayer player = null; // = new AmazonPlayer();

        AmazonGameClient client = new AmazonGameClient("Name","Password", player);
    }

    public static void main(String[] args) {

        AmazonGame game = new AmazonGame();

    }


}
