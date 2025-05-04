package edu.vanier.strawberries.Models.UndoRedo;

import edu.vanier.strawberries.Models.Component;

public class RotateComponentAction implements Action {
    Component component;
    String direction;

    public RotateComponentAction(Component component, String direction) {
        this.component = component;
        this.direction = direction;

        hist.actions.push(this);
        execute(false);
    }

    @Override
    public void execute(boolean redone) {
        component.rotate(direction);
    }

    @Override
    public void undo() {
        if(direction.equalsIgnoreCase("left")) {
            component.rotate("right");
        }
        else {
            component.rotate("left");
        }
    }
}
