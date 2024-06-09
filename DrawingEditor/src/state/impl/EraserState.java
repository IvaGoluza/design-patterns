package state.impl;

import RenderImplementor.Renderer;
import model.drawing.DocumentModel;
import model.graphic.GraphicalObject;
import utils.Point;

import java.util.ArrayList;
import java.util.List;

public class EraserState extends IdleState {

    private final DocumentModel model;

    private final List<Point> eraserPoints;

    public EraserState(DocumentModel model) {
        this.model = model;
        this.eraserPoints = new ArrayList<>();
    }

    @Override
    public void mouseUp(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
        // brisanje svih go obuhvaćenih krivuljom gumice načinjenom od točaka eraserPoints
        for(Point p : this.eraserPoints){
            List<GraphicalObject> objectsToErase = new ArrayList<>();
            for(GraphicalObject object: model.list()){
                if(object.selectionDistance(p) < 3) objectsToErase.add(object);
            }
            if(objectsToErase.size() != 0)
                objectsToErase.forEach(model::removeGraphicalObject);
        }
        eraserPoints.clear();  // obrisana trenutna krivulja gumice
    }

    @Override
    public void mouseDragged(Point mousePoint) {
        eraserPoints.add(mousePoint);
        model.notifyListeners();
    }

    @Override
    public void afterDraw(Renderer r, GraphicalObject go) {
        for(int i = 0; i < eraserPoints.size() - 1; i++){   // vizualizacija krivulje miša
            r.drawLine(eraserPoints.get(i), eraserPoints.get(i+1));
        }
    }


}
