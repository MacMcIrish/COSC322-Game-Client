package AmazonEvaluator;

/**
 * Created by D on 2/12/2017.
 */

    public class InvalidMoveException extends Exception {

        public InvalidMoveException(AmazonMove m) {

            super("Invalid move detected for " + m.toString());
        }
}
