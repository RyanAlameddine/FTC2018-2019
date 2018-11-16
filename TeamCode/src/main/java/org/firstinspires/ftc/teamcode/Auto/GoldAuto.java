package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.corningrobotics.enderbots.endercv.CameraViewDisplay;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.teamcode.GoldManager;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.List;

@Autonomous(name="GoldAuto", group="Production")
public class GoldAuto extends LinearOpMode {
    private GoldManager goldManager;
    private final int cutOff = 1000;

    @Override
    public void runOpMode() {
        goldManager = new GoldManager();

        goldManager.init(hardwareMap.appContext, CameraViewDisplay.getInstance());
        goldManager.setShowContours(true);
        goldManager.enable();

        waitForStart();

        while(opModeIsActive()) {
            goldManager.setShowThreshold(gamepad1.x);

            //List of Contours of detected gold
            List<MatOfPoint> contours = goldManager.getContours();

            //target direction: -1 left 0 straight 1 right
            int location = 0;

            VectorF vectorF = new VectorF(0, 0);
            for (int i = 0; i < contours.size(); i++) {
                Rect boundingRect = Imgproc.boundingRect(contours.get(i));
                VectorF temp = new VectorF(boundingRect.x + boundingRect.width / 2f, boundingRect.y + boundingRect.height / 2f);
                vectorF = new VectorF(temp.get(0) + vectorF.get(0), temp.get(1) + vectorF.get(1));
            }
            vectorF = new VectorF(vectorF.get(0) / contours.size(), vectorF.get(1) / contours.size());

            int screenThird = goldManager.hsv.cols() / 3;
            if (vectorF.get(0) < screenThird) {
                location = -1;
            }
            if (vectorF.get(0) > 2 * screenThird) {
                location = 1;
            }

            telemetry.addData("Rows", goldManager.hsv.rows());
            telemetry.addData("Loc", location);
            telemetry.update();
        }

        goldManager.disable();

    }
}
