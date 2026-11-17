package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.tools;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.config.V5Settings;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.subsystems.FeederSubsystem;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.subsystems.ShooterGateSubsystem;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.subsystems.ShooterSubsystem;

/** Controller-only tuning. Uses the COMP shooter and feeder with per-session values. */
@TeleOp(name = "V5 Shooter + Feeder Tuner", group = "Tuning")
public final class ShooterFeederTuner extends OpMode {
    private enum Setting {
        RPM("Target RPM", 25), P("Velocity P", 1), D("Velocity D", .1),
        TOLERANCE("Ready tolerance (RPM)", 10), STABILITY("Ready stability (ms)", 25);

        final String label;
        final double step;

        Setting(String label, double step) {
            this.label = label;
            this.step = step;
        }
    }

    private final Setting[] settings = Setting.values();
    private int selected;
    private ShooterSubsystem shooter;
    private FeederSubsystem feeder;
    private VoltageSensor voltage;
    private String error;
    private double rpm = V5Settings.Shooter.NORMAL_RPM;
    private double p = V5Settings.Shooter.P;
    private double d = V5Settings.Shooter.D;
    private double tolerance = V5Settings.Shooter.READY_TOL_RPM;
    private long stability = V5Settings.Shooter.READY_STABLE_MS;
    private boolean recovery = V5Settings.Shooter.RECOVERY_ENABLED;
    private boolean previousUp, previousDown, previousLeft, previousRight, previousY;
    private boolean volleyStarted;
    private double minimumVolleyRpm = Double.NaN;

    @Override
    public void init() {
        try {
            shooter = new ShooterSubsystem(hardwareMap);
            ShooterGateSubsystem gate = V5Settings.Hardware.SHOOT_GATE_ENABLED
                    ? new ShooterGateSubsystem(hardwareMap) : null;
            feeder = new FeederSubsystem(hardwareMap, gate);
            voltage = hardwareMap.voltageSensor.iterator().hasNext()
                    ? hardwareMap.voltageSensor.iterator().next() : null;
            applyTuning();
        } catch (RuntimeException failure) {
            error = failure.getMessage();
            stop();
        }
        report();
    }

    @Override
    public void loop() {
        if (shooter == null || feeder == null || error != null) {
            report();
            return;
        }

        readTuningControls();
        boolean spin = gamepad1.left_trigger > .15;
        boolean feed = spin && gamepad1.right_trigger > .15;
        shooter.enable(spin);
        shooter.update(voltage == null ? 12 : voltage.getVoltage());
        feeder.requestForward(feed, recovery);
        feeder.update(shooter);
        recordVolleyRpm(feed);
        report();
    }

    private void readTuningControls() {
        if (gamepad1.dpad_up && !previousUp) selected = (selected + settings.length - 1) % settings.length;
        if (gamepad1.dpad_down && !previousDown) selected = (selected + 1) % settings.length;
        int direction = (gamepad1.dpad_right && !previousRight ? 1 : 0)
                - (gamepad1.dpad_left && !previousLeft ? 1 : 0);
        if (direction != 0) {
            adjust(settings[selected], direction * settings[selected].step);
            applyTuning();
        }
        if (gamepad1.y && !previousY) recovery = !recovery;

        previousUp = gamepad1.dpad_up;
        previousDown = gamepad1.dpad_down;
        previousLeft = gamepad1.dpad_left;
        previousRight = gamepad1.dpad_right;
        previousY = gamepad1.y;
    }

    private void adjust(Setting setting, double amount) {
        switch (setting) {
            case RPM: rpm = Math.max(100, Math.min(V5Settings.Shooter.MAX_RPM, rpm + amount)); break;
            case P: p = Math.max(0, p + amount); break;
            case D: d = Math.max(0, d + amount); break;
            case TOLERANCE: tolerance = Math.max(1, tolerance + amount); break;
            case STABILITY: stability = Math.max(0, stability + (long) amount); break;
        }
    }

    private void applyTuning() {
        shooter.setTestTuning(rpm, p, d, tolerance, stability);
    }

    private void recordVolleyRpm(boolean feed) {
        if (!feed) {
            volleyStarted = false;
            return;
        }
        if (feeder.state() == FeederSubsystem.State.FORWARD || feeder.state() == FeederSubsystem.State.PAUSED) {
            minimumVolleyRpm = volleyStarted ? Math.min(minimumVolleyRpm, shooter.actual()) : shooter.actual();
            volleyStarted = true;
        }
    }

    private void report() {
        if (error != null) telemetry.addData("Hardware error", error);
        telemetry.addLine("LT spin | RT feed | D-pad select/adjust | Y recovery");
        Setting setting = settings[selected];
        telemetry.addData("Editing", "%s | Step %.2f", setting.label, setting.step);
        telemetry.addData("Tuning", "%.0f RPM, P %.2f, D %.2f, tolerance %.0f RPM, stable %d ms",
                rpm, p, d, tolerance, stability);
        if (shooter != null) {
            telemetry.addData("Actual / target RPM", "%.0f / %.0f", shooter.actual(), shooter.target());
            telemetry.addData("Ready", shooter.ready());
        }
        if (feeder != null) telemetry.addData("Feeder", feeder.state().display);
        telemetry.addData("Pause and recover", recovery ? "ON" : "OFF");
        if (!Double.isNaN(minimumVolleyRpm)) telemetry.addData("Volley minimum RPM", "%.0f", minimumVolleyRpm);
        telemetry.update();
    }

    @Override
    public void stop() {
        if (feeder != null) feeder.stop();
        if (shooter != null) shooter.enable(false);
    }
}
