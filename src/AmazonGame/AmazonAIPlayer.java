package AmazonGame;

import AmazonBoard.AmazonSquare;
import AmazonEvaluator.*;
import ygraphs.ai.smart_fox.GameMessage;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * Created by D on 1/26/2017.
 */
public class AmazonAIPlayer extends AmazonPlayer {

    AmazonEvaluator evaluator;

    public AmazonAIPlayer(String name, String password, AmazonEvaluator evaluator) {

        super(name, password);
        this.evaluator = evaluator;
        //connectToServer(name, password);
    }

    /**
     * Responds to the messages sent by the server.
     * This class is only concerned with the game start and the move messages
     *
     * @param messageType The string of the message, from the GameClient class
     * @param msgDetails The data contained in the string
     * @return Not sure, probably intended to say whether the event was consumed
     */
    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {

        System.out.println("Got message: " + messageType);

        if (messageType.equals(GameMessage.GAME_ACTION_START)) {

            //Set the evaluator to color, and execute first move if white
            if (((String) msgDetails.get("player-black")).equals(this.userName()))
                evaluator.setColor(AmazonSquare.PIECETYPE_AMAZON_BLACK);
            else {
                System.out.println("Is first player, finding move.");
                evaluator.setColor(AmazonSquare.PIECETYPE_AMAZON_WHITE);
                takeTurn(); //This is the first move of the game
            }

        } else if (messageType.equals(GameMessage.GAME_ACTION_MOVE)) { //TODO: remove move limit

            respondToMove(msgDetails);
            if (board.checkForWinCondition()) {

                int[] score = board.calculateScore();

                System.out.println("No more valid moves remain.");
                System.out.println("Final score: White - " + score[0] + ", Black - " + score[1]);
                System.out.println("Terminating client");
                gameClient.logout();
                return true;
            }

            takeTurn();
        } else if (messageType.equals(GameMessage.GAME_STATE_PLAYER_LOST)) {
            System.out.println("Other player has conceded. Terminating Client");
            gameClient.logout();
            return true;
        }






        return true;

    }

    /**
     * Take the move data and applies it to the board
     * @param msgDetails The data taken from the move event sent from the server
     */
    private void respondToMove(Map<String, Object> msgDetails) {
        AmazonMove gotMove = generateMoveFromMsg(msgDetails);
        System.out.println(System.currentTimeMillis() + ": Got move: " + gotMove.toString());

        try {
            board.executeMove(gotMove);
        } catch (InvalidMoveException e) {
            e.printStackTrace();
            return;
        }

        moveHistory.add(gotMove);
    }

    /**
     * Finds an acceptable move via the evaluation function, updates our board, then sends it to the opponent
     */
    private void takeTurn() {
        AmazonMove sentMove = evaluator.evaluateBoard(board);

        try {
            board.executeMove(sentMove);
        } catch (InvalidMoveException e) {
            e.printStackTrace();
            return;
        }

        System.out.println(System.currentTimeMillis() + ": Sending move: " + sentMove.toString());
        moveHistory.add(sentMove);
        amazonUI.repaint();
        gameClient.sendMoveMessage(sentMove);
    }

    /**
     * Run this method twice to create two instances of players
     * @param args Args do nothing
     */
    public static void main(String[] args) {

        String uuid = UUID.randomUUID().toString().substring(0, 5);

        //TODO: replace this with a window that will allow you to select a different player
        AmazonAIPlayer p1 = new AmazonAIPlayer(uuid, uuid, new RandomEvaluator());
    }

}
