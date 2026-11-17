package org.firstinspires.ftc.teamcode.pedro.GoBuilda_V5;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants_GoBuilda {

    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("frontLeft");
        c.frontRightName.set("frontRight");
        c.backLeftName.set("backLeft");
        c.backRightName.set("backRight");
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
    });

    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(6.076898649921567);
        c.yPodOffset.set(-1.5606823117714228);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.21777650468080878);
                Controller secondaryTranslationalForward = Controller.proportional(0.08046259938418195);
                Controller primaryTranslationalLateral = Controller.proportional(0.2646882762172046);
                Controller secondaryTranslationalLateral = Controller.proportional(0.0977952454612586);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.015555464345173067));
                c.brake.set(Controller.proportionalFeedforward(0.013222144693397107));

                c.headingFeedback.set(Controller.proportional(3.822377888628225));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.041756306104987984, 0.005690061524921563));

                c.linearBrakeCoefficients.set(Matrix.diag(0.06736955424029518, 0.05061274241651573));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0013314732596466346, 0.001634574469797668));

                c.maxAchievableForwardVelocity.set(65.95600145563223);
                c.maxAchievableStrafeVelocity.set(55.695690829199044);
                c.naturalForwardDeceleration.set(32.03613606550073);
                c.naturalStrafeDeceleration.set(58.41104300065124);
            }
    );

    public static Follower create(HardwareMap h) {
        return new Follower(
                new PinpointLocalizer(h, localizerConfig),
                new Mecanum(h, drivetrainConfig),
                new Foresight(foresightConfig)
        );
    }
}
