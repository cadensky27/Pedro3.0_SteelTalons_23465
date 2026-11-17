package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.commands;
import com.pedropathing.ivy.Command;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.core.Robot;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.config.V5Settings;
import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.*;

/** Distinct Ivy public commands. NO follower or path creation in this mechanism library. */

public final class MechanismCommands {
    private MechanismCommands() {
    }

    public static Command shooterOn(Robot r) {
        return instant(()->{
            if (r.shooter != null)r.shooter.enable(true);
        }
        );
    }

    public static Command shooterOff(Robot r) {
        return instant(r::stopShooter);
    }

    public static Command preset(Robot r, V5Settings.Preset p) {
        return instant(()->{
            if (r.shooter != null)r.shooter.preset(p);
        }
        );
    }

    public static Command intakeStart(Robot r) {
        return instant(()->{
            if (r.intake != null)r.intake.setDirection(1);
        }
        );
    }

    public static Command intakeReverse(Robot r) {
        return instant(()->{
            if (r.intake != null)r.intake.setDirection(-1);
        }
        );
    }

    public static Command intakeStop(Robot r) {
        return instant(()->{
            if (r.intake != null)r.intake.stop();
        }
        );
    }

    public static Command flowerArm(Robot r, V5Settings.ArmPosition p) {
        return instant(()->{
            if (r.arm != null)r.arm.moveTo(p);
        }
        );
    }
    /** Time-limited action with cleanup on command interruption. */

    public static Command intakeForMs(Robot r, long ms) {
        final long[] began = {
            0
        }
        ;
        return Command.build()
        .setStart(()->{
            began[0] = System.nanoTime();if (r.intake != null)r.intake.setDirection(1);
        }
        )
        .setDone(()->(System.nanoTime()-began[0])/1000000 >= ms)
        .setEnd(end->{
            if (r.intake != null)r.intake.stop();
        }
        );
    }

    public static Command waitReady(Robot r, long timeout) {
        final long[] began = {
            0
        }
        ;
        return Command.build()
        .setStart(()->began[0] = System.nanoTime())
        .setDone(()->r.shooter == null || r.shooter.ready() || (System.nanoTime()-began[0])/1000000 >= timeout);
    }

    public static Command feederStart(Robot r) {
        return instant(()->{
            if (r.feeder != null)r.feeder.request();
        }
        );
    }

    public static Command feederStop(Robot r) {
        return instant(()->{
            if (r.feeder != null)r.feeder.stop();
        }
        );
    }
    /** Count actual forward feeder time, with an overall cutoff for the shot. */
    public static Command feederForMs(Robot robot, long activeMs, long wallMs) {
        return feederForMs(robot, activeMs, wallMs, true);
    }

    /** Choose measured-RPM feed or the Auto's explicit timed spin-up policy. */
    public static Command feederForMs(Robot robot, long activeMs, long wallMs, boolean waitForRpm) {
        final long[] beganAt = {0};
        return Command.build()
                .setStart(() -> {
                    beganAt[0] = System.nanoTime();
                    if (robot.feeder != null) {
                        robot.feeder.resetFeedTime();
                        robot.feeder.request(waitForRpm, waitForRpm);
                    }
                })
                .setDone(() -> robot.feeder == null
                        || (System.nanoTime() - beganAt[0]) / 1_000_000L >= wallMs
                        || robot.feeder.forwardSeconds() * 1_000 >= activeMs)
                .setEnd(end -> {
                    if (robot.feeder != null) robot.feeder.stop();
                });
    }

    /** Skip feeding if readiness never arrives within the Auto wait window. */
    public static Command shootPreloads(Robot robot, long feedMs) {
        return sequential(
                shooterOn(robot),
                waitReady(robot, V5Settings.Feeder.READY_TIMEOUT_MS),
                lazy(() -> robot.shooter != null && robot.shooter.ready()
                        ? feederForMs(robot, feedMs, V5Settings.Feeder.FEED_WALL_TIMEOUT_MS)
                        : instant(() -> robot.events.record("AUTO skipped feed: RPM not ready")))
        );
    }
}
