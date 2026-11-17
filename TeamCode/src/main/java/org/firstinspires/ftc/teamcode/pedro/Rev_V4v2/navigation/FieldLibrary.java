package org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.navigation;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.config.V4Settings;

/**
 * Named positions in Pedro's field coordinate system (inches and degrees).
 *
 * <p>POSE ORDER: every pair below is explicitly BLUE FIRST, RED SECOND.
 * Pedro's field visualizer uses +X to the right, +Y upward, and heading:
 * 0° right, 90° up, 180° left, 270° down (counterclockwise positive).
 *
 * <p>Coordinates imported from legacy V4 are STARTING REFERENCES, not measured
 * competition-ready poses. Newly added destinations are guarded by calibration
 * flags to prevent accidental navigation to placeholder (0, 0, 0) values.
 *
 * <p>PARK_RIGHT inherits legacy PARK values for backwards compatibility and
 * the new TeleOp no-handoff starting pose. PARK_LEFT is NEW and unmeasured.
 * "Left" and "Right" must be physically defined on your field diagram before
 * calibrating: these names currently distinguish intended driver destinations.
 */
public final class FieldLibrary {
    private static final PoseFactory FACTORY = PoseFactory.degrees();

    private FieldLibrary() {
    }

    /**
     * Return the named pose for the selected alliance.
     * Null means alliance undefined, destination unsupported, or the new pose
     * group has not been marked calibrated in V4Settings.Navigation.
     */
    public static Pose at(V4Settings.Alliance alliance, V4Settings.Destination destination) {
        if (alliance == null || alliance == V4Settings.Alliance.UNDEFINED || destination == null) {
            return null;
        }

        boolean blue = alliance == V4Settings.Alliance.BLUE;

        switch (destination) {
            case SHOOT_1:
                // BLUE: 82, 133, 90°  | RED: 59, 9, 270°
                return blue ? FACTORY.of(82, 133, 90) : FACTORY.of(59, 9, 270);

            case SHOOT_2:
                // BLUE: 81, 9, 270°   | RED: 59, 132, 90°
                return blue ? FACTORY.of(81, 9, 270) : FACTORY.of(59, 132, 90);

            case GARDEN:
                // BLUE: 132, 129, 90° | RED: 9, 13.5, 270°
                return blue ? FACTORY.of(132, 129, 90) : FACTORY.of(9, 13.5, 270);

            case LOADING:
                // BLUE: 130, 36, 0°   | RED: 12.5, 106, 180°
                return blue ? FACTORY.of(130, 36, 0) : FACTORY.of(12.5, 106, 180);

            case PARK:
            case PARK_RIGHT:
                // BLUE: 128, 48, 180° | RED: 12, 92, 0°
                // PARK is retained as a legacy alias of PARK_RIGHT.
                return blue ? FACTORY.of(128, 48, 180) : FACTORY.of(12, 92, 0);

            case PARK_LEFT:
                if (!V4Settings.Navigation.PARK_LEFT_CALIBRATED) {
                    return null;
                }
                // UNCALIBRATED: replace BOTH BLUE and RED placeholders before enabling.
                // BLUE: X, Y, heading | RED: X, Y, heading
                return blue ? FACTORY.of(0, 0, 0) : FACTORY.of(0, 0, 0);

            case FLOWER_1:
                if (!V4Settings.Navigation.FLOWER_POSES_CALIBRATED) {
                    return null;
                }
                // UNCALIBRATED: flower rail 1 intake end pose; BLUE then RED.
                return blue ? FACTORY.of(0, 0, 0) : FACTORY.of(0, 0, 0);

            case FLOWER_2:
                if (!V4Settings.Navigation.FLOWER_POSES_CALIBRATED) {
                    return null;
                }
                // UNCALIBRATED: flower rail 2 intake end pose; BLUE then RED.
                return blue ? FACTORY.of(0, 0, 0) : FACTORY.of(0, 0, 0);

            case FLOWER_3:
                if (!V4Settings.Navigation.FLOWER_POSES_CALIBRATED) {
                    return null;
                }
                // UNCALIBRATED: flower rail 3 intake end pose; BLUE then RED.
                return blue ? FACTORY.of(0, 0, 0) : FACTORY.of(0, 0, 0);

            case FLOWER_4:
                if (!V4Settings.Navigation.FLOWER_POSES_CALIBRATED) {
                    return null;
                }
                // UNCALIBRATED: flower rail 4 intake end pose; BLUE then RED.
                return blue ? FACTORY.of(0, 0, 0) : FACTORY.of(0, 0, 0);

            case ANGLED_SHOT_LEFT:
                if (!V4Settings.Navigation.ANGLED_SHOTS_CALIBRATED) {
                    return null;
                }
                // UNCALIBRATED: angled shot at left side of hive; BLUE then RED.
                return blue ? FACTORY.of(0, 0, 0) : FACTORY.of(0, 0, 0);

            case ANGLED_SHOT_RIGHT:
                if (!V4Settings.Navigation.ANGLED_SHOTS_CALIBRATED) {
                    return null;
                }
                // UNCALIBRATED: angled shot at right side of hive; BLUE then RED.
                return blue ? FACTORY.of(0, 0, 0) : FACTORY.of(0, 0, 0);

            case UNDER_HIVE:
                // Shared legacy field-center waypoint (not a destination to validate by alliance).
                return FACTORY.of(70.75, 70.75, 0);

            default:
                return null;
        }
    }

    /** Legacy corridor seed; must be physically validated before advanced nav. */
    public static Pose eastCorridor() {
        return FACTORY.of(117.5, 70.75, 180);
    }

    /** Legacy corridor seed; must be physically validated before advanced nav. */
    public static Pose westCorridor() {
        return FACTORY.of(24, 70.75, 0);
    }

    /** Legacy under-hive waypoint; must be physically validated before advanced nav. */
    public static Pose underHive() {
        return FACTORY.of(70.75, 70.75, 0);
    }
}
