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

@Autonomous(name = "Alternate-Shoot-Park Left", group = "BIOBUZZ V5")
public final class AlternateShootParkLeft extends BaseAuto {
    // ROUTINE SETTINGS
    private static final long PARTNER_DELAY_MS = 2000;
    private static final long SPINUP_DELAY_MS = V5Settings.Feeder.READY_TIMEOUT_MS;
    private static final long FEED_WALL_TIMEOUT_MS = V5Settings.Feeder.FEED_WALL_TIMEOUT_MS;
    private static final V5Settings.Preset SHOT_PRESET = V5Settings.Preset.NORMAL;
    private static final long FEED_MS = 5000;

    // FIELD POSES — inches and degrees in the shared Red-authored field frame.
    private final PoseFactory poses = PoseFactory.degrees();
    private final Pose startPose = poses.of(59.5515, 132.8639, 90);
    private final Pose parkPose = poses.of(11.6995, 121.4234, 0);
    private final Pose parkControlPoint = poses.of(67.8627, 108.3747, 0);

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
//  * BIOBUZZ V4 - FOUR PRELOAD / INTAKE / CURVED PATH AUTO
//  *
//  * Autonomous sequence:
//  *
//  * 1. Wait for the alliance-partner start delay.
//  * 2. Select the configured shooter RPM preset.
//  * 3. Start the shooter, intake, and side rollers.
//  * 4. Wait for shooter spin-up or RPM readiness.
//  * 5. Feed the four preloaded balls.
//  * 6. Stop the intake, feeder, and optionally the shooter.
//  * 7. Follow the original Pedro curved path.
//  * 8. Save the final robot pose for TeleOp.
//  *
//  * All delays and movements use Pedro Ivy.
//  *
//  * Shooter RPM interlock is OFF by default in this Auto.
//  * The normal V4 interlock setting is restored afterward.
//  *
//  * Coordinate values are intentionally kept as literal
//  * Pedro Visualizer values, not FieldLibrary references.
//  */
// @Autonomous(
//         name = "Alternate-Shoot-Park Left (RED)",
//         group = "BIOBUZZ V4"
// )
// public final class ASPL_R extends OpMode {
// 
//     // =====================================================
//     // SECTION 1 - AUTONOMOUS CONFIGURATION
//     // =====================================================
//     //
//     // Change these values independently for this Auto.
//     // =====================================================
// 
//     /**
//      * Alliance associated with this Autonomous.
//      *
//      * Used for the Auto-to-TeleOp pose handoff.
//      * Changing this does NOT mirror the path.
//      *
//      * Verify BLUE against your field layout.
//      */
//     private static final V4Settings.Alliance AUTO_ALLIANCE =
//             V4Settings.Alliance.BLUE;
// 
//     /**
//      * Delay before shooting or driving.
//      *
//      * 0    = No delay
//      * 1000 = 1 second
//      * 2000 = 2 seconds
//      * 3000 = 3 seconds
//      * 5000 = 5 seconds
//      *
//      * Default: 2 seconds.
//      */
//     private static final long AUTO_START_DELAY_MS = 2000;
// 
//     /**
//      * Master toggle for four-preload shooting.
//      *
//      * Set false to test movement without shooting.
//      */
//     private static final boolean ENABLE_PRELOAD_SHOOTING = true;
// 
//     /**
//      * Run intake and linked side rollers while shooting.
//      *
//      * The intake starts with shooter spin-up and remains
//      * active until the feeding sequence finishes.
//      *
//      * Requires ENABLE_PRELOAD_SHOOTING to be true.
//      */
//     private static final boolean RUN_INTAKE_WHILE_SHOOTING = true;
// 
//     /**
//      * Enable RPM-based shooter/feeder interlock.
//      *
//      * false:
//      * - Use a fixed spin-up delay.
//      * - Feed without waiting for RPM readiness.
//      * - Do not pause feeding for RPM drops.
//      *
//      * true:
//      * - Wait for shooter readiness.
//      * - Skip feeding if readiness times out.
//      * - Allow feeder RPM recovery pauses.
//      *
//      * Default: false for Autonomous.
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
//      * Actual targets come from V4Settings.
//      */
//     private static final V4Settings.Preset SHOOTER_PRESET =
//             V4Settings.Preset.NORMAL;
// 
//     /**
//      * Fixed shooter spin-up delay when interlock is OFF.
//      *
//      * Uses the established V4 readiness timeout value.
//      *
//      * Default: 3500 milliseconds.
//      */
//     private static final long SHOOTER_SPINUP_DELAY_MS =
//             V4Settings.Feeder.READY_TIMEOUT_MS;
// 
//     /**
//      * Requested active feeder-running time.
//      *
//      * Default: 2800 milliseconds.
//      *
//      * This is a timed feed, not a physical ball counter.
//      * Calibrate it for reliable four-preload shooting.
//      */
//     private static final long FOUR_PRELOAD_FEED_MS = 5000;
// 
//     /**
//      * Maximum elapsed feeding time.
//      *
//      * Default: 6000 milliseconds.
//      *
//      * Prevents feeder commands from waiting indefinitely.
//      */
//     private static final long FEED_WALL_TIMEOUT_MS =
//             V4Settings.Feeder.FEED_WALL_TIMEOUT_MS;
// 
//     /**
//      * Stop the shooter before moving.
//      *
//      * true  = Turn shooter off after feeding.
//      * false = Keep shooter spinning while driving.
//      *
//      * All mechanisms still stop when Auto completes.
//      */
//     private static final boolean STOP_SHOOTER_BEFORE_DRIVING = true;
// 
//     /**
//      * Enable the curved movement path.
//      *
//      * Set false for a stationary shooting test.
//      */
//     private static final boolean ENABLE_MOVEMENT = true;
// 
//     /**
//      * Initial flower arm position, if installed.
//      *
//      * The sorting gate is set UP by Robot.startAuto().
//      */
//     private static final V4Settings.ArmPosition AUTO_ARM_START =
//             V4Settings.ArmPosition.UP;
// 
// 
//     // =====================================================
//     // SECTION 2 - PEDRO VISUALIZER COORDINATES
//     // =====================================================
//     //
//     // All original coordinates are preserved exactly.
//     //
//     // We are not using FieldLibrary constants yet.
//     // =====================================================
// 
//     private final PoseFactory poseFactory =
//             PoseFactory.degrees();
// 
//     /**
//      * Autonomous starting position.
//      *
//      * X = 59.5515
//      * Y = 132.8639
//      * Heading = 90 degrees
//      */
//     private final Pose start =
//             poseFactory.of(59.5515, 132.8639, 90);
// 
//     /**
//      * Final shooting-to-parking path destination.
//      *
//      * X = 11.6995
//      * Y = 121.4234
//      * Heading = 0 degrees
//      */
//     private final Pose path1End =
//             poseFactory.of(11.6995, 121.4234, 0);
// 
//     /**
//      * Bezier control point for the curved path.
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
//      * Preserve the normal interlock setting.
//      *
//      * This Auto temporarily changes the setting to its
//      * local preference and restores it when finished.
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
//      * Creates a nonblocking delay command.
//      *
//      * The timer starts when Ivy activates this command.
//      *
//      * Unlike Thread.sleep(), this does not block the
//      * Pedro follower or subsystem periodic updates.
//      *
//      * @param durationMs Delay in milliseconds.
//      * @return Ivy command for the specified delay.
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
//      * Creates the full four-preload shooting sequence.
//      *
//      * The intake and side rollers start before the shooter
//      * begins spinning up and remain active until feeding
//      * has finished.
//      *
//      * INTERLOCK OFF:
//      *
//      * 1. Start intake.
//      * 2. Start shooter.
//      * 3. Wait a fixed spin-up duration.
//      * 4. Feed for the configured active time.
//      * 5. Stop feeder and intake.
//      *
//      * INTERLOCK ON:
//      *
//      * 1. Start intake.
//      * 2. Start shooter.
//      * 3. Wait for stable RPM readiness.
//      * 4. Feed with RPM protection.
//      * 5. Stop feeder and intake.
//      *
//      * The optional shooter gate opening delay is handled
//      * internally by the FeederSubsystem.
//      *
//      * @return Ivy shooting sequence.
//      */
//     private Command shootFourPreloads() {
// 
//         return sequential(
// 
//                 // -----------------------------------------
//                 // STEP A - START INTAKE AND SIDE ROLLERS
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
//                         ? MechanismCommands.shootPreloads(
//                         robot,
//                         FOUR_PRELOAD_FEED_MS
//                 )
//                         : sequential(
// 
//                         // Wait for fixed spin-up time.
//                         waitMilliseconds(
//                                 SHOOTER_SPINUP_DELAY_MS
//                         ),
// 
//                         // Run the feeder without RPM
//                         // readiness restrictions.
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
//                 // STEP E - STOP INTAKE AND SIDE ROLLERS
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
//     // EDIT THIS SECTION TO CHANGE ACTION ORDER.
//     //
//     // Ivy sequential() finishes each action before
//     // moving to the next one.
//     // =====================================================
// 
//     private Command autoRoutine() {
// 
//         return sequential(
// 
//                 // -----------------------------------------
//                 // STEP 0 - ALLIANCE PARTNER START DELAY
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
//                 // STEP 2 - SHOOT FOUR PRELOADS
//                 //          WITH INTAKE RUNNING
//                 // -----------------------------------------
// 
//                 ENABLE_PRELOAD_SHOOTING
//                         ? shootFourPreloads()
//                         : instant(() -> {}),
// 
//                 // -----------------------------------------
//                 // STEP 3 - ENSURE MECHANISMS ARE STOPPED
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
//                 // STEP 4 - EXECUTE ORIGINAL PEDRO CURVE
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
//     // SECTION 7 - PEDRO PATH DEFINITION
//     // =====================================================
// 
//     /**
//      * PATH 1 - CURVED MOVEMENT
//      *
//      * Starting pose:
//      * (59.5515, 132.8639, 90 degrees)
//      *
//      * Bezier control point:
//      * (67.8627, 108.3747, 0 degrees)
//      *
//      * Final pose:
//      * (11.6995, 121.4234, 0 degrees)
//      *
//      * Uses a quadratic Bezier curve and linear heading
//      * interpolation from 90 degrees to 0 degrees.
//      *
//      * Geometry is unchanged from the supplied path.
//      *
//      * @return Pedro path object.
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
//     // SECTION 8 - AUTONOMOUS INITIALIZATION
//     // =====================================================
// 
//     @Override
//     public void init() {
// 
//         // Clear any previous Ivy commands.
//         Scheduler.reset();
// 
//         // Invalidate old Auto-to-TeleOp pose handoff data.
//         MatchHandoff.beginAuto();
// 
//         // Create the Pedro follower using goBILDA constants.
//         follower = Constants_GoBuilda.create(hardwareMap);
// 
//         // Initialize localization at the selected start pose.
//         follower.setPose(start);
//         follower.update();
// 
//         // Initialize all installed V4 mechanisms.
//         robot = new Robot(hardwareMap);
// 
//         // Save the original shooter interlock setting.
//         originalShooterInterlock =
//                 V4Settings.Shooter.INTERLOCK;
// 
//         interlockOverridden = false;
//         complete = false;
// 
//         // ---------------------------------------------
//         // DRIVER STATION INIT TELEMETRY
//         // ---------------------------------------------
// 
//         telemetry.addLine("BIOBUZZ V4 - SHOOT + INTAKE CURVE");
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
//                 "Active feed time (ms)",
//                 FOUR_PRELOAD_FEED_MS
//         );
// 
//         telemetry.addData(
//                 "Feeding wall timeout (ms)",
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
//     // SECTION 9 - AUTONOMOUS START
//     // =====================================================
// 
//     @Override
//     public void start() {
// 
//         // Set safe mechanism startup states.
//         //
//         // - Shooter OFF
//         // - Feeder OFF
//         // - Intake OFF
//         // - Side rollers OFF
//         // - Sorting gate UP, if installed
//         // - Flower arm in configured position
//         robot.startAuto(AUTO_ARM_START);
// 
//         // Apply this Auto's shooter interlock preference.
//         //
//         // The original setting is restored after Auto.
//         V4Settings.Shooter.INTERLOCK =
//                 AUTO_SHOOTER_INTERLOCK;
// 
//         interlockOverridden = true;
// 
//         // Build and schedule the complete Ivy routine.
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
//         // Update Pedro localization and path following.
//         follower.update();
// 
//         // Update all installed V4 mechanisms.
//         robot.update();
// 
//         // Execute the scheduled Ivy commands.
//         Scheduler.execute();
// 
//         // Continuously save the measured pose for TeleOp.
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
//             // Safely stop installed mechanisms.
//             robot.stop();
// 
//             // Release drivetrain output.
//             follower.manual(0, 0, 0);
// 
//             // Restore TeleOp's normal RPM interlock.
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
//     // SECTION 11 - RESTORE SHOOTER INTERLOCK
//     // =====================================================
// 
//     /**
//      * Restore the original V4 shooter interlock.
//      *
//      * Prevents this Auto from unintentionally leaving
//      * RPM protection disabled during TeleOp.
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
//         // Preserve the last measured pose for TeleOp.
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
//         // Stop all installed mechanism hardware.
//         if (robot != null) {
//             robot.stop();
//         }
// 
//         // Stop drivetrain output.
//         if (follower != null) {
//             follower.manual(0, 0, 0);
//         }
// 
//         // Restore standard shooter interlock behavior.
//         restoreShooterInterlock();
//     }
// }
