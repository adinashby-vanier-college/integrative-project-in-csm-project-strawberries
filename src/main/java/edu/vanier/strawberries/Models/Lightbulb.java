package edu.vanier.strawberries.Models;

public class Lightbulb extends Component {
    public Lightbulb(Node begin, Node end) {
        super(begin, end);
    }

    public void draw() {
        // Implement the drawing logic here
        System.out.println("Drawing a Lightbulb ??  ");
    }
}