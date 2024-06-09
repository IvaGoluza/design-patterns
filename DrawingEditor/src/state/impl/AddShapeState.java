package state.impl;

import model.drawing.DocumentModel;
import model.graphic.GraphicalObject;
import utils.Point;

public class AddShapeState extends IdleState {

    private final GraphicalObject prototype;
    private final DocumentModel model;

    public AddShapeState(DocumentModel model, GraphicalObject prototype){
        this.model = model;
        this.prototype = prototype;
    }

    @Override
    public void mouseDown(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
        GraphicalObject go = prototype.duplicate();
        go.translate(mousePoint);
        model.addGraphicalObject(go);
    }
}
