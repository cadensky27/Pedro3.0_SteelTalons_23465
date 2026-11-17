# GoBuilda V5 — Autonomous Development Guide

V5 uses Pedro paths and Ivy commands. The intended workflow is **write a routine in Java, test it repeatedly, and keep it separate from competition TeleOp**.

## Main files

| File | Role |
|---|---|
| `navigation/FieldLibrary.java` | Named Red-authored poses in inches/degrees |
| `autonomous/UserAutoTemplate.java` | Copy as the starting point for a new Auto |
| `autonomous/GardenHoldParkLeft.java` | Compact drive-only example in supplied tutorial |
| `commands/MechanismCommands.java` | Reusable shooter, feeder and other mechanism actions |
| `Constants_GoBuilda.java` | Pedro follower and localization calibration |
| `config/V4Settings.java` | Shooter and fallback settings |

## Prepare field poses

1. Measure the robot's **center position and orientation** for every relevant target; note the robot's size and field obstacles.
2. Add named poses in `FieldLibrary.at()`. Frame: **Red-authored**, X/Y inches, heading degrees, `0°` along +X, `90°` along +Y.
3. Configure `START_PRIMARY`, `START_ALTERNATE`, `START_GARDEN`, and `START_FLOWER` individually. They are **starts**, not park destinations.
4. Check the opposite alliance by placing the robot at the equivalent physical start and checking orientation and route behavior.
5. Do not run an unconfigured destination; the library is designed to report it and stay put.

## Create a park-only Auto

1. Copy the active part of `UserAutoTemplate.java` to a new class, e.g., `PracticePark.java`, in the same package.
2. Give it a unique `@Autonomous(name = "Practice Park", group = "BIOBUZZ V5")` name and remove `@Disabled` on the copy.
3. Define **measured** `startPose` and `parkPose` using `PoseFactory.degrees().of(x, y, heading)`.
4. Override `startingPose()` to return the start and `usesMechanisms()` to return `false` for drive-only testing.
5. Build a line with `Paths.line(startPose, parkPose).linear(startPose, parkPose)` and schedule it with `follow(follower, pathToPark())` in a `sequential(...)` command.
6. Begin in a clear test space and verify both travel and final heading. The tutorial's `(24,24)` and `(36,24)` coordinates are illustrative, **not** surveyed field positions.

## Add shooting to the route

1. Create measured `shootPose`; make a path from start to shot and another from shot to park.
2. Remove the drive-only `usesMechanisms()` override so mechanisms initialize.
3. Use `parallel(follow(follower, pathToShoot()), MechanismCommands.shooterOn(robot))` to spin up while traveling.
4. After arriving, use `MechanismCommands.shootPreloads(robot, timeMs)` with a **tested** feed limit; then `MechanismCommands.shooterOff(robot)` and follow the park path.
5. The newer readiness command waits within a bounded time and skips feeding if never ready. Verify the chosen routine uses this behavior; converted older routines may retain fixed delays/timed feeding.

## Path rules

- `Paths.line(start, end)` makes a straight segment. `Paths.curve(start, controlPoint, end)` uses a control point to bend a segment.
- `.linear(start, end)` interpolates heading across the segment.
- The endpoint of one segment must match the start of the next. Avoid jumps in pose.
- A straight line does **not** avoid field structures or other robots. Choose and test clearances explicitly.
- `sequential(...)` runs actions in order; `parallel(...)` runs them together. Explicitly consider timeouts and unfinished commands.

## Auto acceptance checklist

- [ ] Starts at the measured physical position and heading.
- [ ] Reaches every target without scraping obstacles.
- [ ] Feeds only when ready and completes or safely times out.
- [ ] Parks within the intended time and footprint.
- [ ] Repeats successfully over multiple runs and practical battery conditions.
- [ ] Handoff to COMP TeleOp displays `AUTO HANDOFF` and controls remain correct.
- [ ] Stopping Auto early has been tested; TeleOp fallback is safe.
- [ ] Both alliance placements have been explicitly verified.
