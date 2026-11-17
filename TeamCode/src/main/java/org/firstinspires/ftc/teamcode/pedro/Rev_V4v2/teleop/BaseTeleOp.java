package org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.teleop;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.Constants_REV;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.config.V4Settings;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.core.*;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.navigation.*;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.subsystems.ManualMecanumDrive;

/** Shared lifecycle, not a generic control-binding system. */
public abstract class BaseTeleOp extends OpMode {
    protected abstract boolean dual();
    private final Buttons g1 = new Buttons(), g2 = new Buttons();
    private final Chord allianceChord = new Chord(), fallbackChord = new Chord();
    private Follower follower;
    private Robot robot;
    private DriveControl drive;
    private Navigator nav;
    private MatchHandoff.Result handoff;
    private boolean headingValid, poseValid;
    private String driveError;
    /** Origin of the pose currently used by Pedro: useful when debugging startup. */
    private String poseSource = "NOT INITIALIZED";
    /** Tracks the alliance whose configured fallback pose is currently loaded in INIT. */
    private V4Settings.Alliance fallbackAlliance = V4Settings.Alliance.UNDEFINED;
    private long telemetryAt;
    private Navigator.Result lastNav;

    @Override
    // Initialize once, then consume any valid pose-and-alliance handoff.
    public void init() {
        robot = new Robot(hardwareMap);
        try {
            if (V4Settings.Drive.BACKEND == V4Settings.DriveBackend.PEDRO) {
                initPedroDrive();
            } else {
                drive = new DriveControl(new ManualMecanumDrive(hardwareMap));
                poseSource = "MANUAL DRIVE";
            }
        } catch (RuntimeException e) {
            driveError = e.toString();
            headingValid = false;
            poseValid = false;
            poseSource = "DRIVE INIT FAILED";
        }
        showInit();
    }

    private void initPedroDrive() {
        follower = Constants_REV.create(hardwareMap);
        drive = new DriveControl(follower);
        nav = new Navigator(follower);
        lastNav = Navigator.Result.IDLE;
        handoff = MatchHandoff.consume();
        if (handoff.valid()) {
            follower.setPose(handoff.pose);
            drive.alliance(handoff.alliance);
            headingValid = true;
            poseValid = true;
            poseSource = "AUTO HANDOFF";
        } else {
            drive.alliance(V4Settings.Drive.DEFAULT_ALLIANCE);
            applyFallbackPose();
        }
    }

    /**
     * Start without Autonomous: seed the localizer at a configured parking pose.
     * This gives field-centric driving a heading, but it does NOT independently
     * verify the robot's physical X/Y location. Navigation is therefore disabled
     * by default until the operator explicitly enables fallback pose trust.
     */
    private void applyFallbackPose() {
        headingValid = false;
        poseValid = false;
        fallbackAlliance = V4Settings.Alliance.UNDEFINED;

        if (follower == null || drive == null) {
            poseSource = "NO FOLLOWER";
            return;
        }

        V4Settings.Alliance alliance = drive.alliance();
        Pose fallback = FieldLibrary.at(alliance, V4Settings.Navigation.FALLBACK_START_POSE);
        if (fallback == null) {
            poseSource = "FALLBACK UNAVAILABLE";
            return;
        }

        follower.setPose(fallback);
        fallbackAlliance = alliance;
        headingValid = true;
        poseValid = V4Settings.Navigation.TRUST_FALLBACK_POSITION_FOR_NAV;
        poseSource = "FALLBACK: " + V4Settings.Navigation.FALLBACK_START_POSE;
    }

    @Override
    public void init_loop() {
        g1.read(gamepad1);
        chords();

        // Only when Auto handoff is missing do we change the assumed starting
        // location after selecting another alliance in INIT. Never overwrite a
        // successful Auto handoff or teleport the pose during active TeleOp.
        if (driveError == null && follower != null && (handoff == null || !handoff.valid())
                && drive != null && drive.alliance() != fallbackAlliance) {
            applyFallbackPose();
        }
        showInit();
    }

    /** Driver Station INIT summary: handoff source and navigation trust are separate. */
    private void showInit() {
        telemetry.addData("SYSTEM", driveError != null ? "CRITICAL DRIVE FAILURE"
                : robot.healthy() ? "READY" : "READY WITH WARNINGS");
        telemetry.addData("DRIVE BACKEND", V4Settings.Drive.BACKEND);
        if (driveError != null) {
            telemetry.addData("DRIVE ERROR", driveError);
        }
        if (!robot.healthy()) {
            telemetry.addData("MECHANISM HARDWARE", robot.critical());
        }
        telemetry.addData("WARNING", robot.events.recent());
        if (driveError != null || follower == null) {
            telemetry.update();
            return;
        }
        telemetry.addData("HANDOFF", handoff == null ? "NONE" : handoff.status);
        telemetry.addData("POSE SOURCE", poseSource);
        if (handoff != null && handoff.valid()) {
            telemetry.addData("AUTO POSE", String.format("%.1f %.1f %.1f°",
                    handoff.pose.x(), handoff.pose.y(), Math.toDegrees(handoff.pose.heading())));
        }
        if (follower != null && headingValid) {
            Pose current = follower.pose();
            telemetry.addData("LOCALIZER POSE", String.format("%.1f %.1f %.1f°",
                    current.x(), current.y(), Math.toDegrees(current.heading())));
        }
        telemetry.addData("ALLIANCE", drive == null ? "NONE" : drive.alliance());
        telemetry.addData("FIELD HEADING", headingValid ? "INITIALIZED" : "UNAVAILABLE");
        telemetry.addData("DRIVER OFFSET", drive == null ? "N/A"
                : String.format("%.0f°", drive.alliance() == V4Settings.Alliance.BLUE
                        ? V4Settings.Drive.BLUE_OFFSET_DEG : V4Settings.Drive.RED_OFFSET_DEG));
        telemetry.addData("NAVIGATION POSE", poseValid ? "TRUSTED" : "UNVERIFIED / DISABLED");
        telemetry.addLine("No handoff: set robot at " + V4Settings.Navigation.FALLBACK_START_POSE
                + " before Play; otherwise use an Auto handoff.");
        telemetry.addLine("Share + Options: switch alliance in INIT; never assume fallback X/Y is measured.");
        telemetry.update();
    }

    @Override
    public void start() {
        if (driveError == null && robot != null) {
            robot.startTeleOp();
        }
    }

    @Override
    // On every frame: read controls, arbitrate manual versus pathing, update mechanisms.
    public void loop() {
        if (robot == null)return;
        g1.read(gamepad1);
        g2.read(gamepad2);
        if (driveError != null) {
            telemetry.addData("DRIVE ERROR", driveError);
            telemetry.update();
            return;
        }
        chords();
        if (g1.pls)drive.toggleSlow();
        double forward = -gamepad1.left_stick_y, strafe = -gamepad1.left_stick_x, turn = gamepad1.right_stick_x;
        if (follower == null) {
            drive.manual(forward, strafe, turn, false);
        } else {
            updatePedroDrive(forward, strafe, turn);
        }
        if (robot.intake != null) {
            boolean f = gamepad1.right_trigger>.15, r = gamepad1.left_trigger>.15;
            robot.intake.setDirection(f == r?0:f?1:-1);
        }
        Gamepad op = dual()?gamepad2:gamepad1;
        Buttons b = dual()?g2:g1;
        if (robot.shooter != null && b.plb) {
            if (robot.shooter.enabled())robot.stopShooter();
            else
                robot.shooter.enable(true);
        }
        if (robot.feeder != null && b.prb)robot.feeder.toggle();
        if (robot.feeder != null)robot.feeder.reverseHeld(dual()?op.right_trigger>.15:(!gamepad1.share && gamepad1.dpad_down));
        if (op.share) {
            if (V4Settings.Shooter.RPM_ADJUST_ENABLED && robot.shooter != null) {
                if (b.pu)robot.shooter.adjust(25);
                if (b.pd)robot.shooter.adjust(-25);
                if (b.pl)robot.shooter.adjust(-10);
                if (b.pr)robot.shooter.adjust(10);
            }
        }
        else
            if (dual()) {
            if (robot.shooter != null) {
                if (b.pu)robot.shooter.preset(V4Settings.Preset.NORMAL);
                if (b.pl)robot.shooter.preset(V4Settings.Preset.CLOSE);
                if (b.pr)robot.shooter.preset(V4Settings.Preset.ANGLED);
            }
            if (robot.arm != null) {
                if (b.py)robot.arm.moveTo(V4Settings.ArmPosition.UP);
                if (b.px)robot.arm.moveTo(V4Settings.ArmPosition.PARTIAL);
                if (b.pa)robot.arm.moveTo(V4Settings.ArmPosition.DOWN);
            }
            if (robot.sorter != null && b.pb)robot.sorter.toggle();
        }
        else {
            if (robot.shooter != null && b.pu)robot.shooter.cycle();
            if (robot.arm != null && b.pr)robot.arm.cycle();
            if (robot.sorter != null && b.pl)robot.sorter.toggle();
        }
        robot.update();
        if (robot.shooter != null && robot.shooter.consumeReadyEvent())rumble(op, V4Settings.Feedback.READY, V4Settings.Feedback.READY_MS);
        report();
    }

    private void updatePedroDrive(double forward, double strafe, double turn) {
        double stickMagnitude = Math.max(Math.abs(forward), Math.max(Math.abs(strafe), Math.abs(turn)));
        boolean cancelNavigation = stickMagnitude > V4Settings.Drive.NAV_CANCEL_DEADZONE;
        if (cancelNavigation && nav.active()) {
            nav.cancel();
        }
        if (V4Settings.Navigation.ENABLE && poseValid) {
            V4Settings.Destination destination = null;
            if (g1.pa) {
                destination = V4Settings.Navigation.A;
            } else if (g1.pb) {
                destination = V4Settings.Navigation.B;
            } else if (g1.px) {
                destination = V4Settings.Navigation.X;
            } else if (g1.py) {
                destination = V4Settings.Navigation.Y;
            }
            if (destination != null) {
                nav.start(drive.alliance(), destination, poseValid);
            }
        }
        if (!nav.active()) {
            drive.manual(forward, strafe, turn, headingValid);
        }
        follower.update();
        if (nav.active()) {
            nav.update();
        }
        if (lastNav != nav.result()) {
            if (nav.result() == Navigator.Result.SUCCESS) {
                rumble(gamepad1, V4Settings.Feedback.NAV_SUCCESS, V4Settings.Feedback.NAV_MS);
            }
            if (nav.result() == Navigator.Result.FAILED) {
                rumble(gamepad1, V4Settings.Feedback.NAV_FAILURE, V4Settings.Feedback.WARN_MS);
            }
            lastNav = nav.result();
        }
    }

    // Special button combinations take priority over ordinary gamepad modifiers.
    private void chords() {
        if (driveError != null || follower == null)return;
        if (allianceChord.step(gamepad1.share, gamepad1.options, V4Settings.Drive.CHORD_MS)) {
            if (nav != null)nav.cancel();
            drive.alliance(drive.alliance() == V4Settings.Alliance.BLUE?V4Settings.Alliance.RED:V4Settings.Alliance.BLUE);
            rumble(gamepad1, V4Settings.Feedback.MODE, V4Settings.Feedback.MODE_MS);
        }
        if (fallbackChord.step(gamepad1.options, gamepad1.right_stick_button && !gamepad1.share, V4Settings.Drive.CHORD_MS)) {
            if (nav != null)nav.cancel();
            drive.toggleFallback();
            rumble(gamepad1, V4Settings.Feedback.MODE, V4Settings.Feedback.MODE_MS);
        }
    }
    private void rumble(Gamepad g, boolean enabled, int duration) {
        if (enabled)g.rumble(V4Settings.Feedback.POWER, V4Settings.Feedback.POWER, duration);
    }
    // Only telemetry is rate-limited; motor updates continue on every loop.
    // Telemetry updates are throttled; robot control updates are not.
    private void report() {
        long ms = System.nanoTime()/1000000;
        if (ms-telemetryAt<(V4Settings.Telemetry.DETAILS?V4Settings.Telemetry.DETAIL_REFRESH_MS:V4Settings.Telemetry.REFRESH_MS))return;
        telemetryAt = ms;
        telemetry.addData("DRIVE BACKEND", V4Settings.Drive.BACKEND);
        telemetry.addData("DRIVE", drive.mode(headingValid));
        telemetry.addData("SPEED", drive.slow()?"SLOW":"FAST");
        if (follower != null) {
            telemetry.addData("ALLIANCE", drive.alliance());
            telemetry.addData("NAV", nav.result());
            telemetry.addData("POSE SOURCE", poseSource);
            telemetry.addData("HEADING", headingValid ? "INITIALIZED" : "UNAVAILABLE");
            telemetry.addData("NAV POSE", poseValid ? "TRUSTED" : "UNVERIFIED");
        }
        if (robot.shooter != null)telemetry.addData("SHOOTER", String.format("%s [ %s ] %.0f/%.0f RPM", robot.shooter.enabled()?"ON":"OFF", robot.shooter.preset(), robot.shooter.actual(), robot.shooter.target()));
        if (robot.feeder != null)telemetry.addData("FEEDER", robot.feeder.state());
        telemetry.addData("WARNING", robot.events.recent());
        if (V4Settings.Telemetry.DETAILS) {
            if (V4Settings.Telemetry.DRIVE && follower != null)telemetry.addData("X/Y/Heading", String.format("%.2f %.2f %.1f", follower.pose().x(), follower.pose().y(), Math.toDegrees(follower.pose().heading())));
            if (V4Settings.Telemetry.INTAKE && robot.intake != null)telemetry.addData("Intake direction", robot.intake.direction());
            if (V4Settings.Telemetry.NAV && nav != null)telemetry.addData("Nav route", nav.description());
            if (V4Settings.Telemetry.SHOOTER)telemetry.addData("Battery Volts", robot.voltage());
            if (V4Settings.Telemetry.ACCESSORIES) {
                telemetry.addData("Arm", robot.arm == null?"OFF":robot.arm.position());
                telemetry.addData("Sorting", robot.sorter == null?"OFF":robot.sorter.isUp());
            }
            if (V4Settings.Telemetry.EVENTS)for (String event:robot.events.all())telemetry.addLine(event);
        }
        telemetry.update();
    }

    @Override
    public void stop() {
        try {
            if (nav != null) {
                nav.cancel();
            }
            if (drive != null) {
                drive.stop();
            }
        } finally {
            if (robot != null) {
                robot.stop();
            }
        }
    }
}
