package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.autonomous;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.ivy.Command;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.commands.MechanismCommands;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.config.V5Settings;

import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

@Autonomous(name = "Primary-Shoot-Park Left", group = "BIOBUZZ V5")
public final class PrimaryShootParkLeft extends BaseAuto {
    // ROUTINE SETTINGS
    private static final long PARTNER_DELAY_MS = 2000;
    private static final long SPINUP_DELAY_MS = V5Settings.Feeder.READY_TIMEOUT_MS;
    private static final long FEED_WALL_TIMEOUT_MS = V5Settings.Feeder.FEED_WALL_TIMEOUT_MS;
    private static final V5Settings.Preset SHOT_PRESET = V5Settings.Preset.NORMAL;
    private static final long FEED_MS = 5000;

    // FIELD POSES — inches and degrees in the shared Red-authored field frame.
    private final PoseFactory poses = PoseFactory.degrees();
    private final Pose startPose = poses.of(58.2431, 7.6262, 270);
    private final Pose parkApproachPose = poses.of(57.4954, 94.6935, 270);
    private final Pose parkPose = poses.of(12.3968, 121.0203, 0);
    private final Pose parkControlPoint = poses.of(59.7877, 131.0324, 0);

    @Override
    protected Pose startingPose() {
        return startPose;
    }

    // MATCH SEQUENCE — Ivy runs these actions from top to bottom.
    @Override
    protected Command buildRoutine() {
        return sequential(
                waitMilliseconds(PARTNER_DELAY_MS),
                MechanismCommands.preset(robot, SHOT_PRESET),
                MechanismCommands.intakeStart(robot),
                MechanismCommands.shooterOn(robot),
                waitMilliseconds(SPINUP_DELAY_MS),
                MechanismCommands.feederForMs(robot, FEED_MS, FEED_WALL_TIMEOUT_MS, false),
                MechanismCommands.intakeStop(robot),
                MechanismCommands.shooterOff(robot),
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
// import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V4v5.commands.MechanismCommands;
// import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V4v5.config.V4Settings;
// import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V4v5.core.MatchHandoff;
// import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V4v5.core.Robot;
// 
// import static com.pedropathing.ivy.Scheduler.schedule;
// import static com.pedropathing.ivy.commands.Commands.instant;
// import static com.pedropathing.ivy.groups.Groups.sequential;
// import static com.pedropathing.ivy.pedro.PedroCommands.follow;
// 
// /**
//  * BIOBUZZ V4 - FOUR PRELOAD AUTO WITH INTAKE
//  *
//  * Autonomous sequence:
//  *
//  * 1. Wait for alliance partner start delay.
//  * 2. Select shooter RPM preset.
//  * 3. Start shooter and intake simultaneously.
//  * 4. Wait for spin-up or RPM readiness.
//  * 5. Feed four preloaded balls.
//  * 6. Stop intake, feeder, and shooter.
//  * 7. Follow the first Pedro path.
//  * 8. Follow the second Pedro path.
//  * 9. Save final localization for TeleOp.
//  *
//  * Uses Pedro Ivy for all timed and path actions.
//  *
//  * The intake includes the linked side rollers through
//  * the existing V4 IntakeSubsystem.
//  *
//  * The shooter interlock defaults to OFF for this Auto.
//  * The original global setting is restored afterward.
//  */
// @Autonomous(
//         name = "Primary-Shoot-Park Left (RED)",
//         group = "BIOBUZZ V4"
// )
// public final class PSPL_R extends OpMode {
// 
//     // =====================================================
//     // SECTION 1 - AUTONOMOUS SETTINGS
//     // =====================================================
//     //
//     // Adjust these settings for this individual Auto.
//     // They do not change the other Autonomous routines.
//     // =====================================================
// 
//     /**
//      * Alliance associated with this path.
//      *
//      * Used for Auto-to-TeleOp localization handoff.
//      * Does not mirror the path.
//      *
//      * RED is assumed from the supplied starting area.
//      * Verify against your Pedro Visualizer layout.
//      */
//     private static final V4Settings.Alliance AUTO_ALLIANCE =
//             V4Settings.Alliance.RED;
// 
//     /**
//      * Delay before shooting or driving.
//      *
//      * Default: 2000 ms = 2 seconds.
//      *
//      * Set to zero for no delay.
//      */
//     private static final long AUTO_START_DELAY_MS = 2000;
// 
//     /**
//      * Master toggle for shooting the four preloads.
//      *
//      * Set false to test movement without shooting.
//      */
//     private static final boolean ENABLE_PRELOAD_SHOOTING = true;
// 
//     /**
//      * Run the main intake and linked side rollers
//      * during the shooting sequence.
//      *
//      * Intake starts when shooter spin-up begins and
//      * stops after feeding completes.
//      *
//      * Requires ENABLE_PRELOAD_SHOOTING = true.
//      */
//     private static final boolean RUN_INTAKE_WHILE_SHOOTING = true;
// 
//     /**
//      * Enable RPM-based feeder interlock.
//      *
//      * false:
//      * - Fixed spin-up delay.
//      * - Feeder can run below target RPM.
//      * - No RPM-based pause during feeding.
//      *
//      * true:
//      * - Wait for stable shooter readiness.
//      * - Skip feeding if readiness times out.
//      * - Allow automatic feeder RPM recovery behavior.
//      *
//      * Default: false for Autonomous.
//      */
//     private static final boolean AUTO_SHOOTER_INTERLOCK = false;
// 
//     /**
//      * Shooter preset.
//      *
//      * NORMAL: 2950 RPM
//      * CLOSE:  2750 RPM
//      * ANGLED: 3150 RPM
//      *
//      * Actual targets are defined in V4Settings.
//      */
//     private static final V4Settings.Preset SHOOTER_PRESET =
//             V4Settings.Preset.NORMAL;
// 
//     /**
//      * Fixed spin-up duration when RPM interlock is OFF.
//      *
//      * Defaults to established V4 readiness timeout:
//      * 3500 milliseconds.
//      */
//     private static final long SHOOTER_SPINUP_DELAY_MS =
//             V4Settings.Feeder.READY_TIMEOUT_MS;
// 
//     /**
//      * Active feeder-running duration for four preloads.
//      *
//      * Default: 2800 milliseconds.
//      *
//      * This is a timed feed, not a physical ball count.
//      * Tune after testing all four balls.
//      */
//     private static final long FOUR_PRELOAD_FEED_MS = 5000;
// 
//     /**
//      * Maximum elapsed feeding duration.
//      *
//      * Default: 6000 milliseconds.
//      *
//      * Protects against a feeder command waiting forever.
//      */
//     private static final long FEED_WALL_TIMEOUT_MS =
//             V4Settings.Feeder.FEED_WALL_TIMEOUT_MS;
// 
//     /**
//      * Stop the shooter after preload feeding.
//      *
//      * true:
//      * Stop the flywheel before movement.
//      *
//      * false:
//      * Keep the flywheel running during movement.
//      * The final robot shutdown still stops it.
//      */
//     private static final boolean STOP_SHOOTER_BEFORE_DRIVING = true;
// 
//     /**
//      * Enable the first movement path.
//      */
//     private static final boolean ENABLE_PATH_1 = true;
// 
//     /**
//      * Enable the second movement path.
//      *
//      * Path 2 assumes the robot reached Path 1's endpoint.
//      */
//     private static final boolean ENABLE_PATH_2 = true;
// 
//     /**
//      * Startup flower arm position, if installed.
//      *
//      * The sorting gate is raised automatically by
//      * Robot.startAuto().
//      */
//     private static final V4Settings.ArmPosition AUTO_ARM_START =
//             V4Settings.ArmPosition.UP;
// 
// 
//     // =====================================================
//     // SECTION 2 - PEDRO VISUALIZER COORDINATES
//     // =====================================================
//     //
//     // Coordinates are preserved exactly from the original
//     // user-supplied two-path routine.
//     //
//     // These are literal values for now.
//     // =====================================================
// 
//     private final PoseFactory poseFactory =
//             PoseFactory.degrees();
// 
//     /**
//      * Autonomous starting pose.
//      */
//     private final Pose start =
//             poseFactory.of(58.2431, 7.6262, 270);
// 
//     /**
//      * Endpoint of Path 1.
//      */
//     private final Pose path1End =
//             poseFactory.of(57.4954, 94.6935, 270);
// 
//     /**
//      * Starting point of Path 2.
//      *
//      * Identical to Path 1's endpoint.
//      */
//     private final Pose point2Start =
//             poseFactory.of(57.4954, 94.6935, 270);
// 
//     /**
//      * Final destination of Path 2.
//      */
//     private final Pose point2End =
//             poseFactory.of(12.3968, 121.0203, 0);
// 
//     /**
//      * Bezier control point for Path 2.
//      */
//     private final Pose point2Control1 =
//             poseFactory.of(59.7877, 131.0324, 0);
// 
// 
//     // =====================================================
//     // SECTION 3 - ROBOT OBJECTS
//     // =====================================================
// 
//     private Follower follower;
//     private Robot robot;
//     private Command routine;
// 
//     private boolean complete = false;
// 
//     /**
//      * Preserves the original V4 shooter interlock.
//      *
//      * Each Auto can override the setting temporarily,
//      * but must restore it when finished.
//      */
//     private boolean originalShooterInterlock;
//     private boolean interlockOverridden = false;
// 
// 
//     // =====================================================
//     // SECTION 4 - NONBLOCKING IVY DELAY
//     // =====================================================
// 
//     /**
//      * Create a nonblocking Ivy timer.
//      *
//      * Timing begins when the command starts, not when
//      * the command object is constructed.
//      *
//      * Pedro and robot mechanisms continue updating.
//      *
//      * @param durationMs Delay duration in milliseconds.
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
//     // SECTION 5 - FOUR-PRELOAD SHOOTING
//     // =====================================================
// 
//     /**
//      * Run the V4 four-preload shooting sequence.
//      *
//      * The intake starts before shooting and stays on
//      * through spin-up, feeding, and any RPM pauses.
//      *
//      * When the RPM interlock is OFF:
//      *
//      * - Start shooter.
//      * - Wait fixed spin-up duration.
//      * - Feed for the configured active duration.
//      *
//      * When the RPM interlock is ON:
//      *
//      * - Start shooter.
//      * - Wait for stable shooter readiness.
//      * - Skip feeding if readiness times out.
//      * - Manage RPM recovery during feeding.
//      *
//      * The optional shooter gate is controlled internally
//      * by the feeder subsystem.
//      *
//      * @return Ivy shooting command.
//      */
//     private Command shootFourPreloads() {
// 
//         return sequential(
// 
//                 // -----------------------------------------
//                 // STEP A - START MAIN INTAKE + SIDE ROLLERS
//                 // -----------------------------------------
// 
//                 RUN_INTAKE_WHILE_SHOOTING
//                         ? MechanismCommands.intakeStart(robot)
//                         : instant(() -> {}),
// 
//                 // -----------------------------------------
//                 // STEP B - START SHOOTER
//                 // -----------------------------------------
// 
//                 MechanismCommands.shooterOn(robot),
// 
//                 // -----------------------------------------
//                 // STEP C - SPIN UP AND FEED
//                 // -----------------------------------------
// 
//                 AUTO_SHOOTER_INTERLOCK
//                         ? sequential(
// 
//                         // Enforces RPM readiness and
//                         // feeding timeout protections.
//                         MechanismCommands.shootPreloads(
//                                 robot,
//                                 FOUR_PRELOAD_FEED_MS
//                         )
//                 )
//                         : sequential(
// 
//                         // Fixed delay, no RPM check.
//                         waitMilliseconds(
//                                 SHOOTER_SPINUP_DELAY_MS
//                         ),
// 
//                         // Timed feed with wall timeout.
//                         MechanismCommands.feederForMs(
//                                 robot,
//                                 FOUR_PRELOAD_FEED_MS,
//                                 FEED_WALL_TIMEOUT_MS
//                         )
//                 ),
// 
//                 // -----------------------------------------
//                 // STEP D - STOP FEEDER
//                 // -----------------------------------------
// 
//                 MechanismCommands.feederStop(robot),
// 
//                 // -----------------------------------------
//                 // STEP E - STOP INTAKE + SIDE ROLLERS
//                 // -----------------------------------------
// 
//                 MechanismCommands.intakeStop(robot)
//         );
//     }
// 
// 
//     // =====================================================
//     // SECTION 6 - MAIN AUTONOMOUS ROUTINE
//     // =====================================================
//     //
//     // EDIT THIS SECTION TO CHANGE THE ACTION ORDER.
//     //
//     // Ivy sequential() waits for each command to finish
//     // before starting the next command.
//     // =====================================================
// 
//     private Command autoRoutine() {
// 
//         return sequential(
// 
//                 // -----------------------------------------
//                 // STEP 0 - ALLIANCE PARTNER DELAY
//                 // -----------------------------------------
// 
//                 waitMilliseconds(
//                         AUTO_START_DELAY_MS
//                 ),
// 
//                 // -----------------------------------------
//                 // STEP 1 - SELECT SHOOTER PRESET
//                 // -----------------------------------------
// 
//                 MechanismCommands.preset(
//                         robot,
//                         SHOOTER_PRESET
//                 ),
// 
//                 // -----------------------------------------
//                 // STEP 2 - SHOOT WITH INTAKE RUNNING
//                 // -----------------------------------------
// 
//                 ENABLE_PRELOAD_SHOOTING
//                         ? shootFourPreloads()
//                         : instant(() -> {}),
// 
//                 // -----------------------------------------
//                 // STEP 3 - SAFETY MECHANISM SHUTDOWN
//                 // -----------------------------------------
// 
//                 MechanismCommands.feederStop(robot),
// 
//                 MechanismCommands.intakeStop(robot),
// 
//                 STOP_SHOOTER_BEFORE_DRIVING
//                         ? MechanismCommands.shooterOff(robot)
//                         : instant(() -> {}),
// 
//                 // -----------------------------------------
//                 // STEP 4 - FIRST PEDRO PATH
//                 // -----------------------------------------
// 
//                 ENABLE_PATH_1
//                         ? follow(follower, path1())
//                         : instant(() -> {}),
// 
//                 // -----------------------------------------
//                 // STEP 5 - SECOND PEDRO PATH
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
//     // SECTION 7 - PEDRO PATH DEFINITIONS
//     // =====================================================
// 
//     /**
//      * PATH 1
//      *
//      * Straight-line movement.
//      *
//      * Start:
//      * (58.2431, 7.6262, 270 degrees)
//      *
//      * End:
//      * (57.4954, 94.6935, 270 degrees)
//      *
//      * Heading remains 270 degrees.
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
//      * Curved movement using one Bezier control point.
//      *
//      * Start:
//      * (57.4954, 94.6935, 270 degrees)
//      *
//      * Control:
//      * (59.7877, 131.0324, 0 degrees)
//      *
//      * End:
//      * (12.3968, 121.0203, 0 degrees)
//      *
//      * Heading transitions from 270 to 0 degrees
//      * using Pedro's linear heading interpolation.
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
//     // SECTION 8 - AUTONOMOUS INITIALIZATION
//     // =====================================================
// 
//     @Override
//     public void init() {
// 
//         // Clear commands from any previous OpMode.
//         Scheduler.reset();
// 
//         // Invalidate any old Auto-to-TeleOp handoff.
//         MatchHandoff.beginAuto();
// 
//         // Create the Pedro follower.
//         follower = Constants_GoBuilda.create(hardwareMap);
// 
//         // Set starting localization.
//         follower.setPose(start);
//         follower.update();
// 
//         // Initialize the V4 mechanism container.
//         robot = new Robot(hardwareMap);
// 
//         // Preserve the original interlock setting.
//         originalShooterInterlock =
//                 V4Settings.Shooter.INTERLOCK;
// 
//         interlockOverridden = false;
//         complete = false;
// 
//         // ---------------------------------------------
//         // DRIVER STATION INITIALIZATION TELEMETRY
//         // ---------------------------------------------
// 
//         telemetry.addLine("BIOBUZZ V4 - SHOOT + INTAKE AUTO");
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
//                 "Preload shooting",
//                 ENABLE_PRELOAD_SHOOTING
//         );
// 
//         telemetry.addData(
//                 "Intake while shooting",
//                 RUN_INTAKE_WHILE_SHOOTING
//         );
// 
//         telemetry.addData(
//                 "Shooter interlock",
//                 AUTO_SHOOTER_INTERLOCK
//         );
// 
//         telemetry.addData(
//                 "Shooter preset",
//                 SHOOTER_PRESET
//         );
// 
//         telemetry.addData(
//                 "Shooter spin-up (ms)",
//                 SHOOTER_SPINUP_DELAY_MS
//         );
// 
//         telemetry.addData(
//                 "Active feed (ms)",
//                 FOUR_PRELOAD_FEED_MS
//         );
// 
//         telemetry.addData(
//                 "Feed timeout (ms)",
//                 FEED_WALL_TIMEOUT_MS
//         );
// 
//         telemetry.addData(
//                 "Path 1",
//                 ENABLE_PATH_1
//         );
// 
//         telemetry.addData(
//                 "Path 2",
//                 ENABLE_PATH_1 && ENABLE_PATH_2
//         );
// 
//         telemetry.addData(
//                 "Robot healthy",
//                 robot.healthy()
//         );
// 
//         telemetry.addData(
//                 "Hardware warnings",
//                 robot.critical()
//         );
// 
//         telemetry.update();
//     }
// 
// 
//     // =====================================================
//     // SECTION 9 - AUTONOMOUS START
//     // =====================================================
// 
//     @Override
//     public void start() {
// 
//         // Set safe mechanism startup conditions.
//         //
//         // Shooter OFF
//         // Feeder OFF
//         // Intake OFF
//         // Sorting gate UP (if installed)
//         // Flower arm in configured position
//         robot.startAuto(AUTO_ARM_START);
// 
//         // Apply this Auto's shooter interlock preference.
//         V4Settings.Shooter.INTERLOCK =
//                 AUTO_SHOOTER_INTERLOCK;
// 
//         interlockOverridden = true;
// 
//         // Build and schedule the Ivy sequence.
//         routine = autoRoutine();
// 
//         schedule(routine);
//     }
// 
// 
//     // =====================================================
//     // SECTION 10 - PERIODIC AUTONOMOUS LOOP
//     // =====================================================
// 
//     @Override
//     public void loop() {
// 
//         // Update Pedro localization and movement.
//         follower.update();
// 
//         // Update shooter, feeder, and other mechanisms.
//         robot.update();
// 
//         // Execute scheduled Ivy actions.
//         Scheduler.execute();
// 
//         // Continuously preserve measured field position.
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
//             // Stop every installed mechanism.
//             robot.stop();
// 
//             // Stop drivetrain output.
//             follower.manual(0, 0, 0);
// 
//             // Restore TeleOp interlock behavior.
//             restoreShooterInterlock();
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
//         telemetry.addData(
//                 "Shooter interlock",
//                 V4Settings.Shooter.INTERLOCK
//         );
// 
//         if (robot.shooter != null) {
// 
//             telemetry.addData(
//                     "Shooter target RPM",
//                     robot.shooter.target()
//             );
// 
//             telemetry.addData(
//                     "Shooter actual RPM",
//                     robot.shooter.actual()
//             );
// 
//             telemetry.addData(
//                     "Shooter ready",
//                     robot.shooter.ready()
//             );
//         }
// 
//         if (robot.feeder != null) {
// 
//             telemetry.addData(
//                     "Feeder state",
//                     robot.feeder.state()
//             );
// 
//             telemetry.addData(
//                     "Active feed seconds",
//                     robot.feeder.forwardSeconds()
//             );
//         }
// 
//         if (robot.intake != null) {
// 
//             telemetry.addData(
//                     "Intake during shooting",
//                     RUN_INTAKE_WHILE_SHOOTING
//             );
//         }
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
//     // SECTION 11 - RESTORE SHOOTER INTERLOCK
//     // =====================================================
// 
//     /**
//      * Restore the original global shooter interlock.
//      *
//      * Prevents this Autonomous from accidentally leaving
//      * the interlock disabled during the next TeleOp.
//      */
//     private void restoreShooterInterlock() {
// 
//         if (interlockOverridden) {
// 
//             V4Settings.Shooter.INTERLOCK =
//                     originalShooterInterlock;
// 
//             interlockOverridden = false;
//         }
//     }
// 
// 
//     // =====================================================
//     // SECTION 12 - AUTONOMOUS STOP
//     // =====================================================
// 
//     @Override
//     public void stop() {
// 
//         // Save the final measured localization pose.
//         if (follower != null) {
// 
//             MatchHandoff.save(
//                     follower.pose(),
//                     AUTO_ALLIANCE
//             );
//         }
// 
//         // Cancel remaining scheduled Ivy commands.
//         Scheduler.reset();
// 
//         // Stop all mechanism hardware.
//         if (robot != null) {
//             robot.stop();
//         }
// 
//         // Stop the drivetrain.
//         if (follower != null) {
//             follower.manual(0, 0, 0);
//         }
// 
//         // Restore normal shooter interlock behavior.
//         restoreShooterInterlock();
//     }
// }
