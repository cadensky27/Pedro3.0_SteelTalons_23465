package org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.navigation;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.api.Paths;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.config.V4Settings;
/** Direct Pedro line or explicit vetted multi-leg route, never two competing navigators. */
public final class Navigator {
    public enum Result {
        IDLE, RUNNING, SUCCESS, CANCELLED, FAILED
    }
    private final Follower follower;
    private Pose[] legs;
    private int leg;
    private boolean active, advanced;
    private Result result = Result.IDLE;
    private long began, stableAt = -1;
    private String description = "";
    public Navigator(Follower follower) {
        this.follower = follower;
    }
    // Start at the measured current pose; use direct line or vetted waypoint legs.
    // Replan from the measured robot position, never an assumed route endpoint.
    public boolean start(V4Settings.Alliance alliance, V4Settings.Destination dest, boolean poseValid) {
        cancel();
        Pose goal = FieldLibrary.at(alliance, dest);
        if (!poseValid || goal == null)return fail("Invalid pose or alliance");
        advanced = V4Settings.Navigation.ADVANCED;
        if (advanced) {
            if (!V4Settings.Navigation.ADVANCED_ROUTES_FIELD_VERIFIED)return fail("Advanced corridor geometry not verified");
            Pose here = follower.pose();
            Pose via = dest == V4Settings.Destination.UNDER_HIVE?FieldLibrary.underHive():
            (Math.abs(here.x()-goal.x())>65 && Math.abs(here.y()-goal.y())>60)?FieldLibrary.underHive():
            (here.x()+goal.x()<141.5?FieldLibrary.westCorridor():FieldLibrary.eastCorridor());
            legs = new Pose[]{
                via, goal
            }
            ;
            description = "ADVANCED (check corridor physically)";
        }
        else {
            legs = new Pose[]{
                goal
            }
            ;
            description = "BASIC direct";
        }
        leg = 0;
        active = true;
        result = Result.RUNNING;
        began = System.nanoTime();
        stableAt = -1;
        launch();
        return true;
    }
    private void launch() {
        Pose from = follower.pose(), to = legs[leg];
        follower.follow(Paths.line(from, to).linear(from, to));
    }
    private boolean fail(String msg) {
        active = false;
        result = Result.FAILED;
        description = msg;
        follower.manual(0, 0, 0);
        return false;
    }
    // Track path timeouts and require stable position plus heading before success.
    public void update() {
        if (!active)return;
        long now = System.nanoTime(), ms = (now-began)/1000000;
        if (ms>(advanced?V4Settings.Navigation.ADVANCED_TIMEOUT_MS:V4Settings.Navigation.BASIC_TIMEOUT_MS)) {
            fail("Navigation timeout");
            return;
        }
        Pose measured = follower.pose(), goal = legs[leg];
        double dist = Math.hypot(measured.x()-goal.x(), measured.y()-goal.y());
        double delta = Math.atan2(Math.sin(measured.heading()-goal.heading()), Math.cos(measured.heading()-goal.heading()));
        double tol = advanced?V4Settings.Navigation.ADVANCED_POS_TOL:V4Settings.Navigation.BASIC_POS_TOL;
        double heading = Math.toRadians(advanced?V4Settings.Navigation.ADVANCED_HEAD_TOL_DEG:V4Settings.Navigation.BASIC_HEAD_TOL_DEG);
        if (!follower.isBusy() && dist <= tol && Math.abs(delta) <= heading) {
            if (stableAt<0)stableAt = now;
            if ((now-stableAt)/1000000 >= V4Settings.Navigation.ARRIVAL_STABLE_MS) {
                if (++leg<legs.length) {
                    stableAt = -1;
                    launch();
                }
                else {
                    active = false;
                    result = Result.SUCCESS;
                    follower.manual(0, 0, 0);
                }
            }
        }
        else
            stableAt = -1;
    }
    // Release follower control without reporting a successful arrival.
    public void cancel() {
        if (active) {
            active = false;
            result = Result.CANCELLED;
            follower.manual(0, 0, 0);
        }
    }
    public boolean active() {
        return active;
    }
    public Result result() {
        return result;
    }
    public String description() {
        return description;
    }
}
