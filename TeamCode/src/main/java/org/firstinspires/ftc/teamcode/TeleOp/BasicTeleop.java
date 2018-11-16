package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Projects.Project0;

@TeleOp(name="Teleop", group="Production")
public class BasicTeleop extends LinearOpMode{

    @Override
    public void runOpMode() throws InterruptedException {
        Project0 robot = new Project0();
        float speedMultiplier = 1;
        boolean aPressed = false;
        robot.init(hardwareMap);

        //Waiting for start
        waitForStart();

        while(opModeIsActive()) {
            if(gamepad1.a && !aPressed){
                aPressed = true;
                speedMultiplier *= -1;
            }else{
                aPressed = false;
            }
            robot.rightMotor.setPower(gamepad1.right_stick_y * speedMultiplier);
            robot.leftMotor .setPower(gamepad1.left_stick_y  * speedMultiplier);
        }

        robot.rightMotor.setPower(0);
        robot.leftMotor .setPower(0);
    }
}
