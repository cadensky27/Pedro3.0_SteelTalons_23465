package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.autonomous;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.ivy.Command;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.commands.MechanismCommands;

import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import static com.pedropathing.ivy.groups.Groups.parallel;

/** Copy this file, rename the class and OpMode, then supply your measured route. */
@Disabled
@Autonomous(name = "V5 AUTO TEMPLATE - DO NOT COMPETE", group = "BIOBUZZ V5")
public final class UserAutoTemplate extends BaseAuto {
    // ROUTINE SETTINGS
    private static final long FEED_MS = 2800;

    // FIELD POSES — inches and degrees in the shared Red-authored field frame.
    private final PoseFactory poses = PoseFactory.degrees();
    private final Pose startPose = poses.of(56, 8, 270);
    private final Pose shootPose = poses.of(59, 9, 270);
    private final Pose parkPose = poses.of(12, 92, 0);

    @Override
    protected Pose startingPose() {
        return startPose;
    }

    // MATCH SEQUENCE — Ivy runs these actions from top to bottom.
    @Override
    protected Command buildRoutine() {
        return sequential(
                parallel(follow(follower, pathToShoot()), MechanismCommands.shooterOn(robot)),
                MechanismCommands.shootPreloads(robot, FEED_MS),
                follow(follower, pathToPark()),
                MechanismCommands.shooterOff(robot)
        );
    }

    // PEDRO PATHS — adjacent segments reuse their common pose.
    private Path pathToShoot() {
        return Paths.line(startPose, shootPose)
                .linear(startPose, shootPose);
    }

    private Path pathToPark() {
        return Paths.line(shootPose, parkPose)
                .linear(shootPose, parkPose);
    }

}

// ORIGINAL V4v5 SOURCE — commented out for reference.
// package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V4v5.autonomous;
// import com.qualcomm.robotcore.eventloop.opmode.*;
// import com.pedropathing.api.*;
// import com.pedropathing.math.Pose;
// import com.pedropathing.paths.Path;
// import com.pedropathing.follower.Follower;
// import com.pedropathing.ivy.Command;
// import com.pedropathing.ivy.Scheduler;
// import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V4v5.Constants_GoBuilda;
// import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V4v5.core.*;
// import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V4v5.commands.MechanismCommands;
// import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V4v5.config.V4Settings;
// import static com.pedropathing.ivy.Scheduler.schedule;
// import static com.pedropathing.ivy.groups.Groups.*;
// import static com.pedropathing.ivy.pedro.PedroCommands.follow;
// /** User replaces ALL sample path coordinates. @Disabled until field tested and renamed. */
// @Disabled
// @Autonomous(name="V4 AUTO TEMPLATE - DO NOT COMPETE", group="BIOBUZZ V4")
// public final class UserAutoTemplate extends OpMode {
//     private final PoseFactory p = PoseFactory.degrees();
//     private final Pose start = p.of(56, 8, 270), shoot = p.of(59, 9, 270), park = p.of(12, 92, 0);
//     private final V4Settings.Alliance alliance = V4Settings.Alliance.RED;
//     private Follower follower;
//     private Robot robot;
//     private Command routine;
//     private boolean complete;
//     private Path pathToShoot() {
//         return Paths.line(start, shoot).linear(start, shoot);
//     }
//     private Path pathToPark() {
//         return Paths.line(shoot, park).linear(shoot, park);
//     }
//     /** This is the ONLY location to edit the path/action order. */
//     private Command routine() {
//         return sequential(
//         parallel(follow(follower, pathToShoot()), MechanismCommands.shooterOn(robot)),
//         MechanismCommands.shootPreloads(robot, 2800),
//         follow(follower, pathToPark()), MechanismCommands.shooterOff(robot));
//     }
// 
//     @Override
//     public void init() {
//         Scheduler.reset();
//         MatchHandoff.beginAuto();
//         follower = Constants_GoBuilda.create(hardwareMap);
//         follower.setPose(start);
//         robot = new Robot(hardwareMap);
//         telemetry.addLine("TEMPLATE ONLY - paths NOT measured");
//         telemetry.update();
//     }
// 
//     @Override
//     public void start() {
//         robot.startAuto(V4Settings.ArmPosition.UP);
//         routine = routine();
//         schedule(routine);
//     }
// 
//     @Override
//     public void loop() {
//         follower.update();
//         Scheduler.execute();
//         robot.update();
//         MatchHandoff.save(follower.pose(), alliance);
//         if (!complete && routine != null && !Scheduler.isScheduled(routine)) {
//             complete = true;
//             robot.stop();
//             follower.manual(0, 0, 0);
//         }
//         telemetry.addData("Auto", complete?"COMPLETE - IDLE":"RUNNING");
//         telemetry.addData("Pose", follower.pose());
//         telemetry.update();
//     }
// 
//     @Override
//     public void stop() {
//         if (follower != null)MatchHandoff.save(follower.pose(), alliance);
//         Scheduler.reset();
//         if (robot != null)robot.stop();
//         if (follower != null)follower.manual(0, 0, 0);
//     }
// }
