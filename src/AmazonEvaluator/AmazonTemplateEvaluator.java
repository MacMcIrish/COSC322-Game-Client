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
        AmazonMove move = null;

        AmazonBoard currentBoard = new AmazonBoard(board);
        while (move == null && !kill) { //This is the flag for the thread. Once the timer is up, kill = true, and thread will stop


        /*

        All the code for minimax or whatever algorithm goes here

        Store the best move of all moves that have been tested in bestCurrentMove.
        When thread is stopped, the player will take the bestCurrentMove and compare it to the other evaluators.

        For each node being traversed, calculate scores via board.getBoardCalculator().calculateScore(int type).
           For the return, the score index for the player is [playerColor - 1].
           The type doesn't matter for the function of this method.

         */
            ArrayList<AmazonSquare> queenList = currentBoard.getQueenList(getColor());
            // Needs to be like this to combat concurrency exceptions
            for (int i = 0; i < queenList.size(); i++) {
                if (kill && move != null) break;
                AmazonSquare queen = queenList.get(i);
                ArrayList<AmazonSquare> moves = currentBoard.getBoardCalculator().generateListOfValidMoves(queen);
                for (AmazonSquare availableMove : moves) {
                    ArrayList<AmazonSquare> shots = currentBoard.getBoardCalculator().generateListOfValidShots(queen, availableMove);
                    for (AmazonSquare shot : shots) {
                        score = alphaBeta(shot, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false, currentBoard);
                        if (score > runningBestScore) {
                            runningBestScore = score;
                            AmazonSquare queenMove = board.getSquare(queen.getPosX(), queen.getPosY());
                            AmazonSquare newPost = board.getSquare(availableMove.getPosX(), availableMove.getPosY());
                            AmazonSquare newShot = board.getSquare(shot.getPosX(), shot.getPosY());

                            move = new AmazonMove(queenMove, newPost, newShot);
                            bestCurrentMove = move;
                            System.out.println("Best current move for minimax inside method" + bestCurrentMove + " piece type:" + bestCurrentMove.getInitial().getPieceType());
                        }
                    }
                }
            }
        }

//        bestCurrentMove = move;
        System.out.println("2: Best current move for minimax " + bestCurrentMove + " piece type:" + bestCurrentMove.getInitial().getPieceType());
        System.out.println("2: Best current move for minimax " + move + " piece type:" + move.getInitial().getPieceType());

        return move; //doesn't do anything, as nothing needs it as a return

        //*************************************************
    }

    private int alphaBeta(AmazonSquare node, int depth, float alpha, float beta, boolean maximizingPlayer, AmazonBoard oldBoard) {
        int v;
        AmazonBoard currentBoard = new AmazonBoard(oldBoard);
        ArrayList<AmazonSquare> children = currentBoard.getBoardCalculator().generateListOfValidMoves(node);

        if (depth == 0 || children.size() == 0 || kill) {
            int score = currentBoard.getBoardCalculator().calculateScore(2)[playerColor - 1];
            return score;
        }

        if (maximizingPlayer) {
            v = Integer.MIN_VALUE;
            for (AmazonSquare child : children) {
                ArrayList<AmazonSquare> shots = currentBoard.getBoardCalculator().generateListOfValidShots(node, child);
                for (AmazonSquare potentialShot : shots) {
                    AmazonSquare shot = new AmazonSquare(potentialShot);
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
                ArrayList<AmazonSquare> shots = currentBoard.getBoardCalculator().generateListOfValidShots(node, child);
                for (AmazonSquare potentialShot : shots) {
                    AmazonBoard newBoard = new AmazonBoard(currentBoard);
                    AmazonSquare shot = newBoard.getSquare(potentialShot.getPosX(), potentialShot.getPosY());
                    AmazonSquare child2 = newBoard.getSquare(child.getPosX(), child.getPosY());
//                    System.out.println(node + ": Shooting " + child + " to " + shot);
                    newBoard.moveAmazon(node, child);
                    newBoard.shootArrow(child, shot);
                    v = Math.min(v, alphaBeta(child2, depth - 1, alpha, beta, true, newBoard));
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
