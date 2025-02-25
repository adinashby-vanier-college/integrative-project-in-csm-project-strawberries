package com.example.fractal.Components;
import com.example.fractal.Component;
import com.example.fractal.Node;
import javafx.geometry.Point3D;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.paint.Color;
import java.util.Objects;

public class Wire extends Component {
    private ColorAdjust color;
    private double current;
    private double voltage;
    private double resistance; 

    public Wire(Node begin, Node end, ColorAdjust color, double current, double voltage) {
        super(begin, end);
        this.color = color;
        this.current = current;
        this.voltage = voltage;
        DIAGRAM_DISPLAY = new Image(Objects.requireNonNull(getClass().getResource("/com/example/fractal/images/line.png")).toExternalForm());
        setFitWidth(0);
//        IMAGE_DISPLAY = new Image(Objects.requireNonNull(getClass().getResource("/com/example/fractal/images/imagename.png")).toExternalForm());
        display = DIAGRAM_DISPLAY; //TODO update to take realistic images into account too
        // Playing around with changing wire color!!
        this.setEffect(color);
        this.setImage(display);
    }

    @Override
    public void draw() {
        // calculate angle of rotation
        double x = end.getX()- begin.getX();
        double y = end.getY() - begin.getY();
        double angle = Math.toDegrees(Math.atan(y/x)); // Only works for Q1 and Q4
        if(begin.getX()>end.getX()) {
            if(begin.getY()<end.getY()) angle = 180+angle; // Q3
            else angle = -180+angle; // Q2
        }
        // calculate width
        double width =  Math.sqrt(x*x + y*y);
        // set x and y coords + width and height accordingly
        setFitWidth(width);
        Rotate rotate = new Rotate(angle,begin.getX(), begin.getY());
        getTransforms().clear();
        getTransforms().add(rotate);
    }

    public ColorAdjust getColor() {
        return color;
    }

    public boolean hasCurrent() {
        return current > 0;  // Simple check: if current is greater than 0, it has current :)
    }

    public double getCurrent() {
        return current;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }
    
    // Setter for the resistance of the wire
    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

    // Calculate current based on Ohm's Law
    public void calculateCurrent() {
        if (resistance != 0) {
            this.current = voltage / resistance; 
        } else {
            this.current = 0; 
        }
    }
}
