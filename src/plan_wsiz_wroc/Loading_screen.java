package plan_wsiz_wroc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;

public class Loading_screen extends JFrame {

    boolean flaga = false;
    boolean dodawanie = true;
    Dane dane = new Dane();

    public Loading_screen(String nazwa) {
        super(nazwa);
        ustawienia();
        rysowanieTlo();

        dane.plan_link = new Link_Nowego_Planu().link();
        if (!dane.plan_link.equals("")) {
            dane.znaleziono_plan = true;
            dane.plan_nowy_nazwa = dane.plan_link.substring(dane.plan_link.indexOf("plan"));
        }
        if (dane.znaleziono_plan) {
            if (!dane.plan_nowy_nazwa.equals(new Stary_plan("").plik_planu(false))) {
                dane.znaleziony_plan_jest_nowy = true;
                new Pobieranie_Planu(dane);
            }
        } else {
            dane.plan_nowy_nazwa = new Stary_plan("").plik_planu(false);
        }
        dane.plan_zapisany_nazwa = new Stary_plan(dane.plan_nowy_nazwa).plik_planu(true);
        if (!dane.plan_zapisany_nazwa.equals("")) {
            dane.zapisany_plan = true;
        }

        try {
            Thread.sleep(3000);
        } catch (Exception ex) {
        }
    }

    private void ustawienia() {
        setSize(900, 800);
        setResizable(false);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        setLocation(x, y);
        setBackground(Color.BLUE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void rysowanieTlo() {
        Rysowanie rys = new Rysowanie();
        new Thread(new Runnable() {
            public void run() {
                int cykl = 1;
                while (true) {
                    if (flaga == true) {
                        break;
                    }
                    if (cykl == 7) {
                        dodawanie = false;
                    } else if (cykl == 1) {
                        dodawanie = true;
                    }
                    rys.etap = cykl;
                    repaint();
                    if (dodawanie == true) {
                        cykl++;
                    } else {
                        cykl--;
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(70);
                    } catch (Exception ex) {
                        break;
                    }
                }
            }
        }).start();
        this.add(rys);
        setVisible(true);
    }

    public Dane zwrocDane() {
        return this.dane;
    }
}
