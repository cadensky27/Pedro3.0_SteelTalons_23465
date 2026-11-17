package org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.commands;
import com.pedropathing.ivy.Command;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.core.Robot;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.config.V4Settings;
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

    public static Command preset(Robot r, V4Settings.Preset p) {
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

    public static Command flowerArm(Robot r, V4Settings.ArmPosition p) {
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
    /** Feed duration accumulates actual forward-output time; wall timeout prevents infinite waits. */

    public static Command feederForMs(Robot r, long activeMs, long wallMs) {
        final long[] began = {
            0
        }
        ;
        return Command.build()
        .setStart(()->{
            began[0] = System.nanoTime();if (r.feeder != null) {
                r.feeder.resetFeedTime();r.feeder.request();
            }
        }
        )
        .setDone(()->r.feeder == null || (System.nanoTime()-began[0])/1000000 >= wallMs || r.feeder.forwardSeconds()*1000 >= activeMs)
        .setEnd(end->{
            if (r.feeder != null)r.feeder.stop();
        }
        );
    }
    /** Higher-level shot: do not begin feeding if the readiness wait times out. */

    public static Command shootPreloads(Robot r, long feedMs) {
        return sequential(shooterOn(r), waitReady(r, V4Settings.Feeder.READY_TIMEOUT_MS),
        lazy(()->r.shooter != null && r.shooter.ready()?feederForMs(r, feedMs, V4Settings.Feeder.FEED_WALL_TIMEOUT_MS):instant(()->r.events.record("AUTO skipped feed: RPM not ready"))));
    }
}
