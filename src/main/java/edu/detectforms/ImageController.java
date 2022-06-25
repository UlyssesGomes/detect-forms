package edu.detectforms;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.File;
import java.io.FileNotFoundException;

public class ImageController extends Controller {

    @FXML
    private AnchorPane topPane;
    @FXML
    private ImageView topImageView;

    @FXML
    private AnchorPane bottomPane;
    @FXML
    private ImageView bottomImageView;

    @FXML
    private void initialize() {
        topImageView.fitHeightProperty().bind(topPane.heightProperty());
        bottomImageView.fitHeightProperty().bind(bottomPane.heightProperty());
    }

    private String getFullFilePath(String path) {
        return getClass().getResource(path).getFile();
    }

    public void openImage(File file) throws FileNotFoundException {
        Image image = new Image(file.toURI().toString());
        topImageView.setImage(image);
    }
}
