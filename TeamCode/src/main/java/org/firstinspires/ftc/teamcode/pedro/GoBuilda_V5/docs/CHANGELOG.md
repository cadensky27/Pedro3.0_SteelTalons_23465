# GoBuilda V5 — Documentation Changelog

Use this file to track **verified changes to the deployed robot software**, not ideas or proposed fixes.

## V5 — Documentation baseline (2026-11-15)

- V5 tutorial establishes single-driver and two-driver COMP layouts with distinct bindings.
- Subsystems remain separate from controls; `BaseTeleOp.java` and `Binding.java` own input semantics.
- Flywheel shooter with NORMAL/CLOSE/ANGLED presets, initial-ready feeder interlock, and optional pause-and-recover behavior.
- Intake and installed side rollers operate together; dedicated feeder reverse and deliberate full-system reverse are documented.
- Pedro and Pinpoint drive/navigation with named Red-authored field poses.
- Four starting pose categories: `START_PRIMARY`, `START_ALTERNATE`, `START_GARDEN`, and `START_FLOWER` (coordinates must be verified).
- User-created Java Auto paths supported with Pedro and Ivy commands; tutorial includes park-only and shoot-and-park examples.
- Existing V5 configuration still references `V4Settings.java` by name per supplied tutorial.
- Created consolidated Markdown guides for setup, controls, wiring, settings, tuning, Auto, troubleshooting, architecture and pre-event checks.

**Source caveat:** This is a summary of the provided V5 tutorial and the agreed physical wiring map. It is **not** a line-by-line audited software release note because the complete Java source was not supplied.

## Template for next change

### YYYY-MM-DD — Short change title

**What changed:**

**Files/settings affected:**

**Why:**

**Verification performed:**

**Rollback / known-good commit:**

## Pending verification

- [ ] Check all documents against the current V5 Java files and deployed button maps.
- [ ] Replace unmeasured field coordinates and verify both alliances.
- [ ] Record competition-tested shooter, feeder, and drive constants.
- [ ] Record the first verified competition release version/commit.
