package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.subsystems;
import com.qualcomm.robotcore.hardware.*;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.config.V4Settings;
/** Single 5203 6000-RPM 1:1 flywheel. Hub velocity PIDF owns the feedback loop. */
public final class ShooterSubsystem {
    private final DcMotorEx motor;
    private V4Settings.Preset preset = V4Settings.Preset.NORMAL;
    private boolean enabled, ready, readyEvent, rumbleArmed = true;
    /** Jam clear bypasses normal velocity PIDF and applies bounded reverse power. */
    private boolean jamClearRequested, jamClearReversing;
    private long stableSince = -1;
    private double target, actual, offset;
    private boolean initialReady;
    private double p = V4Settings.Shooter.P;
    private double d = V4Settings.Shooter.D;
    private final double i = V4Settings.Shooter.I;
    private final double nominalF = V4Settings.Shooter.F;
    private double toleranceRpm = V4Settings.Shooter.READY_TOL_RPM;
    private long stableMs = V4Settings.Shooter.READY_STABLE_MS;
    private double appliedP = Double.NaN, appliedD, appliedF;
    private Double testTargetRpm;
    public ShooterSubsystem(HardwareMap h) {
        motor = h.get(DcMotorEx.class, V4Settings.Hardware.SHOOTER);
        motor.setDirection(V4Settings.Shooter.REVERSED?DcMotorSimple.Direction.REVERSE:DcMotorSimple.Direction.FORWARD);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setVelocity(0);
    }
    public void enable(boolean on) {
        // Normal shooting and reverse clearing must never command the motor together.
        if (jamClearRequested) jamClearHeld(false);
        if (enabled == on) return;
        rumbleArmed = on;
        enabled = on;
        reset();
        if (!on) {
            target = 0;
            motor.setVelocity(0);
        }
    }
    public void toggle() {
        enable(!enabled);
    }
    public boolean enabled() {
        return enabled;
    }
    private void reset() {
        ready = false;
        initialReady = false;
        readyEvent = false;
        stableSince = -1;
    }
    private double baseline() {
        if (testTargetRpm != null) return testTargetRpm;
        return preset == V4Settings.Preset.CLOSE?V4Settings.Shooter.CLOSE_RPM:preset == V4Settings.Preset.ANGLED?V4Settings.Shooter.ANGLED_RPM:V4Settings.Shooter.NORMAL_RPM;
    }
    public void preset(V4Settings.Preset p) {
        preset = p;
        testTargetRpm = null;
        offset = 0;
        rumbleArmed = true;
        reset();
    }
    public V4Settings.Preset preset() {
        return preset;
    }
    public void cycle() {
        preset(preset == V4Settings.Preset.NORMAL?V4Settings.Preset.CLOSE:preset == V4Settings.Preset.CLOSE?V4Settings.Preset.ANGLED:V4Settings.Preset.NORMAL);
    }
    public void adjust(double delta) {
        double nextOffset = Math.max(0, Math.min(V4Settings.Shooter.MAX_RPM, baseline() + offset + delta)) - baseline();
        if (nextOffset != offset) {
            offset = nextOffset;
            reset();
        }
    }

    /** Changes apply only to this instance of the shooter in the pit tuner. */
    public void setTestTuning(double rpm, double newP, double newD, double tolerance, long timeMs) {
        testTargetRpm = rpm;
        offset = 0;
        p = newP;
        d = newD;
        toleranceRpm = tolerance;
        stableMs = timeMs;
        rumbleArmed = true;
        reset();
    }

    /** Latched until shooter shutdown, reversal, or target/tuning change. */
    public boolean reachedInitialSpeed() {
        return initialReady;
    }

    public boolean reversing() {
        return jamClearReversing;
    }

    /**
     * Start/stop the coordinated reverse shooter mode. Called only after the
     * TeleOp's 500-ms button hold has completed. Release is immediate.
     *
     * An RPM target alone cannot limit a stalled motor's torque. For this
     * reason reverse uses RUN_WITHOUT_ENCODER and explicit output limiting.
     */
    public void jamClearHeld(boolean held) {
        if (!held) {
            if (jamClearRequested) {
                motor.setPower(0);
                if (jamClearReversing) {
                    motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    appliedP = Double.NaN;
                }
                motor.setVelocity(0);
            }
            jamClearRequested = false;
            jamClearReversing = false;
            return;
        }

        if (!jamClearRequested) {
            enabled = false;
            target = 0;
            reset();
            motor.setVelocity(0);
            jamClearRequested = true;
            jamClearReversing = false;
        }
    }

    /** True when the driver has activated coordinated reverse mode. */
    public boolean jamClearing() {
        return jamClearRequested;
    }
    // Refresh measured RPM/readiness without blocking driving or other mechanisms.
    public void update(double volts) {
        if (jamClearRequested) {
            double signedRpm = motor.getVelocity() * 60.0
                    / V4Settings.Shooter.TICKS_PER_REV
                    / V4Settings.Shooter.GEAR_RATIO;
            actual = Math.abs(signedRpm);
            target = 0;

            // Coast down first: never command reverse into a spinning flywheel.
            if (!jamClearReversing) {
                motor.setVelocity(0);
                if (actual <= V4Settings.JamClear.START_MAX_ABS_RPM) {
                    motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    jamClearReversing = true;
                }
            }

            if (jamClearReversing) {
                double maxPower = Math.max(0, Math.min(1.0,
                        V4Settings.JamClear.SHOOTER_MAX_REVERSE_POWER));
                double targetRpm = -Math.abs(V4Settings.JamClear.SHOOTER_REVERSE_RPM);
                double error = targetRpm - signedRpm;
                // Nonpositive power only: never drive forward during jam clear.
                double power = Math.max(-maxPower, Math.min(0,
                        error * V4Settings.JamClear.SHOOTER_REVERSE_KP));
                motor.setPower(power);
            }
            return;
        }
        target = enabled?Math.max(0, Math.min(V4Settings.Shooter.MAX_RPM, baseline()+offset)):0;
        double scale = V4Settings.Shooter.VOLTAGE_F_COMP && volts>1?Math.max(1/V4Settings.Shooter.MAX_F_SCALE, Math.min(V4Settings.Shooter.MAX_F_SCALE, V4Settings.Shooter.NOMINAL_VOLTS/volts)):1;
        double f = nominalF * scale;
        if (appliedP != p || appliedD != d || Math.abs(appliedF - f) > .02) {
            motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(p, i, d, f));
            appliedP = p;
            appliedD = d;
            appliedF = f;
        }
        motor.setVelocity(target*V4Settings.Shooter.TICKS_PER_REV*V4Settings.Shooter.GEAR_RATIO/60.0);
        actual = Math.abs(motor.getVelocity())*60.0/V4Settings.Shooter.TICKS_PER_REV/V4Settings.Shooter.GEAR_RATIO;
        if (!enabled || target <= 0 || Math.abs(actual - target) > toleranceRpm) {
            ready = false;
            stableSince = -1;
            return;
        }
        long now = System.nanoTime()/1000000;
        if (stableSince<0)stableSince = now;
        if (!ready && now - stableSince >= stableMs) {
            ready = true;
            initialReady = true;
            if (rumbleArmed) {
                readyEvent = true;
                rumbleArmed = false;
            }
        }
    }
    public boolean consumeReadyEvent() {
        boolean e = readyEvent;
        readyEvent = false;
        return e;
    }
    public boolean ready() {
        return ready;
    }
    public double target() {
        return target;
    }
    public double actual() {
        return actual;
    }
}
