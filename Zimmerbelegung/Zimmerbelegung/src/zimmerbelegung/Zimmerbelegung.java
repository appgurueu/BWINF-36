package zimmerbelegung;

//Nötige Bibliotheken importieren
//Dateien einlesen
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
//Listen
import java.util.ArrayList;
//HashMap
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
//Benutzereingabe
import java.util.Scanner;

/**
 *
 * @author lars
 */
public class Zimmerbelegung {

    public static final Scanner EINGABE=new Scanner(System.in); //Benutzereingabe
    public static Zimmer zimmer; //Aktuelles Zimmer
    public static HashMap<String, Maedchen> maedchen; //Liste aller Maedchen
    public static ArrayList<Zimmer> besteZimmerbelegung; //Die ideale Zimmerbelegung(nach der Vorstellung des Lehrers)
    
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
            if (f.exists() && !f.isDirectory() && f.canRead()) { //Prüfe, ob : - existiert die Datei - ist es kein Ordner - ist sie lesbar
                return f; //Gebe Pfad zurück
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
    
    public static <T> String gebeListeAus(Set<T> k) { //Gibt die "Schlüssel" der Einträge einer HashMap benutzerfreundlich aus
        int zeilenumbruch = (int) (Math.sqrt(k.size()));
        int counter = 0;
        int index = 0;
        String s = "";
        for (T objekt : k) {
            Entry e=(Entry)objekt;
            s += e.getKey().toString();
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

    public static void zimmerAufloesen(ArrayList<Zimmer> aktuelleZimmerbelegung, boolean einzelzimmer) { //Versucht, Zimmer zusammenzuführen, wobei welche "aufgelöst" werden.
        boolean kein_zusammenfuehren = true; //Konnten Zimmer zusammengeführt werden ?
        for (int i = 0; i < aktuelleZimmerbelegung.size(); i++) { //Alle Zimmer durchgehen
            Zimmer z = aktuelleZimmerbelegung.get(i);
            //Für alle Zimmer, für die noch nicht geprüft wurde, ob sie mit diesem Zimmer zusammengeführt werden können, wird dies geprüft und weiterverfolgt
            for (int j = i + 1; j < aktuelleZimmerbelegung.size(); j++) {
                Zimmer k = aktuelleZimmerbelegung.get(j);
                //Will der Lehrer nur Einzelzimmer vermeiden, werden keine nicht-Einzelzimmer mit nicht-Einzelzimmern zusammengeführt
                if (einzelzimmer && (z.zimmerinsassen.size() != 1 && k.zimmerinsassen.size() != 1)) {
                    continue;
                }
                if (k.kannLeiden(z)) { //Können die Zimmer zusammengeführt werden ?
                    kein_zusammenfuehren = false; //Dann konnten offensichtlich noch Zimmer zusammengeführt werden.
                    //Diese fiktive Zimmerbelegung wird weiter überlegt
                    ArrayList<Zimmer> kopie = new ArrayList();
                    kopie.add(k.zimmerZusammenfuehren(z)); //Zimmer k und z als neues Zimmer hinzufügen
                    //Kopie der aktuellen Zimmerbelegung anfertigen, ausgenommen Zimmer k und z
                    for (int n = 0; n < aktuelleZimmerbelegung.size(); n++) {
                        if (n != i && n != j) {
                            kopie.add(aktuelleZimmerbelegung.get(n));
                        }
                    }
                    zimmerAufloesen(kopie, einzelzimmer); //Versuchen, weitere Zimmer zusammenzuführen und alle Möglichkeiten dies zu tun erstellen
                }
            }
        }
        if (kein_zusammenfuehren) { //Falls keine Zimmer mehr zusammengeführt werden konnten
            if (aktuelleZimmerbelegung.size() < besteZimmerbelegung.size()) { //Gibt es bei dieser Zimmerbelegung weniger Zimmer als bei der als ideal geltenden
                besteZimmerbelegung = new ArrayList();
                besteZimmerbelegung.addAll(aktuelleZimmerbelegung); //So gilt diese nun als ideal !
            }
        }
    }

    public static void pruefeMaedchen(String girl) { //Prüft, ob ein Mädchen in einem Zimmer aufgenommen werden kann
        Maedchen g = maedchen.get(girl); //Freundes/Feindeslisten holen
        g.zimmer = zimmer.id; //Das Mädchen soll ab jetzt als Mitglied dieses Zimmers gelten
        maedchen.put(girl, g); //HashMap updaten
        zimmer.zimmerinsassen.put(girl, false); //Es gehört nun zu den Mitgliedern des Zimmers
        for (String s : g.feinde) { //Für alle unerwünschten Mädchen
            if (zimmer.zimmerinsassen.get(s) == null) { //Befindet sich das unerwünschte Mädchen in diesem Zimmer
                zimmer.zimmergegner.put(s, false); //Dieser Gegner wird zum Gegner des aktuellen Zimmers erklärt
            } else { //Befindet sich ein Gegner des Mädchens im Zimmer, sind nicht alle Wünsche erfüllbar, und das Programm stoppt
                System.out.println(girl + "s Wunsch kann leider nicht erfüllt werden. Denn sie kann die Insassin " + s + " nicht leiden");
                System.exit(0);
                return;
            }
        }
        ArrayList<String> freunde_und_bewunderer = new ArrayList();
        freunde_und_bewunderer.addAll(g.freunde);
        for (Entry e : maedchen.entrySet()) { //Finde alle Bewunderer, die noch aufgenommen werden sollen
            Maedchen moeglicher_bewunderer = (Maedchen) e.getValue();
            if (moeglicher_bewunderer.zimmer == -1) { //Ist das Mädchen noch nicht in einem Zimmer, sonst hätte es nämlich schon alle dort aufnehmen lassen, die es mag
                for (String bewundert : moeglicher_bewunderer.freunde) {
                    if (bewundert.equals(girl)) { //Wird dieses Mädchen bewundert
                        freunde_und_bewunderer.add((String) e.getKey()); //Zu Freunden und Bewunderern hinzufügen
                    }
                }
            }
        }
        for (String friend : freunde_und_bewunderer) { //Jetzt muss geprüft werden, ob besagte Freunde und Bewunderer aufgenommen werden können
            if (zimmer.zimmergegner.get(friend) != null) { //Wenn eine dieser Personen als Gegner bekannt ist, sind nicht alle Wünsche erfüllbar, und das Programm stoppt
                System.out.println("Freundeswunsch nicht erfüllbar : " + friend + " von " + girl + ", da " + friend + " ein Gegner des Zimmers ist");
                System.exit(0);
                return;
            } else if (maedchen.get(friend).zimmer == -1) { //Wenn das Mädchen noch nicht verplant ist, muss es neu geprüft werden
                pruefeMaedchen(friend); //Das Mädchen prüfen
            }
        }
    }

    public static void main(String[] args) throws IOException {
        maedchen = new HashMap(); //HashMap mit Mädchen initialisieren
        zimmer = new Zimmer((byte) 1); //Aktuelles Zimmer initialisieren
        ArrayList<Zimmer> gewuenschte_zimmerbelegung = new ArrayList(); //Liste von Zimmern(erwünschte Zimmerbelegung)
        String[] zettel = leseDatei(dateiFrage("Wo befindet sich die Aufgabe")).split("\n\n"); //Liste aller Zettel. split("\n\n") teilt die Zeichenkette bei zwei Zeilenumbrüchen
        boolean einzelzimmerAufloesen = jaNeinFrage("Hat es für sie Priorität, Einzelzimmer zu vermeiden");
        boolean zimmerAufloesen = jaNeinFrage("Soll versucht werden, möglichst wenige Zimmer zu erstellen");
        for (String z : zettel) { //Alle Zettel durchgehen
            String[] infos = z.split("\n"); //Informationen des jeweiligen Mädchens
            String name = infos[0];
            String[] friends, enemies;
            friends = enemies = new String[]{};
            if (infos[1].charAt(0) == '+') { //Entspricht das erste Zeichen der zweiten Zeile des Zettels einem Plus, werden zuerst die Freunde, dann die Feinde aufgeführt
                if (infos[1].length() > 2) {
                    friends = infos[1].substring(2).split(" ");
                }
                if (infos[2].length() > 2) {
                    enemies = infos[2].substring(2).split(" ");
                }
            } else { //Sollte es nicht so sein, werden zuerst die Feinde, dann die Freunde aufgeführt
                if (infos[2].length() > 2) {
                    friends = infos[2].substring(2).split(" ");
                }
                if (infos[1].length() > 2) {
                    enemies = infos[1].substring(2).split(" ");
                }
            }
            maedchen.put(name, new Maedchen(friends, enemies));
        }
        for (Entry e : maedchen.entrySet()) {
            Maedchen mz = (Maedchen) e.getValue();
            if (mz.zimmer == -1) { //Sollte das Mädchen noch nicht verplant sein
                pruefeMaedchen((String) e.getKey()); //So wird es in ein neues Zimmer gepackt
                gewuenschte_zimmerbelegung.add(zimmer); //Das Zimmer wird zur Zimmerbelegung hinzugefügt
                zimmer = new Zimmer((byte) (zimmer.id + 1)); //Für's nächste Zimmer geht die Zimmernummer um 1 hoch
            }
        }
        besteZimmerbelegung = new ArrayList();
        besteZimmerbelegung.addAll(gewuenschte_zimmerbelegung);
        if (einzelzimmerAufloesen) {
            zimmerAufloesen(besteZimmerbelegung, true); //Falls gewünscht, wird die Zimmerbelegung optimiert indem zuerst Einzelzimmer aufgelöst werden.
        }
        if (zimmerAufloesen) {
            zimmerAufloesen(besteZimmerbelegung, false); //Falls gewünscht, wird die Zimmerbelegung optimiert indem möglichst wenige Zimmer entstehen sollen.
        }
        for (Zimmer z : besteZimmerbelegung) {
            System.out.println(z); //Ausgabe des Zimmers, hierbei werden die Zimmer automatisch mithilfe des Zählers durchnummeriert
        }
    }
}
