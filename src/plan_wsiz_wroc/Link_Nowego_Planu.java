package plan_wsiz_wroc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Link_Nowego_Planu extends Webowe_Komponenty {

    String kod_html_strony = "";
    boolean bezpiecznik = false;

    public Link_Nowego_Planu() {

    }

    public void kod_html() { //pobieramy kod html strony szkoły
        try {
            link = new URL("https://www.wsiz.wroc.pl/plany-zajec/");
            ws = link.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(ws, "UTF8"));
            String line;
            while ((line = br.readLine()) != null) {
                kod_html_strony += (line + "\n");
            }
            if (ws != null) {
                ws.close();
            }
        } catch (IOException ioe) {
            bezpiecznik = true;
        }
    }

    public String link() { //w kodzie html wyszukujemy linku do planu lekcji
        kod_html();
        if (bezpiecznik == true) {
            return "";
        }
        System.out.println("Trwa wyodrębnianie linku nowego planu lekcji z kodu html");
        int index_start = kod_html_strony.indexOf("Plan zajęć (S)");
        index_start = kod_html_strony.indexOf("https://", index_start);
        int index_stop = kod_html_strony.indexOf("\"", index_start);
        return kod_html_strony.substring(index_start, index_stop);
    }
}
