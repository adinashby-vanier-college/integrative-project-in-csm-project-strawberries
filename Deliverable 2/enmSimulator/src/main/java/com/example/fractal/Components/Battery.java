package com.example.fractal.Components;
import com.example.fractal.Component;
import com.example.fractal.Node;
import javafx.scene.image.Image;

public class Battery extends Component {
    private double potential;
    private boolean start;
    private boolean End; // got to change the name bc we already have a end node

    public Battery(Node begin, Node end, double potential) {
        super(begin, end);
        this.potential = potential;
        this.start = start;
        this.End = End;
    }

    public double getPotential() {
        return potential;
    }
    public boolean getPolarity() {
        return start;
    }
    public void setPolarity(boolean polarity){
        this.start = polarity;
    }
    @Override
    public Image getSymbol() {
        return DIAGRAM_DISPLAY;
    }

    @Override
    public void draw() {
        System.out.println("Battery from " + begin + " to " + end);
    }
}