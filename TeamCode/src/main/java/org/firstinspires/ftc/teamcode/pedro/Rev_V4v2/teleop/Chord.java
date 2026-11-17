package org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.teleop;
/** Debounced two-button hold, once per chord. */
public final class Chord {
    private long began = -1;
    private boolean fired;
    public boolean step(boolean a, boolean b, long ms) {
        if (!a || !b) {
            began = -1;
            fired = false;
            return false;
        }
        long now = System.nanoTime()/1000000;
        if (began<0)began = now;
        if (!fired && now-began >= ms) {
            fired = true;
            return true;
        }
        return false;
    }
}
