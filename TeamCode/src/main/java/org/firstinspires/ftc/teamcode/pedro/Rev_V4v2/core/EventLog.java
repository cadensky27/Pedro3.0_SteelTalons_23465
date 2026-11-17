package org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.core;
import java.util.ArrayDeque;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.config.V4Settings;
/** Bounded in-memory events; never write to disk in control loop. */
public final class EventLog {
    private final ArrayDeque<String> history = new ArrayDeque<>();
    private final long start = System.nanoTime();
    public void record(String s) {
        while (history.size() >= Math.max(1, V4Settings.Telemetry.HISTORY))history.removeFirst();
        history.addLast(String.format("%.2fs %s", (System.nanoTime()-start)/1e9, s));
    }
    public String recent() {
        return history.isEmpty()?"NONE":history.getLast();
    }
    public Iterable<String> all() {
        return history;
    }
}
