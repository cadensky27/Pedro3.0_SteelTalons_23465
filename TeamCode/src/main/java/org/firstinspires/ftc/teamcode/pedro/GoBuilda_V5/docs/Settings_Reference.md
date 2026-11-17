# GoBuilda V5 — Settings Reference

V5 currently calls its centralized configuration class **`config/V4Settings.java`**. Despite the historical filename, it is used by the V5 tutorial. Do not rename it casually: the Java imports must be changed together.

## Where settings live

| Area | Location described by V5 tutorial | What to adjust | Verify after changing |
|---|---|---|---|
| Hardware names and installed accessories | `V4Settings.java` | Device names and accessory enablement | Robot INIT successfully maps exactly the installed devices |
| Shooter presets | `V4Settings.Shooter` | NORMAL, CLOSE, ANGLED target RPM | Accurate tach/telemetry, shot location, recovery under load |
| Shooter control | `V4Settings.Shooter` and tuner | PID velocity values, ready tolerance, stability time | Spin-up and consecutive-shot recovery; no oscillation |
| Navigation fallback | `V4Settings.Navigation.FALLBACK_START_POSE` | Named pose used when no Auto handoff exists | Correct physical start and heading |
| Field destinations | `navigation/FieldLibrary.java`, `FieldLibrary.at()` | Red-authored X/Y/heading values | Actual measured location, both alliance placements |
| TeleOp navigation buttons | `teleop/BaseTeleOp.java` | Four destination constants | Safe route and immediate stick cancellation |
| Driver button layouts | `teleop/BaseTeleOp.java` | Single/two-driver bindings and priorities | Press/hold/release and modifier combinations |
| Motor behavior | `subsystems/` | Physical output behavior, gains and safeguards | Safe testing on blocks before field use |
| Pedro drivetrain and localization | `Constants_GoBuilda.java` | Motor, Pinpoint and follower tuning | Direction, heading, path completion accuracy |

## Safe settings-edit process

1. Record the current value and the problem you are trying to solve.
2. Locate the exact constant in the current Java file; **do not assume a setting exists just because a related setting existed in V4**.
3. Make one adjustment at a time. Keep the current value in Git or a backup.
4. Build and deploy. Confirm INIT shows no hardware or configuration error.
5. Test unloaded first, then test with actual game pieces as applicable.
6. Record old/new values, battery condition, location, and test result; retain or revert.

## Shooter-specific notes

- Presets are **NORMAL**, **CLOSE**, **ANGLED**. Selecting a new preset clears its temporary match trim.
- Match trim is temporary; it is **not** an automatic permanent edit to `V5Settings`.
- The Shooter + Feeder Tuner can adjust RPM, P, D, ready tolerance in RPM, and stability in milliseconds. Tuner changes are **session-local** until copied into settings.
- Initial feeding waits for readiness. With pause-and-recover enabled, feeding stops when shooter speed falls below the pause threshold and resumes after it stays within the recovery band.
- No fixed PIDF coefficients, voltage-compensation constants or absolute motor limits are prescribed here; read and verify the current source and robot.

## Navigation-specific notes

- Field positions are measured in **inches** and headings in **degrees** in a shared **Red-authored** frame.
- Heading `0°` points along `+X`; `90°` along `+Y`.
- Four starting categories exist: `START_PRIMARY`, `START_ALTERNATE`, `START_GARDEN`, `START_FLOWER`. Each requires its own measured pose.
- `PARK_RIGHT` is the documented default fallback in the supplied tutorial. Set a real measured pose before relying on it.
- INIT shows `AUTO HANDOFF` for a prior measured ending pose or `FALLBACK: <pose>` when seeding from the library.
- Unconfigured destinations are reported and left inactive; never replace that safety behavior with arbitrary zero coordinates.

## Settings audit before competition

- [ ] Hardware names match the robot configuration.
- [ ] Uninstalled accessories are disabled and installed accessories are tested.
- [ ] Shooter targets, ready behavior and recovery were tested with a loaded volley.
- [ ] Navigation fallback and all used destinations were measured.
- [ ] Pedro/Pinpoint tuning and Auto-to-TeleOp handoff were verified.
- [ ] Current setting changes are committed and logged.
