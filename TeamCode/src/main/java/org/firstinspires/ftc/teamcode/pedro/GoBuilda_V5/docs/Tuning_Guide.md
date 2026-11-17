# GoBuilda V5 — Tuning Guide

Tune **mechanical reliability first**, then localization and drive, then shooter and feeder, then autonomous. Avoid changing several constants at once.

## Before any tuning

- [ ] Tighten mounts, check drivetrain geometry, clean wheels and rollers.
- [ ] Confirm motor directions, encoder responses and Pinpoint mounting/orientation.
- [ ] Charge a suitable battery and record its voltage at the start of each test.
- [ ] Save the current settings and prepare a way to revert them.
- [ ] Clear the test area; support the robot when checking individual motors.

## 1. Drivetrain and Pinpoint

1. Use the Pedro/Pinpoint tuning procedures compatible with the installed Pedro version. Open `Constants_GoBuilda.java` and record its original values.
2. Verify each mecanum motor direction with low-power manual movement. Forward, strafe and turn should each match the requested direction.
3. Validate the Pinpoint pose while moving the robot known distances and rotating it known angles. Verify X/Y sign, heading sign and heading zero.
4. Tune or correct physical calibration (including pod geometry/offsets) using actual measurements rather than guesses.
5. Run a small simple route, measure its endpoint and repeat it multiple times. Observe both position error and heading error.
6. Test driver cancellation of a BASIC destination and Auto-to-TeleOp handoff. Verify `AUTO HANDOFF` or the deliberately selected fallback.

**Record:** route, starting pose, measured endpoint, reported endpoint, heading error, battery voltage, changes made, result.

## 2. Shooter RPM and readiness

1. Use the **Shooter + Feeder Tuner** Driver Station OpMode. `LT` spins, `RT` feeds, D-pad Up/Down chooses a setting, and Left/Right adjusts it; `Y` toggles recovery.
2. From a consistent shooting location, test one preset without feeding. Confirm target vs. actual RPM and stable speed.
3. Adjust one of the available tuner values at a time: RPM, P, D, ready tolerance (RPM) or stability time (ms).
4. Test a realistic multi-ball volley. Note the **lowest observed RPM during feeding**, shot consistency, and time to recover.
5. Repeat with recovery on/off. Initial readiness is always required; recovery changes behavior after shooter speed drops during feeding.
6. Choose settings based on loaded-shot repeatability, not just unloaded steady-state RPM.
7. Copy selected tuner values into `V4Settings` manually. The tuner session does not persist them.

**Important:** Verify the goBILDA motor encoder conversion, output gearing and actual flywheel RPM in the current code before interpreting telemetry. Never assume target ticks/s and RPM are interchangeable.

## 3. Feeder and intake

- Test intake and both side rollers together, forward and reverse. Confirm no binding or game-piece jamming.
- Check feeder-only reverse without commanding forward feed simultaneously.
- Test the 500 ms full-system reverse hold, flywheel slow-down behavior and stop on release.
- Ensure feed waits for initial ready state and recovers correctly following a substantial RPM drop.
- Test repeated loaded cycles, counting actual successful feeds and misfeeds.

## 4. Validate with match conditions

- Run every intended Auto multiple times from the true start pose.
- Run a **full timed practice match** using normal gamepads and operator roles.
- Record cycle time, misses, jams, battery behavior and control mistakes.
- After any change, rerun the exact test it was intended to improve and a short regression test of unaffected controls.

## Suggested tuning log

| Date | Subsystem / condition | Old value | New value | Result / keep or revert |
|---|---|---|---|---|
| | | | | |
| | | | | |

**Do not treat example coordinates or starting gains from tutorials as field-calibrated competition values.**
