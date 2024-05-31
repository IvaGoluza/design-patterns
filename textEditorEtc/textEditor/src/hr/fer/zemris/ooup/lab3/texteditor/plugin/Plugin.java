package hr.fer.zemris.ooup.lab3.texteditor.plugin;

import hr.fer.zemris.ooup.lab3.texteditor.ClipboardStack;
import hr.fer.zemris.ooup.lab3.texteditor.TextEditorModel;
import hr.fer.zemris.ooup.lab3.texteditor.UndoManager;

public interface Plugin {
    String getName();
    String getDescription();
    void execute(TextEditorModel model, UndoManager undoManager, ClipboardStack clipboardStack);
}
