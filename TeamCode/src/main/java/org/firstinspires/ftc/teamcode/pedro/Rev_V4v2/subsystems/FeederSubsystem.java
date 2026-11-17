package org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.subsystems;
import com.qualcomm.robotcore.hardware.*;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.config.V4Settings;
/** Nonblocking continuous CR roller feeder; gate and RPM thresholds handled here. */
public final class FeederSubsystem {
    public enum State {
        OFF, WAIT_RPM, OPEN_GATE, FORWARD, PAUSED, REVERSE
    }
    private final CRServo servo;
    private final ShooterGateSubsystem gate;
    private State state = State.OFF;
    private boolean feedRequest, reverseHeld;
    private long gateAt = -1, recoveredAt = -1, previousUpdate = System.nanoTime();
    private long forwardNanos;
    public FeederSubsystem(HardwareMap h, ShooterGateSubsystem g) {
        servo = h.get(CRServo.class, V4Settings.Hardware.FEEDER);
        gate = g;
        servo.setPower(0);
    }
    public void toggle() {
        if (feedRequest)stop();
        else
            request();
    }
    public void request() {
        feedRequest = true;
        reverseHeld = false;
        state = State.WAIT_RPM;
    }
    public void reverseHeld(boolean held) {
        if (held && !reverseHeld) {
            stop();
            reverseHeld = true;
        }
        else
            if (!held && reverseHeld)stop();
    }
    // Cancel pending feed requests and close the gate immediately.
    public void stop() {
        feedRequest = false;
        reverseHeld = false;
        state = State.OFF;
        servo.setPower(0);
        if (gate != null)gate.close();
        gateAt = -1;
        recoveredAt = -1;
    }
    public State state() {
        return state;
    }
    public double forwardSeconds() {
        return forwardNanos/1e9;
    }
    public void resetFeedTime() {
        forwardNanos = 0;
    }
    // Nonblocking feeder state machine: readiness, gate delay, active RPM, and jam reverse.
    // Nonblocking RPM/gate state machine; timing must not stall the OpMode loop.
    public void update(ShooterSubsystem shooter) {
        long now = System.nanoTime(), ms = now/1000000;
        if (state == State.FORWARD)forwardNanos+=Math.max(0, now-previousUpdate);
        previousUpdate = now;
        if (reverseHeld) {
            if (gate != null && gateAt<0) {
                gate.open();
                gateAt = ms;
            }
            if (gate == null || ms-gateAt >= V4Settings.Feeder.GATE_DELAY_MS) {
                servo.setPower(V4Settings.Feeder.REVERSE_POWER);
                state = State.REVERSE;
            }
            else {
                servo.setPower(0);
                state = State.OPEN_GATE;
            }
            return;
        }
        if (!feedRequest) {
            servo.setPower(0);
            state = State.OFF;
            return;
        }
        if (V4Settings.Shooter.INTERLOCK && !shooter.enabled()) {
            stop();
            return;
        }
        if (state == State.FORWARD || state == State.PAUSED) {
            if (V4Settings.Shooter.INTERLOCK && shooter.actual()<V4Settings.Shooter.ACTIVE_MIN_RPM) {
                servo.setPower(0);
                state = State.PAUSED;
                recoveredAt = -1;
                return;
            }
            if (state == State.PAUSED) {
                if (!V4Settings.Shooter.INTERLOCK || shooter.actual() >= V4Settings.Shooter.RECOVER_RPM) {
                    if (recoveredAt<0)recoveredAt = ms;
                    if (ms-recoveredAt >= V4Settings.Shooter.RECOVER_STABLE_MS)state = State.FORWARD;
                }
                else
                    recoveredAt = -1;
            }
            servo.setPower(state == State.FORWARD?V4Settings.Feeder.POWER:0);
            return;
        }
        if (state == State.OPEN_GATE) {
            if (gate == null || ms-gateAt >= V4Settings.Feeder.GATE_DELAY_MS) {
                state = State.FORWARD;
                servo.setPower(V4Settings.Feeder.POWER);
            }
            return;
        }
        if (V4Settings.Shooter.INTERLOCK && !shooter.ready()) {
            state = State.WAIT_RPM;
            servo.setPower(0);
            return;
        }
        if (gate != null) {
            gate.open();
            gateAt = ms;
            servo.setPower(0);
            state = State.OPEN_GATE;
        }
        else {
            state = State.FORWARD;
            servo.setPower(V4Settings.Feeder.POWER);
        }
    }
}
