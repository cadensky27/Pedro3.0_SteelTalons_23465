package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.tools;

import com.pedropathing.math.Pose;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.Constants_GoBuilda;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.config.V4Settings;
import org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5.navigation.FieldLibrary;

@TeleOp(name = "V5 Pinpoint Validation", group = "Tuning")
public final class PinpointVerification extends OpMode {
    private PinpointLocalizer pinpoint;
    private String error;

    @Override
    public void init() {
        try {
            Pose start = FieldLibrary.require(V4Settings.Navigation.FALLBACK_START_POSE);
            pinpoint = new PinpointLocalizer(hardwareMap, Constants_GoBuilda.localizerConfig);
            pinpoint.setPose(start);
        } catch (RuntimeException failure) {
            error = failure.getMessage();
            pinpoint = null;
        }
        reportPose();
    }

    @Override
    public void init_loop() {
        reportPose();
    }

    @Override
    public void loop() {
        reportPose();
    }

    private void reportPose() {
        telemetry.addData("Starting pose", V4Settings.Navigation.FALLBACK_START_POSE);
        if (pinpoint != null) {
            pinpoint.update();
            Pose pose = pinpoint.pose();
            telemetry.addData("X (in)", pose.x());
            telemetry.addData("Y (in)", pose.y());
            telemetry.addData("Heading (deg)", Math.toDegrees(pose.heading()));
        } else {
            telemetry.addData("Pinpoint error", error);
        }
        telemetry.update();
    }
}
