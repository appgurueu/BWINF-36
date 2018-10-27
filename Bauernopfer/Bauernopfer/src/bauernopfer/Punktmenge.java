/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bauernopfer;

import java.util.ArrayList;

/**
 *
 * @author lars
 */
public class Punktmenge { //Speichert eine Punktmenge auf einem Brett
    public boolean[][] raster; //Punktmenge als Raster/Spielbrett repräsentiert
    public ArrayList<Punkt> punkte; //Punktmenge als Liste repräsentiert
    public Punktmenge() { //Konstruktor - Erzeugt eine leere Punktmenge
        this.punkte=new ArrayList();
        this.raster=new boolean[8][8];
    }
    public Punktmenge(boolean[][] raster) { //Konstruktor - Erzeugt eine Punktmenge aus einem Spielbrett, wo Punkte als "WAHR" eingetragen sind
        this.punkte=new ArrayList();
        this.raster=new boolean[8][8];
        for (byte x=0; x < 8; x++) {
            for (byte y=0; y < 8; y++) {
                if (raster[x][y]) { //Falls sich dort ein Punkt befindet,
                    //Punkt übernehmen
                    this.raster[x][y]=true;
                    punkte.add(new Punkt(x,y));
                }
            }
        }
    }
    public Punktmenge(byte[][] raster) { //Konstruktor - Erzeugt eine Punktmenge aus einem Spielbrett, wo Punkte als != 0 eingetragen sind
        this.punkte=new ArrayList();
        this.raster=new boolean[8][8];
        for (byte x=0; x < 8; x++) {
            for (byte y=0; y < 8; y++) {
                if (raster[x][y] != 0) { //Falls sich dort ein Punkt befindet,
                    //Punkt übernehmen
                    this.raster[x][y]=true;
                    punkte.add(new Punkt(x,y));
                }
            }
        }
    }
    public Punktmenge(ArrayList<Punkt> punkte ) { //Konstruktor - Erzeugt eine Punktmenge aus einer Liste
        this.punkte=new ArrayList();
        this.raster=new boolean[8][8];
        for (Punkt p:punkte) {
            //Jeden Punkt übernehmen
            this.raster[p.x][p.y]=true;
            this.punkte.add(p);
        }
    }
    public Punktmenge oder(Punktmenge feld) { //Logischer "oder" Operator
        boolean[][] ergebnis=new boolean[8][8];
        for (byte x=0; x < 8; x++) {
            for (byte y=0; y < 8; y++) {
                if (this.raster[x][y] || feld.raster[x][y]) { //Ist der Punkt in mindestens einer der beiden Punktmengen vorhanden
                    ergebnis[x][y]=true; //So wird er ins Ergebnis übernommen
                }
            }
        }
        return new Punktmenge(ergebnis);
    }
    public Punktmenge nund(Punktmenge feld) { //Logischer (1), aber nicht (2) Operator, also werden die Überschneidungen beider Punktmengen entfernt
        boolean[][] ergebnis=new boolean[8][8];
        for (byte x=0; x < 8; x++) {
            for (byte y=0; y < 8; y++) {
                if (this.raster[x][y] && !feld.raster[x][y]) {
                    ergebnis[x][y]=true; //Wenn sich der Punkt nicht mit dem anderen überschneidet, dann kann er ins Ergebnis übernommen werden
                }
            }
        }
        return new Punktmenge(ergebnis);
    }
    @Override
    public String toString() { //Benutzerfreundliche Ausgabe des Feldes, "x" für verzeichnete Punkt, "o" für nicht Verzeichnete
        String s="";
        for (byte x=0; x < 8; x++) {
            for (byte y=0; y < 8; y++) {
                if (raster[x][y]) {
                    s+="x";
                }
                else {
                    s+="o";
                }
            }
            s+="\n";
        }
        return s;
    }
}
