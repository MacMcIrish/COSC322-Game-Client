package AmazonGame;

import ygraphs.ai.smart_fox.games.GameClient;

import java.util.Map;

/**
 * Created by D on 2/9/2017.
 */
public class HumanPlayer extends AmazonPlayer {

    public HumanPlayer(GameClient client) {
        super(client);
    }

    @Override
    void handleMouseClick(int posX, int posY) {

    }

    @Override
    void handleOpponentMove(Map<String, Object> msgDetails) {

    }

    @Override
    int[] findMove() {
        return new int[0];
    }

    @Override
    public void onLogin() {

    }

    @Override
    public String userName() {
        return null;
    }
}
