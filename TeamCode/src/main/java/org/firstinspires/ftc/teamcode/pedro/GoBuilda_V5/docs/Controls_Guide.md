# BIOBUZZ GoBuilda V5 — Driver Controls Guide

Standalone controller reference extracted and expanded from `Tutorial_Guide.md`.

## Controller basics

- **Hold:** the action runs only while the control stays held.
- **Press:** the action happens once when pressed.
- **Toggle:** each press switches a feature between states.
- **L3 / R3:** press the left / right joystick inward.
- **Share / Options:** modifier buttons used together with another control.
- **Gamepad 1 (G1):** driver controller. **Gamepad 2 (G2):** operator controller in two-driver mode.

The single-driver and two-driver layouts are **different**. Check which TeleOp is selected before a match.

## COMP Single Driver — Gamepad 1

### Driving and navigation

| Control | Action | What to know |
|---|---|---|
| Left stick | Drive/strafe | Move the robot forward, backward, and sideways with the mecanum drivetrain. |
| Right stick | Rotate | Turn the robot. Manual stick input also cancels an active BASIC navigation request. |
| L3 — press | Toggle slow mode | Switch between normal and reduced-speed driving for careful alignment. |
| D-pad Down | BASIC → Shoot 1 | Request the first saved shooting destination. |
| D-pad Right | BASIC → Shoot 2 | Request the second saved shooting destination. |
| D-pad Left | BASIC → Garden | Request the saved Garden destination. |
| D-pad Up | BASIC → Loading | Request the saved Loading destination. |
| Options + R3 — hold 750 ms | Switch field-centric / robot-centric | Hold both long enough to change the driving reference. Field-centric depends on the robot's pose estimate. |

### Intake, shooter, and feeder

| Control | Action | What to know |
|---|---|---|
| LT — hold | Intake | Runs intake while held; linked side rollers operate with the intake. |
| LB — hold | Outtake | Reverses intake while held to eject or clear game pieces. |
| RB — press | Toggle shooter | One press starts the flywheel; another switches it off. |
| RT — hold | Feed forward | Requests feeding; the feeder waits for the shooter to reach its initial ready condition. |
| A — hold | Feeder-only reverse | Reverses the feeder to clear a local jam. This takes priority over forward feeding. |
| Y — hold 500 ms | Full system reverse | Emergency/jam-clearing sequence for the mechanism system. Waits for the flywheel to slow; stops when released. |
| B — press | Cycle RPM preset | Repeated presses select **NORMAL → CLOSE → ANGLED → NORMAL**. |
| Share + D-pad Up / Down | +25 / −25 RPM | Temporarily increase or decrease the selected target RPM in 25-RPM steps. |
| Share + D-pad Left / Right | −10 / +10 RPM | Make finer 10-RPM adjustments to the selected target. |
| Options + X | Toggle pause-and-recover | Controls whether feeding pauses to let shooter speed recover between shots. |
| X alone | Unassigned | Available for a future custom control. |

## COMP Two Driver

### Gamepad 1 — Driver

| Control | Action | What to know |
|---|---|---|
| Left stick | Drive/strafe | Forward, backward, and sideways mecanum translation. |
| Right stick | Rotate | Turn; moving the sticks cancels BASIC navigation. |
| RT — hold | Intake | Runs the intake and linked side rollers while held. |
| LT — hold | Outtake | Reverses the intake while held. |
| LB — press | Toggle slow mode | Useful for lining up at a goal or loading area. |
| A — press | BASIC → Shoot 1 | Navigate to the first saved shooting position. |
| B — press | BASIC → Shoot 2 | Navigate to the second saved shooting position. |
| X — press | BASIC → Garden | Navigate to the saved Garden position. |
| Y — press | BASIC → Loading | Navigate to the saved Loading position. |
| RB — press | Signal “lined up” | Sends **two rumble pulses on Gamepad 2** to tell the operator the driver is ready. Does not fire automatically. |
| Options + R3 — hold 750 ms | Field-centric / robot-centric | Switch how movement directions relate to the field or robot. |

### Gamepad 2 — Operator

| Control | Action | What to know |
|---|---|---|
| LT — hold | Run shooter | Keeps the flywheel requested while the trigger is held. Unlike single-driver RB, this is **not a toggle**. |
| RT — hold | Request feed | Feeds when the initial shooter-ready condition has been met. |
| RB — hold | Feeder-only reverse | Reverse the feeder to clear a local jam; overrides forward feed. |
| B — press | CLOSE RPM preset | Select the close-range flywheel target. |
| X — press | NORMAL RPM preset | Select the standard flywheel target. |
| Y — press | ANGLED RPM preset | Select the angled-shot flywheel target. |
| D-pad Up — press | Toggle pause-and-recover | Enable or disable recovery pauses during a feeding sequence. |
| D-pad Down — hold 500 ms | Full system reverse | Overrides shooter/feed/intake requests after a deliberate half-second hold; release to end. |
| D-pad Left / Right | −5 / +5 RPM | Make small live adjustments to the selected shooter target. |
| A | Unassigned | Reserved for a future custom mechanism action. |
| Share + face buttons | Unassigned | Reserved for future optional accessories. |

## What happens during common situations

### Shooting normally

1. Select the correct **CLOSE**, **NORMAL**, or **ANGLED** preset.
2. Run the shooter: **RB toggle on** in single-driver, or **hold G2 LT** in two-driver.
3. Wait for the flywheel's ready indication. The shooter-ready rumble is **one pulse**.
4. Request feed: **hold RT** on the relevant controller. The initial readiness condition prevents early feeding.
5. If pause-and-recover is enabled, feeding can pause while the flywheel regains speed.

### Clearing a jam

- For a jam in the feeder alone, **hold A** (single-driver) or **G2 RB** (two-driver).
- For a broader jam, **hold Y** (single-driver) or **G2 D-pad Down** (two-driver) for **500 ms**. The full reverse sequence waits for the flywheel to slow and overrides competing requests.
- Release the reverse control to stop reversing. **Reissue any desired forward, feed, or shooter commands afterward**; do not assume the previous requests resume automatically.

### Using BASIC navigation

- A navigation button commands the robot toward a **saved** destination, with a straight-line route and heading interpolation.
- Moving the driving sticks cancels navigation and returns control to the driver.
- The robot uses its pose from Auto or its configured fallback. **Verify odometry, heading, and saved destinations before trusting autonomous movement.** BASIC navigation is not obstacle detection or automatic avoidance of other robots.

### Understanding rumble signals

| Signal | Meaning |
|---|---|
| One pulse | Shooter has reached its ready condition. |
| Two pulses on G2 | G1 driver manually signaled “lined up.” It does **not** mean the shooter is ready or automatically trigger a shot. |

## Quick reminders

- **Single-driver intake:** LT forward / LB reverse. **Two-driver G1 intake:** RT forward / LT reverse.
- **Single-driver shooter:** RB toggles. **Two-driver G2 shooter:** LT must be held.
- **Single-driver navigation:** D-pad. **Two-driver G1 navigation:** A/B/X/Y.
- **Single-driver RPM:** B cycles presets; Share + D-pad trims. **Two-driver G2 RPM:** B/X/Y choose presets; D-pad Left/Right trims.
- All button mappings here come from the provided V5 tutorial. Verify with the selected deployed TeleOp if the code has been modified since that tutorial was written.
