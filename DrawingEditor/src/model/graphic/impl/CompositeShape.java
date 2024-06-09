package model.graphic.impl;

import RenderImplementor.Renderer;
import model.graphic.GraphicalObject;
import observer.GraphicalObjectListener;
import utils.Point;
import utils.Rectangle;

import java.util.*;

public class CompositeShape implements GraphicalObject {

    private final List<GraphicalObject> gObjects;
    private final List<GraphicalObjectListener> listeners = new ArrayList<>();

    private boolean selected;

    public CompositeShape(List<GraphicalObject> gObjects) {
        super();
        this.gObjects = gObjects;
        this.selected = false;
    }

    public void addChild(GraphicalObject go) {
        this.gObjects.add(go);
    }

    private void notifyListeners() {
        listeners.forEach(l -> l.graphicalObjectChanged(this));
    }

    private void notifySelectionListeners() {
        listeners.forEach(l -> l.graphicalObjectSelectionChanged(this));
    }

    public List<GraphicalObject> getGObjects() {
        return Collections.unmodifiableList(this.gObjects);
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        gObjects.forEach(go -> go.setSelected(selected));
        notifySelectionListeners();
        notifyListeners();
    }

    @Override
    public int getNumberOfHotPoints() {
        return -1;
    }

    @Override
    public Point getHotPoint(int index) {
        return null;
    }

    @Override
    public void setHotPoint(int index, Point point) {
        throw new UnsupportedOperationException(); }

    @Override
    public boolean isHotPointSelected(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHotPointSelected(int index, boolean selected) {
        throw new UnsupportedOperationException(); }

    @Override
    public double getHotPointDistance(int index, Point mousePoint) {
        return -1;
    }

    @Override
    public void translate(Point delta) {
        gObjects.forEach(go -> go.translate(delta));
        notifyListeners();
    }

    @Override
    public Rectangle getBoundingBox() {
        Rectangle boundingBox = null;
        for(GraphicalObject go : this.gObjects) {
            Rectangle goBBox = go.getBoundingBox();
            boundingBox = boundingBox == null ? goBBox : boundingBox.union(goBBox);
        }
        return boundingBox;
    }

    @Override
    public double selectionDistance(Point mousePoint) {
        double minDist = Double.MAX_VALUE;
        for (GraphicalObject go : gObjects) {
            minDist = Math.min(minDist, go.selectionDistance(mousePoint));
        }
        return minDist;
    }

    @Override
    public void render(Renderer r) {
        gObjects.forEach(go -> go.render(r));
    }

    @Override
    public void addGraphicalObjectListener(GraphicalObjectListener l) {
        listeners.add(l);
    }

    @Override
    public void removeGraphicalObjectListener(GraphicalObjectListener l) {
        listeners.remove(l);
    }

    @Override
    public String getShapeName() {
        return "KompozitGO";
    }

    @Override
    public GraphicalObject duplicate() {
        List<GraphicalObject> list = new ArrayList<>();
        gObjects.forEach(go -> list.add(go.duplicate()));
        return new CompositeShape(list);
    }

    @Override
    public String getShapeID() {
        return "@COMP";
    }

    @Override
    public void save(List<String> rows) {
        // @COMP broj neposredno prethodno dostupnih objekata koji čine njegovu djecu.
        gObjects.forEach(go -> go.save(rows));
        rows.add(getShapeID() + " " + gObjects.size());
    }

    @Override
    public void load(Stack<GraphicalObject> stack, String data) {
        if(!data.trim().contains(" ")) {
            int objNum = Integer.parseInt(data.trim());  // koliko prethodno-stvorenih objekata na stogu čini djecu kompozita

            CompositeShape go = (CompositeShape) duplicate();  // stvara se NOVI kompozit

            for(int i = 0; i < objNum; i++) {
                // sa stoga se skida utvrđeni broj objekata i postavlja ih se kao djecu novostvorenog kompozita
                go.addChild(stack.pop());
            }

            stack.push(go);  // kompozit stavlja na stog
        }
    }

}
