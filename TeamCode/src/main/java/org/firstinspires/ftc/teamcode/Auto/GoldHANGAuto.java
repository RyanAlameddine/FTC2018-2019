package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.corningrobotics.enderbots.endercv.CameraViewDisplay;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.teamcode.GoldManager;
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
        sleep(500);
        while(robot.imu.getAngularOrientation().firstAngle > 35 | robot.imu.getAngularOrientation().firstAngle < 25){
            robot.rightMotor.setPower(-.9f);
            robot.leftMotor .setPower( .9f);
        }
        robot.rightMotor.setPower(-.8f);
        robot.leftMotor.setPower(-.8f);
        sleep(100);
        robot.rightMotor.setPower(0);
        robot.leftMotor.setPower(0);
        robot.liftMotor.setPower(-1);
        sleep(2000);
        robot.liftMotor.setPower(0);
        while(robot.imu.getAngularOrientation().firstAngle > 5 | robot.imu.getAngularOrientation().firstAngle < -5){
            robot.rightMotor.setPower( .9f);
            robot.leftMotor .setPower(-.9f);
        }
        robot.rightMotor.setPower(.4f);
        robot.leftMotor.setPower(.4f);
        sleep(500);
        //left imu.angularorientation positive
        float offset = robot.imu.getAngularOrientation().firstAngle;
        sleep(10);
        eTime.reset();


        float timeOffset = 0f;
        while(opModeIsActive() && eTime.time() < timeOffset + 2.5f) {
            goldManager.setShowThreshold(gamepad1.x);

            if(robot.imu.getAngularOrientation().firstAngle > 38) {
                double start = eTime.time();
                robot.rightMotor.setPower(-.8f);
                robot.leftMotor.setPower(-.8f);
                sleep(200);
                while (robot.imu.getAngularOrientation().firstAngle > -35) {
                    robot.rightMotor.setPower(1f);
                    robot.leftMotor.setPower(-1f);

                }
                timeOffset += eTime.time() - start + 1;
            }

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

            int screenThird = goldManager.hsv.rows() / 3 + 100;
            if (vectorF.get(1) <= screenThird) {
                location = -1;
            }
            else if (vectorF.get(1) > goldManager.hsv.rows() - screenThird) {
                location = 1;
            }

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

        while(opModeIsActive() && eTime.time() < 5 + timeOffset) {
            robot.leftMotor .setPower(-.6);
            robot.rightMotor.setPower(-.6);
        }

        robot.stop();

        robot.stop();
        goldManager.disable();
    }
}
