package hr.fer.zemris.ooup.lab3.task1;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("MyComponent");
        MyComponent myComponent = new MyComponent();
        frame.add(myComponent);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        myComponent.setFocusable(true);
        myComponent.requestFocusInWindow();
        myComponent.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(myComponent);
                    topFrame.dispose();
                }
            }
        });
    }
}
