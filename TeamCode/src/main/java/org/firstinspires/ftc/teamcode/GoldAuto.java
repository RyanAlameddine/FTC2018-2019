package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.corningrobotics.enderbots.endercv.CameraViewDisplay;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.List;
import java.util.Locale;

@TeleOp(name="Gold Auto", group="Test")
public class GoldAuto extends OpMode {
    private GoldManager goldManager;
    @Override
    public void init() {
        goldManager = new GoldManager();

        goldManager.init(hardwareMap.appContext, CameraViewDisplay.getInstance());
        goldManager.setShowCountours(true);
        goldManager.enable();
    }

    @Override
    public void loop() {
        //goldManager.setShowCountours(gamepad1.x);

        // get a list of contours from the vision system
        List<MatOfPoint> contours = goldManager.getContours();
        for (int i = 0; i < contours.size(); i++) {
            Rect boundingRect = Imgproc.boundingRect(contours.get(i));
            telemetry.addData("contour" + Integer.toString(i),
                    String.format(Locale.getDefault(), "(%d, %d)", (boundingRect.x + boundingRect.width) / 2, (boundingRect.y + boundingRect.height) / 2));
        }
    }

    public void stop() {
        goldManager.disable();
    }
}
