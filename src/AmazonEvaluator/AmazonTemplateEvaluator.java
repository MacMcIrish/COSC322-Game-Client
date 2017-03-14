package AmazonEvaluator;

import AmazonBoard.AmazonBoard;
import AmazonBoard.AmazonSquare;
import ygraphs.ai.smart_fox.games.Amazon;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by D on 3/13/2017.
 */
public class AmazonTemplateEvaluator extends AmazonEvaluator {
    @Override
    public AmazonMove evaluateBoard() {

        // All the code between the ***** must be in the evaluateBoard function or nothing will work
        //**********************************************

        int score = 0;
        int runningBestScore = 0;

        AmazonBoard currentBoard = new AmazonBoard(board);
        while (!kill) { //This is the flag for the thread. Once the timer is up, kill = true, and thread will stop


        /*

        All the code for minimax or whatever algorithm goes here

        Store the best move of all moves that have been tested in bestCurrentMove.
        When thread is stopped, the player will take the bestCurrentMove and compare it to the other evaluators.

        For each node being traversed, calculate scores via board.getBoardCalculator().calculateScore(int type).
           For the return, the score index for the player is [playerColor - 1].
           The type doesn't matter for the function of this method.

         */
            // Get a list of all the queens and their moves

            ArrayList<AmazonSquare> queenList = currentBoard.getQueenList(getColor());
            // TODO concurrency modification exception
            for (int i = 0; i < queenList.size(); i++){
                AmazonSquare queen = queenList.get(i);
//            for (AmazonSquare queen : queenList) {
                ArrayList<AmazonSquare> moves = currentBoard.getBoardCalculator().generateListOfValidMoves(queen);
                System.out.println("Found " + moves.size() + " valid moves in minimax");
                for (AmazonSquare move : moves) {
                    ArrayList<AmazonSquare> shots = currentBoard.getBoardCalculator().generateListOfValidShots(queen, move);
                    for (AmazonSquare shot : shots) {
                        score = alphaBeta(shot, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false, currentBoard);
                        if (score > runningBestScore) {
                            runningBestScore = score;
                            bestCurrentMove = new AmazonMove(queen, move, shot);
                        }
                    }
                }
//                System.out.println("Checking next queen hopefully");
            }

        }

        return bestCurrentMove; //doesn't do anything, as nothing needs it as a return

        //*************************************************
    }

    public int alphaBeta(AmazonSquare node, int depth, float alpha, float beta, boolean maximizingPlayer, AmazonBoard currentBoard) {
        int v;

        ArrayList<AmazonSquare> children = currentBoard.getBoardCalculator().generateListOfValidMoves(node);

        if (depth == 0 || children.size() == 0) {
            int score = currentBoard.getBoardCalculator().calculateScore(2)[playerColor - 1];
            return score;
        }

        if (maximizingPlayer) {
            v = Integer.MIN_VALUE;
            for (AmazonSquare child : children) {
                ArrayList<AmazonSquare> shots = currentBoard.getBoardCalculator().generateListOfValidShots(node, child);
                for (AmazonSquare shot : shots) {
                    AmazonBoard newBoard = new AmazonBoard(currentBoard);
                    newBoard.moveAmazon(node, child);
                    newBoard.shootArrow(child, shot);
                    v = Math.max(v, alphaBeta(child, depth - 1, alpha, beta, false, newBoard));
                    alpha = Math.max(v, alpha);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return v;
        } else {
            v = Integer.MAX_VALUE;
            for (AmazonSquare child : children) {

                ArrayList<AmazonSquare> shots = currentBoard.getBoardCalculator(). (node, child);
                for (AmazonSquare shot : shots) {
                    AmazonBoard newBoard = new AmazonBoard(currentBoard);
                    newBoard.moveAmazon(node, child);
                    newBoard.shootArrow(child, shot);
                    v = Math.min(v, alphaBeta(child, depth - 1, alpha, beta, true, newBoard));
                    beta = Math.min(v, beta);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return v;
        }
    }
}
