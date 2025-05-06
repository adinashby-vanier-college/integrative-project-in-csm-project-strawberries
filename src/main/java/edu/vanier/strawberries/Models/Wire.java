package edu.vanier.strawberries.Models;
import javafx.animation.PathTransition;
import javafx.scene.paint.Color;

public class Wire extends Component {
    private Color color;
    private PathTransition transition;

    public Wire(Node begin, Node end, Color color, double current, double voltage) {
        super(begin, end, false);
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

    /**
     * Creates a new Component of the same type and properties as the current (calling) component
     *
     * @return a new instance of Component
     */
    @Override
    public Component createCopy() {
        begin.lock();
        end.lock();
        return new Wire(Node.copyOf(begin),Node.copyOf(end),color,current,voltage);
    }
}
