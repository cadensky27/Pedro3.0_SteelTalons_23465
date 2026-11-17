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

@Autonomous(name = "Alternate-Shoot-Park Right", group = "BIOBUZZ V5")
public final class AlternateShootParkRight extends BaseAuto {
    // ROUTINE SETTINGS
    private static final long PARTNER_DELAY_MS = 2000;
    private static final long SPINUP_DELAY_MS = V5Settings.Feeder.READY_TIMEOUT_MS;
    private static final long FEED_WALL_TIMEOUT_MS = V5Settings.Feeder.FEED_WALL_TIMEOUT_MS;
    private static final V5Settings.Preset SHOT_PRESET = V5Settings.Preset.NORMAL;
    private static final long FEED_MS = 5000;

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

    // MATCH SEQUENCE — Ivy runs these actions from top to bottom.
    @Override
    protected Command buildRoutine() {
        return sequential(
                waitMilliseconds(PARTNER_DELAY_MS),
                MechanismCommands.preset(robot, SHOT_PRESET),
                MechanismCommands.shooterOn(robot),
                waitMilliseconds(SPINUP_DELAY_MS),
                MechanismCommands.feederForMs(robot, FEED_MS, FEED_WALL_TIMEOUT_MS, false),
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
//  * BIOBUZZ V4 - FOUR PRELOAD / TWO-PATH AUTONOMOUS
//  *
//  * Autonomous sequence:
//  *
//  * 1. Wait for the configured alliance-partner delay.
//  * 2. Select the configured shooter RPM preset.
//  * 3. Spin up and fire the four preloaded balls.
//  * 4. Stop the feeder and optionally stop the shooter.
//  * 5. Follow the first Pedro path.
//  * 6. Follow the second Pedro path.
//  * 7. Save the final measured pose for TeleOp.
//  *
//  * Uses the shared V4 mechanism system and Pedro Ivy.
//  *
//  * All delays are nonblocking. The follower, mechanisms,
//  * and Ivy scheduler continue updating throughout Auto.
//  *
//  * Shooter RPM interlock is disabled by default for this
//  * Autonomous only. The original setting is restored
//  * when the routine completes or the OpMode stops.
//  */
// @Autonomous(
//         name = "Alternate-Shoot-Park Right (RED)",
//         group = "BIOBUZZ V4"
// )
// public final class ASPR_R extends OpMode {
// 
//     // =====================================================
//     // SECTION 1 - AUTONOMOUS CONFIGURATION
//     // =====================================================
//     //
//     // These values can be changed independently in each
//     // Autonomous OpMode.
//     // =====================================================
// 
//     /**
//      * Alliance associated with this path.
//      *
//      * Used for the Auto-to-TeleOp pose handoff.
//      *
//      * Changing this does not mirror the path coordinates.
//      */
//     private static final V4Settings.Alliance AUTO_ALLIANCE =
//             V4Settings.Alliance.BLUE;
// 
//     /**
//      * Delay before any shooting or movement begins.
//      *
//      * Allows an alliance partner to leave the starting area.
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
//      * Master toggle for preload shooting.
//      *
//      * Set false to test the path without shooting.
//      */
//     private static final boolean ENABLE_PRELOAD_SHOOTING = true;
// 
//     /**
//      * Enable the shooter RPM interlock in this Auto.
//      *
//      * false:
//      * - Wait a fixed spin-up delay.
//      * - Feed regardless of actual shooter RPM.
//      * - Do not pause feeding for RPM drops.
//      *
//      * true:
//      * - Wait for stable RPM readiness.
//      * - Skip feeding if readiness times out.
//      * - Pause/resume feeding according to V4 settings.
//      *
//      * This setting is independent for this Auto.
//      */
//     private static final boolean AUTO_SHOOTER_INTERLOCK = false;
// 
//     /**
//      * Shooter RPM preset.
//      *
//      * NORMAL = 2950 RPM
//      * CLOSE  = 2750 RPM
//      * ANGLED = 3150 RPM
//      *
//      * Actual RPM values are defined in V4Settings.
//      */
//     private static final V4Settings.Preset SHOOTER_PRESET =
//             V4Settings.Preset.NORMAL;
// 
//     /**
//      * Fixed shooter spin-up delay when interlock is OFF.
//      *
//      * Uses the established V4 readiness timeout value
//      * as the initial delay.
//      *
//      * This is a fixed wait, not an RPM measurement.
//      */
//     private static final long SHOOTER_SPINUP_DELAY_MS =
//             V4Settings.Feeder.READY_TIMEOUT_MS;
// 
//     /**
//      * Requested ACTIVE feeder-running time.
//      *
//      * Initial value: 2800 ms.
//      *
//      * The feeder does not count balls individually.
//      * Calibrate this value for four preloads.
//      */
//     private static final long FOUR_PRELOAD_FEED_MS = 5000;
// 
//     /**
//      * Maximum elapsed feeding time.
//      *
//      * Uses the shared V4 feeding timeout.
//      *
//      * Initial value: 6000 ms.
//      */
//     private static final long FEED_WALL_TIMEOUT_MS =
//             V4Settings.Feeder.FEED_WALL_TIMEOUT_MS;
// 
//     /**
//      * Stop the shooter after the four preloads finish.
//      *
//      * true:
//      * Shooter turns off before movement.
//      *
//      * false:
//      * Shooter remains active during movement.
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
//      * IMPORTANT:
//      * Path 2 starts where Path 1 ends.
//      * Leave Path 1 enabled when running Path 2.
//      */
//     private static final boolean ENABLE_PATH_2 = true;
// 
//     /**
//      * Starting position for the optional flower arm.
//      */
//     private static final V4Settings.ArmPosition AUTO_ARM_START =
//             V4Settings.ArmPosition.UP;
// 
// 
//     // =====================================================
//     // SECTION 2 - ORIGINAL PEDRO COORDINATES
//     // =====================================================
//     //
//     // These coordinates are preserved from the supplied
//     // Pedro Visualizer routine.
//     //
//     // They are deliberately not using FieldLibrary yet.
//     // =====================================================
// 
//     private final PoseFactory poseFactory =
//             PoseFactory.degrees();
// 
//     /**
//      * Starting position.
//      */
//     private final Pose start =
//             poseFactory.of(58.9908, 132.677, 90);
// 
//     /**
//      * First path endpoint.
//      */
//     private final Pose path1End =
//             poseFactory.of(58.9908, 32.6354, 90);
// 
//     /**
//      * Second path starting point.
//      *
//      * Equal to the first path endpoint.
//      */
//     private final Pose point2Start =
//             poseFactory.of(58.9908, 32.6354, 90);
// 
//     /**
//      * Second path endpoint.
//      */
//     private final Pose point2End =
//             poseFactory.of(11.4338, 93.3361, 0);
// 
//     /**
//      * Bezier control point for the second path.
//      */
//     private final Pose point2Control1 =
//             poseFactory.of(11.0855, 32.9439, 0);
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
//      * Preserves the normal V4 shooter interlock value
//      * so this Auto cannot permanently override TeleOp.
//      */
//     private boolean originalShooterInterlock;
//     private boolean interlockOverridden = false;
// 
// 
//     // =====================================================
//     // SECTION 4 - CUSTOM IVY DELAY
//     // =====================================================
// 
//     /**
//      * Creates a nonblocking Ivy timer.
//      *
//      * The timer begins when Ivy starts this command,
//      * not when the command object is constructed.
//      *
//      * This allows mechanisms and Pedro localization
//      * to continue updating during the delay.
//      *
//      * @param durationMs Desired delay in milliseconds.
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
//     // SECTION 5 - FOUR-PRELOAD SHOOTING COMMAND
//     // =====================================================
// 
//     /**
//      * Creates the four-preload shooting sequence.
//      *
//      * When the RPM interlock is enabled:
//      *
//      * The existing V4 shootPreloads command manages
//      * spin-up, readiness, feeding, and timeouts.
//      *
//      * When the RPM interlock is disabled:
//      *
//      * 1. Enable the shooter.
//      * 2. Wait for the fixed spin-up duration.
//      * 3. Run the feeder for the requested active time.
//      *
//      * Optional shooter-gate delays are still managed
//      * internally by the feeder subsystem.
//      *
//      * @return An Ivy command that performs shooting.
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
//                 MechanismCommands.shooterOn(robot),
// 
//                 waitMilliseconds(
//                         SHOOTER_SPINUP_DELAY_MS
//                 ),
// 
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
//     // SECTION 6 - MAIN AUTONOMOUS ROUTINE
//     // =====================================================
//     //
//     // EDIT THIS SECTION TO CHANGE THE ACTION SEQUENCE.
//     //
//     // Ivy executes every command from top to bottom.
//     //
//     // Path movement cannot begin until shooting finishes.
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
//                 // STEP 1 - SHOOTER PRESET
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
//                 // STEP 4 - OPTIONAL SHOOTER SHUTDOWN
//                 // -----------------------------------------
// 
//                 STOP_SHOOTER_BEFORE_DRIVING
//                         ? MechanismCommands.shooterOff(robot)
//                         : instant(() -> {}),
// 
//                 // -----------------------------------------
//                 // STEP 5 - FIRST PEDRO PATH
//                 // -----------------------------------------
// 
//                 ENABLE_PATH_1
//                         ? follow(follower, path1())
//                         : instant(() -> {}),
// 
//                 // -----------------------------------------
//                 // STEP 6 - SECOND PEDRO PATH
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
//      * Straight line from the starting position to
//      * the first endpoint.
//      *
//      * Heading remains at 90 degrees.
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
//      * Quadratic Bezier curve using one control point.
//      *
//      * Heading transitions from 90 degrees to 0 degrees.
//      *
//      * Original path geometry is preserved.
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
//     // SECTION 8 - INITIALIZATION
//     // =====================================================
// 
//     @Override
//     public void init() {
// 
//         // Remove commands from a previous OpMode.
//         Scheduler.reset();
// 
//         // Invalidate any previous Auto handoff data.
//         MatchHandoff.beginAuto();
// 
//         // Construct Pedro using the renamed constants class.
//         follower = Constants_GoBuilda.create(hardwareMap);
// 
//         // Initialize localization at the supplied start pose.
//         follower.setPose(start);
//         follower.update();
// 
//         // Initialize all installed V4 mechanisms.
//         robot = new Robot(hardwareMap);
// 
//         // Preserve the current global interlock setting.
//         originalShooterInterlock =
//                 V4Settings.Shooter.INTERLOCK;
// 
//         interlockOverridden = false;
//         complete = false;
// 
//         // ---------------------------------------------
//         // INITIALIZATION TELEMETRY
//         // ---------------------------------------------
// 
//         telemetry.addLine("BIOBUZZ V4 - TWO PATH AUTO");
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
//                 "Partner delay (ms)",
//                 AUTO_START_DELAY_MS
//         );
// 
//         telemetry.addData(
//                 "Preload shooting",
//                 ENABLE_PRELOAD_SHOOTING
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
//                 "Spin-up delay (ms)",
//                 SHOOTER_SPINUP_DELAY_MS
//         );
// 
//         telemetry.addData(
//                 "Feed active time (ms)",
//                 FOUR_PRELOAD_FEED_MS
//         );
// 
//         telemetry.addData(
//                 "Feed timeout (ms)",
//                 FEED_WALL_TIMEOUT_MS
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
//         // Initialize safe mechanism states.
//         // The sorting gate is raised if installed.
//         robot.startAuto(AUTO_ARM_START);
// 
//         // Apply this Auto's local shooting preference.
//         V4Settings.Shooter.INTERLOCK =
//                 AUTO_SHOOTER_INTERLOCK;
// 
//         interlockOverridden = true;
// 
//         // Build and schedule the Ivy action sequence.
//         routine = autoRoutine();
// 
//         schedule(routine);
//     }
// 
// 
//     // =====================================================
//     // SECTION 10 - AUTONOMOUS PERIODIC UPDATE
//     // =====================================================
// 
//     @Override
//     public void loop() {
// 
//         // Update Pedro localization and drivetrain.
//         follower.update();
// 
//         // Update shooter, feeder, and mechanism logic.
//         robot.update();
// 
//         // Execute all currently scheduled Ivy commands.
//         Scheduler.execute();
// 
//         // Save measured pose throughout Autonomous.
//         MatchHandoff.save(
//                 follower.pose(),
//                 AUTO_ALLIANCE
//         );
// 
//         // ---------------------------------------------
//         // DETECT AUTONOMOUS COMPLETION
//         // ---------------------------------------------
// 
//         if (!complete
//                 && routine != null
//                 && !Scheduler.isScheduled(routine)) {
// 
//             complete = true;
// 
//             // Stop all mechanisms.
//             robot.stop();
// 
//             // Release drivetrain movement commands.
//             follower.manual(0, 0, 0);
// 
//             // Restore standard shooting behavior.
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
//                     "Active feed (seconds)",
//                     robot.feeder.forwardSeconds()
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
//      * Restores the original V4 interlock setting.
//      *
//      * This prevents one Autonomous OpMode from changing
//      * the behavior of a later TeleOp.
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
//         // Preserve the final pose for the next TeleOp.
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
//         // Stop installed mechanisms.
//         if (robot != null) {
//             robot.stop();
//         }
// 
//         // Stop drivetrain output.
//         if (follower != null) {
//             follower.manual(0, 0, 0);
//         }
// 
//         // Restore normal interlock configuration.
//         restoreShooterInterlock();
//     }
// }
