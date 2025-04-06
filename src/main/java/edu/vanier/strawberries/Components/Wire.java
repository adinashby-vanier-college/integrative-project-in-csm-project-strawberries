package edu.vanier.strawberries.Components;

import edu.vanier.strawberries.Component;
import edu.vanier.strawberries.Node;
import javafx.animation.PathTransition;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;

import java.util.Objects;

public class Wire extends Component {
    private ColorAdjust color;
    private double current;
    private double voltage;
    private double resistance;

    private PathTransition transition;
    private Circle animatedDot;

    // Used to track the info label
    private Label infoLabel;

    public Wire(Node begin, Node end, ColorAdjust color, double current, double voltage) {
        super(begin, end);
        this.color = color;
        this.current = current;
        this.voltage = voltage;
        DIAGRAM_DISPLAY = new Image(Objects.requireNonNull(getClass().getResource("/images/line.png")).toExternalForm());
        setFitWidth(0);
        display = DIAGRAM_DISPLAY;
        this.setEffect(color);
        this.setImage(display);
    }

    @Override
    public void draw() {
        setX(begin.getX());
        setY(begin.getY());

        double x = end.getX() - begin.getX();
        double y = end.getY() - begin.getY();

        double angle = Math.toDegrees(Math.atan(y / x));
        if (begin.getX() > end.getX()) {
            if (begin.getY() < end.getY()) angle = 180 + angle;
            else angle = -180 + angle;
        }

        double width = Math.sqrt(x * x + y * y);
        setFitWidth(width);

        Rotate rotate = new Rotate(angle, begin.getX(), begin.getY());
        getTransforms().clear();
        getTransforms().add(rotate);
    }

    @Override
    public void handleEdit(MouseEvent event) {
        System.out.println(this + " has been clicked");

        Pane parentPane = (Pane) this.getParent();
        if (parentPane == null) return;

        // Remove label if it already exists (toggle)
        if (infoLabel != null && parentPane.getChildren().contains(infoLabel)) {
            parentPane.getChildren().remove(infoLabel);
            infoLabel = null;
            return;
        }

        // Create and style the label
        infoLabel = new Label("V: " + getVoltage() + " V\nI: " + getCurrent() + " A");
        infoLabel.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-padding: 4px; -fx-font-size: 10px;");

        // Position it near the wire's midpoint
        double midX = (begin.getX() + end.getX()) / 2;
        double midY = (begin.getY() + end.getY()) / 2;
        infoLabel.setLayoutX(midX + 10);
        infoLabel.setLayoutY(midY - 10);

        parentPane.getChildren().add(infoLabel);

        // Allow dragging/modifying the wire
        this.setOnMouseDragged(e -> {
            Point2D origin = new Point2D(e.getX(), e.getY());
            boolean editBegin = (origin.distance(new Point2D(begin.getX(), begin.getY())) <= 20);
            boolean editEnd = (origin.distance(new Point2D(end.getX(), end.getY())) <= 20);

            if (editBegin) begin.setPosition(e.getX(), e.getY());
            else if (editEnd) end.setPosition(e.getX(), e.getY());
            else {
                this.setLayoutX(e.getX());
                this.setLayoutY(e.getY());
            }

            this.draw();

            // Move the label with the wire if it's showing
            if (infoLabel != null) {
                double newMidX = (begin.getX() + end.getX()) / 2;
                double newMidY = (begin.getY() + end.getY()) / 2;
                infoLabel.setLayoutX(newMidX + 10);
                infoLabel.setLayoutY(newMidY - 10);
            }
        });
    }

  
    public PathTransition getTransition() {
        return transition;
    }

    public void setTransition(PathTransition transition) {
        this.transition = transition;
    }

    public Circle getAnimatedDot() {
        return animatedDot;
    }

    public void setAnimatedDot(Circle animatedDot) {
        this.animatedDot = animatedDot;
    }

  
    public ColorAdjust getColor() {
        return color;
    }

    public boolean hasCurrent() {
        return current > 0;
    }

    public double getCurrent() {
        return current;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public void setResistance(double resistance) {
        this.resistance = resistance;
    }
    public void setCurrent(double current) {
    this.current = current;
}


    public void calculateCurrent() {
        if (resistance != 0) {
            this.current = voltage / resistance;
        } else {
            this.current = 0;
        }
    }
}
