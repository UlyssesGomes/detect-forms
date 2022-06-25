package edu.detectforms;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RootController extends Controller {

    @FXML
    private BorderPane rootPane;

    private ImageController imageController;
    private FileChooser fileChooser;


    @FXML
    private void initialize() {
        configureFileChooser();

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApplication.class.getResource("ImageView.fxml"));
            AnchorPane pane = (AnchorPane) loader.load();
            rootPane.setCenter(pane);
            imageController = loader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void closeApplication() {
        MainApplication.getInstance().getPrimaryState().close();
    }

    @FXML
    public void openFile() throws FileNotFoundException {
        File file = fileChooser.showOpenDialog(MainApplication.getInstance().getPrimaryState());
        imageController.openImage(file);
    }

    private void configureFileChooser() {
        fileChooser = new FileChooser();
        fileChooser.setTitle("View Pictures");
//        fileChooser.setInitialDirectory(
//                new File(System.getProperty("user.home"))
//        );
    }
}
