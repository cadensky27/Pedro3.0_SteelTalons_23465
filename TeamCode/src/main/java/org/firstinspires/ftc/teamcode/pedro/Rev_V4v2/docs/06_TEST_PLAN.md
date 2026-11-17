# REV verification plan

## First REV bring-up

1. Compile the REV sources in the Android Studio FTC/Pedro/Ivy project. To verify package independence, compile in a checkout containing the REV sources and the required libraries, with the goBILDA V4 package omitted.
2. Keep `MANUAL` selected and unplug Pinpoint. Test both REV TeleOps. Check forward, strafe right, clockwise turn, fast/slow, mechanism controls, and immediate drive stop. Driver Station should show MANUAL.
3. Select the Pedro-only test tools with MANUAL selected and confirm they stay idle and report the setting they require.
4. After measuring the REV Pinpoint offsets and verifying motor directions, enable `PEDRO` and test field-centric drive, then navigation and Auto handoff.

## Inherited mechanism and Pedro checklist
1. Verify exact installed Pedro V3/Ivy dependency versions and compile. This zip includes no Gradle so there is no credible automatic build guarantee.
2. Elevate robot and verify each wheel motor, intake DC, two CR rollers, feeder roller, and shooter encoder ticks/directions.
3. Verify optional positional servos one at a time, left and right arm separately, and one-servo gate names. Do not enable optional positional servo mechanisms at placeholder positions.
4. Run FieldCentricValidation: straight forward during continuous spinning; heading offsets across both alliances; robot-centric fallback; right-stick overrides experimental auto-facing.
5. Confirm Share+Options and Options+R3 PS4 button chords in Driver Station and prevent repeat activations.
6. Check shooter readiness within tolerance for 250ms; gate delay; feeder RPM pause/recovery; jam reverse; shooter shutdown clears feed.
7. Verify shared field poses, under-hive clearance for all arm positions, and every Advanced corridor before changing safety toggle.
8. Test Basic Navigation: start, immediate reroute, joystick cancel, heading+position stability, timeout and rumble. Check shooting controls remain independent while moving.
9. Run 20 normal Auto→TeleOp handoffs (>=18 passing = 90% observed) then expand toward 100+ for stronger confidence; measure actual pose error separately from data transfer.
10. Verify both TeleOps, safe stop, optional missing hardware, minimal INIT telemetry, and standalone dashboard tuner in its own SDK environment.

**Current status:** An Android Gradle build and physical robot validation are pending. Treat all path/servo calibration and drive heading transforms as unverified until tested.

## Update 1 targeted tests (NOT YET RUN)

1. Compile the REV TeamCode sources against installed FTC/Pedro/Ivy dependency versions.
2. After a successful Auto handoff, verify exact X/Y/heading and alliance are retained (not reset to PARK_RIGHT).
3. With no handoff, verify default RED selects legacy RED PARK_RIGHT (12, 92, 0°); BLUE selects legacy BLUE PARK_RIGHT (128, 48, 180°).
4. In INIT, change alliance and verify corresponding fallback pose updates. After Play, switching alliance must **not** teleport the pose.
5. Validate standard field-centric translation at multiple headings, and robot-centric fallback.
6. With default `TRUST_FALLBACK_POSITION_FOR_NAV = false`, ABXY navigation must be unavailable under fallback alone. After physically validating placement and enabling the setting, confirm navigation.
7. Map A/B/X/Y temporarily to an uncalibrated new flower pose and verify the Navigator rejects it safely rather than driving to (0,0).
8. Confirm existing `PARK` destination still resolves to same seed coordinates as `PARK_RIGHT`.
9. Test Pinpoint on the REV robot’s configured I²C port for reliable heading and position data.
10. Check wiring documentation against photograph and actual REV Robot Configuration; fill TBD port assignments.
