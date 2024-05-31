package hr.fer.zemris.ooup.lab3.texteditor.command.impl;

import hr.fer.zemris.ooup.lab3.texteditor.command.EditAction;
import hr.fer.zemris.ooup.lab3.texteditor.Location;
import hr.fer.zemris.ooup.lab3.texteditor.LocationRange;
import hr.fer.zemris.ooup.lab3.texteditor.TextEditorModel;

import java.util.ArrayList;
import java.util.List;


public class InsertTextAction implements EditAction {
    private final TextEditorModel textEditorModel;
    private final String text;
    private Location rangeStart;

    public InsertTextAction(TextEditorModel textEditorModel,String text){
        this.textEditorModel = textEditorModel;
        this.text = text;
    }
    @Override
    public void execute_do() {
        rangeStart = new Location(textEditorModel.getCursorLocation().getRow(), textEditorModel.getCursorLocation().getColumn());
        textEditorModel.insert(text);
    }

    @Override
    public void execute_undo() {
        Location rangeEnd;
        if(text.contains("\n")) {
            List<String> lines = new ArrayList<>(List.of(this.text.split("\n")));
            rangeEnd = new Location(rangeStart.getRow() + lines.size()-1, lines.get(lines.size()-1).length());
        } else {
            rangeEnd = new Location(rangeStart.getRow(), rangeStart.getColumn() + text.length());
        }
        
        textEditorModel.deleteRange(new LocationRange(rangeStart, rangeEnd));
    }

}
