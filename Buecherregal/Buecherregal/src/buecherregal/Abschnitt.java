/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buecherregal;

import java.util.ArrayList;

/**
 *
 * @author lars
 */
public class Abschnitt extends ArrayList<Integer> { //Abschnitt - speichert Höhen der Bücher als Liste

    public static byte ZAEHLER = 1;

    public Abschnitt() {
        super();
    }

    public static void zaehlerZuruecksetzen() {
        ZAEHLER = 1;
    }

    public static String toString(ArrayList<Abschnitt> abschnitte) {
        String s = "";
        for (Abschnitt abschnitt : abschnitte) {
            s += abschnitt.toString();
            s += "\n";
        }
        return s;
    }

    @Override
    public String toString() { //Optimierte Darstellung/Ausgabe
        String b = "Abschnitt : " + Byte.toString(ZAEHLER) + " mit " + Integer.toString(size()) + " ";
        if (size() != 1) {
            b += "Büchern";
        } else {
            b += "Buch";
        }
        String s = "";
        if (ZAEHLER != 1) {
            for (int i = 0; i < b.length(); i++) {
                s += "-";
            }
        }
        s += "\n" + b + "\n";
        b = null;
        byte index = 0;
        byte counter = 0;
        byte zeilenumbruch = (byte) (Math.sqrt(size()));
        for (Integer e : this) {
            s += Integer.toString(e);
            counter++;
            index++;
            if (index != size()) {
                if (counter == zeilenumbruch) {
                    s += "\n";
                    counter = 0;
                } else if (index != size()) {
                    s += ", ";
                }
            }
        }
        ZAEHLER++;
        return s;
    }
}
