package edu.vanier.strawberries.Components;
import edu.vanier.strawberries.Component;
import edu.vanier.strawberries.Node;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
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
        DIAGRAM_DISPLAY = new Image(Objects.requireNonNull(getClass().getResource("/images/line.png")).toExternalForm());
        setFitWidth(0);
//        IMAGE_DISPLAY = new Image(Objects.requireNonNull(getClass().getResource("/com/example/fractal/images/imagename.png")).toExternalForm());
        display = DIAGRAM_DISPLAY; //TODO update to take realistic images into account too
        // Playing around with changing wire color!!
        this.setEffect(color);
        this.setImage(display);
    }

    @Override
    public void draw() {
        //set layout x and y
        setX(begin.getX());
        setY(begin.getY());

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

    @Override
    public void handleEdit(MouseEvent event) {
        System.out.println(this+" has been clicked");
        System.out.println("Clicked: ("+event.getX()+","+event.getY()+")");

        setOnMouseDragged(e -> {
            System.out.println("Dragged: ("+e.getX()+","+e.getY()+")");
            System.out.println("End: ("+end.getX()+","+end.getY()+")");

            //Check if the mouse is on a Node
            Point2D origin = new Point2D(e.getX(),e.getY());
            boolean editBegin = (origin.distance(new Point2D(begin.getX(), begin.getY()))<=20);
            boolean editEnd = (origin.distance(new Point2D(end.getX(), end.getY()))<=20);
            System.out.println("editBegin = "+editBegin+"\teditEnd = "+editEnd);
            if(editBegin) begin.setPosition(e.getX(),e.getY());
            else if(editEnd) end.setPosition(e.getX(),e.getY());
            else {
                //Find displacement vector
                this.setLayoutX(e.getX());
                this.setLayoutY(e.getY());
            }
            this.draw();
        });
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
