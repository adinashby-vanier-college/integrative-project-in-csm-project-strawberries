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
    // Add this to Battery.java
    
public Battery(Node begin, Node end, double potential, boolean skipUI) {
    super(begin, end);
    this.potential = potential;

    if (!skipUI) {
        try {
            URL imgUrl = getClass().getResource("/images/battery_diagram.png");
            display = new Image(Objects.requireNonNull(imgUrl).toExternalForm());
            enableDragAndRotate();
        } catch (NullPointerException e) {
            System.out.println("Could not load battery image");
            display = null;
        }
    }
}


    public Battery(Node begin, Node end, double potential) {
        super(begin, end);
        this.potential = potential;

        try{
            URL imgUrl = getClass().getResource("/images/battery_diagram.png"); // debug for the image
            display = new Image(Objects.requireNonNull(imgUrl).toExternalForm());
            enableDragAndRotate();
        }
        catch(NullPointerException e) {
            System.out.println("Could not load battery image");
            display = null;
        }
        

    }

    public void enableDragAndRotate() {
        this.setOnMousePressed(e -> { // stores information as to where the mouse is from the image's corner
            mouseOffsetX = e.getSceneX() - this.getLayoutX();
            mouseOffsetY = e.getSceneY() - this.getLayoutY();
            e.consume();
        });

        this.setOnMouseDragged(e -> { // calculates angle between components center and the mouse location
            if (e.isSecondaryButtonDown()) { // the right mouse button
                double centerX = this.getLayoutX() + this.getBoundsInParent().getWidth() / 2;
                double centerY = this.getLayoutY() + this.getBoundsInParent().getHeight() / 2;
                double angle = Math.toDegrees(Math.atan2(e.getSceneY() - centerY, e.getSceneX() - centerX));
                this.setRotate(angle); // rotates using trig
            } else {
                double newX = e.getSceneX() - mouseOffsetX;
                double newY = e.getSceneY() - mouseOffsetY;
                this.setLayoutX(newX);
                this.setLayoutY(newY);
                //  update the logical node positions too
                this.begin.setPosition(newX, newY);
                // keep end node relative to the original angle/distance
                double deltaX = end.getX() - begin.getX();
                double deltaY = end.getY() - begin.getY();
                this.end.setPosition(newX + deltaX, newY + deltaY);

            }
            e.consume(); // basically makes it so that only the battery moves
        });
    }

    // Getter for potential (voltage)
    public double getPotential() {
        return potential;
    }

    // Getter for polarity of the start (positive side)
    public boolean getStartPolarity() {
        return startPolarity;
    }

    // Getter for polarity of the end (negative side)
    public boolean getEndPolarity() {
        return endPolarity;
    }

    // Set the polarity of the start (positive side)
    public void setStartPolarity(boolean polarity) {
        this.startPolarity = polarity;
    }

    // Set the polarity of the end (negative side)
    public void setEndPolarity(boolean polarity) {
        this.endPolarity = polarity;
    }

    // Swap the start and end polarities
    public void swapPolarity() {
        boolean temp = startPolarity;
        startPolarity = endPolarity;
        endPolarity = temp;
    }

    public void snapNearbyNode(Node nodeToCheck) {
        Point2D nodePos = new Point2D(nodeToCheck.getX(), nodeToCheck.getY());
        Point2D beginPos = new Point2D(begin.getX(), begin.getY());
        Point2D endPos = new Point2D(end.getX(), end.getY());

        double snapThreshold = 30;

        if (nodePos.distance(beginPos) <= snapThreshold) {
            nodeToCheck.setPosition(begin.getX(), begin.getY());
        } else if (nodePos.distance(endPos) <= snapThreshold) {
            nodeToCheck.setPosition(end.getX(), end.getY());
        }
    }

public void handleEdit(Pane parentPane)
 {
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


    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
