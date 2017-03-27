package AmazonUI;

import AmazonBoard.*;
import AmazonGame.*;
import ygraphs.ai.smart_fox.games.BoardGameModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.function.Function;

import AmazonGame.*;

import java.awt.event.*;


/**
 * Created by D on 2/8/2017.
 */
public class AmazonBoardUI extends JLayeredPane {

    AmazonBoard board;

    private AmazonPlayer player;

    private static final long serialVersionUID = 1L;
    private int rows = 10;  //TODO: have these taken from the gameboard
    private int cols = 10;

    int width = 700;
    int height = 700;
    int cellDim = width / 10;
    int offset = 0;//width / 20;

    Image iconQueenWhite = null, iconQueenBlack = null, iconArrow = null, iconTransparent = null, backgroundAtlas;


    HeatMapUI heatMapUI;
    GameBoardUI boardUI;
    BackgroundUI backgroundUI;

    /**
     * Creates the game board on the left of the screen
     *
     * @param player The Player
     */
    public AmazonBoardUI(AmazonPlayer player) {

        this.player = player;

        setPreferredSize(new Dimension(width, height));

        board = player.getBoard();

        boardUI = new GameBoardUI(this);
        heatMapUI = new HeatMapUI(this);
        backgroundUI = new BackgroundUI(this);

        boardUI.setBounds(0, 0, width + 1, height + 1);
        heatMapUI.setBounds(0, 0, width + 1, height + 1 + 50);
        backgroundUI.setBounds(0, 0, width + 1, height + 1 + 50);

        add(boardUI);
        add(heatMapUI);
        add(backgroundUI);
        setLayer(backgroundUI, 1);
        setLayer(heatMapUI, 2);
        setLayer(boardUI, 3);

        // heatMapUI.setFunction(AmazonSquare::getSquareStrength);
    }

    /**
     * Gets the board associated with the UI
     *
     * @return The game board
     */
    private AmazonBoard getBoard() {
        return board;
    }


    /**
     * Get the pixel x coordinate for a particular square on the board
     *
     * @param posX The x value of the board (for 10x10, range is 1-10)
     * @return The pixel x coordinate
     */
    private int panel2pixelX(int posX) {
        return (posX - 1) * getCellDim() + getOffset();
    }

    /**
     * Get the pixel y coordinate for a particular square on the board
     *
     * @param posY The y value of the board (for 10x10, range is 1-10)
     * @return The pixel y coordinate
     */
    private int panel2pixelY(int posY) {
        return (9 - posY + 1) * getCellDim() + getOffset();
    }

    /**
     * Converts a mouse click point to a particular square on the game board
     *
     * @param pixelX The x pixel value of a mouse click
     * @return The x coord of the square being clicked (1-10)
     */
    private int pixel2panelX(int pixelX) {
        return (pixelX - getOffset()) / getCellDim() + 1;
    }

    /**
     * Converts a mouse click point to a particular square on the game board
     *
     * @param pixelY The y pixel value of a mouse click
     * @return The y coord of the square being clicked (1-10)
     */
    private int pixel2panelY(int pixelY) {
        return 9 - (pixelY - getOffset()) / getCellDim() + 1;
    }


    private int getCellDim() {
        return cellDim;
    }

    private int getOffset() {
        return offset;
    }

    private class GameBoardUI extends JPanel {

        JComponent parent;

        public GameBoardUI(JComponent parent) {
            // this.game = game;
            // gameModel = new BoardGameModel(this.rows + 1, this.cols + 1);

            //if(!game.isGamebot){
            //    addMouseListener(new Amazon.GameBoard.GameEventHandler());
            //}

            this.parent = parent;

            addMouseListener(new GameEventHandler());
            loadImages();
        }


        /**
         * Loads the images from files.
         * Should only run once
         */
        private void loadImages() {

            try {
                iconQueenWhite = ImageIO.read(this.getClass().getResourceAsStream("/AmazonUI/whiteTank.png"));
                iconQueenBlack = ImageIO.read(this.getClass().getResourceAsStream("/AmazonUI/blackTank.png"));
                iconArrow = ImageIO.read(this.getClass().getResourceAsStream("/AmazonUI/arrow.png"));
                iconTransparent = ImageIO.read(this.getClass().getResourceAsStream("/AmazonUI/transparent.png"));
                backgroundAtlas = ImageIO.read(this.getClass().getResourceAsStream("/AmazonUI/atlasbg.jpg"));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        protected void paintComponent(Graphics gg) {
            Graphics g = (Graphics2D) gg;

            //  paintHeatmap(g, AmazonSquare::getWhiteKingDistance);
            paintIcons(g);
        }

        /**
         * Paints the icons for the square on the board
         *
         * @param g The graphics for the board
         */
        private void paintIcons(Graphics g) {

            int minX = 1, maxX = 10, minY = 1, maxY = 10; //TODO: have actual variables from gameBoard

            for (int x = minX; x <= maxX; x++)
                for (int y = minY; y <= maxY; y++)
                    g.drawImage(getIcon(getBoard().getSquare(x, y)), panel2pixelX(x), panel2pixelY(y), getCellDim(), getCellDim(), this);

        }

        /**
         * Get the particular icon for a square
         * Needs to have called loadImages() for anything to display
         *
         * @param square The square to get the icon for
         * @return An image of the type of square
         */
        private Image getIcon(AmazonSquare square) {

            switch (square.getPieceType()) {
                //     case AmazonSquare.PIECETYPE_ARROW:
                //       return iconArrow;
                case AmazonSquare.PIECETYPE_AMAZON_WHITE:
                    return iconQueenWhite;
                case AmazonSquare.PIECETYPE_AMAZON_BLACK:
                    return iconQueenBlack;
            }
            return iconTransparent;
        }


        public class GameEventHandler extends MouseAdapter {

            //Variables
            int clicks = 0;
            AmazonSquare sInit, sFinal, sArrow;

            public void mousePressed(MouseEvent e) {

                //TODO: code to handle clicks and movement is just for testing, should be implemented through the humanPlayer

                int xClick = e.getX();
                int yClick = e.getY();

                int xPos = pixel2panelX(xClick);
                int yPos = pixel2panelY(yClick);

                System.out.println("Mouse clicked: (" + xClick + ", " + yClick + ") hit square (" + xPos + ", " + yPos + ")");

                AmazonSquare target = board.getSquare(xPos, yPos);
                System.out.println("White distance: " + target.getDistance(AmazonSquare.PIECETYPE_AMAZON_WHITE, 1) + "\tBlack distance: " + target.getDistance(AmazonSquare.PIECETYPE_AMAZON_BLACK, 1));
                System.out.println("Strength: " + target.getSquareStrength());
/*

                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (clicks == 0) {

                        if (target.getPieceType() == AmazonSquare.PIECETYPE_AMAZON_WHITE ||
                                target.getPieceType() == AmazonSquare.PIECETYPE_AMAZON_BLACK) {
                            sInit = target;
                            clicks++;
                            System.out.println("Selected unit at: (" + xPos + ", " + yPos + ")");
                        } else {
                            System.out.println("No valid unit: (" + xPos + ", " + yPos + ")");
                        }

                    } else if (clicks == 1) {

                        if (target.getPieceType() == AmazonSquare.PIECETYPE_AVAILABLE && board.isMoveValid(sInit, target)) {
                            sFinal = target;
                            clicks++;
                            board.moveAmazon(sInit, sFinal);
                            System.out.println("Amazon moved to: (" + xPos + ", " + yPos + ")");
                            board.calculateBoard();
                            parent.repaint();

                        } else {
                            System.out.println("Invalid move to: (" + xPos + ", " + yPos + ")");

                        }


                    } else if (clicks == 2) {

                        if (board.getSquare(xPos, yPos).getPieceType() == AmazonSquare.PIECETYPE_AVAILABLE && board.isMoveValid(sFinal, target)) {
                            sArrow = target;
                            board.shootArrow(sFinal, sArrow);
                            clicks = 0;
                            board.calculateBoard();
                            parent.repaint();

                        } else {
                            System.out.println("Invalid move to: (" + xPos + ", " + yPos + ")");

                        }

                    }


                }
                if (e.getButton() == MouseEvent.BUTTON3) {

                    board.forceArrow(target);
                    parent.repaint();
                    System.out.println(board.toString());

                }*/
            }


        }
    }//end of GameEventHandler


    private class BackgroundUI extends JPanel implements ActionListener {

        JComponent parent;

        public BackgroundUI(JComponent parent) {
            this.parent = parent;
        }

        /**
         * Required for repaint
         *
         * @param gg The graphics
         */
        protected void paintComponent(Graphics gg) {
            Graphics g = (Graphics2D) gg;
            paintBackground(g);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            repaint();
            parent.repaint();
        }


        /**
         * Paints a heatmap of a particular variable on the board
         * Will create a gradient between the min and max value of the variable, with red being low and green being high
         * TODO: should color under queens show as colored for distance?
         *
         * @param g The graphic for the board
         */
        private void paintBackground(Graphics g) {

            System.out.println("Painting background");

            // int[][] moves = {{-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}};

            int[][] moves = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

            //Exclude outer edge
            for (int x = 1; x <= 10; x++)
                for (int y = 1; y <= 10; y++) {

                    int n = 1000;
                    int val = n*10;

                    for (int[] move : moves) {

                        if (getBoard().getSquare(x + move[0], y + move[1]).getPieceType() == AmazonSquare.PIECETYPE_ARROW)
                            val += n;
                        n /= 10;

                    }

                    int[] coord = getAtlasCoords(val, getBoard().getSquare(x, y).getPieceType() == AmazonSquare.PIECETYPE_ARROW);

                    int xraw = panel2pixelX(x);
                    int yraw =  panel2pixelY(y);

                    int xatlas = (coord[0]-1)*32;
                    int yatlas = (6-coord[1])*32;

                    g.drawImage(backgroundAtlas, xraw, yraw, xraw + getCellDim(), yraw+getCellDim(),
                            xatlas,yatlas, xatlas+32,yatlas+32, this);
                    // 0,0, 32,32, this);

                    // g.drawImage(backgroundAtlas, xraw, yraw, getCellDim(), getCellDim(), this);

                }
        }

        private int[] getAtlasCoords(int val, boolean arrow) {

            if (!arrow) return new int[]{1,1};

            switch (val) {
                case 10000: return new int[]{2,1};
                case 10001: return new int[]{6,5};
                case 10010: return new int[]{4,6};
                case 10011: return new int[]{3,3};
                case 10100: return new int[]{6,3};
                case 10101: return new int[]{6,4};
                case 10110: return new int[]{3,1};
                case 10111: return new int[]{3,2};
                case 11000: return new int[]{6,6};
                case 11001: return new int[]{5,3};
                case 11010: return new int[]{5,6};
                case 11011: return new int[]{4,3};
                case 11100: return new int[]{5,1};
                case 11101: return new int[]{5,2};
                case 11110: return new int[]{4,1};
                case 11111: return new int[]{4,2};

               /* case 10000: return new int[]{4,2};
                case 10001: return new int[]{4,1};
                case 10010: return new int[]{5,2};
                case 10011: return new int[]{5,1};
                case 10100: return new int[]{4,3};
                case 10101: return new int[]{5,6};
                case 10110: return new int[]{5,3};
                case 10111: return new int[]{6,6};
                case 11000: return new int[]{3,2};
                case 11001: return new int[]{3,1};
                case 11010: return new int[]{6,4};
                case 11011: return new int[]{6,3};
                case 11100: return new int[]{3,3};
                case 11101: return new int[]{4,6};
                case 11110: return new int[]{6,5};
                case 11111: return new int[]{6,2};*/
            }

            return new int[]{1,6};
        }

    }


    /**
     * This class shows the heat map under the game board, and shows the options to change the function
     * displaying the heat map
     */
    private class HeatMapUI extends JPanel implements ActionListener {

        Function<AmazonSquare, Integer> mapFunction = null;
        boolean invert = false;

        JRadioButton noneRB, strengthRB, mobilityRB, distanceWhiteQueenRB, distanceBlackQueenRB, distanceWhiteKingRB, distanceBlackKingRB;

        private static final String NONE = "None";
        private static final String STRENGTH = "Strength";
        private static final String MOBILITY = "Mobility";
        private static final String DISTANCEWQ = "WQ Distance";
        private static final String DISTANCEBQ = "BQ Distance";
        private static final String DISTANCEWK = "WK Distance";
        private static final String DISTANCEBK = "BK Distance";

        JComponent parent;

        public HeatMapUI(JComponent parent) {

            this.parent = parent;

            setLayout(new BorderLayout());

            //Setup the radio buttons
            noneRB = new JRadioButton(NONE);
            noneRB.setActionCommand(NONE);

            strengthRB = new JRadioButton(STRENGTH);
            strengthRB.setActionCommand(STRENGTH);

            mobilityRB = new JRadioButton(MOBILITY);
            mobilityRB.setActionCommand(MOBILITY);

            distanceWhiteQueenRB = new JRadioButton(DISTANCEWQ);
            distanceWhiteQueenRB.setActionCommand(DISTANCEWQ);

            distanceBlackQueenRB = new JRadioButton(DISTANCEBQ);
            distanceBlackQueenRB.setActionCommand(DISTANCEBQ);

            distanceWhiteKingRB = new JRadioButton(DISTANCEWK);
            distanceWhiteKingRB.setActionCommand(DISTANCEWK);

            distanceBlackKingRB = new JRadioButton(DISTANCEBK);
            distanceBlackKingRB.setActionCommand(DISTANCEBK);

            ButtonGroup group = new ButtonGroup();
            group.add(noneRB);
            group.add(strengthRB);
            group.add(mobilityRB);
            group.add(distanceWhiteQueenRB);
            group.add(distanceBlackQueenRB);
            group.add(distanceWhiteKingRB);
            group.add(distanceBlackKingRB);

            noneRB.addActionListener(this);
            strengthRB.addActionListener(this);
            mobilityRB.addActionListener(this);
            distanceWhiteQueenRB.addActionListener(this);
            distanceBlackQueenRB.addActionListener(this);
            distanceWhiteKingRB.addActionListener(this);
            distanceBlackKingRB.addActionListener(this);


            JPanel radioPanel = new JPanel(new FlowLayout());
            radioPanel.add(noneRB);
            radioPanel.add(strengthRB);
            radioPanel.add(mobilityRB);
            radioPanel.add(distanceWhiteQueenRB);
            radioPanel.add(distanceBlackQueenRB);
            radioPanel.add(distanceWhiteKingRB);
            radioPanel.add(distanceBlackKingRB);

            noneRB.setSelected(true);

            add(radioPanel, BorderLayout.PAGE_END);

        }

        @Override
        public void actionPerformed(ActionEvent e) {

            Function<AmazonSquare, Integer> f = null;

            switch (e.getActionCommand()) {

                // Set the function in which to generate the heat map with
                // Can set flag for inversion, for values were low is favorable
                case STRENGTH:
                    f = AmazonSquare::getSquareStrength;
                    invert = false;
                    break;
                case MOBILITY:
                    f = AmazonSquare::getMobility;
                    invert = false;
                    break;
                case DISTANCEWQ:
                    f = AmazonSquare::getWhiteQueenDistance;
                    invert = true;
                    break;
                case DISTANCEBQ:
                    f = AmazonSquare::getBlackQueenDistance;
                    invert = true;
                    break;
                case DISTANCEWK:
                    f = AmazonSquare::getWhiteKingDistance;
                    invert = true;
                    break;
                case DISTANCEBK:
                    f = AmazonSquare::getBlackKingDistance;
                    invert = true;
                    break;
                default:
                    f = null;
                    break;
            }

            setFunction(f);

        }

        /**
         * Set the function for the heat map to display
         * Function must have a return type of null, and take no arguments
         *
         * @param f The function in AmazonSquare to show
         */
        public void setFunction(Function<AmazonSquare, Integer> f) {

            mapFunction = f;
            repaint();
            parent.repaint();
        }

        /**
         * Required for repaint
         *
         * @param gg The graphics
         */
        protected void paintComponent(Graphics gg) {
            Graphics g = (Graphics2D) gg;
            paintHeatmap(g);
        }

        /**
         * Paints a heatmap of a particular variable on the board
         * Will create a gradient between the min and max value of the variable, with red being low and green being high
         * TODO: should color under queens show as colored for distance?
         *
         * @param g The graphic for the board
         */
        private void paintHeatmap(Graphics g) {

            if (mapFunction == null) return; //If function is null, prevents painting

            int maxValue = Integer.MIN_VALUE;
            int minValue = Integer.MAX_VALUE;


            // System.out.println(board.toString());

            //Find max value, so can normalize the array
            //Exclude outer edge
            for (int x = 1; x <= 10; x++)
                for (int y = 1; y <= 10; y++) {

                    //Remove any of the MAX_VALUE
                    int value = mapFunction.apply(getBoard().getSquare(x, y));
                    if (value == Integer.MAX_VALUE || value == Integer.MIN_VALUE || value == 0) continue;

                    minValue = Math.min(minValue, value);
                    maxValue = Math.max(maxValue, value);

                }

            //System.out.println("MAXVAL FOR ARRAY: " + maxValue);

            //Fill the grid based on the value relative to maxValue (low is red, mid is orange, high is green)
            //Exclude outer edge
            for (int x = 1; x <= 10; x++)
                for (int y = 1; y <= 10; y++) {

                    //Remove any of the MAX_VALUE
                    int value = mapFunction.apply(getBoard().getSquare(x, y));
                    float colorFraction = ((float) (maxValue - minValue) - (value - minValue)) / (maxValue - minValue);

                    if (value == Integer.MAX_VALUE || value == Integer.MIN_VALUE || value == 0) g.setColor(Color.gray);
                    else if (invert)
                        g.setColor(getHeatMapColor(colorFraction));
                    else
                        g.setColor(getHeatMapColor(1 - colorFraction));

                    g.drawRect(panel2pixelX(x), panel2pixelY(y), width / rows, width / rows);  //TODO: change fill size
                    g.fillRect(panel2pixelX(x), panel2pixelY(y), width / rows, width / rows);  //TODO: change fill size

                }


            paintGrid(g);
        }

        /**
         * Converts the heatmap square fraction to HSB values
         *
         * @param colorFraction Range of [0,1] where 1 is max value and 0 is min value
         * @return Color based on the fraction
         */
        private Color getHeatMapColor(float colorFraction) {

            return Color.getHSBColor(colorFraction * 0.35f, 1f, 1f);

        }

        /**
         * Paints the grid on the board
         *
         * @param g The graphic for the board
         */
        private void paintGrid(Graphics g) {

            int rows = 10; //TODO: have actual variables from gameBoard

            g.setColor(Color.BLACK);

            for (int i = 0; i < rows + 1; i++) {
                g.drawLine(i * getCellDim() + getOffset(), getOffset(), i * getCellDim() + getOffset(), rows * getCellDim() + getOffset());
                g.drawLine(getOffset(), i * getCellDim() + getOffset(), cols * getCellDim() + getOffset(), i * getCellDim() + getOffset());
            }
        }

    }
}

