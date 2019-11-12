package plan_wsiz_wroc;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Stary_plan {

    String nazwa_planu = "";

    public Stary_plan(String nazwa) {
        this.nazwa_planu = nazwa;
    }

    public String plik_planu(boolean usuwanie) {
        File folder = new File(System.getProperty("user.dir"));

        File[] files = folder.listFiles();
        List<File> pliki = new ArrayList<File>();

        for (File file : files) {
            if (file.getName().contains(".xls") && !file.getName().equals(nazwa_planu)) {
                pliki.add(file);
            }
        }
        if (pliki.isEmpty()) {
            return "";
        }

        File najnowszy = pliki.get(0);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        try {
            for (File file : pliki) {
                Date date1 = sdf.parse(sdf.format(najnowszy.lastModified()));
                Date date2 = sdf.parse(sdf.format(file.lastModified()));
                if (date2.compareTo(date1) > 0) {
                    najnowszy = file;
                }
            }
        } catch (Exception ex) {
            return "";
        }
        if (usuwanie) {
            usuwanie_starych_planow(pliki, najnowszy);
        }
        return najnowszy.getName();
    }

    void usuwanie_starych_planow(List<File> pliki, File najnowszy) {
        for (File file : pliki) {
            if (file != najnowszy) {
                file.delete();
            }
        }
    }

}
