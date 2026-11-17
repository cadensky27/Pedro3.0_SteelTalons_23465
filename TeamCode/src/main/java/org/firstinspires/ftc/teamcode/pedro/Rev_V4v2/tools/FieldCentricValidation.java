package org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.tools;
import com.qualcomm.robotcore.eventloop.opmode.*;
import com.pedropathing.follower.*;
import com.pedropathing.drivetrain.DrivePowers;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.Constants_REV;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.config.V4Settings;

/** Pedro field-centric live test. Dpad up/down changes offset by 5°; copy result to V4Settings. */
@TeleOp(name="REV V4v2 Field Centric Validation", group="REV V4v2 Tuning")

public final class FieldCentricValidation extends OpMode {
    private Follower follower;
    private double offset;
    private boolean up, down, field = true;

    @Override

    public void init() {
        if (V4Settings.Drive.BACKEND != V4Settings.DriveBackend.PEDRO) {
            telemetry.addLine("This tool requires PEDRO in V4Settings.Drive.BACKEND.");
            telemetry.update();
            return;
        }
        follower = Constants_REV.create(hardwareMap);
    }

    @Override

    public void loop() {
        if (follower == null) {
            return;
        }
        if (gamepad1.dpad_up && !up)offset+=5;
        if (gamepad1.dpad_down && !down)offset-=5;
        up = gamepad1.dpad_up;
        down = gamepad1.dpad_down;
        if (gamepad1.a)field = true;
        if (gamepad1.b)field = false;
        double f = -gamepad1.left_stick_y, s = gamepad1.left_stick_x, t = gamepad1.right_stick_x;
        if (field) {
            DrivePowers p = ManualDrive.fieldCentric(f, s, t, follower.pose().heading()+Math.toRadians(offset));
            follower.manual(p);
        }
        else
            follower.manual(f, s, t);
        follower.update();
        telemetry.addData("Mode", field?"FIELD":"ROBOT");
        telemetry.addData("Heading deg", Math.toDegrees(follower.pose().heading()));
        telemetry.addData("Offset deg", offset);
        telemetry.addData("X/Y", follower.pose().x()+" / "+follower.pose().y());
        telemetry.update();
    }

    @Override

    public void stop() {
        if (follower != null) {
            follower.manual(0, 0, 0);
        }
    }
}
