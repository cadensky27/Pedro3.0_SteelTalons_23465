package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.subsystems;
import com.qualcomm.robotcore.hardware.*;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.config.V4Settings;
/** Exactly ONE positional sorting servo. */
public final class SortingGateSubsystem {
    private final Servo servo;
    private boolean up = true;
    public SortingGateSubsystem(HardwareMap h) {
        servo = h.get(Servo.class, V4Settings.Hardware.SORT_GATE);
    }
    public void setUp(boolean b) {
        up = b;
        servo.setPosition(up?V4Settings.Accessories.SORT_GATE_UP:V4Settings.Accessories.SORT_GATE_DOWN);
    }
    public void toggle() {
        setUp(!up);
    }
    public boolean isUp() {
        return up;
    }
}
