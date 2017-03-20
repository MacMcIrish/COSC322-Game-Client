package AmazonEvaluator;

import AmazonGame.AmazonNode;
import AmazonBoard.AmazonBoard;
import AmazonBoard.AmazonSquare;
import ygraphs.ai.smart_fox.games.Amazon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by jeff on 19/03/17.
 */
public class NodeMinimaxEvaluator extends AmazonEvaluator {
    @Override
    public AmazonMove evaluateBoard() {
        bestCurrentMove = null;
        AmazonNode origin = new AmazonNode(null, null);
        origin.setNodeBoard(new AmazonBoard(board));
//        AmazonMove bestMove;
        double score, bestScore;
        bestScore = -10000;

        int depth = 1;
        long l = System.currentTimeMillis();
//        origin.generateChildren(getColor());
        AmazonNode bestNode = null;
        while (!kill) {
            alphaBeta(origin, depth, -Double.MAX_VALUE, Double.MAX_VALUE, true, getColor());
            System.out.println("Finished ab at " + depth);
            bestScore = -Double.MAX_VALUE;
//            depth++;
            for (AmazonNode node : origin.children) {
                score = node.getScore();
                if (score > bestScore) {
//                    System.out.println("New best " + score + " replaces " + bestScore + " and " + node + " replaces " + bestNode);
                    bestScore = score;
                    bestNode = node;
                    AmazonMove bestMove = bestNode.getMove();
                    AmazonSquare sInit = board.getSquare(bestMove.getInitial().getPosX(), bestMove.getInitial().getPosY());
                    AmazonSquare sFinal = board.getSquare(bestMove.getFinal().getPosX(), bestMove.getFinal().getPosY());
                    AmazonSquare arrow = board.getSquare(bestMove.getArrow().getPosX(), bestMove.getArrow().getPosY());
                    bestCurrentMove = new AmazonMove(sInit, sFinal, arrow);
                }
            }
            System.out.println("Bumping up depth");
            depth++;
        }
        System.out.println("Took " + (System.currentTimeMillis() - l) + " to generate " + origin.getChildren().size() + " children");
//        AmazonNode bestNode = Collections.max(origin.children, Comparator.comparing(AmazonNode::getScore));

        return bestCurrentMove;
    }

    public double alphaBeta(AmazonNode node, int depth, double alpha, double beta, boolean maximizingPlayer, int currentPlayerColor) {
        int otherPlayerColor = AmazonSquare.PIECETYPE_AMAZON_WHITE == getColor() ? 2 : 1;

        if (depth == 0 || node.nodeBoard.getBoardCalculator().checkForWinCondition() || kill) {
            double score = node.nodeBoard.getBoardCalculator().calculateDeltaTerrainScore()[currentPlayerColor - 1];
//            double score = 0;
            score += node.getMove().getFinal().getMobility();
            if (node.getMove().getInitial().getDistance(otherPlayerColor, 1) == Integer.MAX_VALUE)
                score -= 1000;
            ArrayList<AmazonSquare> queenList = node.nodeBoard.getQueenList(currentPlayerColor);

            double otherQueenScore = 0;
            for (AmazonSquare otherQueen : queenList) {
                otherQueenScore += otherQueen.getMobility();
            }
            score -= 5 * otherQueenScore;
            return score;
        }

        double v;
        if (maximizingPlayer) {
            v = -Double.MAX_VALUE;
            // Generate list of all possible outcomes
            if (node.children.size() == 0) {
                ArrayList<AmazonSquare> queenList = node.nodeBoard.getQueenList(currentPlayerColor);
                for (int i = 0; i < queenList.size(); i++) {
                    if (kill)
                        break;
                    AmazonSquare queen = queenList.get(i);
                    ArrayList<AmazonSquare> moves = node.nodeBoard.getBoardCalculator().generateListOfValidMoves(queen);
                    for (AmazonSquare sFinal : moves) {
                        if (kill)
                            break;
                        ArrayList<AmazonSquare> shots = node.nodeBoard.getBoardCalculator().generateListOfValidShots(queen, sFinal);
//                    System.out.println("Moves: " + shots.size() + "\tTotal moves: " + node.children.size());
                        for (AmazonSquare shot : shots) {
                            if (kill)
                                break;
                            AmazonBoard childBoard = new AmazonBoard(node.nodeBoard);
                            AmazonSquare childInit = childBoard.getSquare(queen.getPosX(), queen.getPosY());
                            AmazonSquare childFin = childBoard.getSquare(sFinal.getPosX(), sFinal.getPosY());
                            AmazonSquare childShot = childBoard.getSquare(shot.getPosX(), shot.getPosY());
                            AmazonMove childMove = new AmazonMove(childInit, childFin, childShot);
                            AmazonNode childNode = new AmazonNode(node, childMove);
                            try {
                                childBoard.executeMove(childMove);
                            } catch (InvalidMoveException ignored) {
//                                e.printStackTrace();
                            }
                            childNode.setNodeBoard(childBoard);
                            childNode.setScore(alphaBeta(childNode, depth - 1, alpha, beta, false, otherPlayerColor));
                            node.addChild(childNode);
                            v = Math.max(v, node.getScore());
                            alpha = Math.max(v, alpha);
                            if (beta <= alpha)
                                break;
                        }
                    }
                }
            } else {
                node.children.sort(Comparator.comparing(AmazonNode::getScore));
                for (AmazonNode childNode : node.children) {
                    node.setScore(alphaBeta(childNode, depth - 1, alpha, beta, false, otherPlayerColor));
                    v = Math.max(v, node.getScore());
                    alpha = Math.max(v, alpha);
                    if (beta <= alpha)
                        break;
                }
            }
            return v;
        } else {
            v = Double.MAX_VALUE;
            // Generate list of all possible outcomes
            if (node.children.size() == 0) {
                ArrayList<AmazonSquare> queenList = node.nodeBoard.getQueenList(currentPlayerColor);
                for (int i = 0; i < queenList.size(); i++) {
                    if (kill)
                        break;
                    AmazonSquare queen = queenList.get(i);
                    ArrayList<AmazonSquare> moves = node.nodeBoard.getBoardCalculator().generateListOfValidMoves(queen);
                    for (AmazonSquare sFinal : moves) {
                        if (kill) break;
                        ArrayList<AmazonSquare> shots = node.nodeBoard.getBoardCalculator().generateListOfValidShots(queen, sFinal);
//                        shots.sort(Comparator.comparing(c -> c.getMobility()).reversed());
                        for (AmazonSquare shot : shots) {
                            if (kill)
                                break;
                            AmazonBoard childBoard = new AmazonBoard(node.nodeBoard);
                            AmazonSquare childInit = childBoard.getSquare(queen.getPosX(), queen.getPosY());
                            AmazonSquare childFin = childBoard.getSquare(sFinal.getPosX(), sFinal.getPosY());
                            AmazonSquare childShot = childBoard.getSquare(shot.getPosX(), shot.getPosY());
                            AmazonMove childMove = new AmazonMove(childInit, childFin, childShot);
                            AmazonNode childNode = new AmazonNode(node, childMove);
                            try {
                                childBoard.executeMove(childMove);
                            } catch (InvalidMoveException ignored) {
//                                e.printStackTrace();
                            }
                            childNode.setNodeBoard(childBoard);
                            childNode.setScore(alphaBeta(childNode, depth - 1, alpha, beta, true, otherPlayerColor));
                            node.addChild(childNode);
                            v = Math.min(v, node.getScore());
                            beta = Math.min(v, beta);
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
            return v;

        }
    }
}
