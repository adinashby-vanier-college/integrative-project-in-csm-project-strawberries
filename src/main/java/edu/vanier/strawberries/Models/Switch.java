package edu.vanier.strawberries.Models;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;

import java.net.URL;

public class Switch extends Component {
    private boolean isClosed;
    private double mouseOffsetX;
    private double mouseOffsetY;
    private final ImageView openswitchImageView;

    public Switch(Node begin, Node end, boolean isClosed) {
        super(begin, end);
        this.isClosed = isClosed;

        URL imgUrl = getClass().getResource("/images/open_switch_diagram.png");
        if (imgUrl == null) {
            System.out.println("Could not load open switch image");
        }

        Image openswitchImage = new Image(imgUrl.toExternalForm());
        DIAGRAM_DISPLAY = openswitchImage;
        openswitchImageView = new ImageView(openswitchImage);
        openswitchImageView.setFitWidth(100);
        openswitchImageView.setPreserveRatio(true);
        getChildren().add(openswitchImageView);

        enableDragAndRotate();
    }

    public void toggle() {
        isClosed = !isClosed;
        // Optional: update image to show closed/open state
    }

    public void setOpen(boolean open) {
        this.isClosed = !open;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public ImageView getImageView() {
        return openswitchImageView;
    }

    public Image getSymbol() {
        return openswitchImageView.getImage();
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
