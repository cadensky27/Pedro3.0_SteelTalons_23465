package org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.autonomous;
import com.qualcomm.robotcore.eventloop.opmode.*;
import com.pedropathing.api.*;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.Constants_REV;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.core.*;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.commands.MechanismCommands;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.config.V4Settings;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.*;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
/** User replaces ALL sample path coordinates. @Disabled until field tested and renamed. */
@Disabled
@Autonomous(name="REV V4v2 AUTO TEMPLATE - DO NOT COMPETE", group="REV V4v2")
public final class UserAutoTemplate extends OpMode {
    private Pose start, shoot, park;
    private final V4Settings.Alliance alliance = V4Settings.Alliance.RED;
    private Follower follower;
    private Robot robot;
    private Command routine;
    private boolean complete;
    private Path pathToShoot() {
        return Paths.line(start, shoot).linear(start, shoot);
    }
    private Path pathToPark() {
        return Paths.line(shoot, park).linear(shoot, park);
    }
    /** This is the ONLY location to edit the path/action order. */
    private Command routine() {
        return sequential(
        parallel(follow(follower, pathToShoot()), MechanismCommands.shooterOn(robot)),
        MechanismCommands.shootPreloads(robot, 2800),
        follow(follower, pathToPark()), MechanismCommands.shooterOff(robot));
    }

    @Override
    public void init() {
        if (V4Settings.Drive.BACKEND != V4Settings.DriveBackend.PEDRO) {
            telemetry.addLine("REV Auto requires PEDRO in V4Settings.Drive.BACKEND.");
            telemetry.update();
            return;
        }
        PoseFactory p = PoseFactory.degrees();
        start = p.of(56, 8, 270);
        shoot = p.of(59, 9, 270);
        park = p.of(12, 92, 0);
        Scheduler.reset();
        MatchHandoff.beginAuto();
        follower = Constants_REV.create(hardwareMap);
        follower.setPose(start);
        robot = new Robot(hardwareMap);
        telemetry.addLine("TEMPLATE ONLY - paths NOT measured");
        telemetry.update();
    }

    @Override
    public void start() {
        if (robot == null) {
            return;
        }
        robot.startAuto(V4Settings.ArmPosition.UP);
        routine = routine();
        schedule(routine);
    }

    @Override
    public void loop() {
        if (robot == null) {
            return;
        }
        follower.update();
        Scheduler.execute();
        robot.update();
        MatchHandoff.save(follower.pose(), alliance);
        if (!complete && routine != null && !Scheduler.isScheduled(routine)) {
            complete = true;
            robot.stop();
            follower.manual(0, 0, 0);
        }
        telemetry.addData("Auto", complete?"COMPLETE - IDLE":"RUNNING");
        telemetry.addData("Pose", follower.pose());
        telemetry.update();
    }

    @Override
    public void stop() {
        if (follower != null) {
            MatchHandoff.save(follower.pose(), alliance);
            Scheduler.reset();
        }
        if (robot != null)robot.stop();
        if (follower != null)follower.manual(0, 0, 0);
    }
}
