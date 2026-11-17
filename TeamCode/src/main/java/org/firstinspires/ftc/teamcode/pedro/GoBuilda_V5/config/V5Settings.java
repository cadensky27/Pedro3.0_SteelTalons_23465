package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.config;

/**
 * Centralized, human-editable configuration for the BIOBUZZ V5 robot.
 *
 * <p>Change these values rather than scattering numbers across TeleOp and subsystems.
 * Hardware-map strings and tested tuning values originate from the legacy V4. Servo
 * endpoints are UNCALIBRATED until measured on the physical robot.
 *
 * <p>These fields are intentionally not dashboard-adjustable during competition.
 * After testing a value in a standalone tuner, copy it here and redeploy.
 */

public final class V5Settings {
    private V5Settings() {
    }
    public enum Preset {
        NORMAL, CLOSE, ANGLED
    }
    public enum ArmPosition {
        UP, PARTIAL, DOWN
    }
    public enum Destination {
        SHOOT_1, SHOOT_2, GARDEN, LOADING,
        PARK_LEFT, PARK_RIGHT,
        START_PRIMARY, START_ALTERNATE, START_GARDEN, START_FLOWER,
        FLOWER_1, FLOWER_2, FLOWER_3, FLOWER_4,
        ANGLED_SHOT_1, ANGLED_SHOT_2
    }
    /**
     * Installed hardware and optional accessory switches.
     * All strings must exactly match the REV Robot Configuration. Disabled optional hardware is not mapped.
     */
    public static final class Hardware {
        /** Hardware-map name of the main intake DC motor. */
        public static String INTAKE = "mainIntake";
        /** Hardware-map name of the 6000-RPM flywheel DC motor. */
        public static String SHOOTER = "flywheel";
        /** Hardware-map name of the horizontal CR-servo feeder (legacy name retained). */
        public static String FEEDER = "pusher";
        /** Hardware-map name of the goBilda Pinpoint odometry device. */
        public static String PINPOINT = "pinpoint";
        /** Left CR side-roller configuration name. */
        public static String ROLLER_LEFT = "leftSideRoller";
        /** Right CR side-roller configuration name. */
        public static String ROLLER_RIGHT = "rightSideRoller";
        /** Left positional flower-arm servo name. */
        public static String ARM_LEFT = "leftFlowerArm";
        /** Right positional flower-arm servo name. */
        public static String ARM_RIGHT = "rightFlowerArm";
        /** Single positional shot-restrictor servo name. */
        public static String SHOOT_GATE = "shotRestrictor";
        /** Single positional sorting-gate servo name; verify the legacy mapping. */
        public static String SORT_GATE = "sorterLeft";
        // VERIFY sorterLeft: former 2-servo system
        /** Map and operate the left + right side rollers with intake (default enabled). */
        public static boolean SIDE_ROLLERS = true;
        /** Map both left + right positional arm servos (default disabled). */
        public static boolean FLOWER_ARM = false;
        /** Map the single shooter gate servo (default disabled). */
        public static boolean SHOOT_GATE_ENABLED = false;
        /** Map the single sorting gate servo (default disabled). */
        public static boolean SORT_GATE_ENABLED = false;
    }
    /**
     * Intake motor and roller power calibration.
     * Both side rollers follow the main intake direction; no independent roller mode.
     */
    public static final class Intake {
        /** Main intake forward motor power, normally +1.0. */
        public static double FORWARD = 1;
        /** Main intake reverse motor power, normally -1.0. */
        public static double REVERSE = -1;
        /** Left roller command used while intaking; sign is mounting-dependent. */
        public static double LEFT_ROLLER = 1;
        /** Right roller command used while intaking; sign is mounting-dependent. */
        public static double RIGHT_ROLLER = -1;
    }
    /**
     * Flywheel speed, PIDF, readiness, and feed safety.
     * RPM means shaft revolutions per minute. These are calibration starting points,    not guaranteed competition values.
     */
    public static final class Shooter {
        /** Default long-range/normal shooting target, RPM. */
        public static double NORMAL_RPM = 3160;
        /** Close-distance shot target, RPM. */
        public static double CLOSE_RPM = 3000;
        /** Angled-shot target, RPM. */
        public static double ANGLED_RPM = 3300;
        /** FTC SDK velocity PIDF proportional gain; tune on actual motor. */
        public static double P = 44;
        /** FTC SDK velocity PIDF integral gain. */
        public static double I = 0;
        /** FTC SDK velocity PIDF derivative gain. */
        public static double D = 0;
        /** FTC SDK velocity PIDF feedforward gain. */
        public static double F = 12;
        /** Motor encoder ticks per motor shaft revolution (check 28 empirically). */
        public static double TICKS_PER_REV = 28;
        /** Physical shooter shaft revolutions per motor revolution (1.0 = direct drive). */
        public static double GEAR_RATIO = 1;
        /** Whether the flywheel motor direction is reversed. */
        public static boolean REVERSED = false;
        /** Default TeleOp pause-and-recover behavior after initial readiness. */
        public static boolean RECOVERY_ENABLED = true;
        /** Allow the layout-specific in-match RPM trim buttons. */
        public static boolean RPM_ADJUST_ENABLED = true;
        /** Enable shooter feedforward voltage scaling; confirm no double compensation. */
        public static boolean VOLTAGE_F_COMP = false;
        /** Permitted deviation ±RPM around the requested target for initial readiness. */
        public static double READY_TOL_RPM = 150;
        /** Drop below target RPM that pauses an active volley. */
        public static double PAUSE_DROP_RPM = 575;
        /** Maximum RPM shortfall allowed during recovery before resuming. */
        public static double RECOVER_DROP_RPM = 375;
        /** Software upper bound on manual RPM target requests. */
        public static double MAX_RPM = 6000;
        /** Required continuous time inside ready band before first feed. */
        public static long READY_STABLE_MS = 250;
        /** Required continuous recovery time before paused feeding resumes. */
        public static long RECOVER_STABLE_MS = 150;
        /** Reference voltage for optional shooter feedforward scaling. */
        public static double NOMINAL_VOLTS = 12;
        /** Maximum allowed voltage compensation multiplier for feedforward. */
        public static double MAX_F_SCALE = 1.25;
    }
    /**
     * Continuous-rotation wheel feeder and gate timing.
     * The feeder is a horizontal roller, not the legacy Spindex. Timers are nonblocking.
     */
    public static final class Feeder {
        /** Forward CR-servo feeder power. */
        public static double POWER = 1;
        /** Reduced reverse CR-servo power for clearing jams. */
        public static double REVERSE_POWER = -.40;
        /** Wait after opening shooter gate before feeding or reversing. */
        public static long GATE_DELAY_MS = 200;
        /** Maximum wait for shooter readiness in timed autonomous actions. */
        public static long READY_TIMEOUT_MS = 3500;
        /** Overall wall-clock cutoff protecting timed auto feed commands. */
        public static long FEED_WALL_TIMEOUT_MS = 6000;
    }

    /**
     * Coordinated reverse jam clearing for both COMP TeleOps.
     *
     * The operator must hold the assigned button before any mechanism reverses.
     * After activation, release stops all affected mechanisms immediately.
     * There is deliberately no automatic running-time limit.
     *
     * Reverse RPM is a velocity *target*, not a guarantee of available torque.
     * Output power is independently limited while the shooter is clearing a jam.
     */
    public static final class JamClear {
        /** Master enable/disable switch for the entire feature. */
        public static boolean ENABLED = true;

        /** Continuous button-hold time required before activation. */
        public static long ACTIVATION_HOLD_MS = 500;

        /** Shooter reverse velocity target (motor shaft, 1:1 shooter). */
        public static double SHOOTER_REVERSE_RPM = 800;

        /** Maximum magnitude of reverse shooter motor power (0 to 1). */
        public static double SHOOTER_MAX_REVERSE_POWER = 0.35;

        /** Proportional motor-power correction per RPM of reverse velocity error. */
        public static double SHOOTER_REVERSE_KP = 0.0004;

        /** Do not begin reversing until the shooter has almost stopped. */
        public static double START_MAX_ABS_RPM = 100;

        /** Reverse power for the continuous-rotation feeder servo. */
        public static double FEEDER_REVERSE_POWER = -0.40;

        /** Reverse the main intake and linked side rollers. */
        public static boolean REVERSE_INTAKE = true;
    }
    /**
     * Uncalibrated positional-servo endpoints.
     * Values range from 0.0 to 1.0. Calibrate each servo with mechanisms unloaded; disabled by default.
     */
    public static final class Accessories {
        // Positional servo values are PLACEHOLDERS. Keep each accessory OFF until calibrated.
        /** Left arm servo position with arm fully retracted. */
        public static double ARM_LEFT_UP = .5;
        /** Left arm servo position at partial/nectar-blocking height. */
        public static double ARM_LEFT_PARTIAL = .5;
        /** Left arm servo position fully lowered for collecting flowers. */
        public static double ARM_LEFT_DOWN = .5;
        /** Right arm servo position with arm fully retracted. */
        public static double ARM_RIGHT_UP = .5;
        /** Right arm servo position at partial/nectar-blocking height. */
        public static double ARM_RIGHT_PARTIAL = .5;
        /** Right arm servo position fully lowered for collecting flowers. */
        public static double ARM_RIGHT_DOWN = .5;
        /** Shooter restrictor servo open position. */
        public static double SHOOT_GATE_OPEN = .5;
        /** Shooter restrictor servo closed position. */
        public static double SHOOT_GATE_CLOSED = .5;
        /** Sorting gate servo up position; forced for autonomous. */
        public static double SORT_GATE_UP = .5;
        /** Sorting gate servo down position. */
        public static double SORT_GATE_DOWN = .5;
        /** Flower-arm startup position commanded at PLAY. */
        public static ArmPosition ARM_START = ArmPosition.UP;
        /** Whether sorting gate starts raised when TeleOp begins. */
        public static boolean SORT_START_UP = true;
    }
    /** Driver feel, independent of the current field pose. */
    public static final class Drive {
        /** Start in field-centric mode when a pose is available. */
        public static boolean FIELD_CENTRIC_ENABLED = true;
        /** Driver-perspective trim in degrees for the shared Red-authored frame. */
        public static double DRIVER_OFFSET_DEG = 0;
        /** Max stick command multiplier at normal and slow speed, respectively. */
        public static double FAST = 1;
        public static double SLOW = .4;
        /** Ignore small stick motion at rest. */
        public static double DEADZONE = .07;
        /** Stick shaping blend: 0 = linear, 1 = cubic. */
        public static double CUBIC_BLEND = .40;
        /** Stick input above this level cancels a Navigator move. */
        public static double NAV_CANCEL_DEADZONE = .23;
        /** Deliberate fallback mode chord hold, milliseconds. */
        public static long CHORD_MS = 750;
    }

    /** BASIC uses a straight Pedro path and checks both position and heading. */
    public static final class Navigation {
        /** Pose to use when TeleOp starts without a measured Auto handoff. */
        public static Destination FALLBACK_START_POSE = Destination.PARK_RIGHT;
        public static boolean ENABLE = true;
        /** Timeout and required arrival stability, in milliseconds. */
        public static long BASIC_TIMEOUT_MS = 3800;
        public static long ARRIVAL_STABLE_MS = 150;
        /** Arrival tolerances in inches and degrees. */
        public static double BASIC_POS_TOL = 2;
        public static double BASIC_HEAD_TOL_DEG = 5;
    }

    /** Distinct gamepad cues. Intensities range from 0 to 1. */
    public static final class Feedback {
        public static double POWER = .7;
        public static int READY_MS = 180;
        public static int NAV_SUCCESS_MS = 200;
        public static int NAV_FAILURE_MS = 400;
        public static int LINED_UP_PULSE_MS = 90;
        public static int LINED_UP_GAP_MS = 80;
    }

    /** Compact match display, or expanded pit diagnostics with one setting. */
    public static final class Telemetry {
        public static boolean SHOW_TELEOP_DIAGNOSTICS = false;
        public static long REFRESH_MS = 150;
        public static int HISTORY = 50;
    }
}
