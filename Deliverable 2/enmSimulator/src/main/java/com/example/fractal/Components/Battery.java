package com.example.fractal.Components;
import com.example.fractal.Component;
import com.example.fractal.Node;
import javafx.scene.image.Image;

public class Battery extends Component {
    public Battery(Node begin, Node end) {
        super(begin, end);
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