package AmazonEvaluator;

import AmazonBoard.AmazonBoard;
import AmazonBoard.AmazonSquare;

import java.util.ArrayList;

/**
 * Created by D on 2/12/2017.
 */
public class BestMobilityEvaluator extends AmazonEvaluator {

    /**
     * Evaluates the board based on the best difference between max mobility and current mobility
     *
     * @param board The board to evaluate
     * @return
     */

    @Override
    public AmazonMove evaluateBoard(AmazonBoard board) {
        this.board = board;

        AmazonMove move = null;

        while (move == null) {

            AmazonSquare sInit = null, sFinal = null, arrow;

            //Find the position with the highest mobility, and the respective queen

            ArrayList<AmazonSquare> queens = board.getQueenList(getColor());

            int highValue = -100; //potential difference of mobilities can be < 0, but will never exceed -100

            for (AmazonSquare queen : queens) {

                ArrayList<AmazonSquare> moves = board.getBoardCalculator().generateListOfValidMoves(queen);

                if (moves.size() < 1) continue; //Ignore queen if there are not valid moves

                for (AmazonSquare square : moves) {
                   // System.out.println("Mobility of " + square.toString() + ": " + (square.getMobility()-queen.getMobility()) + " vs " + highValue);

                    // Save the squares with the best potential mobility difference
                    if (square.getMobility()-queen.getMobility() > highValue) {
                        highValue = (square.getMobility() - queen.getMobility());
                        sFinal = square;
                        sInit = queen;
                    }
                }
            }

           // System.out.println("Max mobility found for " + sInit.toString() + " at " + sFinal.toString() + " with " + sFinal.getMobility());

            arrow = getRandomShot(sInit, sFinal);

            if (arrow == null) continue;

           // System.out.println("Shooting arrow to " + arrow.toString());

            move = new AmazonMove(sInit, sFinal, arrow);
            // if (!board.isMoveValid(move)) continue;

        }

        return move;
    }
}
