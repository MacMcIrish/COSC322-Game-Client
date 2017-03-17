package AmazonEvaluator;

import AmazonBoard.AmazonBoard;
import AmazonBoard.AmazonSquare;
import ygraphs.ai.smart_fox.games.Amazon;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by D on 3/13/2017.
 */
public class AmazonTemplateEvaluator extends AmazonEvaluator {
    @Override
    public AmazonMove evaluateBoard() {

        // All the code between the ***** must be in the evaluateBoard function or nothing will work
        //**********************************************

        int score;
        int runningBestScore = 0;
        AmazonMove move = null;
        LinkedList<AmazonMove> moveStack = new LinkedList<AmazonMove>();
        AmazonBoard currentBoard = new AmazonBoard(this.board);

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
                moveStack.clear();

                for (AmazonSquare availableMove : moves) {
                    ArrayList<AmazonSquare> shots = currentBoard.getBoardCalculator().generateListOfValidShots(queen, availableMove);
                    for (AmazonSquare shot : shots) {

                        move = new AmazonMove(queen, availableMove, shot);
                        moveStack.add(move);
                        score = alphaBeta(queen, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, true, currentBoard, moveStack);

                        if (score > runningBestScore) {
                            AmazonSquare realQueen = board.getSquare(queen.getPosX(), queen.getPosY());
                            AmazonSquare realMove = board.getSquare(availableMove.getPosX(), availableMove.getPosY());
                            AmazonSquare realShot = board.getSquare(shot.getPosX(), shot.getPosY());

                            move = new AmazonMove(realQueen, realMove, realShot);
                            System.out.println("New score " + score + " > " + runningBestScore + " means " + move + "" +
                                    "replaces " + bestCurrentMove);
                            runningBestScore = score;

                            bestCurrentMove = move;
                        }
                    }
                }
            }
        }

        return move; //doesn't do anything, as nothing needs it as a return

        //*************************************************
    }

    private int alphaBeta(AmazonSquare node, int depth, float alpha, float beta, boolean maximizingPlayer, AmazonBoard oldBoard, LinkedList<AmazonMove> moveStack) {
        int v;
        ArrayList<AmazonSquare> children = oldBoard.getBoardCalculator().generateListOfValidMoves(node);
        if (depth == 0 || children.size() == 0 || kill) {
            return oldBoard.getBoardCalculator().calculateScore(1)[playerColor - 1];
        }

        if (maximizingPlayer) {
            v = Integer.MIN_VALUE;
            for (AmazonSquare child : children) {
                ArrayList<AmazonSquare> shots = oldBoard.getBoardCalculator().generateListOfValidShots(node, child);
                for (AmazonSquare potentialShot : shots) {
                    AmazonMove move = new AmazonMove(node, child, potentialShot);
                    try {
                        oldBoard.executeMove(move);
                        moveStack.addFirst(move);
                        v = Math.max(v, alphaBeta(child, depth - 1, alpha, beta, false, oldBoard, moveStack));
                        oldBoard.undoMove(moveStack.removeFirst());
                        alpha = Math.max(v, alpha);
                    } catch (InvalidMoveException e) {
//                        e.printStackTrace();
                    }
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return v;
        } else {
            v = Integer.MAX_VALUE;
            for (AmazonSquare child : children) {
                ArrayList<AmazonSquare> shots = oldBoard.getBoardCalculator().generateListOfValidShots(node, child);
                for (AmazonSquare potentialShot : shots) {
                    AmazonMove move = new AmazonMove(node, child, potentialShot);
                    try {
                        oldBoard.executeMove(move);
                        moveStack.addFirst(move);
                        v = Math.min(v, alphaBeta(child, depth - 1, alpha, beta, true, oldBoard, moveStack));
                        oldBoard.undoMove(moveStack.removeFirst());
                        beta = Math.min(v, beta);
                    } catch (InvalidMoveException e) {
//                        e.printStackTrace();
                    }
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return v;
        }
    }
}
