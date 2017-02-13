package AmazonEvaluator;

import AmazonBoard.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by D on 2/12/2017.
 */
public class RandomEvaluator extends AmazonEvaluator {

    /**
     * Finds a random queen, selects a move from the list of available moves for that queen, then finds another available
     *  move from that position to shoot the arrow.
     * The function will fail if a queen is selected, but doesn't have any available moves
     * TODO: Add try/catch for handling no movement queens
     *
     * @param board The board to evaluate
     * @return The randomized move
     */
    @Override
    public AmazonMove evaluateBoard(AmazonBoard board) {
        this.board = board;

        AmazonSquare queen = getRandomQueen(playerColor);
        AmazonSquare moveTo = getRandomMove(queen);
        AmazonSquare arrow = getRandomMove(moveTo);

        return new AmazonMove(queen, moveTo, arrow);
    }

    /**
     * Finds a random queen on the board
     * @param color The color to get
     * @return The square of the random queen
     */
    public AmazonSquare getRandomQueen(int color) {

        ArrayList<AmazonSquare> list = board.getQueenList(color);
        return list.get((new Random()).nextInt(list.size()));

    }

    /**
     * Gets a random move (queen distance) from a square
     * @param s The square to get the move from
     * @return The randomly selected square from the list of available moves for that square
     */
    public AmazonSquare getRandomMove(AmazonSquare s) {

        ArrayList<AmazonSquare> list = board.generateListOfValidMoves(s);
        return list.get((new Random()).nextInt(list.size()));

    }

}
