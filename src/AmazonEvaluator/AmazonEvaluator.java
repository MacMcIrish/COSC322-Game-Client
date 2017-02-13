package AmazonEvaluator;
import AmazonBoard.*;

/**
 * Created by D on 2/12/2017.
 */
public abstract class AmazonEvaluator {

    AmazonBoard board;
    int playerColor = AmazonSquare.PIECETYPE_AMAZON_WHITE;

    public AmazonEvaluator() {}

    /**
     * This function does all the magic
     * @param board The board to evaluate
     * @return The optimal move for the board
     */
    public abstract AmazonMove evaluateBoard(AmazonBoard board);

    /**
     * Sets the color for the evaluator
     * TODO: should eventually be put in the constructor
     * @param color the color of the player
     */
    public void setColor(int color) {
        playerColor = color;
    }

    /**
     * Gets the color of the evaluator (player)
     * @return Color of the player
     */
    public int getColor() {
        return playerColor;
    }

}
