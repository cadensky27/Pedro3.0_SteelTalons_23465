package org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.teleop;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.drivetrain.DrivePowers;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.config.V4Settings;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.subsystems.ManualMecanumDrive;

/** Shared stick processing with one drivetrain selected at initialization. */
public final class DriveControl {
    private final Follower follower;
    private final ManualMecanumDrive sdkDrive;
    private boolean slow, fallback;
    private V4Settings.Alliance alliance = V4Settings.Alliance.UNDEFINED;
    private double lastF, lastS, lastT;
    private long previous = System.nanoTime();
    public DriveControl(Follower f) {
        follower = f;
        sdkDrive = null;
    }
    public DriveControl(ManualMecanumDrive sdkDrive) {
        this.sdkDrive = sdkDrive;
        follower = null;
    }
    public void alliance(V4Settings.Alliance a) {
        alliance = a;
    }
    public V4Settings.Alliance alliance() {
        return alliance;
    }
    public void toggleSlow() {
        slow = !slow;
    }
    public boolean slow() {
        return slow;
    }
    public void toggleFallback() {
        fallback = !fallback;
    }
    // Return robot-centric whenever fallback is active or heading is untrusted.
    public V4Settings.DriveMode mode(boolean headingValid) {
        return sdkDrive != null || fallback || !headingValid
                ? V4Settings.DriveMode.ROBOT_CENTRIC : V4Settings.Drive.DEFAULT_MODE;
    }
    private static double clamp(double v) {
        return Math.max(-1, Math.min(1, v));
    }
    private static double shape(double v) {
        double k = V4Settings.Drive.CUBIC_BLEND;
        return (1-k)*v+k*v*v*v;
    }
    /** Shape driver inputs before sending them to the selected drivetrain. */
    public void manual(double f, double s, double t, boolean headingValid) {
        f = Math.abs(f)<V4Settings.Drive.DEADZONE?0:shape(f);
        s = Math.abs(s)<V4Settings.Drive.DEADZONE?0:shape(s);
        t = Math.abs(t)<V4Settings.Drive.DEADZONE?0:shape(t);
        double k = slow?V4Settings.Drive.SLOW:V4Settings.Drive.FAST;
        f*=k;
        s*=k;
        t*=k;
        long now = System.nanoTime();
        double dt = Math.min(.1, Math.max(.001, (now-previous)/1e9));
        previous = now;
        if (V4Settings.Drive.SLEW) {
            double max = V4Settings.Drive.SLEW_UNITS_PER_SEC*dt;
            lastF+=Math.copySign(Math.min(max, Math.abs(f-lastF)), f-lastF);
            lastS+=Math.copySign(Math.min(max, Math.abs(s-lastS)), s-lastS);
            lastT+=Math.copySign(Math.min(max, Math.abs(t-lastT)), t-lastT);
            f = lastF;
            s = lastS;
            t = lastT;
        }
        else {
            lastF = f;
            lastS = s;
            lastT = t;
        }
        if (sdkDrive != null) {
            sdkDrive.driveRobotCentric(f, s, t, k);
            return;
        }
        V4Settings.DriveMode mode = mode(headingValid);
        if (mode == V4Settings.DriveMode.ROBOT_CENTRIC) {
            follower.manual(f, s, t);
            return;
        }
        double offset = Math.toRadians(alliance == V4Settings.Alliance.BLUE?V4Settings.Drive.BLUE_OFFSET_DEG:V4Settings.Drive.RED_OFFSET_DEG);
        double heading = follower.pose().heading()+offset;
        if (mode == V4Settings.DriveMode.AUTO_FACING) {
            if (Math.hypot(f, s)>V4Settings.Drive.DEADZONE && Math.abs(t)<.12) {
                // Axis convention MUST be verified with dedicated test. Desired translation vector is field-frame.
                double desired = Math.atan2(s, f)+offset;
                double error = Math.atan2(Math.sin(desired-follower.pose().heading()), Math.cos(desired-follower.pose().heading()));
                if (Math.abs(error)>Math.toRadians(V4Settings.Drive.AUTO_FACE_TOL_DEG)) {
                    t = clamp(error*V4Settings.Drive.AUTO_FACE_KP)*V4Settings.Drive.AUTO_FACE_MAX_TURN;
                    double reduction = 1-V4Settings.Drive.AUTO_FACE_ROTATION_PRIORITY*Math.min(1, Math.abs(error)/Math.PI);
                    f*=reduction;
                    s*=reduction;
                }
                else
                    t = 0;
            }
            else
                if (Math.abs(t)<.12)t = 0;
            // No heading hold with no translation.
        }
        DrivePowers powers = ManualDrive.fieldCentric(f, s, t, heading);
        follower.manual(powers);
    }

    /** Stop the selected drivetrain immediately, without applying the stick ramp. */
    public void stop() {
        if (sdkDrive != null) {
            sdkDrive.stop();
        } else {
            follower.manual(0, 0, 0);
        }
    }
}
