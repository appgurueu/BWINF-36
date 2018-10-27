/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bauernopfer;

/**
 *
 * @author lars
 */
public class Punkt { //Speichert einen Punkt auf dem Spielbrett

    public byte x; //X-Koordinate(von 0-7), deshalb reicht 1 Byte
    public byte y; //Y-Koordinate(von 0-7), deshalb reicht 1 Byte

    public Punkt(byte x, byte y) { //Konstruktor - X und Y werden gegeben
        this.x = x;
        this.y = y;
    }

    public double entfernung(Punkt p) { //Bestimmt nach dem Satz des Pythagoras die Entfernung zweier Punkte
        byte xw=(byte) (p.x-x);
        byte yw=(byte) (p.y-y);
        return  Math.sqrt(xw * xw + yw * yw);
    }

    public Punkt plus(Punkt p) { //Addiert auf diesen Punkt einen Punkt p
        return new Punkt((byte)(this.x+p.x),(byte)(this.y+p.y));
    }
    
    public Punkt minus(Punkt p) { //Zieht von diesem Punkt einen Punkt p ab
        return new Punkt((byte)(this.x-p.x),(byte)(this.y-p.y));
    }
    
    @Override
    public String toString() { //Gibt diesen Punkt in Tupleschreibweise aus
        return "(" + Byte.toString(x) + "|" + Byte.toString(y) + ")";
    }
}
