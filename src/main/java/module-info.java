module com.example.balanzapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires javafx.base;
    requires itextpdf;
    requires java.desktop;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;


    opens com.example.balanzapp to javafx.fxml;
    opens com.example.balanzapp.models to javafx.base;
    exports com.example.balanzapp;
    exports com.example.balanzapp.controllers;
    opens com.example.balanzapp.controllers to javafx.fxml;
}