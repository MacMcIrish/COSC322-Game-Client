package AmazonEvaluator;
import AmazonBoard.*;

/**
 * Created by D on 2/12/2017.
 */
public abstract class AmazonEvaluator {

    AmazonBoard board;
    int playerColor = AmazonSquare.PIECETYPE_AMAZON_WHITE;

    public AmazonEvaluator() {}

    public abstract AmazonMove evaluateBoard(AmazonBoard board);

    public void setColor(int color) {
        playerColor = color;
    }

    public int getColor() {
        return playerColor;
    }

}
