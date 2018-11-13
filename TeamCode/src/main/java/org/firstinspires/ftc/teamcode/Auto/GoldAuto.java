package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.corningrobotics.enderbots.endercv.CameraViewDisplay;
import org.corningrobotics.enderbots.endercv.OpenCVPipeline;
import org.firstinspires.ftc.teamcode.GoldManager;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.List;
import java.util.Locale;

@Autonomous(name="GoldAuto", group="Production")
public class GoldAuto extends OpMode {
    private GoldManager goldManager;
    private final int cubeSize = 100;

    @Override
    public void init() {
        goldManager = new GoldManager();

        goldManager.init(hardwareMap.appContext, CameraViewDisplay.getInstance());
        goldManager.setShowCountours(true);
        goldManager.enable();
    }

    @Override
    public void loop() {
        goldManager.setShowThreshhold(gamepad1.x);

        //List of Contours of detected gold
        List<MatOfPoint> contours = goldManager.getContours();

        //Index of contour that best fits expected result;
        int IxOfBestFit = 0;

        //Error of best fit contour
        double lowestError = Integer.MAX_VALUE;

        for (int i = 0; i < contours.size(); i++) {
            //Rect boundingRect = Imgproc.boundingRect(contours.get(i));


            double error = Math.abs(cubeSize - Imgproc.contourArea(contours.get(i)));

            if(error < lowestError){
                lowestError = error;
                IxOfBestFit = i;
            }
            //telemetry.addData("contour" + Integer.toString(i), String.format(Locale.getDefault(), "(%d, %d)", (boundingRect.x + boundingRect.width) / 2, (boundingRect.y + boundingRect.height) / 2));
        }

        Rect boundingRect = Imgproc.boundingRect(contours.get(IxOfBestFit));
        telemetry.addData("contour", boundingRect.x + ", " + boundingRect.y);
    }

    public void stop() {
        goldManager.disable();
    }
}
