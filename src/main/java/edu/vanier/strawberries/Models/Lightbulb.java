package edu.vanier.strawberries.Models;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;

public class Lightbulb extends Component {
    private boolean on;
    private double minVoltage;
    private Color color;

    // Shared image references
    private static final Image OFF_IMAGE;
    private static final Image YELLOW_ON, RED_ON, BLUE_ON, GREEN_ON;
    private static final Image DIAGRAM_IMAGE, REALISTIC_IMAGE;

    static {
        try {
            OFF_IMAGE = new Image(Objects.requireNonNull(Lightbulb.class.getResource("/images/lightbulb_real.png")).toExternalForm());
            YELLOW_ON = new Image(Objects.requireNonNull(Lightbulb.class.getResource("/images/lightbulb_diagram_yellow.png")).toExternalForm());
            RED_ON = new Image(Objects.requireNonNull(Lightbulb.class.getResource("/images/lightbulb_diagram_red.png")).toExternalForm());
            BLUE_ON = new Image(Objects.requireNonNull(Lightbulb.class.getResource("/images/lightbulb_diagram_blue.png")).toExternalForm());
            GREEN_ON = new Image(Objects.requireNonNull(Lightbulb.class.getResource("/images/lightbulb_diagram_green.png")).toExternalForm());

            DIAGRAM_IMAGE = new Image(Objects.requireNonNull(Lightbulb.class.getResource("/images/lightbulb_diagram.png")).toExternalForm());
            REALISTIC_IMAGE = new Image(Objects.requireNonNull(Lightbulb.class.getResource("/images/lightbulb_real.png")).toExternalForm());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load lightbulb images.");
        }
    }

    public Lightbulb(Node begin, Node end, Color color, double resistance, boolean diagramView) {
        super(begin, end, diagramView);
        this.color = color;
        this.minVoltage = 10;

        this.DIAGRAM_DISPLAY = DIAGRAM_IMAGE;
        this.IMAGE_DISPLAY = OFF_IMAGE;
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
            display = OFF_IMAGE;
        } else {
            if (color.equals(Color.YELLOW)) {
                display = YELLOW_ON;
            } else if (color.equals(Color.RED)) {
                display = RED_ON;
            } else if (color.equals(Color.BLUE)) {
                display = BLUE_ON;
            } else if (color.equals(Color.GREEN)) {
                display = GREEN_ON;
            } else {
                display = OFF_IMAGE; // fallback
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
