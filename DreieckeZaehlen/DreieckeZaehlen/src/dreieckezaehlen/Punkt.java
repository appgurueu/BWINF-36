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
public class Punkt { //Speichert einen 2d-Punkt
    public Dezimalzahl x; //X-Koordinate
    public Dezimalzahl y; //Y-Koordinate
    public Punkt(Dezimalzahl x, Dezimalzahl y) { //Konstruktor-die Koordinaten werden vorgegeben
        this.x=x;
        this.y=y;
    }
    public boolean e(Punkt p) { //Entspricht dieser Punkt einem anderen Punkt ?
        return this.x.e(p.x) && this.y.e(p.y); //Sind X-und Y-Koordinate gleich ?
    }
    public Punkt minus(Punkt p) {
        return new Punkt(x.minus(p.x),y.minus(p.y)); //Zieht einen Punkt p von diesem Punkt ab
    }
    @Override
    public String toString() { //Textausgabe
        return "("+x.toString()+"|"+y.toString()+")";
    }
}
