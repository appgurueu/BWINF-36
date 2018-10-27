/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schwimmbad;

import java.util.Scanner; //Benutzereingabe

/**
 *
 * @author lars
 */
public class Schwimmbad {

    public static final Scanner EINGABE = new Scanner(System.in); //Benutzereingabe
    public static boolean wochentag; //Wochentag ?
    public static boolean schultag; //Schultag ?
    public static int personen; //Anzahl Personen, exklusive Kleinkinder
    public static int erwachsene; //Anzahl Erwachsene
    public static int kinder; //Anzahl Kinder, exklusive Kleinkinder
    public static int gutscheine; //Gutscheine, 0 wenn es kein Schultag ist, oder es einfach keine gibt
    public static Besuch bester_besuch; //Billigste Möglichkeit, Karten zu kaufen, für die gegeben Infos(Gutscheine, E, K, Schul-/Wochentag, etc.)

    public static int[] familienKarten(int t1, int t2, int erwachsene, int kinder) { //Versucht für Erwachsene und Kinder t1 FKs Typ 1, sowie t2 FKs Typ 2 zu organisieren. Mit Priorität werden hierbei Typ 1 FKs erzeugt.
        //Konzept identisch zu imperfekteFamilienkarten, nur eben mit normalen FKs
        int e = erwachsene;
        int k = kinder;
        int f1, f2;
        f1 = 0;
        f2 = 0;
        //Zuerst Typ 1 Familienkarten erzeugen, da diese mehr Ersparnis bringen, solange es geht
        for (int i = 0; i < t1; i++) {
            if (e < 2 || k < 2) {
                break;
            }
            e -= 2;
            k -= 2;
            f1++;
        }
        //Danach Typ 2 erzeugen, solange dass gut geht
        for (int i = 0; i < t2; i++) {
            if (e < 1 || k < 3) {
                break;
            }
            e--;
            k -= 3;
            f2++;
        }
        return new int[]{f1, f2, e, k};
    }

    public static int[] illegaleFamilienKarten(int t1, int t2, int erwachsene, int kinder) {
        //Konzept identisch zu familienkarten, nur eben mit "nicht-vollen" aka "illegalen/imperfekten" FKs. Diese sind : Typ 1 : 2 E, 1 K, sowie 1 E, 2 K
        int e = erwachsene;
        int k = kinder;
        int f1, f2;
        f1 = 0;
        f2 = 0;
        for (int i = 0; i < t1; i++) {
            if (e < 2 || k < 1) {
                break;
            }
            e -= 2;
            k--;
            f1++;
        }
        for (int i = 0; i < t2; i++) {
            if (e < 1 || k < 2) {
                break;
            }
            e--;
            k -= 2;
            f2++;
        }
        return new int[]{f1, f2, e, k};
    }

    public static int[] fuelleFamilienkarten(int b, int erwachsene, int kinder) {
        //Konzept identisch zu fülleImperfekteFamilienkarten, nur eben mit "normalen" Familienkarten
        //Generiert alle Möglichkeiten, "b" FKs zu erzeugen. Die Möglichkeit, bei der der Rest am billigsten wäre, wird zurückgegeben.
        //Achtung : 20 % Ermässigung sind hierbei egal, da das Verhältnis wichtig ist
        int[] best_combo = new int[4];
        int min_rest = Integer.MAX_VALUE;
        float min_preis = Float.MAX_VALUE;
        for (int i = 0; i <= b; i++) {
            int j = (b - i);
            int[] combo = familienKarten(i, j, erwachsene, kinder); //Familienkarten, soviel es geht, nach dieser Kombi erzeugen
            if (combo[2] + combo[3] < min_rest) {
                System.arraycopy(combo, 0, best_combo, 0, 4);
                min_rest = (combo[2] + combo[3]);
                min_preis = combo[2] * 3.50f + combo[3] * 2.50f;
            } else if (combo[2] + combo[3] == min_rest) {
                float preis = combo[2] * 3.50f + combo[3] * 2.50f;
                if (preis < min_preis) { //Wäre der Preis des Restes "geringer" ?
                    //Gilt dies nun als die beste Kombi !
                    System.arraycopy(combo, 0, best_combo, 0, 4);
                    min_preis = combo[2] * 3.50f + combo[3] * 2.50f;
                }
            }
        }
        return best_combo;
    }

    public static int[] fuelleImperfekteFamilienkarten(int karten, int e, int k) {
        //Konzept identisch zu fülleFamilienkarten, nur eben  mit "imperfekten/illegalen" Familienkarten
        //Achtung : 20 % Ermässigung sind hierbei egal, da das Verhältnis wichtig ist
        int[] best_combo = new int[4];
        int min_rest = Integer.MAX_VALUE;
        float min_preis = Float.MAX_VALUE;
        for (int i = 0; i <= karten; i++) {
            int j = (karten - i);
            int[] combo = illegaleFamilienKarten(i, j, e, k); //Illegale Familienkarten, soviel es geht, nach dieser Kombi erzeugen
            if (combo[2] + combo[3] < min_rest) {
                System.arraycopy(combo, 0, best_combo, 0, 4);
                min_rest = (combo[2] + combo[3]);
                min_preis = combo[2] * 3.50f + combo[3] * 2.50f;
            } else if (combo[2] + combo[3] == min_rest) {
                float preis = combo[2] * 3.50f + combo[3] * 2.50f;
                if (preis < min_preis) {
                    System.arraycopy(combo, 0, best_combo, 0, 4);
                    min_preis = combo[2] * 3.50f + combo[3] * 2.50f;
                }
            }
        }
        return best_combo;
    }

    public static int[] fuelleImperfekteFamilienkarten(int e, int k) {
        return fuelleImperfekteFamilienkarten((e + k) / 3, e, k); //Imperfekte Familienkarten erzeugen, soviel es geht
    }

    public static int[] fuelleTageskarten(int b, int erwachsene, int kinder) { //Versucht, b Tageskarten zu erstellen. Gibt zurück, was übrig bleibt.
        int e = erwachsene;
        int k = kinder;
        for (int i = 0; i < b; i++) {
            for (int j = 0; j < 6; j++) {
                if (e != 0) { //Solange es noch Erwachsene gibt, diese in Tageskarten packen, da sie teurer sind
                    e--;
                } else if (k != 0) {
                    k--; //Ansonsten Kinder in Tageskarten packen
                } else { //Es gibt keine Erwachsenen und keine Kinder mehr
                    break; //Man kann aufhören
                }
            }
        }
        return new int[]{e, k};
    }

    public static void kartenWochentag() { //Kartenverteilung an einem Wochentag, Achtung : 20 % REDUKTION, also lohnen sich imperfekte Familienkarten nicht mehr !
        int[] result = new int[3];
        result[0] = (personen / 6); //Tageskarten, die verteilt werden werden
        int rest = personen - (result[0] * 6); //Zahl der Personen, die übrig bleiben werden
        if (rest <= 2) { //Gibt es zwei, oder gar weniger als zwei Personen Rest
            int[] restliche_einzelkarten = fuelleTageskarten(result[0], erwachsene, kinder); //Tageskarten erstellen
            bester_besuch = new Besuch(restliche_einzelkarten[0] /*Restliche Erwachsene*/, restliche_einzelkarten[1] /*Restliche Kinder*/, new int[4] /*TKs*/, result[0]); //Ergebnis zurückgeben
        } else if (rest == 3) { //Gibt es 3 Personenen Rest
            int[] restliche_einzelkarten = fuelleTageskarten(result[0], erwachsene, kinder); //Tageskarten erstellen
            bester_besuch = new Besuch(restliche_einzelkarten[0], restliche_einzelkarten[1], new int[4], result[0]);
        } else if (rest == 4) { //Gibt es 4 P Rest
            int[] restliche_personen = fuelleFamilienkarten(1, erwachsene, kinder); //Wir versuchen, eine Familienkarte unterzubringen
            if (restliche_personen[0] + restliche_personen[1] == 0) { //Hat dies nicht geklappt
                if (kinder == 0) { //Worst case : Alle sind erwachsen
                    bester_besuch = new Besuch(0, 0, new int[4], result[0] + 1); //Nur dann lohnt sich doch eine imperfekte Tageskarte !
                } else {
                    int[] restliche_einzelkarten = fuelleTageskarten(result[0], erwachsene, kinder); //Ansonsten einfach billige, ermässigte Einzelkarten
                    bester_besuch = new Besuch(restliche_einzelkarten[0], restliche_einzelkarten[1], new int[4], result[0]); //Ansonsten lohnen sich wohl doch die Einzelkarten am meisten !
                }
            } else { //Hat's geklappt
                bester_besuch = new Besuch(0, 0, new int[]{restliche_personen[0], restliche_personen[1], 0, 0}, result[0]);
            }
        } else if (rest == 5) { //Gibt es 5 P Rest
            int[] restliche_personen = fuelleFamilienkarten(1, erwachsene, kinder); //Erstmal ermitteln wir, ob sich noch eine Familienkarte unterbringen ließe
            if (restliche_personen[0] + restliche_personen[1] == 0) { //Ist dies der Fall
                bester_besuch = new Besuch(restliche_personen[2], restliche_personen[3], new int[4], result[0] + 1); //Haben wir ein Ergebnis !
            } else if (restliche_personen[2] > 1){ //Ist dies nicht der Fall, gebe es aber mindestens zwei Erwachsene, lohnt sich eine imperfekte TK
                bester_besuch = new Besuch(0, 0, new int[]{restliche_personen[0], restliche_personen[1], 0, 0}, result[0]);
            }
        }
    }

    public static void kartenWochenende() { //Kartenverteilung an keinem Wochentag : Keine Tageskarten
        int[] fakas = fuelleFamilienkarten(personen / 4, erwachsene, kinder); //Zuerst werden Familienkarten erstellt
        int rest_fakas[] = fuelleImperfekteFamilienkarten(fakas[2], fakas[3]); //Aus dem Rest werden imperfekte Familienkarten erstellt
        if (rest_fakas[2] > 2) { //Gibt es mindestens drei übrig bleibende Erwachsene
            if (fakas[1] > 0) { //Gibt es mindestens eine Familienkarte vom Typ 2, also 1 E, 3 K
                fakas[1]--; //Dann lösen wir diese auf
                rest_fakas[0]++; //Und erstellen eine neue, imperfekte Familienkarte mit 2 Erwachsenen und einem Kind
                fakas[0]++; //Sowie eine mit 2 E, 2 K (Typ 1)
                rest_fakas[2] -= 2; //Jetzt sind die Erwachsenen verplant
            }
        }
        fakas[2] = rest_fakas[0];
        fakas[3] = rest_fakas[1];
        bester_besuch = new Besuch(rest_fakas[2] /*Erwachsene*/, rest_fakas[3]/*Kinder*/, fakas /*Familienkarten*/, 0 /*Tageskarten*/); //Besuch einspeichern in globaler Variable
    }

    public static boolean jaNeinFrage(String frage) { //Stellt eine jaNeinFrage
        while (true) {
            System.out.println(frage + "(j/n) ? ");
            String s = EINGABE.nextLine().toLowerCase();
            if (s.equals("j")) {
                return true;
            } else if (s.equals("n")) {
                return false;
            }
            System.out.println("Bitte antworten sie mit j/n beziehungsweise J/N. Versuchen sie es erneut.");
        }
    }

    public static int mengenFrage(String frage) { //Fragt nach einer ganzen Zahl > 0
        while (true) {
            System.out.println(frage + "(Zahl) ? ");
            String s = EINGABE.nextLine().toLowerCase();
            try {
                int so = Integer.parseInt(s);
                if (so >= 0) {
                    return so;
                }
            } catch (Exception e) {

            }
            System.out.println("Bitte antworten sie mit einer ganzen Zahl > 0. Versuchen sie es erneut.");
        }
    }

    public static void karten() { //Verteilt Karten
        if (wochentag) { //Ist Wochentag ?
            kartenWochentag();
        } else { //Ansonsten
            kartenWochenende();
        }
    }

    public static void main(String[] args) {
        wochentag = jaNeinFrage("Wochentag");
        schultag = jaNeinFrage("Schultag");
        personen = mengenFrage("Wie viele Personen");
        if (schultag) { //Nur an Schultagen
            gutscheine = mengenFrage("Wie viele Gutscheine"); //Sind Gutscheine relevant
        }
        kinder = mengenFrage("Wie viele sind Kinder(unter 16)"); //Kleinkinder eingeschlossen
        int kleinkinder = mengenFrage("Wie viele davon sind Kleinkinder(unter 4)");
        erwachsene = personen - kinder; //Alle Personen, die keine Kinder(Kleinkinder inklusive) sind, sind erwachsen
        personen -= kleinkinder; //Kleinkinder sind irrelevant
        kinder -= kleinkinder; //Kleinkinder sind irrelevant
        if (erwachsene == 0 && kleinkinder != 0) { //Gibt es keine Erwachsenen, aber Kleinkinder
            System.out.println("Die " + Integer.toString(kleinkinder) + " Kleinkinder dürfen nicht ohne die Aufsicht einer Erwachsenen Person das Schwimmbad besuchen.");
            System.exit(0); //Wir sind fertig !
        }
        int kinderecht = kinder;
        int erwachseneecht = erwachsene;
        if (gutscheine >= personen) { // Gibt es mehr Gutscheine als Personen
            //Können einfach alle Gutscheine eingesetzt werden
            System.out.println("Setzen sie " + Integer.toString(personen) + " Gutscheine ein, und sie werden nichts bezahlen müssen.");
            System.exit(0); //Wir sind fertig !
        }
        if (gutscheine > 0) { //Gibt es mindestens einen Gutschein
            //Alle Möglichkeiten, die Gutscheine einzusetzen, generieren
            //Erstmal, falls ein Gutschein für die Ermässigung von 10 % eingesetzt werden soll
            Besuch resultat = null;
            if (gutscheine > 1) { //Wenn es genau einen Gutschein gibt, und dieser für 10 % Ermässigung eingesetzt wird, brauchen wir keine "Einsatzmöglichkeiten" für 0 Gutscheine generieren
                for (int i = 0; i < gutscheine; i++) {
                    kinder = kinderecht;
                    erwachsene = erwachseneecht;
                    int ge = (gutscheine - 1) - i;
                    kinder -= i;
                    erwachsene -= ge;
                    erwachsene = Math.max(erwachsene, 0);
                    kinder = Math.max(kinder, 0);
                    personen = kinder + erwachsene;
                    karten();
                    if (resultat == null || bester_besuch.preis < resultat.preis) { //Gibt es noch keinen Rekordhalter, oder ist diese Variante günstiger
                        //Ist diese nun der "Rekordhalter"
                        resultat = bester_besuch;
                        resultat.gutscheine_kinder = i;
                        resultat.gutscheine_erwachsene = ge;
                    }
                }
            }
            else {
                karten();
            }
            resultat.setErmaessigung(); //Ermässigung
            //Und ohne einen Gutschein für die Ermässigung von 10 % einzusetzen
            for (int i = 0; i <= gutscheine; i++) {
                kinder = kinderecht;
                erwachsene = erwachseneecht;
                int ge = gutscheine - i;
                kinder -= i;
                erwachsene -= ge;
                erwachsene = Math.max(erwachsene, 0);
                kinder = Math.max(kinder, 0);
                personen = kinder + erwachsene;
                karten();
                if (bester_besuch.preis < resultat.preis) { //Ist diese Variante günstiger
                    //Ist diese nun der "Rekordhalter"
                    resultat = bester_besuch;
                    resultat.gutscheine_kinder = i;
                    resultat.gutscheine_erwachsene = ge;
                }
            }
            System.out.println(resultat); //Ergebnis ausgeben
            System.exit(0); //Wir sind fertig !
        }
        karten(); //Gibt es keine Gutscheine, können wir einfach normal Karten kaufen
        System.out.println(bester_besuch); //Ergebnis ausgeben
    }
}
