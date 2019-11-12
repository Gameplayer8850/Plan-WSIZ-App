package plan_wsiz_wroc;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class Rysowanie extends JPanel {

    int x;
    int y;
    int szerokosc;
    int wysokosc;
    int etap;

    @Override
    public void paintComponent(Graphics g) {
        szerokosc = wysokosc = etap * 50;
        g.setColor(Color.ORANGE);
        g.fillOval(450 - (szerokosc / 2), 400 - (szerokosc / 2), szerokosc, wysokosc);
        szerokosc = wysokosc = (8 - etap) * 50;
        g.setColor(Color.BLACK);
        g.fillOval(450 - (szerokosc / 2), 400 - (szerokosc / 2), szerokosc, wysokosc);

        if (etap < (8 - etap)) {
            szerokosc = wysokosc = etap * 50;
            g.setColor(Color.ORANGE);
            g.fillOval(450 - (szerokosc / 2), 400 - (szerokosc / 2), szerokosc, wysokosc);
        }

    }
}
