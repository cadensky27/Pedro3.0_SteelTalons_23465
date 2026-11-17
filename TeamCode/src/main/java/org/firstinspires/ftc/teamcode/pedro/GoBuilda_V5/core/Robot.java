package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.core;
import com.qualcomm.robotcore.hardware.*;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.config.V4Settings;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.subsystems.*;
/** Single composition root for MECHANISMS; Pedro follower belongs to OpMode. */
public final class Robot {
    public final EventLog events = new EventLog();
    public final IntakeSubsystem intake;
    public final ShooterSubsystem shooter;
    public final FeederSubsystem feeder;
    public final FlowerArmSubsystem arm;
    public final SortingGateSubsystem sorter;
    public final ShooterGateSubsystem gate;
    private final VoltageSensor voltage;
    private String critical = "";
    public Robot(HardwareMap h) {
        voltage = h.voltageSensor.iterator().hasNext()?h.voltageSensor.iterator().next():null;
        IntakeSubsystem i = null;
        ShooterSubsystem s = null;
        FeederSubsystem f = null;
        try {
            i = new IntakeSubsystem(h, events);
        }
        catch (RuntimeException e) {
            critical += "INTAKE " + V4Settings.Hardware.INTAKE + ": " + e.getMessage() + "; ";
        }
        try {
            s = new ShooterSubsystem(h);
        }
        catch (RuntimeException e) {
            critical += "SHOOTER " + V4Settings.Hardware.SHOOTER + ": " + e.getMessage() + "; ";
        }
        FlowerArmSubsystem a = V4Settings.Hardware.FLOWER_ARM?mapArm(h):null;
        SortingGateSubsystem so = V4Settings.Hardware.SORT_GATE_ENABLED?mapSort(h):null;
        ShooterGateSubsystem g = V4Settings.Hardware.SHOOT_GATE_ENABLED?mapGate(h):null;
        try {
            if (!V4Settings.Hardware.SHOOT_GATE_ENABLED || g != null) {
                f = new FeederSubsystem(h, g);
            } else {
                h.get(CRServo.class, V4Settings.Hardware.FEEDER).setPower(0);
                critical += "FEEDER blocked by shot-restrictor error; ";
            }
        }
        catch (RuntimeException e) {
            critical += "FEEDER " + V4Settings.Hardware.FEEDER + ": " + e.getMessage() + "; ";
        }
        intake = i;
        shooter = s;
        feeder = f;
        arm = a;
        sorter = so;
        gate = g;
    }
    private FlowerArmSubsystem mapArm(HardwareMap h) {
        try {
            return new FlowerArmSubsystem(h);
        }
        catch (RuntimeException e) {
            events.record("Arm OFF: "+e.getMessage());
            return null;
        }
    }
    private SortingGateSubsystem mapSort(HardwareMap h) {
        try {
            return new SortingGateSubsystem(h);
        }
        catch (RuntimeException e) {
            events.record("Sorter OFF: "+e.getMessage());
            return null;
        }
    }
    private ShooterGateSubsystem mapGate(HardwareMap h) {
        try {
            return new ShooterGateSubsystem(h);
        }
        catch (RuntimeException e) {
            critical += "SHOT RESTRICTOR " + V4Settings.Hardware.SHOOT_GATE + ": " + e.getMessage() + "; ";
            return null;
        }
    }
    public boolean healthy() {
        return critical.isEmpty();
    }
    public String critical() {
        return critical;
    }
    public double voltage() {
        return voltage == null?12:voltage.getVoltage();
    }
    /** Call on Play, not INIT, to set optional positional servos. */
    public void startTeleOp() {
        stop();
        if (shooter != null)shooter.preset(V4Settings.Preset.NORMAL);
        if (arm != null)arm.moveTo(V4Settings.Accessories.ARM_START);
        if (sorter != null)sorter.setUp(V4Settings.Accessories.SORT_START_UP);
    }
    public void startAuto(V4Settings.ArmPosition armPosition) {
        startTeleOp();
        if (arm != null)arm.moveTo(armPosition);
        if (sorter != null)sorter.setUp(true);
    }
    public void update() {
        if (shooter != null)shooter.update(voltage());
        if (feeder != null)feeder.update(shooter);
    }
    public void stopShooter() {
        if (shooter != null)shooter.enable(false);
        if (feeder != null)feeder.stop();
    }
    // Put every installed mechanism in its safe stopped state.
    public void stop() {
        if (intake != null)intake.stop();
        if (feeder != null)feeder.stop();
        if (shooter != null)shooter.enable(false);
    }
}
