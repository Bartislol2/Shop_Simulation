package pl.gawryszewski.pw_projekt;

import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Sklep {
    private int ile_max;
    private int ile_kas;
    private int ile_klientow;
    private int licznik =0;
    private Kasa[] kasy;
    private static Semaphore Klienci_max;
    ArrayList<Semaphore> wejscie1 = new ArrayList<Semaphore>();
    ArrayList<Semaphore> kasa_semafor = new ArrayList<Semaphore>();
    private final Semaphore kasa_chron = new Semaphore(1);
    Random random = new Random();
    public Sklep(int ile, Kasa[] kasy, int ile_kas, int ile_klientow)
    {
        this.ile_max = ile;
        this.kasy = kasy;
        this.ile_kas = ile_kas;
        this.ile_klientow = ile_klientow;
        Klienci_max = new Semaphore(ile_max);
        for(int i=0; i<ile_kas; i++)
        {
            kasa_semafor.add(new Semaphore(0));
        }
        for(int i=0; i<ile_klientow; i++)
        {
            wejscie1.add(new Semaphore(1));
        }
    }
    public void klient(Klient klient) throws InterruptedException
    {
        while(true)
        {
            Circle circle = new Circle(300+50*(klient.getNumer()), 400, 20);
            circle.setFill(Color.RED);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(2);
            //sprawy wlasne
            Thread.sleep(random.nextInt(4000)+1000);
            try {
                wejscie1.get(klient.getNumer()).acquire();
                Klienci_max.acquire();
                ArrayList<Kasa> wybor = new ArrayList<>();
                for (int i=0; i<kasy.length; i++)
                {
                    if(!kasy[i].przerwa)
                    {
                        wybor.add(kasy[i]);
                    }
                }
                if (wybor.size()==0){
                    Klienci_max.release();
                    wejscie1.get(klient.getNumer()).release();
                }
                else {
                    licznik++;
                    kasa_chron.acquire();
                    int indeks = 0;
                    for(int i=1; i<wybor.size(); i++)
                    {
                        if(wybor.get(indeks).kolejka.size()>wybor.get(i).kolejka.size())
                        {
                            indeks = i;
                        }
                    }
                    kasa_chron.release();
                    wybor.get(indeks).kolejka.add(klient);
                    int numer = wybor.get(indeks).getNumber();
                    Platform.runLater(() -> {
                        HelloApplication.root.getChildren().add(circle);
                    });
                    MoveTo mov = new MoveTo();
                    mov.setX(circle.getCenterX());
                    mov.setY(circle.getCenterY());
                    LineTo line = new LineTo();
                    line.setX(323 + numer * 100);
                    line.setY(100);
                    Path path = new Path();
                    path.getElements().addAll(mov, line);
                    PathTransition pt = new PathTransition(Duration.millis(1000), path, circle);
                    final int il = indeks;
                    pt.setOnFinished(e -> {
                        synchronized (this) {
                            wybor.get(il).text.setText("" + wybor.get(il).kolejka.size() + "");
                            notify();
                        }
                    });
                    Platform.runLater(() -> {
                        pt.play();
                    });
                    synchronized (this) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Platform.runLater(() -> {
                        HelloApplication.root.getChildren().remove(circle);
                    });
                    System.out.println("[" + Thread.currentThread().getName() + "] >> [" + wybor.get(indeks).getName() + "]");
                    System.out.println("Liczba klientow w sklepie: " + licznik);
                    kasa_semafor.get(wybor.get(indeks).getNumber()).release();
                }
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void kasa(Kasa kasa) throws InterruptedException
    {
        while(true)
        {
            if(kasa.przerwa&&kasa.kolejka.size()==0)
            {
                System.out.println(Thread.currentThread().getName()+" przerwa");
                kasa.text.setText("X");
                Thread.sleep(10000);
                kasa.obsluzeni = 0;
                System.out.println(Thread.currentThread().getName()+" koniec przerwy");
                kasa.text.setText("0");
                kasa.przerwa = false;
            }
            else
            {
            Thread.sleep(random.nextInt(5000)+2000);
            try {
                kasa_semafor.get(kasa.getNumber()).acquire();
                System.out.println(Thread.currentThread().getName()+" liczba oczekujacych: "+kasa.kolejka.size());
                System.out.println("["+Thread.currentThread().getName()+"] << ["+kasa.kolejka.peek().getNazwa()+"]");
                Circle circle = new Circle(323+100*(kasa.getNumber()), 100, 20);
                circle.setFill(Color.RED);
                circle.setStroke(Color.BLACK);
                circle.setStrokeWidth(2);
                Platform.runLater(() -> {
                    HelloApplication.root.getChildren().add(circle);
                });
                MoveTo mov = new MoveTo();
                mov.setX(circle.getCenterX());
                mov.setY(circle.getCenterY());
                LineTo line = new LineTo();
                line.setX(323+kasa.getNumber()*100);
                line.setY(0);
                Path path = new Path();
                path.getElements().addAll(mov, line);
                PathTransition pt = new PathTransition(Duration.millis(1000), path, circle);
                pt.setOnFinished(e-> {kasa.text.setText(""+(kasa.kolejka.size()-1)+""); synchronized (this) {notify();}});
                Platform.runLater(()->{pt.play();});
                synchronized (this){
                    try{
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Platform.runLater(() -> {
                    HelloApplication.root.getChildren().remove(circle);
                });
                wejscie1.get(kasa.kolejka.peek().getNumer()).release();
                kasa.kolejka.poll();
                licznik--;
                Klienci_max.release();
                if(kasa.obsluzeni%kasa.kiedy_przerwa==kasa.kiedy_przerwa-1)
                 {
                    kasa.przerwa = true;
                 }
                 else if(!kasa.przerwa)
                 {
                    kasa.obsluzeni++;
                 }
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            }
        }
    }
}

