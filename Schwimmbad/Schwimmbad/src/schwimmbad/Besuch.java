/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schwimmbad;
/**
 *
 * @author lars
 */
public class Besuch { //Besuch, speichert alles, was wir über einen Besuch wissen wollen

    public float preis;
    public int einzelkarten_kinder;
    public int einzelkarten_erwachsene;
    public int gutscheine_kinder;
    public int gutscheine_erwachsene;
    public boolean ermaessigung;
    public int[] familienkarten;
    public int tageskarten;

    public Besuch(int einzelkarten_erwachsene, int einzelkarten_kinder, int[] familienkarten, int tageskarten) {
        this.einzelkarten_kinder = einzelkarten_kinder;
        this.einzelkarten_erwachsene = einzelkarten_erwachsene;
        this.familienkarten=new int[4];
        System.arraycopy(familienkarten,0,this.familienkarten,0,4);
        this.tageskarten = tageskarten;
        float einzelkarten_preis=einzelkarten_kinder * 2.50f + einzelkarten_erwachsene * 3.50f;
        if (Schwimmbad.wochentag) { //An Wochentagen
            einzelkarten_preis*=0.8f; //Ist der Einzelkartenpreis um 20 % reduziert
        }
        this.preis = einzelkarten_preis  + (familienkarten[0] + familienkarten[1] + familienkarten[2] + familienkarten[3]) * 8.0f + tageskarten * 11.0f;
    }

    @Override
    public String toString() { //ALLES ausgeben
        String s = "";
        s+="Besuch beim Schwimmbad - Kosten";
        s+="\n";
        s+="Familienkarten(2 Erwachsene, 2 Kinder) : "+Integer.toString(familienkarten[0]);
        s+="\n";
        s+="Familienkarten(1 Erwachsener, 3 Kinder) : "+Integer.toString(familienkarten[1]);
        s+="\n";
        s+="Familienkarten(2 Erwachsene, 1 Kind) : "+Integer.toString(familienkarten[2]);
        s+="\n";
        s+="Familienkarten(1 Erwachsener, 2 Kinder) : "+Integer.toString(familienkarten[3]);
        s+="\n";
        s+="Tageskarten : "+Integer.toString(tageskarten);
        s+="\n";
        s+="Einzelkarten Erwachsene : "+Integer.toString(einzelkarten_erwachsene);
        s+="\n";
        s+="Einzelkarten Kinder : "+Integer.toString(einzelkarten_kinder);
        s+="\n";
        s+="Es werden "+Integer.toString(gutscheine_erwachsene)+" Gutscheine für den freien Eintritt von Erwachsenen benutzt.";
        s+="\n";
        s+="Es werden "+Integer.toString(gutscheine_kinder)+" Gutscheine für den freien Eintritt von Kindern benutzt.";
        s+="\n";
        s+="Es wird ";
        if (ermaessigung) {
            s+="ein";
        }
        else {
            s+="kein";
        }
        s+=" Gutschein für die Ermäßigung von 10 % eingesetzt.";
        s+="\n";
        s+="Preis : "+Float.toString(preis);
        return s;
    }
    
    public void setErmaessigung() {
        ermaessigung=true;
        this.preis*=0.9f; //-10 %
    }
}
