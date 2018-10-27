/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zimmerbelegung;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author lars
 */
public class Zimmer {

    public static byte ZAEHLER = 1; //Zähler fürs Durchnummerieren
    public byte id; //ID
    public HashMap<String, Boolean> zimmergegner; //Gegner, Wert ist hier egal
    public HashMap<String, Boolean> zimmerinsassen; //Insassen, Wert ist hier egal

    public Zimmer(byte id) { //Konstruktor, Zimmernummer wird vorgegeben
        this.id = id;
        zimmerinsassen = new HashMap();
        zimmergegner = new HashMap();
    }

    public static void zaehlerZuruecksetzen() {
        ZAEHLER = 1;
    }

    public boolean kannLeiden(Zimmer z) { //Prüft, ob ein Zimmer und ein Anderes zusammengeführt werden können, also ob kein Zimmerinsasse einen Insassen des anderen Zimmers als Gegner hat
        for (Entry e : z.zimmerinsassen.entrySet()) {
            String insasse=(String)e.getKey();
            for (Entry v : zimmergegner.entrySet()) {
                String gegner=(String)v.getKey();
                if (insasse.equals(gegner)) { //Ist ein Gegner dieses Zimmers im anderen Zimmer ?
                    return false; //Gebe zurück : Leider können sich die Zimmer nicht leiden !
                }
            }
        }
        for (Entry e : zimmerinsassen.entrySet()) {
            String insasse=(String)e.getKey();
            for (Entry v : z.zimmergegner.entrySet()) {
                String gegner=(String)v.getKey();
                if (insasse.equals(gegner)) { //Ist ein Gegner des anderen Zimmers in diesem Zimmer ?
                    return false; //Gebe zurück : Leider können sich die Zimmer nicht leiden !
                }
            }
        }
        return true; //Wenn nichts davon der Fall ist, können sich die Zimmer leiden.
    }

    public Zimmer zimmerZusammenfuehren(Zimmer z) { //Neues Zimmer aus diesem Zimmer + Zimmer z erstellen
        Zimmer g=new Zimmer(this.id);
        g.zimmerinsassen.putAll(z.zimmerinsassen);
        g.zimmergegner.putAll(z.zimmergegner);
        g.zimmerinsassen.putAll(zimmerinsassen);
        g.zimmergegner.putAll(zimmergegner);
        return g;
    }

    @Override
    public String toString() { //Zimmer ausgeben, optimierte Darstellung
        String b = "Zimmer : " + Byte.toString(ZAEHLER) + " mit " + Integer.toString(zimmerinsassen.size()) + " Bewohner";
        if (zimmerinsassen.size() != 1) {
            b += "n";
        }
        String s = "";
        if (ZAEHLER != 1) {
            for (int i = 0; i < b.length(); i++) {
                s += "-";
            }
        }
        s += "\n" + b + "\n";
        s+=Zimmerbelegung.gebeListeAus(zimmerinsassen.entrySet());
        ZAEHLER++;
        return s;
    }
}
