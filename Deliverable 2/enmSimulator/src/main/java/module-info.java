module com.example.fractal {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.desktop;


    opens com.example.fractal to javafx.fxml;
    exports com.example.fractal;
}