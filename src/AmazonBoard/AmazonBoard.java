package AmazonBoard;

import java.util.*;

/**
 * Created by Drew on 2/4/2017.
 * .
 * This class represents the game board for Game of the Amazons
 * .
 * The structure uses a 12x12 array to hold individual tiles
 * The outer row/column will be all arrows to reduce complexity of checking algorithms (movement/capture)
 * .
 * Similar to the 10x12 board in chess:
 * https://chessprogramming.wikispaces.com/10x12+Board
 */
public class AmazonBoard {

    int minX = 0;
    int maxX = 11;
    int minY = 0;
    int maxY = 11;

    AmazonSquare[][] board = new AmazonSquare[maxY + 1][maxX + 1];
    ArrayList<AmazonSquare> whitePieces = new ArrayList<AmazonSquare>();
    ArrayList<AmazonSquare> blackPieces = new ArrayList<AmazonSquare>();

    /**
     * Create the game board object, and set the initial positions of all the amazons
     */
    public AmazonBoard() {

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {

                if (x == minX || x == maxX || y == minY || y == maxY)
                    board[y][x] = new AmazonSquare(x, y, AmazonSquare.PIECETYPE_ARROW);
                else board[y][x] = new AmazonSquare(x, y, AmazonSquare.PIECETYPE_AVAILABLE);

            }
        }

        //TODO: change the positions to scale with the board
        whitePieces.add(setSquare(minX + 1, minY + 4, AmazonSquare.PIECETYPE_AMAZON_WHITE));
        whitePieces.add(setSquare(minX + 4, minX + 1, AmazonSquare.PIECETYPE_AMAZON_WHITE));
        whitePieces.add(setSquare(maxX - 4, minX + 1, AmazonSquare.PIECETYPE_AMAZON_WHITE));
        whitePieces.add(setSquare(maxX - 1, minX + 4, AmazonSquare.PIECETYPE_AMAZON_WHITE));

        blackPieces.add(setSquare(minX + 1, maxY - 4, AmazonSquare.PIECETYPE_AMAZON_BLACK));
        blackPieces.add(setSquare(minX + 4, maxY - 1, AmazonSquare.PIECETYPE_AMAZON_BLACK));
        blackPieces.add(setSquare(maxX - 4, maxY - 1, AmazonSquare.PIECETYPE_AMAZON_BLACK));
        blackPieces.add(setSquare(maxX - 1, maxY - 4, AmazonSquare.PIECETYPE_AMAZON_BLACK));

        System.out.println(this.toString());

        moveAmazon(1, 4, 1, 1);

        System.out.println(this.toString());

        shootArrow(4, 1, 4, 6);

        System.out.println(this.toString());

        generateStrengthValues();
        calculateDistances(AmazonSquare.PIECETYPE_AMAZON_WHITE);
        calculateDistances(AmazonSquare.PIECETYPE_AMAZON_BLACK);

        System.out.println(this.toString());
    }

    /**
     * Gets a list of all the squares in the game.
     * Probably should just use this for testing
     *
     * TODO: Could order this based on the ASCII output of board
     *
     * @return ArrayList of AmazonSquares
     */
    public ArrayList<AmazonSquare> getListOfSquares() {

        ArrayList<AmazonSquare> list = new ArrayList<AmazonSquare>();

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                list.add(getSquare(x, y));

        return list;
    }

    /**
     * Checks all 6 possible directions of movement/shooting for potential open squares.
     * It will iterate away from the position
     * TODO: Move this to an evaluation class
     * TODO: Switch list to set
     *
     * @param color equal to 1 if white, equal to 2 if black
     * @return A list of available squares
     */
    private ArrayList<AmazonSquare> generateListOfValidMoves(int color) {

        //TODO: remove assertions
        assert (color == AmazonSquare.PIECETYPE_AMAZON_WHITE || color == AmazonSquare.PIECETYPE_AMAZON_BLACK);

        ArrayList<AmazonSquare> list = new ArrayList<AmazonSquare>();

        for (AmazonSquare s : (color == AmazonSquare.PIECETYPE_AMAZON_WHITE ? whitePieces : blackPieces))
            list.addAll(generateListOfValidMoves(s));

        return list;
    }

    /**
     * Checks all 6 possible directions of movement/shooting for potential open squares.
     * It will iterate away from the position
     * TODO: Move this to an evaluation class
     *
     * @param square The square to check
     * @return A list of available squares
     */
    private ArrayList<AmazonSquare> generateListOfValidMoves(AmazonSquare square) {

        ArrayList<AmazonSquare> list = new ArrayList<AmazonSquare>();

        for (int moveX = -1; moveX <= 1; moveX++)
            for (int moveY = -1; moveY <= 1; moveY++) {

                if (moveX == 0 && moveY == 0) continue; // skip the center square
                list.addAll(checkLineOfMoves(square.getPosX(), square.getPosY(), moveX, moveY));

            }

        return list;
    }

    /**
     * Checks in a direction based on the increment of moveX, moveY and returns a list of available moves
     * ie. from (0,0), if moveX = 1 and moveY = 0, it will increment through (0,1) to (0,2) to (0,3), etc
     * until an invalid move is found.
     * TODO: Move this to an evaluation class
     *
     * @param posX  The x-position of the square being checked
     * @param posY  The y-postion of the square being checked
     * @param moveX The amount to increment X when checking
     * @param moveY The amount to increment Y when checking
     * @return A list of available moves in the form of arrays as [X,Y]
     */
    private ArrayList<AmazonSquare> checkLineOfMoves(int posX, int posY, int moveX, int moveY) {

        ArrayList list = new ArrayList();

        int n = 0;

        // System.out.println("\nChecking direction " + moveX + ", " + moveY);

        do {

            posX += moveX;
            posY += moveY;

            // System.out.println("Unit '" + getSquare(posX, posY) + "' at " + posX + ", " + posY );

            // Exit if a non-possible move if found
            if (!(getSquare(posX, posY).getPieceType() == AmazonSquare.PIECETYPE_AVAILABLE)) break;

            list.add(getSquare(posX, posY));

        } while (++n < maxX); // just to prevent rogue infinity loop

        return list;
    }

    /**
     * Returns the contents of a particular square
     * Orients from the bottom left, with a range of [1,10]
     *
     * @param xPos The x positions of the square to get
     * @param yPos The y position of the square to get
     * @return The string representing the selected position
     */
    private AmazonSquare getSquare(int xPos, int yPos) {

        //TODO: remove assertions
        assert xPos >= minX;
        assert xPos <= maxX;
        assert yPos >= minY;
        assert yPos <= maxY;

        return board[yPos][xPos];
    }

    /**
     * Sets the type of square at a particular location
     *
     * @param xPos      The x position of the square to set
     * @param yPos      The y position of the square to set
     * @param pieceType 0 - available, 1 - white, 2 - black, 3 - arrow
     * @return
     */
    private AmazonSquare setSquare(int xPos, int yPos, int pieceType) {

        board[yPos][xPos].setPieceType(pieceType);
        return getSquare(xPos, yPos);
    }

    /**
     * Checks to see if a move is on the list of acceptable moves
     * TODO: Move this to an evaluation class
     *
     * @param sInit  The starting square
     * @param sFinal The final square
     * @return Boolean Whether the movement is valid
     */
    private boolean isMoveValid(AmazonSquare sInit, AmazonSquare sFinal) {

        ArrayList<AmazonSquare> moves = generateListOfValidMoves(sInit);

        //System.out.println(Arrays.toString(moves.toArray()));

        return moves.contains(getSquare(sFinal.getPosX(), sFinal.getPosY()));

    }

    /**
     * Moves an amazon from a particular position to another
     * Should only use this for testing
     * TODO: move to GameAction class
     *
     * @param initPosX  The x position of the initial position
     * @param initPosY  The y position of the initial position
     * @param finalPosX The x position of the final position
     * @param finalPosY The y position of the final position
     */
    public void moveAmazon(int initPosX, int initPosY, int finalPosX, int finalPosY) {

        moveAmazon(getSquare(initPosX, initPosY), getSquare(finalPosX, finalPosY));
    }

    /**
     * Moves a queen from a particular space to another
     * TODO: move to GameAction class
     *
     * @param sInit  The initial position
     * @param sFinal The final position
     */
    public void moveAmazon(AmazonSquare sInit, AmazonSquare sFinal) {

        //TODO: remove assertions
        assert (sInit.getPieceType() == AmazonSquare.PIECETYPE_AMAZON_WHITE
                || sInit.getPieceType() == AmazonSquare.PIECETYPE_AMAZON_BLACK);

        assert (isMoveValid(sInit, sFinal));

        switch (sInit.getPieceType()) {
            case AmazonSquare.PIECETYPE_AMAZON_WHITE:
                whitePieces.remove(sInit);
                whitePieces.add(sFinal);
                break;
            case AmazonSquare.PIECETYPE_AMAZON_BLACK:
                blackPieces.remove(sInit);
                blackPieces.add(sFinal);
                break;
        }

        sFinal.setPieceType(sInit.getPieceType());
        sInit.setPieceType(AmazonSquare.PIECETYPE_AVAILABLE);

    }

    /**
     * Shoots an arrow from a particular position to another
     * Should only use this for testing
     * TODO: move to GameAction class
     *
     * @param initPosX  The x position of the initial position
     * @param initPosY  The y position of the initial position
     * @param finalPosX The x position of the final position
     * @param finalPosY The y position of the final position
     */
    public void shootArrow(int initPosX, int initPosY, int finalPosX, int finalPosY) {

        shootArrow(getSquare(initPosX, initPosY), getSquare(finalPosX, finalPosY));

    }

    /**
     * Shoots an arrow from a particular space to another
     * TODO: move to GameAction class
     *
     * @param amazon The square with the amazon
     * @param arrow  The square with the arrow
     */

    public void shootArrow(AmazonSquare amazon, AmazonSquare arrow) {

        //TODO: remove assertions
        assert (amazon.getPieceType() == AmazonSquare.PIECETYPE_AMAZON_WHITE
                || amazon.getPieceType() == AmazonSquare.PIECETYPE_AMAZON_BLACK);

        assert arrow.getPieceType() != AmazonSquare.PIECETYPE_AVAILABLE;

        assert isMoveValid(amazon, arrow);

        arrow.setPieceType(AmazonSquare.PIECETYPE_ARROW);
    }

    /**
     * Calculates the minimum distances between all squares and the closest amazon on a particular team
     * TODO: Move this to evaluation class
     * TODO: Make this actually efficient
     *
     * @param color The color of player in which to calculate min distances from
     */
    public void calculateDistances(int color) {

        //TODO: remove assertions
        assert (color == AmazonSquare.PIECETYPE_AMAZON_WHITE || color == AmazonSquare.PIECETYPE_AMAZON_BLACK);

        Set<AmazonSquare> list = new HashSet<AmazonSquare>();
        List<AmazonSquare> tempList = new ArrayList<AmazonSquare>();

        list.addAll(generateListOfValidMoves(color));

        for (int n = 1; n <= 100; n++) {

            Iterator<AmazonSquare> iterator = list.iterator();

            while (iterator.hasNext()) {

                AmazonSquare s = iterator.next();

                System.out.println("Square (" + s.getPosX() + ", " + s.getPosY() + ") distance = " + s.getDistance(color) + " vs " + n);

                if (s.getDistance(color) > n) {

                    s.setDistance(color, n);
                    tempList.addAll(generateListOfValidMoves(s));
                }

                iterator.remove();
            }

            list.addAll(tempList);
            tempList.clear();

            System.out.println("Calculate Distance: List length = " + list.size());

            if (list.size() == 0) break;

        }
    }

    public void calculateCapturedSquare() {


    }


    /**
     * Iterates though all of the squares on the board to calculate the strength
     * Strength = the number of adjacent open squares
     */
    public void generateStrengthValues() {

        //don't iterate the outer perimeter
        for (int x = minX + 1; x <= maxX - 1; x++)
            for (int y = minY + 1; y <= maxY - 1; y++) {

                if (getSquare(x, y).getPieceType() != AmazonSquare.PIECETYPE_AVAILABLE)
                    getSquare(x, y).setSquareStrength(0);
                else
                    getSquare(x, y).setSquareStrength(calculateSquareStrength(getSquare(x, y)));

            }
    }

    /**
     * Calculates the strength of an individual square
     * Iterates through all adjacent squares, and increments the strength if that square is free
     *
     * @param square The square to calculate the strength of
     * @return The calculated strength value
     */
    public int calculateSquareStrength(AmazonSquare square) {

        int n = 0;

        for (int moveX = -1; moveX <= 1; moveX++)
            for (int moveY = -1; moveY <= 1; moveY++)
                if (getSquare(square.getPosX() + moveX, square.getPosY() + moveY).getPieceType() == AmazonSquare.PIECETYPE_AVAILABLE)
                    n++;

        return n;
    }

    /**
     * Creates a ASCII representation of the piece types on the board
     * X = Arrow
     * O = White
     * * = Black
     * '  ' = Available
     *
     * @return The string representation of the piece types on the board
     */
    public String getPieceString() {
        String s = "";

        for (int y = maxY; y >= minY; y--) { //needs to create s from top to bottom
            for (int x = minX; x <= maxX; x++) {
                switch (getSquare(x, y).getPieceType()) {
                    case AmazonSquare.PIECETYPE_AVAILABLE:
                        s += " ";
                        break;
                    case AmazonSquare.PIECETYPE_ARROW:
                        s += "X";
                        break;
                    case AmazonSquare.PIECETYPE_AMAZON_BLACK:
                        s += "*";
                        break;
                    case AmazonSquare.PIECETYPE_AMAZON_WHITE:
                        s += "O";
                        break;
                    default:
                        s += "E";
                }
            }
            s += "\n";
        }
        return s;
    }

    /**
     * Creates a numerical version of the board with each square listing its strength value
     *
     * @return The string representation of the strength values on the board
     */
    public String getStrengthString() {

        String s = "";

        for (int y = maxY; y >= minY; y--) { //needs to create s from top to bottom
            for (int x = minX; x <= maxX; x++)
                s += (getSquare(x, y).getSquareStrength());
            s += "\n";
        }

        return s;

    }

    /**
     * Creates a numerical version of the board with each square listing it's distance to a color
     * Will only display a max of 9 distance
     * <p>
     * Shows an X for any unavailable spaces
     * <p>
     * TODO: Implement an extended hex to calculate > 9, if necessary
     *
     * @param color The color of player in which to calculate for
     * @return The string representation of the distance value on the board
     */
    public String getDistanceString(int color) {

        String s = "";

        for (int y = maxY; y >= minY; y--) { //needs to create s from top to bottom
            for (int x = minX; x <= maxX; x++) {
                if (getSquare(x, y).getPieceType() != AmazonSquare.PIECETYPE_AVAILABLE)
                    s += "X";
                else {
                    int distance = getSquare(x, y).getDistance(color);
                    s += Math.min(9, distance);
                }
            }
            s += "\n";
        }

        return s;
    }

    /**
     * Prints a ASCII version of the board to the console
     */
    public String toString() {

        String[] pieceTypes = getPieceString().split("\n");
        String[] strengthValues = getStrengthString().split("\n");
        String[] whiteDistance = getDistanceString(AmazonSquare.PIECETYPE_AMAZON_WHITE).split("\n");
        String[] blackDistance = getDistanceString(AmazonSquare.PIECETYPE_AMAZON_BLACK).split("\n");

        String s = "Piece types:   Strength:      White Dis:     Black Dis:" + "\n";

        for (int i = 0; i <= maxY; i++)
            s += (pieceTypes[i] + "   " +
                    strengthValues[i] + "   " +
                    whiteDistance[i] + "   " +
                    blackDistance[i] + "\n"
            );

        return s;

    }
}
