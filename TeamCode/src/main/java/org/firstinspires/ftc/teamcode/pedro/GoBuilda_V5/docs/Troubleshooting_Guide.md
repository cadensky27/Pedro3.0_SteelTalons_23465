# GoBuilda V5 — Troubleshooting Guide

Use the symptom to narrow the cause. **Record the exact error or telemetry before changing settings.** For hardware testing, keep the robot supported, remove game pieces when appropriate, and use low power.

## Fast diagnosis table

| Symptom | Check first | Next action |
|---|---|---|
| INIT says hardware device not found | Exact device name vs. active Driver Station robot configuration | Compare with [Wiring Reference](Wiring_Reference.md); verify correct hub/port and configured device type |
| A motor will not move | Battery, physical cable, motor port, program output | Test a safe low-power command; check stop conditions, direction and intended OpMode |
| Wheel turns the wrong way | Motor direction settings and physical wheel location | Correct one motor at a time and retest forward/strafe/rotation |
| Driver moves forward but pose moves backward | Pinpoint orientation, odometry direction, coordinate convention | Validate known translation and rotation; correct calibration, not field targets |
| Field-centric drive feels rotated | Actual robot heading vs. software heading and Auto handoff/fallback | Verify `AUTO HANDOFF` or `FALLBACK`; select correct reference and test |
| BASIC navigator does nothing | Destination may be unconfigured, wrong assigned target, or pose guard | Read telemetry; configure the measured named pose and retry in a safe area |
| BASIC navigator takes an unsafe line | BASIC is straight-line pathing, not obstacle avoidance | Disable that shortcut until a safe route has been designed and tested |
| Auto stops early | First incomplete sequential action, timeout, or command status | Inspect ordered routine, path completion, shooter readiness and feeder duration |
| Auto ends at wrong place | Start pose, Pinpoint drift, Pedro calibration or field target | Test a known simple path repeatedly before altering the Auto sequence |
| Shooter reports zero RPM | Power output, motor-map name, cable, encoder source/conversion | Check whether flywheel physically spins; inspect telemetry units and velocity sensor |
| Shooter overshoots / oscillates | Velocity-control gains, encoder conversion, shot load | Return to known stable gains; retune one parameter at a time |
| Feeder waits even with RT held | Shooter not initially ready; target/actual RPM or interlock | Compare readiness telemetry, operator requests and selected preset |
| Feeder pauses during shooting | Pause-and-recover mode and RPM drop | Check recovery threshold, hold time, feed load and jam conditions |
| Feeder runs backward unexpectedly | Reverse held, button conflict, program priority | Release all controls; test feeder-only and full reverse separately |
| Side roller not moving with intake | Accessory enabled state, port, servo name, direction | Confirm both `leftSideRoller` and `rightSideRoller` are installed and mapped |
| DS loses connection / robot resets | Battery voltage, XT30, cables, load, hub power | Inspect power path and connectors; reduce unsafe mechanical loads |
| Java build fails | First compiler error, imports, class name, dependency version | Fix first error, rebuild, then inspect subsequent errors |

## A consistent debugging process

1. **Reproduce** the problem with minimal, safe actions.
2. **Observe** exact messages, current OpMode, gamepad inputs, voltage, current/target values, and pose source.
3. **Isolate** the affected hardware or software subsystem; do not change several unrelated constants.
4. **Change one thing**, document it, and rerun the same test.
5. **Regression-test** related behavior (release, STOP, recovery, Auto-to-TeleOp handoff).
6. **Keep or revert** using the most recent known-good Git commit/project copy.

## Controller-specific checks

- Single-driver: intake **LT**, outtake **LB**, shooter **RB toggle**, feed **RT hold**.
- Two-driver: G1 intake **RT**, outtake **LT**; G2 shooter **LT hold**, feed **RT hold**.
- The full-system reverse requires a **500 ms hold**, then stops on release. Reissue normal requests afterward.
- Two rumble pulses on G2 are a **lined-up signal** from G1. One shooter-ready rumble means something different.

## When to stop testing

Stop immediately for hot electrical connections, damaged insulation, repeated power loss, severe mechanism binding, unpredictable motion, or risk to people/field elements. Repair and reverify before another powered test.

## Before redeploying

- [ ] Save / identify known-good code version.
- [ ] Build succeeds in the actual FTC Android Studio project.
- [ ] INIT maps all installed devices.
- [ ] TeleOp mechanisms stop correctly on release and STOP.
- [ ] Navigation cancel and one representative Auto still work.
