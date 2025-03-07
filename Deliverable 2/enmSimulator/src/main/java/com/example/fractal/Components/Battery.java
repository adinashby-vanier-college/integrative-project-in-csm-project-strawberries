package com.example.fractal.Components;

import com.example.fractal.Component;
import com.example.fractal.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Battery extends Component {
    private double potential;  
    private boolean startPolarity; 
    private boolean endPolarity;  
    private ImageView batteryImageView;  

    // Constructor with a potential value and polarity settings for start and end
    public Battery(Node begin, Node end, double potential) {
        super(begin, end);
        this.potential = potential;
        this.startPolarity = true;  // Default polarity for the start
        this.endPolarity = false;   // Default polarity for the end

        this.setImage(new Image(getClass().getResource("/com/example/fractal/images/Batterysymbol.png").toExternalForm()));

        this.setX(begin.getX());
        this.setY(begin.getY());
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

        // Set the rotation of the battery image based on the angle

        this.setRotate(angle);
     
        this.setX(begin.getX());
        this.setY(begin.getY());
    }


    public ImageView getImageView() {
        return batteryImageView;
    }
}
