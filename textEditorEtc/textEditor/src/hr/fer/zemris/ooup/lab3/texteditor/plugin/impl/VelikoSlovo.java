package hr.fer.zemris.ooup.lab3.texteditor.plugin.impl;

import hr.fer.zemris.ooup.lab3.texteditor.*;
import hr.fer.zemris.ooup.lab3.texteditor.command.EditAction;
import hr.fer.zemris.ooup.lab3.texteditor.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;


public class VelikoSlovo implements Plugin {

    @Override
    public String getName() {
        return "Veliko Slovo";
    }

    @Override
    public String getDescription() {
        return "Plugin prolazi kroz dokument i svako prvo slovo riječi mijenja u veliko (\"ovo je tekst\" ==> \"Ovo Je Tekst\")";
    }

    @Override
    public void execute(TextEditorModel model, UndoManager undoManager, ClipboardStack clipboardStack) {
        EditAction capitalLetterAction = new CapitalLetterAction(model);
        capitalLetterAction.execute_do();
        undoManager.push(capitalLetterAction);
    }

    private static class CapitalLetterAction implements EditAction{
        private final TextEditorModel textEditorModel;
        private List<String> prevLines;
        private CapitalLetterAction(TextEditorModel textEditorModel){
            this.textEditorModel = textEditorModel;
        }
        @Override
        public void execute_do() {
            List<String> lines = new ArrayList<>();
            prevLines = textEditorModel.getLines();
            for(String line : textEditorModel) {
                StringBuilder newLine = new StringBuilder();
                for(String word : line.split(" ")) {
                    if(word.isBlank()) newLine.append(word).append(" ");
                    else newLine.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase()).append(" ");
                }
                lines.add(newLine.toString());
            }
            textEditorModel.setLines(lines);
        }

        @Override
        public void execute_undo() {
            textEditorModel.setLines(prevLines);
        }
    }
}
