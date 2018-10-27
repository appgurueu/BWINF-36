/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wintervorrat;

/**
 *
 * @author lars
 */
public class Adler { //Speichert alles, was wir über einen Adler im Rechteckwald wissen müssen

    public short startminute; //Startminute
    public Punkt position; //Startposition
    public Punkt flugrichtung; //Flugrichtung

    public Adler(short startminute, Punkt position, Punkt flugrichtung) { //Konstruktor - Neuen Adler mit gegebenen Werten erzeugen
        this.startminute = startminute;
        this.position = position;
        this.flugrichtung = flugrichtung;
    }
}
