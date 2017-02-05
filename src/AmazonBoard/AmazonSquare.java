package AmazonBoard;

/**
 * Created by D on 2/3/2017.
 */
public class AmazonSquare {

    private int posX, posY, distanceWhite = 9, distanceBlack = 9, squareStrength = 0, pieceType = 0;
    private boolean captured, counted;

    public static final int PIECETYPE_AVAILABLE = 0;
    public static final int PIECETYPE_AMAZON_WHITE = 1;
    public static final int PIECETYPE_AMAZON_BLACK = 2;
    public static final int PIECETYPE_ARROW = 3;

    public AmazonSquare(int posX, int posY, int pieceType) {

        setPosX(posX);
        setPosY(posY);
        setPieceType(pieceType);

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

    public int getDistance(int color) {
        if (color == PIECETYPE_AMAZON_WHITE) return distanceWhite;
        else return distanceBlack;
    }

    public void setDistance(int color, int distance) {
        if (color == PIECETYPE_AMAZON_WHITE) distanceWhite = distance;
        else distanceBlack = distance;
    }

    public int getDisBlack() {
        return distanceBlack;
    }

    public void setDisBlack(int disBlack) {
        this.distanceBlack = disBlack;
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

}
