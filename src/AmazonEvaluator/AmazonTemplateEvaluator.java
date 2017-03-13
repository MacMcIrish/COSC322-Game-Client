package AmazonEvaluator;

import AmazonBoard.AmazonSquare;

/**
 * Created by D on 3/13/2017.
 */
public class AmazonTemplateEvaluator extends AmazonEvaluator {
    @Override
    public AmazonMove evaluateBoard() {

        // All the code between the ***** must be in the evaluateBoard function or nothing will work
        //**********************************************

        while (!kill) { //This is the flag for the thread. Once the timer is up, kill = true, and thread will stop

        /*

        All the code for minimax or whatever algorithm goes here

        Store the best move of all moves that have been tested in bestCurrentMove.
        When thread is stopped, the player will take the bestCurrentMove and compare it to the other evaluators.

        For each node being traversed, calculate scores via board.getBoardCalculator().calculateScore(int type).
           For the return, the score index for the player is [playerColor - 1].
           The type doesn't matter for the function of this method.

         */

        }

        return bestCurrentMove; //doesn't do anything, as nothing needs it as a return

        //*************************************************
    }
}
