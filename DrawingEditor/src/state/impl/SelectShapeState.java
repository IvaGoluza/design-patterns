package state.impl;

import RenderImplementor.Renderer;
import model.drawing.DocumentModel;
import model.graphic.GraphicalObject;
import model.graphic.impl.CompositeShape;
import utils.Point;
import utils.Rectangle;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class SelectShapeState extends IdleState{

    private final DocumentModel model;

    public SelectShapeState(DocumentModel model) {
        this.model = model;
    }

    @Override
    public void mouseDown(Point mousePoint, boolean shiftDown, boolean ctrlDown) {

        if (!ctrlDown) model.clearSelectedObjects();  // bez CTRL: ako je neki drugi go selektiran, on se automatski treba odselektirati

        GraphicalObject go = model.findSelectedGraphicalObject(mousePoint);  // Ako drži pritisnutu tipku CTRL, objekti se dodaju u selekciju
        if (go != null) go.setSelected(true);

    }

    @Override
    public void afterDraw(Renderer r, GraphicalObject go) {
        if (!go.isSelected()) return;

        for (GraphicalObject selectedObject : model.getSelectedObjects()) {
            drawRect(selectedObject.getBoundingBox(), r);
        }

        // if (go instanceof CompositeShape) return;
        if(model.getSelectedObjects().size() == 1) {   // ako je samo jedan selektirani go, prikaži mu i hotpointe
            for (int i = 0; i < go.getNumberOfHotPoints(); i++) {
                drawHotPoint(go.getHotPoint(i), r);
            }
        }
    }

    private void drawRect(Rectangle rect, Renderer r) {

        Point bottomLeft = new Point(rect.getX(), rect.getY());
        Point topLeft = new Point(rect.getX(), rect.getY() + rect.getHeight());
        Point bottomRight = new Point(rect.getX() + rect.getWidth(), rect.getY());
        Point topRight = new Point(rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight());

        r.drawLine(bottomLeft, topLeft);
        r.drawLine(topLeft, topRight);
        r.drawLine(topRight, bottomRight);
        r.drawLine(bottomLeft, bottomRight);

    }

    private void drawHotPoint(Point hp, Renderer r) {

        Point bottomLeft = new Point(hp.getX() - 3, hp.getY() - 3);
        Point topLeft = new Point(hp.getX() - 3, hp.getY() + 3);
        Point bottomRight = new Point(hp.getX() + 3, hp.getY() - 3);
        Point topRight = new Point(hp.getX() + 3, hp.getY() + 3);

        r.drawLine(bottomLeft, topLeft);
        r.drawLine(topLeft, topRight);
        r.drawLine(topRight, bottomRight);
        r.drawLine(bottomLeft, bottomRight);

    }

    @Override
    public void mouseDragged(Point mousePoint) {
        if (model.getSelectedObjects().size() == 1) {  // samo 1 selected go: omogući pomicanje hotpointa
            GraphicalObject go = model.getSelectedObjects().get(0);

            int hpIdx = model.findSelectedHotPoint(go, mousePoint);  // hotpoint najbliži pokazivaču miša
            if (hpIdx != -1) {
                go.setHotPointSelected(hpIdx, true);
                go.setHotPoint(hpIdx, mousePoint);  // translatacija hotpointa tamo gdje pokazuje miš
            }
        }
    }

    @Override
    public void keyPressed(int keyCode ) {

        if(model.getSelectedObjects().size() == 0) return;

        // translatacija za 1 ili više selektiranih objekata
        if (keyCode == KeyEvent.VK_UP) {
            model.getSelectedObjects().forEach(go -> go.translate(new Point(0, -1)));
        }
        if (keyCode == KeyEvent.VK_DOWN)
            model.getSelectedObjects().forEach(go-> go.translate(new Point(0, 1)));
        if (keyCode == KeyEvent.VK_RIGHT)
            model.getSelectedObjects().forEach(go-> go.translate(new Point(1, 0)));
        if (keyCode == KeyEvent.VK_LEFT)
            model.getSelectedObjects().forEach(go-> go.translate(new Point(-1, 0)));

        // promjena Z-poretka ako je selektiran samo jedan go
        if (keyCode == KeyEvent.VK_PLUS && model.getSelectedObjects().size() == 1)
            model.increaseZ(model.getSelectedObjects().get(0));
        if (keyCode == KeyEvent.VK_MINUS && model.getSelectedObjects().size() == 1)
            model.decreaseZ(model.getSelectedObjects().get(0));

        // grupiranje selektiranih go u kompozitni go
        if (keyCode == KeyEvent.VK_G) {
            CompositeShape compositeShape = new CompositeShape(new ArrayList<>(model.getSelectedObjects()));  // novi kompozit čija su djeca selektirani go
            compositeShape.getGObjects().forEach(model::removeGraphicalObject);  // obriši pojedinačne selektirane go
            model.addGraphicalObject(compositeShape);  // dodaj kompozit u model
            compositeShape.setSelected(true);
        }
        // odgrupiranje kompozita
        if (keyCode == KeyEvent.VK_U) {
            List<GraphicalObject> selectedObjects = model.getSelectedObjects();
            if (selectedObjects.size() == 1 && selectedObjects.get(0) instanceof CompositeShape compositeShape) {  // ako je jedini selektirani objekt - kompozit
                model.removeGraphicalObject(compositeShape);  // iz modela briše taj kompozit
                List<GraphicalObject> objects = compositeShape.getGObjects();
                objects.forEach(graphicalObject -> {
                    graphicalObject.setSelected(true);  // selektirana djeca
                    model.addGraphicalObject(graphicalObject); // ponovno dodaje u model djecu obrisanog kompozita
                });
            }
        }
    }

    @Override
    public void onLeaving() {
        model.clearSelectedObjects();  // Napuštanjem ovog stanja automatski treba desektirati sve selektirane objekte
    }

}
