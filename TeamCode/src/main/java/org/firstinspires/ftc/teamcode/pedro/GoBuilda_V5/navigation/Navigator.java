package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.navigation;

import com.pedropathing.api.Paths;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.config.V4Settings;

public final class Navigator {
    public enum Result { IDLE, RUNNING, SUCCESS, CANCELLED, FAILED }

    private final Follower follower;
    private Result result = Result.IDLE;
    private Pose goal;
    private long startedAt;
    private long stableSince = -1;
    private String status = "Idle";

    public Navigator(Follower follower) {
        this.follower = follower;
    }

    /** Drive one straight line from the measured pose and interpolate its heading. */
    public void start(V4Settings.Destination destination, boolean poseAvailable) {
        cancel();
        goal = FieldLibrary.at(destination);
        if (!poseAvailable || goal == null) {
            finish(Result.FAILED, !poseAvailable ? "Starting pose unavailable"
                    : destination + ": pose not configured");
            return;
        }
        Pose start = follower.pose();
        follower.follow(Paths.line(start, goal).linear(start, goal));
        startedAt = System.nanoTime();
        stableSince = -1;
        result = Result.RUNNING;
        status = "Aligning to " + destination;
    }

    public void update() {
        if (!active()) {
            return;
        }
        long now = System.nanoTime();
        if ((now - startedAt) / 1_000_000L >= V4Settings.Navigation.BASIC_TIMEOUT_MS) {
            finish(Result.FAILED, "Alignment timed out");
            return;
        }

        Pose measured = follower.pose();
        double distance = Math.hypot(measured.x() - goal.x(), measured.y() - goal.y());
        double angleDifference = measured.heading() - goal.heading();
        double headingError = Math.atan2(Math.sin(angleDifference), Math.cos(angleDifference));
        boolean withinTolerance = distance <= V4Settings.Navigation.BASIC_POS_TOL
                && Math.abs(headingError) <= Math.toRadians(V4Settings.Navigation.BASIC_HEAD_TOL_DEG);
        if (follower.isBusy() || !withinTolerance) {
            stableSince = -1;
            return;
        }
        if (stableSince < 0) {
            stableSince = now;
        }
        if ((now - stableSince) / 1_000_000L >= V4Settings.Navigation.ARRIVAL_STABLE_MS) {
            finish(Result.SUCCESS, "Aligned");
        }
    }

    public void cancel() {
        if (active()) {
            finish(Result.CANCELLED, "Alignment cancelled");
        }
    }

    private void finish(Result outcome, String message) {
        follower.manual(0, 0, 0);
        result = outcome;
        status = message;
    }

    public boolean active() {
        return result == Result.RUNNING;
    }

    public Result result() {
        return result;
    }

    public String status() {
        return status;
    }
}
