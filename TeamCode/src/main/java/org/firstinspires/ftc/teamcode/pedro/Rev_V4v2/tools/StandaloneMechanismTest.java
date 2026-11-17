package org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.tools;
import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.*;

/** SDK ONLY, low power intake/side roller/feeder test; no Pedro. */
@TeleOp(name="REV V4v2 Intake Feeder Check", group="REV V4v2 Tuning")

public final class StandaloneMechanismTest extends OpMode {
    private DcMotorEx intake;
    private CRServo left, right, feeder;

    @Override

    public void init() {
        intake = hardwareMap.get(DcMotorEx.class, "mainIntake");
        left = hardwareMap.get(CRServo.class, "leftSideRoller");
        right = hardwareMap.get(CRServo.class, "rightSideRoller");
        feeder = hardwareMap.get(CRServo.class, "pusher");
    }

    @Override

    public void loop() {
        double p = gamepad1.right_trigger>.2?.2:gamepad1.left_trigger>.2?-.2:0;
        intake.setPower(p);
        left.setPower(-p);
        right.setPower(p);
        feeder.setPower(gamepad1.right_bumper?.2:gamepad1.left_bumper?-.2:0);
        telemetry.addData("Intake", p);
        telemetry.addLine("R2/L2 intake ±; R1/L1 feeder ±; 20% power");
        telemetry.update();
    }

    @Override

    public void stop() {
        intake.setPower(0);
        left.setPower(0);
        right.setPower(0);
        feeder.setPower(0);
    }
}
