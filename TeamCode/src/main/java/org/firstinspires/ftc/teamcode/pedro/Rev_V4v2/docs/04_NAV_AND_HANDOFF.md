# Pedro navigation, field-centric and handoff

**REV mode** — A/B/X/Y navigation, field-centric drive, pose handoff, and Pedro Auto require `Drive.BACKEND = PEDRO`. In `MANUAL`, both TeleOps still have robot-centric stick drive and the same mechanism controls.
## Field-centric
Pedro `ManualDrive.fieldCentric()` is the primary implementation, taking Pinpoint/Pedro heading and an alliance-specific driver offset. These offsets do NOT modify actual global pose. To test: push left stick forward while steadily spinning; movement must remain field-forward at achievable combined wheel speed. Robot-centric fallback bypasses field transform entirely. Auto-facing aggressively turns toward commanded field direction but is NOT recommended for competition until carefully calibrated. No extra heading-hold PID in standard modes.

## Basic / advanced
Basic: one straight `Paths.line(current, destination).linear(current,destination)`. Advanced: small waypoint corridors; **disabled until all routes verified**, even if enable flag is set. Shared destination library contains legacy placeholder points; A/B/X/Y directions are configurable. New nav requests supersede old. Manual stick beyond deadzone cancels. Arrival requires heading+position tolerance stable 150ms and Pedro not busy; timeouts report failure; rumbles on success/failure. Driver controls shooter and feeder throughout navigation.

## New destinations and startup fallback

`FieldLibrary.at()` now includes four flower collection end poses, two angled shooting poses, `PARK_LEFT`, and `PARK_RIGHT`. See `09_FIELD_POSES_AND_CALIBRATION.md`. New poses return `null` until their calibration settings are enabled. `PARK` is kept as an alias of legacy-seeded `PARK_RIGHT`.

## Handoff and no-Auto initialization

Every Auto must call `MatchHandoff.beginAuto()` in INIT, save measured pose and alliance regularly, and make a final save in `stop()`. The next TeleOp consumes this handoff once in INIT.

**If valid:** restore Auto X/Y/heading and Auto alliance. Do not overwrite with any parking pose.

**If missing/invalid:** select `V4Settings.Drive.DEFAULT_ALLIANCE` and set Pedro's pose to the alliance's `V4Settings.Navigation.FALLBACK_START_POSE`, initially `PARK_RIGHT`. This seeds the heading for field-centric driving. When alliance is changed during INIT, reload the *corresponding* fallback pose. Changing alliance after Play adjusts the driver reference only; it does not teleport the localizer.

The fallback is an **assumption**. `POSE SOURCE` displays `FALLBACK: PARK_RIGHT`, and `NAV POSE` remains `UNVERIFIED` unless `TRUST_FALLBACK_POSITION_FOR_NAV` is explicitly enabled after physically confirming placement. The driver can always choose robot-centric fallback. Field-centric requires an initialized heading. Pose source appears in INIT and active telemetry.

**Critical limitation:** Simply having finite X/Y/heading does NOT guarantee that Pinpoint is accurate. Verify real pose with field marks. 90% is minimum *test target*, not guaranteed.

**Independent navigation speed settings:** Basic/Advanced speed constants exist but are not yet applied to Pedro's Foresight velocity constraints; do not claim those speed settings are operational. Match your exact installed Pedro API before connecting them.
