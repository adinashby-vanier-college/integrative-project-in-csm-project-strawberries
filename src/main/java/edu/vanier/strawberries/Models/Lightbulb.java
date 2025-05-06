package edu.vanier.strawberries.Models;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;

public class Lightbulb extends Component {
    private boolean on;
    private double minVoltage;
    private Color color;

    // Shared image references
    private static final Image DIAGRAM_OFF_IMAGE, REALISTIC_OFF_IMAGE, REALISTIC_ON_IMAGE;
    private static final Image DIAGRAM_YELLOW_ON, DIAGRAM_RED_ON, DIAGRAM_BLUE_ON, DIAGRAM_GREEN_ON;

    static {
        try {
            DIAGRAM_OFF_IMAGE = new Image(Objects.requireNonNull(Lightbulb.class.getResource("/images/lightbulb-off_diagram.png")).toExternalForm());
            REALISTIC_OFF_IMAGE = new Image(Objects.requireNonNull(Lightbulb.class.getResource("/images/lightbulb-off_real.png")).toExternalForm());
            REALISTIC_ON_IMAGE = new Image(Objects.requireNonNull(Lightbulb.class.getResource("/images/lightbulb-on_real.png")).toExternalForm());

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
        minVoltage = 10;
        this.resistance = resistance;

        DIAGRAM_DISPLAY = DIAGRAM_OFF_IMAGE;
        IMAGE_DISPLAY = REALISTIC_OFF_IMAGE;
        display = diagramView ? DIAGRAM_DISPLAY : IMAGE_DISPLAY;
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
                else DIAGRAM_DISPLAY = DIAGRAM_OFF_IMAGE;
            }
            else {
                IMAGE_DISPLAY = REALISTIC_ON_IMAGE;
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

    /**
     * Creates a new Component of the same type and properties as the current (calling) component
     *
     * @return a new instance of Component
     */
    @Override
    public Component createCopy() {
        return new Lightbulb(Node.copyOf(begin), Node.copyOf(end), color, resistance, diagramView);
    }
}