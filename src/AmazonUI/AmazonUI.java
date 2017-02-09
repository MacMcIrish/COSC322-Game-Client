package AmazonUI;

import AmazonGame.AmazonGame;
import ygraphs.ai.smart_fox.games.Amazon;

import javax.swing.*;
import java.awt.*;

/**
 * Created by D on 2/8/2017.
 */
public class AmazonUI extends JFrame {

    private AmazonGame game;

    public AmazonUI(AmazonGame game) {

        this.game = game;

        setSize(1300, 800);

        setTitle("Game of the Amazons (COSC 322, UBCO)");

        setLocationRelativeTo(null);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
        //contentPane.add(Box.createVerticalGlue());

        AmazonBoardUI amazonBoardUI = new AmazonBoardUI(game);
        AmazonSideUI amazonSideUI = new AmazonSideUI(game);

        //createGameBoard();
        contentPane.add(amazonBoardUI);
        contentPane.add(amazonSideUI);

        setVisible(true);
        repaint();
    }





}
