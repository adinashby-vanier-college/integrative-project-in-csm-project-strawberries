package edu.vanier.strawberries.Models.UndoRedo;

import edu.vanier.strawberries.Models.Component;
import javafx.geometry.Point2D;

public class MoveComponentAction implements Action {
    Component component;
    Point2D initialBeginPosition, initialEndPosition, finalBeginPosition, finalEndPosition;

    public MoveComponentAction(Component component) {
        this.component = component;
        initialBeginPosition = component.begin.getPosition();
        initialEndPosition = component.end.getPosition();

        hist.actions.push(this);
    }

    @Override
    public void execute(boolean redone) {
        component.begin.setPosition(finalBeginPosition);
        component.end.setPosition(finalEndPosition);
    }

    @Override
    public void undo() {
        component.begin.setPosition(initialBeginPosition);
        component.end.setPosition(initialEndPosition);
    }

    public boolean hasMoved() {
        return (component.begin.getPosition().distance(initialBeginPosition)!=0 || component.end.getPosition().distance(initialEndPosition)!=0);
    }

    public void setFinalPositions(Point2D finalBeginPosition, Point2D finalEndPosition) {
        this.finalBeginPosition = finalBeginPosition;
        this.finalEndPosition = finalEndPosition;
        execute(false);
    }
}
