package org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.config.V4Settings;

/** Robot-centric mecanum drive using only the FTC SDK. */
public final class ManualMecanumDrive {
    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor backLeft;
    private final DcMotor backRight;

    public ManualMecanumDrive(HardwareMap hardwareMap) {
        frontLeft = hardwareMap.get(DcMotor.class, V4Settings.Hardware.FRONT_LEFT);
        frontRight = hardwareMap.get(DcMotor.class, V4Settings.Hardware.FRONT_RIGHT);
        backLeft = hardwareMap.get(DcMotor.class, V4Settings.Hardware.BACK_LEFT);
        backRight = hardwareMap.get(DcMotor.class, V4Settings.Hardware.BACK_RIGHT);

        stop();
        configureMotor(frontLeft, V4Settings.Hardware.FRONT_LEFT_DIRECTION);
        configureMotor(frontRight, V4Settings.Hardware.FRONT_RIGHT_DIRECTION);
        configureMotor(backLeft, V4Settings.Hardware.BACK_LEFT_DIRECTION);
        configureMotor(backRight, V4Settings.Hardware.BACK_RIGHT_DIRECTION);
    }

    private static void configureMotor(DcMotor motor, Direction direction) {
        motor.setDirection(direction);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /** Apply shaped chassis inputs. Positive commands move forward, right, and clockwise. */
    public void driveRobotCentric(double forward, double strafe, double turn, double powerLimit) {
        double leftFrontPower = forward + strafe + turn;
        double rightFrontPower = forward - strafe - turn;
        double leftBackPower = forward - strafe + turn;
        double rightBackPower = forward + strafe - turn;

        // Scale all wheels together so saturation preserves the requested direction.
        double maxPower = Math.max(
                Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower)),
                Math.max(Math.abs(leftBackPower), Math.abs(rightBackPower)));
        double limit = Math.max(0.0, Math.min(1.0, powerLimit));
        double scale = maxPower > limit ? limit / maxPower : 1.0;
        frontLeft.setPower(leftFrontPower * scale);
        frontRight.setPower(rightFrontPower * scale);
        backLeft.setPower(leftBackPower * scale);
        backRight.setPower(rightBackPower * scale);
    }

    /** Bypass input shaping and slew limiting when the OpMode stops. */
    public void stop() {
        driveRobotCentric(0, 0, 0, 0);
    }
}
