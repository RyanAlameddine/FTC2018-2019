package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.corningrobotics.enderbots.endercv.CameraViewDisplay;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.teamcode.GoldManager;
import org.firstinspires.ftc.teamcode.Projects.Project0;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.List;

@Autonomous(name="GoldAuto", group="Production")
public class GoldAuto extends LinearOpMode {
    private GoldManager goldManager;
    private final int cutOff = 1000;
    private ElapsedTime eTime = new ElapsedTime();

    @Override
    public void runOpMode() {
        Project0 robot = new Project0();
        robot.init(hardwareMap);
        goldManager = new GoldManager();

        goldManager.init(hardwareMap.appContext, CameraViewDisplay.getInstance());
        goldManager.setShowContours(true);
        goldManager.enable();

        waitForStart();
        eTime.reset();

        while(opModeIsActive() && eTime.time() < 3.5) {
            goldManager.setShowThreshold(gamepad1.x);

            //List of Contours of detected gold
            List<MatOfPoint> contours = goldManager.getContours();

            //target direction: -1 left 0 straight 1 right
            int location = 0;

            VectorF vectorF;
            //ioBF
            int indexofBestFit = 0;
            double topFitness = 0;
            Rect bestRect = new Rect(0, 0, 1, 1);
            for (int i = 0; i < contours.size(); i++) {

                Rect boundingRect = Imgproc.boundingRect(contours.get(i));
                if(boundingRect.size().area() > topFitness){
                    indexofBestFit = i;
                    topFitness = boundingRect.size().area();
                    //rectangle with the best fitness
                    bestRect = boundingRect;
                }
            }
            vectorF = new VectorF(bestRect.x + bestRect.width / 2f, bestRect.y + bestRect.height / 2f);

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

            if(location == -1){
                robot.leftMotor.setPower(.6);
                robot.rightMotor.setPower(-.6);
            }else if(location == 1){
                robot.leftMotor.setPower(-.6);
                robot.rightMotor.setPower(.6);
            }else{
                robot.leftMotor.setPower(-.6);
                robot.rightMotor.setPower(-.6);
            }
        }

        robot.leftMotor.setPower(0);
        robot.rightMotor.setPower(0);

        goldManager.disable();
    }
}
