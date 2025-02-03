package com.example.fractal.Components;
import com.example.fractal.Component;
import com.example.fractal.Node;

public class Battery extends Component {
    public Battery(Node begin, Node end) {
        super(begin, end);
    }

    @Override
    public String getSymbol() {
        return "--| i--";
    }

    @Override
    public void draw() {
        System.out.println("Battery from " + begin + " to " + end);
    }
}