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

    //For NxM board:
    int minX = 0;   // N = maxX - minX - 1
    int maxX = 11;
    int minY = 0;   // M = maxY - minY - 1
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
        whitePieces.add(setSquare(minX + 4, minY + 1, AmazonSquare.PIECETYPE_AMAZON_WHITE));
        whitePieces.add(setSquare(maxX - 4, minY + 1, AmazonSquare.PIECETYPE_AMAZON_WHITE));
        whitePieces.add(setSquare(maxX - 1, minY + 4, AmazonSquare.PIECETYPE_AMAZON_WHITE));

        blackPieces.add(setSquare(minX + 1, maxY - 4, AmazonSquare.PIECETYPE_AMAZON_BLACK));
        blackPieces.add(setSquare(minX + 4, maxY - 1, AmazonSquare.PIECETYPE_AMAZON_BLACK));
        blackPieces.add(setSquare(maxX - 4, maxY - 1, AmazonSquare.PIECETYPE_AMAZON_BLACK));
        blackPieces.add(setSquare(maxX - 1, maxY - 4, AmazonSquare.PIECETYPE_AMAZON_BLACK));


        long time = System.currentTimeMillis();


        System.out.println(this.toString());

        moveAmazon(1, 4, 1, 6);

        System.out.println(this.toString());

        shootArrow(1, 6, 2, 6);
        shootArrow(1, 6, 2, 7);
        shootArrow(1, 6, 2, 5);

        System.out.println(this.toString());

        generateStrengthValues();
        calculateDistances();
        generateMobilityValues();

        System.out.println(this.toString());

        int[] score = calculateScore();

        System.out.println("White score: " + score[0] + ", Black score: " + score[1]);


        time = System.currentTimeMillis() - time;

        System.out.println("Milliseconds: " + time);

    }

    /**
     * Gets a list of all the squares in the game.
     * Probably should just use this for testing
     * <p>
     * TODO: Order this based on the ASCII output of board
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
     *
     * @param color equal to 1 if white, equal to 2 if black
     * @return A list of available squares
     */
    private Set<AmazonSquare> generateListOfValidMoves(int color) {
        return generateListOfValidMoves(color, maxX);
    }

    /**
     * Checks all 6 possible directions of movement/shooting for potential open squares.
     * It will iterate away from the position
     * TODO: Move this to an evaluation class
     *
     * @param color    equal to 1 if white, equal to 2 if black
     * @param distance How far to check away from the position (typically 10 for queen, 1 for king)
     * @return A list of available squares
     */
    private Set<AmazonSquare> generateListOfValidMoves(int color, int distance) {

        //TODO: remove assertions
        assert (color == AmazonSquare.PIECETYPE_AMAZON_WHITE || color == AmazonSquare.PIECETYPE_AMAZON_BLACK);

        Set<AmazonSquare> list = new HashSet<AmazonSquare>();

        for (AmazonSquare s : (color == AmazonSquare.PIECETYPE_AMAZON_WHITE ? whitePieces : blackPieces))
            list.addAll(generateListOfValidMoves(s, distance));

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

        return generateListOfValidMoves(square, maxX);
    }

    /**
     * Checks all 6 possible directions within a possible distance of movement/shooting for potential open squares
     * It will iterate away from the position
     * TODO: Move this to an evaluation class
     *
     * @param square  The square to check
     * @param maxStep The max step size for movement (typically 10 for queen, 1 for king)
     * @return A list of available squares
     */
    private ArrayList<AmazonSquare> generateListOfValidMoves(AmazonSquare square, int maxStep) {

        ArrayList<AmazonSquare> list = new ArrayList<AmazonSquare>();

        for (int moveX = -1; moveX <= 1; moveX++)
            for (int moveY = -1; moveY <= 1; moveY++) {

                if (moveX == 0 && moveY == 0) continue; // skip the center square
                list.addAll(checkLineOfMoves(square.getPosX(), square.getPosY(), moveX, moveY, maxStep));

            }

        return list;
    }


    /**
     * Checks in a direction based on the increment of moveX, moveY and returns a list of available moves
     * ie. from (0,0), if moveX = 1 and moveY = 0, it will increment through (1,0) to (2,0) to (3,0), etc
     * until an invalid move is found.
     * TODO: Move this to an evaluation class
     *
     * @param posX  The x-position of the square being checked
     * @param posY  The y-position of the square being checked
     * @param moveX The amount to increment X when checking
     * @param moveY The amount to increment Y when checking
     * @return A list of available moves in the form of arrays as [X,Y]
     */
    private ArrayList<AmazonSquare> checkLineOfMoves(int posX, int posY, int moveX, int moveY, int maxStep) {

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

        } while (++n < maxStep);

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
     * Should only use this directly when setting positions on the board
     *
     * @param xPos      The x position of the square to set
     * @param yPos      The y position of the square to set
     * @param pieceType 0 - available, 1 - white, 2 - black, 3 - arrow
     * @return
     */
    private AmazonSquare setSquare(int xPos, int yPos, int pieceType) {

        getSquare(xPos, yPos).setPieceType(pieceType);
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

        return moves.contains(sFinal);

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
     * Calculates the queen and king distances for both players
     * TODO: Move this to evaluation class
     */
    public void calculateDistances() {

        calculateQueenDistances(AmazonSquare.PIECETYPE_AMAZON_WHITE);
        calculateKingDistances(AmazonSquare.PIECETYPE_AMAZON_WHITE);

        calculateQueenDistances(AmazonSquare.PIECETYPE_AMAZON_BLACK);
        calculateKingDistances(AmazonSquare.PIECETYPE_AMAZON_BLACK);

    }


    /**
     * Using queen movement, calculates the minimum distances between all squares and the closest amazon on a particular team
     * TODO: Move this to evaluation class
     *
     * @param color The color of player in which to calculate min distances from
     */
    public void calculateQueenDistances(int color) {
        calculateDistances(color, AmazonSquare.DISTANCE_QUEEN);
    }

    /**
     * Using king movement, calculates the minimum distances between all squares and the closest amazon on a particular team
     * TODO: Move this to evaluation class
     *
     * @param color The color of player in which to calculate min distances from
     */
    public void calculateKingDistances(int color) {
        calculateDistances(color, AmazonSquare.DISTANCE_KING);
    }

    /**
     * Calculates the minimum distances between all squares and the closest amazon on a particular team
     * TODO: Move this to evaluation class
     * TODO: Make this actually efficient
     * TODO: Use different variable for queenOrKing - is not particularly descriptive or helpful
     *
     * @param color   The color of player in which to calculate min distances from
     * @param queenOrKing The max step size for movement (10 for queen, 1 for king)
     */
    public void calculateDistances(int color, int queenOrKing) {

        //TODO: remove assertions
        assert (color == AmazonSquare.PIECETYPE_AMAZON_WHITE || color == AmazonSquare.PIECETYPE_AMAZON_BLACK);

        Set<AmazonSquare> list = new HashSet<AmazonSquare>();
        List<AmazonSquare> tempList = new ArrayList<AmazonSquare>();

        list.addAll(generateListOfValidMoves(color, queenOrKing));

        for (int n = 1; n <= (maxX*maxY); n++) { // maxX * maxY is the theoretical max moves, but will never actually be hit

            Iterator<AmazonSquare> iterator = list.iterator(); // Have to use an iterator, since you can't add/remove from a list in a for loop

            while (iterator.hasNext()) {

                AmazonSquare s = iterator.next();

               //System.out.println("Square (" + s.getPosX() + ", " + s.getPosY() + ") distance = " + s.getDistance(color, queenOrKing) + " vs " + n);

                if (s.getDistance(color, queenOrKing) > n) { //if the square has a higher distance than is being checked
                    s.setDistance(color, n, queenOrKing);    // then set to the new distance
                    tempList.addAll(generateListOfValidMoves(s, queenOrKing)); // and add all of the possible moves from the square
                }

                iterator.remove(); //remove the square that was just checked from the set
            }

            list.addAll(tempList); //add all of the new squares, if any
            tempList.clear();

           // System.out.println("Calculate Distance: List length = " + list.size());

            if (list.size() == 0) break; //once all squares are at the minimum possible distance, exit

        }
    }

    /**
     * Will set all distances to the max value
     */
    public void resetDistances() {

        for (AmazonSquare s : getListOfSquares()) s.resetDistances();

    }


    /**
     * Run only after calculateDistances has been run, otherwise all squares will show as captured
     *  If distance = Max_int, then a particular color isn't able to reach that square, meaning the square is
     *  captured, and we don't need to do anything to it anymore
     *
     *  TODO: Haven't actually tested this yet
     */
    public void calculateCapturedSquares() {

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)

                if (getSquare(x, y).getQueenDistance(AmazonSquare.PIECETYPE_AMAZON_WHITE) == Integer.MAX_VALUE
                        || getSquare(x, y).getQueenDistance(AmazonSquare.PIECETYPE_AMAZON_BLACK) == Integer.MAX_VALUE)
                    getSquare(x, y).setCaptured(true);
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
     * Calculates the overall score of the board
     * Should be called after calling getDistances methods, otherwise score will be zero
     * Score = the number of squares where player distance < opponent distance
     * TODO: int[] return is gross, should change it
     *
     * @return An int array where a[0] = whiteScore and a[1] = blackScore
     */
    public int[] calculateScore() {

        int whiteScore = 0;
        int blackScore = 0;

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++) {
                int diff = getSquare(x, y).getQueenDistance(AmazonSquare.PIECETYPE_AMAZON_WHITE) - getSquare(x, y).getQueenDistance(AmazonSquare.PIECETYPE_AMAZON_BLACK);

                if (diff < 0) whiteScore++;
                else if (diff > 0) blackScore++;
                else continue;
            }

        return new int[]{whiteScore, blackScore};

    }

    /**
     * Calculates mobility of a queen, defined as the
     * As per An evaluation function for the game of amazons by Jens Lieberum:
     * http://ac.els-cdn.com/S0304397505005979/1-s2.0-S0304397505005979-main.pdf?_tid=d829bf2c-edc3-11e6-9fba-00000aab0f27&acdnat=1486533788_fbd052d744bf4a318972608ab142ac17
     *
     * For the length of each queens move, it calculates the (square strength) / 2^(kings move distance) and sums it to get mobility
     *
     * @param amazon The queen in which to check
     * @return The mobility value
     */
    public int calculateSquareMobility(AmazonSquare amazon) {

        //assert (whitePieces.contains(amazon) || blackPieces.contains(amazon));

        double mobility = 0;
        ArrayList<AmazonSquare> list;

        for (int moveX = -1; moveX <= 1; moveX++)
            for (int moveY = -1; moveY <= 1; moveY++) {
                list = checkLineOfMoves(amazon.getPosX(), amazon.getPosY(), moveX, moveY, maxX);
                for (int i = 0; i < list.size(); i++)
                    mobility += (list.get(i).getSquareStrength() / (Math.pow(2, i)));
            }

            return (int) mobility;
    }

    /**
     * Iterates through the board and calculates the mobility scores for each square     *
     * TODO: Should combine this with the strength calculation
     */
    public void generateMobilityValues() {

        //don't iterate the outer perimeter
        for (int x = minX+1; x <= maxX-1; x++)
            for (int y = minY+1; y <= maxY-1; y++) {

                if (getSquare(x, y).getPieceType() == AmazonSquare.PIECETYPE_ARROW) //ignore arrow squares
                    getSquare(x, y).setMobility(0);
                else
                    getSquare(x, y).setMobility(calculateSquareMobility(getSquare(x, y)));

                System.out.println("Mobility for (" + x + " , " + y + "): " + getSquare(x, y).getMobility());
            }
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
    public String getQueenDistanceString(int color) {

        String s = "";

        for (int y = maxY; y >= minY; y--) { //needs to create s from top to bottom
            for (int x = minX; x <= maxX; x++) {
                switch(getSquare(x, y).getPieceType()) {
                    case AmazonSquare.PIECETYPE_AVAILABLE:
                        int distance = getSquare(x, y).getQueenDistance(color);
                        s += (Integer.toHexString(Math.min(15, distance))).toUpperCase();
                        break;
            default:
                        s += "X";
                        break;
                }
            }
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
     * TODO: should be combined somehow with the queen distance string method to avoid code duplication
     *
     * @param color The color of player in which to calculate for
     * @return The string representation of the distance value on the board
     */
    public String getKingDistanceString(int color) {

        String s = "";

        for (int y = maxY; y >= minY; y--) { //needs to create s from top to bottom
            for (int x = minX; x <= maxX; x++) {
                switch (getSquare(x, y).getPieceType()) {
                    case AmazonSquare.PIECETYPE_AVAILABLE:
                        int distance = getSquare(x, y).getKingDistance(color);
                        s += (Integer.toHexString(Math.min(15, distance))).toUpperCase();
                        break;
                    default:
                        s += "X";
                        break;
                }
            }
            s += "\n";
        }

        return s;
    }

    /**
     * Creates a Hex version of the mobility value, scaled based on the max mobility value: (value/maxvalue)*15
     * TODO: Create an extended hex system to have a better range
     *
     * @return The ASCII representation of the mobility values for each square
     */
    public String getMobilityString() {

        List<AmazonSquare> list = getListOfSquares();
        int max = 0;

        for (AmazonSquare s : list) if (s.getMobility() > max) max = s.getMobility();

        String s = "";

        for (int y = maxY; y >= minY; y--) { //needs to create s from top to bottom
            for (int x = minX; x <= maxX; x++) {
                double val = ((double)getSquare(x, y).getMobility()/(max+1))*16; //put max+1 to avoid divide by zero error
                s += Integer.toHexString(Math.min((int)val,15)).toUpperCase();
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
        String[] whiteQueenDistance = getQueenDistanceString(AmazonSquare.PIECETYPE_AMAZON_WHITE).split("\n");
        String[] blackQueenDistance = getQueenDistanceString(AmazonSquare.PIECETYPE_AMAZON_BLACK).split("\n");
        String[] whiteKingDistance = getKingDistanceString(AmazonSquare.PIECETYPE_AMAZON_WHITE).split("\n");
        String[] blackKingDistance = getKingDistanceString(AmazonSquare.PIECETYPE_AMAZON_BLACK).split("\n");
        String[] mobilityValue = getMobilityString().split("\n");

        String s = "Piece types:   Strength:      Mobility:      White Q Dis:   Black Q Dis:   White K Dis:   Black K Dis:   " + "\n";

        for (int i = 0; i <= maxY; i++)
            s += (pieceTypes[i] + "   " +
                    strengthValues[i] + "   " +
                    mobilityValue[i]  + "   " +
                    whiteQueenDistance[i] + "   " +
                    blackQueenDistance[i] + "   " +
                    whiteKingDistance[i] + "   " +
                    blackKingDistance[i] + "\n"
            );

        return s;

    }
}