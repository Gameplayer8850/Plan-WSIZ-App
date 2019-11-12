package plan_wsiz_wroc;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.TimeZone;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Maro
 */
public class Menu extends JFrame implements ActionListener {

    JMenuBar menu = new JMenuBar();
    JPanel panel = new JPanel();
    JMenu funkcje = new JMenu("Funkcje");
    JMenu ustawienia = new JMenu("Ustawienia");
    JMenu informacje = new JMenu("Informacje");

    JMenuItem plan_najblizszy = new JMenuItem("Plan na najbliższe dni");
    JMenuItem dany_dzien = new JMenuItem("Plan z podanego dnia");
    JMenuItem zmiany_w_planie = new JMenuItem("Zmiany w planie");
    JMenuItem numer_zajec = new JMenuItem("Wyszukaj zajęcia numer X");
    JMenuItem zmien_semestr = new JMenuItem("Zmień semestr");
    JMenuItem zmien_grupe = new JMenuItem("Zmień grupę");
    JMenuItem creditsy = new JMenuItem("Creditsy");

    JButton zatwierdz;
    Dane dane;
    String[][] dane_do_tabeli;
    boolean zmiana_grupy_menu = false;

    public Menu(String nazwa, Dane dane) {
        super(nazwa);
        this.dane = dane;
        dane.aktualna_funkcja_programu = 1;
        ustawienia(true);

    }

    void ustawienia(boolean main) {
        setSize(900, 800);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        setLocation(x, y);

        plan_najblizszy.addActionListener(this);
        dany_dzien.addActionListener(this);
        zmiany_w_planie.addActionListener(this);
        zmien_semestr.addActionListener(this);
        zmien_grupe.addActionListener(this);
        numer_zajec.addActionListener(this);
        creditsy.addActionListener(this);

        funkcje.add(plan_najblizszy);
        funkcje.add(dany_dzien);
        funkcje.add(zmiany_w_planie);
        funkcje.add(numer_zajec);
        ustawienia.add(zmien_semestr);
        ustawienia.add(zmien_grupe);

        informacje.add(creditsy);

        menu.add(funkcje);
        menu.add(ustawienia);
        menu.add(informacje);

        if (!dane.zapisany_plan) {
            zmiany_w_planie.setEnabled(false);
        }
        zmien_grupe.setEnabled(false);
        add(menu);
        setJMenuBar(menu);
        BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxlayout);
        if (main) {
            Plan_dzisiejszy();
        }
        add(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == plan_najblizszy) {
            dane.aktualna_funkcja_programu = 1;
            Refresh(1);
        } else if (source == dany_dzien) {
            boolean flaga;
            do {
                flaga = false;
                String miesiac = JOptionPane.showInputDialog("Podaj miesiąc: ");
                try {
                    dane.miesiac_wybrany = Integer.parseInt(miesiac);
                    if (dane.miesiac_wybrany < 1 || dane.miesiac_wybrany > 12) {
                        flaga = true;
                    }
                } catch (Exception ex) {
                    flaga = true;
                }
            } while (flaga);
            do {
                flaga = false;
                String dzien = JOptionPane.showInputDialog("Podaj dzień: ");
                try {
                    dane.dzien_wybrany = Integer.parseInt(dzien);
                    if (dane.dzien_wybrany < 1 || dane.dzien_wybrany > 31) {
                        flaga = true;
                    }
                } catch (Exception ex) {
                    flaga = true;
                }
            } while (flaga);
            dane.aktualna_funkcja_programu = 2;
            Refresh(2);
        } else if (source == zmiany_w_planie && dane.zapisany_plan) {
            dane.aktualna_funkcja_programu = 3;
            Refresh(3);
        } else if (source == zmien_semestr) {
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
            Refresh(dane.aktualna_funkcja_programu);
        } else if (source == zmien_grupe) {
            zmiana_grupy_menu = true;
            boolean flaga;
            do {
                flaga = false;
                String grupa = JOptionPane.showInputDialog("Podaj grupę: ");
                try {
                    char znak = grupa.toUpperCase().charAt(0);
                    dane.numer_grupy = znak - 64;
                    if (dane.numer_grupy > dane.kolumna_do - dane.kolumna_od + 1 || dane.numer_grupy < 1) {
                        flaga = true;
                    }
                } catch (Exception ex) {
                    flaga = true;
                }
            } while (flaga);
            Refresh(dane.aktualna_funkcja_programu);
        } else if (source == numer_zajec) {
            dane.aktualna_funkcja_programu = 4;
            boolean flaga;
            do {
                flaga = false;
                try {
                    dane.nazwa_przedmiotu = JOptionPane.showInputDialog("Podaj nazwę przedmiotu: ").toLowerCase();
                } catch (Exception ex) {
                    flaga = true;
                }
            } while (flaga);
            do {
                flaga = false;
                String ktore_zajecia = JOptionPane.showInputDialog("Podaj numer zajęć: ");
                try {
                    dane.ktore_zajecia = Integer.parseInt(ktore_zajecia);
                    if (dane.ktore_zajecia < 1) {
                        flaga = true;
                    }
                } catch (Exception ex) {
                    flaga = true;
                }
            } while (flaga);
            Refresh(dane.aktualna_funkcja_programu);
        } else if (source == creditsy) {
            JOptionPane.showMessageDialog(rootPane, "Program został wykonany przez Marka Żubryckiego");
        } else if (source == zatwierdz) {
            Refresh(dane.aktualna_funkcja_programu);
        }
    }

    void Plan_dzisiejszy() {
        JPanel panel2 = new JPanel();
        BoxLayout boxlayout = new BoxLayout(panel2, BoxLayout.Y_AXIS);
        panel2.setLayout(boxlayout);
        try {
            BufferedImage logo = ImageIO.read(new File("WSIZ.jpg"));
            JLabel picLabel = new JLabel(new ImageIcon(logo));
            panel2.add(picLabel);
        } catch (Exception ex) {
        }
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        dane.dzien = calendar.get(Calendar.DATE);
        dane.miesiac = calendar.get(Calendar.MONTH) + 1;
        int ilosc_dni = dane.ilosc_dni;
        Dane_z_Planu dzp = new Dane_z_Planu(dane);
        do {
            do {
                dzp.Odczytywanie_wykladow();
                if (dzp.nie_ma_podanego_dnia && dane.dzien < 31) {
                    dane.dzien++;
                } else if (dzp.nie_ma_podanego_dnia) {
                    dane.dzien = 1;
                    if (dane.miesiac < 12) {
                        dane.miesiac++;
                    } else {
                        dane.miesiac = 1;
                    }
                }
                if (dane.miesiac == 2 || dane.miesiac == 7) {
                    break;
                }
            } while (dzp.nie_ma_podanego_dnia);
            if (dzp.nie_ma_podanego_dnia) {
                break;
            }
            JLabel napis = new JLabel(dzp.plan_zajec[0][0]);
            panel2.add(napis);
            dzp.plan_zajec[1][0] = "Godzina";
            String[] kolumny = dzp.plan_zajec[1];
            String[][] wyklady = new String[dzp.ilosc_wierszy * 2][dzp.ilosc_kolumn];
            int index_wierszy = -2;
            for (int i = 2; i < dzp.ilosc_wierszy; i++) {
                for (int j = 0; j < dzp.ilosc_kolumn; j++) {
                    if (j == 0 && dzp.plan_zajec[i][j] == null) {
                        break;
                    }
                    if (j == 0) {
                        index_wierszy += 2;
                    }
                    if (dzp.plan_zajec[i][j] == null) {
                        dzp.plan_zajec[i][j] = "";
                    }
                    wyklady[index_wierszy][j] = dzp.plan_zajec[i][j];
                }
            }
            if (index_wierszy < 0) {
                index_wierszy = 0;
            }
            String[][] wyklady_finalnie = Arrays.copyOf(wyklady, index_wierszy + 1);
            JTable table = new JTable(wyklady_finalnie, kolumny);
            JScrollPane sp = new JScrollPane(table);
            panel2.add(sp);
            if (dane.dzien < 31) {
                dane.dzien++;
            } else {
                dane.dzien = 1;
                if (dane.miesiac < 12) {
                    dane.miesiac++;
                } else {
                    dane.miesiac = 1;
                }
            }
            if (dane.miesiac == 2 || dane.miesiac == 7) {
                break;
            }
            ilosc_dni--;
        } while (ilosc_dni != 0);
        Slider(panel2);
        panel2.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        panel = panel2;
    }

    JPanel Plan_na_podany_dzien() {
        JPanel panel2 = new JPanel();
        BoxLayout boxlayout = new BoxLayout(panel2, BoxLayout.Y_AXIS);
        panel2.setLayout(boxlayout);
        try {
            BufferedImage myPicture = ImageIO.read(new File("WSIZ.jpg"));
            JLabel picLabel = new JLabel(new ImageIcon(myPicture));
            panel2.add(picLabel);
        } catch (Exception ex) {
        }
        int ilosc_dni = dane.ilosc_dni;
        Dane_z_Planu dzp = new Dane_z_Planu(dane);
        boolean flaga = false;
        do {
            do {
                dzp.Odczytywanie_wykladow();
                if (dzp.nie_ma_podanego_dnia && dane.dzien < 31) {
                    dane.dzien++;
                } else if (dzp.nie_ma_podanego_dnia) {
                    dane.dzien = 1;
                    if (dane.miesiac < 12) {
                        dane.miesiac++;
                    } else {
                        dane.miesiac = 1;
                    }
                }
                if (dane.miesiac == 2 || dane.miesiac == 7) {
                    break;
                }
            } while (dzp.nie_ma_podanego_dnia);
            if (dzp.nie_ma_podanego_dnia) {
                break;
            }
            JLabel napis = new JLabel(dzp.plan_zajec[0][0]);
            panel2.add(napis);
            dzp.plan_zajec[1][0] = "Godzina";
            String[] kolumny = dzp.plan_zajec[1];
            String[][] wyklady = new String[dzp.ilosc_wierszy * 2][dzp.ilosc_kolumn];
            int index_wierszy = -2;
            for (int i = 2; i < dzp.ilosc_wierszy; i++) {
                for (int j = 0; j < dzp.ilosc_kolumn; j++) {
                    if (j == 0 && dzp.plan_zajec[i][j] == null) {
                        break;
                    }
                    if (j == 0) {
                        index_wierszy += 2;
                    }
                    if (dzp.plan_zajec[i][j] == null) {
                        dzp.plan_zajec[i][j] = "";
                    }
                    wyklady[index_wierszy][j] = dzp.plan_zajec[i][j];
                }
            }
            if (index_wierszy < 0) {
                index_wierszy = 0;
            }
            String[][] wyklady_finalnie = Arrays.copyOf(wyklady, index_wierszy + 1);
            JTable table = new JTable(wyklady_finalnie, kolumny);
            JScrollPane sp = new JScrollPane(table);
            panel2.add(sp);
            if (dane.dzien < 31) {
                dane.dzien++;
            } else {
                dane.dzien = 1;
                if (dane.miesiac < 12) {
                    dane.miesiac++;
                } else {
                    dane.miesiac = 1;
                }
            }
            if (dane.miesiac == 2 || dane.miesiac == 7) {
                break;
            }
            ilosc_dni--;
        } while (ilosc_dni != 0);
        Slider(panel2);
        panel2.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        return panel2;
    }

    void Slider(JPanel panel) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 1, 3, dane.ilosc_dni);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        Hashtable labelTable = new Hashtable();
        labelTable.put(1, new JLabel("1"));
        labelTable.put(2, new JLabel("2"));
        labelTable.put(3, new JLabel("3"));
        slider.setLabelTable(labelTable);

        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                dane.ilosc_dni = slider.getValue();
            }
        });
        JPanel slider_panel = new JPanel();
        slider_panel.setLayout(new BoxLayout(slider_panel, BoxLayout.LINE_AXIS));
        slider_panel.add(Box.createHorizontalGlue());
        JLabel label = new JLabel("Liczba wyświetlanych dni: ");
        slider_panel.add(label);
        slider_panel.add(Box.createHorizontalGlue());
        slider_panel.add(slider);
        zatwierdz = new JButton("Zatwierdź");
        zatwierdz.addActionListener(this);

        slider_panel.add(Box.createHorizontalGlue());
        slider_panel.add(zatwierdz);
        slider_panel.add(Box.createHorizontalGlue());
        panel.add(slider_panel);

    }

    JPanel Zajecia_o_podanym_numerze() {
        JPanel panel2 = new JPanel();
        BoxLayout boxlayout = new BoxLayout(panel2, BoxLayout.Y_AXIS);
        panel2.setLayout(boxlayout);
        try {
            BufferedImage myPicture = ImageIO.read(new File("WSIZ.jpg"));
            JLabel picLabel = new JLabel(new ImageIcon(myPicture));
            panel2.add(picLabel);
        } catch (Exception ex) {
        }
        Dane_z_Planu dzp = new Dane_z_Planu(dane);
        dzp.Wyszukaj_dane_zajecia();
        if (dzp.nie_ma_podanego_dnia) {
            JLabel napis = new JLabel("Nie odnaleziono podanego numeru zajęć.");
            panel2.add(napis);
            panel2.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            return panel2;
        }

        JLabel napis = new JLabel(dzp.plan_zajec[0][0]);
        panel2.add(napis);
        dzp.plan_zajec[1][0] = "Godzina";
        String[] kolumny = dzp.plan_zajec[1];
        String[][] wyklady = new String[dzp.ilosc_wierszy * 2][dzp.ilosc_kolumn];
        int index_wierszy = -2;
        for (int i = 2; i < dzp.ilosc_wierszy; i++) {
            for (int j = 0; j < dzp.ilosc_kolumn; j++) {
                if (j == 0 && dzp.plan_zajec[i][j] == null) {
                    break;
                }
                if (j == 0) {
                    index_wierszy += 2;
                }
                if (dzp.plan_zajec[i][j] == null) {
                    dzp.plan_zajec[i][j] = "";
                }
                wyklady[index_wierszy][j] = dzp.plan_zajec[i][j];
            }
        }
        if (index_wierszy < 0) {
            index_wierszy = 0;
        }
        String[][] wyklady_finalnie = Arrays.copyOf(wyklady, index_wierszy + 1);
        JTable table = new JTable(wyklady_finalnie, kolumny);
        JScrollPane sp = new JScrollPane(table);
        panel2.add(sp);
        if (dane.dzien < 31) {
            dane.dzien++;
        } else {
            dane.dzien = 1;
            if (dane.miesiac < 12) {
                dane.miesiac++;
            } else {
                dane.miesiac = 1;
            }
        }
        panel2.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        return panel2;
    }

    public void Refresh(int aktualny_stan_programu) {
        if (aktualny_stan_programu != 4) {
            setVisible(false);
        }
        zmien_grupe.setEnabled(false);
        switch (aktualny_stan_programu) {
            case 1:
                this.setTitle("Główne menu");
                this.remove(panel);
                Plan_dzisiejszy();
                this.add(panel);
                revalidate();
                repaint();
                setVisible(true);
                break;
            case 2:
                dane.dzien = dane.dzien_wybrany;
                dane.miesiac = dane.miesiac_wybrany;
                this.setTitle("Plan na: " + dane.dzien + "." + dane.miesiac);
                this.remove(panel);
                this.panel = Plan_na_podany_dzien();
                this.add(panel);
                revalidate();
                repaint();
                setVisible(true);
                break;
            case 3:
                this.setTitle("Zmiany w planie");
                this.remove(panel);
                Plan_dzisiejszy();
                this.add(panel);
                this.remove(panel);
                this.panel = Zmiany_w_planie();
                this.add(panel);
                revalidate();
                repaint();
                setVisible(true);
                break;
            case 4:
                this.remove(panel);
                Plan_dzisiejszy();
                boolean flaga;
                if (dane.kolumna_do - dane.kolumna_od + 1 > 2) {
                    zmien_grupe.setEnabled(true);
                    if (!zmiana_grupy_menu) {
                        do {
                            flaga = false;
                            String grupa = JOptionPane.showInputDialog("Podaj grupę: ");
                            try {
                                char znak = grupa.toUpperCase().charAt(0);
                                dane.numer_grupy = znak - 64;
                                if (dane.numer_grupy > dane.kolumna_do - dane.kolumna_od + 1 || dane.numer_grupy < 1) {
                                    flaga = true;
                                }
                            } catch (Exception ex) {
                                flaga = true;
                            }
                        } while (flaga);
                    }
                    zmiana_grupy_menu = false;
                }
                setVisible(false);
                this.setTitle("Zajęcia \"" + dane.nazwa_przedmiotu + "\" o numerze " + dane.ktore_zajecia);
                this.remove(panel);
                Plan_dzisiejszy();
                this.add(panel);
                this.remove(panel);
                this.panel = Zajecia_o_podanym_numerze();
                this.add(panel);
                revalidate();
                repaint();
                setVisible(true);
                break;
            default:
                break;
        }

    }

    JPanel Zmiany_w_planie() {
        JPanel panel2 = new JPanel();
        BoxLayout boxlayout = new BoxLayout(panel2, BoxLayout.Y_AXIS);
        panel2.setLayout(boxlayout);
        try {
            BufferedImage myPicture = ImageIO.read(new File("WSIZ.jpg"));
            JLabel picLabel = new JLabel(new ImageIcon(myPicture));
            panel2.add(picLabel);
        } catch (Exception ex) {
        }
        Dane_z_Planu dzp = new Dane_z_Planu(dane);
        String[] kolumny;
        kolumny = new String[(dane.kolumna_do - dane.kolumna_od + 1) * 2];
        for (int i = 0; i < ((dane.kolumna_do - dane.kolumna_od + 1) * 2); i++) {
            if (i == (((dane.kolumna_do - dane.kolumna_od + 1) % 2 != 0 ? (dane.kolumna_do - dane.kolumna_od + 1) + 1 : (dane.kolumna_do - dane.kolumna_od + 1)) / 2) - 1) {
                kolumny[i] = "Nowy Plan";
            } else if (i == dane.kolumna_do - dane.kolumna_od + 1 + ((dane.kolumna_do - dane.kolumna_od + 1) % 2 != 0 ? dane.kolumna_do - dane.kolumna_od + 2 : dane.kolumna_do - dane.kolumna_od + 1) / 2 - 1) {
                kolumny[i] = "Stary Plan";
            } else {
                kolumny[i] = "";
            }
        }
        dane_do_tabeli = dzp.Porownywanie();
        JTable table = new JTable(dane_do_tabeli, kolumny);
        if (dane.kolumna_od != dane.kolumna_do) {
            for (int i = 0; i < kolumny.length; i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(new CustomRenderer());
            }
        }
        JScrollPane sp = new JScrollPane(table);
        panel2.add(sp);
        panel2.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        return panel2;
    }

    class CustomRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int pomocnicza = column < table.getColumnCount() / 2 ? table.getColumnCount() / 2 : -(table.getColumnCount() / 2);
            if (!dane_do_tabeli[row][column].equals(dane_do_tabeli[row][column + pomocnicza])) {
                cellComponent.setBackground(Color.YELLOW);
            } else {
                cellComponent.setBackground(Color.WHITE);
            }
            if (value.toString().contains("PONIEDZIAŁEK") || value.toString().contains("WTOREK") || value.toString().contains("ŚRODA") || value.toString().contains("CZWARTEK")) {
                cellComponent.setBackground(Color.RED);
            }
            return cellComponent;
        }
    }
}
