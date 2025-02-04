package frc.robot;

import com.pathplanner.lib.config.PIDConstants;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;

public final class Constants {

  public static final boolean DEBUGGING = true;
  public static final boolean TESTING = false;

  public static final double TRIGGER_DEADBAND = 0.01;
  public static final double STICK_DEADBAND = 0.01;
  public static final double TRIGGER_SPEEDFACTOR = 0.5;

  public static final double ROBOT_MASS = (148 - 20.3) * 0.453592; // 32lbs * kg per pound
  // public static final Matter CHASSIS =
  //     new Matter(new Translation3d(0, 0, Units.inchesToMeters(8)), ROBOT_MASS);
  public static final double LOOP_TIME = 0.13; // s, 20ms + 110ms sprk max velocity lag
  public static final double TURN_CONSTANT = 0.75;

  public static final class Vision {

    public static final String LIMELIGHTNAME = "limelight";
    public static final String LIMELIGHTURL = "limelight.local";
    public static final String PHOTONVISIONURL = "photonvision.local";

    public static final String kCameraName1 = "OV9281";
    public static final String kCameraName2 = "camera2";

    // Cam mounted - x = +toward front, 0 center, -toward rear in meters.
    //               y = +left of center, 0 center, -right of center in meters
    //               z = +up from base of robot in meters
    //              roll = rotate around front/rear in radians. PI = upsidedown
    //              pitch = tilt down/up along left/right axis. PI/4 = tilt down 45 degrees, -PI/4 = tilt up 45
    //              yaw = rotate left/right around z axis. PI/4 = rotate camera to the left 45 degrees.
    public static final Transform3d kRobotToCam1 =
        new Transform3d(new Translation3d(0, 0, 0), new Rotation3d(0, 0, 0));

    // The standard deviations of our vision estimated poses, which affect correction rate
    // (Fake values. Experiment and determine estimation noise on an actual robot.)
    public static final Matrix<N3, N1> kSingleTagStdDevs = VecBuilder.fill(4, 4, 8);
    public static final Matrix<N3, N1> kMultiTagStdDevs = VecBuilder.fill(0.5, 0.5, 1);
  }

  public static final class DriveTrainConstants {
    /** Maximum Speed - Meters per Second */
    public static final double maxSpeed = 4.5; //TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
    public static final double maxAngularRate = 5.0; //RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity
  }

  public static final class AutoConstants {
    public static final PIDConstants translationPID = new PIDConstants(5.0, 0.0, 0.0);
    public static final PIDConstants rotationPID = new PIDConstants(5.0, 0.0, 0.0);

    public static final double kMaxSpeedMetersPerSecond = 1.0;
    public static final double kMaxAccelerationMetersPerSecondSquared = 1.0;
    public static final double kMaxAngularSpeedRadiansPerSecond = Math.PI * 1.5;
    public static final double kMaxAngularSpeedRadiansPerSecondSquared = Math.PI;
  }

  public static final class AlgaeGrabberConstants {
    public static final int motorId = 1;

    public static final double intakeSpeed = 0.15;
    public static final double outtakeSpeed = -0.25;
  }

  public static final class ArmConstants {
      public static final int motorId = 1;
      public static final int sensorId = 1;
      public static final double sensorOffset = 0;
      public static final double[] armPID = {0, 0, 0}; //TODO
      public static final double forwardSoftLimit = 0;
      public static final double reverseSoftLimit = 0;
      public static final double maxForwardSpeed = 0;
      public static final double maxReverseSpeed = 0;
      public static final double tolerance = 1;

      public final class algaePositions { //with offset
          public static final double L2 = 0;
          public static final double L3 = 0;
          public static final double fieldAlgae = 0;
          public static final double processor = 0;
          public static final double barge = 0;
          public static final double home = 0;
          public static final double floor = 0;
      }
  }

  public static final class ElevatorConstants {
      public static final int leftMotorId = 1;
      public static final int rightMotorId = 2;
      public static final double[] elevatorPID = {0.05, 0, 0};
      public static final double softLimit = 24.8;
      public static final double gravityFeedforward = 0;
      public static final double maxForwareSpeed = 0.5;
      public static final double maxReverseSpeed = -0.3;
      public static final double tolerance = 1;
      public static final double swerveLimitThreshold = 10;

      public final class scoringPositioins {
          //TODO find these
          public static final double L2 = 0;
          public static final double L3 = 0;
          public static final double L4 = 0;
      }
  }

  public static final class EndeffectorConstants {
      public static final int motorId = 3;
      public static final int sensorId = 1;
      public static final double coralDistanceThreshold = 0.1;
      public static final double coralLoadTime = 0.5;
  }
}
