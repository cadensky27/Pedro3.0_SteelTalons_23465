package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.tools;
import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.Servo;

/** SDK ONLY. One positional servo at a time; no movement until A is held to ARM. */
@TeleOp(name="Standalone V4 Servo Calibration", group="Tuning")

public final class StandaloneServoCalibrator extends OpMode {
    public static String NAME = "leftFlowerArm";
    private Servo servo;
    private boolean armed, up, down;
    private double pos = .5;

    @Override

    public void init() {
        servo = hardwareMap.get(Servo.class, NAME);
        telemetry.addLine("No servo movement until A pressed after Play");
        telemetry.update();
    }

    @Override

    public void loop() {
        if (gamepad1.a)armed = true;
        if (armed) {
            if (gamepad1.dpad_up && !up)pos = Math.min(1, pos+.01);
            if (gamepad1.dpad_down && !down)pos = Math.max(0, pos-.01);
            servo.setPosition(pos);
        }
        up = gamepad1.dpad_up;
        down = gamepad1.dpad_down;
        telemetry.addData("Servo", NAME);
        telemetry.addData("Armed", armed);
        telemetry.addData("Position", pos);
        telemetry.update();
    }
}
