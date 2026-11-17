package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "V5 COMP - Single Driver", group = "BIOBUZZ V5")
public final class CompetitionSingleDriver extends BaseTeleOp {
    @Override
    protected boolean twoDrivers() {
        return false;
    }
}
