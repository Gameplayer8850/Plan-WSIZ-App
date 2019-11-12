package plan_wsiz_wroc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

public class Dane_z_Planu {

    HSSFWorkbook wb;
    HSSFWorkbook wb2;
    HSSFSheet sheet;
    HSSFSheet sheet2;
    Dane dane;
    public String plan_zajec[][];
    public int ilosc_wierszy = 0;
    public int ilosc_kolumn = 0;
    public boolean nie_ma_podanego_dnia = false;

    public Dane_z_Planu(Dane dane) {
        this.dane = dane;
    }

    public void Szukanie_arkusza(String zrodlo) throws IOException {//przekazaniu pliku do streama
        FileInputStream fis = new FileInputStream(new File(zrodlo));
        wb = new HSSFWorkbook(fis);
    }

    public void Szukanie_arkusza(String zrodlo, String zrodlo2) throws IOException {//przekazaniu pliku do streama
        FileInputStream fis = new FileInputStream(new File(zrodlo));
        FileInputStream fis2 = new FileInputStream(new File(zrodlo2));
        wb = new HSSFWorkbook(fis);
        wb2 = new HSSFWorkbook(fis2);
    }

    public int Szukanie_arkusza() { //szuka arkusza, w którym znajduje się podany przez nas dzień
        try {
            Szukanie_arkusza(System.getProperty("user.dir") + "/" + dane.plan_nowy_nazwa);
        } catch (IOException io) {
        }
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            String nazwa = wb.getSheetName(i);
            if (!nazwa.contains("-")) {
                continue;
            }
            int index_kreski = nazwa.indexOf("-");
            int index_miesiaca_do = nazwa.indexOf(".", index_kreski) + 1;
            String miesiac_system_rzymski;
            if (nazwa.substring(index_miesiaca_do).contains(".")) {
                miesiac_system_rzymski = nazwa.substring(index_miesiaca_do, nazwa.indexOf(".", index_miesiaca_do));
            } else {
                miesiac_system_rzymski = nazwa.substring(index_miesiaca_do);
            }
            int miesiac_arkusz = Konwersja_z_Rzymskich_na_Arabskie(miesiac_system_rzymski);
            if (miesiac_arkusz == 0) {
                return -1; //błąd
            }
            if (miesiac_arkusz == dane.miesiac) {
                int dzien_arkusz;
                try {
                    dzien_arkusz = Integer.parseInt(nazwa.substring((index_kreski + 1), (index_miesiaca_do - 1)));
                } catch (Exception exception) {
                    return -1;
                }
                if (dane.dzien > dzien_arkusz) {
                    continue;
                }
                return i;
            } else {
                if (nazwa.indexOf(".") == (index_miesiaca_do - 1)) {
                    continue;
                }
                int miesiac_arkusz_od = Konwersja_z_Rzymskich_na_Arabskie(nazwa.substring((nazwa.indexOf(".") + 1), index_kreski));
                if (miesiac_arkusz_od == dane.miesiac) {
                    return i;
                }
            }
        }
        return -1;
    }

    int Konwersja_z_Rzymskich_na_Arabskie(String miesiac) {
        switch (miesiac) {
            case "I":
                return 1;
            case "II":
                return 2;
            case "III":
                return 3;
            case "IV":
                return 4;
            case "V":
                return 5;
            case "VI":
                return 6;
            case "VII":
                return 7;
            case "VIII":
                return 8;
            case "IX":
                return 9;
            case "X":
                return 10;
            case "XI":
                return 11;
            case "XII":
                return 12;
            default:
                return 0;
        }
    }

    public void Odczytywanie_wykladow() { //wydziela i wyświetla lekcji z podanego przez nas dnia
        nie_ma_podanego_dnia = false;
        int numer_arkusza = Szukanie_arkusza();
        if (numer_arkusza == -1) {
            nie_ma_podanego_dnia = true;
            return;
        }
        if (dane.numer_semestru % 2 == 0) {
            dane.numer_semestru--;
        }
        sheet = wb.getSheetAt(numer_arkusza);

        FormulaEvaluator forlulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
        String data = dane.miesiac < 10 ? dane.dzien + ".0" + dane.miesiac : dane.dzien + "." + dane.miesiac;
        int index_start = 0, index_stop = 0;
        String[][] text = new String[sheet.getLastRowNum() + 1][sheet.getRow(0).getLastCellNum() + 1];
        List<CellRangeAddress> index = sheet.getMergedRegions();
        Integer[][] pola_scalone = new Integer[4][2];
        for (int k = 0; k < 4; k++) {
            pola_scalone[k][0] = pola_scalone[k][1] = 100;
        }
        int kolumna_od, kolumna_do;
        kolumna_od = kolumna_do = 0;
        int cykl = 0;
        for (CellRangeAddress cell_scalona : index) {
            if (cell_scalona.getFirstRow() == 1) {
                pola_scalone[cykl][0] = cell_scalona.getFirstColumn();
                pola_scalone[cykl][1] = cell_scalona.getLastColumn();
                cykl++;
            }
        }
        //pola_scalone
        Arrays.sort(pola_scalone, (a, b) -> Integer.compare(a[0], b[0]));
        if (pola_scalone[((dane.numer_semestru + 1) / 2) - 1][1] == 100) {
            kolumna_od = dane.kolumna_od = kolumna_do = dane.kolumna_do = 0;
        } else {
            kolumna_od = dane.kolumna_od = pola_scalone[((dane.numer_semestru + 1) / 2) - 1][0];
            kolumna_do = dane.kolumna_do = pola_scalone[((dane.numer_semestru + 1) / 2) - 1][1];
        }

        for (Row row : sheet) {
            for (Cell cell : row) {
                switch (forlulaEvaluator.evaluateInCell(cell).getCellType()) {
                    case NUMERIC:
                        text[row.getRowNum()][cell.getColumnIndex()] = (cell.getNumericCellValue() + "");
                        break;
                    case STRING:
                        if (cell.getStringCellValue().contains(data)) {
                            index_start = row.getRowNum();
                        }
                        if (cell.getStringCellValue().contains("Gr") && cell.getStringCellValue().length() < 3 && index_start != 0 && index_stop == 0 && row.getRowNum() > (index_start + 2)) {
                            index_stop = row.getRowNum();
                        }
                        text[row.getRowNum()][cell.getColumnIndex()] = cell.getStringCellValue();
                        break;
                }
            }
        }
        if (index_start == 0) {
            nie_ma_podanego_dnia = true;
            return;
        }
        if (index_stop == 0) {
            index_stop = sheet.getLastRowNum() - 3;
        }

        plan_zajec = new String[index_stop - index_start][kolumna_do - kolumna_od + 1];
        ilosc_wierszy = index_stop - index_start;
        ilosc_kolumn = kolumna_do - kolumna_od + 1;

        for (int i = index_start; i < index_stop; i++) {
            for (int j = kolumna_od; j < kolumna_do + 1; j++) {
                if (i == index_start && j == kolumna_od) {
                    plan_zajec[i - index_start][j - kolumna_od] = text[index_start][kolumna_od];
                }
                if (j == kolumna_od) {
                    if (text[i][j] == null) {
                        break;
                    }
                    if (text[i][j + 1] != null && (text[i][j + 1].replace(" ", "")).equals("")) {
                        text[i][j + 1] = null;
                    }
                    if (text[i][kolumna_do] != null && (text[i][kolumna_do].replace(" ", "")).equals("")) {
                        text[i][kolumna_do] = null;
                    }
                    if (text[i][j + 1] == null && text[i][kolumna_do] == null) {
                        break;
                    }
                    String pomocnicza_zmienna = text[i][j].replace(" ", "");
                    if (pomocnicza_zmienna.equals("")) {
                        break;
                    }
                }

                for (CellRangeAddress cell_scalona : index) {
                    if (cell_scalona.getLastColumn() == j && cell_scalona.getLastRow() == i) {
                        text[i][j] = text[i][j - 1];
                        break;
                    }
                }
                if (text[i][j] == null) {
                    continue;
                } else {
                    plan_zajec[i - index_start][j - kolumna_od] = text[i][j];
                }
            }
        }
    }

    public String[][] Porownywanie() {
        try {
            Szukanie_arkusza(System.getProperty("user.dir") + "/" + dane.plan_nowy_nazwa, System.getProperty("user.dir") + "/" + dane.plan_zapisany_nazwa);
        } catch (IOException io) {
        }
        int kolumna_od, kolumna_do;
        kolumna_od = dane.kolumna_od;
        kolumna_do = dane.kolumna_do;
        List<List> wiersze = new ArrayList<List>();
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            if (kolumna_od == kolumna_do) {
                break;
            }
            int[][] indexy_dni = new int[4][2];
            int dzien = 0;
            sheet = wb.getSheetAt(i);
            sheet2 = wb2.getSheetAt(i);
            if (!sheet.getSheetName().contains("-") || sheet.getSheetName().contains("Sesja")) {
                continue;
            }
            FormulaEvaluator forlulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
            String[][] text = new String[sheet.getLastRowNum() + 1][sheet.getRow(0).getLastCellNum() + 1];
            List<CellRangeAddress> indexy_scalone = sheet.getMergedRegions();
            for (Row row : sheet) {
                for (Cell cell : row) {
                    switch (forlulaEvaluator.evaluateInCell(cell).getCellType()) {
                        case NUMERIC:
                            text[row.getRowNum()][cell.getColumnIndex()] = (cell.getNumericCellValue() + " ");
                            break;
                        case STRING:
                            if (cell.getStringCellValue().contains("Gr") && cell.getStringCellValue().length() < 3) {
                                if (dzien == 0 || (dzien != 0 && indexy_dni[dzien - 1][0] != row.getRowNum() - 1)) {
                                    indexy_dni[dzien][0] = row.getRowNum() - 1;
                                    dzien++;
                                }
                            }
                            text[row.getRowNum()][cell.getColumnIndex()] = cell.getStringCellValue() != null ? cell.getStringCellValue() : " ";
                            break;
                    }
                }
            }
            for (int k = 0; k < sheet.getLastRowNum(); k++) {
                for (int j = dane.kolumna_od; j < dane.kolumna_do + 1; j++) {
                    for (CellRangeAddress cell_scalona : indexy_scalone) {
                        if (cell_scalona.getLastColumn() == j && cell_scalona.getLastRow() == k) {
                            if (text[k][j - 1] == null || (text[k][j - 1].contains("PONIEDZIAŁEK") || text[k][j - 1].contains("WTOREK") || text[k][j - 1].contains("ŚRODA") || text[k][j - 1].contains("CZWARTEK"))) {
                                continue;
                            }
                            text[k][j] = text[k][j - 1];
                            break;
                        }
                    }
                }
            }
            dzien = 0;
            FormulaEvaluator forlulaEvaluator2 = wb2.getCreationHelper().createFormulaEvaluator();
            String[][] text2 = new String[sheet2.getLastRowNum() + 1][sheet2.getRow(0).getLastCellNum() + 1];
            List<CellRangeAddress> indexy_scalone2 = sheet2.getMergedRegions();
            for (Row row : sheet2) {
                for (Cell cell : row) {
                    switch (forlulaEvaluator2.evaluateInCell(cell).getCellType()) {
                        case NUMERIC:
                            text2[row.getRowNum()][cell.getColumnIndex()] = (cell.getNumericCellValue() + " ");
                            break;
                        case STRING:
                            if (cell.getStringCellValue().contains("Gr") && cell.getStringCellValue().length() < 3) {
                                if (dzien == 0 || (dzien != 0 && indexy_dni[dzien - 1][1] != row.getRowNum() - 1)) {
                                    indexy_dni[dzien][1] = row.getRowNum() - 1;
                                    dzien++;
                                }
                            }
                            text2[row.getRowNum()][cell.getColumnIndex()] = cell.getStringCellValue() != null ? cell.getStringCellValue() : " ";
                            break;
                    }
                }
            }
            for (int k = 0; k < sheet2.getLastRowNum(); k++) {
                for (int j = dane.kolumna_od; j < dane.kolumna_do + 1; j++) {
                    for (CellRangeAddress cell_scalona : indexy_scalone2) {
                        if (cell_scalona.getLastColumn() == j && cell_scalona.getLastRow() == k) {
                            if (text2[k][j - 1] == null || (text2[k][j - 1].contains("PONIEDZIAŁEK") || text2[k][j - 1].contains("WTOREK") || text2[k][j - 1].contains("ŚRODA") || text2[k][j - 1].contains("CZWARTEK"))) {
                                continue;
                            }
                            text2[k][j] = text2[k][j - 1];
                            break;
                        }
                    }
                }
            }
            int cykl = 0;
            while (cykl < 4) {
                int index_start_1 = indexy_dni[cykl][0];
                int index_start_2 = indexy_dni[cykl][1];
                int index_stop_1 = sheet.getLastRowNum() - 3;
                int index_stop_2 = sheet2.getLastRowNum() - 3;
                if (cykl != 3) {
                    index_stop_1 = indexy_dni[cykl + 1][0];
                    index_stop_2 = indexy_dni[cykl + 1][1];
                }
                boolean roznica = false;
                if (index_stop_1 - index_start_1 != index_stop_2 - index_start_1) {
                    roznica = true;
                }
                while (roznica == false) {
                    for (int index = kolumna_od; index < kolumna_do + 1; index++) {
                        if (text[index_start_1][index] == null) {
                            text[index_start_1][index] = "";
                        }
                        if (text2[index_start_2][index] == null) {
                            text2[index_start_2][index] = "";
                        }
                        if (!text[index_start_1][index].replace(" ", "").equals(text2[index_start_2][index].replace(" ", ""))) {
                            roznica = true;
                            break;
                        }
                    }
                    if (index_start_1 == index_stop_1) {
                        break;
                    }
                    index_start_1++;
                    index_start_2++;
                }
                if (roznica) {
                    index_start_1 = indexy_dni[cykl][0];
                    index_start_2 = indexy_dni[cykl][1];
                    index_stop_1 = sheet.getLastRowNum() - 3;
                    index_stop_2 = sheet2.getLastRowNum() - 3;
                    if (cykl != 3) {
                        index_stop_1 = indexy_dni[cykl + 1][0];
                        index_stop_2 = indexy_dni[cykl + 1][1];
                    }
                    while (true) {
                        List<String> kolumny = new ArrayList<String>();
                        if (index_start_1 == index_stop_1 - 1 && index_start_2 != index_stop_2 - 1) {
                            for (int index = kolumna_od; index < kolumna_do + 1; index++) {
                                kolumny.add("");
                            }
                            for (int index = kolumna_od; index < kolumna_do + 1; index++) {
                                if (text2[index_start_2][index] == null) {
                                    kolumny.add("");
                                } else {
                                    kolumny.add(text2[index_start_2][index]);
                                }
                            }
                            wiersze.add(kolumny);
                            index_start_2++;
                            continue;
                        } else if (index_start_1 != index_stop_1 - 1 && index_start_2 == index_stop_2 - 1) {
                            for (int index = kolumna_od; index < kolumna_do + 1; index++) {
                                if (text[index_start_1][index] == null) {
                                    kolumny.add("");
                                } else {
                                    kolumny.add(text[index_start_1][index]);
                                }
                            }
                            for (int index = kolumna_od; index < kolumna_do + 1; index++) {
                                kolumny.add("");
                            }
                            wiersze.add(kolumny);
                            index_start_1++;
                            continue;
                        } else if (index_start_1 == index_stop_1 - 1 && index_start_2 == index_stop_2 - 1) {
                            break;
                        }

                        for (int index = kolumna_od; index < kolumna_do + 1; index++) {
                            if (text[index_start_1][index] == null) {
                                kolumny.add("");
                            } else {
                                kolumny.add(text[index_start_1][index]);
                            }
                        }
                        for (int index = kolumna_od; index < kolumna_do + 1; index++) {
                            if (text2[index_start_2][index] == null) {
                                kolumny.add("");
                            } else {
                                kolumny.add(text2[index_start_2][index]);
                            }
                        }
                        wiersze.add(kolumny);
                        index_start_1++;
                        index_start_2++;
                    }
                }
                cykl++;
            }
        }
        if (dane.kolumna_od == dane.kolumna_do || wiersze.isEmpty()) {
            String[][] dane_t = new String[1][((dane.kolumna_do - dane.kolumna_od + 1) * 2)];
            for (int i = 0; i < ((dane.kolumna_do - dane.kolumna_od + 1) * 2); i++) {
                dane_t[0][i] = "";
            }
            return dane_t;
        }
        String[][] dane_t = new String[wiersze.size()][wiersze.get(0).size()];
        for (int i = 0; i < wiersze.size(); i++) {
            for (int j = 0; j < wiersze.get(0).size(); j++) {
                dane_t[i][j] = (String) wiersze.get(i).toArray()[j];
            }
        }
        return dane_t;
    }

    public void Wyszukaj_dane_zajecia() {
        try {
            Szukanie_arkusza(System.getProperty("user.dir") + "/" + dane.plan_nowy_nazwa);
        } catch (IOException io) {
        }
        int kolumna_od, kolumna_do;
        kolumna_od = dane.kolumna_od;
        kolumna_do = dane.kolumna_do;
        if (kolumna_do - kolumna_od + 1 > 2) {
            kolumna_od = kolumna_do = dane.kolumna_od + dane.numer_grupy;
        }
        int liczba_zajec = 0;
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            if (dane.kolumna_od == dane.kolumna_do) {
                break;
            }
            int[] indexy_dni = new int[4];
            int dzien = 0;
            sheet = wb.getSheetAt(i);
            if (!sheet.getSheetName().contains("-") || sheet.getSheetName().contains("Sesja")) {
                continue;
            }
            FormulaEvaluator forlulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
            String[][] text = new String[sheet.getLastRowNum() + 1][sheet.getRow(0).getLastCellNum() + 1];
            List<CellRangeAddress> indexy_scalone = sheet.getMergedRegions();
            for (Row row : sheet) {
                for (Cell cell : row) {
                    switch (forlulaEvaluator.evaluateInCell(cell).getCellType()) {
                        case NUMERIC:
                            text[row.getRowNum()][cell.getColumnIndex()] = (cell.getNumericCellValue() + " ");
                            break;
                        case STRING:
                            if (cell.getStringCellValue().contains("Gr") && cell.getStringCellValue().length() < 3) {
                                if (dzien == 0 || (dzien != 0 && indexy_dni[dzien - 1] != row.getRowNum() - 1)) {
                                    indexy_dni[dzien] = row.getRowNum() - 1;
                                    dzien++;
                                }
                            }
                            text[row.getRowNum()][cell.getColumnIndex()] = cell.getStringCellValue() != null ? cell.getStringCellValue() : " ";
                            break;
                    }
                }
            }
            for (int k = 0; k < sheet.getLastRowNum(); k++) {
                for (int j = dane.kolumna_od; j < dane.kolumna_do + 1; j++) {
                    for (CellRangeAddress cell_scalona : indexy_scalone) {
                        if (cell_scalona.getLastColumn() == j && cell_scalona.getLastRow() == k) {
                            if (text[k][j - 1] == null || (text[k][j - 1].contains("PONIEDZIAŁEK") || text[k][j - 1].contains("WTOREK") || text[k][j - 1].contains("ŚRODA") || text[k][j - 1].contains("CZWARTEK"))) {
                                continue;
                            }
                            text[k][j] = text[k][j - 1];
                            break;
                        }
                    }
                }
            }
            dzien = 3;
            boolean flaga = false;
            String[] frazy = dane.nazwa_przedmiotu.split(" ");
            for (int k = 0; k < sheet.getLastRowNum() - 5; k++) {
                for (int j = kolumna_od; j < kolumna_do + 1; j++) {
                    if (text[k][j] != null) {
                        boolean bezpiecznik = true;
                        for (int z = 0; z < frazy.length; z++) {
                            if (!text[k][j].toLowerCase().contains(frazy[z])) {
                                bezpiecznik = false;
                                break;
                            }
                        }
                        if (bezpiecznik) {
                            liczba_zajec++;
                            j = kolumna_do;
                        }
                    }
                    if (liczba_zajec == dane.ktore_zajecia) {
                        while (dzien > -1) {
                            if (k > indexy_dni[dzien]) {
                                flaga = true;
                                break;
                            } else {
                                dzien--;
                            }
                        }
                        break;
                    }
                }
                if (flaga) {
                    break;
                }
            }
            if (liczba_zajec == dane.ktore_zajecia) {
                int index1 = indexy_dni[dzien];
                int index2 = dzien != 3 ? indexy_dni[dzien + 1] : sheet.getLastRowNum() - 3;
                ilosc_wierszy = index2 - index1;
                ilosc_kolumn = dane.kolumna_do - dane.kolumna_od + 1;
                plan_zajec = new String[index2 - index1][dane.kolumna_do - dane.kolumna_od + 1];
                for (int k = index1; k < index2; k++) {
                    for (int j = dane.kolumna_od; j < dane.kolumna_do + 1; j++) {
                        if (k == index1 && j == dane.kolumna_od) {
                            plan_zajec[k - index1][j - dane.kolumna_od] = text[index1][dane.kolumna_od];
                        }
                        if (j == dane.kolumna_od) {
                            if (text[k][j] == null) {
                                break;
                            }
                            if (text[k][j + 1] != null && (text[k][j + 1].replace(" ", "")).equals("")) {
                                text[k][j + 1] = null;
                            }
                            if (text[k][dane.kolumna_do] != null && (text[k][dane.kolumna_do].replace(" ", "")).equals("")) {
                                text[k][dane.kolumna_do] = null;
                            }
                            if (text[k][j + 1] == null && text[k][dane.kolumna_do] == null) {
                                break;
                            }
                            String pomocnicza_zmienna = text[k][j].replace(" ", "");
                            if (pomocnicza_zmienna.equals("")) {
                                break;
                            }
                        }
                        if (text[k][j] != null) {
                            plan_zajec[k - index1][j - dane.kolumna_od] = text[k][j];
                        }
                    }
                }
                return;
            }
        }
        this.nie_ma_podanego_dnia = true;
    }
}
