package hr.fer.zemris.ooup.lab3.texteditor.observer;

import hr.fer.zemris.ooup.lab3.texteditor.UndoManager;

public interface StacksObserver {
    void update(UndoManager undoManager);
}
