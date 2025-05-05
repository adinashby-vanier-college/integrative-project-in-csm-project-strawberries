package edu.vanier.strawberries.Models.UndoRedo;

import edu.vanier.strawberries.Models.Component;

public class RemoveComponentAction implements Action {
    Component component;

    public RemoveComponentAction(Component component) {
        this.component = component;

        hist.actions.push(this);
        execute(false);
    }

    @Override
    public void execute(boolean redone) {
        circuit.deleteComponent(component);
    }

    @Override
    public void undo() {
        circuit.addComponent(component);
    }
}
