# Field poses, flower positions, and calibration

**REV mode** — A/B/X/Y navigation, field-centric drive, pose handoff, and Pedro Auto require `Drive.BACKEND = PEDRO`. In `MANUAL`, both TeleOps still have robot-centric stick drive and the same mechanism controls.

## Coordinate conventions (Pedro Visualizer)

Pedro uses **inches** for field X/Y and **counterclockwise-positive headings**:

| Heading | Robot faces |
|---|---|
| 0° | +X / right on visualizer |
| 90° | +Y / up on visualizer |
| 180° | −X / left on visualizer |
| 270° | −Y / down on visualizer |

`FieldLibrary` defines poses using `PoseFactory.degrees()`; `follower.pose().heading()` is in **radians**.

## Which row is which?

**BLUE ALWAYS FIRST; RED ALWAYS SECOND.** In each `FieldLibrary.at` ternary:

```java
return blue ? FACTORY.of(BLUE_X, BLUE_Y, BLUE_HEADING)
            : FACTORY.of(RED_X, RED_Y, RED_HEADING);
```

Do not swap the two when measuring. Update both alliance entries separately, even when some coordinates happen to be shared.

## Existing legacy seeds

| Destination | Blue X / Y / heading | Red X / Y / heading | Calibration |
|---|---|---|---|
| SHOOT_1 | 82 / 133 / 90° | 59 / 9 / 270° | Legacy, recheck |
| SHOOT_2 | 81 / 9 / 270° | 59 / 132 / 90° | Legacy, recheck |
| GARDEN | 132 / 129 / 90° | 9 / 13.5 / 270° | Legacy, recheck |
| LOADING | 130 / 36 / 0° | 12.5 / 106 / 180° | Legacy, recheck |
| PARK_RIGHT | 128 / 48 / 180° | 12 / 92 / 0° | **Legacy PARK seed**, not yet measured |
| PARK | Same as PARK_RIGHT | Same as PARK_RIGHT | Compatibility alias |
| UNDER_HIVE | 70.75 / 70.75 / 0° | Same | Legacy waypoint, recheck |

## New poses — NOT VALIDATED

| Destination | Description | Status |
|---|---|---|
| PARK_LEFT | Alternate parking end pose | 0/0/0 placeholders on both alliances |
| FLOWER_1 | Final intake alignment at flower rail 1 | 0/0/0 placeholders |
| FLOWER_2 | Final intake alignment at flower rail 2 | 0/0/0 placeholders |
| FLOWER_3 | Final intake alignment at flower rail 3 | 0/0/0 placeholders |
| FLOWER_4 | Final intake alignment at flower rail 4 | 0/0/0 placeholders |
| ANGLED_SHOT_LEFT | Shot pose left of hive | 0/0/0 placeholders |
| ANGLED_SHOT_RIGHT | Shot pose right of hive | 0/0/0 placeholders |

**Flower numbering and "left/right" require a physical field reference** before calibration; do not infer the numbering from an unprovided field image. Choose a consistent orientation, label it on your field map, then replace the placeholders in `navigation/FieldLibrary.java`.

Calibration guards in `V4Settings.Navigation`:

- `PARK_LEFT_CALIBRATED = false`
- `FLOWER_POSES_CALIBRATED = false`
- `ANGLED_SHOTS_CALIBRATED = false`

These return `null` instead of a path endpoint while disabled. Turn each on **only after both alliance rows in that group are calibrated**. The new destinations can then be assigned to A/B/X/Y without modifying `Navigator`.

## Important: parking fallback is not a measurement

With no valid Auto handoff, TeleOp uses `V4Settings.Drive.DEFAULT_ALLIANCE` and `V4Settings.Navigation.FALLBACK_START_POSE`, currently `PARK_RIGHT`.

`follower.setPose(...)` establishes a **known starting assumption**, not independent proof of where the robot is. Physically put the robot at that park-right pose with the specified heading when testing the fallback. By default, `TRUST_FALLBACK_POSITION_FOR_NAV = false` blocks auto-navigation but allows field-centric heading control. Enable it only for verified, repeatable parking placement. Missing pose, undefined alliance, or missing localizer leaves robot-relative fallback.

**Alliance heading offset is not park heading:** Actual park heading initializes Pedro localization. `BLUE_OFFSET_DEG` and `RED_OFFSET_DEG` rotate only the driver's control axes. Switching alliance during a match changes driver controls but does not teleport the pose.

### Quick alignment test

1. Set default alliance to the side you are testing, and verify physical robot heading matches that alliance's `PARK_RIGHT` entry.
2. Start TeleOp without running Auto. Check `POSE SOURCE: FALLBACK: PARK_RIGHT` and the reported heading.
3. Push left-stick forward and check which field direction moves. Adjust the corresponding **driver offset** in `V4Settings.Drive` only if the driver-frame direction needs rotation.
4. Turn the robot 90° and repeat the same forward command. Field travel direction should remain unchanged.
5. Verify both alliances and the emergency robot-centric fallback, then validate the true X/Y origin before enabling navigation.

Official Pedro references: https://pedropathing.com/docs/pathing/reference/coordinates and https://pedropathing.com/docs/pathing/guide/teleop-usage
