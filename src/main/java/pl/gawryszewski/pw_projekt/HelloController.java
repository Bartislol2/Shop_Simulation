package pl.gawryszewski.pw_projekt;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class HelloController {
    public Button button;
    //public Button button1;
    public TextField text1 = new TextField();
    public TextField text2 = new TextField();
    public TextField text3 = new TextField();
    public TextField text4 = new TextField();
    Klient[] klienci;
    Kasa[] kasy;
    public void handleButtonClick()
    {
        int kasy_ilosc = Integer.parseInt(text1.getText());
        int klienci_ilosc = Integer.parseInt(text2.getText());
        int max_klientow = Integer.parseInt(text3.getText());
        int przerwa = Integer.parseInt(text4.getText());
        kasy = new Kasa[kasy_ilosc];
        klienci = new Klient[klienci_ilosc];
        Sklep sklep = new Sklep(max_klientow, kasy, kasy_ilosc, klienci_ilosc);
        for(int i=0; i<klienci_ilosc; i++)
        {
            klienci[i] = new Klient(i, sklep);
            Rectangle klient = new Rectangle(277+50*i, 378, 45, 45);
            klient.setFill(Color.GRAY);
            klient.setStroke(Color.BLACK);
            klient.setStrokeWidth(2);
            Platform.runLater(() -> {
                HelloApplication.root.getChildren().add(klient);
            });
        }
        for(int i=0; i<kasy_ilosc; i++)
        {
            kasy[i] = new Kasa(i, sklep, przerwa);
            kasy[i].text.setX(320+i*100);
            kasy[i].text.setY(105);
            kasy[i].text.setText("0");
            Rectangle kasa = new Rectangle(300+100*i, 78, 46, 46);
            kasa.setFill(Color.GRAY);
            kasa.setStroke(Color.BLACK);
            kasa.setStrokeWidth(2);
            final int il = i;
            Platform.runLater(() -> {
                HelloApplication.root.getChildren().add(kasa);
                HelloApplication.root.getChildren().add(kasy[il].text);
            });
        }
        for(int i=0; i<kasy_ilosc; i++)
        {
            kasy[i].start();
        }
        for(int i=0; i<klienci_ilosc; i++) {
            klienci[i].start();
        }
    }
}