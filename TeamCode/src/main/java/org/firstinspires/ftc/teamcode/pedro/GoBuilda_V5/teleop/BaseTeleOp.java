package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.teleop;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.Constants_GoBuilda;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.config.V5Settings;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.config.V5Settings.Destination;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.core.MatchHandoff;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.core.Robot;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.navigation.FieldLibrary;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.navigation.Navigator;

import static org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.teleop.Binding.Button.*;
import static org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.teleop.Binding.Mode.*;

/** Shared loop for the two competition layouts. Button assignments live here. */
public abstract class BaseTeleOp extends OpMode {
    // CONTROLS — SINGLE DRIVER (Gamepad 1)
    private final Binding singleIntake = new Binding(LT, HOLD);
    private final Binding singleOuttake = new Binding(LB, HOLD);
    private final Binding singleSlow = new Binding(L3, TOGGLE);
    private final Binding singleShooter = new Binding(RB, TOGGLE);
    private final Binding singleFeed = new Binding(RT, HOLD);
    private final Binding singleFeederReverse = new Binding(A, HOLD);
    private final Binding singleFullReverse = new Binding(Y, HOLD);
    private final Binding singleCyclePreset = new Binding(B, PRESS);
    // Options + X toggles recovery. Plain X is free for a future accessory.
    private final Binding singleRecovery = new Binding(X, PRESS);
    private final Binding singleShoot1 = new Binding(DOWN, PRESS);
    private final Binding singleShoot2 = new Binding(RIGHT, PRESS);
    private final Binding singleGarden = new Binding(LEFT, PRESS);
    private final Binding singleLoading = new Binding(UP, PRESS);
    // Share + D-pad trims RPM. Share gives these controls priority over navigation.
    private final Binding singleRpmUp25 = new Binding(UP, PRESS);
    private final Binding singleRpmDown25 = new Binding(DOWN, PRESS);
    private final Binding singleRpmDown10 = new Binding(LEFT, PRESS);
    private final Binding singleRpmUp10 = new Binding(RIGHT, PRESS);

    // CONTROLS — TWO DRIVER, DRIVER (Gamepad 1)
    private final Binding driverIntake = new Binding(RT, HOLD);
    private final Binding driverOuttake = new Binding(LT, HOLD);
    private final Binding driverSlow = new Binding(LB, TOGGLE);
    private final Binding driverLinedUp = new Binding(RB, PRESS);
    private final Binding driverShoot1 = new Binding(A, PRESS);
    private final Binding driverShoot2 = new Binding(B, PRESS);
    private final Binding driverGarden = new Binding(X, PRESS);
    private final Binding driverLoading = new Binding(Y, PRESS);

    // CONTROLS — TWO DRIVER, OPERATOR (Gamepad 2)
    private final Binding operatorShooter = new Binding(LT, HOLD);
    private final Binding operatorFeed = new Binding(RT, HOLD);
    private final Binding operatorFeederReverse = new Binding(RB, HOLD);
    private final Binding operatorFullReverse = new Binding(DOWN, HOLD);
    private final Binding operatorClose = new Binding(B, PRESS);
    private final Binding operatorNormal = new Binding(X, PRESS);
    private final Binding operatorAngled = new Binding(Y, PRESS);
    private final Binding operatorRecovery = new Binding(UP, PRESS);
    private final Binding operatorRpmDown = new Binding(LEFT, PRESS);
    private final Binding operatorRpmUp = new Binding(RIGHT, PRESS);
    // Plain A and Share + face buttons are currently free.

    // CONTROLS — BOTH LAYOUTS, Gamepad 1 field-centric / robot-centric chord
    private static final Binding.Button DRIVE_MODE_MODIFIER = OPTIONS;
    private static final Binding.Button DRIVE_MODE_BUTTON = R3;

    // Remap a shortcut's destination here; edit its actual pose in FieldLibrary.
    private static final Destination FIRST_SHORTCUT = Destination.ANGLED_SHOT_1;
    private static final Destination SECOND_SHORTCUT = Destination.ANGLED_SHOT_2;
    private static final Destination THIRD_SHORTCUT = Destination.SHOOT_1;
    private static final Destination FOURTH_SHORTCUT = Destination.SHOOT_2;

    private final Chord robotCentricChord = new Chord();
    private Robot robot;
    private Follower follower;
    private DriveControl drive;
    private Navigator navigator;
    private boolean poseAvailable;
    private String poseSource = "Unavailable";
    private String driveError;
    private boolean recoverBetweenShots;
    private long reverseHoldStarted = -1;
    private boolean fullReverseActive;
    private long linedUpUntil;
    private long telemetryAt;
    private Navigator.Result lastNavigatorResult = Navigator.Result.IDLE;

    protected abstract boolean twoDrivers();

    @Override
    public final void init() {
        recoverBetweenShots = V5Settings.Shooter.RECOVERY_ENABLED;
        robot = new Robot(hardwareMap);
        Pose start = MatchHandoff.consume();
        if (start != null) {
            poseSource = "AUTO HANDOFF";
        } else {
            Destination fallback = V5Settings.Navigation.FALLBACK_START_POSE;
            start = FieldLibrary.at(fallback);
            poseSource = "FALLBACK: " + fallback + (start == null ? " (unconfigured)" : "");
        }

        try {
            follower = Constants_GoBuilda.create(hardwareMap);
            if (start != null) {
                follower.setPose(start);
                poseAvailable = true;
            }
            drive = new DriveControl(follower);
            navigator = new Navigator(follower);
        } catch (RuntimeException failure) {
            driveError = "Drive/Pinpoint: " + failure.getMessage();
        }
        reportInit();
    }

    @Override
    public final void init_loop() {
        reportInit();
    }

    private void reportInit() {
        reportHardware();
        for (String event : robot.events.all()) telemetry.addLine(event);
        telemetry.addData("Pose source", poseSource);
        if (poseAvailable && follower != null) reportPose();
        telemetry.addData("BASIC Navigator", driveError == null && poseAvailable
                && V5Settings.Navigation.ENABLE ? "Available" : "Unavailable");
        telemetry.update();
    }

    @Override
    public final void start() {
        robot.startTeleOp();
    }

    @Override
    public final void loop() {
        if (driveError != null) {
            robot.stop();
            reportMatchStatus();
            return;
        }
        boolean linedUp = twoDrivers() && driverLinedUp.read(gamepad1);
        if (linedUp) sendLinedUpSignal();
        handleDriveAndNavigator();

        // Full reverse owns these outputs while it runs. Normal controls return on release.
        if (!updateJamClear()) {
            handleIntakeControls();
            handleShooterAndFeederControls();
        }
        robot.update();

        if (robot.shooter != null && robot.shooter.consumeReadyEvent()
                && System.nanoTime() >= linedUpUntil) {
            (twoDrivers() ? gamepad2 : gamepad1).rumble(V5Settings.Feedback.READY_MS);
        }
        reportMatchStatus();
    }

    private void handleDriveAndNavigator() {
        drive.setSlow((twoDrivers() ? driverSlow : singleSlow).read(gamepad1));
        if (robotCentricChord.step(DRIVE_MODE_MODIFIER.held(gamepad1), DRIVE_MODE_BUTTON.held(gamepad1),
                V5Settings.Drive.CHORD_MS)) {
            navigator.cancel();
            drive.toggleFieldCentric();
        }

        double forward = -gamepad1.left_stick_y;
        double strafe = -gamepad1.left_stick_x;
        double turn = -gamepad1.right_stick_x;
        double stickInput = Math.max(Math.abs(forward), Math.max(Math.abs(strafe), Math.abs(turn)));
        Destination destination = readNavigatorShortcut();

        // Read the sticks even during path following so the driver can take over immediately.
        if (stickInput > V5Settings.Drive.NAV_CANCEL_DEADZONE) {
            navigator.cancel();
        } else if (destination != null && V5Settings.Navigation.ENABLE) {
            navigator.start(destination, poseAvailable);
        }

        if (!navigator.active()) drive.manual(forward, strafe, turn, poseAvailable);
        follower.update();
        navigator.update();
        if (navigator.result() != lastNavigatorResult) {
            lastNavigatorResult = navigator.result();
            if (lastNavigatorResult == Navigator.Result.SUCCESS) {
                gamepad1.rumble(V5Settings.Feedback.NAV_SUCCESS_MS);
            } else if (lastNavigatorResult == Navigator.Result.FAILED) {
                gamepad1.rumble(V5Settings.Feedback.NAV_FAILURE_MS);
            }
        }
    }

    private Destination readNavigatorShortcut() {
        boolean unmodified = !gamepad1.share && !gamepad1.options;
        boolean first = (twoDrivers() ? driverShoot1 : singleShoot1).read(gamepad1, unmodified);
        boolean second = (twoDrivers() ? driverShoot2 : singleShoot2).read(gamepad1, unmodified);
        boolean third = (twoDrivers() ? driverGarden : singleGarden).read(gamepad1, unmodified);
        boolean fourth = (twoDrivers() ? driverLoading : singleLoading).read(gamepad1, unmodified);
        if (first) return FIRST_SHORTCUT;
        if (second) return SECOND_SHORTCUT;
        if (third) return THIRD_SHORTCUT;
        return fourth ? FOURTH_SHORTCUT : null;
    }

    private void handleIntakeControls() {
        boolean collect = (twoDrivers() ? driverIntake : singleIntake).read(gamepad1);
        boolean outtake = (twoDrivers() ? driverOuttake : singleOuttake).read(gamepad1);
        if (robot.intake != null) {
            int direction = (collect ? 1 : 0) - (outtake ? 1 : 0);
            robot.intake.setDirection(direction);
        }
    }

    // Add new shooter or feeder inputs here, after defining their bindings above.
    private void handleShooterAndFeederControls() {
        Gamepad operator = twoDrivers() ? gamepad2 : gamepad1;
        boolean shooterRequested = (twoDrivers() ? operatorShooter : singleShooter).read(operator);
        boolean feedRequested = (twoDrivers() ? operatorFeed : singleFeed).read(operator);
        boolean feederReverse = (twoDrivers() ? operatorFeederReverse : singleFeederReverse)
                .read(operator, !operator.share && !operator.options);
        boolean recoveryPressed = twoDrivers()
                ? operatorRecovery.read(operator, !operator.share)
                : singleRecovery.read(operator, operator.options);
        if (recoveryPressed) recoverBetweenShots = !recoverBetweenShots;

        if (robot.shooter != null) {
            robot.shooter.enable(shooterRequested);
            handlePresetAndRpmControls(operator);
        }
        if (robot.feeder == null) return;

        // Feeder-only reverse has priority over forward feeding and works without the shooter.
        robot.feeder.reverseHeld(feederReverse);
        if (!feederReverse) {
            // A held trigger survives spin-up. Releasing feed or turning the shooter off cancels it.
            robot.feeder.requestForward(feedRequested && shooterRequested, recoverBetweenShots);
        }
    }

    private void handlePresetAndRpmControls(Gamepad operator) {
        int trim = 0;
        if (twoDrivers()) {
            boolean plain = !operator.share;
            if (operatorClose.read(operator, plain)) robot.shooter.preset(V5Settings.Preset.CLOSE);
            if (operatorNormal.read(operator, plain)) robot.shooter.preset(V5Settings.Preset.NORMAL);
            if (operatorAngled.read(operator, plain)) robot.shooter.preset(V5Settings.Preset.ANGLED);
            if (operatorRpmDown.read(operator, plain)) trim -= 5;
            if (operatorRpmUp.read(operator, plain)) trim += 5;
        } else {
            if (singleCyclePreset.read(operator, !operator.options)) robot.shooter.cycle();
            if (singleRpmUp25.read(operator, operator.share)) trim += 25;
            if (singleRpmDown25.read(operator, operator.share)) trim -= 25;
            if (singleRpmDown10.read(operator, operator.share)) trim -= 10;
            if (singleRpmUp10.read(operator, operator.share)) trim += 10;
        }
        if (V5Settings.Shooter.RPM_ADJUST_ENABLED && trim != 0) robot.shooter.adjust(trim);
    }

    /** Returns true while full reverse owns the mechanisms, including the release loop. */
    private boolean updateJamClear() {
        Gamepad operator = twoDrivers() ? gamepad2 : gamepad1;
        boolean held = (twoDrivers() ? operatorFullReverse : singleFullReverse)
                .read(operator, !operator.share && !operator.options);
        if (!V5Settings.JamClear.ENABLED || !held) {
            reverseHoldStarted = -1;
            if (!fullReverseActive) return false;

            fullReverseActive = false;
            robot.stop();
            // A previous held or toggled shot must not restart after a clear.
            (twoDrivers() ? operatorShooter : singleShooter).clearUntilReleased(operator);
            (twoDrivers() ? operatorFeed : singleFeed).clearUntilReleased(operator);
            (twoDrivers() ? operatorFeederReverse : singleFeederReverse).clearUntilReleased(operator);
            (twoDrivers() ? driverIntake : singleIntake).clearUntilReleased(gamepad1);
            (twoDrivers() ? driverOuttake : singleOuttake).clearUntilReleased(gamepad1);
            return true;
        }
        if (reverseHoldStarted < 0) reverseHoldStarted = System.nanoTime();
        if ((System.nanoTime() - reverseHoldStarted) / 1_000_000L < V5Settings.JamClear.ACTIVATION_HOLD_MS) {
            return false;
        }

        if (!fullReverseActive) robot.stopShooter();
        fullReverseActive = true;
        if (robot.shooter != null) robot.shooter.jamClearHeld(true);
        // Let the flywheel coast before pushing pieces backward through the system.
        boolean canReverse = robot.shooter == null || robot.shooter.reversing();
        if (robot.feeder != null) robot.feeder.reverseHeld(canReverse, V5Settings.JamClear.FEEDER_REVERSE_POWER);
        if (robot.intake != null) robot.intake.setDirection(canReverse && V5Settings.JamClear.REVERSE_INTAKE ? -1 : 0);
        return true;
    }

    private void sendLinedUpSignal() {
        int pulse = V5Settings.Feedback.LINED_UP_PULSE_MS;
        int gap = V5Settings.Feedback.LINED_UP_GAP_MS;
        double power = V5Settings.Feedback.POWER;
        gamepad2.runRumbleEffect(new Gamepad.RumbleEffect.Builder()
                .addStep(power, power, pulse)
                .addStep(0, 0, gap)
                .addStep(power, power, pulse)
                .build());
        linedUpUntil = System.nanoTime() + (2L * pulse + gap) * 1_000_000L;
    }

    private void reportHardware() {
        if (driveError != null) telemetry.addData("Drive error", driveError);
        if (!robot.healthy()) telemetry.addData("Mechanism errors", robot.critical());
        telemetry.addData("Available", "Intake %s | Shooter %s | Feeder %s",
                robot.intake != null, robot.shooter != null, robot.feeder != null);
        telemetry.addData("Accessories available", "Arm %s | Sorter %s | Shot gate %s",
                robot.arm != null, robot.sorter != null, robot.gate != null);
    }

    private void reportPose() {
        Pose pose = follower.pose();
        telemetry.addData("Pose", "X %.1f in | Y %.1f in | %.1f°",
                pose.x(), pose.y(), Math.toDegrees(pose.heading()));
    }

    private void reportMatchStatus() {
        long now = System.nanoTime();
        if ((now - telemetryAt) / 1_000_000L < V5Settings.Telemetry.REFRESH_MS) return;
        telemetryAt = now;
        if (driveError != null) {
            telemetry.addData("Drive error", driveError);
        } else {
            telemetry.addData("Drive", "%s | %s",
                    drive.fieldCentric(poseAvailable) ? "Field-centric" : "Robot-centric",
                    drive.slow() ? "Slow" : "Normal");
            telemetry.addData("Navigator", V5Settings.Navigation.ENABLE ? navigator.status() : "Disabled");
        }
        telemetry.addData("Pose source", poseSource);
        if (robot.shooter != null) {
            telemetry.addData("Shooter", "%s | %.0f / %.0f RPM | %s",
                    robot.shooter.preset(), robot.shooter.actual(), robot.shooter.target(),
                    robot.shooter.ready() ? "Ready" : robot.shooter.enabled() ? "Spinning up" : "Off");
        }
        if (robot.feeder != null) telemetry.addData("Feeder", robot.feeder.state().display);
        telemetry.addData("RPM recovery", recoverBetweenShots ? "ON" : "OFF");
        if (fullReverseActive) {
            telemetry.addData("Jam clear", robot.shooter != null && !robot.shooter.reversing()
                    ? "Waiting for flywheel to slow" : "Full reverse active");
        }
        if (V5Settings.Telemetry.SHOW_TELEOP_DIAGNOSTICS) {
            reportHardware();
            if (follower != null) reportPose();
            telemetry.addData("Battery volts", robot.voltage());
            if (robot.intake != null) telemetry.addData("Intake direction", robot.intake.direction());
            if (robot.arm != null) telemetry.addData("Arm", robot.arm.position());
            if (robot.sorter != null) telemetry.addData("Sorting gate up", robot.sorter.isUp());
            for (String event : robot.events.all()) telemetry.addLine(event);
        }
        telemetry.update();
    }

    @Override
    public final void stop() {
        if (robot != null) robot.stop();
        if (follower != null) {
            follower.manual(0, 0, 0);
            follower.update();
        }
    }
}
