package AmazonUI;

import AmazonGame.AmazonPlayer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class AmazonTimerUI extends JPanel {
    private JLabel timeLabel = new JLabel(" ", JLabel.CENTER);
    double nSeconds;

    private AmazonPlayer player;

    public AmazonTimerUI(AmazonPlayer player) {
        this.player = player;


        //setLayout(new BorderLayout());

        //JPanel timerPanel = new JPanel(new FlowLayout());
        JFrame f = new JFrame("Seconds");
        //timerPanel.add(timeLabel);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(timeLabel);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        //timer.schedule(new TimerUI.UpdateUITask(), 0, 100);
        startTimer();



    }

    public void startTimer() {

        if (!(player.getTurnStartTime() < 1)) {
            nSeconds = ((double) player.getGameMoveTime()) - ((double) (System.currentTimeMillis() - player.getTurnStartTime())) / 1000;
        } else {
            nSeconds = 0;
        }
        timeLabel.setText(String.valueOf(nSeconds));
        new javax.swing.Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!(player.getTurnStartTime() < 1)) {
                    nSeconds = ((double) player.getGameMoveTime()) - ((double) (System.currentTimeMillis() - player.getTurnStartTime())) / 1000;
                } else {
                    nSeconds = 0.0;
                }
                timeLabel.setText(String.valueOf(nSeconds));
            }
        }).start();
    }

}