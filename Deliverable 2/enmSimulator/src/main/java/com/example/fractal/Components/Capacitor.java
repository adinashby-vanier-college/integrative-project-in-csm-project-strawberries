package com.example.fractal.Components;
import com.example.fractal.Component;
import com.example.fractal.Node;

public class Capacitor extends Component {
    private double storedEnergy;
    private boolean start;
    private boolean End; // gotta change the name here bc its too similar

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

    @Override
    public void draw() {
        System.out.println("Capacitor from " + begin + " to " + end);
    }
}