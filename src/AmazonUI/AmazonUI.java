package AmazonUI;

import AmazonBoard.*;
import ygraphs.ai.smart_fox.games.Amazon;
import ygraphs.ai.smart_fox.games.BetterAmazon;
import ygraphs.ai.smart_fox.games.BoardGameModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.function.Function;

/**
 * Created by D on 2/8/2017.
 */
public class AmazonUI extends JFrame {

    AmazonBoard board;

    public AmazonUI() {

        setSize(800, 600);

        setTitle("Game of the Amazons (COSC 322, UBCO)");

        setLocation(200, 200);
        setVisible(true);
        repaint();
        setLayout(null);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(Box.createVerticalGlue());

        board = new AmazonBoard();

        GameBoardUI gameBoardUI = new GameBoardUI();

        //createGameBoard();
        contentPane.add(gameBoardUI, BorderLayout.CENTER);
    }

    private AmazonBoard getBoard() {
        return board;
    }


    public class GameBoardUI extends JPanel {

        private static final long serialVersionUID = 1L;
        private int rows = 10;  //TODO: have these taken from the gameboard
        private int cols = 10;

        int width = 500;
        int height = 500;
        int cellDim = width / 10;
        int offset = width / 20;

        int posX = -1;
        int posY = -1;

        int r = 0;
        int c = 0;

        Image iconQueenWhite = null, iconQueenBlack = null, iconArrow = null, iconTransparent = null;

        Amazon game = null;
        private BoardGameModel gameModel = null;

        boolean playerAMove;

        public GameBoardUI() {
            // this.game = game;
            // gameModel = new BoardGameModel(this.rows + 1, this.cols + 1);

            //if(!game.isGamebot){
            //    addMouseListener(new Amazon.GameBoard.GameEventHandler());
            //}


            loadImages();
        }


        /**
         * Loads the images from files.
         * Should only run once
         */
        private void loadImages() {

            try {
                iconQueenWhite = ImageIO.read(this.getClass().getResourceAsStream("/AmazonUI/whiteQueen.png"));
                iconQueenBlack = ImageIO.read(this.getClass().getResourceAsStream("/AmazonUI/blackQueen.png"));
                iconArrow = ImageIO.read(this.getClass().getResourceAsStream("/AmazonUI/arrow.png"));
                iconTransparent = ImageIO.read(this.getClass().getResourceAsStream("/AmazonUI/transparent.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Get the pixel x coordinate for a particular square on the board
         * @param posX The x value of the board (for 10x10, range is 1-10)
         * @return The pixel x coordinate
         */
        private int getPanelXcoord(int posX) {
            return (posX - 1) * cellDim + offset;
        }

        /**
         * Get the pixel y coordinate for a particular square on the board
         * @param posY The y value of the board (for 10x10, range is 1-10)
         * @return The pixel y coordinate
         */
        private int getPanelYcoord(int posY) {
            return (9 - posY + 1) * cellDim + offset;
        }

        private int getCellDim() {
            return width / 10;
        }

        private int getOffset() {
            return width / 20;
        }

        protected void paintComponent(Graphics gg) {
            Graphics g = (Graphics2D) gg;

            paintHeatmap(g, AmazonSquare::getWhiteKingDistance);
            paintGrid(g);
            paintIcons(g);

        }

        /**
         * Paints the icons for the square on the board
         * @param g The graphics for the board
         */
        private void paintIcons(Graphics g) {

            int minX = 1, maxX = 10, minY = 1, maxY = 10; //TODO: have actual variables from gameBoard

            for (int x = minX; x <= maxX; x++)
                for (int y = minY; y <= maxY; y++)
                    g.drawImage(getIcon(getBoard().getSquare(x, y)), getPanelXcoord(x), getPanelYcoord(y), getCellDim(), getCellDim(), this);

        }

        /**
         * Get the particular icon for a square
         * @param square The square to get the icon for
         * @return An image of the type of square
         */
        private Image getIcon(AmazonSquare square) {

            switch (square.getPieceType()) {
                case AmazonSquare.PIECETYPE_ARROW:
                    return iconArrow;
                case AmazonSquare.PIECETYPE_AMAZON_WHITE:
                    return iconQueenWhite;
                case AmazonSquare.PIECETYPE_AMAZON_BLACK:
                    return iconQueenBlack;
            }
            return iconTransparent;
        }

        /**
         * Paints the grid on the board         *
         *
         * @param g The graphic for the board
         */
        private void paintGrid(Graphics g) {

            int rows = 10; //TODO: have actual variables from gameBoard

            g.setColor(Color.BLACK);

            for (int i = 0; i < rows + 1; i++) {
                g.drawLine(i * getCellDim() + getOffset(), getOffset(), i * getCellDim() + getOffset(), rows * getCellDim() + getOffset());
                g.drawLine(offset, i * getCellDim() + getOffset(), cols * getCellDim() + getOffset(), i * getCellDim() + getOffset());
            }
        }

        /**
         * Paints a heatmap of a particular variable on the board
         * Will create a gradient between the min and max value of the variable, with red being low and green being high
         *
         * @param g The graphic for the board
         * @param f The method to call in AmazonSquare for the heat map to display
         */
        private void paintHeatmap(Graphics g, Function<AmazonSquare, Integer> f) {

            int maxValue = 0;

            //Find max value, so can normalize the array
            //Exclude outer edge
            for (int x = 1; x <= 10; x++)
                for (int y = 1; y <= 10; y++) {

                    //Remove any of the MAX_VALUE
                    int value = f.apply(board.getSquare(x, y));
                    if (value == Integer.MAX_VALUE) value = 0;

                    maxValue = Math.max(maxValue, value);
                }

            //System.out.println("MAXVAL FOR ARRAY: " + maxValue);

            //Fill the grid based on the value relative to maxValue (low is red, mid is orange, high is green)
            //Exclude outer edge
            for (int x = 1; x <= 10; x++)
                for (int y = 1; y <= 10; y++) {

                    //Remove any of the MAX_VALUE
                    int value = f.apply(board.getSquare(x, y));
                    if (value == Integer.MAX_VALUE) value = 0;

                    //System.out.println("Making color with r:" + (((float)maxValue - value)/maxValue) + " g: " + ((float)value/maxValue));

                    //Color is a gradient between red and green. Red when low, and green when high
                    g.setColor(new Color((((float) maxValue - value) / maxValue), ((float) value / maxValue), 0));
                    g.drawRect(getPanelXcoord(x), getPanelYcoord(y), 50, 50);  //TODO: change fill size
                    g.fillRect(getPanelXcoord(x), getPanelYcoord(y), 50, 50);  //TODO: change fill size

                }
        }
    }
}
