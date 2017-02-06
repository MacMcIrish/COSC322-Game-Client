

import ygraphs.ai.smart_fox.games.BoardGameModel;


import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by D on 1/26/2017.
 */
public abstract class Player {

    BoardGameModel gameBoard;

    public void loadBoard(int[][] board) {

    }

    abstract void handleMouseClick(int posX, int posY); //blank method if computer player

    abstract int[] findMove(); //blank if human player

  //  abstract


    //abstract int[]

private int[][] calculateStrength(int[][] gameboard) {

        int[][] strengthboard = new int[9][9];

        for (int posX = 1; posX <= 10; posX++) {
            for (int posY = 1; posY <= 10; posY++) {

                int strength = 0;

                for (int moveX = -1; moveX <= 1 ; moveX++) {
                    for (int moveY = -1; moveY <= 1 ; moveY++) {

                        if (posX + moveX < 1 || posX + moveX > 10) continue;
                        if (posY + moveY < 1 || posY + moveY > 10) continue;

                       // if (getSquare(posX, posY).equalsIgnoreCase(BoardGameModel.POS_AVAILABLE)) strength++;

                    }
                }

                strengthboard[posY-1][posX-1] = strength;

            }
        }

        return strengthboard;
}




}
