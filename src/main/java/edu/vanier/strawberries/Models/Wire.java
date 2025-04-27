package edu.vanier.strawberries.Models;
import javafx.animation.PathTransition;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class Wire extends Component {
    private Color color;
    private double resistance;
    private PathTransition transition;

    // Used to track the info label
    private Label infoLabel;

    public Wire(Node begin, Node end, Color color, double current, double voltage) {
        super(begin, end);
        this.color = color;
    }

    public PathTransition getTransition() {
        return transition;
    }

    public void setTransition(PathTransition transition) {
        this.transition = transition;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
