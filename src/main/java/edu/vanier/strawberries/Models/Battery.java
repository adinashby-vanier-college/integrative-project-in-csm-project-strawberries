package edu.vanier.strawberries.Models;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import java.net.URL;
import java.util.Objects;

public class Battery extends Component {
    private double potential;
    private boolean startPolarity;
    private boolean endPolarity;
    private double mouseOffsetX;
    private double mouseOffsetY;

    public Battery(Node begin, Node end, double potential, boolean diagramView, boolean skipUI) {
        super(begin, end, diagramView);
        this.potential = potential;

        if (!skipUI) {
            try {
                URL imgUrl = getClass().getResource("/images/battery_diagram.png");
                DIAGRAM_DISPLAY = new Image(Objects.requireNonNull(imgUrl).toExternalForm());
            } catch (NullPointerException e) {
                System.out.println("Could not load battery image");
                display = null;
            }
        }

        display = DIAGRAM_DISPLAY;
    }

    public Battery(Node begin, Node end, double potential, boolean diagramView) {
        super(begin, end, diagramView);
        this.potential = potential;

        try{
            URL imgUrl = getClass().getResource("/images/battery_diagram.png"); // debug for the image
            DIAGRAM_DISPLAY = new Image(Objects.requireNonNull(imgUrl).toExternalForm());
        }
        catch(NullPointerException e) {
            System.out.println("Could not load battery image");
            DIAGRAM_DISPLAY = null;
        }
        try{
            URL imgUrl2 = getClass().getResource("/images/battery_real.png"); // debug for the image
            IMAGE_DISPLAY = new Image(Objects.requireNonNull(imgUrl2).toExternalForm());
        }
        catch(NullPointerException e) {
            System.out.println("Could not load battery image");
            IMAGE_DISPLAY = null;
        }

        display = diagramView ? DIAGRAM_DISPLAY : IMAGE_DISPLAY;
    }

    /**
     *
     * @return The potential of the battery
     */
    public double getPotential() {
        return potential;
    }

    /**
     * Getter for polarity of the start (positive side)
     */
    public boolean getStartPolarity() {
        return startPolarity;
    }

    /**
     * Getter for polarity of the end (negative side)
     */
    public boolean getEndPolarity() {
        return endPolarity;
    }

    /**
     * Set the polarity of the start (positive side)
     */
    public void setStartPolarity(boolean polarity) {
        this.startPolarity = polarity;
    }

    /**
     * Set the polarity of the end (negative side)
     */
    public void setEndPolarity(boolean polarity) {
        this.endPolarity = polarity;
    }

    /**
     * Swap the start and end polarities
     */
    public void swapPolarity() {
        boolean temp = startPolarity;
        startPolarity = endPolarity;
        endPolarity = temp;
    }

    /**
     * Show the editing dialog of the battery when it is being edited
     * @param parentPane The layout pane where the editing box will be displayed
     */
    public void handleEdit(Pane parentPane) {
        javafx.scene.control.TextField inputField = new javafx.scene.control.TextField(String.valueOf(potential));
        inputField.setPrefWidth(60);
        inputField.setStyle("-fx-font-size: 10px; -fx-background-color: white; -fx-border-color: black;");
        if (parentPane == null) {
            System.out.println(" No parent pane found for battery!");
            return;
        }

        // Calculate midpoint based on begin and end nodes
        double midX = (begin.getX() + end.getX()) / 2;
        double midY = (begin.getY() + end.getY()) / 2;

        // Add the input field at the center of the battery
        inputField.setLayoutX(midX + 10);
        inputField.setLayoutY(midY - 10);
        parentPane.getChildren().add(inputField);
        inputField.requestFocus();

        inputField.setOnAction(_ -> updateVoltageFromField(inputField, parentPane));
        inputField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                updateVoltageFromField(inputField, parentPane);
            }
        });
    }

    /**
     * Update the battery's voltage based on the user's input from the popup field from {@link #handleEdit(Pane)}
     * @param inputField The input field
     * @param parentPane The layout pane where the editing box was displayed
     */
    private void updateVoltageFromField(javafx.scene.control.TextField inputField, Pane parentPane) {
        try {
            double newPotential = Double.parseDouble(inputField.getText());
            if (newPotential >= 0 && newPotential <= 1000) {
                this.potential = newPotential;
                System.out.println("Voltage updated to: " + potential + " V");
            } else {
                System.out.println("Voltage must be between 0–1000 V");
            }
        } catch (NumberFormatException ex) {
            System.out.println("Invalid voltage input.");
        }

        parentPane.getChildren().remove(inputField);
    }

    /**
     * Display an error message when the user inputs an invalid voltage for the battery
     * @param msg The error message to display
     */
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @Override
    public Component createCopy() {
        return new Battery(Node.copyOf(begin),Node.copyOf(end),potential,diagramView);
    }
}
