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


    public AmazonUI() {

        setSize(1000, 1000);

        setTitle("Game of the Amazons (COSC 322, UBCO)");

        setLocation(200, 200);
        setVisible(true);
        repaint();
        setLayout(null);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(Box.createVerticalGlue());

        AmazonBoardUI AmazonBoardUI = new AmazonBoardUI();

        //createGameBoard();
        contentPane.add(AmazonBoardUI, BorderLayout.CENTER);
    }
}
