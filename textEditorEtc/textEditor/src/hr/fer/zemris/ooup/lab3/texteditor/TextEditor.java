package hr.fer.zemris.ooup.lab3.texteditor;

import hr.fer.zemris.ooup.lab3.texteditor.action.PasteAction;
import hr.fer.zemris.ooup.lab3.texteditor.action.UnReDoAction;
import hr.fer.zemris.ooup.lab3.texteditor.command.EditAction;
import hr.fer.zemris.ooup.lab3.texteditor.command.impl.DeleteAfterAction;
import hr.fer.zemris.ooup.lab3.texteditor.command.impl.DeleteBeforeAction;
import hr.fer.zemris.ooup.lab3.texteditor.command.impl.DeleteRangeAction;
import hr.fer.zemris.ooup.lab3.texteditor.command.impl.InsertTextAction;
import hr.fer.zemris.ooup.lab3.texteditor.observer.CursorObserver;
import hr.fer.zemris.ooup.lab3.texteditor.observer.TextObserver;
import hr.fer.zemris.ooup.lab3.texteditor.plugin.Plugin;
import hr.fer.zemris.ooup.lab3.texteditor.plugin.PluginFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class TextEditor extends JComponent implements CursorObserver, TextObserver {

    private final TextEditorModel textEditorModel;
    private final ClipboardStack clipboardStack;
    private final UndoManager undoManager;
    private final JLabel statusBar;

    public TextEditor(TextEditorModel textEditorModel) throws Exception {
        setPreferredSize(new Dimension(500, 600));
        this.textEditorModel = textEditorModel;
        this.undoManager = UndoManager.getInstance();
        this.textEditorModel.subscribeCursorObserver(this);
        this.textEditorModel.subscribeTextObserver(this);
        this.clipboardStack = new ClipboardStack();
        this.statusBar = new JLabel("Ln " + (this.textEditorModel.getCursorLocation().getRow() + 1) + ", Col " + (this.textEditorModel.getCursorLocation().getColumn() + 1) + " | Lines: " + textEditorModel.getLines().size());
        this.statusBar.setBorder(BorderFactory.createEtchedBorder());
        this.statusBar.setFocusable(false);

        addKeyListener(new KeyListener() {
            private boolean selectionShift;

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                selectionShift = e.isShiftDown();
                Location oldCursorLocation = new Location(textEditorModel.getCursorLocation().getRow(), textEditorModel.getCursorLocation().getColumn());

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> textEditorModel.moveCursorLeft();
                    case KeyEvent.VK_RIGHT -> textEditorModel.moveCursorRight();
                    case KeyEvent.VK_UP -> textEditorModel.moveCursorUp();
                    case KeyEvent.VK_DOWN -> textEditorModel.moveCursorDown();
                    case KeyEvent.VK_BACK_SPACE -> {
                        if (textEditorModel.getSelectionRange() != null) {
                            EditAction deleteRangeAction = new DeleteRangeAction(textEditorModel);
                            deleteRangeAction.execute_do();
                            undoManager.push(deleteRangeAction);
                            // textEditorModel.deleteRange(textEditorModel.getSelectionRange());
                        } else {
                            EditAction deleteBeforeAction = new DeleteBeforeAction(textEditorModel);
                            deleteBeforeAction.execute_do();
                            undoManager.push(deleteBeforeAction);
                            //textEditorModel.deleteBefore();
                        }
                    }
                    case KeyEvent.VK_DELETE -> {
                        if (textEditorModel.getSelectionRange() != null) {
                            EditAction deleteRangeAction = new DeleteRangeAction(textEditorModel);
                            deleteRangeAction.execute_do();
                            undoManager.push(deleteRangeAction);
                            // textEditorModel.deleteRange(textEditorModel.getSelectionRange());
                        } else {
                            EditAction deleteAfterAction = new DeleteAfterAction(textEditorModel);
                            deleteAfterAction.execute_do();
                            undoManager.push(deleteAfterAction);
                            // textEditorModel.deleteAfter();
                        }
                    }
                    case KeyEvent.VK_ENTER -> {
                        EditAction insertAction = new InsertTextAction(textEditorModel, "\n");
                        insertAction.execute_do();
                        undoManager.push(insertAction);
                        // textEditorModel.insert("\n");
                    }
                    default -> {
                        if (!e.isMetaDown() && !e.isShiftDown() && !e.isActionKey() && !e.isControlDown() && !e.isAltDown()) {
                            EditAction insertAction = new InsertTextAction(textEditorModel, String.valueOf(e.getKeyChar()));
                            insertAction.execute_do();
                            undoManager.push(insertAction);
                            //textEditorModel.insert(e.getKeyChar());
                        }
                    }
                }
                if(selectionShift) {
                    if(textEditorModel.getSelectionRange() == null) {
                        textEditorModel.setSelectionRange(new LocationRange(oldCursorLocation, textEditorModel.getCursorLocation()));
                    } else {
                        textEditorModel.setSelectionRange(new LocationRange(textEditorModel.getSelectionRange().getTextStart(), textEditorModel.getCursorLocation()));
                    }
                    
                } else {
                    if(textEditorModel.getSelectionRange() != null && (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) == 0) textEditorModel.setSelectionRange(null);
                }

                if((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
                    switch (e.getKeyCode()){
                        case KeyEvent.VK_C -> copyHandler(false);
                        case KeyEvent.VK_X -> cutHandler();
                        case KeyEvent.VK_V -> {
                            if(selectionShift) pasteRemoveHandler(true);
                            else pasteHandler();
                        }
                        case KeyEvent.VK_Z -> undoManager.undo();
                        case KeyEvent.VK_Y -> undoManager.redo();
                    }
                }
                repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT && this.selectionShift) {
                    this.selectionShift = false;
                }
            }
        });

        setFocusable(true);
        requestFocusInWindow();
        gui();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;
        Font font = new Font("Monospaced", Font.PLAIN, 11);
        graphics2D.setFont(font);
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        int y = 20;
        graphics2D.setColor(Color.BLACK);

        int row = textEditorModel.getCursorLocation().getRow();
        int col = textEditorModel.getCursorLocation().getColumn();

        int currentRow = 0;
        for(String line : textEditorModel) {
            graphics2D.drawString(line, 10, y);
            if (currentRow == row) {
                int cursorX;
                if (col <= line.length()) {
                    cursorX = 10 + fontMetrics.stringWidth(line.substring(0, col));
                } else {
                    cursorX = 10 + fontMetrics.stringWidth(line);
                }
                int cursorYStart = y - fontMetrics.getAscent();
                int cursorYEnd = cursorYStart + fontMetrics.getHeight();
                graphics2D.drawLine(cursorX, cursorYStart, cursorX, cursorYEnd);
            }

            y += fontMetrics.getHeight();
            currentRow++;
        }
        if (this.textEditorModel.getSelectionRange() != null) {   // highlight selected text
            graphics2D.setColor(new Color(0, 255, 255, 150));
            LocationRange range = this.textEditorModel.getSelectionRange();
            Location start = range.getTextStart().compareTo(range.getTextEnd()) < 0 ? range.getTextStart() : range.getTextEnd();
            Location end = range.getTextStart().compareTo(range.getTextEnd()) > 0 ? range.getTextStart() : range.getTextEnd();

            int upperLeftPointX = fontMetrics.stringWidth(this.textEditorModel.getLines().get(start.getRow()).substring(0, start.getColumn())) + 10;
            int upperLeftPointY = (start.getRow()-1) * fontMetrics.getHeight() + 20 + fontMetrics.getDescent();
            int rectWidth;
            int rectHeight = fontMetrics.getHeight();

            if (start.getRow() != end.getRow()) {  // more lines are selected
                
                
                rectWidth = fontMetrics.stringWidth(this.textEditorModel.getLines().get(start.getRow()).substring(start.getColumn()));
                graphics2D.fillRect(upperLeftPointX, upperLeftPointY, rectWidth, rectHeight);  // selected part of the first line

                int rowToHighlight = start.getRow() + 1;
                upperLeftPointX = 10;
                Iterator<String> iterator = this.textEditorModel.linesRange(start.getRow() + 1, end.getRow());
                while(iterator.hasNext()) {
                    String line = iterator.next();
                    upperLeftPointY = (rowToHighlight-1) * fontMetrics.getHeight() + 20 + fontMetrics.getDescent();
                    rectWidth = fontMetrics.stringWidth(line);
                    graphics2D.fillRect(upperLeftPointX, upperLeftPointY, rectWidth, rectHeight);  // fully selected lines
                    rowToHighlight++;
                }
                upperLeftPointY = (end.getRow()-1) * fontMetrics.getHeight() + 20 + fontMetrics.getDescent();
                rectWidth = fontMetrics.stringWidth(this.textEditorModel.getLines().get(end.getRow()).substring(0, end.getColumn()));
            } else {
                
                rectWidth = fontMetrics.stringWidth(this.textEditorModel.getLines().get(start.getRow()).substring(start.getColumn(), end.getColumn()));
            }
            graphics2D.fillRect(upperLeftPointX, upperLeftPointY, rectWidth, rectHeight);
        }
    }

    private void copyHandler(boolean deleteSelectedText) {
        LocationRange r = this.textEditorModel.getSelectionRange();
        if(r == null) return;

        Location start = r.getTextStart().compareTo(r.getTextEnd()) < 0 ? r.getTextStart() : r.getTextEnd();
        Location end = r.getTextStart().compareTo(r.getTextEnd()) > 0 ? r.getTextStart() : r.getTextEnd();
        StringBuilder copy;
        if (start.getRow() != end.getRow()) {  // more lines are selected to copy
            copy = new StringBuilder(this.textEditorModel.getLines().get(start.getRow()).substring(start.getColumn()) + "\n");
            Iterator<String> iterator = this.textEditorModel.linesRange(start.getRow() + 1, end.getRow());
            while(iterator.hasNext()) {
                copy.append(iterator.next()).append("\n");
            }
            copy.append(this.textEditorModel.getLines().get(end.getRow()), 0, end.getColumn());
        } else {
            copy = new StringBuilder(this.textEditorModel.getLines().get(start.getRow()).substring(start.getColumn(), end.getColumn()));
        }
        clipboardStack.push(copy.toString());
        if(deleteSelectedText) {
            EditAction deleteRangeAction = new DeleteRangeAction(textEditorModel);
            deleteRangeAction.execute_do();
            undoManager.push(deleteRangeAction);
            // this.textEditorModel.deleteRange(this.textEditorModel.getSelectionRange());
        }
    }

    private void cutHandler() {
        copyHandler(true);
    }

    private void pasteRemoveHandler(boolean removeText) {
        String stackText = "";
        if(removeText && !this.clipboardStack.isEmpty()) {
            stackText = this.clipboardStack.pop();
        } else if(!this.clipboardStack.isEmpty()) {
            stackText = this.clipboardStack.peek();
        }
        if(!Objects.equals(stackText, "")) {
            EditAction insertAction = new InsertTextAction(textEditorModel, stackText);
            insertAction.execute_do();
            undoManager.push(insertAction);
            // this.textEditorModel.insert(stackText);
        }
    }

    private void pasteHandler() {
        pasteRemoveHandler(false);
    }

    public UnReDoAction undoAction = new UnReDoAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            UndoManager.getInstance().undo();
        }
        @Override
        public void update(UndoManager undoManager) {
            this.setEnabled(!undoManager.isUndoStackEmpty());
        }
    };

    public UnReDoAction redoAction = new UnReDoAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            UndoManager.getInstance().redo();
        }
        @Override
        public void update(UndoManager undoManager) {
            this.setEnabled(!undoManager.isRedoStackEmpty());
        }
    };

    private final AbstractAction copyAction = new AbstractAction("Copy") {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            copyHandler(false);
        }
    };

    private final AbstractAction cutAction = new AbstractAction("Cut") {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            cutHandler();
        }
    };

    private final AbstractAction deleteSelectionAction = new AbstractAction("Delete selection") {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            if (textEditorModel.getSelectionRange() != null) {
                EditAction deleteRangeAction = new DeleteRangeAction(textEditorModel);
                deleteRangeAction.execute_do();
                undoManager.push(deleteRangeAction);
            }
        }
    };

    private final PasteAction pasteAction = new PasteAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            pasteHandler();
        }
        @Override
        public void updateClipboard(ClipboardStack clipboardStack) {
            this.setEnabled(!clipboardStack.isEmpty());
        }
    };

    public PasteAction pasteAndTakeAction = new PasteAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            pasteRemoveHandler(true);
        }
        @Override
        public void updateClipboard(ClipboardStack clipboardStack) {
            this.setEnabled(!clipboardStack.isEmpty());
        }
    };

    private void gui() throws Exception {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = createFileMenu();
        JMenu editMenu = createEditMenu();
        JMenu moveMenu = createMoveMenu();
        JMenu pluginMenu = createPluginMenu();

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(moveMenu);
        menuBar.add(pluginMenu);

        JToolBar toolBar = createToolBar();
        JFrame frame = new JFrame("Text Editor");
        frame.setJMenuBar(menuBar);
        frame.add(toolBar, BorderLayout.NORTH);
        frame.add(this, BorderLayout.CENTER);
        frame.add(statusBar, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private JMenu createPluginMenu() throws Exception {
        JMenu pluginMenu = new JMenu("Plugins");
        PluginFactory pluginFactory = new PluginFactory();
        List<Plugin> plugins = pluginFactory.createPlugins();

        for(Plugin plugin: plugins) {
            Action pluginAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    plugin.execute(textEditorModel, undoManager, clipboardStack);
                }
            };
            pluginAction.putValue(Action.NAME, plugin.getName());
            pluginMenu.add(pluginAction);
        }
        return pluginMenu;
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> undoManager.undo());
        undoButton.setFocusable(false);
        toolBar.add(undoButton);

        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(e -> undoManager.redo());
        redoButton.setFocusable(false);
        toolBar.add(redoButton);

        JButton cutButton = new JButton("Cut");
        cutButton.addActionListener(e -> cutHandler());
        cutButton.setFocusable(false);
        toolBar.add(cutButton);

        JButton copyButton = new JButton("Copy");
        copyButton.addActionListener(e -> copyHandler(false));
        copyButton.setFocusable(false);
        toolBar.add(copyButton);

        JButton pasteButton = new JButton("Paste");
        pasteButton.addActionListener(e -> pasteHandler());
        pasteButton.setFocusable(false);
        toolBar.add(pasteButton);

        toolBar.setFocusable(false);
        return toolBar;
    }

    private JMenu createMoveMenu() {
        JMenu moveMenu = new JMenu("Move");
        moveMenu.add(new JMenuItem(new AbstractAction("Cursor to document start") {
            @Override
            public void actionPerformed(ActionEvent e) {
                textEditorModel.moveCursorToDocumentStart();
            }
        }));
        moveMenu.add(new JMenuItem(new AbstractAction("Cursor to document end") {
            @Override
            public void actionPerformed(ActionEvent e) {
                textEditorModel.moveCursorToDocumentEnd();
            }
        }));
        return moveMenu;
    }

    private JMenu createEditMenu() {
        JMenu editMenu = new JMenu("Edit");
        UndoManager.getInstance().subscribeStacksObserver(undoAction);
        undoAction.putValue(Action.NAME, "Undo");
        undoAction.setEnabled(false);
        editMenu.add(new JMenuItem(undoAction));

        UndoManager.getInstance().subscribeStacksObserver(redoAction);
        redoAction.putValue(Action.NAME, "Redo");
        redoAction.setEnabled(false);
        editMenu.add(new JMenuItem(redoAction));

        cutAction.setEnabled(false);
        editMenu.add(new JMenuItem(cutAction));
        copyAction.setEnabled(false);
        editMenu.add(new JMenuItem(copyAction));

        pasteAction.putValue(Action.NAME, "Paste");
        pasteAction.setEnabled(false);
        editMenu.add(new JMenuItem(pasteAction));
        clipboardStack.subscribeClipboardObserver(pasteAction);

        pasteAndTakeAction.putValue(Action.NAME, "Paste and Take");
        pasteAndTakeAction.setEnabled(false);
        editMenu.add(new JMenuItem(pasteAndTakeAction));
        clipboardStack.subscribeClipboardObserver(pasteAndTakeAction);

        deleteSelectionAction.setEnabled(false);
        editMenu.add(new JMenuItem(deleteSelectionAction));
        editMenu.add(new JMenuItem(new AbstractAction("Clear document") {
            @Override
            public void actionPerformed(ActionEvent e) {
                textEditorModel.clearDocument();
            }
        }));
        return editMenu;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem(new AbstractAction("Open") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        List<String> lines = Files.readAllLines(file.toPath());
                        textEditorModel.setLines(lines);
                        
                    } catch (IOException exception) {
                        exception.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error opening file.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }));
        fileMenu.add(new JMenuItem(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showSaveDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    StringBuilder sb = new StringBuilder();
                    for (String line : textEditorModel.getLines()) {
                        sb.append(line).append("\n");
                    }
                    try {
                        Files.writeString(fileChooser.getSelectedFile().toPath(), sb.toString(), StandardOpenOption.TRUNCATE_EXISTING);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    
                }
            }
        }));
        fileMenu.add(new JMenuItem(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        }));
        return fileMenu;
    }

    private void updateStatusBar() {
        Location cursorLocation = this.textEditorModel.getCursorLocation();
        int numberOfLines = this.textEditorModel.getLines().size();
        this.statusBar.setText("Ln " + (cursorLocation.getRow() + 1) + ", Col " + (cursorLocation.getColumn() + 1) + " | Lines: " + numberOfLines);
    }

    @Override
    public void updateCursorLocation(Location loc) {
        updateStatusBar();
        repaint();
    }

    @Override
    public void updateText() {
        copyAction.setEnabled(this.textEditorModel.getSelectionRange() != null);
        cutAction.setEnabled(this.textEditorModel.getSelectionRange() != null);
        deleteSelectionAction.setEnabled(this.textEditorModel.getSelectionRange() != null);
        updateStatusBar();
        repaint();
    }

}
