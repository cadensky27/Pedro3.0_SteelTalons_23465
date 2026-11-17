package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.subsystems;
import com.qualcomm.robotcore.hardware.*;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.config.V4Settings;
/** Exactly TWO positional servos; each has independent calibration values. */
public final class FlowerArmSubsystem {
    private final Servo left, right;
    private V4Settings.ArmPosition position = V4Settings.Accessories.ARM_START;
    public FlowerArmSubsystem(HardwareMap h) {
        left = h.get(Servo.class, V4Settings.Hardware.ARM_LEFT);
        right = h.get(Servo.class, V4Settings.Hardware.ARM_RIGHT);
    }
    public void moveTo(V4Settings.ArmPosition p) {
        position = p;
        switch (p) {
            case UP:left.setPosition(V4Settings.Accessories.ARM_LEFT_UP);
            right.setPosition(V4Settings.Accessories.ARM_RIGHT_UP);
            break;
            case PARTIAL:left.setPosition(V4Settings.Accessories.ARM_LEFT_PARTIAL);
            right.setPosition(V4Settings.Accessories.ARM_RIGHT_PARTIAL);
            break;
            case DOWN:left.setPosition(V4Settings.Accessories.ARM_LEFT_DOWN);
            right.setPosition(V4Settings.Accessories.ARM_RIGHT_DOWN);
            break;
        }
    }
    public void cycle() {
        moveTo(position == V4Settings.ArmPosition.UP?V4Settings.ArmPosition.PARTIAL:position == V4Settings.ArmPosition.PARTIAL?V4Settings.ArmPosition.DOWN:V4Settings.ArmPosition.UP);
    }
    public V4Settings.ArmPosition position() {
        return position;
    }
}
