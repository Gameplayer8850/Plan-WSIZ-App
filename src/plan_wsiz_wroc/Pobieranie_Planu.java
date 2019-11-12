package plan_wsiz_wroc;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class Pobieranie_Planu extends Webowe_Komponenty {

    public Pobieranie_Planu(Dane dane) {
        this.pobranie(dane);
    }

    public void pobranie(Dane dane) { //pobieramy najnowszy plan lekcji i zapisujemy go lokacji skąd uruchomiliśmy program
        try {
            inputStream = new BufferedInputStream(new URL(dane.plan_link).openStream());
            fileOS = new FileOutputStream(System.getProperty("user.dir") + "/" + dane.plan_nowy_nazwa);
            data = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                fileOS.write(data, 0, byteContent);
            }
        } catch (IOException e) {

        }
    }
}
