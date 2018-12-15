package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.corningrobotics.enderbots.endercv.CameraViewDisplay;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.teamcode.GoldManager;
import org.firstinspires.ftc.teamcode.Projects.Project0;
import org.firstinspires.ftc.teamcode.Projects.Project2;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.List;

@Autonomous(name="GoldHANGAuto", group="Production")
public class GoldHANGAuto extends LinearOpMode {
    private GoldManager goldManager;
    private final int cutOff = 1000;
    private ElapsedTime eTime = new ElapsedTime();

    @Override
    public void runOpMode() {
        Project2 robot = new Project2();
        robot.init(hardwareMap);

        robot.liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.liftMotor.setPower(1f);
        robot.liftMotor.setTargetPosition(robot.liftMotor.getCurrentPosition());

        goldManager = new GoldManager();

        goldManager.init(hardwareMap.appContext, CameraViewDisplay.getInstance());
        goldManager.setShowContours(true);
        goldManager.enable();
        waitForStart();

        robot.liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        robot.liftMotor.setPower(1);
        sleep(2000);
        robot.liftMotor.setPower(0);
        sleep(1000);
        robot.rightMotor.setPower(-.4f);
        robot.leftMotor.setPower(.4f);
        sleep(500);
        robot.rightMotor.setPower(.2f);
        robot.leftMotor.setPower(-.2f);
        sleep(300);

        eTime.reset();

        while(opModeIsActive() && eTime.time() < 2.5) {
            goldManager.setShowThreshold(gamepad1.x);

            //List of Contours of detected gold
            List<MatOfPoint> contours = goldManager.getContours();

            //target direction: -1 left 0 straight 1 right
            int location = 0;

            VectorF vectorF;
            //Index of Best Fit is a measure of the contour index which has the highest fitness
            int indexofBestFit = 0;
            double topFitness = 0;
            Rect bestRect = new Rect(0, 0, 1, 1);
            //Fitness Algorithm
            for (int i = 0; i < contours.size(); i++) {
                Rect boundingRect = Imgproc.boundingRect(contours.get(i));
                double fitness = boundingRect.size().area();
                vectorF = new VectorF(boundingRect.x + boundingRect.width / 2f, boundingRect.y + boundingRect.height / 2f);
                if(vectorF.get(0) > goldManager.hsv.cols() / 2){
                    fitness = 0;
                }
                if(fitness > topFitness){
                    indexofBestFit = i;
                    topFitness = fitness;
                    //rectangle with the best fitness
                    bestRect = boundingRect;
                }
            }
            vectorF = new VectorF(bestRect.x + bestRect.width / 2f, bestRect.y + bestRect.height / 2f);

            int screenThird = goldManager.hsv.rows() / 3;
            if (vectorF.get(1) < screenThird) {
                location = -1;
            }
            else if (vectorF.get(1) > 2 * screenThird) {
                location = 1;
            }

            telemetry.addData("Rows", goldManager.hsv.rows());
            telemetry.addData("Cols", goldManager.hsv.cols());
            telemetry.addData("Loc", location);
            telemetry.update();

            if(location == -1){
                robot.leftMotor .setPower(.7);
                robot.rightMotor.setPower(-.7);
            }else if(location == 1){
                robot.leftMotor .setPower(-.7);
                robot.rightMotor.setPower(.7);
            }else{
                robot.leftMotor .setPower(-.2);
                robot.rightMotor.setPower(-.2);
            }
        }

        while(opModeIsActive() && eTime.time() < 5.5) {
            robot.leftMotor .setPower(-.6);
            robot.rightMotor.setPower(-.6);
        }

        robot.leftMotor .setPower(0);
        robot.rightMotor.setPower(0);

        while(opModeIsActive() && eTime.time() < 7){
            robot.markerServo.setPosition(1);
        }
        sleep(1000);


        goldManager.disable();
    }
}
