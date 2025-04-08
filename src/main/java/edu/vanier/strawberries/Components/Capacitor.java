package edu.vanier.strawberries.Components;

import edu.vanier.strawberries.Component;
import edu.vanier.strawberries.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;

import java.net.URL;

public class Capacitor extends Component {
    private double storedEnergy, capacity;
    private boolean isStartTerminal;
    private boolean isEndTerminal;
    private double mouseOffsetX;
    private double mouseOffsetY;
    private final ImageView capacitorImageView;

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

        Image capacitorImage = new Image(imgUrl.toExternalForm());
        DIAGRAM_DISPLAY = capacitorImage;
        capacitorImageView = new ImageView(capacitorImage);
        capacitorImageView.setFitWidth(100);
        capacitorImageView.setPreserveRatio(true);
        getChildren().add(capacitorImageView);
        enableDragAndRotate();
    }

    public ImageView getImageView() {
        return capacitorImageView;
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

    public Image getSymbol() {
        return capacitorImageView.getImage();
    }

    public double getStoredEnergy() {
        return storedEnergy;
    }

    public boolean isFullyCharged() {
        return storedEnergy == capacity;
    }

    @Override
    public void draw() {
        double deltaX = end.getX() - begin.getX();
        double deltaY = end.getY() - begin.getY();
        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));

        Rotate rotation = getAngleRotate();
        display.getTransforms().clear();
        display.getTransforms().add(rotation);
        setLayoutX(begin.getX());
        setLayoutY(begin.getY());
    }
}
