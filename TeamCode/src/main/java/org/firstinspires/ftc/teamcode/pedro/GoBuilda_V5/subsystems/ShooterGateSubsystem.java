package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.subsystems;
import com.qualcomm.robotcore.hardware.*;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.config.V5Settings;
/** Exactly ONE positional shot restrictor servo; NOT the future shot-blocker release. */
public final class ShooterGateSubsystem {
    private final Servo servo;
    private boolean opened;
    public ShooterGateSubsystem(HardwareMap h) {
        servo = h.get(Servo.class, V5Settings.Hardware.SHOOT_GATE);
    }
    public void open() {
        opened = true;
        servo.setPosition(V5Settings.Accessories.SHOOT_GATE_OPEN);
    }
    public void close() {
        opened = false;
        servo.setPosition(V5Settings.Accessories.SHOOT_GATE_CLOSED);
    }
    public boolean isOpen() {
        return opened;
    }
}
