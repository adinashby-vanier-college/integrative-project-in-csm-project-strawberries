package com.example.fractal.Components;

import com.example.fractal.Component;
import com.example.fractal.Node;
import javafx.scene.image.Image;

public class Battery extends Component {
    private double potential;  // The potential difference (voltage) of the battery
    private boolean startPolarity; // The positive side (start) of the battery
    private boolean endPolarity;  // The negative side (end) of the battery

    // Constructor with a potential value and polarity settings for start and end
    public Battery(Node begin, Node end, double potential) {
        super(begin, end);
        this.potential = potential;
        this.startPolarity = true;  // Default polarity for the start
        this.endPolarity = false;   // Default polarity for the end
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

    @Override
    public Image getSymbol() {
        return DIAGRAM_DISPLAY;  // Use the symbol image for the battery
    }

    @Override
    public void draw() {
        System.out.println("Battery from " + begin + " to " + end);
        // Add drawing logic if needed, e.g., rendering a battery symbol
    }
}
