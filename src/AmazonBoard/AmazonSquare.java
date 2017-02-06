package AmazonBoard;

/**
 * Created by D on 2/3/2017.
 */
public class AmazonSquare {

    private int posX, posY, squareStrength = 0, pieceType = 0, dis;

    private int distanceQueenWhite, distanceQueenBlack, distanceKingWhite, distanceKingBlack;

    private boolean captured, counted;

    public static final int PIECETYPE_AVAILABLE = 0;
    public static final int PIECETYPE_AMAZON_WHITE = 1;
    public static final int PIECETYPE_AMAZON_BLACK = 2;
    public static final int PIECETYPE_ARROW = 3;

    public static final int DISTANCE_QUEEN = 100;
    public static final int DISTANCE_KING = 1;


    public AmazonSquare(int posX, int posY, int pieceType) {

        setPosX(posX);
        setPosY(posY);
        setPieceType(pieceType);

        resetDistances();

    }

    public String toString() {

        return "PosX: " + posX +
                ", PosY: " + posY +
                ", PieceType: " + pieceType;

    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getQueenDistance(int color) {

        if (color == PIECETYPE_AMAZON_WHITE) return distanceQueenWhite;
        if (color == PIECETYPE_AMAZON_BLACK) return distanceQueenBlack;

        return Integer.MAX_VALUE;
    }

    public int getKingDistance(int color) {

        if (color == PIECETYPE_AMAZON_WHITE) return distanceKingWhite;
        if (color == PIECETYPE_AMAZON_BLACK) return distanceKingBlack;

        return Integer.MAX_VALUE;
    }

    public void setQueenDistance(int color, int distance) {

        if (color == PIECETYPE_AMAZON_WHITE) distanceQueenWhite = distance;
        if (color == PIECETYPE_AMAZON_BLACK) distanceQueenBlack = distance;

    }

    public void setKingDistance(int color, int distance) {

        if (color == PIECETYPE_AMAZON_WHITE) distanceKingWhite = distance;
        if (color == PIECETYPE_AMAZON_BLACK) distanceKingBlack = distance;

    }

    public int getDistance(int color, int queenOrKing) {

        if (queenOrKing == DISTANCE_QUEEN) return getQueenDistance(color);
        if (queenOrKing == DISTANCE_KING) return getKingDistance(color);

        return Integer.MAX_VALUE;
    }

    public void setDistance(int color, int distance, int queenOrKing) {

        if (queenOrKing == DISTANCE_QUEEN) setQueenDistance(color, distance);
        if (queenOrKing == DISTANCE_KING) setKingDistance(color, distance);

    }


    public void resetDistances() {

        int reset = Integer.MAX_VALUE;

        setQueenDistance(PIECETYPE_AMAZON_WHITE, reset);
        setQueenDistance(PIECETYPE_AMAZON_BLACK, reset);
        setKingDistance(PIECETYPE_AMAZON_WHITE, reset);
        setKingDistance(PIECETYPE_AMAZON_BLACK, reset);
    }

    public int getSquareStrength() {
        return squareStrength;
    }

    public void setSquareStrength(int squareStrength) {
        this.squareStrength = squareStrength;
    }


    public int getPieceType() {
        return pieceType;
    }

    public void setPieceType(int pieceType) {
        this.pieceType = pieceType;
    }

    public boolean isCaptured() {
        return captured;
    }

    public void setCaptured(boolean captured) {
        this.captured = captured;
    }
}