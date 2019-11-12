package plan_wsiz_wroc;

import javax.swing.JOptionPane;

public class Plan_WSIZ_Wroc {

    public static void main(String[] args) {
        Loading_screen ls = new Loading_screen("LOADING SCREEN...");
        Dane dane = ls.zwrocDane();
        boolean flaga;
        do {
            flaga = false;
            String nr_semestru = JOptionPane.showInputDialog("Na którym semestrze jesteś: ");
            try {
                dane.numer_semestru = Integer.parseInt(nr_semestru);
                if (dane.numer_semestru < 1 || dane.numer_semestru > 7) {
                    flaga = true;
                }
            } catch (Exception ex) {
                flaga = true;
            }
        } while (flaga);
        ls.flaga = false;
        ls.setVisible(false);
        ls.dispose();
        Menu menu = new Menu("Główne menu", dane);
    }

}
