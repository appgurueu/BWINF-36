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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author lars
 */
public class Maus implements MouseListener {
    public int mouseX; //Speichert die X-Position der Maus
    public int mouseY; //Speichert die Y-Position der Maus
    public int LMB; //Speichert, ob die linke Maustaste gedrückt worden ist
    public int RMB; //Speichert, ob die rechte Maustaste gedrückt worden ist
    public Maus() {
        RMB=0;
        LMB=0;
        Bauernopfer.fenster.requestFocus(); //Alle Maus-Ereignisse von diesem Fenster abfangen lassen
    }
    @Override
    public void mousePressed(MouseEvent m) { //Mausknopf gedrückt
        if (m.getButton() == MouseEvent.BUTTON3) { //Rechte Maustaste
            RMB=1;
        }
        if (m.getButton() == MouseEvent.BUTTON1) { //Linke Maustaste
            LMB=1;
        }
    }
    @Override
    public void mouseClicked(MouseEvent m) { //Mausknopf geklickt
        if (m.getButton() == MouseEvent.BUTTON3) { //Rechte Maustaste
            RMB=2;
        }
        if (m.getButton() == MouseEvent.BUTTON1) {
            LMB=2;
        }
    }
    @Override
    public void mouseReleased(MouseEvent m) { //Mausknopf losgelassen
        if (m.getButton() == MouseEvent.BUTTON3) { //Rechte Maustaste
            RMB=3;
        }
        if (m.getButton() == MouseEvent.BUTTON1) { //Linke Maustaste
            LMB=3;
        }
    }
    @Override
    public void mouseEntered(MouseEvent m) {
    }
    @Override
    public void mouseExited(MouseEvent m) {
    }
    public void infosHolen() {
        Bauernopfer.fenster.requestFocus(); //Alle Maus-Ereignisse von diesem Fenster abfangen lassen
        try {
        //Versucht, die Maus-Koordinaten relativ zum Fenster zu erfahren, erzeugt einen Fehler, wenn die Maus außerhalb des Fensters ist
        mouseX=Bauernopfer.fenster.getMousePosition().x;
        mouseY=Bauernopfer.fenster.getMousePosition().y;
        } catch (Exception e) {}
    }
}

