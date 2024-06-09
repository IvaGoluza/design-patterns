package model;

import RenderImplementor.impl.SVGRendererImpl;
import model.drawing.DocumentModel;
import model.drawing.DrawingCanvas;
import model.graphic.GraphicalObject;
import observer.DocumentModelListener;
import state.State;
import state.impl.AddShapeState;
import state.impl.EraserState;
import state.impl.IdleState;
import state.impl.SelectShapeState;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;


public class GUI extends JFrame implements DocumentModelListener {

    private final List<GraphicalObject> prototypes; // za svaki stvori button u toolBaru

    private final DocumentModel model;
    private final DrawingCanvas drawingCanvas;

    private State currentState;

    private final Map<String, GraphicalObject> prototypesDuplicates = new HashMap<>();

    public GUI(List<GraphicalObject> prototypes) {
        this.prototypes = prototypes;
        this.model = new DocumentModel();
        this.drawingCanvas = new DrawingCanvas(this, model);
        this.currentState = new IdleState();
        this.prototypes.forEach(o -> prototypesDuplicates.put(o.getShapeID(),o));

        this.model.addDocumentModelListener(this); // na model pretplati gui kako bi se na canvasu očitovale promjene sa svih go

        // gui initialization
        setTitle("Vector Drawing");
        setMinimumSize(new Dimension(1000, 600));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(drawingCanvas, BorderLayout.CENTER);  // drawing canvas part
        Toolbar(); // ToolBar part
    }

    private void Toolbar() {
        JToolBar toolbar = new JToolBar();


        // gumb "Učitaj" koji učitava crteže i odabranu datoteku
        JButton loadButton = new JButton("Učitaj");

        loadButton.addActionListener(e -> {

            // pita korisnika iz koje datoteke se učitava
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Odaberite datoteku za učitavanje.");
            int fileChooserState = fileChooser.showSaveDialog(this);

            if(fileChooserState == JFileChooser.APPROVE_OPTION) {

                model.clear();
                Path filePath = Path.of(fileChooser.getSelectedFile().getAbsolutePath());
                List<String> rows;
                try {
                    rows = Files.readAllLines(filePath);  //  učitavanje redaka odabrane datotek u pomoćnu listu redaka
                    if(rows.size() == 0) return;
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                Stack<GraphicalObject> stack = new Stack<>();  // prazan stog grafičkih objekata

                for(String row: rows){
                    String id = row.substring(0,row.indexOf(" "));
                    String data = row.substring(row.indexOf(" "));
                    GraphicalObject o = prototypesDuplicates.get(id);  // Za svaki redak izvuci id i u mapi pronađi prototip
                    if(o != null) o.load(stack,data);  // odgovarajući objekt stavi se na stog
                }

                // popis objekata stoga dodajemo u model
                while(!stack.isEmpty()){
                    model.addGraphicalObject(stack.pop());
                }
            }
        });
        loadButton.setFocusable(false);
        toolbar.add(loadButton);


        // gumb "Pohrani" koji pohranjuje crteže i odabranu datoteku
        JButton saveButton = new JButton("Pohrani");

        saveButton.addActionListener(e -> {

            // pita korisnika u koju datoteku da pohrani crtež
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Odaberite lokaciju pohrane crteža.");
            int fileChooserState = fileChooser.showSaveDialog(this);

            if(fileChooserState == JFileChooser.APPROVE_OPTION) {

                // stvara praznu listu redaka
                List<String> rows = new ArrayList<>();

                // traži svaki objekt u modelu da se pohrani u tu listu
                for (GraphicalObject go : model.list()) {
                    go.save(rows);
                }

                // pohrana liste u datoteku
                Path filePath = Path.of(fileChooser.getSelectedFile().getAbsolutePath());
                try {
                    Files.deleteIfExists(filePath);
                    Files.createFile(filePath);
                    Files.write(filePath, rows);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        saveButton.setFocusable(false);
        toolbar.add(saveButton);


        // gumb SVG Export: korisnika odabire direktorij i ime datoteke koju želi stvoriti (Save dialog), i potom se generira SVG opis slike
        JButton svgButton = new JButton("SVG Export");

        svgButton.addActionListener(e -> {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Odaberite lokaciju stvaranja svoje SVG datoteke.");
            int fileChooserState = fileChooser.showSaveDialog(this);

            if(fileChooserState == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getAbsolutePath();
                // System.out.println("SVG File: " + filename);
                SVGRendererImpl svgRenderer = new SVGRendererImpl(filename);

                for (GraphicalObject go : model.list()) {
                    go.render(svgRenderer);
                }
                try {
                    svgRenderer.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

        });
        svgButton.setFocusable(false);
        toolbar.add(svgButton);


        // dodaj gumbe za svaki prototip objekt (npr line i oval)
        for (GraphicalObject go : prototypes) {
            JButton button = new JButton(go.getShapeName());
            button.addActionListener(e -> {
                currentState.onLeaving();
                currentState = new AddShapeState(model, go);  // pritiskom na gumb promjeni se trenutno stanje u primjerak stanja AddShapeState
            });
            button.setFocusable(false);
            toolbar.add(button);
        }


        // gumb "Selektiraj" koji program prebacuje u stanje za selekciju
        JButton button = new JButton("Selektiraj");
        button.addActionListener(e -> {
            currentState.onLeaving();
            currentState = new SelectShapeState(model);  // pritiskom na gumb promjeni se trenutno stanje u primjerak stanja SelectShapeState
        });
        button.setFocusable(false);
        toolbar.add(button);

        // gumb "Brisalo" koji briše go kroz koje prolazi krivulja gumice
        JButton eraseButton = new JButton("Brisalo");
        eraseButton.addActionListener(e -> {
            currentState.onLeaving();
            System.out.println("postava");
            currentState = new EraserState(model);  // pritiskom na gumb promjeni se trenutno stanje u primjerak stanja EraserState
        });
        eraseButton.setFocusable(false);
        toolbar.add(eraseButton);

        getContentPane().add(toolbar, BorderLayout.NORTH);
    }
    @Override
    public void documentChange() {
        drawingCanvas.repaint();
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }
}
