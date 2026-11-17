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

@Autonomous(name = "Primary-Shoot-Intake Garden-Shoot-Park Right", group = "BIOBUZZ V5")
public final class PrimaryTwoCycleParkRight extends BaseAuto {
    // ROUTINE SETTINGS
    private static final long PARTNER_DELAY_MS = 2000;
    private static final long SPINUP_DELAY_MS = V4Settings.Feeder.READY_TIMEOUT_MS;
    private static final long FEED_WALL_TIMEOUT_MS = V4Settings.Feeder.FEED_WALL_TIMEOUT_MS;
    private static final V4Settings.Preset FIRST_SHOOTER_PRESET = V4Settings.Preset.NORMAL;
    private static final V4Settings.Preset SECOND_SHOOTER_PRESET = V4Settings.Preset.NORMAL;
    private static final long FIRST_FEED_MS = 2800;
    private static final long SECOND_FEED_MS = 2800;
    private static final long GARDEN_COLLECTION_WAIT_MS = 2000;

    // FIELD POSES — inches and degrees in the shared Red-authored field frame.
    private final PoseFactory poses = PoseFactory.degrees();
    private final Pose startPose = poses.of(57.8692, 8.1869, 90);
    private final Pose gardenApproachPose = poses.of(10.2041, 22.3547, 270);
    private final Pose gardenPickupPose = poses.of(7.7383, 8.0298, 270);
    private final Pose gardenExitPose = poses.of(10.2079, 22.2037, 270);
    private final Pose secondShotPose = poses.of(58.4456, 132.6705, 90);
    private final Pose secondShotControlPoint = poses.of(78.1738, 0.871, 0);
    private final Pose parkPose = poses.of(11.0076, 92.2988, 0);
    private final Pose parkControlPoint = poses.of(63.419, 104.9091, 0);

    @Override
    protected Pose startingPose() {
        return startPose;
    }

    // MATCH SEQUENCE — Ivy runs these actions from top to bottom.
    @Override
    protected Command buildRoutine() {
        return sequential(
                waitMilliseconds(PARTNER_DELAY_MS),
                showStage("Shoot preloads"),
                shootWithIntake(FIRST_SHOOTER_PRESET, FIRST_FEED_MS),
                MechanismCommands.shooterOff(robot),
                showStage("Approach garden"),
                follow(follower, pathToGardenApproach()),
                showStage("Collect in garden"),
                MechanismCommands.intakeStart(robot),
                follow(follower, pathIntoGarden()),
                waitMilliseconds(GARDEN_COLLECTION_WAIT_MS),
                showStage("Leave garden"),
                follow(follower, pathOutOfGarden()),
                MechanismCommands.intakeStop(robot),
                showStage("Move to second shot"),
                follow(follower, pathToSecondShot()),
                showStage("Shoot collected pieces"),
                shootWithIntake(SECOND_SHOOTER_PRESET, SECOND_FEED_MS),
                MechanismCommands.shooterOff(robot),
                showStage("Park"),
                follow(follower, pathToPark()),
                showStage("Complete")
        );
    }

    /** Both volleys use the existing timed spin-up, with the intake running. */
    private Command shootWithIntake(V4Settings.Preset preset, long feedMs) {
        return sequential(
                MechanismCommands.preset(robot, preset),
                MechanismCommands.intakeStart(robot),
                MechanismCommands.shooterOn(robot),
                waitMilliseconds(SPINUP_DELAY_MS),
                MechanismCommands.feederForMs(robot, feedMs, FEED_WALL_TIMEOUT_MS, false),
                MechanismCommands.intakeStop(robot)
        );
    }

    // PEDRO PATHS — adjacent segments reuse their common pose.
    private Path pathToGardenApproach() {
        return Paths.line(startPose, gardenApproachPose)
                .linear(startPose, gardenApproachPose);
    }

    private Path pathIntoGarden() {
        return Paths.line(gardenApproachPose, gardenPickupPose)
                .constant(gardenPickupPose);
    }

    private Path pathOutOfGarden() {
        return Paths.line(gardenPickupPose, gardenExitPose)
                .constant(gardenExitPose);
    }

    private Path pathToSecondShot() {
        return Paths.curve(gardenExitPose, secondShotControlPoint, secondShotPose)
                .linear(gardenExitPose, secondShotPose);
    }

    private Path pathToPark() {
        return Paths.curve(secondShotPose, parkControlPoint, parkPose)
                .linear(secondShotPose, parkPose);
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
//  * BIOBUZZ V4 - EXPERIMENTAL GARDEN AUTONOMOUS
//  *
//  * Full sequence:
//  *
//  *  1. Wait for alliance partner.
//  *  2. Shoot four preloaded balls with intake running.
//  *  3. Path 1: Approach the garden.
//  *  4. Start intake and follow Path 2 into the garden.
//  *  5. Wait while collecting balls.
//  *  6. Follow Path 3 while continuing intake.
//  *  7. Stop intake.
//  *  8. Path 4: Travel to opposite shooting position.
//  *  9. Shoot four collected balls with intake running.
//  * 10. Path 5: Drive to final parking position.
//  * 11. Stop mechanisms and preserve localization.
//  *
//  * Uses Pedro Ivy for mechanism actions, delays, and paths.
//  *
//  * The shooter RPM interlock defaults to OFF for this Auto.
//  * The previous interlock setting is restored afterward.
//  *
//  * IMPORTANT:
//  * The routine does not count balls or confirm collection.
//  * All collection and feeding durations require testing.
//  *
//  * IMPORTANT:
//  * Verify the total routine completes within the
//  * 30-second Autonomous period.
//  */
// @Autonomous(
//         name = "Primary-Shoot-Intake Garden-Shoot-Park Right (RED)!",
//         group = "BIOBUZZ V4"
// )
// public final class PS2PR_Rxx extends OpMode {
// 
//     // =====================================================
//     // SECTION 1 - AUTONOMOUS CONFIGURATION
//     // =====================================================
//     //
//     // All routinely adjustable settings are kept here.
//     //
//     // Values affect THIS Autonomous only, except the
//     // temporary runtime shooter interlock override.
//     // =====================================================
// 
//     /**
//      * Alliance for the Auto-to-TeleOp localization handoff.
//      *
//      * RED is an initial assumption based on the field
//      * coordinates. Verify before competition.
//      *
//      * Changing this does not mirror the paths.
//      */
//     private static final V4Settings.Alliance AUTO_ALLIANCE =
//             V4Settings.Alliance.RED;
// 
//     /**
//      * Alliance partner delay before any actions begin.
//      *
//      * Default: 2000 milliseconds.
//      */
//     private static final long AUTO_START_DELAY_MS = 2000;
// 
//     /**
//      * Enable the first four-preload shooting cycle.
//      */
//     private static final boolean ENABLE_FIRST_SHOOTING = true;
// 
//     /**
//      * Enable the second shooting cycle after collection.
//      */
//     private static final boolean ENABLE_SECOND_SHOOTING = true;
// 
//     /**
//      * Run intake and linked side rollers during shooting.
//      *
//      * Applies to both shooting cycles.
//      *
//      * Default: true.
//      */
//     private static final boolean RUN_INTAKE_WHILE_SHOOTING = true;
// 
//     /**
//      * Collect balls while driving through the garden.
//      *
//      * When false, Path 2 and Path 3 still execute,
//      * but the intake remains stopped.
//      */
//     private static final boolean ENABLE_GARDEN_INTAKE = true;
// 
//     /**
//      * Additional stationary intake time after Path 2.
//      *
//      * Default: 2000 milliseconds.
//      *
//      * The intake remains active during this delay.
//      *
//      * Reduce this value if the Autonomous exceeds
//      * the competition time limit.
//      */
//     private static final long GARDEN_COLLECTION_WAIT_MS = 2000;
// 
//     /**
//      * Enable RPM-based feeder interlocking.
//      *
//      * false:
//      * - Fixed shooter spin-up delay.
//      * - No RPM readiness requirement before feeding.
//      * - No RPM-based feeder pauses.
//      *
//      * true:
//      * - Wait for stable shooter readiness.
//      * - Skip feeding if the readiness timeout expires.
//      * - Pause feeder on excessive RPM drops.
//      *
//      * Default: false for Autonomous.
//      */
//     private static final boolean AUTO_SHOOTER_INTERLOCK = false;
// 
//     /**
//      * RPM preset for the first shooting cycle.
//      */
//     private static final V4Settings.Preset FIRST_SHOOTER_PRESET =
//             V4Settings.Preset.NORMAL;
// 
//     /**
//      * RPM preset for the opposite shooting position.
//      *
//      * Initially NORMAL, but independently adjustable.
//      */
//     private static final V4Settings.Preset SECOND_SHOOTER_PRESET =
//             V4Settings.Preset.NORMAL;
// 
//     /**
//      * Fixed spin-up time when interlock is disabled.
//      *
//      * Uses the established V4 readiness timeout:
//      * 3500 milliseconds.
//      *
//      * This is a fixed delay, not an RPM check.
//      */
//     private static final long SHOOTER_SPINUP_DELAY_MS =
//             V4Settings.Feeder.READY_TIMEOUT_MS;
// 
//     /**
//      * Active feeder duration for the four preloaded balls.
//      *
//      * Default: 2800 milliseconds.
//      */
//     private static final long FIRST_FEED_MS = 2800;
// 
//     /**
//      * Active feeder duration for the collected balls.
//      *
//      * Default: 2800 milliseconds.
//      *
//      * Adjust independently after testing garden collection.
//      */
//     private static final long SECOND_FEED_MS = 2800;
// 
//     /**
//      * Maximum elapsed feeding time for either cycle.
//      *
//      * Default: 6000 milliseconds.
//      */
//     private static final long FEED_WALL_TIMEOUT_MS =
//             V4Settings.Feeder.FEED_WALL_TIMEOUT_MS;
// 
//     /**
//      * Stop the shooter between shooting cycles.
//      *
//      * true:
//      * - Shooter stops after the first cycle.
//      * - Spins up again before the second cycle.
//      *
//      * false:
//      * - Shooter can continue running while collecting.
//      * - The second cycle still uses its normal preparation.
//      *
//      * Default: true.
//      */
//     private static final boolean STOP_SHOOTER_BETWEEN_CYCLES = true;
// 
//     /**
//      * Stop the shooter before the final parking path.
//      */
//     private static final boolean STOP_SHOOTER_BEFORE_PARK = true;
// 
//     /**
//      * Enable or disable movement paths.
//      *
//      * Individual toggles are useful for testing,
//      * but paths depend on their preceding endpoints.
//      *
//      * When a path is disabled, all later paths are
//      * disabled automatically to prevent discontinuities.
//      */
//     private static final boolean ENABLE_PATH_1 = true;
//     private static final boolean ENABLE_PATH_2 = true;
//     private static final boolean ENABLE_PATH_3 = true;
//     private static final boolean ENABLE_PATH_4 = true;
//     private static final boolean ENABLE_PATH_5 = true;
// 
//     /**
//      * Startup position of the optional flower arm.
//      *
//      * Sorting gate is automatically set UP in Auto.
//      */
//     private static final V4Settings.ArmPosition AUTO_ARM_START =
//             V4Settings.ArmPosition.UP;
// 
// 
//     // =====================================================
//     // SECTION 2 - ORIGINAL PEDRO FIELD COORDINATES
//     // =====================================================
//     //
//     // These are the exact coordinates supplied by the
//     // Pedro Visualizer-generated Autonomous.
//     //
//     // No automatic alliance mirroring is performed.
//     // =====================================================
// 
//     private final PoseFactory poseFactory =
//             PoseFactory.degrees();
// 
//     /**
//      * Starting shooting position.
//      */
//     private final Pose start =
//             poseFactory.of(57.8692, 8.1869, 90);
// 
//     /**
//      * Path 1 endpoint:
//      * Garden intake-ready position.
//      */
//     private final Pose path1End =
//             poseFactory.of(10.2041, 22.3547, 270);
// 
//     /**
//      * Path 2 endpoint:
//      * Forward collection position in the garden.
//      */
//     private final Pose point2 =
//             poseFactory.of(7.7383, 8.0298, 270);
// 
//     /**
//      * Path 3 endpoint:
//      * Withdrawn garden-ready position.
//      */
//     private final Pose point3 =
//             poseFactory.of(10.2079, 22.2037, 270);
// 
//     /**
//      * Path 4 starting position.
//      *
//      * Equals Path 3 endpoint.
//      */
//     private final Pose point4Start =
//             poseFactory.of(10.2079, 22.2037, 270);
// 
//     /**
//      * Path 4 destination:
//      * Opposite shooting position.
//      */
//     private final Pose point4End =
//             poseFactory.of(58.4456, 132.6705, 90);
// 
//     /**
//      * Bezier control point for Path 4.
//      */
//     private final Pose point4Control1 =
//             poseFactory.of(78.1738, 0.871, 0);
// 
//     /**
//      * Path 5 starting position.
//      *
//      * Equals Path 4 endpoint.
//      */
//     private final Pose point5Start =
//             poseFactory.of(58.4456, 132.6705, 90);
// 
//     /**
//      * Path 5 endpoint:
//      * Final parking position.
//      */
//     private final Pose point5End =
//             poseFactory.of(11.0076, 92.2988, 0);
// 
//     /**
//      * Bezier control point for Path 5.
//      */
//     private final Pose point5Control1 =
//             poseFactory.of(63.419, 104.9091, 0);
// 
// 
//     // =====================================================
//     // SECTION 3 - ROBOT OBJECTS AND STATE
//     // =====================================================
// 
//     private Follower follower;
//     private Robot robot;
//     private Command routine;
// 
//     private boolean complete = false;
// 
//     /**
//      * Current Autonomous stage for Driver Station telemetry.
//      *
//      * Updated by instantaneous Ivy commands.
//      */
//     private String currentStage = "NOT STARTED";
// 
//     /**
//      * Start timestamp used to measure total Auto duration.
//      */
//     private long autoStartedNs = 0;
// 
//     /**
//      * Preserve the global interlock setting so the
//      * Autonomous can restore it afterward.
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
//      * Nonblocking Ivy delay.
//      *
//      * The timer starts when Ivy activates the command,
//      * not when the command object is constructed.
//      *
//      * @param durationMs Delay in milliseconds.
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
//     /**
//      * Update the displayed Autonomous stage.
//      *
//      * This command finishes immediately.
//      */
//     private Command stage(String description) {
// 
//         return instant(() -> {
//             currentStage = description;
//         });
//     }
// 
//     /**
//      * Returns an instantaneous no-operation command.
//      *
//      * Used to preserve clean Ivy sequencing when
//      * optional behaviors are disabled.
//      */
//     private Command skip() {
// 
//         return instant(() -> {});
//     }
// 
//     /**
//      * Creates a complete shooting cycle.
//      *
//      * The intake and side rollers run while shooting,
//      * provided RUN_INTAKE_WHILE_SHOOTING is enabled.
//      *
//      * With the interlock disabled:
//      * - Start shooter.
//      * - Wait a fixed spin-up duration.
//      * - Feed for the configured active duration.
//      *
//      * With the interlock enabled:
//      * - Wait for stable shooter readiness.
//      * - Skip feeding if the readiness timeout expires.
//      * - Apply automatic RPM-based pauses when feeding.
//      *
//      * The optional gate delay remains controlled by the
//      * V4 FeederSubsystem.
//      *
//      * The shooter is not stopped inside this helper.
//      * This is controlled separately by the main routine.
//      *
//      * @param preset Shooter RPM preset.
//      * @param feedMs Requested active feeder time.
//      * @return Complete Ivy shooting command.
//      */
//     private Command shootCycle(
//             V4Settings.Preset preset,
//             long feedMs) {
// 
//         return sequential(
// 
//                 // Select shooter target RPM.
//                 MechanismCommands.preset(
//                         robot,
//                         preset
//                 ),
// 
//                 // Start intake and linked side rollers.
//                 RUN_INTAKE_WHILE_SHOOTING
//                         ? MechanismCommands.intakeStart(robot)
//                         : skip(),
// 
//                 // Start the shooter flywheel.
//                 MechanismCommands.shooterOn(robot),
// 
//                 // Wait and feed using the selected
//                 // interlock configuration.
//                 AUTO_SHOOTER_INTERLOCK
//                         ? MechanismCommands.shootPreloads(
//                         robot,
//                         feedMs
//                 )
//                         : sequential(
// 
//                         waitMilliseconds(
//                                 SHOOTER_SPINUP_DELAY_MS
//                         ),
// 
//                         MechanismCommands.feederForMs(
//                                 robot,
//                                 feedMs,
//                                 FEED_WALL_TIMEOUT_MS
//                         )
//                 ),
// 
//                 // Ensure feeder request is canceled.
//                 MechanismCommands.feederStop(robot),
// 
//                 // Stop intake and side rollers.
//                 MechanismCommands.intakeStop(robot)
//         );
//     }
// 
// 
//     // =====================================================
//     // SECTION 5 - MAIN AUTONOMOUS IVY ROUTINE
//     // =====================================================
//     //
//     // This is the primary section for changing the
//     // behavior and ordering of Autonomous actions.
//     //
//     // All actions execute sequentially unless grouped
//     // with a parallel Ivy command.
//     // =====================================================
// 
//     private Command autoRoutine() {
// 
//         return sequential(
// 
//                 // =========================================
//                 // PHASE 0 - ALLIANCE PARTNER DELAY
//                 // =========================================
// 
//                 stage("START DELAY"),
// 
//                 waitMilliseconds(
//                         AUTO_START_DELAY_MS
//                 ),
// 
// 
//                 // =========================================
//                 // PHASE 1 - SHOOT FOUR PRELOADS
//                 // =========================================
// 
//                 stage("SHOOTING PRELOADS"),
// 
//                 ENABLE_FIRST_SHOOTING
//                         ? shootCycle(
//                         FIRST_SHOOTER_PRESET,
//                         FIRST_FEED_MS
//                 )
//                         : skip(),
// 
//                 MechanismCommands.feederStop(robot),
// 
//                 MechanismCommands.intakeStop(robot),
// 
//                 STOP_SHOOTER_BETWEEN_CYCLES
//                         ? MechanismCommands.shooterOff(robot)
//                         : skip(),
// 
// 
//                 // =========================================
//                 // PHASE 2 - APPROACH GARDEN
//                 // =========================================
// 
//                 stage("PATH 1 - GARDEN APPROACH"),
// 
//                 ENABLE_PATH_1
//                         ? follow(follower, path1())
//                         : skip(),
// 
// 
//                 // =========================================
//                 // PHASE 3 - INTAKE THROUGH GARDEN
//                 // =========================================
// 
//                 stage("PATH 2 - GARDEN COLLECTION"),
// 
//                 ENABLE_PATH_1 && ENABLE_PATH_2
//                         ? sequential(
// 
//                         // Activate main intake and
//                         // linked side rollers.
//                         ENABLE_GARDEN_INTAKE
//                                 ? MechanismCommands.intakeStart(robot)
//                                 : skip(),
// 
//                         // Drive into the garden.
//                         follow(follower, path2())
//                 )
//                         : skip(),
// 
// 
//                 // =========================================
//                 // PHASE 4 - STATIONARY COLLECTION DELAY
//                 // =========================================
// 
//                 stage("GARDEN COLLECTION WAIT"),
// 
//                 ENABLE_PATH_1 && ENABLE_PATH_2
//                         ? waitMilliseconds(
//                         GARDEN_COLLECTION_WAIT_MS
//                 )
//                         : skip(),
// 
// 
//                 // =========================================
//                 // PHASE 5 - WITHDRAW FROM GARDEN
//                 // =========================================
// 
//                 stage("PATH 3 - GARDEN WITHDRAWAL"),
// 
//                 ENABLE_PATH_1 && ENABLE_PATH_2 && ENABLE_PATH_3
//                         ? follow(follower, path3())
//                         : skip(),
// 
//                 // Stop intake after garden collection.
//                 MechanismCommands.intakeStop(robot),
// 
// 
//                 // =========================================
//                 // PHASE 6 - TRAVEL TO OPPOSITE SHOOTER
//                 // =========================================
// 
//                 stage("PATH 4 - OPPOSITE SHOOTING"),
// 
//                 ENABLE_PATH_1 && ENABLE_PATH_2
//                         && ENABLE_PATH_3 && ENABLE_PATH_4
//                         ? follow(follower, path4())
//                         : skip(),
// 
// 
//                 // =========================================
//                 // PHASE 7 - SECOND FOUR-BALL SHOOT
//                 // =========================================
// 
//                 stage("SECOND SHOOTING CYCLE"),
// 
//                 ENABLE_SECOND_SHOOTING
//                         && ENABLE_PATH_1
//                         && ENABLE_PATH_2
//                         && ENABLE_PATH_3
//                         && ENABLE_PATH_4
//                         ? shootCycle(
//                         SECOND_SHOOTER_PRESET,
//                         SECOND_FEED_MS
//                 )
//                         : skip(),
// 
//                 MechanismCommands.feederStop(robot),
// 
//                 MechanismCommands.intakeStop(robot),
// 
//                 STOP_SHOOTER_BEFORE_PARK
//                         ? MechanismCommands.shooterOff(robot)
//                         : skip(),
// 
// 
//                 // =========================================
//                 // PHASE 8 - FINAL PARKING PATH
//                 // =========================================
// 
//                 stage("PATH 5 - PARKING"),
// 
//                 ENABLE_PATH_1 && ENABLE_PATH_2
//                         && ENABLE_PATH_3 && ENABLE_PATH_4
//                         && ENABLE_PATH_5
//                         ? follow(follower, path5())
//                         : skip(),
// 
// 
//                 // =========================================
//                 // PHASE 9 - FINAL SHUTDOWN
//                 // =========================================
// 
//                 stage("FINAL MECHANISM SHUTDOWN"),
// 
//                 MechanismCommands.feederStop(robot),
// 
//                 MechanismCommands.intakeStop(robot),
// 
//                 MechanismCommands.shooterOff(robot),
// 
//                 stage("AUTONOMOUS COMPLETE")
//         );
//     }
// 
// 
//     // =====================================================
//     // SECTION 6 - PEDRO PATH DEFINITIONS
//     // =====================================================
//     //
//     // All five original paths are preserved.
//     //
//     // Path 2 and Path 3 use constant headings.
//     // Path 1, Path 4, and Path 5 use linear heading
//     // interpolation.
//     // =====================================================
// 
//     /**
//      * PATH 1 - GARDEN INTAKE-READY POSITION
//      *
//      * Start: (57.8692, 8.1869, 90)
//      * End:   (10.2041, 22.3547, 270)
//      *
//      * Straight line with linear heading interpolation.
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
//      * PATH 2 - FORWARD GARDEN COLLECTION
//      *
//      * Start: (10.2041, 22.3547, 270)
//      * End:   (7.7383, 8.0298, 270)
//      *
//      * Straight line with constant heading.
//      *
//      * Intake runs during this path.
//      */
//     private Path path2() {
// 
//         return Paths.line(
//                 path1End,
//                 point2
//         ).constant(
//                 point2
//         );
//     }
// 
//     /**
//      * PATH 3 - GARDEN WITHDRAWAL
//      *
//      * Start: (7.7383, 8.0298, 270)
//      * End:   (10.2079, 22.2037, 270)
//      *
//      * Straight line with constant heading.
//      *
//      * Intake remains active until this path finishes.
//      */
//     private Path path3() {
// 
//         return Paths.line(
//                 point2,
//                 point3
//         ).constant(
//                 point3
//         );
//     }
// 
//     /**
//      * PATH 4 - OPPOSITE SHOOTING POSITION
//      *
//      * Start:   (10.2079, 22.2037, 270)
//      * Control: (78.1738, 0.871, 0)
//      * End:     (58.4456, 132.6705, 90)
//      *
//      * Curved path with linear heading interpolation.
//      *
//      * CAUTION:
//      * The control point is close to the field boundary.
//      * Confirm robot clearance in the Visualizer.
//      */
//     private Path path4() {
// 
//         return Paths.curve(
//                 point4Start,
//                 point4Control1,
//                 point4End
//         ).linear(
//                 point4Start,
//                 point4End
//         );
//     }
// 
//     /**
//      * PATH 5 - FINAL PARKING PATH
//      *
//      * Start:   (58.4456, 132.6705, 90)
//      * Control: (63.4190, 104.9091, 0)
//      * End:     (11.0076, 92.2988, 0)
//      *
//      * Curved path with linear heading interpolation.
//      */
//     private Path path5() {
// 
//         return Paths.curve(
//                 point5Start,
//                 point5Control1,
//                 point5End
//         ).linear(
//                 point5Start,
//                 point5End
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
//         // Invalidate stale Auto-to-TeleOp localization.
//         MatchHandoff.beginAuto();
// 
//         // Initialize the Pedro follower.
//         follower = Constants_GoBuilda.create(hardwareMap);
// 
//         // Set the original starting pose.
//         follower.setPose(start);
//         follower.update();
// 
//         // Initialize the V4 mechanism container.
//         robot = new Robot(hardwareMap);
// 
//         // Save the standard shooter interlock preference.
//         originalShooterInterlock =
//                 V4Settings.Shooter.INTERLOCK;
// 
//         interlockOverridden = false;
//         complete = false;
// 
//         currentStage = "READY";
// 
//         // ---------------------------------------------
//         // INITIALIZATION TELEMETRY
//         // ---------------------------------------------
// 
//         telemetry.addLine("V4 EXPERIMENTAL GARDEN AUTO");
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
//                 "First shooting",
//                 ENABLE_FIRST_SHOOTING
//         );
// 
//         telemetry.addData(
//                 "Garden intake",
//                 ENABLE_GARDEN_INTAKE
//         );
// 
//         telemetry.addData(
//                 "Garden wait (ms)",
//                 GARDEN_COLLECTION_WAIT_MS
//         );
// 
//         telemetry.addData(
//                 "Second shooting",
//                 ENABLE_SECOND_SHOOTING
//         );
// 
//         telemetry.addData(
//                 "Shooter interlock",
//                 AUTO_SHOOTER_INTERLOCK
//         );
// 
//         telemetry.addData(
//                 "First feed (ms)",
//                 FIRST_FEED_MS
//         );
// 
//         telemetry.addData(
//                 "Second feed (ms)",
//                 SECOND_FEED_MS
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
//     // SECTION 8 - AUTONOMOUS START
//     // =====================================================
// 
//     @Override
//     public void start() {
// 
//         // Initialize safe mechanism starting states.
//         //
//         // - Shooter OFF
//         // - Feeder OFF
//         // - Intake OFF
//         // - Side rollers OFF
//         // - Flower arm in configured position
//         // - Sorting gate UP, if installed
//         robot.startAuto(AUTO_ARM_START);
// 
//         // Apply this Auto's interlock preference.
//         V4Settings.Shooter.INTERLOCK =
//                 AUTO_SHOOTER_INTERLOCK;
// 
//         interlockOverridden = true;
// 
//         autoStartedNs = System.nanoTime();
// 
//         // Build and schedule the complete Ivy routine.
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
//         // Update localization and Pedro path following.
//         follower.update();
// 
//         // Update the shooter and feeder state machines.
//         robot.update();
// 
//         // Execute all scheduled Ivy commands.
//         Scheduler.execute();
// 
//         // Save the measured pose for TeleOp.
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
//             // Stop installed mechanisms.
//             robot.stop();
// 
//             // Stop drivetrain output.
//             follower.manual(0, 0, 0);
// 
//             // Restore normal V4 interlock behavior.
//             restoreShooterInterlock();
// 
//             currentStage = "COMPLETE - IDLE";
//         }
// 
//         // ---------------------------------------------
//         // DRIVER STATION TELEMETRY
//         // ---------------------------------------------
// 
//         double elapsedSeconds =
//                 (System.nanoTime() - autoStartedNs)
//                         / 1_000_000_000.0;
// 
//         telemetry.addData(
//                 "Auto status",
//                 complete ? "COMPLETE" : "RUNNING"
//         );
// 
//         telemetry.addData(
//                 "Current stage",
//                 currentStage
//         );
// 
//         telemetry.addData(
//                 "Elapsed seconds",
//                 elapsedSeconds
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
//                     "Path distance remaining",
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
//     // SECTION 10 - RESTORE SHOOTER INTERLOCK
//     // =====================================================
// 
//     /**
//      * Restore the original global shooter interlock.
//      *
//      * Ensures TeleOp does not inherit this Autonomous's
//      * relaxed shooting configuration.
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
//         // Preserve the latest estimated pose for TeleOp.
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
//         // Stop all installed mechanisms.
//         if (robot != null) {
//             robot.stop();
//         }
// 
//         // Stop drivetrain output.
//         if (follower != null) {
//             follower.manual(0, 0, 0);
//         }
// 
//         // Restore the normal shooter interlock.
//         restoreShooterInterlock();
//     }
// }
// 
