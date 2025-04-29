package edu.vanier.strawberries.Models;

public class Fuse extends Component {
    private boolean blown;
    private double maxCurrent;

    public Fuse(Node begin, Node end, double maxVoltage, boolean diagramView) {
        super(begin, end, diagramView);

        display = diagramView ? DIAGRAM_DISPLAY : IMAGE_DISPLAY;
    }

    public boolean isBlown() {
        return blown;
    }

    public void setMaxCurrent(double maxCurrent) {
        this.maxCurrent = maxCurrent;
    }
}

