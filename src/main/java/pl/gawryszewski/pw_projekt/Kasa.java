package pl.gawryszewski.pw_projekt;

import javafx.scene.text.Text;

import java.util.LinkedList;
import java.util.Queue;

public class Kasa extends Thread{
    private int nr;
    public Text text;
    private int rep;
    private Sklep sklep;
    public int kiedy_przerwa;
    public Queue<Klient> kolejka = new LinkedList<Klient>();
    public boolean przerwa = false;
    public int obsluzeni = 0;
    public Kasa(int nr, Sklep sklep, int przerwa)
    {
        super("Kasa-"+nr);
        this.nr = nr;
        this.sklep = sklep;
        this.text = new Text();
        this.kiedy_przerwa = przerwa;
    }
    public int getNumber()
    {
        int number = this.nr;
        return number;
    }
    @Override
    public void run()
    {
        try {
            sklep.kasa(this);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

