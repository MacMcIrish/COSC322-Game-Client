package AmazonEvaluator;

import AmazonBoard.AmazonBoardCalculator;
import AmazonBoard.AmazonSquare;

/**
 * Created by D on 3/13/2017.
 */
public class ScoredRandomEvaluator extends AmazonEvaluator {

    @Override
    public AmazonMove evaluateBoard() {

        int bestScore = 0;

        // All the code between the ***** must be in the evaluateBoard function or nothing will work
        //**********************************************

        while (!kill) { //This is the flag for the thread. Once the timer is up, kill = true, and thread will stop

            AmazonSquare sInit = getRandomQueen(playerColor);
            if (sInit == null) continue;

            AmazonSquare sFinal = getRandomMove(sInit);
            if (sFinal == null) continue;

            AmazonSquare arrow = getRandomShot(sInit, sFinal);
            if (arrow == null) continue;

            int score[] = board.getBoardCalculator().calculateScore(AmazonBoardCalculator.RELATIVE_TERRAIN_SCORE);

            if (score[playerColor] > bestScore) {
                bestScore = score[playerColor];
                bestCurrentMove = new AmazonMove(sInit, sFinal, arrow);
            }

        }

        return bestCurrentMove;//doesn't do anything, as nothing needs it as a return

    }
}
