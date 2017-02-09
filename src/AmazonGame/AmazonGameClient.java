package AmazonGame;

import AmazonBoard.AmazonSquare;
import ygraphs.ai.smart_fox.games.*;

/**
 * Created by D on 2/8/2017.
 */
public class AmazonGameClient extends GameClient {

    public static final String GAME_START = "cosc322.game-action.start";
    public static final String GAME_MOVE = "cosc322.game-action.move";

    public AmazonGameClient(String handle, String passwd) {
        super(handle, passwd);
    }

    public AmazonGameClient(String handle, String passwd, GamePlayer delegate) {
        super(handle, passwd, delegate);
    }

    public void sendMoveMessage(AmazonSquare amazonInitial, AmazonSquare amazonFinal, AmazonSquare arrow) {

        int[] qf = new int[2];
        qf[0] = amazonInitial.getPosY();
        qf[1] = amazonInitial.getPosX();

        int[] qn = new int[2];
        qn[0] = amazonFinal.getPosY();
        qn[1] = amazonFinal.getPosY();

        int[] ar = new int[2];
        ar[0] = arrow.getPosY();
        ar[1] = arrow.getPosX();

        sendMoveMessage(qf, qn, ar);
    }

}
