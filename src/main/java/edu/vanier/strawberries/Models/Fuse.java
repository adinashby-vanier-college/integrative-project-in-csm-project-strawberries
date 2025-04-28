package edu.vanier.strawberries.Models;

public class Fuse extends Component {
    private boolean blown;
    private double maxVoltage;

    public Fuse(Node begin, Node end, double maxVoltage) {
        super(begin, end);
    }

    public boolean isBlown() {
        return blown;
    }

    public void setMaxVoltage(double maxVoltage) {
        this.maxVoltage = maxVoltage;
    }
}

