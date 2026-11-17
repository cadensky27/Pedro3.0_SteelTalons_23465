package org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.teleop;
import com.qualcomm.robotcore.hardware.Gamepad;
/** Edge detector for FTC/PS4 buttons. */
public final class Buttons {
    private boolean a, b, x, y, lb, rb, ls, rs, u, d, l, r;
    public boolean pa, pb, px, py, plb, prb, pls, prs, pu, pd, pl, pr;
    public void read(Gamepad g) {
        pa = g.a && !a;
        pb = g.b && !b;
        px = g.x && !x;
        py = g.y && !y;
        plb = g.left_bumper && !lb;
        prb = g.right_bumper && !rb;
        pls = g.left_stick_button && !ls;
        prs = g.right_stick_button && !rs;
        pu = g.dpad_up && !u;
        pd = g.dpad_down && !d;
        pl = g.dpad_left && !l;
        pr = g.dpad_right && !r;
        a = g.a;
        b = g.b;
        x = g.x;
        y = g.y;
        lb = g.left_bumper;
        rb = g.right_bumper;
        ls = g.left_stick_button;
        rs = g.right_stick_button;
        u = g.dpad_up;
        d = g.dpad_down;
        l = g.dpad_left;
        r = g.dpad_right;
    }
}
