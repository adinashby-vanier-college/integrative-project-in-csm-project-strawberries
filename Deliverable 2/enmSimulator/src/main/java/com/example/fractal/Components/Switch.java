package com.example.fractal.Components;
import com.example.fractal.Component;
import com.example.fractal.Node;

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