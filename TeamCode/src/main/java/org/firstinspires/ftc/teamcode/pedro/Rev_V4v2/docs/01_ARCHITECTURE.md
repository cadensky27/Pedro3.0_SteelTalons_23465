# Architecture and ownership
`config/V4Settings.java` selects the drive backend before deployment and holds the wheel names and directions. `subsystems/ManualMecanumDrive` maps and drives the four wheels through the FTC SDK in MANUAL. In PEDRO, `Constants.create(hardwareMap)` creates the follower and Pinpoint localizer using the shared wheel settings. Only the selected backend commands the wheels.

`core/Robot`: builds mechanism subsystems, only maps optional accessories if enabled, records missing accessory errors without disabling other mechanisms, and stops motors/CR servos safely. No generic hardware factories.

`subsystems/`: fixed independent mechanisms: one flywheel, one continuous roller feeder, one DC intake plus exactly two optional side CR servos, optional two positional flower arm servos, optional one positional sorter, optional one positional shot restrictor. No catapult or Spindex variant.

`teleop/`: single- and dual-driver class selected explicitly; a small common lifecycle to avoid duplicated handoff/heading/rumble logic; controls call public subsystem methods directly. No generic button-binding registry.

`navigation/`: Basic uses straight Pedro line from current pose; Advanced is a separately selectable, fail-closed corridor selector. Navigator owns follower only while running, and joystick movement cancels it.

`commands/`: reusable Ivy `Command` factories; commands start/stop mechanisms, not construct paths. User-written Auto classes compose them with `follow(follower,path)`.

Naming: subsystem methods are public hardware operations, private helpers are local, and command factories under `MechanismCommands` are explicit autonomous entry points. Settings are authoritative in `config/V4Settings.java`, except Pedro's necessary tuned `Constants.java`.
