package dreieckezaehlen;

//Dateien & Ordner
import java.io.BufferedWriter; //Dateien schreiben
import java.io.File;  //Dateien & Ordner
import java.io.FileNotFoundException; //Datei-nicht-gefunden-Fehler
import java.io.FileReader; //Dateien lesen
import java.io.FileWriter; //Dateien schreiben
import java.io.IOException; //Eingabe/Ausgabe-Fehler
import java.util.Scanner; //Benutzereingabe

public class DreieckeZaehlen {

    public static final Scanner EINGABE=new Scanner(System.in); //Benutzereingabe
    public static Strecke[] strecken; //Alle Strecken

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

    public static Schnittpunkt holeSchnittpunkt(Strecke e, Strecke f) { //Gibt den Schnittpunkt zweier Strecken zurück
        Schnittpunkt c = e.schnitte.get(f); //Ist der Schnittpunkt gespeichert
        if (c == null) { //Falls leider nicht, müssen wir ihn ermitteln
            c = f.schnittPunkt(e); //Ermittelt den Schnittpunkt
            //Den Schnitt einspeichern
            e.schnitte.put(f, c);
            f.schnitte.put(e, c);
        }
        return c; //Schnittpunkt zurückgeben
    }

    public static void main(String[] args) throws IOException {
        String[] aufgabe = leseDatei(dateiFrage("Wo befindet sich die Aufgabe")).split("\n");
        String ausgabe = textFrage("Wo soll das Ausgabebild gespeichert werden(Pfad zu .svg)");
        int d = 0; //Noch wurden 0 Dreiecke gezählt
        strecken = new Strecke[aufgabe.length - 1]; //Es gibt so viele Strecken, wie die Aufgabe Zeilen hat abzüglich 1, wegen der obersten Zeile
        for (int i = 1; i < aufgabe.length; i++) { //Alle Strecken von Textform in Streckenform umwandeln
            String s = aufgabe[i];
            String[] p = s.split(" ");
            Punkt a = new Punkt(new Dezimalzahl(Double.parseDouble(p[0])), new Dezimalzahl(Double.parseDouble(p[1]))); //Punkt a lesen
            Punkt b = new Punkt(new Dezimalzahl(Double.parseDouble(p[2])), new Dezimalzahl(Double.parseDouble(p[3]))); //Punkt b lesen
            strecken[i - 1] = new Strecke(a, b); //Strecke speichern
        }
        double minx = Double.MAX_VALUE; //Kleinster X-Wert von allen Strecken
        double miny = Double.MAX_VALUE; //Kleinster Y-Wert von allen Strecken
        double maxx = Double.MIN_VALUE; //Größter X-Wert von allen Strecken
        double maxy = Double.MIN_VALUE; //Größter Y-Wert von allen Strecken
        for (Strecke s : strecken) { //Ermittelt die darüber aufgeführten Werte
            double ax = s.a.x.wert();
            double ay = s.a.y.wert();
            double bx = s.b.x.wert();
            double by = s.b.y.wert();
            if (ax > maxx) {
                maxx = ax;
            }
            if (ax < minx) {
                minx = ax;
            }
            if (ay > maxy) {
                maxy = ay;
            }
            if (ay < miny) {
                miny = ay;
            }
            if (bx > maxx) {
                maxx = bx;
            }
            if (bx < minx) {
                minx = bx;
            }
            if (by > maxy) {
                maxy = by;
            }
            if (by < miny) {
                miny = by;
            }
        }
        double dimx = Math.abs(maxx - minx); //Breite des Bildes
        double dimy = Math.abs(maxy - miny); //Höhe des Bildes
        //SVG Header
        String svg = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n"
                + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20010904//EN\"\n"
                + "\"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">"
                + "<svg xmlns=\"http://www.w3.org/2000/svg\"\n"
                + "    xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n"
                + "    version=\"1.1\" baseProfile=\"full\"\n"
                + String.format("    width=\"%fpx\" height=\"%fpx\"\n", dimx, dimy)
                + String.format("    viewBox=\"%f %f %f %f\">\n", minx, miny, maxx, maxy);
        String lines = ""; //Linien als SVG-Elemente
        String triangles = ""; //Dreiecke als SVG-Elemente
        for (int i = 0; i < strecken.length; i++) { //Alle Strecken durchgehen
            Strecke a = strecken[i];
            //Strecke in der Vektorgrafik darstellen
            double[] xl = new double[]{a.a.x.wert(), a.b.x.wert()}; //Punkte a und b der Strecke, X-Koordinaten
            double[] yl = new double[]{dimy - a.a.y.wert(), dimy - a.b.y.wert()}; //Punkte a und b der Strecke, Y-Koordinaten, gespiegelt
            //Koordinaten in SVG-gerechten Text umwandeln
            String ls = ""; //Koordinaten als Text
            for (int l = 0; l < 2; l++) {
                ls += Double.toString(xl[l]);
                ls += ","; //Immer ein Kommata zwischen ein Koordinatenpaar
                ls += Double.toString(yl[l]);
                if (l != 1) {
                    ls += " ";
                }
            }
            lines += "\n    <polyline points=\"" + ls + "\" style=\"fill:rgb(0,0,0); stroke:rgb(0,0,0); stroke-width:2px\" />"; //Linie im SVG speichern
            for (int j = i + 1; j < strecken.length; j++) { //Alle Strecken durchgehen, von i ausgehend, da i Strecken alle schon "benutzt" worden sind
                Strecke b = strecken[j];
                Schnittpunkt A = holeSchnittpunkt(a, b); //Schnittpunkt zwischen a und b ermitteln...
                if (A.schnitt) { //...schneiden die sich überhaupt ?
                    for (int k = j + 1; k < strecken.length; k++) { //Falls ja, kann Ausschau nach einer Strecke c, die das Dreieck vervollständigt, gehalten werden, von j ausgehend, da für dieses Element j Strecken alle schon "benutzt" worden sind, Die benutzten i Strecken sind schon enthalten
                        Strecke c = strecken[k];
                        Schnittpunkt B = holeSchnittpunkt(c, b); //Schnittpunkt zwischen b und c ermitteln...
                        if (B.schnitt && !A.e(B)) { //...schneiden die sich überhaupt, und : sind A und B verschiedene Punkte ?
                            Schnittpunkt C = holeSchnittpunkt(c, a); //Schnittpunkt C-A : Nötig, damit das Dreieck geschlossen ist
                            if (C.schnitt && !A.e(C) && !B.e(C)) { //...ist es das überhaupt, und : sind A und C bzw. B und C verschiedene Punkte ?
                                //Dreieck im SVG speichern
                                double[] x = new double[]{A.x.wert(), C.x.wert(), B.x.wert()}; //Eckpunkte des Dreiecks, X-Koordinaten
                                double[] y = new double[]{dimy - A.y.wert(), dimy - C.y.wert(), dimy - B.y.wert()}; //Eckpunkte des Dreiecks, Y-Koordinaten, gespiegelt
                                //Koordinaten in SVG-gerechten Text umwandeln
                                String ps = ""; //Koordinaten als Text
                                for (int l = 0; l < 3; l++) {
                                    ps += Double.toString(x[l]);
                                    ps += ","; //Immer ein Kommata zwischen ein Koordinatenpaar
                                    ps += Double.toString(y[l]);
                                    if (l != 2) {
                                        ps += " ";
                                    }
                                }
                                String color = String.format("rgb(%s,%s,%s)", (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)); //Zufällige Farbe
                                triangles += "\n    <polygon points=\"" + ps + "\" style=\"fill:" + color + "; fill-opacity:0.5; stroke:rgb(255,165,0); stroke-width:0.5px\" />"; //Dreieck im SVG speichern
                                System.out.println("Neues Dreieck : A=" + A.toString() + ", B=" + B.toString() + ", C=" + C.toString()); //Dreieck(dessen Eckpunkte) ausgeben
                                d++; //Ein weiteres Dreieck !
                            }
                        }
                    }
                }
            }
        }
        svg += lines; //Linien im SVG speichern
        svg += triangles; //Dreiecke im SVG speichern
        svg += "\n</svg>";
        //Neue Datei erzeugen
        File f = new File(ausgabe);
        f.createNewFile();
        //SVG speichern
        BufferedWriter w = new BufferedWriter(new FileWriter(f));
        w.write(svg);
        w.close();
        System.out.println("Es konnten " + Integer.toString(d) + " Dreiecke gefunden werden."); //Anzahl der gefundenen Dreiecke ausgeben
    }
}
