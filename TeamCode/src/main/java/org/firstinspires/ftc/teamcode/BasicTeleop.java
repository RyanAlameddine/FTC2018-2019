package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="BasicTeleop", group="Test")
public class BasicTeleop extends LinearOpMode{

    @Override
    public void runOpMode() throws InterruptedException {
        Project0 robot = new Project0();

        robot.init(hardwareMap);

        //Waiting for start
        waitForStart();

        while(opModeIsActive()) {
            robot.rightMotor.setPower(gamepad1.right_stick_y);
            robot.leftMotor .setPower(gamepad1.left_stick_y );
        }

        robot.rightMotor.setPower(0);
        robot.leftMotor .setPower(0);
    }
}
