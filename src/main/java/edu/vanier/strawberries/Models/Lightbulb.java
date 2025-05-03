package edu.vanier.strawberries.Models;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.net.URL;

public class Lightbulb extends Component {
    private boolean on;
    private double minVoltage;
    private Color color;

    public Lightbulb(Node begin, Node end, Color color, double resistance, boolean diagramView) {
        super(begin, end, diagramView);
        this.color = color;
        minVoltage = 10;

        URL imgURL;
        URL imgURL2;
        try {
            imgURL = getClass().getResource("/images/lightbulb_diagram.png");
            this.DIAGRAM_DISPLAY = new Image(String.valueOf(imgURL));
        }
        catch(Exception e) {
            System.out.println("Unable to find image reference.");
        }
        try {
            imgURL2 = getClass().getResource("/images/lightbulb_real.png");
            this.IMAGE_DISPLAY = new Image(String.valueOf(imgURL2));
        }
        catch(Exception e) {
            System.out.println("Unable to find image reference.");
        }
        display = diagramView ? DIAGRAM_DISPLAY : IMAGE_DISPLAY;
    }

    public double getMinVoltage() {
        return minVoltage;
    }

    public void setMinVoltage(double minVoltage) {
        this.minVoltage = minVoltage;
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

    public void setColor(Color color) {
        this.color = color;
    }

}