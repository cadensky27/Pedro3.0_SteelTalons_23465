package org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.core;
import com.pedropathing.math.Pose;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.config.V4Settings;
/** Single-consumption measured Pose+alliance stored within the active RC Java process. */
public final class MatchHandoff {
    private static Pose pose;
    private static V4Settings.Alliance alliance;
    private static long time;
    private MatchHandoff() {
    }
    // Prevent a previous match from supplying an obsolete TeleOp starting pose.
    public static synchronized void beginAuto() {
        pose = null;
        alliance = null;
        time = 0;
    }
    // Store only finite measured Pedro poses from a known alliance.
    public static synchronized void save(Pose p, V4Settings.Alliance a) {
        if (p == null || a == null || a == V4Settings.Alliance.UNDEFINED)return;
        if (!Double.isFinite(p.x()) || !Double.isFinite(p.y()) || !Double.isFinite(p.heading()))return;
        pose = new Pose(p.x(), p.y(), p.heading());
        alliance = a;
        time = System.nanoTime();
    }
    public static final class Result {
        public final Pose pose;
        public final V4Settings.Alliance alliance;
        public final String status;
        Result(Pose p, V4Settings.Alliance a, String s) {
            pose = p;
            alliance = a;
            status = s;
        }
        public boolean valid() {
            return pose != null;
        }
    }
    // Consume at most once; reject stale pose storage on read.
    // One-time consumption guards against accidentally reusing a prior match pose.
    public static synchronized Result consume() {
        boolean valid = pose != null && time>0 && System.nanoTime()-time<120_000_000_000L;
        Result result = valid?new Result(pose, alliance, "SUCCESS"):new Result(null, V4Settings.Alliance.UNDEFINED, pose == null?"MISSING":"INVALID/STALE");
        pose = null;
        alliance = null;
        time = 0;
        return result;
    }
}
