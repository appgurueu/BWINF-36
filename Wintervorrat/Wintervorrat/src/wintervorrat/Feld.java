/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wintervorrat;

import java.util.ArrayList;

/**
 *
 * @author lars
 */
public class Feld extends ArrayList<Zeitraum> { //Ein Feld : Enthält alles was wir über ein Feld wissen müssen,,so eine Liste von sicheren Zeiträumen
    public boolean totalSicher; //Speichert, ob das Feld total sicher ist
    public short anfangSichererZeitraum; //Speichert den letzten Adlerüberflug + 1, den Anfang eines sicheren Zeitraums
    public Feld(boolean totalSicher) { //Konstruktor, Variablen initialiseren
        super(); //Liste initialiseren, noch kein Zeitraum bekannt
        this.totalSicher=totalSicher; //Ob das Feld als total sicher gilt, wird vorgegeben
        this.anfangSichererZeitraum=-1; //Noch kein bekannter sicherer Zeitraum
    }
}
