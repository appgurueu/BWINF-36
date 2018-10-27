/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bauernopfer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author lars
 */
public class Brett extends JPanel {
    public static final BufferedImage SCHACHBRETT;
    public static final BufferedImage BAUER_BILD=ladeBild("res/bauer.png", Color.WHITE); //Bild eines Bauern
    public static final BufferedImage TURM_BILD; //Bild des Turmes/der Dame
    static {
        //Bild der Dame/des Turmes laden
        if (!Bauernopfer.dame) {
            TURM_BILD=ladeBild("res/turm.png", Color.BLACK); //Bild des Turmes
        }
        else {
            TURM_BILD=ladeBild("res/dame.png", Color.BLACK); //Bild der Dame
        }
        //Schachbrett zeichnen
        SCHACHBRETT=new BufferedImage(400,400, BufferedImage.TYPE_INT_RGB);
        Graphics2D g=SCHACHBRETT.createGraphics();
        //Hintergrund hellbraun füllen
        g.setColor(new Color(139, 69, 19));
        g.fillRect(0,0,400,400);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                //Jedes 2. Feld dunkelbraun füllen
                if ((i + j) % 2 == 0) {
                    g.setColor(new Color(69, 35, 10));
                    g.fillRect(i * 50, j * 50, 50, 50);
                }
            }
        }
    }
    public Punktmenge bauern; //Speichert die Positionen aller Bauern
    public Punkt turm; //Speichert die Position des Turms/der Dame

    public void platziereTurm(Punkt position) { //Platziert den Turm an gegebener Position
        turm=position;
    }

    public void platziereBauern(Punktmenge positionen) { //Platziert alle Bauern an gegebenen Positionen
        bauern=positionen;
    }

    public void bewegeTurm(Punkt position) { //Bewegt den Turm zu einer gegebenen Position
        turm=position;
    }

    public void bewegeBauern(Punkt bauer, Punkt position) { //Bewegt den Bauern am Punkt "bauer" zu dem Punkt "position"
        bauern.raster[bauer.x][bauer.y]=false; //Den Bauern an alter Stelle löschen
        bauern.raster[position.x][position.y]=true; //Und an neuer platzieren
        if (position.x == turm.x && position.y == turm.y) { //Konnte die Dame/der Turm geschlagen werden
            System.out.println("Die Bauern haben gewonnen ! ");
            System.exit(0); //Programm beenden
        }
        bauern=new Punktmenge(bauern.raster); //Bauern aktualisieren
    }

    public static BufferedImage ladeBild(String pfad, Color farbe) {
        try {
            return ImageIO.read(new File(pfad)); //Versuche, das Bild zu laden
        } catch (Exception fehler) {
            //Ansonsten wird ein farbiger Kreis als Ersatz benutzt
            BufferedImage ziel = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
            Graphics2D zielg = ziel.createGraphics();
            zielg.setBackground(new Color(0, 0, 0, 0));
            zielg.setColor(farbe);
            zielg.fillOval(5, 5, 40, 40);
            return ziel;
        }
    }

    public Brett() { //Konstruktor - noch nichts vorgegeben
        bauern=new Punktmenge();
        turm=null;
    }

    @Override
    public void paintComponent(Graphics g) { //Brett "zeichnen"
        g.drawImage(SCHACHBRETT,0,0,null);
        if (Bauernopfer.bauern_dran) {
            byte[][] erreichbar=BauernAL.bestimmeFelder(bauern,Bauernopfer.zuege); //Alle Felder, die von den Bauern erreichbar sind
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (erreichbar[i][j] > 0) { //Wenn das Feld erreichbar ist
                        g.setColor(new Color(Math.min(255,30*erreichbar[i][j]),Math.max(0,255-(85*erreichbar[i][j])),255-Math.min(255,30*erreichbar[i][j]))); //Jenachdem, wie viele Züge die Bauern minimal zum Erreichen eines Feldes benötigen, färben wir dies ein
                        g.fillRect(i * 50+5, j * 50+5, 40, 40);
                    }
                }
            }
        } else {
            byte[][] erreichbar=TurmAL.gewichteteFelder(bauern, turm); //Alle Felder, die vom Turm erreichbar sind
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (erreichbar[i][j] > 0) {
                        g.setColor(new Color(Math.min(255,30*erreichbar[i][j]),Math.max(0,255-(85*erreichbar[i][j])),255-Math.min(255,30*erreichbar[i][j]))); //Jenachdem, wie viele Züge der Turm/die Dame minimal zum Erreichen eines Feldes benötigen, färben wir dies ein
                        g.fillRect(i * 50+5, j * 50+5, 40, 40);
                    }
                }
            }
            for (Punkt p : TurmAL.smarteFelder(bauern, turm).punkte) { //Felder, welche von der TurmAL als Möglichkeiten, wo der Turm/die Dame hingehen sollte, betrachtet werden, 
                //Werden rot eingefärbt
                g.setColor(Color.RED);
                g.fillRect(p.x * 50 + 10, p.y * 50 + 10, 30, 30);
            }
        }
        if (turm != null) { //Wenn der Turm schon platziert wurde, 
            g.drawImage(TURM_BILD, turm.x * 50, turm.y * 50, this); //Dann können wir ihn auch anzeigen lassen
        }
        for (Punkt bauer : bauern.punkte) {
            g.drawImage(BAUER_BILD, bauer.x * 50, bauer.y * 50, this); //Alle schon platzierten Bauern können wir auch anzeigen lassen
        }
    }
}
