module edu.detectforms {
    requires javafx.controls;
    requires javafx.fxml;
    requires opencv;
    requires transitive java.desktop;

    opens edu.detectforms to javafx.fxml;
    exports edu.detectforms;
}