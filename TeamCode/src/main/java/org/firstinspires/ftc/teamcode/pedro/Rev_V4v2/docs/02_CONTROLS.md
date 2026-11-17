# V4 Printable Controller Reference

**REV mode** — A/B/X/Y navigation, field-centric drive, pose handoff, and Pedro Auto require `Drive.BACKEND = PEDRO`. In `MANUAL`, both TeleOps still have robot-centric stick drive and the same mechanism controls.
## Single-driver (Gamepad 1)
| Input | Operation |
|---|---|
| Left stick | Translation |
| Right stick | Rotation |
| R2 hold / L2 hold | Intake forward / reverse; both = stop |
| R1 / L1 | Feeder toggle / Shooter toggle |
| L3 | Fast/Slow |
| A/B/X/Y | Four configurable navigation destinations |
| D-pad Up | Cycle NORMAL → CLOSE → ANGLED |
| D-pad Down hold | Reverse feeder to clear jam; release = stop |
| D-pad Left / Right | Sorting toggle / Cycle flower arm three positions |
| Share + D-pad Up/Down | Shooter target ±25 RPM (when enabled) |
| Share + D-pad Right/Left | Shooter target +10/-10 RPM (when enabled) |
| Hold Share + Options 0.75s | Alliance BLUE/RED switch |
| Hold Options + R3 0.75s | Robot-centric fallback on/off |
| R3 alone | Unassigned |

## Two-driver
**Gamepad 1:** Same drive, intake, L3 speed toggle, A/B/X/Y navigation, and special Share/Options combinations.

**Gamepad 2:** L1 shooter toggle; R1 feeder toggle; R2 hold feeder reverse; Triangle/Y flower arm UP; Square/X PARTIAL; Cross/A DOWN; Circle/B sorter; D-pad Up NORMAL preset; Left CLOSE preset; Right ANGLED preset; Down unassigned. Share+D-pad same RPM modifier as single-driver.

Share modifier takes precedence over all ordinary D-pad actions, including feeder reverse. No competition gate manual override; no driver interlock bypass; no dedicated emergency-stop chord. Shooter OFF always cancels feeder and closes shooter gate.
