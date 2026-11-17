package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.core;

import com.pedropathing.math.Pose;

/** Stores the last measured Auto pose for one TeleOp initialization in the same app process. */
public final class MatchHandoff {
    private static Pose pose;
    private static long savedAt;

    private MatchHandoff() {
    }

    public static synchronized void beginAuto() {
        pose = null;
    }

    public static synchronized void save(Pose measured) {
        if (measured == null || !Double.isFinite(measured.x())
                || !Double.isFinite(measured.y()) || !Double.isFinite(measured.heading())) {
            return;
        }
        pose = new Pose(measured.x(), measured.y(), measured.heading());
        savedAt = System.nanoTime();
    }

    public static synchronized Pose consume() {
        // The handoff is one-use and expires after two minutes.
        Pose result = System.nanoTime() - savedAt < 120_000_000_000L ? pose : null;
        pose = null;
        return result;
    }
}
