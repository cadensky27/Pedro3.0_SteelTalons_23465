package org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.config;

import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;

/**
 * Centralized, human-editable configuration for the REV BIOBUZZ V4 robot.
 *
 * <p>Change these values rather than scattering numbers across TeleOp and subsystems.
 * Hardware-map strings and tested tuning values originate from the legacy V4. Servo
 * endpoints are UNCALIBRATED until measured on the physical robot.
 *
 * <p>These fields are intentionally not dashboard-adjustable during competition.
 * After testing a value in a standalone tuner, copy it here and redeploy.
 */

public final class V4Settings {
    private V4Settings() {
    }
    public enum Alliance {
        UNDEFINED, BLUE, RED
    }
    public enum DriveMode {
        FIELD_CENTRIC, AUTO_FACING, ROBOT_CENTRIC
    }
    public enum DriveBackend {
        MANUAL, PEDRO
    }
    public enum Preset {
        NORMAL, CLOSE, ANGLED
    }
    public enum ArmPosition {
        UP, PARTIAL, DOWN
    }
    public enum Destination {
        SHOOT_1, SHOOT_2, GARDEN, LOADING,
        PARK, PARK_LEFT, PARK_RIGHT, UNDER_HIVE,
        FLOWER_1, FLOWER_2, FLOWER_3, FLOWER_4,
        ANGLED_SHOT_LEFT, ANGLED_SHOT_RIGHT
    }
    /**
     * Installed hardware and optional accessory switches.
     * All strings must exactly match the REV Robot Configuration. Disabled optional hardware is not mapped.
     */
    public static final class Hardware {
        /** Drive motor hardware-map names shared by the SDK drive, Pedro, and the wheel test. */
        public static String FRONT_LEFT = "frontLeft";
        public static String FRONT_RIGHT = "frontRight";
        public static String BACK_LEFT = "backLeft";
        public static String BACK_RIGHT = "backRight";

        /** Starting directions inherited from V4. Verify each wheel on the REV chassis. */
        public static Direction FRONT_LEFT_DIRECTION = Direction.REVERSE;
        public static Direction FRONT_RIGHT_DIRECTION = Direction.FORWARD;
        public static Direction BACK_LEFT_DIRECTION = Direction.REVERSE;
        public static Direction BACK_RIGHT_DIRECTION = Direction.FORWARD;

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
        public static boolean SIDE_ROLLERS = false;
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
        public static double LEFT_ROLLER = -1;
        /** Right roller command used while intaking; sign is mounting-dependent. */
        public static double RIGHT_ROLLER = 1;
    }
    /**
     * Flywheel speed, PIDF, readiness, and feed safety.
     * RPM means shaft revolutions per minute. These are calibration starting points, not guaranteed competition values.
     */
    public static final class Shooter {
        /** Default long-range/normal shooting target, RPM. */
        public static double NORMAL_RPM = 2750;
        /** Close-distance shot target, RPM. */
        public static double CLOSE_RPM = 2500;
        /** Angled-shot target, RPM. */
        public static double ANGLED_RPM = 3000;
        /** FTC SDK velocity PIDF proportional gain; tune on actual motor. */
        public static double P = 48;
        /** FTC SDK velocity PIDF integral gain. */
        public static double I = 0;
        /** FTC SDK velocity PIDF derivative gain. */
        public static double D = 0;
        /** FTC SDK velocity PIDF feedforward gain. */
        public static double F = 14;
        /** Motor encoder ticks per motor shaft revolution (check 28 empirically). */
        public static double TICKS_PER_REV = 28;
        /** Physical shooter shaft revolutions per motor revolution (1.0 = direct drive). */
        public static double GEAR_RATIO = 1;
        /** Whether the flywheel motor direction is reversed. */
        public static boolean REVERSED = false;
        /** Require shooter readiness before forward feeder activation (default ON). */
        public static boolean INTERLOCK = false;
        /** Allow Share + D-pad in-match RPM edits (default OFF). */
        public static boolean RPM_ADJUST_ENABLED = true;
        /** Enable shooter feedforward voltage scaling; confirm no double compensation. */
        public static boolean VOLTAGE_F_COMP = true;
        /** Permitted deviation ±RPM around the requested target for initial readiness. */
        public static double READY_TOL_RPM = 75;
        /** Minimum RPM during feeding before the feeder pauses. */
        public static double ACTIVE_MIN_RPM = 2600;
        /** Minimum RPM required to restart a paused feeder after stability delay. */
        public static double RECOVER_RPM = 2800;
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
        public static double REVERSE_POWER = -.35;
        /** Wait after opening shooter gate before feeding or reversing. */
        public static long GATE_DELAY_MS = 200;
        /** Maximum wait for shooter readiness in timed autonomous actions. */
        public static long READY_TIMEOUT_MS = 3500;
        /** Overall wall-clock cutoff protecting timed auto feed commands. */
        public static long FEED_WALL_TIMEOUT_MS = 6000;
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
    /**
     * Manual drive controls and experimental auto-facing.
     * Both drive backends share stick shaping and speed settings.
     */
    public static final class Drive {
        /**
         * Select before INIT and redeploy. MANUAL drives robot-centric using the FTC SDK
         * and needs no Pinpoint. PEDRO enables localization, field-relative drive,
         * navigation, Pedro diagnostics, and Pedro autonomous.
         */
        public static final DriveBackend BACKEND = DriveBackend.MANUAL;

        /** Stick drive mode when Pedro is selected and heading is available. */
        public static DriveMode DEFAULT_MODE = DriveMode.ROBOT_CENTRIC;
        /**
         * Used only when an Autonomous pose handoff is unavailable.
         * Select BLUE or RED here; UNDEFINED disables field-relative driving.
         */
        public static Alliance DEFAULT_ALLIANCE = Alliance.RED;

        /**
         * Driver-perspective offsets, in degrees, applied to Pedro's measured heading.
         * Pedro field headings: 0 = +X (right), 90 = +Y (up), 180 = -X (left),
         * 270 = -Y (down). Counterclockwise is positive.
         * These values adjust joystick orientation ONLY; never change the stored pose.
         * Test using FieldCentricValidation before competition.
         */
        public static double BLUE_OFFSET_DEG = 180;
        public static double RED_OFFSET_DEG = 0;
        /** Fast manual drive power multiplier (0.0–1.0). */
        public static double FAST = 1;
        /** Precision/slow manual drive power multiplier. */
        public static double SLOW = .4;
        /** Joystick deadzone for manual driving. */
        public static double DEADZONE = .07;
        /** Stick shaping blend: 0 = linear; 1 = cubic. */
        public static double CUBIC_BLEND = .35;
        /** Stick magnitude needed to interrupt an active navigator. */
        public static double NAV_CANCEL_DEADZONE = .23;
        /** Enable manual-drive slew-rate limiting (disabled by default). */
        public static boolean SLEW = false;
        /** Reserved drive compensation option; do not assume Pedro accepts it. */
        public static boolean VOLTAGE_COMPENSATION = false;
        // drive comp reserved until Pedro support confirmed
        /** Maximum manual command change per second when slew is on. */
        public static double SLEW_UNITS_PER_SEC = 3;
        /** Experimental heading error proportional gain. */
        public static double AUTO_FACE_KP = 1.8;
        /** Experimental maximum auto-facing turn command. */
        public static double AUTO_FACE_MAX_TURN = 1;
        /** Auto-facing heading deadband/tolerance, degrees. */
        public static double AUTO_FACE_TOL_DEG = 5;
        /** How much translation is reduced for large auto-facing heading errors. */
        public static double AUTO_FACE_ROTATION_PRIORITY = .65;
        /** Hold duration for deliberate two-button gamepad combinations. */
        public static long CHORD_MS = 750;
    }
    /**
     * Basic direct and advanced waypoint navigation.
     * Field poses and safe corridors must be validated physically before competitive use.
     */
    public static final class Navigation {
        /**
         * Fallback pose used only when no valid Auto handoff exists.
         * PARK_RIGHT initially inherits the old PARK coordinates; these are
         * unverified physical measurements. Place the robot accordingly.
         * The alliance chooses between BLUE and RED coordinates.
         */
        public static Destination FALLBACK_START_POSE = Destination.PARK_RIGHT;

        /**
         * If false (competition-safe default), park fallback enables field-centric
         * heading but DOES NOT authorize automated navigation. Set true only after
         * checking the robot really starts at the selected fallback pose.
         * This does not certify the pose automatically.
         */
        public static boolean TRUST_FALLBACK_POSITION_FOR_NAV = false;

        /**
         * New flower poses are intentionally unavailable until all coordinates
         * have been replaced and checked on the actual field.
         */
        public static boolean FLOWER_POSES_CALIBRATED = false;

        /** Both angled shot destination coordinates and headings have been measured. */
        public static boolean ANGLED_SHOTS_CALIBRATED = false;

        /** PARK_LEFT is a new, currently unmeasured parking destination. */
        public static boolean PARK_LEFT_CALIBRATED = false;

        /** Enable A/B/X/Y TeleOp navigation (basic by default). */
        public static boolean ENABLE = true;
        /** Use vetted advanced routes rather than basic straight lines. */
        public static boolean ADVANCED = false;
        /** Explicit safety gate: only permit physically verified advanced routes. */
        public static boolean ADVANCED_ROUTES_FIELD_VERIFIED = false;
        /** Target for gamepad A when navigation is enabled. */
        public static Destination A = Destination.SHOOT_1;
        /** Target for gamepad B when navigation is enabled. */
        public static Destination B = Destination.SHOOT_2;
        /** Target for gamepad X when navigation is enabled. */
        public static Destination X = Destination.GARDEN;
        /** Target for gamepad Y when navigation is enabled. */
        public static Destination Y = Destination.LOADING;
        /** Basic navigation maximum allowed time in milliseconds. */
        public static long BASIC_TIMEOUT_MS = 5000;
        /** Advanced navigation maximum allowed time in milliseconds. */
        public static long ADVANCED_TIMEOUT_MS = 9000;
        /** Time both position and heading must remain within tolerance. */
        public static long ARRIVAL_STABLE_MS = 150;
        /** Basic position arrival tolerance in Pedro field distance units (inches). */
        public static double BASIC_POS_TOL = 2;
        /** Advanced position arrival tolerance in inches. */
        public static double ADVANCED_POS_TOL = 2;
        /** Basic navigation heading tolerance, degrees. */
        public static double BASIC_HEAD_TOL_DEG = 5;
        /** Advanced navigation heading tolerance, degrees. */
        public static double ADVANCED_HEAD_TOL_DEG = 5;
        // Additional constraints are tied to Pedro Foresight. Do not invent a second power controller.
        /** Intended basic navigation speed setting; actual API usage must be verified. */
        public static double BASIC_SPEED = .9;
        /** Intended advanced speed; verify integration with native Pedro constraints. */
        public static double ADVANCED_SPEED = .7;
    }
    /**
     * Controller rumble events.
     * Event booleans enable/disable vibration; time values are milliseconds.
     */
    public static final class Feedback {
        /** Enable one-time rumble when shooter reaches stable ready RPM. */
        public static boolean READY = true;
        /** Enable success rumble when navigation meets position and heading criteria. */
        public static boolean NAV_SUCCESS = true;
        /** Enable warning rumble when navigation fails or times out. */
        public static boolean NAV_FAILURE = true;
        /** Enable rumble when mode or alliance changes. */
        public static boolean MODE = true;
        /** Enable warning rumble, disabled by default. */
        public static boolean WARNING = false;
        /** Rumble motor intensity, from 0 (off) to 1 (full). */
        public static double POWER = .7;
        /** Shooter-ready rumble duration in milliseconds. */
        public static int READY_MS = 180;
        /** Navigation-success rumble duration in milliseconds. */
        public static int NAV_MS = 200;
        /** Navigation-error rumble duration in milliseconds. */
        public static int WARN_MS = 400;
        /** Mode-switch rumble duration in milliseconds. */
        public static int MODE_MS = 130;
    }
    /**
     * Driver Station telemetry and bounded event history.
     * The control loop is never rate-limited by the display refresh settings.
     */
    public static final class Telemetry {
        /** Master detailed-telemetry toggle; false leaves compact competition display. */
        public static boolean DETAILS = false;
        /** Include extended drivetrain/pose telemetry when DETAILS is true. */
        public static boolean DRIVE = false;
        /** Include flywheel/voltage diagnostic details when DETAILS is true. */
        public static boolean SHOOTER = false;
        /** Include intake direction telemetry when DETAILS is true. */
        public static boolean INTAKE = false;
        /** Include arm and sorting-gate telemetry when DETAILS is true. */
        public static boolean ACCESSORIES = false;
        /** Include navigation route/status telemetry when DETAILS is true. */
        public static boolean NAV = false;
        /** Include bounded event-history lines when DETAILS is true. */
        public static boolean EVENTS = false;
        /** Minimum competition telemetry interval, milliseconds. */
        public static long REFRESH_MS = 150;
        /** Minimum detailed telemetry interval, milliseconds. */
        public static long DETAIL_REFRESH_MS = 100;
        /** Maximum number of recent event-log records in memory. */
        public static int HISTORY = 50;
    }
}
