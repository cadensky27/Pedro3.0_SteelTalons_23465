# Autonomous authoring — Pedro V3 + Ivy

**REV mode** — A/B/X/Y navigation, field-centric drive, pose handoff, and Pedro Auto require `Drive.BACKEND = PEDRO`. In `MANUAL`, both TeleOps still have robot-centric stick drive and the same mechanism controls.
You author and calibrate paths in the Pedro visualizer and provide code. Use individual `@Autonomous` OpModes. `UserAutoTemplate` is deliberately `@Disabled` so unverified geometry cannot be selected in competition.

- `init`: `Scheduler.reset()`, `MatchHandoff.beginAuto()`, create follower with `Constants.create`, `follower.setPose(knownStart)`, create `Robot`.
- `start`: `robot.startAuto(requiredArmPos)`, `schedule(buildRoutine())`.
- `loop`: `follower.update()`, `Scheduler.execute()`, `robot.update()`, `MatchHandoff.save(follower.pose(),alliance)`; when routine ends, stop motors but keep updating localizer through end of 30-second period.
- `stop`: last pose save, `Scheduler.reset()`, motor safe stop.

`MechanismCommands` provides shooterOn/off, preset, intakeStart/reverse/stop, intakeForMs, flowerArm, waitReady, feederStart/stop, feederForMs (actual forward time + wall timeout), shootPreloads (bounded wait and skip if not ready). Use Ivy `sequential`, `parallel`, and `follow(follower,path)` with each user-authored path. No path generator or Dijkstra graph. If a path fails, verify actual pose before any location-dependent shooting action.

Documentation: https://pedropathing.com/docs/pathing/guide/auto-usage and https://pedropathing.com/docs/ivy/creating-opmodes
