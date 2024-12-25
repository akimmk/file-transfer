module app {
    exports org.example;

    opens org.example.controllers to javafx.fxml;

    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
}
