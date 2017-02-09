package AmazonUI;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import AmazonGame.*;

/**
 * Created by D on 2/9/2017.
 */
public class AmazonSideUI extends JPanel implements ActionListener {

    private AmazonGame game;

    public AmazonSideUI(AmazonGame game) {

        this.game = game;

        setPreferredSize(new Dimension(500, 800));

        JoinGameUI joinGameUI = new JoinGameUI();

        add(joinGameUI);


    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public class JoinGameUI extends JPanel implements ActionListener {


        List<String> playerList = Arrays.asList("HumanPlayer", "RandomAI", "SmartAI");

        JComboBox playerListCB;
        JButton joinGameB;

        public JoinGameUI() {
            //playerList = Arrays.asList("Human player", "Random AI", "Smart AI");

            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

            playerListCB = new JComboBox(playerList.toArray(new String[0]));
           // playerListCB.setSelectedIndex(0);
           // playerListCB.addActionListener(this);

            joinGameB = new JButton("Join Game");
            joinGameB.addActionListener(this);
            joinGameB.setActionCommand("selected");

            add(playerListCB);
            add(joinGameB);
        }


        /**
         * Sends the selected player name to the game, which will let you log in to the server
         * @param e Holds the string contained in playerListCB, equal to the class name of the player selected
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            game.joinGame(e.getActionCommand());
        }
    }
}
