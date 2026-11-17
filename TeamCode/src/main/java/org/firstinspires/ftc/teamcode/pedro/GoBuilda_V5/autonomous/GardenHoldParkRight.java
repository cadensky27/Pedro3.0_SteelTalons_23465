package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.autonomous;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.ivy.Command;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

@Autonomous(name = "Garden-Hold-Park Right", group = "BIOBUZZ V5")
public final class GardenHoldParkRight extends BaseAuto {
    // ROUTINE SETTINGS
    private static final long PARTNER_DELAY_MS = 2000;

    // FIELD POSES — inches and degrees in the shared Red-authored field frame.
    private final PoseFactory poses = PoseFactory.degrees();
    private final Pose startPose = poses.of(20.6252, 131.9046, 270);
    private final Pose parkPose = poses.of(12.0103, 93.4555, 0);

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
//  * BIOBUZZ V4 - DRIVE-ONLY AUTONOMOUS
//  *
//  * Sequence:
//  *
//  * 1. Initialize Pedro at the configured starting pose.
//  * 2. Wait for the alliance-partner start delay.
//  * 3. Follow the original straight-line Pedro path.
//  * 4. Stop drivetrain movement.
//  * 5. Save the final measured pose for TeleOp.
//  *
//  * No shooting, feeding, or intake mechanisms are activated.
//  *
//  * Uses Pedro Ivy for the delay and path execution.
//  */
// @Autonomous(
//         name = "Garden-Hold-Park Right (RED)",
//         group = "BIOBUZZ V4"
// )
// public final class GHPR_R extends OpMode {
// 
//     // =====================================================
//     // SECTION 1 - AUTONOMOUS CONFIGURATION
//     // =====================================================
// 
//     /**
//      * Alliance for the Auto-to-TeleOp pose handoff.
//      *
//      * Changing this value does not mirror the path.
//      * Verify the alliance using Pedro Visualizer.
//      */
//     private static final V4Settings.Alliance AUTO_ALLIANCE =
//             V4Settings.Alliance.BLUE;
// 
//     /**
//      * Delay after the Autonomous period begins.
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
//      * Enable the movement path.
//      *
//      * Set false to test the starting pose and delay
//      * without driving.
//      */
//     private static final boolean ENABLE_MOVEMENT = true;
// 
// 
//     // =====================================================
//     // SECTION 2 - PEDRO VISUALIZER COORDINATES
//     // =====================================================
//     //
//     // These values are preserved exactly from the
//     // supplied Pedro Visualizer routine.
//     // =====================================================
// 
//     private final PoseFactory poseFactory =
//             PoseFactory.degrees();
// 
//     /**
//      * Starting pose.
//      *
//      * X = 20.6252
//      * Y = 131.9046
//      * Heading = 270 degrees
//      */
//     private final Pose start =
//             poseFactory.of(20.6252, 131.9046, 270);
// 
//     /**
//      * Final parking pose.
//      *
//      * X = 12.0103
//      * Y = 93.4555
//      * Heading = 0 degrees
//      */
//     private final Pose path1End =
//             poseFactory.of(12.0103, 93.4555, 0);
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
//      * Creates an Ivy delay without blocking the OpMode.
//      *
//      * The timer begins when Ivy starts this command.
//      *
//      * Pedro localization continues updating while
//      * the robot waits.
//      *
//      * @param durationMs Delay in milliseconds.
//      * @return Ivy command that finishes after the delay.
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
//      * Executes the start delay followed by the path.
//      *
//      * Sequential commands run from top to bottom.
//      *
//      * @return Complete Ivy Autonomous routine.
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
//                 // STEP 2 - FOLLOW ORIGINAL PEDRO PATH
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
//     // SECTION 6 - PEDRO PATH
//     // =====================================================
// 
//     /**
//      * Straight-line path with linear heading interpolation.
//      *
//      * Start:
//      * (20.6252, 131.9046, 270 degrees)
//      *
//      * End:
//      * (12.0103, 93.4555, 0 degrees)
//      *
//      * Pedro interpolates the robot heading from the
//      * starting orientation to the ending orientation.
//      *
//      * @return Pedro path.
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
//     // SECTION 7 - INITIALIZATION
//     // =====================================================
// 
//     @Override
//     public void init() {
// 
//         // Clear any previously scheduled Ivy commands.
//         Scheduler.reset();
// 
//         // Invalidate old Auto-to-TeleOp handoff data.
//         MatchHandoff.beginAuto();
// 
//         // Construct the goBILDA Pedro follower.
//         follower = Constants_GoBuilda.create(hardwareMap);
// 
//         // Initialize localization at the starting pose.
//         follower.setPose(start);
//         follower.update();
// 
//         complete = false;
// 
//         // ---------------------------------------------
//         // INITIALIZATION TELEMETRY
//         // ---------------------------------------------
// 
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
//         // Schedule the complete Ivy Autonomous sequence.
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
//         // Update Pedro localization and the drivetrain.
//         follower.update();
// 
//         // Execute the Ivy scheduler.
//         Scheduler.execute();
// 
//         // Continuously save the measured pose for TeleOp.
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
//                     "Path distance remaining",
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
//         // Save the latest measured localization pose.
//         if (follower != null) {
// 
//             MatchHandoff.save(
//                     follower.pose(),
//                     AUTO_ALLIANCE
//             );
//         }
// 
//         // Cancel remaining Ivy commands.
//         Scheduler.reset();
// 
//         // Stop drivetrain movement.
//         if (follower != null) {
//             follower.manual(0, 0, 0);
//         }
//     }
// }
