package hr.fer.zemris.ooup.lab3.texteditor.command.impl;

import hr.fer.zemris.ooup.lab3.texteditor.command.EditAction;
import hr.fer.zemris.ooup.lab3.texteditor.Location;
import hr.fer.zemris.ooup.lab3.texteditor.TextEditorModel;

public class DeleteBeforeAction implements EditAction {

    private final TextEditorModel textEditorModel;
    private char character;
    private Location location;


    public DeleteBeforeAction(TextEditorModel textEditorModel){
        this.textEditorModel = textEditorModel;
    }
    @Override
    public void execute_do() {
        Location currentCursor = textEditorModel.getCursorLocation();
        if(currentCursor.getColumn() == 0) {
            if(currentCursor.getRow() == 0) return;
            else character = "\n".charAt(0);
        } else
            character = textEditorModel.getLines().get(currentCursor.getRow()).charAt(currentCursor.getColumn()-1);
        textEditorModel.deleteBefore();
        location = new Location(textEditorModel.getCursorLocation().getRow(), textEditorModel.getCursorLocation().getColumn());
    }

    @Override
    public void execute_undo() {
        textEditorModel.setCursorLocation(location.getRow(), location.getColumn());
        textEditorModel.insert(character);
    }
}
