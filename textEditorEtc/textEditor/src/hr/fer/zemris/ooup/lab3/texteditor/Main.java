package hr.fer.zemris.ooup.lab3.texteditor;

public class Main {
    public static void main(String[] args) throws Exception {

        TextEditorModel textEditorModel = new TextEditorModel("Prvi red lalalal rim tim tagidim\nDrugi red     malo razmaka\nTreci red\naicbao aouch oauhc oaihdc hac\naidub iaudh iaudh aiub");
        new TextEditor(textEditorModel);

    }
}
