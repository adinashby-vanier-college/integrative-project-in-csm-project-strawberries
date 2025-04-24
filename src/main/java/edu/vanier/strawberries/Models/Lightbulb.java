package edu.vanier.strawberries.Models;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.net.URL;

public class Lightbulb extends Component {
    boolean on;

    public Lightbulb(Node begin, Node end, Color color, double resistance) {
        super(begin, end);

        URL imgURL;
        try {
            imgURL = getClass().getResource("/images/lightbulb_diagram.png");
            this.display = new Image(String.valueOf(imgURL));
        }
        catch(Exception e) {
            System.out.println("Unable to find image reference.");
        }
    }

    public void turnOn(boolean on) {
        this.on = on;
    }

    public boolean isOn() {
        return on;
    }

}