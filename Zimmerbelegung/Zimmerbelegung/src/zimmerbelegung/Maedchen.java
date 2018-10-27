/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zimmerbelegung;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author lars
 */
public class Maedchen {

    public List<String> freunde; //Liste von Freunden
    public List<String> feinde; //Liste von Feinden
    public byte zimmer; //Nummer des Zimmers

    public Maedchen(String[] friends, String[] enemies) { //Konstruktor
        this.freunde = Arrays.asList(friends);
        this.feinde = Arrays.asList(enemies);
        zimmer = -1; //Unverplante Mädchen haben -1 als Zimmernummer
    }
}
