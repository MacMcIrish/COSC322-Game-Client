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

        int score = 0;
        int runningBestScore = 0;
        AmazonMove move = null;
        LinkedList<AmazonMove> moveStack = new LinkedList<AmazonMove>();
        AmazonBoard currentBoard = new AmazonBoard(this.board);
        System.out.println("Board before minimax: " + board);
        while (move == null && !kill) { //This is the flag for the thread. Once the timer is up, kill = true, and thread will stop
            System.out.println("Board at beginning of while: " + board);

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

                System.out.println("Board at beginning of for: " + board);
                AmazonSquare queen = queenList.get(i);
                ArrayList<AmazonSquare> moves = currentBoard.getBoardCalculator().generateListOfValidMoves(queen);
                moveStack.clear();

                for (AmazonSquare availableMove : moves) {
                    ArrayList<AmazonSquare> shots = currentBoard.getBoardCalculator().generateListOfValidShots(queen, availableMove);
                    for (AmazonSquare shot : shots) {

                        move = new AmazonMove(queen, availableMove, shot);
                        moveStack.add(move);
                        System.out.println("Board after picking shot: " + board);
                        score = alphaBeta(queen, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false, currentBoard, moveStack);

                        if (score > runningBestScore) {
                            runningBestScore = score;
//                            AmazonSquare queenMove = board.getSquare(queen.getPosX(), queen.getPosY());
//                            AmazonSquare newPost = board.getSquare(availableMove.getPosX(), availableMove.getPosY());
//                            AmazonSquare newShot = board.getSquare(shot.getPosX(), shot.getPosY());

                            move = new AmazonMove(queen, availableMove, shot);

                            System.out.println("Board before bestCurrentMove: " + board);
                            bestCurrentMove = move;
//                            System.out.println("Queen list for the board: " + board.getQueenList(getColor()));
//                            System.out.println("Best current move for minimax inside method" + bestCurrentMove + " piece type:" + bestCurrentMove.getInitial().getPieceType());
                        }
                    }
                }
            }
        }

//        bestCurrentMove = move;
//        System.out.println("2: Best current move for minimax " + bestCurrentMove + " piece type:" + bestCurrentMove.getInitial().getPieceType());
//        System.out.println("2: Best current move for minimax " + move + " piece type:" + move.getInitial().getPieceType());
        return move; //doesn't do anything, as nothing needs it as a return

        //*************************************************
    }

    private int alphaBeta(AmazonSquare node, int depth, float alpha, float beta, boolean maximizingPlayer, AmazonBoard oldBoard, LinkedList<AmazonMove> moveStack) {
        int v;
        int missingPieceValue = board.getSquare(1,4).getPieceType();
        System.out.println(missingPieceValue + "Board at beginning of ab: " + board);
        ArrayList<AmazonSquare> children = oldBoard.getBoardCalculator().generateListOfValidMoves(node);
        if (depth == 0 || children.size() == 0 || kill) {
            // Separate only for debugging
            int score = oldBoard.getBoardCalculator().calculateScore(2)[playerColor - 1];
//            oldBoard.undoMove(moveStack.removeFirst());
            return score;
        }


        if (maximizingPlayer) {
            v = Integer.MIN_VALUE;
            for (AmazonSquare child : children) {
                ArrayList<AmazonSquare> shots = oldBoard.getBoardCalculator().generateListOfValidShots(node, child);
                for (AmazonSquare potentialShot : shots) {
                    AmazonMove move = new AmazonMove(node, child, potentialShot);
                    try {
                        oldBoard.executeMove(move);
                        v = Math.max(v, alphaBeta(child, depth - 1, alpha, beta, false, oldBoard, moveStack));
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
//                    System.out.println(node + ": Shooting " + child + " to " + shot);
//                    oldBoard.moveAmazon(node, child);
//                    oldBoard.shootArrow(child, shot);
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
