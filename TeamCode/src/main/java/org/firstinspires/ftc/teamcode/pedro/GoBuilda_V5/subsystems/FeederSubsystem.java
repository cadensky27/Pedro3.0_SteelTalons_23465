package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.config.V4Settings;

/** Owns optional gate timing and the roller. Call update every OpMode loop. */
public final class FeederSubsystem {
    public enum State {
        OFF("Off"), WAIT_RPM("Waiting for RPM"), OPEN_GATE("Opening gate"),
        FORWARD("Feeding"), PAUSED("Recovering RPM"), REVERSE("Reversing feeder"),
        NO_SHOOTER("Shooter unavailable");

        public final String display;

        State(String display) {
            this.display = display;
        }
    }

    private final CRServo servo;
    private final ShooterGateSubsystem gate;
    private State state = State.OFF;
    private boolean forwardRequested;
    private boolean reverseRequested;
    private boolean requireInitialSpeed = true;
    private boolean pauseForRecovery = V4Settings.Shooter.RECOVERY_ENABLED;
    private double reversePower;
    private long gateOpenedAt = -1;
    private long recoveredAt = -1;
    private long lastOutputChange = System.nanoTime();
    private long forwardNanos;

    public FeederSubsystem(HardwareMap hardwareMap, ShooterGateSubsystem gate) {
        servo = hardwareMap.get(CRServo.class, V4Settings.Hardware.FEEDER);
        this.gate = gate;
        servo.setPower(0);
    }

    /** TeleOp and the pit tuner share this entry point. The initial check is mandatory. */
    public void requestForward(boolean requested, boolean recover) {
        if (!requested) {
            if (forwardRequested) stop();
            return;
        }
        request(true, recover);
    }

    /** Auto can retain its explicit timed or measured-RPM feed policy. */
    public void request(boolean waitForRpm, boolean recover) {
        if (reverseRequested) stop();
        forwardRequested = true;
        requireInitialSpeed = waitForRpm;
        pauseForRecovery = recover;
    }

    public void request() {
        request(true, V4Settings.Shooter.RECOVERY_ENABLED);
    }

    public void reverseHeld(boolean held) {
        reverseHeld(held, V4Settings.Feeder.REVERSE_POWER);
    }

    public void reverseHeld(boolean held, double power) {
        if (held) {
            if (!reverseRequested) stop();
            reverseRequested = true;
            reversePower = Math.max(-1, Math.min(0, power));
        } else if (reverseRequested) {
            stop();
        }
    }

    public void stop() {
        forwardRequested = false;
        reverseRequested = false;
        setOutput(State.OFF, 0, System.nanoTime());
        closeGate();
        recoveredAt = -1;
    }

    public State state() {
        return state;
    }

    public double forwardSeconds() {
        long currentRun = state == State.FORWARD ? System.nanoTime() - lastOutputChange : 0;
        return (forwardNanos + currentRun) / 1e9;
    }

    public void resetFeedTime() {
        forwardNanos = 0;
        lastOutputChange = System.nanoTime();
    }

    public void update(ShooterSubsystem shooter) {
        long now = System.nanoTime();
        if (reverseRequested) {
            if (gateReady(now)) {
                setOutput(State.REVERSE, reversePower, now);
            } else {
                setOutput(State.OPEN_GATE, 0, now);
            }
            return;
        }
        if (!forwardRequested) return;

        if (shooter == null) {
            waitWithGateClosed(State.NO_SHOOTER, now);
        } else if (!shooter.enabled() || (requireInitialSpeed && !shooter.reachedInitialSpeed())) {
            waitWithGateClosed(State.WAIT_RPM, now);
        } else if (shouldPause(shooter, now)) {
            setOutput(State.PAUSED, 0, now);
        } else if (!gateReady(now)) {
            setOutput(State.OPEN_GATE, 0, now);
        } else {
            setOutput(State.FORWARD, V4Settings.Feeder.POWER, now);
        }
    }

    private boolean shouldPause(ShooterSubsystem shooter, long now) {
        if (!pauseForRecovery) {
            recoveredAt = -1;
            return false;
        }

        if (state == State.PAUSED) {
            double resumeRpm = Math.max(0, shooter.target() - V4Settings.Shooter.RECOVER_DROP_RPM);
            if (shooter.actual() < resumeRpm) {
                recoveredAt = -1;
                return true;
            }
            if (recoveredAt < 0) recoveredAt = now;
            if ((now - recoveredAt) / 1_000_000L < V4Settings.Shooter.RECOVER_STABLE_MS) {
                return true;
            }
            recoveredAt = -1;
            return false;
        }
        if (state != State.FORWARD && state != State.OPEN_GATE) return false;
        double pauseRpm = Math.max(0, shooter.target() - V4Settings.Shooter.PAUSE_DROP_RPM);
        return shooter.actual() < pauseRpm;
    }

    private void waitWithGateClosed(State reason, long now) {
        setOutput(reason, 0, now);
        closeGate();
        recoveredAt = -1;
    }

    private boolean gateReady(long now) {
        if (gate == null) return true;
        if (gateOpenedAt == -1) {
            gate.open();
            gateOpenedAt = now;
        }
        return (now - gateOpenedAt) / 1_000_000L >= V4Settings.Feeder.GATE_DELAY_MS;
    }

    private void closeGate() {
        if (gate != null) gate.close();
        gateOpenedAt = -1;
    }

    private void setOutput(State nextState, double power, long now) {
        if (state == State.FORWARD) forwardNanos += now - lastOutputChange;
        lastOutputChange = now;
        state = nextState;
        servo.setPower(power);
    }
}
