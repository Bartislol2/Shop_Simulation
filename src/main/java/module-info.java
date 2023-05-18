module com.example.fxmltest {
    requires javafx.controls;
    requires javafx.fxml;


    opens pl.gawryszewski.pw_projekt to javafx.fxml;
    exports pl.gawryszewski.pw_projekt;
}