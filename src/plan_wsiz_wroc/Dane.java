package plan_wsiz_wroc;

public class Dane {

    public boolean zapisany_plan, znaleziono_plan, pobrano_plan, znaleziony_plan_jest_nowy;
    public String plan_zapisany_nazwa, plan_nowy_nazwa, plan_link, nazwa_przedmiotu;
    public int numer_semestru, miesiac, miesiac_wybrany, dzien, dzien_wybrany, kolumna_od, kolumna_do, aktualna_funkcja_programu, ktore_zajecia, numer_grupy;
    public int ilosc_dni = 3;

    public Dane() {
        zapisany_plan = znaleziono_plan = pobrano_plan = znaleziony_plan_jest_nowy = false;
        plan_zapisany_nazwa = plan_nowy_nazwa = plan_link = nazwa_przedmiotu = "";
        numer_semestru = miesiac = miesiac_wybrany = dzien = dzien_wybrany = kolumna_od = kolumna_do = aktualna_funkcja_programu = ktore_zajecia = numer_grupy = 0;
    }
}
