package com.example.fractal.Components;
import com.example.fractal.Component;
import com.example.fractal.Node;

public class Wire extends Component {
    public Wire(Node begin, Node end) {
        super(begin, end);
    }

    @Override
    public String getSymbol() {
        return "-------";
    }

    @Override
    public void draw() {
        System.out.println("Wire from " + begin + " to " + end);
    }
}