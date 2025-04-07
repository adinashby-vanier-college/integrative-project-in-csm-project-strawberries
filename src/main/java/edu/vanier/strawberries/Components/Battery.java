package edu.vanier.strawberries.Components;

import edu.vanier.strawberries.Component;
import edu.vanier.strawberries.Node;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;

import java.net.URL;
import java.util.Objects;

public class Battery extends Component {
    private double potential;
    private boolean startPolarity;
    private boolean endPolarity;
    private final ImageView batteryImageView;
    private double mouseOffsetX;
    private double mouseOffsetY;

    public Battery(Node begin, Node end, double potential) {
        super(begin, end);
        this.potential = potential;

        URL imgUrl = getClass().getResource("/images/battery_diagram.png"); // debug for the image
        if (imgUrl == null) {
            System.out.println("Could not load battery image");
        }
        Image batteryImage = new Image(imgUrl.toExternalForm());
        DIAGRAM_DISPLAY = batteryImage;
        batteryImageView = new ImageView(batteryImage);
        batteryImageView.setFitWidth(100);
        batteryImageView.setPreserveRatio(true);
        getChildren().add(batteryImageView);
        enableDragAndRotate();
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

    
    @Override
    public Image getSymbol() {
        return batteryImageView.getImage();
    }

    @Override
    public void draw() {
       double deltaX = end.getX() - begin.getX();
       double deltaY = end.getY() - begin.getY();
       double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));

        Rotate rotation = getAngleRotate();

        // Set the rotation of the battery image based on the angle

        display.getTransforms().clear();
        display.getTransforms().add(rotation);
        setLayoutX(begin.getX());
        setLayoutY(begin.getY());
    }

    public ImageView getImageView() {
        return batteryImageView;
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


   @Override
public void handleEdit(MouseEvent event) {
    javafx.scene.control.TextField inputField = new javafx.scene.control.TextField(String.valueOf(potential));
    inputField.setPrefWidth(60);
    inputField.setStyle("-fx-font-size: 10px; -fx-background-color: white; -fx-border-color: black;");

    // Position the input next to the battery
    double midX = (begin.getX() + end.getX()) / 2;
    double midY = (begin.getY() + end.getY()) / 2;
    inputField.setLayoutX(midX + 10);
    inputField.setLayoutY(midY - 10);

    Pane parentPane = (Pane) this.getParent();
    if (parentPane == null) return;

    parentPane.getChildren().add(inputField);
    inputField.requestFocus();

    // When user presses Enter or loses focus
    inputField.setOnAction(_ -> {
        updateVoltageFromField(inputField, parentPane);
    });
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
            System.out.println("Battery voltage updated to: " + potential + " V");
        } else {
            System.out.println("⚠ Voltage must be between 0–1000 V");
        }
    } catch (NumberFormatException ex) {
        System.out.println("⚠ Invalid voltage input.");
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
