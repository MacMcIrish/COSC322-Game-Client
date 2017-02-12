package AmazonGame;

import AmazonBoard.AmazonBoard;
import AmazonBoard.AmazonSquare;
import AmazonEvaluator.AmazonMove;
import AmazonUI.AmazonUI;
import ygraphs.ai.smart_fox.games.AmazonsGameMessage;
import ygraphs.ai.smart_fox.games.GamePlayer;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by D on 2/12/2017.
 */
public abstract class AmazonPlayer extends GamePlayer {

    AmazonGameClient gameClient;

    AmazonUI amazonUI;
    AmazonBoard board;
    String name = "team6", password = "team6";

    public AmazonPlayer(String name, String password) {

        this.name = name;
        this.password = password;
        board = new AmazonBoard();
        amazonUI = new AmazonUI(this);
        connectToServer(name, password);

    }


    public void connectToServer(String name, String passwd){
        System.out.println("Attempting to connect to server...");
        gameClient = new AmazonGameClient(name, passwd, this);
        System.out.println("Connected to server.");
    }

    public AmazonBoard getBoard() {return board;}

    @Override
    public String userName(){return name;}

    //handle the event that the opponent makes a move.
    public AmazonMove generateMoveFromMsg(Map<String, Object> msgDetails){
        ArrayList<Integer> qcurr = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR);
        ArrayList<Integer> qnew = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.Queen_POS_NEXT);
        ArrayList<Integer> arrow = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS);

        AmazonSquare sInit = getBoard().getSquare(qcurr.get(1),qcurr.get(0));
        AmazonSquare sFinal = getBoard().getSquare(qnew.get(1),qnew.get(0));
        AmazonSquare sArrow = getBoard().getSquare(arrow.get(1),arrow.get(0));

        AmazonMove move =new AmazonMove(sInit, sFinal, sArrow);

        return move;
    }



}
