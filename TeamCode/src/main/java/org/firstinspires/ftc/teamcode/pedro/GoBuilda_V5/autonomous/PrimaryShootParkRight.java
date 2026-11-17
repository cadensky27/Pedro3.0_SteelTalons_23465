package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.autonomous;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.ivy.Command;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.commands.MechanismCommands;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.config.V4Settings;

import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

@Autonomous(name = "Primary-Shoot-Park Right", group = "BIOBUZZ V5")
public final class PrimaryShootParkRight extends BaseAuto {
    // ROUTINE SETTINGS
    private static final long SPINUP_DELAY_MS = V4Settings.Feeder.READY_TIMEOUT_MS;
    private static final long FEED_WALL_TIMEOUT_MS = V4Settings.Feeder.FEED_WALL_TIMEOUT_MS;
    private static final V4Settings.Preset SHOT_PRESET = V4Settings.Preset.NORMAL;
    private static final long FEED_MS = 5000;

    // FIELD POSES — inches and degrees in the shared Red-authored field frame.
    private final PoseFactory poses = PoseFactory.degrees();
    private final Pose startPose = poses.of(57.4954, 9.4954, 270);
    private final Pose parkPose = poses.of(12.0733, 93.1982, 0);
    private final Pose parkControlPoint = poses.of(14.8677, 39.4808, 0);

    @Override
    protected Pose startingPose() {
        return startPose;
    }

    // MATCH SEQUENCE — Ivy runs these actions from top to bottom.
    @Override
    protected Command buildRoutine() {
        return sequential(
                MechanismCommands.preset(robot, SHOT_PRESET),
                MechanismCommands.shooterOn(robot),
                waitMilliseconds(SPINUP_DELAY_MS),
                MechanismCommands.feederForMs(robot, FEED_MS, FEED_WALL_TIMEOUT_MS, false),
                MechanismCommands.shooterOff(robot),
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
// import static com.pedropathing.ivy.Scheduler.schedule;
// import static com.pedropathing.ivy.commands.Commands.instant;
// import static com.pedropathing.ivy.groups.Groups.sequential;
// import static com.pedropathing.ivy.pedro.PedroCommands.follow;
// 
// import com.pedropathing.api.Paths;
// import com.pedropathing.api.PoseFactory;
// import com.pedropathing.follower.Follower;
// import com.pedropathing.ivy.Command;
// import com.pedropathing.ivy.Scheduler;
// import com.pedropathing.math.Pose;
// import com.pedropathing.paths.Path;
// import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
// import com.qualcomm.robotcore.eventloop.opmode.OpMode;
// 
// import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V4v5.Constants_GoBuilda;
// import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V4v5.commands.MechanismCommands;
// import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V4v5.config.V4Settings;
// import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V4v5.core.MatchHandoff;
// import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V4v5.core.Robot;
// 
// /**
//  * BIOBUZZ V4 - FOUR PRELOAD AUTONOMOUS
//  *
//  * Sequence:
//  * 1. Initialize Pedro and all installed mechanisms.
//  * 2. Select the requested shooter RPM preset.
//  * 3. Spin up the shooter.
//  * 4. Shoot the four preloaded balls.
//  * 5. Stop the feeder and optionally stop the shooter.
//  * 6. Follow the original Pedro path.
//  * 7. Save the final robot pose for TeleOp.
//  *
//  * All actions use Pedro Ivy commands.
//  *
//  * SHOOTER INTERLOCK:
//  * Disabled by default for this Autonomous.
//  *
//  * When disabled, the feeder starts after a fixed spin-up
//  * delay without checking whether the shooter is ready.
//  *
//  * When enabled, the existing V4 shootPreloads command
//  * enforces RPM readiness and timeout protections.
//  *
//  * NOTE:
//  * The existing V4 interlock setting is global/static.
//  * This OpMode temporarily changes it and restores the
//  * previous value when Autonomous finishes or stops.
//  */
// @Autonomous(
//         name = "Primary-Shoot-Park Right (RED)",
//         group = "BIOBUZZ V4"
// )
// public final class PSPR_R extends OpMode {
// 
//     // =====================================================
//     // SECTION 1 - AUTONOMOUS CONFIGURATION
//     // =====================================================
//     //
//     // Adjust these settings independently for each Auto.
//     // No changes to V4Settings should be necessary.
//     // =====================================================
// 
//     /**
//      * Alliance used for the Auto-to-TeleOp pose handoff.
//      *
//      * Changing the alliance does NOT mirror the path.
//      */
//     private static final V4Settings.Alliance AUTO_ALLIANCE =
//             V4Settings.Alliance.RED;
// 
//     /**
//      * Shooter RPM preset.
//      *
//      * NORMAL: 2950 RPM
//      * CLOSE:  2750 RPM
//      * ANGLED: 3150 RPM
//      *
//      * Actual values come from V4Settings.
//      */
//     private static final V4Settings.Preset SHOOTER_PRESET =
//             V4Settings.Preset.NORMAL;
// 
//     /**
//      * Enable automatic shooting of the four preloaded balls.
//      */
//     private static final boolean ENABLE_PRELOAD_SHOOTING = true;
// 
//     /**
//      * Enable shooter RPM interlock in this Auto.
//      *
//      * false:
//      * - Wait a fixed spin-up delay.
//      * - Feed regardless of actual RPM.
//      * - Do not pause feeding for RPM drops.
//      *
//      * true:
//      * - Wait until RPM is stable.
//      * - Skip feeding if readiness times out.
//      * - Pause/resume feeding as necessary.
//      */
//     private static final boolean AUTO_SHOOTER_INTERLOCK = false;
// 
//     /**
//      * Delay before feeding when the interlock is disabled.
//      *
//      * Defaults to the established V4 readiness timeout:
//      * 3500 milliseconds.
//      *
//      * Unlike an RPM readiness check, this is a fixed delay.
//      */
//     private static final long SHOOTER_SPINUP_DELAY_MS =
//             V4Settings.Feeder.READY_TIMEOUT_MS;
// 
//     /**
//      * Total ACTIVE feeder-running time for four preloads.
//      *
//      * This is not a ball counter.
//      *
//      * Increase if the last ball is not consistently fired.
//      */
//     private static final long FOUR_PRELOAD_FEED_MS = 5000;
// 
//     /**
//      * Maximum elapsed time allowed for the feeding command.
//      *
//      * Default: 6000 milliseconds.
//      */
//     private static final long FEED_WALL_TIMEOUT_MS =
//             V4Settings.Feeder.FEED_WALL_TIMEOUT_MS;
// 
//     /**
//      * Stop the shooter after the preloads finish.
//      *
//      * If false, the shooter stays on during path movement.
//      * The robot still stops all mechanisms at Auto completion.
//      */
//     private static final boolean STOP_SHOOTER_BEFORE_DRIVING = true;
// 
//     /**
//      * Enable the movement path after shooting.
//      *
//      * Set false to test shooting without driving.
//      */
//     private static final boolean ENABLE_MOVEMENT = true;
// 
//     /**
//      * Starting flower arm position, if installed.
//      *
//      * Sorting gate is automatically raised in startAuto().
//      */
//     private static final V4Settings.ArmPosition AUTO_ARM_START =
//             V4Settings.ArmPosition.UP;
// 
// 
//     // =====================================================
//     // SECTION 2 - PEDRO FIELD COORDINATES
//     // =====================================================
//     //
//     // These are the ORIGINAL coordinates you provided.
//     //
//     // We will replace them with FieldLibrary constants
//     // later, after learning the mechanism command system.
//     // =====================================================
// 
//     private final PoseFactory poseFactory =
//             PoseFactory.degrees();
// 
//     /** Physical starting position for this Auto. */
//     private final Pose start =
//             poseFactory.of(57.4954, 9.4954, 270);
// 
//     /** Final position of the movement path. */
//     private final Pose path1 =
//             poseFactory.of(12.0733, 93.1982, 0);
// 
//     /** Control point for the curved movement path. */
//     private final Pose path1Control1 =
//             poseFactory.of(14.8677, 39.4808, 0);
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
//      * Original global interlock value.
//      *
//      * Preserved so this Auto does not permanently change
//      * the normal TeleOp interlock behavior.
//      */
//     private boolean originalShooterInterlock;
//     private boolean interlockOverridden = false;
// 
// 
//     // =====================================================
//     // SECTION 4 - IVY HELPER COMMANDS
//     // =====================================================
// 
//     /**
//      * Creates a nonblocking Ivy delay.
//      *
//      * Unlike Thread.sleep(), this does not stop the robot's
//      * normal follower, subsystem, or scheduler updates.
//      */
//     private Command waitMilliseconds(long durationMs) {
// 
//         final long[] startTime = {0};
// 
//         return Command.build()
//                 .setStart(() -> {
//                     startTime[0] = System.nanoTime();
//                 })
//                 .setDone(() -> {
//                     long elapsedMs =
//                             (System.nanoTime() - startTime[0])
//                                     / 1_000_000L;
// 
//                     return elapsedMs >= durationMs;
//                 });
//     }
// 
//     /**
//      * Shoots the preloaded balls using the selected
//      * interlock behavior.
//      *
//      * INTERLOCK ENABLED:
//      * The V4 mechanism library handles shooter spin-up,
//      * RPM readiness, feeding, and timeout behavior.
//      *
//      * INTERLOCK DISABLED:
//      * Uses a fixed spin-up delay and then commands the
//      * feeder to run for the requested active duration.
//      *
//      * The FeederSubsystem still handles the optional
//      * shooter gate opening delay automatically.
//      */
//     private Command shootFourPreloads() {
// 
//         if (AUTO_SHOOTER_INTERLOCK) {
// 
//             return MechanismCommands.shootPreloads(
//                     robot,
//                     FOUR_PRELOAD_FEED_MS
//             );
//         }
// 
//         return sequential(
// 
//                 // Start the flywheel.
//                 MechanismCommands.shooterOn(robot),
// 
//                 // Allow the flywheel to accelerate.
//                 waitMilliseconds(SHOOTER_SPINUP_DELAY_MS),
// 
//                 // Feed without RPM-based restrictions.
//                 MechanismCommands.feederForMs(
//                         robot,
//                         FOUR_PRELOAD_FEED_MS,
//                         FEED_WALL_TIMEOUT_MS
//                 )
//         );
//     }
// 
// 
//     // =====================================================
//     // SECTION 5 - IVY AUTONOMOUS ROUTINE
//     // =====================================================
//     //
//     // THIS IS THE MAIN ACTION SEQUENCE.
//     //
//     // Ivy sequential() executes commands in order.
//     // Each command must finish before the next starts.
//     // =====================================================
// 
//     private Command autoRoutine() {
// 
//         return sequential(
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
//                 // STEP 2 - SHOOT FOUR PRELOADS
//                 // -----------------------------------------
// 
//                 ENABLE_PRELOAD_SHOOTING
//                         ? shootFourPreloads()
//                         : instant(() -> {}),
// 
//                 // -----------------------------------------
//                 // STEP 3 - STOP FEEDER
//                 // -----------------------------------------
// 
//                 MechanismCommands.feederStop(robot),
// 
//                 // -----------------------------------------
//                 // STEP 4 - OPTIONAL SHOOTER STOP
//                 // -----------------------------------------
// 
//                 STOP_SHOOTER_BEFORE_DRIVING
//                         ? MechanismCommands.shooterOff(robot)
//                         : instant(() -> {}),
// 
//                 // -----------------------------------------
//                 // STEP 5 - EXECUTE ORIGINAL PEDRO PATH
//                 // -----------------------------------------
// 
//                 ENABLE_MOVEMENT
//                         ? follow(follower, path1())
//                         : instant(() -> {})
//         );
//     }
// 
// 
//     // =====================================================
//     // SECTION 6 - PEDRO PATH DEFINITION
//     // =====================================================
// 
//     /**
//      * Original curved path with linear heading interpolation.
//      *
//      * Geometry preserved from your supplied AutoPath.
//      */
//     private Path path1() {
// 
//         return Paths.curve(
//                 start,
//                 path1Control1,
//                 path1
//         ).linear(
//                 start,
//                 path1
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
//         // Clear any previously scheduled Ivy commands.
//         Scheduler.reset();
// 
//         // Prevent a stale previous Auto pose from being
//         // transferred to TeleOp.
//         MatchHandoff.beginAuto();
// 
//         // Construct the Pedro follower.
//         follower = Constants_GoBuilda.create(hardwareMap);
// 
//         // Set the initial localization pose.
//         follower.setPose(start);
//         follower.update();
// 
//         // Initialize V4 mechanisms.
//         robot = new Robot(hardwareMap);
// 
//         // Preserve the standard global interlock setting.
//         originalShooterInterlock =
//                 V4Settings.Shooter.INTERLOCK;
// 
//         complete = false;
//         interlockOverridden = false;
// 
//         // INIT telemetry.
//         telemetry.addLine("BIOBUZZ V4 - FOUR PRELOAD AUTO");
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
//                 "Shooter preset",
//                 SHOOTER_PRESET
//         );
// 
//         telemetry.addData(
//                 "Auto shooter interlock",
//                 AUTO_SHOOTER_INTERLOCK
//         );
// 
//         telemetry.addData(
//                 "Spin-up delay (ms)",
//                 SHOOTER_SPINUP_DELAY_MS
//         );
// 
//         telemetry.addData(
//                 "Active feed time (ms)",
//                 FOUR_PRELOAD_FEED_MS
//         );
// 
//         telemetry.addData(
//                 "Feed wall timeout (ms)",
//                 FEED_WALL_TIMEOUT_MS
//         );
// 
//         telemetry.addData(
//                 "Movement enabled",
//                 ENABLE_MOVEMENT
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
//     // SECTION 8 - START AUTONOMOUS
//     // =====================================================
// 
//     @Override
//     public void start() {
// 
//         // Configure mechanisms for autonomous operation.
//         robot.startAuto(AUTO_ARM_START);
// 
//         // Apply this Auto's independent interlock choice.
//         //
//         // This must be set before the feeder begins.
//         V4Settings.Shooter.INTERLOCK =
//                 AUTO_SHOOTER_INTERLOCK;
// 
//         interlockOverridden = true;
// 
//         // Create and schedule the complete Ivy routine.
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
//         // Update Pedro localization and path following.
//         follower.update();
// 
//         // Update shooter RPM and feeder state machines.
//         robot.update();
// 
//         // Execute scheduled Ivy commands.
//         Scheduler.execute();
// 
//         // Continuously save the latest measured pose.
//         // This supports the Auto-to-TeleOp handoff.
//         MatchHandoff.save(
//                 follower.pose(),
//                 AUTO_ALLIANCE
//         );
// 
//         // Detect completion of the full Ivy routine.
//         if (!complete
//                 && routine != null
//                 && !Scheduler.isScheduled(routine)) {
// 
//             complete = true;
// 
//             // Stop all installed mechanisms.
//             robot.stop();
// 
//             // Stop manual drive output.
//             follower.manual(0, 0, 0);
// 
//             // Restore normal V4 interlock behavior.
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
//                 "Shooter interlock",
//                 V4Settings.Shooter.INTERLOCK
//         );
// 
//         telemetry.addData(
//                 "Current pose",
//                 follower.pose()
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
//         if (follower.currentPath() != null) {
// 
//             telemetry.addData(
//                     "Remaining path distance",
//                     follower.distanceToEndpoint()
//             );
// 
//             telemetry.addData(
//                     "Path index",
//                     follower.pathIndex()
//             );
//         }
// 
//         telemetry.update();
//     }
// 
// 
//     // =====================================================
//     // SECTION 10 - INTERLOCK RESTORATION
//     // =====================================================
// 
//     /**
//      * Restore the pre-Auto interlock setting.
//      *
//      * This prevents the Auto's bypass choice from
//      * unintentionally affecting a later TeleOp.
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
//     // SECTION 11 - AUTONOMOUS STOP
//     // =====================================================
// 
//     @Override
//     public void stop() {
// 
//         // Preserve the last measured pose for TeleOp.
//         if (follower != null) {
// 
//             MatchHandoff.save(
//                     follower.pose(),
//                     AUTO_ALLIANCE
//             );
//         }
// 
//         // Cancel all scheduled Ivy commands.
//         Scheduler.reset();
// 
//         // Safely stop every mechanism.
//         if (robot != null) {
//             robot.stop();
//         }
// 
//         // Stop the drivetrain.
//         if (follower != null) {
//             follower.manual(0, 0, 0);
//         }
// 
//         // Return to the standard V4 interlock setting.
//         restoreShooterInterlock();
//     }
// }
// 
