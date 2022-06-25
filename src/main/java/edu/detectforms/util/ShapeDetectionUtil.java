package edu.detectforms.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShapeDetectionUtil {
    private Mat frame;

    public void openMatImage(File file) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            frame = bufferedImageToMat(bufferedImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Mat processImage(Mat mat) {
        final Mat processed = new Mat(mat.height(), mat.width(), mat.type());
        // Blur an image using a Gaussian filter
        Imgproc.GaussianBlur(mat, processed, new Size(7, 7), 1);

        // Switch from RGB to GRAY
        Imgproc.cvtColor(processed, processed, Imgproc.COLOR_RGB2GRAY);

        // Find edges in an image using the Canny algorithm
        Imgproc.Canny(processed, processed, 200, 25);

        // Dilate an image by using a specific structuring element
        // https://en.wikipedia.org/wiki/Dilation_(morphology)
        Imgproc.dilate(processed, processed, new Mat(), new Point(-1, -1), 1);

        return processed;
    }

    private void drawInfo(Mat originalImage, MatOfPoint contour, double value, Rect  rect, Scalar scalar) {
        Imgproc.putText (
                originalImage,
                "Area: " + (int) value,
                new Point(rect.x, rect.y + rect.height +15),
                2,
                0.5,
                scalar, //new Scalar(124, 252, 0),
                1
        );

        MatOfPoint2f dst = new MatOfPoint2f();
        contour.convertTo(dst, CvType.CV_32F);
        Imgproc.approxPolyDP(dst, dst, 0.02 * Imgproc.arcLength(dst, true), true);
        Imgproc.putText (
                originalImage,
                "Points: " + dst.toArray().length,
                new Point(rect.x, rect.y + rect.height + 30),
                2,
                0.5,
                scalar, //new Scalar(124, 252, 0),
                1
        );
    }

    private void findInnerContour(MatOfPoint contourProcessed, Mat originalImage) {

//        byte [] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
//        Mat frame = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
//        frame.put(0, 0, data);
//        return frame;

        int sum = 0;
        for(Point p : contourProcessed.toArray()) {
            System.out.println((sum++) + " Point: " + p.toString());
        }

//        Mat proc2 = new Mat(contourProcessed.size(), CvType.CV_8UC3);
//        proc2.put(0, 0, contourProcessed.getData());
//        // Find contours of an image
//        final List<MatOfPoint> allContours = new ArrayList<>();
//        Imgproc.findContours(
//                contourProcessed,
//                allContours,
//                new Mat(contourProcessed.size(), contourProcessed.type()),
//                Imgproc.RETR_EXTERNAL,
//                Imgproc.CHAIN_APPROX_NONE
//        );
//
//        // Filter out noise and display contour area value
//        final List<MatOfPoint> filteredContours = allContours.stream()
//                .filter(contour -> {
//                    final double value = Imgproc.contourArea(contour);
//                    final Rect rect = Imgproc.boundingRect(contour);
//
//                    final boolean isNotNoise = value > 100;
//
//                    if (isNotNoise) {
//                        drawInfo(originalImage, contour, value, rect, new Scalar(124, 0, 252));
//                    }
//
//                    return isNotNoise;
//                }).collect(Collectors.toList());
//
//        // Mark contours
//        Imgproc.drawContours(
//                originalImage,
//                filteredContours,
//                -1, // Negative value indicates that we want to draw all of contours
//                new Scalar(124, 0, 252), // Green color
//                2 // line width
//        );
    }

    private void markOuterContour(final Mat processedImage, final Mat originalImage) {
        // Find contours of an image
        final List<MatOfPoint> allContours = new ArrayList<>();
        Imgproc.findContours(
                processedImage,
                allContours,
                new Mat(processedImage.size(), processedImage.type()),
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_NONE
        );


        // Filter out noise and display contour area value
        final List<MatOfPoint> filteredContours = allContours.stream()
                .filter(contour -> {
                    final double value = Imgproc.contourArea(contour);
                    final Rect rect = Imgproc.boundingRect(contour);

                    final boolean isNotNoise = value > 5000;

                    if (isNotNoise) {
                        drawInfo(originalImage, contour, value, rect, new Scalar(124, 252, 0));
                    }

                    return isNotNoise;
                }).collect(Collectors.toList());

        MatOfPoint biggestContour = null;
        double contourValue = 0;
        for(MatOfPoint contour : filteredContours) {
            double value = Imgproc.contourArea(contour);
            if (biggestContour == null)
                biggestContour = contour;
            else if (contourValue < value) {
                contourValue = value;
                biggestContour = contour;
            }
        }

        System.out.println("Biggest area: " + contourValue);
        findInnerContour(biggestContour, originalImage);

        // Mark contours
        Imgproc.drawContours(
                originalImage,
                filteredContours,
                -1, // Negative value indicates that we want to draw all of contours
                new Scalar(124, 252, 0), // Green color
                2 // line width
        );
    }

    public BufferedImage convertMatToBufferedImage(Mat mat) {
        BufferedImage image = new BufferedImage(mat.width(), mat.height(),
                mat.channels() == 1 ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR);

        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        mat.get(0,0, dataBuffer.getData());

        return image;
    }

    public Mat bufferedImageToMat(BufferedImage image) {
        byte [] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat frame = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        frame.put(0, 0, data);
        return frame;
    }

    public void drawImage(ImageView originalView, ImageView processedView) {
        Mat processed = processImage(frame);
        markOuterContour(processed, frame);

        MatOfByte byteMat = new MatOfByte();
        Imgcodecs.imencode(".bmp", frame, byteMat);
        originalView.setImage(new Image(new ByteArrayInputStream(byteMat.toArray())));

        byteMat = new MatOfByte();
        Imgcodecs.imencode(".bmp", processed, byteMat);
        processedView.setImage(new Image(new ByteArrayInputStream(byteMat.toArray())));
    }
}
