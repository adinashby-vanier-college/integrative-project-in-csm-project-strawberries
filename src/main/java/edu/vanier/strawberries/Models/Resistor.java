package edu.vanier.strawberries.Models;

import javafx.scene.image.Image;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.LinkedList;
import java.util.Objects;

public class Resistor extends Component {
    private double mouseOffsetX;
    private double mouseOffsetY;

    
    public Resistor(Node begin, Node end, double resistance, boolean skipUI) {
        super(begin, end);
        this.resistance = resistance;
        this.current = 0;

        if (!skipUI) {
            try {
                URL imgUrl = getClass().getResource("/images/resistor_diagram.png");
                display = new Image(Objects.requireNonNull(imgUrl).toExternalForm());
                enableDragAndRotate();
            } catch (NullPointerException e) {
                System.out.println("Could not load resistor image");
                display = null;
            }
        }
    }

 
    public Resistor(Node begin, Node end, double resistance) {
        super(begin, end);
        this.resistance = resistance;
        this.current = 0;

        URL imgUrl = getClass().getResource("/images/resistor_diagram.png");
        if (imgUrl == null) {
            System.out.println("Could not load resistor image");
        } else {
            display = new Image(imgUrl.toExternalForm());
            enableDragAndRotate();
        }
    }

    public void enableDragAndRotate() {
        this.setOnMousePressed(e -> {
            mouseOffsetX = e.getSceneX() - this.getLayoutX();
            mouseOffsetY = e.getSceneY() - this.getLayoutY();
            e.consume();
        });

        this.setOnMouseDragged(e -> {
            if (e.isSecondaryButtonDown()) {
                double centerX = this.getLayoutX() + this.getBoundsInParent().getWidth() / 2;
                double centerY = this.getLayoutY() + this.getBoundsInParent().getHeight() / 2;
                double angle = Math.toDegrees(Math.atan2(e.getSceneY() - centerY, e.getSceneX() - centerX));
                this.setRotate(angle);
            } else {
                double newX = e.getSceneX() - mouseOffsetX;
                double newY = e.getSceneY() - mouseOffsetY;
                this.setLayoutX(newX);
                this.setLayoutY(newY);
                this.begin.setPosition(newX, newY);
                double deltaX = end.getX() - begin.getX();
                double deltaY = end.getY() - begin.getY();
                this.end.setPosition(newX + deltaX, newY + deltaY);
            }
            e.consume();
        });

        this.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && e.isPrimaryButtonDown()) {
                TextField inputField = new TextField();
                inputField.setLayoutX(this.getLayoutX());
                inputField.setLayoutY(this.getLayoutY() - 25);
                Pane parentPane = (Pane) this.getParent();
                parentPane.getChildren().add(inputField);
                inputField.requestFocus();

                inputField.setOnAction(_-> updateResistanceFromField(inputField, parentPane));
                inputField.setOnKeyPressed(event -> {
                    switch (event.getCode()) {
                        case ENTER -> updateResistanceFromField(inputField, parentPane);
                        case ESCAPE -> parentPane.getChildren().remove(inputField);
                    }
                });
            }
        });
    }

    private void updateResistanceFromField(TextField inputField, Pane parentPane) {
        try {
            double newResistance = Double.parseDouble(inputField.getText());
            if (newResistance >= 0 && newResistance <= 10000) {
                this.resistance = newResistance;
                System.out.println("Resistance updated to: " + resistance + " Ω");
            } else {
                System.out.println("Resistance must be between 0–10000 Ω");
            }
        } catch (NumberFormatException ex) {
            System.out.println("Invalid resistance input.");
        }

        parentPane.getChildren().remove(inputField);
    }

    public boolean isConnected(Circuit circuit) {
        int resistorIndex = circuit.getIndex(this);
        for (LinkedList<Component> list : circuit.arrayList) {
            for (Component component : list) {
                if (component == this) continue;
                int otherIndex = circuit.getIndex(component);
                if (circuit.checkEdge(resistorIndex, otherIndex)) {
                    return true;
                }
            }
        }
        return false;
    }
}
