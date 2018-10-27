package autoscrabble;

import java.io.File; //Für Dateien
import java.io.FileNotFoundException; //Fehler, entsteht, wenn eine Datei nicht aufzufinden ist
import java.io.FileReader; //Liest Dateien
import java.io.IOException; //Eingabe/Ausgabe Fehler, entsteht, wenn eine Datei nicht lesbar ist
import java.util.ArrayList; //Listen
import java.util.HashMap; //"Wörterbuch" : jedem Schlüssel(wert) s wird ein Wert w zugeordnet
import java.util.Scanner; //Terminal : Eingabe

/**
 *
 * @author lars
 */
public class AutoScrabble {

    public static final Scanner EINGABE=new Scanner(System.in); //Benutzereingabe
    public static String[] kuerzel; //Alle Kürzel
    public static String[] woerter; //Alle zu prüfenden Wörter
    public static String wort; //Aktuell geprüftes Wort
    public static HashMap positionen; //Stellen, an denen Kürzel im Wort auftauchen
    public static char[] buchstaben; //Großbuchstaben
    public static ArrayList<String> kombinationen; //Alle möglichen Zeichenketten
    public static int n; //Von n Zeichen
    public static final String SEPARATOR = fuelle('-', 50); //Separator

    public static String leseDatei(File pfad_zur_datei) throws FileNotFoundException, IOException { //Liest eine Datei ein, und gibt Text zurück
        FileReader datei = new FileReader(pfad_zur_datei);
        String r = "";
        int i = datei.read();
        while (i != -1) {
            r += (char) i;
            i = datei.read();
        }
        return r;
    }

    public static File dateiFrage(String frage) { //Fragt nach einem Pfad und prüft, ob dieser existiert
        while (true) {
            System.out.println(frage + "(Pfad zu .txt Datei) ? ");
            String s = EINGABE.nextLine();
            File f = new File(s);
            if (f.exists() && !f.isDirectory() && f.canRead()) {
                return f;
            }
            System.out.println("Bitte antworten sie mit einem vorhandenen Pfad einer .txt Datei. Versuchen sie es erneut.");
        }
    }

    public static boolean jaNeinFrage(String frage) { //Stellt eine ja/nein Frage
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

    public static <T> String gebeListeAus(ArrayList<T> k) { //Gibt eine Liste benutzerfreundlich aus
        int zeilenumbruch = (int) (Math.sqrt(k.size()));
        int counter = 0;
        int index = 0;
        String s = "";
        for (T objekt : k) {
            s += objekt.toString();
            counter++;
            index++;
            if (index != k.size()) {
                if (counter == zeilenumbruch) {
                    s += "\n";
                    counter = 0;
                } else {
                    s += ", ";
                }
            }
        }
        return s;
    }

    public static String fuelle(char c, int m) { //Erzeugt einen Text aus m mal Schriftzeichen c
        String e = "";
        for (int i = 0; i < m; i++) {
            e += c;
        }
        return e;
    }

    public static boolean umlaut(char c) { //Prüft, ob ein Schriftzeichen ein Umlaut ist
        return c == 'Ä' || c == 'Ö' || c == 'Ü';
        //Gebe "Wahr" zurück, wenn das Schriftzeichen Ö, Ä oder Ü ist, ansonsten gebe "Falsch" zurück. 
        //Die Kleinbuchstaben müssen wir hier nicht prüfen, da wir nur mit Großbuchstaben arbeiten
    }

    public static void moeglichkeiten(int n, String aktuell) { //Rekursive Funktion, generiert alle Möglichkeiten mit n Zeichen aus Großbuchstaben
        for (int i = 0; i < buchstaben.length; i++) {
            if (n == AutoScrabble.n) { //Wenn diese Kombination fertig generiert wurde
                kombinationen.add(aktuell + buchstaben[i]); //Kann sie an die Liste der Kombis angehängt werden
            } else {
                moeglichkeiten(n + 1, aktuell + buchstaben[i]); //Sonst muss wieder gestartet werden, diesmal mit aktueller Kombi als Startwert
            }
        }
    }

    public static HashMap<Integer, ArrayList<String>> findKuerzels(String s) { //Findet Städtekuerzel in einem Kennzeichen und speichert, an welchen Indexes welche Kürzel zu finden sind.
        HashMap<Integer, ArrayList<String>> result = new HashMap();
        char[] chars = s.toCharArray();
        int kplace;
        for (String k : kuerzel) { //Für alle Kürzel
            kplace = 0;
            for (int i = 0; i < chars.length; i++) { //Buchstaben des Wortes durchgehen
                if (chars[i] == k.charAt(kplace)) { //Entspricht der aktuelle Buchstabe dem Buchstaben, der jetzt kommen müsste
                    kplace++;
                } else { //Falls nicht
                    i -= kplace; //Gehe zurück, weil sich das Kennzeichen hier finden lassen könnte
                    kplace = 0; //Wir fangen an, den ersten Buchstaben des Wortes zum Vergleich zu stellen
                }
                if (kplace == k.length()) { //Wenn das komplette Wort aufzufinden ist
                    int key = i + 1 - k.length(); //Anfangsposition im Wort
                    ArrayList<String> value = result.get(key); //Gibt es schon eine Kürzel an dieser Stelle ?
                    if (value == null) { //Wenn nicht, erstellen wir eine neue Liste und fügen das eben gefundene Kürzel hinzu
                        ArrayList<String> a = new ArrayList();
                        a.add(k);
                        result.put(i + 1 - k.length(), a);
                    } else { //Es gibt schon eine Liste
                        value.add(k); //Fügt das Kennzeichen zu der Liste hinzu
                    }
                    kplace = 0; //Wir fangen an, den ersten Buchstaben des Wortes zum Vergleich zu stellen
                }
            }
        }
        return result; //Gefunden Kürzel als "Wörterbuch" zurückgeben
    }

    public static boolean schreibbar(String s) { //Gibt aus, ob eine ein Wort mit einem Kennzeichen schreibbar ist. Sinnvoll für Wörter aus 2-5 Buchstaben
        char[] c = s.toCharArray();
        for (String k : kuerzel) {
            if (k.length() < c.length && c.length - 2 <= k.length()) { //Wäre der Anhang maximal zwei Buchstaben, und mindestens einen lang
                boolean unmoeglich = false;
                for (int j = c.length - 1; j >= k.length(); j--) { //Würde der Anhang Umlaute enthalten, ist es nicht mit diesem Kürzel möglich
                    if (umlaut(c[j])) {
                        unmoeglich = true;
                        break;
                    }
                }
                if (unmoeglich) {
                    continue;
                }
                for (int i = 0; i < k.length(); i++) { //Prüfen, ob das Kürzel auch tatsächlich ab Wortanfang vorzufinden ist, falls nicht, ist es mit diesem Kürzel nicht möglich
                    if (k.charAt(i) != c[i]) {
                        unmoeglich = true;
                        break;
                    }
                }
                if (unmoeglich) {
                    continue;
                }
                return true; //Sollte es mit diesem Kürzel gehen, ist das Wort offensichtlich schreibbar
            }
        }
        return false; //Sollte es mit keinem Kürzel geklappt haben, ist es wohl leider nicht möglich
    }

    public static ArrayList<String> pruefe(int z) { //Gibt alle Wörter mit z Buchstaben zurück, die mit Kennzeichen nicht schreibbar sind
        ArrayList<String> ergebnis = new ArrayList(); //Liste nicht schreibbarer Wörter
        kombinationen = new ArrayList(); //Alle Kombinationen aus z Buchstaben
        n = z - 1;
        moeglichkeiten(0, ""); //Alle Wörter generieren
        System.gc(); //Speicher freigeben
        for (String c : kombinationen) {
            if (!schreibbar(c)) { //Falls nicht schreibbar
                ergebnis.add(c); //Gilt dieses Wort als nicht schreibbar
            }
        }
        return ergebnis; //Liste der nicht schreibbaren Wörter mit z Buchstaben zurückgeben
    }

    public static void pruefeMoegliche() { //Ermittelt nicht schreibbare Wörter
        System.out.println(SEPARATOR);
        boolean timo_schreibbar = schreibbar("TIMO"); //Prüfen, ob "TIMO" schreibbar ist
        System.out.println("TIMO ist ");
        if (!timo_schreibbar) { //Wenn "TIMO" nicht schreibbar ist, wird zwischen "TIMO" und "ist schreibbar" nicht ausgegeben
            System.out.println("nicht ");
        }
        System.out.println("schreibbar");
        System.out.println(SEPARATOR); //Trennlinie
        System.out.println("Nicht schreibbare Wörter mit zwei Buchstaben : ");
        System.out.println(gebeListeAus(pruefe(2))); //Alle nicht schreibbaren Wörter mit zwei Buchstaben ausgeben
        System.gc(); //Speicher freigeben
        System.out.println(SEPARATOR); //Trennlinie
        System.out.println("Nicht schreibbare Wörter mit drei Buchstaben : ");
        System.out.println(gebeListeAus(pruefe(3))); //Alle nicht schreibbaren Wörter mit drei Buchstaben ausgeben
        System.gc(); //Speicher freigeben
        System.out.println(SEPARATOR); //Trennlinie
        if (jaNeinFrage("Wollen sie alle Kombinationen von Wörtern mit 4 Buchstaben, die nicht mit einem Kennzeichen schreibbar sind, anzeigen lassen")) { //Falls erwünscht...
            System.out.println("Nicht schreibbare Wörter mit vier Buchstaben : ");
            System.out.println(gebeListeAus(pruefe(4))); //...alle nicht schreibbaren Wörter mit vier Buchstaben ausgeben(dauert allerdings)
            System.out.println(SEPARATOR); //Trennlinie
        }
    }

    public static ArrayList<Kennzeichen> schreibeWort(ArrayList<Kennzeichen> a, int stelle) { //Schreibt ein Wort aus Kennzeichen, falls das Wort nicht schreibbar ist, wird "null" zurückgegeben
        if (wort==null) { //STOP ! Es ist schon eine Lösung gefunden worden.
            return null;
        }
        if (stelle == wort.length()) {
            wort=null; //Bedeutet : Es ist eine Lösung gefunden worden, sämtliche Prozesse, die gestartet wurden und auch nach einer Lösung suchen, werden dann angehalten
            return a; //Kennzeichenliste zurückgeben
        }
        ArrayList<String> kuerzel_bei_stelle = (ArrayList<String>) positionen.get(stelle); //Alle Kürzel einholen, die sich an dem Anfang des noch zu schreibenden Teils befinden
        if (kuerzel_bei_stelle == null || wort.length()-stelle == 1) { //Dieser Prozess fand keine Lösung, wenn es im jetzt noch zu schreibenden Teil kein Kürzel am Anfang gibt oder noch ein Buchstabe übrig bleibt
            return null;
        } else {
            for (String s : kuerzel_bei_stelle) { //Unter Verwendung dieses Kürzels
                int pos = s.length() + stelle; //Stelle im Wort + Kuerzel
                if (wort.length() - pos >= 1 && !umlaut(wort.charAt(pos))) { //Bleibt mindestens noch ein Buchstabe für den Anhang unter Verwendung dieses Kürzels, und wäre dies kein Umlaut ?
                    //Kopie der Kennzeichen-Liste erstellen
                    ArrayList b = new ArrayList();
                    b.addAll(a);
                    //Neues Kennzeichen hinzufügen (Aktuell geprüftes Kürzel+ein Buchstabe Anhang, durchnummerieren)
                    b.add(new Kennzeichen(s, "" + wort.charAt(pos), a.size() + 1));
                    ArrayList c = schreibeWort(b, pos + 1); //Weiter "schreiben"
                    if (c != null) { //Konnte es zuendegeschrieben werden ?
                        return c; //Gebe das Ergebnis zurück
                    }
                }
                if (wort.length() - pos >= 2 && !umlaut(wort.charAt(pos)) && !umlaut(wort.charAt(pos + 1))) { //Bleiben mindestens noch zwei Buchstabe für den Anhang unter Verwendung dieses Kürzels, und wäre unter diesen kein Umlaut ?
                    //Kopie der Kennzeichen-Liste erstellen
                    ArrayList b = new ArrayList();
                    b.addAll(a);
                    //Neues Kennzeichen hinzufügen (Aktuell geprüftes Kürzel+zwei Buchstaben Anhang, durchnummerieren)
                    b.add(new Kennzeichen(s, "" + wort.charAt(pos) + wort.charAt(pos + 1), a.size() + 1));
                    ArrayList c = schreibeWort(b, pos + 2); //Weiter "schreiben"
                    if (c != null) { //Konnte es zuendegeschrieben werden ?
                        return c; //Gebe das Ergebnis zurück
                    }
                }
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        buchstaben = new char[29];
        for (int c = 0; c < 26; c++) { //Generiert alle Buchstaben
            buchstaben[c] = (char) (65 + c); //Wandelt Zahl in Buchstaben um
        }
        buchstaben[26] = 'Ä';
        buchstaben[27] = 'Ö';
        buchstaben[28] = 'Ü';
        //Das Buchstaben-Array enthält jetzt : buchstaben = [A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z, Ä, Ö, Ü]
        kuerzel = leseDatei(dateiFrage("Wo befindet sich die Liste aller Städtekürzel")).split("\n"); 
        pruefeMoegliche(); //Ermittele nicht-schreibbare Wörter
        woerter = leseDatei(dateiFrage("Wo befindet sich die Aufgabe")).split("\n");
        System.gc(); //Speicher befreien
        for (String w : woerter) { //Wörter durchgehen
            System.out.println(SEPARATOR); //Trennline
            wort = w;
            System.out.println(wort); //Wort ausgeben
            positionen = findKuerzels(wort); //Alle Kürzel im Wort ausmachen
            ArrayList<Kennzeichen> k = schreibeWort(new ArrayList(), 0); //Ermittle eine Liste aus Kennzeichen, mit denen das Wort schreibbar ist
            if (k != null) { //Sollte eine gefunden worden sein
                System.out.println(gebeListeAus(k));
            } else { //Ansonsten war's wohl nicht schreibbar
                System.out.println("Nicht schreibbar");
            }
            System.gc(); //Speicher freigeben
        }
    }
}
