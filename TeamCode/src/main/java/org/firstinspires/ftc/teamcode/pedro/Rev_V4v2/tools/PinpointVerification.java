package org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.tools;
import com.qualcomm.robotcore.eventloop.opmode.*;
import com.pedropathing.follower.Follower;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.Constants_REV;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.config.V4Settings;

/** Pedro/Pinpoint field reference check. Hand-push on a clear test surface. */
@TeleOp(name="REV V4v2 Pinpoint Validation", group="REV V4v2 Tuning")

public final class PinpointVerification extends OpMode {
    private Follower follower;

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
        follower.manual(0, 0, 0);
        follower.update();
        telemetry.addData("X (in)", follower.pose().x());
        telemetry.addData("Y (in)", follower.pose().y());
        telemetry.addData("Heading (deg)", Math.toDegrees(follower.pose().heading()));
        telemetry.update();
    }

    @Override

    public void stop() {
        if (follower != null) {
            follower.manual(0, 0, 0);
        }
    }
}
