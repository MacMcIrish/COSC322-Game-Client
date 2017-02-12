package AmazonEvaluator;

import AmazonBoard.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by D on 2/12/2017.
 */
public class RandomEvaluator extends AmazonEvaluator {

    @Override
    public AmazonMove evaluateBoard(AmazonBoard board) {
        this.board = board;

        AmazonSquare queen = getRandomQueen(playerColor);
        AmazonSquare moveTo = getRandomMove(queen);
        AmazonSquare arrow = getRandomMove(moveTo);

        return new AmazonMove(queen, moveTo, arrow);
    }

    public AmazonSquare getRandomQueen(int color) {

        ArrayList<AmazonSquare> list = board.getQueenList(color);
        return list.get((new Random()).nextInt(list.size()));

    }

    public AmazonSquare getRandomMove(AmazonSquare s) {

        ArrayList<AmazonSquare> list = board.generateListOfValidMoves(s);
        return list.get((new Random()).nextInt(list.size()));

    }

}
