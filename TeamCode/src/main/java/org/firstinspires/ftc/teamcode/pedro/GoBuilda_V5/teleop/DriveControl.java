package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.teleop;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.config.V4Settings;

/** Shapes driver sticks and hands their result to Pedro's mecanum drive. */
public final class DriveControl {
    private final Follower follower;
    private boolean slow;
    private boolean fieldCentric = V4Settings.Drive.FIELD_CENTRIC_ENABLED;

    public DriveControl(Follower follower) {
        this.follower = follower;
    }

    public void setSlow(boolean slow) {
        this.slow = slow;
    }

    public boolean slow() {
        return slow;
    }

    public void toggleFieldCentric() {
        fieldCentric = !fieldCentric;
    }

    public boolean fieldCentric(boolean poseAvailable) {
        return fieldCentric && poseAvailable;
    }

    public void manual(double forward, double strafe, double turn, boolean poseAvailable) {
        double scale = slow ? V4Settings.Drive.SLOW : V4Settings.Drive.FAST;
        forward = shape(forward) * scale;
        strafe = shape(strafe) * scale;
        turn = shape(turn) * scale;
        if (fieldCentric(poseAvailable)) {
            double heading = follower.pose().heading()
                    + Math.toRadians(V4Settings.Drive.DRIVER_OFFSET_DEG);
            follower.manual(ManualDrive.fieldCentric(forward, strafe, turn, heading));
        } else {
            follower.manual(forward, strafe, turn);
        }
    }

    private static double shape(double input) {
        if (Math.abs(input) < V4Settings.Drive.DEADZONE) {
            return 0;
        }
        double blend = V4Settings.Drive.CUBIC_BLEND;
        return (1 - blend) * input + blend * input * input * input;
    }
}
