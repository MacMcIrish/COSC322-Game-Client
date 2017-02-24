package AmazonGame;

import AmazonBoard.AmazonSquare;
import AmazonEvaluator.*;
import AmazonTest.AmazonAutomatedTest;
import ygraphs.ai.smart_fox.GameMessage;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.UUID;

/**
 * Created by D on 1/26/2017.
 */
public class AmazonAIPlayer extends AmazonPlayer {

    public AmazonAIPlayer(String name, String password, AmazonEvaluator evaluator) {

        super(name, password);
        this.evaluator = evaluator;
        amazonUI.setTitle(amazonUI.getTitle() + ", Type: " + getAIType());

    }

    /**
     * Responds to the messages sent by the server.
     * This class is only concerned with the game start and the move messages
     *
     * @param messageType The string of the message, from the GameClient class
     * @param msgDetails  The data contained in the string
     * @return Not sure, probably intended to say whether the event was consumed
     */
    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {

        System.out.println("Got message: " + messageType);

        if (messageType.equals(GameMessage.GAME_ACTION_START)) {

            //Set the evaluator to color, and execute first move if white
            if (((String) msgDetails.get("player-black")).equals(this.userName())) {
                evaluator.setColor(AmazonSquare.PIECETYPE_AMAZON_BLACK);
                amazonUI.setTitle(amazonUI.getTitle() + ", Black Player");
            } else {
                System.out.println("Is first player, finding move.");
                evaluator.setColor(AmazonSquare.PIECETYPE_AMAZON_WHITE);
                amazonUI.setTitle(amazonUI.getTitle() + ", White Player");
                takeTurn(); //This is the first move of the game
            }

        } else if (messageType.equals(GameMessage.GAME_ACTION_MOVE)) {

            respondToMove(msgDetails);
            if (checkForWinCondition()) return true;
            takeTurn();
           // if (checkForWinCondition()) return true;

        } else if (messageType.equals(GameMessage.GAME_STATE_PLAYER_LOST)) {

            System.out.println("Other player has conceded. Terminating Client");
            gameClient.logout();
            return true;

        }

        return true;

    }

    /**
     * Checks the board to see if a player has won, then logs out.
     * TODO: Need to change this based on the procedures for the competition
     * TODO: Kinda gross how it handles everything, should put the log out stuff on a button
     *
     * @return
     */
    private boolean checkForWinCondition() {

        if (board.getBoardCalculator().checkForWinCondition()) {

            endGame();
            return true;
        }

        return false;
    }

    public boolean endGame() {
        int[] score = board.getBoardCalculator().calculateScore();

        System.out.println("No more valid moves remain.");
        System.out.println("Final score: White - " + score[0] + ", Black - " + score[1]);

        boolean didIWin = score[evaluator.getColor() - 1] > score[Math.abs((evaluator.getColor() - 1) - 1)];

        if (didIWin) System.out.println(evaluator.getClass().getSimpleName() + " wins.");
        else System.out.println(evaluator.getClass().getSimpleName() + " lost.");

        System.out.println("Terminating client");
        gameClient.logout();

        return didIWin;

    }




    /**
     * Take the move data and applies it to the board
     *
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

        amazonUI.repaint();
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
     * If run without command line arguments, will default to the random AI
     * For command line arguments, look at the order of evaluators. On the command line, specify the index value for
     * the AI you wish to use.
     *
     * @param args int value of the evaluator you want to use
     */
    public static void main(String[] args) {

        String uuid = UUID.randomUUID().toString().substring(0, 10);

        //TODO: have the list of acceptable evaluators generated dynamically
        AmazonEvaluator[] evaluators = {new RandomEvaluator(), new MaxMobilityEvaluator(), new BestMobilityEvaluator()};

        int evaluator = 0; //Default is the random evaluator

        if (args.length != 0) evaluator = Integer.parseInt(args[0]);

        //TODO: replace this with a window that will allow you to select a different player
        AmazonAIPlayer p1 = new AmazonAIPlayer(uuid, uuid, evaluators[evaluator]);
        AmazonAIPlayer p2 = new AmazonAIPlayer(uuid+"2", uuid+"2", new RandomEvaluator());
        //AmazonAIPlayer p3 = new AmazonAIPlayer(uuid+"3", uuid+"3", new BestMobilityEvaluator());
    }

    @Override
    public String getAIType() {
        return evaluator.getClass().getSimpleName();
    }
}
