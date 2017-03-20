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
        AmazonNode origin = new AmazonNode(null, null);
        origin.setNodeBoard(new AmazonBoard(board));
//        AmazonMove bestMove;
        double score, bestScore;
        bestScore = Double.MIN_VALUE;

        int depth = 0;
        long l = System.currentTimeMillis();
//        origin.generateChildren(getColor());
        while (!kill) {
            alphaBeta(origin, 1, Double.MIN_VALUE, Double.MAX_VALUE, true, getColor());
        }
        System.out.println("Took " + (System.currentTimeMillis() - l) + " to generate " + origin.getChildren().size() + " children");
        AmazonNode bestNode = Collections.max(origin.children, Comparator.comparing(AmazonNode::getScore));
        AmazonMove bestMove = bestNode.getMove();
        AmazonSquare sInit = board.getSquare(bestMove.getInitial().getPosX(), bestMove.getInitial().getPosY());
        AmazonSquare sFinal = board.getSquare(bestMove.getFinal().getPosX(), bestMove.getFinal().getPosY());
        AmazonSquare arrow = board.getSquare(bestMove.getArrow().getPosX(), bestMove.getArrow().getPosY());
        bestCurrentMove = new AmazonMove(sInit, sFinal, arrow);
        return null;
    }

    public double alphaBeta(AmazonNode node, int depth, double alpha, double beta, boolean maximizingPlayer, int playerColor) {
        int otherPlayerColor = AmazonSquare.PIECETYPE_AMAZON_WHITE == getColor() ? 2 : 1;

        if (depth == 0 || node.nodeBoard.getBoardCalculator().checkForWinCondition() || kill) {
            return node.nodeBoard.getBoardCalculator().calculateDeltaTerrainScore()[playerColor - 1];
        }

        double v;
        if (maximizingPlayer) {
            v = Double.MIN_VALUE;
            // Generate list of all possible outcomes
            ArrayList<AmazonSquare> queenList = node.nodeBoard.getQueenList(playerColor);
            for (int i = 0; i < queenList.size(); i++) {
                if (kill)
                    break;
                AmazonSquare queen = queenList.get(i);
                ArrayList<AmazonSquare> moves = node.nodeBoard.getBoardCalculator().generateListOfValidMoves(queen);
                for (AmazonSquare sFinal : moves) {
                    if (kill)
                        break;
                    ArrayList<AmazonSquare> shots = node.nodeBoard.getBoardCalculator().generateListOfValidShots(queen, sFinal);
                    for (AmazonSquare shot : shots) {
                        if (kill)
                            break;
                        AmazonBoard childBoard = new AmazonBoard(node.nodeBoard);
                        AmazonSquare childInit = childBoard.getSquare(queen.getPosX(), queen.getPosY());
                        AmazonSquare childFin = childBoard.getSquare(sFinal.getPosX(), sFinal.getPosY());
                        AmazonSquare childShot = childBoard.getSquare(shot.getPosX(), shot.getPosY());
                        AmazonMove childMove = new AmazonMove(childInit, childFin, childShot);
                        AmazonNode childNode = new AmazonNode(node, childMove);
                        childNode.setNodeBoard(childBoard);
                        node.addChild(childNode);
                        node.setScore(alphaBeta(childNode, depth - 1, alpha, beta, false, otherPlayerColor));
                        v = Math.max(v, node.getScore());
                        alpha = Math.max(v, alpha);
                        if (beta <= alpha)
                            break;
                    }
                }
            }
            return v;
        } else {
            v = Double.MAX_VALUE;
            // Generate list of all possible outcomes
            ArrayList<AmazonSquare> queenList = node.nodeBoard.getQueenList(playerColor);
            for (int i = 0; i < queenList.size(); i++) {
                if (kill)
                    break;
                AmazonSquare queen = queenList.get(i);
                ArrayList<AmazonSquare> moves = node.nodeBoard.getBoardCalculator().generateListOfValidMoves(queen);
                for (AmazonSquare sFinal : moves) {
                    if (kill) break;
                    ArrayList<AmazonSquare> shots = node.nodeBoard.getBoardCalculator().generateListOfValidShots(queen, sFinal);
                    for (AmazonSquare shot : shots) {
                        if (kill)
                            break;
                        AmazonBoard childBoard = new AmazonBoard(node.nodeBoard);
                        AmazonSquare childInit = childBoard.getSquare(queen.getPosX(), queen.getPosY());
                        AmazonSquare childFin = childBoard.getSquare(sFinal.getPosX(), sFinal.getPosY());
                        AmazonSquare childShot = childBoard.getSquare(shot.getPosX(), shot.getPosY());
                        AmazonMove childMove = new AmazonMove(childInit, childFin, childShot);
                        AmazonNode childNode = new AmazonNode(node, childMove);
                        node.addChild(childNode);
                        node.setScore(alphaBeta(childNode, depth - 1, alpha, beta, true, otherPlayerColor));
                        v = Math.min(v, node.getScore());
                        beta = Math.min(v, beta);
                        if (beta <= alpha)
                            break;
                    }
                }
            }
            return v;

        }
    }
}
