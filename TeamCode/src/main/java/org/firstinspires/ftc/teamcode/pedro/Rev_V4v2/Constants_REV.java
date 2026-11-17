package org.firstinspires.ftc.teamcode.pedro.Rev_V4v2;
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
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.pedro.Rev_V4v2.config.V4Settings;

/** Pedro configuration for the REV package. Measure offsets and retune on the REV chassis. */
public class Constants_REV {
    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set(V4Settings.Hardware.FRONT_LEFT);
        c.frontRightName.set(V4Settings.Hardware.FRONT_RIGHT);
        c.backLeftName.set(V4Settings.Hardware.BACK_LEFT);
        c.backRightName.set(V4Settings.Hardware.BACK_RIGHT);
        c.frontLeftDirection.set(V4Settings.Hardware.FRONT_LEFT_DIRECTION);
        c.frontRightDirection.set(V4Settings.Hardware.FRONT_RIGHT_DIRECTION);
        c.backLeftDirection.set(V4Settings.Hardware.BACK_LEFT_DIRECTION);
        c.backRightDirection.set(V4Settings.Hardware.BACK_RIGHT_DIRECTION);
    }
    );
    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set(V4Settings.Hardware.PINPOINT);
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(5.696131788839505);
        c.yPodOffset.set(-0.9529286482202726);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    }
    );
    public static ForesightConfig foresightConfig = new ForesightConfig(
    c -> {
        Controller primaryTranslationalForward = Controller.proportional(0.24549865173469218);
        Controller secondaryTranslationalForward = Controller.proportional(0.09070519197118002);
        Controller primaryTranslationalLateral = Controller.proportional(0.33752766739215934);
        Controller secondaryTranslationalLateral = Controller.proportional(0.1247074541960266);
        c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
        c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));
        c.coast.set(Controller.proportionalFeedforward(0.015888455253663086));
        c.brake.set(Controller.proportionalFeedforward(0.013505186965613623));
        c.headingFeedback.set(Controller.proportional(4.593394221998197));
        c.headingBrakeCoefficients.set(Vector2D.cartesian(0.05296995667185284, 0.003478076248329871));
        c.linearBrakeCoefficients.set(Matrix.diag(0.09773367046398758, 0.05545842357709076));
        c.quadraticBrakeCoefficients.set(Matrix.diag(5.314743711424796E-4, 0.0014408194148535765));
        c.maxAchievableForwardVelocity.set(65.49557786602023);
        c.maxAchievableStrafeVelocity.set(54.3265492623456);
        c.naturalForwardDeceleration.set(29.357629741431015);
        c.naturalStrafeDeceleration.set(57.25420150235543);
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
