package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.autonomous;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.ivy.Command;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

@Autonomous(name = "Alternate-Hold-Park Left", group = "BIOBUZZ V5")
public final class AlternateHoldParkLeft extends BaseAuto {
    // ROUTINE SETTINGS
    private static final long PARTNER_DELAY_MS = 2000;

    // FIELD POSES — inches and degrees in the shared Red-authored field frame.
    private final PoseFactory poses = PoseFactory.degrees();
    private final Pose startPose = poses.of(59.5515, 132.8639, 90);
    private final Pose parkPose = poses.of(11.6995, 121.4234, 0);
    private final Pose parkControlPoint = poses.of(67.8627, 108.3747, 0);

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
                follow(follower, pathToPark())
        );
    }

    // PEDRO PATHS — adjacent segments reuse their common pose.
    private Path pathToPark() {
        return Paths.curve(startPose, parkControlPoint, parkPose)
                .linear(startPose, parkPose);
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
// import static com.pedropathing.ivy.groups.Groups.sequential;
// import static com.pedropathing.ivy.pedro.PedroCommands.follow;
// 
// /**
//  * BIOBUZZ V4 - DRIVE-ONLY CURVED AUTONOMOUS
//  *
//  * Sequence:
//  *
//  * 1. Initialize Pedro at the supplied start pose.
//  * 2. Wait for the alliance-partner start delay.
//  * 3. Follow the original curved Pedro path.
//  * 4. Stop drivetrain movement.
//  * 5. Preserve the final measured pose for TeleOp.
//  *
//  * No shooter, feeder, intake, or other mechanisms
//  * are activated.
//  *
//  * Ivy handles the start delay and path execution.
//  */
// @Autonomous(
//         name = "Alternate-Hold-Park Left (RED)",
//         group = "BIOBUZZ V4"
// )
// public final class AHPL_R extends OpMode {
// 
//     // =====================================================
//     // SECTION 1 - AUTONOMOUS SETTINGS
//     // =====================================================
// 
//     /**
//      * Alliance associated with this Autonomous.
//      *
//      * This is used for the Auto-to-TeleOp pose handoff.
//      * Changing it does not mirror the movement path.
//      *
//      * Verify this against the actual field orientation.
//      */
//     private static final V4Settings.Alliance AUTO_ALLIANCE =
//             V4Settings.Alliance.BLUE;
// 
//     /**
//      * Delay after Autonomous starts before driving.
//      *
//      * Allows an alliance partner to move first.
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
//      * Enable or disable the movement path.
//      *
//      * Set false to test the start delay and localization
//      * without driving.
//      */
//     private static final boolean ENABLE_MOVEMENT = true;
// 
// 
//     // =====================================================
//     // SECTION 2 - PEDRO VISUALIZER COORDINATES
//     // =====================================================
//     //
//     // Original coordinates preserved exactly.
//     // These are literal values, not FieldLibrary poses.
//     // =====================================================
// 
//     private final PoseFactory poseFactory =
//             PoseFactory.degrees();
// 
//     /**
//      * Starting pose.
//      *
//      * X = 59.5515
//      * Y = 132.8639
//      * Heading = 90 degrees
//      */
//     private final Pose start =
//             poseFactory.of(59.5515, 132.8639, 90);
// 
//     /**
//      * Final destination.
//      *
//      * X = 11.6995
//      * Y = 121.4234
//      * Heading = 0 degrees
//      */
//     private final Pose path1End =
//             poseFactory.of(11.6995, 121.4234, 0);
// 
//     /**
//      * Bezier control point.
//      *
//      * X = 67.8627
//      * Y = 108.3747
//      * Heading = 0 degrees
//      */
//     private final Pose path1Control1 =
//             poseFactory.of(67.8627, 108.3747, 0);
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
//      * Creates a nonblocking Ivy delay command.
//      *
//      * Timing begins when Ivy activates the command,
//      * rather than when the command is constructed.
//      *
//      * The Pedro follower continues updating while
//      * this delay is running.
//      *
//      * @param durationMs Delay duration in milliseconds.
//      * @return An Ivy command that finishes after the delay.
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
//     // Ivy runs these actions in sequence.
//     //
//     // No mechanism commands are included.
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
//                 // STEP 2 - FOLLOW ORIGINAL PEDRO CURVE
//                 // -----------------------------------------
// 
//                 ENABLE_MOVEMENT
//                         ? follow(follower, path1())
//                         : waitMilliseconds(0)
//         );
//     }
// 
// 
//     // =====================================================
//     // SECTION 6 - PEDRO PATH DEFINITION
//     // =====================================================
// 
//     /**
//      * PATH 1 - CURVED MOVEMENT
//      *
//      * Starting pose:
//      * (59.5515, 132.8639, 90 degrees)
//      *
//      * Control point:
//      * (67.8627, 108.3747, 0 degrees)
//      *
//      * Final pose:
//      * (11.6995, 121.4234, 0 degrees)
//      *
//      * Uses Pedro's quadratic Bezier curve with linear
//      * heading interpolation from 90 to 0 degrees.
//      *
//      * Geometry is unchanged from the original Auto.
//      *
//      * @return Pedro Path.
//      */
//     private Path path1() {
// 
//         return Paths.curve(
//                 start,
//                 path1Control1,
//                 path1End
//         ).linear(
//                 start,
//                 path1End
//         );
//     }
// 
// 
//     // =====================================================
//     // SECTION 7 - AUTONOMOUS INITIALIZATION
//     // =====================================================
// 
//     @Override
//     public void init() {
// 
//         // Reset Ivy before starting this OpMode.
//         Scheduler.reset();
// 
//         // Invalidate previous Auto-to-TeleOp handoff data.
//         MatchHandoff.beginAuto();
// 
//         // Create the Pedro follower with goBILDA constants.
//         follower = Constants_GoBuilda.create(hardwareMap);
// 
//         // Initialize localization at the supplied pose.
//         follower.setPose(start);
//         follower.update();
// 
//         complete = false;
// 
//         // ---------------------------------------------
//         // INITIALIZATION TELEMETRY
//         // ---------------------------------------------
// 
//         telemetry.addLine("BIOBUZZ V4 - DRIVE-ONLY CURVE");
// 
//         telemetry.addData(
//                 "Alliance",
//                 AUTO_ALLIANCE
//         );
// 
//         telemetry.addData(
//                 "Starting pose",
//                 start
//         );
// 
//         telemetry.addData(
//                 "Start delay (ms)",
//                 AUTO_START_DELAY_MS
//         );
// 
//         telemetry.addData(
//                 "Movement enabled",
//                 ENABLE_MOVEMENT
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
//         // Schedule the Ivy wait-and-drive sequence.
//         routine = autoRoutine();
// 
//         schedule(routine);
//     }
// 
// 
//     // =====================================================
//     // SECTION 9 - AUTONOMOUS PERIODIC LOOP
//     // =====================================================
// 
//     @Override
//     public void loop() {
// 
//         // Update Pedro localization and drivetrain.
//         follower.update();
// 
//         // Execute scheduled Ivy commands.
//         Scheduler.execute();
// 
//         // Save the current measured pose for TeleOp.
//         MatchHandoff.save(
//                 follower.pose(),
//                 AUTO_ALLIANCE
//         );
// 
//         // ---------------------------------------------
//         // DETECT ROUTINE COMPLETION
//         // ---------------------------------------------
// 
//         if (!complete
//                 && routine != null
//                 && !Scheduler.isScheduled(routine)) {
// 
//             complete = true;
// 
//             // Stop drivetrain output.
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
//                     "Remaining path distance",
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
//         // Preserve final localization for the next TeleOp.
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
