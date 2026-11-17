# BIOBUZZ V4 changelog

## REV V4v2

- Moved the REV Java package and optional tuner under `teamcode.pedro` alongside `GoBuilda_V4v4`.

- Copied V4 Update 1 into an independent REV Java package with distinct Driver Station names.
- Added a default FTC SDK robot-centric drivetrain and a startup selector for Pedro/Pinpoint.
- Both REV TeleOps share stick shaping and the existing mechanism controls.
- Packaged the standalone Dashboard shooter tuner separately.

## V4 unified rebuild (source baseline)
Removed: hardware factories and variant selectors, catapult, dual shooter, old four-sided Spindex, generalized AutoEngine/AutoMenu/AutoPlan, legacy Dijkstra route graph, old custom Auto, redundant RobotConstants, heading hold, generic control binding.

Preserved: exact tuned Pedro `Constants.java` and Pinpoint offsets, old hardware-map names for unchanged devices, flywheel PIDF and RPM seeds, optional accessories as standalone subsystems, telemetry concept, interchangeable use of single-/dual-driver TeleOps without hardware changes.

New: horizontal roller feeder, optional fixed-count servo hardware, intake side rollers, V4Settings, coordinated gate-feed interlock, live navigation mode hierarchy, Auto-to-TeleOp measured pose handoff, experimental Auto-Facing mode, dedicated standalone Dashboard tuner, disabled user-authored Ivy Auto template and comprehensive guides.

No spring-loaded shot-blocker release this round. No competition-ready autonomous routine because you create path geometry yourself. Unverified advanced route geometry fails closed. Actual compilation and on-robot calibration pending.

## Update 1 — Navigation destinations, park-right fallback, wiring record

- Added BLUE/RED destinations `FLOWER_1` through `FLOWER_4`, `ANGLED_SHOT_LEFT/RIGHT`, and `PARK_LEFT/RIGHT`. Kept `PARK` as legacy alias of `PARK_RIGHT`.
- Placed unmeasured coordinates behind calibration flags so A/B/X/Y does not drive to placeholder field origin.
- With missing Auto handoff, TeleOp now uses configured default alliance and `PARK_RIGHT` (legacy parking pose seed) to establish field-centric heading. Fallback navigation position remains untrusted unless explicitly enabled.
- Added `POSE SOURCE`, `FIELD HEADING`, and separate navigation-pose trust telemetry.
- Documented Pedro 0/90/180/270-degree heading conventions and BLUE-FIRST / RED-SECOND coordinate ordering.
- Added wiring overview, including requested goBILDA Pinpoint on Control Hub I²C Bus 0, with explicit shared-IMU-bus caution and unknown ports marked TBD.
- Updated all existing docs in place and added field calibration and wiring guides. No controls or mechanisms intentionally changed.
