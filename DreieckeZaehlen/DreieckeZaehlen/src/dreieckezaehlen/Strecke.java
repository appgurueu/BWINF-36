/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dreieckezaehlen;

import java.util.HashMap; //"Wörterbücher" : Jedem "Schlüssel" s wird ein "Wert" w zugeordnet

/**
 *
 * @author lars
 */
public class Strecke { //Speichert eine Strecke

    public HashMap<Strecke, Schnittpunkt> schnitte; //Schnittpunkt mit anderen Strecken
    public Rechteck r; //Umfassendes Rechteck
    public Punkt a, b; //Startpunkt/Endpunkt
    public Dezimalzahl steigungX; //Steigung per x-Wert
    public Dezimalzahl steigungY; //Steigung per y-Wert
    public Dezimalzahl Y; //Y-Achsenabschnitt
    public Dezimalzahl X; //X-Achsenabschnitt
    public boolean waagerecht; //Speichert für Strecken ohne Steigung die Ausrichtung
    public boolean sonderfall; //Speichert, ob diese Strecke solch eine ist

    public Strecke(Punkt a, Punkt b) { //Konstruktor - Start/Endpunkt werden vorgegeben
        schnitte = new HashMap();
        r = new Rechteck(a, b); //Umfassendes Rechteck erzeugen
        this.a = a;
        this.b = b;
        Punkt temp = b.minus(a); //b-a=(delta x|delta y)
        if (!temp.x.isNull() && !temp.y.isNull()) { //Falls diese kein Sonderfall ist(nicht waagerecht oder senkrecht)
            sonderfall = false; //Kein Sonderfall
            steigungX = temp.y.geteilt(temp.x); //Steigung X bestimmen
            Y = this.a.y.minus(this.a.x.mal(steigungX)); //X-Achsenabschnitt berechnen
            steigungY = temp.x.geteilt(temp.y); //Steigung Y bestimmen
            X = this.a.x.minus(this.a.y.mal(steigungY)); //Y-Achsenabschnitt berechnen
        } else {
            sonderfall = true; //Ansonsten ist es ein Sonderfall, dann gilt es nur zu ermitteln, ob waagerecht oder senkrecht
            steigungX = null;
            steigungY = null;
            X = null;
            Y = null;
            waagerecht = !temp.x.isNull(); //Sollte die x-Achsendifferenz null sein, ist die Strecke senkrecht also !waagerecht
        }
    }

    public Schnittpunkt schnittPunkt(Strecke s) { //Ermittelt den Schnittpunkt zweier Strecken, und damit auch, ob sie sich schneiden
        Dezimalzahl schnitt_x, schnitt_y; //Schnitt X-und Y-Koordinate
        schnitt_x = null;
        schnitt_y = null;
        if (!s.sonderfall && !this.sonderfall) { //Sollten beide Strecken keine Sonderfälle sein
            if (s.steigungY.e(this.steigungY)) { //Sind die Steigungen identisch
                return new Schnittpunkt(); //Gibt es keinen Schnittpunkt
            }
            Dezimalzahl steigungsdifferenz = this.steigungX.minus(s.steigungX); //Formel : Differenz der Steigungen
            Dezimalzahl offsetdifferenz = s.Y.minus(this.Y); //Formel : Differenz der Y-Achsenabschnitte
            schnitt_x = offsetdifferenz.geteilt(steigungsdifferenz); //Formel : Schnittpunkt X berechnen
            schnitt_y = this.Y.plus(schnitt_x.mal(this.steigungX)); //Schnittpunkt X einsetzen in eine der beiden Geradengleichungen
        } else if (s.sonderfall && this.sonderfall) { //Sollten beide Strecken Sonderfälle sein
            if (s.waagerecht != this.waagerecht) { //Sind sie nicht parallel zueinander
                if (s.waagerecht) { //Ist s waagerecht ?
                    schnitt_y = s.a.y; //Die Y-Koordinate des Schnittes ist dann von s
                    schnitt_x = this.a.x; //Entsprechend ist die X-Koordinate von dieser
                } else { //Sollte es andersrum sein...
                    //Werden auch die Koordinaten des Schnittes andersrum belegt
                    schnitt_x = s.a.x;
                    schnitt_y = this.a.y;
                }
            } else { //Wenn sie parallel zueinander sind, schneiden sie sich nicht
                return new Schnittpunkt();
            }
        } else if (s.sonderfall) { //Ist nur s ein Sonderfall
            if (s.waagerecht) { //Ist s waagerecht
                schnitt_y = s.a.y; //Ist die Y-Koordinate des Schnittpunkts klar von s
                Dezimalzahl offsetdifferenz = schnitt_y.minus(this.Y);
                schnitt_x = offsetdifferenz.geteilt(this.steigungX); //Nach der Formel wird dann die X-Koordinate ermittelt
            } else {
                schnitt_x = s.a.x; //Ansonsten ist es die X-Koordinate, welche von s ist
                Dezimalzahl offsetdifferenz = schnitt_x.minus(this.X);
                schnitt_y = offsetdifferenz.geteilt(this.steigungY); //Nach der Formel wird dann die Y-Koordinate ermittelt
            }
        } else if (this.sonderfall) { //Sollte diese Gerade von beiden der Sonderfall sein, geschieht das Selbe wie oben; lediglich s udn diese Gerade müssen vertauscht werden
            if (this.waagerecht) {
                schnitt_y = this.a.y;
                Dezimalzahl offsetdifferenz = schnitt_y.minus(s.Y);
                schnitt_x = offsetdifferenz.geteilt(s.steigungX);
            } else {
                schnitt_x = this.a.x;
                Dezimalzahl offsetdifferenz = schnitt_x.minus(s.X);
                schnitt_y = offsetdifferenz.geteilt(s.steigungY);
            }
        }
        Punkt p = new Punkt(schnitt_x, schnitt_y);
        if (s.r.schneidetPunkt(p) && this.r.schneidetPunkt(p)) { //Ist der Schnittpunkt "valid", liegt er also auf beiden Strecken
            return new Schnittpunkt(p.x, p.y); //Kann er zurückgegeben werden
        }
        return new Schnittpunkt(); //Sonst liegt wohl kein Schnitt vor...
    }
}
