module edu.detectforms {
    requires javafx.controls;
    requires javafx.fxml;
    requires opencv;


    opens edu.detectforms to javafx.fxml;
    exports edu.detectforms;
}