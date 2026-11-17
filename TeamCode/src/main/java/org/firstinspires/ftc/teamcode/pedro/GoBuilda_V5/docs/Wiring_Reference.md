# BIOBUZZ — Wiring Reference (GoBuilda V5)

## Currently Used Hardware

| Component | Hub | Port | Configuration Name |
|---|---|---|---|
| Front Left Drive | Control Hub | Motor 0 / Encoder 0 | `frontLeft` |
| Front Right Drive | Control Hub | Motor 1 / Encoder 1 | `frontRight` |
| Back Left Drive | Control Hub | Motor 2 / Encoder 2 | `backLeft` |
| Back Right Drive | Control Hub | Motor 3 / Encoder 3 | `backRight` |
| Main Intake | Expansion Hub | Motor 0 / Encoder 0 | `mainIntake` |
| Shooter | Expansion Hub | Motor 1 / Encoder 1 | `flywheel` |
| Left Side Roller (Servo 1) | Control Hub | Servo 0 | `leftSideRoller` |
| Right Side Roller (Servo 2) | Control Hub | Servo 2 | `rightSideRoller` |
| Pusher / Feeder (Windmill) | Control Hub | Servo 4 | `pusher` |
| goBILDA Pinpoint | Control Hub | I²C 0 | `pinpoint` |

## Optional Accessories

| Component | Hub | Port | Configuration Name |
|---|---|---|---|
| Left Flower Arm (Servo 1) | Control Hub | Servo 0 | `leftFlowerArm` |
| Right Flower Arm (Servo 2) | Control Hub | Servo 2 | `rightFlowerArm` |
| Sorting Gate (Servo 1) | Control Hub | Servo 0 | `sorterLeft` |
| Shot Restrictor (Servo 2) | Control Hub | Servo 2 | `shotRestrictor` |

*Servo 1 = left / Control Hub port 0; Servo 2 = right / Control Hub port 2. Accessories listed on the same port are interchangeable, not simultaneous. The pusher/feeder always uses the Windmill connection (Control Hub servo port 4).*
