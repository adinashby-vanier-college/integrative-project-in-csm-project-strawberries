package com.example.fractal.Components;
import com.example.fractal.Component;
import com.example.fractal.Node;
import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;

import java.util.Objects;

public class Wire extends Component {

    public Wire(Node begin, Node end) {
        super(begin, end);
        DIAGRAM_DISPLAY = new Image(Objects.requireNonNull(getClass().getResource("/com/example/fractal/images/line.png")).toExternalForm());
        setFitWidth(0);
//        IMAGE_DISPLAY = new Image(Objects.requireNonNull(getClass().getResource("/com/example/fractal/images/imagename.png")).toExternalForm());
        this.setImage(DIAGRAM_DISPLAY); //TODO update to take realistic images into account too
    }

    // DOES NOT WORK
    @Override
    public void draw() {
        System.out.println("Wire from " + begin + " to " + end);
        //TODO maybe turn into a calculation using a Vector Class?
        // calculate angle of rotation
        double x = end.getX()- begin.getX();
        double y = end.getY() - begin.getY();
        double angle = Math.toDegrees(Math.atan(y/x));
        System.out.println(angle);
        // calculate width
        double width =  Math.sqrt(x*x + y*y);
        // set x and y coords + width and height accordingly
        setFitWidth(width);
        Rotate rotate = new Rotate(angle,begin.getX(), begin.getY());
        getTransforms().clear();
        getTransforms().add(rotate);
    }
}