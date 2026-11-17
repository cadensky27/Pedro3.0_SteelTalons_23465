# BIOBUZZ V4 — Wiring and ports

The table lists inherited hardware-map names. Record the actual REV ports and update `V4Settings.Hardware` to match the Robot Configuration. Pinpoint is only required in PEDRO. The Update 1 wiring diagram was for the goBILDA build.

| Device | FTC hardware-map name | Device type | Physical connection / port |
|---|---|---|---|
| goBILDA Pinpoint | `pinpoint` | I²C odometry computer | I²C port TBD on REV |
| Front left drive | `frontLeft` | DC motor | Motor port TBD |
| Front right drive | `frontRight` | DC motor | Motor port TBD |
| Back left drive | `backLeft` | DC motor | Motor port TBD |
| Back right drive | `backRight` | DC motor | Motor port TBD |
| Main intake | `mainIntake` | DC motor | Motor port TBD |
| Flywheel shooter | `flywheel` | DC motor w/ encoder | Motor port TBD |
| Roller feeder | `pusher` | CR servo | Servo port TBD |
| Left intake side roller | `leftSideRoller` | CR servo | Servo port TBD |
| Right intake side roller | `rightSideRoller` | CR servo | Servo port TBD |
| Flower arm left | `leftFlowerArm` | Positional servo (optional) | Servo port TBD |
| Flower arm right | `rightFlowerArm` | Positional servo (optional) | Servo port TBD |
| Shooter restrictor gate | `shotRestrictor` | Positional servo (optional) | Servo port TBD |
| Sorting gate | `sorterLeft` | Positional servo (optional) | Servo port TBD; single-servo name is legacy assumption |

**Motor count note:** Six DC motors are listed, more than the four motor ports on one standard Control Hub. An additional motor controller / Expansion Hub or an accurate actual layout will be needed in the physical diagram; do not invent which device is connected to which hub.

## Pinpoint in PEDRO mode

Confirm the configured I²C port and verify heading and position readings with the REV Pinpoint Validation OpMode.

## Completion checklist

- [ ] Attach the reference wiring photograph to the documentation.
- [ ] Record each actual Control Hub and Expansion Hub motor/servo port.
- [ ] Confirm which hub supplies each mechanism and all USB/RS485 connections.
- [ ] Verify Pinpoint responds reliably on the REV robot’s configured I²C port in PEDRO.
- [ ] Confirm all hardware-map names match the active robot configuration.
- [ ] Record expansion hub power and 12V wiring after physical inspection.

References: https://docs.revrobotics.com/duo-control/sensors/i2c and https://docs.revrobotics.com/duo-control/sensors/i2c/imu
