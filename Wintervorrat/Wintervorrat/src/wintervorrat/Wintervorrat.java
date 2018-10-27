/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wintervorrat;

//Dateien
import java.io.File;
import java.io.FileNotFoundException; //Datei-nicht-gefunden-Fehler
import java.io.FileReader; //Dateien lesen
import java.io.IOException; //Eingabe-Ausgabe-Fehler
import java.util.Scanner; //Benutzereingabe

/**
 *
 * @author lars
 */

public class Wintervorrat {

    public static final Scanner EINGABE=new Scanner(System.in); //Benutzereingabe
    public static final int DAY = 60 * 12; //Länge eines Tages in Minuten
    
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
    
    public static String textFrage(String frage) { //Fragt nach einer Zeichenkette
        System.out.println(frage + "(Zeichenkette) ? ");
        String s = EINGABE.nextLine();
        return s;
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

    public static Punkt parseDir(String dir) { //Wandelt eine Himmelsrichtung in einen "Vektor"(Punkt) um
        switch (dir.charAt(0)) {
            case 'N': //Norden : nach oben
                return new Punkt(0, 1);
            case 'O': //Osten : nach rechts
                return new Punkt(1, 0);
            case 'S': //Süden : nach unten
                return new Punkt(0, -1);
            default: //Westen : nach links
                return new Punkt(-1, 0);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        String file = leseDatei(dateiFrage("Wo befindet sich die Aufgabe"));
        boolean bilder = jaNeinFrage("Sollen Ausgabebilder gespeichert werden");
        boolean svg=false;
        boolean jpg=false;
        String ausgabe="?";
        if (bilder) {
            ausgabe = textFrage("Wo sollen die Ausgabebilder gespeichert werden(ohne Endung & Nummer)");
            svg = jaNeinFrage("Als SVG");
            jpg = jaNeinFrage("Als JPG");
        }
        String[] zeilen = file.split("\n"); //Zeilen der Datei, split("\n") trennt den String bei Zeilenumbrüchen
        String[] dim = zeilen[0].split(" "); //Dimensionen des Rechteckwalds, split(" ") trennt den String bei Leerzeichen
        Adler[] adler = new Adler[Integer.parseInt(zeilen[1])]; //Adler-Array
        for (int i = 2; i < zeilen.length; i++) {
            String[] info = zeilen[i].split(" "); //Werte eines Adlers, split(" ") trennt den String bei Leerzeichen
            Adler a = new Adler(Short.parseShort(info[2]), new Punkt(Integer.parseInt(info[0])-1, Integer.parseInt(info[1])-1), parseDir(info[3])); //Neuen Adler erzeugen
            adler[i - 2] = a; //Und speichern
        }
        Wald wald = new Wald(ausgabe,Integer.parseInt(dim[0]), Integer.parseInt(dim[1]), adler, svg, jpg); //Wald mit gegebener Breite und Höhe erzeugen, sowie gegebenem Speichernamen für die Bilder und ob svgs oder jpgs gespeichert werden sollen
        wald.simulieren(); //Wald simulieren
        System.out.println(wald); //Sicherheiten der Felder des Waldes ausgeben
    }

}
