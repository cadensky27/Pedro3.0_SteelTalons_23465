# Competition checklist
- [ ] Verify all hub motor/servo ports and configuration names.
- [ ] Side rollers ON, other optional accessories OFF unless installed/calibrated.
- [ ] Confirm shot RPM tune and correct flywheel direction.
- [ ] Confirm gamepads recognized; test special chords.
- [ ] Verify the selected drive backend. In PEDRO, confirm alliance, pose handoff status, and field orientation.
- [ ] Confirm A/B/X/Y destinations physically verified; Advanced OFF unless every corridor measured.
- [ ] Confirm feed interlock, reverse jam clearing, gate close on feed cancel.
- [ ] Run independent autonomous path and its handoff test; do NOT use disabled template.
- [ ] Verify Driver Station Stop safely stops all motors and CR servos.
- [ ] Save known-working project backup/commit before new tuning experiments.

### No-Auto TeleOp startup (Update 1)

- [ ] Verify `DEFAULT_ALLIANCE` is correct and the robot is placed at the selected `PARK_RIGHT` field pose with its heading matched.
- [ ] Check INIT `POSE SOURCE` says `FALLBACK: PARK_RIGHT` when no handoff exists.
- [ ] Test field-centric forward command with correct Blue/Red driver heading offset.
- [ ] Leave `TRUST_FALLBACK_POSITION_FOR_NAV = false` until the park pose has been measured and physical placement verified.
- [ ] Confirm Pinpoint communication on the configured REV I²C port is stable.
