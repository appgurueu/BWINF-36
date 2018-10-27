/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autoscrabble;

/**
 *
 * @author lars
 */
public class Kennzeichen { //Speichert Kennzeichen

    public String kuerzel; //Städtekürzel
    public String wert; //Anhang
    public int zahl; //Nummer

    public Kennzeichen(String kuerzel, String wert, int zahl) { //Konstruktor, alle Variablen werden vorgegeben
        this.kuerzel = kuerzel;
        this.wert = wert;
        this.zahl = zahl;
    }

    @Override
    public String toString() { //Benutzerfreundliche Darstellung
        return String.format("|%s:%s %d|", kuerzel, wert, zahl); //String formatieren : Text vor(Kuerzel) und hinter(Anhang) dem Doppelpunkt, Nummer einfügen
    }
}
