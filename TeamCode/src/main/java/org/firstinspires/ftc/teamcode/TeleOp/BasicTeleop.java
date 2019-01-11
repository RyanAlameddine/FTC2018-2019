package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Projects.Project0;
import org.firstinspires.ftc.teamcode.Projects.Project1;
import org.firstinspires.ftc.teamcode.Projects.Project2;

@TeleOp(name="Teleop", group="Production")
public class BasicTeleop extends LinearOpMode{

    @Override
    public void runOpMode() throws InterruptedException {
        Project2 robot = new Project2();
        float speedMultiplier = 1;
        robot.init(hardwareMap);

        //Waiting for start
        waitForStart();

        while(opModeIsActive()) {
            if(gamepad1.a){
                speedMultiplier = .5f;
            }else if(!gamepad1.a){
                speedMultiplier = 1f;
            }
            robot.rightMotor.setPower(gamepad1.right_stick_y * speedMultiplier);
            robot.leftMotor .setPower(gamepad1.left_stick_y  * speedMultiplier);


            if(gamepad1.dpad_up){
                robot.liftMotor.setPower(1);
            }else if(gamepad1.dpad_down){
                robot.liftMotor.setPower(-1);
            }else{
                robot.liftMotor.setPower(0);
            }

            robot.markerServo.setPosition(gamepad1.right_trigger);
            telemetry.addData("IMU", robot.imu.getAngularOrientation());
            telemetry.update();
        }

        robot.stop();
    }
}
