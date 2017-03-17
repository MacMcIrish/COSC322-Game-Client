package AmazonUI;

import AmazonGame.AmazonPlayer;

import java.awt.EventQueue;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class AmazonTimerUI {

    private Timer timer = new Timer();
    private JLabel timeLabel = new JLabel(" ", JLabel.CENTER);

    private AmazonPlayer player;

    public AmazonTimerUI(AmazonPlayer player) {
        this.player = player;

        JFrame f = new JFrame("Seconds");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(timeLabel);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        timer.schedule(new UpdateUITask(), 0, 1000);
    }

    private class UpdateUITask extends TimerTask {
        float nSeconds;

//        if (!player.turnStartTime == 0) {
//            nSeconds = ((float) player.gameMoveTime) - (System.currentTimeMillis() - player.turnStartTime) / 1000;
//        } else {
//            nSeconds = 0;
//        }


        @Override
        public void run() {
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    timeLabel.setText(String.valueOf(player.turnStartTime));
                }
            });
        }
    }

//    public static void main(String args[]) {
//        EventQueue.invokeLater(new Runnable() {
//
//            @Override
//            public void run() {
//                final AmazonTimerUI clock = new AmazonTimerUI();
//            }
//        });
//    }
}