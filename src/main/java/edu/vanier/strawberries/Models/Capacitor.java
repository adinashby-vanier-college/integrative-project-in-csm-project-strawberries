package edu.vanier.strawberries.Models;

import javafx.scene.image.Image;

import java.net.URL;

public class Capacitor extends Component {
    private double storedEnergy, capacity;
    private boolean isStartTerminal;
    private boolean isEndTerminal;
    private double mouseOffsetX;
    private double mouseOffsetY;

    public Capacitor(Node begin, Node end, double storedEnergy, boolean diagramView) {
        super(begin, end, diagramView);
        this.storedEnergy = storedEnergy;

        // Load the image from resources
        URL imgUrl = getClass().getResource("/images/capacitor_diagram.png"); // Make sure this file exists
        if (imgUrl == null) {
            System.out.println("Could not load capacitor image.");
        }
        else DIAGRAM_DISPLAY = new Image(imgUrl.toExternalForm());
        // Load the image from resources
        URL imgUrl2 = getClass().getResource("/images/capacitor_real.png"); // Make sure this file exists
        if (imgUrl2 == null) {
            System.out.println("Could not load capacitor image.");
        }
        else IMAGE_DISPLAY = new Image(imgUrl2.toExternalForm());

        display = diagramView ? DIAGRAM_DISPLAY : IMAGE_DISPLAY;

        System.out.println(DIAGRAM_DISPLAY);
        System.out.println("is diagram: "+diagramView);
        System.out.println("display: "+display);
    }

    // TODO: Fix the method above to have a text field pop up to input the information for the component

    public double getStoredEnergy() {
        return storedEnergy;
    }

    public boolean isFullyCharged() {
        return storedEnergy == capacity;
    }

}
