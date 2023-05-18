package pl.gawryszewski.pw_projekt;

public class Klient extends Thread{
    private int nr;
    private Sklep sklep;
    public Klient(int nr, Sklep sklep)
    {
        super("Klient-"+nr);
        this.nr = nr;
        this.sklep = sklep;
    }
    public String getNazwa()
    {
        String name = getName();
        return name;
    }
    public int getNumer()
    {
        int numer = this.nr;
        return numer;
    }
    @Override
    public void run()
    {
        try {
            sklep.klient(this);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

