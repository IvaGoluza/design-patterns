package model.drawing;

import RenderImplementor.Renderer;
import RenderImplementor.impl.G2DRendererImpl;
import model.GUI;
import state.impl.IdleState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DrawingCanvas extends JComponent {
    private final GUI gui;
    private final DocumentModel model;

    public DrawingCanvas(GUI gui, DocumentModel model) {
        super();
        this.gui = gui;
        this.model = model;

        registerKeyListeners();
        registerMouseListeners();
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        Renderer r = new G2DRendererImpl(g2d);

        model.list().forEach(go -> {
            go.render(r);
            gui.getCurrentState().afterDraw(r, go);  // nakon što je platno nacrtalo grafički objekt predan kao argument
        });
        gui.getCurrentState().afterDraw(r);  // nakon što je platno nacrtalo čitav crtež

    }

    private void registerKeyListeners() {

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    gui.getCurrentState().onLeaving();
                    gui.setCurrentState(new IdleState());
                }
                gui.getCurrentState().keyPressed(e.getKeyCode());
            }
        });
        setFocusable(true);
    }

    private void registerMouseListeners() {

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                MouseEvent mouseEvent = SwingUtilities.convertMouseEvent(e.getComponent(), e, DrawingCanvas.this);
                gui.getCurrentState().mouseDown(new utils.Point(mouseEvent.getX(), mouseEvent.getY()),
                        e.isShiftDown(),
                        e.isControlDown());
                e.consume();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                MouseEvent mouseEvent = SwingUtilities.convertMouseEvent(e.getComponent(), e, DrawingCanvas.this);
                gui.getCurrentState().mouseUp(new utils.Point(mouseEvent.getX(), mouseEvent.getY()),
                        e.isShiftDown(),
                        e.isControlDown());
                e.consume();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                MouseEvent mouseEvent = SwingUtilities.convertMouseEvent(e.getComponent(), e, DrawingCanvas.this);
                gui.getCurrentState().mouseDragged(new utils.Point(mouseEvent.getX(), mouseEvent.getY()));
                e.consume();
            }
        });
    }

}
