package edu.vanier.strawberries.Components;

import edu.vanier.strawberries.Component;
import edu.vanier.strawberries.Node;

public class Capacitor extends Component {
    private double storedEnergy,capacity;
    private boolean start;
    private boolean End; // gotta change the name here bc its too similar

    // ADD capacity (the most energy that can be stored; the MAX before it stops charging)
    public Capacitor(Node begin, Node end, double storedEnergy, boolean start, boolean End) {
        super(begin, end);
        this.storedEnergy = storedEnergy;
        this.start = start;
        this.End = End;
    }

    public double getStoredEnergy() {
        return storedEnergy;
    }
    public boolean getPolarity() {
        return true;
    }
    public boolean setPolarity() {
        return true;
    }
    public boolean isFullyCharged() {
        return storedEnergy==capacity;
    }

    @Override
    public void draw() {
        System.out.println("Capacitor from " + begin + " to " + end);
    }
}