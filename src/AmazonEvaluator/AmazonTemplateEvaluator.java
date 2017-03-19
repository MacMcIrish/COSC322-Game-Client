package AmazonEvaluator;

import AmazonBoard.AmazonBoard;
import AmazonBoard.AmazonSquare;
import ygraphs.ai.smart_fox.games.Amazon;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by D on 3/13/2017.
 */
public class AmazonTemplateEvaluator extends AmazonEvaluator {
    @Override
    public AmazonMove evaluateBoard() {

        // All the code between the ***** must be in the evaluateBoard function or nothing will work
        //**********************************************

        double score;
        double runningBestScore = Double.MAX_VALUE;

        double moveScore = 0;
        double runningBestMoveScore = -1;
        AmazonSquare bestMove;

        AmazonMove move = null;
        LinkedList<AmazonMove> moveStack = new LinkedList<AmazonMove>();
        AmazonBoard currentBoard = new AmazonBoard(this.board);
        AmazonIterations iterations = new AmazonIterations();

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

            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < queenList.size(); i++) {
                if (kill && move != null)
                    break;
                AmazonSquare queen = queenList.get(i);
                // Find the best move for this queen

                ArrayList<AmazonSquare> moveList = currentBoard.getBoardCalculator().generateListOfValidMoves(queen);
                if (moveList.size() == 0)
                    continue;
                bestMove = Collections.max(moveList, Comparator.comparing(m -> m.getMobility()));

                ArrayList<AmazonSquare> bestMoveShots = currentBoard.getBoardCalculator().generateListOfValidShots(queen, bestMove);

                for (AmazonSquare shot : bestMoveShots) {
                    move = new AmazonMove(queen, bestMove, shot);
                    try {
                        currentBoard.executeMove(move);
                        moveStack.add(move);
                        score = alphaBetaMove(currentBoard, 1, Double.MIN_VALUE, Double.MAX_VALUE, false, moveStack);
                        currentBoard.undoMove(move);

                        if (score < runningBestScore) {
                            AmazonSquare realQueen = board.getSquare(queen.getPosX(), queen.getPosY());
                            AmazonSquare realMove = board.getSquare(bestMove.getPosX(), bestMove.getPosY());
                            AmazonSquare realShot = board.getSquare(shot.getPosX(), shot.getPosY());

                            System.out.println("New score " + score + " > " + runningBestScore + " means " + move + "" +
                                    "replaces " + bestCurrentMove);
                            bestCurrentMove = new AmazonMove(realQueen, realMove, realShot);
                            runningBestScore = score;
                        }
                    } catch (InvalidMoveException ignored) {
                    }
                }

            }
            // Needs to be like this to combat concurrency exceptions

            /*for (int i = 0; i < queenList.size(); i++) {
                if (kill && move != null) break;
                AmazonSquare queen = queenList.get(i);
                ArrayList<AmazonSquare> moves = currentBoard.getBoardCalculator().generateListOfValidMoves(queen);
                moveStack.clear();

                for (AmazonSquare availableMove : moves) {

                    // From list of moves, pick best move
                    moveScore = alphaBetaMove(queen, availableMove)

                    ArrayList<AmazonSquare> shots = currentBoard.getBoardCalculator().generateListOfValidShots(queen, availableMove);
                    for (AmazonSquare shot : shots) {

                        move = new AmazonMove(queen, availableMove, shot);
                        moveStack.add(move);
                        long in = System.currentTimeMillis();
                        score = alphaBeta(move, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false, currentBoard, moveStack, iterations.increment());
                        long fin = System.currentTimeMillis() - in;
                        System.out.println("That took: " + fin + " seconds for " + move + " and iterations " + iterations);

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
            }*/
        }
        System.out.println("Iterations: " + iterations);
        return move; //doesn't do anything, as nothing needs it as a return

        //*************************************************
    }

    private double alphaBeta(AmazonSquare node, int depth, double alpha, double beta, boolean maximizingPlayer, AmazonBoard oldBoard, LinkedList<AmazonMove> moveStack, AmazonIterations iterations) {
        double v;

        long i = System.currentTimeMillis();
        ArrayList<AmazonSquare> children = oldBoard.getBoardCalculator().generateListOfValidMoves(node);
        if (depth == 0 || children.size() == 0 || kill) {
            return oldBoard.getBoardCalculator().calculateDeltaTerrainScore()[playerColor - 1];
        }

        if (maximizingPlayer) {
            v = Integer.MIN_VALUE;
            for (AmazonSquare child : children) {
                ArrayList<AmazonSquare> shots = oldBoard.getBoardCalculator().generateListOfValidShots(node, child);
                for (AmazonSquare potentialShot : shots) {
                    if (kill)
                        return v;
                    AmazonMove move = new AmazonMove(node, child, potentialShot);
                    try {
                        oldBoard.executeMove(move);
                        moveStack.addFirst(move);
                        v = Math.max(v, alphaBeta(child, depth - 1, alpha, beta, false, oldBoard, moveStack, iterations.increment()));
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
                    long f = System.currentTimeMillis() - i;
                    if (kill)
                        return v;
                    AmazonMove move = new AmazonMove(node, child, potentialShot);
                    try {
                        oldBoard.executeMove(move);
                        moveStack.addFirst(move);
                        v = Math.min(v, alphaBeta(child, depth - 1, alpha, beta, true, oldBoard, moveStack, iterations.increment()));
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

    public double alphaBetaMove(AmazonBoard node, int depth, double alpha, double beta, boolean maximizingPlayer, LinkedList<AmazonMove> moveStack) {
        double v = 0;
        int otherPlayerColour = AmazonSquare.PIECETYPE_AMAZON_WHITE == getColor() ? 2 : 1;

        ArrayList<AmazonSquare> queenList = maximizingPlayer ? node.getQueenList(getColor()) : node.getQueenList(otherPlayerColour);
        AmazonSquare bestQueenMove;

        if (kill) {
            return v;
        } else if (depth == 0) {
            return node.getBoardCalculator().calculateDeltaTerrainScore()[getColor() - 1];
        }

        if (maximizingPlayer) {
            for (int i = 0; i < queenList.size(); i++) {
                AmazonSquare queen = queenList.get(i);
                ArrayList<AmazonSquare> queenMoveList = node.getBoardCalculator().generateListOfValidMoves(queen);
                if (queenMoveList.size() == 0)
                    continue;
                bestQueenMove = Collections.max(queenMoveList, Comparator.comparing(c -> c.getMobility()));
                ArrayList<AmazonSquare> queenShotList = node.getBoardCalculator().generateListOfValidShots(queen, bestQueenMove);

                v = Double.MIN_VALUE;
                for (AmazonSquare shot : queenShotList) {
                    AmazonMove move = new AmazonMove(queen, bestQueenMove, shot);
                    try {
                        node.executeMove(move);
                        moveStack.addFirst(move);
                        v = Math.max(v, alphaBetaMove(node, depth - 1, alpha, beta, false, moveStack));
                        alpha = Math.max(v, alpha);
                        node.undoMove(moveStack.removeFirst());

                    } catch (InvalidMoveException ignored) {
                    }
                    if (beta <= alpha)
                        break;
                }
            }
            return v;

        } else {
            for (int i = 0; i < queenList.size(); i++) {
                AmazonSquare queen = queenList.get(i);
                ArrayList<AmazonSquare> queenMoveList = node.getBoardCalculator().generateListOfValidMoves(queen);
                if (queenMoveList.size() == 0)
                    continue;
                bestQueenMove = Collections.max(queenMoveList, Comparator.comparing(c -> c.getMobility()));
                ArrayList<AmazonSquare> queenShotList = node.getBoardCalculator().generateListOfValidShots(queen, bestQueenMove);

                v = Double.MAX_VALUE;
                for (AmazonSquare shot : queenShotList) {
                    AmazonMove move = new AmazonMove(queen, bestQueenMove, shot);
                    try {
                        node.executeMove(move);
                        moveStack.addFirst(move);
                        v = Math.min(v, alphaBetaMove(node, depth - 1, alpha, beta, true, moveStack));
                        beta = Math.min(beta, v);
                        node.undoMove(moveStack.removeFirst());
                    } catch (InvalidMoveException ignored) {
                    }
                    if (beta <= alpha)
                        break;
                }
            }
            return v;
        }
    }

}
