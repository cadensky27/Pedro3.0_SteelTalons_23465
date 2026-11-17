# Hardware and tuning settings
Edit `config/V4Settings.java` and redeploy. All major capabilities have settings. Do not introduce a second legacy config file. Pedro's tuned `Constants.java` is separate for library integration only.

## Startup drive selection
`Drive.BACKEND` defaults to `MANUAL` for robot-centric SDK drive. Set it to `PEDRO` and redeploy to enable Pinpoint, field-centric drive, navigation, and Pedro Auto. `Hardware.FRONT_LEFT`, `FRONT_RIGHT`, `BACK_LEFT`, and `BACK_RIGHT` and their corresponding `_DIRECTION` settings apply to both backends and the wheel test. Left REVERSE/right FORWARD are inherited starting values.

## Hardware-map strings from legacy
`frontLeft`, `frontRight`, `backLeft`, `backRight`; `pinpoint`; `mainIntake`; `leftSideRoller`, `rightSideRoller`; `flywheel`; `pusher` (now roller feeder); `leftFlowerArm`, `rightFlowerArm`; `shotRestrictor`; `sorterLeft` (**verify: now exactly ONE sorter servo, whereas older config had two**).

Defaults: side rollers ENABLED, all three optional arm/shooter gate/sorting gate DISABLED; no shot-blocker release. Flower arm always TWO standard servos; side rollers always TWO CR servos; sorting and restrictor always ONE standard servo each.

Shooter legacy seeds: NORMAL=2950, CLOSE=2750, ANGLED=3150 RPM; P=47, I=0, D=0, F=12.5; 28 ticks/motor revolution; gear ratio 1:1; readiness tolerance 75 RPM; hold 250ms.

New unverified seeds: active-feeding minimum=2600 RPM, recovery=2800 RPM stable 150ms, gate opening delay=200ms. RPM interlock enabled by default. Reduced reverse feed power=-0.35. Servo positions of 0.5 are PLACEHOLDERS; do NOT enable optional positional mechanisms before calibrating.

In PEDRO, `Drive.DEFAULT_MODE` defaults to field-centric when heading is available. MANUAL always uses robot-centric controls. Auto-facing experimental selected only through `DEFAULT_MODE`. Slew rate disabled by default. Dashboard tuner is wholly separate and values must be copied back to settings.

## V4 pose update settings

- `Drive.DEFAULT_ALLIANCE`: BLUE or RED for no-Auto TeleOp. Initially RED.
- `Drive.BLUE_OFFSET_DEG` / `RED_OFFSET_DEG`: driver reference offsets, not global pose headings (see field calibration guide).
- `Navigation.FALLBACK_START_POSE`: defaults to `PARK_RIGHT` when no valid Auto handoff exists.
- `Navigation.TRUST_FALLBACK_POSITION_FOR_NAV`: false by default. Field-centric uses fallback heading; navigation remains disabled without verified placement.
- `Navigation.FLOWER_POSES_CALIBRATED`, `ANGLED_SHOTS_CALIBRATED`, `PARK_LEFT_CALIBRATED`: false by default. Prevent navigation to new placeholder coordinates until measured.
- `Navigation.A/B/X/Y`: may reference any available destination; no new buttons.

Coordinate reference: `09_FIELD_POSES_AND_CALIBRATION.md`. Wiring record: `10_WIRING_AND_PORTS.md`.
