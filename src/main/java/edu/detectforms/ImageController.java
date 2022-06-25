package edu.detectforms;

import edu.detectforms.util.ShapeDetectionUtil;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.opencv.core.Mat;

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

    private ShapeDetectionUtil shapeDetectionUtil;

    @FXML
    private void initialize() {
        topImageView.fitHeightProperty().bind(topPane.heightProperty());
        bottomImageView.fitHeightProperty().bind(bottomPane.heightProperty());

        shapeDetectionUtil = new ShapeDetectionUtil();
    }

    private String getFullFilePath(String path) {
        return getClass().getResource(path).getFile();
    }

    public void openImage(File file) throws FileNotFoundException {
        Image image = new Image(file.toURI().toString());
        topImageView.setImage(image);

        shapeDetectionUtil.openMatImage(file);
        shapeDetectionUtil.drawImage(topImageView, bottomImageView);
    }
}
