package edu.vanier.strawberries.Components;
import edu.vanier.strawberries.Component;
import edu.vanier.strawberries.Node;
import javafx.animation.PathTransition;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.concurrent.atomic.AtomicReference;

public class Wire extends Component {
    private Color color;
    private double current;
    private double voltage;
    private double resistance;
    private Line line;
    private final AtomicReference<String> toMove;
    private PathTransition transition;
    private Circle animatedDot;

    // Used to track the info label
    private Label infoLabel;

    public Wire(Node begin, Node end, Color color, double current, double voltage) {
        super(begin, end);
        this.color = color;
        this.current = current;
        this.voltage = voltage;
        toMove = new AtomicReference<>(null);
    }

    @Override
    public void draw() {
        getChildren().clear();

        line = new Line(begin.getX(), begin.getY(), end.getX(), end.getY());
        line.setFill(color);
        line.setStrokeWidth(4);
        getChildren().add(line);

        double minX = Math.min(begin.getX(), end.getX());
        double maxX = Math.max(begin.getX(), end.getX());
        double minY = Math.min(begin.getY(), end.getY());
        double maxY = Math.max(begin.getY(), end.getY());

        if (selected) {

            if (toMove.get() == null) toMove.set("full");
            begin.getMarker().addEventHandler(MouseEvent.MOUSE_PRESSED, _ -> toMove.set("begin"));
            end.getMarker().addEventHandler(MouseEvent.MOUSE_PRESSED, _ -> toMove.set("end"));
            line.addEventHandler(MouseEvent.MOUSE_PRESSED, _ -> toMove.set("full"));

            getChildren().addAll(begin.getMarker(), end.getMarker());

            begin.getMarker().setTranslateX(((minX == begin.getX()) ? -Math.abs(maxX - minX) / 2 : Math.abs(maxX - minX) / 2));
            begin.getMarker().setTranslateY(((minY == begin.getY()) ? -Math.abs(maxY - minY) / 2 : Math.abs(maxY - minY) / 2));
            end.getMarker().setTranslateX(((minX == end.getX()) ? -Math.abs(maxX - minX) / 2 : Math.abs(maxX - minX) / 2));
            end.getMarker().setTranslateY(((minY == end.getY()) ? -Math.abs(maxY - minY) / 2 : Math.abs(maxY - minY) / 2));
        }

        //set layout x and y
        setLayoutX(minX);
        setLayoutY(minY);
    }

    @Override
    public void handleEdit(MouseEvent event) {
        markAsSelected(true);
        draw();

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
        infoLabel.setStyle("-fx-padding: 4px; -fx-font-size: 10px;");
        infoLabel.getStyleClass().add("info-label");
        // Position it near the wire's midpoint
        double midX = (begin.getX() + end.getX()) / 2;
        double midY = (begin.getY() + end.getY()) / 2;
        infoLabel.setLayoutX(midX + 10);
        infoLabel.setLayoutY(midY - 10);

        parentPane.getChildren().add(infoLabel);

        System.out.println("TOMOVE: "+ toMove.get());

        switch (toMove.get()) {
            case "begin" -> {
                begin.getMarker().setLayoutX(event.getX() - line.getLayoutBounds().getWidth() / 2);
                begin.getMarker().setLayoutY(event.getY() - line.getLayoutBounds().getHeight() / 2);
            }
            case "end" -> {
                end.getMarker().setLayoutX(event.getX() - line.getLayoutBounds().getWidth() / 2);
                end.getMarker().setLayoutY(event.getY() - line.getLayoutBounds().getHeight() / 2);
            }
            case "full" -> {
                setLayoutX(Math.min(begin.getX(), end.getX()));
                setLayoutY(Math.min(begin.getY(), end.getY()));
            }
            default -> markAsSelected(false);
        }

//        // Allow dragging/modifying the wire
//        this.setOnMouseDragged(e -> {
//            Point2D origin = new Point2D(e.getX(), e.getY());
//            boolean editBegin = (origin.distance(new Point2D(begin.getX(), begin.getY())) <= 20);
//            boolean editEnd = (origin.distance(new Point2D(end.getX(), end.getY())) <= 20);
//
//            if (editBegin) begin.setPosition(e.getX(), e.getY());
//            else if (editEnd) end.setPosition(e.getX(), e.getY());
//            else {
//                this.setLayoutX(e.getX());
//                this.setLayoutY(e.getY());
//            }
//            this.draw();

        // Move the label with the wire if it's showing
        if (infoLabel != null) {
            double newMidX = (begin.getX() + end.getX()) / 2;
            double newMidY = (begin.getY() + end.getY()) / 2;
            infoLabel.setLayoutX(newMidX + 10);
            infoLabel.setLayoutY(newMidY - 10);
        }
//        });
        this.draw();
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


    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
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
