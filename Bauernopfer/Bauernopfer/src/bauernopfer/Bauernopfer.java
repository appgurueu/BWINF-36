/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bauernopfer;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList; //Listen
import java.util.Scanner; //Benutzereingabe
import javax.swing.JFrame; //Fenster

public class Bauernopfer {

    public static boolean bauern_dran = true; //Sind die Bauern momentan dran ?
    public static boolean dame = false; //Wird mit Dame gespielt ?
    public static byte zuege; //Wie viele Züge haben die Bauern ?
    public static byte bleibende_zuege; //Wie viele Züge bleiben den Bauern noch
    public static final Scanner EINGABE = new Scanner(System.in); //Benutzereingabe
    public static JFrame fenster;

    public static boolean jaNeinFrage(String frage) { //Stellt eine ja/nein Frage an den Benutzer
        while (true) {
            System.out.println(frage + "(j/n) ? ");
            String s = EINGABE.nextLine().toLowerCase();
            if (s.equals("j")) {
                return true;
            } else if (s.equals("n")) {
                return false;
            }
            System.out.println("Bitte antworten sie mit j/n beziehungsweise J/N. Versuchen sie es erneut.");
        }
    }

    public static byte mengenFrage(String frage) { //Fragt nach einer Zahl von 1-8
        while (true) {
            System.out.println(frage + "(Zahl von 1-8) ? ");
            String s = EINGABE.nextLine().toLowerCase();
            try {
                byte so = Byte.parseByte(s);
                if (so > 0 && so < 9) {
                    return so;
                }
            } catch (NumberFormatException e) {
                System.out.println("Bitte antworten sie mit einer Zahl von 1 bis 8. Versuchen sie es erneut.");
            }
        }
    }

    public static void main(String[] args) {
        dame = jaNeinFrage("Soll mit Dame gespielt werden");
        boolean turmal = jaNeinFrage("Soll der Turm/die Dame vom Rechner gesteuert werden");
        boolean bauernal = jaNeinFrage("Sollen die Bauern vom Rechner gesteuert werden");
        byte bauern = mengenFrage("Wie viele Bauern soll es geben");
        zuege = mengenFrage("Wie viele Züge sollen sie haben");
        bleibende_zuege = zuege;
        fenster = new JFrame(); //Neues Fenster erzeugen
        fenster.setSize(400, 400); //Größe auf 400x400 = 50*8 x 50*8 Schachbrett
        Brett brett = new Brett(); //Neues Spielbrett erzeugen
        fenster.setContentPane(brett); //Brett im Fenster anzeigen
        fenster.setVisible(true); //Fenster sichtbar machen
        fenster.setFocusable(true); //Für die Maus fokusierbar machen
        fenster.requestFocus(); //Maus auf dieses Fenster fokusieren
        fenster.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Programm beendet.");
                System.exit(0); //Wird das Fenster geschlossen, soll das komplette Programm anhalten
            }
        });
        long now; //FPS-Zähler
        Maus maus = new Maus(); //Neue "Maus" erzeugen, die alle für dieses Programm relevanten "Maus-Infos" speichert
        fenster.addMouseListener(maus); //"Maus" zum Fenster hinzufügen, um Position etc. bei Änderung und pro Frame abzufangen
        if (bauernal) { //Werden die Bauern vom Rechner gesteuert ?
            brett.platziereBauern(BauernAL.platziereBauern((byte) bauern)); //Bauern von der BauernAL platzieren lassen
            brett.repaint(); //Anzeigen, wie sie platziert wurden
        } else { //Ansonsten kann man die Bauern selbst platzieren
            ArrayList<Punkt> plaetze = new ArrayList();
            byte platzieren = 0;
            now = System.currentTimeMillis();
            while (true) {
                if (System.currentTimeMillis() - now > 50) { //20 FPS
                    now = System.currentTimeMillis();
                    maus.infosHolen(); //Maus-Infos abfangen
                    if (maus.LMB > 1) { //Wurde die linke Maustaste gedrückt
                        Punkt p = new Punkt((byte) (maus.mouseX / 50), (byte) (maus.mouseY / 50)); //Ausgewähltes Feld
                        if (!brett.bauern.raster[p.x][p.y]) { //Ist an dem ausgewählten Feld noch kein Bauer
                            plaetze.add(p); //So fügen wir diesen hinzu
                            brett.platziereBauern(new Punktmenge(plaetze)); //Und updaten das Brett
                            brett.repaint(); //Brett neuzeichnen
                            platzieren++;
                            if (platzieren == bauern) { //Wurden alle Bauern platziert
                                break; //Sind wir hier fertig
                            }
                        }
                        maus.LMB = 0;
                    }
                }
            }
        }
        if (turmal) { //Falls der Turm/die Dame vom Computer gesteuert werden soll
            brett.platziereTurm(TurmAL.platziereTurm(brett.bauern)); //Automatisch platzieren
            brett.repaint(); //Brett neuzeichen
        } else {
            now = System.currentTimeMillis();
            while (true) {
                maus.infosHolen(); //Maus-Infos abfangen
                if (System.currentTimeMillis() - now > 50) { //20 FPS
                    now = System.currentTimeMillis();
                    if (maus.LMB > 1) { //Wurde die linke Maustaste gedrückt
                        Punkt p = new Punkt((byte) (maus.mouseX / 50), (byte) (maus.mouseY / 50)); //Ausgewähltes Feld
                        if (!brett.bauern.raster[p.x][p.y]) { //Ist an dem ausgewählten Feld noch kein Bauer
                            brett.platziereTurm(p); //So kann dort der Turm/die Dame platziert werden
                            brett.repaint(); //Brett neuzeichnen
                            break; //Der Turm/die Dame wurde platziert, hier sind wir fertig
                        }
                        maus.LMB = 0;
                    }
                }
            }
        }
        Punkt b = null; //Koordinaten vom aktuell ausgewählten Bauern. Ist "null" wenn keiner ausgewählt wurde.
        //K und L : WENN K+L == 9 ?
        now = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - now > 50) { //20 FPS
                maus.infosHolen(); //Maus-Infos abfangen
                if (maus.LMB > 1) { //Wurde die linke Maustaste gedrückt
                    if (bauern_dran) { //Sind die Bauern dran
                        if (bauernal) { //Werden die Bauern vom Computer gesteuert
                            Punkt[] bewegen = BauernAL.zieheBauern(brett.bauern, brett.turm); //Bauern ziehen, gibt (1) die Ausgangsposition des Bauern, also welcher Bauer gezogen wird, und (2) dessen Zielposition zurück
                            brett.bewegeBauern(bewegen[0], bewegen[1]); //Brett aktualisieren
                            bleibende_zuege--;
                            if (bleibende_zuege == 0) { //Sollten den Bauern keine Züge mehr bleiben,
                                bauern_dran = false; //Ist wieder der Turm dran
                                bleibende_zuege = zuege; //Und die Bauern haben wieder volle belibende Zugzahl
                            }
                        } else if (b == null) { //Wenn der Mensch(der Spieler) spielt, und noch keinen Bauern ausgewählt hat, 
                            Punkt p = new Punkt((byte) (maus.mouseX / 50), (byte) (maus.mouseY / 50)); //Ausgewähltes Feld
                            if (brett.bauern.raster[p.x][p.y]) { //Befindet sich dort ein Bauer
                                b = p; //Wird dieser ausgewählt
                            }
                        } else { //Wenn der Mensch(der Spieler) spielt, und schon einen Bauern ausgewählt hat, 
                            ArrayList<Punkt> begehbar = BauernAL.bestimmeFelder(brett.bauern, b); //Ermitteln wir, welche Felder alle von dem Bauer begangen werden können
                            Punkt q = new Punkt((byte) (maus.mouseX / 50), (byte) (maus.mouseY / 50)); //Ausgewähltes Feld
                            boolean c = false; //Kontrolle c : Ist das ausgewählte Feld begehbar ?
                            for (Punkt k : begehbar) {
                                if (k.x == q.x && k.y == q.y) { //Es ist begehbar
                                    c = true;
                                    break; //Schleife beenden
                                }
                            }
                            if (c) {
                                brett.bewegeBauern(b, q); //Da es ja begehbar ist, können wir nun den Bauern ziehen
                                bleibende_zuege--;
                                if (bleibende_zuege == 0) { //Sollten den Bauern keine Züge mehr bleiben,
                                    bauern_dran = false; //Ist wieder der Turm dran
                                    bleibende_zuege = zuege; //Und die Bauern haben wieder volle belibende Zugzahl
                                }
                                b = null; //Nach dem Zug muss der menschliche Spieler erneut einen Bauern auswählen
                            } else if (brett.bauern.raster[q.x][q.y]) { //Sollte sich an der gewünschten Position ein Bauer befinden, c also "Falsch" sein, 
                                b = q; //So ist dieser nun ausgewählt
                            }
                        }
                    } else if (turmal) { //Sonst ist der Turm dran, falls er vom Rechner gesteuert werden soll...
                        Punkt bewegen = TurmAL.zieheTurm(brett.bauern, brett.turm); //Turm ziehen, gibt neue Position des Turmes zurück
                        brett.bewegeTurm(bewegen); //Brett aktualisieren
                        bauern_dran = true; //Danach sind wieder die Bauern dran
                    } else { //...und ansonsten steuert der Spieler den Turm
                        Punkt p = new Punkt((byte) (maus.mouseX / 50), (byte) (maus.mouseY / 50));
                        Punktmenge begehbar = TurmAL.bestimmeFelder(brett.bauern, brett.turm); //Alle für den Turm/die Dame begehbare Felder
                        if (begehbar.raster[p.x][p.y]) { //Ist das ausgewählte Feld begehbar ?
                            brett.bewegeTurm(p); //Wird er dahinbewegt
                            bauern_dran = true; //Danach sind wieder die Bauern dran
                        }
                    }
                    brett.repaint(); //Brett nach jedem Zug neuzeichnen
                    maus.LMB = 0;
                }
                now = System.currentTimeMillis();
                System.gc(); //"Müll" sammeln : Gibt Speicher frei
            }
        }
    }
}
