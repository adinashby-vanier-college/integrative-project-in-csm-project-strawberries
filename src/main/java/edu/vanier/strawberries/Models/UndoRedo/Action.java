package edu.vanier.strawberries.Models.UndoRedo;

import edu.vanier.strawberries.Models.Circuit;
import edu.vanier.strawberries.ui.MainApp;

/**
 * Any action performed by the user will be translated into a class implementing the Action interface.
 * <br>For an example, see {@link AddComponentAction}
 * @see History
 *
 */
public interface Action {
    History hist = MainApp.mainAppFXMLController.history;
    Circuit circuit = MainApp.mainAppFXMLController.circuit;
    void execute(boolean redone);
    void undo();
}


