package org.firstinspires.ftc.teamcode;

import org.corningrobotics.enderbots.endercv.OpenCVPipeline;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class GoldManager extends OpenCVPipeline {
    private boolean showContours = true;
    private boolean showThreshhold = false;

    private Mat hsv = new Mat();
    private Mat thresholded = new Mat();

    private List<MatOfPoint> contours = new ArrayList<>();

    public synchronized void setShowCountours(boolean enabled) {
        showContours = enabled;
    }
    public synchronized void setShowThreshhold(boolean enabled) {
        showThreshhold = enabled;
    }
    public synchronized List<MatOfPoint> getContours() {
        return contours;
    }

    // Must be called every frame
    @Override
    public Mat processFrame(Mat rgba, Mat gray) {
        // RGBA to HSV, because HSV is much easier to detect color with
        Imgproc.cvtColor(rgba, hsv, Imgproc.COLOR_RGB2HSV, 3);


        Imgproc.blur(hsv, hsv, new Size(26, 26));

        Core.inRange(hsv, new Scalar(2, 188, 85), new Scalar(35, 255, 255), thresholded);

        //TODO detect shape?


        contours = new ArrayList<>();

        Imgproc.findContours(thresholded, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        if (showContours) {
            Imgproc.drawContours(rgba, contours, -1, new Scalar(0, 0, 255), 3, 8);
        }
        if(showThreshhold){
            return thresholded;
        }
        return rgba;
    }
}
