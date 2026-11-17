# BIOBUZZ V5 Tutorial Guide

This guide assumes you can read Java methods and variables. Start with the task
you need to do, then test the change on the robot.

- [Find your way around](#find-your-way-around)
- [Look up a controller binding](#controller-reference)
- [Add a control from scratch](#add-a-control-from-scratch)
- [Tune a shot and keep the result](#tune-a-shot)
- [Add or edit a field pose](#edit-field-poses)
- [Build an Auto from scratch](#build-an-auto-from-scratch)
- [Troubleshoot, deploy, and roll back](#troubleshoot-and-deploy)

## Find your way around

| File | What you edit there |
|---|---|
| `teleop/BaseTeleOp.java` | Buttons, hold/press/toggle behavior, and what wins when commands overlap |
| `teleop/Binding.java` | How button states become continuous requests, single presses, or toggles |
| `config/V4Settings.java` | Hardware names, installed accessories, RPM, power, drive feel, and timing |
| `subsystems/` | How each motor or servo behaves |
| `navigation/FieldLibrary.java` | Named X/Y/heading poses |
| `autonomous/` | Each Auto's starting pose, paths, and actions in order |
| `commands/MechanismCommands.java` | Ivy actions used by Auto routines |
| `Constants_GoBuilda.java` | Four drive motors, Pinpoint calibration, and tuned Pedro values |

**Follow a real input.** A driver holds intake. `BaseTeleOp.loop()` calls
`handleIntakeControls()` and reads the intake binding for that layout.
`IntakeSubsystem.setDirection()` writes power to the motor and linked rollers.
The next loop sees the release and sends direction zero. That is the path to
follow when adding a control.

**OpMode lifecycle.** INIT maps hardware and selects the starting pose. START
sets the match starting state. Each loop reads controls, chooses actions, and
updates hardware and telemetry. STOP finishes pending actions and shuts down
the outputs. New hold controls need to reach a stop path on both release and STOP.

## Controller reference

### COMP Single Driver — Gamepad 1

| Input | Action |
|---|---|
| Left / right stick | Mecanum translation / rotation |
| LT / LB, hold | Intake / outtake |
| L3, press | Toggle slow mode |
| RB, press | Toggle shooter |
| RT, hold | Feed forward when first ready |
| A, hold | Feeder-only reverse |
| Y, hold 500 ms | Full system reverse |
| B, press | Cycle NORMAL, CLOSE, ANGLED |
| X alone | Free |
| D-pad Down / Right / Left / Up | BASIC to Shoot 1 / Shoot 2 / Garden / Loading |
| Share + D-pad Up / Down | +25 / −25 RPM |
| Share + D-pad Left / Right | −10 / +10 RPM |
| Options + X | Toggle pause-and-recover during shooting |
| Options + R3, hold 750 ms | Switch field-centric / robot-centric |

### COMP Two Driver

| Controller | Input | Action |
|---|---|---|
| Gamepad 1 | Left / right stick | Mecanum translation / rotation |
| Gamepad 1 | RT / LT, hold | Intake / outtake |
| Gamepad 1 | LB, press | Toggle slow mode |
| Gamepad 1 | A / B / X / Y | BASIC to Shoot 1 / Shoot 2 / Garden / Loading |
| Gamepad 1 | RB, press | Two pulses on Gamepad 2 for “lined up” |
| Gamepad 1 | Options + R3, hold 750 ms | Switch field-centric / robot-centric |
| Gamepad 2 | LT / RT, hold | Run shooter / request feed |
| Gamepad 2 | RB, hold | Feeder-only reverse |
| Gamepad 2 | B / X / Y | CLOSE / NORMAL / ANGLED RPM presets |
| Gamepad 2 | D-pad Up | Toggle pause-and-recover |
| Gamepad 2 | D-pad Down, hold 500 ms | Full system reverse |
| Gamepad 2 | D-pad Left / Right | −5 / +5 RPM |
| Gamepad 2 | A; Share + face buttons | Free; Share combinations reserved for future accessories |

Field-centric uses the pose from Auto or the configured fallback. BASIC follows
a straight line with heading interpolation. Move the sticks to cancel it.
Feeder-only reverse takes priority over forward feed. Full reverse overrides
shooting and intake after its 500 ms hold, waits for the flywheel to slow, and
stops on release. Drivers reissue the relevant commands to resume. A single
shooter-ready pulse differs from the two-pulse lined-up signal.

## Add a control from scratch

1. Pick a free input in the layout you want to change. Use `HOLD` for an action
   that stops on release, `PRESS` for one event per press, or `TOGGLE` for a state.
2. Add a named binding to the Controls section in `BaseTeleOp.java`.
3. Read it once per loop in the relevant handler. Call an existing subsystem
   method, or add a small method if the action is new.
4. Decide what happens on release and when another control wants the same
   output. Keep one place that decides what the output should do in a loop.
5. Check press, hold, release, conflicting input, and Driver Station STOP.

The two examples below are practice edits. Use a copy of the project or a Git
branch so you can try them independently.

### Example 1 — Hold X as a second intake button

In the Single Driver Controls section, add this binding. The modifier check
keeps Options + X available for the recovery toggle.

```java
private final Binding practiceIntake = new Binding(X, HOLD);
```

In `handleIntakeControls()`, after reading the existing intake and outtake
bindings, insert the following before calculating `direction`.

```java
boolean practiceCollect = practiceIntake.read(gamepad1, !twoDrivers() && !gamepad1.options);
collect = collect || practiceCollect;
```

The existing `setDirection(direction)` call does the work. Holding X sets
`collect` true. Releasing it returns the intake to zero unless LT is held.
If outtake is also requested, the two directions cancel. Try X on its own,
then with LB, then with Options, and check that STOP also stops the intake.

### Example 2 — Hold Gamepad 2 A for shooter-only reverse at 75% power

The control is a held input, one named power value, and a stop on release.
V5 already has flywheel reverse in `ShooterSubsystem`. For this practice example
we add a fixed-power mode there. The **75% value is illustrative**. Verify motor
direction and test the output uncoupled or unloaded before using it with game pieces.

1. In the Two Driver operator Controls section of `BaseTeleOp.java`, add
   `private final Binding practiceShooterReverse = new Binding(A, HOLD);` and
   `private static final double PRACTICE_REVERSE_POWER = .75;`.
2. In `ShooterSubsystem.java`, add the field and method below. At the top of
   `enable(boolean on)`, add `if (!on) setPracticeReverseHeld(false, 0);`
   so STOP also ends this practice command. At the top of
   `jamClearHeld(boolean held)`, add `if (held) setPracticeReverseHeld(false, 0);`
   so full reverse takes priority. Add `if (practiceReverseActive) return;`
   at the top of `update()` so normal velocity control respects the example.

```java
private boolean practiceReverseActive;

public void setPracticeReverseHeld(boolean held, double powerMagnitude) {
    if (held && !practiceReverseActive) {
        enable(false);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        practiceReverseActive = true;
    }
    if (held) {
        motor.setPower(-powerMagnitude);
    } else if (practiceReverseActive) {
        motor.setPower(0);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setVelocity(0);
        appliedP = Double.NaN; // Reapply tuned gains when velocity control resumes.
        practiceReverseActive = false;
    }
}
```

3. In `handleShooterAndFeederControls()`, after choosing the operator Gamepad
   and before processing normal outputs, add this. The LT/RT reset requires a
   fresh trigger pull before a shot can resume.

```java
if (twoDrivers() && robot.shooter != null) {
    boolean reverseHeld = practiceShooterReverse.read(operator, !operator.share);
    robot.shooter.setPracticeReverseHeld(reverseHeld, PRACTICE_REVERSE_POWER);
    if (reverseHeld) {
        operatorShooter.clearUntilReleased(operator);
        operatorFeed.clearUntilReleased(operator);
        if (robot.feeder != null) robot.feeder.stop();
        return;
    }
}
```

Read the first test in two halves: while A is held, the example owns the shooter
motor. On release, verify the motor output goes to zero and the flywheel
can coast. Then check A with LT/RT, full reverse, and Driver Station STOP. When
you add your own action, keep its input in TeleOp and the hardware behavior in
the subsystem, using this same pattern.

## Tune a shot

- **Preset:** NORMAL, CLOSE, and ANGLED choose the RPM values under
  `V4Settings.Shooter`. A new preset resets the temporary trim.
- **Match trim:** Use the controller bindings above to shift the selected RPM.
  Selection or trim changes require the shooter to establish readiness again.
- **Shooter + Feeder Tuner:** Driver Station OpMode. LT spins, RT feeds,
  D-pad Up/Down selects a setting and Left/Right adjusts it. Y toggles
  recovery. Available settings are RPM, P, D, readiness tolerance in RPM,
  and readiness stability time in ms. Telemetry shows the tuned values,
  actual/target RPM, feeder state, and the lowest observed RPM during feeding.
- **Keep a result:** Compare an unloaded spin-up and a loaded volley from the
  same shooting location. Copy the chosen values into `V4Settings` after
  practice. Tuner edits are local to its session.
- **Dashboard:** The separate shooter PIDF tuner is for a Dashboard-enabled
  SDK project.

The feeder always waits for initial readiness in COMP and the controller tuner.
With recovery on, a speed loss below the pause threshold stops feeding until
the configured recovery band is held.

## Edit field poses

The library uses inches and degrees in a **shared Red-authored frame**. Heading
0° faces +X, and 90° faces +Y. Start at the equivalent location and orientation
on the other alliance to use the same route or target.

1. Enter the measured X/Y/heading in the target's case under `FieldLibrary.at()`.
2. If you want a shortcut to that pose, change one of the four destination
   constants next to the bindings in `BaseTeleOp.java`.
3. To start standalone TeleOp or Pinpoint Validation from that pose, change
   `V4Settings.Navigation.FALLBACK_START_POSE`.

`START_PRIMARY`, `START_ALTERNATE`, `START_GARDEN`, and `START_FLOWER` are
shared starting categories awaiting agreed field coordinates. Enter each
independently. `PARK_RIGHT` is the current default fallback. INIT shows
`AUTO HANDOFF` when the measured ending pose was handed over, and
`FALLBACK: <pose>` when it seeds Pedro from the library. An unconfigured
target reports its name and stays put. Set a real pose before using it as a
fallback.

## Build an Auto from scratch

### 1. Create a park-only routine

Copy the active portion of `UserAutoTemplate.java` into `PracticePark.java` in
the same package. Rename the class to `PracticePark`, set the annotation to
`@Autonomous(name = "Practice Park", group = "BIOBUZZ V5")` and remove
`@Disabled` from the new file. For a compact drive-only example, open
`GardenHoldParkLeft.java`.

Replace the class body, inside its outer braces, with the example below.
Then replace the start and destination values with your measured locations. The
values below illustrate a small straight practice move. Pick a clear test area
and match the robot's physical start before pressing START.

```java
private final PoseFactory poses = PoseFactory.degrees();
private final Pose startPose = poses.of(24, 24, 0);
private final Pose parkPose = poses.of(36, 24, 0);

@Override
protected Pose startingPose() {
    return startPose;
}

@Override
protected boolean usesMechanisms() {
    return false;
}

private Path pathToPark() {
    return Paths.line(startPose, parkPose).linear(startPose, parkPose);
}

@Override
protected Command buildRoutine() {
    return sequential(
            waitMilliseconds(2_000),
            follow(follower, pathToPark())
    );
}
```

Use `FieldLibrary.require(Destination.START_PRIMARY)` in place of the local
start only after that library pose has been configured and matches your route.
A curve replaces `Paths.line(start, end)` with
`Paths.curve(start, controlPoint, end)`. Control points shape the path.
The `.linear(start, end)` call controls how the robot's heading changes along
it. When you join paths, use the same pose for the previous end and next start.

### 2. Run and verify the practice route

Build and deploy. Choose **Practice Park** in Auto on the Driver Station.
Verify the location in INIT, then check the delay, travel direction, final
heading, and park. Switch to COMP TeleOp without moving the robot and check
that INIT reports **AUTO HANDOFF**. Repeat after stopping Auto partway through.

### 3. Add a shoot-and-park sequence

Remove the `usesMechanisms()` override so the new routine initializes its
mechanisms. Add a measured `shootPose`, insert `pathToShoot()`, and change
`pathToPark()` to start at `shootPose`. Keep the template's imports.

```java
private final Pose shootPose = poses.of(30, 24, 0); // Replace with your measured shot pose.

private Path pathToShoot() {
    return Paths.line(startPose, shootPose).linear(startPose, shootPose);
}

private Path pathToPark() {
    return Paths.line(shootPose, parkPose).linear(shootPose, parkPose);
}
```

Replace the body of `buildRoutine()` with this sequence:

```java
return sequential(
        waitMilliseconds(2_000),
        parallel(follow(follower, pathToShoot()), MechanismCommands.shooterOn(robot)),
        MechanismCommands.shootPreloads(robot, 2_800),
        MechanismCommands.shooterOff(robot),
        follow(follower, pathToPark())
);
```

`sequential()` starts each step when the previous one finishes.
`parallel()` lets the flywheel start while the robot travels. The readiness
command waits within a bounded window and skips feeding if the shooter never
becomes ready. The feed time counts actual forward motion. Converted V4v5
routines that used a fixed spin-up delay still explicitly schedule their
original delay and timed feed. New routines can choose whichever policy
their test results support.

## Troubleshoot and deploy

| Symptom | Check first |
|---|---|
| Hardware error during INIT | Device name in the error versus the active robot configuration. Use the [Wiring Reference](Wiring/Wiring_Reference.md) for the expected name. |
| Feeder waits or pauses | Compare target/actual RPM and feeder status. Check the operator's feed request and the recovery toggle. |
| Drive direction feels wrong | Compare the displayed pose source and the physical starting heading. Use COMP diagnostics and Pinpoint Validation. |
| Navigator reports an unconfigured target | Confirm the assigned destination and enter its measured pose in the library. |
| Auto appears to stop too soon | Read its ordered sequence, actual feeder run time, and wait/timeout values. |
| Java fails to compile | Read the first compiler error, check the filename/class name or import, then rebuild after fixing that error. |

**Build and rollback.** Save a known-good commit or project copy before
editing. Apply one change at a time, build in Android Studio, and deploy it
using your working FTC configuration. Use the pit OpMode for the hardware
you changed, then COMP for the real driver interaction. Roll back by checking
out your known-good version or restoring that project copy and redeploying.

**Rebuild acceptance checks.** On the robot, check both COMP layouts,
early feed with recovery on and off, full reverse release and trigger rearm,
stick cancellation during alignment, an Auto stopped before its end,
standalone fallback, and a fresh park-only Auto made from the template.
Verify both alliance placements using the same Red-authored route.
