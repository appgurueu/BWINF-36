/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dreieckezaehlen;

/**
 *
 * @author lars
 */
public class Rechteck {
    public Dezimalzahl x;
    public Dezimalzahl y;
    public Dezimalzahl w;
    public Dezimalzahl h;
    public Punkt[] eckpunkte;
    public Rechteck(Dezimalzahl x, Dezimalzahl y, Dezimalzahl w, Dezimalzahl h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        eckpunkte=new Punkt[] {
          new Punkt(x,y),
          new Punkt(x.plus(w),y),
          new Punkt(x.plus(w),y.plus(h)),
          new Punkt(x,y.plus(h)),
        };
    }
    public Rechteck(Punkt a, Punkt b) {
        this.w=Dezimalzahl.abs(a.x.minus(b.x));
        this.h=Dezimalzahl.abs(a.y.minus(b.y));
        this.x=Dezimalzahl.min(a.x,b.x);
        this.y=Dezimalzahl.min(a.y,b.y);
        eckpunkte=new Punkt[] {
          new Punkt(x,y),
          new Punkt(x.plus(w),y),
          new Punkt(x.plus(w),y.plus(h)),
          new Punkt(x,y.plus(h)),
        };
    }
    public boolean schneidetPunkt(Punkt p) {
        return p.x.gg(eckpunkte[0].x) && p.x.kg(eckpunkte[1].x) && p.y.gg(eckpunkte[0].y) && p.y.kg(eckpunkte[3].y);
    }
    @Override
    public String toString() {
        String s="X : "+x.toString()+"Y : "+y.toString()+" W : "+w.toString()+" H : "+h.toString();
        return s;
    }
}
