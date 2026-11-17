# BIOBUZZ GoBuilda V5 — Documentation

This documentation accompanies the 2026–27 FTC BIOBUZZ goBILDA mecanum robot. V5 uses a subsystem-based FTC Java structure, Pedro Pathing and goBILDA Pinpoint for navigation, a flywheel shooter, intake with side rollers, and a continuous feeder.

## Start here

| Document | Use it for |
|---|---|
| [Controls Guide](Controls_Guide.md) | Competition single-driver and two-driver controls |
| [Wiring Reference](Wiring_Reference.md) | Motor, servo, sensor names and physical port assignments |
| [Tutorial Guide](Tutorial_Guide.md) | Coding examples, adding controls, and building a new Auto |
| [Settings Reference](Settings_Reference.md) | Where configuration values live and how to change them safely |
| [Tuning Guide](Tuning_Guide.md) | Repeatable drive, localization, shooter and feeder tuning |
| [Autonomous Guide](Autonomous_Guide.md) | Field poses, paths, Ivy commands and Auto test procedure |
| [Troubleshooting Guide](Troubleshooting_Guide.md) | Symptom-based diagnosis and rollback |
| [System Architecture](System_Architecture.md) | How OpModes, bindings, subsystems and navigation interact |
| [Pre-Competition Checklist](PreCompetition_Checklist.md) | Tasks 1–2 days before competition |
| [Changelog](CHANGELOG.md) | Documented V5 design decisions and future changes |

## First use

1. Check hardware names against [Wiring Reference](Wiring_Reference.md).
2. Confirm Pedro and Pinpoint calibration, physical directions, and a known starting heading.
3. Verify the shooter and feeder with the mechanism tuner before shooting game pieces.
4. Measure and enter real field coordinates. Library placeholders are **not safe destinations**.
5. Test the selected Auto repeatedly, then test Auto-to-TeleOp handoff.
6. Run both COMP layouts and the [Pre-Competition Checklist](PreCompetition_Checklist.md).

## Source-of-truth note

The `Tutorial_Guide.md` supplied with V5 is the basis for code paths, controls and behavior in this package. The complete current V5 Java source was **not included** with this documentation request. Where exact defaults, ranges, names or implementation details depend on current source, the guides say **verify in code** rather than invent values. If the code and these documents disagree, verify the deployed code and correct the documentation.

## Robot hardware names

`frontLeft`, `frontRight`, `backLeft`, `backRight`, `mainIntake`, `leftSideRoller`, `rightSideRoller`, `flywheel`, `pusher`, `pinpoint`.

## Editing workflow

Commit or copy a known-good version, change one feature, build, test the affected subsystem, test COMP interaction, and only then update the changelog.
