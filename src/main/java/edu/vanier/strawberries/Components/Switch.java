package edu.vanier.strawberries.Components;
import edu.vanier.strawberries.Component;
import edu.vanier.strawberries.Node;

public class Switch extends Component {
    private boolean isClosed;

    public Switch(Node begin, Node end, boolean isClosed) {
        super(begin, end);
        this.isClosed = isClosed;
    }

    public void toggle() {
        isClosed = !isClosed;
    }

    public void setOpen(boolean open) {
       //
    }

    @Override
    public void draw() {
        System.out.println("Switch (" + (isClosed ? "Closed" : "Open") + ") from " + begin + " to " + end);
    }
}