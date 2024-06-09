package model.graphic.impl;

import model.graphic.GraphicalObject;
import observer.GraphicalObjectListener;
import utils.GeometryUtil;
import utils.Point;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGraphicalObject implements GraphicalObject {

    private final Point[] hotPoints;
    private final boolean[] hotPointsSelected; // are hotPoints selected
    private boolean selected;  // is graphical object selected
    protected final List<GraphicalObjectListener> listeners = new ArrayList<>();

    public AbstractGraphicalObject(Point[] hotPoints) {
        this.hotPoints = hotPoints;
        this.hotPointsSelected = new boolean[hotPoints.length];
    }

    @Override
    public Point getHotPoint(int index) {
        if (index < 0 || index >= hotPoints.length)
            throw new IllegalArgumentException();
        return hotPoints[index];
    }

    @Override
    public void setHotPoint(int index, Point point) {
        if (index < 0 || index >= hotPoints.length)
            throw new IllegalArgumentException();
        hotPoints[index] = point;
        notifyListeners();
    }

    @Override
    public int getNumberOfHotPoints() {
        return hotPoints.length;
    }

    @Override
    public double getHotPointDistance(int hotpointIndex, Point point) {
        if (hotpointIndex < 0 || hotpointIndex >= hotPoints.length)
            throw new IllegalArgumentException();
        return GeometryUtil.distanceFromPoint(hotPoints[hotpointIndex], point);
    }

    @Override
    public boolean isHotPointSelected(int index) {
        if (index < 0 || index >= hotPoints.length)
            throw new IllegalArgumentException();
        return hotPointsSelected[index];
    }

    @Override
    public void setHotPointSelected(int index, boolean isSelected) {
        if ( index < 0 || index >= hotPoints.length)
            throw new IllegalArgumentException();
        hotPointsSelected[index] = isSelected;
        notifyListeners();
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        notifySelectionListeners();
        notifyListeners();
    }

    @Override
    public void translate(Point d) {
        for (int i = 0; i < hotPoints.length; i++) {
            hotPoints[i] = hotPoints[i].translate(d);
        }
        notifyListeners();
    }

    public void addGraphicalObjectListener(GraphicalObjectListener graphicalObjectListener){
        listeners.add(graphicalObjectListener);
    }
    public void removeGraphicalObjectListener(GraphicalObjectListener graphicalObjectListener){
        listeners.remove(graphicalObjectListener);
    }

    protected void notifyListeners(){
        listeners.forEach(listener -> listener.graphicalObjectChanged(this));
    }
    protected void notifySelectionListeners(){
        listeners.forEach(listener -> listener.graphicalObjectSelectionChanged(this));
    }

}
