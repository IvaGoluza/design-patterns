package model.drawing;

import model.graphic.GraphicalObject;
import observer.DocumentModelListener;
import observer.GraphicalObjectListener;
import utils.GeometryUtil;
import utils.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DocumentModel {

    public final static double SELECTION_PROXIMITY = 10;

    private final List<GraphicalObject> objects = new ArrayList<>();  // Kolekcija svih grafičkih objekata

    private final List<GraphicalObject> roObjects = Collections.unmodifiableList(objects); // Read-Only proxy oko kolekcije grafičkih objekata

    private final List<DocumentModelListener> listeners = new ArrayList<>(); // Kolekcija prijavljenih promatrača

    private final List<GraphicalObject> selectedObjects = new ArrayList<>(); // Kolekcija selektiranih objekata

    private final List<GraphicalObject> roSelectedObjects = Collections.unmodifiableList(selectedObjects); // Read-Only proxy oko kolekcije selektiranih objekata

    // Promatrač DocumentModela koji će biti registriran nad svim objektima crteža...(npr oval i line)
    private final GraphicalObjectListener goListener = new GraphicalObjectListener() {
        @Override
        public void graphicalObjectChanged(GraphicalObject go) {   // ako se neki od likova dokumenta promijenio, javi (npr. platnu)
            notifyListeners();
        }

        @Override
        public void graphicalObjectSelectionChanged(GraphicalObject go) {

            if (go.isSelected()) {
                if(selectedObjects.contains(go))
                    return;
                else
                    selectedObjects.add(go);
            } else
                selectedObjects.remove(go);

            notifyListeners();
        }

    };

    // Konstruktor...
    public DocumentModel() {}

    // Brisanje svih objekata iz modela
    public void clear() {
        objects.forEach(go -> go.removeGraphicalObjectListener(goListener)); // odregistriraj goListener sa svih objekata
        objects.clear();
        notifyListeners();  // obavijesti sve promatrače modela
    }

    // Dodavanje objekta u dokument
    public void addGraphicalObject(GraphicalObject obj) {
        obj.addGraphicalObjectListener(goListener);  // prvo na objekt registriraj goListener
        objects.add(obj);
        if(obj.isSelected()) selectedObjects.add(obj);  // ako je novi objekt već selektiran
        notifyListeners();
    }

    // Uklanjanje objekta iz dokumenta
    public void removeGraphicalObject(GraphicalObject obj) {
        obj.removeGraphicalObjectListener(goListener);  // prvo odregistriraj goListener s objekta koji se uklanja
        objects.remove(obj);
        if(obj.isSelected()) selectedObjects.remove(obj);  // izbaci objekt i iz liste selektiranih ako je bio selektiran
        notifyListeners();
    }

    // Vrati nepromjenjivu listu postojećih objekata (izmjene smiju ići samo kroz metode modela)
    public List<GraphicalObject> list() {
        return roObjects;
    }

    // Prijava promatrača modelaa (npr platno)
    public void addDocumentModelListener(DocumentModelListener l) {
        listeners.add(l);
    }

    // Odjava promatrača modelaa (npr platno)
    public void removeDocumentModelListener(DocumentModelListener l) {
        listeners.remove(l);
    }

    // Obavještavanje njegovih promatrača o promjenama na go ovog dokumenta(npr. platno za crtanje onda odradi repaint)
    public void notifyListeners() {
        listeners.forEach(DocumentModelListener::documentChange);
    }

    // Vrati nepromjenjivu listu selektiranih objekata
    public List<GraphicalObject> getSelectedObjects() {
        return roSelectedObjects;
    }

    // Pomakni predani objekt u listi objekata na jedno mjesto kasnije...
    // Time će se on iscrtati kasnije (pa će time možda veći dio biti vidljiv)
    public void increaseZ(GraphicalObject go) {
        int index = objects.indexOf(go);
        if (index == -1 || index == objects.size() - 1) return; // objekt nije u dokumnetu ili se već iscrtava zadnji
        Collections.swap(objects, index, index + 1);
        notifyListeners();
    }

    // Pomakni predani objekt u listi objekata na jedno mjesto ranije...
    public void decreaseZ(GraphicalObject go) {
        int index = objects.indexOf(go);
        if (index == -1 || index == 0) return;  // objekt nije u dokumnetu ili se već iscrtava prvi
        Collections.swap(objects, index, index - 1);
        notifyListeners();
    }

    // Pronađi postoji li u modelu neki objekt koji klik na točku koja je
    // predana kao argument selektira i vrati ga ili vrati null. Točka selektira
    // objekt kojemu je najbliža uz uvjet da ta udaljenost nije veća od
    // SELECTION_PROXIMITY. Status selektiranosti objekta ova metoda NE dira.
    public GraphicalObject findSelectedGraphicalObject(Point mousePoint) {

        GraphicalObject selectedObj = null;
        double minDist = -1;
        for (GraphicalObject go : objects){
            double dist = go.selectionDistance(mousePoint);
            if(dist <= SELECTION_PROXIMITY && (minDist == -1 || dist < minDist)){
                minDist = dist;
                selectedObj = go;
            }
        }

        return selectedObj;
    }

    // Pronađi da li u predanom objektu predana točka miša selektira neki hot-point.
    // Točka miša selektira onaj hot-point objekta kojemu je najbliža uz uvjet da ta
    // udaljenost nije veća od SELECTION_PROXIMITY. Vraća se indeks hot-pointa
    // kojeg bi predana točka selektirala ili -1 ako takve nema. Status selekcije
    // se pri tome NE dira.
    public int findSelectedHotPoint(GraphicalObject object, Point mousePoint) {

        int selectedHP = -1;
        double minDist = -1;
        for(int i = 0; i < object.getNumberOfHotPoints(); i++){
            Point hp = object.getHotPoint(i);
            double dist = GeometryUtil.distanceFromPoint(hp,mousePoint);
            if(dist <= SELECTION_PROXIMITY && (selectedHP == -1 || minDist > dist)){
                minDist = dist;
                selectedHP = i;
            }
        }
        return selectedHP;
    }

    public void clearSelectedObjects() {
        while (!getSelectedObjects().isEmpty())
            getSelectedObjects().get(0).setSelected(false);  // goListener će dobiti info od svih go i pokrenut ce notify
    }

}
