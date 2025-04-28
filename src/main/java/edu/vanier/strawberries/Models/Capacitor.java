package edu.vanier.strawberries.Models;

import javafx.scene.image.Image;

import java.net.URL;

public class Capacitor extends Component {
    private double storedEnergy, capacity;
    private boolean isStartTerminal;
    private boolean isEndTerminal;
    private double mouseOffsetX;
    private double mouseOffsetY;

    public Capacitor(Node begin, Node end, double storedEnergy, boolean isStartTerminal, boolean isEndTerminal) {
        super(begin, end);
        this.storedEnergy = storedEnergy;
        this.isStartTerminal = isStartTerminal;
        this.isEndTerminal = isEndTerminal;

        // Load the image from resources
        URL imgUrl = getClass().getResource("/images/capacitor_diagram.png"); // Make sure this file exists
        if (imgUrl == null) {
            System.out.println("Could not load capacitor image.");
        }
        else {
            DIAGRAM_DISPLAY = new Image(imgUrl.toExternalForm());
            enableDragAndRotate();
        }
        // Load the image from resources
        URL imgUrl2 = getClass().getResource("/images/capacitor_real.png"); // Make sure this file exists
        if (imgUrl2 == null) {
            System.out.println("Could not load capacitor image.");
        }
        else {
            IMAGE_DISPLAY = new Image(imgUrl2.toExternalForm());
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
    }

    // TODO: Fix the method above to have a text field pop up to input the information for the component


    public double getStoredEnergy() {
        return storedEnergy;
    }

    public boolean isFullyCharged() {
        return storedEnergy == capacity;
    }

}
