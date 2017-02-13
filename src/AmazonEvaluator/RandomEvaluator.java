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

        AmazonMove move = null;

        while (move == null) {

            AmazonSquare queen = getRandomQueen(playerColor);
            if (queen == null) continue;
            System.out.println("Selecting queen at " + queen.toString());

            AmazonSquare moveTo = getRandomMove(queen);
            if (moveTo == null) continue;
            System.out.println("Moving queen to " + moveTo.toString());

            AmazonSquare arrow = getRandomShot(queen, moveTo);
            if (arrow == null) continue;
            System.out.println("Shooting arrow to " + arrow.toString());

            move = new AmazonMove(queen, moveTo, arrow);
           // if (!board.isMoveValid(move)) continue;
        }

        return move;
    }

    /**
     * Finds a random queen on the board
     * @param color The color to get
     * @return The square of the random queen
     */
    public AmazonSquare getRandomQueen(int color) {

        ArrayList<AmazonSquare> list = board.getQueenList(color);

        if (list.size() < 1) return null;

        return list.get((new Random()).nextInt(list.size()));

    }

    /**
     * Gets a random move (queen distance) from a square
     * @param s The square to get the move from
     * @return The randomly selected square from the list of available moves for that square
     */
    public AmazonSquare getRandomMove(AmazonSquare square) {

        ArrayList<AmazonSquare> list = board.generateListOfValidMoves(square);

        if (list.size() < 1) return null;

        return list.get((new Random()).nextInt(list.size()));
    }

    public AmazonSquare getRandomShot(AmazonSquare queen, AmazonSquare arrow) {

        ArrayList<AmazonSquare> list = board.generateListOfValidShots(queen, arrow);

        if (list.size() < 1) return null;

        return list.get((new Random()).nextInt(list.size()));
    }
}
