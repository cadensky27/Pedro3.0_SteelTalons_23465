package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.autonomous;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.Constants_GoBuilda;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.config.V5Settings;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.core.MatchHandoff;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.core.Robot;

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.instant;

/** Shared FTC lifecycle. Each actual Auto defines its own start, paths and Ivy sequence. */
public abstract class BaseAuto extends OpMode {
    protected Follower follower;
    protected Robot robot;
    private Command routine;
    private boolean started;
    private boolean complete;
    private long startedAt;
    private String stage = "";

    protected abstract Pose startingPose();

    protected abstract Command buildRoutine();

    /** Override for a routine that only drives, such as park-only. */
    protected boolean usesMechanisms() {
        return true;
    }

    protected V5Settings.ArmPosition startingArmPosition() {
        return V5Settings.ArmPosition.UP;
    }

    @Override
    public final void init() {
        Scheduler.reset();
        MatchHandoff.beginAuto();
        started = false;
        complete = false;
        stage = "";
        follower = Constants_GoBuilda.create(hardwareMap);
        Pose start = startingPose();
        follower.setPose(start);
        follower.update();

        robot = usesMechanisms() ? new Robot(hardwareMap) : null;
        telemetry.addData("Starting pose", start);
        if (robot != null && !robot.healthy()) telemetry.addData("Hardware errors", robot.critical());
        if (robot != null && !"NONE".equals(robot.events.recent())) {
            telemetry.addData("Hardware warning", robot.events.recent());
        }
        telemetry.update();
    }

    @Override
    public final void start() {
        if (robot != null) robot.startAuto(startingArmPosition());
        startedAt = System.nanoTime();
        routine = buildRoutine();
        schedule(routine);
        started = true;
    }

    @Override
    public final void loop() {
        follower.update();
        if (!complete) {
            if (robot != null) robot.update();
            Scheduler.execute();
            if (!Scheduler.isScheduled(routine)) {
                complete = true;
                stopOutputs();
            }
        }
        // Keep measuring after parking until the Driver Station ends Auto.
        MatchHandoff.save(follower.pose());
        reportStatus();
    }

    @Override
    public final void stop() {
        // Cancelling during INIT leaves no handoff from this routine.
        if (started && follower != null) MatchHandoff.save(follower.pose());
        try {
            Scheduler.reset();
        } finally {
            stopOutputs();
        }
    }

    /** Nonblocking delay so localization and other running mechanisms keep updating. */
    protected final Command waitMilliseconds(long durationMs) {
        final long[] began = {0};
        return Command.build()
                .setStart(() -> began[0] = System.nanoTime())
                .setDone(() -> (System.nanoTime() - began[0]) / 1_000_000L >= durationMs);
    }

    protected final Command showStage(String description) {
        return instant(() -> stage = description);
    }

    private void stopOutputs() {
        if (robot != null) robot.stop();
        if (follower != null) {
            follower.manual(0, 0, 0);
            follower.update();
        }
    }

    private void reportStatus() {
        telemetry.addData("Auto", complete ? "Complete" : "Running");
        telemetry.addData("Pose", follower.pose());
        telemetry.addData("Elapsed (s)", (System.nanoTime() - startedAt) / 1e9);
        if (!stage.isEmpty()) telemetry.addData("Stage", stage);
        if (follower.currentPath() != null) {
            telemetry.addData("Path distance remaining", follower.distanceToEndpoint());
        }
        if (robot != null && robot.shooter != null) {
            telemetry.addData("Shooter RPM", "%.0f / %.0f", robot.shooter.actual(), robot.shooter.target());
        }
        if (robot != null && robot.feeder != null) {
            telemetry.addData("Feeder", robot.feeder.state().display);
        }
        telemetry.update();
    }
}
