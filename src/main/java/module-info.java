module com.example.balanzapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires javafx.base;


    opens com.example.balanzapp to javafx.fxml;
    exports com.example.balanzapp;
    exports com.example.balanzapp.controllers;
    opens com.example.balanzapp.controllers to javafx.fxml;
}