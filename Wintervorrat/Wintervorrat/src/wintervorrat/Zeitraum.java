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
public class Zeitraum { //Speichert einen Zeitraum

    public short anfangSichererZeitraum; //Erste Minute des sicheren Zeitraumes
    public short endeSichererZeitraum; //Letzte Minute des sicheren Zeitraumes

    public Zeitraum(short anfangSichererZeitraum, short endeSichererZeitraum) { //Konstruktor - Neuen Zeitraum aus vorgegebener Anfangs-und Endminute erstellen
        this.anfangSichererZeitraum = anfangSichererZeitraum;
        this.endeSichererZeitraum = endeSichererZeitraum;
    }
}
