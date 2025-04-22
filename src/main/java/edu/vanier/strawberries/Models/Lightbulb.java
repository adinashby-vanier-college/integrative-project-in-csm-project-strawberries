package edu.vanier.strawberries.Models;

import javafx.scene.paint.Color;

import java.net.URL;

public class Lightbulb extends Component {
    public Lightbulb(Node begin, Node end, Color color, double resistance) {
        super(begin, end);

        URL imgURL;
        try {
            imgURL = getClass().getResource("/images/battery_diagra");
//            this.did

        }
        catch(NullPointerException e) {
            System.out.println("Unable to find image reference.\n"+e);
        }
    }


}