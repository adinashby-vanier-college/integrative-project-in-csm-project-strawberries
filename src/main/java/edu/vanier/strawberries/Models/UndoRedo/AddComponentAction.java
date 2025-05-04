package edu.vanier.strawberries.Models.UndoRedo;

import edu.vanier.strawberries.Models.Component;

public class AddComponentAction implements Action {
    Component component;

    public AddComponentAction(Component component) {
        this.component = component;

        hist.actions.push(this);
        execute(false);
    }

    @Override
    public void execute(boolean redone) {
        circuit.addComponent(component);
    }

    @Override
    public void undo() {
        circuit.deleteComponent(component);
    }
}
