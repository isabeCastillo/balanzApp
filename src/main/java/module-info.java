module com.example.balanzapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.balanzapp to javafx.fxml;
    exports com.example.balanzapp;
}