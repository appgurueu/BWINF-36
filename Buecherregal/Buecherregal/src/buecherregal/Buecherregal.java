package buecherregal;

//Bibliotheken, um Dateien zu lesen
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
//Listen
import java.util.ArrayList;
//Benutzer-Eingabe
import java.util.Scanner;

/**
 *
 * @author lars
 */

public class Buecherregal {

    public static final Scanner EINGABE = new Scanner(System.in);

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

    public static ArrayList<Integer> sort(ArrayList<Integer> liste, ArrayList<Integer> sortiert) { //Sortiert eine Liste
        //Das kleinste Element und dessen Position(Index) bestimmen
        int kleinster_wert_index = 0;             
        int kleinster_wert = liste.get(0);         
        for (int i = 1; i < liste.size(); i++) {   
            int wert = liste.get(i);             
            if (wert < kleinster_wert) {         
                kleinster_wert_index = i;          
                kleinster_wert = wert;
            }
        }
        if (liste.size() > 1) { //Falls es noch zu sortierende Elemente gibt
            liste.remove(kleinster_wert_index); //Den ermittelten Wert von der Liste entfernen
            sortiert.add(kleinster_wert); //Zur sortierten Liste hinzufügen
            return sort(liste, sortiert); //Rufe Funktion noch einmal auf, diesesmal wurde der jetzt ermittelte kleinste Wert aber entfernt
        }
        return sortiert; //Wenn das letzte Element angehängt wurde, kann die sortierte Liste zurückgegeben werden
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        String aufgabe = leseDatei(dateiFrage("Wo befindet sich die Aufgabe")); //Inhalt der Aufgabendatei
        String[] zeilen = aufgabe.split("\n"); //Sämtliche Zahlen in der Aufgabedatei, welche ja untereinander geschrieben waren(deswegen trennen wir nach Zeilenumbrüchen)
        ArrayList<Integer> hoehen_liste = new ArrayList(); //Liste, welche die Höhen aller Bücher speichert
        int figuren = Integer.parseInt(zeilen[0]); //Anzahl der Dekofiguren
        for (int i = 2; i < zeilen.length; i++) { //Mit Zeile 3 fangen die Höhen der Bücher an
            hoehen_liste.add(Integer.parseInt(zeilen[i]));
        }
        hoehen_liste = sort(hoehen_liste, new ArrayList()); //Wir sortieren die Bücher der Höhe nach
        //Anmerkung : Es könnte auch eine Die Built-In Sortierfunktion von Java(Arrays.sort(<hoehen>)) auf ein Array angewendet werden
        //Da ich jedoch den Sortieralgorithmus für einen wichtigen Teil der Aufgabe halte, habe ich diesen selbst implementiert
        byte a = 0; //Aktueller Abschnitt a
        int min = hoehen_liste.get(0); //Kleinstes Buch des aktuellen Abschnittes
        ArrayList<Abschnitt> abschnitte = new ArrayList();
        abschnitte.add(new Abschnitt());
        abschnitte.get(0).add(hoehen_liste.get(0));
        for (int i = 1; i < hoehen_liste.size(); i++) {
            int wert = hoehen_liste.get(i);
            if (wert - min > 30) { //Wenn das jetztige Buch mehr als 30 mm höher ist als das kleinste dieses Abschnittes, so muss zwangsweise ein neuer Abschnitt beginnen
                min = wert; //Kleinstes Buch des neuen Abschnittes ist das jetztige Buch
                a++; //Neuer Abschnitt
                abschnitte.add(new Abschnitt()); //Neuen Abschnitt zu der Liste der Abschnitte hinzufügen
            }
            abschnitte.get(a).add(wert); //Dem Abschnitt mit Index a in der Liste von Abschnitten die Höhe des Buches mit Nummer i hinzufügen
        }
        if (abschnitte.size() < figuren) { //Haben wir noch nicht genug Abschnitte ?
            for (int i = 0; i < abschnitte.size(); i++) {
                if (abschnitte.size() == figuren) {
                    break;
                } else {
                    int buecher_im_abschnitt = abschnitte.get(i).size();
                    if (buecher_im_abschnitt > 1) {
                        Abschnitt ab = new Abschnitt();
                        ab.add(abschnitte.get(i).get(buecher_im_abschnitt - 1)); //Neuer Abschnitt aus dem letzten Buch dieses Abschnittes
                        abschnitte.get(i).remove(buecher_im_abschnitt - 1); //Letztes Buch aus altem Abschnitt entfernen
                        abschnitte.add(ab); //Neuen Abschnitt hinzufügen
                    }
                    if (i == abschnitte.size() - 1) {
                        i = 0; //Wieder von vorne anfangen, wenn immer noch nicht genügend Abschnitte vorhanden sein sollten
                    }
                }
            }
        }
        if (abschnitte.size() >= figuren) { //Gibt es genug Abschnitte ?
            System.out.println(Abschnitt.toString(abschnitte)); //Lösung ausgeben
        } else { //Ansonsten war diese Aufgabe offensichtlich unlösbar
            System.out.println("Diese Aufgabe lässt sich leider nicht lösen.");
        }
    }

}
