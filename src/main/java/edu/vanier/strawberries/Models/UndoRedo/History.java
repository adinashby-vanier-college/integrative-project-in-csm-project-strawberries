package edu.vanier.strawberries.Models.UndoRedo;

import java.util.EmptyStackException;
import java.util.Stack;

public class History {
    Stack<Action> actions = new Stack<>();
    Stack<Action> undone = new Stack<>();

    public void undo() {
        try {
            actions.peek().undo();
            undone.push(actions.pop());
        }
        catch(EmptyStackException e){
            System.out.println("Nothing to undo.");
        }
    }
    public void redo() {
        try {
            undone.peek().execute(true);
            actions.push(undone.pop());
        }
        catch (EmptyStackException e){
            System.out.println("Nothing to redo.");
        }
    }
    public void clear() {
        actions.clear();
        undone.clear();
    }
    public void add(Action action) {
        actions.push(action);
    }
}