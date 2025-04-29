package edu.vanier.strawberries.Models;

import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import java.net.URL;
import java.util.LinkedList;
import java.util.Objects;

public class Resistor extends Component {
    private double mouseOffsetX;
    private double mouseOffsetY;

    public Resistor(Node begin, Node end, double resistance, boolean skipUI, boolean diagramView) {
        super(begin, end, diagramView);
        this.resistance = resistance;
        this.current = 0;

        if (!skipUI) {
            try {
                URL imgUrl = getClass().getResource("/images/resistor_diagram.png");
                DIAGRAM_DISPLAY = new Image(Objects.requireNonNull(imgUrl).toExternalForm());
                enableDragAndRotate();
            } catch (NullPointerException e) {
                System.out.println("Could not load resistor image");
                DIAGRAM_DISPLAY = null;
            }
        }

        display = diagramView ? DIAGRAM_DISPLAY : IMAGE_DISPLAY;
    }

    public Resistor(Node begin, Node end, double resistance, boolean diagramView) {
        super(begin, end, diagramView);
        this.resistance = resistance;
        this.current = 0;

        try {
            URL imgUrl = getClass().getResource("/images/resistor_diagram.png");
            DIAGRAM_DISPLAY = new Image(Objects.requireNonNull(imgUrl).toExternalForm());
            enableDragAndRotate();
        } catch (NullPointerException e) {
            System.out.println("Could not load resistor image");
            DIAGRAM_DISPLAY = null;
        }
        try {
            URL imgUrl = getClass().getResource("/images/resistor_real.png");
            IMAGE_DISPLAY = new Image(Objects.requireNonNull(imgUrl).toExternalForm());
            enableDragAndRotate();
        } catch (NullPointerException e) {
            System.out.println("Could not load resistor image");
            IMAGE_DISPLAY = null;
        }

        display = diagramView ? DIAGRAM_DISPLAY : IMAGE_DISPLAY;
    }

    public void enableDragAndRotate() {
        this.setOnMousePressed(e -> {
            mouseOffsetX = e.getSceneX() - this.getLayoutX();
            mouseOffsetY = e.getSceneY() - this.getLayoutY();
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

       
    }

public void handleEdit(Pane parentPane) {
    if (parentPane == null) {
        System.out.println("No parent pane found for resistor!");
        return;
    }

    double midX = (begin.getX() + end.getX()) / 2;
    double midY = (begin.getY() + end.getY()) / 2;

    // ❗ Use TextArea, not TextField
    javafx.scene.control.TextArea infoArea = new javafx.scene.control.TextArea();
    infoArea.setEditable(false);
    infoArea.setPrefWidth(180);
    infoArea.setPrefHeight(80);
    infoArea.setStyle("-fx-font-size: 10px; -fx-background-color: white; -fx-border-color: black;");
    infoArea.setText(
            String.format(
                "Current: %.3f A\nVoltage: %.3f V\nResistance: %.3f Ω",
                current, current * resistance, resistance
            )
    );

    infoArea.setLayoutX(midX + 10);
    infoArea.setLayoutY(midY - 30);

    parentPane.getChildren().add(infoArea);
    infoArea.requestFocus();

    infoArea.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
        if (!isNowFocused) {
            parentPane.getChildren().remove(infoArea);
        }
    });

    // 🎯 Double-click the box to change resistance
    infoArea.setOnMouseClicked(e -> {
        if (e.getClickCount() == 2) { // Double-click
            TextField editField = new TextField();
            editField.setPrefWidth(80);
            editField.setLayoutX(midX + 10);
            editField.setLayoutY(midY + 60);
            parentPane.getChildren().add(editField);
            editField.requestFocus();

            editField.setOnAction(_ -> {
                try {
                    double newResistance = Double.parseDouble(editField.getText());
                    if (newResistance >= 0 && newResistance <= 10000) {
                        this.resistance = newResistance;
                        System.out.println("Resistance updated to: " + resistance + " Ω");
                    } else {
                        System.out.println("Resistance must be between 0–10000 Ω");
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid resistance input.");
                }
                parentPane.getChildren().remove(editField);
                parentPane.getChildren().remove(infoArea);
            });

            editField.focusedProperty().addListener((obs2, wasFocused2, isNowFocused2) -> {
                if (!isNowFocused2) {
                    parentPane.getChildren().remove(editField);
                    parentPane.getChildren().remove(infoArea);
                }
            });
        }
    });
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
