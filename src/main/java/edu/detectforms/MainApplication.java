package edu.detectforms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;


// Apagar....
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class MainApplication extends Application {

    private static MainApplication mainApplication;
    private Stage stage;
    private FXMLLoader fxmlLoader;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        mainApplication = this;
        fxmlLoader = new FXMLLoader();

        stage.setTitle("DetectFormOpenCV");
        stage.getIcons().add(new Image("file:resources/icons/icon2.png"));

        loadRootScene();

        //AnchorPane anchorPane = fxmlLoader.load(MainApplication.class.getResource());
    }

    public static MainApplication getInstance() {
        return mainApplication;
    }

    private void loadRootScene() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApplication.class.getResource("RootView.fxml"));
        BorderPane rootPane = (BorderPane) loader.load();
        Scene scene = new Scene(rootPane);
        stage.setScene(scene);
        stage.show();
    }

    public Stage getPrimaryState() { return stage; }

    public static void main(String[] args) {
        //OpenCV.loadShared();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
        System.out.println("mat = " + mat.dump());
        System.out.println(Core.VERSION);
        launch();
    }
}