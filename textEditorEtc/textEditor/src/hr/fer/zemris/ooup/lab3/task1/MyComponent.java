package hr.fer.zemris.ooup.lab3.task1;

import javax.swing.*;
import java.awt.*;

public class MyComponent extends JComponent {
    public MyComponent() {
        setPreferredSize(new Dimension(500, 500));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.RED);
        g.drawLine(15, 15, getWidth() - 15, 15); //  -
        g.drawLine(15, 20, 15, getHeight() - 20); //  |

        g.setColor(Color.BLACK);
        g.drawString("Prvi red teksta", 30, 40);
        g.drawString("Drugi red teksta", 30, getHeight() - 30);
    }
}

