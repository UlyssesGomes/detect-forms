package edu.detectforms;

import edu.detectforms.util.ShapeDetectionUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.opencv.core.Mat;
import org.opencv.core.Point;

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
    }

    @FXML
    private void handleMouseMove(MouseEvent event) {
        Mat image = shapeDetectionUtil.getMatFrame();
        if (image != null) {
            Point virtualPosition = getMouseVirtualPosition(image, event);
            labelPosition.setText("W: " + image.width() + "\nH: " + image.height() +  "\nX: " + virtualPosition.x + "\nY: " + virtualPosition.y);
        }
    }

    private Point getMouseVirtualPosition(Mat image, MouseEvent event) {
        double X = (event.getX() * image.width()) / topImageView.getLayoutBounds().getWidth();
        double Y = (event.getY() * image.height()) /topImageView.getLayoutBounds().getHeight();

        return new Point(X, Y);
    }


    private String getFullFilePath(String path) {
        return getClass().getResource(path).getFile();
    }

    public void openImage(File file) {
        Image image = new Image(file.toURI().toString());
        topImageView.setImage(image);

        shapeDetectionUtil.openMatImage(file);
        shapeDetectionUtil.drawImage(topImageView, bottomImageView);
    }
}
