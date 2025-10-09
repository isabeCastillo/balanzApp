module com.example.balanzapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.balanzapp to javafx.fxml;
    exports com.example.balanzapp;
}