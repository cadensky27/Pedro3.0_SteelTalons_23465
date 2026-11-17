package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.tools;
import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.*;

/** SDK ONLY. With robot ELEVATED, hold A/B/X/Y to test four wheels independently. */
@TeleOp(name="Standalone V4 Drive Motor Test", group="Tuning")

public final class StandaloneDriveMotorTest extends OpMode {
    private DcMotorEx fl, fr, bl, br;

    @Override

    public void init() {
        fl = hardwareMap.get(DcMotorEx.class, "frontLeft");
        fr = hardwareMap.get(DcMotorEx.class, "frontRight");
        bl = hardwareMap.get(DcMotorEx.class, "backLeft");
        br = hardwareMap.get(DcMotorEx.class, "backRight");
        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);
        fr.setDirection(DcMotorSimple.Direction.FORWARD);
        br.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Override

    public void loop() {
        fl.setPower(gamepad1.a?.15:0);
        fr.setPower(gamepad1.b?.15:0);
        bl.setPower(gamepad1.x?.15:0);
        br.setPower(gamepad1.y?.15:0);
        telemetry.addLine("A FL, B FR, X BL, Y BR. ELEVATE robot.");
        telemetry.update();
    }

    @Override

    public void stop() {
        fl.setPower(0);
        fr.setPower(0);
        bl.setPower(0);
        br.setPower(0);
    }
}
