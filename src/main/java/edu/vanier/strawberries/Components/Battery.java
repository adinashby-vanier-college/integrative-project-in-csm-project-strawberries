package edu.vanier.strawberries.Components;

import edu.vanier.strawberries.Component;
import edu.vanier.strawberries.Node;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.Objects;

public class Battery extends Component {
    private double potential;
    private boolean startPolarity;
    private boolean endPolarity;
    private ImageView batteryImageView;
    private double mouseOffsetX;
    private double mouseOffsetY;


    public Battery(Node begin, Node end, double potential) {
        super(begin, end);
        this.potential = potential;
        this.startPolarity = true;
        this.endPolarity = false;

        Image batteryImage = new Image(Objects.requireNonNull(getClass().getResource("/images/battery_diagram.png")).toExternalForm());
        this.setImage(batteryImage);
        this.display = batteryImage;
        this.DIAGRAM_DISPLAY = batteryImage;
        this.batteryImageView = new ImageView(batteryImage);

       
        this.setFitWidth(100);
        this.setPreserveRatio(true);

        this.setX(begin.getX());
        this.setY(begin.getY());

       enableDragAndRotate(); // create a method for the drag and rotate
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
            } else { // based on where the mouse moved
                this.setLayoutX(e.getSceneX() - mouseOffsetX);
                this.setLayoutY(e.getSceneY() - mouseOffsetY);
            }
            e.consume(); // basically makes it so that only the battery moves
        });
    }

    public double getPotential() {
        return potential;
    }

    public boolean getStartPolarity() {
        return startPolarity;
    }

    public boolean getEndPolarity() {
        return endPolarity;
    }

    public void setStartPolarity(boolean polarity) {
        this.startPolarity = polarity;
    }

    public void setEndPolarity(boolean polarity) {
        this.endPolarity = polarity;
    }

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

        this.setRotate(angle);
        this.setX(begin.getX());
        this.setY(begin.getY());
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
    inputField.setOnAction(e -> {
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
