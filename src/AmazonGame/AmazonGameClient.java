package AmazonGame;

import AmazonEvaluator.AmazonMove;
import ygraphs.ai.smart_fox.games.GameClient;
import ygraphs.ai.smart_fox.games.GamePlayer;

/**
 * Created by D on 2/8/2017.
 */
public class AmazonGameClient extends GameClient {

    public AmazonGameClient(String handle, String passwd, GamePlayer delegate) {
        super(handle, passwd, delegate);

    }

    /**
     * Packages up the move into the accepted format needed by the GameClient
     * @param move The move to be performed
     */
    public void sendMoveMessage(AmazonMove move) {

        int[] qf = new int[2];
        qf[0] = move.getInitial().getPosY();
        qf[1] = move.getInitial().getPosX();

        int[] qn = new int[2];
        qn[0] = move.getFinal().getPosY();
        qn[1] = move.getFinal().getPosX();

        int[] ar = new int[2];
        ar[0] = move.getArrow().getPosY();
        ar[1] = move.getArrow().getPosX();

        sendMoveMessage(qf, qn, ar);
    }

}
