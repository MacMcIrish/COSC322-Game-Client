package AmazonGame;

import static AmazonBoard.AmazonBoardCalculator.*;

/**
 * Created by D on 3/24/2017.
 */
public class AmazonConstants {

    public static final double MIN_TURN_LENGTH = 5; //in seconds
    public static final double MAX_TURN_LENGTH = 25; //in seconds
    public static final double INC_TURN_LENGTH = 0.7; //in seconds
    public static final int ROOM_NUMBER = 2;
    public static final int WEIGHT_OF_CAPTURED_SQUARE = 3;

    /*
    MOBILITY_SCORE = 1;
    TERRAIN_SCORE = 2;
    RELATIVE_TERRAIN_SCORE = 3;
    QD_KD_MOB_SCORE = 4;
    KD_SCORE = 5
    CAPTURED_SCORE = 6
    */
    public static final int START_PHASE_CALCULATOR = RELATIVE_TERRAIN_SCORE;
    public static final int LENGTH_OF_START_PHASE = 100;
    public static final int MID_PHASE_CALCULATOR = MOBILITY_SCORE;
    public static final int LENGTH_OF_MID_PHASE = 10;
    public static final int END_PHASE_CALCULATOR = RELATIVE_TERRAIN_SCORE;

    public static final int SCORING_CALCULATOR = 4;

}