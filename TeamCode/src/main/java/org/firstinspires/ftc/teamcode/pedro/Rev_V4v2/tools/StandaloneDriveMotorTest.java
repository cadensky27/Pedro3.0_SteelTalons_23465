package org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.tools;
import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.*;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.config.V4Settings;

/** SDK ONLY. With robot ELEVATED, hold A/B/X/Y to test four wheels independently. */
@TeleOp(name="REV V4v2 Drive Motor Test", group="REV V4v2 Tuning")

public final class StandaloneDriveMotorTest extends OpMode {
    private DcMotorEx fl, fr, bl, br;

    @Override

    public void init() {
        fl = hardwareMap.get(DcMotorEx.class, V4Settings.Hardware.FRONT_LEFT);
        fr = hardwareMap.get(DcMotorEx.class, V4Settings.Hardware.FRONT_RIGHT);
        bl = hardwareMap.get(DcMotorEx.class, V4Settings.Hardware.BACK_LEFT);
        br = hardwareMap.get(DcMotorEx.class, V4Settings.Hardware.BACK_RIGHT);
        fl.setDirection(V4Settings.Hardware.FRONT_LEFT_DIRECTION);
        fr.setDirection(V4Settings.Hardware.FRONT_RIGHT_DIRECTION);
        bl.setDirection(V4Settings.Hardware.BACK_LEFT_DIRECTION);
        br.setDirection(V4Settings.Hardware.BACK_RIGHT_DIRECTION);
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
