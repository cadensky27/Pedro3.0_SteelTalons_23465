package org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.subsystems;
import com.qualcomm.robotcore.hardware.*;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.config.V4Settings;
/** Single 5203 6000-RPM 1:1 flywheel. Hub velocity PIDF owns the feedback loop. */
public final class ShooterSubsystem {
    private final DcMotorEx motor;
    private V4Settings.Preset preset = V4Settings.Preset.NORMAL;
    private boolean enabled, ready, readyEvent, rumbleArmed = true;
    private long stableSince = -1;
    private double target, actual, offset, appliedF = Double.NaN;
    public ShooterSubsystem(HardwareMap h) {
        motor = h.get(DcMotorEx.class, V4Settings.Hardware.SHOOTER);
        motor.setDirection(V4Settings.Shooter.REVERSED?DcMotorSimple.Direction.REVERSE:DcMotorSimple.Direction.FORWARD);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setVelocity(0);
    }
    public void enable(boolean on) {
        if (enabled != on)rumbleArmed = on;
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
        readyEvent = false;
        stableSince = -1;
    }
    private double baseline() {
        return preset == V4Settings.Preset.CLOSE?V4Settings.Shooter.CLOSE_RPM:preset == V4Settings.Preset.ANGLED?V4Settings.Shooter.ANGLED_RPM:V4Settings.Shooter.NORMAL_RPM;
    }
    public void preset(V4Settings.Preset p) {
        preset = p;
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
        offset = Math.max(0, Math.min(V4Settings.Shooter.MAX_RPM, baseline()+offset+delta))-baseline();
        rumbleArmed = true;
        reset();
    }
    // Refresh measured RPM/readiness without blocking driving or other mechanisms.
    public void update(double volts) {
        target = enabled?Math.max(0, Math.min(V4Settings.Shooter.MAX_RPM, baseline()+offset)):0;
        double scale = V4Settings.Shooter.VOLTAGE_F_COMP && volts>1?Math.max(1/V4Settings.Shooter.MAX_F_SCALE, Math.min(V4Settings.Shooter.MAX_F_SCALE, V4Settings.Shooter.NOMINAL_VOLTS/volts)):1;
        double f = V4Settings.Shooter.F*scale;
        if (Double.isNaN(appliedF) || Math.abs(appliedF-f)>.02) {
            motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(V4Settings.Shooter.P, V4Settings.Shooter.I, V4Settings.Shooter.D, f));
            appliedF = f;
        }
        motor.setVelocity(target*V4Settings.Shooter.TICKS_PER_REV*V4Settings.Shooter.GEAR_RATIO/60.0);
        actual = Math.abs(motor.getVelocity())*60.0/V4Settings.Shooter.TICKS_PER_REV/V4Settings.Shooter.GEAR_RATIO;
        if (!enabled || Math.abs(actual-target)>V4Settings.Shooter.READY_TOL_RPM) {
            ready = false;
            stableSince = -1;
            return;
        }
        long now = System.nanoTime()/1000000;
        if (stableSince<0)stableSince = now;
        if (!ready && now-stableSince >= V4Settings.Shooter.READY_STABLE_MS) {
            ready = true;
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
