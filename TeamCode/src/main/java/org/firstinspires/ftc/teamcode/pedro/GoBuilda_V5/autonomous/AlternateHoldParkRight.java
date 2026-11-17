package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.autonomous;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.ivy.Command;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

@Autonomous(name = "Alternate-Hold-Park Right", group = "BIOBUZZ V5")
public final class AlternateHoldParkRight extends BaseAuto {
    // ROUTINE SETTINGS
    private static final long PARTNER_DELAY_MS = 2000;

    // FIELD POSES — inches and degrees in the shared Red-authored field frame.
    private final PoseFactory poses = PoseFactory.degrees();
    private final Pose startPose = poses.of(58.9908, 132.677, 90);
    private final Pose parkApproachPose = poses.of(58.9908, 32.6354, 90);
    private final Pose parkPose = poses.of(11.4338, 93.3361, 0);
    private final Pose parkControlPoint = poses.of(11.0855, 32.9439, 0);

    @Override
    protected Pose startingPose() {
        return startPose;
    }

    @Override
    protected boolean usesMechanisms() {
        return false;
    }

    // MATCH SEQUENCE — Ivy runs these actions from top to bottom.
    @Override
    protected Command buildRoutine() {
        return sequential(
                waitMilliseconds(PARTNER_DELAY_MS),
                follow(follower, pathToParkApproach()),
                follow(follower, pathToPark())
        );
    }

    // PEDRO PATHS — adjacent segments reuse their common pose.
    private Path pathToParkApproach() {
        return Paths.line(startPose, parkApproachPose)
                .linear(startPose, parkApproachPose);
    }

    private Path pathToPark() {
        return Paths.curve(parkApproachPose, parkControlPoint, parkPose)
                .linear(parkApproachPose, parkPose);
    }

}

// ORIGINAL V4v5 SOURCE — commented out for reference.
// 
// package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V4v5.autonomous;
// 
// import com.pedropathing.api.Paths;
// import com.pedropathing.api.PoseFactory;
// import com.pedropathing.follower.Follower;
// import com.pedropathing.math.Pose;
// import com.pedropathing.paths.Path;
// 
// import com.pedropathing.ivy.Command;
// import com.pedropathing.ivy.Scheduler;
// 
// import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
// import com.qualcomm.robotcore.eventloop.opmode.OpMode;
// 
// import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V4v5.Constants_GoBuilda;
// import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V4v5.config.V4Settings;
// import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V4v5.core.MatchHandoff;
// 
// import static com.pedropathing.ivy.Scheduler.schedule;
// import static com.pedropathing.ivy.commands.Commands.instant;
// import static com.pedropathing.ivy.groups.Groups.sequential;
// import static com.pedropathing.ivy.pedro.PedroCommands.follow;
// 
// /**
//  * BIOBUZZ V4 - DRIVE ONLY AUTONOMOUS
//  *
//  * This Autonomous performs no shooting, feeding, or
//  * intake operations.
//  *
//  * Sequence:
//  * 1. Initialize Pedro at the starting pose.
//  * 2. Wait for the configurable alliance-partner delay.
//  * 3. Follow Path 1.
//  * 4. Follow Path 2.
//  * 5. Stop and preserve the measured pose for TeleOp.
//  *
//  * Uses Pedro Ivy for nonblocking delays and path
//  * execution.
//  */
// @Autonomous(
//         name = "Alternate-Hold-Park Right (RED)",
//         group = "BIOBUZZ V4"
// )
// public final class AHPR_R extends OpMode {
// 
//     // =====================================================
//     // SECTION 1 - AUTONOMOUS CONFIGURATION
//     // =====================================================
// 
//     /**
//      * Alliance associated with this Autonomous.
//      *
//      * Used for the Auto-to-TeleOp pose handoff.
//      * Does not mirror the movement paths.
//      */
//     private static final V4Settings.Alliance AUTO_ALLIANCE =
//             V4Settings.Alliance.BLUE;
// 
//     /**
//      * Delay before the robot starts moving.
//      *
//      * Allows an alliance partner to leave the area.
//      *
//      * 0    = No delay
//      * 1000 = 1 second
//      * 2000 = 2 seconds
//      * 3000 = 3 seconds
//      * 5000 = 5 seconds
//      */
//     private static final long AUTO_START_DELAY_MS = 2000;
// 
//     /**
//      * Enable the first movement path.
//      */
//     private static final boolean ENABLE_PATH_1 = true;
// 
//     /**
//      * Enable the second movement path.
//      *
//      * Path 2 requires Path 1 because its starting
//      * position matches Path 1's endpoint.
//      */
//     private static final boolean ENABLE_PATH_2 = true;
// 
// 
//     // =====================================================
//     // SECTION 2 - ORIGINAL PEDRO COORDINATES
//     // =====================================================
//     //
//     // All coordinates are preserved from the original
//     // two-path Autonomous routine.
//     // =====================================================
// 
//     private final PoseFactory poseFactory =
//             PoseFactory.degrees();
// 
//     /**
//      * Initial robot position.
//      */
//     private final Pose start =
//             poseFactory.of(58.9908, 132.677, 90);
// 
//     /**
//      * Endpoint of the first path.
//      */
//     private final Pose path1End =
//             poseFactory.of(58.9908, 32.6354, 90);
// 
//     /**
//      * Starting point of the second path.
//      *
//      * Matches Path 1's endpoint.
//      */
//     private final Pose point2Start =
//             poseFactory.of(58.9908, 32.6354, 90);
// 
//     /**
//      * Endpoint of the second path.
//      */
//     private final Pose point2End =
//             poseFactory.of(11.4338, 93.3361, 0);
// 
//     /**
//      * Bezier control point for Path 2.
//      */
//     private final Pose point2Control1 =
//             poseFactory.of(11.0855, 32.9439, 0);
// 
// 
//     // =====================================================
//     // SECTION 3 - PEDRO AND IVY OBJECTS
//     // =====================================================
// 
//     private Follower follower;
//     private Command routine;
// 
//     private boolean complete = false;
// 
// 
//     // =====================================================
//     // SECTION 4 - NONBLOCKING IVY DELAY
//     // =====================================================
// 
//     /**
//      * Creates a nonblocking delay using Ivy.
//      *
//      * The delay timer begins when this command starts,
//      * rather than when the command is constructed.
//      *
//      * Pedro localization continues updating while
//      * the delay is active.
//      *
//      * @param durationMs Delay duration in milliseconds.
//      * @return Ivy delay command.
//      */
//     private Command waitMilliseconds(long durationMs) {
// 
//         final long[] startTimeNs = {0};
// 
//         return Command.build()
//                 .setStart(() -> {
//                     startTimeNs[0] = System.nanoTime();
//                 })
//                 .setDone(() -> {
// 
//                     long elapsedMs =
//                             (System.nanoTime() - startTimeNs[0])
//                                     / 1_000_000L;
// 
//                     return elapsedMs >= durationMs;
//                 });
//     }
// 
// 
//     // =====================================================
//     // SECTION 5 - MAIN AUTONOMOUS ROUTINE
//     // =====================================================
//     //
//     // All actions execute in the order shown.
//     //
//     // No shooting or other mechanisms are activated.
//     // =====================================================
// 
//     private Command autoRoutine() {
// 
//         return sequential(
// 
//                 // -----------------------------------------
//                 // STEP 1 - ALLIANCE PARTNER DELAY
//                 // -----------------------------------------
// 
//                 waitMilliseconds(
//                         AUTO_START_DELAY_MS
//                 ),
// 
//                 // -----------------------------------------
//                 // STEP 2 - FIRST PEDRO PATH
//                 // -----------------------------------------
// 
//                 ENABLE_PATH_1
//                         ? follow(follower, path1())
//                         : instant(() -> {}),
// 
//                 // -----------------------------------------
//                 // STEP 3 - SECOND PEDRO PATH
//                 // -----------------------------------------
// 
//                 ENABLE_PATH_1 && ENABLE_PATH_2
//                         ? follow(follower, path2())
//                         : instant(() -> {})
//         );
//     }
// 
// 
//     // =====================================================
//     // SECTION 6 - PEDRO PATH DEFINITIONS
//     // =====================================================
// 
//     /**
//      * PATH 1
//      *
//      * Straight-line movement.
//      *
//      * From:
//      * (58.9908, 132.677, 90 degrees)
//      *
//      * To:
//      * (58.9908, 32.6354, 90 degrees)
//      */
//     private Path path1() {
// 
//         return Paths.line(
//                 start,
//                 path1End
//         ).linear(
//                 start,
//                 path1End
//         );
//     }
// 
//     /**
//      * PATH 2
//      *
//      * Curved movement using a single control point.
//      *
//      * From:
//      * (58.9908, 32.6354, 90 degrees)
//      *
//      * Control:
//      * (11.0855, 32.9439, 0 degrees)
//      *
//      * To:
//      * (11.4338, 93.3361, 0 degrees)
//      */
//     private Path path2() {
// 
//         return Paths.curve(
//                 point2Start,
//                 point2Control1,
//                 point2End
//         ).linear(
//                 point2Start,
//                 point2End
//         );
//     }
// 
// 
//     // =====================================================
//     // SECTION 7 - INITIALIZATION
//     // =====================================================
// 
//     @Override
//     public void init() {
// 
//         // Reset Ivy before beginning this Autonomous.
//         Scheduler.reset();
// 
//         // Clear any previous Auto-to-TeleOp handoff.
//         MatchHandoff.beginAuto();
// 
//         // Construct Pedro using the goBILDA configuration.
//         follower = Constants_GoBuilda.create(hardwareMap);
// 
//         // Initialize localization at the supplied pose.
//         follower.setPose(start);
//         follower.update();
// 
//         complete = false;
// 
//         // Show the configured routine on Driver Station.
//         telemetry.addLine("BIOBUZZ V4 - DRIVE ONLY");
// 
//         telemetry.addData(
//                 "Alliance",
//                 AUTO_ALLIANCE
//         );
// 
//         telemetry.addData(
//                 "Start pose",
//                 start
//         );
// 
//         telemetry.addData(
//                 "Start delay (ms)",
//                 AUTO_START_DELAY_MS
//         );
// 
//         telemetry.addData(
//                 "Path 1 enabled",
//                 ENABLE_PATH_1
//         );
// 
//         telemetry.addData(
//                 "Path 2 enabled",
//                 ENABLE_PATH_1 && ENABLE_PATH_2
//         );
// 
//         telemetry.update();
//     }
// 
// 
//     // =====================================================
//     // SECTION 8 - AUTONOMOUS START
//     // =====================================================
// 
//     @Override
//     public void start() {
// 
//         // Build and schedule the drive-only Ivy sequence.
//         routine = autoRoutine();
// 
//         schedule(routine);
//     }
// 
// 
//     // =====================================================
//     // SECTION 9 - PERIODIC UPDATE
//     // =====================================================
// 
//     @Override
//     public void loop() {
// 
//         // Update Pedro localization and path following.
//         follower.update();
// 
//         // Run pending Ivy commands.
//         Scheduler.execute();
// 
//         // Save the measured pose for the next TeleOp.
//         MatchHandoff.save(
//                 follower.pose(),
//                 AUTO_ALLIANCE
//         );
// 
//         // Detect completion of the full command sequence.
//         if (!complete
//                 && routine != null
//                 && !Scheduler.isScheduled(routine)) {
// 
//             complete = true;
// 
//             // Release drivetrain commands.
//             follower.manual(0, 0, 0);
//         }
// 
//         // ---------------------------------------------
//         // DRIVER STATION TELEMETRY
//         // ---------------------------------------------
// 
//         telemetry.addData(
//                 "Auto status",
//                 complete ? "COMPLETE" : "RUNNING"
//         );
// 
//         telemetry.addData(
//                 "Alliance",
//                 AUTO_ALLIANCE
//         );
// 
//         telemetry.addData(
//                 "Current pose",
//                 follower.pose()
//         );
// 
//         if (follower.currentPath() != null) {
// 
//             telemetry.addData(
//                     "Distance remaining",
//                     follower.distanceToEndpoint()
//             );
// 
//             telemetry.addData(
//                     "Current path index",
//                     follower.pathIndex()
//             );
//         }
// 
//         telemetry.update();
//     }
// 
// 
//     // =====================================================
//     // SECTION 10 - AUTONOMOUS STOP
//     // =====================================================
// 
//     @Override
//     public void stop() {
// 
//         // Preserve the final pose for TeleOp.
//         if (follower != null) {
// 
//             MatchHandoff.save(
//                     follower.pose(),
//                     AUTO_ALLIANCE
//             );
//         }
// 
//         // Cancel any unfinished Ivy commands.
//         Scheduler.reset();
// 
//         // Stop drivetrain movement.
//         if (follower != null) {
//             follower.manual(0, 0, 0);
//         }
//     }
// }
