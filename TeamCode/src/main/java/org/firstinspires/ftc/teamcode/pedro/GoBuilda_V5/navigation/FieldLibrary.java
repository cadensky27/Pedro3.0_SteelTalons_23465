package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.navigation;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.config.V5Settings.Destination;

/** Shared Red-authored field frame. Place the robot at the equivalent start on either alliance. */
public final class FieldLibrary {
    private static final PoseFactory POSES = PoseFactory.degrees();

    private FieldLibrary() {
    }

    /** Add a measured pose here, in inches and degrees. A null entry is unconfigured. */
    public static Pose at(Destination destination) {
        if (destination == null) {
            return null;
        }
        switch (destination) {
            case SHOOT_1: return POSES.of(57, 9, 270);
            case SHOOT_2: return POSES.of(58, 133.5, 90);
            case GARDEN: return POSES.of(10, 12, 270);
            case LOADING: return POSES.of(12, 108.5, 180);
            case PARK_RIGHT: return POSES.of(9, 92, 0);
            case PARK_LEFT: return POSES.of(9,122.5,0);
            case FLOWER_1: return null;
            case FLOWER_2: return null;
            case FLOWER_3: return null;
            case FLOWER_4: return null;
            case ANGLED_SHOT_1: return POSES.of(15.8,16.5,225);
            case ANGLED_SHOT_2: return POSES.of(14,126.8,135);

            // Confirm these shared starts on the field before entering their coordinates.
            // Existing Autos keep the individual starting poses that match their paths.
            case START_PRIMARY: return null;
            case START_ALTERNATE: return null;
            case START_GARDEN: return null;
            case START_FLOWER: return null;
            default: return null;
        }
    }

    /** Convenience for a new Auto using a measured shared starting position. */
    public static Pose require(Destination destination) {
        Pose pose = at(destination);
        if (pose == null) {
            throw new IllegalStateException(destination + ": pose not configured in FieldLibrary");
        }
        return pose;
    }
}
