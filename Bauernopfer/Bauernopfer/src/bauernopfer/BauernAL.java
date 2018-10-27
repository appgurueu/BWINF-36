/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bauernopfer;

import java.util.ArrayList;

/**
 *
 * @author lars
 */
public class BauernAL {

    public static final Punkt[] RICHTUNGEN = new Punkt[]{new Punkt((byte) 0, (byte) -1), new Punkt((byte) 0, (byte) 1), new Punkt((byte) -1, (byte) 0), new Punkt((byte) 1, (byte) 0)}; //Bewegungsrichtungen Bauer

    public static Punktmenge platziereBauern(byte bauern) { //Platziert "bauern" Bauern, falls eine Dame im Spiel ist, in einer Reihe, sonst in einer Diagonale
        ArrayList<Punkt> result = new ArrayList();
        float faktor = 8.0f / (float) bauern; //Sorgt für eine gleichmäßige Verteilung der Bauern auf der 8 Felder langen Diagonalen/Reihe
        if (!Bauernopfer.dame) {
            for (byte b = 0; b < bauern; b++) {
                result.add(new Punkt((byte) (b * faktor), (byte) (b * faktor))); //X und Y-Koordinate ändern sich
            }
        } else {
            for (byte b = 0; b < bauern; b++) {
                result.add(new Punkt((byte) (b * faktor), (byte) 3)); //Nur die X-Koordinate ändert sich, die Y-Koordinate bleibt 3(möglichst mittig)
            }
        }
        return new Punktmenge(result);
    }

    public static boolean imFeld(Punkt p) { //Ermittelt, ob ein Punkt p innerhalb des Spielfeldes liegt
        return p.x > 0 && p.y > 0 && p.x < 8 && p.y < 8;
    }

    public static ArrayList<Punkt> bestimmeFelder(Punktmenge bauern, Punkt bauer) { //Bestimmt die Punkte, wo ein Bauer innerhalb eines Zuges hinziehen kann
        ArrayList<Punkt> ergebnis = new ArrayList();
        if (bauer.x != 0) { //Befindet er sich nicht am linken Rand
            ergebnis.add(new Punkt((byte) (bauer.x - 1), bauer.y)); //Kann er nach links ziehen
        }
        if (bauer.y != 0) { //Befindet er sich nicht am rechten Rand
            ergebnis.add(new Punkt(bauer.x, (byte) (bauer.y - 1))); //Kann er nach rechts ziehen
        }
        if (bauer.x != 7) { //Befindet er sich nicht am unteren Rand
            ergebnis.add(new Punkt((byte) (bauer.x + 1), bauer.y)); //Kann er nach unten ziehen
        }
        if (bauer.y != 7) { //Befindet er sich nicht am oberen Rand
            ergebnis.add(new Punkt(bauer.x, (byte) (bauer.y + 1))); //Kann er nach oben ziehen
        }
        Punktmenge feld = new Punktmenge(ergebnis);
        Punktmenge felder = feld.nund(bauern); //Alle Bewegungsmöglichkeiten, wo sich anderer Bauer befindet, entfernen
        return felder.punkte;
    }

    public static Punktmenge bestimmeFelder(Punktmenge bauern) { //Bestimmt alle Felder, die von allen "bauern" zusammen innerhalb eines Zuges erreicht werden können
        ArrayList<Punkt> insgesamt = new ArrayList(); //Ergebnis
        for (Punkt bauer : bauern.punkte) {
            insgesamt.addAll(bestimmeFelder(bauern, bauer)); //Alle Zugmöglichkeiten jedes Bauern zum Ergebnis hinzufügen
        }
        return new Punktmenge(new Punktmenge(insgesamt).raster); //(1) Gibt das Ergebnis als Punktmenge zurück, und (2) entfernt doppelt vorkommende Punkte 
    }

    public static byte[][] bestimmeFelder(Punktmenge bauern, byte zuege) { //Bestimmt alle Felder, die von allen "bauern" zusammen innerhalb von "zuege" Zügen erreicht werden können
        byte[][] ergebnis = new byte[8][8]; //Ergebnis als Spielfeld. Jedes Feld speichert eine Nummer, die angibt, wie viele Züge die Bauern brauchen, um das Feld zu erreichen
        ArrayList<Punkt> neu_checken; //Punkte, die danach neu hinzukommen, und geprüft werden sollen
        ArrayList<Punkt> checken = new ArrayList(); //Punkte, für die ermittelt werden soll, welche Felder die Bauern von ihnen aus erreichen können
        checken.addAll(bauern.punkte); //Es fängt bei den Bauern an
        byte durchlauf = 1; //Gibt an, der wievielte Zug momentan simuliert wird
        while (true) {
            if (checken.isEmpty() || durchlauf > zuege) { //Wenn es keine weitere von den Bauern erreichbare Felder gibt, oder schon genug Züge simuliert worden sind
                break; //Dann sind wir fertig
            }
            neu_checken = new ArrayList();
            for (Punkt p : checken) { //Für alle zu prüfenden Punkte
                ArrayList<Punkt> vpe = bestimmeFelder(new Punktmenge(), p); //Die von ihnen aus erreichbaren Punkte bestimmen
                for (Punkt q : vpe) { //Von den erreichbaren Punkten
                    if (ergebnis[q.x][q.y] == 0 && !bauern.raster[q.x][q.y]) { //Wenn der Punkt nicht schon erreicht wurde, und dort kein Bauer ist
                        ergebnis[q.x][q.y] = (byte) (durchlauf); //Dann speichern wir, das er im Zug "durchlauf" erreicht werden kann
                        neu_checken.add(q); //Für diesen Punkt soll dann im nächsten Durchlauf geprüft werden, welche Felder von ihm aus erreichbar sind
                    }
                }
            }
            //Im nächsten Durchlauf sollen die Punkte geprüft werden, die im jetztigen Durchlauf als "im nächsten Durchlauf zu prüfen" deklariert wurden
            checken = new ArrayList();
            for (Punkt k : neu_checken) {
                checken.add(k);
            }
            durchlauf++; //Nächster Durchlauf !
        }
        return ergebnis;
    }

    public static Punkt[] zieheBauern(Punktmenge bauern, Punkt turm) { //Gibt ein Punkt-Array zurück. Der erste Punkt gibt den gewählten Bauern an, der zweite den Punkt, wo dieser hinzieht
        byte[][] schlagbar = BauernAL.bestimmeFelder(bauern, Bauernopfer.bleibende_zuege); //Bestimmt alle Felder, die von allen Bauern zusammen innerhalb von den bleibenden Zügen erreicht werden können
        byte bester_bauer = 0;
        byte beste_zugzahl = Byte.MAX_VALUE;
        if (schlagbar[turm.x][turm.y] != 0) { //Sollte der Turm mithilfe der übrig bleibenden Züge erreichbar sein, wird zum Turm hin gezogen
            //So ermitteln wir zuerst den Bauern, der am nächsten am Turm liegt
            for (byte i = 0; i < bauern.punkte.size(); i++) {
                Punkt d = turm.minus(bauern.punkte.get(i)); //Abstand in x und y Richtung zum Turm
                byte f = (byte) (Math.abs(d.x) + Math.abs(d.y)); //Benötigte Züge
                if (f < beste_zugzahl) { //Wenn dieser Bauer weniger Züge benötigen würde
                    beste_zugzahl = f; //Die beste Zugzahl ist dann f
                    bester_bauer = i; //Der beste Bauer ist dann dieser
                }
            }
            Punkt d = turm.minus(bauern.punkte.get(bester_bauer)); //Weg, der zum Turm gegangen werden muss
            byte r;
            if (d.x != 0) { //Ist der noch nicht auf der gleichen x-Koordinate wie der Turm
                //Dann muss er da erstmal hin gehen
                if (d.x < 0) { //Ist er rechts vom Turm
                    r = 2; //Muss er nach links gehen
                } else {
                    r = 3; //Sonst nach rechts
                }
            } else if (d.y < 0) { //Ansonsten ist er auf der gleichen x-Koordinate wie der Turm, in diesem Fall unter ihm
                r = 0; //Dann muss er nach oben gehen
            } else {
                r = 1; //Ansonsten geht's nach unten
            }
            return new Punkt[] {bauern.punkte.get(bester_bauer), bauern.punkte.get(bester_bauer).plus(RICHTUNGEN[r])}; //Geben wir zurück : Der nächste Bauer in die bestimmte Richtung
        }
        short beste_gewichtung = Short.MAX_VALUE;
        byte meiste_erreichbare_felder = Byte.MAX_VALUE;
        double beste_entfernung = Double.MAX_VALUE;
        byte richtung = 0;
        //Brute-Force ! Wir ermitteln, welcher Zug sich aus Sicht der Bauern am meisten lohnt
        for (byte i = 0; i < bauern.punkte.size(); i++) { //Alle Bauern durchgehen
            for (byte r = 0; r < 4; r++) { //Alle möglichen 4 Bewegungsrichtungen für jeden durchgehen
                Punkt bauer = bauern.punkte.get(i);
                Punkt neue_position = bauer.plus(RICHTUNGEN[r]); //Position des Bauern nach ziehen in die Richtung r
                if (!BauernAL.imFeld(neue_position) || bauern.raster[neue_position.x][neue_position.y]) {
                    continue; //Dann simulieren wir hier nicht weiter
                }
                //Wir simulieren nun
                Punktmenge bauern_moved = new Punktmenge(bauern.raster); //Die Bauern, nach dem Zug
                bauern_moved.raster[bauer.x][bauer.y] = false; //Der Bauer wird an der alten Stelle gelöscht
                bauern_moved.raster[neue_position.x][neue_position.y] = true; //Und an der neuen gesetzt
                bauern_moved = new Punktmenge(bauern_moved.raster); //Punktmenge aktualisieren
                byte[][] gewichtungen = TurmAL.gewichteteFelder(bauern_moved, turm); //Spielbrett, auf dem eingetragen ist, 4-wieviele Züge der Turm bräuchte, um das jeweilige Feld zu erreichen, also die "Gewichtungen". Felder, die er nicht erreichen kann, sind 0.
                Punktmenge p = new Punktmenge(gewichtungen); //Punktmenge, repräsentiert erreichbare Felder
                byte e = (byte) p.punkte.size(); //Wie viele Felder sind überhaupt erreichbar ?
                short g = 0; //Insgesamt-Gewichtung
                for (byte[] spalte : gewichtungen) {
                    for (byte zeile : spalte) {
                        g += zeile; //Von jedem Feld die Gewichtung hinzufügen
                    }
                }
                double entfernung = TurmAL.bestimmeEntfernung(bauern_moved, turm); //Insgesamt-Entfernung der Bauern zum Turm
                if (e < meiste_erreichbare_felder) { //Würde dieser Zug dem Turm mehr Freiheiten wegnehmen ?
                    //Alle "Rekord-Variablen" auf den Stand vom jetzigen Rekordhalter setzen, und speichern, das diesen Bauer in diese Richtung zu ziehen die beste Wahl ist
                    richtung = r;
                    bester_bauer = i;
                    meiste_erreichbare_felder = e;
                    beste_entfernung = entfernung;
                    beste_gewichtung = g;
                } else if (e == meiste_erreichbare_felder) { //Wäre dieser Zug in Hinsicht auf die erreichbaren Felder equivalent zum jetzigen Rekordhalter ?
                    if (g < beste_gewichtung) { //Würde dieser Zug im Vergleich zum jetzigen Rekordhalter dem Turm "wertvollere" Freiheiten wegnehmen ?
                        //Alle "Rekord-Variablen" auf den Stand vom jetzigen Rekordhalter setzen, und speichern, das diesen Bauer in diese Richtung zu ziehen die beste Wahl ist
                        beste_gewichtung = g;
                        richtung = r;
                        bester_bauer = i;
                        meiste_erreichbare_felder = e;
                        beste_entfernung = entfernung;
                    } else if (g == beste_gewichtung) { //Wäre dieser Zug AUCH in Hinsicht auf die "wertvollen" Freiheiten equivalent zum jetzigen Rekordhalter ?
                        if (entfernung < beste_entfernung) { //Jedoch bei diesem Zug die Bauern näher am Turm dran
                            //Alle "Rekord-Variablen" auf den Stand vom jetzigen Rekordhalter setzen, und speichern, das diesen Bauer in diese Richtung zu ziehen die beste Wahl ist
                            beste_gewichtung = g;
                            richtung = r;
                            bester_bauer = i;
                            meiste_erreichbare_felder = e;
                            beste_entfernung = entfernung;
                        }
                    }
                }
            }
        }
        return new Punkt[] {bauern.punkte.get(bester_bauer), bauern.punkte.get(bester_bauer).plus(RICHTUNGEN[richtung])}; //Am Ende geben wir den nach den Prinzipien gefundenen besten Zug zurück
    }
}
