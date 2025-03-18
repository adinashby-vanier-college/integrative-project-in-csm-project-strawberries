package edu.vanier.strawberries.Components;
import edu.vanier.strawberries.Component;
import edu.vanier.strawberries.Node;

public class Fuse extends Component {
    public Fuse(Node begin, Node end) {
        super(begin, end);
    }

    public void draw() {
        // Implement the drawing logic here
        System.out.println("Drawing a Fuse ");
    }
}

