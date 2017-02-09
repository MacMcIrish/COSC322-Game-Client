package AmazonGame;

import AmazonBoard.AmazonSquare;
import ygraphs.ai.smart_fox.GameMessage;
import ygraphs.ai.smart_fox.games.BoardGameModel;
import ygraphs.ai.smart_fox.games.GameClient;
import ygraphs.ai.smart_fox.games.GamePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by D on 1/26/2017.
 */
public abstract class AmazonPlayer extends GamePlayer {

    GameClient client;

    public AmazonPlayer(GameClient client) {
        this.client = client;
    }

    @Override
    public void onLogin() {
        ArrayList<String> rooms = client.getRoomList();
        this.client.joinRoom(rooms.get(0)); //TODO: should be able to choose which room to join
    }

    abstract void handleMouseClick(int posX, int posY); //blank method if computer player

    public void loadBoard() {

    }

    abstract void handleOpponentMove(Map<String, Object> msgDetails);

    abstract int[] findMove(); //blank if human player

    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails){

        if(messageType.equals(GameMessage.GAME_ACTION_START)){

            if(((String) msgDetails.get("player-black")).equals(this.userName())){
                System.out.println("Game State: " +  msgDetails.get("player-black"));
            }

        }
        else if(messageType.equals(GameMessage.GAME_ACTION_MOVE)){
            handleOpponentMove(msgDetails);
        }
        return true;
    }
}
