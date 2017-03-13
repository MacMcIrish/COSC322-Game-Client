package AmazonEvaluator;

import AmazonBoard.AmazonSquare;

/**
 * Created by D on 3/13/2017.
 */
public class AmazonTemplateEvaluator extends AmazonEvaluator {
    @Override
    public AmazonMove evaluateBoard() {

        while (!kill) { //This is the flag for the thread. Once the timer is up, kill = true, and thread will stop

        /*

        All the code goes here

        Store the best current move in bestCurrentMove

         */

        }

        return bestCurrentMove; //doesn't do anything, as nothing needs it as a return
    }
}
