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
    private int noiseValueFilter = 200;

    public void openMatImage(File file) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            frame = Imgcodecs.imread(file.getAbsolutePath());;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Mat processImage(Mat mat) {
        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);

        Imgproc.threshold(gray, gray, 100, 255, Imgproc.THRESH_BINARY);

        return gray;
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

    private void biggestContour(MatOfPoint contourProcessed, Mat originalImage) {

        int sum = 0;
        Point maxPoint = new Point(0.0, 0.0);
        Point minPoint = new Point( Double.MAX_VALUE,  Double.MAX_VALUE);
        if(contourProcessed != null) {
            for (Point p : contourProcessed.toArray()) {
                if (maxPoint.x < p.x)
                    maxPoint.x = p.x;

                if (maxPoint.y < p.y)
                    maxPoint.y = p.y;

                if (minPoint.x > p.x)
                    minPoint.x = p.x;

                if (minPoint.y > p.y)
                    minPoint.y = p.y;
            }
            System.out.println("max (" + maxPoint.x + ", " + maxPoint.y + ")");
            System.out.println("min (" + minPoint.x + ", " + minPoint.y + ")");
        }
    }

    private void markOuterContour(final Mat processedImage, final Mat originalImage) {
        // Find contours of an image
        final List<MatOfPoint> allContours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(
                processedImage,
                allContours,
                hierarchy,
                Imgproc.RETR_CCOMP,
                Imgproc.CHAIN_APPROX_SIMPLE
        );

        double value;
        Rect rect;
        MatOfPoint biggestContour = null;
        double contourValue = 0;
        for (int i = 0; i < allContours.size(); i++) {
            if (hierarchy.get(0, i)[3] != -1) {

                MatOfPoint contour = allContours.get(i);
                value = Imgproc.contourArea(contour);
                rect = Imgproc.boundingRect(contour);

                final boolean isNotNoise = value > noiseValueFilter;

                if (isNotNoise) {
                    // draw external shape
                    Imgproc.drawContours(originalImage, allContours, i, new Scalar(124, 252, 0), 2);
                    // draw info about current shape
                    drawInfo(originalImage, contour, value, rect, new Scalar(124, 252, 0));

                    value = Imgproc.contourArea(contour);
                    if (biggestContour == null)
                        biggestContour = contour;
                    else if (contourValue < value) {
                        contourValue = value;
                        biggestContour = contour;
                    }
                }
            }
        }

        System.out.println("Biggest area: " + contourValue);
        biggestContour(biggestContour, originalImage);
    }

    public BufferedImage convertMatToBufferedImage(Mat mat) {
        BufferedImage image = new BufferedImage(mat.width(), mat.height(),
                mat.channels() == 1 ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR);

        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        mat.get(0,0, dataBuffer.getData());

        return image;
    }

    // TODO - this method is not necessary now, but in the future implement convertion ABGR to BGRA
    public Mat bufferedImageToMat(BufferedImage image) {
        System.out.println("Number of bands: " + image.getRaster().getNumBands());
        switch(image.getType()) {
            case BufferedImage.TYPE_INT_RGB:
                System.out.println("tipo rgb");
                break;
            case BufferedImage.TYPE_INT_ARGB:
                System.out.println("tipo ARGB");

                break;
            case BufferedImage.TYPE_4BYTE_ABGR:
                System.out.println("tipo ABGR");
                break;
            case BufferedImage.TYPE_3BYTE_BGR:

                System.out.println("tipo bgr");
                break;
        }

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
