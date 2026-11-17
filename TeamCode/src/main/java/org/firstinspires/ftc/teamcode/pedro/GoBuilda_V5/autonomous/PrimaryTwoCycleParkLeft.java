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

@Autonomous(name = "Primary-Shoot-Intake Garden-Shoot-Park Left", group = "BIOBUZZ V5")
public final class PrimaryTwoCycleParkLeft extends BaseAuto {
    // ROUTINE SETTINGS
    private static final long PARTNER_DELAY_MS = 2000;
    private static final long SPINUP_DELAY_MS = V5Settings.Feeder.READY_TIMEOUT_MS;
    private static final long FEED_WALL_TIMEOUT_MS = V5Settings.Feeder.FEED_WALL_TIMEOUT_MS;
    private static final V5Settings.Preset FIRST_SHOOTER_PRESET = V5Settings.Preset.NORMAL;
    private static final V5Settings.Preset SECOND_SHOOTER_PRESET = V5Settings.Preset.NORMAL;
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
    private final Pose parkPose = poses.of(10.4469, 120.8978, 0);
    private final Pose parkControlPoint = poses.of(57.8113, 113.8814, 0);

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
    private Command shootWithIntake(V5Settings.Preset preset, long feedMs) {
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
//  * BIOBUZZ V4 - EXPERIMENTAL GARDEN / OPPOSITE PARK
//  *
//  * Full sequence:
//  *
//  *  1. Wait for the alliance partner.
//  *  2. Shoot four preloaded balls with intake running.
//  *  3. Path 1: Drive to garden intake-ready position.
//  *  4. Start intake and follow Path 2 into the garden.
//  *  5. Wait while collecting balls.
//  *  6. Follow Path 3 while continuing intake.
//  *  7. Stop garden intake.
//  *  8. Path 4: Drive to opposite shooting position.
//  *  9. Shoot collected balls with intake running.
//  * 10. Path 5: Drive to the opposite parking position.
//  * 11. Stop all mechanisms and save the final pose.
//  *
//  * The complete routine is coordinated using Pedro Ivy.
//  *
//  * Shooter RPM interlock is disabled by default for
//  * this Autonomous only.
//  *
//  * The interlock's original setting is restored when
//  * Autonomous finishes or stops.
//  *
//  * NOTE:
//  * Shooting and collection are time-based. The robot
//  * does not count or independently verify four balls.
//  *
//  * NOTE:
//  * This experimental routine may exceed the 30-second
//  * Autonomous period. Test and tune the timings.
//  */
// @Autonomous(
//         name = "Primary-Shoot-Intake Garden-Shoot-Park Left (RED)!",
//         group = "BIOBUZZ V4"
// )
// public final class PS2PL_Rxx extends OpMode {
// 
//     // =====================================================
//     // SECTION 1 - AUTONOMOUS CONFIGURATION
//     // =====================================================
//     //
//     // Adjust these values independently for this Auto.
//     // =====================================================
// 
//     /**
//      * Alliance associated with this routine.
//      *
//      * The alliance is transferred to TeleOp with the
//      * final measured pose.
//      *
//      * Changing alliance does not mirror path geometry.
//      */
//     private static final V4Settings.Alliance AUTO_ALLIANCE =
//             V4Settings.Alliance.RED;
// 
//     /**
//      * Delay before any shooting or driving begins.
//      *
//      * Default: 2000 milliseconds.
//      */
//     private static final long AUTO_START_DELAY_MS = 2000;
// 
//     /**
//      * Enable or disable the initial preload shooting.
//      */
//     private static final boolean ENABLE_FIRST_SHOOTING = true;
// 
//     /**
//      * Enable or disable the second shooting cycle.
//      */
//     private static final boolean ENABLE_SECOND_SHOOTING = true;
// 
//     /**
//      * Run the main intake and linked side rollers
//      * during both shooting cycles.
//      */
//     private static final boolean RUN_INTAKE_WHILE_SHOOTING = true;
// 
//     /**
//      * Enable garden collection.
//      *
//      * If false, the collection paths still execute,
//      * but the intake will remain stopped.
//      */
//     private static final boolean ENABLE_GARDEN_INTAKE = true;
// 
//     /**
//      * Stationary collection delay after Path 2.
//      *
//      * The intake remains active during this delay.
//      *
//      * Default: 2000 milliseconds.
//      */
//     private static final long GARDEN_COLLECTION_WAIT_MS = 2000;
// 
//     /**
//      * Enable shooter RPM-based feeder protection.
//      *
//      * false:
//      * - Wait a fixed spin-up duration.
//      * - Feed without requiring RPM readiness.
//      * - Do not pause feeding because of RPM drops.
//      *
//      * true:
//      * - Wait for stable RPM readiness.
//      * - Skip feeding if the readiness timeout expires.
//      * - Permit feeder pauses during RPM recovery.
//      *
//      * Default: false for Autonomous.
//      */
//     private static final boolean AUTO_SHOOTER_INTERLOCK = false;
// 
//     /**
//      * Shooter preset for the initial four preloads.
//      */
//     private static final V4Settings.Preset FIRST_SHOOTER_PRESET =
//             V4Settings.Preset.NORMAL;
// 
//     /**
//      * Shooter preset for the collected balls.
//      *
//      * Adjust independently for the opposite shooting
//      * position if needed.
//      */
//     private static final V4Settings.Preset SECOND_SHOOTER_PRESET =
//             V4Settings.Preset.NORMAL;
// 
//     /**
//      * Fixed shooter spin-up time with interlock OFF.
//      *
//      * Uses the established V4 timeout value:
//      * 3500 milliseconds.
//      */
//     private static final long SHOOTER_SPINUP_DELAY_MS =
//             V4Settings.Feeder.READY_TIMEOUT_MS;
// 
//     /**
//      * Active feeder-running duration for preloads.
//      *
//      * Default: 2800 milliseconds.
//      */
//     private static final long FIRST_FEED_MS = 2800;
// 
//     /**
//      * Active feeder-running duration for collected balls.
//      *
//      * Default: 2800 milliseconds.
//      *
//      * Tune separately from FIRST_FEED_MS.
//      */
//     private static final long SECOND_FEED_MS = 2800;
// 
//     /**
//      * Maximum wall-clock duration for either feed cycle.
//      *
//      * Default: 6000 milliseconds.
//      */
//     private static final long FEED_WALL_TIMEOUT_MS =
//             V4Settings.Feeder.FEED_WALL_TIMEOUT_MS;
// 
//     /**
//      * Stop shooter between shooting cycles.
//      *
//      * If false, the shooter remains running during
//      * the garden collection and cross-field drive.
//      */
//     private static final boolean STOP_SHOOTER_BETWEEN_CYCLES = true;
// 
//     /**
//      * Stop the shooter before executing the final
//      * parking path.
//      */
//     private static final boolean STOP_SHOOTER_BEFORE_PARK = true;
// 
//     /**
//      * Individual path toggles for experimentation.
//      *
//      * Paths depend on the endpoints of earlier paths.
//      * Disabling any path automatically prevents later
//      * paths from running.
//      */
//     private static final boolean ENABLE_PATH_1 = true;
//     private static final boolean ENABLE_PATH_2 = true;
//     private static final boolean ENABLE_PATH_3 = true;
//     private static final boolean ENABLE_PATH_4 = true;
//     private static final boolean ENABLE_PATH_5 = true;
// 
//     /**
//      * Initial flower arm position, if installed.
//      *
//      * Sorting gate is initialized UP in Autonomous.
//      */
//     private static final V4Settings.ArmPosition AUTO_ARM_START =
//             V4Settings.ArmPosition.UP;
// 
// 
//     // =====================================================
//     // SECTION 2 - PEDRO VISUALIZER COORDINATES
//     // =====================================================
//     //
//     // All paths retain their original coordinates.
//     //
//     // Path 5 uses the NEW opposite-parking geometry.
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
//      * Path 1 endpoint: Garden intake-ready position.
//      */
//     private final Pose path1End =
//             poseFactory.of(10.2041, 22.3547, 270);
// 
//     /**
//      * Path 2 endpoint: Garden collection position.
//      */
//     private final Pose point2 =
//             poseFactory.of(7.7383, 8.0298, 270);
// 
//     /**
//      * Path 3 endpoint: Garden withdrawal position.
//      */
//     private final Pose point3 =
//             poseFactory.of(10.2079, 22.2037, 270);
// 
//     /**
//      * Path 4 starting point.
//      *
//      * Matches Path 3's endpoint.
//      */
//     private final Pose point4Start =
//             poseFactory.of(10.2079, 22.2037, 270);
// 
//     /**
//      * Path 4 endpoint: Opposite shooting position.
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
//      * Path 5 starting point.
//      *
//      * Matches Path 4's endpoint.
//      */
//     private final Pose point5Start =
//             poseFactory.of(58.4456, 132.6705, 90);
// 
//     /**
//      * NEW Path 5 endpoint:
//      * Opposite parking position.
//      */
//     private final Pose point5End =
//             poseFactory.of(10.4469, 120.8978, 0);
// 
//     /**
//      * NEW Bezier control point for Path 5.
//      */
//     private final Pose point5Control1 =
//             poseFactory.of(57.8113, 113.8814, 0);
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
//      * Human-readable current stage for telemetry.
//      */
//     private String currentStage = "NOT STARTED";
// 
//     /**
//      * Autonomous start timestamp for elapsed-time
//      * telemetry.
//      */
//     private long autoStartedNs = 0;
// 
//     /**
//      * Preserves the original shooter interlock setting.
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
//      * The timer begins when the command starts.
//      *
//      * Pedro and mechanism periodic updates continue
//      * running during this delay.
//      *
//      * @param durationMs Delay in milliseconds.
//      * @return An Ivy command.
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
//      * Updates the current Autonomous stage for telemetry.
//      */
//     private Command stage(String description) {
// 
//         return instant(() -> {
//             currentStage = description;
//         });
//     }
// 
//     /**
//      * Instantaneous no-operation command.
//      *
//      * Used when optional actions are disabled.
//      */
//     private Command skip() {
// 
//         return instant(() -> {});
//     }
// 
//     /**
//      * Executes one complete shooting cycle.
//      *
//      * 1. Set shooter preset.
//      * 2. Start intake and side rollers, if enabled.
//      * 3. Start flywheel.
//      * 4. Wait for readiness or fixed spin-up time.
//      * 5. Run timed feeder.
//      * 6. Stop feeder.
//      * 7. Stop intake and side rollers.
//      *
//      * Shooter shutdown is handled separately.
//      *
//      * @param preset Desired shooter RPM preset.
//      * @param feedMs Desired active feeder-running time.
//      * @return Ivy shooting command.
//      */
//     private Command shootCycle(
//             V4Settings.Preset preset,
//             long feedMs) {
// 
//         return sequential(
// 
//                 // Select target shooting RPM.
//                 MechanismCommands.preset(
//                         robot,
//                         preset
//                 ),
// 
//                 // Run intake during shooting.
//                 RUN_INTAKE_WHILE_SHOOTING
//                         ? MechanismCommands.intakeStart(robot)
//                         : skip(),
// 
//                 // Start shooter flywheel.
//                 MechanismCommands.shooterOn(robot),
// 
//                 // Execute the requested shooting behavior.
//                 AUTO_SHOOTER_INTERLOCK
//                         ? MechanismCommands.shootPreloads(
//                         robot,
//                         feedMs
//                 )
//                         : sequential(
// 
//                         // Fixed spin-up delay.
//                         waitMilliseconds(
//                                 SHOOTER_SPINUP_DELAY_MS
//                         ),
// 
//                         // Timed feeder request.
//                         MechanismCommands.feederForMs(
//                                 robot,
//                                 feedMs,
//                                 FEED_WALL_TIMEOUT_MS
//                         )
//                 ),
// 
//                 // Cancel feeder request after shooting.
//                 MechanismCommands.feederStop(robot),
// 
//                 // Stop intake and linked side rollers.
//                 MechanismCommands.intakeStop(robot)
//         );
//     }
// 
// 
//     // =====================================================
//     // SECTION 5 - MAIN AUTONOMOUS IVY ROUTINE
//     // =====================================================
//     //
//     // Change this section to modify the action sequence.
//     //
//     // Every stage executes sequentially unless an
//     // explicit Ivy parallel group is introduced.
//     // =====================================================
// 
//     private Command autoRoutine() {
// 
//         return sequential(
// 
//                 // =========================================
//                 // PHASE 0 - ALLIANCE PARTNER START DELAY
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
//                 // PHASE 1 - INITIAL FOUR-PRELOAD SHOOT
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
//                 // PHASE 2 - PATH 1: APPROACH GARDEN
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
//                 // PHASE 3 - PATH 2: ENTER GARDEN
//                 // =========================================
// 
//                 stage("PATH 2 - GARDEN COLLECTION"),
// 
//                 ENABLE_PATH_1 && ENABLE_PATH_2
//                         ? sequential(
// 
//                         // Start garden intake before
//                         // entering the collection area.
//                         ENABLE_GARDEN_INTAKE
//                                 ? MechanismCommands.intakeStart(robot)
//                                 : skip(),
// 
//                         // Move through the garden.
//                         follow(follower, path2())
//                 )
//                         : skip(),
// 
// 
//                 // =========================================
//                 // PHASE 4 - STATIONARY GARDEN WAIT
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
//                 // PHASE 5 - PATH 3: WITHDRAW FROM GARDEN
//                 // =========================================
// 
//                 stage("PATH 3 - GARDEN WITHDRAWAL"),
// 
//                 ENABLE_PATH_1 && ENABLE_PATH_2
//                         && ENABLE_PATH_3
//                         ? follow(follower, path3())
//                         : skip(),
// 
//                 // Stop collection mechanisms.
//                 MechanismCommands.intakeStop(robot),
// 
// 
//                 // =========================================
//                 // PHASE 6 - PATH 4: OPPOSITE SHOOTING
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
//                 // PHASE 7 - SECOND SHOOTING CYCLE
//                 // =========================================
// 
//                 stage("SECOND FOUR-BALL SHOOT"),
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
//                 // PHASE 8 - PATH 5: OPPOSITE PARKING
//                 // =========================================
// 
//                 stage("PATH 5 - OPPOSITE PARK"),
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
//                 stage("FINAL SHUTDOWN"),
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
//     // Paths 1 through 4 are unchanged.
//     //
//     // Path 5 contains the new parking destination
//     // and Bezier control point.
//     // =====================================================
// 
//     /**
//      * PATH 1 - GARDEN INTAKE-READY POSITION
//      *
//      * Start: (57.8692, 8.1869, 90 degrees)
//      * End:   (10.2041, 22.3547, 270 degrees)
//      *
//      * Straight path with linear heading interpolation.
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
//      * PATH 2 - ENTER GARDEN
//      *
//      * Start: (10.2041, 22.3547, 270 degrees)
//      * End:   (7.7383, 8.0298, 270 degrees)
//      *
//      * Straight path with constant heading.
//      *
//      * Intake runs during this movement.
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
//      * PATH 3 - WITHDRAW FROM GARDEN
//      *
//      * Start: (7.7383, 8.0298, 270 degrees)
//      * End:   (10.2079, 22.2037, 270 degrees)
//      *
//      * Straight path with constant heading.
//      *
//      * Garden intake continues during this movement.
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
//      * Start:   (10.2079, 22.2037, 270 degrees)
//      * Control: (78.1738, 0.8710, 0 degrees)
//      * End:     (58.4456, 132.6705, 90 degrees)
//      *
//      * Curved path with linear heading interpolation.
//      *
//      * Verify full robot clearance near the field
//      * boundary before running this experimentally.
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
//      * PATH 5 - NEW OPPOSITE PARKING POSITION
//      *
//      * Start:
//      * (58.4456, 132.6705, 90 degrees)
//      *
//      * Control:
//      * (57.8113, 113.8814, 0 degrees)
//      *
//      * End:
//      * (10.4469, 120.8978, 0 degrees)
//      *
//      * Curved path with linear heading interpolation.
//      *
//      * This is the only path whose geometry differs
//      * from the previous experimental garden routine.
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
//         // Reset previous Ivy commands.
//         Scheduler.reset();
// 
//         // Invalidate old Auto-to-TeleOp localization.
//         MatchHandoff.beginAuto();
// 
//         // Construct Pedro using goBILDA constants.
//         follower = Constants_GoBuilda.create(hardwareMap);
// 
//         // Initialize the starting pose.
//         follower.setPose(start);
//         follower.update();
// 
//         // Initialize installed V4 mechanisms.
//         robot = new Robot(hardwareMap);
// 
//         // Preserve standard shooter interlock setting.
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
//         telemetry.addLine("V4 GARDEN - OPPOSITE PARK");
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
//         // Establish safe mechanism startup states.
//         //
//         // Shooter OFF
//         // Feeder OFF
//         // Intake OFF
//         // Side rollers OFF
//         // Flower arm at configured position
//         // Sorting gate UP, if installed
//         robot.startAuto(AUTO_ARM_START);
// 
//         // Temporarily apply this Auto's interlock choice.
//         V4Settings.Shooter.INTERLOCK =
//                 AUTO_SHOOTER_INTERLOCK;
// 
//         interlockOverridden = true;
// 
//         // Begin measuring Autonomous elapsed time.
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
//     // SECTION 9 - PERIODIC AUTONOMOUS LOOP
//     // =====================================================
// 
//     @Override
//     public void loop() {
// 
//         // Update Pedro localization and drivetrain.
//         follower.update();
// 
//         // Update all installed V4 mechanisms.
//         robot.update();
// 
//         // Execute scheduled Ivy commands.
//         Scheduler.execute();
// 
//         // Save the measured pose for TeleOp.
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
//             // Stop installed mechanisms.
//             robot.stop();
// 
//             // Stop drivetrain output.
//             follower.manual(0, 0, 0);
// 
//             // Restore standard shooter safety settings.
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
//      * Restore the previous global shooter interlock.
//      *
//      * Prevents this experimental Auto from affecting
//      * the next TeleOp.
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
//         // Save the last estimated pose for TeleOp.
//         if (follower != null) {
// 
//             MatchHandoff.save(
//                     follower.pose(),
//                     AUTO_ALLIANCE
//             );
//         }
// 
//         // Cancel unfinished Ivy commands.
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
//         // Restore the normal interlock setting.
//         restoreShooterInterlock();
//     }
// }
