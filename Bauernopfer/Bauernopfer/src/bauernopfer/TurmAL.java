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
class TurmAL {
    public static final Punkt[] DAME_RICHTUNGEN=new Punkt[] {new Punkt((byte)1,(byte)1),new Punkt((byte)-1,(byte)-1),new Punkt((byte)-1,(byte)1),new Punkt((byte)1,(byte)-1)}; //Richtungen, in die eine Dame ziehen kann
    
    public static Punkt platziereTurm(Punktmenge bauern) { //Platziert den Turm
        Punktmenge unsicher = BauernAL.bestimmeFelder(bauern).oder(bauern); //Felder, wo sich (1) ein Bauer befindet, oder (2) ein Bauer hin schlagen kann, gelten alle als "unsicher"
        ArrayList<Punkt> fmmf = new ArrayList(); //Felder, die vorerst zur Auswahl stehen
        byte rekord = 0;
        for (byte x = 0; x < 8; x++) {
            for (byte y = 0; y < 8; y++) {
                if (!unsicher.raster[x][y]) { //Wenn das Feld sicher, also nicht unsicher ist
                    byte[][] a=gewichteteFelder(bauern, new Punkt(x,y)); //Ermitteln wir, welche Felder der Turm von hier aus in wievielen Zügen erreichen könnte, als Gewichtung
                    byte wert = 0;
                    for (byte x2 = 0; x2 < 8; x2++) {
                        for (byte y2 = 0; y2 < 8; y2++) {
                            if (a[x2][y2] != 0) { //Wenn das Feld gewichtet ist
                                unsicher.raster[x2][y2] = true; //Alle Felder, die der Turm erreichen kann, brauchen wir nicht darauf zu prüfen, wie viele Felder von dort aus erreichbar sind
                                wert+=(byte)(4-a[x2][y2]); //Felder, für die es mehr Züge braucht, gelten als wertvoller
                            }
                        }
                    }
                    if (wert > rekord) { //Wenn diese Gewichtung den Rekordhalter schlägt
                        fmmf = new ArrayList(); //Ist der alte Bereich nicht mehr relevant
                    }
                    if (wert >= rekord) { //Wenn dieser Bereich äquivalent dem Rekordhalter, oder besser ist...
                        for (byte x2 = 0; x2 < 8; x2++) {
                            for (byte y2 = 0; y2 < 8; y2++) {
                                if (a[x2][y2] != 0) {
                                    fmmf.add(new Punkt(x2, y2)); //Kommen dessen Punkte alle hinzu
                                }
                            }
                        }
                    }
                }
            }
        }
        Punkt rhalter = new Punkt((byte) 0, (byte) 0); //Bester Punkt
        double rdis = 0; //Rekorddistanz Turm zum nächsten Bauern
        if (fmmf.isEmpty()) { //Es konnte kein sicheres Feld gefunden werden
            //So ermitteln wir von allen Punkten den, wo der Turm vom nächsten Bauern entfernt ist
            for (byte x = 0; x < 8; x++) {
                for (byte y = 0; y < 8; y++) {
                    if (!bauern.raster[x][y]) { //Alle Punkte, wo kein Bauer ist
                        Punkt k = new Punkt(x, y);
                        double mindis = Double.MAX_VALUE;
                        for (Punkt bauer : bauern.punkte) {
                            double dis = bauer.entfernung(k);
                            if (dis < mindis) {
                                mindis = dis;
                            }
                        }
                        if (mindis > rdis) {
                            rdis = mindis;
                            rhalter = k;
                        }
                    }
                }
            }
            fmmf.add(rhalter); //Der Rekordhalter kommt infrage
        }
        //Besten Platzierungspunkt aus den infrage kommenden bestimmen
        Punkt rekordhalter = fmmf.get(0); 
        double rekord_distanz = bestimmeEntfernung(bauern, rekordhalter);
        for (byte i = 1; i < fmmf.size(); i++) {
            double d = bestimmeEntfernung(bauern, fmmf.get(i));
            if (d > rekord_distanz) {
                rekord_distanz = d;
                rekordhalter = fmmf.get(i);
            }
        }
        return rekordhalter; //Diesen zurückgeben
    }

    public static double bestimmeEntfernung(Punktmenge bauern, Punkt t) { //Bestimmt die insgesamte Entfernung des Turmes t zu den "bauern"
        double result = 0;
        for (Punkt bauer : bauern.punkte) {
            result += bauer.entfernung(t);
        }
        return result;
    }

    public static Punktmenge bestimmeFelder(Punktmenge bauern, Punkt turm) { //Bestimmt alle Felder, die der Turm/die Dame innerhalb eines Zuges erreichen kann
        ArrayList<Punkt> ergebnis = new ArrayList();
        byte minx = -1;
        byte maxx = 8;
        byte miny = -1;
        byte maxy = 8;
        for (Punkt bauer : bauern.punkte) {
            //Welche Bauern beschränken den Turm in der Waagerechten ?
            if (bauer.x == turm.x) {
                if (bauer.y < turm.y) {
                    if (bauer.y > miny) {
                        miny = bauer.y;
                    }
                } else if (bauer.y < maxy) {
                    maxy = bauer.y;
                }
            }
            //Und welche in der Senkrechten ?
            else if (bauer.y == turm.y) {
                if (bauer.x < turm.x) {
                    if (bauer.x > minx) {
                        minx = bauer.x;
                    }
                } else if (bauer.x < maxx) {
                    maxx = bauer.x;
                }
            }
        }
        //Dann kann er vom Bauern, der am wenigsten links von ihm steht, bis zum Bauern, der am wenigsten rechts von ihm steht
        for (byte y = (byte) (miny + 1); y < maxy; y++) {
            ergebnis.add(new Punkt(turm.x, y));
        }
        //Dann kann er vom Bauern, der am wenigsten über ihm steht, bis zum Bauern, der am wenigsten unter ihm steht
        for (byte x = (byte) (minx + 1); x < maxx; x++) {
            ergebnis.add(new Punkt(x, turm.y));
        }
        if (Bauernopfer.dame) { //Wird mit Dame gespielt
            boolean[] nicht_checken=new boolean[4]; //Welche Bewegungsrichtungen der Dame brauchen wir nicht mehr prüfen ?
            for (int d=1; d < 8; d++) { //Wir gehen bis zu 7 mögliche Diagonalzüge durch
                boolean b=true;
                for (int i=0; i < 4; i++) { //Alle Bewegungsmöglichkeiten der Dame durchgehen
                    if (!nicht_checken[i]) { //Wenn sie in die Richtung noch weiterlaufen kann
                        Punkt c=DAME_RICHTUNGEN[i];
                        Punkt cpos=new Punkt((byte)(turm.x+c.x*d),(byte)(turm.y+c.y*d)); //Dann ermitteln wir, wo sie da hin käme
                        if (!BauernAL.imFeld(cpos) || bauern.raster[cpos.x][cpos.y]) { //Wurde sie (1) ausßerhalb des Feldes laufen oder (2) auf einen Bauern laufen
                            nicht_checken[i]=true; //Geht's in die Richtung nicht mehr weiter
                            continue; //Diese Richtung wird nicht weiter verfolgt
                        }
                        b=false; //Wenn noch eine weitere Zugmöglichkeit gefunden wurde, simulieren wir weiter
                        ergebnis.add(cpos); //Und fügen die Zugmöglichkeit zum Ergebnis zurück
                    }
                }
                if (b) { //Konnte sich die Dame nicht weiter bewegen
                    break; //Sind wir fertig
                }
            }
        }
        return new Punktmenge(ergebnis); //Ergebnis zurückgeben
    }

    public static Punktmenge sichereFelder(Punktmenge bauern, Punkt turm) { //Ermittelt die Felder, die vom Turm, aber nicht von den Bauern in der ihnen bleibenden Zugzahl betreten werden können
        Punktmenge begehbareFelder = TurmAL.bestimmeFelder(bauern, turm); //Vom Turm begehbare Felder
        Punktmenge unsicher = new Punktmenge(BauernAL.bestimmeFelder(bauern, Bauernopfer.bleibende_zuege)); //Von den Bauern innerhalb der bleibenden Züge erreichbare Felder
        Punktmenge sichereFelder = begehbareFelder.nund(unsicher); //Alle Felder, wo der Turm aber kein Bauer hinkommt
        return sichereFelder;
    }

    public static Punkt zieheTurm(Punktmenge bauern, Punkt turm) { //Zieht den Turm
        Punktmenge moeglichkeiten = smarteFelder(bauern, turm); //Alle vorgeschlagenen Zugmöglichkeiten
        Punkt best_punkt = null;
        double best_entfernung = 0.0f;
        byte max_sum = 0;
        for (Punkt p : moeglichkeiten.punkte) { //Von allen vorgeschlagenen Zugmöglichkeiten
            byte sum = 0;
            byte[][] gewichtete_felder = gewichteteFelder(bauern, p);
            for (byte x = 0; x < 8; x++) {
                for (byte y = 0; y < 8; y++) {
                    sum += gewichtete_felder[x][y];
                }
            }
            double e = 0;
            for (Punkt b : bauern.punkte) {
                e += p.entfernung(b);
            }
            if (sum > max_sum) { //Resultieren aus diesem Zug "bessere" / "wertvollere" Freiheiten ?
                //Gilt dieser jetzt als Bester
                max_sum = sum;
                best_entfernung = e;
                best_punkt = p;
            } else if (sum == max_sum && e > best_entfernung) { //Ist dieser Zug äquivalent, aber wäre der Turm hiermit weiter entfernt
                //Gilt dieser jetzt als Bester
                best_entfernung = e;
                best_punkt = p;
            }
        }
        return best_punkt;
    }

    public static byte[][] gewichteteFelder(Punktmenge bauern, Punkt turm) {
        byte[][] ergebnis = new byte[8][8]; //Ergebnis als Spielfeld. Jedes Feld speichert eine Nummer, die angibt, wie viele Züge die Bauern brauchen, um das Feld zu erreichen, als Gewichtung : 4-n
        ArrayList<Punkt> checken = new ArrayList(); //Punkte, die danach neu hinzukommen, und geprüft werden sollen
        ArrayList<Punkt> neu_checken; //Punkte, für die ermittelt werden soll, welche Felder der Turm von ihnen aus erreichen können
        checken.add(turm); //Beim Turm fangen wir an
        byte durchlauf = 1;
        while (true) {
            if (checken.isEmpty()) { //Gibt's keine weiteren zu prüfenden Felder
                break; //Sind wir fertig
            }
            neu_checken = new ArrayList();
            for (Punkt p : checken) { //Für alle zu prüfenden Punkte
                Punktmenge vpe = TurmAL.sichereFelder(bauern, p); //Die von ihnen aus erreichbaren(sicheren) Punkte bestimmen
                for (Punkt q : vpe.punkte) { //Von den erreichbaren Punkten
                    if (ergebnis[q.x][q.y] == 0) { //Wenn der Punkt nicht schon erreicht wurde
                        ergebnis[q.x][q.y] = (byte) (4 - durchlauf); //Dann speichern wir, dass er mit Gewichtung 4-"durchlauf", also 4-Gewichtung Zügen erreicht werden kann
                        neu_checken.add(q); //Für diesen Punkt soll dann im nächsten Durchlauf geprüft werden, welche Felder von ihm aus erreichbar sind
                    }
                }
            }
            //Im nächsten Durchlauf sollen die Punkte geprüft werden, die im jetztigen Durchlauf als "im nächsten Durchlauf zu prüfen" deklariert wurden
            checken = new ArrayList();
            for (Punkt q2 : neu_checken) {
                checken.add(q2);
            }
            durchlauf++; //Nächster Durchlauf !
        }
        return ergebnis;
    }

    public static Punktmenge smarteFelder(Punktmenge bauern, Punkt turm) { //Felder, die für den Turm zum hingehen in Betracht gezogen werden
        Punkt[][] links = new Punkt[8][8]; //Speichert, von welchen Punkt aus ein Feld erreicht wurde
        byte[][] w = new byte[8][8]; //Speichert die Gewichtungen der erreichbaren Felder
        ArrayList<Punkt> checken = new ArrayList(); //Punkte, die danach neu hinzukommen, und geprüft werden sollen
        ArrayList<Punkt> neu_checken; //Punkte, für die ermittelt werden soll, welche Felder der Turm von ihnen aus erreichen können
        checken.add(new Punkt(turm.x, turm.y)); //Beim Turm fangen wir an
        byte durchlauf = 1;
        while (true) {
            if (checken.isEmpty()) { //Gibt's keine weiteren zu prüfenden Felder
                break; //Sind wir fertig
            }
            neu_checken = new ArrayList();
            for (Punkt p : checken) { //Für alle zu prüfenden Punkte
                Punktmenge vpe = TurmAL.sichereFelder(bauern, p); //Die von ihnen aus erreichbaren(sicheren) Punkte bestimmen
                for (Punkt q : vpe.punkte) { //Von den erreichbaren Punkten
                    if (links[q.x][q.y] == null) { //Wenn der Punkt nicht schon erreicht wurde
                        w[q.x][q.y] = durchlauf; //Dann speichern wir, dass das Feld im "durchlauf" Durchlauf erreicht werden könnte
                        links[q.x][q.y] = p; //Wir speichern, von welchem Punkt aus er erreicht wurde
                        neu_checken.add(q); //Für diesen Punkt soll dann im nächsten Durchlauf geprüft werden, welche Felder von ihm aus erreichbar sind
                    }
                }
            }
            //Im nächsten Durchlauf sollen die Punkte geprüft werden, die im jetztigen Durchlauf als "im nächsten Durchlauf zu prüfen" deklariert wurden
            checken = new ArrayList();
            for (Punkt q2 : neu_checken) {
                checken.add(q2);
            }
            durchlauf++; //Nächster Durchlauf !
        }
        //Jetzt ermitteln wir die Ursprungspunkte für die in 3 Zügen erreichbaren Felder, diese sind das Ergebnis
        ArrayList<Punkt> ergebnis = new ArrayList();
        for (byte x = 0; x < 8; x++) {
            for (byte y = 0; y < 8; y++) {
                if (w[x][y] == 3) {
                    Punkt p = links[x][y];
                    Punkt r = links[p.x][p.y];
                    ergebnis.add(r);
                }
            }
        }
        if (ergebnis.isEmpty()) { //Gab es keine in 3 Zügen erreichbare Felder
            //So ermitteln wir die Ursprungspunkte für die in zwei Zügen Erreichbaren
            for (byte x = 0; x < 8; x++) {
                for (byte y = 0; y < 8; y++) {
                    if (w[x][y] == 2) {
                        Punkt p = links[x][y];
                        ergebnis.add(p);
                    }
                }
            }
        }
        if (ergebnis.isEmpty()) { //Gab es SOGAR keine in 2 Zügen erreichbare Felder
            //So ermitteln wir alle in einem Zug erreichbaren
            for (byte x = 0; x < 8; x++) {
                for (byte y = 0; y < 8; y++) {
                    if (w[x][y] == 1) {
                        ergebnis.add(new Punkt(x, y));
                    }
                }
            }
        }
        if (ergebnis.isEmpty()) { //Gab es keine sicheren Felder, die in einem Zug erreicht werden konnten
            ergebnis.add(turm); //Ist es egal, was der Turm/die Dame, einfach stehenbleiben !
        }
        return new Punktmenge(ergebnis);
    }
}
