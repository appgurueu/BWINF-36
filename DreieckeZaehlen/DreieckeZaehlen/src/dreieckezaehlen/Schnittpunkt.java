/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dreieckezaehlen;

/**
 *
 * @author lars
 */
public class Schnittpunkt extends Punkt { //Speichert einen Schnitt zweier Strecken, erweitert die Klasse "Punkt"
    public boolean schnitt; //Schneiden sich die Strecken überhaupt ?
    public Schnittpunkt() { //Erzeugt einen Schnittpunkt, der speichert, dass kein Schnitt vorliegt
        super(null,null); //Keine X und Y-Koordinaten
        schnitt=false; //Kein Schnitt
    }
    public Schnittpunkt(Dezimalzahl x, Dezimalzahl y) { //Erzeugt einen Schnittpunkt, der einen vorhandenen Schnitt speichert
        super(x,y); //X-und Y-Koordinate speichern
        schnitt=true; //Ein Schnitt
    }
}
