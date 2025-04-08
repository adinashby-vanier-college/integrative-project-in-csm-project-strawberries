package edu.vanier.strawberries.Components;

import edu.vanier.strawberries.Component;
import edu.vanier.strawberries.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;

import java.net.URL;

public class Resistor extends Component {
    private double resistance;
    private double current;
    private double voltage;
    private double mouseOffsetX;
    private double mouseOffsetY;
    private final ImageView resistorView;

    public Resistor(Node begin, Node end, double resistance) {
        super(begin, end);
        this.resistance = resistance;
        this.current = 0;

        URL imgUrl = getClass().getResource("/images/resistor_diagram.png");
        if (imgUrl == null) {
            System.out.println("Could not load resistor image");
        }

        Image resistorImage = new Image(imgUrl.toExternalForm());
        DIAGRAM_DISPLAY = resistorImage;
        resistorView = new ImageView(resistorImage);
        resistorView.setFitWidth(100);
        resistorView.setPreserveRatio(true);
        getChildren().add(resistorView);

        enableDragAndRotate();
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

        // Double-click to edit resistance
        this.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && e.isPrimaryButtonDown()) {
                TextField inputField = new TextField();
                inputField.setLayoutX(this.getLayoutX());
                inputField.setLayoutY(this.getLayoutY() - 25); // slightly above the component

                Pane parentPane = (Pane) this.getParent();
                parentPane.getChildren().add(inputField);
                inputField.requestFocus();

                inputField.setOnAction(event -> updateResistanceFromField(inputField, parentPane));
                inputField.setOnKeyPressed(event -> {
                    switch (event.getCode()) {
                        case ENTER -> updateResistanceFromField(inputField, parentPane);
                        case ESCAPE -> parentPane.getChildren().remove(inputField);
                    }
                });
            }
        });
    }

    // TODO: Fix the method above to have a text field pop up to input the information for the component

    public ImageView getImageView() {
        return resistorView;
    }

    public Image getSymbol() {
        return resistorView.getImage();
    }

    public double getResistance() {
        return resistance;
    }

    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public void calculateCurrent(double voltage) {
        this.voltage = voltage;
        this.current = (resistance != 0) ? voltage / resistance : 0;
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
