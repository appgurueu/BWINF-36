/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wintervorrat;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author lars
 */
public class Wald { //Der Rechteckwald

    public static final short DAY = 60 * 12; //Ein Tag ist 12 Stunden(60 Minuten) lang
    public Adler[] adler; //Liste der Adler
    public Feld[][] felder; //Tabelle mit Feldern
    public String name; //Speichername für Dateien
    public BufferedImage hintergrund; //Das Feld : grün - total sicher rot - nicht total sicher
    public String svg; //Als Vektorgrafik : Header und das Feld als Vektorgrafik : grün - total sicher rot - nicht total sicher
    public boolean svg_erstellen, jpg_erstellen; //Sollen Vektorgrafiken und Bilder als Ausgabe erzeugt werden ?
    public static final Punkt[] ADLER_SVG = new Punkt[]{new Punkt(0, 0), new Punkt(40, 20), new Punkt(0, 40), new Punkt(10, 20)}; //Ein Adler(Pfeil) als Punktmenge für die SVG-Darstellung

    public Wald(String name, int x, int y, Adler[] adler, boolean erstelle_svg, boolean erstelle_jpg) {
        this.svg_erstellen = erstelle_svg;
        this.jpg_erstellen = erstelle_jpg;
        if (erstelle_svg) { //Falls SVGs erzeugt werden sollen
            this.svg = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n" //SVG Header
                    + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20010904//EN\"\n"
                    + "\"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">"
                    + "<svg xmlns=\"http://www.w3.org/2000/svg\"\n"
                    + "    xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n"
                    + "    version=\"1.1\" baseProfile=\"full\"\n"
                    + String.format("    width=\"%dpx\" height=\"%dpx\"\n", x * 40, y * 40) //Breite, Höhe
                    + String.format("    viewBox=\"0 0 %d %d\">\n", x * 40, y * 40); //Angezeigter Bereich
            svg += String.format("    <rect x=\"0\" y=\"0\" width=\"%d\" height=\"%d\" fill=\"rgb(0,255,0)\" />\n", x * 40, y * 40); //Hintergrund grün färben
        }
        Graphics2D hg = null;
        if (erstelle_jpg) { //Falls JPGs erzeugt werden sollen
            hintergrund = new BufferedImage(x * 40, y * 40, BufferedImage.TYPE_INT_RGB); //Hintergrundbild erstellen
            hg = hintergrund.createGraphics();
            //Hintergrund grün füllen
            hg.setColor(Color.GREEN);
            hg.fillRect(0, 0, hintergrund.getWidth(), hintergrund.getHeight());
        }
        this.name = name; //Name, um die Dateien zu speichern
        this.felder = new Feld[x][y]; //Speichert den alle Felder
        for (int xw = 0; xw < x; xw++) {
            for (int yw = 0; yw < y; yw++) {
                felder[xw][yw] = new Feld(true); //Wir gehen erstmal davon aus, dass jedes Feld total sicher ist
            }
        }
        this.adler = adler; //Adler werden vorgegeben
        for (Adler a : adler) { //Adler durchgehen, um nicht vollkommen sichere Felder zu ermitteln
            if (a.flugrichtung.x == 0) { //Sollte er in y-Richtung fliegen(Norden-Süden)
                for (int yw = 0; yw < y; yw++) { //So sind alle auf seiner Linie liegenden Felder...
                    int xw = a.position.x;
                    felder[xw][yw].totalSicher = false; //...nicht total sicher
                    if (erstelle_jpg) { //Und werden dementsprechend rot markiert, falls erwünscht, hier im Bild...
                        hg.setColor(Color.RED);
                        hg.fillRect(xw * 40, yw * 40, 40, 40);
                    }
                    if (erstelle_svg) { //...und hier in der Vektorgrafik, falls erwünscht
                        svg += String.format("    <rect x=\"%d\" y=\"%d\" width=\"40\" height=\"40\" fill=\"rgb(255,0,0)\" />\n", xw * 40, yw * 40);
                    }
                }
            } else { //Sollte er in x-Richtung fliegen(Osten-Westen)
                for (int xw = 0; xw < x; xw++) { //So sind alle auf seiner Linie liegenden Felder...
                    int yw = a.position.y;
                    felder[xw][yw].totalSicher = false; //...nicht total sicher
                    if (erstelle_jpg) { //Und werden dementsprechend rot markiert, falls erwünscht, hier im Bild...
                        hg.setColor(Color.RED);
                        hg.fillRect(xw * 40, yw * 40, 40, 40);
                    }
                    if (erstelle_svg) { //...und hier in der Vektorgrafik, falls erwünscht
                        svg += String.format("    <rect x=\"%d\" y=\"%d\" width=\"40\" height=\"40\" fill=\"rgb(255,0,0)\" />\n", xw * 40, yw * 40);
                    }
                }
            }
        }
    }

    public void simulieren() throws IOException {
        for (int x = 0; x < felder.length; x++) {
            for (int y = 0; y < felder[0].length; y++) {
                felder[x][y].anfangSichererZeitraum = 1;
            }
        }
        for (short m = 1; m <= DAY; m++) { //Alle Minuten durchsimulieren
            Graphics2D grafik = null;
            BufferedImage img = null;
            if (jpg_erstellen) { //Falls Erstellung von Bildern gewünscht ist, Kopie des Hintergrunds erstellen
                img = new BufferedImage(felder.length * 40, felder[0].length * 40, BufferedImage.TYPE_INT_RGB);
                grafik = img.createGraphics();
                grafik.drawImage(hintergrund, 0, 0, null);
            }
            String svg_to_save = "";
            if (svg_erstellen) { //Falls ein SVG gewünscht ist, Kopie des Headers + des Hintergrundes als Vektorgrafik erstellen
                svg_to_save = svg;
            }
            for (Adler a : adler) { //Alle Adler durchgehen
                if (m >= a.startminute) { //Davon sind nur die, die schon gestartet sind, relevant
                    if (svg_erstellen || jpg_erstellen) { //Falls eine grafische Darstellung gefordert ist...
                        int xw = a.position.x * 40 + 5;
                        int yw = a.position.y * 40 + 5;
                        if (jpg_erstellen) {
                            //Adler als weiße Kreise anzeigen, Radius 30
                            grafik.setColor(Color.WHITE);
                            grafik.fillOval(xw, yw, 30, 30);
                        }
                        if (svg_erstellen) {
                            //Adler als festgelegten Pfeil anzeigen
                            //Adler richtig drehen
                            String t = "rotate(";
                            if (a.flugrichtung.x != 0) {
                                if (a.flugrichtung.x < 0) {
                                    t += "180";
                                } else {
                                    t += "0";
                                }
                            } else if (a.flugrichtung.y < 0) {
                                t += "270";
                            } else {
                                t += "90";
                            }
                            t += " ";
                            t += Integer.toString(xw + 15); //Drehungsmittelpunkt X
                            t += " ";
                            t += Integer.toString(yw + 15); //Drehungsmittelpunkt Y
                            t += ")";
                            //Koordinaten des Pfeils versetzen
                            String ps = "";
                            for (int p = 0; p < ADLER_SVG.length; p++) {
                                ps += Integer.toString(xw - 5 + ADLER_SVG[p].x);
                                ps += ",";
                                ps += Integer.toString(yw - 5 + ADLER_SVG[p].y);
                                if (p != ADLER_SVG.length - 1) {
                                    ps += " ";
                                }
                            }
                            svg_to_save += "    <polygon points=\"" + ps + "\" style=\"fill:rgb(255,255,255); fill-opacity:0.75; stroke:rgb(255,165,0); stroke-width:0.5px\" transform=\"" + t + "\"/>\n"; //Adler(Pfeil) versetzt zum SVG, mit gegebener Richtung(Drehung), hinzufügen, ebnfalls in weiß, nun aber auch teilweise durchsichtig 
                        }
                    }
                    if (a.flugrichtung.x != 0) { //Sollte der Adler in x-Richtung fliegen(Westen-Osten)
                        if (a.position.x + a.flugrichtung.x < 0 || a.position.x + a.flugrichtung.x > felder.length - 1) { //Würde er über den Rechteckwald hinausfliegen...
                            a.flugrichtung.x = -a.flugrichtung.x; //...fliegt er wieder zurück, ab jetzt also in umgekehrter Richtung
                        }
                    } else if (a.position.y + a.flugrichtung.y < 0 || a.position.y + a.flugrichtung.y > felder[0].length - 1) { //Sollte der Adler in y-Richtung fliegen(Norden-Süden), und würde er über den Rechteckwald hinausfliegen...
                        a.flugrichtung.y = -a.flugrichtung.y; //...fliegt er wieder zurück, ab jetzt also in umgekehrter Richtung
                    }
                    Feld f = felder[a.position.x][a.position.y]; //Das Feld, über dem er sich jetzt befindet
                    if (m - f.anfangSichererZeitraum >= 30) { //Sollte dieses mindestens satte 30 Minuten sicher gewesen sein : 
                        felder[a.position.x][a.position.y].add(new Zeitraum(f.anfangSichererZeitraum, m)); //Zeitraum zu sicheren Zeiträumen des Feldes hinzufügen
                    }
                    felder[a.position.x][a.position.y].anfangSichererZeitraum = (short) (m + 1); //Nach Überflug ist das Feld wieder sicher
                    a.position = a.position.add(a.flugrichtung); //Weiterfliegen !
                }
            }
            if (svg_erstellen || jpg_erstellen) { //Für eine eventuell gewünschte Darstellung : 
                for (int x = 0; x < felder.length; x++) {
                    for (int y = 0; y < felder[0].length; y++) {
                        if (!felder[x][y].totalSicher) { //Ist das Feld nicht total sicher
                            if (m - felder[x][y].anfangSichererZeitraum >= 30) { //Sichere Felder einfärben, solange sie einen sicheren Zeitraum durchlaufen
                                int xw = x * 40 + 5;
                                int yw = y * 40 + 5;
                                if (jpg_erstellen) {
                                    //Im Bild als blaue Quadrate darstellen
                                    grafik.setColor(Color.BLUE);
                                    grafik.fillRect(xw, yw, 30, 30);
                                }
                                if (svg_erstellen) {
                                    //Genauso im SVG
                                    svg_to_save += String.format("    <rect x=\"%d\" y=\"%d\" width=\"30\" height=\"30\" fill=\"rgb(0,0,255)\" />\n", xw, yw);
                                }
                            }
                        }
                    }
                }
            }
            //Simultaionsminute in die Darstellung/en einfügen und diese dann speichern
            if (jpg_erstellen) {
                //Simulationsminute einfügen, schwarz, Schriftgröße 24
                grafik.setColor(Color.BLACK);
                grafik.setFont(new Font("sans", Font.PLAIN, 24));
                grafik.drawString(Short.toString(m), 0, 24);
                File ausgabe = new File(name + Short.toString(m) + ".jpg"); //Ausgabe : name+minute+".jpg"
                ImageIO.write(img, "JPG", ausgabe); //Bild als JPG speichern
            }
            if (svg_erstellen) {
                //Simulationsminute einfügen, schwarz, Schriftgröße 24
                svg_to_save += String.format("<text x=\"0\" y=\"24\" fill=\"black\" style=\"font-size:24px\">%s</text>", Short.toString(m));
                svg_to_save += "\n</svg>";
                File svg_ausgabe = new File(name + Short.toString(m) + ".svg");
                if (svg_ausgabe.exists()) { //Existiert die Datei schon
                    svg_ausgabe.delete(); //So wollen wir diese komplett löschen
                }
                svg_ausgabe.createNewFile(); //Wir erzeugen die Ausgabedatei
                //SVG speichern
                BufferedWriter w = new BufferedWriter(new FileWriter(svg_ausgabe));
                w.write(svg_to_save);
                w.close();
            }
            System.gc(); //Speicher freigeben
        }
        for (int x = 0; x < felder.length; x++) {
            for (int y = 0; y < felder[0].length; y++) {
                if (!felder[x][y].totalSicher) { //Alle nicht total sicheren Felder
                    if (DAY - felder[x][y].anfangSichererZeitraum >= 30) { //"Alles muss raus"-Felder, die vor Ende des Tages nur einmal überflogen wurden, was jetzt schon mehr als eine 1/2 h her ist
                        felder[x][y].add(new Zeitraum(felder[x][y].anfangSichererZeitraum, DAY)); //Zeitraum zu sicheren Zeiträumen des Feldes hinzufügen
                    }
                    //Alle sicheren Zeiträume des Feldes ausgeben
                    if (!felder[x][y].isEmpty()) { //Gibt es bei diesem Feld sichere Zeiträume
                        System.out.print("X=" + Integer.toString(x) + " Y=" + Integer.toString(y));
                        for (Zeitraum z : felder[x][y]) {
                            System.out.print(" SM=" + Short.toString(z.anfangSichererZeitraum) + " EM=" + Short.toString(z.endeSichererZeitraum));
                        }
                        System.out.println();
                    }
                }
            }
        }
    }

    @Override
    public String toString() { //Sicherheiten der Felder angeben
        String s = "";
        for (int y = felder[0].length - 1; y > -1; y--) { //Y-Achse umdrehen
            for (int x = 0; x < felder.length; x++) {
                if (felder[x][y].totalSicher) { //Ist das Feld total sicher, 2 ausgeben an der Stelle
                    s += "2";
                } else if (felder[x][y].isEmpty()) { //Gibt es keine sicheren Zeiträume für das Feld
                    s += "0"; //0 ausgeben
                } else { //Ansonsten gibt es Sichere
                    s += "1"; //1 ausgeben
                }
                if (x != felder.length - 1) {
                    s += " ";
                }
            }
            s += "\n";
        }
        return s; //Textdarstellung zurückgeben
    }
}
