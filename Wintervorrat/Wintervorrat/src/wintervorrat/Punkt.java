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
public class Punkt { //Speichert eine 2D-Koordinate

    public int x; //X
    public int y; //Y

    public Punkt(int x, int y) { //X und Y werden vorgegeben
        this.x = x;
        this.y = y;
    }

    public Punkt subtract(Punkt p) { //Erzeugt einen neuen Punkt von diesem Punkt minus einen Punkt einen Punkt p
        return new Punkt(x - p.x, y - p.y);
    }

    public Punkt add(Punkt p) { //Erzeugt einen neuen Punkt von diesem Punkt plus einen Punkt einen Punkt p
        return new Punkt(x + p.x, y + p.y);
    }
}
