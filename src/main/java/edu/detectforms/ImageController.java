package edu.detectforms;

import edu.detectforms.util.ShapeDetectionUtil;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
    @FXML
    private Label labelPosition;

    private ShapeDetectionUtil shapeDetectionUtil;

    @FXML
    private void initialize() {
        topImageView.fitHeightProperty().bind(topPane.heightProperty());
        bottomImageView.fitHeightProperty().bind(bottomPane.heightProperty());

        shapeDetectionUtil = new ShapeDetectionUtil();
        System.out.println("set on mouse move");
//        topPane.setOnMouseMoved(event -> handleMouseMove(event));
//        topPane.setOnMouseClicked(event -> handleMouseMove(event));
//        topImageView.addEventHandler(MouseEvent.MOUSE_PRESSED,
//                new EventHandler<MouseEvent>() {
//                    @Override
//                    public void handle(MouseEvent mouseEvent) {
//                        System.out.println("clicou");
//                    }
//                });
    }

    @FXML
    private void handleMouseMove(MouseEvent event) {
        Mat image = shapeDetectionUtil.getMatFrame();
        labelPosition.setText("W: " + image.width() + "\nH: " + image.height() +  "\nX: " + event.getX() + "\nY: " + event.getY());
//        topPane.setVisible(false);
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
