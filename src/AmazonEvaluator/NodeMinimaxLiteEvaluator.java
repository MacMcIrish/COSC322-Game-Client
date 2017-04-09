package AmazonEvaluator;

import AmazonBoard.AmazonBoard;
import AmazonBoard.AmazonSquare;
import AmazonGame.AmazonNode;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by d on 29/03/17.
 */
public class NodeMinimaxLiteEvaluator extends AmazonEvaluator {

    int nodesChecked = 0;

    @Override
    public AmazonMove evaluateBoard() {
        bestCurrentMove = null;
        AmazonNode origin = new AmazonNode(null, null);
        //origin.setNodeBoard(new AmazonBoard(board));
//        AmazonMove bestMove;
        double score, bestScore;

        int depth = 1;
        long time = System.currentTimeMillis();
//        origin.generateChildren(getColor());
        AmazonNode bestNode = null;
        while (!kill && depth < 15) {

            time = System.currentTimeMillis();

            alphaBeta(origin, depth, -Double.MAX_VALUE, Double.MAX_VALUE, true, getColor());
            bestScore = -Double.MAX_VALUE;
            System.out.println("Picking from " + origin.children.size());
            int i = 0;
            for (AmazonNode node : origin.children) {

                score = node.getScore();
                if (score > bestScore) {
                    System.out.println("New score: " + score + " replaces old score: " + bestScore + " with " + node.getMove() + " replacing " + bestCurrentMove);
                    bestScore = score;
                    bestNode = node;
                    bestCurrentMove = bestNode.getMove();
                }
            }

            System.out.println("Depth " + depth + " finished in " + ((System.currentTimeMillis() - time)) + " ms");
            depth++;
        }

        return bestCurrentMove;
    }

    public double alphaBeta(AmazonNode node, int depth, double alpha, double beta, boolean maximizingPlayer, int currentPlayerColor) {
        int otherPlayerColor = (AmazonSquare.PIECETYPE_AMAZON_WHITE == currentPlayerColor ? 2 : 1);

        if (depth == 0 || kill) {
            double score = board.getBoardCalculator().calculateScore()[otherPlayerColor - 1];

            if (maximizingPlayer)
                score = -score;

            return score;
        }

        double v;
        if (maximizingPlayer) {
            v = -Double.MAX_VALUE;
            // Generate list of all possible outcomes
            if (node.children.size() == 0) {
                ArrayList<AmazonSquare> queenList = board.getQueenList(currentPlayerColor);
                for (int i = 0; i < queenList.size(); i++) {
                    if (kill)
                        break;
                    AmazonSquare queen = queenList.get(i);
                    ArrayList<AmazonSquare> moves = board.getBoardCalculator().generateListOfValidMoves(queen);
                    for (AmazonSquare sFinal : moves) {
                        if (kill)
                            break;
                        ArrayList<AmazonSquare> shots = board.getBoardCalculator().generateListOfValidShots(queen, sFinal);
//                    System.out.println("Moves: " + shots.size() + "\tTotal moves: " + node.children.size());
                        for (AmazonSquare shot : shots) {
                            if (kill)
                                break;

                            AmazonMove move = new AmazonMove(queen, sFinal, shot);

                            try {
                            board.executeMove(move);
                            } catch (InvalidMoveException ignored) {
//                                e.printStackTrace();
                            }

                            AmazonNode childNode = new AmazonNode(node, move);

                            board.getBoardCalculator().calculateBoard();
                            childNode.setScore(alphaBeta(childNode, depth - 1, alpha, beta, false, otherPlayerColor));
                            node.addChild(childNode);
                            v = Math.max(v, node.getScore());
                            alpha = Math.max(v, alpha);

                            board.undoMove();

                            if (beta <= alpha)
                                break;
                        }
                    }
                }
            } else {
                node.children.sort(Comparator.comparing(AmazonNode::getScore).reversed());
                for (AmazonNode childNode : node.children) {
                    node.setScore(alphaBeta(childNode, depth - 1, alpha, beta, false, otherPlayerColor));
                    v = Math.max(v, node.getScore());
                    alpha = Math.max(v, alpha);
                    if (beta <= alpha)
                        break;
                }
            }
            node.setScore(v);
            return v;
        } else {
            v = Double.MAX_VALUE;
            // Generate list of all possible outcomes
            if (node.children.size() == 0) {
                ArrayList<AmazonSquare> queenList = board.getQueenList(currentPlayerColor);
                for (int i = 0; i < queenList.size(); i++) {
                    if (kill)
                        break;
                    AmazonSquare queen = queenList.get(i);
                    ArrayList<AmazonSquare> moves = board.getBoardCalculator().generateListOfValidMoves(queen);
                    for (AmazonSquare sFinal : moves) {
                        if (kill) break;
                        ArrayList<AmazonSquare> shots = board.getBoardCalculator().generateListOfValidShots(queen, sFinal);
                        for (AmazonSquare shot : shots) {
                            if (kill)
                                break;


                            AmazonMove move = new AmazonMove(queen, sFinal, shot);

                            try {
                                board.executeMove(move);
                            } catch (InvalidMoveException ignored) {
//                                e.printStackTrace();
                            }

                            AmazonNode childNode = new AmazonNode(node, move);

                            board.getBoardCalculator().calculateBoard();
                            childNode.setScore(alphaBeta(childNode, depth - 1, alpha, beta, false, otherPlayerColor));
                            node.addChild(childNode);
                            v = Math.min(v, node.getScore());
                            beta = Math.min(v, beta);

                            board.undoMove();

                            if (beta <= alpha)
                                break;

                        }
                    }
                }
            } else {
                node.children.sort(Comparator.comparing(AmazonNode::getScore).reversed());
                for (AmazonNode childNode : node.children) {
                    node.setScore(alphaBeta(childNode, depth - 1, alpha, beta, true, otherPlayerColor));
                    v = Math.min(v, node.getScore());
                    beta = Math.min(v, beta);
                    if (beta <= alpha)
                        break;
                }
            }
            node.setScore(v);
            return v;
        }
    }
}
