package edu.vanier.strawberries.Models;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.net.URL;

public class Lightbulb extends Component {
    private boolean on;
    private double minVoltage;
    private Color color;

    public Lightbulb(Node begin, Node end, Color color, double resistance) {
        super(begin, end);
        this.color = color;

        URL imgURL;
        try {
            imgURL = getClass().getResource("/images/lightbulb_diagram.png");
            this.display = new Image(String.valueOf(imgURL));
        }
        catch(Exception e) {
            System.out.println("Unable to find image reference.");
        }

        on = true;
    }

    public double getMinVoltage() {
        return minVoltage;
    }

    public double getLightIntensity() {
        if(voltage >= minVoltage) return voltage/2*minVoltage;
        else return 0;
    }

    public void turnOn(boolean on) {
        this.on = on;
    }

    public boolean isOn() {
        return on;
    }

    public Color getColor() {
        return color;
    }

}