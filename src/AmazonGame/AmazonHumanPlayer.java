package AmazonGame;

import AmazonBoard.*;
import ygraphs.ai.smart_fox.games.GameClient;

import java.util.Map;

/**
 * Created by D on 2/9/2017.
 */
public class AmazonHumanPlayer extends AmazonPlayer {

    public AmazonHumanPlayer(String name, String password) {
        super(name, password);
    }

    @Override
    public void onLogin() {

    }

    @Override
    public boolean handleGameMessage(String s, Map<String, Object> map) {
        return false;
    }

    @Override
    public String userName() {
        return null;
    }

}
