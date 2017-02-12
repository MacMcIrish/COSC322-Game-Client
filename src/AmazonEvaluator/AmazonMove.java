package AmazonEvaluator;

import AmazonBoard.*;

/**
 * Created by D on 2/12/2017.
 */
public class AmazonMove {

    AmazonSquare sInit, sFinal, arrow;

    public AmazonSquare getInitial() {
        return sInit;
    }

    public AmazonSquare getFinal() {
        return sFinal;
    }

    public AmazonSquare getArrow() {
        return arrow;
    }

    public AmazonMove(AmazonSquare sInit, AmazonSquare sFinal, AmazonSquare arrow) {

        this.sInit = sInit;
        this.sFinal = sFinal;
        this.arrow = arrow;


    }

    public String toString() {

        return sInit.toStringCoords() + ", " + sFinal.toStringCoords() + ", " + arrow.toStringCoords();


    }

}
