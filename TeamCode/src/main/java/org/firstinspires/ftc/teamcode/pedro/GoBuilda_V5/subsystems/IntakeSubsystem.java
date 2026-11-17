package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.subsystems;
import com.qualcomm.robotcore.hardware.*;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.config.V5Settings;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.core.EventLog;
/** Intake DC motor plus exactly TWO optional CR side rollers, always coupled to intake. */
public final class IntakeSubsystem {
    private final DcMotorEx motor;
    private CRServo left, right;
    private int direction;
    public IntakeSubsystem(HardwareMap map, EventLog log) {
        motor = map.get(DcMotorEx.class, V5Settings.Hardware.INTAKE);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        if (V5Settings.Hardware.SIDE_ROLLERS)try {
            left = map.get(CRServo.class, V5Settings.Hardware.ROLLER_LEFT);
            right = map.get(CRServo.class, V5Settings.Hardware.ROLLER_RIGHT);
        }
        catch (RuntimeException ex) {
            left = null;
            right = null;
            log.record("Side-roller pair disabled; main intake available: "+ex.getMessage());
        }
        setDirection(0);
    }
    /** Public: -1 reverse; 0 stop; +1 collect. */
    public void setDirection(int d) {
        direction = Integer.compare(d, 0);
        motor.setPower(direction>0? V5Settings.Intake.FORWARD:direction<0? V5Settings.Intake.REVERSE:0);
        if (left != null && right != null) {
            left.setPower(direction* V5Settings.Intake.LEFT_ROLLER);
            right.setPower(direction* V5Settings.Intake.RIGHT_ROLLER);
        }
    }
    public int direction() {
        return direction;
    }
    public void stop() {
        setDirection(0);
    }
}
