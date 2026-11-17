# REV BIOBUZZ V4v2

## Install in Android Studio

1. From the ZIP, copy `pedro/Rev_V4v2` into `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/` as `Rev_V4v2`. Its Java package is `org.firstinspires.ftc.teamcode.pedro.Rev_V4v2`, a sibling of `GoBuilda_V4v4`.
   If you installed the previous ZIP, remove the old `teamcode/Rev_V4v2` folder after moving any local edits. Keeping both copies registers the same Driver Station names twice.
2. Build using the FTC SDK and the Pedro V3 and Ivy dependencies used for V4 Update 1. The archive contains TeamCode sources. It can coexist with the goBILDA code.
3. Match the REV Robot Configuration to `config/V4Settings.java`. Use **REV V4v2 Drive Motor Test** with the chassis elevated to check each drive motor and correct the shared directions in `V4Settings.Hardware`.
4. Select **REV V4v2 Single Driver** or **REV V4v2 Two Driver** in Driver Station. The group is **REV V4v2**.

The optional shooter PIDF tuner is at `optional-dashboard/pedro/Rev_V4v2/tools/StandaloneShooterDashboardTuner.java` in the ZIP. Copy it to `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/Rev_V4v2/tools/` in the separate FTC Dashboard SDK project. The tuner imports only the FTC SDK and Dashboard. Copy measured values back into the REV `V5Settings`.

## Choose the drive backend

Set `V4Settings.Drive.BACKEND` and redeploy.

| Value | TeleOp behavior |
|---|---|
| `DriveBackend.MANUAL` (default) | Direct FTC SDK mecanum drive with robot-relative sticks. Both TeleOps keep the same mechanism controls, stick shaping, and L3 fast/slow toggle. Pinpoint can be unplugged. A/B/X/Y navigation, the alliance and heading chords, pose handoff, Pedro-only tools, and the Pedro Auto template are inactive. |
| `DriveBackend.PEDRO` | Pedro drives the wheels for manual control and paths. Pinpoint supplies localization. Field-centric needs a valid heading, and button navigation needs a trusted pose. |

Drive names and directions are shared by the SDK drive, Pedro, and the wheel test. The REV copies inherit starting values from V4 Update 1. Verify motor directions, the flywheel hardware and encoder, servo positions, and the Robot Configuration on this robot. Before running paths, measure the REV Pinpoint offsets and tune Pedro's motion constants in `Constants.java`. The example Auto remains disabled pending field calibration and user-authored paths.

## Reference

- [Architecture](01_ARCHITECTURE.md), [controls](02_CONTROLS.md), [settings](03_SETTINGS.md)
- [Navigation and handoff](04_NAV_AND_HANDOFF.md), [Ivy Auto](05_IVY_AUTO.md), [field poses](09_FIELD_POSES_AND_CALIBRATION.md)
- [Test plan](06_TEST_PLAN.md), [match checklist](07_MATCH_CHECKLIST.md), [wiring](10_WIRING_AND_PORTS.md), [changelog](08_CHANGELOG.md)
