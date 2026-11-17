package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.teleop;

import com.qualcomm.robotcore.hardware.Gamepad;

/** A button and the way its state is interpreted across OpMode loops. */
final class Binding {
    enum Mode { HOLD, PRESS, TOGGLE }

    enum Button {
        A, B, X, Y, LB, RB, LT, RT, L3, R3, OPTIONS, UP, DOWN, LEFT, RIGHT;

        boolean held(Gamepad pad) {
            switch (this) {
                case A: return pad.a;
                case B: return pad.b;
                case X: return pad.x;
                case Y: return pad.y;
                case LB: return pad.left_bumper;
                case RB: return pad.right_bumper;
                case LT: return pad.left_trigger > .15;
                case RT: return pad.right_trigger > .15;
                case L3: return pad.left_stick_button;
                case R3: return pad.right_stick_button;
                case OPTIONS: return pad.options;
                case UP: return pad.dpad_up;
                case DOWN: return pad.dpad_down;
                case LEFT: return pad.dpad_left;
                case RIGHT: return pad.dpad_right;
                default: return false;
            }
        }
    }

    private final Button button;
    private final Mode mode;
    private boolean previouslyDown;
    private boolean toggled;
    private boolean waitForRelease;

    Binding(Button button, Mode mode) {
        this.button = button;
        this.mode = mode;
    }

    boolean read(Gamepad pad) {
        return read(pad, true);
    }

    boolean read(Gamepad pad, boolean allowed) {
        boolean down = button.held(pad);
        boolean pressed = down && !previouslyDown;
        previouslyDown = down;
        if (!allowed && down) waitForRelease = true;
        if (!down) waitForRelease = false;
        if (!allowed || waitForRelease) return false;

        switch (mode) {
            case HOLD: return down;
            case PRESS: return pressed;
            case TOGGLE:
                if (pressed) toggled = !toggled;
                return toggled;
            default: return false;
        }
    }

    void clearUntilReleased(Gamepad pad) {
        toggled = false;
        waitForRelease = button.held(pad);
        previouslyDown = waitForRelease;
    }
}
