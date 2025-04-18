package frc.robot.commands;

import static edu.wpi.first.units.Units.*;
import static frc.robot.Constants.Vision.*;

import com.ctre.phoenix6.Utils;

import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.VisionSubsystem;

public class PhotonVisionCommand extends Command {
  private final VisionSubsystem vision;
  private final CommandSwerveDrivetrain drivetrain;

  public PhotonVisionCommand(VisionSubsystem vision, CommandSwerveDrivetrain drivetrain) {
    this.vision = vision;
    this.drivetrain = drivetrain;

    addRequirements(vision);
  }

  @Override
  public void initialize() {}

  @Override
  public void execute() {

    if (!RobotState.isEnabled()) {
      // Correct pose estimate with vision measurements
      var visionEst = vision.getEstimatedGlobalPose();
      visionEst.ifPresent(
          est -> {
            drivetrain.addVisionMeasurement(
                est.estimatedPose.toPose2d(),
                Utils.fpgaToCurrentTime(est.timestampSeconds),
                vision.getEstimationStdDevs());
          });

      if (USE_LIMELIGHT) {
        var limelightEst = vision.getVisionMeasurement_MT2(drivetrain.getState().Pose);
        limelightEst.ifPresent(
            est -> {
              if (est.tagCount >= 1) {
                if (Math.abs(drivetrain.getState().Speeds.omegaRadiansPerSecond) < RotationsPerSecond.of(2).in(RadiansPerSecond)) {
                  drivetrain.addVisionMeasurement(
                    est.pose,
                    est.timestampSeconds, 
                    vision.getEstimationStdDevs());
                }
              }
          });
      }
    }
  }
}
