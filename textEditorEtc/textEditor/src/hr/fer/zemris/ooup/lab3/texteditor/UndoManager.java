package hr.fer.zemris.ooup.lab3.texteditor;

import hr.fer.zemris.ooup.lab3.texteditor.command.EditAction;
import hr.fer.zemris.ooup.lab3.texteditor.observer.StacksObserver;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class UndoManager {

    private static UndoManager instance = null;
    private final Stack<EditAction> undoStack;
    private final Stack<EditAction> redoStack;
    private final List<StacksObserver> stacksObservers = new LinkedList<>();

    private UndoManager() {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    public static UndoManager getInstance() {
        if(instance == null) instance = new UndoManager();
        return instance;
    }

    // removes the command from the undoStack, pushes it to the redoStack and executes it
    public void undo() {
        if(!this.undoStack.isEmpty()) {
            this.redoStack.push(undoStack.peek());
            this.undoStack.pop().execute_undo();
            notifyStacksObservers();
        }
    }
    // removes the command from the redoStack, pushes it to the undoStack and executes it
    public void redo() {
        if(!this.redoStack.isEmpty()) {
            this.undoStack.push(redoStack.peek());
            this.redoStack.pop().execute_do();
            notifyStacksObservers();
        }
    }
    // clears redoStack, pushes command to undoStack
    public void push(EditAction c) {
        
        this.redoStack.clear();
        this.undoStack.push(c);
        notifyStacksObservers();
    }

    public boolean isUndoStackEmpty() {
        return this.undoStack.isEmpty();
    }

    public boolean isRedoStackEmpty() {
        return this.redoStack.isEmpty();
    }

    public void subscribeStacksObserver(StacksObserver observer){
        stacksObservers.add(observer);
    }
    public void unsubscribeStacksObservers(StacksObserver observer){
        stacksObservers.remove(observer);
    }
    public void notifyStacksObservers(){
        stacksObservers.forEach(observer -> observer.update(this));
    }
}
