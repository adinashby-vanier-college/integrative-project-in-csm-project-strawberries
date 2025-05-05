package edu.vanier.strawberries.Models;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;

public class Lightbulb extends Component {
    private boolean on;
    private double minVoltage;
    private Color color;

    // Shared image references
    private static final Image DIAGRAM_OFF_IMAGE, REALISTIC_OFF_IMAGE;
    private static final Image DIAGRAM_YELLOW_ON, DIAGRAM_RED_ON, DIAGRAM_BLUE_ON, DIAGRAM_GREEN_ON;
  
    static {
        try {
            DIAGRAM_OFF_IMAGE = new Image(Objects.requireNonNull(Lightbulb.class.getResource("/images/lightbulb_diagram.png")).toExternalForm());
            REALISTIC_OFF_IMAGE = new Image(Objects.requireNonNull(Lightbulb.class.getResource("/images/lightbulb_real.png")).toExternalForm());

            DIAGRAM_YELLOW_ON = new Image(Objects.requireNonNull(Lightbulb.class.getResource("/images/lightbulb_diagram_yellow.png")).toExternalForm());
            DIAGRAM_RED_ON = new Image(Objects.requireNonNull(Lightbulb.class.getResource("/images/lightbulb_diagram_red.png")).toExternalForm());
            DIAGRAM_BLUE_ON = new Image(Objects.requireNonNull(Lightbulb.class.getResource("/images/lightbulb_diagram_blue.png")).toExternalForm());
            DIAGRAM_GREEN_ON = new Image(Objects.requireNonNull(Lightbulb.class.getResource("/images/lightbulb_diagram_green.png")).toExternalForm());


        } catch (Exception e) {
            throw new RuntimeException("Failed to load lightbulb images.");
        }
    }

    public Lightbulb(Node begin, Node end, Color color, double resistance, boolean diagramView) {
        super(begin, end, diagramView);
        this.color = color;
        this.minVoltage = 10;

        this.DIAGRAM_DISPLAY = DIAGRAM_OFF_IMAGE;
        this.IMAGE_DISPLAY = REALISTIC_OFF_IMAGE;
        this.display = diagramView ? DIAGRAM_DISPLAY : IMAGE_DISPLAY;
    }

    public double getMinVoltage() {
        return minVoltage;
    }

    public void setMinVoltage(double minVoltage) {
        this.minVoltage = minVoltage;
    }

    public void turnOn(boolean on) {
        this.on = on;

        if (!on) {
            display = diagramView ? DIAGRAM_OFF_IMAGE : REALISTIC_OFF_IMAGE;
        } else {
            if (diagramView) {
                if (color.equals(Color.YELLOW)) display = DIAGRAM_YELLOW_ON;
                else if (color.equals(Color.RED)) display = DIAGRAM_RED_ON;
                else if (color.equals(Color.BLUE)) display = DIAGRAM_BLUE_ON;
                else if (color.equals(Color.GREEN)) display = DIAGRAM_GREEN_ON;
                else display = DIAGRAM_OFF_IMAGE;
          } 
  
        }
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

    @Override
    public Component createCopy() {
        return new Lightbulb(Node.copyOf(begin), Node.copyOf(end), color, resistance, diagramView);
    }
}
