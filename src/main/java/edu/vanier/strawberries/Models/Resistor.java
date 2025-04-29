package edu.vanier.strawberries.Models;

import javafx.scene.image.Image;
import java.net.URL;
import java.util.LinkedList;
import java.util.Objects;

public class Resistor extends Component {


    public Resistor(Node begin, Node end, double resistance, boolean skipUI, boolean diagramView) {
        super(begin, end, diagramView);
        this.resistance = resistance;
        this.current = 0;

        if (!skipUI) {
            try {
                URL imgUrl = getClass().getResource("/images/resistor_diagram.png");
                DIAGRAM_DISPLAY = new Image(Objects.requireNonNull(imgUrl).toExternalForm());
            } catch (NullPointerException e) {
                System.out.println("Could not load resistor image");
                DIAGRAM_DISPLAY = null;
            }
        }

        display = diagramView ? DIAGRAM_DISPLAY : IMAGE_DISPLAY;
    }

    public Resistor(Node begin, Node end, double resistance, boolean diagramView) {
        super(begin, end, diagramView);
        this.resistance = resistance;
        this.current = 0;

        try {
            URL imgUrl = getClass().getResource("/images/resistor_diagram.png");
            DIAGRAM_DISPLAY = new Image(Objects.requireNonNull(imgUrl).toExternalForm());
        } catch (NullPointerException e) {
            System.out.println("Could not load resistor image");
            DIAGRAM_DISPLAY = null;
        }
        try {
            URL imgUrl = getClass().getResource("/images/resistor_real.png");
            IMAGE_DISPLAY = new Image(Objects.requireNonNull(imgUrl).toExternalForm());
        } catch (NullPointerException e) {
            System.out.println("Could not load resistor image");
            IMAGE_DISPLAY = null;
        }

        display = diagramView ? DIAGRAM_DISPLAY : IMAGE_DISPLAY;
    }

    public boolean isConnected(Circuit circuit) {
        int resistorIndex = circuit.getIndex(this);
        for (LinkedList<Component> list : circuit.arrayList) {
            for (Component component : list) {
                if (component == this) continue;
                int otherIndex = circuit.getIndex(component);
                if (circuit.checkEdge(resistorIndex, otherIndex)) {
                    return true;
                }
            }
        }
        return false;
    }
}
