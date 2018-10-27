/*
 * To change value license header, choose License Headers in Project Properties.
 * To change value template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dreieckezaehlen;

import java.math.BigDecimal; //Dezimalzahlen mit vielen Nachkommastellen
import java.text.DecimalFormat; //Textausgabe

/**
 *
 * @author lars
 */

public class Dezimalzahl {

    public static final Dezimalzahl ZERO=new Dezimalzahl(new BigDecimal("0")); //Dezimalzahl 0
    public static final DecimalFormat DARSTELLUNG=new DecimalFormat("#.######"); //Ausgabeformat/Darstellung : 6 Nachkommastellen
    public BigDecimal wert; //Wert der Dezimalzahl
    
    public Dezimalzahl(double w) {
        wert=new BigDecimal(w);
    }
    
    public Dezimalzahl(BigDecimal w) {
        wert=w;
    }
    
    public boolean isNull() { //Ermittelt, ob diese Dezimalzahl 0 ist
        return this.w().compareTo(BigDecimal.ZERO) == 0;
    }
    
    public boolean e(Dezimalzahl b) { //Ermittelt, ob zwei Dezimalzahlen einander entsprechen
        return this.w().compareTo(b.w())==0;
    }
    
    public BigDecimal w() { //Gerundet auf 12 Nachkommastellen
        return wert.setScale(12, BigDecimal.ROUND_HALF_UP);
    }
    
    public Dezimalzahl minus() { //Vorzeichen umdrehen
        return new Dezimalzahl(wert.negate());
    }
    
    public boolean gg(Dezimalzahl b) { //Ist diese Dezimalzahl größer-gleich b ?
        return this.w().compareTo(b.w()) >= 0;
    }
    
    public boolean kg(Dezimalzahl b) { //Ist diese Dezimalzahl kleiner-gleich b ?
        return this.w().compareTo(b.w()) <= 0;
    }
    
    public double wert() { //Gibt diese Dezimalzahl im "double"-Format zurück
        return this.w().doubleValue();
    }
    
    public Dezimalzahl plus(Dezimalzahl b) {
        return new Dezimalzahl(wert.add(b.wert));
    }
    
    public Dezimalzahl minus(Dezimalzahl b) {
        return new Dezimalzahl(wert.subtract(b.wert));
    }
    public Dezimalzahl mal(Dezimalzahl b) {
        return new Dezimalzahl(wert.multiply(b.wert));
    }
    public Dezimalzahl geteilt(Dezimalzahl b) {
        return new Dezimalzahl(wert.divide(b.wert,64,BigDecimal.ROUND_HALF_UP)); //Mit 64 Nachkommastellen Genauigkeit rechnen
    }
    public static Dezimalzahl abs(Dezimalzahl b) { //Absolutwert von einer Dezimalzahl b
        return new Dezimalzahl(b.wert.abs());
    }
    public static Dezimalzahl min(Dezimalzahl g,Dezimalzahl b) { //Kleinster Wert von b und g
        return new Dezimalzahl(g.wert.min(b.wert));
    }
    public static Dezimalzahl max(Dezimalzahl g,Dezimalzahl b) { //Größter Wert von b und g
        return new Dezimalzahl(g.wert.max(b.wert));
    }
    @Override
    public String toString() { //Darstellung : 6 Nachkommastellen
        String s=DARSTELLUNG.format(this.w()); //Mit 6 Nachkommastellen anzeigen
        if (s.length() == 2 && s.charAt(0) == '-' && s.charAt(1) == '0') { //-0 wird zu 0
            return "0";
        }
        return s;
    }
}

