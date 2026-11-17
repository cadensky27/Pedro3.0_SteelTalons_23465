package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.autonomous;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.ivy.Command;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

@Autonomous(name = "Garden-Hold-Park Left", group = "BIOBUZZ V5")
public final class GardenHoldParkLeft extends BaseAuto {
    // ROUTINE SETTINGS
    private static final long PARTNER_DELAY_MS = 2000;

    // FIELD POSES — inches and degrees in the shared Red-authored field frame.
    private final PoseFactory poses = PoseFactory.degrees();
    private final Pose startPose = poses.of(22.9148, 132.677, 270);
    private final Pose parkPose = poses.of(13.3818, 121.0495, 0);

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
        return Paths.line(startPose, parkPose)
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
//  * BIOBUZZ V4 - SIMPLE DELAY AND PARK AUTONOMOUS
//  *
//  * Sequence:
//  *
//  * 1. Initialize Pedro at the starting pose.
//  * 2. Wait for the alliance partner.
//  * 3. Follow the original straight path.
//  * 4. Stop drivetrain movement.
//  * 5. Preserve the final measured pose for TeleOp.
//  *
//  * No shooting, feeding, or intake actions occur.
//  *
//  * All timing and movement use Pedro Ivy.
//  */
// @Autonomous(
//         name = "Garden-Hold-Park Left (RED)",
//         group = "BIOBUZZ V4"
// )
// public final class GHPL_R extends OpMode {
// 
//     // =====================================================
//     // SECTION 1 - AUTONOMOUS CONFIGURATION
//     // =====================================================
// 
//     /**
//      * Alliance for the Auto-to-TeleOp pose handoff.
//      *
//      * Set according to the actual starting side.
//      *
//      * Changing this does not mirror the movement path.
//      */
//     private static final V4Settings.Alliance AUTO_ALLIANCE =
//             V4Settings.Alliance.BLUE;
// 
//     /**
//      * Delay before the robot starts driving.
//      *
//      * Allows an alliance partner to move first.
//      *
//      * 0    = No delay
//      * 1000 = 1 second
//      * 2000 = 2 seconds
//      * 3000 = 3 seconds
//      * 5000 = 5 seconds
//      *
//      * Default: 2000 milliseconds.
//      */
//     private static final long AUTO_START_DELAY_MS = 2000;
// 
//     /**
//      * Master toggle for the movement path.
//      *
//      * Set false to test the delay without moving.
//      */
//     private static final boolean ENABLE_MOVEMENT = true;
// 
// 
//     // =====================================================
//     // SECTION 2 - ORIGINAL PEDRO COORDINATES
//     // =====================================================
//     //
//     // Preserved exactly from the supplied routine.
//     // =====================================================
// 
//     private final PoseFactory poseFactory =
//             PoseFactory.degrees();
// 
//     /**
//      * Autonomous starting pose.
//      *
//      * X = 22.9148
//      * Y = 132.677
//      * Heading = 270 degrees
//      */
//     private final Pose start =
//             poseFactory.of(22.9148, 132.677, 270);
// 
//     /**
//      * Final parking pose.
//      *
//      * X = 13.3818
//      * Y = 121.0495
//      * Heading = 0 degrees
//      */
//     private final Pose path1End =
//             poseFactory.of(13.3818, 121.0495, 0);
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
//      * Creates a nonblocking Ivy delay.
//      *
//      * Unlike Thread.sleep(), it does not block follower
//      * updates or the command scheduler.
//      *
//      * @param durationMs Desired delay in milliseconds.
//      * @return Ivy command that completes after the delay.
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
// 
//     /**
//      * Waits for the alliance partner before following
//      * the original straight path.
//      *
//      * @return Complete Ivy routine.
//      */
//     private Command autoRoutine() {
// 
//         return sequential(
// 
//                 // -----------------------------------------
//                 // STEP 1 - ALLIANCE PARTNER START DELAY
//                 // -----------------------------------------
// 
//                 waitMilliseconds(
//                         AUTO_START_DELAY_MS
//                 ),
// 
//                 // -----------------------------------------
//                 // STEP 2 - DRIVE TO PARK
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
//     // SECTION 6 - ORIGINAL PEDRO PATH
//     // =====================================================
// 
//     /**
//      * Straight path with linear heading interpolation.
//      *
//      * Start:
//      * (22.9148, 132.677, 270 degrees)
//      *
//      * End:
//      * (13.3818, 121.0495, 0 degrees)
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
// 
//     // =====================================================
//     // SECTION 7 - AUTONOMOUS INITIALIZATION
//     // =====================================================
// 
//     @Override
//     public void init() {
// 
//         // Reset any previously scheduled Ivy commands.
//         Scheduler.reset();
// 
//         // Clear the previous Auto-to-TeleOp handoff.
//         MatchHandoff.beginAuto();
// 
//         // Initialize Pedro using renamed constants class.
//         follower = Constants_GoBuilda.create(hardwareMap);
// 
//         // Set the original Autonomous starting pose.
//         follower.setPose(start);
//         follower.update();
// 
//         complete = false;
// 
//         // ---------------------------------------------
//         // DRIVER STATION INIT TELEMETRY
//         // ---------------------------------------------
// 
//         telemetry.addLine("BIOBUZZ V4 - SIMPLE DELAY PARK");
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
//         // Schedule the delayed movement sequence.
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
//         // Update Pedro localization and path follower.
//         follower.update();
// 
//         // Execute Ivy commands.
//         Scheduler.execute();
// 
//         // Continuously save the measured robot pose.
//         MatchHandoff.save(
//                 follower.pose(),
//                 AUTO_ALLIANCE
//         );
// 
//         // Detect completion of the movement sequence.
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
//         // Preserve final localization for TeleOp.
//         if (follower != null) {
// 
//             MatchHandoff.save(
//                     follower.pose(),
//                     AUTO_ALLIANCE
//             );
//         }
// 
//         // Cancel any remaining Ivy commands.
//         Scheduler.reset();
// 
//         // Stop drivetrain movement.
//         if (follower != null) {
//             follower.manual(0, 0, 0);
//         }
//     }
// }
// 
