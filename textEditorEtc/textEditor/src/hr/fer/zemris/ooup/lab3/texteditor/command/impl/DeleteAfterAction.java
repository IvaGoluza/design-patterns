package hr.fer.zemris.ooup.lab3.texteditor.command.impl;

import hr.fer.zemris.ooup.lab3.texteditor.command.EditAction;
import hr.fer.zemris.ooup.lab3.texteditor.Location;
import hr.fer.zemris.ooup.lab3.texteditor.TextEditorModel;

public class DeleteAfterAction implements EditAction {
    private final TextEditorModel textEditorModel;
    private char character;
    private Location location;


    public DeleteAfterAction(TextEditorModel textEditorModel){
        this.textEditorModel = textEditorModel;
    }
    @Override
    public void execute_do() {
        location = new Location(textEditorModel.getCursorLocation().getRow(), textEditorModel.getCursorLocation().getColumn());
        character = textEditorModel.getLines().get(location.getRow()).charAt(location.getColumn()+1);
        textEditorModel.deleteAfter();
    }

    @Override
    public void execute_undo() {
        textEditorModel.setCursorLocation(location.getRow(), location.getColumn());
        textEditorModel.insert(character);
    }
}
