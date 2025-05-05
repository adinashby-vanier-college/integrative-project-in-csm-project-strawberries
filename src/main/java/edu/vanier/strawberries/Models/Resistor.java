package edu.vanier.strawberries.Models;


import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.LinkedList;
import java.util.Objects;

public class Resistor extends Component {

    //contructor
   public Resistor(Node begin, Node end, double resistance, boolean skipUI, boolean diagramView) {
        super(begin, end, diagramView);
        this.resistance = resistance;
        this.current = 0;

        if (!skipUI) {
            try {
                URL imgUrl = getClass().getResource("/images/resistor_diagram.png");
                DIAGRAM_DISPLAY = new Image(Objects.requireNonNull(imgUrl).toExternalForm());
            } catch (NullPointerException e) {
                System.out.println("Could not load resistor_diagram.png");
                DIAGRAM_DISPLAY = null;
            }

            try {
                URL imgUrl = getClass().getResource("/images/resistor_real.png");
                IMAGE_DISPLAY = new Image(Objects.requireNonNull(imgUrl).toExternalForm());
            } catch (NullPointerException e) {
                System.out.println("Could not load resistor_real");
                IMAGE_DISPLAY = null;
            }
        }

        display = diagramView ? DIAGRAM_DISPLAY : IMAGE_DISPLAY;

        if (!skipUI) {
            this.setOnMouseClicked(e -> {
                if (e.getClickCount() == 1 && this.getParent() instanceof Pane pane) {
                    handleEdit(pane);
                } else if (e.getClickCount() == 2 && this.getParent() instanceof Pane pane) {
                    showInfoBox(pane);
                }
            });
        }
    }

    // Convenience constructor for real app usage (UI enabled)
    public Resistor(Node begin, Node end, double resistance, boolean diagramView) {
        this(begin, end, resistance, false, diagramView);
    }

     public static Resistor createForTest(Node begin, Node end, double resistance) {
        return new Resistor(begin, end, resistance, true, false);
    }

    /**
     * Show the editing dialog of the resistor when it is being edited
     * @param parentPane The layout pane where the editing box will be displayed
     */
    public void handleEdit(Pane parentPane) {
        TextField inputField = new TextField(String.valueOf(resistance));
        inputField.setPrefWidth(60);
        inputField.setStyle("-fx-font-size: 10px; -fx-background-color: white; -fx-border-color: black;");

        if (parentPane == null) {
            System.out.println("No parent pane found for resistor!");
            return;
        }

        double midX = (begin.getX() + end.getX()) / 2;
        double midY = (begin.getY() + end.getY()) / 2;

        inputField.setLayoutX(midX + 10);
        inputField.setLayoutY(midY - 10);
        parentPane.getChildren().add(inputField);
        inputField.requestFocus();

        inputField.setOnAction(_ -> updateResistanceFromField(inputField, parentPane));
        inputField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                updateResistanceFromField(inputField, parentPane);
            }
        });
    }

    /**
     * Update the resistor's resistance based on the user's input from the popup field from {@link #handleEdit(Pane)}
     * @param inputField The input field
     * @param parentPane The layout pane where the editing box was displayed
     */
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

    /**
     *
     * @implNote This method is called when the resistor is double-clicked
     * @param parentPane The layout pane to display the info box on
     */
    public void showInfoBox(Pane parentPane) {
        TextArea infoArea = new TextArea();
        infoArea.setEditable(false);
        infoArea.setPrefWidth(180);
        infoArea.setPrefHeight(80);
        infoArea.setStyle("-fx-font-size: 10px; -fx-background-color: white; -fx-border-color: black;");
        infoArea.setText(String.format(
                "Current: %.3f A\nVoltage: %.3f V\nResistance: %.3f Ω",
                current, current * resistance, resistance
        ));

        double midX = (begin.getX() + end.getX()) / 2;
        double midY = (begin.getY() + end.getY()) / 2;

        infoArea.setLayoutX(midX + 10);
        infoArea.setLayoutY(midY - 50);
        parentPane.getChildren().add(infoArea);
        infoArea.toFront();
        infoArea.requestFocus();

        infoArea.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                parentPane.getChildren().remove(infoArea);
            }
        });
        
        
        infoArea.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case ESCAPE, ENTER -> parentPane.getChildren().remove(infoArea);
            }
        });
    }

    /**
     * Checks if it is connected to the rest of the circuit
     * @param circuit The circuit
     * @return True if it is connected
     */
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

    /**
     * Creates a new Component of the same type and properties as the current (calling) component
     *
     * @return a new instance of Component
     */
    @Override
    public Component createCopy() {
        return new Resistor(Node.copyOf(begin),Node.copyOf(end),resistance,diagramView);
    }
}
